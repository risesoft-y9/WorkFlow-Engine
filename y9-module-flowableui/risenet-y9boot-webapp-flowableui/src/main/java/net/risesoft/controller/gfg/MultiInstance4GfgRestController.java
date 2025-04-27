package net.risesoft.controller.gfg;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.validation.constraints.NotBlank;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.api.itemadmin.SignDeptDetailApi;
import net.risesoft.api.itemadmin.SignDeptInfoApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.api.processadmin.VariableApi;
import net.risesoft.enums.SignDeptDetailStatusEnum;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.model.itemadmin.SignDeptDetailModel;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Position;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.MultiInstanceService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 加减签
 *
 * @author zhangchongjie
 * @date 2024/06/05
 */
@Validated
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping(value = "/vue/multiInstance/gfg", produces = MediaType.APPLICATION_JSON_VALUE)
public class MultiInstance4GfgRestController {

    private final MultiInstanceService multiInstanceService;

    private final TaskApi taskApi;

    private final ProcessDefinitionApi processDefinitionApi;

    private final VariableApi variableApi;

    private final ProcessParamApi processParamApi;

    private final SignDeptDetailApi signDeptDetailApi;

    private final OrgUnitApi orgUnitApi;

    private final SignDeptInfoApi signDeptInfoApi;

