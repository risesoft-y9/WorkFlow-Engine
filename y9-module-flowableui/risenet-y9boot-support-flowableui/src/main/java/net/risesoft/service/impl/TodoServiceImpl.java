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
import net.risesoft.y9.Y9FlowableHolder;
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
            String tenantId = Y9LoginUserHolder.getTenantId();
            String positionId = Y9FlowableHolder.getPositionId();
            ItemModel item = itemApi.getByItemId(tenantId, itemId).getData();
            String processDefinitionKey = item.getWorkflowGuid();
            String itemName = item.getName();
            Y9Page<TaskModel> taskPage;
            if (StringUtils.isBlank(searchTerm)) {
                taskPage = processTodoApi.getListByUserIdAndProcessDefinitionKey(tenantId, positionId,
                    processDefinitionKey, page, rows);
            } else {
                taskPage = processTodoApi.searchListByUserIdAndProcessDefinitionKey(tenantId, positionId,
                    processDefinitionKey, searchTerm, page, rows);
            }
            List<TaskModel> taskList = taskPage.getRows();
            List<Map<String, Object>> items = new ArrayList<>();
            List<String> processSerialNumbers = new ArrayList<>();
            int serialNumber = (page - 1) * rows;
            for (TaskModel task : taskList) {
                Map<String, Object> itemMap = buildTodoListItem(task, tenantId, itemId, itemName, processSerialNumbers);
                itemMap.put("serialNumber", serialNumber + 1);
                serialNumber++;
                items.add(itemMap);
            }
            handleFormDataService.execute(itemId, items, processSerialNumbers);
            return Y9Page.success(page, taskPage.getTotalPages(), taskPage.getTotal(), items, "获取列表成功");
        } catch (Exception e) {
            LOGGER.error("获取待办异常", e);
        }
        return Y9Page.failure(0, 0, 0, List.of(), "获取列表失败", 500);
    }

    private Map<String, Object> buildTodoListItem(TaskModel task, String tenantId, String itemId, String itemName,
        List<String> processSerialNumbers) {
        Map<String, Object> mapTemp = new HashMap<>(16);
        String taskId = task.getId();
        String processInstanceId = task.getProcessInstanceId();

        try {
            // 基本任务信息
            Date taskCreateTime = task.getCreateTime();
            String taskAssignee = task.getAssignee();
            String description = task.getDescription();
            String taskName = task.getName();

            // 获取任务发送者
            Collection<String> keys = new ArrayList<>();
            keys.add(SysVariables.TASK_SENDER);
            Map<String, Object> vars =
                variableApi.getVariablesByProcessInstanceId(tenantId, processInstanceId, keys).getData();
            String taskSender = Strings.nullToEmpty((String)vars.get(SysVariables.TASK_SENDER));

            // 新待办标识
            int isNewTodo = StringUtils.isBlank(task.getFormKey()) ? 1 : Integer.parseInt(task.getFormKey());

            // 流程参数信息
            ProcessParamModel processParam =
                processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
            String processSerialNumber = processParam.getProcessSerialNumber();
            processSerialNumbers.add(processSerialNumber);

            // 设置基本字段
            mapTemp.put(SysVariables.PROCESS_SERIAL_NUMBER, processSerialNumber);
            mapTemp.put("processInstanceId", processInstanceId);
            mapTemp.put("processDefinitionId", task.getProcessDefinitionId());
            mapTemp.put("itemId", itemId);
            mapTemp.put("itemName", itemName);
            mapTemp.put("taskId", taskId);
            mapTemp.put("description", description);
            mapTemp.put("taskName", taskName);
            mapTemp.put("taskCreateTime", Y9DateTimeUtils.formatDateTimeMinute(taskCreateTime));
            mapTemp.put("taskAssignee", taskAssignee);
            mapTemp.put(SysVariables.TASK_SENDER, taskSender);
            mapTemp.put(SysVariables.IS_NEW_TODO, isNewTodo);

            // 处理并行任务
            handleParallelTaskInfo(mapTemp, task, tenantId, processParam);

            // 处理任务状态标识
            handleTaskStatusFlags(mapTemp, task, tenantId, processInstanceId);

            // 设置公共数据
            utilService.setPublicData(mapTemp, processInstanceId, List.of(task), ItemBoxTypeEnum.TODO);
        } catch (Exception e) {
            LOGGER.error("获取待办异常taskId:{}", taskId, e);
        }

        return mapTemp;
    }

    private void handleParallelTaskInfo(Map<String, Object> mapTemp, TaskModel task, String tenantId,
        ProcessParamModel processParam) {
        try {
            String multiInstance =
                processDefinitionApi.getNodeType(tenantId, task.getProcessDefinitionId(), task.getTaskDefinitionKey())
                    .getData();
            mapTemp.put(FlowableUiConsts.ISZHUBAN, "");

            if (SysVariables.PARALLEL.equals(multiInstance)) {
                mapTemp.put(FlowableUiConsts.ISZHUBAN, "false");
                String sponsorGuid = processParam.getSponsorGuid();

                if (StringUtils.isNotBlank(sponsorGuid) && task.getAssignee().equals(sponsorGuid)) {
                    mapTemp.put(FlowableUiConsts.ISZHUBAN, "true");
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
        } catch (Exception e) {
            LOGGER.warn("处理并行任务信息异常, taskId: {}", task.getId(), e);
        }
    }

    private void handleTaskStatusFlags(Map<String, Object> mapTemp, TaskModel task, String tenantId,
        String processInstanceId) {
        String taskId = task.getId();
        try {
            // 发送标识
            mapTemp.put(FlowableUiConsts.ISFORWARDING_KEY, false);
            TaskVariableModel taskVariableModel =
                taskvariableApi.findByTaskIdAndKeyName(tenantId, taskId, FlowableUiConsts.ISFORWARDING_KEY).getData();
            if (taskVariableModel != null) {
                mapTemp.put(FlowableUiConsts.ISFORWARDING_KEY, taskVariableModel.getText().contains("true"));
            }
            // 退回件标识
            String rollBack = variableApi.getVariableLocal(tenantId, taskId, SysVariables.ROLLBACK).getData();
            if (Boolean.parseBoolean(rollBack)) {
                mapTemp.put("rollBack", true);
            }
            // 收回件标识
            handleTakeBackFlag(mapTemp, task, tenantId, processInstanceId);
        } catch (Exception e) {
            LOGGER.warn("处理任务状态标识异常, taskId: {}", taskId, e);
        }
    }

    private void handleTakeBackFlag(Map<String, Object> mapTemp, TaskModel task, String tenantId,
        String processInstanceId) {
        try {
            String taskId = task.getId();
            String takeBack = variableApi.getVariableLocal(tenantId, taskId, SysVariables.TAKEBACK).getData();
            if (Boolean.parseBoolean(takeBack)) {
                List<HistoricTaskInstanceModel> hlist =
                    historicTaskApi.findTaskByProcessInstanceIdOrderByStartTimeAsc(tenantId, processInstanceId, "")
                        .getData();
                if (!hlist.isEmpty() && hlist.get(0).getTaskDefinitionKey().equals(task.getTaskDefinitionKey())) {
                    mapTemp.put("takeBack", true);
                }
            }
        } catch (Exception e) {
            LOGGER.error("收回件异常", e);
        }
    }
}