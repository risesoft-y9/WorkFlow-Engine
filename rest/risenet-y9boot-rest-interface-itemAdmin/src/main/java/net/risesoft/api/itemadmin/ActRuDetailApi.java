package net.risesoft.api.itemadmin;

import java.util.List;

import net.risesoft.model.itemadmin.ActRuDetailModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface ActRuDetailApi {

    /**
     * 标记流程为办结
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程序列号
     * @return
     */
    boolean endByProcessInstanceId(String tenantId, String processInstanceId);

    /**
     * 标记流程为办结
     *
     * @param tenantId
     * @param processSerialNumber
     * @return
     */
    boolean endByProcessSerialNumber(String tenantId, String processSerialNumber);

    /**
     * 根据流程实例和状态查找正在办理的人员信息
     *
     * @param tenantId
     * @param processInstanceId
     * @param status 0为待办，1位在办
     * @return
     */
    List<ActRuDetailModel> findByProcessInstanceIdAndStatus(String tenantId, String processInstanceId, int status);

    /**
     * 根据流程序列号查找正在办理的人员信息
     *
     * @param tenantId
     * @param processSerialNumber
     * @return
     */
    List<ActRuDetailModel> findByProcessSerialNumber(String tenantId, String processSerialNumber);

    /**
     * 根据流程序列号查找正在办理的人员信息
     *
     * @param tenantId
     * @param processSerialNumber
     * @param assignee
     * @return
     */
    ActRuDetailModel findByProcessSerialNumberAndAssignee(String tenantId, String processSerialNumber, String assignee);

    /**
     * 根据流程序列号查找正在办理的人员信息
     *
     * @param tenantId
     * @param processSerialNumber
     * @param status 0为待办，1位在办
     * @return
     */
    List<ActRuDetailModel> findByProcessSerialNumberAndStatus(String tenantId, String processSerialNumber, int status);

    /**
     * 恢复整个流程的办件详情
     *
     * @param tenantId
     * @param processInstanceId
     * @return
     */
    boolean recoveryByProcessInstanceId(String tenantId, String processInstanceId);

    /**
     * 删除整个流程的办件详情
     *
     * @param tenantId
     * @param processInstanceId
     * @return
     */
    boolean removeByProcessInstanceId(String tenantId, String processInstanceId);

    /**
     * 删除整个流程的办件详情
     *
     * @param tenantId
     * @param processSerialNumber
     * @return
     */
    boolean removeByProcessSerialNumber(String tenantId, String processSerialNumber);

    /**
     * 删除某个参与人的办件详情
     * 
     * @param tenantId
     * @param processSerialNumber
     * @param assignee
     * @return
     */
    boolean removeByProcessSerialNumberAndAssignee(String tenantId, String processSerialNumber, String assignee);

    /**
     * 保存或者更新
     *
     * @param tenantId
     * @param actRuDetailModel
     * @return
     */
    boolean saveOrUpdate(String tenantId, ActRuDetailModel actRuDetailModel);

    /**
     * 恢复整个流程的办件详情
     *
     * @param tenantId
     * @param processInstanceId
     * @return
     */
    boolean syncByProcessInstanceId(String tenantId, String processInstanceId);
}
