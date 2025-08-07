package net.risesoft.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.AssociatedFileApi;
import net.risesoft.api.itemadmin.AttachmentApi;
import net.risesoft.api.itemadmin.ChaoSongApi;
import net.risesoft.api.itemadmin.OfficeFollowApi;
import net.risesoft.api.itemadmin.SignDeptDetailApi;
import net.risesoft.api.itemadmin.SpeakInfoApi;
import net.risesoft.api.itemadmin.TaskRelatedApi;
import net.risesoft.api.itemadmin.TransactionWordApi;
import net.risesoft.api.itemadmin.core.ActRuDetailApi;
import net.risesoft.api.itemadmin.core.DocumentApi;
import net.risesoft.api.itemadmin.core.ItemApi;
import net.risesoft.api.itemadmin.core.ProcessParamApi;
import net.risesoft.api.itemadmin.documentword.DocumentWordApi;
import net.risesoft.api.itemadmin.form.FormDataApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.permission.PositionRoleApi;
import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.ProcessTodoApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.consts.processadmin.SysVariables;
import net.risesoft.enums.ActRuDetailStatusEnum;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.enums.SignDeptDetailStatusEnum;
import net.risesoft.enums.TaskRelatedEnum;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.log.FlowableLogLevelEnum;
import net.risesoft.log.FlowableOperationTypeEnum;
import net.risesoft.log.annotation.FlowableLog;
import net.risesoft.model.itemadmin.DocUserChoiseModel;
import net.risesoft.model.itemadmin.ItemButtonModel;
import net.risesoft.model.itemadmin.ItemListModel;
import net.risesoft.model.itemadmin.ItemStartNodeRoleModel;
import net.risesoft.model.itemadmin.OpenDataModel;
import net.risesoft.model.itemadmin.SignDeptDetailModel;
import net.risesoft.model.itemadmin.SignTaskConfigModel;
import net.risesoft.model.itemadmin.StartProcessResultModel;
import net.risesoft.model.itemadmin.TaskRelatedModel;
import net.risesoft.model.itemadmin.TransactionWordModel;
import net.risesoft.model.itemadmin.core.ActRuDetailModel;
import net.risesoft.model.itemadmin.core.DocumentDetailModel;
import net.risesoft.model.itemadmin.core.ItemModel;
import net.risesoft.model.itemadmin.core.ProcessParamModel;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Position;
import net.risesoft.model.processadmin.TargetModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.ButtonOperationService;
import net.risesoft.service.ProcessParamService;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.configuration.app.flowble.Y9FlowableProperties;
import net.risesoft.y9.json.Y9JsonUtil;

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

    private final AttachmentApi attachmentApi;

    private final TransactionWordApi transactionWordApi;

    private final ChaoSongApi chaoSongApi;

    private final TaskApi taskApi;

    private final ProcessDefinitionApi processDefinitionApi;

    private final OrgUnitApi orgUnitApi;

    private final SignDeptDetailApi signDeptDetailApi;

    private final PositionRoleApi positionRoleApi;

    private final SpeakInfoApi speakInfoApi;

    private final AssociatedFileApi associatedFileApi;

    private final OfficeFollowApi officeFollowApi;

    private final ProcessTodoApi processTodoApi;

    private final ProcessParamService processParamService;

    private final TaskRelatedApi taskRelatedApi;

    private final ActRuDetailApi actRuDetailApi;

    private final DocumentWordApi documentWordApi;

    private final FormDataApi formDataApi;

    private final Y9FlowableProperties y9FlowableProperties;

    @Resource(name = "jdbcTemplate4Tenant")
    private JdbcTemplate jdbcTemplate;

    /**
     * 获取新建办件初始化数据
     *
     * @param itemId 事项id
     * @return Y9Result<Map < String, Object>>
     */
    @GetMapping(value = "/add")
    public Y9Result<OpenDataModel> add(@RequestParam @NotBlank String itemId) {
        return documentApi.add(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPositionId(), itemId, false);
    }

    /**
     * 获取新建办件初始化数据
     *
     * @param itemId 事项id
     * @param startTaskDefKey 开始节点定义key
     * @return Y9Result<DocumentDetailModel>
     */
    @GetMapping(value = "/addWithStartTaskDefKey")
    public Y9Result<DocumentDetailModel> addWithStartTaskDefKey(@RequestParam @NotBlank String itemId,
        @RequestParam @NotBlank String startTaskDefKey) {
        return documentApi.addWithStartTaskDefKey(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPositionId(),
            itemId, startTaskDefKey, false);
    }

    /**
     * 检查是否可以批量发送
     *
     * @param taskIdAndProcessSerialNumbers
     * @return
     */
    @PostMapping(value = "/check4Batch")
    public Y9Result<List<TargetModel>> check4Batch(@RequestParam String[] taskIdAndProcessSerialNumbers) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            StringBuilder msg = new StringBuilder();
            List<String> list = new ArrayList<>();
            List<TaskModel> signList = new ArrayList<>();
            List<TaskModel> taskList = new ArrayList<>();
            Arrays.stream(taskIdAndProcessSerialNumbers).forEach(tp -> {
                String[] tpArr = tp.split(":");
                TaskModel task = taskApi.findById(tenantId, tpArr[0]).getData();
                if (null == task) {
                    ProcessParamModel ppModel = processParamApi.findByProcessSerialNumber(tenantId, tpArr[1]).getData();
                    if (StringUtils.isBlank(msg)) {
                        msg.append(ppModel.getTitle());
                    } else {
                        msg.append(",").append(ppModel.getTitle());
                    }
                } else {
                    taskList.add(task);
                    if (StringUtils.isBlank(task.getAssignee())) {
                        signList.add(task);
                    }
                    if (!list.contains(task.getTaskDefinitionKey())) {
                        list.add(task.getTaskDefinitionKey());
                    }
                }
            });
            if (!signList.isEmpty()) {
                return Y9Result.failure("不能批量发送，存在未签收的待办");
            }
            if (StringUtils.isNotBlank(msg)) {
                return Y9Result.failure("不能批量发送，以下待办已处理：" + msg);
            }
            if (list.size() > 1) {
                return Y9Result.failure("不能批量发送，存在不同的办理环节");
            }
            List<TargetModel> routeToTasks = processDefinitionApi
                .getTargetNodes(tenantId, taskList.get(0).getProcessDefinitionId(),
                    taskList.get(0).getTaskDefinitionKey())
                .getData().stream()
                .filter(m -> !"退回".equals(m.getTaskDefName()) && !"Exclusive Gateway".equals(m.getTaskDefName()))
                .collect(Collectors.toList());
            return Y9Result.success(routeToTasks);
        } catch (Exception e) {
            LOGGER.error("校验失败", e);
        }
        return Y9Result.failure("校验失败，发生异常");
    }

    /**
     * 检查是否可以批量重定向
     *
     * @param processSerialNumber
     * @param documentId
     * @return
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
                if (!signDeptDetail.getStatus().equals(SignDeptDetailStatusEnum.DOING.getValue())) {
                    return Y9Result.failure("不能重定向，" + signDeptDetail.getDeptName() + "的会签件已被处理。");
                }
                List<TargetModel> subTargetModelList = processDefinitionApi
                    .getSubProcessChildNode(tenantId, taskList.get(0).getProcessDefinitionId()).getData();
                return Y9Result.success(subTargetModelList);
            }
        } catch (Exception e) {
            LOGGER.error("校验失败", e);
        }
        return Y9Result.failure("校验失败，发生异常");
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
     * @param taskIdAndProcessSerialNumbers 任务id和流程序列号集合
     * @return Y9Result<String>
     */
    @FlowableLog(operationName = "批量办结", operationType = FlowableOperationTypeEnum.COMPLETE)
    @PostMapping(value = "/completeTodo")
    public Y9Result<String> completeTodo(@RequestParam String[] taskIdAndProcessSerialNumbers) {
        for (String tandp : taskIdAndProcessSerialNumbers) {
            try {
                String[] tpArr = tandp.split(":");
                String taskId = tpArr[0];
                String guid = tpArr[1];
                TaskModel task = taskApi.findById(Y9LoginUserHolder.getTenantId(), taskId).getData();
                if (null == task) {
                    continue;
                }
                buttonOperationService.complete(taskId, "办结", "已办结", "");
                jdbcTemplate.execute("update y9_form_fw set bbhBanjie = '[\"1\"]' where guid = '" + guid + "'");
            } catch (Exception e) {
                LOGGER.error("流程办结失败", e);
            }
        }
        return Y9Result.successMsg("办结成功");
    }

    /**
     * 复制并起草
     *
     * @param processSerialNumber
     * @return
     */
    @FlowableLog(operationName = "复制并起草", operationType = FlowableOperationTypeEnum.DRAFT)
    @PostMapping(value = "/copy2Todo")
    public Y9Result<Map<String, Object>> copy2Todo(@RequestParam @NotBlank String processSerialNumber,
        @RequestParam(required = false) String startTaskDefKey) {
        String tenantId = Y9LoginUserHolder.getTenantId(), personId = Y9LoginUserHolder.getPersonId();
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
        Position position = Y9LoginUserHolder.getPosition();
        // 5 设置原文件的相关信息
        TaskRelatedModel sourceTaskRelated = new TaskRelatedModel();
        sourceTaskRelated.setInfoType(TaskRelatedEnum.YUAN.getValue());
        sourceTaskRelated.setProcessInstanceId(oldProcessParam.getProcessInstanceId());
        sourceTaskRelated.setProcessSerialNumber(processSerialNumber);
        sourceTaskRelated.setExecutionId(oldProcessParam.getProcessInstanceId());
        sourceTaskRelated.setTaskId("copy2Todo");
        sourceTaskRelated.setSub(false);
        // sourceTaskRelated.setMsgContent(numberY9Result.getData());
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
            .filter(actRuDetailModel -> actRuDetailModel.getTaskId().equals(startProcess.getTaskId())).findFirst()
            .orElse(null);
        Map<String, Object> map = new HashMap<>();
        assert actRuDetail != null;
        map.put("taskId", actRuDetail.getTaskId());
        map.put("taskName", actRuDetail.getTaskDefName());
        map.put("actRuDetailId", actRuDetail.getId());
        // map.put("lsh", numberY9Result.getData());
        map.put("userName", actRuDetail.getAssigneeName());
        if (yuanResult.isSuccess() && fuResult.isSuccess()) {
            return Y9Result.success(map);
        }
        return Y9Result.success(map, "复制并起草成功，但是建立关联关系失败！");
    }

    /**
     * 获取编辑办件数据
     *
     * @param itembox 办件状态
     * @param taskId 任务id
     * @param processInstanceId 流程实例id
     * @param itemId 事项id
     * @return Y9Result<Map < String, Object>>
     */
    @GetMapping(value = "/edit")
    public Y9Result<Map<String, Object>> edit(@RequestParam @NotBlank String itembox,
        @RequestParam(required = false) String taskId, @RequestParam @NotBlank String processInstanceId,
        @RequestParam @NotBlank String itemId) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId(), userId = person.getPersonId();
        String monitor = itembox;
        if (itembox.equals("monitorDone") || itembox.equals("monitorRecycle")) {
            itembox = ItemBoxTypeEnum.DONE.getValue();
        }
        try {
            OpenDataModel model = documentApi.edit(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPositionId(),
                itembox, taskId, processInstanceId, itemId, false).getData();

            String str = Y9JsonUtil.writeValueAsString(model);
            Map<String, Object> map = Y9JsonUtil.readHashMap(str);
            String processSerialNumber = model.getProcessSerialNumber();
            Integer fileNum = attachmentApi.fileCounts(tenantId, processSerialNumber).getData();
            int docNum = 0;
            // 是否正文正常
            TransactionWordModel wordMap =
                transactionWordApi.findWordByProcessSerialNumber(tenantId, processSerialNumber).getData();
            if (wordMap != null && wordMap.getId() != null) {
                docNum = 1;
            }
            int speakInfoNum = speakInfoApi.getNotReadCount(tenantId, userId, processInstanceId).getData();
            int associatedFileNum = associatedFileApi.countAssociatedFile(tenantId, processSerialNumber).getData();
            map.put("speakInfoNum", speakInfoNum);
            map.put("associatedFileNum", associatedFileNum);
            map.put("docNum", docNum);
            map.put("monitor", monitor);
            map.put("fileNum", fileNum);
            map.put("userName", Y9LoginUserHolder.getPosition().getName());
            map.put("tenantId", tenantId);
            map.put("userId", userId);
            int follow = officeFollowApi
                .countByProcessInstanceId(tenantId, Y9LoginUserHolder.getPositionId(), processInstanceId).getData();
            map.put("follow", follow > 0);
            return Y9Result.success(map, "获取成功");
        } catch (Exception e) {
            LOGGER.error("获取编辑办件数据失败", e);
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 获取传签件办件数据
     *
     * @param processSerialNumber 流程序列号
     * @return
     */
    @FlowableLog(operationName = "传签件详情")
    @GetMapping(value = "/editCopy")
    public Y9Result<DocumentDetailModel> editCopy(@RequestParam @NotBlank String processSerialNumber) {
        try {
            DocumentDetailModel model = documentApi.editCopy(Y9LoginUserHolder.getTenantId(),
                Y9LoginUserHolder.getPositionId(), processSerialNumber, false).getData();
            return Y9Result.success(model, "获取成功");
        } catch (Exception e) {
            LOGGER.error("获取编辑办件数据失败", e);
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 获取编辑办件数据
     *
     * @param processInstanceId 流程实例id
     * @return Y9Result<Map < String, Object>>
     */
    @FlowableLog(operationName = "在办详情")
    @GetMapping(value = "/editDoing")
    public Y9Result<DocumentDetailModel> editDoing(@RequestParam @NotBlank String processInstanceId,
        @RequestParam @NotBlank String documentId) {
        try {
            DocumentDetailModel model =
                documentApi.editDoing(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPositionId(),
                    processInstanceId, documentId, false, ItemBoxTypeEnum.DOING).getData();
            return Y9Result.success(model, "获取成功");
        } catch (Exception e) {
            LOGGER.error("获取编辑办件数据失败", e);
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 获取编辑办件数据
     *
     * @param processInstanceId 流程实例id
     * @return Y9Result<Map < String, Object>>
     */
    @FlowableLog(operationName = "监控在办件详情", logLevel = FlowableLogLevelEnum.ADMIN)
    @GetMapping(value = "/editDoing4Admin")
    public Y9Result<DocumentDetailModel> editDoing4Admin(@RequestParam @NotBlank String processInstanceId,
        @RequestParam @NotBlank String documentId) {
        try {
            DocumentDetailModel model =
                documentApi.editDoing(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPositionId(),
                    processInstanceId, documentId, true, ItemBoxTypeEnum.MONITOR_DOING).getData();
            return Y9Result.success(model, "获取成功");
        } catch (Exception e) {
            LOGGER.error("获取编辑办件数据失败", e);
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 获取编辑办件数据
     *
     * @param processInstanceId 流程实例id
     * @return Y9Result<Map < String, Object>>
     */
    @FlowableLog(operationName = "办结详情")
    @GetMapping(value = "/editDone")
    public Y9Result<DocumentDetailModel> editDone(@RequestParam @NotBlank String processInstanceId,
        @RequestParam @NotBlank String documentId) {
        try {
            DocumentDetailModel model =
                documentApi.editDone(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPositionId(),
                    processInstanceId, documentId, false, ItemBoxTypeEnum.DONE).getData();
            return Y9Result.success(model, "获取成功");
        } catch (Exception e) {
            LOGGER.error("获取编辑办件数据失败", e);
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 获取编辑办件数据
     *
     * @param processInstanceId 流程实例id
     * @return Y9Result<Map < String, Object>>
     */
    @FlowableLog(operationName = "监控办结详情", logLevel = FlowableLogLevelEnum.ADMIN)
    @GetMapping(value = "/editDone4Admin")
    public Y9Result<DocumentDetailModel> editDone4Admin(@RequestParam @NotBlank String processInstanceId,
        @RequestParam @NotBlank String documentId, @RequestParam @NotBlank String itemBox) {
        try {
            DocumentDetailModel model =
                documentApi.editDone(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPositionId(),
                    processInstanceId, documentId, true, ItemBoxTypeEnum.fromString(itemBox)).getData();
            return Y9Result.success(model, "获取成功");
        } catch (Exception e) {
            LOGGER.error("获取编辑办件数据失败", e);
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 获取回收站办件数据
     *
     * @param processInstanceId 流程实例id
     * @return Y9Result<Map < String, Object>>
     */
    @FlowableLog(operationName = "回收件详情")
    @GetMapping(value = "/editRecycle")
    public Y9Result<DocumentDetailModel> editRecycle(@RequestParam @NotBlank String processInstanceId) {
        try {
            DocumentDetailModel model = documentApi.editRecycle(Y9LoginUserHolder.getTenantId(),
                Y9LoginUserHolder.getPositionId(), processInstanceId, false).getData();
            return Y9Result.success(model, "获取成功");
        } catch (Exception e) {
            LOGGER.error("获取编辑办件数据失败", e);
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 获取编辑办件数据
     *
     * @param taskId 任务id
     * @return Y9Result<Map < String, Object>>
     */
    @FlowableLog(operationName = "待办详情")
    @GetMapping(value = "/editTodo")
    public Y9Result<DocumentDetailModel> editTodo(@RequestParam @NotBlank String taskId) {
        try {
            TaskModel task = taskApi.findById(Y9LoginUserHolder.getTenantId(), taskId).getData();
            if (null == task) {
                return Y9Result.failure("当前待办已处理！");
            }
            DocumentDetailModel model = documentApi
                .editTodo(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPositionId(), taskId, false).getData();
            return Y9Result.success(model, "获取成功");
        } catch (Exception e) {
            LOGGER.error("获取编辑办件数据失败", e);
        }
        return Y9Result.failure("获取失败");
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
            ProcessParamModel processParamModel = processParamApi
                .findByProcessSerialNumber(Y9LoginUserHolder.getTenantId(), processSerialNumber).getData();
            processParamModel.setIsSendSms(isSendSms);
            processParamModel.setIsShuMing(isShuMing);
            processParamModel.setSmsContent(smsContent);
            processParamModel.setSmsPersonId("");
            processParamApi.saveOrUpdate(Y9LoginUserHolder.getTenantId(), processParamModel);
            Y9Result<String> y9Result = documentApi.saveAndForwarding(Y9LoginUserHolder.getTenantId(),
                Y9LoginUserHolder.getPositionId(), processInstanceId, taskId, sponsorHandle, itemId,
                processSerialNumber, processDefinitionKey, userChoice, sponsorGuid, routeToTaskId, variables);
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
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    processParamModel.setDueDate(sdf.parse(dueDate));
                }
                processParamModel.setDescription(description);
                processParamApi.saveOrUpdate(Y9LoginUserHolder.getTenantId(), processParamModel);

                Y9Result<String> y9Result = documentApi.forwarding(Y9LoginUserHolder.getTenantId(),
                    Y9LoginUserHolder.getPositionId(), tpArr[0], userChoice, routeToTaskId, sponsorHandle, sponsorGuid);
                if (y9Result.isSuccess()) {
                    success.getAndIncrement();
                } else {
                    fail.getAndIncrement();
                }
            } catch (ParseException e) {
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
        return documentApi.getAllStartTaskDefKey(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPositionId(),
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
            List<ItemButtonModel> buttonList = documentApi
                .getButtons(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPositionId(), taskId).getData();
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
                itemApi.getItemList(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPositionId()).getData();
            map.put("itemMap", listMap);
            map.put("notReadCount",
                chaoSongApi.getTodoCount(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPositionId()).getData());
            // int followCount = officeFollowApi.getFollowCount(tenantId, Y9LoginUserHolder.getPositionId());
            // map.put("followCount", followCount);
            // 公共角色
            boolean b = positionRoleApi.hasPublicRole(tenantId, y9FlowableProperties.getMonitorManageRoleName(),
                Y9LoginUserHolder.getPositionId()).getData();
            map.put("monitorManage", b);

            boolean b1 = positionRoleApi.hasRole(tenantId, Y9Context.getSystemName(), "",
                y9FlowableProperties.getLeaveManageRoleName(), Y9LoginUserHolder.getPositionId()).getData();
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
            String positionId = Y9LoginUserHolder.getPositionId();
            List<Map<String, Object>> list = new ArrayList<>();
            List<ItemModel> listMap = itemApi.getAllItem(Y9LoginUserHolder.getTenantId()).getData();
            for (ItemModel itemModel : listMap) {
                Map<String, Object> newmap = new HashMap<>(16);
                newmap.put("systemName", itemModel.getSystemName());
                newmap.put("systemCnName", itemModel.getSysLevel());
                if (!list.contains(newmap)) {
                    list.add(newmap);
                }
            }
            for (Map<String, Object> nmap : list) {
                long todoCount = processTodoApi
                    .getTodoCountByUserIdAndSystemName(tenantId, positionId, (String)nmap.get("systemName")).getData();
                nmap.put("todoCount", todoCount);
                List<ItemModel> itemList = new ArrayList<>();
                for (ItemModel itemModel : listMap) {
                    if (nmap.get("systemName").equals(itemModel.getSystemName())) {
                        itemList.add(itemModel);
                    }
                }
                nmap.put("itemList", itemList);
            }

            map.put("systemList", list);
            map.put("notReadCount",
                chaoSongApi.getTodoCount(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPositionId()).getData());
            // 公共角色
            boolean b = positionRoleApi.hasPublicRole(tenantId, y9FlowableProperties.getMonitorManageRoleName(),
                Y9LoginUserHolder.getPositionId()).getData();
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
     * @param taskId 任务id
     * @return Y9Result<Map < String, Object>>
     */
    @GetMapping(value = "/getParallelNames")
    public Y9Result<Map<String, Object>> getParallelNames(@RequestParam @NotBlank String taskId) {
        String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
        String parallelDoing = "";
        Map<String, Object> map = new HashMap<>(16);
        map.put("isParallel", false);
        int i = 0;
        List<TaskModel> list = null;
        try {
            TaskModel taskModel = taskApi.findById(tenantId, taskId).getData();
            String multiInstance = processDefinitionApi
                .getNodeType(tenantId, taskModel.getProcessDefinitionId(), taskModel.getTaskDefinitionKey()).getData();
            if (multiInstance.equals(SysVariables.PARALLEL)) {// 并行
                map.put("isParallel", true);
                list = taskApi.findByProcessInstanceId(tenantId, taskModel.getProcessInstanceId(), true).getData();
                for (TaskModel task : list) {
                    if (i < 5) {
                        String assigneeId = task.getAssignee();
                        OrgUnit employee = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, assigneeId).getData();
                        if (employee != null && !employee.getId().equals(positionId)) { // 协办人员
                            if (StringUtils.isBlank(parallelDoing)) {
                                parallelDoing = employee.getName();
                            } else {
                                parallelDoing = parallelDoing + "、" + employee.getName();
                            }
                            i++;
                        }
                    }
                }
            }
            map.put("parallelDoing", parallelDoing);
            map.put("count", list != null ? list.size() - 1 : 0);// 减去主办任务数
            return Y9Result.success(map, "获取成功");
        } catch (Exception e) {
            LOGGER.error("获取协办人员办理情况失败", e);
        }
        return Y9Result.failure("获取失败");
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

    @FlowableLog(operationName = "批量签收", operationType = FlowableOperationTypeEnum.CLAIM)
    @PostMapping(value = "/sign4Batch")
    public Y9Result<String> sign4Batch(@RequestParam String[] taskIdAndProcessSerialNumbers) {
        String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
        try {
            StringBuilder msg = new StringBuilder();
            StringBuilder msg4sign = new StringBuilder();
            Arrays.stream(taskIdAndProcessSerialNumbers).forEach(tp -> {
                String[] tpArr = tp.split(":");
                TaskModel task = taskApi.findById(tenantId, tpArr[0]).getData();
                ProcessParamModel ppModel = processParamApi.findByProcessSerialNumber(tenantId, tpArr[1]).getData();
                if (null == task) {
                    if (StringUtils.isBlank(msg)) {
                        msg.append(ppModel.getTitle());
                    } else {
                        msg.append(",").append(ppModel.getTitle());
                    }
                } else {
                    if (StringUtils.isNotBlank(task.getAssignee())) {
                        if (StringUtils.isBlank(msg4sign)) {
                            msg4sign.append(ppModel.getTitle());
                        } else {
                            msg4sign.append(",").append(ppModel.getTitle());
                        }
                    }
                }
            });
            if (StringUtils.isNotBlank(msg4sign)) {
                return Y9Result.failure("不能批量签收，以下待办已签收：" + msg4sign);
            }
            if (StringUtils.isNotBlank(msg)) {
                return Y9Result.failure("不能批量签收，以下待办已处理：" + msg);
            }
            AtomicInteger success = new AtomicInteger();
            AtomicInteger fail = new AtomicInteger();
            Arrays.stream(taskIdAndProcessSerialNumbers).forEach(tp -> {
                String[] tpArr = tp.split(":");
                Y9Result<Object> y9Result = taskApi.claim(tenantId, positionId, tpArr[0]);
                if (y9Result.isSuccess()) {
                    success.getAndIncrement();
                } else {
                    fail.getAndIncrement();
                }

            });
            return Y9Result.successMsg("签收成功" + success.get() + "条，签收失败" + fail.get() + "条");
        } catch (Exception e) {
            LOGGER.error("校验失败", e);
        }
        return Y9Result.failure("校验失败，发生异常");
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
        return documentApi.signTaskConfig(tenantId, Y9LoginUserHolder.getPositionId(), itemId, processDefinitionId,
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
        return documentApi.saveAndSubmitTo(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPositionId(), taskId,
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
            Y9LoginUserHolder.getPositionId(), itemId, "", processDefinitionId, taskId, routeToTask, processInstanceId);
    }
}
