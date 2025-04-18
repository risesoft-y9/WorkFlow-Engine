package net.risesoft.service;

import java.util.List;

import org.flowable.bpmn.model.FlowElement;

import net.risesoft.model.processadmin.FlowElementModel;
import net.risesoft.model.processadmin.GatewayModel;
import net.risesoft.model.processadmin.TargetModel;
import net.risesoft.pojo.Y9Result;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
public interface CustomProcessDefinitionService {
    /**
     * 获取流程模型中的节点元素okk
     *
     * @param processDefinitionId 流程定义ID
     * @return List<FlowElement>
     */
    List<FlowElement> getFlowElements(String processDefinitionId);

    /**
     * 查找当前任务节点的输出的结束节点
     *
     * @param taskId
     * @return
     */
    Y9Result<TargetModel> getEndNode(String taskId);

    /**
     * 获取具体流程的某个节点
     *
     * @param processDefinitionId
     * @param taskDefKey
     * @return
     */
    FlowElementModel getNode(String processDefinitionId, String taskDefKey);

    /**
     * 根据taskId获取某个节点除去end节点和默认路由节点的所有的输出线路的个数
     *
     * @param taskId
     * @return
     */
    Integer getOutPutNodeCount(String taskId);

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
     * 根据taskDefinitionKey获取子流程父节点
     *
     * @param processDefinitionId
     * @param taskDefKey
     * @return
     */
    TargetModel getSubProcessParentNode(String processDefinitionId, String taskDefKey);

    /**
     * 根据任务Id获取流程的结束节点信息
     *
     * @param processDefinitionId
     * @return
     */
    String getTaskDefKey4EndEvent(String processDefinitionId);

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
     * @return
     */
    Boolean isContainEndEvent(String taskId);

    /**
     * 判断流程定义的节点是否是SubProcess节点
     *
     * @param processDefinitionId
     * @param taskDefKey
     * @return
     */
    Boolean isSubProcess(String processDefinitionId, String taskDefKey);

    /**
     * 判断流程定义的节点是否是SubProcess内的节点
     *
     * @param processDefinitionId
     * @param taskDefKey
     * @return
     */
    Boolean isSubProcessChildNode(String processDefinitionId, String taskDefKey);

    /**
     * 判断流程定义的节点是否是SubProcess内的节点
     *
     * @param processDefinitionId
     * @return
     */
    Y9Result<List<TargetModel>> getSubProcessChildNode(String processDefinitionId);

    /**
     * 获取有办结权限的UserTask
     *
     * @param processDefinitionId
     * @return
     */
    Y9Result<List<TargetModel>> listContainEndEvent4UserTask(String processDefinitionId);

    /**
     * 根据流程定义Id获取节点信息
     *
     * @param processDefinitionId
     * @return Y9Result<List < FlowElementModel>>
     */
    Y9Result<List<FlowElementModel>> listUserTask(String processDefinitionId);

    /**
     * 根据流程定义Id获取节点信息 isContainStartNode为true时，不包含开始节点
     *
     * @param processDefinitionId
     * @return Y9Result<List < TargetModel>>
     */
    Y9Result<List<TargetModel>> listNodesByProcessDefinitionId(String processDefinitionId);

    /**
     * 根据流程定义Id和流程节点Key获取目标任务节点集合
     *
     * @param processDefinitionId
     * @param taskDefKey
     * @return Y9Result<List < GatewayModel>>
     */
    Y9Result<List<GatewayModel>> listParallelGateway(String processDefinitionId, String taskDefKey);

    /**
     * 根据流程定义Id和流程节点Key获取目标任务节点集合
     *
     * @param processDefinitionId
     * @param taskDefKey
     * @return Y9Result<List < TargetModel>>
     */
    Y9Result<List<TargetModel>> listTargetNodes(String processDefinitionId, String taskDefKey);

    /**
     * Description: 根据流程定义Id和流程节点Key获取目标任务节点集合
     *
     * @param processDefinitionId
     * @param taskDefKey
     * @param isContainEndNode
     * @return
     */
    Y9Result<List<TargetModel>> listTargetNodes4UserTask(String processDefinitionId, String taskDefKey,
        Boolean isContainEndNode);
}