    /**
     * 并行加签
     * 
     * @param processInstanceId 流程实例id
     * @param activityId 加签节点id
     * @param userChoice 选择人员
     * @param dueDate 到期时间
     * @param description 办文说明
     * @return
     */
    @PostMapping(value = "/addExecutionId")
    public Y9Result<List<SignDeptDetailModel>> addExecutionId(@RequestParam @NotBlank String processInstanceId,
        @RequestParam @NotBlank String activityId, @RequestParam @NotBlank String userChoice,
        @RequestParam(required = false) String dueDate, @RequestParam(required = false) String description) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            ProcessParamModel processParamModel =
                processParamApi.findByProcessInstanceId(Y9LoginUserHolder.getTenantId(), processInstanceId).getData();
            List<SignDeptDetailModel> signDeptDetailModels =
                signDeptDetailApi.findByProcessSerialNumberAndStatus(tenantId,
                    processParamModel.getProcessSerialNumber(), SignDeptDetailStatusEnum.DOING.getValue()).getData();
            String[] users = userChoice.split(";");
            StringBuffer doingDept = new StringBuffer();
            Arrays.stream(users).forEach(user -> {
                OrgUnit bureau = orgUnitApi.getBureau(tenantId, user).getData();
                if (signDeptDetailModels.stream().anyMatch(sdd -> sdd.getDeptId().equals(bureau.getId()))) {
                    if (doingDept.length() == 0) {
                        doingDept.append(bureau.getName());
                    } else {
                        doingDept.append(",").append(bureau.getName());
                    }
                }
            });
            if (doingDept.length() != 0) {
                return Y9Result.failure("加签失败:" + doingDept + "已经在会签！");
            }
            processParamModel.setDueDate(null);
            if (StringUtils.isNotBlank(dueDate)) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    processParamModel.setDueDate(sdf.parse(dueDate));
                } catch (ParseException e) {
                    LOGGER.error("办理期限转换失败{}", dueDate);
                }
            }
            processParamModel.setDescription(description);
            processParamApi.saveOrUpdate(Y9LoginUserHolder.getTenantId(), processParamModel);
            /*
             * 保存至表单中的委内会签
             */
            StringBuffer deptIds = new StringBuffer();
            Arrays.stream(userChoice.split(";")).forEach(user -> {
                OrgUnit bureau = orgUnitApi.getBureau(tenantId, user).getData();
                if (!deptIds.toString().contains(bureau.getId())) {
                    if (StringUtils.isBlank(deptIds)) {
                        deptIds.append(bureau.getId());
                    } else {
                        deptIds.append(",").append(bureau.getId());
                    }
                }
            });
            signDeptInfoApi.addSignDept(tenantId, Y9LoginUserHolder.getPositionId(), deptIds.toString(), "0",
                processParamModel.getProcessSerialNumber());
            multiInstanceService.addExecutionId(processParamModel, activityId, userChoice);
            return Y9Result.successMsg("加签成功");
        } catch (Exception e) {
            LOGGER.error("加签失败", e);
        }
        return Y9Result.failure("加签失败");
    }

    /**
     * 获取加减签任务列表
     *
     * @param processInstanceId 流程实例id
     * @return Y9Result<Map < String, Object>>
     */
    @GetMapping(value = "/getAddOrDeleteMultiInstance")
    public Y9Result<Map<String, Object>> getAddOrDeleteMultiInstance(@RequestParam @NotBlank String processInstanceId) {
        Map<String, Object> map = new HashMap<>(16);
        Position position = Y9LoginUserHolder.getPosition();
        String tenantId = Y9LoginUserHolder.getTenantId();
        TaskModel task = null;
        List<TaskModel> list = taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
        if (!list.isEmpty()) {
            task = list.get(0);
        }
        List<Map<String, Object>> listMap = new ArrayList<>();
        if (task != null) {
            String type = processDefinitionApi
                .getNodeType(tenantId, task.getProcessDefinitionId(), task.getTaskDefinitionKey()).getData();
            if (SysVariables.PARALLEL.equals(type)) {
                listMap = multiInstanceService.listAssignee4Parallel(processInstanceId);
            } else if (SysVariables.SEQUENTIAL.equals(type)) {
                listMap = multiInstanceService.listAssignee4Sequential(task.getId());
            }
            map.put("type", type);
            map.put("taskId", task.getId());
            map.put("taskDefinitionKey", task.getTaskDefinitionKey());
            map.put("userName", position.getName());
            map.put("userId", position.getId());
        }
        map.put("rows", listMap);
        return Y9Result.success(map, "获取成功");
    }

    /**
     * 并行减签
     *
     * @param executionId 执行实例id
     * @param taskId 任务id
     * @param elementUser 减签人员
     * @return Y9Result<String>
     */
    @PostMapping(value = "/removeExecution")
    public Y9Result<String> removeExecution(@RequestParam @NotBlank String executionId,
        @RequestParam @NotBlank String taskId, @RequestParam @NotBlank String elementUser) {
        try {
            multiInstanceService.removeExecution(executionId, taskId, elementUser);
            return Y9Result.successMsg("减签成功");
        } catch (Exception e) {
            LOGGER.error("减签失败", e);
        }
        return Y9Result.failure("减签失败");
    }

    /**
     * 串行减签
     *
     * @param executionId 执行实例id
     * @param taskId 任务id
     * @param elementUser 减签人员
     * @param num 减签序号
     * @return Y9Result<String>
     */
    @PostMapping(value = "/removeExecution4Sequential")
    public Y9Result<String> removeExecution4Sequential(@RequestParam @NotBlank String executionId,
        @RequestParam @NotBlank String taskId, @RequestParam @NotBlank String elementUser,
        @RequestParam(required = false) int num) {
        try {
            multiInstanceService.removeExecution4Sequential(executionId, taskId, elementUser, num);
            return Y9Result.successMsg("减签成功");
        } catch (Exception e) {
            LOGGER.error("减签失败", e);
        }
        return Y9Result.failure("减签失败");
    }

    /**
     * 设置主办人
     *
     * @param taskId 任务id
     * @return Y9Result<String>
     */
    @PostMapping(value = "/setSponsor")
    public Y9Result<String> setSponsor(@RequestParam @NotBlank String taskId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        TaskModel taskModel = taskApi.findById(tenantId, taskId).getData();
        List<TaskModel> list = taskApi.findByProcessInstanceId(tenantId, taskModel.getProcessInstanceId()).getData();
        String sponsorTaskId = "";
        for (TaskModel task : list) {
            String parallelSponsor =
                variableApi.getVariableLocal(tenantId, task.getId(), SysVariables.PARALLELSPONSOR).getData();
            if (parallelSponsor != null) {
                sponsorTaskId = task.getId();
                break;
            }
        }
        if (StringUtils.isNotBlank(sponsorTaskId)) {
            // 删除任务变量
            variableApi.deleteVariableLocal(tenantId, sponsorTaskId, SysVariables.PARALLELSPONSOR);
        }

        // 重设任务变量
        Map<String, Object> val = new HashMap<>();
        val.put("val", taskModel.getAssignee());
        variableApi.setVariableLocal(tenantId, taskId, SysVariables.PARALLELSPONSOR, val);

        // 修改自定义变量主办人字段
        ProcessParamModel processParam =
            processParamApi.findByProcessInstanceId(tenantId, taskModel.getProcessInstanceId()).getData();
        processParam.setSponsorGuid(taskModel.getAssignee());
        processParamApi.saveOrUpdate(tenantId, processParam);
        return Y9Result.successMsg("设置成功");
    }
}
