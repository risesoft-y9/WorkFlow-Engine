package net.risesoft.service;

import java.util.Map;

import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.task.service.delegate.DelegateTask;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
public interface TodoTaskService {

    /**
     * Description:
     * 
     * @param taskEntityHti
     * @param variables
     */
    public void deleteTodo(final DelegateTask taskEntityHti, final Map<String, Object> variables);

    /**
     * Description:
     * 
     * @param event
     * @param variables
     */
    public void deleteTodoByProcessInstanceId(final FlowableEvent event, final Map<String, Object> variables);

    /**
     * Description:
     * 
     * @param task
     * @param variables
     */
    public void saveTodoTask(final DelegateTask task, final Map<String, Object> variables);

}
