package net.risesoft.api.processadmin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.processadmin.HistoricProcessInstanceModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;

/**
 * 监控流程实例接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface MonitorApi {

    /**
     * 根据流程定义Key获取监控在办件统计
     *
     * @param tenantId 租户Id
     * @param processDefinitionKey 流程定义Key
     * @return {@code Y9Result<Long>} 通用请求返回对象 - data 在办件数量
     * @since 9.6.6
     */
    @GetMapping(value = "/getDoingCountByProcessDefinitionKey")
    Y9Result<Long> getDoingCountByProcessDefinitionKey(@RequestParam("tenantId") String tenantId,
        @RequestParam("processDefinitionKey") String processDefinitionKey);

    /**
     * 根据系统英文名称获取监控在办件数量
     *
     * @param tenantId 租户Id
     * @param systemName 系统英文名称
     * @return {@code Y9Result<Long>} 通用请求返回对象 - data 在办件数量
     * @since 9.6.6
     */
    @GetMapping(value = "/getDoingCountBySystemName")
    Y9Result<Long> getDoingCountBySystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("systemName") String systemName);

    /**
     * 根据流程定义Key获取监控在办件
     *
     * @param tenantId 租户Id
     * @param processDefinitionKey 流程定义Key
     * @param page 页码
     * @param rows 行数
     * @return {@code Y9Page<HistoricProcessInstanceModel>} 通用请求返回对象 - rows 在办件列表
     * @since 9.6.6
     */
    @GetMapping(value = "/getDoingListByProcessDefinitionKey")
    Y9Page<HistoricProcessInstanceModel> getDoingListByProcessDefinitionKey(@RequestParam("tenantId") String tenantId,
        @RequestParam("processDefinitionKey") String processDefinitionKey, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows);

    /**
     * 根据系统名称获取监控在办件
     *
     * @param tenantId 租户Id
     * @param systemName 系统英文名称
     * @param page 页码
     * @param rows 行数
     * @return {@code Y9Page<HistoricProcessInstanceModel>} 通用请求返回对象 - rows 在办件列表
     * @since 9.6.6
     */
    @GetMapping(value = "/getDoingListBySystemName")
    Y9Page<HistoricProcessInstanceModel> getDoingListBySystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("systemName") String systemName, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows);

    /**
     * 根据系统名称获取监控在办件
     *
     * @param tenantId 租户Id
     * @param systemName 系统英文名称
     * @param page 页码
     * @param rows 行数
     * @return {@code Y9Page<HistoricProcessInstanceModel>} 通用请求返回对象 - rows 在办件列表
     * @since 9.6.6
     */
    @GetMapping(value = "/getAllListBySystemName")
    Y9Page<HistoricProcessInstanceModel> getAllListBySystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("systemName") String systemName, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows);

    /**
     * 根据流程定义Key获取监控办结件统计
     *
     * @param tenantId 租户Id
     * @param processDefinitionKey 流程定义Key
     * @return {@code Y9Result<Long>} 通用请求返回对象 - data 办结件统计
     * @since 9.6.6
     */
    @GetMapping(value = "/getDoneCountByProcessDefinitionKey")
    Y9Result<Long> getDoneCountByProcessDefinitionKey(@RequestParam("tenantId") String tenantId,
        @RequestParam("processDefinitionKey") String processDefinitionKey);

    /**
     * 根据流程定义Key条件搜索在办件
     *
     * @param tenantId 租户id
     * @param processDefinitionKey 流程定义Key
     * @param searchTerm 搜索词
     * @param page 页码
     * @param rows 行数
     * @return {@code Y9Page<HistoricProcessInstanceModel>} 通用请求返回对象 - rows 在办件
     * @since 9.6.6
     */
    @GetMapping(value = "/searchDoingListByProcessDefinitionKey")
    Y9Page<HistoricProcessInstanceModel> searchDoingListByProcessDefinitionKey(
        @RequestParam("tenantId") String tenantId, @RequestParam("processDefinitionKey") String processDefinitionKey,
        @RequestParam("searchTerm") String searchTerm, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows);

    /**
     * 根据系统英文名称条件搜索在办件
     *
     * @param tenantId 租户id
     * @param systemName 系统英文名称
     * @param searchTerm 搜索词
     * @param page 页码
     * @param rows 行数
     * @return {@code Y9Page<HistoricProcessInstanceModel>} 通用请求返回对象 - rows 在办件
     * @since 9.6.6
     */
    @GetMapping(value = "/searchDoingListBySystemName")
    Y9Page<HistoricProcessInstanceModel> searchDoingListBySystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("systemName") String systemName, @RequestParam("searchTerm") String searchTerm,
        @RequestParam("page") Integer page, @RequestParam("rows") Integer rows);
}
