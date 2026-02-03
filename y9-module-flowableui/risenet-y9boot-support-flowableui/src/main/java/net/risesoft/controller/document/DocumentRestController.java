package net.risesoft.controller.document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import jakarta.validation.constraints.NotBlank;

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

import net.risesoft.api.itemadmin.ChaoSongApi;
import net.risesoft.api.itemadmin.OfficeFollowApi;
import net.risesoft.api.itemadmin.SignDeptDetailApi;
import net.risesoft.api.itemadmin.SmsDetailApi;
import net.risesoft.api.itemadmin.TaskRelatedApi;
import net.risesoft.api.itemadmin.core.ActRuDetailApi;
import net.risesoft.api.itemadmin.core.DocumentApi;
import net.risesoft.api.itemadmin.core.ItemApi;
import net.risesoft.api.itemadmin.core.ProcessParamApi;
import net.risesoft.api.itemadmin.documentword.DocumentWordApi;
import net.risesoft.api.itemadmin.form.FormDataApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.permission.cache.PositionRoleApi;
import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.ProcessTodoApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.consts.FlowableUiConsts;
import net.risesoft.consts.processadmin.SysVariables;
import net.risesoft.enums.ActRuDetailStatusEnum;
import net.risesoft.enums.SignDeptDetailStatusEnum;
import net.risesoft.enums.TaskRelatedEnum;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.log.FlowableOperationTypeEnum;
import net.risesoft.log.annotation.FlowableLog;
import net.risesoft.model.itemadmin.DocUserChoiseModel;
import net.risesoft.model.itemadmin.ItemButtonModel;
import net.risesoft.model.itemadmin.ItemListModel;
import net.risesoft.model.itemadmin.ItemStartNodeRoleModel;
import net.risesoft.model.itemadmin.SignDeptDetailModel;
import net.risesoft.model.itemadmin.SignTaskConfigModel;
import net.risesoft.model.itemadmin.SmsDetailModel;
import net.risesoft.model.itemadmin.StartProcessResultModel;
import net.risesoft.model.itemadmin.TaskRelatedModel;
import net.risesoft.model.itemadmin.core.ActRuDetailModel;
import net.risesoft.model.itemadmin.core.ItemModel;
import net.risesoft.model.itemadmin.core.ProcessParamModel;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.model.platform.org.Position;
import net.risesoft.model.processadmin.TargetModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.ButtonOperationService;
import net.risesoft.service.ProcessParamService;
import net.risesoft.util.Y9DateTimeUtils;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9FlowableHolder;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.configuration.app.flowble.Y9FlowableProperties;

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
@RequestMapping(value = "/vue/document", produces = MediaType.APPLICATION_JSON_VALUE)
public class DocumentRestController {

    private final ItemApi itemApi;
    private final DocumentApi documentApi;
    private final ButtonOperationService buttonOperationService;
    private final ProcessParamApi processParamApi;
    private final ChaoSongApi chaoSongApi;
    private final TaskApi taskApi;
    private final ProcessDefinitionApi processDefinitionApi;
    private final OrgUnitApi orgUnitApi;
    private final SignDeptDetailApi signDeptDetailApi;
    private final PositionRoleApi positionRoleApi;
    private final ProcessTodoApi processTodoApi;
    private final ProcessParamService processParamService;
    private final TaskRelatedApi taskRelatedApi;
    private final ActRuDetailApi actRuDetailApi;
    private final DocumentWordApi documentWordApi;
    private final FormDataApi formDataApi;
    private final Y9FlowableProperties y9FlowableProperties;
    private final SmsDetailApi smsDetailApi;
    private final OfficeFollowApi officeFollowApi;

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
                SignDeptDetailModel signDeptDetail = signDeptDetailApi.findById(tenantId, documentId).getData();
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
     * 流程办结
     *
     * @param taskId 任务id
     * @param infoOvert 办结数据是否在数据中心公开
     * @return Y9Result<String>
     */
    @PostMapping(value = "/complete")
    public Y9Result<String> complete(@RequestParam @NotBlank String taskId,
        @RequestParam(required = false) String infoOvert) {
        try {
            buttonOperationService.complete(taskId, "办结", "已办结", infoOvert);
            return Y9Result.successMsg("办结成功");
        } catch (Exception e) {
            LOGGER.error("流程办结失败", e);
        }
        return Y9Result.failure("办结失败");
    }

