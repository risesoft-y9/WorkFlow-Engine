package net.risesoft.api;

import java.util.List;
import java.util.Map;

import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.processadmin.RepositoryApi;
import net.risesoft.model.processadmin.ProcessDefinitionModel;
import net.risesoft.service.CustomRepositoryService;
import net.risesoft.service.FlowableTenantInfoHolder;
import net.risesoft.util.FlowableModelConvertUtil;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@RestController
@RequestMapping(value = "/services/rest/repository")
public class RepositoryApiImpl implements RepositoryApi {

    @Autowired
    private CustomRepositoryService customRepositoryService;

    /**
     * 删除部署的流程
     *
     * @param tenantId 租户id
     * @param deploymentId 部署id
     * @return Map<String, Object>
     */
    @Override
    @PostMapping(value = "/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> delete(String tenantId, String deploymentId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Map<String, Object> map = customRepositoryService.delete(deploymentId);
        return map;
    }

    /*
     * @Override public Map<String, Object> deploy(String tenantId, String fileName,
     * InputStream fileInputStream) {
     * FlowableTenantInfoHolder.setTenantId(tenantId); Map<String, Object> map =
     * customRepositoryService.deploy(fileName, fileInputStream); return map; }
     */

    /**
     * 根据流程定义key获取最新部署的流程定义
     *
     * @param tenantId 租户id
     * @param processDefinitionKey 流程定义key
     * @return ProcessDefinitionModel
     */
    @Override
    @GetMapping(value = "/getLatestProcessDefinitionByKey", produces = MediaType.APPLICATION_JSON_VALUE)
    public ProcessDefinitionModel getLatestProcessDefinitionByKey(String tenantId, String processDefinitionKey) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        ProcessDefinition pd = customRepositoryService.getLatestProcessDefinitionByKey(processDefinitionKey);
        ProcessDefinitionModel pdModel = FlowableModelConvertUtil.processDefinition2Model(pd);
        return pdModel;
    }

    /**
     * 获取所有流程定义最新版本的集合
     *
     * @param tenantId 租户id
     * @return List<ProcessDefinitionModel>
     */
    @Override
    @GetMapping(value = "/getLatestProcessDefinitionList", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ProcessDefinitionModel> getLatestProcessDefinitionList(String tenantId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        List<ProcessDefinition> pdList = customRepositoryService.getLatestProcessDefinitionList();
        List<ProcessDefinitionModel> pdModelList = FlowableModelConvertUtil.processDefinitionList2ModelList(pdList);
        return pdModelList;
    }

    /**
     * 根据流程定义Id获取上一个版本的流程定义，如果当前版本是1，则返回自己
     *
     * @param tenantId 租户id
     * @param processDefinitionId 流程定义Id
     * @return ProcessDefinitionModel
     */
    @Override
    @GetMapping(value = "/getPreviousProcessDefinitionById", produces = MediaType.APPLICATION_JSON_VALUE)
    public ProcessDefinitionModel getPreviousProcessDefinitionById(String tenantId, String processDefinitionId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        ProcessDefinition pd = customRepositoryService.getPreviousProcessDefinitionById(processDefinitionId);
        ProcessDefinitionModel pdModel = FlowableModelConvertUtil.processDefinition2Model(pd);
        return pdModel;
    }

    /**
     * 根据流程定义Id获取流程定义
     *
     * @param tenantId 租户id
     * @param processDefinitionId 流程定义Id
     * @return ProcessDefinitionModel
     */
    @Override
    @GetMapping(value = "/getProcessDefinitionById", produces = MediaType.APPLICATION_JSON_VALUE)
    public ProcessDefinitionModel getProcessDefinitionById(String tenantId, String processDefinitionId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        ProcessDefinition pd = customRepositoryService.getProcessDefinitionById(processDefinitionId);
        ProcessDefinitionModel pdModel = FlowableModelConvertUtil.processDefinition2Model(pd);
        return pdModel;
    }

    /**
     * 根据流程定义key获取最新部署的流程定义
     *
     * @param tenantId 租户id
     * @param processDefinitionKey 流程定义key
     * @return List<ProcessDefinitionModel>
     */
    @Override
    @GetMapping(value = "/getProcessDefinitionListByKey", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ProcessDefinitionModel> getProcessDefinitionListByKey(String tenantId, String processDefinitionKey) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        List<ProcessDefinition> pdList = customRepositoryService.getProcessDefinitionListByKey(processDefinitionKey);
        List<ProcessDefinitionModel> pdModelList = FlowableModelConvertUtil.processDefinitionList2ModelList(pdList);
        return pdModelList;
    }

    /**
     * 激活/挂起流程的状态
     *
     * @param tenantId 租户id
     * @param state 状态
     * @param processDefinitionId 流程定义Id
     * @return Map<String, Object>
     */
    @Override
    @PostMapping(value = "/switchSuspendOrActive", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> switchSuspendOrActive(String tenantId, String state, String processDefinitionId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Map<String, Object> map = customRepositoryService.switchSuspendOrActive(state, processDefinitionId);
        return map;
    }
}
