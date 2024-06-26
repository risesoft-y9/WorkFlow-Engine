package net.risesoft.service;

import java.util.List;
import java.util.Map;

import net.risesoft.model.itemadmin.AddItemListModel;
import net.risesoft.model.itemadmin.DocUserChoiseModel;
import net.risesoft.model.itemadmin.OpenDataModel;
import net.risesoft.model.itemadmin.SignTaskConfigModel;
import net.risesoft.model.itemadmin.StartProcessResultModel;
import net.risesoft.pojo.Y9Result;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface DocumentService {

    /**
     * Description: 事项新建公文
     *
     * @param itemId 事项id
     * @param mobile
     * @param map
     * @return
     */
    OpenDataModel add(String itemId, boolean mobile);

    /**
     * Description: 办结
     *
     * @param taskId 任务id
     * @throws Exception
     */
    void complete(String taskId) throws Exception;

    /**
     * Description: 发送对象获取（单个串行-并行节点）
     *
     * @param itemId 事项id
     * @param processDefinitionKey 流程定义key
     * @param processDefinitionId 流程定义id
     * @param taskId 任务id
     * @param routeToTask
     * @param processInstanceId 流程实例id
     * @return
     */
    DocUserChoiseModel docUserChoise(String itemId, String processDefinitionKey, String processDefinitionId,
        String taskId, String routeToTask, String processInstanceId);

    /**
     * Description: 办件办理
     *
     * @param itembox
     * @param taskId 任务id
     * @param processInstanceId 流程实例id
     * @param itemId 事项id
     * @param mobile
     * @return
     */
    OpenDataModel edit(String itembox, String taskId, String processInstanceId, String itemId, boolean mobile);

    /**
     * Description: 发送
     *
     * @param taskId 任务id
     * @param sponsorHandle
     * @param userChoice
     * @param routeToTaskId
     * @param sponsorGuid
     * @return
     */
    Y9Result<String> forwarding(String taskId, String sponsorHandle, String userChoice, String routeToTaskId,
        String sponsorGuid);

    /**
     * Description: 获取绑定表单
     *
     * @param itemId 事项id
     * @param processDefinitionKey 流程定义key
     * @param processDefinitionId 流程定义id
     * @param taskDefinitionKey 任务节点key
     * @param mobile
     * @param map
     * @return
     */
    OpenDataModel genDocumentModel(String itemId, String processDefinitionKey, String processDefinitionId,
        String taskDefinitionKey, boolean mobile, OpenDataModel model);

    /**
     * Description: 获取首个事项id
     *
     * @return
     */
    String getFirstItem();

    /**
     * 根据事项id获取绑定表单
     *
     * @param itemId 事项id
     * @param processDefinitionKey 流程定义key
     * @return
     */
    String getFormIdByItemId(String itemId, String processDefinitionKey);

    /**
     * 获取新建事项列表
     *
     * @return
     */
    List<AddItemListModel> getItemList();

    /**
     * 获取个人有权限列表
     *
     * @return
     */
    List<Map<String, Object>> getMyItemList();

    /**
     * Description: 获取菜单
     *
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey
     * @param taskId 任务id
     * @param map
     * @param itembox
     * @return
     */
    OpenDataModel menuControl(String itemId, String processDefinitionId, String taskDefKey, String taskId,
        OpenDataModel model, String itembox);

    /**
     * Description: 解析工作流发送时用户选取的人员
     *
     * @param userChoice
     * @return
     */
    List<String> parseUserChoice(String userChoice);

    /**
     * 重定位
     *
     * @param taskId 任务id
     * @param userChoice
     * @return
     */
    Map<String, Object> reposition(String taskId, String userChoice);

    /**
     * Description: 启动流程并发送
     *
     * @param itemId 事项id
     * @param processSerialNumber 流程序列号
     * @param processDefinitionKey 流程定义key
     * @param userChoice
     * @param sponsorGuid
     * @param routeToTaskId
     * @param variables
     * @return
     */
    Y9Result<String> saveAndForwarding(String itemId, String processSerialNumber, String processDefinitionKey,
        String userChoice, String sponsorGuid, String routeToTaskId, Map<String, Object> variables);

    /**
     * Description: 启动流程并发送(指定)
     *
     * @param itemId 事项id
     * @param processSerialNumber 流程序列号
     * @param processDefinitionKey 流程定义key
     * @param userChoice
     * @param sponsorGuid
     * @param routeToTaskId
     * @param startRouteToTaskId
     * @param variables
     * @return
     */
    Y9Result<String> saveAndForwardingByTaskKey(String itemId, String processSerialNumber, String processDefinitionKey,
        String userChoice, String sponsorGuid, String routeToTaskId, String startRouteToTaskId,
        Map<String, Object> variables);

    /**
     * Description: 启动流程并提交
     *
     * @param itemId 事项id
     * @param processSerialNumber 流程序列号
     * @return
     */
    Y9Result<Object> saveAndSubmitTo(String itemId, String processSerialNumber);

    /**
     * 获取签收任务配置
     *
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @param taskDefinitionKey 任务节点key
     * @param processSerialNumber 流程序列号
     * @return
     */
    SignTaskConfigModel signTaskConfig(String itemId, String processDefinitionId, String taskDefinitionKey,
        String processSerialNumber);

    /**
     * 启动流程，用于当前人启动本租户的流程，启动者是人
     *
     * @param itemId 事项id
     * @param processSerialNumber 流程序列号
     * @param processDefinitionKey 流程定义key
     * @return
     */
    StartProcessResultModel startProcess(String itemId, String processSerialNumber, String processDefinitionKey);

    /**
     * 启动流程，多人
     *
     * @param itemId 事项id
     * @param processSerialNumber 流程序列号
     * @param processDefinitionKey 流程定义key
     * @param positionIds
     * @return
     */
    StartProcessResultModel startProcess(String itemId, String processSerialNumber, String processDefinitionKey,
        String positionIds);

    /**
     * 启动流程，指定任务节点
     *
     * @param itemId 事项id
     * @param processSerialNumber 流程序列号
     * @param processDefinitionKey 流程定义key
     * @param startRouteToTaskId
     * @return
     */
    Map<String, Object> startProcessByTaskKey(String itemId, String processSerialNumber, String processDefinitionKey,
        String startRouteToTaskId);

    /**
     * Description: 启动流程并提交
     *
     * @param processSerialNumber 流程序列号
     * @param taskId 任务id
     * @return
     */
    Y9Result<Object> submitTo(String processSerialNumber, String taskId);

}
