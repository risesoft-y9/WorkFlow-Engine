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
     * @param deploymentId 部署id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> delete(@RequestParam String deploymentId) {
        return customRepositoryService.delete(deploymentId);
    }

    /**
     * 部署流程
     *
     * @param file 流程文件
     * @return {@code Y9Result<String>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> deploy(MultipartFile file) {
        return customRepositoryService.deploy(file);
    }

    /**
     * 根据流程定义key获取最新部署的流程定义
     *
     * @param processDefinitionKey 流程定义key
     * @return {@code Y9Result<ProcessDefinitionModel>} 通用请求返回对象 - data 是最新部署的流程定义
     * @since 9.6.6
     */
    @Override
    public Y9Result<ProcessDefinitionModel> getLatestProcessDefinitionByKey(@RequestParam String processDefinitionKey) {
        ProcessDefinition pd = customRepositoryService.getLatestProcessDefinitionByKey(processDefinitionKey);
        return Y9Result.success(FlowableModelConvertUtil.processDefinition2Model(pd));
    }

    /**
     * 获取所有流程定义最新版本的集合
     *
     * @return {@code Y9Result<List<ProcessDefinitionModel>>} 通用请求返回对象 - data 是最新部署的流程定义列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<ProcessDefinitionModel>> getLatestProcessDefinitionList() {
        List<ProcessDefinition> pdList = customRepositoryService.listLatestProcessDefinition();
        return Y9Result.success(FlowableModelConvertUtil.processDefinitionList2ModelList(pdList));
    }

    /**
     * 根据流程定义Id获取上一个版本的流程定义，如果当前版本是1，则返回自己
     *
     * @param processDefinitionId 流程定义Id
     * @return {@code Y9Result<ProcessDefinitionModel>} 通用请求返回对象 - data 是流程定义信息
     * @since 9.6.6
     */
    @Override
    public Y9Result<ProcessDefinitionModel> getPreviousProcessDefinitionById(@RequestParam String processDefinitionId) {
        ProcessDefinition pd = customRepositoryService.getPreviousProcessDefinitionById(processDefinitionId);
        return Y9Result.success(FlowableModelConvertUtil.processDefinition2Model(pd));
    }

    /**
     * 根据流程定义Id获取流程定义
     *
     * @param processDefinitionId 流程定义Id
     * @return {@code Y9Result<ProcessDefinitionModel>} 通用请求返回对象 - data 是流程定义信息
     * @since 9.6.6
     */
    @Override
    public Y9Result<ProcessDefinitionModel> getProcessDefinitionById(@RequestParam String processDefinitionId) {
        ProcessDefinition pd = customRepositoryService.getProcessDefinitionById(processDefinitionId);
        return Y9Result.success(FlowableModelConvertUtil.processDefinition2Model(pd));
    }

    /**
     * 根据流程定义key获取最新部署的流程定义
     *
     * @param processDefinitionKey 流程定义key
     * @return {@code Y9Result<List<ProcessDefinitionModel>>} 通用请求返回对象 - data 是流程定义信息
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<ProcessDefinitionModel>>
        getProcessDefinitionListByKey(@RequestParam String processDefinitionKey) {
        List<ProcessDefinition> pdList = customRepositoryService.listProcessDefinitionByKey(processDefinitionKey);
        return Y9Result.success(FlowableModelConvertUtil.processDefinitionList2ModelList(pdList));
    }

    /**
     * 获取流程定义xml
     *
     * @param resourceType xml
     * @param processInstanceId 流程实例id
     * @param processDefinitionId 流程定义id
     * @return {@code Y9Result<String>} 通用请求返回对象 - data 是流程定义xml
     * @since 9.6.6
     */
    @Override
    public Y9Result<String> getXmlByProcessInstance(@RequestParam String resourceType,
        @RequestParam String processInstanceId, @RequestParam String processDefinitionId) {
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
     * @return {@code Y9Result<List<Map<String, Object>>>} 通用请求返回对象 - data 是流程定义信息
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<ProcessDefinitionModel>> list() {
        return customRepositoryService.list("");
    }

    /**
     * 激活/挂起流程的状态
     *
     * @param state 状态
     * @param processDefinitionId 流程定义Id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> switchSuspendOrActive(@RequestParam String state,
        @RequestParam String processDefinitionId) {
        return customRepositoryService.switchSuspendOrActive(state, processDefinitionId);
    }
}
