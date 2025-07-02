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

import net.risesoft.api.itemadmin.ButtonOperationApi;
import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.api.itemadmin.SignDeptDetailApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.processadmin.RuntimeApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.api.processadmin.VariableApi;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.model.itemadmin.SignDeptDetailModel;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.processadmin.TaskModel;
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

    private final OrgUnitApi orgUnitApi;

    private final ButtonOperationApi buttonOperationApi;

    private final RuntimeApi runtimeApi;

    private final ProcessParamApi processParamApi;

    private final Process4SearchService process4SearchService;

    private final SignDeptDetailApi signDeptDetailApi;

    @Override
    public void addExecutionId(String processInstanceId, String taskId, String userChoice, String isSendSms,
        String isShuMing, String smsContent) throws Exception {
        String tenantId = Y9LoginUserHolder.getTenantId();
        TaskModel task = taskApi.findById(tenantId, taskId).getData();
        String activityId = task.getTaskDefinitionKey();
        String[] users = userChoice.split(";");
        ProcessParamModel processParamModel =
            processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
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
            buttonOperationApi.addMultiInstanceExecution(tenantId, activityId, processInstanceId, taskId, user);
        }
        if (processParamModel != null) {
            process4SearchService.saveToDataCenter1(tenantId, taskId, processParamModel);
        }
    }

    @Override
    public void addExecutionId(ProcessParamModel processParamModel, String activityId, String userChoice) {
        String tenantId = Y9LoginUserHolder.getTenantId(), processInstanceId = processParamModel.getProcessInstanceId(),
            processSerialNumber = processParamModel.getProcessSerialNumber(),
            positionId = Y9LoginUserHolder.getPositionId();
        String[] users = userChoice.split(";");
        for (String user : users) {
            buttonOperationApi.addMultiInstanceExecutionByActivityId(tenantId, activityId, processInstanceId, user);
        }
        List<TaskModel> taskList = taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
        List<SignDeptDetailModel> signDeptDetailModels =
            signDeptDetailApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
        SignDeptDetailModel ssd = signDeptDetailModels.get(0);
        taskList.forEach(task -> {
            if (signDeptDetailModels.stream().noneMatch(sdd -> sdd.getExecutionId().equals(task.getExecutionId()))) {
                OrgUnit bureau = orgUnitApi.getBureau(tenantId, task.getAssignee()).getData();
                SignDeptDetailModel signDeptDetail = new SignDeptDetailModel();
                signDeptDetail.setProcessSerialNumber(processSerialNumber);
                signDeptDetail.setProcessInstanceId(processInstanceId);
                signDeptDetail.setExecutionId(task.getExecutionId());
                signDeptDetail.setTaskId(ssd.getTaskId());
                signDeptDetail.setTaskName(ssd.getTaskName());
                signDeptDetail.setSenderId(ssd.getSenderId());
                signDeptDetail.setSenderName(ssd.getSenderName());
                signDeptDetail.setDeptId(bureau.getId());
                signDeptDetail.setDeptName(bureau.getName());
                signDeptDetail.setTabIndex(bureau.getTabIndex());
                signDeptDetailApi.saveOrUpdate(tenantId, positionId, signDeptDetail);
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addExecutionId4Sequential(String executionId, String taskId, String userChoice, String selectUserId,
        int num) throws Exception {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String[] userChoiceArr = userChoice.split(";");
        String usersObj =
            variableApi.getVariableByProcessInstanceId(tenantId, executionId, SysVariables.USERS).getData();
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
        int nrOfInstances = Integer.parseInt(
            variableApi.getVariableByProcessInstanceId(tenantId, executionId, SysVariables.NROFINSTANCES).getData());
        Map<String, Object> val1 = new HashMap<>();
        val1.put("val", nrOfInstances + userChoiceArr.length);
        runtimeApi.setVariable(tenantId, executionId, SysVariables.NROFINSTANCES, val1);
    }

    @Override
    public List<Map<String, Object>> listAssignee4Parallel(String processInstanceId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<TaskModel> taskList = taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
        List<Map<String, Object>> listMap = new ArrayList<>();
        Map<String, Object> mapTemp;
        OrgUnit personTemp;
        int num = 0;
        ProcessParamModel processParamModel =
            processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
        String parallelSponsor = processParamModel == null ? "" : processParamModel.getSponsorGuid();
        for (TaskModel tm : taskList) {
            mapTemp = new HashMap<>(16);
            mapTemp.put("num", num + 1);
            mapTemp.put("taskId", tm.getId());
            mapTemp.put("executionId", tm.getExecutionId());
            mapTemp.put("assigneeId", tm.getAssignee());
            personTemp = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, tm.getAssignee()).getData();
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
    public List<Map<String, Object>> listAssignee4Sequential(String taskId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        TaskModel taskModel = taskApi.findById(tenantId, taskId).getData();
        String currentAssignee = taskModel.getAssignee();
        String usersObj = variableApi.getVariable(tenantId, taskId, SysVariables.USERS).getData();
        List<String> users = Y9JsonUtil.readValue(usersObj, List.class);
        List<Map<String, Object>> listMap = new ArrayList<>();
        Map<String, Object> mapTemp;
        OrgUnit personTemp;
        boolean notStart = false;
        int num = 0;
        if (users != null) {
            for (Object obj : users) {
                String user = obj.toString();
                personTemp = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, user).getData();
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
    public void removeExecution(String executionId, String taskId, String elementUser) throws Exception {
        String tenantId = Y9LoginUserHolder.getTenantId();
        buttonOperationApi.deleteMultiInstanceExecution(tenantId, executionId, taskId, elementUser);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void removeExecution4Sequential(String executionId, String taskId, String elementUser, int num)
        throws Exception {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String usersObj =
            variableApi.getVariableByProcessInstanceId(tenantId, executionId, SysVariables.USERS).getData();
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
        int nrOfInstances = Integer.parseInt(
            variableApi.getVariableByProcessInstanceId(tenantId, executionId, SysVariables.NROFINSTANCES).getData());
        Map<String, Object> val1 = new HashMap<>();
        val1.put("val", nrOfInstances - 1);
        runtimeApi.setVariable(tenantId, executionId, SysVariables.NROFINSTANCES, val1);
    }
}
