package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.TransactionHistoryWord;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Repository
public interface TransactionHistoryWordRepository
    extends JpaRepository<TransactionHistoryWord, String>, JpaSpecificationExecutor<TransactionHistoryWord> {

    /**
     * 根据processSerialNumber获取历史正文
     * 
     * @param processSerialNumber
     * @return
     */
    @Query("From TransactionHistoryWord d where d.processSerialNumber=?1 order by d.saveDate desc")
    List<TransactionHistoryWord> findByProcessSerialNumber(String processSerialNumber);

    /**
     * 根据processSerialNumber获取历史正文
     * 
     * @param processSerialNumber
     * @return
     */
    @Query("From TransactionHistoryWord d where d.processSerialNumber=?1 and d.istaohong=?2 order by d.saveDate desc")
    List<TransactionHistoryWord> findByProcessSerialNumberAndIsTaoHong(String processSerialNumber, String isTaoHong);

    @Query("From TransactionHistoryWord d where d.processSerialNumber in (?1)")
    public List<TransactionHistoryWord> findByProcessSerialNumbers(List<String> processSerialNumbers);

    /**
     * 根据任务ID获取历史正文
     * 
     * @param processSerialNumber
     * @return
     */
    @Query("From TransactionHistoryWord d where d.taskId=?1 order by d.saveDate desc")
    List<TransactionHistoryWord> findListByTaskId(String taskId);

    /**
     * 根据processSerialNumber和任务ID获取历史正文
     * 
     * @param processSerialNumber
     * @return
     */
    @Query("From TransactionHistoryWord d where d.processSerialNumber=?1 and d.istaohong=?2 and d.taskId=?3 order by d.saveDate desc")
    List<TransactionHistoryWord> getByProcessSerialNumberAndIsTaoHongAndTaskId(String processSerialNumber,
        String istaohong, String taskId);

    /**
     * 根据processSerialNumber和任务ID获取历史正文
     * 
     * @param processSerialNumber
     * @return
     */
    @Query("From TransactionHistoryWord d where d.processSerialNumber=?1 and d.taskId=?2 order by d.saveDate desc")
    List<TransactionHistoryWord> getByProcessSerialNumberAndTaskId(String processSerialNumber, String taskId);

    /**
     * 根据processSerialNumber和任务ID获取历史正文
     * 
     * @param processSerialNumber
     * @return
     */
    @Query("From TransactionHistoryWord d where d.processSerialNumber=?1 and d.istaohong=?2 and d.taskId=?3 order by d.saveDate desc")
    List<TransactionHistoryWord> getByProcessSerialNumberAndTaskIdAndIsTaoHong(String processSerialNumber,
        String istaohong, String taskId);

    @Query("select max(h.version) from TransactionHistoryWord h where h.processSerialNumber=?1")
    Integer getMaxHistoryVersion(String processSerialNumber);

    @Query("From TransactionHistoryWord h where h.taskId=?1")
    List<TransactionHistoryWord> getTransactionHistoryWordByTaskId(String taskId);

    @Transactional(readOnly = false)
    @Modifying
    @Query("update TransactionHistoryWord t set t.taskId=?1 where t.processSerialNumber=?2")
    int update(String taskId, String processSerialNumber);

    /**
     * 更新word
     * 
     * @param fileStoreId
     * @param fileSize
     * @param saveDate
     * @param userId
     * @param id
     */
    @Transactional(readOnly = false)
    @Modifying
    @Query("update TransactionHistoryWord t set t.fileStoreId=?1,t.fileSize=?2,t.istaohong=?3,t.saveDate=?4,t.userId=?5 where t.id=?6")
    public void updateTransactionHistoryWordById(String fileStoreId, String fileSize, String isTaoHong, String saveDate,
        String userId, String id);
}
