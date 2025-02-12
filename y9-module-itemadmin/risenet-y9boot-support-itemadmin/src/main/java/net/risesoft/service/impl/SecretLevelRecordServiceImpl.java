package net.risesoft.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.SecretLevelRecord;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.repository.jpa.SecretLevelRecordRepository;
import net.risesoft.service.SecretLevelRecordService;
import net.risesoft.y9.Y9LoginUserHolder;

@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class SecretLevelRecordServiceImpl implements SecretLevelRecordService {

    private final SecretLevelRecordRepository secretLevelRecordRepository;

    @Override
    @Transactional
    public void save(String processSerialNumber, String secretLevel, String secretBasis, String secretItem,
        String description) {
        SecretLevelRecord secretLevelRecord = new SecretLevelRecord();
        secretLevelRecord.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        secretLevelRecord.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        secretLevelRecord.setProcessSerialNumber(processSerialNumber);
        secretLevelRecord.setSecretLevel(secretLevel);
        secretLevelRecord.setSecretBasis(secretBasis);
        secretLevelRecord.setSecretItem(secretItem);
        secretLevelRecord.setDescription(description);
        secretLevelRecord.setCreateUserId(Y9LoginUserHolder.getPersonId());
        secretLevelRecordRepository.save(secretLevelRecord);
    }
}
