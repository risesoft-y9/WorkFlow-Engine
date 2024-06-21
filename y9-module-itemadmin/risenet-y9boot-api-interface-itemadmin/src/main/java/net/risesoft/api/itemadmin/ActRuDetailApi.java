package net.risesoft.api.itemadmin;

import java.util.List;

import net.risesoft.model.itemadmin.ActRuDetailModel;
import net.risesoft.pojo.Y9Result;

/**
 * 流转信息接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface ActRuDetailApi {

    /**
     * 标记流程为办结
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return Y9Result<Object>
     */
    Y9Result<Object> endByProcessInstanceId(String tenantId, String processInstanceId);

    /**
     * 标记流程为办结
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程序列号
     * @return Y9Result<Object>
     */
    Y9Result<Object> endByProcessSerialNumber(String tenantId, String processSerialNumber);

    /**
     * 根据流程实例和状态查找正在办理的人员信息
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param status 0为待办，1位在办
     * @return Y9Result<List < ActRuDetailModel>>
     */
    Y9Result<List<ActRuDetailModel>> findByProcessInstanceIdAndStatus(String tenantId, String processInstanceId, int status);

    /**
     * 根据流程序列号查找正在办理的人员信息
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程序列号
     * @return Y9Result<List < ActRuDetailModel>>
     */
    Y9Result<List<ActRuDetailModel>> findByProcessSerialNumber(String tenantId, String processSerialNumber);

    /**
     * 根据流程序列号查找正在办理的人员信息
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程序列号
     * @param assignee 办理人Id
     * @return Y9Result<ActRuDetailModel>
     */
    Y9Result<ActRuDetailModel> findByProcessSerialNumberAndAssignee(String tenantId, String processSerialNumber, String assignee);

    /**
     * 根据流程序列号查找正在办理的人员信息
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程序列号
     * @param status 0为待办，1位在办
     * @return Y9Result<List<ActRuDetailModel>>
     */
    Y9Result<List<ActRuDetailModel>> findByProcessSerialNumberAndStatus(String tenantId, String processSerialNumber, int status);

    /**
     * 恢复整个流程的办件详情
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return Y9Result<Object>
     */
    Y9Result<Object> recoveryByProcessInstanceId(String tenantId, String processInstanceId);

    /**
     * 删除整个流程的办件详情
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例Id
     * @return Y9Result<Object>
     */
    Y9Result<Object> removeByProcessInstanceId(String tenantId, String processInstanceId);

    /**
     * 删除整个流程的办件详情
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程序列号
     * @return Y9Result<Object>
     */
    Y9Result<Object> removeByProcessSerialNumber(String tenantId, String processSerialNumber);

    /**
     * 删除某个参与人的办件详情
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程序列号
     * @param assignee 办理人Id
     * @return Y9Result<Object>
     */
    Y9Result<Object> removeByProcessSerialNumberAndAssignee(String tenantId, String processSerialNumber, String assignee);

    /**
     * 保存或者更新
     *
     * @param tenantId 租户id
     * @param actRuDetailModel 办理详情实体
     * @return Y9Result<Object>
     */
    Y9Result<Object> saveOrUpdate(String tenantId, ActRuDetailModel actRuDetailModel);

    /**
     * 恢复整个流程的办件详情
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例Id
     * @return Y9Result<Object>
     */
    Y9Result<Object> syncByProcessInstanceId(String tenantId, String processInstanceId);
}
