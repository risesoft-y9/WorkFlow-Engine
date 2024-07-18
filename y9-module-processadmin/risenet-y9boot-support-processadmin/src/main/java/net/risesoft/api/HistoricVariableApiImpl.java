package net.risesoft.api;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.flowable.variable.api.history.HistoricVariableInstance;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.processadmin.HistoricVariableApi;
import net.risesoft.model.processadmin.HistoricVariableInstanceModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.CustomHistoricVariableService;
import net.risesoft.util.FlowableModelConvertUtil;
import net.risesoft.y9.FlowableTenantInfoHolder;

/**
 * 历史变量相关接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/historicVariable")
public class HistoricVariableApiImpl implements HistoricVariableApi {

    private final CustomHistoricVariableService customHistoricVariableService;

    /**
     * 根据流程实例Id,获取历史流程变量集合
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<List<HistoricVariableInstanceModel>>} 通用请求返回对象 - data 历史任务变量的值列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<HistoricVariableInstanceModel>> getByProcessInstanceId(@RequestParam String tenantId,
        @RequestParam String processInstanceId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        List<HistoricVariableInstance> hviList =
            customHistoricVariableService.listByProcessInstanceId(processInstanceId);
        return Y9Result.success(FlowableModelConvertUtil.historicVariableInstanceList2ModelList(hviList));
    }

    /**
     * 根据流程实例Id和流程变量的Key,获取历史流程变量的值
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param variableName 变量名
     * @param year 年份
     * @return {@code Y9Result<HistoricVariableInstanceModel>} 通用请求返回对象 - data 历史任务变量的值
     * @since 9.6.6
     */
    @Override
    public Y9Result<HistoricVariableInstanceModel> getByProcessInstanceIdAndVariableName(@RequestParam String tenantId,
        @RequestParam String processInstanceId, @RequestParam String variableName, String year) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        HistoricVariableInstance hvi =
            customHistoricVariableService.getByProcessInstanceIdAndVariableName(processInstanceId, variableName, year);
        if (hvi == null) {
            return Y9Result.failure("该流程实例下没有该变量");
        }
        return Y9Result.success(FlowableModelConvertUtil.historicVariableInstance2Model(hvi));
    }

    /**
     * 根据任务Id,获取历史任务变量的值集合
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @return {@code Y9Result<List<HistoricVariableInstanceModel>>} 通用请求返回对象 - data 历史任务变量的值列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<HistoricVariableInstanceModel>> getByTaskId(@RequestParam String tenantId,
        @RequestParam String taskId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        List<HistoricVariableInstance> hviList = customHistoricVariableService.listByTaskId(taskId);
        return Y9Result.success(FlowableModelConvertUtil.historicVariableInstanceList2ModelList(hviList));
    }

    /**
     * 根据任务Id和变量Key,获取历史任务变量的值
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param variableName 变量名
     * @param year 年份
     * @return {@code Y9Result<HistoricVariableInstanceModel>} 通用请求返回对象 - data 历史任务变量的值
     * @since 9.6.6
     */
    @Override
    public Y9Result<HistoricVariableInstanceModel> getByTaskIdAndVariableName(@RequestParam String tenantId,
        @RequestParam String taskId, @RequestParam String variableName, String year) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        HistoricVariableInstance hvi =
            customHistoricVariableService.getByTaskIdAndVariableName(taskId, variableName, year);
        if (hvi == null) {
            return Y9Result.failure("流程变量不存在");
        }
        return Y9Result.success(FlowableModelConvertUtil.historicVariableInstance2Model(hvi));
    }

    /**
     * 根据流程实例Id,获取指定的流程变量
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param keys 变量集合
     * @return {@code Y9Result<Map<String, Object>>} 通用请求返回对象 - data 流程变量
     * @since 9.6.6
     */
    @Override
    public Y9Result<Map<String, Object>> getVariables(@RequestParam String tenantId,
        @RequestParam String processInstanceId, @RequestBody Collection<String> keys) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        return Y9Result.success(customHistoricVariableService.getVariables(tenantId, processInstanceId, keys));
    }
}
