package net.risesoft.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.impl.HistoricProcessInstanceQueryProperty;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.processadmin.RuntimeApi;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.processadmin.ExecutionModel;
import net.risesoft.model.processadmin.ProcessInstanceModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.CustomRuntimeService;
import net.risesoft.service.CustomTaskService;
import net.risesoft.util.FlowableModelConvertUtil;
import net.risesoft.y9.FlowableTenantInfoHolder;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * 正在运行流程实例操作接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/runtime", produces = MediaType.APPLICATION_JSON_VALUE)
public class RuntimeApiImpl implements RuntimeApi {

    private final CustomRuntimeService customRuntimeService;

    private final RuntimeService runtimeService;

    private final CustomTaskService customTaskService;

    private final OrgUnitApi orgUnitApi;

    /**
     * 加签
     *
     * @param tenantId 租户id
     * @param activityId 活动节点id
     * @param parentExecutionId 父执行实例id
     * @param map 参数
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> addMultiInstanceExecution(@RequestParam String tenantId, @RequestParam String activityId,
        @RequestParam String parentExecutionId, @RequestBody Map<String, Object> map) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        customRuntimeService.addMultiInstanceExecution(activityId, parentExecutionId, map);
        return Y9Result.success();
    }

    /**
     * 真办结
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param processInstanceId 流程实例id
     * @param taskId 任务id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> complete(@RequestParam String tenantId, @RequestParam String orgUnitId,
        @RequestParam String processInstanceId, @RequestParam String taskId) throws Exception {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, orgUnitId).getData();
        Y9LoginUserHolder.setOrgUnit(orgUnit);
        customTaskService.complete(processInstanceId, taskId);
        return Y9Result.success();
    }

    /**
     * 减签
     *
     * @param tenantId 租户id
     * @param executionId 执行实例id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> deleteMultiInstanceExecution(@RequestParam String tenantId,
        @RequestParam String executionId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        customRuntimeService.deleteMultiInstanceExecution(executionId);
        return Y9Result.success();
    }

    /**
     * 根据执行Id获取当前活跃的节点信息
     *
     * @param tenantId 租户id
     * @param executionId 执行实例id
     * @return {@code Y9Result<List<String>>} 通用请求返回对象 - data 是当前活跃的节点信息
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<String>> getActiveActivityIds(@RequestParam String tenantId,
        @RequestParam String executionId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        return Y9Result.success(customRuntimeService.getActiveActivityIds(executionId));
    }

    /**
     * 根据执行实例Id查找执行实例
     *
     * @param tenantId 租户id
     * @param executionId 执行实例id
     * @return {@code Y9Result<ExecutionModel>} 通用请求返回对象 - data 是执行实例
     * @since 9.6.6
     */
    @Override
    public Y9Result<ExecutionModel> getExecutionById(@RequestParam String tenantId, @RequestParam String executionId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Execution execution = customRuntimeService.getExecutionById(executionId);
        if (null == execution) {
            return Y9Result.failure("未找到执行实例");
        }
        ExecutionModel eModel = new ExecutionModel();
        Y9BeanUtil.copyProperties(execution, eModel);
        return Y9Result.success(eModel);
    }

    /**
     * 根据父流程实例获取子流程实例
     *
     * @param tenantId 租户id
     * @param superProcessInstanceId 父流程实例id
     * @return {@code Y9Result<List<ProcessInstanceModel>>} 通用请求返回对象 - data 是子流程实例列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<ProcessInstanceModel>> getListBySuperProcessInstanceId(@RequestParam String tenantId,
        @RequestParam String superProcessInstanceId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        List<ProcessInstance> list = customRuntimeService.listBySuperProcessInstanceId(superProcessInstanceId);
        return Y9Result.success(FlowableModelConvertUtil.processInstanceList2ModelList(list));
    }

    /**
     * 根据流程实例Id获取流程实例信息
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<ProcessInstanceModel>} 通用请求返回对象 - data 是流程实例
     * @since 9.6.6
     */
    @Override
    public Y9Result<ProcessInstanceModel> getProcessInstance(@RequestParam String tenantId,
        @RequestParam String processInstanceId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        ProcessInstance pi = customRuntimeService.getProcessInstance(processInstanceId);
        if (null == pi) {
            return Y9Result.failure("流程实例不存在");
        }
        ProcessInstanceModel pim = new ProcessInstanceModel();
        Y9BeanUtil.copyProperties(pi, pim);
        return Y9Result.success(pim);
    }

