package net.risesoft.api;

import lombok.RequiredArgsConstructor;
import net.risesoft.api.processadmin.HistoricActivityApi;
import net.risesoft.model.processadmin.HistoricActivityInstanceModel;
import net.risesoft.service.CustomHistoricActivityService;
import net.risesoft.service.FlowableTenantInfoHolder;
import net.risesoft.util.FlowableModelConvertUtil;
import org.flowable.engine.history.HistoricActivityInstance;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 获取历史节点实例
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/historicActivity")
public class HistoricActivityApiImpl implements HistoricActivityApi {

    private final CustomHistoricActivityService customHistoricActivityService;

    /**
     * 根据流程实例获取历史节点实例
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return List<HistoricActivityInstanceModel>
     */
    @Override
    @GetMapping(value = "/getByProcessInstanceId", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<HistoricActivityInstanceModel> getByProcessInstanceId(@RequestParam String tenantId, @RequestParam String processInstanceId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        List<HistoricActivityInstance> list = customHistoricActivityService.getByProcessInstanceId(processInstanceId);
        return FlowableModelConvertUtil.historicActivityInstanceList2Model(list);
    }

    /**
     * 根据年份，流程实例获取历史节点实例
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param year 年度
     * @return List<HistoricActivityInstanceModel>
     */
    @Override
    @GetMapping(value = "/getByProcessInstanceIdAndYear", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<HistoricActivityInstanceModel> getByProcessInstanceIdAndYear(@RequestParam String tenantId, @RequestParam String processInstanceId, @RequestParam String year) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        List<HistoricActivityInstance> list = customHistoricActivityService.getByProcessInstanceIdAndYear(processInstanceId, year);
        return FlowableModelConvertUtil.historicActivityInstanceList2Model(list);
    }

}
