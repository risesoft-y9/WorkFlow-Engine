package net.risesoft.api;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.processadmin.MonitorApi;
import net.risesoft.model.processadmin.HistoricProcessInstanceModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.CustomMonitorService;
import net.risesoft.service.CustomRecycleService;

/**
 * 监控流程实例接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/monitor", produces = MediaType.APPLICATION_JSON_VALUE)
public class MonitorApiImpl implements MonitorApi {

    private final CustomMonitorService customMonitorService;

    private final CustomRecycleService customRecycleService;

    /**
     * 根据系统名称获取监控在办件
     *
     * @param systemName 系统英文名称
     * @param page 页码
     * @param rows 行数
     * @return {@code Y9Page<HistoricProcessInstanceModel>} 通用请求返回对象 - rows 在办件列表
     * @since 9.6.6
     */
    @Override
    public Y9Page<HistoricProcessInstanceModel> getAllListBySystemName(@RequestParam String systemName,
        @RequestParam Integer page, @RequestParam Integer rows) {
        return customMonitorService.pageDoingListBySystemName(systemName, page, rows);
    }

    /**
     * 根据流程定义Key获取监控在办件统计数量
     *
     * @param processDefinitionKey 流程定义Key
     * @return {@code Y9Result<Long>} 通用请求返回对象 - data 在办件数量
     * @since 9.6.6
     */
    @Override
    public Y9Result<Long> getDoingCountByProcessDefinitionKey(@RequestParam String processDefinitionKey) {
        return Y9Result.success(customMonitorService.getDoingCountByProcessDefinitionKey(processDefinitionKey));
    }

    /**
     * 根据系统英文名称获取监控在办件数量
     *
     * @param systemName 系统英文名称
     * @return {@code Y9Result<Long>} 通用请求返回对象 - data 在办件数量
     * @since 9.6.6
     */
    @Override
    public Y9Result<Long> getDoingCountBySystemName(@RequestParam String systemName) {
        return Y9Result.success(customMonitorService.getDoingCountBySystemName(systemName));
    }

    /**
     * 根据流程定义Key获取监控在办件
     *
     * @param processDefinitionKey 流程定义Key
     * @param page 页码
     * @param rows 行数
     * @return {@code Y9Page<HistoricProcessInstanceModel>} 通用请求返回对象 - rows 在办件列表
     * @since 9.6.6
     */
    @Override
    @GetMapping(value = "/getDoingListByProcessDefinitionKey")
    public Y9Page<HistoricProcessInstanceModel> getDoingListByProcessDefinitionKey(
        @RequestParam String processDefinitionKey, @RequestParam Integer page, @RequestParam Integer rows) {
        return customMonitorService.pageDoingListByProcessDefinitionKey(processDefinitionKey, page, rows);
    }

    /**
     * 根据系统名称获取监控在办件
     *
     * @param systemName 系统英文名称
     * @param page 页码
     * @param rows 行数
     * @return {@code Y9Page<HistoricProcessInstanceModel>} 通用请求返回对象 - rows 在办件列表
     * @since 9.6.6
     */
    @Override
    public Y9Page<HistoricProcessInstanceModel> getDoingListBySystemName(@RequestParam String systemName,
        @RequestParam Integer page, @RequestParam Integer rows) {
        return customMonitorService.pageDoingListBySystemName(systemName, page, rows);
    }

    /**
     * 根据流程定义Key获取监控办结件统计数量
     *
     * @param processDefinitionKey 流程定义Key
     * @return {@code Y9Result<Long>} 通用请求返回对象 - data 办结件统计数量
     * @since 9.6.6
     */
    @Override
    public Y9Result<Long> getDoneCountByProcessDefinitionKey(@RequestParam String processDefinitionKey) {
        return Y9Result.success(customMonitorService.getDoneCountByProcessDefinitionKey(processDefinitionKey));
    }

    /**
     * 根据流程定义Key和其他条件搜索在办件
     *
     * @param processDefinitionKey 流程定义Key
     * @param searchTerm 搜索词
     * @param page 页码
     * @param rows 行数
     * @return {@code Y9Page<HistoricProcessInstanceModel>} 通用请求返回对象 - rows 在办件
     * @since 9.6.6
     */
    @Override
    public Y9Page<HistoricProcessInstanceModel> searchDoingListByProcessDefinitionKey(
        @RequestParam String processDefinitionKey, @RequestParam String searchTerm, @RequestParam Integer page,
        @RequestParam Integer rows) {
        return customMonitorService.searchDoingListByProcessDefinitionKey(processDefinitionKey, searchTerm, page, rows);
    }

    /**
     * 根据系统英文名称和其他条件搜索在办件
     *
     * @param systemName 系统英文名称
     * @param searchTerm 搜索词
     * @param page 页码
     * @param rows 行数
     * @return {@code Y9Page<HistoricProcessInstanceModel>} 通用请求返回对象 - rows 在办件
     * @since 9.6.6
     */
    @Override
    public Y9Page<HistoricProcessInstanceModel> searchDoingListBySystemName(@RequestParam String systemName,
        @RequestParam String searchTerm, @RequestParam Integer page, @RequestParam Integer rows) {
        return customMonitorService.searchDoingListBySystemName(systemName, searchTerm, page, rows);
    }
}