    /**
     * 根据流程定义id获取流程实例列表
     *
     * @param tenantId 租户id
     * @param processDefinitionId 流程定义id
     * @param page 页码
     * @param rows 行数
     * @return {@code Y9Page<ProcessInstanceModel>} 通用分页请求返回对象 - rows 是流程实例
     * @since 9.6.6
     */
    @Override
    public Y9Page<ProcessInstanceModel> getProcessInstancesByDefId(@RequestParam String tenantId,
        @RequestParam String processDefinitionId, @RequestParam Integer page, @RequestParam Integer rows) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        long totalCount = runtimeService.createProcessInstanceQuery().processDefinitionId(processDefinitionId).count();
        List<ProcessInstance> list =
            runtimeService.createProcessInstanceQuery().processDefinitionId(processDefinitionId)
                .orderBy(HistoricProcessInstanceQueryProperty.START_TIME).desc().listPage((page - 1) * rows, rows);
        List<ProcessInstanceModel> hpiModelList = FlowableModelConvertUtil.processInstanceList2ModelList(list);
        return Y9Page.success(page, (int)(totalCount + rows - 1) / rows, totalCount, hpiModelList);
    }

    /**
     * 根据流程定义Key获取流程实例列表
     *
     * @param tenantId 租户id
     * @param processDefinitionKey 流程定义key
     * @return {@code Y9Result<List<ProcessInstanceModel>>} 通用请求返回对象 - data 是流程实例
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<ProcessInstanceModel>> getProcessInstancesByKey(@RequestParam String tenantId,
        @RequestParam String processDefinitionKey) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        List<ProcessInstance> list = customRuntimeService.listProcessInstancesByKey(processDefinitionKey);
        return Y9Result.success(FlowableModelConvertUtil.processInstanceList2ModelList(list));
    }

    /**
     * 真办结后恢复流程实例为待办状态
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param processInstanceId 流程实例id
     * @param year 年份
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> recovery4Completed(@RequestParam String tenantId, @RequestParam String orgUnitId,
        @RequestParam String processInstanceId, @RequestParam String year) throws Exception {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, orgUnitId).getData();
        Y9LoginUserHolder.setOrgUnit(orgUnit);
        customRuntimeService.recoveryCompleted(processInstanceId, year);
        return Y9Result.success();
    }

    /**
     * 恢复流程实例为待办状态，其实是先激活，再设置流程实例的结束时间为null
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> recovery4SetUpCompleted(@RequestParam String tenantId,
        @RequestParam String processInstanceId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        customRuntimeService.recovery4SetUpCompleted(processInstanceId);
        return Y9Result.success();
    }

    /**
     * 获取正在运行流程实例列表
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param page 页吗
     * @param rows 条数
     * @return {@code Y9Page<Map<String, Object>>} 通用分页请求返回对象 - rows 是流程实例
     * @since 9.6.6
     */
    @Override
    public Y9Page<ProcessInstanceModel> runningList(@RequestParam String tenantId,
        @RequestParam String processInstanceId, @RequestParam int page, @RequestParam int rows) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        List<ProcessInstanceModel> items = new ArrayList<>();
        long totalCount;
        List<ProcessInstance> processInstanceList;
        if (StringUtils.isBlank(processInstanceId)) {
            totalCount = runtimeService.createProcessInstanceQuery().count();
            processInstanceList =
                runtimeService.createProcessInstanceQuery().orderByStartTime().desc().listPage((page - 1) * rows, rows);
        } else {
            totalCount = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).count();
            processInstanceList = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId)
                .orderByStartTime().desc().listPage((page - 1) * rows, rows);
        }
        OrgUnit orgUnit;
        ProcessInstanceModel piModel;
        for (ProcessInstance processInstance : processInstanceList) {
            processInstanceId = processInstance.getId();
            piModel = new ProcessInstanceModel();
            piModel.setId(processInstanceId);
            piModel.setProcessDefinitionId(processInstance.getProcessDefinitionId());
            piModel.setProcessDefinitionName(processInstance.getProcessDefinitionName());
            piModel.setStartTime(processInstance.getStartTime());
            try {
                piModel
                    .setActivityName(runtimeService.createActivityInstanceQuery().processInstanceId(processInstanceId)
                        .orderByActivityInstanceStartTime().desc().list().get(0).getActivityName());
                piModel.setSuspended(processInstance.isSuspended());
                piModel.setStartUserName("无");
                if (StringUtils.isNotBlank(processInstance.getStartUserId())) {
                    String[] userIdAndDeptId = processInstance.getStartUserId().split(":");
                    if (userIdAndDeptId.length == 1) {
                        orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, userIdAndDeptId[0]).getData();
                        OrgUnit parent = orgUnitApi.getParent(tenantId, orgUnit.getId()).getData();
                        piModel.setStartUserName(orgUnit.getName() + "(" + parent.getName() + ")");
                    } else {
                        orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, userIdAndDeptId[0]).getData();
                        if (null != orgUnit) {
                            OrgUnit parent = orgUnitApi
                                .getOrgUnit(tenantId, processInstance.getStartUserId().split(":")[1]).getData();
                            if (null == parent) {
                                piModel.setStartUserName(orgUnit.getName());
                            } else {
                                piModel.setStartUserName(orgUnit.getName() + "(" + parent.getName() + ")");
                            }
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.error("获取流程实例[" + processInstanceId + "]的活动节点名称失败", e);
            }
            items.add(piModel);
        }
        return Y9Page.success(page, (int)totalCount / rows + 1, totalCount, items, "获取列表成功");
    }

    /**
     * 设置流程实例为办结的状态，其实是先暂停，再设置流程结束时间为当前时间
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> setUpCompleted(@RequestParam String tenantId, @RequestParam String processInstanceId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        customRuntimeService.setUpCompleted(processInstanceId);
        return Y9Result.success();
    }

    /**
     * 根据流程实例id设置流程变量
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param key 变量key
     * @param map 变量map
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> setVariable(@RequestParam String tenantId, @RequestParam String processInstanceId,
        @RequestParam String key, @RequestBody Map<String, Object> map) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        customRuntimeService.setVariable(processInstanceId, key, map.get("val"));
        return Y9Result.success();
    }

    /**
     * 根据执行实例id设置流程变量
     *
     * @param tenantId 租户id
     * @param executionId 执行实例id
     * @param map 变量map
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> setVariables(@RequestParam String tenantId, @RequestParam String executionId,
        @RequestBody Map<String, Object> map) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        customRuntimeService.setVariables(executionId, map);
        return Y9Result.success();
    }

    /**
     * 根据流程定义Key启动流程实例，设置流程变量,并返回流程实例,流程启动人是人员Id
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param processDefinitionKey 流程定义key
     * @param systemName 系统名称
     * @param map 变量map
     * @return {@code Y9Result<ProcessInstanceModel>} 通用请求返回对象 - data 是流程实例
     * @since 9.6.6
     */
    @Override
    public Y9Result<ProcessInstanceModel> startProcessInstanceByKey(@RequestParam String tenantId,
        @RequestParam String orgUnitId, @RequestParam String processDefinitionKey, @RequestParam String systemName,
        @RequestBody Map<String, Object> map) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, orgUnitId).getData();
        Y9LoginUserHolder.setOrgUnit(orgUnit);
        ProcessInstance pi = customRuntimeService.startProcessInstanceByKey(processDefinitionKey, systemName, map);
        ProcessInstanceModel pim = null;
        if (pi != null) {
            pim = new ProcessInstanceModel();
            Y9BeanUtil.copyProperties(pi, pim);
        }
        return Y9Result.success(pim);
    }

    /**
     * 判断是否是挂起实例
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<Boolean>} 通用请求返回对象 - data 属性判断流程是否挂起
     * @since 9.6.6
     */
    @Override
    public Y9Result<Boolean> suspendedByProcessInstanceId(@RequestParam String tenantId,
        @RequestParam String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return Y9Result.success(runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId)
            .singleResult().isSuspended());
    }

    /**
     * 挂起或者激活流程实例
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param state 状态
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> switchSuspendOrActive(@RequestParam String tenantId, @RequestParam String processInstanceId,
        @RequestParam String state) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        customRuntimeService.switchSuspendOrActive(processInstanceId, state);
        return Y9Result.success();
    }
}
