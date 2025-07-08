package net.risesoft.repository.attachment;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.attachment.Attachment;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Repository
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface AttachmentRepository extends JpaRepository<Attachment, String>, JpaSpecificationExecutor<Attachment> {

    @Modifying
    @Transactional(readOnly = false)
    @Query("delete from Attachment where processSerialNumber in (?1)")
    void delBatchByProcessSerialNumbers(List<String> processSerialNumbers);

    @Modifying
    @Transactional(readOnly = false)
    @Query("delete from Attachment t where t.processSerialNumber=?1 and t.name=?2")
    int delFileByName(String processSerialNumber, String name);

    @Query("select count(*) from Attachment t where t.processSerialNumber=?1")
    Integer fileCounts(String processSerialNumber);

    @Query("from Attachment t where t.processSerialNumber=?1 order by t.uploadTime desc")
    List<Attachment> findByProcessSerialNumber(String processSerialNumber);

    @Query("from Attachment t where t.processSerialNumber=?1 and t.fileSource=?2 order by t.uploadTime desc")
    List<Attachment> findByProcessSerialNumberAndFileSource(String processSerialNumber, String fileSource);

    @Query("from Attachment t where t.processSerialNumber in (?1)")
    List<Attachment> findByProcessSerialNumbers(List<String> processSerialNumbers);

    @Query("from Attachment t where t.processSerialNumber=?1 order by t.tabIndex ASC")
    List<Attachment> findTransactionFileByProcessSerialNumber(String processSerialNumber);

    @Query("from Attachment t where t.processSerialNumber=?1")
    List<Attachment> getAttachmentList(String processSerialNumber);

    @Query("from Attachment t where t.processSerialNumber=?1")
    Page<Attachment> getAttachmentList(String processSerialNumber, Pageable pageable);

    @Query("from Attachment t where t.processSerialNumber=?1 and t.fileSource=?2")
    List<Attachment> getAttachmentList(String processSerialNumber, String fileSource);

    @Query("from Attachment t where t.processSerialNumber=?1 and t.fileSource=?2")
    Page<Attachment> getAttachmentList(String processSerialNumber, String fileSource, Pageable pageable);

    @Query("from Attachment t where t.name=?1 and t.processSerialNumber=?2 order by t.uploadTime desc")
    Attachment getFileInfoByFileName(String name, String processSerialNumber);

    @Query("select count(*) from Attachment t where t.processSerialNumber=?1 and t.fileSource = ?2")
    Integer getTransactionFileCount(String processSerialNumber, String fileSource);

    @Query("select count(*) from Attachment t where t.processSerialNumber=?1 and t.fileSource = ?2 and t.fileType = ?3")
    Integer getTransactionFileCountByFileType(String processSerialNumber, String fileSource, String fileType);

    @Query("from Attachment t where t.tabIndex=?1 and t.processSerialNumber=?2 order by t.tabIndex asc")
    Attachment getUpFileInfoByTabIndexOrProcessSerialNumber(Integer tabIndex, String processSerialNumber);

    @Modifying
    @Transactional(readOnly = false)
    @Query("update Attachment t set t.processInstanceId=?1,t.taskId=?2 where t.processSerialNumber=?3")
    int update(String processInstanceId, String taskId, String processSerialNumber);
}
