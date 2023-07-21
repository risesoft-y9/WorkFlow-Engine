package net.risesoft.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.impl.HistoricProcessInstanceQueryProperty;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.org.PersonApi;
import net.risesoft.api.org.PositionApi;
import net.risesoft.api.processadmin.RuntimeApi;
import net.risesoft.model.Person;
import net.risesoft.model.Position;
import net.risesoft.model.processadmin.ExecutionModel;
import net.risesoft.model.processadmin.ProcessInstanceModel;
import net.risesoft.service.CustomRuntimeService;
import net.risesoft.service.CustomTaskService;
import net.risesoft.service.FlowableTenantInfoHolder;
import net.risesoft.util.FlowableModelConvertUtil;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@RestController
@RequestMapping(value = "/services/rest/runtime")
public class RuntimeApiImpl implements RuntimeApi {

    @Autowired
    private CustomRuntimeService customRuntimeService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private PersonApi personManager;

    @Autowired
    private PositionApi positionManager;

    @Autowired
    private CustomTaskService customTaskService;

    /**
     * 加签
     *
     * @param tenantId 租户id
     * @param activityId 活动节点id
     * @param parentExecutionId 父执行实例id
     * @throws Exception Exception
     */
    @Override
    @PostMapping(value = "/addMultiInstanceExecution", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void addMultiInstanceExecution(String tenantId, String activityId, String parentExecutionId, @RequestBody Map<String, Object> map) throws Exception {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        customRuntimeService.addMultiInstanceExecution(activityId, parentExecutionId, map);
    }

    /**
     * 加签/岗位
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param processInstanceId 流程实例id
     * @param taskId 任务id
     * @throws Exception Exception
     */
    @Override
    @PostMapping(value = "/complete4Position", produces = MediaType.APPLICATION_JSON_VALUE)
    public void complete4Position(String tenantId, String positionId, String processInstanceId, String taskId) throws Exception {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionManager.getPosition(tenantId, positionId);
        Y9LoginUserHolder.setPosition(position);
        customTaskService.complete4Position(processInstanceId, taskId);
    }

    /**
     * 真办结
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processInstanceId 流程实例id
     * @throws Exception Exception
     */
    @Override
    @PostMapping(value = "/completed", produces = MediaType.APPLICATION_JSON_VALUE)
    public void completed(String tenantId, String userId, String processInstanceId, String taskId) throws Exception {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId);
        Y9LoginUserHolder.setPerson(person);
        customTaskService.complete(processInstanceId, taskId);
    }

