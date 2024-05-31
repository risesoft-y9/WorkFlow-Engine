package net.risesoft.controller.mobile.v1;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.api.itemadmin.position.ButtonOperation4PositionApi;
import net.risesoft.api.itemadmin.position.Document4PositionApi;
import net.risesoft.api.itemadmin.position.ProcessTrack4PositionApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.api.processadmin.HistoricProcessApi;
import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.SpecialOperationApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.api.processadmin.VariableApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.model.itemadmin.ProcessTrackModel;
import net.risesoft.model.platform.Person;
import net.risesoft.model.platform.Position;
import net.risesoft.model.processadmin.HistoricProcessInstanceModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.ButtonOperationService;
import net.risesoft.service.MultiInstanceService;
import net.risesoft.service.Process4SearchService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.util.Y9Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 菜单按钮方法接口
 *
 * @author zhangchongjie
 * @date 2024/01/17
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/mobile/v1/buttonOperation")
public class MobileV1ButtonOperationController {


    private final PersonApi personApi;

    private final PositionApi positionApi;

    private final TaskApi taskApi;

    private final ButtonOperation4PositionApi buttonOperation4PositionApi;

    private final HistoricProcessApi historicProcessApi;

    private final Document4PositionApi document4PositionApi;

    private final SpecialOperationApi specialOperationApi;

    private final VariableApi variableApi;

    private final ButtonOperationService buttonOperationService;

    private final Process4SearchService process4SearchService;

    private final ProcessParamApi processParamApi;

    private final ProcessDefinitionApi processDefinitionApi;

    private final MultiInstanceService multiInstanceService;

    private final ProcessTrack4PositionApi processTrack4PositionApi;

