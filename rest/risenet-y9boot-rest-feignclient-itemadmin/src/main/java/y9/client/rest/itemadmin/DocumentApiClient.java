package y9.client.rest.itemadmin;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.itemadmin.DocumentApi;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "DocumentApiClient", name = "${y9.service.itemAdmin.name:itemAdmin}", url = "${y9.service.itemAdmin.directUrl:}",
    path = "/${y9.service.itemAdmin.name:itemAdmin}/services/rest/document")
public interface DocumentApiClient extends DocumentApi {

    /**
     * 新建 Description:
     *
     * @param tenantId
     * @param userId
     * @param itemId
     * @param mobile
     * @return
     */
    @Override
    @GetMapping("/add")
    public Map<String, Object> add(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("itemId") String itemId, @RequestParam("mobile") boolean mobile);

    /**
     * 流程办结
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param taskId 任务id
     * @throws Exception Exception
     */
    @Override
    @PostMapping("/complete")
    public void complete(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("taskId") String taskId) throws Exception;

    /**
     *
     * Description: 获取发送选人信息
     *
     * @param tenantId
     * @param userId
     * @param itemId
     * @param processDefinitionKey
     * @param processDefinitionId
     * @param taskId
     * @param routeToTask
     * @param processInstanceId
     * @return
     */
    @Override
    @GetMapping("/docUserChoise")
    public Map<String, Object> docUserChoise(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("itemId") String itemId,
        @RequestParam("processDefinitionKey") String processDefinitionKey,
        @RequestParam("processDefinitionId") String processDefinitionId, @RequestParam("taskId") String taskId,
        @RequestParam("routeToTask") String routeToTask, @RequestParam("processInstanceId") String processInstanceId);

    /**
     *
     * Description: 编辑文档
     *
     * @param tenantId
     * @param userId
     * @param itembox
     * @param taskId
     * @param processInstanceId
     * @param itemId
     * @param mobile
     * @return
     */
    @Override
    @GetMapping("/edit")
    public Map<String, Object> edit(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("itembox") String itembox, @RequestParam("taskId") String taskId,
        @RequestParam("processInstanceId") String processInstanceId, @RequestParam("itemId") String itemId,
        @RequestParam("mobile") boolean mobile);

    /**
     *
     * Description:
     *
     * @param tenantId
     * @param userId
     * @param taskId
     * @param userChoice
     * @param routeToTaskId
     * @param variables
     * @return
     */
    @Override
    @PostMapping(value = "/forwardingSendReceive", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> forwardingSendReceive(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("taskId") String taskId,
        @RequestParam("userChoice") String userChoice, @RequestParam("routeToTaskId") String routeToTaskId,
        @RequestBody Map<String, Object> variables);

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
    @Override
    @PostMapping(value = "/saveAndForwarding", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> saveAndForwarding(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("processInstanceId") String processInstanceId,
        @RequestParam("taskId") String taskId, @RequestParam("sponsorHandle") String sponsorHandle,
        @RequestParam("itemId") String itemId, @RequestParam("processSerialNumber") String processSerialNumber,
        @RequestParam("processDefinitionKey") String processDefinitionKey,
        @RequestParam("userChoice") String userChoice, @RequestParam("sponsorGuid") String sponsorGuid,
        @RequestParam("routeToTaskId") String routeToTaskId, @RequestBody Map<String, Object> variables);

    /**
     *
     * Description: 指定任务节点发送
     *
     * @param tenantId
     * @param userId
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
    @Override
    @PostMapping(value = "/saveAndForwardingByTaskKey", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> saveAndForwardingByTaskKey(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("processInstanceId") String processInstanceId,
        @RequestParam("taskId") String taskId, @RequestParam("sponsorHandle") String sponsorHandle,
        @RequestParam("itemId") String itemId, @RequestParam("processSerialNumber") String processSerialNumber,
        @RequestParam("processDefinitionKey") String processDefinitionKey,
        @RequestParam("userChoice") String userChoice, @RequestParam("sponsorGuid") String sponsorGuid,
        @RequestParam("routeToTaskId") String routeToTaskId,
        @RequestParam("startRouteToTaskId") String startRouteToTaskId, @RequestBody Map<String, Object> variables);

    /**
     * 获取签收任务配置
     *
     * @param tenantId
     * @param userId
     * @param itemId
     * @param processDefinitionId
     * @param taskDefinitionKey
     * @param processSerialNumber
     * @return
     */
    @Override
    @GetMapping("/signTaskConfig")
    public Map<String, Object> signTaskConfig(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("itemId") String itemId,
        @RequestParam("processDefinitionId") String processDefinitionId,
        @RequestParam("taskDefinitionKey") String taskDefinitionKey,
        @RequestParam("processSerialNumber") String processSerialNumber);

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
    @Override
    @PostMapping("/startProcess")
    public Map<String, Object> startProcess(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("itemId") String itemId,
        @RequestParam("processSerialNumber") String processSerialNumber,
        @RequestParam("processDefinitionKey") String processDefinitionKey) throws Exception;

    /**
     * 启动流程,多人
     *
     * @param tenantId 租户id
     * @param positionId 人员id
     * @param itemId 事项id
     * @param processSerialNumber 流程编号
     * @param processDefinitionKey 流程定义key
     * @param userIds 人员ids
     * @return Map&lt;String, Object&gt;
     * @throws Exception Exception
     */
    @Override
    @PostMapping("/startProcess1")
    public Map<String, Object> startProcess(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("itemId") String itemId,
        @RequestParam("processSerialNumber") String processSerialNumber,
        @RequestParam("processDefinitionKey") String processDefinitionKey, @RequestParam("userIds") String userIds)
        throws Exception;

}
