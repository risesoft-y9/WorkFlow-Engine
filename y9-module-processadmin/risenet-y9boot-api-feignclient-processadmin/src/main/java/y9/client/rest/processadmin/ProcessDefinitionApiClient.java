package y9.client.rest.processadmin;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.model.processadmin.TargetModel;
import net.risesoft.pojo.Y9Result;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "ProcessDefinitionApiClient", name = "${y9.service.processAdmin.name:processAdmin}",
    url = "${y9.service.processAdmin.directUrl:}",
    path = "/${y9.service.processAdmin.name:processAdmin}/services/rest/processDefinition")
public interface ProcessDefinitionApiClient extends ProcessDefinitionApi {

    /**
     * 获取有办结权限的UserTask
     *
     * @param tenantId 租户id
     * @param processDefinitionId 流程定义id
     * @return
     */
    @Override
    @GetMapping("/getContainEndEvent4UserTask")
    List<Map<String, String>> getContainEndEvent4UserTask(@RequestParam("tenantId") String tenantId,
        @RequestParam("processDefinitionId") String processDefinitionId);

    /**
     * 获取某一任务所在节点的目标是结束节点的目标节点Key
     *
     * @param tenantId 租户Id
     * @param taskId 任务id
     * @return String
     */
    @Override
    @GetMapping("/getEndNodeKeyByTaskId")
    String getEndNodeKeyByTaskId(@RequestParam("tenantId") String tenantId, @RequestParam("taskId") String taskId);

    @Override
    @GetMapping("/getFlowElement")
    List<Map<String, Object>> getFlowElement(@RequestParam("tenantId") String tenantId,
        @RequestParam("processDefinitionId") String processDefinitionId,
        @RequestParam("isContainStartNode") Boolean isContainStartNode);

    /**
     * 根据流程定义Id获取节点信息 isContainStartNode为true时，不包含开始节点
     *
     * @param tenantId 租户Id
     * @param processDefinitionId processDefinitionId
     * @param isContainStartNode isContainStartNode
     * @return List&lt;Map&lt;String, Object&gt;&gt;
     */
    @Override
    @GetMapping("/getNodes")
    List<Map<String, Object>> getNodes(@RequestParam("tenantId") String tenantId,
        @RequestParam("processDefinitionId") String processDefinitionId,
        @RequestParam("isContainStartNode") Boolean isContainStartNode);

    /**
     * 获取具体流程的某个节点类型
     *
     * @param tenantId 租户Id
     * @param processDefinitionId processDefinitionId
     * @param taskDefKey taskDefKey
     * @return String
     */
    @Override
    @GetMapping("/getNodeType")
    String getNodeType(@RequestParam("tenantId") String tenantId,
        @RequestParam("processDefinitionId") String processDefinitionId, @RequestParam("taskDefKey") String taskDefKey);

    /**
     * 根据taskId获取某个节点除去end节点和默认路由节点的所有的输出线路的个数
     *
     * @param tenantId 租户Id
     * @param taskId 任务id
     * @return Integer
     */
    @Override
    @GetMapping("/getOutPutNodeCount")
    Integer getOutPutNodeCount(@RequestParam("tenantId") String tenantId, @RequestParam("taskId") String taskId);

    /**
     * 根据流程定义Id和流程节点Key获取目标任务节点集合
     *
     * @param tenantId 租户Id
     * @param processDefinitionId processDefinitionId
     * @param taskDefKey taskDefKey
     * @return List&lt;Map&lt;String, Object&gt;&gt;
     */
    @Override
    @GetMapping("/getParallelGatewayList")
    List<Map<String, String>> getParallelGatewayList(@RequestParam("tenantId") String tenantId,
        @RequestParam("processDefinitionId") String processDefinitionId, @RequestParam("taskDefKey") String taskDefKey);

    /**
     * 根据流程定义id获取开始节点
     *
     * @param tenantId 租户Id
     * @param processDefinitionId 流程定义id
     * @return String
     */
    @Override
    @GetMapping("/getStartNodeKeyByProcessDefinitionId")
    String getStartNodeKeyByProcessDefinitionId(@RequestParam("tenantId") String tenantId,
        @RequestParam("processDefinitionId") String processDefinitionId);

