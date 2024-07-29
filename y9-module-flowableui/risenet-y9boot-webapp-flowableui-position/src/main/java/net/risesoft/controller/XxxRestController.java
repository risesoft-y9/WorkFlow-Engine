package net.risesoft.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import net.risesoft.api.itemadmin.FormDataApi;
import net.risesoft.api.itemadmin.position.ButtonOperation4PositionApi;
import net.risesoft.api.itemadmin.position.Document4PositionApi;
import net.risesoft.api.itemadmin.position.Item4PositionApi;
import net.risesoft.api.itemadmin.position.OfficeDoneInfo4PositionApi;
import net.risesoft.api.itemadmin.position.ProcessTrack4PositionApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.api.processadmin.HistoricTaskApi;
import net.risesoft.api.processadmin.IdentityApi;
import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.ProcessTodoApi;
import net.risesoft.api.processadmin.RepositoryApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.api.processadmin.VariableApi;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.model.itemadmin.BindFormModel;
import net.risesoft.model.itemadmin.HistoricActivityInstanceModel;
import net.risesoft.model.itemadmin.ItemModel;
import net.risesoft.model.itemadmin.OfficeDoneInfoModel;
import net.risesoft.model.platform.Position;
import net.risesoft.model.processadmin.HistoricTaskInstanceModel;
import net.risesoft.model.processadmin.IdentityLinkModel;
import net.risesoft.model.processadmin.TargetModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9Util;

/**
 * xxx接口
 *
 * @author zhangchongjie
 * @date 2024/06/05
 */
@Validated
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping(value = "/vue/xxx", produces = MediaType.APPLICATION_JSON_VALUE)
public class XxxRestController {

    private final ProcessTodoApi processTodoApi;

    private final OfficeDoneInfo4PositionApi officeDoneInfo4PositionApi;

    private final Document4PositionApi document4PositionApi;

    private final Item4PositionApi item4PositionApi;

    private final TaskApi taskApi;

    private final PositionApi positionApi;

    private final IdentityApi identityApi;

    private final ProcessTrack4PositionApi processTrack4PositionApi;

    private final FormDataApi formDataApi;

    private final RepositoryApi repositoryApi;

    private final HistoricTaskApi historicTaskApi;

    private final ButtonOperation4PositionApi buttonOperation4PositionApi;

    private final ProcessDefinitionApi processDefinitionApi;

    private final VariableApi variableApi;

