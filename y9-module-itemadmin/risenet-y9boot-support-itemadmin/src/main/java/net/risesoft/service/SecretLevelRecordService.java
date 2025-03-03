package net.risesoft.service;

import java.util.List;

import net.risesoft.entity.SecretLevelRecord;

public interface SecretLevelRecordService {

    /**
     * 根据流程实例号获取定密修改记录
     *
     * @param processSerialNumber 流程实例号
     * @return {@link List}<{@link SecretLevelRecord}>
     */
    List<SecretLevelRecord> getRecord(String processSerialNumber);

    /**
     * 保存定密修改记录
     *
     * @param processSerialNumber 流程实例号
     * @param secretLevel 密级
     * @param secretBasis 密级依据
     * @param secretItem 密级项目
     * @param description 说明
     * @param tableName 表名
     * @param fieldName 字段名
     */
    void save(String processSerialNumber, String secretLevel, String secretBasis, String secretItem, String description,
        String tableName, String fieldName);
}
