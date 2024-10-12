package net.risesoft.api;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.processadmin.RepositoryApi;
import net.risesoft.model.processadmin.ProcessDefinitionModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.CustomRepositoryService;
import net.risesoft.util.FlowableModelConvertUtil;
import net.risesoft.y9.FlowableTenantInfoHolder;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * 部署流程相关接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/repository", produces = MediaType.APPLICATION_JSON_VALUE)
public class RepositoryApiImpl implements RepositoryApi {

    private final CustomRepositoryService customRepositoryService;

    /**
     * 删除部署的流程
     *
     * @param tenantId 租户id
     * @param deploymentId 部署id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> delete(@RequestParam String tenantId, @RequestParam String deploymentId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        return customRepositoryService.delete(deploymentId);
    }

    /**
     * 部署流程
     *
     * @param tenantId 租户id
     * @param file 流程文件
     * @return {@code Y9Result<String>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> deploy(@RequestParam String tenantId, MultipartFile file) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        return customRepositoryService.deploy(file);
    }

    /**
     * 根据流程定义key获取最新部署的流程定义
     *
     * @param tenantId 租户id
     * @param processDefinitionKey 流程定义key
     * @return {@code Y9Result<ProcessDefinitionModel>} 通用请求返回对象 - data 是最新部署的流程定义
     * @since 9.6.6
     */
    @Override
    public Y9Result<ProcessDefinitionModel> getLatestProcessDefinitionByKey(@RequestParam String tenantId,
        @RequestParam String processDefinitionKey) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        ProcessDefinition pd = customRepositoryService.getLatestProcessDefinitionByKey(processDefinitionKey);
        ProcessDefinitionModel model = null;
        if (pd != null) {
            model = new ProcessDefinitionModel();
            Y9BeanUtil.copyProperties(pd, model);
        }
        return Y9Result.success(model);
    }

    /**
     * 获取所有流程定义最新版本的集合
     *
     * @param tenantId 租户id
     * @return {@code Y9Result<List<ProcessDefinitionModel>>} 通用请求返回对象 - data 是最新部署的流程定义列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<ProcessDefinitionModel>> getLatestProcessDefinitionList(@RequestParam String tenantId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        List<ProcessDefinition> pdList = customRepositoryService.listLatestProcessDefinition();
        return Y9Result.success(FlowableModelConvertUtil.processDefinitionList2ModelList(pdList));
    }

    /**
     * 根据流程定义Id获取上一个版本的流程定义，如果当前版本是1，则返回自己
     *
     * @param tenantId 租户id
     * @param processDefinitionId 流程定义Id
     * @return {@code Y9Result<ProcessDefinitionModel>} 通用请求返回对象 - data 是流程定义信息
     * @since 9.6.6
     */
    @Override
    public Y9Result<ProcessDefinitionModel> getPreviousProcessDefinitionById(@RequestParam String tenantId,
        @RequestParam String processDefinitionId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        ProcessDefinition pd = customRepositoryService.getPreviousProcessDefinitionById(processDefinitionId);
        ProcessDefinitionModel model = null;
        if (pd != null) {
            model = new ProcessDefinitionModel();
            Y9BeanUtil.copyProperties(pd, model);
        }
        return Y9Result.success(model);
    }

    /**
     * 根据流程定义Id获取流程定义
     *
     * @param tenantId 租户id
     * @param processDefinitionId 流程定义Id
     * @return {@code Y9Result<ProcessDefinitionModel>} 通用请求返回对象 - data 是流程定义信息
     * @since 9.6.6
     */
    @Override
    public Y9Result<ProcessDefinitionModel> getProcessDefinitionById(@RequestParam String tenantId,
        @RequestParam String processDefinitionId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        ProcessDefinition pd = customRepositoryService.getProcessDefinitionById(processDefinitionId);
        ProcessDefinitionModel model = null;
        if (pd != null) {
            model = new ProcessDefinitionModel();
            Y9BeanUtil.copyProperties(pd, model);
        }
        return Y9Result.success(model);
    }

    /**
     * 根据流程定义key获取最新部署的流程定义
     *
     * @param tenantId 租户id
     * @param processDefinitionKey 流程定义key
     * @return {@code Y9Result<List<ProcessDefinitionModel>>} 通用请求返回对象 - data 是流程定义信息
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<ProcessDefinitionModel>> getProcessDefinitionListByKey(@RequestParam String tenantId,
        @RequestParam String processDefinitionKey) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        List<ProcessDefinition> pdList = customRepositoryService.listProcessDefinitionByKey(processDefinitionKey);
        return Y9Result.success(FlowableModelConvertUtil.processDefinitionList2ModelList(pdList));
    }

    /**
     * 获取流程定义xml
     *
     * @param tenantId 租户id
     * @param resourceType xml
     * @param processInstanceId 流程实例id
     * @param processDefinitionId 流程定义id
     * @return {@code Y9Result<String>} 通用请求返回对象 - data 是流程定义xml
     * @since 9.6.6
     */
    @Override
    public Y9Result<String> getXmlByProcessInstance(@RequestParam String tenantId, @RequestParam String resourceType,
        @RequestParam String processInstanceId, @RequestParam String processDefinitionId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        InputStream resourceAsStream =
            customRepositoryService.getProcessInstance(resourceType, processInstanceId, processDefinitionId);
        try {
            String xmlStr = IOUtils.toString(resourceAsStream, StandardCharsets.UTF_8);
            return Y9Result.success(xmlStr);
        } catch (IOException e) {
            LOGGER.error("获取流程定义xml失败", e);
        }
        return Y9Result.failure("获取流程定义xml失败");
    }

    /**
     * 获取已部署流程定义列表
     *
     * @param tenantId 租户id
     * @return {@code Y9Result<List<Map<String, Object>>>} 通用请求返回对象 - data 是流程定义信息
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<ProcessDefinitionModel>> list(@RequestParam String tenantId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        return customRepositoryService.list("");
    }

    /**
     * 激活/挂起流程的状态
     *
     * @param tenantId 租户id
     * @param state 状态
     * @param processDefinitionId 流程定义Id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> switchSuspendOrActive(@RequestParam String tenantId, @RequestParam String state,
        @RequestParam String processDefinitionId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        return customRepositoryService.switchSuspendOrActive(state, processDefinitionId);
    }
}
