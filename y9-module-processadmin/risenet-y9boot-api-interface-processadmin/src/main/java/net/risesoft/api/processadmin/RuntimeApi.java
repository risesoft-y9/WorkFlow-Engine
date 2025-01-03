package net.risesoft.api.processadmin;

import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.processadmin.ExecutionModel;
import net.risesoft.model.processadmin.ProcessInstanceModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;

/**
 * 正在运行流程实例操作接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface RuntimeApi {

    /**
     * 加签
     *
     * @param tenantId 租户id
     * @param activityId 活动节点id
     * @param parentExecutionId 父执行实例id
     * @param map 参数
     * @return {@code Y9Result<ExecutionModel>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @PostMapping(value = "/addMultiInstanceExecution", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Object> addMultiInstanceExecution(@RequestParam("tenantId") String tenantId,
        @RequestParam("activityId") String activityId, @RequestParam("parentExecutionId") String parentExecutionId,
        @RequestBody Map<String, Object> map);

    /**
     * 真办结
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param processInstanceId 流程实例id
     * @param taskId 任务id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @PostMapping("/complete")
    Y9Result<Object> complete(@RequestParam("tenantId") String tenantId, @RequestParam("orgUnitId") String orgUnitId,
        @RequestParam("processInstanceId") String processInstanceId, @RequestParam("taskId") String taskId)
        throws Exception;

    /**
     * 真办结
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param taskId 任务id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @PostMapping("/completeSub")
    Y9Result<Object> completeSub(@RequestParam("tenantId") String tenantId, @RequestParam("orgUnitId") String orgUnitId,
        @RequestParam("taskId") String taskId, @RequestParam("userList") List<String> userList) throws Exception;

    /**
     * 减签
     *
     * @param tenantId 租户id
     * @param executionId 执行实例id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @PostMapping("/deleteMultiInstanceExecution")
    Y9Result<Object> deleteMultiInstanceExecution(@RequestParam("tenantId") String tenantId,
        @RequestParam("executionId") String executionId);

    /**
     * 根据执行Id获取当前活跃的节点信息
     *
     * @param tenantId 租户id
     * @param executionId 执行实例id
     * @return {@code Y9Result<List<String>>} 通用请求返回对象 - data 是当前活跃的节点信息
     * @since 9.6.6
     */
    @GetMapping("/getActiveActivityIds")
    Y9Result<List<String>> getActiveActivityIds(@RequestParam("tenantId") String tenantId,
        @RequestParam("executionId") String executionId);

    /**
     * 根据执行实例Id查找执行实例
     *
     * @param tenantId 租户id
     * @param executionId 执行实例id
     * @return {@code Y9Result<ExecutionModel>} 通用请求返回对象 - data 是执行实例
     * @since 9.6.6
     */
    @GetMapping("/getExecutionById")
    Y9Result<ExecutionModel> getExecutionById(@RequestParam("tenantId") String tenantId,
        @RequestParam("executionId") String executionId);

    /**
     * 根据父流程实例获取子流程实例
     *
     * @param tenantId 租户id
     * @param superProcessInstanceId 父流程实例id
     * @return {@code Y9Result<List<ProcessInstanceModel>>} 通用请求返回对象 - data 是子流程实例列表
     * @since 9.6.6
     */
    @GetMapping("/getListBySuperProcessInstanceId")
    Y9Result<List<ProcessInstanceModel>> getListBySuperProcessInstanceId(@RequestParam("tenantId") String tenantId,
        @RequestParam("superProcessInstanceId") String superProcessInstanceId);

    /**
     * 根据流程实例Id获取流程实例
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<ProcessInstanceModel>} 通用请求返回对象 - data 是流程实例
     * @since 9.6.6
     */
    @GetMapping("/getProcessInstance")
    Y9Result<ProcessInstanceModel> getProcessInstance(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 根据流程定义id获取流程实例列表
     *
     * @param tenantId 租户id
     * @param processDefinitionId 流程定义id
     * @param page 页码
     * @param rows 行数
     * @return {@code Y9Page<ProcessInstanceModel>} 通用分页请求返回对象 - rows 是流程实例
     * @since 9.6.6
     */
    @GetMapping("/getProcessInstancesByDefId")
    Y9Page<ProcessInstanceModel> getProcessInstancesByDefId(@RequestParam("tenantId") String tenantId,
        @RequestParam("processDefinitionId") String processDefinitionId, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows);

    /**
     * 根据流程定义Key获取流程实例列表
     *
     * @param tenantId 租户id
     * @param processDefinitionKey 流程定义key
     * @return {@code Y9Result<List<ProcessInstanceModel>>} 通用请求返回对象 - data 是流程实例
     * @since 9.6.6
     */
    @GetMapping("/getProcessInstancesByKey")
    Y9Result<List<ProcessInstanceModel>> getProcessInstancesByKey(@RequestParam("tenantId") String tenantId,
        @RequestParam("processDefinitionKey") String processDefinitionKey);

    /**
     * 真办结后恢复流程实例为待办状态
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param processInstanceId 流程实例id
     * @param year 年份
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @PostMapping("/recovery4Completed")
    Y9Result<Object> recovery4Completed(@RequestParam("tenantId") String tenantId,
        @RequestParam("orgUnitId") String orgUnitId, @RequestParam("processInstanceId") String processInstanceId,
        @RequestParam(value = "year", required = false) String year) throws Exception;

    /**
     * 恢复流程实例为待办状态，其实是先激活，再设置流程实例的结束时间为null
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @PostMapping("/recovery4SetUpCompleted")
    Y9Result<Object> recovery4SetUpCompleted(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 获取正在运行流程实例列表
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param page 页吗
     * @param rows 条数
     * @return {@code Y9Page<Map<String, Object>>} 通用分页请求返回对象 - rows 是流程实例
     * @since 9.6.6
     */
    @GetMapping(value = "/runningList")
    Y9Page<ProcessInstanceModel> runningList(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId, @RequestParam("page") int page,
        @RequestParam("rows") int rows);

    /**
     * 设置流程实例为办结的状态，其实是先暂停，再设置流程结束时间为当前时间
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @PostMapping("/setUpCompleted")
    Y9Result<Object> setUpCompleted(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 根据流程实例id设置流程变量
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param key 变量key
     * @param map 变量map
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @PostMapping(value = "/setVariable", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Object> setVariable(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId, @RequestParam("key") String key,
        @RequestBody Map<String, Object> map);

    /**
     * 根据流程实例id设置流程变量
     *
     * @param tenantId 租户id
     * @param executionId 执行实例id
     * @param map 变量map
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @PostMapping(value = "/setVariables", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Object> setVariables(@RequestParam("tenantId") String tenantId,
        @RequestParam("executionId") String executionId, @RequestBody Map<String, Object> map);

    /**
     * 根据流程定义Key启动流程实例，设置流程变量,并返回流程实例,流程启动人是人员Id
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param processDefinitionKey 流程定义key
     * @param systemName 系统名称
     * @param map 变量map
     * @return {@code Y9Result<ProcessInstanceModel>} 通用请求返回对象 - data 是流程实例
     * @since 9.6.6
     */
    @PostMapping(value = "/startProcessInstanceByKey", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<ProcessInstanceModel> startProcessInstanceByKey(@RequestParam("tenantId") String tenantId,
        @RequestParam("orgUnitId") String orgUnitId, @RequestParam("processDefinitionKey") String processDefinitionKey,
        @RequestParam("systemName") String systemName, @RequestBody Map<String, Object> map);

    /**
     * 判断是否是挂起实例
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<Boolean>} 通用请求返回对象 - data 属性判断流程是否挂起
     * @since 9.6.6
     */
    @GetMapping("/suspendedByProcessInstanceId")
    Y9Result<Boolean> suspendedByProcessInstanceId(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 挂起或者激活流程实例
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param state 状态
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @PostMapping("/switchSuspendOrActive")
    Y9Result<Object> switchSuspendOrActive(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId, @RequestParam("state") String state);

}
