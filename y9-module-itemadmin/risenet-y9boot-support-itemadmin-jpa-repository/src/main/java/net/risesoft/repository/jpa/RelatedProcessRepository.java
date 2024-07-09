package net.risesoft.repository.jpa;

import net.risesoft.entity.RelatedProcess;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface RelatedProcessRepository extends JpaRepository<RelatedProcess, String>, JpaSpecificationExecutor<RelatedProcess> {
	
	Page<RelatedProcess> findByParentItemId(String itemId, Pageable pageable);

	List<RelatedProcess> findByParentItemIdAndItemNameLike(String parentItemId,String itemName);

	List<RelatedProcess> findByParentItemId(String parentItemId);

	RelatedProcess findByParentItemIdAndItemId(String parentItemId,String itemId);
}
