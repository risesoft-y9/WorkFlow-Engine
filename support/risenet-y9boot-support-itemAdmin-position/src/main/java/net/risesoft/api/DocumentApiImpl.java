package net.risesoft.api;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.position.Document4PositionApi;
import net.risesoft.api.org.PersonApi;
import net.risesoft.api.org.PositionApi;
import net.risesoft.api.processadmin.VariableApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.model.Person;
import net.risesoft.model.Position;
import net.risesoft.service.DocumentService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 办件操作接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequestMapping(value = "/services/rest/document4Position")
public class DocumentApiImpl implements Document4PositionApi {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private PersonApi personManager;

    @Autowired
    private PositionApi positionManager;

    @Autowired
    private VariableApi variableManager;

    /**
     * 新建
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param itemId 事项id
     * @param mobile 是否手机端
     * @return map
     */
    @Override
    @GetMapping(value = "/add", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> add(String tenantId, String positionId, String itemId, boolean mobile) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionManager.getPosition(tenantId, positionId);
        Y9LoginUserHolder.setPosition(position);
        Map<String, Object> returnMap = new HashMap<>(16);
        returnMap = documentService.add(itemId, mobile, returnMap);
        return returnMap;
    }

    /**
     * 办件办结
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @throws Exception Exception
     */
    @Override
    @PostMapping(value = "/complete", produces = MediaType.APPLICATION_JSON_VALUE)
    public void complete(String tenantId, String positionId, String taskId) throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionManager.getPosition(tenantId, positionId);
        Y9LoginUserHolder.setPosition(position);
        documentService.complete(taskId);
    }

    /**
     * 获取发送选人信息
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param itemId 事项id
     * @param processDefinitionKey 流程定义key
     * @param processDefinitionId 流程定义Id
     * @param taskId 任务id
     * @param routeToTask 任务key
     * @param processInstanceId 流程实例id
     * @return Map
     */
    @Override
    @GetMapping(value = "/docUserChoise", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> docUserChoise(String tenantId, String userId, String positionId, String itemId,
        String processDefinitionKey, String processDefinitionId, String taskId, String routeToTask,
        String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId);
        Y9LoginUserHolder.setPerson(person);
        Position position = positionManager.getPosition(tenantId, positionId);
        Y9LoginUserHolder.setPosition(position);
        Map<String, Object> returnMap = new HashMap<>(16);
        returnMap = documentService.docUserChoise(itemId, processDefinitionKey, processDefinitionId, taskId,
            routeToTask, processInstanceId);
        return returnMap;
    }

    /**
     * 编辑文档
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param itembox 办件状态，todo（待办），doing（在办），done（办结）
     * @param taskId 任务id
     * @param processInstanceId 流程实例id
     * @param itemId 事项id
     * @param mobile 是否手机端
     * @return Map
     */
    @Override
    @GetMapping(value = "/edit", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> edit(String tenantId, String positionId, String itembox, String taskId,
        String processInstanceId, String itemId, boolean mobile) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionManager.getPosition(tenantId, positionId);
        Y9LoginUserHolder.setPosition(position);
        Map<String, Object> returnMap = new HashMap<>(16);
        returnMap = documentService.edit(itembox, taskId, processInstanceId, itemId, mobile);
        return returnMap;
    }

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
    @Override
    @PostMapping(value = "/saveAndForwarding", produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> saveAndForwarding(String tenantId, String positionId, String processInstanceId,
        String taskId, String sponsorHandle, String itemId, String processSerialNumber, String processDefinitionKey,
        String userChoice, String sponsorGuid, String routeToTaskId, @RequestBody Map<String, Object> variables) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionManager.getPosition(tenantId, positionId);
        Y9LoginUserHolder.setPosition(position);
        Map<String, Object> map = new HashMap<String, Object>(16);
        if (StringUtils.isBlank(processInstanceId) || UtilConsts.NULL.equals(processInstanceId)) {
            map = documentService.saveAndForwarding(itemId, processSerialNumber, processDefinitionKey, userChoice,
                sponsorGuid, routeToTaskId, variables);
        } else {
            variableManager.setVariables(tenantId, taskId, variables);
            map = documentService.forwarding(taskId, sponsorHandle, userChoice, routeToTaskId, sponsorGuid);
        }
        return map;
    }

    /**
     * 指定任务节点发送
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
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
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @PostMapping(value = "/saveAndForwardingByTaskKey", produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> saveAndForwardingByTaskKey(String tenantId, String positionId, String processInstanceId,
        String taskId, String sponsorHandle, String itemId, String processSerialNumber, String processDefinitionKey,
        String userChoice, String sponsorGuid, String routeToTaskId, String startRouteToTaskId,
        @RequestBody Map<String, Object> variables) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionManager.getPosition(tenantId, positionId);
        Y9LoginUserHolder.setPosition(position);
        Map<String, Object> map = new HashMap<String, Object>(16);
        if (StringUtils.isBlank(processInstanceId) || UtilConsts.NULL.equals(processInstanceId)) {
            map = documentService.saveAndForwardingByTaskKey(itemId, processSerialNumber, processDefinitionKey,
                userChoice, sponsorGuid, routeToTaskId, startRouteToTaskId, variables);
        } else {
            if (!variables.isEmpty()) {
                variableManager.setVariables(tenantId, taskId, variables);
            }
            map = documentService.forwarding(taskId, sponsorHandle, userChoice, routeToTaskId, sponsorGuid);
        }
        return map;
    }

    /**
     * 获取签收任务配置
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @param taskDefinitionKey 任务key
     * @param processSerialNumber 流程编号
     * @return Map<String, Object>
     */
    @Override
    @GetMapping(value = "/signTaskConfig", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> signTaskConfig(String tenantId, String positionId, String itemId,
        String processDefinitionId, String taskDefinitionKey, String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionManager.getPosition(tenantId, positionId);
        Y9LoginUserHolder.setPosition(position);
        Map<String, Object> map =
            documentService.signTaskConfig(itemId, processDefinitionId, taskDefinitionKey, processSerialNumber);
        return map;
    }

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
    @Override
    @PostMapping(value = "/startProcess", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> startProcess(String tenantId, String positionId, String itemId,
        String processSerialNumber, String processDefinitionKey) throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionManager.getPosition(tenantId, positionId);
        Y9LoginUserHolder.setPosition(position);
        Map<String, Object> map = documentService.startProcess(itemId, processSerialNumber, processDefinitionKey);
        return map;
    }

    /**
     * 启动流程，多人
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param itemId 事项id
     * @param processSerialNumber 流程编号
     * @param processDefinitionKey 流程定义key
     * @param positionIds 岗位ids
     * @return Map<String, Object>
     * @throws Exception
     */
    @Override
    @PostMapping(value = "/startProcess1", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> startProcess(String tenantId, String positionId, String itemId,
        String processSerialNumber, String processDefinitionKey, String positionIds) throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionManager.getPosition(tenantId, positionId);
        Y9LoginUserHolder.setPosition(position);
        Map<String, Object> map =
            documentService.startProcess(itemId, processSerialNumber, processDefinitionKey, positionIds);
        return map;
    }
}
