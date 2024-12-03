package net.risesoft.service;

import org.flowable.task.service.delegate.DelegateTask;

import java.util.Map;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
public interface ProcessTaskRelatedService {

    /**
     * Description:
     *
     * @param task 任务
     * @param vars 流程变量
     * @param local 任务变量
     */
    void execute(final DelegateTask task, final Map<String, Object> vars, final Map<String, Object> local);
}
