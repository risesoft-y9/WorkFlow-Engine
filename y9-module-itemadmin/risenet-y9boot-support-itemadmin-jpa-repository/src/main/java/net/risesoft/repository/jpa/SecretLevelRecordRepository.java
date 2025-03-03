package net.risesoft.repository.jpa;

import java.util.List;


import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import net.risesoft.entity.SecretLevelRecord;

@JaversSpringDataAuditable
public interface SecretLevelRecordRepository
        extends JpaRepository<SecretLevelRecord, String>, JpaSpecificationExecutor<SecretLevelRecord> {

    List<SecretLevelRecord> findByProcessSerialNumberOrderByCreateTimeDesc(String processSerialNumber);
}
