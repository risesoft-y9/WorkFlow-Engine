package net.risesoft.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.api.itemadmin.SpeakInfoApi;
import net.risesoft.api.itemadmin.TransactionWordApi;
import net.risesoft.api.itemadmin.position.*;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.api.platform.permission.PositionRoleApi;
import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.ProcessTodoApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.model.itemadmin.ItemModel;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.model.platform.Position;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.ButtonOperationService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.configuration.Y9Properties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import javax.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 发送，办结相关
 *
 * @author zhangchongjie
 * @date 2024/06/05
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/vue/document")
public class DocumentRestController {

    private final Item4PositionApi item4PositionApi;

    private final Document4PositionApi document4PositionApi;

    private final ButtonOperationService buttonOperationService;

    private final ProcessParamApi processParamApi;

    private final Attachment4PositionApi attachment4PositionApi;

    private final TransactionWordApi transactionWordApi;

    private final ChaoSong4PositionApi chaoSong4PositionApi;

    private final TaskApi taskApi;

    private final ProcessDefinitionApi processDefinitionApi;

    private final PositionApi positionApi;

    private final PositionRoleApi positionRoleApi;

    private final SpeakInfoApi speakInfoApi;

    private final AssociatedFile4PositionApi associatedFile4PositionApi;

    private final OfficeFollow4PositionApi officeFollow4PositionApi;

    private final Y9Properties y9Config;

    private final ProcessTodoApi processTodoApi;

