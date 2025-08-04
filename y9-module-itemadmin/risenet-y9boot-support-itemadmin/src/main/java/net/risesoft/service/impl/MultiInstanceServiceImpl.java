package net.risesoft.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.processadmin.RuntimeApi;
import net.risesoft.api.processadmin.VariableApi;
import net.risesoft.consts.processadmin.SysVariables;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.MultiInstanceService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;

/*
 * @author qinman
 * 
 * @author zhangchongjie
 *
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
public class MultiInstanceServiceImpl implements MultiInstanceService {

    private final VariableApi variableApi;

    private final RuntimeApi runtimeApi;

    @SuppressWarnings("unchecked")
    @Override
    public void addMultiInstanceExecution(String activityId, String parentExecutionId, String taskId,
        String elementUser) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        /*
         * 改变流程变量中users的值
         */
        String userObj = variableApi.getVariable(tenantId, taskId, SysVariables.USERS).getData();
        List<String> users = userObj == null ? new ArrayList<>() : Y9JsonUtil.readValue(userObj, List.class);
        users.add(elementUser);
        Map<String, Object> val = new HashMap<>();
        val.put("val", users);
        variableApi.setVariable(tenantId, taskId, SysVariables.USERS, val);
        /*
         * 新增执行实例
         */
        Map<String, Object> map = new HashMap<>(16);
        map.put("elementUser", elementUser);
        runtimeApi.addMultiInstanceExecution(tenantId, activityId, parentExecutionId, map);

        // 加签后,活动实例数需修改+1
        // Object nrOfActiveInstances = variableApi.getVariable(tenantId, taskId,
        // SysVariables.NR_OF_ACTIVE_INSTANCES);
        // if(nrOfActiveInstances != null) {
        // variableApi.setVariable(tenantId, taskId,
        // SysVariables.NR_OF_ACTIVE_INSTANCES, (int)nrOfActiveInstances + 1);
        // }
    }

    @Override
    public Y9Result<Object> addMultiInstanceExecution(String activityId, String parentExecutionId, String elementUser) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        /*
         * 新增执行实例
         */
        Map<String, Object> map = new HashMap<>(16);
        map.put("elementUser", elementUser);
        return runtimeApi.addMultiInstanceExecution(tenantId, activityId, parentExecutionId, map);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void deleteMultiInstanceExecution(String executionId, String taskId, String elementUser) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        /*
         * 改变流程变量中users的值
         */
        String userObj = variableApi.getVariable(tenantId, taskId, SysVariables.USERS).getData();
        List<Object> users = userObj == null ? new ArrayList<>() : Y9JsonUtil.readValue(userObj, List.class);
        List<String> usersTemp = new ArrayList<>();
        boolean isDelete = false;
        // 存在相同人员id时，减签只一个人员
        assert users != null;
        for (Object user : users) {
            String user1 = user.toString();
            if (isDelete) {
                usersTemp.add(user1);
            } else {
                // 排除减签人员
                if (elementUser.equals(user1)) {
                    isDelete = true;
                }
            }
        }
        Map<String, Object> vmap = new HashMap<>(16);
        vmap.put(SysVariables.USERS, usersTemp);
        variableApi.setVariables(tenantId, taskId, vmap);
        /*
         * 新删除执行实例
         */
        runtimeApi.deleteMultiInstanceExecution(tenantId, executionId);
    }
}
