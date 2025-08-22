package net.risesoft.service;

import net.risesoft.entity.SmsDetail;

public interface SmsDetailService {

    SmsDetail findByProcessSerialNumberAndPositionId(String processSerialNumber, String positionId);

    void saveOrUpdate(SmsDetail smsDetail);
}
