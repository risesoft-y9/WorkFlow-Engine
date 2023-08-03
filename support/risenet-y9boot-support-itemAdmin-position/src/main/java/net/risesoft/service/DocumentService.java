package net.risesoft.service;

import java.util.List;
import java.util.Map;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface DocumentService {

    /**
     * Description: 事项新建公文
     *
     * @param itemId
     * @param mobile
     * @param map
     * @return
     */
    public Map<String, Object> add(String itemId, boolean mobile, Map<String, Object> map);

    /**
     * Description: 办结
     *
     * @param taskId
     * @throws Exception
     */
    public void complete(String taskId) throws Exception;

    /**
     * Description: 发送对象获取（单个串行-并行节点）
     *
     * @param itemId
     * @param processDefinitionKey
     * @param processDefinitionId
     * @param taskId
     * @param routeToTask
     * @param processInstanceId
     * @return
     */
    public Map<String, Object> docUserChoise(String itemId, String processDefinitionKey, String processDefinitionId,
        String taskId, String routeToTask, String processInstanceId);

    /**
     * Description: 办件办理
     *
     * @param itembox
     * @param taskId
     * @param processInstanceId
     * @param itemId
     * @param mobile
     * @return
     */
    public Map<String, Object> edit(String itembox, String taskId, String processInstanceId, String itemId,
        boolean mobile);

    /**
     * Description: 发送
     *
     * @param taskId
     * @param sponsorHandle
     * @param userChoice
     * @param routeToTaskId
     * @param sponsorGuid
     * @return
     */
    public Map<String, Object> forwarding(String taskId, String sponsorHandle, String userChoice, String routeToTaskId,
        String sponsorGuid);

    /**
     * Description: 获取绑定表单
     *
     * @param itemId
     * @param processDefinitionKey
     * @param processDefinitionId
     * @param taskDefinitionKey
     * @param mobile
     * @param map
     * @return
     */
    Map<String, Object> genDocumentModel(String itemId, String processDefinitionKey, String processDefinitionId,
        String taskDefinitionKey, boolean mobile, Map<String, Object> map);

    /**
     *
     * Description: 获取首个事项id
     *
     * @return
     */
    String getFirstItem();

    /**
     * 根据事项id获取绑定表单
     *
     * @param itemId
     * @param processDefinitionKey
     * @return
     */
    public String getFormIdByItemId(String itemId, String processDefinitionKey);

    /**
     * 获取新建事项列表
     *
     * @return
     */
    public List<Map<String, Object>> getItemList();

    /**
     * 获取个人有权限列表
     *
     * @return
     */
    List<Map<String, Object>> getMyItemList();

    /**
     * Description: 获取菜单
     *
     * @param itemId
     * @param processDefinitionId
     * @param taskDefKey
     * @param taskId
     * @param map
     * @param itembox
     * @return
     */
    Map<String, Object> menuControl(String itemId, String processDefinitionId, String taskDefKey, String taskId,
        Map<String, Object> map, String itembox);

    /**
     *
     * Description: 解析工作流发送时用户选取的人员
     *
     * @param userChoice
     * @return
     */
    List<String> parseUserChoice(String userChoice);

    /**
     * 重定位
     *
     * @param taskId
     * @param userChoice
     * @return
     */
    public Map<String, Object> reposition(String taskId, String userChoice);

    /**
     * Description: 启动流程并发送
     *
     * @param itemId
     * @param processSerialNumber
     * @param processDefinitionKey
     * @param userChoice
     * @param sponsorGuid
     * @param routeToTaskId
     * @param variables
     * @return
     */
    Map<String, Object> saveAndForwarding(String itemId, String processSerialNumber, String processDefinitionKey,
        String userChoice, String sponsorGuid, String routeToTaskId, Map<String, Object> variables);

    /**
     * Description: 启动流程并发送(指定)
     *
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
    public Map<String, Object> saveAndForwardingByTaskKey(String itemId, String processSerialNumber,
        String processDefinitionKey, String userChoice, String sponsorGuid, String routeToTaskId,
        String startRouteToTaskId, Map<String, Object> variables);

    /**
     * 获取签收任务配置
     *
     * @param itemId
     * @param processDefinitionId
     * @param taskDefinitionKey
     * @param processSerialNumber
     * @return
     */
    public Map<String, Object> signTaskConfig(String itemId, String processDefinitionId, String taskDefinitionKey,
        String processSerialNumber);

    /**
     * 启动流程，用于当前人启动本租户的流程，启动者是人
     *
     * @param itemId
     * @param processSerialNumber
     * @param processDefinitionKey
     * @return
     */
    public Map<String, Object> startProcess(String itemId, String processSerialNumber, String processDefinitionKey);

    /**
     * 启动流程，多人
     *
     * @param itemId
     * @param processSerialNumber
     * @param processDefinitionKey
     * @param positionIds
     * @return
     */
    public Map<String, Object> startProcess(String itemId, String processSerialNumber, String processDefinitionKey,
        String positionIds);

    /**
     * 启动流程，指定任务节点
     *
     * @param itemId
     * @param processSerialNumber
     * @param processDefinitionKey
     * @param startRouteToTaskId
     * @return
     */
    public Map<String, Object> startProcessByTaskKey(String itemId, String processSerialNumber,
        String processDefinitionKey, String startRouteToTaskId);

}
