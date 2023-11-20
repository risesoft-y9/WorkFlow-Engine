package net.risesoft.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.risesoft.api.itemadmin.ButtonOperationApi;
import net.risesoft.api.itemadmin.DocumentApi;
import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.api.org.PersonApi;
import net.risesoft.api.processadmin.RuntimeApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.api.processadmin.VariableApi;
import net.risesoft.model.platform.Person;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.service.MultiInstanceService;
import net.risesoft.service.Process4SearchService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@Service(value = "multiInstanceService")
public class MultiInstanceServiceImpl implements MultiInstanceService {

    @Autowired
    private TaskApi taskManager;

    @Autowired
    private VariableApi variableManager;

    @Autowired
    private PersonApi personApi;

    @Autowired
    private ButtonOperationApi buttonOperationManager;

    @Autowired
    private DocumentApi documentManager;

    @Autowired
    private RuntimeApi runtimeManager;

    @Autowired
    private ProcessParamApi processParamManager;

    @Autowired
    private Process4SearchService process4SearchService;

    @Override
    public void addExecutionId(String processInstanceId, String taskId, String userChoice, String isSendSms,
        String isShuMing, String smsContent) throws Exception {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String personId = userInfo.getPersonId(), tenantId = Y9LoginUserHolder.getTenantId();
        TaskModel task = taskManager.findById(tenantId, taskId);
        String activityId = task.getTaskDefinitionKey();
        String[] users = userChoice.split(";");
        ProcessParamModel processParamModel = processParamManager.findByProcessInstanceId(tenantId, processInstanceId);
        try {
            if (processParamModel != null && processParamModel.getId() != null) {
                processParamModel.setSmsContent(smsContent);
                processParamModel.setIsSendSms(isSendSms);
                processParamModel.setIsShuMing(isShuMing);
                processParamModel.setSmsPersonId("");
                processParamManager.saveOrUpdate(tenantId, processParamModel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (String user : users) {
            buttonOperationManager.addMultiInstanceExecution(tenantId, personId, activityId, processInstanceId, taskId,
                user);
        }
        process4SearchService.saveToDataCenter1(tenantId, taskId, processParamModel);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addExecutionId4Sequential(String executionId, String taskId, String userChoice, String selectUserId,
        int num) throws Exception {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String[] userChoiceArr = userChoice.split(";");
        String usersObj = variableManager.getVariableByProcessInstanceId(tenantId, executionId, SysVariables.USERS);
        // 计算添加后的users
        List<String> usersList = new ArrayList<>();
        List<String> usersListTemp = new ArrayList<>();
        if (null != usersObj) {
            usersList = Y9JsonUtil.readValue(usersObj, List.class);
        }
        int number = 1;
        for (Object obj : usersList) {
            String user = obj.toString();
            usersListTemp.add(user);
            if (num == number) {
                if (user.equals(selectUserId)) {
                    for (String userTemp : userChoiceArr) {
                        usersListTemp.add(userTemp);
                    }
                }
            }
            number += 1;
        }
        // 改变流程变量中的users
        Map<String, Object> val = new HashMap<String, Object>();
        val.put("val", usersListTemp);
        runtimeManager.setVariable(tenantId, executionId, SysVariables.USERS, val);
        // 改变任务变量中的users
        variableManager.setVariableLocal(tenantId, taskId, SysVariables.USERS, val);

        // 改变多实例的标量
        Integer nrOfInstances = Integer
            .valueOf(variableManager.getVariableByProcessInstanceId(tenantId, executionId, SysVariables.NROFINSTANCES));
        Map<String, Object> val2 = new HashMap<String, Object>();
        val2.put("val", nrOfInstances + userChoiceArr.length);
        runtimeManager.setVariable(tenantId, executionId, SysVariables.NROFINSTANCES, val2);
    }

    @Override
    public List<Map<String, Object>> assigneeList4Parallel(String processInstanceId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<TaskModel> taskList = taskManager.findByProcessInstanceId(tenantId, processInstanceId);
        List<Map<String, Object>> listMap = new ArrayList<>();
        Map<String, Object> mapTemp = null;
        Person personTemp = null;
        int num = 0;
        ProcessParamModel processParamModel = processParamManager.findByProcessInstanceId(tenantId, processInstanceId);
        String parallelSponsor = processParamModel == null ? "" : processParamModel.getSponsorGuid();
        for (TaskModel tm : taskList) {
            mapTemp = new HashMap<>(16);
            mapTemp.put("num", num + 1);
            mapTemp.put("taskId", tm.getId());
            mapTemp.put("executionId", tm.getExecutionId());
            mapTemp.put("assigneeId", tm.getAssignee());
            personTemp = personApi.getPerson(tenantId, tm.getAssignee()).getData();
            mapTemp.put("assigneeName", personTemp == null ? "" : personTemp.getName());
            mapTemp.put("name", tm.getName());
            mapTemp.put("isZhuBan", "否");
            if (StringUtils.isNotEmpty(parallelSponsor) && tm.getAssignee().contains(parallelSponsor)) {
                mapTemp.put("isZhuBan", "是");
            }
            listMap.add(mapTemp);
            num += 1;
        }

        return listMap;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Map<String, Object>> assigneeList4Sequential(String taskId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        TaskModel taskModel = taskManager.findById(tenantId, taskId);
        String currentAssignee = taskModel.getAssignee();

        String usersObj = variableManager.getVariable(tenantId, taskId, SysVariables.USERS);
        List<String> users = Y9JsonUtil.readValue(usersObj, List.class);

        List<Map<String, Object>> listMap = new ArrayList<>();
        Map<String, Object> mapTemp = null;
        Person personTemp = null;
        Boolean notStart = false;
        int num = 0;
        for (Object obj : users) {
            String user = obj.toString();
            personTemp = personApi.getPerson(tenantId, user).getData();
            mapTemp = new HashMap<>(16);
            mapTemp.put("num", num + 1);
            mapTemp.put("taskId", taskId);
            mapTemp.put("name", taskModel.getName());
            mapTemp.put("executionId", taskModel.getExecutionId());
            mapTemp.put("assigneeId", user);
            mapTemp.put("assigneeName", personTemp == null ? "" : personTemp.getName());
            if (user.equals(currentAssignee)) {
                mapTemp.put("status", "正在办理");
                listMap.add(mapTemp);
                notStart = true;
                num += 1;
                continue;
            }
            if (notStart) {
                mapTemp.put("status", "未开始");
            } else {
                mapTemp.put("status", "已办理");
            }
            listMap.add(mapTemp);
            num += 1;
        }
        return listMap;
    }

    @Override
    public Map<String, Object> docUserChoise(String processInstanceId) {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        Map<String, Object> map = new HashMap<>(16);
        String personId = userInfo.getPersonId(), tenantId = Y9LoginUserHolder.getTenantId();
        List<TaskModel> taskList = taskManager.findByProcessInstanceId(tenantId, processInstanceId);
        if (!taskList.isEmpty()) {
            ProcessParamModel processParamModel =
                processParamManager.findByProcessInstanceId(tenantId, processInstanceId);
            TaskModel task = taskList.get(0);
            String taskId = task.getId(), processDefinitionId = task.getProcessDefinitionId(),
                processDefinitionKey = processDefinitionId.split(SysVariables.COLON)[0];
            String routeToTask = task.getTaskDefinitionKey();
            String itemId = processParamModel.getItemId();
            map = documentManager.docUserChoise(tenantId, personId, itemId, processDefinitionKey, processDefinitionId,
                taskId, routeToTask, processInstanceId);
            map.put("taskId", taskId);
            map.put("processInstanceId", processInstanceId);
        }
        return map;
    }

    @Override
    public void removeExecution(String executionId, String taskId, String elementUser) throws Exception {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String personId = userInfo.getPersonId(), tenantId = Y9LoginUserHolder.getTenantId();
        buttonOperationManager.deleteMultiInstanceExecution(tenantId, personId, executionId, taskId, elementUser);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void removeExecution4Sequential(String executionId, String taskId, String elementUser, int num)
        throws Exception {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String usersObj = variableManager.getVariableByProcessInstanceId(tenantId, executionId, SysVariables.USERS);
        // 计算删除后的users
        List<String> usersList = new ArrayList<>();
        List<String> usersListTemp = new ArrayList<>();
        if (null != usersObj) {
            usersList = Y9JsonUtil.readValue(usersObj, List.class);
        }
        int number = 1;
        for (Object obj : usersList) {
            String user = obj.toString();
            // 为防止串行中有两个同一人，删除时只删除对应的
            if (num == number) {
                if (!user.equals(elementUser)) {
                    usersListTemp.add(user);
                }
            } else {
                usersListTemp.add(user);
            }
            number += 1;
        }
        // 改变流程变量中的users
        Map<String, Object> val = new HashMap<String, Object>();
        val.put("val", usersListTemp);
        runtimeManager.setVariable(tenantId, executionId, SysVariables.USERS, val);
        // 改变任务变量中的users
        variableManager.setVariableLocal(tenantId, taskId, SysVariables.USERS, val);
        // 改变多实例的标量
        Integer nrOfInstances = Integer
            .valueOf(variableManager.getVariableByProcessInstanceId(tenantId, executionId, SysVariables.NROFINSTANCES));
        Map<String, Object> val1 = new HashMap<String, Object>();
        val1.put("val", nrOfInstances - 1);
        runtimeManager.setVariable(tenantId, executionId, SysVariables.NROFINSTANCES, val1);
    }
}
