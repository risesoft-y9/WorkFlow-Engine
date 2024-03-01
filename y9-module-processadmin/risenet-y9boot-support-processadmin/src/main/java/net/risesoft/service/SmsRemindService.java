package net.risesoft.service;

import java.util.Map;

import org.flowable.task.service.delegate.DelegateTask;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
public interface SmsRemindService {

    /**
     * Description:
     *
     * @param task
     * @param vars
     * @param local
     */
    public void smsRemind(final DelegateTask task, final Map<String, Object> vars, final Map<String, Object> local);
}
