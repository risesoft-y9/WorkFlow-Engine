package net.risesoft.api.processadmin;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import net.risesoft.model.processadmin.ProcessDefinitionModel;
import net.risesoft.pojo.Y9Result;

/**
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
     * @return Y9Result<Object>
     */
    Y9Result<Object> delete(String tenantId, String deploymentId);

    /**
     * 部署流程
     *
     * @param tenantId 租户id
     * @param file 流程文件
     * @return Y9Result<String>
     */
    Y9Result<Object> deploy(String tenantId, MultipartFile file);

    /**
     * 根据流程定义key获取最新部署的流程定义
     *
     * @param tenantId 租户id
     * @param processDefinitionKey 流程定义key
     * @return Y9Result<ProcessDefinitionModel>
     */
    Y9Result<ProcessDefinitionModel> getLatestProcessDefinitionByKey(String tenantId, String processDefinitionKey);

    /**
     * 获取所有流程定义最新版本的集合
     *
     * @param tenantId 租户id
     * @return Y9Result<List<ProcessDefinitionModel>>
     */
    Y9Result<List<ProcessDefinitionModel>> getLatestProcessDefinitionList(String tenantId);

    /**
     * 根据流程定义Id获取上一个版本的流程定义，如果当前版本是1，则返回自己
     *
     * @param tenantId 租户id
     * @param processDefinitionId 流程定义Id
     * @return ProcessDefinitionModel
     */
    Y9Result<ProcessDefinitionModel> getPreviousProcessDefinitionById(String tenantId, String processDefinitionId);

    /**
     * 根据流程定义Id获取流程定义
     *
     * @param tenantId 租户id
     * @param processDefinitionId 流程定义Id
     * @return ProcessDefinitionModel
     */
    Y9Result<ProcessDefinitionModel> getProcessDefinitionById(String tenantId, String processDefinitionId);

    /**
     * 根据流程定义key获取最新部署的流程定义
     *
     * @param tenantId 租户id
     * @param processDefinitionKey 流程定义key
     * @return List&lt;ProcessDefinitionModel&gt;
     */
    Y9Result<List<ProcessDefinitionModel>> getProcessDefinitionListByKey(String tenantId, String processDefinitionKey);

    /**
     * 获取流程定义xml
     *
     * @param tenantId 租户id
     * @param resourceType xml
     * @param processInstanceId 流程实例id
     * @param processDefinitionId 流程定义id
     * @return Y9Result<String>
     */
    Y9Result<String> getXmlByProcessInstance(String tenantId, String resourceType, String processInstanceId,
        String processDefinitionId);

    /**
     * 获取已部署流程定义列表
     *
     * @param tenantId 租户id
     * @return Y9Result<List<Map<String, Object>>>
     */
    Y9Result<List<ProcessDefinitionModel>> list(String tenantId);

    /**
     * 激活/挂起流程的状态
     *
     * @param tenantId 租户id
     * @param state 状态
     * @param processDefinitionId 流程定义Id
     * @return Y9Result<Object>
     */
    Y9Result<Object> switchSuspendOrActive(String tenantId, String state, String processDefinitionId);
}
