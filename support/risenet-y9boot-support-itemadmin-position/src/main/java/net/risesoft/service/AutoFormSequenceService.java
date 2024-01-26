package net.risesoft.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.AutoFormSequence;
import net.risesoft.entity.DocumentNumberDetail;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.repository.jpa.AutoFormSequenceRepository;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class AutoFormSequenceService {

    @Autowired
    private AutoFormSequenceRepository autoFormSequenceRepository;

    @Autowired
    private DocumentNumberDetailService documentNumberDetailService;

    /**
     *
     * @param patternLength 要生成的总长度
     * @param sequence 序列号
     * @return
     */
    public String calculateSequence(int patternLength, int sequence) {
        StringBuilder stringBuilder = new StringBuilder();
        int numberLength = Integer.toString(sequence).length();
        if (patternLength > 0) {
            stringBuilder.append(Integer.toString(sequence));
            for (int i = 1; i <= (patternLength - numberLength); i++) {
                stringBuilder.insert(0, "0");
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 生成带有格式的序列号
     *
     * @param tenantId
     * @param labelName
     * @return
     */
    public String genSequence(String tenantId, String labelName, String character) {
        if (character == null) {
            character = "";
        }
        Integer sequence = getSequence(tenantId, labelName, character);
        String result = "";
        if (sequence > 0) {
            DocumentNumberDetail yanl = documentNumberDetailService.findAll().get(0);
            result = calculateSequence(yanl.getNumLength(), sequence);
            if (StringUtils.isNotBlank(result)) {
                Integer calendarYear = yanl.getCalendarYear();
                result = character + "﹝" + calendarYear + "﹞" + result + "号";
            }
        }
        return result;
    }

    /**
     * 获取序列号，并更新数据库表中的值，这里暂时没有考虑并发的情况，先实现了功能 对并发的处理有如下几种考虑： 1、使用jdbctemplate，在sql语句中设置锁的级别是Serializable
     * 2、对每一个标签生成一个表，在程序中生成一个32位码并将其插入到数据表中，从数据表中查找该32位码对应的自增字段， 优点是记录当前的序列号是否被使用过，缺点是每个序列号都要一个数据表与之对应 3、使用mongdb的原子性操作
     *
     * @param tenantId
     * @param labelName
     * @return
     */
    @Transactional(readOnly = false)
    public Integer getSequence(String tenantId, String labelName, String character) {
        Integer sequence = 1;
        if (StringUtils.isNotBlank(tenantId) && StringUtils.isNotBlank(labelName)
            && StringUtils.isNotBlank(character)) {
            DocumentNumberDetail yanl = documentNumberDetailService.findAll().get(0);
            AutoFormSequence autoFormSequence =
                autoFormSequenceRepository.findOne(tenantId, labelName, character, yanl.getCalendarYear());
            if (autoFormSequence != null && autoFormSequence.getSequenceValue() != null) {
                sequence = autoFormSequence.getSequenceValue();
            } else {
                sequence = documentNumberDetailService.findAll().get(0).getSequenceInitValue();
                AutoFormSequence afs = new AutoFormSequence();
                afs.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                afs.setCharacterValue(character);
                afs.setLabelName(labelName);
                afs.setTenantId(tenantId);
                afs.setSequenceValue(sequence);
                afs.setCalendarYear(yanl.getCalendarYear());
                autoFormSequenceRepository.save(afs);
            }
            autoFormSequenceRepository.updateSequence(tenantId, labelName, character, yanl.getCalendarYear());
        }
        return sequence;
    }

    /**
     * 更新指定的序列号，执行加一操作
     *
     * @param tenantId
     * @param labelName
     */
    @Transactional(readOnly = false)
    public void updateSequence(String tenantId, String labelName, String character) {
        if (StringUtils.isNotBlank(tenantId) && StringUtils.isNotBlank(labelName)
            && StringUtils.isNotBlank(character)) {
            DocumentNumberDetail yanl = documentNumberDetailService.findAll().get(0);
            autoFormSequenceRepository.updateSequence(tenantId, labelName, character, yanl.getCalendarYear());
        }
    }
}
