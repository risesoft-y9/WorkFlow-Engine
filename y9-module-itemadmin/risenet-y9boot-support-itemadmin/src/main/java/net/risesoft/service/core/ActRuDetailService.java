package net.risesoft.service.core;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import net.risesoft.entity.ActRuDetail;
import net.risesoft.enums.ActRuDetailStatusEnum;
import net.risesoft.pojo.Y9Result;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
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
     * 查找个人办结的件的数量
     *
     * @param systemName
     * @param assignee
     * @return
     */
    int countBySystemNameAndAssignee(String systemName, String assignee);

    /**
     * 查找个人待办，在办件数量
     *
     * @param assignee
     * @param status 0为待办，1位在办
     * @return
     */
    int countByAssigneeAndStatus(String assignee, ActRuDetailStatusEnum status);

    /**
     * 查找个人待办，在办件数量
     *
     * @param systemName
     * @param assignee
     * @param status 0为待办，1位在办
     * @return
     */
    int countBySystemNameAndAssigneeAndStatus(String systemName, String assignee, ActRuDetailStatusEnum status);

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
     * @param executionId
     * @return
     */
    boolean deleteByExecutionId(String executionId);

    /**
     * 根据流程实例Id和办理人查找(一个件一个人只会有一个在办信息)
     *
     * @param processInstanceId
     * @param assignee
     * @return
     */
    ActRuDetail findByProcessInstanceIdAndAssigneeAndStatusEquals1(String processInstanceId, String assignee);

    /**
     * 根据流程序列号和办理人查找在办信息(一个件一个人只会有一个在办信息)
     *
     * @param processSerialNumber
     * @param assignee
     * @return
     */
    ActRuDetail findByProcessSerialNumberAndAssigneeAndStatusEquals1(String processSerialNumber, String assignee);

    /**
     * 根据任务Id和办理人查找
     *
     * @param taskId
     * @param assignee
     * @return
     */
    ActRuDetail findByTaskIdAndAssignee(String taskId, String assignee);

    /**
     * 根据流程实例id获取列表
     *
     * @param processInstanceId
     * @return
     */
    List<ActRuDetail> listByProcessInstanceId(String processInstanceId);

    /**
     * 根据流程实例Id和状态查找
     *
     * @param processInstanceId
     * @param status 0位待办，1位在办
     * @return
     */
    List<ActRuDetail> listByProcessInstanceIdAndStatus(String processInstanceId, ActRuDetailStatusEnum status);

    /**
     * 根据流程实例Id和办理人查找
     *
     * @param processSerialNumber
     * @return
     */
    List<ActRuDetail> listByProcessSerialNumber(String processSerialNumber);

    /**
     * Description: 根据流程编号和是否结束查找
     *
     * @param processSerialNumber
     * @param ended
     * @return
     */
    List<ActRuDetail> listByProcessSerialNumberAndEnded(String processSerialNumber, boolean ended);

    /**
     * 根据流程实例Id和状态查找
     *
     * @param processSerialNumber
     * @param status 0位待办，1位在办
     * @return
     */
    List<ActRuDetail> listByProcessSerialNumberAndStatus(String processSerialNumber, ActRuDetailStatusEnum status);

    /**
     * 查找个人办结列表
     *
     * @param systemName
     * @param assignee
     * @param rows
     * @param page
     * @param sort
     * @return
     */
    Page<ActRuDetail> pageBySystemNameAndAssigneeAndEnded(String systemName, String assignee, boolean ended, int rows,
        int page, Sort sort);

    /**
     * 查找系统在办、办结列表
     *
     * @param systemName
     * @param rows
     * @param page
     * @param sort
     * @return
     */
    Page<ActRuDetail> pageBySystemNameAndEnded(String systemName, boolean ended, int page, int rows, Sort sort);

    /**
     * 查找个人删除列表
     *
     * @param systemName
     * @param assignee
     * @param rows
     * @param page
     * @param sort
     * @return
     */
    Page<ActRuDetail> pageBySystemNameAndAssigneeAndDeletedTrue(String systemName, String assignee, int rows, int page,
        Sort sort);

    /**
     * 查找系统删除列表
     *
     * @param systemName
     * @param rows
     * @param page
     * @param sort
     * @return
     */
    Page<ActRuDetail> pageBySystemNameAndDeletedTrue(String systemName, int page, int rows, Sort sort);

    /**
     * 查找个人删除列表
     *
     * @param systemName
     * @param deptId
     * @param isBureau
     * @param rows
     * @param page
     * @param sort
     * @return
     */
    Page<ActRuDetail> pageBySystemNameAndDeptIdAndDeletedTrue(String systemName, String deptId, boolean isBureau,
        int rows, int page, Sort sort);

    /**
     * 查找个人待办，在办列表
     *
     * @param assignee
     * @param status 0为待办，1位在办
     * @param rows
     * @param page
     * @param sort
     * @return
     */
    Page<ActRuDetail> pageByAssigneeAndStatus(String assignee, ActRuDetailStatusEnum status, int rows, int page,
        Sort sort);

    /**
     * 查找个人待办，在办列表
     *
     * @param systemName
     * @param assignee
     * @param status 0为待办，1位在办
     * @param rows
     * @param page
     * @param sort
     * @return
     */
    Page<ActRuDetail> pageBySystemNameAndAssigneeAndStatus(String systemName, String assignee,
        ActRuDetailStatusEnum status, int rows, int page, Sort sort);

    /**
     * 查找个人待办，在办列表
     *
     * @param systemName
     * @param assignee
     * @param status 0为待办，1位在办
     * @param taskDefKey 任务key
     * @param rows
     * @param page
     * @param sort
     * @return
     */
    Page<ActRuDetail> pageBySystemNameAndAssigneeAndStatusAndTaskDefKey(String systemName, String assignee,
        ActRuDetailStatusEnum status, String taskDefKey, int rows, int page, Sort sort);

    /**
     * 查找科室在办，办结列表
     *
     * @param systemName
     * @param deptId
     * @param ended 是否办结
     * @param rows
     * @param page
     * @param sort
     * @return
     */
    Page<ActRuDetail> pageBySystemNameAndDeptIdAndEnded(String systemName, String deptId, boolean isBureau,
        boolean ended, int rows, int page, Sort sort);

    /**
     * 查找个人所有件列表（不包含回收站）
     *
     * @param systemName
     * @param assignee
     * @param rows
     * @param page
     * @param sort
     * @return
     */
    Page<ActRuDetail> pageBySystemNameAndAssignee(String systemName, String assignee, int rows, int page, Sort sort);

    /**
     * 查找所有件列表（不包含回收站）
     *
     * @param systemName
     * @param rows
     * @param page
     * @param sort
     * @return
     */
    Page<ActRuDetail> pageBySystemName(String systemName, int rows, int page, Sort sort);

    /**
     * 查找个人已办（在办、办结）
     *
     * @param systemName
     * @param assignee
     * @param rows
     * @param page
     * @param sort
     * @return
     */
    Page<ActRuDetail> pageBySystemNameAndAssigneeAndStatusEquals1(String systemName, String assignee, int rows,
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
    boolean recoveryByProcessInstanceId(String processSerialNumber);

    /**
     * 恢复流程的办理详情
     *
     * @param processSerialNumber
     * @return
     */
    boolean recoveryByProcessSerialNumber(String processSerialNumber);

    /**
     * 恢复流程的办理详情
     *
     * @param executionId
     * @return
     */
    void recoveryByExecutionId(String executionId);

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
     * 签收
     *
     * @param taskId 任务id
     * @param assignee 办理人id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.8
     */
    Y9Result<Object> claim(String taskId, String assignee);

    /**
     * 签收
     *
     * @param taskId 任务id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.8
     */
    Y9Result<Object> unClaim(String taskId);

    /**
     * 签收
     *
     * @param taskId 任务id
     * @param assignee 办理人id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.8
     */
    Y9Result<Object> refuseClaim(String taskId, String assignee);

    Y9Result<Object> todo2doing(String taskId, String assignee);

    void setRead(String id);

    /**
     * 恢复流程的办理详情
     *
     * @param processInstanceId
     * @return
     */
    boolean syncByProcessInstanceId(String processInstanceId);
}
