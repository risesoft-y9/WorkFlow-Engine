package net.risesoft.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.SmsDetail;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.repository.jpa.SmsDetailRepository;
import net.risesoft.service.SmsDetailService;
import net.risesoft.util.Y9DateTimeUtils;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class SmsDetailServiceImpl implements SmsDetailService {

    private final SmsDetailRepository smsDetailRepository;

    @Override
    public SmsDetail findByProcessSerialNumberAndPositionId(String processSerialNumber, String positionId) {
        return smsDetailRepository.findByProcessSerialNumberAndPositionId(processSerialNumber, positionId);
    }

    @Override
    @Transactional
    public void saveOrUpdate(SmsDetail smsDetail) {
        SmsDetail oldSmsDetail =
            this.findByProcessSerialNumberAndPositionId(smsDetail.getProcessSerialNumber(), smsDetail.getPositionId());
        if (null != oldSmsDetail) {
            oldSmsDetail.setContent(smsDetail.getContent());
            oldSmsDetail.setSign(smsDetail.isSign());
            oldSmsDetail.setSend(smsDetail.isSend());
            oldSmsDetail.setPositionIds(smsDetail.getPositionIds());
            oldSmsDetail.setModifyDate(Y9DateTimeUtils.formatCurrentDateTime());
            smsDetailRepository.save(oldSmsDetail);
        } else {
            smsDetail.setId(Y9IdGenerator.genId());
            smsDetail.setCreateDate(Y9DateTimeUtils.formatCurrentDateTime());
            smsDetail.setModifyDate(Y9DateTimeUtils.formatCurrentDateTime());
            smsDetailRepository.save(smsDetail);
        }
    }
}
