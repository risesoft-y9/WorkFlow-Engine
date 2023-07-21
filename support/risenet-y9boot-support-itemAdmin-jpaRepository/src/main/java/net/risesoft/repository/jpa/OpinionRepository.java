package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.Opinion;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface OpinionRepository extends JpaRepository<Opinion, String>, JpaSpecificationExecutor<Opinion> {

    @Query("from Opinion t where t.processInstanceId=?1 order by t.createDate ASC")
    List<Opinion> findByProcessInstanceId(String processInstanceId);

    /**
     * 根据processSerialNumber查找所有意见
     *
     * @param processSerialNumber
     * @return
     */
    @Query("from Opinion t where t.processSerialNumber=?1 order by t.createDate ASC")
    List<Opinion> findByProcessSerialNumber(String processSerialNumber);

    /**
     * 获取当前用户填写的流程意见
     *
     * @param processSerialNumber
     * @param userId
     * @return
     */
    @Query("from Opinion t where t.processSerialNumber=?1 and t.userId=?2 and taskId=?3")
    List<Opinion> findByProcessSerialNumberAndUserId(String processSerialNumber, String userId, String taskId);

    List<Opinion> findByProcessSerialNumberOrderByCreateDateDesc(String processSerialNumber);

    /**
     * 根据processSerialNumber查找意见，用于未启动流程发送前的是否填写意见校验
     *
     * @param processSerialNumber
     * @return
     */
    @Query("select count(*) from Opinion t where t.processSerialNumber = ?1")
    int findByProcSerialNumber(String processSerialNumber);

    /**
     * 根据意见框id和流程唯一标示查找意见
     *
     * @param processSerialNumber
     * @param opinionFrameMark
     * @return
     */
    @Query("from Opinion t where t.processSerialNumber=?1 and t.opinionFrameMark=?2 order by t.createDate ASC")
    List<Opinion> findByProcSerialNumberAndOpinionFrameMark(String processSerialNumber, String opinionFrameMark);

    /**
     *
     * @param processSerialNumbers
     * @param opinionFrameMark
     * @return
     */
    @Query("from Opinion t where t.processSerialNumber in ?1 and t.opinionFrameMark=?2 order by t.createDate ASC")
    List<Opinion> findByPsnsAndOfid(List<String> processSerialNumbers, String opinionFrameMark);

    @Query("from Opinion t where t.processSerialNumber = ?1 and t.taskId=?2 and t.opinionFrameMark=?3 and t.userId=?4 order by t.createDate ASC")
    Opinion findByPsnsAndTaskIdAndOfidAndUserId(String processSerialNumber, String taskId, String opinionFrameMark, String userId);

    @Query("from Opinion t where t.taskId=?1 order by t.createDate desc")
    List<Opinion> findByTaskId(String taskId);

    List<Opinion> findByTaskIdAndPositionIdAndProcessTrackIdIsNull(String taskId, String positionId);

    List<Opinion> findByTaskIdAndProcessTrackIdOrderByCreateDateDesc(String taskId, String processTrackId);

    /**
     * 获取某个人在某个任务的意见数量
     *
     * @param taskId
     * @param userId
     * @return
     */
    List<Opinion> findByTaskIdAndUserIdAndProcessTrackIdIsNull(String taskId, String userId);

    /**
     * 流程未启动：为''或者null获取当前意见框的个人意见数量
     *
     * @param processSerialNumber
     * @param taskId
     * @param category
     * @param userId
     * @return
     */
    @Query("select count(*) from Opinion t where t.processSerialNumber = ?1 and t.opinionFrameMark=?2 and t.userId=?3  and (t.taskId is null or length(trim(t.taskId))=0) order by t.createDate ASC")
    Integer getCount4Personal(String processSerialNumber, String opinionFrameMark, String userId);

    /**
     * 流程已启动：taskId不为''或者null获取当前意见框的个人意见数量
     *
     * @param processSerialNumber
     * @param taskId
     * @param category
     * @param userId
     * @return
     */
    @Query("select count(id) from Opinion t where t.processSerialNumber = ?1 and t.taskId=?2 and t.opinionFrameMark=?3 and t.userId=?4 order by t.createDate ASC")
    Integer getCount4Personal(String processSerialNumber, String taskId, String opinionFrameMark, String userId);

    /**
     * 获取某个任务的意见数量
     *
     * @param taskId
     * @return
     */
    @Query("select count(id) from Opinion t where t.taskId=?1")
    int getCountByTaskId(String taskId);

    @Modifying
    @Transactional(readOnly = false)
    @Query("update Opinion t set t.processInstanceId=?1,t.taskId=?2 where t.processSerialNumber=?3")
    int update(String processInstanceId, String taskId, String processSerialNumber);

}
