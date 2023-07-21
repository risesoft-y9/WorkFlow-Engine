package net.risesoft.listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.flowable.common.engine.api.delegate.event.AbstractFlowableEventListener;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.task.service.impl.persistence.entity.TaskEntityImpl;
import org.springframework.beans.BeansException;

import net.risesoft.service.CustomHistoricProcessService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9Context;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
public class EventListener4ExcludeTodo2Doing extends AbstractFlowableEventListener {

    @Override
    public boolean isFailOnException() {
        // TODO Auto-generated method stub
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onEvent(FlowableEvent event) {
        FlowableEngineEventType type = (FlowableEngineEventType)event.getType();
        switch (type) {
            case TASK_CREATED:
                org.flowable.common.engine.impl.event.FlowableEntityEventImpl entity = (org.flowable.common.engine.impl.event.FlowableEntityEventImpl)event;
                TaskEntityImpl taskEntity = (TaskEntityImpl)entity.getEntity();
                String assignee = taskEntity.getAssignee();
                if (StringUtils.isNotBlank(assignee)) {
                    taskEntity.setVariable(assignee, assignee);
                }
                /**
                 * 下面是添加其他业务逻辑需要的任务变量String taskSenderId=(String)
                 */
                Map<String, Object> mapTemp = new HashMap<String, Object>(16);
                String user = (String)taskEntity.getVariable(SysVariables.USER);
                List<String> users = (List<String>)taskEntity.getVariable(SysVariables.USERS);
                String taskSender = (String)taskEntity.getVariable(SysVariables.TASKSENDER);
                String taskSenderId = (String)taskEntity.getVariable(SysVariables.TASKSENDERID);
                Integer priority = (Integer)taskEntity.getVariable(SysVariables.PRIORITY);

                if (null != user) {
                    mapTemp.put(SysVariables.USER, user);
                }
                if (null != users && users.size() > 0) {
                    mapTemp.put(SysVariables.USERS, users);
                }

                if (StringUtils.isNotBlank(taskSender)) {
                    mapTemp.put(SysVariables.TASKSENDER, taskSender);
                }
                if (StringUtils.isNotBlank(taskSenderId)) {
                    mapTemp.put(SysVariables.TASKSENDERID, taskSenderId);
                }
                if (null != priority) {
                    Integer p = taskEntity.getPriority();
                    if (!p.equals(priority)) {
                        taskEntity.setPriority(priority);
                        try {
                            Y9Context.getBean(CustomHistoricProcessService.class).setPriority(taskEntity.getProcessInstanceId(), priority.toString());
                        } catch (BeansException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                taskEntity.setVariablesLocal(mapTemp);

                HistoricProcessInstance historicProcessInstance = Y9Context.getBean(CustomHistoricProcessService.class).getById(taskEntity.getProcessInstanceId());
                if (null != historicProcessInstance) {
                    String businessKey = historicProcessInstance.getBusinessKey();
                    taskEntity.setCategory(businessKey);
                }
                break;
            case TASK_COMPLETED:
                break;
            default:
        }
    }

}
