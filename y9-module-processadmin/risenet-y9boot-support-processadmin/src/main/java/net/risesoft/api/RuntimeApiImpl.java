package net.risesoft.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.impl.HistoricProcessInstanceQueryProperty;
import org.flowable.engine.runtime.ActivityInstance;
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
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.model.processadmin.ExecutionModel;
import net.risesoft.model.processadmin.ProcessInstanceModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.CustomRuntimeService;
import net.risesoft.service.CustomTaskService;
import net.risesoft.util.FlowableModelConvertUtil;
import net.risesoft.y9.Y9LoginUserHolder;

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
     * @param activityId 活动节点id
     * @param parentExecutionId 父执行实例id
     * @param map 参数
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> addMultiInstanceExecution(@RequestParam String activityId,
        @RequestParam String parentExecutionId, @RequestBody Map<String, Object> map) {
        customRuntimeService.addMultiInstanceExecution(activityId, parentExecutionId, map);
        return Y9Result.success();
    }

    /**
     * 真办结
     *
     * @param processInstanceId 流程实例id
     * @param taskId 任务id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> complete(@RequestParam String processInstanceId, @RequestParam String taskId)
        throws Exception {
        customTaskService.complete(processInstanceId, taskId);
        return Y9Result.success();
    }

    /**
     * 真办结
     *
     * @param taskId 任务id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> completeSub(@RequestParam String taskId, List<String> userList) throws Exception {
        customTaskService.completeSub(taskId, userList);
        return Y9Result.success();
    }

    /**
     * 减签
     *
     * @param executionId 执行实例id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> deleteMultiInstanceExecution(@RequestParam String executionId) {
        customRuntimeService.deleteMultiInstanceExecution(executionId);
        return Y9Result.success();
    }

    /**
     * 根据执行Id获取当前活跃的节点信息
     *
     * @param executionId 执行实例id
     * @return {@code Y9Result<List<String>>} 通用请求返回对象 - data 是当前活跃的节点信息
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<String>> getActiveActivityIds(@RequestParam String executionId) {
        return Y9Result.success(customRuntimeService.getActiveActivityIds(executionId));
    }

    /**
     * 根据执行实例Id查找执行实例
     *
     * @param executionId 执行实例id
     * @return {@code Y9Result<ExecutionModel>} 通用请求返回对象 - data 是执行实例
     * @since 9.6.6
     */
    @Override
    public Y9Result<ExecutionModel> getExecutionById(@RequestParam String executionId) {
        Execution execution = customRuntimeService.getExecutionById(executionId);
        if (null == execution) {
            return Y9Result.failure("未找到执行实例");
        }
        return Y9Result.success(FlowableModelConvertUtil.execution2Model(execution));
    }

    /**
     * 根据父流程实例获取子流程实例
     *
     * @param superProcessInstanceId 父流程实例id
     * @return {@code Y9Result<List<ProcessInstanceModel>>} 通用请求返回对象 - data 是子流程实例列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<ProcessInstanceModel>>
        getListBySuperProcessInstanceId(@RequestParam String superProcessInstanceId) {
        List<ProcessInstance> list = customRuntimeService.listBySuperProcessInstanceId(superProcessInstanceId);
        return Y9Result.success(FlowableModelConvertUtil.processInstanceList2ModelList(list));
    }

    /**
     * 根据流程实例Id获取流程实例信息
     *
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<ProcessInstanceModel>} 通用请求返回对象 - data 是流程实例
     * @since 9.6.6
     */
    @Override
    public Y9Result<ProcessInstanceModel> getProcessInstance(@RequestParam String processInstanceId) {
        ProcessInstance pi = customRuntimeService.getProcessInstance(processInstanceId);
        if (null == pi) {
            return Y9Result.failure("流程实例不存在");
        }
        return Y9Result.success(FlowableModelConvertUtil.processInstance2Model(pi));
    }

    /**
     * 根据流程定义id获取流程实例列表
     *
     * @param processDefinitionId 流程定义id
     * @param page 页码
     * @param rows 行数
     * @return {@code Y9Page<ProcessInstanceModel>} 通用分页请求返回对象 - rows 是流程实例
     * @since 9.6.6
     */
    @Override
    public Y9Page<ProcessInstanceModel> getProcessInstancesByDefId(@RequestParam String processDefinitionId,
        @RequestParam Integer page, @RequestParam Integer rows) {
        long totalCount = runtimeService.createProcessInstanceQuery().processDefinitionId(processDefinitionId).count();
        List<ProcessInstance> list = runtimeService.createProcessInstanceQuery()
            .processDefinitionId(processDefinitionId)
            .orderBy(HistoricProcessInstanceQueryProperty.START_TIME)
            .desc()
            .listPage((page - 1) * rows, rows);
        List<ProcessInstanceModel> hpiModelList = FlowableModelConvertUtil.processInstanceList2ModelList(list);
        return Y9Page.success(page, (int)(totalCount + rows - 1) / rows, totalCount, hpiModelList);
    }

    /**
     * 根据流程定义Key获取流程实例列表
     *
     * @param processDefinitionKey 流程定义key
     * @return {@code Y9Result<List<ProcessInstanceModel>>} 通用请求返回对象 - data 是流程实例
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<ProcessInstanceModel>> getProcessInstancesByKey(@RequestParam String processDefinitionKey) {
        List<ProcessInstance> list = customRuntimeService.listProcessInstancesByKey(processDefinitionKey);
        return Y9Result.success(FlowableModelConvertUtil.processInstanceList2ModelList(list));
    }

    /**
     * 真办结后恢复流程实例为待办状态
     *
     * @param processInstanceId 流程实例id
     * @param year 年份
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> recovery4Completed(@RequestParam String processInstanceId, @RequestParam String year)
        throws Exception {
        customRuntimeService.recoveryCompleted(processInstanceId, year);
        return Y9Result.success();
    }

    /**
     * 恢复流程实例为待办状态，其实是先激活，再设置流程实例的结束时间为null
     *
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> recovery4SetUpCompleted(@RequestParam String processInstanceId) {
        customRuntimeService.recovery4SetUpCompleted(processInstanceId);
        return Y9Result.success();
    }

    /**
     * 获取正在运行流程实例列表
     *
     * @param processInstanceId 流程实例id
     * @param page 页吗
     * @param rows 条数
     * @return {@code Y9Page<Map<String, Object>>} 通用分页请求返回对象 - rows 是流程实例
     * @since 9.6.6
     */
    @Override
    public Y9Page<ProcessInstanceModel> runningList(@RequestParam String processInstanceId, @RequestParam int page,
        @RequestParam int rows) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<ProcessInstanceModel> items = new ArrayList<>();
        long totalCount;
        List<ProcessInstance> processInstanceList;
        if (StringUtils.isBlank(processInstanceId)) {
            totalCount = runtimeService.createProcessInstanceQuery().count();
            processInstanceList =
                runtimeService.createProcessInstanceQuery().orderByStartTime().desc().listPage((page - 1) * rows, rows);
        } else {
            totalCount = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).count();
            processInstanceList = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .orderByStartTime()
                .desc()
                .listPage((page - 1) * rows, rows);
        }

        List<String> startUserIdList = processInstanceList.stream()
            .map(ProcessInstance::getStartUserId)
            .filter(StringUtils::isNotBlank)
            .collect(Collectors.toList());
        Map<String,
            OrgUnit> idOrgUnitMap = orgUnitApi.listPersonOrPositionByIds(tenantId, startUserIdList)
                .getData()
                .stream()
                .collect(Collectors.toMap(OrgUnit::getId, orgUnit -> orgUnit));
        for (ProcessInstance processInstance : processInstanceList) {
            processInstanceId = processInstance.getId();
            ProcessInstanceModel piModel = new ProcessInstanceModel();
            piModel.setId(processInstanceId);
            piModel.setProcessDefinitionId(processInstance.getProcessDefinitionId());
            piModel.setProcessDefinitionName(processInstance.getProcessDefinitionName());
            piModel.setStartTime(processInstance.getStartTime());
            try {
                ActivityInstance activityInstance = runtimeService.createActivityInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .orderByActivityInstanceStartTime()
                    .desc()
                    .list()
                    .get(0);
                piModel.setActivityName(activityInstance != null ? activityInstance.getActivityName() : "");
                piModel.setSuspended(processInstance.isSuspended());
                piModel.setStartUserName("无");
                if (StringUtils.isNotBlank(processInstance.getStartUserId())) {
                    OrgUnit orgUnit = idOrgUnitMap.get(processInstance.getStartUserId());
                    OrgUnit parent = orgUnitApi.getOrgUnitAsParent(tenantId, orgUnit.getParentId()).getData();
                    piModel.setStartUserName(orgUnit.getName() + "(" + parent.getName() + ")");
                }
            } catch (Exception e) {
                LOGGER.error("获取流程实例[{}]的活动节点名称失败", processInstanceId, e);
            }
            items.add(piModel);
        }
        return Y9Page.success(page, (int)totalCount / rows + 1, totalCount, items, "获取列表成功");
    }

    /**
     * 设置流程实例为办结的状态，其实是先暂停，再设置流程结束时间为当前时间
     *
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> setUpCompleted(@RequestParam String processInstanceId) {
        customRuntimeService.setUpCompleted(processInstanceId);
        return Y9Result.success();
    }

    /**
     * 根据流程实例id设置流程变量
     *
     * @param processInstanceId 流程实例id
     * @param key 变量key
     * @param map 变量map
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> setVariable(@RequestParam String processInstanceId, @RequestParam String key,
        @RequestBody Map<String, Object> map) {
        customRuntimeService.setVariable(processInstanceId, key, map.get("val"));
        return Y9Result.success();
    }

    /**
     * 根据执行实例id设置流程变量
     *
     * @param executionId 执行实例id
     * @param map 变量map
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> setVariables(@RequestParam String executionId, @RequestBody Map<String, Object> map) {
        customRuntimeService.setVariables(executionId, map);
        return Y9Result.success();
    }

    /**
     * 根据流程定义Key启动流程实例，设置流程变量,并返回流程实例,流程启动人是人员Id
     *
     * @param processDefinitionKey 流程定义key
     * @param systemName 系统名称
     * @param map 变量map
     * @return {@code Y9Result<ProcessInstanceModel>} 通用请求返回对象 - data 是流程实例
     * @since 9.6.6
     */
    @Override
    public Y9Result<ProcessInstanceModel> startProcessInstanceByKey(@RequestParam String processDefinitionKey,
        @RequestParam String systemName, @RequestBody Map<String, Object> map) {
        ProcessInstance pi = customRuntimeService.startProcessInstanceByKey(processDefinitionKey, systemName, map);
        return Y9Result.success(FlowableModelConvertUtil.processInstance2Model(pi));
    }

    /**
     * 判断是否是挂起实例
     *
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<Boolean>} 通用请求返回对象 - data 属性判断流程是否挂起
     * @since 9.6.6
     */
    @Override
    public Y9Result<Boolean> suspendedByProcessInstanceId(@RequestParam String processInstanceId) {
        return Y9Result.success(runtimeService.createProcessInstanceQuery()
            .processInstanceId(processInstanceId)
            .singleResult()
            .isSuspended());
    }

    /**
     * 挂起或者激活流程实例
     *
     * @param processInstanceId 流程实例id
     * @param state 状态
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> switchSuspendOrActive(@RequestParam String processInstanceId, @RequestParam String state) {
        customRuntimeService.switchSuspendOrActive(processInstanceId, state);
        return Y9Result.success();
    }
}
