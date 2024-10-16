package net.risesoft.api;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.ItemTodoTaskApi;
import net.risesoft.model.itemadmin.TodoTaskModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.ItemTodoTaskService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 统一待办接口实现
 *
 * @author zhangchongjie
 * @author mengjuhua
 *
 * @date 2022/12/28
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/itemTodoTask", produces = MediaType.APPLICATION_JSON_VALUE)
public class ItemTodoTaskApiImpl implements ItemTodoTaskApi {

    private final ItemTodoTaskService itemTodoTaskService;

    /**
     * 根据receiverId统计待办数量
     *
     * @param tenantId 租户id
     * @param receiverId 接收人id
     * @return {@code Y9Result<Integer>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Integer> countByReceiverId(@RequestParam String tenantId, @RequestParam String receiverId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        int count = itemTodoTaskService.countByReceiverId(receiverId);
        return Y9Result.success(count);
    }

    /**
     * 根据processInstanceId删除待办
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<Boolean>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Boolean> deleteByProcessInstanceId(@RequestParam String tenantId,
        @RequestParam String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Boolean result = itemTodoTaskService.deleteByProcessInstanceId(processInstanceId);
        return Y9Result.success(result);
    }

    /**
     * 根据taskId,processInstanceId删除待办
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> deleteByProcessInstanceId4New(@RequestParam String tenantId, @RequestParam String taskId,
        @RequestParam String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        itemTodoTaskService.deleteByProcessInstanceId4New(taskId, processInstanceId);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 根据id删除待办
     *
     * @param tenantId 租户id
     * @param id 主键id
     * @return {@code Y9Result<Boolean>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Boolean> deleteTodoTask(@RequestParam String tenantId, @RequestParam String id) {
        Y9LoginUserHolder.setTenantId(tenantId);
        boolean result = itemTodoTaskService.deleteTodoTask(id);
        return Y9Result.success(result);
    }

    /**
     * 根据taskId删除待办
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @return {@code Y9Result<Boolean>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Boolean> deleteTodoTaskByTaskId(@RequestParam String tenantId, @RequestParam String taskId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        boolean result = itemTodoTaskService.deleteTodoTaskByTaskId(taskId);
        return Y9Result.success(result);
    }

    /**
     * 根据taskId,receiverId删除待办
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param receiverId 接收人id
     * @return {@code Y9Result<Boolean>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Boolean> deleteTodoTaskByTaskIdAndReceiverId(@RequestParam String tenantId,
        @RequestParam String taskId, @RequestParam String receiverId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        boolean result = itemTodoTaskService.deleteTodoTaskByTaskIdAndReceiverId(taskId, receiverId);
        return Y9Result.success(result);
    }

    /**
     * 恢复待办
     *
     * @param tenantId 租户id
     * @param id 主键id
     * @return {@code Y9Result<Boolean>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Boolean> recoveryTodoTaskByTaskId(@RequestParam String tenantId, @RequestParam String id) {
        Y9LoginUserHolder.setTenantId(tenantId);
        boolean result = itemTodoTaskService.recoveryTodoTaskByTaskId(id);
        return Y9Result.success(result);
    }

    /**
     * 保存待办
     *
     * @param tenantId 租户id
     * @param todo 待办信息
     * @return {@code Y9Result<Boolean>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Boolean> saveTodoTask(@RequestParam String tenantId, @RequestBody TodoTaskModel todo) {
        Y9LoginUserHolder.setTenantId(tenantId);
        boolean result = itemTodoTaskService.saveTodoTask(todo);
        return Y9Result.success(result);
    }

    /**
     * 设置已读待办
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param newtodoStr 新待办状态
     * @return {@code Y9Result<Boolean>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Boolean> setIsNewTodo(@RequestParam String tenantId, @RequestParam String taskId,
        @RequestParam String newtodoStr) {
        Y9LoginUserHolder.setTenantId(tenantId);
        boolean result = itemTodoTaskService.setIsNewTodo(taskId, newtodoStr);
        return Y9Result.success(result);
    }

    /**
     * 根据processInstanceId更新待办标题
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param documentTitle 标题
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> updateTitle(@RequestParam String tenantId, @RequestParam String processInstanceId,
        @RequestParam String documentTitle) {
        Y9LoginUserHolder.setTenantId(tenantId);
        itemTodoTaskService.updateTitle(processInstanceId, documentTitle);
        return Y9Result.successMsg("更新成功");
    }

}
