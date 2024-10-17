package net.risesoft.service.extend;

import net.risesoft.model.itemadmin.TodoTaskModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface ItemTodoTaskService {

    int countByReceiverId(String receiverId);

    Boolean deleteByProcessInstanceId(String processInstanceId);

    void deleteByProcessInstanceId4New(String taskId, String processInstanceId);

    boolean deleteTodoTask(String id);

    boolean deleteTodoTaskByTaskId(String taskId);

    boolean deleteTodoTaskByTaskIdAndReceiverId(String taskId, String receiverId);

    boolean recoveryTodoTaskByTaskId(String id);

    boolean saveTodoTask(TodoTaskModel todo);

    boolean setIsNewTodo(String taskId, String newtodoStr);

    void updateTitle(String processInstanceId, String documentTitle);
}
