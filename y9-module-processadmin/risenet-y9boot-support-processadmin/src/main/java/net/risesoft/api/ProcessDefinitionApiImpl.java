package net.risesoft.api;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.model.processadmin.FlowElementModel;
import net.risesoft.model.processadmin.GatewayModel;
import net.risesoft.model.processadmin.TargetModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.CustomProcessDefinitionService;
import net.risesoft.y9.FlowableTenantInfoHolder;

/**
 * 流程定义相关接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/processDefinition", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProcessDefinitionApiImpl implements ProcessDefinitionApi {

    private final CustomProcessDefinitionService customProcessDefinitionService;

    /**
     * 获取有办结权限的UserTask
     *
     * @param tenantId 租户Id
     * @param processDefinitionId 流程定义id
     * @return {@code Y9Result<List<TargetModel>>} 通用请求返回对象 - data 有办结权限的UserTask
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<TargetModel>> getContainEndEvent4UserTask(@RequestParam String tenantId,
        @RequestParam String processDefinitionId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        return customProcessDefinitionService.listContainEndEvent4UserTask(processDefinitionId);
    }

    /**
     * 获取某一任务所在节点的目标是结束节点的目标节点Key
     *
     * @param tenantId 租户Id
     * @param taskId 任务id
     * @return {@code Y9Result<String>} 通用请求返回对象 - data 目标节点Key
     * @since 9.6.6
     */
    @Override
    public Y9Result<String> getEndNodeKeyByTaskId(@RequestParam String tenantId, @RequestParam String taskId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        return Y9Result.success(customProcessDefinitionService.getEndNodeKeyByTaskId(taskId));
    }

    /**
     * 根据流程定义Id获取节点,路由信息 isContainStartNode为true时，不包含开始节点
     *
     * @param tenantId 租户Id
     * @param processDefinitionId 流程定义id
     * @param isContainStartNode 是否包含开始节点
     * @return {@code List<FlowElementModel>>} 通用请求返回对象 - data 节点集合
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<FlowElementModel>> getFlowElement(@RequestParam String tenantId,
        @RequestParam String processDefinitionId, @RequestParam Boolean isContainStartNode) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        return customProcessDefinitionService.listFlowElementByProcessDefinitionId(processDefinitionId,
            isContainStartNode);
    }

    /**
     * 获取具体流程的某个节点类型
     *
     * @param tenantId 租户Id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务key
     * @return {@code Y9Result<String>} 通用请求返回对象 - data 节点类型
     * @since 9.6.6
     */
    @Override
    public Y9Result<String> getNodeType(@RequestParam String tenantId, @RequestParam String processDefinitionId,
        @RequestParam String taskDefKey) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        return Y9Result.success(customProcessDefinitionService.getNodeType(processDefinitionId, taskDefKey));
    }

    /**
     * 根据流程定义Id获取节点信息 isContainStartNode为true时，不包含开始节点
     *
     * @param tenantId 租户Id
     * @param processDefinitionId 流程定义id
     * @param isContainStartNode 是否包含开始节点
     * @return {@code List<TargetModel>} 通用请求返回对象 - data 节点信息集合
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<TargetModel>> getNodes(@RequestParam String tenantId, @RequestParam String processDefinitionId,
        @RequestParam Boolean isContainStartNode) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        return customProcessDefinitionService.listNodesByProcessDefinitionId(processDefinitionId, isContainStartNode);
    }

    /**
     * 根据taskId获取某个节点除去end节点和默认路由节点的所有的输出线路的个数
     *
     * @param tenantId 租户Id
     * @param taskId 任务id
     * @return {@code Y9Result<Integer>} 通用请求返回对象 - data 输出线路的个数
     * @since 9.6.6
     */
    @Override
    public Y9Result<Integer> getOutPutNodeCount(@RequestParam String tenantId, @RequestParam String taskId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        return Y9Result.success(customProcessDefinitionService.getOutPutNodeCount(taskId));
    }

    /**
     * 根据流程定义Id和流程节点Key获取目标任务节点集合
     *
     * @param tenantId 租户Id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务key
     * @return {@code Y9Result<List<GatewayModel>} 通用请求返回对象 - data 并行网关节点集合
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<GatewayModel>> getParallelGatewayList(@RequestParam String tenantId,
        @RequestParam String processDefinitionId, @RequestParam String taskDefKey) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        return customProcessDefinitionService.listParallelGateway(processDefinitionId, taskDefKey);
    }

    /**
     * 根据流程定义id获取开始节点
     *
     * @param tenantId 租户Id
     * @param processDefinitionId 流程定义id
     * @return {@code Y9Result<String>} 通用请求返回对象 - data 开始节点
     * @since 9.6.6
     */
    @Override
    public Y9Result<String> getStartNodeKeyByProcessDefinitionId(@RequestParam String tenantId,
        @RequestParam String processDefinitionId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        return Y9Result
            .success(customProcessDefinitionService.getStartNodeKeyByProcessDefinitionId(processDefinitionId));
    }

    /**
     * 根据流程定义key获取最新版本的流程定义的启动节点的taskdefineKey
     *
     * @param tenantId 租户Id
     * @param processDefinitionKey 流程定义Key
     * @return {@code Y9Result<String>} 通用请求返回对象 - data taskDefineKey
     * @since 9.6.6
     */
    @Override
    public Y9Result<String> getStartNodeKeyByProcessDefinitionKey(@RequestParam String tenantId,
        @RequestParam String processDefinitionKey) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        return Y9Result
            .success(customProcessDefinitionService.getStartNodeKeyByProcessDefinitionKey(processDefinitionKey));
    }

    /**
     * 根据流程定义Id和流程节点Key获取目标任务节点集合
     *
     * @param tenantId 租户Id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务key
     * @return {@code Y9Result<List<TargetModel>>} 通用请求返回对象 - data 任务节点集合
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<TargetModel>> getTargetNodes(@RequestParam String tenantId,
        @RequestParam String processDefinitionId, @RequestParam String taskDefKey) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        return customProcessDefinitionService.listTargetNodes(processDefinitionId, taskDefKey);
    }

    /**
     * 根据流程定义Id和流程节点Key获取目标任务节点集合,去除名称相等的节点，并且加上结束节点
     *
     * @param tenantId 租户Id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务key
     * @return {@code Y9Result<List<TargetModel>> } 通用请求返回对象 - data 任务节点集合
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<TargetModel>> getTargetNodes1(@RequestParam String tenantId,
        @RequestParam String processDefinitionId, @RequestParam String taskDefKey) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        return customProcessDefinitionService.listTargetNodesContainEndNodeAndNotEq(processDefinitionId, taskDefKey);
    }

    /**
     * 根据流程定义Id和流程节点Key获取目标任务节点集合
     *
     * @param tenantId 租户Id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务key
     * @return {@code  Y9Result<List<GatewayModel>>} 通用请求返回对象 - data 任务节点集合
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<GatewayModel>> getTargetNodes4ParallelGateway(@RequestParam String tenantId,
        @RequestParam String processDefinitionId, @RequestParam String taskDefKey) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        return customProcessDefinitionService.listTargetNodes4ParallelGateway(processDefinitionId, taskDefKey);
    }

    /**
     * 根据流程定义Id和流程节点Key获取目标任务节点集合（可设置是否包含结束节点）
     *
     * @param tenantId 租户Id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务key
     * @param isContainEndNode 是否包含结束节点
     * @return {@code Y9Result<List<TargetModel>} 通用请求返回对象 - data 任务节点集合
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<TargetModel>> getTargetNodes4UserTask(@RequestParam String tenantId,
        @RequestParam String processDefinitionId, @RequestParam String taskDefKey,
        @RequestParam Boolean isContainEndNode) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        return customProcessDefinitionService.listTargetNodes4UserTask(processDefinitionId, taskDefKey,
            isContainEndNode);
    }

    /**
     * 判断流程定义的节点是否是callActivity节点
     *
     * @param tenantId 租户Id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务key
     * @return {@code Y9Result<Boolean>} 通用请求返回对象 - data 判断结果
     * @since 9.6.6
     */
    @Override
    public Y9Result<Boolean> isCallActivity(@RequestParam String tenantId, @RequestParam String processDefinitionId,
        @RequestParam String taskDefKey) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        return Y9Result.success(customProcessDefinitionService.isCallActivity(processDefinitionId, taskDefKey));
    }

    /**
     * 查找当前任务节点的输出目标节点中是否包含某一类型的特定节点
     *
     * @param tenantId 租户Id
     * @param taskId 任务id
     * @param nodeType 节点类型
     * @return {@code Y9Result<Boolean>} 通用请求返回对象 - data 判断结果
     * @since 9.6.6
     */
    @Override
    public Y9Result<Boolean> isContainNodeType(@RequestParam String tenantId, @RequestParam String taskId,
        @RequestParam String nodeType) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        return Y9Result.success(customProcessDefinitionService.isContainNodeType(taskId, nodeType));
    }

    /**
     * 查找当前任务节点的输出目标节点中是否包含某一类型的特定节点
     *
     * @param tenantId 租户Id
     * @param taskId 任务id
     * @return {@code Y9Result<Boolean>} 通用请求返回对象 - data 判断结果
     * @since 9.6.6
     */
    @Override
    public Y9Result<TargetModel> getEndNode(@RequestParam String tenantId, @RequestParam String taskId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        return customProcessDefinitionService.getEndNode(taskId);
    }

    /**
     * 判断流程定义的节点是否是SubProcess节点
     *
     * @param tenantId 租户Id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务key
     * @return {@code Y9Result<Boolean>} 通用请求返回对象 - data 判断结果
     * @since 9.6.6
     */
    @Override
    public Y9Result<Boolean> isSubProcess(@RequestParam String tenantId, @RequestParam String processDefinitionId,
        @RequestParam String taskDefKey) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        return Y9Result.success(customProcessDefinitionService.isSubProcess(processDefinitionId, taskDefKey));
    }

    /**
     * 判断流程定义的节点是否是SubProcess内的节点
     *
     * @param tenantId 租户Id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务key
     * @return {@code Y9Result<Boolean>} 通用请求返回对象 - data 判断结果
     * @since 9.6.6
     */
    @Override
    public Y9Result<Boolean> isSubProcessChildNode(@RequestParam String tenantId,
        @RequestParam String processDefinitionId, @RequestParam String taskDefKey) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        return Y9Result.success(customProcessDefinitionService.isSubProcessChildNode(processDefinitionId, taskDefKey));
    }
}
