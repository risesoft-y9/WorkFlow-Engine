package net.risesoft.service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.web.multipart.MultipartFile;

import net.risesoft.pojo.Y9Result;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
public interface CustomRepositoryService {

    /**
     * 根据部署的Id删除部署的流程定义
     *
     * @param deploymentId
     * @return Y9Result<Object>
     */
    Y9Result<Object> delete(String deploymentId);

    /**
     * 部署流程定义
     *
     * @param file
     * @return Map
     */
    Y9Result<Object> deploy(MultipartFile file);

    /**
     * 根据流程定义Key获取最新部署的流程定义
     *
     * @param processDefinitionKey
     * @return
     */
    ProcessDefinition getLatestProcessDefinitionByKey(String processDefinitionKey);

    /**
     * 获取所有流程定义最新版本的集合
     *
     * @return
     */
    List<ProcessDefinition> getLatestProcessDefinitionList();

    /**
     * Description: 根据流程定义Id获取上一个版本的流程定义，如果当前版本是1，则返回自己
     * 
     * @param processDefinitionId
     * @return
     */
    ProcessDefinition getPreviousProcessDefinitionById(String processDefinitionId);

    /**
     * 根据流程定义Id获取流程定义
     *
     * @param processDefinitionId
     * @return
     */
    ProcessDefinition getProcessDefinitionById(String processDefinitionId);

    /**
     * Description: 根据流程定义Key,获取所有的流程定义
     * 
     * @param processDefinitionKey
     * @return
     */
    List<ProcessDefinition> getProcessDefinitionListByKey(String processDefinitionKey);

    /**
     * Description: 获取流程实例,以InputStream返回
     * 
     * @param resourceType
     * @param processInstanceId
     * @param processDefinitionId
     * @return
     */
    InputStream getProcessInstance(String resourceType, String processInstanceId, String processDefinitionId);

    /**
     * Description: 流程模型列表
     * 
     * @param resourceId
     * @return
     */
    Map<String, Object> list(String resourceId);

    /**
     * 激活或者挂起流程
     *
     * @param state
     * @param processDefinitionId
     * @return
     */
    Y9Result<Object> switchSuspendOrActive(String state, String processDefinitionId);
}
