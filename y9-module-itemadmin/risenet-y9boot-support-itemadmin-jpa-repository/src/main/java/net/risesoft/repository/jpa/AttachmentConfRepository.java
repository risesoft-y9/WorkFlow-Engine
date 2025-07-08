package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.attachment.AttachmentConf;

@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface AttachmentConfRepository
    extends JpaRepository<AttachmentConf, String>, JpaSpecificationExecutor<AttachmentConf> {

    List<AttachmentConf> findByAttachmentTypeOrderByTabIndexAsc(String attachmentType);

    List<AttachmentConf> findByAttachmentTypeAndConfigTypeOrderByTabIndexAsc(String attachmentType, Integer configType);

    @Query("select max(t.tabIndex) from AttachmentConf t where t.attachmentType=?1 and t.configType=?2")
    Integer getMaxTabIndex(String attachmentType, Integer configType);

    /**
     * 根据id修改tabIndex
     * 
     * @param tabIndex
     * @param id
     */
    @Modifying
    @Transactional(readOnly = false)
    @Query("update AttachmentConf t set t.tabIndex=?1 where t.id=?2")
    void updateOrder(Integer tabIndex, String id);
}