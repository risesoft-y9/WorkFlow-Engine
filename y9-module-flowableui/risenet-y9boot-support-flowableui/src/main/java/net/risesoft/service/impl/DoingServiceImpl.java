package net.risesoft.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.ChaoSongApi;
import net.risesoft.api.itemadmin.OfficeFollowApi;
import net.risesoft.api.itemadmin.RemindInstanceApi;
import net.risesoft.api.itemadmin.SpeakInfoApi;
import net.risesoft.api.itemadmin.core.ItemApi;
import net.risesoft.api.itemadmin.core.ProcessParamApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.processadmin.IdentityApi;
import net.risesoft.api.processadmin.ProcessDoingApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.consts.processadmin.SysVariables;
import net.risesoft.model.itemadmin.RemindInstanceModel;
import net.risesoft.model.itemadmin.core.ItemModel;
import net.risesoft.model.itemadmin.core.ProcessParamModel;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.model.processadmin.IdentityLinkModel;
import net.risesoft.model.processadmin.ProcessInstanceModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.service.DoingService;
import net.risesoft.service.HandleFormDataService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9Util;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class DoingServiceImpl implements DoingService {

    private final ProcessDoingApi processDoingApi;

    private final TaskApi taskApi;

    private final ItemApi itemApi;

    private final OrgUnitApi orgUnitApi;

    private final ProcessParamApi processParamApi;

    private final ChaoSongApi chaoSongApi;

    private final HandleFormDataService handleFormDataService;

    private final SpeakInfoApi speakInfoApi;

    private final RemindInstanceApi remindInstanceApi;

    private final OfficeFollowApi officeFollowApi;

    private final IdentityApi identityApi;

    @Override
    public Y9Page<Map<String, Object>> list(String itemId, String searchTerm, Integer page, Integer rows) {
        Y9Page<ProcessInstanceModel> piPage;
        try {
            List<Map<String, Object>> items = new ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String positionId = Y9LoginUserHolder.getPositionId(), tenantId = Y9LoginUserHolder.getTenantId();
            ItemModel item = this.itemApi.getByItemId(tenantId, itemId).getData();
            String processDefinitionKey = item.getWorkflowGuid(), itemName = item.getName();
            List<String> processSerialNumbers = new ArrayList<>();
            if (StringUtils.isBlank(searchTerm)) {
                piPage = this.processDoingApi.getListByUserIdAndProcessDefinitionKeyOrderBySendTime(tenantId,
                    positionId, processDefinitionKey, page, rows);
                List<ProcessInstanceModel> hpiModelList = piPage.getRows();
                int serialNumber = (page - 1) * rows;
                Map<String, Object> mapTemp;
                ProcessParamModel processParam;
                String processDefinitionId = "", processInstanceId = "", processSerialNumber = "", documentTitle,
                    level = "", number = "";
                for (ProcessInstanceModel piModel : hpiModelList) {// 以办理时间排序
                    mapTemp = new HashMap<>(16);
                    try {
                        processInstanceId = piModel.getId();
                        processDefinitionId = piModel.getProcessDefinitionId();
                        Date endTime = piModel.getEndTime();
                        String taskCreateTime = piModel.getEndTime() != null ? sdf.format(endTime) : "";
                        List<TaskModel> taskList =
                            this.taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                        List<String> listTemp = this.getAssigneeIdsAndAssigneeNames(taskList);
                        String taskIds = listTemp.get(0);
                        String assigneeNames = listTemp.get(2);
                        Boolean isReminder = String.valueOf(taskList.get(0).getPriority()).contains("5");
                        processParam =
                            this.processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                        if (null == processParam) {
                            documentTitle = "流程参数表数据不存在";
                        } else {
                            documentTitle =
                                StringUtils.isBlank(processParam.getTitle()) ? "无标题" : processParam.getTitle();
                            level = processParam.getCustomLevel();
                            number = processParam.getCustomNumber();
                            processSerialNumber = processParam.getProcessSerialNumber();
                            processSerialNumbers.add(processSerialNumber);
                        }
                        mapTemp.put(SysVariables.LEVEL, level);
                        mapTemp.put(SysVariables.NUMBER, number);
                        mapTemp.put("itemName", itemName);
                        mapTemp.put("processDefinitionKey", processDefinitionKey);
                        mapTemp.put(SysVariables.PROCESS_SERIAL_NUMBER, processSerialNumber);
                        mapTemp.put("processDefinitionId", processDefinitionId);
                        mapTemp.put(SysVariables.DOCUMENT_TITLE, documentTitle);
                        mapTemp.put("taskDefinitionKey", taskList.get(0).getTaskDefinitionKey());
                        mapTemp.put("taskName", taskList.get(0).getName());
                        mapTemp.put("taskCreateTime", taskCreateTime);
                        mapTemp.put("taskId", taskIds);
                        mapTemp.put("taskAssignee", assigneeNames);
                        mapTemp.put(SysVariables.ITEM_ID, itemId);
                        mapTemp.put("isReminder", isReminder);
                        int chaosongNum =
                            this.chaoSongApi.countByUserIdAndProcessInstanceId(tenantId, positionId, processInstanceId)
                                .getData();
                        mapTemp.put("chaosongNum", chaosongNum);
                        mapTemp.put("status", 1);
                        mapTemp.put("taskDueDate", "");
                        mapTemp.put("processInstanceId", processInstanceId);
                        int speakInfoNum = this.speakInfoApi
                            .getNotReadCount(tenantId, Y9LoginUserHolder.getPersonId(), processInstanceId)
                            .getData();
                        mapTemp.put("speakInfoNum", speakInfoNum);

                        mapTemp.put("remindSetting", false);
                        RemindInstanceModel remindInstanceModel = this.remindInstanceApi
                            .getRemindInstance(tenantId, Y9LoginUserHolder.getPersonId(), processInstanceId)
                            .getData();
                        if (remindInstanceModel != null) {// 流程实例是否设置消息提醒
                            mapTemp.put("remindSetting", true);
                        }
                        int countFollow =
                            this.officeFollowApi.countByProcessInstanceId(tenantId, positionId, processInstanceId)
                                .getData();
                        mapTemp.put("follow", countFollow > 0);
                    } catch (Exception e) {
                        LOGGER.error("获取在办列表失败{}", processInstanceId, e);
                    }
                    mapTemp.put("serialNumber", serialNumber + 1);
                    serialNumber += 1;
                    items.add(mapTemp);
                }
                handleFormDataService.execute(itemId, items, processSerialNumbers);
            } else {
                piPage = this.processDoingApi.searchListByUserIdAndProcessDefinitionKey(tenantId, positionId,
                    processDefinitionKey, searchTerm, page, rows);
                List<ProcessInstanceModel> hpiModelList = piPage.getRows();
                int serialNumber = (page - 1) * rows;
                Map<String, Object> mapTemp;
                ProcessParamModel processParam;
                String processDefinitionId, processInstanceId, processSerialNumber = "", documentTitle, level = "",
                    number = "";
                for (ProcessInstanceModel piModel : hpiModelList) {
                    mapTemp = new HashMap<>(16);
                    processInstanceId = piModel.getId();
                    processDefinitionId = piModel.getProcessDefinitionId();
                    try {
                        List<TaskModel> taskList =
                            this.taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                        List<String> listTemp = this.getAssigneeIdsAndAssigneeNames(taskList);
                        String taskIds = listTemp.get(0), assigneeNames = listTemp.get(2);
                        Boolean isReminder = String.valueOf(taskList.get(0).getPriority()).contains("5");
                        processParam =
                            this.processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                        if (null == processParam) {
                            documentTitle = "流程参数表数据不存在";
                        } else {
                            documentTitle =
                                StringUtils.isBlank(processParam.getTitle()) ? "无标题" : processParam.getTitle();
                            level = processParam.getCustomLevel();
                            number = processParam.getCustomNumber();
                            processSerialNumber = processParam.getProcessSerialNumber();
                            processSerialNumbers.add(processSerialNumber);
                        }

                        mapTemp.put(SysVariables.DOCUMENT_TITLE, documentTitle);
                        mapTemp.put(SysVariables.LEVEL, level);
                        mapTemp.put(SysVariables.NUMBER, number);
                        mapTemp.put("itemName", itemName);
                        mapTemp.put("processInstanceId", processInstanceId);
                        mapTemp.put("processDefinitionKey", processDefinitionKey);
                        mapTemp.put(SysVariables.PROCESS_SERIAL_NUMBER, processSerialNumber);
                        mapTemp.put("processDefinitionId", processDefinitionId);
                        mapTemp.put("taskDefinitionKey", taskList.get(0).getTaskDefinitionKey());
                        mapTemp.put("taskName", taskList.get(0).getName());
                        mapTemp.put("taskCreateTime", sdf.format(taskList.get(0).getCreateTime()));
                        mapTemp.put("taskId", taskIds);
                        mapTemp.put("taskAssignee", assigneeNames);
                        mapTemp.put(SysVariables.ITEM_ID, itemId);

                        mapTemp.put("isReminder", isReminder);
                        int chaosongNum =
                            this.chaoSongApi.countByUserIdAndProcessInstanceId(tenantId, positionId, processInstanceId)
                                .getData();
                        mapTemp.put("chaosongNum", chaosongNum);
                        mapTemp.put("status", 1);
                        mapTemp.put("taskDueDate", "");
                        mapTemp.put("processInstanceId", processInstanceId);
                        int speakInfoNum = this.speakInfoApi
                            .getNotReadCount(tenantId, Y9LoginUserHolder.getPersonId(), processInstanceId)
                            .getData();
                        mapTemp.put("speakInfoNum", speakInfoNum);
                    } catch (Exception e) {
                        LOGGER.error("获取在办列表失败{}", processInstanceId, e);
                    }
                    mapTemp.put("serialNumber", serialNumber + 1);
                    serialNumber += 1;
                    items.add(mapTemp);
                }
                handleFormDataService.execute(itemId, items, processSerialNumbers);
            }
            return Y9Page.success(page, piPage.getTotalPages(), piPage.getTotal(), items, "获取列表成功");
        } catch (Exception e) {
            LOGGER.error("获取在办列表失败", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取列表失败");
    }

    /**
     * 当并行的时候，会获取到多个task，为了并行时当前办理人显示多人，而不是显示多条记录，需要分开分别进行处理
     *
     * @return List<String>
     */
    private List<String> getAssigneeIdsAndAssigneeNames(List<TaskModel> taskList) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String taskIds = "", assigneeIds = "", assigneeNames = "";
        List<String> list = new ArrayList<>();
        int i = 0;
        if (!taskList.isEmpty()) {
            for (TaskModel task : taskList) {
                if (StringUtils.isEmpty(taskIds)) {
                    taskIds = task.getId();
                    String assignee = task.getAssignee();
                    if (StringUtils.isNotBlank(assignee)) {
                        assigneeIds = assignee;
                        OrgUnit personTemp = this.orgUnitApi.getOrgUnitPersonOrPosition(tenantId, assignee).getData();
                        if (personTemp != null) {
                            assigneeNames = personTemp.getName();
                            i += 1;
                        }
                    } else {// 处理单实例未签收的当前办理人显示
                        List<IdentityLinkModel> iList =
                            this.identityApi.getIdentityLinksForTask(tenantId, task.getId()).getData();
                        if (!iList.isEmpty()) {
                            int j = 0;
                            for (IdentityLinkModel identityLink : iList) {
                                String assigneeId = identityLink.getUserId();
                                OrgUnit ownerUser = this.orgUnitApi
                                    .getOrgUnitPersonOrPosition(Y9LoginUserHolder.getTenantId(), assigneeId)
                                    .getData();
                                if (j < 5) {
                                    assigneeNames = Y9Util.genCustomStr(assigneeNames, ownerUser.getName(), "、");
                                    assigneeIds = Y9Util.genCustomStr(assigneeIds, assigneeId, SysVariables.COMMA);
                                } else {
                                    assigneeNames = assigneeNames + "等，共" + iList.size() + "人";
                                    break;
                                }
                                j++;
                            }
                        }
                    }
                } else {
                    taskIds = Y9Util.genCustomStr(taskIds, task.getId(), SysVariables.COMMA);
                    String assignee = task.getAssignee();
                    if (i < 5) {
                        if (StringUtils.isNotBlank(assignee)) {
                            assigneeIds = Y9Util.genCustomStr(assigneeIds, task.getAssignee(), SysVariables.COMMA);// 并行时，领导选取时存在顺序，因此这里也存在顺序
                            OrgUnit personTemp =
                                this.orgUnitApi.getOrgUnitPersonOrPosition(tenantId, assignee).getData();
                            if (personTemp != null) {
                                assigneeNames = Y9Util.genCustomStr(assigneeNames, personTemp.getName(), "、");// 并行时，领导选取时存在顺序，因此这里也存在顺序
                                i += 1;
                            }
                        }
                    }
                }
            }
            if (taskList.size() > 5) {
                assigneeNames += "等，共" + taskList.size() + "人";
            }
        } else {
            /*
             * List<HistoricActivityInstance> historicActivityInstanceList =
             * historyService.createHistoricActivityInstanceQuery().
             * processInstanceId(processInstanceId).activityType(SysVariables.
             * CALLACTIVITY).list(); if (historicActivityInstanceList != null &&
             * historicActivityInstanceList.size() > 0) { Map<String, Object> record =
             * setTodoElement(historicActivityInstanceList.get(0), existAssigneeId,
             * existAssigneeName); items.add(record); }
             */
        }
        list.add(taskIds);
        list.add(assigneeIds);
        list.add(assigneeNames);
        return list;
    }

    @Override
    public Y9Page<Map<String, Object>> list4Mobile(String itemId, String searchTerm, Integer page, Integer rows) {
        Y9Page<ProcessInstanceModel> piPage;
        try {
            List<Map<String, Object>> items = new ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String positionId = Y9LoginUserHolder.getPositionId(), tenantId = Y9LoginUserHolder.getTenantId();
            ItemModel item = this.itemApi.getByItemId(tenantId, itemId).getData();
            String processDefinitionKey = item.getWorkflowGuid(), itemName = item.getName();
            if (StringUtils.isBlank(searchTerm)) {
                piPage = this.processDoingApi.getListByUserIdAndProcessDefinitionKeyOrderBySendTime(tenantId,
                    positionId, processDefinitionKey, page, rows);
                List<ProcessInstanceModel> hpiModelList = piPage.getRows();
                int serialNumber = (page - 1) * rows;
                Map<String, Object> mapTemp;
                ProcessParamModel processParam;
                for (ProcessInstanceModel piModel : hpiModelList) {// 以办理时间排序
                    mapTemp = new HashMap<>(16);
                    try {
                        String processInstanceId = piModel.getId();
                        String processDefinitionId = piModel.getProcessDefinitionId();
                        Date endTime = piModel.getEndTime();
                        // endTime.setTime(endTime.getTime() + 8 * 60 * 60 * 1000);
                        String taskCreateTime = piModel.getEndTime() != null ? sdf.format(endTime) : "";
                        List<TaskModel> taskList =
                            this.taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                        List<String> listTemp = this.getAssigneeIdsAndAssigneeNames(taskList);
                        String taskIds = listTemp.get(0);
                        String assigneeNames = listTemp.get(2);
                        Boolean isReminder = String.valueOf(taskList.get(0).getPriority()).contains("5");
                        processParam =
                            this.processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                        String processSerialNumber = processParam.getProcessSerialNumber();
                        String documentTitle =
                            StringUtils.isBlank(processParam.getTitle()) ? "无标题" : processParam.getTitle();
                        String level = processParam.getCustomLevel();
                        String number = processParam.getCustomNumber();
                        mapTemp.put("itemName", itemName);
                        mapTemp.put("processInstanceId", processInstanceId);
                        mapTemp.put("processDefinitionKey", processDefinitionKey);
                        mapTemp.put(SysVariables.PROCESS_SERIAL_NUMBER, processSerialNumber);
                        mapTemp.put("processDefinitionId", processDefinitionId);
                        mapTemp.put(SysVariables.DOCUMENT_TITLE, documentTitle);
                        mapTemp.put("taskDefinitionKey", taskList.get(0).getTaskDefinitionKey());
                        mapTemp.put("taskName", taskList.get(0).getName());
                        mapTemp.put("taskCreateTime", taskCreateTime);
                        mapTemp.put("taskId", taskIds);
                        mapTemp.put("taskAssignee", assigneeNames);
                        mapTemp.put(SysVariables.ITEM_ID, itemId);
                        mapTemp.put(SysVariables.LEVEL, level);
                        mapTemp.put(SysVariables.NUMBER, number);
                        mapTemp.put("isReminder", isReminder);
                        int chaosongNum =
                            this.chaoSongApi.countByUserIdAndProcessInstanceId(tenantId, positionId, processInstanceId)
                                .getData();
                        mapTemp.put("chaosongNum", chaosongNum);
                        mapTemp.put("status", 1);
                        mapTemp.put("taskDueDate", "");
                    } catch (Exception e) {
                        LOGGER.error("获取在办列表失败", e);
                    }
                    mapTemp.put("serialNumber", serialNumber + 1);
                    serialNumber += 1;
                    items.add(mapTemp);
                }
            } else {
                piPage = this.processDoingApi.searchListByUserIdAndProcessDefinitionKey(tenantId, positionId,
                    processDefinitionKey, searchTerm, page, rows);
                List<ProcessInstanceModel> hpiModelList = piPage.getRows();
                int serialNumber = (page - 1) * rows;
                Map<String, Object> mapTemp;
                ProcessParamModel processParam;
                for (ProcessInstanceModel piModel : hpiModelList) {
                    mapTemp = new HashMap<>(16);
                    try {
                        String processInstanceId = piModel.getId();
                        String processDefinitionId = piModel.getProcessDefinitionId();
                        List<TaskModel> taskList =
                            this.taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                        List<String> listTemp = this.getAssigneeIdsAndAssigneeNames(taskList);
                        String taskIds = listTemp.get(0), assigneeNames = listTemp.get(2);
                        Boolean isReminder = String.valueOf(taskList.get(0).getPriority()).contains("5");
                        processParam =
                            this.processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                        String processSerialNumber = processParam.getProcessSerialNumber();
                        String documentTitle =
                            StringUtils.isBlank(processParam.getTitle()) ? "无标题" : processParam.getTitle();
                        String level = processParam.getCustomLevel();
                        String number = processParam.getCustomNumber();
                        mapTemp.put("itemName", itemName);
                        mapTemp.put("processInstanceId", processInstanceId);
                        mapTemp.put("processDefinitionKey", processDefinitionKey);
                        mapTemp.put(SysVariables.PROCESS_SERIAL_NUMBER, processSerialNumber);
                        mapTemp.put("processDefinitionId", processDefinitionId);
                        mapTemp.put(SysVariables.DOCUMENT_TITLE, documentTitle);
                        mapTemp.put("taskDefinitionKey", taskList.get(0).getTaskDefinitionKey());
                        mapTemp.put("taskName", taskList.get(0).getName());
                        mapTemp.put("taskCreateTime", sdf.format(taskList.get(0).getCreateTime()));
                        mapTemp.put("taskId", taskIds);
                        mapTemp.put("taskAssignee", assigneeNames);
                        mapTemp.put(SysVariables.ITEM_ID, itemId);
                        mapTemp.put(SysVariables.LEVEL, level);
                        mapTemp.put(SysVariables.NUMBER, number);
                        mapTemp.put("isReminder", isReminder);
                        int chaosongNum =
                            this.chaoSongApi.countByUserIdAndProcessInstanceId(tenantId, positionId, processInstanceId)
                                .getData();
                        mapTemp.put("chaosongNum", chaosongNum);
                        mapTemp.put("status", 1);
                        mapTemp.put("taskDueDate", "");
                    } catch (Exception e) {
                        LOGGER.error("获取在办列表失败", e);
                    }
                    mapTemp.put("serialNumber", serialNumber + 1);
                    serialNumber += 1;
                    items.add(mapTemp);
                }
            }
            return Y9Page.success(page, piPage.getTotalPages(), piPage.getTotal(), items, "获取列表成功");
        } catch (Exception e) {
            LOGGER.error("获取在办列表失败", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取列表失败");
    }
}