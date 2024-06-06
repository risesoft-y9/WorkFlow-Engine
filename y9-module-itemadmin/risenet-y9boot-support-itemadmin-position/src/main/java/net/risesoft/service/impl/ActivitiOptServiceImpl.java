package net.risesoft.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.risesoft.api.processadmin.RuntimeApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.model.processadmin.ProcessInstanceModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.service.ActivitiOptService;
import net.risesoft.util.CommonOpt;
import net.risesoft.y9.Y9LoginUserHolder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ActivitiOptServiceImpl implements ActivitiOptService {

    private final RuntimeApi runtimeManager;

    private final TaskApi taskManager;

    @Override
    public TaskModel startProcess(String processSerialNumber, String processDefinitionKey, String systemName,
        Map<String, Object> map) {
        TaskModel task = new TaskModel();
        try {
            String tenantId = Y9LoginUserHolder.getTenantId(), userId = Y9LoginUserHolder.getPositionId();
            map = CommonOpt.setVariables(userId, Y9LoginUserHolder.getPosition().getName(), "", Collections.singletonList(userId),
                processSerialNumber, "", map);
            ProcessInstanceModel piModel =
                runtimeManager.startProcessInstanceByKey(tenantId, userId, processDefinitionKey, systemName, map);
            // 获取运行的任务节点,这里没有考虑启动节点下一个用户任务节点是多实例的情况
            String processInstanceId = piModel.getId();
            task = taskManager.findByProcessInstanceId(tenantId, processInstanceId).get(0);
        } catch (Exception e) {
            LOGGER.error("启动流程失败", e);
        }
        return task;
    }
}
