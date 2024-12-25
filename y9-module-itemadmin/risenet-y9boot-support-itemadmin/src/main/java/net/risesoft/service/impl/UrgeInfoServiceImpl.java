package net.risesoft.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.entity.ProcessParam;
import net.risesoft.entity.UrgeInfo;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.repository.jpa.UrgeInfoRepository;
import net.risesoft.service.ProcessParamService;
import net.risesoft.service.UrgeInfoService;
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
        String tenantId = Y9LoginUserHolder.getTenantId();
        ProcessParam processParam = processParamService.findByProcessSerialNumber(processSerialNumber);
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        if (StringUtils.isBlank(processParam.getCompleter())) {
            List<TaskModel> taskList =
                taskApi.findByProcessInstanceId(Y9LoginUserHolder.getTenantId(), processParam.getProcessInstanceId())
                    .getData();
            boolean isSub = processDefinitionApi.isSubProcessChildNode(tenantId,
                taskList.get(0).getProcessDefinitionId(), taskList.get(0).getTaskDefinitionKey()).getData();
            UrgeInfo urgeInfo = new UrgeInfo();
            urgeInfo.setSub(isSub);
            urgeInfo.setProcessSerialNumber(processSerialNumber);
            urgeInfo.setProcessInstanceId(processParam.getProcessInstanceId());
            urgeInfo.setExecutionId(taskList.get(0).getExecutionId());
            urgeInfo.setUserId(userInfo.getPersonId());
            urgeInfo.setUserName(userInfo.getName());
            urgeInfo.setMsgContent(msgContent);
            urgeInfo.setCreateTime(new Date());
            if (isSub) {
                List<String> executionIds = new ArrayList<>();
                taskList.stream().forEach(task -> {
                    if (!executionIds.contains(task.getExecutionId())) {
                        executionIds.add(task.getExecutionId());
                    }
                });
                executionIds.forEach(executionId -> {
                    urgeInfo.setId(Y9IdGenerator.genId());
                    urgeInfoRepository.save(urgeInfo);
                });
            } else {
                urgeInfo.setId(Y9IdGenerator.genId());
                urgeInfoRepository.save(urgeInfo);
            }
        }
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        urgeInfoRepository.deleteById(id);
    }
}
