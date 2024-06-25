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
     * @param taskEntity 任务实体
     * @param variables 流程变量
     * @param local 任务变量
     */
    void weiXinRemind(final DelegateTask taskEntity, final Map<String, Object> variables,
        final Map<String, Object> local);
}
