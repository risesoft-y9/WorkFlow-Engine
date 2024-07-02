package net.risesoft.api;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.impl.HistoricProcessInstanceQueryProperty;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.api.processadmin.RuntimeApi;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Person;
import net.risesoft.model.platform.Position;
import net.risesoft.model.processadmin.ExecutionModel;
import net.risesoft.model.processadmin.ProcessInstanceModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.CustomRuntimeService;
import net.risesoft.service.CustomTaskService;
import net.risesoft.service.FlowableTenantInfoHolder;
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
@RequestMapping(value = "/services/rest/runtime")
public class RuntimeApiImpl implements RuntimeApi {

    private final CustomRuntimeService customRuntimeService;

    private final RuntimeService runtimeService;

    private final PersonApi personManager;

    private final PositionApi positionManager;

    private final CustomTaskService customTaskService;

    private final OrgUnitApi orgUnitApi;

    private final PositionApi positionApi;

    /**
     * 加签
     *
     * @param tenantId 租户id
     * @param activityId 活动节点id
     * @param parentExecutionId 父执行实例id
     */
    @Override
    @PostMapping(value = "/addMultiInstanceExecution", produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Object> addMultiInstanceExecution(@RequestParam String tenantId, @RequestParam String activityId,
        @RequestParam String parentExecutionId, @RequestBody Map<String, Object> map) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        customRuntimeService.addMultiInstanceExecution(activityId, parentExecutionId, map);
        return Y9Result.success();
    }

