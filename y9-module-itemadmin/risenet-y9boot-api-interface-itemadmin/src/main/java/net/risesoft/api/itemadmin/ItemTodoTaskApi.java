package net.risesoft.api.itemadmin;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.TodoTaskModel;
import net.risesoft.pojo.Y9Result;

/**
 * 统一待办
 *
 * @author zhangchongjie
 * @author mengjuhua
 *
 * @date 2022/12/28
 */
public interface ItemTodoTaskApi {

    /**
     * 根据receiverId统计待办数量
     *
     * @param tenantId 租户id
     * @param receiverId 接收人id
     * @return {@code Y9Result<Integer>} 通用请求返回对象
     * @since 9.6.6
     */
    @GetMapping("/countByReceiverId")
    Y9Result<Integer> countByReceiverId(@RequestParam("tenantId") String tenantId,
        @RequestParam("receiverId") String receiverId);

    /**
     * 根据processInstanceId删除待办
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<Boolean>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/deleteByProcessInstanceId")
    Y9Result<Boolean> deleteByProcessInstanceId(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 根据taskId,processInstanceId删除待办
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/deleteByProcessInstanceId4New")
    Y9Result<Object> deleteByProcessInstanceId4New(@RequestParam("tenantId") String tenantId,
        @RequestParam("taskId") String taskId, @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 根据id删除待办
     *
     * @param tenantId 租户id
     * @param id 主键id
     * @return {@code Y9Result<Boolean>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/deleteTodoTask")
    Y9Result<Boolean> deleteTodoTask(@RequestParam("tenantId") String tenantId, @RequestParam("id") String id);

    /**
     * 根据taskId删除待办
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @return {@code Y9Result<Boolean>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/deleteTodoTaskByTaskId")
    Y9Result<Boolean> deleteTodoTaskByTaskId(@RequestParam("tenantId") String tenantId,
        @RequestParam("taskId") String taskId);

    /**
     * 根据taskId,receiverId删除待办
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param receiverId 接收人id
     * @return {@code Y9Result<Boolean>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/deleteTodoTaskByTaskIdAndReceiverId")
    Y9Result<Boolean> deleteTodoTaskByTaskIdAndReceiverId(@RequestParam("tenantId") String tenantId,
        @RequestParam("taskId") String taskId, @RequestParam("receiverId") String receiverId);

    /**
     * 恢复待办
     *
     * @param tenantId 租户id
     * @param id 主键id
     * @return {@code Y9Result<Boolean>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/recoveryTodoTaskByTaskId")
    Y9Result<Boolean> recoveryTodoTaskByTaskId(@RequestParam("tenantId") String tenantId,
        @RequestParam("id") String id);

    /**
     * 保存待办
     *
     * @param tenantId 租户id
     * @param todo 待办信息
     * @return {@code Y9Result<Boolean>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping(value = "/saveTodoTask", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Boolean> saveTodoTask(@RequestParam("tenantId") String tenantId, @RequestBody TodoTaskModel todo);

    /**
     * 设置已读待办
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param newtodoStr 新待办状态
     * @return {@code Y9Result<Boolean>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/setIsNewTodo")
    Y9Result<Boolean> setIsNewTodo(@RequestParam("tenantId") String tenantId, @RequestParam("taskId") String taskId,
        @RequestParam("newtodoStr") String newtodoStr);

    /**
     * 根据processInstanceId更新待办标题
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param documentTitle 标题
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/updateTitleByProcessInstanceId")
    Y9Result<Object> updateTitle(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId,
        @RequestParam("documentTitle") String documentTitle);

}