    /**
     * 根据流程定义key获取最新版本的流程定义的启动节点的taskdefineKey
     *
     * @param tenantId 租户Id
     * @param processDefinitionKey processDefinitionKey
     * @return String
     */
    @Override
    @GetMapping("/getStartNodeKeyByProcessDefinitionKey")
    String getStartNodeKeyByProcessDefinitionKey(@RequestParam("tenantId") String tenantId,
        @RequestParam("processDefinitionKey") String processDefinitionKey);

    /**
     * 根据流程定义Id和流程节点Key获取目标任务节点集合
     *
     * @param tenantId 租户Id
     * @param processDefinitionId processDefinitionId
     * @param taskDefKey taskDefKey
     * @return Y9Result<List<TargetModel>>
     */
    @Override
    @GetMapping("/getTargetNodes")
    Y9Result<List<TargetModel>> getTargetNodes(@RequestParam("tenantId") String tenantId,
        @RequestParam("processDefinitionId") String processDefinitionId, @RequestParam("taskDefKey") String taskDefKey);

    /**
     * 根据流程定义Id和流程节点Key获取目标任务节点集合,去除名称相等的节点，并且加上结束节点
     *
     * @param tenantId 租户Id
     * @param processDefinitionId processDefinitionId
     * @param taskDefKey taskDefKey
     * @return List&lt;Map&lt;String, Object&gt;&gt;
     */
    @Override
    @GetMapping("/getTargetNodes1")
    List<Map<String, String>> getTargetNodes1(@RequestParam("tenantId") String tenantId,
        @RequestParam("processDefinitionId") String processDefinitionId, @RequestParam("taskDefKey") String taskDefKey);

    /**
     * 根据流程定义Id和流程节点Key获取目标任务节点集合
     *
     * @param tenantId 租户Id
     * @param processDefinitionId processDefinitionId
     * @param taskDefKey taskDefKey
     * @return List&lt;Map&lt;String, Object&gt;&gt;
     */
    @Override
    @GetMapping("/getTargetNodes4ParallelGateway")
    List<Map<String, String>> getTargetNodes4ParallelGateway(@RequestParam("tenantId") String tenantId,
        @RequestParam("processDefinitionId") String processDefinitionId, @RequestParam("taskDefKey") String taskDefKey);

    /**
     * 根据流程定义Id和流程节点Key获取目标任务节点集合
     *
     * @param tenantId 租户id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务key
     * @param isContainEndNode 是否包含结束节点
     * @return
     */
    @Override
    @GetMapping("/getTargetNodes4UserTask")
    Y9Result<List<TargetModel>> getTargetNodes4UserTask(@RequestParam("tenantId") String tenantId,
        @RequestParam("processDefinitionId") String processDefinitionId, @RequestParam("taskDefKey") String taskDefKey,
        @RequestParam("isContainEndNode") Boolean isContainEndNode);

    /**
     * 判断流程定义的节点是否是callActivity节点
     *
     * @param tenantId 租户Id
     * @param processDefinitionId processDefinitionId
     * @param taskDefKey taskDefKey
     * @return Y9Result<Boolean>
     */
    @Override
    @GetMapping("/isCallActivity")
    Y9Result<Boolean> isCallActivity(@RequestParam("tenantId") String tenantId,
        @RequestParam("processDefinitionId") String processDefinitionId, @RequestParam("taskDefKey") String taskDefKey);

    /**
     * 查找当前任务节点的输出目标节点中是否包含某一类型的特定节点
     *
     * @param tenantId 租户Id
     * @param taskId 任务id
     * @param nodeType nodeType
     * @return Y9Result<Boolean>
     */
    @Override
    @GetMapping("/isContainNodeType")
    Y9Result<Boolean> isContainNodeType(@RequestParam("tenantId") String tenantId,
        @RequestParam("taskId") String taskId, @RequestParam("nodeType") String nodeType);
}
