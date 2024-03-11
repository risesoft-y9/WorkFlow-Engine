package net.risesoft.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.ButtonOperationApi;
import net.risesoft.api.itemadmin.CustomProcessInfoApi;
import net.risesoft.api.itemadmin.DocumentApi;
import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.api.itemadmin.ProcessTrackApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.api.processadmin.HistoricTaskApi;
import net.risesoft.api.processadmin.HistoricVariableApi;
import net.risesoft.api.processadmin.IdentityApi;
import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.api.processadmin.VariableApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.model.itemadmin.CustomProcessInfoModel;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.model.itemadmin.ProcessTrackModel;
import net.risesoft.model.platform.Person;
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
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@RestController
@RequestMapping("/vue/buttonOperation")
public class ButtonOperationRestController {

    protected Logger log = LoggerFactory.getLogger(ButtonOperationRestController.class);

    @Autowired
    private ButtonOperationApi buttonOperationManager;

    @Autowired
    private TaskApi taskManager;

    @Autowired
    private IdentityApi identityManager;

    @Autowired
    private VariableApi variableManager;

    @Autowired
    private HistoricVariableApi historicVariableManager;

    @Autowired
    private ProcessDefinitionApi processDefinitionManager;

    @Autowired
    private PersonApi personApi;

    @Autowired
    private HistoricTaskApi historicTaskManager;

    @Autowired
    private DocumentApi documentManager;

    @Autowired
    private MultiInstanceService multiInstanceService;

    @Autowired
    private ProcessTrackApi processTrackManager;

    @Autowired
    private Process4SearchService process4SearchService;

    @Autowired
    private ProcessParamApi processParamManager;

    @Autowired
    private CustomProcessInfoApi customProcessInfoManager;

    @Autowired
    private ButtonOperationService buttonOperationService;

