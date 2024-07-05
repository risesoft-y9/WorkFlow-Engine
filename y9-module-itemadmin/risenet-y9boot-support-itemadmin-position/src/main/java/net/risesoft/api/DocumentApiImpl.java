package net.risesoft.api;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.position.Document4PositionApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.api.processadmin.VariableApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.model.itemadmin.DocUserChoiseModel;
import net.risesoft.model.itemadmin.OpenDataModel;
import net.risesoft.model.itemadmin.SignTaskConfigModel;
import net.risesoft.model.itemadmin.StartProcessResultModel;
import net.risesoft.model.platform.Person;
import net.risesoft.model.platform.Position;
import net.risesoft.pojo.Y9Result;
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
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/document4Position")
public class DocumentApiImpl implements Document4PositionApi {

    private final DocumentService documentService;

    private final PersonApi personManager;

    private final PositionApi positionManager;

    private final VariableApi variableManager;

    /**
     * 新建
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param itemId 事项id
     * @param mobile 是否手机端
     * @return {@code Y9Result<OpenDataModel>} 通用请求返回对象 - data是流程详情
     * @since 9.6.6
     */
    @Override
    public Y9Result<OpenDataModel> add(@RequestParam String tenantId, @RequestParam String positionId,
        @RequestParam String itemId, @RequestParam boolean mobile) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionManager.get(tenantId, positionId).getData();
        Y9LoginUserHolder.setPosition(position);
        OpenDataModel model = documentService.add(itemId, mobile);
        return Y9Result.success(model);
    }

    /**
     * 办件办结
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @throws Exception Exception
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> complete(@RequestParam String tenantId, @RequestParam String positionId,
        @RequestParam String taskId) throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionManager.get(tenantId, positionId).getData();
        Y9LoginUserHolder.setPosition(position);
        documentService.complete(taskId);
        return Y9Result.success();
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
     * @return {@code Y9Result<DocUserChoiseModel>} 通用请求返回对象 - data是发送选人信息
     * @since 9.6.6
     */
    @Override
    public Y9Result<DocUserChoiseModel> docUserChoise(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam String positionId, @RequestParam String itemId, @RequestParam String processDefinitionKey,
        @RequestParam String processDefinitionId, String taskId, @RequestParam String routeToTask,
        String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        Position position = positionManager.get(tenantId, positionId).getData();
        Y9LoginUserHolder.setPosition(position);
        DocUserChoiseModel model = documentService.docUserChoise(itemId, processDefinitionKey, processDefinitionId,
            taskId, routeToTask, processInstanceId);
        return Y9Result.success(model);
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
     * @return {@code Y9Result<OpenDataModel>} 通用请求返回对象 - data是流程详情数据
     * @since 9.6.6
     */
    @Override
    public Y9Result<OpenDataModel> edit(@RequestParam String tenantId, @RequestParam String positionId,
        @RequestParam String itembox, @RequestParam(required = false) String taskId,
        @RequestParam String processInstanceId, @RequestParam String itemId, @RequestParam boolean mobile) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionManager.get(tenantId, positionId).getData();
        Y9LoginUserHolder.setPosition(position);
        OpenDataModel model = documentService.edit(itembox, taskId, processInstanceId, itemId, mobile);
        return Y9Result.success(model);
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
     * @return {@code Y9Result<String>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<String> saveAndForwarding(@RequestParam String tenantId, @RequestParam String positionId,
        String processInstanceId, String taskId, String sponsorHandle, @RequestParam String itemId,
        @RequestParam String processSerialNumber, @RequestParam String processDefinitionKey,
        @RequestParam String userChoice, String sponsorGuid, @RequestParam String routeToTaskId,
        @RequestBody Map<String, Object> variables) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionManager.get(tenantId, positionId).getData();
        Y9LoginUserHolder.setPosition(position);
        if (StringUtils.isBlank(processInstanceId) || UtilConsts.NULL.equals(processInstanceId)) {
            return documentService.saveAndForwarding(itemId, processSerialNumber, processDefinitionKey, userChoice,
                sponsorGuid, routeToTaskId, variables);
        } else {
            variableManager.setVariables(tenantId, taskId, variables);
            return documentService.forwarding(taskId, sponsorHandle, userChoice, routeToTaskId, sponsorGuid);
        }
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
     * @return {@code Y9Result<String>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<String> saveAndForwardingByTaskKey(@RequestParam String tenantId, @RequestParam String positionId,
        String processInstanceId, String taskId, String sponsorHandle, @RequestParam String itemId,
        @RequestParam String processSerialNumber, @RequestParam String processDefinitionKey,
        @RequestParam String userChoice, String sponsorGuid, @RequestParam String routeToTaskId,
        @RequestParam String startRouteToTaskId, @RequestBody Map<String, Object> variables) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionManager.get(tenantId, positionId).getData();
        Y9LoginUserHolder.setPosition(position);
        if (StringUtils.isBlank(processInstanceId) || UtilConsts.NULL.equals(processInstanceId)) {
            return documentService.saveAndForwardingByTaskKey(itemId, processSerialNumber, processDefinitionKey,
                userChoice, sponsorGuid, routeToTaskId, startRouteToTaskId, variables);
        } else {
            if (!variables.isEmpty()) {
                variableManager.setVariables(tenantId, taskId, variables);
            }
            return documentService.forwarding(taskId, sponsorHandle, userChoice, routeToTaskId, sponsorGuid);
        }
    }

    /**
     * 带自定义变量发送
     *
     * @param tenantId 租户id
     * @param positionId 岗位 id
     * @param taskId 任务id
     * @param itemId 事项id
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> saveAndSubmitTo(@RequestParam String tenantId, @RequestParam String positionId,
        String taskId, @RequestParam String itemId, @RequestParam String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionManager.get(tenantId, positionId).getData();
        Y9LoginUserHolder.setPosition(position);
        if (StringUtils.isBlank(taskId) || UtilConsts.NULL.equals(taskId)) {
            return documentService.saveAndSubmitTo(itemId, processSerialNumber);
        } else {
            return documentService.submitTo(processSerialNumber, taskId);
        }
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
     * @return {@code Y9Result<SignTaskConfigModel>} 通用请求返回对象 - data是签收任务配置
     * @since 9.6.6
     */
    @Override
    public Y9Result<SignTaskConfigModel> signTaskConfig(@RequestParam String tenantId, @RequestParam String positionId,
        @RequestParam String itemId, @RequestParam String processDefinitionId, @RequestParam String taskDefinitionKey,
        @RequestParam String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionManager.get(tenantId, positionId).getData();
        Y9LoginUserHolder.setPosition(position);
        SignTaskConfigModel model =
            documentService.signTaskConfig(itemId, processDefinitionId, taskDefinitionKey, processSerialNumber);
        return Y9Result.success(model);
    }

    /**
     * 启动流程
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param itemId 事项id
     * @param processSerialNumber 流程编号
     * @param processDefinitionKey 流程定义key
     * @return {@code Y9Result<StartProcessResultModel>} 通用请求返回对象 - data是启动流程返回信息
     * @throws Exception Exception
     * @since 9.6.6
     */
    @Override
    public Y9Result<StartProcessResultModel> startProcess(@RequestParam String tenantId,
        @RequestParam String positionId, @RequestParam String itemId, @RequestParam String processSerialNumber,
        @RequestParam String processDefinitionKey) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionManager.get(tenantId, positionId).getData();
        Y9LoginUserHolder.setPosition(position);
        StartProcessResultModel model = documentService.startProcess(itemId, processSerialNumber, processDefinitionKey);
        return Y9Result.success(model);
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
     * @return {@code Y9Result<StartProcessResultModel>} 通用请求返回对象 - data是启动流程返回信息
     * @throws Exception Exception
     * @since 9.6.6
     */
    @Override
    public Y9Result<StartProcessResultModel> startProcess(@RequestParam String tenantId,
        @RequestParam String positionId, @RequestParam String itemId, @RequestParam String processSerialNumber,
        @RequestParam String processDefinitionKey, @RequestParam String positionIds) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionManager.get(tenantId, positionId).getData();
        Y9LoginUserHolder.setPosition(position);
        StartProcessResultModel model =
            documentService.startProcess(itemId, processSerialNumber, processDefinitionKey, positionIds);
        return Y9Result.success(model);
    }
}
