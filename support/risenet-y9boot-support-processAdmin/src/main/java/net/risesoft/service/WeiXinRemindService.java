package net.risesoft.service;

import java.util.Map;

import org.flowable.task.service.delegate.DelegateTask;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
public interface WeiXinRemindService {

    /**
     * Description:
     * 
     * @param taskEntity
     * @param variables
     * @param local
     */
    public void weiXinRemind(final DelegateTask taskEntity, final Map<String, Object> variables, final Map<String, Object> local);
}
