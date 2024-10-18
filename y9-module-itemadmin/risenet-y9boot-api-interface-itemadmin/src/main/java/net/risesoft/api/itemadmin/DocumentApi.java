package net.risesoft.api.itemadmin;

import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.DocUserChoiseModel;
import net.risesoft.model.itemadmin.ItemStartNodeRoleModel;
import net.risesoft.model.itemadmin.OpenDataModel;
import net.risesoft.model.itemadmin.SignTaskConfigModel;
import net.risesoft.model.itemadmin.StartProcessResultModel;
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
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param itemId 事项id
     * @param mobile 是否手机端
     * @return {@code Y9Result<OpenDataModel>} 通用请求返回对象 - data是流程详情
     * @since 9.6.6
     */
    @GetMapping("/add")
    Y9Result<OpenDataModel> add(@RequestParam("tenantId") String tenantId, @RequestParam("orgUnitId") String orgUnitId,
        @RequestParam("itemId") String itemId, @RequestParam("mobile") boolean mobile);

    /**
     * 新建
     * 用于一个开始节点经过排他网关到达多个任务节点的情况，具体到达哪个任务节点开始，需要由用户选择
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param itemId 事项id
     * @param startTaskDefKey 开始任务节点
     * @param mobile 是否手机端
     * @return {@code Y9Result<OpenDataModel>} 通用请求返回对象 - data是流程详情
     * @since 9.6.8
     */
    @GetMapping("/addWithStartTaskDefKey")
    Y9Result<OpenDataModel> addWithStartTaskDefKey(@RequestParam("tenantId") String tenantId, @RequestParam("orgUnitId") String orgUnitId,
                                @RequestParam("itemId") String itemId,@RequestParam("startTaskDefKey") String startTaskDefKey, @RequestParam("mobile") boolean mobile);

    /**
     * 办件办结
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param taskId 任务id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @throws Exception Exception
     * @since 9.6.6
     */
    @PostMapping("/complete")
    Y9Result<Object> complete(@RequestParam("tenantId") String tenantId, @RequestParam("orgUnitId") String orgUnitId,
        @RequestParam("taskId") String taskId) throws Exception;

    /**
     * 获取发送选人信息
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param orgUnitId 人员、岗位id
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
    Y9Result<DocUserChoiseModel> docUserChoise(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("orgUnitId") String orgUnitId,
        @RequestParam("itemId") String itemId, @RequestParam("processDefinitionKey") String processDefinitionKey,
        @RequestParam("processDefinitionId") String processDefinitionId,
        @RequestParam(value = "taskId", required = false) String taskId,
        @RequestParam("routeToTask") String routeToTask,
        @RequestParam(value = "processInstanceId", required = false) String processInstanceId);

    /**
     * 编辑文档
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param itembox 办件状态，todo（待办），doing（在办），done（办结）
     * @param taskId 任务id
     * @param processInstanceId 流程实例id
     * @param itemId 事项id
     * @param mobile 是否手机端
     * @return {@code Y9Result<OpenDataModel>} 通用请求返回对象 - data是流程详情数据
     * @since 9.6.6
     */
    @GetMapping("/edit")
    Y9Result<OpenDataModel> edit(@RequestParam("tenantId") String tenantId, @RequestParam("orgUnitId") String orgUnitId,
        @RequestParam("itembox") String itembox, @RequestParam(value = "taskId", required = false) String taskId,
        @RequestParam("processInstanceId") String processInstanceId, @RequestParam("itemId") String itemId,
        @RequestParam("mobile") boolean mobile);

    /**
     * 解析用户
     * 
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
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
    Y9Result<List<String>> parserUser(@RequestParam("tenantId") String tenantId,
        @RequestParam("orgUnitId") String orgUnitId, @RequestParam("itemId") String itemId,
        @RequestParam("processDefinitionId") String processDefinitionId,
        @RequestParam("routeToTaskId") String routeToTaskId,
        @RequestParam(value = "taskDefName", required = false) String taskDefName,
        @RequestParam(value = "processInstanceId", required = false) String processInstanceId,
        @RequestParam(value = "multiInstance", required = false) String multiInstance);

    /**
     * 带自定义变量发送
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位 id
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
     * @return {@code Y9Result<String>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping(value = "/saveAndForwarding", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<String> saveAndForwarding(@RequestParam("tenantId") String tenantId,
        @RequestParam("orgUnitId") String orgUnitId,
        @RequestParam(value = "processInstanceId", required = false) String processInstanceId,
        @RequestParam(value = "taskId", required = false) String taskId,
        @RequestParam(value = "sponsorHandle", required = false) String sponsorHandle,
        @RequestParam("itemId") String itemId, @RequestParam("processSerialNumber") String processSerialNumber,
        @RequestParam("processDefinitionKey") String processDefinitionKey,
        @RequestParam("userChoice") String userChoice,
        @RequestParam(value = "sponsorGuid", required = false) String sponsorGuid,
        @RequestParam("routeToTaskId") String routeToTaskId, @RequestBody Map<String, Object> variables);

    /**
     * 指定任务节点发送
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
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
    Y9Result<String> saveAndForwardingByTaskKey(@RequestParam("tenantId") String tenantId,
        @RequestParam("orgUnitId") String orgUnitId,
        @RequestParam(value = "processInstanceId", required = false) String processInstanceId,
        @RequestParam(value = "taskId", required = false) String taskId,
        @RequestParam(value = "sponsorHandle", required = false) String sponsorHandle,
        @RequestParam("itemId") String itemId, @RequestParam("processSerialNumber") String processSerialNumber,
        @RequestParam("processDefinitionKey") String processDefinitionKey,
        @RequestParam("userChoice") String userChoice,
        @RequestParam(value = "sponsorGuid", required = false) String sponsorGuid,
        @RequestParam("routeToTaskId") String routeToTaskId,
        @RequestParam("startRouteToTaskId") String startRouteToTaskId, @RequestBody Map<String, Object> variables);

    /**
     * 带自定义变量发送
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位 id
     * @param taskId 任务id
     * @param itemId 事项id
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping(value = "/saveAndSubmitTo", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Object> saveAndSubmitTo(@RequestParam("tenantId") String tenantId,
        @RequestParam("orgUnitId") String orgUnitId, @RequestParam(value = "taskId", required = false) String taskId,
        @RequestParam("itemId") String itemId, @RequestParam("processSerialNumber") String processSerialNumber);

    /**
     * 获取签收任务配置
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @param taskDefinitionKey 任务key
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<SignTaskConfigModel>} 通用请求返回对象 - data是签收任务配置
     * @since 9.6.6
     */
    @GetMapping("/signTaskConfig")
    Y9Result<SignTaskConfigModel> signTaskConfig(@RequestParam("tenantId") String tenantId,
        @RequestParam("orgUnitId") String orgUnitId, @RequestParam("itemId") String itemId,
        @RequestParam("processDefinitionId") String processDefinitionId,
        @RequestParam("taskDefinitionKey") String taskDefinitionKey,
        @RequestParam("processSerialNumber") String processSerialNumber);

    /**
     * 获取签收任务配置
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param itemId 事项id
     * @return {@code Y9Result<List<ItemStartNodeRoleModel>>} 通用请求返回对象 - data是启动任务节点
     * @since 9.6.6
     */
    @GetMapping("/getAllStartTaskDefKey")
    Y9Result<List<ItemStartNodeRoleModel>> getAllStartTaskDefKey(@RequestParam("tenantId") String tenantId,
                                                           @RequestParam("orgUnitId") String orgUnitId, @RequestParam("itemId") String itemId);

    /**
     * 启动流程
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param itemId 事项id
     * @param processSerialNumber 流程编号
     * @param processDefinitionKey 流程定义key
     * @return {@code Y9Result<StartProcessResultModel>} 通用请求返回对象 - data是启动流程返回信息
     * @throws Exception Exception
     * @since 9.6.6
     */
    @PostMapping("/startProcess")
    Y9Result<StartProcessResultModel> startProcess(@RequestParam("tenantId") String tenantId,
        @RequestParam("orgUnitId") String orgUnitId, @RequestParam("itemId") String itemId,
        @RequestParam("processSerialNumber") String processSerialNumber,
        @RequestParam("processDefinitionKey") String processDefinitionKey) throws Exception;

    /**
     * 启动流程，多人
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param itemId 事项id
     * @param processSerialNumber 流程编号
     * @param processDefinitionKey 流程定义key
     * @param userIds 人员、岗位ids
     * @return {@code Y9Result<StartProcessResultModel>} 通用请求返回对象 - data是启动流程返回信息
     * @throws Exception Exception
     * @since 9.6.6
     */
    @PostMapping("/startProcess1")
    Y9Result<StartProcessResultModel> startProcess(@RequestParam("tenantId") String tenantId,
        @RequestParam("orgUnitId") String orgUnitId, @RequestParam("itemId") String itemId,
        @RequestParam("processSerialNumber") String processSerialNumber,
        @RequestParam("processDefinitionKey") String processDefinitionKey, @RequestParam("userIds") String userIds)
        throws Exception;

}
