package net.risesoft.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.TaskVariableApi;
import net.risesoft.api.itemadmin.core.ItemApi;
import net.risesoft.api.itemadmin.core.ProcessParamApi;
import net.risesoft.api.processadmin.HistoricTaskApi;
import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.ProcessTodoApi;
import net.risesoft.api.processadmin.VariableApi;
import net.risesoft.consts.FlowableUiConsts;
import net.risesoft.consts.processadmin.SysVariables;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.model.itemadmin.TaskVariableModel;
import net.risesoft.model.itemadmin.core.ItemModel;
import net.risesoft.model.itemadmin.core.ProcessParamModel;
import net.risesoft.model.processadmin.HistoricTaskInstanceModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.service.HandleFormDataService;
import net.risesoft.service.TodoService;
import net.risesoft.service.UtilService;
import net.risesoft.util.Y9DateTimeUtils;
import net.risesoft.y9.Y9LoginUserHolder;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class TodoServiceImpl implements TodoService {

    private final ProcessTodoApi processTodoApi;
    private final ItemApi itemApi;
    private final VariableApi variableApi;
    private final HistoricTaskApi historicTaskApi;
    private final ProcessParamApi processParamApi;
    private final ProcessDefinitionApi processDefinitionApi;
    private final TaskVariableApi taskvariableApi;
    private final HandleFormDataService handleFormDataService;
    private final UtilService utilService;

    @Override
    public Y9Page<Map<String, Object>> list(String itemId, String searchTerm, Integer page, Integer rows) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
            ItemModel item = itemApi.getByItemId(tenantId, itemId).getData();
            String processDefinitionKey = item.getWorkflowGuid(), itemName = item.getName();
            Y9Page<TaskModel> taskPage;
            if (StringUtils.isBlank(searchTerm)) {
                taskPage = processTodoApi.getListByUserIdAndProcessDefinitionKey(tenantId, positionId,
                    processDefinitionKey, page, rows);
            } else {
                taskPage = processTodoApi.searchListByUserIdAndProcessDefinitionKey(tenantId, positionId,
                    processDefinitionKey, searchTerm, page, rows);
            }
            List<TaskModel> list = taskPage.getRows();
            ObjectMapper objectMapper = new ObjectMapper();
            List<TaskModel> taslList = objectMapper.convertValue(list, new TypeReference<>() {});
            List<Map<String, Object>> items = new ArrayList<>();
            int serialNumber = (page - 1) * rows;
            Map<String, Object> vars;
            Collection<String> keys;
            Map<String, Object> mapTemp;
            ProcessParamModel processParam;
            String taskId;
            List<String> processSerialNumbers = new ArrayList<>();
            for (TaskModel task : taslList) {
                mapTemp = new HashMap<>(16);
                taskId = task.getId();
                String processInstanceId = task.getProcessInstanceId();
                String processDefinitionId = task.getProcessDefinitionId();
                try {
                    Date taskCreateTime = task.getCreateTime();
                    String taskAssignee = task.getAssignee();
                    String description = task.getDescription();
                    String taskName = task.getName();
                    keys = new ArrayList<>();
                    keys.add(SysVariables.TASK_SENDER);
                    vars = variableApi.getVariablesByProcessInstanceId(tenantId, processInstanceId, keys).getData();
                    String taskSender = Strings.nullToEmpty((String)vars.get(SysVariables.TASK_SENDER));
                    int isNewTodo = StringUtils.isBlank(task.getFormKey()) ? 1 : Integer.parseInt(task.getFormKey());
                    processParam = processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                    String processSerialNumber = processParam.getProcessSerialNumber();
                    processSerialNumbers.add(processSerialNumber);
                    mapTemp.put(SysVariables.PROCESS_SERIAL_NUMBER, processSerialNumber);
                    mapTemp.put("processInstanceId", processInstanceId);
                    mapTemp.put("processDefinitionId", processDefinitionId);
                    mapTemp.put("itemId", itemId);
                    mapTemp.put("itemName", itemName);
                    mapTemp.put("taskId", taskId);
                    mapTemp.put("description", description);
                    mapTemp.put("taskName", taskName);
                    mapTemp.put("taskCreateTime", Y9DateTimeUtils.formatDateTimeMinute(taskCreateTime));
                    mapTemp.put("taskAssignee", taskAssignee);
                    mapTemp.put(SysVariables.TASK_SENDER, taskSender);
                    mapTemp.put(SysVariables.IS_NEW_TODO, isNewTodo);
                    String multiInstance = processDefinitionApi
                        .getNodeType(tenantId, task.getProcessDefinitionId(), task.getTaskDefinitionKey())
                        .getData();
                    mapTemp.put(FlowableUiConsts.ISZHUBAN, "");
                    if (multiInstance.equals(SysVariables.PARALLEL)) {
                        mapTemp.put(FlowableUiConsts.ISZHUBAN, "false");
                        String sponsorGuid = processParam.getSponsorGuid();
                        if (StringUtils.isNotBlank(sponsorGuid)) {
                            if (task.getAssignee().equals(sponsorGuid)) {
                                mapTemp.put(FlowableUiConsts.ISZHUBAN, "true");
                            }
                        }
                        String obj =
                            variableApi
                                .getVariableByProcessInstanceId(tenantId, task.getExecutionId(),
                                    SysVariables.NR_OF_ACTIVE_INSTANCES)
                                .getData();
                        int nrOfActiveInstances = obj != null ? Integer.parseInt(obj) : 0;
                        if (nrOfActiveInstances == 1) {
                            mapTemp.put(FlowableUiConsts.ISZHUBAN, "true");
                        }
                        if (StringUtils.isNotBlank(task.getOwner()) && !task.getOwner().equals(task.getAssignee())) {
                            mapTemp.put(FlowableUiConsts.ISZHUBAN, "");
                        }
                    }
                    mapTemp.put(FlowableUiConsts.ISFORWARDING_KEY, false);
                    TaskVariableModel taskVariableModel =
                        taskvariableApi.findByTaskIdAndKeyName(tenantId, taskId, FlowableUiConsts.ISFORWARDING_KEY)
                            .getData();
                    if (taskVariableModel != null) {// 是否正在发送标识
                        mapTemp.put(FlowableUiConsts.ISFORWARDING_KEY, taskVariableModel.getText().contains("true"));
                    }
                    String rollBack = variableApi.getVariableLocal(tenantId, taskId, SysVariables.ROLLBACK).getData();
                    if (Boolean.parseBoolean(rollBack)) {// 退回件
                        mapTemp.put("rollBack", true);
                    }
                    try {
                        String takeBack =
                            variableApi.getVariableLocal(tenantId, taskId, SysVariables.TAKEBACK).getData();
                        if (Boolean.parseBoolean(takeBack)) {// 收回件
                            List<HistoricTaskInstanceModel> hlist = historicTaskApi
                                .findTaskByProcessInstanceIdOrderByStartTimeAsc(tenantId, processInstanceId, "")
                                .getData();
                            if (hlist.get(0).getTaskDefinitionKey().equals(task.getTaskDefinitionKey())) {// 起草收回件，可删除
                                mapTemp.put("takeBack", true);
                            }
                        }
                    } catch (Exception e) {
                        LOGGER.error("收回件异常", e);
                    }
                    utilService.setPublicData(mapTemp, processInstanceId, List.of(task), ItemBoxTypeEnum.TODO);
                } catch (Exception e) {
                    LOGGER.error("获取待办异常taskId:{}", taskId, e);
                }
                mapTemp.put("serialNumber", serialNumber + 1);
                serialNumber += 1;
                items.add(mapTemp);
            }
            handleFormDataService.execute(itemId, items, processSerialNumbers);
            return Y9Page.success(page, taskPage.getTotalPages(), taskPage.getTotal(), items, "获取列表成功");
        } catch (Exception e) {
            LOGGER.error("获取待办异常", e);
        }
        return Y9Page.failure(0, 0, 0, List.of(), "获取列表失败", 500);
    }

    @Override
    public Y9Page<Map<String, Object>> list4Mobile(String itemId, String searchTerm, Integer page, Integer rows) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
            ItemModel item = itemApi.getByItemId(tenantId, itemId).getData();
            String processDefinitionKey = item.getWorkflowGuid(), itemName = item.getName();
            Y9Page<TaskModel> taskPage;
            if (StringUtils.isBlank(searchTerm)) {
                taskPage = processTodoApi.getListByUserIdAndProcessDefinitionKey(tenantId, positionId,
                    processDefinitionKey, page, rows);
            } else {
                taskPage = processTodoApi.searchListByUserIdAndProcessDefinitionKey(tenantId, positionId,
                    processDefinitionKey, searchTerm, page, rows);
            }
            List<TaskModel> list = taskPage.getRows();
            ObjectMapper objectMapper = new ObjectMapper();
            List<TaskModel> taslList = objectMapper.convertValue(list, new TypeReference<>() {});
            List<Map<String, Object>> items = new ArrayList<>();
            int serialNumber = (page - 1) * rows;
            Map<String, Object> vars;
            Collection<String> keys;
            Map<String, Object> mapTemp;
            ProcessParamModel processParam;
            String taskId = "";
            List<String> processSerialNumbers = new ArrayList<>();
            for (TaskModel task : taslList) {
                mapTemp = new HashMap<>(16);
                try {
                    taskId = task.getId();
                    String processInstanceId = task.getProcessInstanceId();
                    String processDefinitionId = task.getProcessDefinitionId();
                    Date taskCreateTime = task.getCreateTime();
                    String taskAssignee = task.getAssignee();
                    String description = task.getDescription();
                    String taskName = task.getName();
                    keys = new ArrayList<>();
                    keys.add(SysVariables.TASK_SENDER);
                    vars = variableApi.getVariablesByProcessInstanceId(tenantId, processInstanceId, keys).getData();
                    String taskSender = Strings.nullToEmpty((String)vars.get(SysVariables.TASK_SENDER));
                    int isNewTodo = StringUtils.isBlank(task.getFormKey()) ? 1 : Integer.parseInt(task.getFormKey());
                    // 催办的时候任务的优先级+5
                    processParam = processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                    String processSerialNumber = processParam.getProcessSerialNumber();
                    processSerialNumbers.add(processSerialNumber);
                    mapTemp.put(SysVariables.PROCESS_SERIAL_NUMBER, processSerialNumber);
                    mapTemp.put("itemName", itemName);
                    mapTemp.put("processInstanceId", processInstanceId);
                    mapTemp.put("processDefinitionId", processDefinitionId);
                    mapTemp.put("taskId", taskId);
                    mapTemp.put("description", description);
                    mapTemp.put("taskName", taskName);
                    mapTemp.put("taskCreateTime", Y9DateTimeUtils.formatDateTimeMinute(taskCreateTime));
                    mapTemp.put("taskAssignee", taskAssignee);
                    mapTemp.put(SysVariables.TASK_SENDER, taskSender);
                    mapTemp.put(SysVariables.IS_NEW_TODO, isNewTodo);
                    String multiInstance = processDefinitionApi
                        .getNodeType(tenantId, task.getProcessDefinitionId(), task.getTaskDefinitionKey())
                        .getData();
                    mapTemp.put(FlowableUiConsts.ISZHUBAN, "");
                    if (multiInstance.equals(SysVariables.PARALLEL)) {
                        mapTemp.put(FlowableUiConsts.ISZHUBAN, "false");
                        String sponsorGuid = processParam.getSponsorGuid();
                        if (StringUtils.isNotBlank(sponsorGuid)) {
                            if (task.getAssignee().equals(sponsorGuid)) {
                                mapTemp.put(FlowableUiConsts.ISZHUBAN, "true");
                            }
                        }
                        String obj =
                            variableApi
                                .getVariableByProcessInstanceId(tenantId, task.getExecutionId(),
                                    SysVariables.NR_OF_ACTIVE_INSTANCES)
                                .getData();
                        int nrOfActiveInstances = obj != null ? Integer.parseInt(obj) : 0;
                        if (nrOfActiveInstances == 1) {
                            mapTemp.put(FlowableUiConsts.ISZHUBAN, "true");
                        }
                        if (StringUtils.isNotBlank(task.getOwner()) && !task.getOwner().equals(task.getAssignee())) {
                            mapTemp.put(FlowableUiConsts.ISZHUBAN, "");
                        }
                    }
                    utilService.setPublicData(mapTemp, processInstanceId, List.of(task), ItemBoxTypeEnum.TODO);
                } catch (Exception e) {
                    LOGGER.error("查询待办任务出错taskId:{}", taskId, e);
                }
                mapTemp.put("serialNumber", serialNumber + 1);
                serialNumber += 1;
                items.add(mapTemp);
            }
            handleFormDataService.execute(itemId, items, processSerialNumbers);
            return Y9Page.success(page, taskPage.getTotalPages(), taskPage.getTotal(), items, "获取列表成功");
        } catch (Exception e) {
            LOGGER.error("查询待办任务出错", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取列表失败");
    }
}