package y9.client.rest.processadmin;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import net.risesoft.api.processadmin.RepositoryApi;
import net.risesoft.model.processadmin.ProcessDefinitionModel;
import net.risesoft.pojo.Y9Result;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "RepositoryApiClient", name = "${y9.service.processAdmin.name:processAdmin}",
    url = "${y9.service.processAdmin.directUrl:}",
    path = "/${y9.service.processAdmin.name:processAdmin}/services/rest/repository")
public interface RepositoryApiClient extends RepositoryApi {

    /**
     * 删除部署的流程
     *
     * @param tenantId 租户id
     * @param deploymentId 部署id
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @PostMapping("/delete")
    Map<String, Object> delete(@RequestParam("tenantId") String tenantId,
        @RequestParam("deploymentId") String deploymentId);

    /**
     *
     */
    @Override
    @PostMapping("/deploy")
    Y9Result<String> deploy(@RequestParam("tenantId") String tenantId, MultipartFile file);

    /**
     * 根据流程定义key获取最新部署的流程定义
     *
     * @param tenantId 租户id
     * @param processDefinitionKey processDefinitionKey
     * @return ProcessDefinitionModel
     */
    @Override
    @GetMapping("/getLatestProcessDefinitionByKey")
    ProcessDefinitionModel getLatestProcessDefinitionByKey(@RequestParam("tenantId") String tenantId,
        @RequestParam("processDefinitionKey") String processDefinitionKey);

    /**
     * 获取所有流程定义最新版本的集合
     *
     * @param tenantId 租户id
     * @return List&lt;ProcessDefinitionModel&gt;
     */
    @Override
    @GetMapping("/getLatestProcessDefinitionList")
    List<ProcessDefinitionModel> getLatestProcessDefinitionList(@RequestParam("tenantId") String tenantId);

    /**
     * 根据流程定义Id获取上一个版本的流程定义，如果当前版本是1，则返回自己
     *
     * @param tenantId 租户id
     * @param processDefinitionId processDefinitionId
     * @return ProcessDefinitionModel
     */
    @Override
    @GetMapping("/getPreviousProcessDefinitionById")
    ProcessDefinitionModel getPreviousProcessDefinitionById(@RequestParam("tenantId") String tenantId,
        @RequestParam("processDefinitionId") String processDefinitionId);

    /**
     * 根据流程定义Id获取流程定义
     *
     * @param tenantId 租户id
     * @param processDefinitionId processDefinitionId
     * @return ProcessDefinitionModel
     */
    @Override
    @GetMapping("/getProcessDefinitionById")
    ProcessDefinitionModel getProcessDefinitionById(@RequestParam("tenantId") String tenantId,
        @RequestParam("processDefinitionId") String processDefinitionId);

    /**
     * 根据流程定义key获取最新部署的流程定义
     *
     * @param tenantId 租户id
     * @param processDefinitionKey processDefinitionKey
     * @return List&lt;ProcessDefinitionModel&gt;
     */
    @Override
    @GetMapping("/getProcessDefinitionListByKey")
    List<ProcessDefinitionModel> getProcessDefinitionListByKey(@RequestParam("tenantId") String tenantId,
        @RequestParam("processDefinitionKey") String processDefinitionKey);

    /**
    *
    */
    @Override
    @GetMapping("/getXmlByProcessInstance")
    Y9Result<String> getXmlByProcessInstance(@RequestParam("tenantId") String tenantId,
        @RequestParam("resourceType") String resourceType, @RequestParam("processInstanceId") String processInstanceId,
        @RequestParam("processDefinitionId") String processDefinitionId);

    /**
    *
    */
    @Override
    @GetMapping("/list")
    Y9Result<List<Map<String, Object>>> list(@RequestParam("tenantId") String tenantId);

    /**
     * 激活/挂起流程的状态
     *
     * @param tenantId 租户id
     * @param state 状态
     * @param processDefinitionId processDefinitionId
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @PostMapping("/switchSuspendOrActive")
    Map<String, Object> switchSuspendOrActive(@RequestParam("tenantId") String tenantId,
        @RequestParam("state") String state, @RequestParam("processDefinitionId") String processDefinitionId);

}
