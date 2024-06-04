package net.risesoft.api;

import lombok.RequiredArgsConstructor;
import net.risesoft.api.processadmin.HistoricVariableApi;
import net.risesoft.model.processadmin.HistoricVariableInstanceModel;
import net.risesoft.service.CustomHistoricVariableService;
import net.risesoft.service.FlowableTenantInfoHolder;
import net.risesoft.util.FlowableModelConvertUtil;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.Map;

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
     * @return List<HistoricVariableInstanceModel>
     */
    @Override
    @GetMapping(value = "/getByProcessInstanceId", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<HistoricVariableInstanceModel> getByProcessInstanceId(@RequestParam String tenantId, @RequestParam String processInstanceId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        List<HistoricVariableInstance> hviList = customHistoricVariableService.getByProcessInstanceId(processInstanceId);
        List<HistoricVariableInstanceModel> hviModelList = FlowableModelConvertUtil.historicVariableInstanceList2ModelList(hviList);
        return hviModelList;
    }

    /**
     * 根据流程实例Id和流程变量的Key,获取历史流程变量的值
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param variableName 变量名
     * @param year 年份
     * @return HistoricVariableInstanceModel
     */
    @Override
    @GetMapping(value = "/getByProcessInstanceIdAndVariableName", produces = MediaType.APPLICATION_JSON_VALUE)
    public HistoricVariableInstanceModel getByProcessInstanceIdAndVariableName(@RequestParam String tenantId, @RequestParam String processInstanceId, @RequestParam String variableName, @RequestParam String year) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        HistoricVariableInstance hvi = customHistoricVariableService.getByProcessInstanceIdAndVariableName(processInstanceId, variableName, year);
        HistoricVariableInstanceModel model = new HistoricVariableInstanceModel();
        if (hvi != null) {
            model = FlowableModelConvertUtil.historicVariableInstance2Model(hvi);
        }
        return model;
    }

    /**
     * 根据流程实例Id,获取历史任务变量的值集合
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @return List<HistoricVariableInstanceModel>
     */
    @Override
    @GetMapping(value = "/getByTaskId", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<HistoricVariableInstanceModel> getByTaskId(@RequestParam String tenantId, @RequestParam String taskId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        List<HistoricVariableInstance> hviList = customHistoricVariableService.getByTaskId(taskId);
        List<HistoricVariableInstanceModel> hviModelList = FlowableModelConvertUtil.historicVariableInstanceList2ModelList(hviList);
        return hviModelList;
    }

    /**
     * 根据流程实例Id和流程变量的Key,获取历史任务变量的值
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param variableName 变量名
     * @param year 年份
     * @return HistoricVariableInstanceModel
     */
    @Override
    @GetMapping(value = "/getByTaskIdAndVariableName", produces = MediaType.APPLICATION_JSON_VALUE)
    public HistoricVariableInstanceModel getByTaskIdAndVariableName(@RequestParam String tenantId, @RequestParam String taskId, @RequestParam String variableName, @RequestParam String year) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        HistoricVariableInstance hvi = customHistoricVariableService.getByTaskIdAndVariableName(taskId, variableName, year);
        HistoricVariableInstanceModel model = new HistoricVariableInstanceModel();
        if (hvi != null) {
            model = FlowableModelConvertUtil.historicVariableInstance2Model(hvi);
            return model;
        }
        return null;
    }

    /**
     * 根据流程实例Id,获取指定的流程变量
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param keys 变量集合
     * @return Map<String, Object>
     */
    @Override
    @GetMapping(value = "/getVariables", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getVariables(@RequestParam String tenantId, @RequestParam String processInstanceId, @RequestBody Collection<String> keys) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        return customHistoricVariableService.getVariables(tenantId, processInstanceId, keys);
    }
}
