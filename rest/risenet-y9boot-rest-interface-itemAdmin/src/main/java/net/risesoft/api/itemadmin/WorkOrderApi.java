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
     * @param processSerialNumber
     * @param state
     * @param processInstanceId
     * @param resultFeedback
     * @return
     */
    Map<String, Object> changeWorkOrderState(String processSerialNumber, String state, String processInstanceId, String resultFeedback);

    /**
     * 删除草稿
     * 
     * @param processSerialNumber
     * @return
     */
    Map<String, Object> deleteDraft(String processSerialNumber);

    /**
     * 获取系统工单草稿
     * 
     * @param userId
     * @param searchTerm
     * @param page
     * @param rows
     * @return
     */
    Map<String, Object> draftlist(String userId, String searchTerm, Integer page, Integer rows);

    /**
     * 获取工单信息
     * 
     * @param processSerialNumber
     * @return
     */
    WorkOrderModel findByProcessSerialNumber(String processSerialNumber);

    /**
     * 获取管理员工单计数
     * 
     * @param userId
     * @return
     */
    Map<String, Object> getAdminCount();

    /**
     * 获取系统管理员未处理计数
     * 
     * @return
     */
    int getAdminTodoCount();

    /**
     * 获取个人工单计数
     * 
     * @param userId
     * @return
     */
    Map<String, Object> getCount(String userId);

    /**
     * 保存工单信息
     * 
     * @param workOrderModel
     * @return
     */
    Map<String, Object> saveWorkOrder(WorkOrderModel workOrderModel);

    /**
     * 获取管理员工单列表
     * 
     * @param searchTerm
     * @param handleType
     * @param page
     * @param rows
     * @return
     */
    Map<String, Object> workOrderAdminList(String searchTerm, String handleType, Integer page, Integer rows);

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
    Map<String, Object> workOrderList(String userId, String searchTerm, String handleType, Integer page, Integer rows);

}
