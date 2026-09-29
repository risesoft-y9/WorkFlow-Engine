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
     * 根据系统名称获取监控在办件
     *
     * @param systemName 系统英文名称
     * @param page 页码
     * @param rows 行数
     * @return {@code Y9Page<HistoricProcessInstanceModel>} 通用请求返回对象 - rows 在办件列表
     * @since 9.6.6
     */
    @GetMapping(value = "/getAllListBySystemName")
    Y9Page<HistoricProcessInstanceModel> getAllListBySystemName(@RequestParam String systemName,
        @RequestParam Integer page, @RequestParam Integer rows);

    /**
     * 根据流程定义Key获取监控在办件统计
     *
     * @param processDefinitionKey 流程定义Key
     * @return {@code Y9Result<Long>} 通用请求返回对象 - data 在办件数量
     * @since 9.6.6
     */
    @GetMapping(value = "/getDoingCountByProcessDefinitionKey")
    Y9Result<Long> getDoingCountByProcessDefinitionKey(@RequestParam String processDefinitionKey);

    /**
     * 根据系统英文名称获取监控在办件数量
     *
     * @param systemName 系统英文名称
     * @return {@code Y9Result<Long>} 通用请求返回对象 - data 在办件数量
     * @since 9.6.6
     */
    @GetMapping(value = "/getDoingCountBySystemName")
    Y9Result<Long> getDoingCountBySystemName(@RequestParam String systemName);

    /**
     * 根据流程定义Key获取监控在办件
     *
     * @param processDefinitionKey 流程定义Key
     * @param page 页码
     * @param rows 行数
     * @return {@code Y9Page<HistoricProcessInstanceModel>} 通用请求返回对象 - rows 在办件列表
     * @since 9.6.6
     */
    @GetMapping(value = "/getDoingListByProcessDefinitionKey")
    Y9Page<HistoricProcessInstanceModel> getDoingListByProcessDefinitionKey(@RequestParam String processDefinitionKey,
        @RequestParam Integer page, @RequestParam Integer rows);

    /**
     * 根据系统名称获取监控在办件
     *
     * @param systemName 系统英文名称
     * @param page 页码
     * @param rows 行数
     * @return {@code Y9Page<HistoricProcessInstanceModel>} 通用请求返回对象 - rows 在办件列表
     * @since 9.6.6
     */
    @GetMapping(value = "/getDoingListBySystemName")
    Y9Page<HistoricProcessInstanceModel> getDoingListBySystemName(@RequestParam String systemName,
        @RequestParam Integer page, @RequestParam Integer rows);

    /**
     * 根据流程定义Key获取监控办结件统计
     *
     * @param processDefinitionKey 流程定义Key
     * @return {@code Y9Result<Long>} 通用请求返回对象 - data 办结件统计
     * @since 9.6.6
     */
    @GetMapping(value = "/getDoneCountByProcessDefinitionKey")
    Y9Result<Long> getDoneCountByProcessDefinitionKey(@RequestParam String processDefinitionKey);

    /**
     * 根据流程定义Key条件搜索在办件
     *
     * @param processDefinitionKey 流程定义Key
     * @param searchTerm 搜索词
     * @param page 页码
     * @param rows 行数
     * @return {@code Y9Page<HistoricProcessInstanceModel>} 通用请求返回对象 - rows 在办件
     * @since 9.6.6
     */
    @GetMapping(value = "/searchDoingListByProcessDefinitionKey")
    Y9Page<HistoricProcessInstanceModel> searchDoingListByProcessDefinitionKey(
        @RequestParam String processDefinitionKey, @RequestParam String searchTerm, @RequestParam Integer page,
        @RequestParam Integer rows);

    /**
     * 根据系统英文名称条件搜索在办件
     *
     * @param systemName 系统英文名称
     * @param searchTerm 搜索词
     * @param page 页码
     * @param rows 行数
     * @return {@code Y9Page<HistoricProcessInstanceModel>} 通用请求返回对象 - rows 在办件
     * @since 9.6.6
     */
    @GetMapping(value = "/searchDoingListBySystemName")
    Y9Page<HistoricProcessInstanceModel> searchDoingListBySystemName(@RequestParam String systemName,
        @RequestParam String searchTerm, @RequestParam Integer page, @RequestParam Integer rows);
}