    /**
     * 签收：抢占式办理时，签收后，其他人不可再签收办理
     *
     * @param taskId 任务id
     */
    @RequestMapping(value = "/claim")
    public Y9Result<String> claim(@RequestParam @NotBlank String taskId) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            String positionId = Y9LoginUserHolder.getPositionId();
            TaskModel task = taskApi.findById(Y9LoginUserHolder.getTenantId(), taskId);
            if (task != null) {
                String assigneeId = task.getAssignee();
                if (StringUtils.isBlank(assigneeId)) {
                    taskApi.claim(tenantId, positionId, taskId);
                    return Y9Result.successMsg("签收成功");
                } else {
                    String assigneeName = positionApi.get(tenantId, assigneeId).getData().getName();
                    return Y9Result.failure("任务已被用户:" + assigneeName + "签收！");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("签收失败");
    }

    /**
     * 流程办结
     *
     * @param taskId 任务id
     */
    @RequestMapping(value = "/complete")
    public Y9Result<String> complete(@RequestParam String taskId) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            String positionId = Y9LoginUserHolder.getPositionId();
            String userId = Y9LoginUserHolder.getPersonId();
            Person person = personApi.get(tenantId, userId).getData();
            Y9LoginUserHolder.setPerson(person);

            Position position = positionApi.get(tenantId, positionId).getData();
            Y9LoginUserHolder.setPosition(position);
            if (StringUtils.isNotBlank(taskId)) {
                buttonOperationService.complete(taskId, "办结", "已办结", "");
                return Y9Result.successMsg("办结成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("办结失败");
    }

    /**
     * 获取办件状态
     *
     * @param taskId            任务id
     * @param processInstanceId 流程实例id
     */
    @RequestMapping(value = "/getItemBox")
    public Y9Result<String> getItemBox(@RequestParam String taskId, @RequestParam String processInstanceId) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        String itembox = ItemBoxTypeEnum.TODO.getValue();
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            TaskModel taskModel = taskApi.findById(tenantId, taskId);
            HistoricProcessInstanceModel hpi = historicProcessApi.getById(tenantId, processInstanceId);
            if (taskModel != null && taskModel.getId() != null) {
                itembox = ItemBoxTypeEnum.TODO.getValue();
            } else {
                if (hpi != null && hpi.getEndTime() == null) {
                    itembox = ItemBoxTypeEnum.DOING.getValue();
                    List<TaskModel> taskList = taskApi.findByProcessInstanceId(tenantId, processInstanceId);
                    taskId = taskList.get(0).getId();
                    map.put("taskId", taskId);
                } else {
                    itembox = ItemBoxTypeEnum.DONE.getValue();
                }
            }
            return Y9Result.success(itembox, "获取成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 办理完成，并行处理时使用
     *
     * @param taskId 任务id
     */
    @SuppressWarnings("unchecked")
    @RequestMapping("/handleParallel")
    public Y9Result<String> handleParallel(@RequestParam String taskId) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            TaskModel task = taskApi.findById(tenantId, taskId);
            if (task == null) {
                return Y9Result.failure("该件已被办理！");
            } else {
                List<TaskModel> list = taskApi.findByProcessInstanceId(tenantId, task.getProcessInstanceId());
                if (list.size() == 1) {// 并行状态且不区分主协办时，多人同时打开办理页面，当其他人都已办理完成，最后一人需提示已是并行办理的最后一人，需刷新重新办理。
                    return Y9Result.failure("您是并行办理的最后一人，请刷新后重新办理。");
                } else {
                    /**
                     * 改变流程变量中users的值
                     */
                    try {
                        String userObj = variableApi.getVariable(tenantId, taskId, SysVariables.USERS);
                        List<String> users = userObj == null ? new ArrayList<>() : Y9JsonUtil.readValue(userObj, List.class);
                        if (users.size() == 0) {
                            List<String> usersTemp = new ArrayList<String>();
                            for (TaskModel t : list) {
                                usersTemp.add(t.getAssignee());
                            }
                            Map<String, Object> vmap = new HashMap<String, Object>(16);
                            vmap.put(SysVariables.USERS, usersTemp);
                            variableApi.setVariables(tenantId, taskId, vmap);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    taskApi.complete(tenantId, taskId);
                }
            }
            return Y9Result.successMsg("办理成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("办理失败");
    }

    /**
     * 处理完成，串行时使用
     *
     * @param taskId 任务id
     */
    @RequestMapping(value = "/handleSerial")
    public Y9Result<String> handleSerial(@RequestParam String taskId) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            String positionId = Y9LoginUserHolder.getPositionId();
            Position position = positionApi.get(tenantId, positionId).getData();
            Y9LoginUserHolder.setPosition(position);
            TaskModel task = taskApi.findById(tenantId, taskId);
            Map<String, Object> vars = task.getVariables();// 获取流程中当前任务的所有变量
            // vars.put(SysVariables.TASKSENDER, position.getName());
            // vars.put(SysVariables.TASKSENDERID, position.getId());
            taskApi.completeWithVariables(tenantId, task.getId(), vars);
            // List<TaskModel> taskNextList1 = taskApi.findByProcessInstanceId(tenantId,
            // task.getProcessInstanceId());
            // for (TaskModel taskNext : taskNextList1) {
            // Map<String, Object> vars1 = new HashMap<String, Object>(16);
            // vars1.put(SysVariables.TASKSENDER, position.getName());
            // vars1.put(SysVariables.TASKSENDERID, position.getId());
            // variableApi.setVariablesLocal(tenantId, taskNext.getId(), vars1);
            // }
            process4SearchService.saveToDataCenter(tenantId, taskId, task.getProcessInstanceId());
            return Y9Result.successMsg("办理成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("办理失败");
    }

    /**
     * 恢复待办
     *
     * @param processInstanceId 流程实例id
     * @param desc              描述
     */
    @RequestMapping(value = "/multipleResumeToDo")
    public Y9Result<String> multipleResumeToDo(@RequestParam(required = false) String processInstanceId, @RequestParam(required = false) String desc) {
        try {
            buttonOperationService.multipleResumeToDo(processInstanceId, desc);
            return Y9Result.successMsg("恢复待办成功");
        } catch (Exception e) {
            LOGGER.error("手机端恢复待办异常");
            e.printStackTrace();
        }
        return Y9Result.failure("恢复待办失败");
    }

    /**
     * 拒签：抢占式办理时，拒签就把自己从多个抢占办理的人中排除掉
     *
     * @param taskId                   任务id
     * @param isLastPerson4RefuseClaim 是否最后一人拒签
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/refuseClaim")
    public Y9Result<String> refuseClaim(@RequestParam String taskId, @RequestParam Boolean isLastPerson4RefuseClaim) {
        String activitiUser = "";
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            String positionId = Y9LoginUserHolder.getPositionId();
            String userId = Y9LoginUserHolder.getPersonId();
            TaskModel task = taskApi.findById(tenantId, taskId);
            if (isLastPerson4RefuseClaim) {// 最后一人拒签，退回
                try {
                    buttonOperation4PositionApi.refuseClaimRollback(tenantId, userId, taskId);
                } catch (Exception e) {
                    taskApi.unclaim(tenantId, taskId);// 失败则撤销签收
                    e.printStackTrace();
                    return Y9Result.failure("拒签失败");
                }
            } else {
                if (task != null) {
                    String assigneeId = task.getAssignee();
                    if (StringUtils.isBlank(assigneeId)) {
                        Map<String, Object> vars = variableApi.getVariables(tenantId, taskId);
                        ArrayList<String> users = (ArrayList<String>) vars.get(SysVariables.USERS);
                        for (Object obj : users) {
                            String user = obj.toString();
                            if (user.contains(positionId)) {
                                activitiUser = user;
                                break;
                            }
                        }
                        taskApi.deleteCandidateUser(tenantId, taskId, activitiUser);
                    } else {
                        String assigneeName = positionApi.get(tenantId, assigneeId).getData().getName();
                        return Y9Result.failure("任务已被用户:" + assigneeName + "签收！");
                    }
                }
            }
            return Y9Result.successMsg("拒签成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("拒签失败");
    }

    /**
     * 重定位
     *
     * @param taskId             任务id
     * @param repositionToTaskId 定位路由key
     * @param userChoice         人员id
     */
    @RequestMapping(value = "/reposition")
    public Y9Result<String> reposition(@RequestParam String taskId, @RequestParam String repositionToTaskId, @RequestParam String userChoice) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            String positionId = Y9LoginUserHolder.getPositionId();
            map.put(UtilConsts.SUCCESS, true);
            if (StringUtils.isNotBlank(taskId)) {
                specialOperationApi.reposition4Position(tenantId, positionId, taskId, repositionToTaskId, Y9Util.stringToList(userChoice, ","), "重定向", "");
            }
            return Y9Result.successMsg("重定向成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("重定向失败");
    }

    /**
     * 重定位
     *
     * @param taskId     任务id
     * @param userChoice 人员id
     */
    @RequestMapping(value = "/reposition1")
    public Y9Result<String> reposition1(@RequestParam String taskId, @RequestParam String userChoice) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            String positionId = Y9LoginUserHolder.getPositionId();
            TaskModel task = taskApi.findById(tenantId, taskId);
            buttonOperation4PositionApi.reposition(tenantId, positionId, taskId, "", Y9Util.stringToList(userChoice, ","), "重定向", "");
            process4SearchService.saveToDataCenter(tenantId, taskId, task.getProcessInstanceId());
            return Y9Result.successMsg("重定位成功");
        } catch (Exception e) {
            e.printStackTrace();
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "重定位失败");
        }
        return Y9Result.failure("重定位向失败");
    }

    /**
     * 退回
     *
     * @param taskId 任务id
     */
    @RequestMapping(value = "/rollback")
    public Y9Result<String> rollback(@RequestParam String taskId) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            String positionId = Y9LoginUserHolder.getPositionId();
            Position position = Y9LoginUserHolder.getPosition();
            TaskModel task = taskApi.findById(tenantId, taskId);
            List<TaskModel> taskList = taskApi.findByProcessInstanceId(tenantId, task.getProcessInstanceId());
            String type = processDefinitionApi.getNodeType(tenantId, task.getProcessDefinitionId(), task.getTaskDefinitionKey());
            String reason = "";
            if (SysVariables.PARALLEL.equals(type) && taskList.size() > 1) {// 并行退回，并行多于2人时，退回使用减签方式
                if (StringUtils.isEmpty(reason)) {
                    reason = "未填写。";
                }
                reason = "该任务由" + position.getName() + "退回:" + reason;
                Map<String, Object> val = new HashMap<String, Object>();
                val.put("val", reason);
                variableApi.setVariableLocal(tenantId, taskId, "rollBackReason", val);
                multiInstanceService.removeExecution(task.getExecutionId(), taskId, task.getAssignee());
            } else {
                buttonOperation4PositionApi.rollBack(tenantId, positionId, taskId, reason);
            }
            return Y9Result.successMsg("退回成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("退回失败");
    }

    /**
     * 返回发起人
     *
     * @param taskId 任务id
     */
    @RequestMapping(value = "/rollbackToStartor")
    public Y9Result<String> rollbackToStartor(@RequestParam String taskId) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            String positionId = Y9LoginUserHolder.getPositionId();
            buttonOperation4PositionApi.rollbackToStartor(tenantId, positionId, taskId, "");
            return Y9Result.successMsg("返回发起人成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("返回发起人失败");
    }

    /**
     * 返回发送人
     *
     * @param taskId 任务id
     */
    @RequestMapping(value = "/sendToSender")
    public Y9Result<String> sendToSender(@RequestParam String taskId) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            String positionId = Y9LoginUserHolder.getPositionId();
            buttonOperation4PositionApi.rollbackToSender(tenantId, positionId, taskId);
            return Y9Result.successMsg("返回发送人成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("返回发送人失败");
    }

    /**
     * 发送拟稿人
     *
     * @param taskId 任务id
     */
    @RequestMapping(value = "/sendToStartor")
    public Y9Result<String> sendToStartor(@RequestParam String taskId) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            String positionId = Y9LoginUserHolder.getPositionId();
            TaskModel taskModel = taskApi.findById(tenantId, taskId);
            String routeToTaskId = taskModel.getTaskDefinitionKey();
            String processInstanceId = taskModel.getProcessInstanceId();
            String processDefinitionKey = taskModel.getProcessDefinitionId().split(":")[0];
            ProcessParamModel processParamModel = processParamApi.findByProcessInstanceId(tenantId, processInstanceId);
            String itemId = processParamModel.getItemId();
            String processSerialNumber = processParamModel.getProcessSerialNumber();
            Map<String, Object> variables = new HashMap<String, Object>(16);

            String user = variableApi.getVariableLocal(tenantId, taskId, SysVariables.TASKSENDERID);
            String userChoice = "3:" + user;

            String multiInstance = processDefinitionApi.getNodeType(tenantId, taskModel.getProcessDefinitionId(), routeToTaskId);
            String sponsorHandle = "";
            if (multiInstance.equals(SysVariables.PARALLEL)) {
                sponsorHandle = "true";
            }
            document4PositionApi.saveAndForwarding(tenantId, positionId, processInstanceId, taskId, sponsorHandle, itemId, processSerialNumber, processDefinitionKey, userChoice, "", routeToTaskId, variables);
            return Y9Result.successMsg("发送拟稿人成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("发送拟稿人失败");
    }

    /**
     * 特殊办结
     *
     * @param taskId 任务id
     * @param reason 办结原因
     */
    @RequestMapping(value = "/specialComplete")
    public Y9Result<String> specialComplete(@RequestParam String taskId, @RequestParam(required = false) String reason) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            String positionId = Y9LoginUserHolder.getPositionId();
            Position position = Y9LoginUserHolder.getPosition();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            TaskModel taskModel = taskApi.findById(tenantId, taskId);
            buttonOperation4PositionApi.specialComplete(tenantId, positionId, taskId, reason);
            // 更新自定义历程结束时间
            List<ProcessTrackModel> ptModelList = processTrack4PositionApi.findByTaskId(tenantId, taskId);
            for (ProcessTrackModel ptModel : ptModelList) {
                if (StringUtils.isBlank(ptModel.getEndTime())) {
                    try {
                        processTrack4PositionApi.saveOrUpdate(tenantId, ptModel);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            /**
             * 3保存历程
             */
            ProcessTrackModel ptModel = new ProcessTrackModel();
            ptModel.setDescribed("已办结");
            ptModel.setProcessInstanceId(taskModel.getProcessInstanceId());
            ptModel.setReceiverName(position.getName());
            ptModel.setSenderName(position.getName());
            ptModel.setStartTime(sdf.format(new Date()));
            ptModel.setEndTime(sdf.format(new Date()));
            ptModel.setTaskDefName("特殊办结");
            ptModel.setTaskId(taskId);
            ptModel.setId("");
            processTrack4PositionApi.saveOrUpdate(tenantId, ptModel);
            return Y9Result.successMsg("特殊办结成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("特殊办结失败");
    }

    /**
     * 收回
     *
     * @param taskId 任务id
     */
    @RequestMapping(value = "/takeback")
    public Y9Result<String> takeback(@RequestParam String taskId) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            String positionId = Y9LoginUserHolder.getPositionId();
            buttonOperation4PositionApi.takeback(tenantId, positionId, taskId, "收回");
            return Y9Result.successMsg("收回成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("收回失败");
    }

    /**
     * 撤销签收：抢占式办理时，签收后，撤销签收可以让此公文重新抢占式办理
     *
     * @param taskId 任务id
     */
    @RequestMapping(value = "/unclaim")
    public Y9Result<String> unclaim(@RequestParam @NotBlank String taskId) {
        try {
            taskApi.unclaim(Y9LoginUserHolder.getTenantId(), taskId);
            return Y9Result.successMsg("撤销签收成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("撤销签收失败");
    }
}
