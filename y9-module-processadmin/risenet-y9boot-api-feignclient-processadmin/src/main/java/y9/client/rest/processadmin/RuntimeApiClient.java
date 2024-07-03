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
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "RuntimeApiClient", name = "${y9.service.processAdmin.name:processAdmin}",
    url = "${y9.service.processAdmin.directUrl:}",
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
     */
    @Override
    @PostMapping(value = "/addMultiInstanceExecution", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Object> addMultiInstanceExecution(@RequestParam("tenantId") String tenantId,
        @RequestParam("activityId") String activityId, @RequestParam("parentExecutionId") String parentExecutionId,
        @RequestBody Map<String, Object> map);

    /**
     *
     * Description: 办结
     *
     * @param tenantId
     * @param positionId
     * @param processInstanceId
     * @param taskId @
     */
    @Override
    @PostMapping("/complete4Position")
    Y9Result<Object> complete4Position(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("processInstanceId") String processInstanceId,
        @RequestParam("taskId") String taskId);

    /**
     *
     * Description: 办结
     *
     * @param tenantId
     * @param userId
     * @param processInstanceId
     * @param taskId @
     */
    @Override
    @PostMapping("/completed")
    Y9Result<Object> completed(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("processInstanceId") String processInstanceId, @RequestParam("taskId") String taskId);

    /**
     *
     * Description: 减签
     *
     * @param tenantId
     * @param executionId
     */
    @Override
    @PostMapping("/deleteMultiInstanceExecution")
    Y9Result<Object> deleteMultiInstanceExecution(@RequestParam("tenantId") String tenantId,
        @RequestParam("executionId") String executionId);

    /**
     * 根据执行Id获取当前活跃的节点信息
     *
     * @param tenantId
     * @param executionId
     * @return Y9Result<List<String>>
     */
    @Override
    @GetMapping("/getActiveActivityIds")
    Y9Result<List<String>> getActiveActivityIds(@RequestParam("tenantId") String tenantId,
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
    Y9Result<ExecutionModel> getExecutionById(@RequestParam("tenantId") String tenantId,
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
    Y9Result<List<ProcessInstanceModel>> getListBySuperProcessInstanceId(@RequestParam("tenantId") String tenantId,
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
    Y9Result<ProcessInstanceModel> getProcessInstance(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 根据流程定义id获取流程实例列表
     *
     * @param tenantId
     * @param processDefinitionId
     * @param page
     * @param rows
     * @return Y9Page<ProcessInstanceModel>
     */
    @Override
    @GetMapping("/getProcessInstancesByDefId")
    Y9Page<ProcessInstanceModel> getProcessInstancesByDefId(@RequestParam("tenantId") String tenantId,
        @RequestParam("processDefinitionId") String processDefinitionId, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows);

    /**
     * 根据流程定义Key获取流程实例列表
     *
     * @param tenantId
     * @param processDefinitionKey
     * @return Y9Result<List<ProcessInstanceModel>>
     */
    @Override
    @GetMapping("/getProcessInstancesByKey")
    Y9Result<List<ProcessInstanceModel>> getProcessInstancesByKey(@RequestParam("tenantId") String tenantId,
        @RequestParam("processDefinitionKey") String processDefinitionKey);

    /**
     *
     * Description: 真办结后恢复流程实例为待办状态
     *
     * @param tenantId
     * @param userId
     * @param processInstanceId
     * @param year @
     */
    @Override
    @PostMapping("/recovery4Completed")
    Y9Result<Object> recovery4Completed(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("processInstanceId") String processInstanceId,
        @RequestParam("year") String year);

    /**
     *
     * Description: 恢复流程实例为待办状态，其实是先激活，再设置流程实例的结束时间为null
     *
     * @param tenantId
     * @param processInstanceId @
     */
    @Override
    @PostMapping("/recovery4SetUpCompleted")
    Y9Result<Object> recovery4SetUpCompleted(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId);

    /**
     *
     */
    @Override
    @GetMapping(value = "/runningList")
    Y9Page<Map<String, Object>> runningList(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId, @RequestParam("page") int page,
        @RequestParam("rows") int rows);

    /**
     *
     * Description: 设置流程实例为办结的状态，其实是先暂停，再设置流程结束时间为当前时间
     *
     * @param tenantId
     * @param processInstanceId @
     */
    @Override
    @PostMapping("/setUpCompleted")
    Y9Result<Object> setUpCompleted(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId);

    /**
     *
     * Description: 根据流程实例id设置流程变量
     *
     * @param tenantId
     * @param processInstanceId
     * @param key @
     */
    @Override
    @PostMapping(value = "/setVariable", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Object> setVariable(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId, @RequestParam("key") String key,
        @RequestBody Map<String, Object> map);

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
    Y9Result<Object> setVariables(@RequestParam("tenantId") String tenantId,
        @RequestParam("executionId") String executionId, @RequestBody Map<String, Object> map);

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
    Y9Result<ProcessInstanceModel> startProcessInstanceByKey(@RequestParam("tenantId") String tenantId,
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
    Y9Result<Boolean> suspendedByProcessInstanceId(@RequestParam("tenantId") String tenantId,
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
    Y9Result<Object> switchSuspendOrActive(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId, @RequestParam("state") String state);

}
