package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.attachment.AttachmentType;

@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface AttachmentTypeRepository
    extends JpaRepository<AttachmentType, String>, JpaSpecificationExecutor<AttachmentType> {

    AttachmentType findByMark(String mark);

    @Query("from AttachmentType t where t.mark not in ?1")
    Page<AttachmentType> findByMarkNotIn(List<String> markList, Pageable pageable);

    @Query("from AttachmentType t where t.mark not in ?1 and t.name like ?2")
    Page<AttachmentType> findByMarkNotInAndNameLike(List<String> markList, String name, Pageable pageable);

    Page<AttachmentType> findByNameContaining(String name, Pageable pageable);
}
