package y9.client.rest.processadmin;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.processadmin.RuntimeApi;
import net.risesoft.model.processadmin.ExecutionModel;
import net.risesoft.model.processadmin.ProcessInstanceModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "RuntimeApiClient", name = "${y9.service.processAdmin.name:processAdmin}", url = "${y9.service.processAdmin.directUrl:}",
    path = "/${y9.service.processAdmin.name:processAdmin}/services/rest/runtime")
public interface RuntimeApiClient extends RuntimeApi {

    /**
     *
     * Description: 加签
     *
     * @param tenantId
     * @param activityId
     * @param parentExecutionId
     * @param map
     * @throws Exception
     */
    @Override
    @PostMapping(value = "/addMultiInstanceExecution", consumes = MediaType.APPLICATION_JSON_VALUE)
    void addMultiInstanceExecution(@RequestParam("tenantId") String tenantId,
        @RequestParam("activityId") String activityId, @RequestParam("parentExecutionId") String parentExecutionId,
        @RequestBody Map<String, Object> map) throws Exception;

    /**
     *
     * Description: 办结
     *
     * @param tenantId
     * @param positionId
     * @param processInstanceId
     * @param taskId
     * @throws Exception
     */
    @Override
    @PostMapping("/complete4Position")
    void complete4Position(@RequestParam("tenantId") String tenantId, @RequestParam("positionId") String positionId,
        @RequestParam("processInstanceId") String processInstanceId, @RequestParam("taskId") String taskId)
        throws Exception;

    /**
     *
     * Description: 办结
     *
     * @param tenantId
     * @param userId
     * @param processInstanceId
     * @param taskId
     * @throws Exception
     */
    @Override
    @PostMapping("/completed")
    void completed(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("processInstanceId") String processInstanceId, @RequestParam("taskId") String taskId)
        throws Exception;

    /**
     *
     * Description: 减签
     *
     * @param tenantId
     * @param executionId
     * @throws Exception
     */
    @Override
    @PostMapping("/deleteMultiInstanceExecution")
    void deleteMultiInstanceExecution(@RequestParam("tenantId") String tenantId,
        @RequestParam("executionId") String executionId) throws Exception;

    /**
     * 根据执行Id获取当前活跃的节点信息
     *
     * @param tenantId
     * @param executionId
     * @return
     */
    @Override
    @GetMapping("/getActiveActivityIds")
    List<String> getActiveActivityIds(@RequestParam("tenantId") String tenantId,
        @RequestParam("executionId") String executionId);

    /**
     *
     * Description: 根据执行实例Id查找执行实例
     *
     * @param tenantId
     * @param executionId
     * @return
     */
    @Override
    @GetMapping("/getExecutionById")
    ExecutionModel getExecutionById(@RequestParam("tenantId") String tenantId,
        @RequestParam("executionId") String executionId);

    /**
     * 根据父流程实例获取子流程实例
     *
     * @param tenantId
     * @param superProcessInstanceId
     * @return
     */
    @Override
    @GetMapping("/getListBySuperProcessInstanceId")
    List<ProcessInstanceModel> getListBySuperProcessInstanceId(@RequestParam("tenantId") String tenantId,
        @RequestParam("superProcessInstanceId") String superProcessInstanceId);

    /**
     * 根据流程实例Id获取流程实例
     *
     * @param tenantId
     * @param processInstanceId
     * @return
     */
    @Override
    @GetMapping("/getProcessInstance")
    ProcessInstanceModel getProcessInstance(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 根据流程定义id获取流程实例列表
     *
     * @param tenantId
     * @param processDefinitionId
     * @param page
     * @param rows
     * @return
     */
    @Override
    @GetMapping("/getProcessInstancesByDefId")
    Map<String, Object> getProcessInstancesByDefId(@RequestParam("tenantId") String tenantId,
        @RequestParam("processDefinitionId") String processDefinitionId, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows);

    /**
     * 根据流程定义Key获取流程实例列表
     *
     * @param tenantId
     * @param processDefinitionKey
     * @return
     */
    @Override
    @GetMapping("/getProcessInstancesByKey")
    List<ProcessInstanceModel> getProcessInstancesByKey(@RequestParam("tenantId") String tenantId,
        @RequestParam("processDefinitionKey") String processDefinitionKey);

    /**
     *
     * Description: 真办结后恢复流程实例为待办状态
     *
     * @param tenantId
     * @param userId
     * @param processInstanceId
     * @param year
     * @throws Exception
     */
    @Override
    @PostMapping("/recovery4Completed")
    void recovery4Completed(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("processInstanceId") String processInstanceId, @RequestParam("year") String year)
        throws Exception;

    /**
     *
     * Description: 恢复流程实例为待办状态，其实是先激活，再设置流程实例的结束时间为null
     *
     * @param tenantId
     * @param processInstanceId
     * @throws Exception
     */
    @Override
    @PostMapping("/recovery4SetUpCompleted")
    void recovery4SetUpCompleted(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId) throws Exception;

    /**
     *
     * Description: 设置流程实例为办结的状态，其实是先暂停，再设置流程结束时间为当前时间
     *
     * @param tenantId
     * @param processInstanceId
     * @throws Exception
     */
    @Override
    @PostMapping("/setUpCompleted")
    void setUpCompleted(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId) throws Exception;

    /**
     *
     * Description: 根据流程实例id设置流程变量
     *
     * @param tenantId
     * @param processInstanceId
     * @param key
     * @param val
     * @throws Exception
     */
    @Override
    @PostMapping(value = "/setVariable", consumes = MediaType.APPLICATION_JSON_VALUE)
    void setVariable(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId, @RequestParam("key") String key,
        @RequestBody Map<String, Object> map) throws Exception;

    /**
     *
     * Description: 根据流程实例id设置流程变量
     *
     * @param tenantId
     * @param executionId
     * @param map
     */
    @Override
    @PostMapping(value = "/setVariables", consumes = MediaType.APPLICATION_JSON_VALUE)
    void setVariables(@RequestParam("tenantId") String tenantId, @RequestParam("executionId") String executionId,
        @RequestBody Map<String, Object> map);

    /**
     * 根据流程定义Key启动流程实例，设置流程变量,并返回流程实例,流程启动人是人员Id
     *
     * @param tenantId
     * @param userId
     * @param processDefinitionKey
     * @param systemName
     * @param map
     * @return
     */
    @Override
    @PostMapping(value = "/startProcessInstanceByKey", consumes = MediaType.APPLICATION_JSON_VALUE)
    ProcessInstanceModel startProcessInstanceByKey(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("processDefinitionKey") String processDefinitionKey,
        @RequestParam("systemName") String systemName, @RequestBody Map<String, Object> map);

    /**
     * 判断是否是挂起实例
     *
     * @param tenantId
     * @param processInstanceId
     * @return
     */
    @Override
    @GetMapping("/suspendedByProcessInstanceId")
    Boolean suspendedByProcessInstanceId(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 挂起或者激活流程实例
     *
     * @param tenantId
     * @param processInstanceId
     * @param state
     */
    @Override
    @PostMapping("/switchSuspendOrActive")
    void switchSuspendOrActive(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId, @RequestParam("state") String state);

}
