package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.TransactionFile;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Repository
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface TransactionFileRepository
    extends JpaRepository<TransactionFile, String>, JpaSpecificationExecutor<TransactionFile> {

    @Modifying
    @Transactional(readOnly = false)
    @Query("delete from TransactionFile where processSerialNumber in (?1)")
    void delBatchByProcessSerialNumbers(List<String> processSerialNumbers);

    @Modifying
    @Transactional(readOnly = false)
    @Query("delete from TransactionFile t where t.processSerialNumber=?1 and t.name=?2")
    int delFileByName(String processSerialNumber, String name);

    @Query("select count(*) from TransactionFile t where t.processSerialNumber=?1")
    Integer fileCounts(String processSerialNumber);

    @Query("from TransactionFile t where t.processSerialNumber=?1 order by t.uploadTime desc")
    List<TransactionFile> findByProcessSerialNumber(String processSerialNumber);

    @Query("from TransactionFile t where t.processSerialNumber=?1 and t.fileSource=?2 order by t.uploadTime desc")
    List<TransactionFile> findByProcessSerialNumberAndFileSource(String processSerialNumber, String fileSource);

    @Query("from TransactionFile t where t.processSerialNumber in (?1)")
    List<TransactionFile> findByProcessSerialNumbers(List<String> processSerialNumbers);

    @Query("from TransactionFile t where t.processSerialNumber=?1 order by t.tabIndex ASC")
    List<TransactionFile> findTransactionFileByProcessSerialNumber(String processSerialNumber);

    @Query("from TransactionFile t where t.processSerialNumber=?1")
    List<TransactionFile> getAttachmentList(String processSerialNumber);

    @Query("from TransactionFile t where t.processSerialNumber=?1")
    Page<TransactionFile> getAttachmentList(String processSerialNumber, Pageable pageable);

    @Query("from TransactionFile t where t.processSerialNumber=?1 and t.fileSource=?2")
    List<TransactionFile> getAttachmentList(String processSerialNumber, String fileSource);

    @Query("from TransactionFile t where t.processSerialNumber=?1 and t.fileSource=?2")
    Page<TransactionFile> getAttachmentList(String processSerialNumber, String fileSource, Pageable pageable);

    @Query("from TransactionFile t where t.name=?1 and t.processSerialNumber=?2 order by t.uploadTime desc")
    TransactionFile getFileInfoByFileName(String name, String processSerialNumber);

    @Query("select count(*) from TransactionFile t where t.processSerialNumber=?1 and t.fileSource = ?2")
    Integer getTransactionFileCount(String processSerialNumber, String fileSource);

    @Query("select count(*) from TransactionFile t where t.processSerialNumber=?1 and t.fileSource = ?2 and t.fileType = ?3")
    Integer getTransactionFileCountByFileType(String processSerialNumber, String fileSource, String fileType);

    @Query("from TransactionFile t where t.tabIndex=?1 and t.processSerialNumber=?2 order by t.tabIndex asc")
    TransactionFile getUpFileInfoByTabIndexOrProcessSerialNumber(Integer tabIndex, String processSerialNumber);

    @Modifying
    @Transactional(readOnly = false)
    @Query("update TransactionFile t set t.processInstanceId=?1,t.taskId=?2 where t.processSerialNumber=?3")
    int update(String processInstanceId, String taskId, String processSerialNumber);
}
