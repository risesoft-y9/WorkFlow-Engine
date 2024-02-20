package y9.client.rest.processadmin;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.processadmin.MonitorApi;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "MonitorApiClient", name = "${y9.service.processAdmin.name:processAdmin}", url = "${y9.service.processAdmin.directUrl:}",
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
    long getDoingCountByProcessDefinitionKey(@RequestParam("tenantId") String tenantId,
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
    long getDoingCountBySystemName(@RequestParam("tenantId") String tenantId,
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
    Map<String, Object> getDoingListByProcessDefinitionKey(@RequestParam("tenantId") String tenantId,
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
    Map<String, Object> getDoingListBySystemName(@RequestParam("tenantId") String tenantId,
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
    long getDoneCountByProcessDefinitionKey(@RequestParam("tenantId") String tenantId,
        @RequestParam("processDefinitionKey") String processDefinitionKey);

    /**
     * 获取监控办结件统计
     *
     * @param tenantId 租户Id
     * @param systemName 系统英文名称
     * @return Integer
     */
    @Override
    @GetMapping("/getDoneCountBySystemName")
    long getDoneCountBySystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("systemName") String systemName);

    /**
     * 根据事项id获取监控办结件
     *
     * @param tenantId 租户Id
     * @param processDefinitionKey 流程定义Key
     * @param page page
     * @param rows rows
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @GetMapping("/getDoneListByProcessDefinitionKey")
    Map<String, Object> getDoneListByProcessDefinitionKey(@RequestParam("tenantId") String tenantId,
        @RequestParam("processDefinitionKey") String processDefinitionKey, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows);

    /**
     * 根据事项id获取监控办结件
     *
     * @param tenantId 租户Id
     * @param systemName 系统英文名称
     * @param page page
     * @param rows rows
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @GetMapping("/getDoneListBySystemName")
    Map<String, Object> getDoneListBySystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("systemName") String systemName, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows);

    /**
     * 获取监控回收站统计
     *
     * @param tenantId 租户Id
     * @param processDefinitionKey 流程定义Key
     * @return Integer
     */
    @Override
    @GetMapping("/getRecycleCountByProcessDefinitionKey")
    long getRecycleCountByProcessDefinitionKey(@RequestParam("tenantId") String tenantId,
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
    long getRecycleCountBySystemName(@RequestParam("tenantId") String tenantId,
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
    long getRecycleCountByUserIdAndProcessDefinitionKey(@RequestParam("tenantId") String tenantId,
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
    long getRecycleCountByUserIdAndSystemName(@RequestParam("tenantId") String tenantId,
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
    Map<String, Object> getRecycleListByProcessDefinitionKey(@RequestParam("tenantId") String tenantId,
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
    Map<String, Object> getRecycleListBySystemName(@RequestParam("tenantId") String tenantId,
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
    Map<String, Object> getRecycleListByUserIdAndProcessDefinitionKey(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("processDefinitionKey") String processDefinitionKey,
        @RequestParam("page") Integer page, @RequestParam("rows") Integer rows);

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
    Map<String, Object> getRecycleListByUserIdAndSystemName(@RequestParam("tenantId") String tenantId,
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
     * @throws Exception
     */
    @Override
    @GetMapping("/searchDoingListByProcessDefinitionKey")
    Map<String, Object> searchDoingListByProcessDefinitionKey(@RequestParam("tenantId") String tenantId,
        @RequestParam("processDefinitionKey") String processDefinitionKey,
        @RequestParam("searchTerm") String searchTerm, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows) throws Exception;

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
     * @throws Exception
     */
    @Override
    @GetMapping("/searchDoingListBySystemName")
    Map<String, Object> searchDoingListBySystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("systemName") String systemName, @RequestParam("searchTerm") String searchTerm,
        @RequestParam("page") Integer page, @RequestParam("rows") Integer rows) throws Exception;

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
     * @throws Exception
     */
    @Override
    @GetMapping("/searchDoneListByProcessDefinitionKey")
    Map<String, Object> searchDoneListByProcessDefinitionKey(@RequestParam("tenantId") String tenantId,
        @RequestParam("processDefinitionKey") String processDefinitionKey,
        @RequestParam("searchTerm") String searchTerm, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows) throws Exception;

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
     * @throws Exception
     */
    @Override
    @GetMapping("/searchDoneListBySystemName")
    Map<String, Object> searchDoneListBySystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("systemName") String systemName, @RequestParam("searchTerm") String searchTerm,
        @RequestParam("page") Integer page, @RequestParam("rows") Integer rows) throws Exception;

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
     * @throws Exception
     */
    @Override
    @GetMapping("/searchRecycleListByProcessDefinitionKey")
    Map<String, Object> searchRecycleListByProcessDefinitionKey(@RequestParam("tenantId") String tenantId,
        @RequestParam("processDefinitionKey") String processDefinitionKey,
        @RequestParam("searchTerm") String searchTerm, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows) throws Exception;

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
     * @throws Exception
     */
    @Override
    @GetMapping("/searchRecycleListBySystemName")
    Map<String, Object> searchRecycleListBySystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("systemName") String systemName, @RequestParam("searchTerm") String searchTerm,
        @RequestParam("page") Integer page, @RequestParam("rows") Integer rows) throws Exception;

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
     * @throws Exception
     */
    @Override
    @GetMapping("/searchRecycleListByUserIdAndProcessDefinitionKey")
    Map<String, Object> searchRecycleListByUserIdAndProcessDefinitionKey(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("processDefinitionKey") String processDefinitionKey,
        @RequestParam("searchTerm") String searchTerm, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows) throws Exception;

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
     * @throws Exception
     */
    @Override
    @GetMapping("/searchRecycleListByUserIdAndSystemName")
    Map<String, Object> searchRecycleListByUserIdAndSystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("systemName") String systemName,
        @RequestParam("searchTerm") String searchTerm, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows) throws Exception;
}
