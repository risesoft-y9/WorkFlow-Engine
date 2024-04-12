package y9.client.rest.processadmin;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.model.processadmin.TaskModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "TaskApiClient", name = "${y9.service.processAdmin.name:processAdmin}", url = "${y9.service.processAdmin.directUrl:}", path = "/${y9.service.processAdmin.name:processAdmin}/services/rest/task")
public interface TaskApiClient extends TaskApi {

    /**
     *
     * Description: 签收任务
     *
     * @param tenantId
     * @param userId
     * @param taskId
     * @throws Exception
     */
    @Override
    @PostMapping("/claim")
    void claim(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId, @RequestParam("taskId") String taskId) throws Exception;

    /**
     * 完成任务（不设置流程变量）
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @throws Exception
     */
    @Override
    @PostMapping("/complete")
    void complete(@RequestParam("tenantId") String tenantId, @RequestParam("taskId") String taskId) throws Exception;

    /**
     *
     * Description: 完成按钮的任务完结
     *
     * @param tenantId
     * @param taskId
     * @throws Exception
     */
    @Override
    @PostMapping("/completeTask")
    void completeTask(@RequestParam("tenantId") String tenantId, @RequestParam("taskId") String taskId) throws Exception;

    /**
     * 完成按钮的任务完结
     *
     * @param tenantId 租户id
     * @param positionId positionId
     * @param processInstanceId processInstanceId
     */
    @Override
    @PostMapping("/completeTaskWithoutAssignee")
    void completeTaskWithoutAssignee(@RequestParam("tenantId") String tenantId, @RequestParam("positionId") String positionId, @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 完成任务（设置流程变量）
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param map map
     * @throws Exception
     */
    @Override
    @PostMapping(value = "/completeWithVariables", consumes = MediaType.APPLICATION_JSON_VALUE)
    void completeWithVariables(@RequestParam("tenantId") String tenantId, @RequestParam("taskId") String taskId, @RequestBody Map<String, Object> map) throws Exception;

    /**
     *
     * Description: 完成任务（设置流程变量）岗位
     *
     * @param tenantId
     * @param userId
     * @param positionId
     * @param taskId
     * @param vars
     */
    @Override
    @PostMapping(value = "/completeWithVariables4Position", consumes = MediaType.APPLICATION_JSON_VALUE)
    void completeWithVariables4Position(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId, @RequestParam("positionId") String positionId, @RequestParam("taskId") String taskId, @RequestBody Map<String, Object> vars);

    /**
     * 创建变量
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param routeToTaskId 任务id
     * @param vars 变量map
     * @param userIdList 人员ids
     * @return TaskModel
     */
    @Override
    @PostMapping(value = "/createWithVariables", consumes = MediaType.APPLICATION_JSON_VALUE)
    TaskModel createWithVariables(@RequestParam("tenantId") String tenantId, @RequestParam("personId") String personId, @RequestParam("routeToTaskId") String routeToTaskId, @RequestBody Map<String, Object> vars, @RequestBody List<String> userIdList);

    /**
     * 创建变量/岗位
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param personId 人员id
     * @param routeToTaskId 任务id
     * @param vars 变量map
     * @param positionIdList 岗位ids
     * @return TaskModel
     */
    @Override
    @PostMapping(value = "/createWithVariables1", consumes = MediaType.APPLICATION_JSON_VALUE)
    TaskModel createWithVariables(@RequestParam("tenantId") String tenantId, @RequestParam("positionId") String positionId, @RequestParam("personId") String personId, @RequestParam("routeToTaskId") String routeToTaskId, @RequestBody Map<String, Object> vars,
        @RequestBody List<String> positionIdList);

    /**
     *
     * Description: 设置任务代理
     *
     * @param tenantId
     * @param taskId
     * @param assignee
     * @throws Exception
     */
    @Override
    @PostMapping("/delegateTask")
    void delegateTask(@RequestParam("tenantId") String tenantId, @RequestParam("taskId") String taskId, @RequestParam("assignee") String assignee) throws Exception;

    /**
     *
     * Description: 删除任务的候选人
     *
     * @param tenantId
     * @param taskId
     * @param assignee
     * @throws Exception
     */
    @Override
    @PostMapping("/deleteCandidateUser")
    void deleteCandidateUser(@RequestParam("tenantId") String tenantId, @RequestParam("taskId") String taskId, @RequestParam("assignee") String assignee) throws Exception;

    /**
     * 查找所有的任务实例
     *
     * @param tenantId 租户id
     * @return List&lt;TaskModel&gt;
     */
    @Override
    @GetMapping("/findAll")
    List<TaskModel> findAll(@RequestParam("tenantId") String tenantId);

    /**
     * 根据任务id查找任务
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @return TaskModel
     */
    @Override
    @GetMapping("/findById")
    TaskModel findById(@RequestParam("tenantId") String tenantId, @RequestParam("taskId") String taskId);

    /**
     * 根据流程实例Id查找任务
     *
     * @param tenantId 租户id
     * @param processInstanceId processInstanceId
     * @return List&lt;TaskModel&gt;
     */
    @Override
    @GetMapping("/findByProcessInstanceId")
    List<TaskModel> findByProcessInstanceId(@RequestParam("tenantId") String tenantId, @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 根据流程实例Id和是否激活状态查找任务
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例Id
     * @param active 是否存活
     * @return List&lt;TaskModel&gt;
     */
    @Override
    @GetMapping("/findByProcessInstanceId1")
    List<TaskModel> findByProcessInstanceId(@RequestParam("tenantId") String tenantId, @RequestParam("processInstanceId") String processInstanceId, @RequestParam("active") boolean active);

    /**
     * 根据人员Id，事项id获取用户的待办任务(分页)
     *
     * @param tenantId 租户Id
     * @param processInstanceId 流程实例Id
     * @param page page
     * @param rows rows
     * @return Map&lt;String, Object&gt;
     * @throws Exception Exception
     */
    @Override
    @GetMapping("/findListByProcessInstanceId")
    Map<String, Object> findListByProcessInstanceId(@RequestParam("tenantId") String tenantId, @RequestParam("processInstanceId") String processInstanceId, @RequestParam("page") Integer page, @RequestParam("rows") Integer rows) throws Exception;

    /**
     * 保存任务
     *
     * @param tenantId 租户id
     * @param taskModel taskModel
     */
    @Override
    @PostMapping(value = "/saveTask", consumes = MediaType.APPLICATION_JSON_VALUE)
    void saveTask(@RequestParam("tenantId") String tenantId, @RequestBody TaskModel taskModel);

    /**
     * 设置任务委托人
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param assignee assignee
     */
    @Override
    @PostMapping("/setAssignee")
    void setAssignee(@RequestParam("tenantId") String tenantId, @RequestParam("taskId") String taskId, @RequestParam("assignee") String assignee);

    /**
     * 设置任务的过期时间
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param date date
     */
    @Override
    @PostMapping("/setDueDate")
    void setDueDate(@RequestParam("tenantId") String tenantId, @RequestParam("taskId") String taskId, @RequestParam("date") Date date);

    /**
     * 设置任务的优先级
     *
     * @param tenantId
     * @param taskId
     * @param priority
     */
    @Override
    @PostMapping("/setPriority")
    void setPriority(@RequestParam("tenantId") String tenantId, @RequestParam("taskId") String taskId, @RequestParam("priority") Integer priority);

    /**
     * 撤销签收任务
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     */
    @Override
    @PostMapping("/unclaim")
    void unclaim(@RequestParam("tenantId") String tenantId, @RequestParam("taskId") String taskId);

}