    private List<String> getAssigneeIdsAndAssigneeNames(List<TaskModel> taskList) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String userId = Y9LoginUserHolder.getPositionId();
        String taskIds = "", assigneeIds = "", assigneeNames = "", itembox = ItemBoxTypeEnum.DOING.getValue(),
            taskId = "";
        List<String> list = new ArrayList<>();
        int i = 0;
        if (!taskList.isEmpty()) {
            for (TaskModel task : taskList) {
                if (StringUtils.isEmpty(taskIds)) {
                    taskIds = task.getId();
                    String assignee = task.getAssignee();
                    if (StringUtils.isNotBlank(assignee)) {
                        assigneeIds = assignee;
                        Position personTemp = positionApi.get(tenantId, assignee).getData();
                        if (personTemp != null) {
                            assigneeNames = personTemp.getName();
                        }
                        i += 1;
                        if (assignee.contains(userId)) {
                            itembox = ItemBoxTypeEnum.TODO.getValue();
                            taskId = task.getId();
                        }
                    } else {// 处理单实例未签收的当前办理人显示
                        List<IdentityLinkModel> iList =
                            identityApi.getIdentityLinksForTask(tenantId, task.getId()).getData();
                        if (!iList.isEmpty()) {
                            int j = 0;
                            for (IdentityLinkModel identityLink : iList) {
                                String assigneeId = identityLink.getUserId();
                                Position ownerUser =
                                    positionApi.get(Y9LoginUserHolder.getTenantId(), assigneeId).getData();
                                if (j < 5) {
                                    assigneeNames = Y9Util.genCustomStr(assigneeNames, ownerUser.getName(), "、");
                                    assigneeIds = Y9Util.genCustomStr(assigneeIds, assigneeId, SysVariables.COMMA);
                                } else {
                                    assigneeNames = assigneeNames + "等，共" + iList.size() + "人";
                                    break;
                                }
                                j++;
                            }
                        }
                    }
                } else {
                    String assignee = task.getAssignee();
                    if (StringUtils.isNotBlank(assignee)) {
                        if (i < 5) {
                            assigneeIds = Y9Util.genCustomStr(assigneeIds, assignee, SysVariables.COMMA);
                            Position personTemp = positionApi.get(tenantId, assignee).getData();
                            if (personTemp != null) {
                                assigneeNames = Y9Util.genCustomStr(assigneeNames, personTemp.getName(), "、");
                            }
                            i += 1;
                        }
                        if (assignee.contains(userId)) {
                            itembox = ItemBoxTypeEnum.TODO.getValue();
                            taskId = task.getId();
                        }
                    }
                }
            }
            if (taskList.size() > 5) {
                assigneeNames += "等，共" + taskList.size() + "人";
            }
        }
        list.add(taskIds);
        list.add(assigneeIds);
        list.add(assigneeNames);
        list.add(itembox);
        list.add(taskId);
        return list;
    }

    /**
     * 获取在办列表
     *
     * @param itemId 事项id
     * @param page 页码
     * @param rows 条数
     * @return Y9Page<Map<String, Object>>
     */
    @GetMapping(value = "/getDoingList")
    public Y9Page<Map<String, Object>> getDoingList(@RequestParam(required = false) String itemId,
        @RequestParam(required = false) String searchTerm, @RequestParam Integer page, @RequestParam Integer rows) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String positionId = Y9LoginUserHolder.getPositionId();
        Y9Page<OfficeDoneInfoModel> y9Page = officeDoneInfo4PositionApi.searchAllByPositionId(tenantId, positionId,
            searchTerm, itemId, "", "todo", "", "", "", page, rows);
        List<Map<String, Object>> list = new ArrayList<>();
        int serialNumber = (page - 1) * rows;
        for (OfficeDoneInfoModel task : y9Page.getRows()) {
            Map<String, Object> map = new HashMap<>(16);
            try {
                map.put("creatUserName", task.getCreatUserName());
                map.put("processDefinitionId", task.getProcessDefinitionId());
                map.put("processInstanceId", task.getProcessInstanceId());
                map.put("createTime", task.getStartTime());
                map.put("processSerialNumber", task.getProcessSerialNumber());
                map.put("title", task.getTitle());
                map.put("itemId", task.getItemId());
                map.put("itemName", task.getItemName());
                map.put("systemName", task.getSystemName());
                map.put("itembox", ItemBoxTypeEnum.DONE.getValue());
                map.put("taskAssignee", task.getUserComplete());
                map.put("endTime", StringUtils.isBlank(task.getEndTime()) ? "--" : task.getEndTime().substring(0, 16));
                if (StringUtils.isBlank(task.getEndTime())) {
                    List<TaskModel> taskList =
                        taskApi.findByProcessInstanceId(tenantId, task.getProcessInstanceId()).getData();
                    List<String> listTemp = getAssigneeIdsAndAssigneeNames(taskList);
                    String taskIds = listTemp.get(0), assigneeIds = listTemp.get(1), assigneeNames = listTemp.get(2);
                    map.put("taskDefinitionKey", taskList.get(0).getTaskDefinitionKey());
                    map.put("taskName", taskList.get(0).getName());
                    map.put("taskId",
                        listTemp.get(3).equals(ItemBoxTypeEnum.DOING.getValue()) ? taskIds : listTemp.get(4));
                    map.put("taskAssigneeId", assigneeIds);
                    map.put("taskAssignee", assigneeNames);
                    map.put("itembox", listTemp.get(3));
                }
            } catch (Exception e) {
                LOGGER.error("获取在办列表失败{}", e, task.getProcessInstanceId());
            }
            map.put("serialNumber", serialNumber + 1);
            serialNumber += 1;
            list.add(map);
        }
        return Y9Page.success(page, rows, y9Page.getTotal(), list);
    }

    /**
     * 获取事项统计
     *
     * @return Y9Result<Map<String, Object>>
     */
    @GetMapping(value = "/getMyCount")
    public Y9Result<Map<String, Object>> getMyCount() {
        Map<String, Object> map = new HashMap<>(16);
        String tenantId = Y9LoginUserHolder.getTenantId();
        String positionId = Y9LoginUserHolder.getPositionId();
        long todoCount;
        long doingCount;
        int doneCount;
        // 统计待办
        todoCount = processTodoApi.countByPositionId(tenantId, positionId).getData();
        // 统计流程在办件
        Y9Page<OfficeDoneInfoModel> y9Page = officeDoneInfo4PositionApi.searchAllByPositionId(tenantId, positionId, "",
            "", "", "todo", "", "", "", 1, 1);
        doingCount = y9Page.getTotal();
        // 统计历史办结件
        doneCount = officeDoneInfo4PositionApi.countByPositionId(tenantId, positionId, "").getData();
        map.put("todoCount", todoCount);
        map.put("doingCount", doingCount);
        map.put("doneCount", doneCount);
        return Y9Result.success(map, "获取成功");
    }

    /**
     * 获取重定向任务列表
     *
     * @param processInstanceId 流程实例id
     * @return Y9Result<List<HistoricTaskInstanceModel>>
     */
    @GetMapping(value = "/getRepositionTask")
    public Y9Result<List<HistoricTaskInstanceModel>>
        getRepositionTask(@RequestParam @NotBlank String processInstanceId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        return historicTaskApi.findTaskByProcessInstanceIdOrderByStartTimeAsc(tenantId, processInstanceId, "");
    }

    /**
     * 获取任务信息
     *
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @param taskKey 任务key
     * @return Y9Result<Map<String, Object>>
     */
    @GetMapping(value = "/getTaskInfo")
    public Y9Result<Map<String, Object>> getTaskInfo(@RequestParam @NotBlank String itemId,
        @RequestParam @NotBlank String processDefinitionId, @RequestParam(required = false) String taskKey) {
        Map<String, Object> map = new HashMap<>(16);
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<BindFormModel> list =
            formDataApi.findFormItemBind(tenantId, itemId, processDefinitionId, taskKey).getData();
        String formName = "";
        for (BindFormModel bindFormModel : list) {
            formName = Y9Util.genCustomStr(formName, bindFormModel.getFormName());
        }
        map.put("formName", formName);
        List<String> userChoiceList = document4PositionApi
            .parserUser(tenantId, Y9LoginUserHolder.getPositionId(), itemId, processDefinitionId, taskKey, "", "", "")
            .getData();
        String positionName = "";
        if (userChoiceList != null) {
            for (String userChoice : userChoiceList) {
                Position position = positionApi.get(tenantId, userChoice).getData();
                if (position != null) {
                    positionName = Y9Util.genCustomStr(positionName, position.getName());
                }
            }
        }
        map.put("positionName", positionName);
        String processDefinitionName =
            repositoryApi.getProcessDefinitionById(tenantId, processDefinitionId).getData().getName();
        map.put("processDefinitionName", processDefinitionName);
        return Y9Result.success(map, "获取成功");
    }

    /**
     * 获取流程图任务节点信息
     *
     * @param processInstanceId 流程实例id
     * @return Y9Result<List<HistoricActivityInstanceModel>>
     */
    @GetMapping(value = "/getTaskList")
    public Y9Result<List<HistoricActivityInstanceModel>> getTaskList(@RequestParam @NotBlank String processInstanceId) {
        return processTrack4PositionApi.getTaskList(Y9LoginUserHolder.getTenantId(), processInstanceId);
    }

    /**
     * 获取待办列表
     *
     * @param itemId 事项id
     * @param page 页码
     * @param rows 条数
     * @return Y9Page<Map<String, Object>>
     */
    @GetMapping(value = "/getTodoList")
    public Y9Page<Map<String, Object>> getTodoList(@RequestParam(required = false) String itemId,
        @RequestParam Integer page, @RequestParam Integer rows) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String positionId = Y9LoginUserHolder.getPositionId();
        int serialNumber = (page - 1) * rows;
        Y9Page<TaskModel> y9Page;
        if (StringUtils.isBlank(itemId)) {
            y9Page = processTodoApi.pageByUserId(tenantId, positionId, page, rows);
        } else {
            ItemModel item = item4PositionApi.getByItemId(tenantId, itemId).getData();
            y9Page = processTodoApi.getListByUserIdAndProcessDefinitionKey(tenantId, positionId, item.getWorkflowGuid(),
                page, rows);
        }
        List<Map<String, Object>> list = new ArrayList<>();
        for (TaskModel task : y9Page.getRows()) {
            Map<String, Object> map = new HashMap<>(16);
            try {
                map.put("taskId", task.getId());
                map.put("taskName", task.getName());
                map.put("processDefinitionId", task.getProcessDefinitionId());
                map.put("processInstanceId", task.getProcessInstanceId());
                map.put("taskDefinitionKey", task.getTaskDefinitionKey());
                OfficeDoneInfoModel doneInfo =
                    officeDoneInfo4PositionApi.findByProcessInstanceId(tenantId, task.getProcessInstanceId()).getData();
                map.put("createTime", doneInfo.getStartTime());
                map.put("processSerialNumber", doneInfo.getProcessSerialNumber());
                map.put("title", doneInfo.getTitle());
                map.put("itemId", doneInfo.getItemId());
                map.put("itemName", doneInfo.getItemName());
                map.put("systemName", doneInfo.getSystemName());
            } catch (Exception e) {
                LOGGER.error("获取待办列表失败{}", e, task.getProcessInstanceId());
            }
            map.put("serialNumber", serialNumber + 1);
            serialNumber += 1;
            list.add(map);
        }
        return Y9Page.success(page, rows, y9Page.getTotal(), list);
    }

    /**
     * 重定向，不用选人
     *
     * @param routeToTaskId 任务key
     * @param taskId 任务id
     * @param processInstanceId 流程实例id
     * @return Y9Result<Object>
     */
    @PostMapping(value = "/reposition")
    public Y9Result<Object> reposition(@RequestParam @NotBlank String routeToTaskId,
        @RequestParam @NotBlank String taskId, @RequestParam @NotBlank String processInstanceId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String positionId = Y9LoginUserHolder.getPositionId();
        OfficeDoneInfoModel officeDoneInfo =
            officeDoneInfo4PositionApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
        String itemId = officeDoneInfo.getItemId();
        String processDefinitionId = officeDoneInfo.getProcessDefinitionId();
        String multiInstance = processDefinitionApi.getNodeType(tenantId, processDefinitionId, routeToTaskId).getData();
        List<String> userChoiceList = document4PositionApi.parserUser(tenantId, positionId, itemId, processDefinitionId,
            routeToTaskId, routeToTaskId, processInstanceId, multiInstance).getData();
        Map<String, Object> map = new HashMap<>();
        map.put("val", false);
        variableApi.setVariable(tenantId, taskId, "stopProcess", map);
        if (userChoiceList == null || userChoiceList.size() == 0) {
            return Y9Result.failure("目标节点未授权人员");
        }
        return buttonOperation4PositionApi.reposition(tenantId, positionId, taskId, routeToTaskId, userChoiceList, "",
            "");
    }

    /**
     * 开始流程
     *
     * @param taskId 任务id
     * @param itemId 事项id
     * @param processSerialNumber 流程编号
     * @return Y9Result<Object>
     */
    @PostMapping(value = "/startProcess")
    public Y9Result<Object> startProcess(@RequestParam String taskId, @RequestParam String itemId,
        @RequestParam String processSerialNumber) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String positionId = Y9LoginUserHolder.getPositionId();
        Map<String, Object> map = new HashMap<>();
        map.put("val", false);
        variableApi.setVariable(tenantId, taskId, "stopProcess", map);
        TaskModel task = taskApi.findById(tenantId, taskId).getData();
        List<TargetModel> targetModelList = processDefinitionApi
            .getTargetNodes(tenantId, task.getProcessDefinitionId(), task.getTaskDefinitionKey()).getData();
        if (targetModelList.size() > 0) {// 有目标任务才执行提交
            return document4PositionApi.saveAndSubmitTo(tenantId, positionId, taskId, itemId, processSerialNumber);
        }
        return Y9Result.success();
    }

    /**
     * 停止流程
     *
     * @param taskId 任务id
     * @return Y9Result<Object>
     */
    @PostMapping(value = "/stopProcess")
    public Y9Result<Object> stopProcess(@RequestParam String taskId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        TaskModel task = taskApi.findById(tenantId, taskId).getData();
        if (task.getTaskDefinitionKey().contains("skip_")) {
            Map<String, Object> map = new HashMap<>();
            map.put("val", true);
            return variableApi.setVariable(tenantId, taskId, "stopProcess", map);
        }
        return Y9Result.success();
    }

    /**
     * 提交流程
     *
     * @param itemId 事项id
     * @param taskId 任务id
     * @param processSerialNumber 流程编号
     * @return Y9Result<Object>
     */
    @PostMapping(value = "/submitTo")
    public Y9Result<Object> submitTo(@RequestParam @NotBlank String itemId,
        @RequestParam(required = false) String taskId, @RequestParam @NotBlank String processSerialNumber) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String positionId = Y9LoginUserHolder.getPositionId();
        Map<String, Object> map = new HashMap<>();
        map.put("val", false);
        variableApi.setVariable(tenantId, taskId, "stopProcess", map);
        return document4PositionApi.saveAndSubmitTo(tenantId, positionId, taskId, itemId, processSerialNumber);
    }
}
