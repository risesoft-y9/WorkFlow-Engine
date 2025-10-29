package net.risesoft.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;

import org.apache.commons.lang3.StringUtils;
import org.flowable.engine.RuntimeService;
import org.flowable.task.api.Task;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.consts.processadmin.SysVariables;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.CustomTaskService;
import net.risesoft.service.CustomVariableService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/vue/variable", produces = MediaType.APPLICATION_JSON_VALUE)
public class VariableVueController {

    private final CustomVariableService customVariableService;

    private final CustomTaskService customTaskService;

    private final RuntimeService runtimeService;

    private final OrgUnitApi orgUnitApi;

    /**
     * 删除流程变量
     *
     * @param processInstanceId 流程实例id
     * @param key 变量key
     * @return Y9Result<String>
     */
    @PostMapping(value = "/deleteProcessVar")
    public Y9Result<String> deleteProcessVar(@RequestParam @NotBlank String processInstanceId,
        @RequestParam @NotBlank String key) {
        runtimeService.removeVariable(processInstanceId, key);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 删除流程变量
     *
     * @param taskId 任务id
     * @param key 变量key
     * @return Y9Result<String>
     */
    @PostMapping(value = "/deleteTaskVar")
    public Y9Result<String> deleteTaskVar(@RequestParam @NotBlank String taskId, @RequestParam @NotBlank String key) {
        customVariableService.removeVariableLocal(taskId, key);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 获取变量信息
     *
     * @param processInstanceId 流程实例id
     * @param key 变量key
     * @return Y9Result<Object>
     */
    @SuppressWarnings("unchecked")
    @GetMapping(value = "/getProcessVariable")
    public Y9Result<Object> getProcessVariable(@RequestParam @NotBlank String processInstanceId,
        @RequestParam @NotBlank String key) {
        Object obj = runtimeService.getVariable(processInstanceId, key);
        if (SysVariables.USERS.equals(key)) {
            List<String> userList = (List<String>)obj;
            String users = "";
            for (String user : userList) {
                if (StringUtils.isBlank(users)) {
                    users = user;
                } else {
                    users += "," + user;
                }
            }
            return Y9Result.success(users, "获取成功");
        } else {
            return Y9Result.success(obj, "获取成功");
        }
    }

    /**
     * 获取任务列表
     *
     * @param processInstanceId 流程实例id
     * @return Y9Result<List < Map < String, Object>>>
     */
    @GetMapping(value = "/getTaskList")
    public Y9Result<List<Map<String, Object>>> getTaskList(@RequestParam @NotBlank String processInstanceId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<Task> taskListTemp = customTaskService.listByProcessInstanceId(processInstanceId);
        List<Map<String, Object>> taskList = new ArrayList<>();
        Map<String, Object> mapTemp;
        for (Task task : taskListTemp) {
            mapTemp = new HashMap<>(16);
            mapTemp.put("taskId", task.getId());
            mapTemp.put("userName", "无");
            String personId = task.getAssignee();
            if (StringUtils.isNotBlank(personId)) {
                OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, personId).getData();
                if (orgUnit != null && StringUtils.isNotBlank(orgUnit.getId())) {
                    mapTemp.put("userName", orgUnit.getName());
                }
            }
            taskList.add(mapTemp);
        }
        return Y9Result.success(taskList, "获取成功");
    }

    /**
     * 获取任务变量信息
     *
     * @param taskId 任务id
     * @param key 变量key
     * @return Y9Result<Object>
     */
    @SuppressWarnings("unchecked")
    @GetMapping(value = "/getTaskVariable")
    public Y9Result<Object> getTaskVariable(@RequestParam @NotBlank String taskId, @RequestParam @NotBlank String key) {
        Object obj = customVariableService.getVariableLocal(taskId, key);
        if (SysVariables.USERS.equals(key)) {
            List<String> userList = (List<String>)obj;
            String users = "";
            for (String user : userList) {
                if (StringUtils.isBlank(users)) {
                    users = user;
                } else {
                    users += "," + user;
                }
            }
            return Y9Result.success(users, "获取成功");
        } else {
            return Y9Result.success(obj, "获取成功");
        }
    }

    /**
     * 获取流程变量列表
     *
     * @param processInstanceId 流程实例id
     * @return Y9Result<List < Map < String, Object>>>
     */
    @GetMapping(value = "/processVarList")
    public Y9Result<List<Map<String, Object>>> processVarList(@RequestParam @NotBlank String processInstanceId) {
        List<Map<String, Object>> items = new ArrayList<>();
        Map<String, Object> vars = runtimeService.getVariables(processInstanceId);
        Map<String, Object> mapTemp;
        for (Map.Entry<String, Object> entry : vars.entrySet()) {
            mapTemp = new HashMap<>(16);
            mapTemp.put("processInstanceId", processInstanceId);
            mapTemp.put("key", entry.getKey());
            mapTemp.put("value", entry.getValue());
            items.add(mapTemp);
        }
        return Y9Result.success(items, "获取成功");
    }

    /**
     * 保存流程变量
     *
     * @param type 类型
     * @param processInstanceId 流程实例id
     * @param key 变量key
     * @param value 变量值
     * @return Y9Result<String>
     */
    @PostMapping(value = "/saveProcessVariable")
    public Y9Result<String> saveProcessVariable(@RequestParam String type, @RequestParam String processInstanceId,
        @RequestParam String key, @RequestParam(required = false) String value) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        // 检查变量是否已存在
        Y9Result<String> checkResult = checkVariableExists(type, processInstanceId, key);
        if (checkResult != null) {
            return checkResult;
        }

        // 根据变量类型处理不同的保存逻辑
        try {
            if (SysVariables.USERS.equals(key)) {
                return handleUsersVariable(processInstanceId, key, value, tenantId);
            } else if (SysVariables.USER.equals(key) || SysVariables.TASK_SENDER_ID.equals(key)) {
                return handleUserVariable(processInstanceId, key, value, tenantId);
            } else {
                runtimeService.setVariable(processInstanceId, key, value);
                return Y9Result.successMsg("保存成功");
            }
        } catch (Exception e) {
            return Y9Result.failure("保存流程变量失败: " + e.getMessage());
        }
    }

