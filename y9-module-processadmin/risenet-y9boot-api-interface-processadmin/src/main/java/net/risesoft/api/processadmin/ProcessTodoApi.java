package net.risesoft.api.processadmin;

import javax.validation.constraints.NotBlank;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.model.processadmin.Y9FlowableCountModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;

/**
 * 待办件列表
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface ProcessTodoApi {

    /**
     * 根据人员、岗位id获取待办统计
     *
     * @param tenantId 租户Id
     * @param orgUnitId 人员、岗位Id
     * @return {@code Y9Result<Long>} 通用请求返回对象 - data 是办件统计
     * @since 9.6.6
     */
    @GetMapping("/countByOrgUnitId")
    Y9Result<Long> countByOrgUnitId(@RequestParam("tenantId") String tenantId,
        @RequestParam("orgUnitId") String orgUnitId);

    /**
     * 根据人员id，流程定义Key获取对应事项的办件统计（包括待办件，在办件，办结件）
     *
     * @param tenantId 租户Id
     * @param userId 人员、岗位Id
     * @param processDefinitionKey 流程定义Key
     * @return {@code Y9Result<Y9FlowableCountModel>} 通用请求返回对象 - data 是办件统计
     * @since 9.6.6
     */
    @GetMapping("/getCountByUserIdAndProcessDefinitionKey")
    Y9Result<Y9FlowableCountModel> getCountByUserIdAndProcessDefinitionKey(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("processDefinitionKey") String processDefinitionKey);

    /**
     * 根据人员id，系统标识获取对应事项的办件统计（包括待办件，在办件，办结件）
     *
     * @param tenantId 租户Id
     * @param userId 人员、岗位Id
     * @param systemName 系统名称
     * @return {@code Y9Result<Y9FlowableCountModel>} 通用请求返回对象 - data 是办件统计
     * @since 9.6.6
     */
    @GetMapping("/getCountByUserIdAndSystemName")
    Y9Result<Y9FlowableCountModel> getCountByUserIdAndSystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("systemName") String systemName);

    /**
     * 根据人员Id，流程定义Key获取用户的待办任务(分页)
     *
     * @param tenantId 租户Id
     * @param userId 人员、岗位Id
     * @param processDefinitionKey 流程定义Key
     * @param page 页码
     * @param rows 行数
     * @return {@code Y9Page<TaskModel>} 通用请求返回对象 - rows 是待办任务列表
     * @since 9.6.6
     */
    @GetMapping("/getListByUserIdAndProcessDefinitionKey")
    Y9Page<TaskModel> getListByUserIdAndProcessDefinitionKey(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("userId") @NotBlank String userId,
        @RequestParam("processDefinitionKey") @NotBlank String processDefinitionKey, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows);

    /**
     * 根据人员Id,系统标识获取用户的待办任务(分页)
     *
     * @param tenantId 租户Id
     * @param userId 人员、岗位Id
     * @param systemName 系统名称
     * @param page 页码
     * @param rows 行数
     * @return {@code Y9Page<TaskModel>} 通用请求返回对象 - rows 是待办任务列表
     * @since 9.6.6
     */
    @GetMapping("/getListByUserIdAndSystemName")
    Y9Page<TaskModel> getListByUserIdAndSystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("systemName") String systemName,
        @RequestParam("page") Integer page, @RequestParam("rows") Integer rows);

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
    @GetMapping("/getListByUserIdAndSystemName4xxx")
    Y9Page<TaskModel> getListByUserIdAndSystemName4xxx(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam(value = "systemName", required = false) String systemName,
        @RequestParam(value = "processDefinitionKey", required = false) String processDefinitionKey,
        @RequestParam(value = "target", required = false) String target, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows);

    /**
     * 根据人员、岗位id,流程定义key获取对应事项的待办数量
     *
     * @param tenantId 租户id
     * @param userId 人员、岗位id
     * @param processDefinitionKey 流程定义key
     * @return {@code Y9Result<Long>} 通用请求返回对象 - data 是待办数量
     * @since 9.6.6
     */
    @GetMapping("/getTodoCountByUserIdAndProcessDefinitionKey")
    Y9Result<Long> getTodoCountByUserIdAndProcessDefinitionKey(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("processDefinitionKey") String processDefinitionKey);

    /**
     * 根据人员id，系统标识获取对应事项的待办数量
     *
     * @param tenantId 租户Id
     * @param userId 人员、岗位Id
     * @param systemName 系统名称
     * @return {@code Y9Result<Long>} 通用请求返回对象 - data 是待办数量
     * @since 9.6.6
     */
    @GetMapping("/getTodoCountByUserIdAndSystemName")
    Y9Result<Long> getTodoCountByUserIdAndSystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("systemName") String systemName);

    /**
     * 根据人员、岗位id获取待办任务(分页)
     *
     * @param tenantId 租户Id
     * @param userId 人员、岗位id
     * @param page 页码
     * @param rows 行数
     * @return {@code Y9Page<TaskModel>} 通用请求返回对象 - rows 是待办任务列表
     * @since 9.6.6
     */
    @GetMapping("/pageByUserId")
    Y9Page<TaskModel> pageByUserId(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("page") Integer page, @RequestParam("rows") Integer rows);

    /**
     * 根据流程定义Key条件搜索待办件
     *
     * @param tenantId 租户Id
     * @param userId 人员、岗位Id
     * @param processDefinitionKey 流程定义Key
     * @param searchTerm 搜索词
     * @param page 页码
     * @param rows 行数
     * @return {@code Y9Page<TaskModel>} 通用请求返回对象 - rows 是待办任务列表
     * @since 9.6.6
     */
    @GetMapping("/searchListByUserIdAndProcessDefinitionKey")
    Y9Page<TaskModel> searchListByUserIdAndProcessDefinitionKey(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("processDefinitionKey") String processDefinitionKey,
        @RequestParam(value = "searchTerm", required = false) String searchTerm, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows);

    /**
     * 根据系统英文名称条件搜索待办件
     *
     * @param tenantId 租户Id
     * @param userId 人员、岗位Id
     * @param systemName 系统英文名称
     * @param searchTerm 搜索词
     * @param page 页码
     * @param rows 行数
     * @return {@code Y9Page<TaskModel>} 通用请求返回对象 - rows 是待办任务列表
     * @since 9.6.6
     */
    @GetMapping("/searchListByUserIdAndSystemName")
    Y9Page<TaskModel> searchListByUserIdAndSystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("systemName") String systemName,
        @RequestParam(value = "searchTerm", required = false) String searchTerm, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows);

}
