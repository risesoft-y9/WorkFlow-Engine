package net.risesoft.api.itemadmin;

import java.util.Map;

import net.risesoft.model.itemadmin.WorkOrderModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface WorkOrderApi {

    /**
     * 改变工单状态
     *
     * @param processSerialNumber 流程序列号
     * @param state 工单状态
     * @param processInstanceId 流程实例id
     * @param resultFeedback 结果反馈
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> changeWorkOrderState(String processSerialNumber, String state, String processInstanceId, String resultFeedback);

    /**
     * 删除草稿
     *
     * @param processSerialNumber 流程序列号
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> deleteDraft(String processSerialNumber);

    /**
     * 获取系统工单草稿
     *
     * @param userId 用户id
     * @param searchTerm searchTerm
     * @param page page
     * @param rows rows
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> draftlist(String userId, String searchTerm, Integer page, Integer rows);

    /**
     * 获取工单信息
     *
     * @param processSerialNumber 流程序列号
     * @return WorkOrderModel
     */
    WorkOrderModel findByProcessSerialNumber(String processSerialNumber);

    /**
     * 获取管理员工单计数
     *
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> getAdminCount();

    /**
     * 获取系统管理员未处理计数
     *
     * @return int
     */
    int getAdminTodoCount();

    /**
     * 获取个人工单计数
     *
     * @param userId 用户id
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> getCount(String userId);

    /**
     * 保存工单信息
     *
     * @param workOrderModel 工单信息
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> saveWorkOrder(WorkOrderModel workOrderModel);

    /**
     * 获取管理员工单列表
     *
     * @param searchTerm searchTerm
     * @param handleType 处理类型，0为草稿，1为未处理，2为处理中，3为已处理
     * @param page page
     * @param rows rows
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> workOrderAdminList(String searchTerm, String handleType, Integer page, Integer rows);

    /**
     * 获取工单列表
     *
     * @param userId 用户id
     * @param searchTerm searchTerm
     * @param handleType 处理类型，0为草稿，1为未处理，2为处理中，3为已处理
     * @param page page
     * @param rows rows
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> workOrderList(String userId, String searchTerm, String handleType, Integer page, Integer rows);

}
