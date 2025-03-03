package net.risesoft.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.SecretLevelRecord;
import net.risesoft.enums.DialectEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.repository.jpa.SecretLevelRecordRepository;
import net.risesoft.service.SecretLevelRecordService;
import net.risesoft.util.form.Y9FormDbMetaDataUtil;
import net.risesoft.y9.Y9LoginUserHolder;

@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class SecretLevelRecordServiceImpl implements SecretLevelRecordService {

    private final SecretLevelRecordRepository secretLevelRecordRepository;

    @Resource(name = "jdbcTemplate4Tenant")
    private JdbcTemplate jdbcTemplate4Tenant;

    @Override
    public List<SecretLevelRecord> getRecord(String processSerialNumber) {
        return secretLevelRecordRepository.findByProcessSerialNumberOrderByCreateTimeDesc(processSerialNumber);
    }

    @Override
    @Transactional
    public void save(String processSerialNumber, String secretLevel, String secretBasis, String secretItem,
        String description, String tableName, String fieldName) {
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

        String dialect = Y9FormDbMetaDataUtil.getDatabaseDialectName(jdbcTemplate4Tenant.getDataSource());
        String dataSql = "";
        if (DialectEnum.ORACLE.getValue().equals(dialect) || DialectEnum.DM.getValue().equals(dialect)
            || DialectEnum.KINGBASE.getValue().equals(dialect)) {
            dataSql = "select * from \"" + tableName + "\" t where t.guid=?";
        } else if (DialectEnum.MYSQL.getValue().equals(dialect)) {
            dataSql = "select * from " + tableName + " t where t.guid=?";
        }
        List<Map<String, Object>> datamap = jdbcTemplate4Tenant.queryForList(dataSql, processSerialNumber);
        if (datamap.isEmpty()) {
            String sql = "insert into " + tableName + "(" + fieldName + ",guid) values('" + secretLevel + "','"
                + processSerialNumber + "')";
            jdbcTemplate4Tenant.execute(sql);
        } else {
            String sql = "update " + tableName + " set " + fieldName + "='" + secretLevel + "' where guid='"
                + processSerialNumber + "'";
            jdbcTemplate4Tenant.execute(sql);
        }
    }
}
