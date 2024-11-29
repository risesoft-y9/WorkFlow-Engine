package net.risesoft.service;

import java.util.List;
import java.util.Map;

import net.risesoft.model.itemadmin.DocUserChoiseModel;
import net.risesoft.model.itemadmin.DocumentDetailModel;
import net.risesoft.model.itemadmin.ItemListModel;
import net.risesoft.model.itemadmin.OpenDataModel;
import net.risesoft.model.itemadmin.SignTaskConfigModel;
import net.risesoft.model.itemadmin.StartProcessResultModel;
import net.risesoft.pojo.Y9Result;
import org.springframework.web.bind.annotation.RequestBody;

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
     * @param mobile 是否是移动端
     * @return OpenDataModel
     */
    OpenDataModel add(String itemId, boolean mobile);

    /**
     * Description: 事项新建公文
     * 用于一个开始节点经过排他网关到达多个任务节点的情况，具体到达哪个任务节点开始，需要由用户选择
     *
     * @param itemId          事项id
     * @param startTaskDefKey 开始任务节点
     * @param mobile          是否是移动端
     * @return OpenDataModel
     */
    DocumentDetailModel addWithStartTaskDefKey(String itemId, String startTaskDefKey, boolean mobile);

    /**
     * Description: 办结
     *
     * @param taskId 任务id
     * @throws Exception
     */
    void complete(String taskId) throws Exception;

    /**
     * Description: 办结
     *
     * @param taskId 任务id
     * @throws Exception
     */
    void completeSub(String taskId) throws Exception;

    /**
     * Description: 发送对象获取（单个串行-并行节点）
     *
     * @param itemId               事项id
     * @param processDefinitionKey 流程定义key
     * @param processDefinitionId  流程定义id
     * @param taskId               任务id
     * @param routeToTask
     * @param processInstanceId    流程实例id
     * @return
     */
    DocUserChoiseModel docUserChoise(String itemId, String processDefinitionKey, String processDefinitionId, String taskId, String routeToTask, String processInstanceId);

    /**
     * Description: 办件办理
     *
     * @param itembox
     * @param taskId            任务id
     * @param processInstanceId 流程实例id
     * @param itemId            事项id
     * @param mobile
     * @return
     */
    OpenDataModel edit(String itembox, String taskId, String processInstanceId, String itemId, boolean mobile);

    /**
     * Description: 办件办理
     *
     * @param taskId 任务id
     * @param mobile 是否移动端
     * @return
     */
    DocumentDetailModel editTodo(String taskId, boolean mobile);

    /**
     * Description: 办件办理
     *
     * @param processInstanceId 流程实例id
     * @param mobile
     * @return
     */
    DocumentDetailModel editDoing(String processInstanceId, boolean mobile);

    /**
     * Description: 办件办理
     *
     * @param processInstanceId 流程实例id
     * @param mobile
     * @return
     */
    DocumentDetailModel editDone(String processInstanceId, boolean mobile);

    /**
     * Description: 办件办理
     *
     * @param processInstanceId 流程实例id
     * @param mobile
     * @return
     */
    DocumentDetailModel editRecycle(String processInstanceId, boolean mobile);

    /**
     * Description: 发送
     *
     * @param taskId        任务id
     * @param sponsorHandle
     * @param userChoice
     * @param routeToTaskId
     * @param sponsorGuid
     * @return
     */
    Y9Result<String> forwarding(String taskId, String sponsorHandle, String userChoice, String routeToTaskId, String sponsorGuid);

    /**
     * Description: 获取绑定表单
     *
     * @param itemId               事项id
     * @param processDefinitionKey 流程定义key
     * @param processDefinitionId  流程定义id
     * @param taskDefinitionKey    任务节点key
     * @param mobile
     * @param model
     * @return
     */
    OpenDataModel genDocumentModel(String itemId, String processDefinitionKey, String processDefinitionId, String taskDefinitionKey, boolean mobile, OpenDataModel model);

    /**
     * Description: 获取绑定表单
     *
     * @param itemId               事项id
     * @param processDefinitionKey 流程定义key
     * @param processDefinitionId  流程定义id
     * @param taskDefinitionKey    任务节点key
     * @param mobile
     * @param model
     * @return
     */
    DocumentDetailModel genTabModel(String itemId, String processDefinitionKey, String processDefinitionId, String taskDefinitionKey, boolean mobile, DocumentDetailModel model);

    /**
     * Description: 获取首个事项id
     *
     * @return
     */
    String getFirstItem();

    /**
     * 根据事项id获取绑定表单
     *
     * @param itemId               事项id
     * @param processDefinitionKey 流程定义key
     * @return
     */
    String getFormIdByItemId(String itemId, String processDefinitionKey);

    /**
     * 获取新建事项列表
     *
     * @return
     */
    List<ItemListModel> listItems();

    /**
     * 获取个人有权限列表
     *
     * @return
     */
    List<ItemListModel> listMyItems();

    /**
     * Description: 获取菜单
     *
     * @param itemId              事项id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey
     * @param taskId              任务id
     * @param model
     * @param itembox
     * @return
     */
    OpenDataModel menuControl(String itemId, String processDefinitionId, String taskDefKey, String taskId, OpenDataModel model, String itembox);

    /**
     * Description: 获取菜单
     *
     * @param itemId              事项id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey
     * @param model
     * @return
     */
    DocumentDetailModel menuControl4Add(String itemId, String processDefinitionId, String taskDefKey, DocumentDetailModel model);

    /**
     * Description: 获取菜单
     *
     * @param itemId              事项id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey
     * @param taskId              任务id
     * @param model
     * @return
     */
    DocumentDetailModel menuControl4Todo(String itemId, String processDefinitionId, String taskDefKey, String taskId, DocumentDetailModel model);

    /**
     * Description: 获取菜单
     *
     * @param itemId              事项id
     * @param taskId              任务id
     * @param model
     * @return
     */
    DocumentDetailModel menuControl4Doing(String itemId, String taskId, DocumentDetailModel model);

    /**
     * Description: 获取菜单
     *
     * @param itemId              事项id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey
     * @param model
     * @return
     */
    DocumentDetailModel menuControl4Done(String itemId, String processDefinitionId, String taskDefKey,DocumentDetailModel model);

    /**
     * Description: 获取菜单
     *
     * @param itemId              事项id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey
     * @param model
     * @return
     */
    DocumentDetailModel menuControl4Recycle(String itemId, String processDefinitionId, String taskDefKey,DocumentDetailModel model);


    /**
     * Description: 解析工作流发送时用户选取的人员
     *
     * @param userChoice
     * @return
     */
    List<String> parseUserChoice(String userChoice);

    Y9Result<List<String>> parserUser(String itemId, String processDefinitionId, String routeToTaskId, String routeToTaskName, String processInstanceId, String multiInstance);

    /**
     * 重定位
     *
     * @param taskId     任务id
     * @param userChoice
     * @return
     */
    Y9Result<String> reposition(String taskId, String userChoice);

    /**
     * Description: 启动流程并发送
     *
     * @param itemId               事项id
     * @param processSerialNumber  流程编号
     * @param processDefinitionKey 流程定义key
     * @param userChoice
     * @param sponsorGuid
     * @param routeToTaskId
     * @param variables
     * @return
     */
    Y9Result<String> saveAndForwarding(String itemId, String processSerialNumber, String processDefinitionKey, String userChoice, String sponsorGuid, String routeToTaskId, Map<String, Object> variables);

    /**
     * Description: 启动流程并发送(指定)
     *
     * @param itemId               事项id
     * @param processSerialNumber  流程编号
     * @param processDefinitionKey 流程定义key
     * @param userChoice
     * @param sponsorGuid
     * @param routeToTaskId
     * @param startRouteToTaskId
     * @param variables
     * @return
     */
    Y9Result<String> saveAndForwardingByTaskKey(String itemId, String processSerialNumber, String processDefinitionKey, String userChoice, String sponsorGuid, String routeToTaskId, String startRouteToTaskId, Map<String, Object> variables);

    /**
     * Description: 启动流程并提交
     *
     * @param itemId              事项id
     * @param processSerialNumber 流程编号
     * @return
     */
    Y9Result<Object> saveAndSubmitTo(String itemId, String processSerialNumber);

    /**
     * 获取签收任务配置
     *
     * @param itemId              事项id
     * @param processDefinitionId 流程定义id
     * @param taskDefinitionKey   任务节点key
     * @param processSerialNumber 流程编号
     * @return
     */
    SignTaskConfigModel signTaskConfig(String itemId, String processDefinitionId, String taskDefinitionKey, String processSerialNumber);

    /*
     * 启动流程发送
     *
     * @param taskId
     * @param routeToTaskId
     * @param sponsorGuid
     * @param userList
     * @return
     */
    Y9Result<String> start4Forwarding(String taskId, String routeToTaskId, String sponsorGuid, List<String> userList);

    /**
     * 启动流程，用于当前人启动本租户的流程，启动者是人
     *
     * @param itemId               事项id
     * @param processSerialNumber  流程编号
     * @param processDefinitionKey 流程定义key
     * @return
     */
    StartProcessResultModel startProcess(String itemId, String processSerialNumber, String processDefinitionKey);

    /**
     * 启动流程，用于当前人启动本租户的流程，启动者是人
     *
     * @param itemId               事项id
     * @param processSerialNumber  流程编号
     * @param processDefinitionKey 流程定义key
     * @return
     */
    StartProcessResultModel startProcessByTheTaskKey(String itemId, String processSerialNumber, String processDefinitionKey,String theTaskKey);

    /**
     * 启动流程，多人
     *
     * @param itemId               事项id
     * @param processSerialNumber  流程编号
     * @param processDefinitionKey 流程定义key
     * @param userIds
     * @return
     */
    StartProcessResultModel startProcess(String itemId, String processSerialNumber, String processDefinitionKey, String userIds);

    /**
     * 启动流程，指定任务节点
     *
     * @param itemId               事项id
     * @param processSerialNumber  流程编号
     * @param processDefinitionKey 流程定义key
     * @param startRouteToTaskId
     * @return
     */
    Map<String, Object> startProcessByTaskKey(String itemId, String processSerialNumber, String processDefinitionKey, String startRouteToTaskId);

    /**
     * Description: 启动流程并提交
     *
     * @param processSerialNumber 流程编号
     * @param taskId              任务id
     * @return
     */
    Y9Result<Object> submitTo(String processSerialNumber, String taskId);

}
