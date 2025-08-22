package net.risesoft.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.SmsDetail;

@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface SmsDetailRepository extends JpaRepository<SmsDetail, String>, JpaSpecificationExecutor<SmsDetail> {

    SmsDetail findByProcessSerialNumberAndPositionId(String processSerialNumber, String positionId);
}
