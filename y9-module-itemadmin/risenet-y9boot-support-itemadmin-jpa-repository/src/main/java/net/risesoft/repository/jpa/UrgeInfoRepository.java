package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.UrgeInfo;

/**
 * @author qinman
 */
@Repository
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface UrgeInfoRepository extends JpaRepository<UrgeInfo, String>, JpaSpecificationExecutor<UrgeInfo> {
    List<UrgeInfo> findByProcessSerialNumberOrderByCreateTimeDesc(String processSerialNumber);
}
