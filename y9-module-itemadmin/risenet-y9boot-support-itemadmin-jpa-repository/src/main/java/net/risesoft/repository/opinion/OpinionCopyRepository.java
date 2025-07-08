package net.risesoft.repository.opinion;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.opinion.OpinionCopy;

/**
 * @author : qinman
 * @date : 2025-02-11
 * @since 9.6.8
 **/
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface OpinionCopyRepository
    extends JpaRepository<OpinionCopy, String>, JpaSpecificationExecutor<OpinionCopy> {

    List<OpinionCopy> findByProcessSerialNumberOrderByCreateTimeAsc(String processSerialNumber);
}
