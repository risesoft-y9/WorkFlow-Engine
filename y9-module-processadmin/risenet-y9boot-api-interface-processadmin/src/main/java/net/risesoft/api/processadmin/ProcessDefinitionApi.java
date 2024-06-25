package net.risesoft.api.processadmin;

import java.util.List;
import java.util.Map;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface ProcessDefinitionApi {

    /**
     * 获取有办结权限的UserTask
     *
     * @param tenantId 租户Id
     * @param processDefinitionId 流程定义id
     * @return List&lt;Map&lt;String, Object&gt;&gt;
     */
    List<Map<String, String>> getContainEndEvent4UserTask(String tenantId, String processDefinitionId);

    /**
     * 获取某一任务所在节点的目标是结束节点的目标节点Key
     *
     * @param tenantId 租户Id
     * @param taskId 任务id
     * @return String
     */
    String getEndNodeKeyByTaskId(String tenantId, String taskId);

    /**
     * 根据流程定义Id获取节点,路由信息 isContainStartNode为true时，不包含开始节点
     *
     * @param tenantId 租户Id
     * @param processDefinitionId 流程定义id
     * @param isContainStartNode 是否包含开始节点
     * @return
     */
    List<Map<String, Object>> getFlowElement(String tenantId, String processDefinitionId, Boolean isContainStartNode);

    /**
     * 根据流程定义Id获取节点信息 isContainStartNode为true时，不包含开始节点
     *
     * @param tenantId 租户Id
     * @param processDefinitionId 流程定义id
     * @param isContainStartNode 是否包含开始节点
     * @return List&lt;Map&lt;String, Object&gt;&gt;
     */
    List<Map<String, Object>> getNodes(String tenantId, String processDefinitionId, Boolean isContainStartNode);

    /**
     * 获取具体流程的某个节点类型
     *
     * @param tenantId 租户Id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务key
     * @return String
     */
    String getNodeType(String tenantId, String processDefinitionId, String taskDefKey);

    /**
     * 根据taskId获取某个节点除去end节点和默认路由节点的所有的输出线路的个数
     *
     * @param tenantId 租户Id
     * @param taskId 任务id
     * @return Integer
     */
    Integer getOutPutNodeCount(String tenantId, String taskId);

    /**
     * 根据流程定义Id和流程节点Key获取目标任务节点集合
     *
     * @param tenantId 租户Id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务key
     * @return List&lt;Map&lt;String, Object&gt;&gt;
     */
    List<Map<String, String>> getParallelGatewayList(String tenantId, String processDefinitionId, String taskDefKey);

    /**
     * 根据流程定义id获取开始节点
     *
     * @param tenantId 租户Id
     * @param processDefinitionId 流程定义id
     * @return String
     */
    String getStartNodeKeyByProcessDefinitionId(String tenantId, String processDefinitionId);

    /**
     * 根据流程定义key获取最新版本的流程定义的启动节点的taskdefineKey
     *
     * @param tenantId 租户Id
     * @param processDefinitionKey 流程定义Key
     * @return String
     */
    String getStartNodeKeyByProcessDefinitionKey(String tenantId, String processDefinitionKey);

    /**
     * 根据流程定义Id和流程节点Key获取目标任务节点集合
     *
     * @param tenantId 租户Id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务key
     * @return List&lt;Map&lt;String, Object&gt;&gt;
     */
    List<Map<String, String>> getTargetNodes(String tenantId, String processDefinitionId, String taskDefKey);

    /**
     * 根据流程定义Id和流程节点Key获取目标任务节点集合,去除名称相等的节点，并且加上结束节点
     *
     * @param tenantId 租户Id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务key
     * @return List&lt;Map&lt;String, Object&gt;&gt;
     */
    List<Map<String, String>> getTargetNodes1(String tenantId, String processDefinitionId, String taskDefKey);

    /**
     * 根据流程定义Id和流程节点Key获取目标任务节点集合
     *
     * @param tenantId 租户Id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务key
     * @return List&lt;Map&lt;String, Object&gt;&gt;
     */
    List<Map<String, String>> getTargetNodes4ParallelGateway(String tenantId, String processDefinitionId,
        String taskDefKey);

    /**
     * 根据流程定义Id和流程节点Key获取目标任务节点集合
     *
     * @param tenantId 租户Id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务key
     * @param isContainEndNode 是否包含结束节点
     * @return List&lt;Map&lt;String, Object&gt;&gt;
     */
    List<Map<String, String>> getTargetNodes4UserTask(String tenantId, String processDefinitionId, String taskDefKey,
        Boolean isContainEndNode);

    /**
     * 判断流程定义的节点是否是callActivity节点
     *
     * @param tenantId 租户Id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务key
     * @return Boolean
     */
    Boolean isCallActivity(String tenantId, String processDefinitionId, String taskDefKey);

    /**
     * 查找当前任务节点的输出目标节点中是否包含某一类型的特定节点
     *
     * @param tenantId 租户Id
     * @param taskId 任务id
     * @param nodeType 节点类型
     * @return Boolean
     */
    Boolean isContainNodeType(String tenantId, String taskId, String nodeType);
}
