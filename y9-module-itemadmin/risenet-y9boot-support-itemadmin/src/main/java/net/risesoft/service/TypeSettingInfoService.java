package net.risesoft.service;

import java.util.List;


import net.risesoft.entity.TypeSettingInfo;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface TypeSettingInfoService {

    void delTypeSetting(String id);

    List<TypeSettingInfo> findByProcessSerialNumber(String processSerialNumber);

    void saveTypeSetting(String processSerialNumber, String jsonData);
}
