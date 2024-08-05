package net.risesoft.api;

import javax.validation.constraints.NotBlank;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.processadmin.ProcessTodoApi;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.model.processadmin.Y9FlowableCountModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.CustomDoingService;
import net.risesoft.service.CustomTodoService;
import net.risesoft.y9.FlowableTenantInfoHolder;

/**
 * 待办件列表
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/processTodo")
public class ProcessTodoApiImpl implements ProcessTodoApi {

    private final CustomTodoService customTodoService;

    private final CustomDoingService customDoingService;

    /**
     * 根据岗位id获取待办数量
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @return {@code Y9Result<Long>} 通用请求返回对象 - data 是待办数量
     * @since 9.6.6
     */
    @Override
    public Y9Result<Long> countByOrgUnitId(@RequestParam String tenantId, @RequestParam String orgUnitId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        return Y9Result.success(customTodoService.getCountByUserId(orgUnitId));
    }

    /**
     * 根据人员、岗位id，流程定义Key获取对应事项的办件统计（包括待办件，在办件，办结件）
     *
     * @param tenantId 租户Id
     * @param userId 人员、岗位id
     * @param processDefinitionKey 流程定义Key
     * @return {@code Y9Result<Y9FlowableCountModel>} 通用请求返回对象 - data 是办件统计
     * @since 9.6.6
     */
    @Override
    public Y9Result<Y9FlowableCountModel> getCountByUserIdAndProcessDefinitionKey(@RequestParam String tenantId,
        @RequestParam String userId, @RequestParam String processDefinitionKey) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        long todoCount = customTodoService.getCountByUserIdAndProcessDefinitionKey(userId, processDefinitionKey);
        long doingCount = customDoingService.getCountByUserIdAndProcessDefinitionKey(userId, processDefinitionKey);
        long doneCount = 0;
        // customDoneService.getCountByUserIdAndProcessDefinitionKey(userId, processDefinitionKey);
        return Y9Result.success(new Y9FlowableCountModel(todoCount, doingCount, doneCount));
    }

    /**
     * 根据人员、岗位id，系统标识获取对应事项的办件统计（包括待办件，在办件，办结件）
     *
     * @param tenantId 租户Id
     * @param userId 人员、岗位id
     * @param systemName 系统名称
     * @return {@code Y9Result<Y9FlowableCountModel>} 通用请求返回对象 - data 是办件统计
     * @since 9.6.6
     */
    @Override
    public Y9Result<Y9FlowableCountModel> getCountByUserIdAndSystemName(@RequestParam String tenantId,
        @RequestParam String userId, @RequestParam String systemName) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        long todoCount = customTodoService.getCountByUserIdAndSystemName(userId, systemName);
        long doingCount = customDoingService.getCountByUserIdAndSystemName(userId, systemName);
        long doneCount = 0;
        // customDoneService.getCountByUserIdAndSystemName(userId, systemName);
        return Y9Result.success(new Y9FlowableCountModel(todoCount, doingCount, doneCount));
    }

    /**
     * 根据人员、岗位id，流程定义Key获取用户的待办任务(分页)
     *
     * @param tenantId 租户Id
     * @param userId 人员、岗位id
     * @param processDefinitionKey 流程定义Key
     * @param page 页码
     * @param rows 行数
     * @return {@code Y9Page<TaskModel>} 通用请求返回对象 - rows 是待办任务列表
     * @since 9.6.6
     */
    @Override
    public Y9Page<TaskModel> getListByUserIdAndProcessDefinitionKey(@RequestParam @NotBlank String tenantId,
        @RequestParam @NotBlank String userId, @RequestParam @NotBlank String processDefinitionKey,
        @RequestParam Integer page, @RequestParam Integer rows) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        return customTodoService.pageByUserIdAndProcessDefinitionKey(userId, processDefinitionKey, page, rows);
    }

    /**
     * 根据人员、岗位id,系统标识获取用户的待办任务(分页)
     *
     * @param tenantId 租户Id
     * @param userId 人员、岗位id
     * @param systemName 系统名称
     * @param page 页码
     * @param rows 行数
     * @return {@code Y9Page<TaskModel>} 通用请求返回对象 - rows 是待办任务列表
     * @since 9.6.6
     */
    @Override
    public Y9Page<TaskModel> getListByUserIdAndSystemName(@RequestParam @NotBlank String tenantId,
        @RequestParam @NotBlank String userId, @RequestParam @NotBlank String systemName, @RequestParam Integer page,
        @RequestParam Integer rows) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        return customTodoService.pageByUserIdAndSystemName(userId, systemName, page, rows);
    }

    /**
     * 获取待办件列表
     *
     * @param tenantId 租户id
     * @param userId 人员、岗位id
     * @param systemName 系统名称
     * @param processDefinitionKey 流程定义key
     * @param target 目标
     * @param page 页码
     * @param rows 行数
     * @return {@code Y9Page<TaskModel>} 通用请求返回对象 - rows 是待办任务列表
     */
    @Override
    public Y9Page<TaskModel> getListByUserIdAndSystemName4xxx(@RequestParam String tenantId,
        @RequestParam String userId, String systemName, String processDefinitionKey, String target,
        @RequestParam Integer page, @RequestParam Integer rows) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        return customTodoService.pageByUserIdAndSystemName4xxx(userId, systemName, processDefinitionKey, target, page,
            rows);
    }

    /**
     * 根据人员、岗位id,流程定义key获取对应事项的待办数量
     *
     * @param tenantId 租户id
     * @param userId 人员、岗位id
     * @param processDefinitionKey 流程定义key
     * @return {@code Y9Result<Long>} 通用请求返回对象 - data 是待办数量
     * @since 9.6.6
     */
    @Override
    public Y9Result<Long> getTodoCountByUserIdAndProcessDefinitionKey(@RequestParam String tenantId,
        @RequestParam String userId, @RequestParam String processDefinitionKey) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        return Y9Result
            .success(customTodoService.getCountByUserIdAndProcessDefinitionKey(userId, processDefinitionKey));
    }

    /**
     * 根据人员、岗位id，系统标识获取对应事项的待办数量
     *
     * @param tenantId 租户Id
     * @param userId 人员、岗位id
     * @param systemName 系统名称
     * @return {@code Y9Result<Long>} 通用请求返回对象 - data 是待办数量
     * @since 9.6.6
     */
    @Override
    public Y9Result<Long> getTodoCountByUserIdAndSystemName(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam String systemName) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        return Y9Result.success(customTodoService.getCountByUserIdAndSystemName(userId, systemName));
    }

    /**
     * 根据人员、岗位id获取用户的待办任务(分页)
     *
     * @param tenantId 租户Id
     * @param userId 人员、岗位id
     * @param page 页码
     * @param rows 行数
     * @return {@code Y9Page<TaskModel>} 通用请求返回对象 - rows 是待办任务列表
     * @since 9.6.6
     */
    @Override
    public Y9Page<TaskModel> pageByUserId(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam Integer page, @RequestParam Integer rows) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        return customTodoService.pageByUserId(userId, page, rows);
    }

    /**
     * 根据流程定义Key条件搜索待办件
     *
     * @param tenantId 租户Id
     * @param userId 人员、岗位id
     * @param processDefinitionKey 流程定义Key
     * @param searchTerm 搜索词
     * @param page 页码
     * @param rows 行数
     * @return {@code Y9Page<TaskModel>} 通用请求返回对象 - rows 是待办任务列表
     * @since 9.6.6
     */
    @Override
    public Y9Page<TaskModel> searchListByUserIdAndProcessDefinitionKey(@RequestParam String tenantId,
        @RequestParam String userId, @RequestParam String processDefinitionKey, String searchTerm,
        @RequestParam Integer page, @RequestParam Integer rows) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        return customTodoService.searchListByUserIdAndProcessDefinitionKey(userId, processDefinitionKey, searchTerm,
            page, rows);
    }

    /**
     * 根据系统英文名称条件搜索待办件
     *
     * @param tenantId 租户Id
     * @param userId 人员、岗位id
     * @param systemName 系统英文名称
     * @param searchTerm 搜索词
     * @param page 页码
     * @param rows 行数
     * @return {@code Y9Page<TaskModel>} 通用请求返回对象 - rows 是待办任务列表
     * @since 9.6.6
     */
    @Override
    public Y9Page<TaskModel> searchListByUserIdAndSystemName(@RequestParam @NotBlank String tenantId,
        @RequestParam @NotBlank String userId, @RequestParam @NotBlank String systemName, String searchTerm,
        @RequestParam Integer page, @RequestParam Integer rows) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        return customTodoService.searchListByUserIdAndSystemName(userId, systemName, searchTerm, page, rows);
    }
}
