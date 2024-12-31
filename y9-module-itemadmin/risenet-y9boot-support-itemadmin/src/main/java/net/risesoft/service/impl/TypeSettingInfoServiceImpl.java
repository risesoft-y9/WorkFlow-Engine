package net.risesoft.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.TypeSettingInfo;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.repository.jpa.TypeSettingInfoRepository;
import net.risesoft.service.TypeSettingInfoService;
import net.risesoft.y9.json.Y9JsonUtil;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TypeSettingInfoServiceImpl implements TypeSettingInfoService {

    private final TypeSettingInfoRepository typeSettingInfoRepository;

    @Override
    public List<TypeSettingInfo> findByProcessSerialNumber(String processSerialNumber) {
        return typeSettingInfoRepository.findByProcessSerialNumberOrderByTabIndexAsc(processSerialNumber);
    }

    @Override
    @Transactional
    public void saveTypeSetting(String processSerialNumber, String jsonData) {
        List<TypeSettingInfo> list = Y9JsonUtil.readList(jsonData, TypeSettingInfo.class);
        int maxTabIndex = 1;
        List<String> ids = new ArrayList<>();
        for (TypeSettingInfo typeSettingInfo : list) {
            if (StringUtils.isBlank(typeSettingInfo.getId())) {
                typeSettingInfo.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            }
            typeSettingInfo.setTabIndex(maxTabIndex);
            typeSettingInfo.setProcessSerialNumber(processSerialNumber);
            typeSettingInfoRepository.save(typeSettingInfo);
            ids.add(typeSettingInfo.getId());
            maxTabIndex++;
        }
        typeSettingInfoRepository.deleteByProcessSerialNumberAndIdNotIn(processSerialNumber, ids);
    }
}
