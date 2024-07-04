package net.risesoft.service;

import net.risesoft.model.processadmin.FlowElementModel;
import net.risesoft.model.processadmin.GatewayModel;
import net.risesoft.model.processadmin.TargetModel;
import net.risesoft.pojo.Y9Result;

import java.util.List;
import java.util.Map;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
public interface CustomProcessDefinitionService {

    /**
     * 获取有办结权限的UserTask
     *
     * @param processDefinitionId
     * @return
     */
    Y9Result<List<TargetModel>> getContainEndEvent4UserTask(String processDefinitionId);

    /**
     * 获取某一任务所在节点的目标是结束节点的目标节点Key,如果有多个结束节点则获取第一个
     *
     * @param taskId
     * @return
     */
    String getEndNodeKeyByTaskId(String taskId);

    /**
     * 根据流程定义Id获取节点,路由信息 isContainStartNode为true时，不包含开始节点
     *
     * @param processDefinitionId
     * @param isContainStartNode
     * @return Y9Result<List<FlowElementModel>>
     */
    Y9Result<List<FlowElementModel>> getFlowElement(String processDefinitionId, Boolean isContainStartNode);

    /**
     * 根据流程定义Id获取节点信息 isContainStartNode为true时，不包含开始节点
     *
     * @param processDefinitionId
     * @param isContainStartNode
     * @return Y9Result<List<TargetModel>>
     */
    Y9Result<List<TargetModel>> getNodes(String processDefinitionId, Boolean isContainStartNode);

    /**
     * 获取具体流程的某个节点类型
     *
     * @param processDefinitionId
     * @param taskDefKey
     * @return
     */
    String getNodeType(String processDefinitionId, String taskDefKey);

    /**
     * 根据taskId获取某个节点除去end节点和默认路由节点的所有的输出线路的个数
     *
     * @param taskId
     * @return
     */
    Integer getOutPutNodeCount(String taskId);

    /**
     * 根据流程定义Id和流程节点Key获取目标任务节点集合
     *
     * @param processDefinitionId
     * @param taskDefKey
     * @return Y9Result<List<GatewayModel>>
     */
    Y9Result<List<GatewayModel>> getParallelGatewayList(String processDefinitionId, String taskDefKey);

    /**
     * Description:
     *
     * @param processDefinitionId
     * @return
     */
    String getStartNodeKeyByProcessDefinitionId(String processDefinitionId);

    /**
     * 根据流程定义key获取最新版本的流程定义的启动节点的taskdefineKey
     *
     * @param processDefinitionKey
     * @return
     */
    String getStartNodeKeyByProcessDefinitionKey(String processDefinitionKey);

    /**
     * 根据流程定义Id和流程节点Key获取目标任务节点集合
     *
     * @param processDefinitionId
     * @param taskDefKey
     * @return Y9Result<List<TargetModel>>
     */
    Y9Result<List<TargetModel>> getTargetNodes(String processDefinitionId, String taskDefKey);

    /**
     * 根据流程定义Id和流程节点Key获取目标任务节点集合,去除名称相等的节点，并且加上结束节点
     *
     * @param processDefinitionId
     * @param taskDefKey
     * @return
     */
    List<Map<String, String>> getTargetNodes1(String processDefinitionId, String taskDefKey);

    /**
     * 根据流程定义Id和流程节点Key获取目标任务节点集合
     *
     * @param processDefinitionId
     * @param taskDefKey
     * @return
     */
    List<Map<String, String>> getTargetNodes4ParallelGateway(String processDefinitionId, String taskDefKey);

    /**
     * Description: 根据流程定义Id和流程节点Key获取目标任务节点集合
     *
     * @param processDefinitionId
     * @param taskDefKey
     * @param isContainEndNode
     * @return
     */
    Y9Result<List<TargetModel>> getTargetNodes4UserTask(String processDefinitionId, String taskDefKey,
        Boolean isContainEndNode);

    /**
     * 根据任务Id获取流程的结束节点信息
     *
     * @param taskId
     * @return
     */
    String getTaskDefKey4EndEvent(String taskId);

    /**
     * 判断流程定义的节点是否是callActivity节点
     *
     * @param processDefinitionId
     * @param taskDefKey
     * @return
     */
    Boolean isCallActivity(String processDefinitionId, String taskDefKey);

    /**
     * 查找当前任务节点的输出目标节点中是否包含某一类型的特定节点
     *
     * @param taskId
     * @param nodeType
     * @return
     */
    Boolean isContainNodeType(String taskId, String nodeType);
}
