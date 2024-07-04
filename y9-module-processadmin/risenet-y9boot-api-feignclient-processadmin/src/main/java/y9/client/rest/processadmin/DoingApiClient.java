package y9.client.rest.processadmin;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.processadmin.DoingApi;
import net.risesoft.model.processadmin.ProcessInstanceModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "DoingApiClient", name = "${y9.service.processAdmin.name:processAdmin}",
    url = "${y9.service.processAdmin.directUrl:}",
    path = "/${y9.service.processAdmin.name:processAdmin}/services/rest/doing")
public interface DoingApiClient extends DoingApi {

    /**
     * 根据人员id获取在办件统计
     *
     * @param tenantId 租户Id
     * @param userId 人员Id
     * @return {@code Y9Result<Long>} 通用请求返回对象 - data 在办件统计
     */
    @Override
    @GetMapping("/getCountByUserId")
    Y9Result<Long> getCountByUserId(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId);

    /**
     * 根据人员Id获取用户的在办任务(分页,包含流程变量)
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param page 页码
     * @param rows 行数
     * @return {@code Y9Page<ProcessInstanceModel> } 通用请求返回对象 - data 在办任务
     */
    @Override
    @GetMapping("/getListByUserId")
    Y9Page<ProcessInstanceModel> getListByUserId(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("page") Integer page, @RequestParam("rows") Integer rows);

    /**
     * 根据人员Id,事项ID获取用户的在办列表(分页,包含流程变量)
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processDefinitionKey 流程定义Key
     * @param page page
     * @param rows rows
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @GetMapping("/getListByUserIdAndProcessDefinitionKey")
    Y9Page<ProcessInstanceModel> getListByUserIdAndProcessDefinitionKey(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("processDefinitionKey") String processDefinitionKey,
        @RequestParam("page") Integer page, @RequestParam("rows") Integer rows);

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
    Y9Page<ProcessInstanceModel> getListByUserIdAndProcessDefinitionKeyOrderBySendTime(
        @RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("processDefinitionKey") String processDefinitionKey, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows);

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
    Y9Page<ProcessInstanceModel> getListByUserIdAndSystemName(@RequestParam("tenantId") String tenantId,
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
    Y9Page<ProcessInstanceModel> searchListByUserId(@RequestParam("tenantId") String tenantId,
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
    Y9Page<ProcessInstanceModel> searchListByUserIdAndProcessDefinitionKey(@RequestParam("tenantId") String tenantId,
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
    Y9Page<ProcessInstanceModel> searchListByUserIdAndSystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("systemName") String systemName,
        @RequestParam("searchTerm") String searchTerm, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows) throws Exception;
}
