package net.risesoft.api.processadmin;

import java.util.List;

import net.risesoft.model.processadmin.HistoricProcessInstanceModel;
import net.risesoft.pojo.Y9Result;

/**
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
     * @return boolean
     */
    Y9Result<Object> deleteProcessInstance(String tenantId, String processInstanceId);

    /**
     * 根据流程实例id获取实例
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return HistoricProcessInstanceModel
     */
    Y9Result<HistoricProcessInstanceModel> getById(String tenantId, String processInstanceId);

    /**
     * 根据流程实例id和年度获取实例
     *
     * @param tenantId 租户id
     * @param id 流程实例id
     * @param year 年份
     * @return HistoricProcessInstanceModel
     */
    Y9Result<HistoricProcessInstanceModel> getByIdAndYear(String tenantId, String id, String year);

    /**
     * 根据父流程实例获取所有历史子流程实例
     *
     * @param tenantId 租户id
     * @param superProcessInstanceId 父流程实例id
     * @return List&lt;HistoricProcessInstanceModel&gt;
     */
    Y9Result<List<HistoricProcessInstanceModel>> getBySuperProcessInstanceId(String tenantId, String superProcessInstanceId);

    /**
     * 根据流程实例获取父流程实例
     *
     * @param tenantId 租户id
     * @param processInstanceId 父流程实例id
     * @return HistoricProcessInstanceModel
     */
    Y9Result<HistoricProcessInstanceModel> getSuperProcessInstanceById(String tenantId, String processInstanceId);

    /**
     * 恢复流程实例
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processInstanceId 流程实例id
     * @return boolean
     */
    Y9Result<Object> recoveryProcess(String tenantId, String userId, String processInstanceId);

    /**
     * 彻底删除流程实例
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return boolean
     */
    Y9Result<Object> removeProcess(String tenantId, String processInstanceId);

    /**
     * 彻底删除流程实例,岗位
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return boolean
     */
    Y9Result<Object> removeProcess4Position(String tenantId, String processInstanceId);

    /**
     * 设置优先级
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param priority priority
     * @throws Exception Exception
     */
    Y9Result<Object> setPriority(String tenantId, String processInstanceId, String priority) throws Exception;
}
