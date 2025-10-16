package net.risesoft.service;

import java.util.Map;

import org.flowable.task.service.delegate.DelegateTask;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
public interface Task4ListenerService {

    /**
     * 异步处理,自定义变量科室id保存
     *
     * @param task 任务
     * @param variables 变量
     */
    void task4AssignmentListener(final DelegateTask task, final Map<String, Object> variables);

    /**
     * 异步处理，记录岗位/人员名称
     *
     * @param task 任务
     * @param variables 变量
     */
    void task4DeleteListener(final DelegateTask task, final Map<String, Object> variables);
}