    /**
     * 减签
     *
     * @param tenantId 租户id
     * @param executionId 执行实例id
     * @throws Exception Exception
     */
    @Override
    @PostMapping(value = "/deleteMultiInstanceExecution", produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteMultiInstanceExecution(String tenantId, String executionId) throws Exception {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        customRuntimeService.deleteMultiInstanceExecution(executionId);
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
    public List<String> getActiveActivityIds(String tenantId, String executionId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        List<String> list = customRuntimeService.getActiveActivityIds(executionId);
        return list;
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
    public ExecutionModel getExecutionById(String tenantId, String executionId) {
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
    public List<ProcessInstanceModel> getListBySuperProcessInstanceId(String tenantId, String superProcessInstanceId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        List<ProcessInstance> list = customRuntimeService.getListBySuperProcessInstanceId(superProcessInstanceId);
        List<ProcessInstanceModel> piModelList = FlowableModelConvertUtil.processInstanceList2ModelList(list);
        return piModelList;
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
    public ProcessInstanceModel getProcessInstance(String tenantId, String processInstanceId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        ProcessInstance pi = customRuntimeService.getProcessInstance(processInstanceId);
        if (null != pi) {
            ProcessInstanceModel pim = FlowableModelConvertUtil.processInstance2Model(pi);
            return pim;
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
    public Map<String, Object> getProcessInstancesByDefId(String tenantId, String processDefinitionId, Integer page, Integer rows) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Map<String, Object> returnMap = new HashMap<String, Object>(16);
        long totalCount = runtimeService.createProcessInstanceQuery().processDefinitionId(processDefinitionId).count();
        List<ProcessInstance> list = runtimeService.createProcessInstanceQuery().processDefinitionId(processDefinitionId).orderBy(HistoricProcessInstanceQueryProperty.START_TIME).desc().listPage((page - 1) * rows, rows);
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
    public List<ProcessInstanceModel> getProcessInstancesByKey(String tenantId, String processDefinitionKey) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        List<ProcessInstance> list = customRuntimeService.getProcessInstancesByKey(processDefinitionKey);
        List<ProcessInstanceModel> piModelList = FlowableModelConvertUtil.processInstanceList2ModelList(list);
        return piModelList;
    }

    /**
     * 真办结后恢复流程实例为待办状态
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @throws Exception Exception
     */
    @Override
    @PostMapping(value = "/recovery4Completed", produces = MediaType.APPLICATION_JSON_VALUE)
    public void recovery4Completed(String tenantId, String userId, String processInstanceId, String year) throws Exception {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId);
        if (person != null && StringUtils.isNotBlank(person.getId())) {
            Y9LoginUserHolder.setPerson(person);
            customRuntimeService.recovery4Completed(processInstanceId, year);
        } else {
            Position position = positionManager.getPosition(tenantId, userId);
            Y9LoginUserHolder.setPosition(position);
            customRuntimeService.recoveryCompleted4Position(processInstanceId, year);
        }
    }

    /**
     * 恢复流程实例为待办状态，其实是先激活，再设置流程实例的结束时间为null
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @throws Exception Exception
     */
    @Override
    @PostMapping(value = "/recovery4SetUpCompleted", produces = MediaType.APPLICATION_JSON_VALUE)
    public void recovery4SetUpCompleted(String tenantId, String processInstanceId) throws Exception {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        customRuntimeService.recovery4SetUpCompleted(processInstanceId);
    }

    /**
     * 设置流程实例为办结的状态，其实是先暂停，再设置流程结束时间为当前时间
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @throws Exception Exception
     */
    @Override
    @PostMapping(value = "/setUpCompleted", produces = MediaType.APPLICATION_JSON_VALUE)
    public void setUpCompleted(String tenantId, String processInstanceId) throws Exception {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        customRuntimeService.setUpCompleted(processInstanceId);
    }

    /**
     * 根据流程实例id设置流程变量
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processInstanceId 流程实例id
     * @param key 变量key
     * @param val 变量值
     * @throws Exception Exception
     */
    @Override
    @PostMapping(value = "/setVariable", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void setVariable(String tenantId, String processInstanceId, String key, @RequestBody Map<String, Object> map) throws Exception {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        customRuntimeService.setVariable(processInstanceId, key, map.get("val"));
    }

    /**
     * 根据流程实例id设置流程变量
     *
     * @param tenantId 租户id
     * @param executionId 执行实例id
     * @param map 变量map
     */
    @Override
    @PostMapping(value = "/setVariables", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void setVariables(String tenantId, String executionId, @RequestBody Map<String, Object> map) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        customRuntimeService.setVariables(executionId, map);
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
    @PostMapping(value = "/startProcessInstanceByKey", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ProcessInstanceModel startProcessInstanceByKey(String tenantId, String userId, String processDefinitionKey, String systemName, @RequestBody Map<String, Object> map) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId);
        if (person != null && StringUtils.isNotBlank(person.getId())) {
            Y9LoginUserHolder.setPerson(person);
            ProcessInstance pi = customRuntimeService.startProcessInstanceByKey(processDefinitionKey, systemName, map);
            ProcessInstanceModel piModel = FlowableModelConvertUtil.processInstance2Model(pi);
            return piModel;
        } else {
            Y9LoginUserHolder.setPositionId(userId);
            ProcessInstance pi = customRuntimeService.startProcessInstanceByKey4Position(processDefinitionKey, systemName, map);
            ProcessInstanceModel piModel = FlowableModelConvertUtil.processInstance2Model(pi);
            return piModel;
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
    public Boolean suspendedByProcessInstanceId(String tenantId, String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Boolean suspended = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult().isSuspended();
        return suspended;
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
    public void switchSuspendOrActive(String tenantId, String processInstanceId, String state) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        customRuntimeService.switchSuspendOrActive(processInstanceId, state);
    }
}
