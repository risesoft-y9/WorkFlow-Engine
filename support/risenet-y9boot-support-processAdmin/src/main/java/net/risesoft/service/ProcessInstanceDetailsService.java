package net.risesoft.service;

import java.util.Map;

import org.flowable.task.service.delegate.DelegateTask;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
public interface ProcessInstanceDetailsService {

    /**
     * Description:
     *
     * @param taskEntity
     * @param variables
     */
    public void saveProcessInstanceDetails(final DelegateTask taskEntity, final Map<String, Object> variables);

    /**
     * Description:
     *
     * @param taskEntity
     * @param variables
     */
    public void updateProcessInstanceDetails(final DelegateTask taskEntity, final Map<String, Object> variables);
}
