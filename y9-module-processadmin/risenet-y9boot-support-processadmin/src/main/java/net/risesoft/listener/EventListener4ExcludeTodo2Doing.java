package net.risesoft.listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.flowable.common.engine.api.delegate.event.AbstractFlowableEventListener;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.engine.delegate.event.impl.FlowableSequenceFlowTakenEventImpl;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.task.service.impl.persistence.entity.TaskEntityImpl;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.TaskRelatedApi;
import net.risesoft.api.itemadmin.core.ProcessParamApi;
import net.risesoft.consts.processadmin.SysVariables;
import net.risesoft.enums.TaskRelatedEnum;
import net.risesoft.model.itemadmin.TaskRelatedModel;
import net.risesoft.model.itemadmin.core.ProcessParamModel;
import net.risesoft.service.CustomHistoricProcessService;
import net.risesoft.service.CustomProcessDefinitionService;
import net.risesoft.service.InterfaceUtilService;
import net.risesoft.y9.Y9Context;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@Slf4j
public class EventListener4ExcludeTodo2Doing extends AbstractFlowableEventListener {

    @Override
    public boolean isFailOnException() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void onEvent(FlowableEvent event) {
        FlowableEngineEventType type = (FlowableEngineEventType)event.getType();
        switch (type) {
            case TASK_CREATED:
                handleTaskCreatedEvent(event);
                break;
            case TASK_COMPLETED:
                // 任务完成事件处理逻辑可以在这里添加
                break;
            case SEQUENCEFLOW_TAKEN:
                handleSequenceFlowTakenEvent(event);
                break;
            default:
                // 默认处理逻辑
                break;
        }
    }

    /**
     * 处理任务创建事件
     */
    private void handleTaskCreatedEvent(FlowableEvent event) {
        org.flowable.common.engine.impl.event.FlowableEntityEventImpl entity =
            (org.flowable.common.engine.impl.event.FlowableEntityEventImpl)event;
        TaskEntityImpl taskEntity = (TaskEntityImpl)entity.getEntity();

        // 设置任务分配人变量
        setAssigneeVariable(taskEntity);

        // 处理任务变量
        Map<String, Object> localVariables = extractTaskVariables(taskEntity);
        taskEntity.setVariablesLocal(localVariables);

        // 处理优先级
        handleTaskPriority(taskEntity);

        // 设置任务到期时间
        setTaskDueDate(taskEntity);

        // 处理任务相关信息
        handleTaskRelatedInfo(taskEntity);

        // 设置业务分类
        setBusinessCategory(taskEntity);
    }

    /**
     * 设置任务分配人变量
     */
    private void setAssigneeVariable(TaskEntityImpl taskEntity) {
        String assignee = taskEntity.getAssignee();
        if (StringUtils.isNotBlank(assignee)) {
            taskEntity.setVariable(assignee, assignee);
        }
    }

    /**
     * 提取任务变量
     */
    private Map<String, Object> extractTaskVariables(TaskEntityImpl taskEntity) {
        Map<String, Object> mapTemp = new HashMap<>(16);

        String user = (String)taskEntity.getVariable(SysVariables.USER);
        @SuppressWarnings("unchecked")
        List<String> users = (List<String>)taskEntity.getVariable(SysVariables.USERS);
        String taskSender = (String)taskEntity.getVariable(SysVariables.TASK_SENDER);
        String taskSenderId = (String)taskEntity.getVariable(SysVariables.TASK_SENDER_ID);

        if (null != user) {
            mapTemp.put(SysVariables.USER, user);
            System.out.println("###########user##" + user);
        }
        if (null != users && !users.isEmpty()) {
            mapTemp.put(SysVariables.USERS, users);
        }
        if (StringUtils.isNotBlank(taskSender)) {
            mapTemp.put(SysVariables.TASK_SENDER, taskSender);
        }
        if (StringUtils.isNotBlank(taskSenderId)) {
            mapTemp.put(SysVariables.TASK_SENDER_ID, taskSenderId);
        }

        return mapTemp;
    }

    /**
     * 处理任务优先级
     */
    private void handleTaskPriority(TaskEntityImpl taskEntity) {
        Integer priority = (Integer)taskEntity.getVariable(SysVariables.PRIORITY);
        if (null != priority) {
            Integer currentPriority = taskEntity.getPriority();
            if (!currentPriority.equals(priority)) {
                taskEntity.setPriority(priority);
                try {
                    Y9Context.getBean(CustomHistoricProcessService.class)
                        .setPriority(taskEntity.getProcessInstanceId(), priority.toString());
                } catch (Exception e) {
                    LOGGER.error("设置优先级失败", e);
                }
            }
        }
    }

    /**
     * 设置任务到期时间
     */
    private void setTaskDueDate(TaskEntityImpl taskEntity) {
        String tenantId = (String)taskEntity.getVariable(SysVariables.TENANT_ID);
        String processSerialNumber = (String)taskEntity.getVariable(SysVariables.PROCESS_SERIAL_NUMBER);

        ProcessParamApi processParamApi = Y9Context.getBean(ProcessParamApi.class);
        ProcessParamModel processParamModel =
            processParamApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();

        if (null != processParamModel.getDueDate()) {
            taskEntity.setDueDate(processParamModel.getDueDate());
        }
    }

