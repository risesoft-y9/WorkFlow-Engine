package net.risesoft.api.processadmin;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.processadmin.HistoricProcessInstanceModel;
import net.risesoft.pojo.Y9Result;

/**
 * 流程实例相关接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface HistoricProcessApi {

    /**
     * 删除流程实例，在办件设为暂停，办结件加删除标识
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @PostMapping("/deleteProcessInstance")
    Y9Result<Object> deleteProcessInstance(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 根据流程实例id获取实例
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<HistoricProcessInstanceModel>} 通用请求返回对象 - data 历史流程实例
     * @since 9.6.6
     */
    @GetMapping("/getById")
    Y9Result<HistoricProcessInstanceModel> getById(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 根据流程实例id和年度获取实例
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param year 年份
     * @return {@code Y9Result<HistoricProcessInstanceModel>} 通用请求返回对象 - data 历史流程实例
     * @since 9.6.6
     */
    @GetMapping("/getByIdAndYear")
    Y9Result<HistoricProcessInstanceModel> getByIdAndYear(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId,
        @RequestParam(value = "year", required = false) String year);

    /**
     * 根据父流程实例获取所有历史子流程实例
     *
     * @param tenantId 租户id
     * @param superProcessInstanceId 父流程实例id
     * @return {@code Y9Result<List<HistoricProcessInstanceModel>>} 通用请求返回对象 - data 历史流程实例
     * @since 9.6.6
     */
    @GetMapping("/getBySuperProcessInstanceId")
    Y9Result<List<HistoricProcessInstanceModel>> getBySuperProcessInstanceId(@RequestParam("tenantId") String tenantId,
        @RequestParam("superProcessInstanceId") String superProcessInstanceId);

    /**
     * 根据流程实例获取父流程实例
     *
     * @param tenantId 租户id
     * @param processInstanceId 父流程实例id
     * @return {@code Y9Result<HistoricProcessInstanceModel>} 通用请求返回对象 - data 历史流程实例
     * @since 9.6.6
     */
    @GetMapping("/getSuperProcessInstanceById")
    Y9Result<HistoricProcessInstanceModel> getSuperProcessInstanceById(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 彻底删除流程实例
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @PostMapping("/removeProcess")
    Y9Result<Object> removeProcess(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 设置流程优先级
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param priority 优先级
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @throws Exception Exception
     * @since 9.6.6
     */
    @PostMapping("/setPriority")
    Y9Result<Object> setPriority(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId, @RequestParam("priority") String priority)
        throws Exception;

}
