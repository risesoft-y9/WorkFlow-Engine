package net.risesoft.service.extend.impl;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.processadmin.ProcessTodoApi;
import net.risesoft.model.itemadmin.TodoTaskModel;
import net.risesoft.service.extend.ItemTodoTaskService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ItemTodoTaskServiceImpl implements ItemTodoTaskService {

    private final ProcessTodoApi processTodoApi;

    @Override
    public int countByReceiverId(String receiverId) {
        return Math.toIntExact(processTodoApi.countByOrgUnitId(Y9LoginUserHolder.getTenantId(), receiverId).getData());
    }

    @Override
    public Boolean deleteByProcessInstanceId(String processInstanceId) {
        return true;
    }

    @Override
    public void deleteByProcessInstanceId4New(String taskId, String processInstanceId) {

    }

    @Override
    public boolean deleteTodoTask(String id) {
        return true;
    }

    @Override
    public boolean deleteTodoTaskByTaskId(String taskId) {
        return true;
    }

    @Override
    public boolean deleteTodoTaskByTaskIdAndReceiverId(String taskId, String receiverId) {
        return true;
    }

    @Override
    public boolean recoveryTodoTaskByTaskId(String id) {
        return true;
    }

    @Override
    public boolean saveTodoTask(TodoTaskModel todo) {
        return true;
    }

    @Override
    public boolean setIsNewTodo(String taskId, String newtodoStr) {
        return true;
    }

    @Override
    public void updateTitle(String processInstanceId, String documentTitle) {

    }
}
