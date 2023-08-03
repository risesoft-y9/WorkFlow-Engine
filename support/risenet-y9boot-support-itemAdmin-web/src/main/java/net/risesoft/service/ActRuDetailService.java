package net.risesoft.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import net.risesoft.entity.ActRuDetail;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
public interface ActRuDetailService {

    /**
     * Description: 复制当前流程的参与人信息到新流程，并且设置为在办信息
     * 
     * @param oldProcessSerialNumber
     * @param newProcessSerialNumber
     * @param newProcessInstanceId
     */
    void copy(String oldProcessSerialNumber, String newProcessSerialNumber, String newProcessInstanceId);

    /**
     * 
     * Description: 查找个人办结的件的数量
     * 
     * @param systemName
     * @return
     */
    int countBySystemName(String systemName);

    /**
     * 查找个人办结的件的数量
     * 
     * @param systemName
     * @param assignee
     * @param rows
     * @param page
     * @param sort
     * @return
     */
    int countBySystemNameAndAssignee(String systemName, String assignee);

    /**
     * 查找个人回收站的数量
     * 
     * @param systemName
     * @param assignee
     * @param rows
     * @param page
     * @param sort
     * @return
     */
    int countBySystemNameAndAssigneeAndDeletedTrue(String systemName, String assignee);

    /**
     * 查找你个人在办的件
     * 
     * @param systemName
     * @param assignee
     * @param status 0为待办，1位在办
     * @param rows
     * @param page
     * @param sort
     * @return
     */
    int countBySystemNameAndAssigneeAndStatus(String systemName, String assignee, int status);

    /**
     * Description:
     * 
     * @param systemName
     * @return
     */
    int countBySystemNameAndDeletedTrue(String systemName);

    /**
     * Description:
     * 
     * @param systemName
     * @param deptId
     * @return
     */
    int countBySystemNameAndDeptIdAndDeletedTrue(String systemName, String deptId);

    /**
     * Description:
     * 
     * @param systemName
     * @param deptId
     * @return
     */
    int countBySystemNameAndDeptIdAndEndedTrueAndDeletedTrue(String systemName, String deptId);

    /**
     * 查找监控回收站的数量
     * 
     * @param systemName
     * @return
     */
    int countBySystemNameAndEndedTrueAndDeletedTrue(String systemName);

    /**
     * 删除流程的办理详情
     * 
     * @param processSerialNumber
     * @return
     */
    boolean deletedByProcessSerialNumber(String processSerialNumber);

    /**
     * 标记流程为办结
     * 
     * @param processInstanceId
     * @return
     */
    boolean endByProcessInstanceId(String processInstanceId);

    /**
     * 标记流程为办结
     * 
     * @param processSerialNumber
     * @return
     */
    boolean endByProcessSerialNumber(String processSerialNumber);

    /**
     * 查找你个人在办的件
     * 
     * @param systemName
     * @param assignee
     * @param status 0为待办，1位在办
     * @param rows
     * @param page
     * @param sort
     * @return
     */
    List<ActRuDetail> findByAssigneeAndStatus(String assignee, int status);

    /**
     * 查找你个人在办的件
     * 
     * @param systemName
     * @param assignee
     * @param status 0为待办，1位在办
     * @param rows
     * @param page
     * @param sort
     * @return
     */
    Page<ActRuDetail> findByAssigneeAndStatus(String assignee, int status, int rows, int page, Sort sort);

    /**
     * Description: 查找你个人在办的件
     * 
     * @param assignee
     * @param status
     * @param systemNameList
     * @param rows
     * @param page
     * @param sort
     * @return
     */
    Page<ActRuDetail> findByAssigneeAndStatusAndSystemNameIn(String assignee, int status, List<String> systemNameList,
        int rows, int page, Sort sort);

    /**
     * 根据流程实例Id和办理人查找
     * 
     * @param processInstanceId
     * @param assignee
     * @return
     */
    ActRuDetail findByProcessInstanceIdAndAssignee(String processInstanceId, String assignee);

