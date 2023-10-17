package net.risesoft.api.itemadmin;

import java.util.Map;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface DocumentApi {

    /**
     * Description: 新建
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param itemId 事项id
     * @param mobile 是否发送手机端
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> add(String tenantId, String userId, String itemId, boolean mobile);

    /**
     * 流程办结
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param taskId 任务id
     * @throws Exception Exception
     */
    void complete(String tenantId, String userId, String taskId) throws Exception;

    /**
     *
     * Description: 获取发送选人信息
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param itemId 事项id
     * @param processDefinitionKey 流程定义key
     * @param processDefinitionId 流程定义id
     * @param taskId 任务id
     * @param routeToTask routeToTask
     * @param processInstanceId 流程实例id
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> docUserChoise(String tenantId, String userId, String itemId, String processDefinitionKey, String processDefinitionId, String taskId, String routeToTask, String processInstanceId);

    /**
     *
     * Description: 编辑文档
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param itembox 办件状态，todo（待办）,doing（在办）,done（办结）
     * @param taskId 任务id
     * @param processInstanceId 流程实例id
     * @param itemId 事项id
     * @param mobile 是否发送手机端
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> edit(String tenantId, String userId, String itembox, String taskId, String processInstanceId, String itemId, boolean mobile);

    /**
     *
     * Description: 发送
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param taskId 任务id
     * @param userChoice userChoice
     * @param routeToTaskId routeToTaskId
     * @param variables 变量
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> forwardingSendReceive(String tenantId, String userId, String taskId, String userChoice, String routeToTaskId, Map<String, Object> variables);

    /**
     * 带自定义变量发送
     *
     * @param tenantId 租户id
     * @param userId 用户id
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
    Map<String, Object> saveAndForwarding(String tenantId, String userId, String processInstanceId, String taskId, String sponsorHandle, String itemId, String processSerialNumber, String processDefinitionKey, String userChoice, String sponsorGuid, String routeToTaskId,
        Map<String, Object> variables);

    /**
     *
     * Description: 指定任务节点发送
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processInstanceId 流程实例id
     * @param taskId 任务id
     * @param sponsorHandle sponsorHandle
     * @param itemId 事项id
     * @param processSerialNumber 流程编号
     * @param processDefinitionKey 流程定义key
     * @param userChoice 人员ids
     * @param sponsorGuid sponsorGuid
     * @param routeToTaskId routeToTaskId
     * @param startRouteToTaskId startRouteToTaskId
     * @param variables variables
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> saveAndForwardingByTaskKey(String tenantId, String userId, String processInstanceId, String taskId, String sponsorHandle, String itemId, String processSerialNumber, String processDefinitionKey, String userChoice, String sponsorGuid, String routeToTaskId,
        String startRouteToTaskId, Map<String, Object> variables);

    /**
     * 获取签收任务配置
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @param taskDefinitionKey taskDefinitionKey
     * @param processSerialNumber 流程编号
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> signTaskConfig(String tenantId, String userId, String itemId, String processDefinitionId, String taskDefinitionKey, String processSerialNumber);

    /**
     * 启动流程
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param itemId 事项id
     * @param processSerialNumber 流程编号
     * @param processDefinitionKey 流程定义key
     * @return Map&lt;String, Object&gt;
     * @throws Exception Exception
     */
    Map<String, Object> startProcess(String tenantId, String userId, String itemId, String processSerialNumber, String processDefinitionKey) throws Exception;

    /**
     * 启动流程，多人
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param itemId 事项id
     * @param processSerialNumber 流程编号
     * @param processDefinitionKey 流程定义key
     * @param userIds 人员ids
     * @return Map&lt;String, Object&gt;
     * @throws Exception 异常
     */
    Map<String, Object> startProcess(String tenantId, String userId, String itemId, String processSerialNumber, String processDefinitionKey, String userIds) throws Exception;

}
