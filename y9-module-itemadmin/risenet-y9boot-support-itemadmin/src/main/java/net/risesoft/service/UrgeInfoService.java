package net.risesoft.service;

import java.util.List;

import net.risesoft.entity.UrgeInfo;

/**
 * @author : qinman
 * @date : 2024-12-24
 * @since 9.6.8
 **/
public interface UrgeInfoService {

    List<UrgeInfo> findByProcessSerialNumber(String processSerialNumber);

    void save(String processSerialNumber, String msgContent);

    void deleteById(String id);
}
