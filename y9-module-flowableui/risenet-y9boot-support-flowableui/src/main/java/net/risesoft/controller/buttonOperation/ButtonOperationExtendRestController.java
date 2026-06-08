package net.risesoft.controller.buttonOperation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.validation.constraints.NotBlank;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.SignDeptDetailApi;
import net.risesoft.api.itemadmin.TaskRelatedApi;
import net.risesoft.api.itemadmin.core.ActRuDetailApi;
import net.risesoft.api.itemadmin.core.DocumentApi;
import net.risesoft.api.itemadmin.core.ProcessParamApi;
import net.risesoft.api.itemadmin.documentword.DocumentWordApi;
import net.risesoft.api.itemadmin.form.FormDataApi;
import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.enums.ActRuDetailStatusEnum;
import net.risesoft.enums.SignDeptDetailStatusEnum;
import net.risesoft.enums.TaskRelatedEnum;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.log.FlowableOperationTypeEnum;
import net.risesoft.log.annotation.FlowableLog;
import net.risesoft.model.itemadmin.ItemButtonModel;
import net.risesoft.model.itemadmin.SignDeptDetailModel;
import net.risesoft.model.itemadmin.StartProcessResultModel;
import net.risesoft.model.itemadmin.TaskRelatedModel;
import net.risesoft.model.itemadmin.core.ActRuDetailModel;
import net.risesoft.model.itemadmin.core.ProcessParamModel;
import net.risesoft.model.platform.org.Position;
import net.risesoft.model.processadmin.TargetModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.ButtonOperationService;
import net.risesoft.service.DocumentHandleService;
import net.risesoft.service.ProcessParamService;
import net.risesoft.util.Y9DateTimeUtils;
import net.risesoft.y9.Y9FlowableHolder;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 发送，办结相关
 *
 * @author zhangchongjie
 * @date 2024/06/05
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/vue/buttonOperation/extend", produces = MediaType.APPLICATION_JSON_VALUE)
public class ButtonOperationExtendRestController {

    private final DocumentApi documentApi;
    private final ProcessParamApi processParamApi;
    private final TaskApi taskApi;
    private final ProcessDefinitionApi processDefinitionApi;
    private final SignDeptDetailApi signDeptDetailApi;
    private final ProcessParamService processParamService;
    private final TaskRelatedApi taskRelatedApi;
    private final ActRuDetailApi actRuDetailApi;
    private final DocumentWordApi documentWordApi;
    private final FormDataApi formDataApi;
    private final DocumentHandleService documentHandleService;
    private final ButtonOperationService buttonOperationService;

