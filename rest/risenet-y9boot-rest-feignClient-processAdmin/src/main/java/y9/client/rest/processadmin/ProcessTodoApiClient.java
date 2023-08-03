package y9.client.rest.processadmin;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.processadmin.ProcessTodoApi;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "ProcessTodoApiClient", name = "processAdmin", url = "${y9.common.processAdminBaseUrl}",
    path = "/services/rest/processTodo")
public interface ProcessTodoApiClient extends ProcessTodoApi {

    /**
     * 根据人员idd获取对应事项的办件统计（包括待办件，在办件，办结件）
     *
     * @param tenantId 租户Id
     * @param userId 人员Id
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @GetMapping("/getCountByUserId")
    Map<String, Object> getCountByUserId(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId);

    /**
     * 根据人员id，事项id获取对应事项的办件统计（包括待办件，在办件，办结件）
     *
     * @param tenantId 租户Id
     * @param userId 人员Id
     * @param processDefinitionKey 流程定义Key
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @GetMapping("/getCountByUserIdAndProcessDefinitionKey")
    Map<String, Object> getCountByUserIdAndProcessDefinitionKey(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("processDefinitionKey") String processDefinitionKey);

    /**
     * 根据人员id，系统标识获取对应事项的办件统计（包括待办件，在办件，办结件）
     *
     * @param tenantId 租户Id
     * @param userId 人员Id
     * @param systemName 系统名称
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @GetMapping("/getCountByUserIdAndSystemName")
    Map<String, Object> getCountByUserIdAndSystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("systemName") String systemName);

    /**
     * 根据人员Id，事项id获取用户的待办任务(分页)
     *
     * @param tenantId 租户Id
     * @param userId 人员Id
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
     * 根据人员Id，事项id获取用户的待办任务(分页)
     *
     * @param tenantId 租户Id
     * @param userId 人员Id
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
     * 根据人员Id,系统标识获取用户的待办任务(分页)
     *
     * @param tenantId 租户Id
     * @param userId 人员Id
     * @param systemName 系统名称
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
     * 根据岗位id,流程定义key获取对应事项的待办数量
     *
     * @param tenantId
     * @param positionId
     * @param processDefinitionKey
     * @return
     */
    @Override
    @GetMapping("/getTodoCountByPositionIdAndProcessDefinitionKey")
    long getTodoCountByPositionIdAndProcessDefinitionKey(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId,
        @RequestParam("processDefinitionKey") String processDefinitionKey);

    /**
     * 根据人员id，事项id获取对应事项的待办数量
     *
     * @param tenantId 租户Id
     * @param userId 人员Id
     * @return int
     */
    @Override
    @GetMapping("/getTodoCountByUserId")
    long getTodoCountByUserId(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId);

    /**
     * 根据人员id,事项id获取对应事项的待办数量
     *
     * @param tenantId 租户Id
     * @param userId 人员Id
     * @param processDefinitionKey 事项id
     * @return int
     */
    @Override
    @GetMapping("/getTodoCountByUserIdAndProcessDefinitionKey")
    long getTodoCountByUserIdAndProcessDefinitionKey(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("processDefinitionKey") String processDefinitionKey);

    /**
     * 根据人员id，系统标识获取对应事项的待办数量
     *
     * @param tenantId 租户Id
     * @param userId 人员Id
     * @param systemName 系统名称
     * @return int
     */
    @Override
    @GetMapping("/getTodoCountByUserIdAndSystemName")
    long getTodoCountByUserIdAndSystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("systemName") String systemName);

    /**
     * 条件搜索待办件
     *
     * @param tenantId 租户Id
     * @param userId 人员Id
     * @param searchTerm 搜索词
     * @param page page
     * @param rows rows
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @GetMapping("/searchListByUserId")
    Map<String, Object> searchListByUserId(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("searchTerm") String searchTerm,
        @RequestParam("page") Integer page, @RequestParam("rows") Integer rows);

    /**
     * 条件搜索待办件
     *
     * @param tenantId 租户Id
     * @param userId 人员Id
     * @param processDefinitionKey 流程定义Key
     * @param searchTerm 搜索词
     * @param page page
     * @param rows rows
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @GetMapping("/searchListByUserIdAndProcessDefinitionKey")
    Map<String, Object> searchListByUserIdAndProcessDefinitionKey(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("processDefinitionKey") String processDefinitionKey,
        @RequestParam("searchTerm") String searchTerm, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows);

    /**
     * 条件搜索待办件
     *
     * @param tenantId 租户Id
     * @param userId 人员Id
     * @param systemName 系统英文名称
     * @param searchTerm 搜索词
     * @param page page
     * @param rows rows
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @GetMapping("/searchListByUserIdAndSystemName")
    Map<String, Object> searchListByUserIdAndSystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("systemName") String systemName,
        @RequestParam("searchTerm") String searchTerm, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows);
}
