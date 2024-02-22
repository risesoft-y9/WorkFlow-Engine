package net.risesoft.service.impl;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.risesoft.model.processadmin.ProcessInstanceModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.service.ActivitiOptService;
import net.risesoft.util.CommonOpt;
import net.risesoft.y9.Y9LoginUserHolder;

import y9.client.rest.processadmin.RuntimeApiClient;
import y9.client.rest.processadmin.TaskApiClient;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@Service(value = "activitiOptService")
public class ActivitiOptServiceImpl implements ActivitiOptService {

    @Autowired
    private RuntimeApiClient runtimeManager;

    @Autowired
    private TaskApiClient taskManager;

    @Override
    public TaskModel startProcess(String processSerialNumber, String processDefinitionKey, String systemName,
        Map<String, Object> map) {
        TaskModel task = new TaskModel();
        try {
            UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
            String tenantId = Y9LoginUserHolder.getTenantId(), userId = userInfo.getPersonId(),
                personName = userInfo.getName();
            map = CommonOpt.setVariables(userId, personName, "", Arrays.asList(userId), processSerialNumber, "", map);
            ProcessInstanceModel piModel =
                runtimeManager.startProcessInstanceByKey(tenantId, userId, processDefinitionKey, systemName, map);
            // 获取运行的任务节点,这里没有考虑启动节点下一个用户任务节点是多实例的情况
            String processInstanceId = piModel.getId();
            task = taskManager.findByProcessInstanceId(tenantId, processInstanceId).get(0);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return task;
    }
}
