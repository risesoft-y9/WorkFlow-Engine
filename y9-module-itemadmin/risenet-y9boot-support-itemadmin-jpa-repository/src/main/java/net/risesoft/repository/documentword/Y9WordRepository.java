package net.risesoft.repository.documentword;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.documentword.Y9Word;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Repository
public interface Y9WordRepository extends JpaRepository<Y9Word, String>, JpaSpecificationExecutor<Y9Word> {

    /**
     * 根据processSerialNumber获取正文
     * 
     * @param processSerialNumber
     * @return
     */
    @Query("From Y9Word d where d.processSerialNumber=?1 order by d.createTime desc")
    List<Y9Word> findByProcessSerialNumber(String processSerialNumber);

    List<Y9Word> findByProcessSerialNumberAndDocCategory(String processSerialNumber, String docCategory);

    /**
     * 根据processSerialNumber和taohong获取正文
     * 
     * @param processSerialNumber
     * @param taohong
     * @return
     */
    @Query("From Y9Word d where d.processSerialNumber=?1 and d.istaohong=?2 order by d.createTime desc")
    List<Y9Word> findByProcessSerialNumberAndIstaohong(String processSerialNumber, String taohong);

    @Query("From Y9Word d where d.processSerialNumber in (?1)")
    List<Y9Word> findByProcessSerialNumbers(List<String> processSerialNumbers);

    /**
     * 更新word
     * 
     * @param fileStoreId
     * @param fileType
     * @param fileName
     * @param fileSize
     * @param isTaoHong
     * @param userId
     * @param id
     */
    @Transactional()
    @Modifying
    @Query("update Y9Word t set t.fileStoreId=?1,t.fileType=?2,t.fileName=?3,t.fileSize=?4,t.istaohong=?5,t.userId=?6 where t.id=?7")
    void updateById(String fileStoreId, String fileType, String fileName, String fileSize, String isTaoHong,
        String userId, String id);

}
