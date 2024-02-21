package net.risesoft.service;

import java.util.Map;

import org.flowable.task.service.delegate.DelegateTask;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
public interface TaskEntrustService {
    /**
     * Description:
     * 
     * @param task
     * @param variables
     * @return
     */
    public DelegateTask entrust(final DelegateTask task, final Map<String, Object> variables);
}
