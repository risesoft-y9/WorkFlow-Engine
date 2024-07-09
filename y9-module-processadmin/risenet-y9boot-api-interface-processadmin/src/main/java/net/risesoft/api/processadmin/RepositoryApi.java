package net.risesoft.api.processadmin;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import net.risesoft.model.processadmin.ProcessDefinitionModel;
import net.risesoft.pojo.Y9Result;

/**
 * 部署流程相关接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface RepositoryApi {

    /**
     * 删除部署的流程
     *
     * @param tenantId 租户id
     * @param deploymentId 部署id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @PostMapping(value = "/delete")
    Y9Result<Object> delete(@RequestParam("tenantId") String tenantId,
        @RequestParam("deploymentId") String deploymentId);

    /**
     * 部署流程
     *
     * @param tenantId 租户id
     * @param file 流程文件
     * @return {@code Y9Result<String>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @PostMapping(value = "/deploy")
    Y9Result<Object> deploy(@RequestParam("tenantId") String tenantId, MultipartFile file);

    /**
     * 根据流程定义key获取最新部署的流程定义
     *
     * @param tenantId 租户id
     * @param processDefinitionKey 流程定义key
     * @return {@code Y9Result<ProcessDefinitionModel>} 通用请求返回对象 - data 是最新部署的流程定义
     * @since 9.6.6
     */
    @GetMapping(value = "/getLatestProcessDefinitionByKey")
    Y9Result<ProcessDefinitionModel> getLatestProcessDefinitionByKey(@RequestParam("tenantId") String tenantId,
        @RequestParam("processDefinitionKey") String processDefinitionKey);

    /**
     * 获取所有流程定义最新版本的集合
     *
     * @param tenantId 租户id
     * @return {@code Y9Result<List<ProcessDefinitionModel>>} 通用请求返回对象 - data 是最新部署的流程定义列表
     * @since 9.6.6
     */
    @GetMapping(value = "/getLatestProcessDefinitionList")
    Y9Result<List<ProcessDefinitionModel>> getLatestProcessDefinitionList(@RequestParam("tenantId") String tenantId);

    /**
     * 根据流程定义Id获取上一个版本的流程定义，如果当前版本是1，则返回自己
     *
     * @param tenantId 租户id
     * @param processDefinitionId 流程定义Id
     * @return {@code Y9Result<ProcessDefinitionModel>} 通用请求返回对象 - data 是流程定义信息
     * @since 9.6.6
     */
    @GetMapping(value = "/getPreviousProcessDefinitionById")
    Y9Result<ProcessDefinitionModel> getPreviousProcessDefinitionById(@RequestParam("tenantId") String tenantId,
        @RequestParam("processDefinitionId") String processDefinitionId);

    /**
     * 根据流程定义Id获取流程定义
     *
     * @param tenantId 租户id
     * @param processDefinitionId 流程定义Id
     * @return {@code Y9Result<ProcessDefinitionModel>} 通用请求返回对象 - data 是流程定义信息
     * @since 9.6.6
     */
    @GetMapping(value = "/getProcessDefinitionById")
    Y9Result<ProcessDefinitionModel> getProcessDefinitionById(@RequestParam("tenantId") String tenantId,
        @RequestParam("processDefinitionId") String processDefinitionId);

    /**
     * 根据流程定义key获取最新部署的流程定义
     *
     * @param tenantId 租户id
     * @param processDefinitionKey 流程定义key
     * @return {@code Y9Result<List<ProcessDefinitionModel>>} 通用请求返回对象 - data 是流程定义信息
     * @since 9.6.6
     */
    @GetMapping(value = "/getProcessDefinitionListByKey")
    Y9Result<List<ProcessDefinitionModel>> getProcessDefinitionListByKey(@RequestParam("tenantId") String tenantId,
        @RequestParam("processDefinitionKey") String processDefinitionKey);

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
    @GetMapping(value = "/getXmlByProcessInstance")
    Y9Result<String> getXmlByProcessInstance(@RequestParam("tenantId") String tenantId,
        @RequestParam("resourceType") String resourceType, @RequestParam("processInstanceId") String processInstanceId,
        @RequestParam("processDefinitionId") String processDefinitionId);

    /**
     * 获取已部署流程定义列表
     *
     * @param tenantId 租户id
     * @return {@code Y9Result<List<Map<String, Object>>>} 通用请求返回对象 - data 是流程定义信息
     * @since 9.6.6
     */
    @GetMapping(value = "/list")
    Y9Result<List<ProcessDefinitionModel>> list(@RequestParam("tenantId") String tenantId);

    /**
     * 激活/挂起流程的状态
     *
     * @param tenantId 租户id
     * @param state 状态
     * @param processDefinitionId 流程定义Id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @PostMapping(value = "/switchSuspendOrActive")
    Y9Result<Object> switchSuspendOrActive(@RequestParam("tenantId") String tenantId,
        @RequestParam("state") String state, @RequestParam("processDefinitionId") String processDefinitionId);
}