    /**
     * 获取新建公文数据
     *
     * @param itemId 事项id
     * @return Y9Result<Map < String, Object>>
     */
    @RequestMapping(value = "/add", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Map<String, Object>> add(@RequestParam @NotBlank String itemId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Map<String, Object> map;
        try {
            map = document4PositionApi.add(tenantId, Y9LoginUserHolder.getPositionId(), itemId, false);
            map.put("tenantId", tenantId);
            map.put("userId", Y9LoginUserHolder.getPositionId());
            map.put("userName", Y9LoginUserHolder.getPosition().getName());
            map.put("itemAdminBaseURL", y9Config.getCommon().getItemAdminBaseUrl());
            map.put("jodconverterURL", y9Config.getCommon().getJodconverterBaseUrl());
            map.put("flowableUIBaseURL", y9Config.getCommon().getFlowableBaseUrl());
            return Y9Result.success(map, "获取成功");
        } catch (Exception e) {
            LOGGER.error("获取新建公文数据失败", e);
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 流程办结
     *
     * @param taskId    任务id
     * @param infoOvert 办结数据是否在数据中心公开
     * @return Y9Result<String>
     */
    @RequestMapping(value = "/complete", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> complete(@RequestParam @NotBlank String taskId, @RequestParam String infoOvert) {
        try {
            buttonOperationService.complete(taskId, "办结", "已办结", infoOvert);
            return Y9Result.successMsg("办结成功");
        } catch (Exception e) {
            LOGGER.error("流程办结失败", e);
        }
        return Y9Result.failure("办结失败");
    }

    /**
     * 获取编辑文档数据
     *
     * @param itembox           文档状态
     * @param taskId            任务id
     * @param processInstanceId 流程实例id
     * @param itemId            事项id
     * @return Y9Result<Map < String, Object>>
     */
    @RequestMapping(value = "/edit", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Map<String, Object>> edit(@RequestParam @NotBlank String itembox, @RequestParam String taskId, @RequestParam @NotBlank String processInstanceId, @RequestParam @NotBlank String itemId) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId(), userId = person.getPersonId();
        String monitor = itembox;
        if (itembox.equals("monitorDone") || itembox.equals("monitorRecycle")) {
            itembox = ItemBoxTypeEnum.DONE.getValue();
        }
        try {
            Map<String, Object> map = document4PositionApi.edit(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPositionId(), itembox, taskId, processInstanceId, itemId, false);
            String processSerialNumber = (String) map.get("processSerialNumber");
            Integer fileNum = attachment4PositionApi.fileCounts(tenantId, processSerialNumber);
            int docNum = 0;
            // 是否正文正常
            Map<String, Object> wordMap = transactionWordApi.findWordByProcessSerialNumber(tenantId, processSerialNumber);
            if (!wordMap.isEmpty()) {
                docNum = 1;
            }
            int speakInfoNum = speakInfoApi.getNotReadCount(tenantId, userId, processInstanceId);
            int associatedFileNum = associatedFile4PositionApi.countAssociatedFile(tenantId, processSerialNumber);
            map.put("speakInfoNum", speakInfoNum);
            map.put("associatedFileNum", associatedFileNum);
            map.put("docNum", docNum);
            map.put("monitor", monitor);
            map.put("fileNum", fileNum);
            map.put("userName", Y9LoginUserHolder.getPosition().getName());
            map.put("tenantId", tenantId);
            map.put("userId", userId);
            map.put("itemAdminBaseURL", y9Config.getCommon().getItemAdminBaseUrl());
            map.put("jodconverterURL", y9Config.getCommon().getJodconverterBaseUrl());
            map.put("flowableUIBaseURL", y9Config.getCommon().getFlowableBaseUrl());
            int follow = officeFollow4PositionApi.countByProcessInstanceId(tenantId, Y9LoginUserHolder.getPositionId(), processInstanceId);
            map.put("follow", follow > 0);
            return Y9Result.success(map, "获取成功");
        } catch (Exception e) {
            LOGGER.error("获取编辑文档数据失败", e);
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 办件发送
     *
     * @param itemId               事项id
     * @param sponsorHandle        是否主办办理
     * @param processInstanceId    流程实例id
     * @param taskId               任务id
     * @param processDefinitionKey 流程定义key
     * @param processSerialNumber  流程编号
     * @param userChoice           收件人
     * @param sponsorGuid          主办人id
     * @param routeToTaskId        发送路由，任务key
     * @param isSendSms            是否短信提醒
     * @param isShuMing            是否署名
     * @param smsContent           短信内容
     * @return Y9Result<Map < String, Object>>
     */
    @RequestMapping(value = "/forwarding", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<Map<String, Object>> forwarding(@RequestParam @NotBlank String itemId, @RequestParam @NotBlank String sponsorHandle, @RequestParam String processInstanceId, @RequestParam String taskId, @RequestParam @NotBlank String processDefinitionKey,
                                                    @RequestParam @NotBlank String processSerialNumber, @RequestParam @NotBlank String userChoice, @RequestParam String sponsorGuid, @RequestParam @NotBlank String routeToTaskId, @RequestParam String isSendSms, @RequestParam String isShuMing, @RequestParam String smsContent) {
        Map<String, Object> map;
        Map<String, Object> variables = new HashMap<>(16);
        try {
            ProcessParamModel processParamModel = processParamApi.findByProcessSerialNumber(Y9LoginUserHolder.getTenantId(), processSerialNumber);
            processParamModel.setIsSendSms(isSendSms);
            processParamModel.setIsShuMing(isShuMing);
            processParamModel.setSmsContent(smsContent);
            processParamModel.setSmsPersonId("");
            processParamApi.saveOrUpdate(Y9LoginUserHolder.getTenantId(), processParamModel);
            map = document4PositionApi.saveAndForwarding(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPositionId(), processInstanceId, taskId, sponsorHandle, itemId, processSerialNumber, processDefinitionKey, userChoice, sponsorGuid, routeToTaskId, variables);
            if ((Boolean) map.get(UtilConsts.SUCCESS)) {
                return Y9Result.success(map, (String) map.get("msg"));
            } else {
                return Y9Result.failure((String) map.get("msg"));
            }
        } catch (Exception e) {
            LOGGER.error("发送失败", e);
        }
        return Y9Result.failure("发送失败，发生异常");
    }

    /**
     * 获取事项列表
     *
     * @return Y9Result<Map < String, Object>>
     */
    @RequestMapping(value = "/getItemList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Map<String, Object>> getItemList() {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Map<String, Object> map = new HashMap<>(16);
        try {
            List<Map<String, Object>> listMap;
            listMap = item4PositionApi.getItemList(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPositionId());
            map.put("itemMap", listMap);
            map.put("notReadCount", chaoSong4PositionApi.getTodoCount(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPositionId()));
            // int followCount = officeFollow4PositionApi.getFollowCount(tenantId, Y9LoginUserHolder.getPositionId());
            // map.put("followCount", followCount);
            // 公共角色
            boolean b = positionRoleApi.hasRole(tenantId, "Y9OrgHierarchyManagement", null, "监控管理员角色", Y9LoginUserHolder.getPositionId()).getData();
            map.put("monitorManage", b);

            boolean b1 = positionRoleApi.hasRole(tenantId, "itemAdmin", "", "人事统计角色", Y9LoginUserHolder.getPositionId()).getData();
            map.put("leaveManage", b1);

            return Y9Result.success(map, "获取成功");
        } catch (Exception e) {
            LOGGER.error("获取事项列表失败", e);
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 获取事项系统列表
     *
     * @return Y9Result<Map < String, Object>>
     */
    @RequestMapping(value = "/getItemSystemList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Map<String, Object>> getItemSystemList() {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Map<String, Object> map = new HashMap<>(16);
        try {
            String positionId = Y9LoginUserHolder.getPositionId();
            List<Map<String, Object>> list = new ArrayList<>();
            List<ItemModel> listMap = item4PositionApi.getAllItem(Y9LoginUserHolder.getTenantId());
            for (ItemModel itemModel : listMap) {
                Map<String, Object> newmap = new HashMap<>(16);
                newmap.put("systemName", itemModel.getSystemName());
                newmap.put("systemCnName", itemModel.getSysLevel());
                if (!list.contains(newmap)) {
                    list.add(newmap);
                }
            }
            for (Map<String, Object> nmap : list) {
                long todoCount = processTodoApi.getTodoCountByUserIdAndSystemName(tenantId, positionId, (String) nmap.get("systemName"));
                nmap.put("todoCount", todoCount);
                List<ItemModel> itemList = new ArrayList<>();
                for (ItemModel itemModel : listMap) {
                    if (nmap.get("systemName").equals(itemModel.getSystemName())) {
                        itemList.add(itemModel);
                    }
                }
                nmap.put("itemList", itemList);
            }

            map.put("systemList", list);
            map.put("notReadCount", chaoSong4PositionApi.getTodoCount(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPositionId()));
            // 公共角色
            boolean b = positionRoleApi.hasRole(tenantId, "Y9OrgHierarchyManagement", null, "监控管理员角色", Y9LoginUserHolder.getPositionId()).getData();
            map.put("monitorManage", b);

            boolean b1 = false;
            map.put("leaveManage", b1);

            return Y9Result.success(map, "获取成功");
        } catch (Exception e) {
            LOGGER.error("获取事项系统列表失败", e);
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 查询协办人员办理情况
     *
     * @param taskId 任务id
     * @return Y9Result<Map < String, Object>>
     */
    @RequestMapping(value = "/getParallelNames", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Map<String, Object>> getParallelNames(@RequestParam @NotBlank String taskId) {
        String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
        String parallelDoing = "";
        Map<String, Object> map = new HashMap<>(16);
        map.put("isParallel", false);
        int i = 0;
        List<TaskModel> list = null;
        try {
            TaskModel taskModel = taskApi.findById(tenantId, taskId);
            String multiInstance = processDefinitionApi.getNodeType(tenantId, taskModel.getProcessDefinitionId(), taskModel.getTaskDefinitionKey());
            if (multiInstance.equals(SysVariables.PARALLEL)) {// 并行
                map.put("isParallel", true);
                list = taskApi.findByProcessInstanceId(tenantId, taskModel.getProcessInstanceId(), true);
                for (TaskModel task : list) {
                    if (i < 5) {
                        String assigneeId = task.getAssignee();
                        Position employee = positionApi.get(tenantId, assigneeId).getData();
                        if (employee != null && !employee.getId().equals(positionId)) { // 协办人员
                            if (StringUtils.isBlank(parallelDoing)) {
                                parallelDoing = employee.getName();
                            } else {
                                parallelDoing = parallelDoing + "、" + employee.getName();
                            }
                            i++;
                        }
                    }
                }
            }
            map.put("parallelDoing", parallelDoing);
            map.put("count", list != null ? list.size() - 1 : 0);// 减去主办任务数
            return Y9Result.success(map, "获取成功");
        } catch (Exception e) {
            LOGGER.error("获取协办人员办理情况失败", e);
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 恢复待办
     *
     * @param processInstanceIds 流程实例ids ，逗号隔开
     * @param desc               原因
     * @return Y9Result<String>
     */
    @RequestMapping(value = "/multipleResumeToDo", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> multipleResumeToDo(@RequestParam @NotBlank String processInstanceIds, @RequestParam String desc) {
        try {
            buttonOperationService.multipleResumeToDo(processInstanceIds, desc);
            return Y9Result.successMsg("恢复成功");
        } catch (Exception e) {
            LOGGER.error("恢复待办失败", e);
        }
        return Y9Result.failure("恢复失败");
    }

    /**
     * 获取签收任务配置,判断是否直接发送
     *
     * @param itemId              事项id
     * @param processDefinitionId 流程定义id
     * @param taskDefinitionKey   任务key
     * @param processSerialNumber 流程编号
     * @return Y9Result<Map < String, Object>>
     */
    @RequestMapping(value = "/signTaskConfig", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Map<String, Object>> signTaskConfig(@RequestParam @NotBlank String itemId, @RequestParam @NotBlank String processDefinitionId, @RequestParam @NotBlank String taskDefinitionKey, @RequestParam @NotBlank String processSerialNumber) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Map<String, Object> map;
        try {
            map = document4PositionApi.signTaskConfig(tenantId, Y9LoginUserHolder.getPositionId(), itemId, processDefinitionId, taskDefinitionKey, processSerialNumber);
            if ((Boolean) map.get(UtilConsts.SUCCESS)) {
                return Y9Result.success(map, (String) map.get("msg"));
            } else {
                return Y9Result.failure((String) map.get("msg"));
            }
        } catch (Exception e) {
            LOGGER.error("获取签收任务配置失败", e);
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 办件发送
     *
     * @param itemId              事项id
     * @param taskId              任务id
     * @param processSerialNumber 流程编号
     * @return Y9Result<Map < String, Object>>
     */
    @RequestMapping(value = "/submitTo", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<Map<String, Object>> submitTo(@RequestParam @NotBlank String itemId, @RequestParam String taskId, @RequestParam @NotBlank String processSerialNumber) {
        Map<String, Object> map;
        try {
            map = document4PositionApi.saveAndSubmitTo(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPositionId(), taskId, itemId, processSerialNumber);
            if ((Boolean) map.get(UtilConsts.SUCCESS)) {
                return Y9Result.success(map, (String) map.get("msg"));
            } else {
                return Y9Result.failure((String) map.get("msg"));
            }
        } catch (Exception e) {
            LOGGER.error("发送失败，发生异常", e);
        }
        return Y9Result.failure("发送失败，发生异常");
    }

    /**
     * 获取用户选取界面数据
     *
     * @param itemId              事项id
     * @param routeToTask         任务路由
     * @param processDefinitionId 流程定义id
     * @param taskId              任务id
     * @param processInstanceId   流程实例id
     * @return Y9Result<Map < String, Object>>
     */
    @RequestMapping(value = "/userChoiseData", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Map<String, Object>> userChoiseData(@RequestParam @NotBlank String itemId, @RequestParam @NotBlank String routeToTask, @RequestParam @NotBlank String processDefinitionId, @RequestParam String taskId, @RequestParam String processInstanceId) {
        try {
            Map<String, Object> map = document4PositionApi.docUserChoise(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPersonId(), Y9LoginUserHolder.getPositionId(), itemId, "", processDefinitionId, taskId, routeToTask, processInstanceId);
            map.put("userName", Y9LoginUserHolder.getPosition().getName());
            return Y9Result.success(map, "获取成功");
        } catch (Exception e) {
            LOGGER.error("获取失败，发生异常", e);
        }
        return Y9Result.failure("获取失败");
    }

}
