package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.documentword.TransactionWord;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Repository
public interface TransactionWordRepository
    extends JpaRepository<TransactionWord, String>, JpaSpecificationExecutor<TransactionWord> {

    /**
     * 根据processSerialNumber获取正文
     * 
     * @param processSerialNumber
     * @return
     */
    @Query("From TransactionWord d where d.processSerialNumber=?1 order by d.saveDate desc")
    List<TransactionWord> findByProcessSerialNumber(String processSerialNumber);

    List<TransactionWord> findByProcessSerialNumberAndDocCategory(String processSerialNumber, String docCategory);

    /**
     * 根据processSerialNumber和taohong获取正文
     * 
     * @param processSerialNumber
     * @param taohong
     * @return
     */
    @Query("From TransactionWord d where d.processSerialNumber=?1 and d.istaohong=?2 order by d.saveDate desc")
    List<TransactionWord> findByProcessSerialNumberAndIstaohong(String processSerialNumber, String taohong);

    @Query("From TransactionWord d where d.processSerialNumber in (?1)")
    List<TransactionWord> findByProcessSerialNumbers(List<String> processSerialNumbers);

    /**
     * 更新word
     * 
     * @param fileStoreId
     * @param fileType
     * @param fileName
     * @param fileSize
     * @param saveDate
     * @param isTaoHong
     * @param userId
     * @param id
     */
    @Transactional(readOnly = false)
    @Modifying
    @Query("update TransactionWord t set t.fileStoreId=?1,t.fileType=?2,t.fileName=?3,t.fileSize=?4,t.saveDate=?5,t.istaohong=?6,t.userId=?7 where t.id=?8")
    void updateTransactionWordById(String fileStoreId, String fileType, String fileName, String fileSize,
        String saveDate, String isTaoHong, String userId, String id);

}
