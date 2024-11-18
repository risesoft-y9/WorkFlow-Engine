package net.risesoft.api;

import java.util.List;
import java.util.Map;

import net.risesoft.model.itemadmin.DocumentDetailModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.DocumentApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.api.processadmin.VariableApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.ProcessParam;
import net.risesoft.model.itemadmin.DocUserChoiseModel;
import net.risesoft.model.itemadmin.ItemStartNodeRoleModel;
import net.risesoft.model.itemadmin.OpenDataModel;
import net.risesoft.model.itemadmin.SignTaskConfigModel;
import net.risesoft.model.itemadmin.StartProcessResultModel;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Person;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.AsyncUtilService;
import net.risesoft.service.DocumentService;
import net.risesoft.service.ProcessParamService;
import net.risesoft.service.config.ItemStartNodeRoleService;
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
@RequestMapping(value = "/services/rest/document", produces = MediaType.APPLICATION_JSON_VALUE)
public class DocumentApiImpl implements DocumentApi {

    private final DocumentService documentService;

    private final PersonApi personApi;

    private final OrgUnitApi orgUnitApi;

    private final VariableApi variableApi;

    private final AsyncUtilService asyncUtilService;

    private final ProcessParamService processParamService;

    private final ItemStartNodeRoleService itemStartNodeRoleService;

