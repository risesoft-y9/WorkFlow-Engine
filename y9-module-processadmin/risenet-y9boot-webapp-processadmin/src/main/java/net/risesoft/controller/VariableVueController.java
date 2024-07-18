package net.risesoft.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
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
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Position;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.CustomTaskService;
import net.risesoft.service.CustomVariableService;
import net.risesoft.util.SysVariables;
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

    private final PositionApi positionApi;

    private final OrgUnitApi orgUnitManager;

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
     * 获取流程实例变量
     *
     * @param processInstanceId 流程实例id
     * @param page 页码
     * @param rows 条数
     * @return Y9Page<Map < String, Object>>
     */
    @GetMapping(value = "/getAllVariable")
    public Y9Page<Map<String, Object>> getAllVariable(@RequestParam(required = false) String processInstanceId,
        @RequestParam int page, @RequestParam int rows) {
        List<Map<String, Object>> items = new ArrayList<>();
        String tenantId = Y9LoginUserHolder.getTenantId();
        long totalCount;
        List<ProcessInstance> list;
        if (StringUtils.isBlank(processInstanceId)) {
            totalCount = runtimeService.createProcessInstanceQuery().count();
            list =
                runtimeService.createProcessInstanceQuery().orderByStartTime().desc().listPage((page - 1) * rows, rows);
        } else {
            totalCount = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).count();
            list = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).orderByStartTime()
                .desc().listPage((page - 1) * rows, rows);
        }
        Position position;
        OrgUnit orgUnit;
        Map<String, Object> mapTemp;
        for (ProcessInstance processInstance : list) {
            mapTemp = new HashMap<>(16);
            mapTemp.put("businessKey", processInstance.getBusinessKey());
            mapTemp.put("processInstanceId", processInstance.getId());
            mapTemp.put("processDefName", processInstance.getProcessDefinitionName());
            mapTemp.put("suspended", processInstance.isSuspended());
            mapTemp.put("startTime", DateFormatUtils.format(processInstance.getStartTime(), "yyyy-MM-dd HH:mm:ss"));
            mapTemp.put("startUserName", "无");
            if (StringUtils.isNotBlank(processInstance.getStartUserId())) {
                String[] userIdAndDeptId = processInstance.getStartUserId().split(":");
                if (userIdAndDeptId.length == 1) {
                    position = positionApi.get(tenantId, userIdAndDeptId[0]).getData();
                    orgUnit = orgUnitManager.getParent(tenantId, position.getId()).getData();
                    mapTemp.put("startUserName", position.getName() + "(" + orgUnit.getName() + ")");
                } else {
                    position = positionApi.get(tenantId, userIdAndDeptId[0]).getData();
                    if (null != position) {
                        orgUnit = orgUnitManager.getOrgUnit(tenantId, processInstance.getStartUserId().split(":")[1])
                            .getData();
                        if (null == orgUnit) {
                            mapTemp.put("startUserName", position.getName());
                        } else {
                            mapTemp.put("startUserName", position.getName() + "(" + orgUnit.getName() + ")");
                        }
                    }
                }
            }
            items.add(mapTemp);
        }
        int totalPages = (int)totalCount / rows + 1;
        return Y9Page.success(page, totalPages, totalCount, items, "获取列表成功");
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
                Position position = positionApi.get(tenantId, personId).getData();
                if (position != null && StringUtils.isNotBlank(position.getId())) {
                    mapTemp.put("userName", position.getName());
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
        if (ItemBoxTypeEnum.ADD.getValue().equals(type)) {
            Object obj = runtimeService.getVariable(processInstanceId, key);
            if (null != obj) {
                return Y9Result.failure(key + "对应的流程变量已存在。");
            }
        }
        if (SysVariables.USERS.equals(key)) {
            String userTemp = "";
            List<String> userList = new ArrayList<>();
            String[] users = value.split(",");
            for (String user : users) {
                Position position = positionApi.get(tenantId, user).getData();
                if (null != position && null != position.getId()) {
                    userList.add(user);
                } else {
                    if (StringUtils.isBlank(userTemp)) {
                        userTemp = user;
                    } else {
                        userTemp += "," + user;
                    }
                }
            }
            if (StringUtils.isBlank(userTemp)) {
                runtimeService.setVariable(processInstanceId, key, userList);
            } else {
                return Y9Result.failure(key + "中[" + userTemp + "]对应的人员不存在。");
            }
        } else if (SysVariables.USER.equals(key) || SysVariables.TASKSENDERID.equals(key)) {
            Position position = positionApi.get(tenantId, value).getData();
            if (null != position && null != position.getId()) {
                runtimeService.setVariable(processInstanceId, key, value);
            } else {
                return Y9Result.failure(key + "[" + value + "]对应的人员不存在。");
            }
        } else {
            runtimeService.setVariable(processInstanceId, key, value);
        }
        return Y9Result.successMsg("保存成功");
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
        if (ItemBoxTypeEnum.ADD.getValue().equals(type)) {
            Object obj = customVariableService.getVariableLocal(taskId, key);
            if (null != obj) {
                return Y9Result.failure(key + "对应的任务变量已存在。");
            }
        }
        if (SysVariables.USERS.equals(key)) {
            String userTemp = "";
            List<String> userList = new ArrayList<>();
            String[] users = value.split(",");
            for (String user : users) {
                Position position = positionApi.get(tenantId, user).getData();
                if (null != position && null != position.getId()) {
                    userList.add(user);
                } else {
                    if (StringUtils.isBlank(userTemp)) {
                        userTemp = user;
                    } else {
                        userTemp += "," + user;
                    }
                }
            }
            if (StringUtils.isBlank(userTemp)) {
                customVariableService.setVariableLocal(taskId, key, userList);
            } else {
                return Y9Result.failure(key + "中[" + userTemp + "]对应的人员不存在。");
            }
        } else if (SysVariables.USER.equals(key) || SysVariables.TASKSENDERID.equals(key)) {
            Position position = positionApi.get(tenantId, value).getData();
            if (null != position && null != position.getId()) {
                customVariableService.setVariableLocal(taskId, key, value);
            } else {
                return Y9Result.failure(key + "[" + value + "]对应的人员不存在。");
            }
        } else {
            customVariableService.setVariableLocal(taskId, key, value);
        }
        return Y9Result.successMsg("保存成功");
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
