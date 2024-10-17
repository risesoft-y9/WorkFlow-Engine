package net.risesoft.service.extend.impl;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.service.extend.DataCenterService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataCenterServiceImpl implements DataCenterService {

    @Override
    public void deleteOfficeInfo(String processInstanceId) {}

    @Override
    public boolean saveToDataCenter(String processInstanceId) {
        return true;
    }

    @Override
    public boolean saveToDateCenter1(String processInstanceId, String processDefinitionId) {
        return true;
    }
}
