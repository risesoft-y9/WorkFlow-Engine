package net.risesoft.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.AutoFormSequence;
import net.risesoft.entity.DocumentNumberDetail;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.repository.jpa.AutoFormSequenceRepository;
import net.risesoft.service.AutoFormSequenceService;
import net.risesoft.service.DocumentNumberDetailService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class AutoFormSequenceServiceImpl implements AutoFormSequenceService {

    private final AutoFormSequenceRepository autoFormSequenceRepository;

    private final DocumentNumberDetailService documentNumberDetailService;

    @Override
    public String calculateSequence(int patternLength, int sequence) {
        StringBuilder stringBuilder = new StringBuilder();
        int numberLength = Integer.toString(sequence).length();
        if (patternLength > 0) {
            stringBuilder.append(sequence);
            for (int i = 1; i <= (patternLength - numberLength); i++) {
                stringBuilder.insert(0, "0");
            }
        }
        return stringBuilder.toString();
    }

    @Override
    public String genSequence(String tenantId, String labelName, String character) {
        if (character == null) {
            character = "";
        }
        Integer sequence = getSequence(tenantId, labelName, character);
        String result = "";
        if (sequence > 0) {
            DocumentNumberDetail detail = documentNumberDetailService.listAll().get(0);
            result = calculateSequence(detail.getNumLength(), sequence);
            if (StringUtils.isNotBlank(result)) {
                Integer calendarYear = detail.getCalendarYear();
                result = character + "﹝" + calendarYear + "﹞" + result + "号";
            }
        }
        return result;
    }

    @Override
    @Transactional
    public Integer getSequence(String tenantId, String labelName, String character) {
        Integer sequence = 1;
        if (StringUtils.isNotBlank(tenantId) && StringUtils.isNotBlank(labelName)
            && StringUtils.isNotBlank(character)) {
            DocumentNumberDetail detail = documentNumberDetailService.listAll().get(0);
            AutoFormSequence autoFormSequence =
                autoFormSequenceRepository.findOne(tenantId, labelName, character, detail.getCalendarYear());
            if (autoFormSequence != null && autoFormSequence.getSequenceValue() != null) {
                sequence = autoFormSequence.getSequenceValue();
            } else {
                sequence = documentNumberDetailService.listAll().get(0).getSequenceInitValue();
                AutoFormSequence afs = new AutoFormSequence();
                afs.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                afs.setCharacterValue(character);
                afs.setLabelName(labelName);
                afs.setTenantId(tenantId);
                afs.setSequenceValue(sequence);
                afs.setCalendarYear(detail.getCalendarYear());
                autoFormSequenceRepository.save(afs);
            }
            autoFormSequenceRepository.updateSequence(tenantId, labelName, character, detail.getCalendarYear());
        }
        return sequence;
    }

    @Override
    @Transactional
    public void updateSequence(String tenantId, String labelName, String character) {
        if (StringUtils.isNotBlank(tenantId) && StringUtils.isNotBlank(labelName)
            && StringUtils.isNotBlank(character)) {
            DocumentNumberDetail detail = documentNumberDetailService.listAll().get(0);
            autoFormSequenceRepository.updateSequence(tenantId, labelName, character, detail.getCalendarYear());
        }
    }
}
