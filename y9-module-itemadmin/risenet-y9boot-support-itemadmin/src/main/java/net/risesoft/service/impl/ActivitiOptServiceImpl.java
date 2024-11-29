package net.risesoft.service.impl;

import java.util.Collections;
import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.processadmin.RuntimeApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.model.processadmin.ProcessInstanceModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.service.ActivitiOptService;
import net.risesoft.util.CommonOpt;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ActivitiOptServiceImpl implements ActivitiOptService {

    private final RuntimeApi runtimeApi;

    private final TaskApi taskApi;

    @Override
    public TaskModel startProcess(String processSerialNumber, String processDefinitionKey, String systemName,
        Map<String, Object> map) {
        TaskModel task = new TaskModel();
        try {
            String tenantId = Y9LoginUserHolder.getTenantId(), userId = Y9LoginUserHolder.getOrgUnitId();
            map = CommonOpt.setVariables(userId, Y9LoginUserHolder.getOrgUnit().getName(), "",
                Collections.singletonList(userId), processSerialNumber, null, map);
            ProcessInstanceModel piModel =
                runtimeApi.startProcessInstanceByKey(tenantId, userId, processDefinitionKey, systemName, map).getData();
            // 获取运行的任务节点,这里没有考虑启动节点下一个用户任务节点是多实例的情况
            String processInstanceId = piModel.getId();
            task = taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData().get(0);
        } catch (Exception e) {
            LOGGER.error("启动流程失败", e);
        }
        return task;
    }
}
