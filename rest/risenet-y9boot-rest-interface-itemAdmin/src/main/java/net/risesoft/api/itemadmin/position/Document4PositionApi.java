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
     * @param tenantId
     * @param positionId
     * @param itemId
     * @param mobile
     * @return
     */
    public Map<String, Object> add(String tenantId, String positionId, String itemId, boolean mobile);

    /**
     * 流程办结
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @throws Exception Exception
     */
    public void complete(String tenantId, String positionId, String taskId) throws Exception;

    /**
     *
     * Description: 获取发送选人信息
     *
     * @param tenantId
     * @param userId
     * @param positionId
     * @param itemId
     * @param processDefinitionKey
     * @param processDefinitionId
     * @param taskId
     * @param routeToTask
     * @param processInstanceId
     * @return
     */
    public Map<String, Object> docUserChoise(String tenantId, String userId, String positionId, String itemId, String processDefinitionKey, String processDefinitionId, String taskId, String routeToTask, String processInstanceId);

    /**
     *
     * Description: 编辑文档
     *
     * @param tenantId
     * @param positionId
     * @param itembox
     * @param taskId
     * @param processInstanceId
     * @param itemId
     * @param mobile
     * @return
     */
    public Map<String, Object> edit(String tenantId, String positionId, String itembox, String taskId, String processInstanceId, String itemId, boolean mobile);

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
    public Map<String, Object> saveAndForwarding(String tenantId, String positionId, String processInstanceId, String taskId, String sponsorHandle, String itemId, String processSerialNumber, String processDefinitionKey, String userChoice, String sponsorGuid, String routeToTaskId,
        Map<String, Object> variables);

    /**
     *
     * Description: 指定任务节点发送
     *
     * @param tenantId
     * @param positionId
     * @param processInstanceId
     * @param taskId
     * @param sponsorHandle
     * @param itemId
     * @param processSerialNumber
     * @param processDefinitionKey
     * @param userChoice
     * @param sponsorGuid
     * @param routeToTaskId
     * @param startRouteToTaskId
     * @param variables
     * @return
     */
    public Map<String, Object> saveAndForwardingByTaskKey(String tenantId, String positionId, String processInstanceId, String taskId, String sponsorHandle, String itemId, String processSerialNumber, String processDefinitionKey, String userChoice, String sponsorGuid, String routeToTaskId,
        String startRouteToTaskId, Map<String, Object> variables);

    /**
     * 获取签收任务配置
     *
     * @param tenantId
     * @param positionId
     * @param itemId
     * @param processDefinitionId
     * @param taskDefinitionKey
     * @param processSerialNumber
     * @return
     */
    public Map<String, Object> signTaskConfig(String tenantId, String positionId, String itemId, String processDefinitionId, String taskDefinitionKey, String processSerialNumber);

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
    public Map<String, Object> startProcess(String tenantId, String positionId, String itemId, String processSerialNumber, String processDefinitionKey) throws Exception;

    /**
     * 启动流程，多人
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param itemId 事项id
     * @param processSerialNumber 流程编号
     * @param processDefinitionKey 流程定义key
     * @param positionIds 岗位ids
     * @return
     * @throws Exception
     */
    Map<String, Object> startProcess(String tenantId, String positionId, String itemId, String processSerialNumber, String processDefinitionKey, String positionIds) throws Exception;
}
