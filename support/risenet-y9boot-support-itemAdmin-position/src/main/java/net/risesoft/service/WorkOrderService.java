package net.risesoft.service;

import java.util.Map;

import net.risesoft.entity.WorkOrderEntity;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface WorkOrderService {

    /**
     * 改变工单状态
     *
     * @param processSerialNumber
     * @param state
     * @param processInstanceId
     * @param resultFeedback
     * @return
     */
    public Map<String, Object> changeWorkOrderState(String processSerialNumber, String state, String processInstanceId,
        String resultFeedback);

    /**
     * 删除草稿
     *
     * @param processSerialNumber
     * @return
     */
    public Map<String, Object> deleteDraft(String processSerialNumber);

    /**
     * 获取工单草稿
     *
     * @param userId
     * @param searchTerm
     * @param page
     * @param rows
     * @return
     */
    public Map<String, Object> draftlist(String userId, String searchTerm, Integer page, Integer rows);

    /**
     * 获取工单信息
     *
     * @param processSerialNumber
     * @return
     */
    public WorkOrderEntity findByProcessSerialNumber(String processSerialNumber);

    /**
     * 获取管理员工单计数
     *
     * @return
     */
    public Map<String, Object> getAdminCount();

    /**
     * 获取管理员未处理工单计数
     *
     * @return
     */
    public int getAdminTodoCount();

    /**
     * 获取个人工单计数
     *
     * @param userId
     * @return
     */
    public Map<String, Object> getCount(String userId);

    /**
     * 保存工单信息
     *
     * @param workOrder
     * @return
     */
    public Map<String, Object> saveWorkOrder(WorkOrderEntity workOrder);

    /**
     * 获取管理员工单列表
     *
     * @param searchTerm
     * @param handleType
     * @param page
     * @param rows
     * @return
     */
    public Map<String, Object> workOrderAdminList(String searchTerm, String handleType, Integer page, Integer rows);

    /**
     * 获取工单列表
     *
     * @param userId
     * @param searchTerm
     * @param handleType
     * @param page
     * @param rows
     * @return
     */
    public Map<String, Object> workOrderList(String userId, String searchTerm, String handleType, Integer page,
        Integer rows);

}