    /**
     * 签收功能
     *
     * @param taskId 任务id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/claim", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> claim(@RequestParam(required = true) String taskId) {
        try {
            UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
            String tenantId = Y9LoginUserHolder.getTenantId(), userId = userInfo.getPersonId();
            List<IdentityLinkModel> list = identityManager.getIdentityLinksForTask(tenantId, taskId);
            for (IdentityLinkModel il : list) {
                if ("assignee".equals(il.getType())) {// 多人同时打开签收件时，一人签收了，其他人需提示该件已被签收。这里判定该任务是否已被签收。
                    return Y9Result.failure("您好，该件已被签收");
                }
            }
            taskManager.claim(tenantId, userId, taskId);
            return Y9Result.successMsg("签收成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("签收失败");
    }

    /**
     * 完成任务. 在协办的时候使用，例如A点击协办按钮，将公文发给B，此时B协办完成后仍然要把流程返给A，这里解决这个问题
     *
     * @param taskId 任务id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/completeTask", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> completeTask(@RequestParam(required = true) String taskId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            taskManager.completeTask(tenantId, taskId);
            return Y9Result.successMsg("完成任务成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("完成任务失败");
    }

    /**
     * 协商：协商是把任务转给其他人操作，被协商人会在待办任务列表里看到这条任务，正常处理任务后，任务会返回给原执行人，流程不会发生变化。
     *
     * @param taskId 任务id
     * @param userChoice 收件人
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/consult", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> consult(@RequestParam(required = true) String taskId,
        @RequestParam(required = true) String userChoice) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            taskManager.delegateTask(tenantId, taskId, userChoice);
            return Y9Result.successMsg("协办成功");
        } catch (Exception e) {
            e.printStackTrace();
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
     * @return
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/customProcessHandle", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> customProcessHandle(@RequestParam(required = true) String itemId,
        @RequestParam(required = true) String multiInstance, @RequestParam(required = true) Boolean nextNode,
        @RequestParam(required = true) String processSerialNumber,
        @RequestParam(required = true) String processDefinitionKey,
        @RequestParam(required = true) String processInstanceId, @RequestParam(required = true) String taskId,
        @RequestParam(required = false) String infoOvert) {
        try {
            UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
            String userId = userInfo.getPersonId(), tenantId = Y9LoginUserHolder.getTenantId();
            Map<String, Object> map = new HashMap<String, Object>(16);
            // 需要发送下一个节点
            if (nextNode) {
                CustomProcessInfoModel customProcessInfo =
                    customProcessInfoManager.getCurrentTaskNextNode(tenantId, processSerialNumber);
                if (customProcessInfo != null) {
                    if (customProcessInfo.getTaskType().equals(SysVariables.ENDEVENT)) {
                        try {
                            buttonOperationService.complete(taskId, "办结", "已办结", infoOvert);
                            // 办结成功后更新当前运行节点
                            customProcessInfoManager.updateCurrentTask(tenantId, processSerialNumber);
                            // 办结后定制流程设为false,恢复待办后不再走定制流程
                            processParamManager.updateCustomItem(tenantId, processSerialNumber, false);
                            return Y9Result.successMsg("办结成功");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return Y9Result.failure("办结失败");
                    }
                    Map<String, Object> variables = new HashMap<String, Object>(16);
                    String userChoice = customProcessInfo.getOrgId();
                    String routeToTaskId = customProcessInfo.getTaskKey();
                    map = documentManager.saveAndForwarding(tenantId, userId, processInstanceId, taskId, "", itemId,
                        processSerialNumber, processDefinitionKey, userChoice, "", routeToTaskId, variables);
                    if (!(boolean)map.get(UtilConsts.SUCCESS)) {
                        return Y9Result.failure("发送失败");
                    }
                    // 发送成功后更新当前运行节点
                    customProcessInfoManager.updateCurrentTask(tenantId, processSerialNumber);
                }
            } else {
                if (multiInstance.equals(SysVariables.PARALLEL)) {
                    List<TaskModel> list = taskManager.findByProcessInstanceId(tenantId, processInstanceId);
                    /**
                     * 改变流程变量中users的值
                     */
                    try {
                        String userObj = variableManager.getVariable(tenantId, taskId, SysVariables.USERS);
                        List<String> users =
                            userObj == null ? new ArrayList<>() : Y9JsonUtil.readValue(userObj, List.class);
                        if (users.size() == 0) {
                            List<String> usersTemp = new ArrayList<String>();
                            for (TaskModel t : list) {
                                usersTemp.add(t.getAssignee());
                            }
                            Map<String, Object> vmap = new HashMap<String, Object>(16);
                            vmap.put(SysVariables.USERS, usersTemp);
                            variableManager.setVariables(tenantId, taskId, vmap);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    taskManager.complete(tenantId, taskId);
                } else if (multiInstance.equals(SysVariables.SEQUENTIAL)) {
                    TaskModel task = taskManager.findById(tenantId, taskId);
                    Map<String, Object> vars = variableManager.getVariables(tenantId, taskId);
                    vars.put(SysVariables.TASKSENDER, userInfo.getName());
                    vars.put(SysVariables.TASKSENDERID, userInfo.getPersonId());
                    taskManager.completeWithVariables(tenantId, taskId, vars);
                    List<TaskModel> taskNextList =
                        taskManager.findByProcessInstanceId(tenantId, task.getProcessInstanceId());
                    for (TaskModel taskNext : taskNextList) {
                        Map<String, Object> mapTemp = new HashMap<String, Object>(16);
                        mapTemp.put(SysVariables.TASKSENDER, userInfo.getName());
                        mapTemp.put(SysVariables.TASKSENDERID, userInfo.getPersonId());
                        variableManager.setVariables(tenantId, taskNext.getId(), mapTemp);
                    }
                    process4SearchService.saveToDataCenter(tenantId, taskId, task.getProcessInstanceId());
                }
            }
            return Y9Result.successMsg("发送成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("发送失败");
    }

