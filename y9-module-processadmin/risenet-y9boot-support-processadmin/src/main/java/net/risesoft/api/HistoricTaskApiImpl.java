package net.risesoft.api;

import lombok.RequiredArgsConstructor;
import net.risesoft.api.processadmin.HistoricTaskApi;
import net.risesoft.model.processadmin.HistoricTaskInstanceModel;
import net.risesoft.service.CustomHistoricTaskService;
import net.risesoft.service.FlowableTenantInfoHolder;
import net.risesoft.util.FlowableModelConvertUtil;
import net.risesoft.y9.Y9LoginUserHolder;
import org.apache.commons.lang3.StringUtils;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 历史任务相关接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/historicTask")
public class HistoricTaskApiImpl implements HistoricTaskApi {

    private final CustomHistoricTaskService customHistoricTaskService;

    /**
     * 根据流程实例id,获取任务
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param year 年份
     * @return List<HistoricTaskInstanceModel>
     */
    @Override
    @GetMapping(value = "/findTaskByProcessInstanceIdOrByEndTimeAsc", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<HistoricTaskInstanceModel> findTaskByProcessInstanceIdOrByEndTimeAsc(@RequestParam String tenantId, @RequestParam String processInstanceId, @RequestParam String year) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        if (StringUtils.isBlank(processInstanceId)) {
            return new ArrayList<HistoricTaskInstanceModel>();
        }
        List<HistoricTaskInstance> list = customHistoricTaskService.getByProcessInstanceIdOrderByEndTimeAsc(processInstanceId, year);
        List<HistoricTaskInstanceModel> htiList = FlowableModelConvertUtil.historicTaskInstanceList2ModelList(list);
        return htiList;
    }

    /**
     * 根据流程实例id,开始时间升序获取任务
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param year 年份
     * @return List<HistoricTaskInstanceModel>
     */
    @Override
    @GetMapping(value = "/findTaskByProcessInstanceIdOrderByStartTimeAsc", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<HistoricTaskInstanceModel> findTaskByProcessInstanceIdOrderByStartTimeAsc(@RequestParam String tenantId, @RequestParam String processInstanceId, @RequestParam String year) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        if (StringUtils.isBlank(processInstanceId)) {
            return new ArrayList<HistoricTaskInstanceModel>();
        }
        List<HistoricTaskInstance> list = customHistoricTaskService.getByProcessInstanceIdOrderByStartTimeAsc(processInstanceId, year);
        List<HistoricTaskInstanceModel> htiList = FlowableModelConvertUtil.historicTaskInstanceList2ModelList(list);
        return htiList;
    }

    /**
     * 根据任务Id获取任务实例
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @return HistoricTaskInstanceModel
     */
    @Override
    @GetMapping(value = "/getById", produces = MediaType.APPLICATION_JSON_VALUE)
    public HistoricTaskInstanceModel getById(@RequestParam String tenantId, @RequestParam String taskId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        HistoricTaskInstance hti = customHistoricTaskService.getById(taskId);
        HistoricTaskInstanceModel htiModel = FlowableModelConvertUtil.historicTaskInstance2Model(hti);
        return htiModel;
    }

    /**
     * 根据流程实例获取所有历史任务实例
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param year 年份
     * @return List<HistoricTaskInstanceModel>
     */
    @Override
    @GetMapping(value = "/getByProcessInstanceId", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<HistoricTaskInstanceModel> getByProcessInstanceId(@RequestParam String tenantId, @RequestParam String processInstanceId, @RequestParam String year) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        List<HistoricTaskInstance> list = customHistoricTaskService.getByProcessInstanceId(processInstanceId, year);
        List<HistoricTaskInstanceModel> htiList = FlowableModelConvertUtil.historicTaskInstanceList2ModelList(list);
        return htiList;
    }

    /**
     * 根据流程实例获取所有历史任务实例-按照办结时间倒序
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param year 年份
     * @return List<HistoricTaskInstanceModel>
     */
    @Override
    @GetMapping(value = "/getByProcessInstanceIdOrderByEndTimeDesc", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<HistoricTaskInstanceModel> getByProcessInstanceIdOrderByEndTimeDesc(@RequestParam String tenantId, @RequestParam String processInstanceId, @RequestParam String year) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        List<HistoricTaskInstance> list = customHistoricTaskService.getByProcessInstanceIdOrderByEndTimeDesc(processInstanceId, year);
        List<HistoricTaskInstanceModel> htiList = FlowableModelConvertUtil.historicTaskInstanceList2ModelList(list);
        return htiList;
    }

    /**
     * 根据执行实例获取已经办理完成的任务数量
     *
     * @param tenantId 租户id
     * @param executionId 执行实例id
     * @return long
     */
    @Override
    @GetMapping(value = "/getFinishedCountByExecutionId", produces = MediaType.APPLICATION_JSON_VALUE)
    public long getFinishedCountByExecutionId(@RequestParam String tenantId, @RequestParam String executionId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        return customHistoricTaskService.getFinishedCountByExecutionId(executionId);
    }

    /**
     * 获取当前任务的上一个任务节点，当前任务只可以是正在运行的任务实例
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @return HistoricTaskInstanceModel
     */
    @Override
    @GetMapping(value = "/getThePreviousTask", produces = MediaType.APPLICATION_JSON_VALUE)
    public HistoricTaskInstanceModel getThePreviousTask(@RequestParam String tenantId, @RequestParam String taskId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        HistoricTaskInstance hti = customHistoricTaskService.getThePreviousTask(taskId);
        HistoricTaskInstanceModel htiModel = FlowableModelConvertUtil.historicTaskInstance2Model(hti);
        return htiModel;
    }

    /**
     * 获取当前任务的上一个任务节点产生的所有任务，当前任务只可以是正在运行的任务实例
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @return List<HistoricTaskInstanceModel>
     */
    @Override
    @GetMapping(value = "/getThePreviousTasks", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<HistoricTaskInstanceModel> getThePreviousTasks(@RequestParam String tenantId, @RequestParam String taskId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        List<HistoricTaskInstance> htiList = customHistoricTaskService.getThePreviousTasks(taskId);
        List<HistoricTaskInstanceModel> htiModelList = FlowableModelConvertUtil.historicTaskInstanceList2ModelList(htiList);
        return htiModelList;
    }

    /**
     * 设置历史任务TANENT_ID_字段，存放协办任务是否被强制办结标识
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     */
    @Override
    @PostMapping(value = "/setTenantId", produces = MediaType.APPLICATION_JSON_VALUE)
    public void setTenantId(@RequestParam String tenantId, @RequestParam String taskId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        customHistoricTaskService.setTenantId(taskId);
    }
}
