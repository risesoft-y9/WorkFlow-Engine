package net.risesoft.api;

import java.util.List;

import org.flowable.engine.history.HistoricActivityInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.processadmin.HistoricActivityApi;
import net.risesoft.model.processadmin.HistoricActivityInstanceModel;
import net.risesoft.service.CustomHistoricActivityService;
import net.risesoft.service.FlowableTenantInfoHolder;
import net.risesoft.util.FlowableModelConvertUtil;

/**
 * 获取历史节点实例
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@RestController
@RequestMapping(value = "/services/rest/historicActivity")
public class HistoricActivityApiImpl implements HistoricActivityApi {

    @Autowired
    private CustomHistoricActivityService customHistoricActivityService;

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
        List<HistoricActivityInstanceModel> haiModel = FlowableModelConvertUtil.historicActivityInstanceList2Model(list);
        return haiModel;
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
        List<HistoricActivityInstanceModel> haiModel = FlowableModelConvertUtil.historicActivityInstanceList2Model(list);
        return haiModel;
    }

}
