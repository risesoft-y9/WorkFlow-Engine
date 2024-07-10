package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.RelatedProcess;

@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface RelatedProcessRepository
    extends JpaRepository<RelatedProcess, String>, JpaSpecificationExecutor<RelatedProcess> {

    Page<RelatedProcess> findByParentItemIdOrderByCreateDateAsc(String itemId, Pageable pageable);

    List<RelatedProcess> findByParentItemIdAndItemNameLike(String parentItemId, String itemName);

    List<RelatedProcess> findByParentItemId(String parentItemId);

    RelatedProcess findByParentItemIdAndItemId(String parentItemId, String itemId);
}
