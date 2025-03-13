package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.LeaderOpinion;

/**
 * @author : qinman
 * @date : 2025-03-12
 * @since 9.6.8
 **/
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface LeaderOpinionRepository
    extends JpaRepository<LeaderOpinion, String>, JpaSpecificationExecutor<LeaderOpinion> {

    List<LeaderOpinion> findByProcessSerialNumberOrderByCreateDateAsc(String processSerialNumber);
}
