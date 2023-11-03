package net.risesoft.api.itemadmin.position;

import java.util.Map;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface Document4PositionApi {

    /**
     *
     * Description: 新建
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param itemId 事项id
     * @param mobile 是否发送手机端
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> add(String tenantId, String positionId, String itemId, boolean mobile);

    /**
     * 流程办结
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @throws Exception Exception
     */
    void complete(String tenantId, String positionId, String taskId) throws Exception;

    /**
     *
     * Description: 获取发送选人信息
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param itemId 事项id
     * @param processDefinitionKey 流程定义key
     * @param processDefinitionId 流程定义id
     * @param taskId 任务id
     * @param routeToTask 任务key
     * @param processInstanceId 流程实例id
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> docUserChoise(String tenantId, String userId, String positionId, String itemId, String processDefinitionKey, String processDefinitionId, String taskId, String routeToTask, String processInstanceId);

    /**
     *
     * Description: 编辑文档
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param itembox 办件状态，todo（待办）,doing（在办）,done（办结）
     * @param taskId 任务id
     * @param processInstanceId 流程实例id
     * @param itemId 事项id
     * @param mobile 是否发送手机端
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> edit(String tenantId, String positionId, String itembox, String taskId, String processInstanceId, String itemId, boolean mobile);

    /**
     * 带自定义变量发送
     *
     * @param tenantId 租户id
     * @param positionId 岗位 id
     * @param processInstanceId 流程实例id
     * @param taskId 任务id
     * @param sponsorHandle 是否主办人办理
     * @param itemId 事项id
     * @param processSerialNumber 流程编号
     * @param processDefinitionKey 流程定义key
     * @param userChoice 选择的发送人员
     * @param sponsorGuid 主办人id
     * @param routeToTaskId 任务key
     * @param variables 保存变量
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> saveAndForwarding(String tenantId, String positionId, String processInstanceId, String taskId, String sponsorHandle, String itemId, String processSerialNumber, String processDefinitionKey, String userChoice, String sponsorGuid, String routeToTaskId,
        Map<String, Object> variables);

    /**
     * 带自定义变量发送
     *
     * @param tenantId 租户id
     * @param positionId 岗位 id
     * @param processInstanceId 流程实例id
     * @param taskId 任务id
     * @param sponsorHandle 是否主办人办理
     * @param itemId 事项id
     * @param processSerialNumber 流程编号
     * @param processDefinitionKey 流程定义key
     * @param userChoice 选择的发送人员
     * @param sponsorGuid 主办人id
     * @param routeToTaskId 任务key
     * @param variables 保存变量
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> saveAndSubmitTo(String tenantId, String positionId, String taskId, String itemId, String processSerialNumber);

    /**
     *
     * Description: 指定任务节点发送
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param processInstanceId 流程实例id
     * @param taskId 任务id
     * @param sponsorHandle sponsorHandle
     * @param itemId 事项id
     * @param processSerialNumber 流程编号
     * @param processDefinitionKey 流程定义key
     * @param userChoice 选择的发送人员
     * @param sponsorGuid 主办人id
     * @param routeToTaskId 任务key
     * @param startRouteToTaskId startRouteToTaskId
     * @param variables 保存变量
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> saveAndForwardingByTaskKey(String tenantId, String positionId, String processInstanceId, String taskId, String sponsorHandle, String itemId, String processSerialNumber, String processDefinitionKey, String userChoice, String sponsorGuid, String routeToTaskId,
        String startRouteToTaskId, Map<String, Object> variables);

    /**
     * 获取签收任务配置
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @param taskDefinitionKey 任务定义key
     * @param processSerialNumber 流程编号
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> signTaskConfig(String tenantId, String positionId, String itemId, String processDefinitionId, String taskDefinitionKey, String processSerialNumber);

    /**
     * 启动流程
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param itemId 事项id
     * @param processSerialNumber 流程编号
     * @param processDefinitionKey 流程定义key
     * @return Map&lt;String, Object&gt;
     * @throws Exception Exception
     */
    Map<String, Object> startProcess(String tenantId, String positionId, String itemId, String processSerialNumber, String processDefinitionKey) throws Exception;

    /**
     * 启动流程，多人
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param itemId 事项id
     * @param processSerialNumber 流程编号
     * @param processDefinitionKey 流程定义key
     * @param positionIds 岗位ids
     * @return Map&lt;String, Object&gt;
     * @throws Exception Exception
     */
    Map<String, Object> startProcess(String tenantId, String positionId, String itemId, String processSerialNumber, String processDefinitionKey, String positionIds) throws Exception;
}