    /**
     * 处理任务相关信息（办文说明、上一步操作等）
     */
    private void handleTaskRelatedInfo(TaskEntityImpl taskEntity) {
        String tenantId = (String)taskEntity.getVariable(SysVariables.TENANT_ID);
        String processSerialNumber = (String)taskEntity.getVariable(SysVariables.PROCESS_SERIAL_NUMBER);
        String taskSenderId = (String)taskEntity.getVariable(SysVariables.TASK_SENDER_ID);
        String taskSender = (String)taskEntity.getVariable(SysVariables.TASK_SENDER);

        ProcessParamApi processParamApi = Y9Context.getBean(ProcessParamApi.class);
        ProcessParamModel processParamModel =
            processParamApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();

        CustomProcessDefinitionService customProcessDefinitionService =
            Y9Context.getBean(CustomProcessDefinitionService.class);
        boolean isSub = customProcessDefinitionService.isSubProcessChildNode(taskEntity.getProcessDefinitionId(),
            taskEntity.getTaskDefinitionKey());

        // 设置办文说明
        setDocumentDescription(taskEntity, processParamModel, isSub, taskSenderId, taskSender, tenantId);

        // 设置任务的上一步操作
        setPreviousAction(taskEntity, processParamModel, isSub, taskSenderId, taskSender, tenantId);
    }

    /**
     * 设置办文说明
     */
    private void setDocumentDescription(TaskEntityImpl taskEntity, ProcessParamModel processParamModel, boolean isSub,
        String taskSenderId, String taskSender, String tenantId) {
        if (StringUtils.isNotBlank(processParamModel.getDescription())) {
            TaskRelatedApi taskRelatedApi = Y9Context.getBean(TaskRelatedApi.class);
            TaskRelatedModel taskRelatedModel = new TaskRelatedModel();
            taskRelatedModel.setInfoType(TaskRelatedEnum.BANWENSHUOMING.getValue());
            taskRelatedModel.setTaskId(taskEntity.getId());
            taskRelatedModel.setProcessInstanceId(taskEntity.getProcessInstanceId());
            taskRelatedModel.setExecutionId(taskEntity.getExecutionId());
            taskRelatedModel.setSub(isSub);
            taskRelatedModel.setProcessSerialNumber(processParamModel.getProcessSerialNumber());
            taskRelatedModel.setMsgContent(processParamModel.getDescription());
            taskRelatedModel.setSenderId(taskSenderId);
            taskRelatedModel.setSenderName(taskSender);
            taskRelatedApi.saveOrUpdate(tenantId, taskRelatedModel);
        }
    }

    /**
     * 设置任务的上一步操作
     */
    private void setPreviousAction(TaskEntityImpl taskEntity, ProcessParamModel processParamModel, boolean isSub,
        String taskSenderId, String taskSender, String tenantId) {
        Object actionName = taskEntity.getVariable(SysVariables.ACTION_NAME + ":" + taskSenderId);
        if (null != actionName) {
            TaskRelatedApi taskRelatedApi = Y9Context.getBean(TaskRelatedApi.class);
            TaskRelatedModel taskRelatedModel = new TaskRelatedModel();
            taskRelatedModel.setInfoType(TaskRelatedEnum.ACTIONNAME.getValue());
            taskRelatedModel.setTaskId(taskEntity.getId());
            taskRelatedModel.setProcessInstanceId(taskEntity.getProcessInstanceId());
            taskRelatedModel.setExecutionId(taskEntity.getExecutionId());
            taskRelatedModel.setSub(isSub);
            taskRelatedModel.setProcessSerialNumber(processParamModel.getProcessSerialNumber());
            taskRelatedModel.setMsgContent(String.valueOf(actionName));
            taskRelatedModel.setSenderId(taskSenderId);
            taskRelatedModel.setSenderName(taskSender);
            taskRelatedApi.saveOrUpdate(tenantId, taskRelatedModel);
        }
    }

    /**
     * 设置业务分类
     */
    private void setBusinessCategory(TaskEntityImpl taskEntity) {
        HistoricProcessInstance historicProcessInstance =
            Y9Context.getBean(CustomHistoricProcessService.class).getById(taskEntity.getProcessInstanceId());
        if (null != historicProcessInstance) {
            String businessKey = historicProcessInstance.getBusinessKey();
            taskEntity.setCategory(businessKey);
        }
    }

    /**
     * 处理路由事件
     */
    private void handleSequenceFlowTakenEvent(FlowableEvent event) {
        // 路由监听
        FlowableSequenceFlowTakenEventImpl entity = (FlowableSequenceFlowTakenEventImpl)event;
        // 接口调用
        InterfaceUtilService interfaceUtilService = Y9Context.getBean(InterfaceUtilService.class);
        try {
            interfaceUtilService.interfaceCallBySequenceFlow(entity, "经过");
        } catch (Exception e) {
            throw new RuntimeException("调用接口失败 EventListener4ExcludeTodo2Doing_SEQUENCEFLOW_TAKEN");
        }
    }
}