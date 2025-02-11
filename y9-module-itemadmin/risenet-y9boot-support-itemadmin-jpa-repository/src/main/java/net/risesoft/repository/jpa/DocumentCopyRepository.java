package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.DocumentCopy;

/**
 * @author : qinman
 * @date : 2025-02-10
 * @since 9.6.8
 **/
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface DocumentCopyRepository
    extends JpaRepository<DocumentCopy, String>, JpaSpecificationExecutor<DocumentCopy> {

    Page<DocumentCopy> findByUserIdAndStatusLessThan(String userId, Integer status, Pageable pageable);

    List<DocumentCopy> findByProcessSerialNumberAndSenderIdOrderByCreateTimeAsc(String processSerialNumber,
        String senderId);
}
