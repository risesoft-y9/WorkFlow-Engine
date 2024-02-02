package y9.client.rest.processadmin;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.processadmin.DoingApi;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "DoingApiClient", name = "processAdmin", url = "${y9.common.processAdminBaseUrl}",
    path = "/services/rest/doing")
public interface DoingApiClient extends DoingApi {

    /**
     * 根据人员id获取在办件统计
     *
     * @param tenantId 租户Id
     * @param userId 人员Id
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @GetMapping("/getCountByUserId")
    long getCountByUserId(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId);

    /**
     * 根据人员Id获取用户的在办任务(分页,包含流程变量)
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param page page
     * @param rows rows
     * @return Map&lt;String, Object&gt;
     * @throws Exception Exception
     */
    @Override
    @GetMapping("/getListByUserId")
    Map<String, Object> getListByUserId(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("page") Integer page, @RequestParam("rows") Integer rows)
        throws Exception;

    /**
     * 根据人员Id,事项ID获取用户的在办列表(分页,包含流程变量)
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processDefinitionKey 流程定义Key
     * @param page page
     * @param rows rows
     * @return Map&lt;String, Object&gt;
     * @throws Exception Exception
     */
    @Override
    @GetMapping("/getListByUserIdAndProcessDefinitionKey")
    Map<String, Object> getListByUserIdAndProcessDefinitionKey(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("processDefinitionKey") String processDefinitionKey,
        @RequestParam("page") Integer page, @RequestParam("rows") Integer rows) throws Exception;

    /**
     * 
     * Description: 获取已办件列表，按办理的时间排序
     * 
     * @param tenantId
     * @param userId
     * @param processDefinitionKey
     * @param page
     * @param rows
     * @return
     * @throws Exception
     */
    @Override
    @GetMapping("/getListByUserIdAndProcessDefinitionKeyOrderBySendTime")
    Map<String, Object> getListByUserIdAndProcessDefinitionKeyOrderBySendTime(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("processDefinitionKey") String processDefinitionKey,
        @RequestParam("page") Integer page, @RequestParam("rows") Integer rows) throws Exception;

    /**
     * 根据人员Id,系统标识获取用户的在办列表(分页,包含流程变量)
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param systemName 英文系统名称
     * @param page page
     * @param rows rows
     * @return Map&lt;String, Object&gt;
     * @throws Exception Exception
     */
    @Override
    @GetMapping("/getListByUserIdAndSystemName")
    Map<String, Object> getListByUserIdAndSystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("systemName") String systemName,
        @RequestParam("page") Integer page, @RequestParam("rows") Integer rows) throws Exception;

    /**
     * 
     * Description: 条件搜索在办件
     * 
     * @param tenantId
     * @param userId
     * @param searchTerm
     * @param page
     * @param rows
     * @return
     * @throws Exception
     */
    @Override
    @GetMapping("/searchListByUserId")
    Map<String, Object> searchListByUserId(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("searchTerm") String searchTerm,
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
    @GetMapping("/searchListByUserIdAndProcessDefinitionKey")
    Map<String, Object> searchListByUserIdAndProcessDefinitionKey(@RequestParam("tenantId") String tenantId,
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
    @GetMapping("/searchListByUserIdAndSystemName")
    Map<String, Object> searchListByUserIdAndSystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("systemName") String systemName,
        @RequestParam("searchTerm") String searchTerm, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows) throws Exception;
}
