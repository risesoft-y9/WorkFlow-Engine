package y9.client.rest.processadmin;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.processadmin.MonitorApi;
import net.risesoft.model.processadmin.HistoricProcessInstanceModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "MonitorApiClient", name = "${y9.service.processAdmin.name:processAdmin}",
    url = "${y9.service.processAdmin.directUrl:}",
    path = "/${y9.service.processAdmin.name:processAdmin}/services/rest/monitor")
public interface MonitorApiClient extends MonitorApi {

    /**
     * 获取监控在办件统计
     *
     * @param tenantId 租户Id
     * @param processDefinitionKey 流程定义Key
     * @return Integer
     */
    @Override
    @GetMapping("/getDoingCountByProcessDefinitionKey")
    Y9Result<Long> getDoingCountByProcessDefinitionKey(@RequestParam("tenantId") String tenantId,
        @RequestParam("processDefinitionKey") String processDefinitionKey);

    /**
     * 获取监控在办件数量
     *
     * @param tenantId 租户Id
     * @param systemName 系统英文名称
     * @return Integer
     */
    @Override
    @GetMapping("/getDoingCountBySystemName")
    Y9Result<Long> getDoingCountBySystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("systemName") String systemName);

    /**
     * 根据事项id获取监控在办件
     *
     * @param tenantId 租户Id
     * @param processDefinitionKey 流程定义Key
     * @param page page
     * @param rows rows
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @GetMapping("/getDoingListByProcessDefinitionKey")
    Y9Page<HistoricProcessInstanceModel> getDoingListByProcessDefinitionKey(@RequestParam("tenantId") String tenantId,
        @RequestParam("processDefinitionKey") String processDefinitionKey, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows);

    /**
     * 根据系统名称获取监控在办件
     *
     * @param tenantId 租户Id
     * @param systemName 系统英文名称
     * @param page page
     * @param rows rows
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @GetMapping("/getDoingListBySystemName")
    Y9Page<HistoricProcessInstanceModel> getDoingListBySystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("systemName") String systemName, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows);

    /**
     * 获取监控办结件统计
     *
     * @param tenantId 租户Id
     * @param processDefinitionKey 流程定义Key
     * @return Integer
     */
    @Override
    @GetMapping("/getDoneCountByProcessDefinitionKey")
    Y9Result<Long> getDoneCountByProcessDefinitionKey(@RequestParam("tenantId") String tenantId,
        @RequestParam("processDefinitionKey") String processDefinitionKey);

    /**
     * 获取监控回收站统计
     *
     * @param tenantId 租户Id
     * @param processDefinitionKey 流程定义Key
     * @return Integer
     */
    @Override
    @GetMapping("/getRecycleCountByProcessDefinitionKey")
    Y9Result<Long> getRecycleCountByProcessDefinitionKey(@RequestParam("tenantId") String tenantId,
        @RequestParam("processDefinitionKey") String processDefinitionKey);

    /**
     * 获取监控回收站统计
     *
     * @param tenantId 租户Id
     * @param systemName 系统英文名称
     * @return Integer
     */
    @Override
    @GetMapping("/getRecycleCountBySystemName")
    Y9Result<Long> getRecycleCountBySystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("systemName") String systemName);

    /**
     * 
     * Description: 获取监控回收站统计
     * 
     * @param tenantId
     * @param userId
     * @param processDefinitionKey
     * @return
     */
    @Override
    @GetMapping("/getRecycleCountByUserIdAndProcessDefinitionKey")
    Y9Result<Long> getRecycleCountByUserIdAndProcessDefinitionKey(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("processDefinitionKey") String processDefinitionKey);

    /**
     * 根据人员id获取监控回收站统计
     *
     * @param tenantId 租户Id
     * @param userId 人员Id
     * @param systemName 系统英文名称
     * @return Integer
     */
    @Override
    @GetMapping("/getRecycleCountByUserIdAndSystemName")
    Y9Result<Long> getRecycleCountByUserIdAndSystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("systemName") String systemName);

    /**
     * 获取回收站列表
     *
     * @param tenantId 租户Id
     * @param processDefinitionKey 流程定义Key
     * @param page page
     * @param rows rows
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @GetMapping("/getRecycleListByProcessDefinitionKey")
    Y9Page<HistoricProcessInstanceModel> getRecycleListByProcessDefinitionKey(@RequestParam("tenantId") String tenantId,
        @RequestParam("processDefinitionKey") String processDefinitionKey, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows);

    /**
     * 获取回收站列表
     *
     * @param tenantId 租户Id
     * @param systemName 系统英文名称
     * @param page page
     * @param rows rows
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @GetMapping("/getRecycleListBySystemName")
    Y9Page<HistoricProcessInstanceModel> getRecycleListBySystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("systemName") String systemName, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows);

    /**
     * 获取回收站列表
     *
     * @param tenantId 租户Id
     * @param userId 用户Id
     * @param processDefinitionKey 流程定义Key
     * @param page page
     * @param rows rows
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @GetMapping("/getRecycleListByUserIdAndProcessDefinitionKey")
    Y9Page<HistoricProcessInstanceModel> getRecycleListByUserIdAndProcessDefinitionKey(
        @RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("processDefinitionKey") String processDefinitionKey, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows);

    /**
     * 根据人员id获取回收站列表
     *
     * @param tenantId 租户Id
     * @param userId 人员Id
     * @param systemName 系统英文名称
     * @param page 当前页
     * @param rows 总条数
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @GetMapping("/getRecycleListByUserIdAndSystemName")
    Y9Page<HistoricProcessInstanceModel> getRecycleListByUserIdAndSystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("systemName") String systemName,
        @RequestParam("page") Integer page, @RequestParam("rows") Integer rows);

    /**
     * 
     * Description: 条件搜索在办件
     * 
     * @param tenantId
     * @param processDefinitionKey
     * @param searchTerm
     * @param page
     * @param rows
     * @return
     * 
     */
    @Override
    @GetMapping("/searchDoingListByProcessDefinitionKey")
    Y9Page<HistoricProcessInstanceModel> searchDoingListByProcessDefinitionKey(
        @RequestParam("tenantId") String tenantId, @RequestParam("processDefinitionKey") String processDefinitionKey,
        @RequestParam("searchTerm") String searchTerm, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows);

    /**
     * 
     * Description: 条件搜索在办件
     * 
     * @param tenantId
     * @param systemName
     * @param searchTerm
     * @param page
     * @param rows
     * @return
     * 
     */
    @Override
    @GetMapping("/searchDoingListBySystemName")
    Y9Page<HistoricProcessInstanceModel> searchDoingListBySystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("systemName") String systemName, @RequestParam("searchTerm") String searchTerm,
        @RequestParam("page") Integer page, @RequestParam("rows") Integer rows);

    /**
     * 
     * Description: 条件搜索在办件
     * 
     * @param tenantId
     * @param processDefinitionKey
     * @param searchTerm
     * @param page
     * @param rows
     * @return
     * 
     */
    @Override
    @GetMapping("/searchRecycleListByProcessDefinitionKey")
    Y9Page<HistoricProcessInstanceModel> searchRecycleListByProcessDefinitionKey(
        @RequestParam("tenantId") String tenantId, @RequestParam("processDefinitionKey") String processDefinitionKey,
        @RequestParam("searchTerm") String searchTerm, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows);

    /**
     * 
     * Description: 条件搜索在办件
     * 
     * @param tenantId
     * @param systemName
     * @param searchTerm
     * @param page
     * @param rows
     * @return
     * 
     */
    @Override
    @GetMapping("/searchRecycleListBySystemName")
    Y9Page<HistoricProcessInstanceModel> searchRecycleListBySystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("systemName") String systemName, @RequestParam("searchTerm") String searchTerm,
        @RequestParam("page") Integer page, @RequestParam("rows") Integer rows);

    /**
     * 
     * Description: 条件搜索在办件
     * 
     * @param tenantId
     * @param userId
     * @param processDefinitionKey
     * @param searchTerm
     * @param page
     * @param rows
     * @return
     * 
     */
    @Override
    @GetMapping("/searchRecycleListByUserIdAndProcessDefinitionKey")
    Y9Page<HistoricProcessInstanceModel> searchRecycleListByUserIdAndProcessDefinitionKey(
        @RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("processDefinitionKey") String processDefinitionKey,
        @RequestParam("searchTerm") String searchTerm, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows);

    /**
     * 
     * Description: 条件搜索在办件
     * 
     * @param tenantId
     * @param userId
     * @param systemName
     * @param searchTerm
     * @param page
     * @param rows
     * @return
     * 
     */
    @Override
    @GetMapping("/searchRecycleListByUserIdAndSystemName")
    Y9Page<HistoricProcessInstanceModel> searchRecycleListByUserIdAndSystemName(
        @RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("systemName") String systemName, @RequestParam("searchTerm") String searchTerm,
        @RequestParam("page") Integer page, @RequestParam("rows") Integer rows);
}
