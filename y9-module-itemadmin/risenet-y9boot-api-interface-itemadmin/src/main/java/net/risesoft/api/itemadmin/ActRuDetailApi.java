package net.risesoft.api.itemadmin;

import java.util.List;

import net.risesoft.model.itemadmin.ActRuDetailModel;

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
     * @return boolean
     */
    boolean endByProcessInstanceId(String tenantId, String processInstanceId);

    /**
     * 标记流程为办结
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程序列号
     * @return boolean
     */
    boolean endByProcessSerialNumber(String tenantId, String processSerialNumber);

    /**
     * 根据流程实例和状态查找正在办理的人员信息
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param status 0为待办，1位在办
     * @return List&lt;ActRuDetailModel&gt;
     */
    List<ActRuDetailModel> findByProcessInstanceIdAndStatus(String tenantId, String processInstanceId, int status);

    /**
     * 根据流程序列号查找正在办理的人员信息
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程序列号
     * @return List&lt;ActRuDetailModel&gt;
     */
    List<ActRuDetailModel> findByProcessSerialNumber(String tenantId, String processSerialNumber);

    /**
     * 根据流程序列号查找正在办理的人员信息
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程序列号
     * @param assignee 办理人Id
     * @return ActRuDetailModel
     */
    ActRuDetailModel findByProcessSerialNumberAndAssignee(String tenantId, String processSerialNumber, String assignee);

    /**
     * 根据流程序列号查找正在办理的人员信息
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程序列号
     * @param status 0为待办，1位在办
     * @return List&lt;ActRuDetailModel&gt;
     */
    List<ActRuDetailModel> findByProcessSerialNumberAndStatus(String tenantId, String processSerialNumber, int status);

    /**
     * 恢复整个流程的办件详情
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return boolean
     */
    boolean recoveryByProcessInstanceId(String tenantId, String processInstanceId);

    /**
     * 删除整个流程的办件详情
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例Id
     * @return boolean
     */
    boolean removeByProcessInstanceId(String tenantId, String processInstanceId);

    /**
     * 删除整个流程的办件详情
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程序列号
     * @return boolean
     */
    boolean removeByProcessSerialNumber(String tenantId, String processSerialNumber);

    /**
     * 删除某个参与人的办件详情
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程序列号
     * @param assignee 办理人Id
     * @return boolean
     */
    boolean removeByProcessSerialNumberAndAssignee(String tenantId, String processSerialNumber, String assignee);

    /**
     * 保存或者更新
     *
     * @param tenantId 租户id
     * @param actRuDetailModel 办理详情实体
     * @return boolean
     */
    boolean saveOrUpdate(String tenantId, ActRuDetailModel actRuDetailModel);

    /**
     * 恢复整个流程的办件详情
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例Id
     * @return boolean
     */
    boolean syncByProcessInstanceId(String tenantId, String processInstanceId);
}