    /**
     * 真办结/岗位
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param processInstanceId 流程实例id
     * @param taskId 任务id
     */
    @Override
    @PostMapping(value = "/complete4Position", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Object> complete4Position(@RequestParam String tenantId, @RequestParam String positionId,
        @RequestParam String processInstanceId, @RequestParam String taskId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionManager.get(tenantId, positionId).getData();
        Y9LoginUserHolder.setPosition(position);
        try {
            customTaskService.complete4Position(processInstanceId, taskId);
            return Y9Result.success();
        } catch (Exception e) {
            return Y9Result.failure("办结异常");
        }
    }

    /**
     * 真办结
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processInstanceId 流程实例id
     * @param taskId 任务id
     */
    @Override
    @PostMapping(value = "/completed", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Object> completed(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam String processInstanceId, @RequestParam String taskId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        try {
            customTaskService.complete(processInstanceId, taskId);
            return Y9Result.success();
        } catch (Exception e) {
            return Y9Result.failure("办结异常");
        }

    }

    /**
     * 减签
     *
     * @param tenantId 租户id
     * @param executionId 执行实例id
     */
    @Override
    @PostMapping(value = "/deleteMultiInstanceExecution", produces = MediaType.APPLICATION_JSON_VALUE)
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
     * @return List<String>
     */
    @Override
    @GetMapping(value = "/getActiveActivityIds", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> getActiveActivityIds(@RequestParam String tenantId, @RequestParam String executionId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        return customRuntimeService.getActiveActivityIds(executionId);
    }

    /**
     * 根据执行实例Id查找执行实例
     *
     * @param tenantId 租户id
     * @param executionId 执行实例id
     * @return ExecutionModel
     */
    @Override
    @GetMapping(value = "/getExecutionById", produces = MediaType.APPLICATION_JSON_VALUE)
    public ExecutionModel getExecutionById(@RequestParam String tenantId, @RequestParam String executionId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Execution execution = customRuntimeService.getExecutionById(executionId);
        if (null != execution) {
            return FlowableModelConvertUtil.execution2Model(execution);
        }
        return null;
    }

    /**
     * 根据父流程实例获取子流程实例
     *
     * @param tenantId 租户id
     * @param superProcessInstanceId 父流程实例id
     * @return List<ProcessInstanceModel>
     */
    @Override
    @GetMapping(value = "/getListBySuperProcessInstanceId", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ProcessInstanceModel> getListBySuperProcessInstanceId(@RequestParam String tenantId,
        @RequestParam String superProcessInstanceId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        List<ProcessInstance> list = customRuntimeService.getListBySuperProcessInstanceId(superProcessInstanceId);
        return FlowableModelConvertUtil.processInstanceList2ModelList(list);
    }

    /**
     * 根据流程实例Id获取流程实例
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return ProcessInstanceModel
     */
    @Override
    @GetMapping(value = "/getProcessInstance", produces = MediaType.APPLICATION_JSON_VALUE)
    public ProcessInstanceModel getProcessInstance(@RequestParam String tenantId,
        @RequestParam String processInstanceId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        ProcessInstance pi = customRuntimeService.getProcessInstance(processInstanceId);
        if (null != pi) {
            return FlowableModelConvertUtil.processInstance2Model(pi);
        }
        return null;
    }

    /**
     * 根据流程定义id获取流程实例列表
     *
     * @param tenantId 租户id
     * @param processDefinitionId 流程定义id
     * @param page 页码
     * @param rows 行数
     * @return Map<String, Object>
     */
    @Override
    @GetMapping(value = "/getProcessInstancesByDefId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getProcessInstancesByDefId(@RequestParam String tenantId,
        @RequestParam String processDefinitionId, @RequestParam Integer page, @RequestParam Integer rows) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Map<String, Object> returnMap = new HashMap<>(16);
        long totalCount = runtimeService.createProcessInstanceQuery().processDefinitionId(processDefinitionId).count();
        List<ProcessInstance> list =
            runtimeService.createProcessInstanceQuery().processDefinitionId(processDefinitionId)
                .orderBy(HistoricProcessInstanceQueryProperty.START_TIME).desc().listPage((page - 1) * rows, rows);
        List<ProcessInstanceModel> hpiModelList = FlowableModelConvertUtil.processInstanceList2ModelList(list);
        returnMap.put("currpage", page);
        returnMap.put("totalpages", (totalCount + rows - 1) / rows);
        returnMap.put("total", totalCount);
        returnMap.put("rows", hpiModelList);
        return returnMap;
    }

    /**
     * 根据流程定义Key获取流程实例列表
     *
     * @param tenantId 租户id
     * @param processDefinitionKey 流程定义key
     * @return List<ProcessInstanceModel>
     */
    @Override
    @GetMapping(value = "/getProcessInstancesByKey", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ProcessInstanceModel> getProcessInstancesByKey(@RequestParam String tenantId,
        @RequestParam String processDefinitionKey) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        List<ProcessInstance> list = customRuntimeService.getProcessInstancesByKey(processDefinitionKey);
        return FlowableModelConvertUtil.processInstanceList2ModelList(list);
    }

    /**
     * 真办结后恢复流程实例为待办状态
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param processInstanceId 流程实例id
     * @param year 年份
     */
    @Override
    @PostMapping(value = "/recovery4Completed", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Object> recovery4Completed(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam String processInstanceId, @RequestParam String year) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.get(tenantId, userId).getData();
        if (person != null && StringUtils.isNotBlank(person.getId())) {
            Y9LoginUserHolder.setPerson(person);
            try {
                customRuntimeService.recovery4Completed(processInstanceId, year);
                return Y9Result.success();
            } catch (Exception e) {
                return Y9Result.failure("恢复待办失败");
            }
        } else {
            Position position = positionManager.get(tenantId, userId).getData();
            Y9LoginUserHolder.setPosition(position);
            try {
                customRuntimeService.recoveryCompleted4Position(processInstanceId, year);
                return Y9Result.success();
            } catch (Exception e) {
                return Y9Result.failure("恢复待办失败");
            }
        }
    }

    /**
     * 恢复流程实例为待办状态，其实是先激活，再设置流程实例的结束时间为null
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     */
    @Override
    @PostMapping(value = "/recovery4SetUpCompleted", produces = MediaType.APPLICATION_JSON_VALUE)
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
     * @return Y9Page<Map<String, Object>>
     */
    @GetMapping(value = "/runningList", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public Y9Page<Map<String, Object>> runningList(@RequestParam String tenantId,
        @RequestParam String processInstanceId, @RequestParam int page, @RequestParam int rows) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<Map<String, Object>> items = new ArrayList<>();
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
        Position position;
        OrgUnit orgUnit;
        Map<String, Object> map;
        for (ProcessInstance processInstance : processInstanceList) {
            processInstanceId = processInstance.getId();
            map = new HashMap<>(16);
            map.put("processInstanceId", processInstanceId);
            map.put("processDefinitionId", processInstance.getProcessDefinitionId());
            map.put("processDefinitionName", processInstance.getProcessDefinitionName());
            map.put("startTime",
                processInstance.getStartTime() == null ? "" : sdf.format(processInstance.getStartTime()));
            try {
                map.put("activityName",
                    runtimeService.createActivityInstanceQuery().processInstanceId(processInstanceId)
                        .orderByActivityInstanceStartTime().desc().list().get(0).getActivityName());
                map.put("suspended", processInstance.isSuspended());
                map.put("startUserName", "无");
                if (StringUtils.isNotBlank(processInstance.getStartUserId())) {
                    String[] userIdAndDeptId = processInstance.getStartUserId().split(":");
                    if (userIdAndDeptId.length == 1) {
                        position = positionApi.get(tenantId, userIdAndDeptId[0]).getData();
                        orgUnit = orgUnitApi.getParent(tenantId, position.getId()).getData();
                        map.put("startUserName", position.getName() + "(" + orgUnit.getName() + ")");
                    } else {
                        position = positionApi.get(tenantId, userIdAndDeptId[0]).getData();
                        if (null != position) {
                            orgUnit = orgUnitApi.getOrgUnit(tenantId, processInstance.getStartUserId().split(":")[1])
                                .getData();
                            if (null == orgUnit) {
                                map.put("startUserName", position.getName());
                            } else {
                                map.put("startUserName", position.getName() + "(" + orgUnit.getName() + ")");
                            }
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.error("获取流程实例[" + processInstanceId + "]的活动节点名称失败", e);
            }
            items.add(map);
        }
        int totalpages = (int)totalCount / rows + 1;
        return Y9Page.success(page, totalpages, totalCount, items, "获取列表成功");
    }

    /**
     * 设置流程实例为办结的状态，其实是先暂停，再设置流程结束时间为当前时间
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     */
    @Override
    @PostMapping(value = "/setUpCompleted", produces = MediaType.APPLICATION_JSON_VALUE)
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
     */
    @Override
    @PostMapping(value = "/setVariable", produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Object> setVariable(@RequestParam String tenantId, @RequestParam String processInstanceId,
        @RequestParam String key, @RequestBody Map<String, Object> map) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        customRuntimeService.setVariable(processInstanceId, key, map.get("val"));
        return Y9Result.success();
    }

    /**
     * 根据流程实例id设置流程变量
     *
     * @param tenantId 租户id
     * @param executionId 执行实例id
     * @param map 变量map
     */
    @Override
    @PostMapping(value = "/setVariables", produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
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
     * @param userId 人员id
     * @param processDefinitionKey 流程定义key
     * @param systemName 系统名称
     * @param map 变量map
     * @return ProcessInstanceModel
     */
    @Override
    @PostMapping(value = "/startProcessInstanceByKey", produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<ProcessInstanceModel> startProcessInstanceByKey(@RequestParam String tenantId,
        @RequestParam String userId, @RequestParam String processDefinitionKey, @RequestParam String systemName,
        @RequestBody Map<String, Object> map) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.get(tenantId, userId).getData();
        if (person != null && StringUtils.isNotBlank(person.getId())) {
            Y9LoginUserHolder.setPerson(person);
            ProcessInstance pi = customRuntimeService.startProcessInstanceByKey(processDefinitionKey, systemName, map);
            return Y9Result.success(FlowableModelConvertUtil.processInstance2Model(pi));
        } else {
            Y9LoginUserHolder.setPositionId(userId);
            ProcessInstance pi =
                customRuntimeService.startProcessInstanceByKey4Position(processDefinitionKey, systemName, map);
            return Y9Result.success(FlowableModelConvertUtil.processInstance2Model(pi));
        }
    }

    /**
     * 判断是否是挂起实例
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return Boolean
     */
    @Override
    @GetMapping(value = "/suspendedByProcessInstanceId", produces = MediaType.APPLICATION_JSON_VALUE)
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
     */
    @Override
    @PostMapping(value = "/switchSuspendOrActive", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Object> switchSuspendOrActive(@RequestParam String tenantId, @RequestParam String processInstanceId,
        @RequestParam String state) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        customRuntimeService.switchSuspendOrActive(processInstanceId, state);
        return Y9Result.success();
    }
}
