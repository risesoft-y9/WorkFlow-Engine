package net.risesoft.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.api.itemadmin.position.ButtonOperation4PositionApi;
import net.risesoft.api.itemadmin.position.Document4PositionApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.api.processadmin.RuntimeApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.api.processadmin.VariableApi;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.model.platform.Position;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.service.MultiInstanceService;
import net.risesoft.service.Process4SearchService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;

@Slf4j
@RequiredArgsConstructor
@Service(value = "multiInstanceService")
public class MultiInstanceServiceImpl implements MultiInstanceService {

    private final TaskApi taskApi;

    private final VariableApi variableApi;

    private final PositionApi positionApi;

    private final ButtonOperation4PositionApi buttonOperation4PositionApi;

    private final Document4PositionApi document4PositionApi;

    private final RuntimeApi runtimeApi;

    private final ProcessParamApi processParamApi;

    private final Process4SearchService process4SearchService;

    @Override
    public void addExecutionId(String processInstanceId, String taskId, String userChoice, String isSendSms,
        String isShuMing, String smsContent) throws Exception {
        String tenantId = Y9LoginUserHolder.getTenantId();
        TaskModel task = taskApi.findById(tenantId, taskId);
        String activityId = task.getTaskDefinitionKey();
        String[] users = userChoice.split(";");
        ProcessParamModel processParamModel = processParamApi.findByProcessInstanceId(tenantId, processInstanceId);
        try {
            if (processParamModel != null && processParamModel.getId() != null) {
                processParamModel.setSmsContent(smsContent);
                processParamModel.setIsSendSms(isSendSms);
                processParamModel.setIsShuMing(isShuMing);
                processParamModel.setSmsPersonId("");
                processParamApi.saveOrUpdate(tenantId, processParamModel);
            }
        } catch (Exception e) {
            LOGGER.error("保存流程参数失败", e);
        }
        for (String user : users) {
            buttonOperation4PositionApi.addMultiInstanceExecution(tenantId, activityId, processInstanceId, taskId,
                user);
        }
        if (processParamModel != null) {
            process4SearchService.saveToDataCenter1(tenantId, taskId, processParamModel);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addExecutionId4Sequential(String executionId, String taskId, String userChoice, String selectUserId,
        int num) throws Exception {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String[] userChoiceArr = userChoice.split(";");
        String usersObj = variableApi.getVariableByProcessInstanceId(tenantId, executionId, SysVariables.USERS);
        // 计算添加后的users
        List<String> usersList = new ArrayList<>();
        List<String> usersListTemp = new ArrayList<>();
        if (null != usersObj) {
            usersList = Y9JsonUtil.readValue(usersObj, List.class);
        }
        int number = 1;
        if (usersList != null) {
            for (Object obj : usersList) {
                String user = obj.toString();
                usersListTemp.add(user);
                if (num == number) {
                    if (user.equals(selectUserId)) {
                        usersListTemp.addAll(Arrays.asList(userChoiceArr));
                    }
                }
                number += 1;
            }
        }
        // 改变流程变量中的users
        Map<String, Object> val = new HashMap<>();
        val.put("val", usersListTemp);
        runtimeApi.setVariable(tenantId, executionId, SysVariables.USERS, val);
        // 改变任务变量中的users
        variableApi.setVariableLocal(tenantId, taskId, SysVariables.USERS, val);

        // 改变多实例的标量
        int nrOfInstances = Integer
            .parseInt(variableApi.getVariableByProcessInstanceId(tenantId, executionId, SysVariables.NROFINSTANCES));
        Map<String, Object> val1 = new HashMap<>();
        val1.put("val", nrOfInstances + userChoiceArr.length);
        runtimeApi.setVariable(tenantId, executionId, SysVariables.NROFINSTANCES, val1);
    }

    @Override
    public List<Map<String, Object>> assigneeList4Parallel(String processInstanceId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<TaskModel> taskList = taskApi.findByProcessInstanceId(tenantId, processInstanceId);
        List<Map<String, Object>> listMap = new ArrayList<>();
        Map<String, Object> mapTemp;
        Position personTemp;
        int num = 0;
        ProcessParamModel processParamModel = processParamApi.findByProcessInstanceId(tenantId, processInstanceId);
        String parallelSponsor = processParamModel == null ? "" : processParamModel.getSponsorGuid();
        for (TaskModel tm : taskList) {
            mapTemp = new HashMap<>(16);
            mapTemp.put("num", num + 1);
            mapTemp.put("taskId", tm.getId());
            mapTemp.put("executionId", tm.getExecutionId());
            mapTemp.put("assigneeId", tm.getAssignee());
            personTemp = positionApi.get(tenantId, tm.getAssignee()).getData();
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
        TaskModel taskModel = taskApi.findById(tenantId, taskId);
        String currentAssignee = taskModel.getAssignee();
        String usersObj = variableApi.getVariable(tenantId, taskId, SysVariables.USERS);
        List<String> users = Y9JsonUtil.readValue(usersObj, List.class);
        List<Map<String, Object>> listMap = new ArrayList<>();
        Map<String, Object> mapTemp;
        Position personTemp;
        boolean notStart = false;
        int num = 0;
        if (users != null) {
            for (Object obj : users) {
                String user = obj.toString();
                personTemp = positionApi.get(tenantId, user).getData();
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
        }
        return listMap;
    }

    @Override
    public Map<String, Object> docUserChoise(String processInstanceId) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        Map<String, Object> map = new HashMap<>(16);
        String personId = person.getPersonId(), tenantId = Y9LoginUserHolder.getTenantId();
        List<TaskModel> taskList = taskApi.findByProcessInstanceId(tenantId, processInstanceId);
        if (!taskList.isEmpty()) {
            ProcessParamModel processParamModel = processParamApi.findByProcessInstanceId(tenantId, processInstanceId);
            TaskModel task = taskList.get(0);
            String taskId = task.getId(), processDefinitionId = task.getProcessDefinitionId(),
                processDefinitionKey = processDefinitionId.split(SysVariables.COLON)[0];
            String routeToTask = task.getTaskDefinitionKey();
            String itemId = processParamModel.getItemId();
            map = document4PositionApi.docUserChoise(tenantId, personId, Y9LoginUserHolder.getPositionId(), itemId,
                processDefinitionKey, processDefinitionId, taskId, routeToTask, processInstanceId);
            map.put("taskId", taskId);
            map.put("processInstanceId", processInstanceId);
        }
        return map;
    }

    @Override
    public void removeExecution(String executionId, String taskId, String elementUser) throws Exception {
        String tenantId = Y9LoginUserHolder.getTenantId();
        buttonOperation4PositionApi.deleteMultiInstanceExecution(tenantId, executionId, taskId, elementUser);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void removeExecution4Sequential(String executionId, String taskId, String elementUser, int num)
        throws Exception {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String usersObj = variableApi.getVariableByProcessInstanceId(tenantId, executionId, SysVariables.USERS);
        // 计算删除后的users
        List<String> usersList = new ArrayList<>();
        List<String> usersListTemp = new ArrayList<>();
        if (null != usersObj) {
            usersList = Y9JsonUtil.readValue(usersObj, List.class);
        }
        int number = 1;
        if (usersList != null) {
            for (Object obj : usersList) {
                String user = obj.toString();
                if (num == number) {// 为防止串行中有两个同一人，删除时只删除对应的
                    if (!user.equals(elementUser)) {
                        usersListTemp.add(user);
                    }
                } else {
                    usersListTemp.add(user);
                }
                number += 1;
            }
        }
        // 改变流程变量中的users
        Map<String, Object> val = new HashMap<>();
        val.put("val", usersListTemp);
        runtimeApi.setVariable(tenantId, executionId, SysVariables.USERS, val);
        // 改变任务变量中的users
        variableApi.setVariableLocal(tenantId, taskId, SysVariables.USERS, val);
        // 改变多实例的标量
        int nrOfInstances = Integer
            .parseInt(variableApi.getVariableByProcessInstanceId(tenantId, executionId, SysVariables.NROFINSTANCES));
        Map<String, Object> val1 = new HashMap<>();
        val1.put("val", nrOfInstances - 1);
        runtimeApi.setVariable(tenantId, executionId, SysVariables.NROFINSTANCES, val1);
    }
}