    /**
     * 新建办件
     *
     * @param tenantId  租户id
     * @param orgUnitId 人员、岗位id
     * @param itemId    事项id
     * @param mobile    是否手机端
     * @return {@code Y9Result<OpenDataModel>} 通用请求返回对象 - data是流程详情
     * @since 9.6.6
     */
    @Override
    public Y9Result<OpenDataModel> add(@RequestParam String tenantId, @RequestParam String orgUnitId,
                                       @RequestParam String itemId, @RequestParam boolean mobile) {
        Y9LoginUserHolder.setTenantId(tenantId);
        OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, orgUnitId).getData();
        Y9LoginUserHolder.setOrgUnit(orgUnit);
        OpenDataModel model = documentService.add(itemId, mobile);
        return Y9Result.success(model);
    }

    /**
     * 新建
     * 用于一个开始节点经过排他网关到达多个任务节点的情况，具体到达哪个任务节点开始，需要由用户选择
     *
     * @param tenantId        租户id
     * @param orgUnitId       人员、岗位id
     * @param itemId          事项id
     * @param startTaskDefKey 开始任务节点
     * @param mobile          是否手机端
     * @return {@code Y9Result<OpenDataModel>} 通用请求返回对象 - data是流程详情
     * @since 9.6.8
     */
    @Override
    public Y9Result<DocumentDetailModel> addWithStartTaskDefKey(@RequestParam String tenantId, @RequestParam String orgUnitId,
                                                          @RequestParam String itemId, @RequestParam String startTaskDefKey, @RequestParam boolean mobile) {
        Y9LoginUserHolder.setTenantId(tenantId);
        OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, orgUnitId).getData();
        Y9LoginUserHolder.setOrgUnit(orgUnit);
        DocumentDetailModel model = documentService.addWithStartTaskDefKey(itemId,startTaskDefKey, mobile);
        return Y9Result.success(model);
    }

    /**
     * 办件办结
     *
     * @param tenantId  租户id
     * @param orgUnitId 人员、岗位id
     * @param taskId    任务id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @throws Exception Exception
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> complete(@RequestParam String tenantId, @RequestParam String orgUnitId,
                                     @RequestParam String taskId) throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, orgUnitId).getData();
        Y9LoginUserHolder.setOrgUnit(orgUnit);
        documentService.complete(taskId);
        return Y9Result.success();
    }

    /**
     * 获取发送选人信息
     *
     * @param tenantId             租户id
     * @param userId               人员id
     * @param orgUnitId            人员、岗位id
     * @param itemId               事项id
     * @param processDefinitionKey 流程定义key
     * @param processDefinitionId  流程定义Id
     * @param taskId               任务id
     * @param routeToTask          任务key
     * @param processInstanceId    流程实例id
     * @return {@code Y9Result<DocUserChoiseModel>} 通用请求返回对象 - data是发送选人信息
     * @since 9.6.6
     */
    @Override
    public Y9Result<DocUserChoiseModel> docUserChoise(@RequestParam String tenantId, @RequestParam String userId,
                                                      @RequestParam String orgUnitId, @RequestParam String itemId, @RequestParam String processDefinitionKey,
                                                      @RequestParam String processDefinitionId, String taskId, @RequestParam String routeToTask,
                                                      String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, orgUnitId).getData();
        Y9LoginUserHolder.setOrgUnit(orgUnit);
        DocUserChoiseModel model = documentService.docUserChoise(itemId, processDefinitionKey, processDefinitionId,
                taskId, routeToTask, processInstanceId);
        return Y9Result.success(model);
    }

    /**
     * 编辑办件
     *
     * @param tenantId          租户id
     * @param orgUnitId         人员、岗位id
     * @param itembox           办件状态，todo（待办），doing（在办），done（办结）
     * @param taskId            任务id
     * @param processInstanceId 流程实例id
     * @param itemId            事项id
     * @param mobile            是否手机端
     * @return {@code Y9Result<OpenDataModel>} 通用请求返回对象 - data是流程详情数据
     * @since 9.6.6
     */
    @Override
    public Y9Result<OpenDataModel> edit(@RequestParam String tenantId, @RequestParam String orgUnitId,
                                        @RequestParam String itembox, @RequestParam(required = false) String taskId,
                                        @RequestParam String processInstanceId, @RequestParam String itemId, @RequestParam boolean mobile) {
        Y9LoginUserHolder.setTenantId(tenantId);
        OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, orgUnitId).getData();
        Y9LoginUserHolder.setOrgUnit(orgUnit);
        OpenDataModel model = documentService.edit(itembox, taskId, processInstanceId, itemId, mobile);
        return Y9Result.success(model);
    }

    /**
     * 编辑办件
     *
     * @param tenantId          租户id
     * @param orgUnitId         人员、岗位id
     * @param processInstanceId 流程实例id
     * @param mobile            是否手机端
     * @return {@code Y9Result<OpenDataModel>} 通用请求返回对象 - data是流程详情数据
     * @since 9.6.6
     */
    @Override
    public Y9Result<DocumentDetailModel> editDoing(@RequestParam String tenantId, @RequestParam String orgUnitId,
                                        @RequestParam String processInstanceId, @RequestParam boolean mobile) {
        Y9LoginUserHolder.setTenantId(tenantId);
        OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, orgUnitId).getData();
        Y9LoginUserHolder.setOrgUnit(orgUnit);
        DocumentDetailModel model = documentService.editDoing(processInstanceId, mobile);
        return Y9Result.success(model);
    }

    /**
     * 编辑办件
     *
     * @param tenantId          租户id
     * @param orgUnitId         人员、岗位id
     * @param taskId            任务id
     * @param mobile            是否手机端
     * @return {@code Y9Result<OpenDataModel>} 通用请求返回对象 - data是流程详情数据
     * @since 9.6.6
     */
    @Override
    public Y9Result<DocumentDetailModel> editTodo(@RequestParam String tenantId, @RequestParam String orgUnitId,
                                                  @RequestParam String taskId,
                                                  @RequestParam boolean mobile) {
        Y9LoginUserHolder.setTenantId(tenantId);
        OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, orgUnitId).getData();
        Y9LoginUserHolder.setOrgUnit(orgUnit);
        DocumentDetailModel model = documentService.editTodo(taskId, mobile);
        return Y9Result.success(model);
    }

    /**
     * 解析当前任务节点配置的用户数据
     *
     * @param tenantId            租户id
     * @param orgUnitId           人员、岗位id
     * @param itemId              事项id
     * @param processDefinitionId 流程定义id
     * @param routeToTaskId       任务key
     * @param taskDefName         任务名称
     * @param processInstanceId   流程实例id
     * @param multiInstance       是否多实例
     * @return {@code Y9Result<List<String>>} 通用请求返回对象 - data是解析后的人员id
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<String>> parserUser(@RequestParam String tenantId, @RequestParam String orgUnitId,
                                             @RequestParam String itemId, @RequestParam String processDefinitionId, @RequestParam String routeToTaskId,
                                             @RequestParam(required = false) String taskDefName, @RequestParam(required = false) String processInstanceId,
                                             @RequestParam(required = false) String multiInstance) {
        Y9LoginUserHolder.setTenantId(tenantId);
        OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, orgUnitId).getData();
        Y9LoginUserHolder.setOrgUnit(orgUnit);
        return documentService.parserUser(itemId, processDefinitionId, routeToTaskId, taskDefName, processInstanceId,
                multiInstance);
    }

    /**
     * 带自定义变量发送
     *
     * @param tenantId             租户id
     * @param orgUnitId            人员、岗位 id
     * @param processInstanceId    流程实例id
     * @param taskId               任务id
     * @param sponsorHandle        是否主办人办理
     * @param itemId               事项id
     * @param processSerialNumber  流程编号
     * @param processDefinitionKey 流程定义key
     * @param userChoice           选择的发送人员
     * @param sponsorGuid          主办人id
     * @param routeToTaskId        任务key
     * @param variables            保存变量
     * @return {@code Y9Result<String>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<String> saveAndForwarding(@RequestParam String tenantId, @RequestParam String orgUnitId,
                                              String processInstanceId, String taskId, String sponsorHandle, @RequestParam String itemId,
                                              @RequestParam String processSerialNumber, @RequestParam String processDefinitionKey,
                                              @RequestParam String userChoice, String sponsorGuid, @RequestParam String routeToTaskId,
                                              @RequestBody Map<String, Object> variables) {
        Y9LoginUserHolder.setTenantId(tenantId);
        OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, orgUnitId).getData();
        Y9LoginUserHolder.setOrgUnit(orgUnit);
        Y9Result<String> y9Result;
        if (StringUtils.isBlank(processInstanceId) || UtilConsts.NULL.equals(processInstanceId)) {
            y9Result = documentService.saveAndForwarding(itemId, processSerialNumber, processDefinitionKey, userChoice,
                    sponsorGuid, routeToTaskId, variables);
        } else {
            variableApi.setVariables(tenantId, taskId, variables);
            y9Result = documentService.forwarding(taskId, sponsorHandle, userChoice, routeToTaskId, sponsorGuid);
        }
        if (y9Result.isSuccess()) {// 异步自动循环发送
            asyncUtilService.loopSending(tenantId, orgUnitId, itemId, y9Result.getData());
        }
        return y9Result;
    }

    /**
     * 指定任务节点发送
     *
     * @param tenantId             租户id
     * @param orgUnitId            人员、岗位id
     * @param processInstanceId    流程实例id
     * @param taskId               任务id
     * @param sponsorHandle        是否主办人办理
     * @param itemId               事项id
     * @param processSerialNumber  流程编号
     * @param processDefinitionKey 流程定义key
     * @param userChoice           选择的发送人员
     * @param sponsorGuid          主办人id
     * @param routeToTaskId        任务key
     * @param startRouteToTaskId   启动节点key
     * @param variables            保存变量
     * @return {@code Y9Result<String>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<String> saveAndForwardingByTaskKey(@RequestParam String tenantId, @RequestParam String orgUnitId,
                                                       String processInstanceId, String taskId, String sponsorHandle, @RequestParam String itemId,
                                                       @RequestParam String processSerialNumber, @RequestParam String processDefinitionKey,
                                                       @RequestParam String userChoice, String sponsorGuid, @RequestParam String routeToTaskId,
                                                       @RequestParam String startRouteToTaskId, @RequestBody Map<String, Object> variables) {
        Y9LoginUserHolder.setTenantId(tenantId);
        OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, orgUnitId).getData();
        Y9LoginUserHolder.setOrgUnit(orgUnit);
        if (StringUtils.isBlank(processInstanceId) || UtilConsts.NULL.equals(processInstanceId)) {
            return documentService.saveAndForwardingByTaskKey(itemId, processSerialNumber, processDefinitionKey,
                    userChoice, sponsorGuid, routeToTaskId, startRouteToTaskId, variables);
        } else {
            if (!variables.isEmpty()) {
                variableApi.setVariables(tenantId, taskId, variables);
            }
            return documentService.forwarding(taskId, sponsorHandle, userChoice, routeToTaskId, sponsorGuid);
        }
    }

    /**
     * 提交并发送
     *
     * @param tenantId            租户id
     * @param orgUnitId           人员、岗位 id
     * @param taskId              任务id
     * @param itemId              事项id
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> saveAndSubmitTo(@RequestParam String tenantId, @RequestParam String orgUnitId,
                                            String taskId, @RequestParam String itemId, @RequestParam String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, orgUnitId).getData();
        Y9LoginUserHolder.setOrgUnit(orgUnit);
        Y9Result<Object> y9Result;
        if (StringUtils.isBlank(taskId) || UtilConsts.NULL.equals(taskId)) {
            y9Result = documentService.saveAndSubmitTo(itemId, processSerialNumber);
        } else {
            y9Result = documentService.submitTo(processSerialNumber, taskId);
        }
        if (y9Result.isSuccess()) {// 异步自动循环发送
            ProcessParam processParam = processParamService.findByProcessSerialNumber(processSerialNumber);
            asyncUtilService.loopSending(tenantId, orgUnitId, itemId, processParam.getProcessInstanceId());
        }
        return y9Result;
    }

    /**
     * 获取签收任务配置
     *
     * @param tenantId            租户id
     * @param orgUnitId           人员、岗位id
     * @param itemId              事项id
     * @param processDefinitionId 流程定义id
     * @param taskDefinitionKey   任务key
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<SignTaskConfigModel>} 通用请求返回对象 - data是签收任务配置
     * @since 9.6.6
     */
    @Override
    public Y9Result<SignTaskConfigModel> signTaskConfig(@RequestParam String tenantId, @RequestParam String orgUnitId,
                                                        @RequestParam String itemId, @RequestParam String processDefinitionId, @RequestParam String taskDefinitionKey,
                                                        @RequestParam String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, orgUnitId).getData();
        Y9LoginUserHolder.setOrgUnit(orgUnit);
        SignTaskConfigModel model =
                documentService.signTaskConfig(itemId, processDefinitionId, taskDefinitionKey, processSerialNumber);
        return Y9Result.success(model);
    }

    @Override
    public Y9Result<List<ItemStartNodeRoleModel>> getAllStartTaskDefKey(@RequestParam String tenantId,
                                                                        @RequestParam String orgUnitId, @RequestParam String itemId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, orgUnitId).getData();
        Y9LoginUserHolder.setOrgUnit(orgUnit);
        List<ItemStartNodeRoleModel> modelList = itemStartNodeRoleService.getAllStartTaskDefKey(itemId);
        return Y9Result.success(modelList);
    }

    /**
     * 启动流程
     *
     * @param tenantId             租户id
     * @param orgUnitId            人员、岗位id
     * @param itemId               事项id
     * @param processSerialNumber  流程编号
     * @param processDefinitionKey 流程定义key
     * @return {@code Y9Result<StartProcessResultModel>} 通用请求返回对象 - data是启动流程返回信息
     * @throws Exception Exception
     * @since 9.6.6
     */
    @Override
    public Y9Result<StartProcessResultModel> startProcess(@RequestParam String tenantId, @RequestParam String orgUnitId,
                                                          @RequestParam String itemId, @RequestParam String processSerialNumber,
                                                          @RequestParam String processDefinitionKey) {
        Y9LoginUserHolder.setTenantId(tenantId);
        OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, orgUnitId).getData();
        Y9LoginUserHolder.setOrgUnit(orgUnit);
        StartProcessResultModel model = documentService.startProcess(itemId, processSerialNumber, processDefinitionKey);
        return Y9Result.success(model);
    }

    /**
     * 启动流程
     *
     * @param tenantId             租户id
     * @param orgUnitId            人员、岗位id
     * @param itemId               事项id
     * @param processSerialNumber  流程编号
     * @param processDefinitionKey 流程定义key
     * @return {@code Y9Result<StartProcessResultModel>} 通用请求返回对象 - data是启动流程返回信息
     * @throws Exception Exception
     * @since 9.6.6
     */
    @Override
    public Y9Result<StartProcessResultModel> startProcessByTheTaskKey(@RequestParam String tenantId, @RequestParam String orgUnitId,
                                                          @RequestParam String itemId, @RequestParam String processSerialNumber,
                                                          @RequestParam String processDefinitionKey,@RequestParam String theTaskKey) {
        Y9LoginUserHolder.setTenantId(tenantId);
        OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, orgUnitId).getData();
        Y9LoginUserHolder.setOrgUnit(orgUnit);
        StartProcessResultModel model = documentService.startProcessByTheTaskKey(itemId, processSerialNumber, processDefinitionKey,theTaskKey);
        return Y9Result.success(model);
    }

    /**
     * 启动流程（多人）
     *
     * @param tenantId             租户id
     * @param orgUnitId            人员、岗位id
     * @param itemId               事项id
     * @param processSerialNumber  流程编号
     * @param processDefinitionKey 流程定义key
     * @param userIds              人员、岗位ids
     * @return {@code Y9Result<StartProcessResultModel>} 通用请求返回对象 - data是启动流程返回信息
     * @throws Exception Exception
     * @since 9.6.6
     */
    @Override
    public Y9Result<StartProcessResultModel> startProcess(@RequestParam String tenantId, @RequestParam String orgUnitId,
                                                          @RequestParam String itemId, @RequestParam String processSerialNumber,
                                                          @RequestParam String processDefinitionKey, @RequestParam String userIds) {
        Y9LoginUserHolder.setTenantId(tenantId);
        OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, orgUnitId).getData();
        Y9LoginUserHolder.setOrgUnit(orgUnit);
        StartProcessResultModel model =
                documentService.startProcess(itemId, processSerialNumber, processDefinitionKey, userIds);
        return Y9Result.success(model);
    }
}
