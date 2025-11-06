package net.risesoft.repository.documentword;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.documentword.Y9WordHistory;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Repository
public interface Y9WordHistoryRepository
    extends JpaRepository<Y9WordHistory, String>, JpaSpecificationExecutor<Y9WordHistory> {

    /**
     * 根据processSerialNumber获取历史正文
     * 
     * @param processSerialNumber
     * @return
     */
    @Query("From Y9WordHistory d where d.processSerialNumber=?1 order by d.createTime desc")
    List<Y9WordHistory> findByProcessSerialNumber(String processSerialNumber);

    /**
     * 根据processSerialNumber获取历史正文
     * 
     * @param processSerialNumber
     * @return
     */
    @Query("From Y9WordHistory d where d.processSerialNumber=?1 and d.istaohong=?2 order by d.createTime desc")
    List<Y9WordHistory> findByProcessSerialNumberAndIsTaoHong(String processSerialNumber, String isTaoHong);

    @Query("From Y9WordHistory d where d.processSerialNumber in (?1)")
    List<Y9WordHistory> findByProcessSerialNumbers(List<String> processSerialNumbers);

    /**
     * 根据任务ID获取历史正文
     * 
     * @param taskId 任务ID
     * @return
     */
    @Query("From Y9WordHistory d where d.taskId=?1 order by d.createTime desc")
    List<Y9WordHistory> findListByTaskId(String taskId);

    /**
     * 根据processSerialNumber和任务ID获取历史正文
     * 
     * @param processSerialNumber
     * @return
     */
    @Query("From Y9WordHistory d where d.processSerialNumber=?1 and d.istaohong=?2 and d.taskId=?3 order by d.createTime desc")
    List<Y9WordHistory> getByProcessSerialNumberAndIsTaoHongAndTaskId(String processSerialNumber, String istaohong,
        String taskId);

    /**
     * 根据processSerialNumber和任务ID获取历史正文
     * 
     * @param processSerialNumber
     * @return
     */
    @Query("From Y9WordHistory d where d.processSerialNumber=?1 and d.taskId=?2 order by d.createTime desc")
    List<Y9WordHistory> getByProcessSerialNumberAndTaskId(String processSerialNumber, String taskId);

    /**
     * 根据processSerialNumber和任务ID获取历史正文
     * 
     * @param processSerialNumber
     * @return
     */
    @Query("From Y9WordHistory d where d.processSerialNumber=?1 and d.istaohong=?2 and d.taskId=?3 order by d.createTime desc")
    List<Y9WordHistory> getByProcessSerialNumberAndTaskIdAndIsTaoHong(String processSerialNumber, String istaohong,
        String taskId);

    @Query("select max(h.version) from Y9WordHistory h where h.processSerialNumber=?1")
    Integer getMaxHistoryVersion(String processSerialNumber);

    List<Y9WordHistory> findByTaskId(String taskId);

    @Transactional(readOnly = false)
    @Modifying
    @Query("update Y9WordHistory t set t.taskId=?1 where t.processSerialNumber=?2")
    int update(String taskId, String processSerialNumber);

    /**
     * 更新word
     *
     * @param fileStoreId
     * @param fileSize
     * @param isTaoHong
     * @param docCategory
     * @param userId
     * @param id
     */
    @Transactional()
    @Modifying
    @Query("update Y9WordHistory t set t.fileStoreId=?1,t.fileSize=?2,t.istaohong=?3,t.docCategory=?4,t.userId=?5 where t.id=?6")
    void updateById(String fileStoreId, String fileSize, String isTaoHong, String docCategory, String userId,
        String id);
}
