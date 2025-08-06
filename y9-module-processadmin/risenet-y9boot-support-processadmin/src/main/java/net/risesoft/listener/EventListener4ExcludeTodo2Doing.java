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

    @SuppressWarnings("unchecked")
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void onEvent(FlowableEvent event) {
        FlowableEngineEventType type = (FlowableEngineEventType)event.getType();
        switch (type) {
            case TASK_CREATED:
                org.flowable.common.engine.impl.event.FlowableEntityEventImpl entity =
                    (org.flowable.common.engine.impl.event.FlowableEntityEventImpl)event;
                TaskEntityImpl taskEntity = (TaskEntityImpl)entity.getEntity();
                String assignee = taskEntity.getAssignee();
                if (StringUtils.isNotBlank(assignee)) {
                    taskEntity.setVariable(assignee, assignee);
                }
                /*
                 * 下面是添加其他业务逻辑需要的任务变量String taskSenderId=(String)
                 */
                Map<String, Object> mapTemp = new HashMap<>(16);
                String user = (String)taskEntity.getVariable(SysVariables.USER);
                List<String> users = (List<String>)taskEntity.getVariable(SysVariables.USERS);
                String taskSender = (String)taskEntity.getVariable(SysVariables.TASK_SENDER);
                String taskSenderId = (String)taskEntity.getVariable(SysVariables.TASK_SENDER_ID);
                String tenantId = (String)taskEntity.getVariable(SysVariables.TENANT_ID);
                String processSerialNumber = (String)taskEntity.getVariable(SysVariables.PROCESS_SERIAL_NUMBER);
                Integer priority = (Integer)taskEntity.getVariable(SysVariables.PRIORITY);
                Object actionName = taskEntity.getVariable(SysVariables.ACTION_NAME + ":" + taskSenderId);

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
                if (null != priority) {
                    Integer p = taskEntity.getPriority();
                    if (!p.equals(priority)) {
                        taskEntity.setPriority(priority);
                        try {
                            Y9Context.getBean(CustomHistoricProcessService.class)
                                .setPriority(taskEntity.getProcessInstanceId(), priority.toString());
                        } catch (Exception e) {
                            LOGGER.error("设置优先级失败", e);
                        }
                    }
                }
                taskEntity.setVariablesLocal(mapTemp);

                /**
                 * 设置到期时间
                 */
                ProcessParamApi processParamApi = Y9Context.getBean(ProcessParamApi.class);
                ProcessParamModel processParamModel =
                    processParamApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
                if (null != processParamModel.getDueDate()) {
                    taskEntity.setDueDate(processParamModel.getDueDate());
                }
                /**
                 * 设置办文说明
                 */
                CustomProcessDefinitionService customProcessDefinitionService =
                    Y9Context.getBean(CustomProcessDefinitionService.class);
                boolean isSub = customProcessDefinitionService
                    .isSubProcessChildNode(taskEntity.getProcessDefinitionId(), taskEntity.getTaskDefinitionKey());
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
                /**
                 * 设置任务的上一步操作
                 */
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

                HistoricProcessInstance historicProcessInstance =
                    Y9Context.getBean(CustomHistoricProcessService.class).getById(taskEntity.getProcessInstanceId());
                if (null != historicProcessInstance) {
                    String businessKey = historicProcessInstance.getBusinessKey();
                    taskEntity.setCategory(businessKey);
                }
                break;
            case TASK_COMPLETED:
                break;
            case SEQUENCEFLOW_TAKEN:
                // 路由监听
                FlowableSequenceFlowTakenEventImpl entity0 = (FlowableSequenceFlowTakenEventImpl)event;
                // 接口调用
                InterfaceUtilService interfaceUtilService = Y9Context.getBean(InterfaceUtilService.class);
                try {
                    interfaceUtilService.interfaceCallBySequenceFlow(entity0, "经过");
                } catch (Exception e) {
                    throw new RuntimeException("调用接口失败 EventListener4ExcludeTodo2Doing_SEQUENCEFLOW_TAKEN");
                }
                break;
            default:
        }
    }

}