    /**
     * 检查是否可以批量发送
     *
     * @param taskIdAndProcessSerialNumbers 任务id和流程实例id
     * @return Y9Result<List<TargetModel>>
     */
    @PostMapping(value = "/check4Batch")
    public Y9Result<List<TargetModel>> check4Batch(@RequestParam String[] taskIdAndProcessSerialNumbers) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            BatchCheckResult checkResult = collectBatchTaskInfo(tenantId, taskIdAndProcessSerialNumbers);
            // 验证任务状态
            Y9Result<List<TargetModel>> validationResult = validateBatchTasks(checkResult);
            if (validationResult != null) {
                return validationResult;
            }
            // 获取目标节点
            List<TargetModel> routeToTasks = getTargetNodesForBatch(tenantId, checkResult.validTasks);
            return Y9Result.success(routeToTasks);
        } catch (Exception e) {
            LOGGER.error("校验批量发送失败", e);
            return Y9Result.failure("校验是否可以批量发送失败，发生异常");
        }
    }

    /**
     * 检查是否可以批量重定向
     *
     * @param processSerialNumber 流程序列号
     * @param documentId 文档id
     * @return Y9Result<List<TargetModel>>
     */
    @PostMapping(value = "/check4Reposition")
    public Y9Result<List<TargetModel>> check4Reposition(@RequestParam @NotBlank String processSerialNumber,
        @RequestParam @NotBlank String documentId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            ProcessParamModel ppModel =
                processParamApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
            String processInstanceId = ppModel.getProcessInstanceId();
            List<TaskModel> taskList = taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
            if (taskList.isEmpty()) {
                return Y9Result.failure("不能重定向，该件已办结。");
            }
            if (documentId.equals(processSerialNumber)) {
                List<TargetModel> mainTargetModelList =
                    processDefinitionApi.getNodesOnlyMain(tenantId, taskList.get(0).getProcessDefinitionId()).getData();
                boolean isMainProcess = mainTargetModelList.stream()
                    .anyMatch(t -> t.getTaskDefKey().equals(taskList.get(0).getTaskDefinitionKey()));
                if (!isMainProcess) {
                    return Y9Result.failure("不能重定向，已流转至会签环节。");
                }
                return Y9Result.success(mainTargetModelList);
            } else {
                SignDeptDetailModel signDeptDetail = signDeptDetailApi.findById(documentId).getData();
                if (!signDeptDetail.getStatus().equals(SignDeptDetailStatusEnum.DOING)) {
                    return Y9Result.failure("不能重定向，" + signDeptDetail.getDeptName() + "的会签件已被处理。");
                }
                List<TargetModel> subTargetModelList =
                    processDefinitionApi.getSubProcessChildNode(tenantId, taskList.get(0).getProcessDefinitionId())
                        .getData();
                return Y9Result.success(subTargetModelList);
            }
        } catch (Exception e) {
            LOGGER.error("校验失败", e);
        }
        return Y9Result.failure("校验是否可以批量重定向失败，发生异常");
    }

    /**
     * 收集批量任务信息
     */
    private BatchCheckResult collectBatchTaskInfo(String tenantId, String[] taskIdAndProcessSerialNumbers) {
        BatchCheckResult result = new BatchCheckResult();
        for (String taskIdAndProcessSerialNumber : taskIdAndProcessSerialNumbers) {
            String[] tpArr = taskIdAndProcessSerialNumber.split(":");
            TaskModel task = taskApi.findById(tenantId, tpArr[0]).getData();
            if (task == null) {
                handleNullTask(tenantId, tpArr, result.processedTaskMsg);
            } else {
                result.validTasks.add(task);
                if (StringUtils.isBlank(task.getAssignee())) {
                    result.unsignedTasks.add(task);
                }
                if (!result.taskDefinitionKeys.contains(task.getTaskDefinitionKey())) {
                    result.taskDefinitionKeys.add(task.getTaskDefinitionKey());
                }
            }
        }
        return result;
    }

    /**
     * 复制并起草
     *
     * @param processSerialNumber 流程序列号
     * @return Y9Result<Map<String, Object>>
     */
    @FlowableLog(operationName = "复制并起草", operationType = FlowableOperationTypeEnum.DRAFT)
    @PostMapping(value = "/copy2Todo")
    public Y9Result<Map<String, Object>> copy2Todo(@RequestParam @NotBlank String processSerialNumber,
        @RequestParam(required = false) String startTaskDefKey) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<TaskRelatedModel> processRelatedList =
            taskRelatedApi.findByProcessSerialNumber(processSerialNumber).getData();
        if (processRelatedList.stream()
            .anyMatch(taskRelatedModel -> TaskRelatedEnum.FU.getValue().equals(taskRelatedModel.getInfoType()))) {
            return Y9Result.failure("操作失败：复制件不能被复制!");
        }
        if (processRelatedList.stream()
            .anyMatch(taskRelatedModel -> TaskRelatedEnum.YUAN.getValue().equals(taskRelatedModel.getInfoType()))) {
            return Y9Result.failure("操作失败：该文件已被复制!");
        }
        // 1复制表单数据
        String targetProcessSerialNumber = Y9IdGenerator.genId();
        Y9Result<Object> copy = formDataApi.copy(tenantId, processSerialNumber, targetProcessSerialNumber);
        if (!copy.isSuccess()) {
            return Y9Result.failure("操作失败：表单数据不存在!");
        }
        Y9Result<ProcessParamModel> processParamY9Result =
            processParamApi.findByProcessSerialNumber(tenantId, processSerialNumber);
        if (!processParamY9Result.isSuccess()) {
            return Y9Result.failure("操作失败：流程参数不存在!");
        }
        ProcessParamModel oldProcessParam = processParamY9Result.getData();
        // 2修改表单流水号

        // 3复制正文数据 正文类别,1:办文要报，2：发文稿纸
        Y9Result<Object> word1 = documentWordApi.copyByProcessSerialNumberAndWordType(tenantId, processSerialNumber,
            targetProcessSerialNumber, "1");
        Y9Result<Object> word2 = documentWordApi.copyByProcessSerialNumberAndWordType(tenantId, processSerialNumber,
            targetProcessSerialNumber, "2");
        if (!word1.isSuccess() || !word2.isSuccess()) {
            return Y9Result.failure("操作失败：复制正文失败!");
        }
        // 4复制流程参数并启动流程
        Y9Result<StartProcessResultModel> startY9Result = processParamService.saveOrUpdate(oldProcessParam.getItemId(),
            targetProcessSerialNumber, "", oldProcessParam.getTitle(), oldProcessParam.getCustomNumber(),
            oldProcessParam.getCustomLevel(), false, startTaskDefKey);
        if (!startY9Result.isSuccess()) {
            return Y9Result.failure("操作失败：保存流程参数启动流程失败!");
        }
        StartProcessResultModel startProcess = startY9Result.getData();
        Position position = Y9FlowableHolder.getPosition();
        // 5 设置原文件的相关信息
        TaskRelatedModel sourceTaskRelated = new TaskRelatedModel();
        sourceTaskRelated.setInfoType(TaskRelatedEnum.YUAN.getValue());
        sourceTaskRelated.setProcessInstanceId(oldProcessParam.getProcessInstanceId());
        sourceTaskRelated.setProcessSerialNumber(processSerialNumber);
        sourceTaskRelated.setExecutionId(oldProcessParam.getProcessInstanceId());
        sourceTaskRelated.setTaskId("copy2Todo");
        sourceTaskRelated.setSub(false);
        sourceTaskRelated.setSenderId(position.getId());
        sourceTaskRelated.setSenderName(position.getName());
        Y9Result<Object> yuanResult = taskRelatedApi.saveOrUpdate(sourceTaskRelated);
        // 6 设置复制件的相关信息
        Map<String, Object> fwFormDataMap =
            formDataApi.getData4TableAlias(tenantId, processSerialNumber, "fw").getData();
        String sourceLsh = (String)fwFormDataMap.getOrDefault("lsh", "流水号不存在");
        TaskRelatedModel targetTaskRelated = new TaskRelatedModel();
        targetTaskRelated.setInfoType(TaskRelatedEnum.FU.getValue());
        targetTaskRelated.setProcessInstanceId(startProcess.getProcessInstanceId());
        targetTaskRelated.setProcessSerialNumber(targetProcessSerialNumber);
        targetTaskRelated.setExecutionId(startProcess.getProcessInstanceId());
        targetTaskRelated.setTaskId("copy2Todo");
        targetTaskRelated.setSub(false);
        targetTaskRelated.setMsgContent(sourceLsh);
        targetTaskRelated.setSenderId(position.getId());
        targetTaskRelated.setSenderName(position.getName());
        Y9Result<Object> fuResult = taskRelatedApi.saveOrUpdate(targetTaskRelated);

        List<ActRuDetailModel> actRuDetailList = actRuDetailApi
            .findByProcessSerialNumberAndStatus(tenantId, targetProcessSerialNumber, ActRuDetailStatusEnum.TODO)
            .getData();
        ActRuDetailModel actRuDetail = actRuDetailList.stream()
            .filter(actRuDetailModel -> actRuDetailModel.getTaskId().equals(startProcess.getTaskId()))
            .findFirst()
            .orElse(null);
        Map<String, Object> map = new HashMap<>();
        assert actRuDetail != null;
        map.put("taskId", actRuDetail.getTaskId());
        map.put("taskName", actRuDetail.getTaskDefName());
        map.put("actRuDetailId", actRuDetail.getId());
        map.put("userName", actRuDetail.getAssigneeName());
        if (yuanResult.isSuccess() && fuResult.isSuccess()) {
            return Y9Result.success(map);
        }
        return Y9Result.success(map, "复制并起草成功，但是建立关联关系失败！");
    }

    /**
     * 办件批量发送
     *
     * @param taskIdAndProcessSerialNumbers 任务id和流程序列号集合
     * @param userChoice 收件人
     * @param routeToTaskId 发送路由，任务key
     * @param sponsorHandle 是否主办办理
     * @param sponsorGuid 主办人id
     * @return Y9Result<Map < String, Object>>
     */
    @FlowableLog(operationName = "批量发送", operationType = FlowableOperationTypeEnum.SEND)
    @PostMapping(value = "/forwarding4Batch")
    public Y9Result<String> forwarding4Batch(@RequestParam String[] taskIdAndProcessSerialNumbers,
        @RequestParam @NotBlank String userChoice, @RequestParam @NotBlank String routeToTaskId,
        @RequestParam(required = false) String sponsorHandle, @RequestParam(required = false) String sponsorGuid,
        @RequestParam(required = false) String dueDate, @RequestParam(required = false) String description) {
        AtomicInteger success = new AtomicInteger();
        AtomicInteger fail = new AtomicInteger();
        Arrays.stream(taskIdAndProcessSerialNumbers).forEach(tp -> {
            String[] tpArr = tp.split(":");
            try {
                ProcessParamModel processParamModel =
                    processParamApi.findByProcessSerialNumber(Y9LoginUserHolder.getTenantId(), tpArr[1]).getData();
                processParamModel.setDueDate(null);
                if (StringUtils.isNotBlank(dueDate)) {
                    processParamModel.setDueDate(Y9DateTimeUtils.parseDate(dueDate));
                }
                processParamModel.setDescription(description);
                processParamApi.saveOrUpdate(Y9LoginUserHolder.getTenantId(), processParamModel);

                Y9Result<String> y9Result = documentApi.forwarding(Y9LoginUserHolder.getTenantId(),
                    Y9FlowableHolder.getPositionId(), tpArr[0], userChoice, routeToTaskId, sponsorHandle, sponsorGuid);
                if (y9Result.isSuccess()) {
                    success.getAndIncrement();
                } else {
                    fail.getAndIncrement();
                }
            } catch (Exception e) {
                fail.getAndIncrement();
            }
        });
        return Y9Result.successMsg("发送成功" + success.get() + "条，发送失败" + fail.get() + "条");
    }

    /**
     * 获取待办按钮
     *
     * @param taskId 任务id
     * @return {@code Y9Result<List<ItemButtonModel>>} 通用请求返回对象 - data是按钮集合
     */
    @GetMapping(value = "/getButtons")
    public Y9Result<List<ItemButtonModel>> getButtons(@RequestParam @NotBlank String taskId) {
        try {
            TaskModel task = taskApi.findById(Y9LoginUserHolder.getTenantId(), taskId).getData();
            if (null == task) {
                return Y9Result.failure("当前待办已处理！");
            }
            List<ItemButtonModel> buttonList =
                documentApi.getButtons(Y9LoginUserHolder.getTenantId(), Y9FlowableHolder.getPositionId(), taskId)
                    .getData();
            return Y9Result.success(buttonList, "获取成功");
        } catch (Exception e) {
            LOGGER.error("获取编辑办件数据失败", e);
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 获取批量任务的目标节点
     */
    private List<TargetModel> getTargetNodesForBatch(String tenantId, List<TaskModel> taskList) {
        if (taskList.isEmpty()) {
            return new ArrayList<>();
        }
        TaskModel firstTask = taskList.get(0);
        return processDefinitionApi
            .getTargetNodes(tenantId, firstTask.getProcessDefinitionId(), firstTask.getTaskDefinitionKey())
            .getData()
            .stream()
            .filter(m -> !"退回".equals(m.getTaskDefName()) && !"Exclusive Gateway".equals(m.getTaskDefName()))
            .collect(Collectors.toList());
    }

    /**
     * 处理空任务情况
     */
    private void handleNullTask(String tenantId, String[] tpArr, StringBuilder msg) {
        try {
            ProcessParamModel ppModel = processParamApi.findByProcessSerialNumber(tenantId, tpArr[1]).getData();
            if (StringUtils.isBlank(msg.toString())) {
                msg.append(ppModel.getTitle());
            } else {
                msg.append(",").append(ppModel.getTitle());
            }
        } catch (Exception e) {
            LOGGER.warn("获取流程参数失败: processSerialNumber={}", tpArr[1], e);
        }
    }

    /**
     * 批量恢复待办
     *
     * @param processInstanceIds 流程实例ids ，逗号隔开
     * @param desc 原因
     * @return Y9Result<String>
     */
    @PostMapping(value = "/multipleResumeToDo")
    public Y9Result<String> multipleResumeToDo(@RequestParam @NotBlank String processInstanceIds,
        @RequestParam(required = false) String desc) {
        try {
            buttonOperationService.multipleResumeToDo(processInstanceIds, desc);
            return Y9Result.successMsg("恢复成功");
        } catch (Exception e) {
            LOGGER.error("恢复待办失败", e);
        }
        return Y9Result.failure("恢复失败");
    }

    /**
     * 批量签收
     *
     * @param taskIdAndProcessSerialNumbers 任务id和流程实例id
     * @return Y9Result<String>
     */
    @FlowableLog(operationName = "批量签收", operationType = FlowableOperationTypeEnum.CLAIM)
    @PostMapping(value = "/sign4Batch")
    public Y9Result<String> sign4Batch(@RequestParam String[] taskIdAndProcessSerialNumbers) {
        return documentHandleService.sign4Batch(taskIdAndProcessSerialNumbers);
    }

    /**
     * 验证批量任务
     */
    private Y9Result<List<TargetModel>> validateBatchTasks(BatchCheckResult checkResult) {
        if (!checkResult.unsignedTasks.isEmpty()) {
            return Y9Result.failure("不能批量发送，存在未签收的待办");
        }
        if (StringUtils.isNotBlank(checkResult.processedTaskMsg.toString())) {
            return Y9Result.failure("不能批量发送，以下待办已处理：" + checkResult.processedTaskMsg);
        }
        if (checkResult.taskDefinitionKeys.size() > 1) {
            return Y9Result.failure("不能批量发送，存在不同的办理环节");
        }
        return null;
    }

    /**
     * 批量检查结果封装类
     */
    private static class BatchCheckResult {
        final StringBuilder processedTaskMsg = new StringBuilder();
        final List<String> taskDefinitionKeys = new ArrayList<>();
        final List<TaskModel> unsignedTasks = new ArrayList<>();
        final List<TaskModel> validTasks = new ArrayList<>();
    }
}
