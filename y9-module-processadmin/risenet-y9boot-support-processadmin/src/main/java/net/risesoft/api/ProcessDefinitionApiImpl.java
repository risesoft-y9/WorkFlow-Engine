package net.risesoft.api;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.service.CustomProcessDefinitionService;
import net.risesoft.service.FlowableTenantInfoHolder;

/**
 * 流程定义相关接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@RestController
@RequestMapping(value = "/services/rest/processDefinition")
public class ProcessDefinitionApiImpl implements ProcessDefinitionApi {

    @Autowired
    private CustomProcessDefinitionService customProcessDefinitionService;

    /**
     * 获取有办结权限的UserTask
     *
     * @param tenantId 租户Id
     * @param processDefinitionId 流程定义id
     * @return List<Map<String, String>>
     */
    @Override
    @GetMapping(value = "/getContainEndEvent4UserTask", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, String>> getContainEndEvent4UserTask(@RequestParam String tenantId, @RequestParam String processDefinitionId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        List<Map<String, String>> listMap = customProcessDefinitionService.getContainEndEvent4UserTask(processDefinitionId);
        return listMap;
    }

    /**
     * 获取某一任务所在节点的目标是结束节点的目标节点Key
     *
     * @param tenantId 租户Id
     * @param taskId 任务id
     * @return String
     */
    @Override
    @GetMapping(value = "/getEndNodeKeyByTaskId", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getEndNodeKeyByTaskId(@RequestParam String tenantId, @RequestParam String taskId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        String endNodeKey = customProcessDefinitionService.getEndNodeKeyByTaskId(taskId);
        return endNodeKey;
    }

    /**
     * 根据流程定义Id获取节点信息 isContainStartNode为true时，不包含开始节点
     *
     * @param tenantId 租户Id
     * @param processDefinitionId 流程定义id
     * @param isContainStartNode 是否包含开始节点
     * @return List<Map<String, Object>>
     */
    @Override
    @GetMapping(value = "/getNodes", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> getNodes(@RequestParam String tenantId, @RequestParam String processDefinitionId, @RequestParam Boolean isContainStartNode) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        List<Map<String, Object>> listMap = customProcessDefinitionService.getNodes(processDefinitionId, isContainStartNode);
        return listMap;
    }

    /**
     * 获取具体流程的某个节点类型
     *
     * @param tenantId 租户Id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务key
     * @return String
     */
    @Override
    @GetMapping(value = "/getNodeType", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getNodeType(@RequestParam String tenantId, @RequestParam String processDefinitionId, @RequestParam String taskDefKey) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        String nodeType = customProcessDefinitionService.getNodeType(processDefinitionId, taskDefKey);
        return nodeType;
    }

    /**
     * 根据taskId获取某个节点除去end节点和默认路由节点的所有的输出线路的个数
     *
     * @param tenantId 租户Id
     * @param taskId 任务id
     * @return Integer
     */
    @Override
    @GetMapping(value = "/getOutPutNodeCount", produces = MediaType.APPLICATION_JSON_VALUE)
    public Integer getOutPutNodeCount(@RequestParam String tenantId, @RequestParam String taskId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Integer outPutNodeCount = customProcessDefinitionService.getOutPutNodeCount(taskId);
        return outPutNodeCount;
    }

    /**
     * 根据流程定义Id和流程节点Key获取目标任务节点集合
     *
     * @param tenantId 租户Id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务key
     * @return List<Map<String, String>>
     */
    @Override
    @GetMapping(value = "/getParallelGatewayList", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, String>> getParallelGatewayList(@RequestParam String tenantId, @RequestParam String processDefinitionId, @RequestParam String taskDefKey) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        List<Map<String, String>> listMap = customProcessDefinitionService.getParallelGatewayList(processDefinitionId, taskDefKey);
        return listMap;
    }

    /**
     * 根据流程定义id获取开始节点
     *
     * @param tenantId 租户Id
     * @param processDefinitionId 流程定义id
     * @return String
     */
    @Override
    @GetMapping(value = "/getStartNodeKeyByProcessDefinitionId", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getStartNodeKeyByProcessDefinitionId(@RequestParam String tenantId, @RequestParam String processDefinitionId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        String startTaskDefineKey = customProcessDefinitionService.getStartNodeKeyByProcessDefinitionId(processDefinitionId);
        return startTaskDefineKey;
    }

    /**
     * 根据流程定义key获取最新版本的流程定义的启动节点的taskdefineKey
     *
     * @param tenantId 租户Id
     * @param processDefinitionKey 流程定义Key
     * @return String
     */
    @Override
    @GetMapping(value = "/getStartNodeKeyByProcessDefinitionKey", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getStartNodeKeyByProcessDefinitionKey(@RequestParam String tenantId, @RequestParam String processDefinitionKey) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        String startTaskDefineKey = customProcessDefinitionService.getStartNodeKeyByProcessDefinitionKey(processDefinitionKey);
        return startTaskDefineKey;
    }

    /**
     * 根据流程定义Id和流程节点Key获取目标任务节点集合
     *
     * @param tenantId 租户Id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务key
     * @return List<Map<String, String>>
     */
    @Override
    @GetMapping(value = "/getTargetNodes", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, String>> getTargetNodes(@RequestParam String tenantId, @RequestParam String processDefinitionId, @RequestParam String taskDefKey) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        List<Map<String, String>> listMap = customProcessDefinitionService.getTargetNodes(processDefinitionId, taskDefKey);
        return listMap;
    }

    /**
     * 根据流程定义Id和流程节点Key获取目标任务节点集合,去除名称相等的节点，并且加上结束节点
     *
     * @param tenantId 租户Id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务key
     * @return List<Map<String, String>>
     */
    @Override
    @GetMapping(value = "/getTargetNodes1", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, String>> getTargetNodes1(@RequestParam String tenantId, @RequestParam String processDefinitionId, @RequestParam String taskDefKey) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        List<Map<String, String>> listMap = customProcessDefinitionService.getTargetNodes1(processDefinitionId, taskDefKey);
        return listMap;
    }

    /**
     * 根据流程定义Id和流程节点Key获取目标任务节点集合
     *
     * @param tenantId 租户Id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务key
     * @return List<Map<String, String>>
     */
    @Override
    @GetMapping(value = "/getTargetNodes4ParallelGateway", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, String>> getTargetNodes4ParallelGateway(@RequestParam String tenantId, @RequestParam String processDefinitionId, @RequestParam String taskDefKey) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        List<Map<String, String>> listMap = customProcessDefinitionService.getTargetNodes4ParallelGateway(processDefinitionId, taskDefKey);
        return listMap;
    }

    /**
     * 根据流程定义Id和流程节点Key获取目标任务节点集合
     *
     * @param tenantId 租户Id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务key
     * @param isContainEndNode 是否包含结束节点
     * @return List<Map<String, String>>
     */
    @Override
    @GetMapping(value = "/getTargetNodes4UserTask", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, String>> getTargetNodes4UserTask(@RequestParam String tenantId, @RequestParam String processDefinitionId, @RequestParam String taskDefKey, @RequestParam Boolean isContainEndNode) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        List<Map<String, String>> listMap = customProcessDefinitionService.getTargetNodes4UserTask(processDefinitionId, taskDefKey, isContainEndNode);
        return listMap;
    }

    /**
     * 判断流程定义的节点是否是callActivity节点
     *
     * @param tenantId 租户Id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务key
     * @return Boolean
     */
    @Override
    @GetMapping(value = "/isCallActivity", produces = MediaType.APPLICATION_JSON_VALUE)
    public Boolean isCallActivity(@RequestParam String tenantId, @RequestParam String processDefinitionId, @RequestParam String taskDefKey) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Boolean isCallActivity = customProcessDefinitionService.isCallActivity(processDefinitionId, taskDefKey);
        return isCallActivity;
    }

    /**
     * 查找当前任务节点的输出目标节点中是否包含某一类型的特定节点
     *
     * @param tenantId 租户Id
     * @param taskId 任务id
     * @param nodeType 节点类型
     * @return Boolean
     */
    @Override
    @GetMapping(value = "/isContainNodeType", produces = MediaType.APPLICATION_JSON_VALUE)
    public Boolean isContainNodeType(@RequestParam String tenantId, @RequestParam String taskId, @RequestParam String nodeType) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Boolean isContainNodeType = customProcessDefinitionService.isContainNodeType(taskId, nodeType);
        return isContainNodeType;
    }
}
