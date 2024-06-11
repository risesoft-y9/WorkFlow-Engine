package net.risesoft.controller.mobile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.api.itemadmin.position.ButtonOperation4PositionApi;
import net.risesoft.api.itemadmin.position.Document4PositionApi;
import net.risesoft.api.itemadmin.position.ProcessTrack4PositionApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.api.processadmin.*;
import net.risesoft.consts.UtilConsts;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.model.itemadmin.ProcessTrackModel;
import net.risesoft.model.platform.Person;
import net.risesoft.model.platform.Position;
import net.risesoft.model.processadmin.HistoricProcessInstanceModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.service.ButtonOperationService;
import net.risesoft.service.MultiInstanceService;
import net.risesoft.service.Process4SearchService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.util.Y9Util;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 菜单方法接口
 *
 * @author 10858
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/mobile/buttonOperation")
public class MobileButtonOperationController {

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
    protected Logger log = LoggerFactory.getLogger(MobileButtonOperationController.class);

    /**
     * 签收：抢占式办理时，签收后，其他人不可再签收办理
     *
     * @param tenantId   租户id
     * @param positionId 岗位id
     * @param taskId     任务id
     */
    @RequestMapping(value = "/claim")
    public void claim(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-positionId") String positionId, @RequestParam @NotBlank String taskId, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>(16);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            TaskModel task = taskApi.findById(tenantId, taskId);
            if (task != null) {
                String assigneeId = task.getAssignee();
                if (StringUtils.isBlank(assigneeId)) {
                    taskApi.claim(tenantId, positionId, taskId);
                    map.put(UtilConsts.SUCCESS, true);
                    map.put("msg", "签收成功");
                } else {
                    String assigneeName = positionApi.get(tenantId, assigneeId).getData().getName();
                    map.put(UtilConsts.SUCCESS, false);
                    map.put("msg", "任务已被用户:" + assigneeName + "签收！");
                }
            }
        } catch (Exception e) {
            LOGGER.error("签收失败", e);
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "签收失败");
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
    }

    /**
     * 流程办结
     *
     * @param tenantId   租户id
     * @param userId     人员id
     * @param positionId 岗位id
     * @param taskId     任务id
     */
    @RequestMapping(value = "/complete")
    public void complete(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @RequestParam @NotBlank String taskId, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>(16);
        Y9LoginUserHolder.setTenantId(tenantId);
        try {
            Person person = personApi.get(tenantId, userId).getData();
            Y9LoginUserHolder.setPerson(person);

            Position position = positionApi.get(tenantId, positionId).getData();
            Y9LoginUserHolder.setPosition(position);
            if (StringUtils.isNotBlank(taskId)) {
                buttonOperationService.complete(taskId, "办结", "已办结", "");
                map.put(UtilConsts.SUCCESS, true);
                map.put("msg", "办结成功");
            } else {
                map.put(UtilConsts.SUCCESS, false);
                map.put("msg", "办结失败");
            }
        } catch (Exception e) {
            LOGGER.error("办结失败", e);
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "办结失败");
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
    }

    /**
     * 获取办件状态
     *
     * @param tenantId          租户id
     * @param taskId            任务id
     * @param processInstanceId 流程实例id
     */
    @RequestMapping(value = "/getItemBox")
    public void getItemBox(@RequestHeader("auth-tenantId") String tenantId, @RequestParam String taskId, @RequestParam String processInstanceId,
                           HttpServletResponse response) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map = new HashMap<>(16);
        String itembox = ItemBoxTypeEnum.TODO.getValue();
        map.put("itembox", itembox);
        try {
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
            map.put("itembox", itembox);
        } catch (Exception e) {
            LOGGER.error("获取办件状态失败", e);
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
    }

    /**
     * 办理完成，并行处理时使用
     *
     * @param tenantId 租户id
     * @param taskId   任务id
     */
    @SuppressWarnings("unchecked")
    @RequestMapping("/handleParallel")
    public void handleParallel(@RequestHeader("auth-tenantId") String tenantId, @RequestParam @NotBlank String taskId, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>(16);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            map.put(UtilConsts.SUCCESS, true);
            map.put("msg", "办理成功");
            TaskModel task = taskApi.findById(tenantId, taskId);
            if (task == null) {
                map.put(UtilConsts.SUCCESS, false);
                map.put("msg", "该件已被办理！");
            } else {
                List<TaskModel> list = taskApi.findByProcessInstanceId(tenantId, task.getProcessInstanceId());
                if (list.size() == 1) {// 并行状态且不区分主协办时，多人同时打开办理页面，当其他人都已办理完成，最后一人需提示已是并行办理的最后一人，需刷新重新办理。
                    map.put("msg", "您是并行办理的最后一人，请刷新后重新办理。");
                } else {
                    /*
                      改变流程变量中users的值
                     */
                    try {
                        String userObj = variableApi.getVariable(tenantId, taskId, SysVariables.USERS);
                        List<String> users = userObj == null ? new ArrayList<>() : Y9JsonUtil.readValue(userObj, List.class);
                        if (users != null && users.isEmpty()) {
                            List<String> usersTemp = new ArrayList<>();
                            for (TaskModel t : list) {
                                usersTemp.add(t.getAssignee());
                            }
                            Map<String, Object> vmap = new HashMap<>(16);
                            vmap.put(SysVariables.USERS, usersTemp);
                            variableApi.setVariables(tenantId, taskId, vmap);
                        }
                    } catch (Exception e) {
                        LOGGER.error("改变流程变量中users的值失败", e);
                    }
                    taskApi.complete(tenantId, taskId);
                }
            }
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "办理失败");
            LOGGER.error("办理失败", e);
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
    }

    /**
     * 处理完成，串行时使用
     *
     * @param tenantId   租户id
     * @param positionId 岗位id
     * @param taskId     任务id
     */
    @RequestMapping(value = "/handleSerial")
    public void handleSerial(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-positionId") String positionId, @RequestParam @NotBlank String taskId, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>(16);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Position position = positionApi.get(tenantId, positionId).getData();
            Y9LoginUserHolder.setPosition(position);
            TaskModel task = taskApi.findById(tenantId, taskId);
            Map<String, Object> vars = task.getVariables();// 获取流程中当前任务的所有变量
            taskApi.completeWithVariables(tenantId, task.getId(), vars);
            process4SearchService.saveToDataCenter(tenantId, taskId, task.getProcessInstanceId());
            map.put(UtilConsts.SUCCESS, true);
            map.put("msg", "办理成功!");
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, true);
            map.put("msg", "办理失败!");
            LOGGER.error("办理失败", e);
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
    }

    /**
     * 恢复待办
     *
     * @param tenantId          租户id
     * @param positionId        岗位id
     * @param processInstanceId 流程实例id
     * @param desc              描述
     */
    @RequestMapping(value = "/multipleResumeToDo")
    public void multipleResumeToDo(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-positionId") String positionId, @RequestParam @NotBlank String processInstanceId, @RequestParam String desc, HttpServletResponse response) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionApi.get(tenantId, positionId).getData();
        Y9LoginUserHolder.setPosition(position);
        Map<String, Object> map = new HashMap<>(16);
        try {
            buttonOperationService.multipleResumeToDo(processInstanceId, desc);
            map.put(UtilConsts.SUCCESS, true);
            map.put("msg", "恢复待办成功");
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "恢复待办失败");
            LOGGER.error("恢复待办失败", e);
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
    }

    /**
     * 拒签：抢占式办理时，拒签就把自己从多个抢占办理的人中排除掉
     *
     * @param tenantId                 租户id
     * @param userId                   人员id
     * @param positionId               岗位id
     * @param taskId                   任务id
     * @param isLastPerson4RefuseClaim 是否最后一人拒签
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/refuseClaim")
    public void refuseClaim(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @RequestParam @NotBlank String taskId, @RequestParam Boolean isLastPerson4RefuseClaim, HttpServletResponse response) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map = new HashMap<>(16);
        String activitiUser = "";
        try {
            TaskModel task = taskApi.findById(tenantId, taskId);
            map.put(UtilConsts.SUCCESS, true);
            map.put("msg", "拒签成功");
            if (isLastPerson4RefuseClaim) {// 最后一人拒签，退回
                try {
                    buttonOperation4PositionApi.refuseClaimRollback(tenantId, userId, taskId);
                } catch (Exception e) {
                    taskApi.unclaim(tenantId, taskId);// 失败则撤销签收
                    map.put(UtilConsts.SUCCESS, false);
                    map.put("msg", "拒签失败");
                    LOGGER.error("拒签失败", e);
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
                        map.put(UtilConsts.SUCCESS, false);
                        map.put("msg", "任务已被用户:" + assigneeName + "签收！");
                    }
                }
            }
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "拒签失败");
            LOGGER.error("拒签失败", e);
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
    }

    /**
     * 重定位
     *
     * @param tenantId           租户id
     * @param positionId         岗位id
     * @param taskId             任务id
     * @param repositionToTaskId 定位路由key
     * @param userChoice         人员id
     */
    @RequestMapping(value = "/reposition")
    public void reposition(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-positionId") String positionId, @RequestParam @NotBlank String taskId, @RequestParam @NotBlank String repositionToTaskId,
                           @RequestParam @NotBlank String userChoice, HttpServletResponse response) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map = new HashMap<>(16);
        try {
            map.put(UtilConsts.SUCCESS, true);
            if (StringUtils.isNotBlank(taskId)) {
                specialOperationApi.reposition4Position(tenantId, positionId, taskId, repositionToTaskId, Y9Util.stringToList(userChoice, ","), "重定向", "");
                map.put("msg", "重定向成功");
            } else {
                map.put(UtilConsts.SUCCESS, false);
                map.put("msg", "重定向失败");
            }
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "重定向失败");
            LOGGER.error("重定位失败", e);
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
    }

    /**
     * 重定位
     *
     * @param tenantId   租户id
     * @param positionId 岗位id
     * @param taskId     任务id
     * @param userChoice 人员id
     */
    @RequestMapping(value = "/reposition1")
    public void reposition1(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-positionId") String positionId, @RequestParam @NotBlank String taskId, @RequestParam @NotBlank String userChoice,
                            HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>(16);
        Y9LoginUserHolder.setTenantId(tenantId);
        try {
            TaskModel task = taskApi.findById(tenantId, taskId);
            buttonOperation4PositionApi.reposition(tenantId, positionId, taskId, "", Y9Util.stringToList(userChoice, ","), "重定向", "");
            process4SearchService.saveToDataCenter(tenantId, taskId, task.getProcessInstanceId());
        } catch (Exception e) {
            LOGGER.error("重定位失败", e);
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "重定位失败");
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
    }

    /**
     * 退回
     *
     * @param tenantId   租户id
     * @param positionId 岗位id
     * @param taskId     任务id
     */
    @RequestMapping(value = "/rollback")
    public void rollback(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-positionId") String positionId, @RequestParam @NotBlank String taskId, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>(16);
        map.put(UtilConsts.SUCCESS, true);
        map.put("msg", "退回成功");
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Position position = positionApi.get(tenantId, positionId).getData();
            Y9LoginUserHolder.setPosition(position);
            TaskModel task = taskApi.findById(tenantId, taskId);
            List<TaskModel> taskList = taskApi.findByProcessInstanceId(tenantId, task.getProcessInstanceId());
            String type = processDefinitionApi.getNodeType(tenantId, task.getProcessDefinitionId(), task.getTaskDefinitionKey());
            String reason = "";
            if (SysVariables.PARALLEL.equals(type) && taskList.size() > 1) {// 并行退回，并行多于2人时，退回使用减签方式
                if (StringUtils.isEmpty(reason)) {
                    reason = "未填写。";
                }
                reason = "该任务由" + position.getName() + "退回:" + reason;
                Map<String, Object> val = new HashMap<>();
                val.put("val", reason);
                variableApi.setVariableLocal(tenantId, taskId, "rollBackReason", val);
                multiInstanceService.removeExecution(task.getExecutionId(), taskId, task.getAssignee());
            } else {
                buttonOperation4PositionApi.rollBack(tenantId, positionId, taskId, reason);
            }
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "退回失败");
            LOGGER.error("退回失败", e);
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
    }

    /**
     * 返回发起人
     *
     * @param tenantId   租户id
     * @param positionId 岗位id
     * @param taskId     任务id
     */
    @RequestMapping(value = "/rollbackToStartor")
    public void rollbackToStartor(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-positionId") String positionId, @RequestParam @NotBlank String taskId, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>(16);
        map.put(UtilConsts.SUCCESS, false);
        map.put("msg", "返回发起人失败");
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            buttonOperation4PositionApi.rollbackToStartor(tenantId, positionId, taskId, "");
            map.put(UtilConsts.SUCCESS, true);
            map.put("msg", "返回发起人成功");
        } catch (Exception e) {
            LOGGER.error("返回发起人失败", e);
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
    }

    /**
     * 返回发送人
     *
     * @param tenantId   租户id
     * @param positionId 岗位id
     * @param taskId     任务id
     */
    @RequestMapping(value = "/sendToSender")
    public void sendToSender(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-positionId") String positionId, @RequestParam @NotBlank String taskId, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>(16);
        map.put(UtilConsts.SUCCESS, false);
        map.put("msg", "返回发送人失败");
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            buttonOperation4PositionApi.rollbackToSender(tenantId, positionId, taskId);
            map.put(UtilConsts.SUCCESS, true);
            map.put("msg", "返回发送人成功");
        } catch (Exception e) {
            LOGGER.error("返回发送人失败", e);
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
    }

    /**
     * 发送拟稿人
     *
     * @param tenantId   租户id
     * @param positionId 岗位id
     * @param taskId     任务id
     */
    @RequestMapping(value = "/sendToStartor")
    public void sendToStartor(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-positionId") String positionId, @RequestParam @NotBlank String taskId, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>(16);
        map.put(UtilConsts.SUCCESS, false);
        map.put("msg", "发送拟稿人失败");
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            TaskModel taskModel = taskApi.findById(tenantId, taskId);
            String routeToTaskId = taskModel.getTaskDefinitionKey();
            String processInstanceId = taskModel.getProcessInstanceId();
            String processDefinitionKey = taskModel.getProcessDefinitionId().split(":")[0];
            ProcessParamModel processParamModel = processParamApi.findByProcessInstanceId(tenantId, processInstanceId);
            String itemId = processParamModel.getItemId();
            String processSerialNumber = processParamModel.getProcessSerialNumber();
            Map<String, Object> variables = new HashMap<>(16);

            String user = variableApi.getVariableLocal(tenantId, taskId, SysVariables.TASKSENDERID);
            String userChoice = "3:" + user;

            String multiInstance = processDefinitionApi.getNodeType(tenantId, taskModel.getProcessDefinitionId(), routeToTaskId);
            String sponsorHandle = "";
            if (multiInstance.equals(SysVariables.PARALLEL)) {
                sponsorHandle = "true";
            }
            document4PositionApi.saveAndForwarding(tenantId, positionId, processInstanceId, taskId, sponsorHandle, itemId, processSerialNumber, processDefinitionKey, userChoice, "", routeToTaskId, variables);
            map.put(UtilConsts.SUCCESS, true);
            map.put("msg", "发送拟稿人成功");
        } catch (Exception e) {
            LOGGER.error("发送拟稿人失败", e);
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
    }

    /**
     * 特殊办结
     *
     * @param tenantId   租户id
     * @param userId     人员id
     * @param positionId 岗位id
     * @param taskId     任务id
     * @param reason     办结原因
     */
    @RequestMapping(value = "/specialComplete")
    public void specialComplete(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @RequestParam @NotBlank String taskId, @RequestParam String reason,
                                HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>(16);
        Y9LoginUserHolder.setTenantId(tenantId);
        try {
            Person person = personApi.get(tenantId, userId).getData();
            Y9LoginUserHolder.setPerson(person);

            Position position = positionApi.get(tenantId, positionId).getData();
            Y9LoginUserHolder.setPosition(position);

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
                        LOGGER.error("更新自定义历程结束时间失败", e);
                    }
                }
            }
            /*
              3保存历程
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
            map.put(UtilConsts.SUCCESS, true);
            map.put("msg", "特殊办结成功");
        } catch (Exception e) {
            LOGGER.error("特殊办结失败", e);
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "特殊办结失败");
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
    }

    /**
     * 收回
     *
     * @param tenantId   租户id
     * @param positionId 岗位id
     * @param taskId     任务id
     */
    @RequestMapping(value = "/takeback")
    public void takeback(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-positionId") String positionId, @RequestParam @NotBlank String taskId, HttpServletResponse response) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map = new HashMap<>(16);
        try {
            map.put(UtilConsts.SUCCESS, true);
            map.put("msg", "收回成功");
            buttonOperation4PositionApi.takeback(tenantId, positionId, taskId, "收回");
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "收回失败");
            LOGGER.error("收回失败", e);
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
    }

    /**
     * 撤销签收：抢占式办理时，签收后，撤销签收可以让此公文重新抢占式办理
     *
     * @param tenantId 租户id
     * @param taskId   任务id
     */
    @RequestMapping(value = "/unclaim")
    public void unclaim(@RequestHeader("auth-tenantId") String tenantId, @RequestParam @NotBlank String taskId, HttpServletResponse response) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map = new HashMap<>(16);
        try {
            map.put(UtilConsts.SUCCESS, true);
            if (StringUtils.isNotBlank(taskId)) {
                taskApi.unclaim(tenantId, taskId);
                map.put("msg", "撤销签收成功");
            } else {
                map.put(UtilConsts.SUCCESS, false);
                map.put("msg", "撤销签收失败");
            }
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "撤销签收失败");
            LOGGER.error("撤销签收失败", e);
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
    }
}