    /**
     * 批量办结
     *
     * @param taskIds 任务id集合
     * @return Y9Result<String>
     */
    @FlowableLog(operationName = "批量办结", operationType = FlowableOperationTypeEnum.COMPLETE)
    @PostMapping(value = "/completeTodo")
    public Y9Result<String> completeTodo(@RequestParam String[] taskIds) {
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();
        Arrays.stream(taskIds).forEach(taskId -> {
            try {
                TaskModel task = taskApi.findById(Y9LoginUserHolder.getTenantId(), taskId).getData();
                if (null != task) {
                    buttonOperationService.complete(taskId, "办结", "已办结", "");
                    successCount.getAndIncrement();
                } else {
                    failCount.getAndIncrement();
                }
            } catch (Exception e) {
                failCount.getAndIncrement();
            }
        });
        return Y9Result.successMsg("批量办结成功，成功：" + successCount.get() + "，失败：" + failCount.get());
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
            taskRelatedApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
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
        Y9Result<Object> yuanResult = taskRelatedApi.saveOrUpdate(tenantId, sourceTaskRelated);
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
        Y9Result<Object> fuResult = taskRelatedApi.saveOrUpdate(tenantId, targetTaskRelated);

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
     * 办件发送
     *
     * @param itemId 事项id
     * @param sponsorHandle 是否主办办理
     * @param processInstanceId 流程实例id
     * @param taskId 任务id
     * @param processDefinitionKey 流程定义key
     * @param processSerialNumber 流程编号
     * @param userChoice 收件人
     * @param sponsorGuid 主办人id
     * @param routeToTaskId 发送路由，任务key
     * @param isSendSms 是否短信提醒
     * @param isShuMing 是否署名
     * @param smsContent 短信内容
     * @return Y9Result<Map < String, Object>>
     */
    @FlowableLog(operationName = "办件发送", operationType = FlowableOperationTypeEnum.SEND)
    @PostMapping(value = "/forwarding")
    public Y9Result<Map<String, Object>> forwarding(@RequestParam @NotBlank String itemId,
        @RequestParam(required = false) String sponsorHandle, @RequestParam(required = false) String processInstanceId,
        @RequestParam(required = false) String taskId, @RequestParam @NotBlank String processDefinitionKey,
        @RequestParam @NotBlank String processSerialNumber, @RequestParam @NotBlank String userChoice,
        @RequestParam(required = false) String sponsorGuid, @RequestParam @NotBlank String routeToTaskId,
        @RequestParam(required = false) String isSendSms, @RequestParam(required = false) String isShuMing,
        @RequestParam(required = false) String smsContent) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> variables = new HashMap<>(16);
        try {
            SmsDetailModel smsDetailModel = SmsDetailModel.builder()
                .processSerialNumber(processSerialNumber)
                .positionId(Y9FlowableHolder.getPositionId())
                .positionName(Y9LoginUserHolder.getUserInfo().getName())
                .send(!StringUtils.isBlank(isSendSms) && Boolean.parseBoolean(isSendSms))
                .sign(!StringUtils.isBlank(isShuMing) && Boolean.parseBoolean(isShuMing))
                .content(smsContent)
                .positionIds(userChoice)
                .build();
            Y9Result<Object> result = smsDetailApi.saveOrUpdate(Y9LoginUserHolder.getTenantId(), smsDetailModel);
            if (!result.isSuccess()) {
                return Y9Result.failure("保存短信详情失败！");
            }
            Y9Result<String> y9Result = documentApi.saveAndForwarding(Y9LoginUserHolder.getTenantId(),
                Y9FlowableHolder.getPositionId(), processInstanceId, taskId, sponsorHandle, itemId, processSerialNumber,
                processDefinitionKey, userChoice, sponsorGuid, routeToTaskId, variables);
            if (y9Result.isSuccess()) {
                map.put("processInstanceId", y9Result.getData());
                return Y9Result.success(map, y9Result.getMsg());
            } else {
                return Y9Result.failure(y9Result.getMsg());
            }
        } catch (Exception e) {
            LOGGER.error("发送失败", e);
        }
        return Y9Result.failure("发送失败，发生异常");
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
     * 获取所有开始节点
     *
     * @param itemId 事项id
     * @return Y9Result<List < ItemStartNodeRoleModel>>
     */
    @GetMapping(value = "/getAllStartTaskDefKey")
    public Y9Result<List<ItemStartNodeRoleModel>> getAllStartTaskDefKey(@RequestParam @NotBlank String itemId) {
        return documentApi.getAllStartTaskDefKey(Y9LoginUserHolder.getTenantId(), Y9FlowableHolder.getPositionId(),
            itemId);
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
     * 获取事项列表（含抄送未阅数量、监控管理权限、人事统计权限）
     *
     * @return Y9Result<Map < String, Object>>
     */
    @GetMapping(value = "/getItemList")
    public Y9Result<Map<String, Object>> getItemList() {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Map<String, Object> map = new HashMap<>(16);
        try {
            List<ItemListModel> listMap =
                itemApi.getItemList(Y9LoginUserHolder.getTenantId(), Y9FlowableHolder.getPositionId()).getData();
            map.put("itemMap", listMap);
            map.put("notReadCount",
                chaoSongApi.getTodoCount(Y9LoginUserHolder.getTenantId(), Y9FlowableHolder.getPositionId()).getData());
            int followCount = officeFollowApi.getFollowCount(tenantId, Y9FlowableHolder.getPositionId()).getData();
            map.put("followCount", followCount);
            // 公共角色
            boolean b =
                positionRoleApi
                    .hasPublicRole(tenantId, y9FlowableProperties.getMonitorManageRoleName(),
                        Y9FlowableHolder.getPositionId())
                    .getData();
            map.put("monitorManage", b);
            boolean b1 =
                positionRoleApi
                    .hasRole(tenantId, Y9Context.getSystemName(), "", y9FlowableProperties.getLeaveManageRoleName(),
                        Y9FlowableHolder.getPositionId())
                    .getData();
            map.put("leaveManage", b1);
            return Y9Result.success(map, "获取成功");
        } catch (Exception e) {
            LOGGER.error("获取事项列表失败", e);
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 获取事项和事项对应的系统列表（含抄送未阅数量、监控管理权限、人事统计权限）
     *
     * @return Y9Result<Map < String, Object>>
     */
    @GetMapping(value = "/getItemSystemList")
    public Y9Result<Map<String, Object>> getItemSystemList() {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Map<String, Object> map = new HashMap<>(16);
        try {
            String positionId = Y9FlowableHolder.getPositionId();
            List<Map<String, Object>> list = new ArrayList<>();
            List<ItemModel> listMap = itemApi.getAllItem(Y9LoginUserHolder.getTenantId()).getData();
            for (ItemModel itemModel : listMap) {
                Map<String, Object> newmap = new HashMap<>(16);
                newmap.put(FlowableUiConsts.SYSTEMNAME_KEY, itemModel.getSystemName());
                newmap.put("systemCnName", itemModel.getSysLevel());
                if (!list.contains(newmap)) {
                    list.add(newmap);
                }
            }
            for (Map<String, Object> nmap : list) {
                long todoCount =
                    processTodoApi
                        .getTodoCountByUserIdAndSystemName(tenantId, positionId,
                            (String)nmap.get(FlowableUiConsts.SYSTEMNAME_KEY))
                        .getData();
                nmap.put("todoCount", todoCount);
                List<ItemModel> itemList = new ArrayList<>();
                for (ItemModel itemModel : listMap) {
                    if (nmap.get(FlowableUiConsts.SYSTEMNAME_KEY).equals(itemModel.getSystemName())) {
                        itemList.add(itemModel);
                    }
                }
                nmap.put("itemList", itemList);
            }

            map.put("systemList", list);
            map.put("notReadCount",
                chaoSongApi.getTodoCount(Y9LoginUserHolder.getTenantId(), Y9FlowableHolder.getPositionId()).getData());
            // 公共角色
            boolean b =
                positionRoleApi
                    .hasPublicRole(tenantId, y9FlowableProperties.getMonitorManageRoleName(),
                        Y9FlowableHolder.getPositionId())
                    .getData();
            map.put("monitorManage", b);

            boolean b1 = false;
            map.put("leaveManage", b1);

            return Y9Result.success(map, "获取成功");
        } catch (Exception e) {
            LOGGER.error("获取事项系统列表失败", e);
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 获取协办人员办理情况
     *
     * @param taskId 任务id * @return Y9Result<Map<String, Object>>
     */
    @GetMapping(value = "/getParallelNames")
    public Y9Result<Map<String, Object>> getParallelNames(@RequestParam @NotBlank String taskId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String positionId = Y9FlowableHolder.getPositionId();
        try {
            TaskModel taskModel = taskApi.findById(tenantId, taskId).getData();
            if (taskModel == null) {
                return Y9Result.failure("任务不存在");
            }
            Map<String, Object> result = new HashMap<>(16);
            String multiInstance = processDefinitionApi
                .getNodeType(tenantId, taskModel.getProcessDefinitionId(), taskModel.getTaskDefinitionKey())
                .getData();
            if (isParallelInstance(multiInstance)) {
                handleParallelInstance(result, tenantId, positionId, taskModel);
            } else {
                result.put("isParallel", false);
                result.put(FlowableUiConsts.PARALLELDOING_KEY, "");
                result.put(FlowableUiConsts.COUNT_KEY, 0);
            }
            return Y9Result.success(result, "获取成功");
        } catch (Exception e) {
            LOGGER.error("获取协办人员办理情况失败", e);
            return Y9Result.failure("获取失败");
        }
    }

    /**
     * 判断是否为并行实例
     */
    private boolean isParallelInstance(String multiInstance) {
        return SysVariables.PARALLEL.equals(multiInstance);
    }

    /**
     * 处理并行实例情况
     */
    private void handleParallelInstance(Map<String, Object> result, String tenantId, String positionId,
        TaskModel taskModel) {
        result.put("isParallel", true);
        try {
            List<TaskModel> taskList =
                taskApi.findByProcessInstanceId(tenantId, taskModel.getProcessInstanceId(), true).getData();
            ParallelProcessingResult processingResult = processAssistantTasks(tenantId, positionId, taskList);
            result.put(FlowableUiConsts.PARALLELDOING_KEY, processingResult.assistantNames);
            result.put(FlowableUiConsts.COUNT_KEY, taskList != null ? taskList.size() - 1 : 0);
        } catch (Exception e) {
            LOGGER.warn("处理并行任务列表失败", e);
            result.put(FlowableUiConsts.PARALLELDOING_KEY, "");
            result.put(FlowableUiConsts.COUNT_KEY, 0);
        }
    }

    /**
     * 处理协办任务
     */
    private ParallelProcessingResult processAssistantTasks(String tenantId, String positionId,
        List<TaskModel> taskList) {
        StringBuilder assistantNames = new StringBuilder();
        int count = 0;
        if (taskList != null) {
            for (TaskModel task : taskList) {
                if (count >= 5) {
                    break;
                }
                String assigneeId = task.getAssignee();
                if (isAssistantUser(assigneeId, positionId)) {
                    OrgUnit employee = getEmployeeInfo(tenantId, assigneeId);
                    if (employee != null) {
                        appendAssistantName(assistantNames, employee.getName());
                        count++;
                    }
                }
            }
        }
        return new ParallelProcessingResult(assistantNames.toString());
    }

    /**
     * 判断是否为协办用户
     */
    private boolean isAssistantUser(String assigneeId, String positionId) {
        return StringUtils.isNotBlank(assigneeId) && !assigneeId.equals(positionId);
    }

    /**
     * 获取员工信息
     */
    private OrgUnit getEmployeeInfo(String tenantId, String assigneeId) {
        try {
            return orgUnitApi.getOrgUnitPersonOrPosition(tenantId, assigneeId).getData();
        } catch (Exception e) {
            LOGGER.warn("获取员工信息失败: assigneeId={}", assigneeId, e);
            return null;
        }
    }

    /**
     * 添加协办人员名称
     */
    private void appendAssistantName(StringBuilder names, String name) {
        if (names.length() == 0) {
            names.append(name);
        } else {
            names.append("、").append(name);
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
        String tenantId = Y9LoginUserHolder.getTenantId();
        String positionId = Y9FlowableHolder.getPositionId();
        try {
            // 收集任务信息
            BatchSignResult batchSignResult = collectBatchSignInfo(tenantId, taskIdAndProcessSerialNumbers);
            // 验证任务状态
            Y9Result<String> validationResult = validateBatchSignTasks(batchSignResult);
            if (validationResult != null) {
                return validationResult;
            }
            // 执行批量签收
            BatchOperationResult operationResult =
                executeBatchSign(tenantId, positionId, taskIdAndProcessSerialNumbers);
            return Y9Result
                .successMsg("签收成功" + operationResult.successCount + "条，签收失败" + operationResult.failCount + "条");
        } catch (Exception e) {
            LOGGER.error("批量签收失败", e);
            return Y9Result.failure("校验是否批量签收失败，发生异常");
        }
    }

    /**
     * 收集批量签收信息
     */
    private BatchSignResult collectBatchSignInfo(String tenantId, String[] taskIdAndProcessSerialNumbers) {
        BatchSignResult result = new BatchSignResult();
        for (String taskIdAndProcessSerialNumber : taskIdAndProcessSerialNumbers) {
            String[] tpArr = taskIdAndProcessSerialNumber.split(":");
            processTaskInfo(tenantId, tpArr, result);
        }
        return result;
    }

    /**
     * 处理单个任务信息
     */
    private void processTaskInfo(String tenantId, String[] tpArr, BatchSignResult result) {
        try {
            TaskModel task = taskApi.findById(tenantId, tpArr[0]).getData();
            ProcessParamModel ppModel = processParamApi.findByProcessSerialNumber(tenantId, tpArr[1]).getData();
            if (task == null) {
                appendTaskTitle(result.processedTaskMsg, ppModel.getTitle());
            } else if (StringUtils.isNotBlank(task.getAssignee())) {
                appendTaskTitle(result.signedTaskMsg, ppModel.getTitle());
            }
        } catch (Exception e) {
            LOGGER.warn("处理任务信息失败: taskId={}, processSerialNumber={}", tpArr[0], tpArr[1], e);
        }
    }

    /**
     * 添加任务标题到消息中
     */
    private void appendTaskTitle(StringBuilder msg, String title) {
        if (StringUtils.isBlank(msg.toString())) {
            msg.append(title);
        } else {
            msg.append(",").append(title);
        }
    }

    /**
     * 验证批量签收任务
     */
    private Y9Result<String> validateBatchSignTasks(BatchSignResult batchSignResult) {
        if (StringUtils.isNotBlank(batchSignResult.signedTaskMsg.toString())) {
            return Y9Result.failure("不能批量签收，以下待办已签收：" + batchSignResult.signedTaskMsg);
        }
        if (StringUtils.isNotBlank(batchSignResult.processedTaskMsg.toString())) {
            return Y9Result.failure("不能批量签收，以下待办已处理：" + batchSignResult.processedTaskMsg);
        }
        return null; // 验证通过
    }

    /**
     * 执行批量签收操作
     */
    private BatchOperationResult executeBatchSign(String tenantId, String positionId,
        String[] taskIdAndProcessSerialNumbers) {
        BatchOperationResult result = new BatchOperationResult();
        for (String taskIdAndProcessSerialNumber : taskIdAndProcessSerialNumbers) {
            String[] tpArr = taskIdAndProcessSerialNumber.split(":");
            executeSingleSign(tenantId, positionId, tpArr[0], result);
        }
        return result;
    }

    /**
     * 执行单个任务签收
     */
    private void executeSingleSign(String tenantId, String positionId, String taskId, BatchOperationResult result) {
        try {
            Y9Result<Object> y9Result = taskApi.claim(tenantId, positionId, taskId);
            if (y9Result.isSuccess()) {
                result.successCount++;
            } else {
                result.failCount++;
                LOGGER.warn("任务签收失败: taskId={}", taskId);
            }
        } catch (Exception e) {
            result.failCount++;
            LOGGER.error("任务签收异常: taskId={}", taskId, e);
        }
    }

    /**
     * 获取签收任务配置（用于判断是否直接发送）
     *
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @param taskDefinitionKey 任务key
     * @param processSerialNumber 流程编号
     * @return Y9Result<Map < String, Object>>
     */
    @GetMapping(value = "/signTaskConfig")
    public Y9Result<SignTaskConfigModel> signTaskConfig(@RequestParam @NotBlank String itemId,
        @RequestParam @NotBlank String processDefinitionId, @RequestParam @NotBlank String taskDefinitionKey,
        @RequestParam @NotBlank String processSerialNumber) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        return documentApi.signTaskConfig(tenantId, Y9FlowableHolder.getPositionId(), itemId, processDefinitionId,
            taskDefinitionKey, processSerialNumber);
    }

    /**
     * 办件发送
     *
     * @param itemId 事项id
     * @param taskId 任务id
     * @param processSerialNumber 流程编号
     * @return Y9Result<Object>
     */
    @PostMapping(value = "/submitTo")
    public Y9Result<Object> submitTo(@RequestParam @NotBlank String itemId,
        @RequestParam(required = false) String taskId, @RequestParam @NotBlank String processSerialNumber) {
        return documentApi.saveAndSubmitTo(Y9LoginUserHolder.getTenantId(), Y9FlowableHolder.getPositionId(), taskId,
            itemId, processSerialNumber);
    }

    /**
     * 获取用户选人发送界面数据
     *
     * @param itemId 事项id
     * @param routeToTask 任务路由
     * @param processDefinitionId 流程定义id
     * @param taskId 任务id
     * @param processInstanceId 流程实例id
     * @return Y9Result<DocUserChoiseModel>
     */
    @GetMapping(value = "/userChoiseData")
    public Y9Result<DocUserChoiseModel> userChoiseData(@RequestParam @NotBlank String itemId,
        @RequestParam @NotBlank String routeToTask, @RequestParam @NotBlank String processDefinitionId,
        @RequestParam(required = false) String taskId, @RequestParam(required = false) String processInstanceId) {
        return documentApi.docUserChoise(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPersonId(),
            Y9FlowableHolder.getPositionId(), itemId, "", processDefinitionId, taskId, routeToTask, processInstanceId);
    }

    /**
     * 批量签收结果封装类
     */
    private static class BatchSignResult {
        final StringBuilder processedTaskMsg = new StringBuilder();
        final StringBuilder signedTaskMsg = new StringBuilder();
    }

    /**
     * 批量操作结果封装类
     */
    private static class BatchOperationResult {
        int successCount = 0;
        int failCount = 0;
    }

    /**
     * 并行处理结果封装类
     */
    private static class ParallelProcessingResult {
        final String assistantNames;

        ParallelProcessingResult(String assistantNames) {
            this.assistantNames = assistantNames;
        }
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
