package net.risesoft.api;

import lombok.RequiredArgsConstructor;
import net.risesoft.api.processadmin.RepositoryApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.model.processadmin.ProcessDefinitionModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.CustomRepositoryService;
import net.risesoft.service.FlowableTenantInfoHolder;
import net.risesoft.util.FlowableModelConvertUtil;
import org.apache.commons.io.IOUtils;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * 部署流程相关接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/repository")
public class RepositoryApiImpl implements RepositoryApi {

    private final  CustomRepositoryService customRepositoryService;

    /**
     * 删除部署的流程
     *
     * @param tenantId 租户id
     * @param deploymentId 部署id
     * @return Map<String, Object>
     */
    @Override
    @PostMapping(value = "/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> delete(@RequestParam String tenantId, @RequestParam String deploymentId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Map<String, Object> map = customRepositoryService.delete(deploymentId);
        return map;
    }

    /**
     * 部署流程
     *
     * @param tenantId 租户id
     * @param file 流程文件
     * @return Y9Result<String>
     */
    @Override
    @PostMapping(value = "/deploy", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<String> deploy(@RequestParam String tenantId, MultipartFile file) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Map<String, Object> map = customRepositoryService.deploy(file);
        if ((boolean)map.get(UtilConsts.SUCCESS)) {
            return Y9Result.successMsg((String)map.get("msg"));
        }
        return Y9Result.failure((String)map.get("msg"));
    }

    /**
     * 根据流程定义key获取最新部署的流程定义
     *
     * @param tenantId 租户id
     * @param processDefinitionKey 流程定义key
     * @return ProcessDefinitionModel
     */
    @Override
    @GetMapping(value = "/getLatestProcessDefinitionByKey", produces = MediaType.APPLICATION_JSON_VALUE)
    public ProcessDefinitionModel getLatestProcessDefinitionByKey(@RequestParam String tenantId, @RequestParam String processDefinitionKey) {
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
    public List<ProcessDefinitionModel> getLatestProcessDefinitionList(@RequestParam String tenantId) {
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
    public ProcessDefinitionModel getPreviousProcessDefinitionById(@RequestParam String tenantId, @RequestParam String processDefinitionId) {
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
    public ProcessDefinitionModel getProcessDefinitionById(@RequestParam String tenantId, @RequestParam String processDefinitionId) {
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
    public List<ProcessDefinitionModel> getProcessDefinitionListByKey(@RequestParam String tenantId, @RequestParam String processDefinitionKey) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        List<ProcessDefinition> pdList = customRepositoryService.getProcessDefinitionListByKey(processDefinitionKey);
        List<ProcessDefinitionModel> pdModelList = FlowableModelConvertUtil.processDefinitionList2ModelList(pdList);
        return pdModelList;
    }

    /**
     * 获取流程定义xml
     * @param tenantId 租户id
     * @param resourceType xml
     * @param processInstanceId 流程实例id
     * @param processDefinitionId 流程定义id
     * @return Y9Result<String>
     * @throws Exception
     */
    @Override
    @GetMapping(value = "/getXmlByProcessInstance", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<String> getXmlByProcessInstance(@RequestParam String tenantId, @RequestParam String resourceType, @RequestParam String processInstanceId, @RequestParam String processDefinitionId) throws Exception {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        InputStream resourceAsStream = customRepositoryService.getProcessInstance(resourceType, processInstanceId, processDefinitionId);
        return Y9Result.success(IOUtils.toString(resourceAsStream, StandardCharsets.UTF_8));
    }

    /**
     * 获取已部署流程定义列表
     *
     * @param tenantId 租户id
     * @return Y9Result<List<Map<String, Object>>>
     */
    @SuppressWarnings("unchecked")
    @Override
    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<List<Map<String, Object>>> list(@RequestParam String tenantId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Map<String, Object> map = customRepositoryService.list("");
        if ((boolean)map.get(UtilConsts.SUCCESS)) {
            return Y9Result.success((List<Map<String, Object>>)map.get("rows"), "获取成功");
        }
        return Y9Result.failure("获取失败");
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
    public Map<String, Object> switchSuspendOrActive(@RequestParam String tenantId, @RequestParam String state, @RequestParam String processDefinitionId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Map<String, Object> map = customRepositoryService.switchSuspendOrActive(state, processDefinitionId);
        return map;
    }
}