    /**
     * 根据流程实例Id和状态查找
     * 
     * @param processInstanceId
     * @param status 0位待办，1位在办
     * @return
     */
    List<ActRuDetail> findByProcessInstanceIdAndStatus(String processInstanceId, int status);

    /**
     * 根据流程实例Id和办理人查找
     * 
     * @param processSerialNumber
     * @return
     */
    List<ActRuDetail> findByProcessSerialNumber(String processSerialNumber);

    /**
     * 根据流程实例Id和办理人查找
     * 
     * @param processSerialNumber
     * @param assignee
     * @return
     */
    ActRuDetail findByProcessSerialNumberAndAssignee(String processSerialNumber, String assignee);

    /**
     * Description: 根据流程序列号和是否结束查找
     * 
     * @param processSerialNumber
     * @param ended
     * @return
     */
    List<ActRuDetail> findByProcessSerialNumberAndEnded(String processSerialNumber, boolean ended);

    /**
     * 根据流程实例Id和状态查找
     * 
     * @param processSerialNumber
     * @param status 0位待办，1位在办
     * @return
     */
    List<ActRuDetail> findByProcessSerialNumberAndStatus(String processSerialNumber, int status);

    /**
     * 查找所有在办的件
     * 
     * @param systemName
     * @param rows
     * @param page
     * @param sort
     * @return
     */
    Page<ActRuDetail> findBySystemName(String systemName, int rows, int page, Sort sort);

    /**
     * 查找个人办结的件
     * 
     * @param systemName
     * @param assignee
     * @param rows
     * @param page
     * @param sort
     * @return
     */
    Page<ActRuDetail> findBySystemNameAndAssignee(String systemName, String assignee, int rows, int page, Sort sort);

    /**
     * 查找个人办结的件
     * 
     * @param systemName
     * @param assignee
     * @param rows
     * @param page
     * @param sort
     * @return
     */
    Page<ActRuDetail> findBySystemNameAndAssigneeAndEndedTrue(String systemName, String assignee, int rows, int page,
        Sort sort);

    /**
     * 查找你个人在办的件
     * 
     * @param systemName
     * @param assignee
     * @param status 0为待办，1位在办
     * @param rows
     * @param page
     * @param sort
     * @return
     */
    Page<ActRuDetail> findBySystemNameAndAssigneeAndStatus(String systemName, String assignee, int status, int rows,
        int page, Sort sort);

    /**
     * 标记流程为归档
     * 
     * @param processSerialNumber
     * @return
     */
    boolean placeOnFileByProcessSerialNumber(String processSerialNumber);

    /**
     * 恢复流程的办理详情
     * 
     * @param processSerialNumber
     * @return
     */
    boolean recoveryByProcessSerialNumber(String processSerialNumber);

    /**
     * Description: 恢复流程的办理详情
     * 
     * @param processSerialNumber
     * @param todoPersonId
     * @return
     */
    boolean recoveryTodoByProcessSerialNumber(String processSerialNumber, String todoPersonId);

    /**
     * 删除流程的办理详情
     * 
     * @param processInstanceId
     * @return
     */
    boolean removeByProcessInstanceId(String processInstanceId);

    /**
     * 删除流程的办理详情
     * 
     * @param processSerialNumber
     * @return
     */
    boolean removeByProcessSerialNumber(String processSerialNumber);

    /**
     * 
     * Description: 删除某个参与人的办理详情
     * 
     * @param processSerialNumber
     * @param assignee
     * @return
     */
    boolean removeByProcessSerialNumberAndAssignee(String processSerialNumber, String assignee);

    /**
     * 
     * Description: 标记流程为归档
     * 
     * @param processSerialNumber
     * @param todoPersonId
     * @return
     */
    boolean revokePlaceOnFileByProcessSerialNumber(String processSerialNumber, String todoPersonId);

    /**
     * 保存或者更新
     * 
     * @param actRuDetail
     * @return
     */
    boolean saveOrUpdate(ActRuDetail actRuDetail);

    /**
     * 恢复流程的办理详情
     * 
     * @param processInstanceId
     * @return
     */
    boolean syncByProcessInstanceId(String processInstanceId);
}
