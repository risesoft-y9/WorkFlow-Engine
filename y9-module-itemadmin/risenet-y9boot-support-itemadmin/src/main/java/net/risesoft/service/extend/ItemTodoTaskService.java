package net.risesoft.service.extend;

import net.risesoft.model.itemadmin.TodoTaskModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface ItemTodoTaskService {

    /**
     * 根据接收人ID统计待办数量
     *
     * @param receiverId 接收人ID
     * @return int
     */
    int countByReceiverId(String receiverId);

    /**
     * 根据流程实例ID删除待办任务
     *
     * @param processInstanceId 流程实例ID
     * @return boolean
     */
    Boolean deleteByProcessInstanceId(String processInstanceId);

    /**
     * 根据流程实例ID删除待办任务
     *
     * @param taskId 任务ID
     * @param processInstanceId 流程实例ID
     */
    void deleteByProcessInstanceId4New(String taskId, String processInstanceId);

    /**
     * 删除待办任务
     *
     * @param id 待办ID
     * @return boolean
     */
    boolean deleteTodoTask(String id);

    /**
     * 根据任务ID删除待办任务
     *
     * @param taskId 任务ID
     * @return boolean
     */
    boolean deleteTodoTaskByTaskId(String taskId);

    /**
     * 根据任务ID和接收人ID删除待办任务
     * 
     * @param taskId 任务ID
     * @param receiverId 接收人ID
     * @return
     */
    boolean deleteTodoTaskByTaskIdAndReceiverId(String taskId, String receiverId);

    /**
     * 根据任务ID恢复待办任务
     *
     * @param id 任务ID
     * @return boolean
     */
    boolean recoveryTodoTaskByTaskId(String id);

    /**
     * 保存待办任务
     *
     * @param todo 待办任务
     * @return boolean
     */
    boolean saveTodoTask(TodoTaskModel todo);

    /**
     * 设置是否新待办
     *
     * @param taskId 任务ID
     * @param newtodoStr 是否新待办,1为新件，0为已阅件
     * @return boolean
     */
    boolean setIsNewTodo(String taskId, String newtodoStr);

    /**
     * 更新标题
     *
     * @param processInstanceId 流程实例ID
     * @param documentTitle 标题
     */
    void updateTitle(String processInstanceId, String documentTitle);
}
