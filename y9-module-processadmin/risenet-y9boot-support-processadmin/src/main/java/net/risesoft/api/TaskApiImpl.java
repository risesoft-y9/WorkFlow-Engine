package net.risesoft.api;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.flowable.task.api.DelegationState;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.model.platform.Position;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.service.CustomTaskService;
import net.risesoft.service.FlowableTenantInfoHolder;
import net.risesoft.util.FlowableModelConvertUtil;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 正在运行任务相关接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@RestController
@RequestMapping(value = "/services/rest/task")
public class TaskApiImpl implements TaskApi {

    @Autowired
    private CustomTaskService customTaskService;

    @Autowired
    private PersonApi personManager;

    @Autowired
    private PositionApi positionManager;

    /**
     * 签收任务
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param taskId 任务id
     * @throws Exception Exception
     */
    @Override
    @PostMapping(value = "/claim", produces = MediaType.APPLICATION_JSON_VALUE)
    public void claim(@RequestParam String tenantId, @RequestParam String userId, @RequestParam String taskId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        try {
            customTaskService.claim(taskId, userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 完成任务（不设置流程变量）
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @throws Exception Exception
     */
    @Override
    @PostMapping(value = "/complete", produces = MediaType.APPLICATION_JSON_VALUE)
    public void complete(@RequestParam String tenantId, @RequestParam String taskId) throws Exception {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        customTaskService.complete(taskId);
    }

    /**
     * 完成按钮的任务完结
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @throws Exception Exception
     */
    @Override
    @PostMapping(value = "/completeTask", produces = MediaType.APPLICATION_JSON_VALUE)
    public void completeTask(@RequestParam String tenantId, @RequestParam String taskId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Task task = customTaskService.findById(taskId);
        if (DelegationState.PENDING == task.getDelegationState()) {
            String taskOwner = task.getOwner();
            customTaskService.resolveTask(taskId);
            customTaskService.setAssignee(taskId, taskOwner);
        }
    }

    /**
     * 完成按钮的任务完结/岗位
     *
     * @param tenantId 租户id
     * @param positionId 岗位Id
     * @param processInstanceId 流程实例id
     */
    @Override
    @PostMapping(value = "/completeTaskWithoutAssignee", produces = MediaType.APPLICATION_JSON_VALUE)
    public void completeTaskWithoutAssignee(@RequestParam String tenantId, @RequestParam String positionId, @RequestParam String processInstanceId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionManager.get(tenantId, positionId).getData();
        Y9LoginUserHolder.setPositionId(position.getId());
        customTaskService.completeTaskWithoutAssignee(processInstanceId);
    }

    /**
     * 完成任务（设置流程变量）
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param map 变量map
     * @throws Exception Exception
     */
    @Override
    @PostMapping(value = "/completeWithVariables", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void completeWithVariables(@RequestParam String tenantId, @RequestParam String taskId, @RequestBody Map<String, Object> map) throws Exception {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        customTaskService.completeWithVariables(taskId, map);
    }

    /**
     * 完成任务（设置流程变量）岗位
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @param vars 变量map
     */
    @Override
    @PostMapping(value = "/completeWithVariables4Position", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void completeWithVariables4Position(@RequestParam String tenantId, @RequestParam String userId, @RequestParam String positionId, @RequestParam String taskId, @RequestBody Map<String, Object> vars) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionManager.get(tenantId, positionId).getData();
        Y9LoginUserHolder.setPositionId(position.getId());
        Y9LoginUserHolder.setPerson(personManager.get(tenantId, userId).getData());
        customTaskService.completeWithVariables(taskId, vars);

    }

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
    @PostMapping(value = "/createWithVariables", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public TaskModel createWithVariables(@RequestParam String tenantId, @RequestParam String personId, @RequestParam String routeToTaskId, @RequestBody Map<String, Object> vars, @RequestBody List<String> positionIdList) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPerson(personManager.get(tenantId, personId).getData());
        return customTaskService.createWithVariables(vars, routeToTaskId, positionIdList);
    }

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
    @PostMapping(value = "/createWithVariables1", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public TaskModel createWithVariables(@RequestParam String tenantId, @RequestParam String positionId, @RequestParam String personId, @RequestParam String routeToTaskId, @RequestBody Map<String, Object> vars, @RequestBody List<String> positionIdList) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionManager.get(tenantId, positionId).getData();
        Y9LoginUserHolder.setPositionId(position.getId());
        Y9LoginUserHolder.setPerson(personManager.get(tenantId, personId).getData());
        return customTaskService.createWithVariables(positionId, vars, routeToTaskId, positionIdList);
    }

    /**
     * 设置任务代理
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param assignee 受让人
     * @throws Exception Exception
     */
    @Override
    @PostMapping(value = "/delegateTask", produces = MediaType.APPLICATION_JSON_VALUE)
    public void delegateTask(@RequestParam String tenantId, @RequestParam String taskId, @RequestParam String assignee) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        try {
            customTaskService.delegateTask(taskId, assignee);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除任务的候选人
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param assignee 受让人
     * @throws Exception Exception
     */
    @Override
    @PostMapping(value = "/deleteCandidateUser", produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteCandidateUser(@RequestParam String tenantId, @RequestParam String taskId, @RequestParam String assignee) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        try {
            customTaskService.deleteCandidateUser(taskId, URLDecoder.decode(assignee, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查找所有的任务实例
     *
     * @param tenantId 租户id
     * @return List<TaskModel>
     */
    @Override
    @GetMapping(value = "/findAll", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TaskModel> findAll(@RequestParam String tenantId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        List<Task> taskList = customTaskService.findAll();
        List<TaskModel> taskModelList = FlowableModelConvertUtil.taskList2TaskModelList(taskList);
        return taskModelList;
    }

    /**
     * 根据任务id查找任务
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @return TaskModel
     */
    @Override
    @GetMapping(value = "/findById", produces = MediaType.APPLICATION_JSON_VALUE)
    public TaskModel findById(@RequestParam String tenantId, @RequestParam String taskId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Task task = customTaskService.findById(taskId);
        TaskModel taskModel = FlowableModelConvertUtil.task2TaskModel(task);
        return taskModel;
    }

    /**
     * 根据流程实例Id查找任务
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return List<TaskModel>
     */
    @Override
    @GetMapping(value = "/findByProcessInstanceId", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TaskModel> findByProcessInstanceId(@RequestParam String tenantId, @RequestParam String processInstanceId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        List<Task> taskList = customTaskService.findByProcessInstanceId(processInstanceId);
        List<TaskModel> taskModelList = FlowableModelConvertUtil.taskList2TaskModelList(taskList);
        return taskModelList;
    }

    /**
     * 根据流程实例Id和是否激活状态查找任务
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例Id
     * @param active 是否存活
     * @return List<TaskModel>
     */
    @Override
    @GetMapping(value = "/findByProcessInstanceId1", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TaskModel> findByProcessInstanceId(@RequestParam String tenantId, @RequestParam String processInstanceId, @RequestParam boolean active) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        List<Task> taskList = customTaskService.findByProcessInstanceId(processInstanceId, active);
        List<TaskModel> taskModelList = FlowableModelConvertUtil.taskList2TaskModelList(taskList);
        return taskModelList;
    }

    /**
     * 根据人员Id，流程实例id获取用户的待办任务(分页)
     *
     * @param tenantId 租户Id
     * @param processInstanceId 流程实例Id
     * @param page 页码
     * @param rows 行数
     * @return Map<String, Object>
     * @throws Exception Exception
     */
    @Override
    @GetMapping(value = "/findListByProcessInstanceId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> findListByProcessInstanceId(@RequestParam String tenantId, @RequestParam String processInstanceId, @RequestParam Integer page, @RequestParam Integer rows) throws Exception {
        if (StringUtils.isEmpty(tenantId) || StringUtils.isEmpty(processInstanceId)) {
            throw new Exception("tenantId or processInstanceId is null !");
        }
        FlowableTenantInfoHolder.setTenantId(tenantId);
        return customTaskService.findListByProcessInstanceId(processInstanceId, page, rows);
    }

    /**
     * 保存任务
     *
     * @param tenantId 租户id
     * @param taskModel 任务实体
     */
    @Override
    @PostMapping(value = "/saveTask", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void saveTask(@RequestParam String tenantId, @RequestBody TaskModel taskModel) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Task task = customTaskService.findById(taskModel.getId());
        task = FlowableModelConvertUtil.taskModel2Task(taskModel, task);
        customTaskService.saveTask(task);
    }

    /**
     * 设置任务委托人
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param assignee 受让人
     */
    @Override
    @PostMapping(value = "/setAssignee", produces = MediaType.APPLICATION_JSON_VALUE)
    public void setAssignee(@RequestParam String tenantId, @RequestParam String taskId, @RequestParam String assignee) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        try {
            customTaskService.setAssignee(taskId, URLDecoder.decode(assignee, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置任务的过期时间
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param date 日期
     */
    @Override
    @PostMapping(value = "/setDueDate", produces = MediaType.APPLICATION_JSON_VALUE)
    public void setDueDate(@RequestParam String tenantId, @RequestParam String taskId, @RequestParam Date date) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        customTaskService.setDueDate(taskId, date);
    }

    /**
     * 设置任务的优先级
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param priority 优先级
     */
    @Override
    @PostMapping(value = "/setPriority", produces = MediaType.APPLICATION_JSON_VALUE)
    public void setPriority(@RequestParam String tenantId, @RequestParam String taskId, @RequestParam Integer priority) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        customTaskService.setPriority(taskId, priority);
    }

    /**
     * 撤销签收任务
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     */
    @Override
    @PostMapping(value = "/unclaim", produces = MediaType.APPLICATION_JSON_VALUE)
    public void unclaim(@RequestParam String tenantId, @RequestParam String taskId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        customTaskService.unclaim(taskId);
    }
}
