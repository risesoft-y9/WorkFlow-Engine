package net.risesoft.api.itemadmin.core;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.dto.itemadmin.ForwardingDTO;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.model.itemadmin.DocUserChoiseModel;
import net.risesoft.model.itemadmin.ItemButtonModel;
import net.risesoft.model.itemadmin.ItemStartNodeRoleModel;
import net.risesoft.model.itemadmin.OpenDataModel;
import net.risesoft.model.itemadmin.SignTaskConfigModel;
import net.risesoft.model.itemadmin.StartProcessResultModel;
import net.risesoft.model.itemadmin.core.DocumentDetailModel;
import net.risesoft.pojo.Y9Result;

/**
 * 办件操作接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface DocumentApi {

    /**
     * 新建
     *
     * @param itemId 事项id
     * @return {@code Y9Result<OpenDataModel>} 通用请求返回对象 - data是流程详情
     * @since 9.6.6
     */
    @GetMapping("/add")
    Y9Result<DocumentDetailModel> add(@RequestParam String itemId);

    /**
     * 新建
     *
     * @param itemId 事项id
     * @param mobile 是否手机端
     * @return {@code Y9Result<OpenDataModel>} 通用请求返回对象 - data是流程详情
     * @since 9.6.6
     */
    @GetMapping("/add4Old")
    Y9Result<OpenDataModel> add4Old(@RequestParam String itemId, @RequestParam boolean mobile);

    /**
     * 新建 用于一个开始节点经过排他网关到达多个任务节点的情况，具体到达哪个任务节点开始，需要由用户选择
     *
     * @param itemId 事项id
     * @param startTaskDefKey 开始任务节点
     * @param mobile 是否手机端
     * @return {@code Y9Result<OpenDataModel>} 通用请求返回对象 - data是流程详情
     * @since 9.6.8
     */
    @GetMapping("/addWithStartTaskDefKey")
    Y9Result<DocumentDetailModel> addWithStartTaskDefKey(@RequestParam String itemId,
        @RequestParam String startTaskDefKey, @RequestParam boolean mobile);

    /**
     * 办件办结
     *
     * @param taskId 任务id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @throws Exception Exception
     * @since 9.6.6
     */
    @PostMapping("/complete")
    Y9Result<Object> complete(@RequestParam String taskId) throws Exception;

    /**
     * 办件办结
     *
     * @param taskId 任务id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @throws Exception Exception
     * @since 9.6.6
     */
    @PostMapping("/completeSub")
    Y9Result<Object> completeSub(@RequestParam String taskId, @RequestParam List<String> userList) throws Exception;

    /**
     * 获取发送选人信息
     *
     * @param itemId 事项id
     * @param processDefinitionKey 流程定义key
     * @param processDefinitionId 流程定义Id
     * @param taskId 任务id
     * @param routeToTask 任务key
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<DocUserChoiseModel>} 通用请求返回对象 - data是发送选人信息
     * @since 9.6.6
     */
    @GetMapping("/docUserChoise")
    Y9Result<DocUserChoiseModel> docUserChoise(@RequestParam String itemId, @RequestParam String processDefinitionKey,
        @RequestParam String processDefinitionId, @RequestParam(required = false) String taskId,
        @RequestParam String routeToTask, @RequestParam(required = false) String processInstanceId);

    /**
     * 编辑文档
     *
     * @param itembox 办件状态，todo（待办），doing（在办），done（办结）
     * @param taskId 任务id
     * @param processInstanceId 流程实例id
     * @param itemId 事项id
     * @param mobile 是否手机端
     * @return {@code Y9Result<OpenDataModel>} 通用请求返回对象 - data是流程详情数据
     * @since 9.6.6
     */
    @GetMapping("/edit")
    Y9Result<OpenDataModel> edit(@RequestParam String itembox, @RequestParam(required = false) String taskId,
        @RequestParam String processInstanceId, @RequestParam String itemId, @RequestParam boolean mobile);

    /**
     * 获取抄送件详情
     *
     * @param id 抄送id
     * @param processInstanceId 抄送的流程实例id
     * @param mobile 是否为移动端
     * @return {@code Y9Result<OpenDataModel>} 通用请求返回对象 - data是送件对象
     * @since 9.6.6
     */
    @GetMapping("/editChaoSong")
    Y9Result<DocumentDetailModel> editChaoSong(@RequestParam String id, @RequestParam String processInstanceId,
        @RequestParam boolean mobile, @RequestParam String itembox);

    /**
     * 编辑文档
     *
     * @param processInstanceId 流程实例id
     * @param documentId 文档id
     * @return {@code Y9Result<OpenDataModel>} 通用请求返回对象 - data是流程详情数据
     * @since 9.6.6
     */
    @GetMapping("/editDoing")
    Y9Result<DocumentDetailModel> editDoing(@RequestParam String processInstanceId, @RequestParam String documentId,
        @RequestParam boolean isAdmin, @RequestParam ItemBoxTypeEnum itemBox, @RequestParam boolean mobile);

    /**
     * 编辑文档
     *
     * @param processInstanceId 流程实例id
     * @param documentId 是否手机端
     * @return {@code Y9Result<OpenDataModel>} 通用请求返回对象 - data是流程详情数据
     * @since 9.6.6
     */
    @GetMapping("/editDone")
    Y9Result<DocumentDetailModel> editDone(@RequestParam String processInstanceId, @RequestParam String documentId,
        @RequestParam boolean isAdmin, @RequestParam ItemBoxTypeEnum itemBox, @RequestParam boolean mobile);

    /**
     * 编辑草稿
     *
     * @param itemId 事项id
     * @param processSerialNumber 流程编号
     * @param mobile 是否手机端
     * @return {@code Y9Result<OpenDataModel>} 通用请求返回对象 - data 是流程详情数据
     * @since 9.6.6
     */
    @GetMapping("/editDraft")
    Y9Result<DocumentDetailModel> editDraft(@RequestParam String itemId, @RequestParam String processSerialNumber,
        @RequestParam boolean mobile);

    /**
     * 编辑文档
     *
     * @param processInstanceId 流程实例id
     * @param mobile 是否手机端
     * @return {@code Y9Result<OpenDataModel>} 通用请求返回对象 - data是流程详情数据
     * @since 9.6.6
     */
    @GetMapping("/editRecycle")
    Y9Result<DocumentDetailModel> editRecycle(@RequestParam String processInstanceId, @RequestParam boolean mobile);

    /**
     * 编辑文档
     *
     * @param taskId 任务id
     * @param mobile 是否手机端
     * @return {@code Y9Result<OpenDataModel>} 通用请求返回对象 - data是流程详情数据
     * @since 9.6.6
     */
    @GetMapping("/editTodo")
    Y9Result<DocumentDetailModel> editTodo(@RequestParam(required = false) String taskId, @RequestParam boolean mobile);

    /**
     * 带自定义变量发送
     *
     * @param taskId 任务id
     * @param sponsorHandle 是否主办人办理
     * @param userChoice 选择的发送人员
     * @param sponsorGuid 主办人id
     * @param routeToTaskId 任务key
     * @return {@code Y9Result<String>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping(value = "/forwarding", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<String> forwarding(@RequestParam String taskId, @RequestParam String userChoice,
        @RequestParam String routeToTaskId, @RequestParam(required = false) String sponsorHandle,
        @RequestParam(required = false) String sponsorGuid);

    /**
     * 获取签收任务配置
     *
     * @param itemId 事项id
     * @return {@code Y9Result<List<ItemStartNodeRoleModel>>} 通用请求返回对象 - data是启动任务节点
     * @since 9.6.6
     */
    @GetMapping("/getAllStartTaskDefKey")
    Y9Result<List<ItemStartNodeRoleModel>> getAllStartTaskDefKey(@RequestParam String itemId);

    /**
     * 获取按钮
     *
     * @param taskId 任务id
     * @return {@code Y9Result<List<ItemButtonModel>>} 通用请求返回对象 - data是按钮集合
     * @since 9.6.8
     */
    @GetMapping("/getButtons")
    Y9Result<List<ItemButtonModel>> getButtons(@RequestParam(required = false) String taskId);

    /**
     * 解析用户
     *
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @param routeToTaskId 任务key
     * @param taskDefName 任务名称
     * @param processInstanceId 流程实例id
     * @param multiInstance 是否多实例
     * @return {@code Y9Result<List<String>>} 通用请求返回对象
     * @since 9.6.6
     */
    @GetMapping("/parserUser")
    Y9Result<List<String>> parserUser(@RequestParam String itemId, @RequestParam String processDefinitionId,
        @RequestParam String routeToTaskId, @RequestParam(required = false) String taskDefName,
        @RequestParam(required = false) String processInstanceId, @RequestParam(required = false) String multiInstance);

    /**
     * 带自定义变量发送
     *
     * @param forwardingDTO 带自定义变量发送
     * @return {@code Y9Result<String>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping(value = "/saveAndForwarding", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<String> saveAndForwarding(@RequestBody @Valid ForwardingDTO forwardingDTO);

    /**
     * 指定任务节点发送
     *
     * @param processInstanceId 流程实例id
     * @param taskId 任务id
     * @param sponsorHandle 是否主办人办理
     * @param itemId 事项id
     * @param processSerialNumber 流程编号
     * @param processDefinitionKey 流程定义key
     * @param userChoice 选择的发送人员
     * @param sponsorGuid 主办人id
     * @param routeToTaskId 任务key
     * @param startRouteToTaskId 启动节点key
     * @param variables 保存变量
     * @return {@code Y9Result<String>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping(value = "/saveAndForwardingByTaskKey", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<String> saveAndForwardingByTaskKey(@RequestParam(required = false) String processInstanceId,
        @RequestParam(required = false) String taskId, @RequestParam(required = false) String sponsorHandle,
        @RequestParam String itemId, @RequestParam String processSerialNumber,
        @RequestParam String processDefinitionKey, @RequestParam String userChoice,
        @RequestParam(required = false) String sponsorGuid, @RequestParam String routeToTaskId,
        @RequestParam String startRouteToTaskId, @RequestBody Map<String, Object> variables);

    /**
     * 带自定义变量发送
     *
     * @param taskId 任务id
     * @param itemId 事项id
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping(value = "/saveAndSubmitTo", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Object> saveAndSubmitTo(@RequestParam(required = false) String taskId, @RequestParam String itemId,
        @RequestParam String processSerialNumber);

    /**
     * 获取签收任务配置
     *
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @param taskDefinitionKey 任务key
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<SignTaskConfigModel>} 通用请求返回对象 - data是签收任务配置
     * @since 9.6.6
     */
    @GetMapping("/signTaskConfig")
    Y9Result<SignTaskConfigModel> signTaskConfig(@RequestParam String itemId, @RequestParam String processDefinitionId,
        @RequestParam String taskDefinitionKey, @RequestParam String processSerialNumber);

    /**
     * 启动流程
     *
     * @param itemId 事项id
     * @param processSerialNumber 流程编号
     * @param processDefinitionKey 流程定义key
     * @return {@code Y9Result<StartProcessResultModel>} 通用请求返回对象 - data是启动流程返回信息
     * @throws Exception Exception
     * @since 9.6.6
     */
    @PostMapping("/startProcess")
    Y9Result<StartProcessResultModel> startProcess(@RequestParam String itemId,
        @RequestParam String processSerialNumber, @RequestParam String processDefinitionKey) throws Exception;

    /**
     * 启动流程，多人
     *
     * @param itemId 事项id
     * @param processSerialNumber 流程编号
     * @param processDefinitionKey 流程定义key
     * @param userIds 人员、岗位ids
     * @return {@code Y9Result<StartProcessResultModel>} 通用请求返回对象 - data是启动流程返回信息
     * @throws Exception Exception
     * @since 9.6.6
     */
    @PostMapping("/startProcess1")
    Y9Result<StartProcessResultModel> startProcess(@RequestParam String itemId,
        @RequestParam String processSerialNumber, @RequestParam String processDefinitionKey,
        @RequestParam String userIds) throws Exception;

    /**
     * 启动流程
     *
     * @param itemId 事项id
     * @param processSerialNumber 流程编号
     * @param processDefinitionKey 流程定义key
     * @return {@code Y9Result<StartProcessResultModel>} 通用请求返回对象 - data是启动流程返回信息
     * @throws Exception Exception
     * @since 9.6.6
     */
    @PostMapping("/startProcessByTaskKey")
    Y9Result<StartProcessResultModel> startProcessByTheTaskKey(@RequestParam String itemId,
        @RequestParam String processSerialNumber, @RequestParam String processDefinitionKey,
        @RequestParam(required = false) String startTaskDefKey, @RequestBody List<String> startOrgUnitIdList)
        throws Exception;

}
