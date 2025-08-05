package net.risesoft.service;

import org.flowable.task.service.delegate.DelegateTask;

/**
 * @author qinman
 * @author zhangchongjie
 */
public interface Task4ActRuDetailService {

    void todo2doing(DelegateTask taskEntity);

    void createTodo(DelegateTask taskEntity);

    /**
     * 单任务节点签收时，当前任务的办理人的待办保留，其他人的待办改为在办
     *
     * @param taskEntity 任务实体
     */
    void claim(DelegateTask taskEntity);

    /**
     * <签收节点还没有签收的时候,如果被重定向/收回,则把待签收人都设置为在办>
     *
     * @param taskEntity 任务实体
     */
    void todo2doing4Jump(DelegateTask taskEntity);

    /**
     * 单任务节点选择多个人发送时，只会产生create事件，所以这里把所有候选人都生成待办
     *
     * @param taskEntity 任务实体
     */
    void unClaim(DelegateTask taskEntity);

    /**
     * 单任务节点选择多个人发送时，只会产生create事件，所以这里把所有候选人都生成待办
     *
     * @param taskEntity 任务实体
     */
    void createTodo4Claim(DelegateTask taskEntity);
}