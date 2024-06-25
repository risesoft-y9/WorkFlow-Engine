package net.risesoft.api.itemadmin.position;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.DocUserChoiseModel;
import net.risesoft.model.itemadmin.OpenDataModel;
import net.risesoft.model.itemadmin.SignTaskConfigModel;
import net.risesoft.model.itemadmin.StartProcessResultModel;
import net.risesoft.pojo.Y9Result;

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
     * @return Y9Result<OpenDataModel>
     */
    @GetMapping("/add")
    Y9Result<OpenDataModel> add(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("itemId") String itemId,
        @RequestParam("mobile") boolean mobile);

    /**
     * 流程办结
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @return Y9Result<Object>
     * @throws Exception Exception
     */
    @PostMapping("/complete")
    Y9Result<Object> complete(@RequestParam("tenantId") String tenantId, @RequestParam("positionId") String positionId,
        @RequestParam("taskId") String taskId) throws Exception;

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
     * @return Y9Result<DocUserChoiseModel>
     */
    @GetMapping("/docUserChoise")
    Y9Result<DocUserChoiseModel> docUserChoise(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("positionId") String positionId,
        @RequestParam("itemId") String itemId, @RequestParam("processDefinitionKey") String processDefinitionKey,
        @RequestParam("processDefinitionId") String processDefinitionId, @RequestParam("taskId") String taskId,
        @RequestParam("routeToTask") String routeToTask, @RequestParam("processInstanceId") String processInstanceId);

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
     * @return Y9Result<OpenDataModel>
     */
    @GetMapping("/edit")
    Y9Result<OpenDataModel> edit(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("itembox") String itembox,
        @RequestParam("taskId") String taskId, @RequestParam("processInstanceId") String processInstanceId,
        @RequestParam("itemId") String itemId, @RequestParam("mobile") boolean mobile);

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
     * @return Y9Result<String>
     */
    @PostMapping(value = "/saveAndForwarding", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<String> saveAndForwarding(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("processInstanceId") String processInstanceId,
        @RequestParam("taskId") String taskId, @RequestParam("sponsorHandle") String sponsorHandle,
        @RequestParam("itemId") String itemId, @RequestParam("processSerialNumber") String processSerialNumber,
        @RequestParam("processDefinitionKey") String processDefinitionKey,
        @RequestParam("userChoice") String userChoice, @RequestParam("sponsorGuid") String sponsorGuid,
        @RequestParam("routeToTaskId") String routeToTaskId, @RequestBody Map<String, Object> variables);

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
     * @return Y9Result<String>
     */
    @PostMapping(value = "/saveAndForwardingByTaskKey", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<String> saveAndForwardingByTaskKey(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("processInstanceId") String processInstanceId,
        @RequestParam("taskId") String taskId, @RequestParam("sponsorHandle") String sponsorHandle,
        @RequestParam("itemId") String itemId, @RequestParam("processSerialNumber") String processSerialNumber,
        @RequestParam("processDefinitionKey") String processDefinitionKey,
        @RequestParam("userChoice") String userChoice, @RequestParam("sponsorGuid") String sponsorGuid,
        @RequestParam("routeToTaskId") String routeToTaskId,
        @RequestParam("startRouteToTaskId") String startRouteToTaskId, @RequestBody Map<String, Object> variables);

    /**
     * 带自定义变量发送
     *
     * @param tenantId 租户id
     * @param positionId 岗位 id
     * @param taskId 任务id
     * @param itemId 事项id
     * @param processSerialNumber 流程编号
     * @return Y9Result<Object>
     */
    @PostMapping(value = "/saveAndSubmitTo", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Object> saveAndSubmitTo(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("taskId") String taskId,
        @RequestParam("itemId") String itemId, @RequestParam("processSerialNumber") String processSerialNumber);

    /**
     * 获取签收任务配置
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @param taskDefinitionKey 任务定义key
     * @param processSerialNumber 流程编号
     * @return Y9Result<SignTaskConfigModel>
     */
    @GetMapping("/signTaskConfig")
    Y9Result<SignTaskConfigModel> signTaskConfig(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("itemId") String itemId,
        @RequestParam("processDefinitionId") String processDefinitionId,
        @RequestParam("taskDefinitionKey") String taskDefinitionKey,
        @RequestParam("processSerialNumber") String processSerialNumber);

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
    @PostMapping("/startProcess")
    Y9Result<StartProcessResultModel> startProcess(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("itemId") String itemId,
        @RequestParam("processSerialNumber") String processSerialNumber,
        @RequestParam("processDefinitionKey") String processDefinitionKey) throws Exception;

    /**
     * 启动流程，多人
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param itemId 事项id
     * @param processSerialNumber 流程编号
     * @param processDefinitionKey 流程定义key
     * @param positionIds 岗位ids
     * @return Y9Result<StartProcessResultModel>
     * @throws Exception Exception
     */
    @PostMapping("/startProcess1")
    Y9Result<StartProcessResultModel> startProcess(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("itemId") String itemId,
        @RequestParam("processSerialNumber") String processSerialNumber,
        @RequestParam("processDefinitionKey") String processDefinitionKey,
        @RequestParam("positionIds") String positionIds) throws Exception;

}