    /**
     * 检查变量是否已存在
     */
    private Y9Result<String> checkVariableExists(String type, String processInstanceId, String key) {
        if (ItemBoxTypeEnum.ADD.getValue().equals(type)) {
            Object obj = runtimeService.getVariable(processInstanceId, key);
            if (null != obj) {
                return Y9Result.failure(key + "对应的流程变量已存在。");
            }
        }
        return null;
    }

    /**
     * 处理 USERS 类型变量
     */
    private Y9Result<String> handleUsersVariable(String processInstanceId, String key, String value, String tenantId) {
        if (StringUtils.isBlank(value)) {
            runtimeService.setVariable(processInstanceId, key, new ArrayList<String>());
            return Y9Result.successMsg("保存成功");
        }
        String invalidUsers = "";
        List<String> userList = new ArrayList<>();
        String[] users = value.split(",");

        for (String user : users) {
            if (StringUtils.isBlank(user)) {
                continue;
            }
            OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, user).getData();
            if (orgUnit != null && orgUnit.getId() != null) {
                userList.add(user);
            } else {
                if (StringUtils.isBlank(invalidUsers)) {
                    invalidUsers = user;
                } else {
                    invalidUsers += "," + user;
                }
            }
        }
        if (StringUtils.isNotBlank(invalidUsers)) {
            return Y9Result.failure(key + "中[" + invalidUsers + "]对应的办理人员不存在。");
        }
        runtimeService.setVariable(processInstanceId, key, userList);
        return Y9Result.successMsg("保存成功");
    }

    /**
     * 处理 USER 或 TASK_SENDER_ID 类型变量
     */
    private Y9Result<String> handleUserVariable(String processInstanceId, String key, String value, String tenantId) {
        if (StringUtils.isBlank(value)) {
            runtimeService.setVariable(processInstanceId, key, value);
            return Y9Result.successMsg("保存成功");
        }

        OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, value).getData();
        if (orgUnit != null && orgUnit.getId() != null) {
            runtimeService.setVariable(processInstanceId, key, value);
            return Y9Result.successMsg("保存成功");
        } else {
            return Y9Result.failure(key + "[" + value + "]对应的人员数据不存在。");
        }
    }

    /**
     * 保存任务变量
     *
     * @param type 类型
     * @param taskId 任务id
     * @param key 变量key
     * @param value 变量值
     * @return Y9Result<String>
     */
    @PostMapping(value = "/saveTaskVariable")
    public Y9Result<String> saveTaskVariable(@RequestParam String type, @RequestParam String taskId,
        @RequestParam String key, @RequestParam(required = false) String value) {
        String tenantId = Y9LoginUserHolder.getTenantId();

        // 检查变量是否已存在
        Y9Result<String> checkResult = checkTaskVariableExists(type, taskId, key);
        if (checkResult != null) {
            return checkResult;
        }

        // 根据变量类型处理不同的保存逻辑
        try {
            if (SysVariables.USERS.equals(key)) {
                return handleTaskUsersVariable(taskId, key, value, tenantId);
            } else if (SysVariables.USER.equals(key) || SysVariables.TASK_SENDER_ID.equals(key)) {
                return handleTaskUserVariable(taskId, key, value, tenantId);
            } else {
                customVariableService.setVariableLocal(taskId, key, value);
                return Y9Result.successMsg("保存成功");
            }
        } catch (Exception e) {
            return Y9Result.failure("保存任务变量失败: " + e.getMessage());
        }
    }

    /**
     * 检查任务变量是否已存在
     */
    private Y9Result<String> checkTaskVariableExists(String type, String taskId, String key) {
        if (ItemBoxTypeEnum.ADD.getValue().equals(type)) {
            Object obj = customVariableService.getVariableLocal(taskId, key);
            if (null != obj) {
                return Y9Result.failure(key + "对应的任务变量已存在。");
            }
        }
        return null;
    }

    /**
     * 处理任务 USERS 类型变量
     */
    private Y9Result<String> handleTaskUsersVariable(String taskId, String key, String value, String tenantId) {
        if (StringUtils.isBlank(value)) {
            customVariableService.setVariableLocal(taskId, key, new ArrayList<String>());
            return Y9Result.successMsg("保存成功");
        }
        String invalidUsers = "";
        List<String> userList = new ArrayList<>();
        String[] users = value.split(",");
        for (String user : users) {
            if (StringUtils.isBlank(user)) {
                continue;
            }
            OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, user).getData();
            if (orgUnit != null && orgUnit.getId() != null) {
                userList.add(user);
            } else {
                if (StringUtils.isBlank(invalidUsers)) {
                    invalidUsers = user;
                } else {
                    invalidUsers += "," + user;
                }
            }
        }
        if (StringUtils.isNotBlank(invalidUsers)) {
            return Y9Result.failure(key + "中[" + invalidUsers + "]对应的办理人员数据不存在。");
        }
        customVariableService.setVariableLocal(taskId, key, userList);
        return Y9Result.successMsg("保存成功");
    }

    /**
     * 处理任务 USER 或 TASK_SENDER_ID 类型变量
     */
    private Y9Result<String> handleTaskUserVariable(String taskId, String key, String value, String tenantId) {
        if (StringUtils.isBlank(value)) {
            customVariableService.setVariableLocal(taskId, key, value);
            return Y9Result.successMsg("保存成功");
        }

        OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, value).getData();
        if (orgUnit != null && orgUnit.getId() != null) {
            customVariableService.setVariableLocal(taskId, key, value);
            return Y9Result.successMsg("保存成功");
        } else {
            return Y9Result.failure(key + "[" + value + "]对应的人员不存在。");
        }
    }

    /**
     * 获取任务变量列表
     *
     * @param taskId 任务id
     * @return Y9Result<List < Map < String, Object>>>
     */
    @GetMapping(value = "/taskVarList")
    public Y9Result<List<Map<String, Object>>> taskVarList(@RequestParam @NotBlank String taskId) {
        List<Map<String, Object>> items = new ArrayList<>();
        Map<String, Object> vars = customVariableService.getVariablesLocal(taskId);
        Map<String, Object> mapTemp;
        for (Map.Entry<String, Object> entry : vars.entrySet()) {
            mapTemp = new HashMap<>(16);
            mapTemp.put("taskId", taskId);
            mapTemp.put("key", entry.getKey());
            mapTemp.put("value", entry.getValue());
            items.add(mapTemp);
        }
        return Y9Result.success(items, "获取成功");
    }
}
