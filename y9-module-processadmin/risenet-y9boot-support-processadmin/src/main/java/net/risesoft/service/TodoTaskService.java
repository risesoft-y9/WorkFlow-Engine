package net.risesoft.service;

import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.task.service.delegate.DelegateTask;

import java.util.Map;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
public interface TodoTaskService {

    /**
     * Description:
     *
     * @param taskEntityHti 历史任务实体
     * @param variables     变量
     */
    void deleteTodo(final DelegateTask taskEntityHti, final Map<String, Object> variables);

    /**
     * Description:
     *
     * @param event     事件
     * @param variables 变量
     */
    void deleteTodoByProcessInstanceId(final FlowableEvent event, final Map<String, Object> variables);

    /**
     * Description:
     *
     * @param task      任务
     * @param variables 变量
     */
    void saveTodoTask(final DelegateTask task, final Map<String, Object> variables);
}
