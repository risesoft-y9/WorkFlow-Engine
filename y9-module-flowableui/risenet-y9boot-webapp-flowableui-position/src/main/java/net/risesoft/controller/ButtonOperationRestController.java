package net.risesoft.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.CustomProcessInfoApi;
import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.api.itemadmin.position.ButtonOperation4PositionApi;
import net.risesoft.api.itemadmin.position.Document4PositionApi;
import net.risesoft.api.itemadmin.position.ProcessTrack4PositionApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.api.processadmin.HistoricTaskApi;
import net.risesoft.api.processadmin.HistoricVariableApi;
import net.risesoft.api.processadmin.IdentityApi;
import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.api.processadmin.VariableApi;
import net.risesoft.model.itemadmin.CustomProcessInfoModel;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.model.itemadmin.ProcessTrackModel;
import net.risesoft.model.platform.Position;
import net.risesoft.model.processadmin.HistoricTaskInstanceModel;
import net.risesoft.model.processadmin.HistoricVariableInstanceModel;
import net.risesoft.model.processadmin.IdentityLinkModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.ButtonOperationService;
import net.risesoft.service.MultiInstanceService;
import net.risesoft.service.Process4SearchService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.util.Y9Util;

/**
 * 按钮方法
 *
 * @author zhangchongjie
 * @date 2024/06/05
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/vue/buttonOperation")
public class ButtonOperationRestController {

    private final ButtonOperation4PositionApi buttonOperation4PositionApi;
    private final TaskApi taskApi;
    private final IdentityApi identityApi;
    private final VariableApi variableApi;
    private final HistoricVariableApi historicvariableApi;
    private final ProcessDefinitionApi processDefinitionApi;
    private final PositionApi positionApi;
    private final HistoricTaskApi historictaskApi;
    private final Document4PositionApi document4PositionApi;
    private final MultiInstanceService multiInstanceService;
    private final ProcessTrack4PositionApi processTrack4PositionApi;
    private final Process4SearchService process4SearchService;
    private final ProcessParamApi processParamApi;
    private final CustomProcessInfoApi customProcessInfoApi;
    private final ButtonOperationService buttonOperationService;
    protected Logger log = LoggerFactory.getLogger(ButtonOperationRestController.class);

    /**
     * 签收功能
     *
     * @param taskId 任务id
     * @return Y9Result<String>
     */
    @RequestMapping(value = "/claim", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> claim(@RequestParam @NotBlank String taskId) {
        try {
            Position position = Y9LoginUserHolder.getPosition();
            String positionId = position.getId(), tenantId = Y9LoginUserHolder.getTenantId();
            List<IdentityLinkModel> list = identityApi.getIdentityLinksForTask(tenantId, taskId);
            for (IdentityLinkModel il : list) {
                if ("assignee".equals(il.getType())) {// 多人同时打开签收件时，一人签收了，其他人需提示该件已被签收。这里判定该任务是否已被签收。
                    return Y9Result.failure("您好，该件已被签收");
                }
            }
            taskApi.claim(tenantId, positionId, taskId);
            return Y9Result.successMsg("签收成功");
        } catch (Exception e) {
            LOGGER.error("签收失败", e);
        }
        return Y9Result.failure("签收失败");
    }

    /**
     * 完成任务. 在协办的时候使用，例如A点击协办按钮，将公文发给B，此时B协办完成后仍然要把流程返给A，这里解决这个问题
     *
     * @param taskId 任务id
     * @return Y9Result<String>
     */
    @RequestMapping(value = "/completeTask", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> completeTask(@RequestParam @NotBlank String taskId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            taskApi.completeTask(tenantId, taskId);
            return Y9Result.successMsg("完成任务成功");
        } catch (Exception e) {
            LOGGER.error("完成任务失败", e);
        }
        return Y9Result.failure("完成任务失败");
    }

    /**
     * 协商：协商是把任务转给其他人操作，被协商人会在待办任务列表里看到这条任务，正常处理任务后，任务会返回给原执行人，流程不会发生变化。
     *
     * @param taskId 任务id
     * @param userChoice 收件人
     * @return Y9Result<String>
     */
    @RequestMapping(value = "/consult", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> consult(@RequestParam @NotBlank String taskId, @RequestParam @NotBlank String userChoice) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            taskApi.delegateTask(tenantId, taskId, userChoice);
            return Y9Result.successMsg("协办成功");
        } catch (Exception e) {
            LOGGER.error("协办失败", e);
        }
        return Y9Result.failure("协办失败");
    }

    /**
     * 定制流程办理
     *
     * @param itemId 事项id
     * @param multiInstance 节点类型
     * @param nextNode 是否下一节点
     * @param processSerialNumber 流程编号
     * @param processDefinitionKey 流程定义key
     * @param processInstanceId 流程实例id
     * @param taskId 任务id
     * @param infoOvert 数据中心公开
     * @return Y9Result<String>
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/customProcessHandle", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> customProcessHandle(@RequestParam @NotBlank String itemId,
        @RequestParam @NotBlank String multiInstance, @RequestParam @NotBlank Boolean nextNode,
        @RequestParam @NotBlank String processSerialNumber, @RequestParam @NotBlank String processDefinitionKey,
        @RequestParam @NotBlank String processInstanceId, @RequestParam @NotBlank String taskId,
        @RequestParam(required = false) String infoOvert) {
        try {
            Position position = Y9LoginUserHolder.getPosition();
            String positionId = Y9LoginUserHolder.getPositionId(), tenantId = Y9LoginUserHolder.getTenantId();
            if (nextNode) {// 需要发送下一个节点
                CustomProcessInfoModel customProcessInfo =
                    customProcessInfoApi.getCurrentTaskNextNode(tenantId, processSerialNumber).getData();
                if (customProcessInfo != null) {
                    if (customProcessInfo.getTaskType().equals(SysVariables.ENDEVENT)) {// 办结
                        try {
                            buttonOperationService.complete(taskId, "办结", "已办结", infoOvert);
                            // 办结成功后更新当前运行节点
                            customProcessInfoApi.updateCurrentTask(tenantId, processSerialNumber);
                            // 办结后定制流程设为false,恢复待办后不再走定制流程
                            processParamApi.updateCustomItem(tenantId, processSerialNumber, false);
                            return Y9Result.successMsg("办结成功");
                        } catch (Exception e) {
                            LOGGER.error("办结失败", e);
                        }
                        return Y9Result.failure("办结失败");
                    }
                    Map<String, Object> variables = new HashMap<>(16);
                    String userChoice = customProcessInfo.getOrgId();
                    String routeToTaskId = customProcessInfo.getTaskKey();
                    Y9Result<String> y9Result = document4PositionApi.saveAndForwarding(tenantId, positionId,
                        processInstanceId, taskId, "", itemId, processSerialNumber, processDefinitionKey, userChoice,
                        "", routeToTaskId, variables);
                    if (!y9Result.isSuccess()) {
                        return Y9Result.failure("发送失败");
                    }
                    // 发送成功后更新当前运行节点
                    customProcessInfoApi.updateCurrentTask(tenantId, processSerialNumber);
                }
            } else {
                if (multiInstance.equals(SysVariables.PARALLEL)) {// 并行处理
                    List<TaskModel> list = taskApi.findByProcessInstanceId(tenantId, processInstanceId);
                    /*
                      改变流程变量中users的值
                     */
                    try {
                        String userObj = variableApi.getVariable(tenantId, taskId, SysVariables.USERS);
                        List<String> users =
                            userObj == null ? new ArrayList<>() : Y9JsonUtil.readValue(userObj, List.class);
                        if (users != null && users.isEmpty()) {
                            List<String> usersTemp = new ArrayList<>();
                            for (TaskModel t : list) {
                                usersTemp.add(t.getAssignee());
                            }
                            Map<String, Object> vmap = new HashMap<>(16);
                            vmap.put(SysVariables.USERS, usersTemp);
                            variableApi.setVariables(tenantId, taskId, vmap);
                        }
                    } catch (Exception e) {
                        LOGGER.error("改变流程变量中users的值失败", e);
                    }
                    taskApi.complete(tenantId, taskId);
                } else if (multiInstance.equals(SysVariables.SEQUENTIAL)) {// 串行处理
                    TaskModel task = taskApi.findById(tenantId, taskId);
                    Map<String, Object> vars = variableApi.getVariables(tenantId, taskId);// 获取流程中当前任务的所有变量
                    vars.put(SysVariables.TASKSENDER, position.getName());
                    vars.put(SysVariables.TASKSENDERID, position.getId());
                    taskApi.completeWithVariables(tenantId, taskId, vars);
                    List<TaskModel> taskNextList =
                        taskApi.findByProcessInstanceId(tenantId, task.getProcessInstanceId());
                    for (TaskModel taskNext : taskNextList) {
                        Map<String, Object> mapTemp = new HashMap<>(16);
                        mapTemp.put(SysVariables.TASKSENDER, position.getName());
                        mapTemp.put(SysVariables.TASKSENDERID, position.getId());
                        variableApi.setVariables(tenantId, taskNext.getId(), mapTemp);
                    }
                    process4SearchService.saveToDataCenter(tenantId, taskId, task.getProcessInstanceId());
                }
            }
            return Y9Result.successMsg("发送成功");
        } catch (Exception e) {
            LOGGER.error("发送失败", e);
        }
        return Y9Result.failure("发送失败");
    }

    /**
     * 直接发送至流程启动人
     *
     * @param processInstanceId 流程实例id
     * @param taskId 任务id
     * @param routeToTask 任务key
     * @return Y9Result<String>
     */
    @RequestMapping(value = "/directSend", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> directSend(@RequestParam @NotBlank String processInstanceId,
        @RequestParam @NotBlank String taskId, @RequestParam @NotBlank String routeToTask) {
        String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
        try {
            Y9Result<Object> y9Result =
                buttonOperation4PositionApi.directSend(tenantId, positionId, taskId, routeToTask, processInstanceId);
            if (y9Result.isSuccess()) {
                return Y9Result.successMsg("发送成功");
            }
        } catch (Exception e) {
            LOGGER.error("发送失败", e);
        }
        return Y9Result.failure("发送失败");
    }

    /**
     * 获取有办结权限的UserTask
     *
     * @param processDefinitionId 流程定义id
     * @return Y9Result<List < Map < String, String>>>
     */
    @RequestMapping(value = "/getContainEndEvent4UserTask", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<Map<String, String>>>
        getContainEndEvent4UserTask(@RequestParam @NotBlank String processDefinitionId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            List<Map<String, String>> routeToTasks =
                processDefinitionApi.getContainEndEvent4UserTask(tenantId, processDefinitionId);
            return Y9Result.success(routeToTasks, "获取成功");
        } catch (Exception e) {
            LOGGER.error("获取失败", e);
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 获取串行办理人顺序
     *
     * @param taskId 任务id
     * @return Y9Result<String>
     */
    @SuppressWarnings("unchecked")

    @RequestMapping(value = "/getHandleSerial", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<String> getHandleSerial(@RequestParam @NotBlank String taskId) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            TaskModel taskModel = taskApi.findById(tenantId, taskId);
            String str = variableApi.getVariableByProcessInstanceId(tenantId, taskModel.getProcessInstanceId(),
                SysVariables.USERS);
            List<String> users = Y9JsonUtil.readValue(str, List.class);
            StringBuilder userNames = new StringBuilder();
            if (users != null) {
                for (Object obj : users) {// 获取下一任务的所有办理人，办理顺序为list的顺序
                    String userId = obj.toString();
                    Position employee = positionApi.get(tenantId, userId).getData();
                    if (userId.equals(taskModel.getAssignee())) {
                        userNames = new StringBuilder("<font color='red'>" + employee.getName() + "</font>");
                    } else {
                        userNames.append("->" + "<font color='green'>").append(employee.getName()).append("</font>");
                    }
                }
            }
            return Y9Result.success(userNames.toString(), "获取成功");
        } catch (Exception e) {
            LOGGER.error("获取失败", e);
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 获取目标任务节点
     *
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务key
     * @return Y9Result<List < Map < String, String>>>
     */
    @RequestMapping(value = "/getTargetNodes", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<Map<String, String>>> getTargetNodes(@RequestParam @NotBlank String processDefinitionId,
        @RequestParam(required = false) String taskDefKey) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            List<Map<String, String>> routeToTasks;
            if (StringUtils.isBlank(taskDefKey)) {
                String startNodeKey =
                    processDefinitionApi.getStartNodeKeyByProcessDefinitionId(tenantId, processDefinitionId);
                // 获取起草节点
                routeToTasks = processDefinitionApi.getTargetNodes(tenantId, processDefinitionId, startNodeKey);
                Map<String, String> startNode = routeToTasks.get(0);
                routeToTasks = processDefinitionApi.getTargetNodes4UserTask(tenantId, processDefinitionId,
                    startNode.get("taskDefKey"), true);
                routeToTasks.add(0, startNode);
            } else {
                routeToTasks =
                    processDefinitionApi.getTargetNodes4UserTask(tenantId, processDefinitionId, taskDefKey, true);
            }
            return Y9Result.success(routeToTasks, "获取成功");
        } catch (Exception e) {
            LOGGER.error("获取失败", e);
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 退回收回任务列表
     *
     * @param taskId 任务id
     * @return Y9Result<Map < String, Object>>
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/getTaskList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Map<String, Object>> getTaskList(@RequestParam @NotBlank String taskId) {
        Map<String, Object> retMap;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Position position = Y9LoginUserHolder.getPosition();
            UserInfo person = Y9LoginUserHolder.getUserInfo();
            String tenantId = Y9LoginUserHolder.getTenantId();
            retMap = new HashMap<>(16);
            retMap.put("employeeName", position.getName());
            retMap.put("employeeMobile", person.getMobile());
            List<Map<String, Object>> listMap = new ArrayList<>();
            Map<String, Object> variables = variableApi.getVariables(tenantId, taskId);
            TaskModel taskModel = taskApi.findById(tenantId, taskId);
            // 得到该节点的multiInstance，PARALLEL表示并行，SEQUENTIAL表示串行,COMMON表示普通单实例
            String multiInstance = processDefinitionApi.getNodeType(tenantId, taskModel.getProcessDefinitionId(),
                taskModel.getTaskDefinitionKey());
            List<String> users = (List<String>)variables.get("users");
            if (multiInstance.equals(SysVariables.COMMON)) {// 普通单实例
                for (String user : users) {
                    Map<String, Object> map = new HashMap<>(16);
                    Position employee = positionApi.get(Y9LoginUserHolder.getTenantId(), user).getData();
                    map.put("user", employee.getName());
                    map.put("order", "");
                    if (StringUtils.isBlank(taskModel.getAssignee())) {// 办理人为空，改件未被签收
                        map.put("status", "等待签收");
                    } else if (StringUtils.isNoneBlank(taskModel.getAssignee())) {
                        map.put("status", "正在处理");
                    }
                    map.put("endTime", "");
                    map.put("multiInstance", "普通单实例");
                    listMap.add(map);
                }
                retMap.put("multiInstance", "普通单实例");
            }
            if (multiInstance.equals(SysVariables.SEQUENTIAL)) {// 串行
                boolean isEnd = true;
                for (int i = 0; i < users.size(); i++) {// 获取下一任务的所有办理人，办理顺序为list的顺序
                    Map<String, Object> map = new HashMap<>(16);
                    Position employee = positionApi.get(Y9LoginUserHolder.getTenantId(), users.get(i)).getData();
                    map.put("user", employee.getName());
                    map.put("order", i + 1);
                    if (users.get(i).equals(taskModel.getAssignee())) {
                        map.put("status", "正在处理");
                        map.put("endTime", "");
                        isEnd = false;
                    } else if (isEnd) {
                        map.put("status", "完成");
                        List<HistoricTaskInstanceModel> htims =
                            historictaskApi.getByProcessInstanceId(tenantId, taskModel.getProcessInstanceId(), "");
                        for (HistoricTaskInstanceModel hai : htims) {
                            if (hai.getAssignee().equals(users.get(i))) {// 获取串行多人处理的完成时间
                                map.put("endTime", sdf.format(hai.getEndTime()));
                            }
                        }
                    } else {
                        map.put("status", "等待");
                        map.put("endTime", "");
                    }
                    map.put("multiInstance", "串行");
                    listMap.add(map);
                }
                retMap.put("multiInstance", "串行");
            }
            if (multiInstance.equals(SysVariables.PARALLEL)) {
                List<HistoricTaskInstanceModel> htims =
                    historictaskApi.getByProcessInstanceId(tenantId, taskModel.getProcessInstanceId(), "");
                for (HistoricTaskInstanceModel hai : htims) {
                    if (hai == null) {
                        continue;
                    }
                    long timediff = taskModel.getCreateTime().getTime() - hai.getStartTime().getTime();
                    if (((timediff >= -3000 && timediff <= 3000) && taskModel.getName().equals(hai.getName()))
                        || hai.getEndTime() == null) {
                        Map<String, Object> map = new HashMap<>(16);
                        Position employee = positionApi.get(tenantId, hai.getAssignee()).getData();
                        map.put("user", employee.getName());
                        Date endTime = hai.getEndTime();
                        String parallelSponsorObj;
                        if (null == endTime) {
                            parallelSponsorObj = variableApi.getVariableLocal(tenantId, hai.getId(), "parallelSponsor");
                        } else {
                            HistoricVariableInstanceModel parallelSponsorObj1 = historicvariableApi
                                .getByTaskIdAndVariableName(tenantId, hai.getId(), "parallelSponsor", "");
                            parallelSponsorObj =
                                parallelSponsorObj1 != null ? parallelSponsorObj1.getValue().toString() : "";
                        }
                        map.put("endTime", endTime == null ? "" : sdf.format(endTime));
                        if (parallelSponsorObj != null) {
                            if (parallelSponsorObj.equals(employee.getId())) {
                                map.put("parallelSponsor", "主办");
                            } else {
                                map.put("parallelSponsor", "协办");
                            }
                        } else {
                            map.put("parallelSponsor", "协办");
                        }
                        map.put("status", endTime == null ? "正在处理" : "完成");
                        map.put("multiInstance", "并行");
                        listMap.add(map);
                    }
                }
                retMap.put("multiInstance", "并行");
            }
            retMap.put("rows", listMap);
            return Y9Result.success(retMap, "获取成功");
        } catch (Exception e) {
            LOGGER.error("getMultiInstanceInfo error", e);
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 办理完成，并行处理时使用
     *
     * @param taskId 任务id
     * @return Y9Result<String>
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/handleParallel", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> handleParallel(@RequestParam @NotBlank String taskId) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            TaskModel task = taskApi.findById(tenantId, taskId);
            if (task == null) {
                return Y9Result.failure("该件已被办理！");
            }
            List<TaskModel> list = taskApi.findByProcessInstanceId(tenantId, task.getProcessInstanceId());
            if (list.size() == 1) {// 并行状态且不区分主协办时，多人同时打开办理页面，当其他人都已办理完成，最后一人需提示已是并行办理的最后一人，需刷新重新办理。
                return Y9Result.failure("您是并行办理的最后一人，请刷新后重新办理。");
            }
            /*
             * 改变流程变量中users的值
             */
            try {
                String userObj = variableApi.getVariable(tenantId, taskId, SysVariables.USERS);
                List<String> users = userObj == null ? new ArrayList<>() : Y9JsonUtil.readValue(userObj, List.class);
                if (users != null && users.isEmpty()) {
                    List<String> usersTemp = new ArrayList<>();
                    for (TaskModel t : list) {
                        usersTemp.add(t.getAssignee());
                    }
                    Map<String, Object> vmap = new HashMap<>(16);
                    vmap.put(SysVariables.USERS, usersTemp);
                    variableApi.setVariables(tenantId, taskId, vmap);
                }
            } catch (Exception e) {
                LOGGER.error("handleParallel error", e);
            }
            taskApi.complete(tenantId, taskId);
            return Y9Result.successMsg("办理成功");
        } catch (Exception e) {
            LOGGER.error("handleParallel error", e);
        }
        return Y9Result.failure("办理失败");
    }

    /**
     * 送下一人，串行时使用
     *
     * @param taskId 任务id
     * @return Y9Result<String>
     */
    @RequestMapping(value = "/handleSerial", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> handleSerial(@RequestParam @NotBlank String taskId) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            TaskModel task = taskApi.findById(tenantId, taskId);
            Map<String, Object> vars = variableApi.getVariables(tenantId, taskId);// 获取流程中当前任务的所有变量
            taskApi.completeWithVariables(tenantId, taskId, vars);
            process4SearchService.saveToDataCenter(tenantId, taskId, task.getProcessInstanceId());
            return Y9Result.successMsg("办理成功");
        } catch (Exception e) {
            LOGGER.error("handleSerial error", e);
        }
        return Y9Result.failure("办理失败");
    }

    /**
     * 委托. 委托是把任务转给其他人操作，被委托人会在待办任务列表里看到这条任务，正常处理任务后，流程会继续向下运行。
     *
     * @param taskId 任务id
     * @param userChoice 收件人
     * @return Y9Result<String>
     */
    @RequestMapping(value = "/reAssign", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> reAssign(@RequestParam @NotBlank String taskId, @RequestParam @NotBlank String userChoice) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            taskApi.setAssignee(tenantId, taskId, userChoice);
            return Y9Result.successMsg("委托成功");
        } catch (Exception e) {
            LOGGER.error("reAssign error", e);
        }
        return Y9Result.failure("委托失败");
    }

    /**
     * 拒签
     *
     * @param taskId 任务id
     * @return Y9Result<String>
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/refuseClaim", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> refuseClaim(@RequestParam @NotBlank String taskId) {
        String activitiUser = "";
        try {
            Position position = Y9LoginUserHolder.getPosition();
            String positionId = position.getId(), tenantId = Y9LoginUserHolder.getTenantId();
            List<IdentityLinkModel> list = identityApi.getIdentityLinksForTask(tenantId, taskId);
            for (IdentityLinkModel il : list) {
                if ("assignee".equals(il.getType())) {// 多人同时打开签收件时，一人签收了，其他人需提示该件已被签收。这里判定该任务是否已被签收。
                    return Y9Result.failure("您好，该件已被签收");
                }
            }
            Map<String, Object> vars = variableApi.getVariables(tenantId, taskId);
            ArrayList<String> users = (ArrayList<String>)vars.get(SysVariables.USERS);
            for (Object obj : users) {
                String user = obj.toString();
                if (user.contains(positionId)) {
                    activitiUser = user;
                    break;
                }
            }
            taskApi.deleteCandidateUser(tenantId, taskId, activitiUser);
            return Y9Result.successMsg("拒签成功");
        } catch (Exception e) {
            LOGGER.error("refuseClaim error", e);
        }
        return Y9Result.failure("拒签失败");
    }

    /**
     * 最后一人拒签退回
     *
     * @param taskId 任务id
     * @return Y9Result<String>
     */
    @RequestMapping(value = "/refuseClaimRollback", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> refuseClaimRollback(@RequestParam @NotBlank String taskId) {
        Position position = Y9LoginUserHolder.getPosition();
        String positionId = position.getId(), tenantId = Y9LoginUserHolder.getTenantId();
        try {
            Y9Result<Object> y9Result = buttonOperation4PositionApi.refuseClaimRollback(tenantId, positionId, taskId);
            if (y9Result.isSuccess()) {
                return Y9Result.successMsg("拒签成功");
            }
        } catch (Exception e) {
            LOGGER.error("refuseClaimRollback error", e);
        }
        return Y9Result.failure("拒签失败");
    }

    /**
     * 重定向(选择任意流程节点重定向)
     *
     * @param taskId 任务Id
     * @param routeToTaskId 流程节点Id
     * @param userChoice 选择重定向的人员
     * @param processSerialNumber 流程编号
     * @param sponsorGuid 主办人id
     * @param isSendSms 是否短信提醒
     * @param isShuMing 是否署名
     * @param smsContent 短信内容
     * @return Y9Result<String>
     */
    @RequestMapping(value = "/reposition", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> reposition(@RequestParam @NotBlank String taskId,
        @RequestParam @NotBlank String routeToTaskId, @RequestParam @NotBlank String processSerialNumber,
        @RequestParam @NotBlank String userChoice, @RequestParam(required = false) String sponsorGuid,
        @RequestParam(required = false) String isSendSms, @RequestParam(required = false) String isShuMing,
        @RequestParam(required = false) String smsContent) {
        Position position = Y9LoginUserHolder.getPosition();
        String positionId = position.getId(), tenantId = Y9LoginUserHolder.getTenantId();
        try {
            TaskModel task = taskApi.findById(tenantId, taskId);
            List<String> users = new ArrayList<>();
            List<String> usersTemp = Y9Util.stringToList(userChoice, ";");
            for (String user : usersTemp) {
                String[] arr = user.split(":");
                users.add(arr[1]);
            }
            ProcessParamModel processParamModel =
                processParamApi.findByProcessSerialNumber(tenantId, processSerialNumber);
            processParamModel.setIsSendSms(isSendSms);
            processParamModel.setIsShuMing(isShuMing);
            processParamModel.setSmsContent(smsContent);
            processParamModel.setSmsPersonId("");
            processParamApi.saveOrUpdate(tenantId, processParamModel);

            buttonOperation4PositionApi.reposition(tenantId, positionId, taskId, routeToTaskId, users, "",
                StringUtils.isBlank(sponsorGuid) ? "" : sponsorGuid);
            process4SearchService.saveToDataCenter(tenantId, taskId, task.getProcessInstanceId());
            return Y9Result.successMsg("重定向成功");
        } catch (Exception e) {
            LOGGER.error("reposition error", e);
        }
        return Y9Result.failure("重定向失败");
    }

    /**
     * 实现回退功能，使任务退回到上一个任务。
     *
     * @param taskId 任务id
     * @param reason 退回原因
     * @return Y9Result<String>
     */
    @RequestMapping(value = "/rollback", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> rollback(@RequestParam @NotBlank String taskId,
        @RequestParam(required = false) String reason) {
        Position position = Y9LoginUserHolder.getPosition();
        String positionId = position.getId(), tenantId = Y9LoginUserHolder.getTenantId();
        try {
            TaskModel task = taskApi.findById(tenantId, taskId);
            List<TaskModel> taskList = taskApi.findByProcessInstanceId(tenantId, task.getProcessInstanceId());
            String type =
                processDefinitionApi.getNodeType(tenantId, task.getProcessDefinitionId(), task.getTaskDefinitionKey());
            if (SysVariables.PARALLEL.equals(type) && taskList.size() > 1) {// 并行退回，并行多于2人时，退回使用减签方式
                if (StringUtils.isEmpty(reason)) {
                    reason = "未填写。";
                }
                reason = "该任务由" + position.getName() + "退回:" + reason;
                Map<String, Object> map = new HashMap<>();
                map.put("val", reason);
                variableApi.setVariableLocal(tenantId, taskId, "rollBackReason", map);
                multiInstanceService.removeExecution(task.getExecutionId(), taskId, task.getAssignee());
            } else {
                buttonOperation4PositionApi.rollBack(tenantId, positionId, taskId, reason);
            }
            // 更新自定义历程结束时间
            List<ProcessTrackModel> ptModelList = processTrack4PositionApi.findByTaskId(tenantId, taskId);
            for (ProcessTrackModel ptModel : ptModelList) {
                if (StringUtils.isBlank(ptModel.getEndTime())) {
                    try {
                        processTrack4PositionApi.saveOrUpdate(tenantId, ptModel);
                    } catch (Exception e) {
                        LOGGER.error("更新自定义历程结束时间失败", e);
                    }
                }
            }
            return Y9Result.successMsg("退回成功");
        } catch (Exception e) {
            LOGGER.error("rollback error", e);
        }
        return Y9Result.failure("退回失败");
    }

    /**
     * 退回发送人
     *
     * @param taskId 任务id
     * @return Y9Result<String>
     */
    @RequestMapping(value = "/rollbackToSender", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> rollbackToSender(@RequestParam @NotBlank String taskId) {
        Position position = Y9LoginUserHolder.getPosition();
        String positionId = position.getId(), tenantId = Y9LoginUserHolder.getTenantId();
        try {
            buttonOperation4PositionApi.rollbackToSender(tenantId, positionId, taskId);
            return Y9Result.successMsg("返回发送人成功");
        } catch (Exception e) {
            LOGGER.error("rollbackToSender error", e);
        }
        return Y9Result.failure("返回发送人失败");
    }

    /**
     * 退回拟稿人
     *
     * @param taskId 任务id
     * @param reason 原因
     * @return Y9Result<String>
     */
    @RequestMapping(value = "/rollbackToStartor", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> rollbackToStartor(@RequestParam @NotBlank String taskId,
        @RequestParam(required = false) String reason) {
        Position position = Y9LoginUserHolder.getPosition();
        String positionId = position.getId(), tenantId = Y9LoginUserHolder.getTenantId();
        try {
            buttonOperation4PositionApi.rollbackToStartor(tenantId, positionId, taskId, reason);
            return Y9Result.successMsg("返回起草人成功");
        } catch (Exception e) {
            LOGGER.error("rollbackToStartor error", e);
        }
        return Y9Result.failure("返回起草人失败");
    }

    /**
     * 保存流程定制信息
     *
     * @param itemId 事项id
     * @param processSerialNumber 流程编号
     * @param processDefinitionKey 流程定义key
     * @param jsonData 数据
     * @return Y9Result<String>
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/saveCustomProcess", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> saveCustomProcess(@RequestParam @NotBlank String itemId,
        @RequestParam @NotBlank String processSerialNumber, @RequestParam @NotBlank String processDefinitionKey,
        @RequestParam @NotBlank String jsonData) {
        try {
            String positionId = Y9LoginUserHolder.getPositionId(), tenantId = Y9LoginUserHolder.getTenantId();
            List<Map<String, Object>> list = Y9JsonUtil.readValue(jsonData, List.class);
            boolean msg = customProcessInfoApi.saveOrUpdate(tenantId, itemId, processSerialNumber, list).isSuccess();
            if (!msg) {
                return Y9Result.failure("保存失败");
            }
            Map<String, Object> map = new HashMap<>();
            if (list != null) {
                map = list.get(1);
            }
            String routeToTaskId = (String)map.get("taskKey");
            Map<String, Object> variables = new HashMap<>(16);
            List<Map<String, Object>> orgList = (List<Map<String, Object>>)map.get("orgList");
            String userChoice = "";
            for (Map<String, Object> org : orgList) {
                userChoice = Y9Util.genCustomStr(userChoice, (String)org.get("id"), ";");
            }
            Y9Result<String> y9Result = document4PositionApi.saveAndForwarding(tenantId, positionId, "", "", "", itemId,
                processSerialNumber, processDefinitionKey, userChoice, "", routeToTaskId, variables);
            if (!y9Result.isSuccess()) {
                return Y9Result.failure("保存成功,发送失败");
            }
            // 发送成功后更新当前运行节点
            customProcessInfoApi.updateCurrentTask(tenantId, processSerialNumber);
            return Y9Result.successMsg("发送成功");
        } catch (Exception e) {
            LOGGER.error("saveCustomProcess error", e);
        }
        return Y9Result.failure("发送失败");
    }

    /**
     * 返回任务发送人，不用选人，直接发送
     *
     * @param taskId 任务id
     * @return Y9Result<String>
     */
    @RequestMapping(value = "/sendToSender", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> sendToSender(@RequestParam @NotBlank String taskId) {
        String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
        try {
            buttonOperation4PositionApi.rollbackToSender(tenantId, positionId, taskId);
            return Y9Result.successMsg("返回发送人成功");
        } catch (Exception e) {
            LOGGER.error("sendToSender error", e);
        }
        return Y9Result.failure("返回发送人失败");
    }

    /**
     * 返回拟稿人，不用选人，直接发送
     *
     * @param taskId 任务id
     * @return Y9Result<String>
     */
    @RequestMapping(value = "/sendToStartor", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> sendToStartor(@RequestParam @NotBlank String taskId) {
        String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
        try {
            TaskModel taskModel = taskApi.findById(tenantId, taskId);
            String routeToTaskId = taskModel.getTaskDefinitionKey();
            String processInstanceId = taskModel.getProcessInstanceId();
            String processDefinitionKey = taskModel.getProcessDefinitionId().split(":")[0];
            ProcessParamModel processParamModel = processParamApi.findByProcessInstanceId(tenantId, processInstanceId);
            String itemId = processParamModel.getItemId();
            String processSerialNumber = processParamModel.getProcessSerialNumber();
            Map<String, Object> variables = new HashMap<>(16);

            String user = variableApi.getVariableLocal(tenantId, taskId, SysVariables.TASKSENDERID);
            String userChoice = "6:" + user;

            String multiInstance =
                processDefinitionApi.getNodeType(tenantId, taskModel.getProcessDefinitionId(), routeToTaskId);
            String sponsorHandle = "";
            if (multiInstance.equals(SysVariables.PARALLEL)) {
                sponsorHandle = "true";
            }
            document4PositionApi.saveAndForwarding(tenantId, positionId, processInstanceId, taskId, sponsorHandle,
                itemId, processSerialNumber, processDefinitionKey, userChoice, "", routeToTaskId, variables);
            return Y9Result.successMsg("发送拟稿人成功");
        } catch (Exception e) {
            LOGGER.error("sendToStartor error", e);
        }
        return Y9Result.failure("发送拟稿人失败");
    }

    /**
     * 特殊办结
     *
     * @param taskId 任务id
     * @param reason 特殊办结原因
     * @return Y9Result<String>
     */
    @RequestMapping(value = "/specialComplete", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> specialComplete(@RequestParam @NotBlank String taskId,
        @RequestParam(required = false) String reason) {
        Position position = Y9LoginUserHolder.getPosition();
        String positionId = position.getId(), tenantId = Y9LoginUserHolder.getTenantId();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            TaskModel taskModel = taskApi.findById(tenantId, taskId);
            buttonOperation4PositionApi.specialComplete(tenantId, positionId, taskId, reason);
            // 更新自定义历程结束时间
            List<ProcessTrackModel> ptModelList = processTrack4PositionApi.findByTaskId(tenantId, taskId);
            for (ProcessTrackModel ptModel : ptModelList) {
                if (StringUtils.isBlank(ptModel.getEndTime())) {
                    try {
                        processTrack4PositionApi.saveOrUpdate(tenantId, ptModel);
                    } catch (Exception e) {
                        LOGGER.error("specialComplete error", e);
                    }
                }
            }
            /*
              3保存历程
             */
            ProcessTrackModel ptModel = new ProcessTrackModel();
            ptModel.setDescribed("已办结");
            ptModel.setProcessInstanceId(taskModel.getProcessInstanceId());
            ptModel.setReceiverName(position.getName());
            ptModel.setSenderName(position.getName());
            ptModel.setStartTime(sdf.format(new Date()));
            ptModel.setEndTime(sdf.format(new Date()));
            ptModel.setTaskDefName("特殊办结");
            ptModel.setTaskId(taskId);
            ptModel.setId("");
            processTrack4PositionApi.saveOrUpdate(tenantId, ptModel);
            return Y9Result.successMsg("特殊办结成功");
        } catch (Exception e) {
            LOGGER.error("specialComplete error", e);
        }
        return Y9Result.failure("特殊办结失败");
    }

    /**
     * 收回
     *
     * @param taskId 任务id
     * @param reason 收回原因
     * @return Y9Result<String>
     */
    @RequestMapping(value = "/takeback", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> takeback(@RequestParam @NotBlank String taskId,
        @RequestParam(required = false) String reason) {
        Position position = Y9LoginUserHolder.getPosition();
        String positionId = position.getId(), tenantId = Y9LoginUserHolder.getTenantId();
        try {
            buttonOperation4PositionApi.takeback(tenantId, positionId, taskId, reason);
            return Y9Result.successMsg("收回成功");
        } catch (Exception e) {
            LOGGER.error("takeback error", e);
        }
        return Y9Result.failure("收回失败");
    }

    /**
     * 撤销签收功能：用户签收后，可以进行撤销签收，撤销签收后，重新进行抢占式办理
     *
     * @param taskId 任务id
     * @return Y9Result<String>
     */
    @RequestMapping(value = "/unclaim", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> unclaim(@RequestParam @NotBlank String taskId) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            taskApi.unclaim(tenantId, taskId);
            return Y9Result.successMsg("撤销签收成功");
        } catch (Exception e) {
            LOGGER.error("unclaim error", e);
        }
        return Y9Result.failure("撤销签收失败");
    }

}
