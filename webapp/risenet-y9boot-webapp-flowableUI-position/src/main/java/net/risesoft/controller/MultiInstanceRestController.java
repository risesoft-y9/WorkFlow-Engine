package net.risesoft.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.api.processadmin.VariableApi;
import net.risesoft.model.Position;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.MultiInstanceService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;

@RestController
@RequestMapping(value = "/vue/multiInstance")
public class MultiInstanceRestController {

    @Autowired
    private MultiInstanceService multiInstanceService;

    @Autowired
    private TaskApi taskManager;

    @Autowired
    private ProcessDefinitionApi processDefinitionManager;

    @Autowired
    private VariableApi variableManager;

    @Autowired
    private ProcessParamApi processParamManager;

    /**
     * 加签
     *
     * @param processInstanceId 流程实例id
     * @param executionId 执行实例id
     * @param taskId 任务id
     * @param userChoice 选择人员
     * @param selectUserId 加签人员
     * @param num 加签序号
     * @param isSendSms 是否短信提醒
     * @param isShuMing 是否署名
     * @param smsContent 短信内容
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/addExecutionId", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> addExecutionId(@RequestParam(required = true) String processInstanceId, @RequestParam(required = true) String executionId, @RequestParam(required = true) String taskId, @RequestParam(required = true) String userChoice, @RequestParam(required = false) String selectUserId,
        @RequestParam(required = false) int num, @RequestParam(required = false) String isSendSms, @RequestParam(required = false) String isShuMing, @RequestParam(required = false) String smsContent) {
        try {
            /**
             * selectUserId不为空说明是从串行加签过来的
             */
            if (StringUtils.isBlank(selectUserId)) {
                multiInstanceService.addExecutionId(processInstanceId, taskId, userChoice, isSendSms, isShuMing, smsContent);
            } else {
                multiInstanceService.addExecutionId4Sequential(executionId, taskId, userChoice, selectUserId, num);
            }
            return Y9Result.successMsg("加签成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("加签失败");
    }

    /**
     * 获取加减签任务列表
     *
     * @param processInstanceId 流程实例id
     * @return
     */
    @RequestMapping(value = "/getAddOrDeleteMultiInstance", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Y9Result<Map<String, Object>> getAddOrDeleteMultiInstance(@RequestParam(required = true) String processInstanceId) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        Position position = Y9LoginUserHolder.getPosition();
        String tenantId = Y9LoginUserHolder.getTenantId();
        TaskModel task = null;
        List<TaskModel> list = taskManager.findByProcessInstanceId(tenantId, processInstanceId);
        if (list.size() > 0) {
            task = list.get(0);
        }
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        if (task != null) {
            String type = processDefinitionManager.getNodeType(tenantId, task.getProcessDefinitionId(), task.getTaskDefinitionKey());
            if (SysVariables.PARALLEL.equals(type)) {
                listMap = multiInstanceService.assigneeList4Parallel(processInstanceId);
            } else if (SysVariables.SEQUENTIAL.equals(type)) {
                listMap = multiInstanceService.assigneeList4Sequential(task.getId());
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
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/removeExecution", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> removeExecution(@RequestParam(required = true) String executionId, @RequestParam(required = true) String taskId, @RequestParam(required = true) String elementUser) {
        try {
            multiInstanceService.removeExecution(executionId, taskId, elementUser);
            return Y9Result.successMsg("减签成功");
        } catch (Exception e) {
            e.printStackTrace();
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
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/removeExecution4Sequential", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> removeExecution4Sequential(@RequestParam(required = true) String executionId, @RequestParam(required = true) String taskId, @RequestParam(required = true) String elementUser, @RequestParam(required = true) int num) {
        try {
            multiInstanceService.removeExecution4Sequential(executionId, taskId, elementUser, num);
            return Y9Result.successMsg("减签成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("减签失败");
    }

    /**
     * 设置主办人
     *
     * @param taskId 任务id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/setSponsor", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> setSponsor(@RequestParam(required = true) String taskId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        TaskModel taskModel = taskManager.findById(tenantId, taskId);
        List<TaskModel> list = taskManager.findByProcessInstanceId(tenantId, taskModel.getProcessInstanceId());
        String sponsorTaskId = "";
        for (TaskModel task : list) {
            String parallelSponsor = variableManager.getVariableLocal(tenantId, task.getId(), SysVariables.PARALLELSPONSOR);
            if (parallelSponsor != null) {
                sponsorTaskId = task.getId();
                break;
            }
        }
        if (StringUtils.isNotBlank(sponsorTaskId)) {
            // 删除任务变量
            variableManager.deleteVariableLocal(tenantId, sponsorTaskId, SysVariables.PARALLELSPONSOR);
        }

        // 重设任务变量
        Map<String, Object> val = new HashMap<String, Object>();
        val.put("val", taskModel.getAssignee());
        variableManager.setVariableLocal(tenantId, taskId, SysVariables.PARALLELSPONSOR, val);

        // 修改自定义变量主办人字段
        ProcessParamModel processParam = processParamManager.findByProcessInstanceId(tenantId, taskModel.getProcessInstanceId());
        processParam.setSponsorGuid(taskModel.getAssignee());
        processParamManager.saveOrUpdate(tenantId, processParam);
        return Y9Result.successMsg("设置成功");
    }
}
