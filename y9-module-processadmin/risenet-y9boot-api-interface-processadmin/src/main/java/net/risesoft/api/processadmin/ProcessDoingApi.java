package net.risesoft.api.processadmin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.processadmin.ProcessInstanceModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;

/**
 * 在办件列表
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface ProcessDoingApi {

    /**
     * 根据人员id获取在办件统计
     *
     * @param tenantId 租户Id
     * @param userId 人员Id
     * @return {@code Y9Result<Long>} 通用请求返回对象 - data 在办件统计
     * @since 9.6.6
     */
    @GetMapping(value = "/getCountByUserId")
    Y9Result<Long> getCountByUserId(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId);

    /**
     * 根据人员Id获取用户的在办任务(分页,包含流程变量)
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param page 页码
     * @param rows 行数
     * @return {@code Y9Page<ProcessInstanceModel> } 通用请求返回对象 - data 在办任务
     * @since 9.6.6
     */
    @GetMapping(value = "/getListByUserId")
    Y9Page<ProcessInstanceModel> getListByUserId(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("page") Integer page, @RequestParam("rows") Integer rows);

    /**
     * 根据人员Id,事项ID获取用户的在办列表(分页,包含流程变量)
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processDefinitionKey 流程定义Key
     * @param page 页码
     * @param rows 行数
     * @return {@code Y9Page<ProcessInstanceModel>} 通用请求返回对象 - data 在办列表
     * @since 9.6.6
     */
    @GetMapping(value = "/getListByUserIdAndProcessDefinitionKey")
    Y9Page<ProcessInstanceModel> getListByUserIdAndProcessDefinitionKey(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("processDefinitionKey") String processDefinitionKey,
        @RequestParam("page") Integer page, @RequestParam("rows") Integer rows);

    /**
     * 获取已办件列表，按办理的时间排序
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processDefinitionKey 流程定义key
     * @param page 页码
     * @param rows 行数
     * @return {@code Y9Page<ProcessInstanceModel>} 通用请求返回对象 - data 在办列表
     * @since 9.6.6
     */
    @GetMapping(value = "/getListByUserIdAndProcessDefinitionKeyOrderBySendTime")
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
     * @param page 页码
     * @param rows 行数
     * @return {@code Y9Page<ProcessInstanceModel>} 通用请求返回对象 - data 在办列表
     * @since 9.6.6
     */
    @GetMapping(value = "/getListByUserIdAndSystemName")
    Y9Page<ProcessInstanceModel> getListByUserIdAndSystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("systemName") String systemName,
        @RequestParam("page") Integer page, @RequestParam("rows") Integer rows);

    /**
     * 条件搜索在办件
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param searchTerm 搜索词
     * @param page 页码
     * @param rows 行数
     * @return {@code Y9Page<ProcessInstanceModel>} 通用请求返回对象 - data 在办列表
     * @since 9.6.6
     */
    @GetMapping(value = "/searchListByUserId")
    Y9Page<ProcessInstanceModel> searchListByUserId(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("searchTerm") String searchTerm,
        @RequestParam("page") Integer page, @RequestParam("rows") Integer rows);

    /**
     * 根据流程定义key条件搜索在办件
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processDefinitionKey 流程定义Key
     * @param searchTerm 搜索词
     * @param page 页码
     * @param rows 行数
     * @return {@code Y9Page<ProcessInstanceModel>} 通用请求返回对象 - data 在办列表
     * @since 9.6.6
     */
    @GetMapping(value = "/searchListByUserIdAndProcessDefinitionKey")
    Y9Page<ProcessInstanceModel> searchListByUserIdAndProcessDefinitionKey(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("processDefinitionKey") String processDefinitionKey,
        @RequestParam(value = "searchTerm", required = false) String searchTerm, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows);

    /**
     * 根据系统名条件搜索在办件
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param systemName 英文系统名称
     * @param searchTerm 搜索词
     * @param page 页码
     * @param rows 行数
     * @return {@code Y9Page<ProcessInstanceModel>} 通用请求返回对象 - data 在办列表
     * @since 9.6.6
     */
    @GetMapping(value = "/searchListByUserIdAndSystemName")
    Y9Page<ProcessInstanceModel> searchListByUserIdAndSystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("systemName") String systemName,
        @RequestParam("searchTerm") String searchTerm, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows);
}