    /**
     * 直接发送至流程启动人
     *
     * @param processInstanceId 流程实例id
     * @param taskId 任务id
     * @param routeToTask 任务key
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/directSend", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> directSend(@RequestParam(required = true) String processInstanceId,
        @RequestParam(required = true) String taskId, @RequestParam(required = true) String routeToTask) {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId(), userId = userInfo.getPersonId();
        try {
            Boolean b = buttonOperationManager.directSend(tenantId, userId, taskId, routeToTask, processInstanceId);
            if (b) {
                return Y9Result.successMsg("发送成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("发送失败");
    }

    /**
     * 获取有办结权限的UserTask
     *
     * @param processDefinitionId 流程定义id
     * @return
     */
    @RequestMapping(value = "/getContainEndEvent4UserTask", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<Map<String, String>>>
        getContainEndEvent4UserTask(@RequestParam(required = true) String processDefinitionId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            List<Map<String, String>> routeToTasks =
                processDefinitionManager.getContainEndEvent4UserTask(tenantId, processDefinitionId);
            return Y9Result.success(routeToTasks, "获取成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 获取串行办理人顺序
     *
     * @param taskId 任务id
     * @return
     */
    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping(value = "/getHandleSerial", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<String> getHandleSerial(@RequestParam(required = true) String taskId) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            TaskModel taskModel = taskManager.findById(tenantId, taskId);
            String str = variableManager.getVariableByProcessInstanceId(tenantId, taskModel.getProcessInstanceId(),
                SysVariables.USERS);
            List<String> users = Y9JsonUtil.readValue(str, List.class);
            String userNames = "";
            for (Object obj : users) {
                String user = obj.toString();
                Person employee = personApi.getPerson(Y9LoginUserHolder.getTenantId(), user).getData();
                if (user.equals(taskModel.getAssignee())) {
                    userNames = "<font color='red'>" + employee.getName() + "</font>";
                } else {
                    userNames += ("->" + "<font color='green'>" + employee.getName() + "</font>");
                }
            }
            return Y9Result.success(userNames, "获取成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 获取目标任务节点
     *
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务key
     * @return
     */
    @RequestMapping(value = "/getTargetNodes", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<Map<String, String>>> getTargetNodes(@RequestParam(required = true) String processDefinitionId,
        @RequestParam(required = false) String taskDefKey) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            List<Map<String, String>> routeToTasks = new ArrayList<Map<String, String>>();
            if (StringUtils.isBlank(taskDefKey)) {
                String startNodeKey =
                    processDefinitionManager.getStartNodeKeyByProcessDefinitionId(tenantId, processDefinitionId);
                // 获取起草节点
                routeToTasks = processDefinitionManager.getTargetNodes(tenantId, processDefinitionId, startNodeKey);
                Map<String, String> startNode = routeToTasks.get(0);
                routeToTasks = processDefinitionManager.getTargetNodes4UserTask(tenantId, processDefinitionId,
                    startNode.get("taskDefKey"), true);
                routeToTasks.add(0, startNode);
            } else {
                routeToTasks =
                    processDefinitionManager.getTargetNodes4UserTask(tenantId, processDefinitionId, taskDefKey, true);
            }
            return Y9Result.success(routeToTasks, "获取成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 退回收回任务列表
     *
     * @param taskId 任务id
     * @return
     */
    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping(value = "/getTaskList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Map<String, Object>> getTaskList(@RequestParam(required = true) String taskId) {
        Map<String, Object> retMap = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
            String tenantId = Y9LoginUserHolder.getTenantId();
            retMap = new HashMap<String, Object>(16);
            retMap.put("employeeName", userInfo.getName());
            retMap.put("employeeMobile", userInfo.getMobile());
            List<Map<String, Object>> listMap = new ArrayList<>();
            Map<String, Object> variables = variableManager.getVariables(tenantId, taskId);
            TaskModel taskModel = taskManager.findById(tenantId, taskId);
            // 得到该节点的multiInstance，PARALLEL表示并行，SEQUENTIAL表示串行,COMMON表示普通单实例
            String multiInstance = processDefinitionManager.getNodeType(tenantId, taskModel.getProcessDefinitionId(),
                taskModel.getTaskDefinitionKey());
            List<String> users = (List<String>)variables.get("users");
            if (multiInstance.equals(SysVariables.COMMON)) {
                for (int i = 0; i < users.size(); i++) {
                    Map<String, Object> map = new HashMap<String, Object>(16);
                    Person employee = personApi.getPerson(Y9LoginUserHolder.getTenantId(), users.get(i)).getData();
                    map.put("user", employee.getName());
                    map.put("order", "");
                    if (StringUtils.isBlank(taskModel.getAssignee())) {
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
            if (multiInstance.equals(SysVariables.SEQUENTIAL)) {
                Boolean isEnd = true;
                for (int i = 0; i < users.size(); i++) {
                    Map<String, Object> map = new HashMap<String, Object>(16);
                    Person employee = personApi.getPerson(Y9LoginUserHolder.getTenantId(), users.get(i)).getData();
                    map.put("user", employee.getName());
                    map.put("order", i + 1);
                    if (users.get(i).equals(taskModel.getAssignee())) {
                        map.put("status", "正在处理");
                        map.put("endTime", "");
                        isEnd = false;
                    } else if (isEnd) {
                        map.put("status", "完成");
                        List<HistoricTaskInstanceModel> htims =
                            historicTaskManager.getByProcessInstanceId(tenantId, taskModel.getProcessInstanceId(), "");
                        for (HistoricTaskInstanceModel hai : htims) {
                            if (hai.getAssignee().equals(users.get(i))) {
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
                    historicTaskManager.getByProcessInstanceId(tenantId, taskModel.getProcessInstanceId(), "");
                for (int i = 0; i < htims.size(); i++) {
                    HistoricTaskInstanceModel hai = htims.get(i);
                    if (hai == null) {
                        continue;
                    }
                    long timediff = taskModel.getCreateTime().getTime() - hai.getStartTime().getTime();
                    boolean b = (timediff >= -3000 && timediff <= 3000) && taskModel.getName().equals(hai.getName());
                    if (b) {
                        Map<String, Object> map = new HashMap<String, Object>(16);
                        Person employee = personApi.getPerson(tenantId, hai.getAssignee()).getData();
                        map.put("user", employee.getName());
                        Date endTime = hai.getEndTime();
                        String parallelSponsorObj = null;
                        if (null == endTime) {
                            parallelSponsorObj =
                                variableManager.getVariableLocal(tenantId, hai.getId(), "parallelSponsor");
                        } else {
                            HistoricVariableInstanceModel parallelSponsorObj1 = historicVariableManager
                                .getByTaskIdAndVariableName(tenantId, hai.getId(), "parallelSponsor", "");
                            parallelSponsorObj =
                                parallelSponsorObj1 != null ? parallelSponsorObj1.getValue().toString() : "";
                        }
                        map.put("endTime", endTime == null ? "" : sdf.format(endTime));
                        if (parallelSponsorObj != null) {
                            String parallelSponsor = parallelSponsorObj;
                            if (parallelSponsor.equals(employee.getId())) {
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
            e.printStackTrace();
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 办理完成，并行处理时使用
     *
     * @param taskId 任务id
     * @return
     */
    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping(value = "/handleParallel", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> handleParallel(@RequestParam(required = true) String taskId) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            TaskModel task = taskManager.findById(tenantId, taskId);
            if (task == null) {
                return Y9Result.failure("该件已被办理！");
            }
            List<TaskModel> list = taskManager.findByProcessInstanceId(tenantId, task.getProcessInstanceId());
            // 并行状态且不区分主协办时，多人同时打开办理页面，当其他人都已办理完成，最后一人需提示已是并行办理的最后一人，需刷新重新办理。
            if (list.size() == 1) {
                return Y9Result.failure("您是并行办理的最后一人，请刷新后重新办理。");
            }
            /**
             * 改变流程变量中users的值
             */
            try {
                String userObj = variableManager.getVariable(tenantId, taskId, SysVariables.USERS);
                List<String> users = userObj == null ? new ArrayList<>() : Y9JsonUtil.readValue(userObj, List.class);
                if (users.size() == 0) {
                    List<String> usersTemp = new ArrayList<String>();
                    for (TaskModel t : list) {
                        usersTemp.add(t.getAssignee());
                    }
                    Map<String, Object> vmap = new HashMap<String, Object>(16);
                    vmap.put(SysVariables.USERS, usersTemp);
                    variableManager.setVariables(tenantId, taskId, vmap);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            taskManager.complete(tenantId, taskId);
            return Y9Result.successMsg("办理成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("办理失败");
    }

    /**
     * 送下一人，串行时使用
     *
     * @param taskId 任务id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/handleSerial", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> handleSerial(@RequestParam(required = true) String taskId) {
        try {
            UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
            String tenantId = Y9LoginUserHolder.getTenantId();
            TaskModel task = taskManager.findById(tenantId, taskId);
            Map<String, Object> vars = variableManager.getVariables(tenantId, taskId);
            vars.put(SysVariables.TASKSENDER, userInfo.getName());
            vars.put(SysVariables.TASKSENDERID, userInfo.getPersonId());
            taskManager.completeWithVariables(tenantId, taskId, vars);
            List<TaskModel> taskNextList = taskManager.findByProcessInstanceId(tenantId, task.getProcessInstanceId());
            for (TaskModel taskNext : taskNextList) {
                Map<String, Object> mapTemp = new HashMap<String, Object>(16);
                mapTemp.put(SysVariables.TASKSENDER, userInfo.getName());
                mapTemp.put(SysVariables.TASKSENDERID, userInfo.getPersonId());
                variableManager.setVariables(tenantId, taskNext.getId(), mapTemp);
            }
            process4SearchService.saveToDataCenter(tenantId, taskId, task.getProcessInstanceId());
            return Y9Result.successMsg("办理成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("办理失败");
    }

    /**
     * 委托. 委托是把任务转给其他人操作，被委托人会在待办任务列表里看到这条任务，正常处理任务后，流程会继续向下运行。
     *
     * @param taskId 任务id
     * @param userChoice 收件人
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/reAssign", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> reAssign(@RequestParam(required = true) String taskId,
        @RequestParam(required = true) String userChoice) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            taskManager.setAssignee(tenantId, taskId, userChoice);
            return Y9Result.successMsg("委托成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("委托失败");
    }

    /**
     * 拒签
     *
     * @param taskId 任务id
     * @return
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/refuseClaim", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Y9Result<String> refuseClaim(@RequestParam(required = true) String taskId) {
        String activitiUser = "";
        try {
            UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
            String tenantId = Y9LoginUserHolder.getTenantId();
            List<IdentityLinkModel> list = identityManager.getIdentityLinksForTask(tenantId, taskId);
            for (IdentityLinkModel il : list) {
                // 多人同时打开签收件时，一人签收了，其他人需提示该件已被签收。这里判定该任务是否已被签收。
                if ("assignee".equals(il.getType())) {
                    return Y9Result.failure("您好，该件已被签收");
                }
            }
            Map<String, Object> vars = variableManager.getVariables(tenantId, taskId);
            ArrayList<String> users = (ArrayList<String>)vars.get(SysVariables.USERS);
            for (String user : users) {
                if (user.contains(userInfo.getPersonId())) {
                    activitiUser = user;
                    break;
                }
            }
            taskManager.deleteCandidateUser(tenantId, taskId, activitiUser);
            return Y9Result.successMsg("拒签成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("拒签失败");
    }

    /**
     * 最后一人拒签退回
     *
     * @param taskId 任务id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/refuseClaimRollback", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> refuseClaimRollback(@RequestParam(required = true) String taskId) {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String userId = userInfo.getPersonId(), tenantId = Y9LoginUserHolder.getTenantId();
        try {
            Map<String, Object> map = new HashMap<>(16);
            map = buttonOperationManager.refuseClaimRollback(tenantId, userId, taskId);
            if ((Boolean)map.get(UtilConsts.SUCCESS)) {
                return Y9Result.successMsg((String)map.get("msg"));
            } else {
                return Y9Result.failure((String)map.get("msg"));
            }
        } catch (Exception e) {
            e.printStackTrace();
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
     * @return
     */
    @RequestMapping(value = "/reposition", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> reposition(@RequestParam(required = true) String taskId,
        @RequestParam(required = true) String routeToTaskId, @RequestParam(required = true) String processSerialNumber,
        @RequestParam(required = true) String userChoice, @RequestParam(required = false) String sponsorGuid,
        @RequestParam(required = false) String isSendSms, @RequestParam(required = false) String isShuMing,
        @RequestParam(required = false) String smsContent) {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String userId = userInfo.getPersonId(), tenantId = Y9LoginUserHolder.getTenantId();
        try {
            /**
             * 解析选择的人员，默认选择的只能是人员，格式如3:_T-KxMB_jEeK9qP_70Nt6Jw:_NdUe8AkcEeKCYZgpLRsxfw，取_T-KxMB_jEeK9qP_70Nt6Jw:_NdUe8AkcEeKCYZgpLRsxfw
             */
            TaskModel task = taskManager.findById(tenantId, taskId);
            List<String> users = new ArrayList<>();
            List<String> usersTemp = Y9Util.stringToList(userChoice, ";");
            for (String user : usersTemp) {
                String[] arr = user.split(":");
                users.add(arr[1]);
            }
            ProcessParamModel processParamModel =
                processParamManager.findByProcessSerialNumber(Y9LoginUserHolder.getTenantId(), processSerialNumber);
            processParamModel.setIsSendSms(isSendSms);
            processParamModel.setIsShuMing(isShuMing);
            processParamModel.setSmsContent(smsContent);
            processParamModel.setSmsPersonId("");
            processParamManager.saveOrUpdate(Y9LoginUserHolder.getTenantId(), processParamModel);

            buttonOperationManager.reposition(tenantId, userId, taskId, routeToTaskId, users, "", sponsorGuid);
            process4SearchService.saveToDataCenter(tenantId, taskId, task.getProcessInstanceId());
            return Y9Result.successMsg("重定向成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("重定向失败");
    }

    /**
     * 实现回退功能，使任务退回到上一个任务。
     *
     * @param taskId 任务id
     * @param reason 退回原因
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/rollback", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> rollback(@RequestParam(required = true) String taskId,
        @RequestParam(required = false) String reason) {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String userId = userInfo.getPersonId(), tenantId = Y9LoginUserHolder.getTenantId();
        try {
            TaskModel task = taskManager.findById(tenantId, taskId);
            List<TaskModel> taskList = taskManager.findByProcessInstanceId(tenantId, task.getProcessInstanceId());
            String type = processDefinitionManager.getNodeType(tenantId, task.getProcessDefinitionId(),
                task.getTaskDefinitionKey());
            // 并行退回，并行多于2人时，退回使用减签方式
            if (SysVariables.PARALLEL.equals(type) && taskList.size() > 1) {
                if (StringUtils.isEmpty(reason)) {
                    reason = "未填写。";
                }
                reason = "该任务由" + userInfo.getName() + "退回:" + reason;
                Map<String, Object> val = new HashMap<String, Object>();
                val.put("val", reason);
                variableManager.setVariableLocal(tenantId, taskId, "rollBackReason", val);
                multiInstanceService.removeExecution(task.getExecutionId(), taskId, task.getAssignee());
            } else {
                buttonOperationManager.rollBack(tenantId, userId, taskId, reason);
            }
            // 更新自定义历程结束时间
            List<ProcessTrackModel> ptModelList =
                processTrackManager.findByTaskId(Y9LoginUserHolder.getTenantId(), taskId);
            for (ProcessTrackModel ptModel : ptModelList) {
                if (StringUtils.isBlank(ptModel.getEndTime())) {
                    try {
                        processTrackManager.saveOrUpdate(Y9LoginUserHolder.getTenantId(), ptModel);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return Y9Result.successMsg("退回成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("退回失败");
    }

    /**
     * 退回发送人
     *
     * @param taskId 任务id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/rollbackToSender", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> rollbackToSender(@RequestParam(required = true) String taskId) {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId(), userId = userInfo.getPersonId();
        try {
            buttonOperationManager.rollbackToSender(tenantId, userId, taskId);
            return Y9Result.successMsg("返回发送人成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("返回发送人失败");
    }

    /**
     * 退回拟稿人
     *
     * @param taskId 任务id
     * @param reason 原因
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/rollbackToStartor", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> rollbackToStartor(@RequestParam(required = true) String taskId,
        @RequestParam(required = false) String reason) {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId(), userId = userInfo.getPersonId();
        try {
            buttonOperationManager.rollbackToStartor(tenantId, userId, taskId, reason);
            return Y9Result.successMsg("返回起草人成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("返回起草人失败");
    }

    /**
     * 保存流程定制信息
     *
     * @param itemId 事项id
     * @param processSerialNumber 流程编号
     * @param taskList 任务列表
     * @return
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/saveCustomProcess", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> saveCustomProcess(@RequestParam(required = true) String itemId,
        @RequestParam(required = true) String processSerialNumber,
        @RequestParam(required = true) String processDefinitionKey, @RequestParam(required = true) String jsonData) {
        try {
            String userId = Y9LoginUserHolder.getPersonId(), tenantId = Y9LoginUserHolder.getTenantId();
            List<Map<String, Object>> list = Y9JsonUtil.readValue(jsonData, List.class);
            boolean msg = customProcessInfoManager.saveOrUpdate(tenantId, itemId, processSerialNumber, list);
            if (!msg) {
                return Y9Result.failure("保存失败");
            }
            Map<String, Object> map = list.get(1);
            String routeToTaskId = (String)map.get("taskKey");
            Map<String, Object> variables = new HashMap<String, Object>(16);
            List<Map<String, Object>> orgList = (List<Map<String, Object>>)map.get("orgList");
            String userChoice = "";
            for (Map<String, Object> org : orgList) {
                userChoice = Y9Util.genCustomStr(userChoice, (String)org.get("id"), ";");
            }
            map = documentManager.saveAndForwarding(tenantId, userId, "", "", "", itemId, processSerialNumber,
                processDefinitionKey, userChoice, "", routeToTaskId, variables);
            if (!(boolean)map.get(UtilConsts.SUCCESS)) {
                return Y9Result.failure("保存成功,发送失败");
            }
            // 发送成功后更新当前运行节点
            customProcessInfoManager.updateCurrentTask(tenantId, processSerialNumber);
            return Y9Result.successMsg("发送成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("发送失败");
    }

    /**
     * 返回任务发送人，不用选人，直接发送
     *
     * @param taskId 任务id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/sendToSender", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> sendToSender(@RequestParam(required = true) String taskId) {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId(), userId = userInfo.getPersonId();
        try {
            buttonOperationManager.rollbackToSender(tenantId, userId, taskId);
            return Y9Result.successMsg("返回发送人成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("返回发送人失败");
    }

    /**
     * 返回拟稿人，不用选人，直接发送
     *
     * @param taskId 任务id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/sendToStartor", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> sendToStartor(@RequestParam(required = true) String taskId) {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId(), userId = userInfo.getPersonId();
        try {
            TaskModel taskModel = taskManager.findById(tenantId, taskId);
            String routeToTaskId = taskModel.getTaskDefinitionKey();
            String processInstanceId = taskModel.getProcessInstanceId();
            String processDefinitionKey = taskModel.getProcessDefinitionId().split(":")[0];
            ProcessParamModel processParamModel =
                processParamManager.findByProcessInstanceId(tenantId, processInstanceId);
            String itemId = processParamModel.getItemId();
            String processSerialNumber = processParamModel.getProcessSerialNumber();
            Map<String, Object> variables = new HashMap<String, Object>(16);

            Object taskSenderIdObject = variableManager.getVariableLocal(tenantId, taskId, SysVariables.TASKSENDERID);
            String user = (String)taskSenderIdObject;
            String userChoice = "3:" + user;

            String multiInstance =
                processDefinitionManager.getNodeType(tenantId, taskModel.getProcessDefinitionId(), routeToTaskId);
            String sponsorHandle = "";
            if (multiInstance.equals(SysVariables.PARALLEL)) {
                sponsorHandle = "true";
            }
            documentManager.saveAndForwarding(tenantId, userId, processInstanceId, taskId, sponsorHandle, itemId,
                processSerialNumber, processDefinitionKey, userChoice, "", routeToTaskId, variables);
            return Y9Result.successMsg("发送拟稿人成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("发送拟稿人失败");
    }

    /**
     * 特殊办结
     *
     * @param taskId 任务id
     * @param reason 特殊办结原因
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/specialComplete", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> specialComplete(@RequestParam(required = true) String taskId,
        @RequestParam(required = false) String reason) {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId(), userId = userInfo.getPersonId();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            TaskModel taskModel = taskManager.findById(tenantId, taskId);
            buttonOperationManager.specialComplete(tenantId, userId, taskId, reason);
            // 更新自定义历程结束时间
            List<ProcessTrackModel> ptModelList =
                processTrackManager.findByTaskId(Y9LoginUserHolder.getTenantId(), taskId);
            for (ProcessTrackModel ptModel : ptModelList) {
                if (StringUtils.isBlank(ptModel.getEndTime())) {
                    try {
                        processTrackManager.saveOrUpdate(Y9LoginUserHolder.getTenantId(), ptModel);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            /**
             * 3保存历程
             */
            ProcessTrackModel ptModel = new ProcessTrackModel();
            ptModel.setDescribed("已办结");
            ptModel.setProcessInstanceId(taskModel.getProcessInstanceId());
            ptModel.setReceiverName(userInfo.getName());
            ptModel.setSenderName(userInfo.getName());
            ptModel.setStartTime(sdf.format(new Date()));
            ptModel.setEndTime(sdf.format(new Date()));
            ptModel.setTaskDefName("特殊办结");
            ptModel.setTaskId(taskId);
            ptModel.setId("");
            processTrackManager.saveOrUpdate(tenantId, ptModel);
            return Y9Result.successMsg("特殊办结成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("特殊办结失败");
    }

    /**
     * 收回
     *
     * @param taskId 任务id
     * @param reason 收回原因
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/takeback", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> takeback(@RequestParam(required = true) String taskId,
        @RequestParam(required = false) String reason) {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String userId = userInfo.getPersonId(), tenantId = Y9LoginUserHolder.getTenantId();
        try {
            buttonOperationManager.takeback(tenantId, userId, taskId, reason);
            return Y9Result.successMsg("收回成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("收回失败");
    }

    /**
     * 撤销签收功能：用户签收后，可以进行撤销签收，撤销签收后，重新进行抢占式办理
     *
     * @param taskId 任务id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/unclaim", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> unclaim(@RequestParam(required = true) String taskId) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            taskManager.unclaim(tenantId, taskId);
            return Y9Result.successMsg("撤销签收成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("撤销签收失败");
    }
}
