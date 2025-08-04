package net.risesoft.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.entity.ProcessParam;
import net.risesoft.entity.UrgeInfo;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.user.UserInfo;
import net.risesoft.repository.jpa.UrgeInfoRepository;
import net.risesoft.service.UrgeInfoService;
import net.risesoft.service.core.ProcessParamService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author : qinman
 * @date : 2024-12-24
 * @since 9.6.8
 **/
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UrgeInfoServiceImpl implements UrgeInfoService {

    private final UrgeInfoRepository urgeInfoRepository;

    private final ProcessParamService processParamService;

    private final TaskApi taskApi;

    private final ProcessDefinitionApi processDefinitionApi;

    @Override
    public List<UrgeInfo> findByProcessSerialNumber(String processSerialNumber) {
        return urgeInfoRepository.findByProcessSerialNumberOrderByCreateTimeDesc(processSerialNumber);
    }

    @Override
    @Transactional
    public void save(String processSerialNumber, String msgContent) {
        String[] isSubAndProcessSerialNumberAndExecutionId = processSerialNumber.split(":");
        String realProcessSerialNumber = isSubAndProcessSerialNumberAndExecutionId[1];
        ProcessParam processParam = processParamService.findByProcessSerialNumber(realProcessSerialNumber);
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        UrgeInfo urgeInfo = new UrgeInfo();
        urgeInfo.setId(Y9IdGenerator.genId());
        urgeInfo.setSub(Boolean.parseBoolean(isSubAndProcessSerialNumberAndExecutionId[0]));
        urgeInfo.setProcessSerialNumber(realProcessSerialNumber);
        urgeInfo.setProcessInstanceId(processParam.getProcessInstanceId());
        urgeInfo.setUserId(userInfo.getPersonId());
        urgeInfo.setUserName(userInfo.getName());
        urgeInfo.setMsgContent(msgContent);
        urgeInfo.setCreateTime(new Date());
        urgeInfo.setExecutionId(isSubAndProcessSerialNumberAndExecutionId[2]);
        urgeInfoRepository.save(urgeInfo);
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        urgeInfoRepository.deleteById(id);
    }
}
