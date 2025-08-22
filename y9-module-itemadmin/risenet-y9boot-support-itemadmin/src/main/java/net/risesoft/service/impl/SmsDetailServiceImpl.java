package net.risesoft.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.SmsDetail;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.repository.jpa.SmsDetailRepository;
import net.risesoft.service.SmsDetailService;

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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SmsDetail oldSmsDetail =
            this.findByProcessSerialNumberAndPositionId(smsDetail.getProcessSerialNumber(), smsDetail.getPositionId());
        if (null != oldSmsDetail) {
            oldSmsDetail.setContent(smsDetail.getContent());
            oldSmsDetail.setSign(smsDetail.isSign());
            oldSmsDetail.setSend(smsDetail.isSend());
            oldSmsDetail.setPositionIds(smsDetail.getPositionIds());
            oldSmsDetail.setModifyDate(sdf.format(new Date()));
            smsDetailRepository.save(oldSmsDetail);
        } else {
            smsDetail.setId(Y9IdGenerator.genId());
            smsDetail.setCreateDate(sdf.format(new Date()));
            smsDetail.setModifyDate(sdf.format(new Date()));
            smsDetailRepository.save(smsDetail);
        }
    }
}
