package net.risesoft.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import net.risesoft.api.itemadmin.TaskRelatedApi;
import net.risesoft.model.LightColorModel;
import net.risesoft.model.itemadmin.TaskRelatedModel;
import net.risesoft.service.WorkDayService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.ChaoSongApi;
import net.risesoft.api.itemadmin.FormDataApi;
import net.risesoft.api.itemadmin.ItemApi;
import net.risesoft.api.itemadmin.ItemDoingApi;
import net.risesoft.api.itemadmin.ItemDoneApi;
import net.risesoft.api.itemadmin.ItemRecycleApi;
import net.risesoft.api.itemadmin.ItemTodoApi;
import net.risesoft.api.itemadmin.OfficeFollowApi;
import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.api.itemadmin.RemindInstanceApi;
import net.risesoft.api.itemadmin.SpeakInfoApi;
import net.risesoft.api.itemadmin.TaskVariableApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.processadmin.HistoricTaskApi;
import net.risesoft.api.processadmin.IdentityApi;
import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.ProcessTodoApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.api.processadmin.VariableApi;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.model.itemadmin.ActRuDetailModel;
import net.risesoft.model.itemadmin.ItemModel;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.model.itemadmin.QueryParamModel;
import net.risesoft.model.itemadmin.RemindInstanceModel;
import net.risesoft.model.itemadmin.TaskVariableModel;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.processadmin.IdentityLinkModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.service.WorkList4GfgService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9Util;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkList4GfgServiceImpl implements WorkList4GfgService {

    private final ProcessTodoApi processTodoApi;

    private final ItemApi itemApi;

    private final VariableApi variableApi;

    private final HistoricTaskApi historicTaskApi;

    private final ProcessParamApi processParamApi;

    private final ProcessDefinitionApi processDefinitionApi;

    private final ChaoSongApi chaoSongApi;

    private final FormDataApi formDataApi;

    private final TaskVariableApi taskvariableApi;

    private final SpeakInfoApi speakInfoApi;

    private final RemindInstanceApi remindInstanceApi;

    private final OfficeFollowApi officeFollowApi;

    private final ItemTodoApi itemTodoApi;

    private final ItemDoingApi itemDoingApi;

    private final ItemDoneApi itemDoneApi;

    private final ItemRecycleApi itemRecycleApi;

    private final TaskApi taskApi;

    private final OrgUnitApi orgUnitApi;

    private final IdentityApi identityApi;

    private final TaskRelatedApi taskRelatedApi;

    private final WorkDayService workDayService;

    @Override
    public Y9Page<Map<String, Object>> allTodoList(QueryParamModel queryParamModel) {
        Y9Page<ActRuDetailModel> itemPage;
        try {
            String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
            itemPage = itemTodoApi.findByUserId(tenantId, positionId, queryParamModel);
            List<ActRuDetailModel> list = itemPage.getRows();
            ObjectMapper objectMapper = new ObjectMapper();
            List<ActRuDetailModel> taslList = objectMapper.convertValue(list, new TypeReference<>() {
            });
            List<Map<String, Object>> items = new ArrayList<>();
            int serialNumber = (queryParamModel.getPage() - 1) * queryParamModel.getRows();
            Map<String, Object> mapTemp;
            ProcessParamModel processParam;
            String processInstanceId;
            for (ActRuDetailModel ardModel : taslList) {
                mapTemp = new HashMap<>(16);
                String taskId = ardModel.getTaskId();
                processInstanceId = ardModel.getProcessInstanceId();
                try {
                    String processSerialNumber = ardModel.getProcessSerialNumber();
                    mapTemp.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber);
                    TaskModel task = taskApi.findById(tenantId, taskId).getData();
                    String taskAssignee = ardModel.getAssigneeName();
                    if (StringUtils.isBlank(task.getAssignee())) {
                        taskAssignee = getAssigneeNames(taskId);
                    }
                    String taskName = task.getName();
                    int priority = task.getPriority();
                    int isNewTodo = StringUtils.isBlank(task.getFormKey()) ? 1 : Integer.parseInt(task.getFormKey());
                    Boolean isReminder = String.valueOf(priority).contains("8");
                    processParam = processParamApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();

                    /**
                     * 暂时取表单所有字段数据
                     */
                    Map<String, Object> formData =
                            formDataApi.getData(tenantId, processParam.getItemId(), processSerialNumber).getData();
                    mapTemp.putAll(formData);

                    mapTemp.put("systemCNName", processParam.getSystemCnName());
                    mapTemp.put("number", processParam.getCustomNumber());
                    mapTemp.put("title", processParam.getTitle());
                    mapTemp.put("bureauName",
                            orgUnitApi.getBureau(tenantId, processParam.getStartor()).getData().getName());
                    mapTemp.put("taskName", taskName);

                    mapTemp.put("itemId", processParam.getItemId());
                    mapTemp.put("processInstanceId", processInstanceId);
                    mapTemp.put("taskId", taskId);
                    mapTemp.put("taskAssignee", taskAssignee);
                    mapTemp.put(SysVariables.ITEMBOX, ItemBoxTypeEnum.TODO.getValue());

                    mapTemp.put(SysVariables.ISNEWTODO, isNewTodo);
                    mapTemp.put(SysVariables.ISREMINDER, isReminder);
                    String rollBack = variableApi.getVariableLocal(tenantId, taskId, SysVariables.ROLLBACK).getData();
                    // 退回件
                    if (Boolean.parseBoolean(rollBack)) {
                        mapTemp.put("rollBack", true);
                    }
                    List<TaskRelatedModel> taskRelatedList = taskRelatedApi.findByTaskId(tenantId, taskId).getData();
                    mapTemp.put(SysVariables.TASKRELATEDLIST, taskRelatedList);
                    /**
                     * 红绿灯
                     */
                    LightColorModel lightColorModel = new LightColorModel();
                    if (null != ardModel.getDueDate()) {
                        lightColorModel = workDayService.getLightColor(new Date(), ardModel.getDueDate());
                    }
                    mapTemp.put("lightColor", lightColorModel);
                } catch (Exception e) {
                    LOGGER.error("获取待办列表失败" + processInstanceId, e);
                }
                mapTemp.put("serialNumber", serialNumber + 1);
                serialNumber += 1;
                items.add(mapTemp);
            }
            return Y9Page.success(queryParamModel.getPage(), itemPage.getTotalPages(), itemPage.getTotal(), items,
                    "获取列表成功");
        } catch (Exception e) {
            LOGGER.error("获取待办列表失败", e);
        }
        return Y9Page.success(queryParamModel.getPage(), 0, 0, new ArrayList<>(), "获取列表失败");
    }

    @Override
    public Y9Page<Map<String, Object>> doingList(String itemId, Integer page, Integer rows) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
            ItemModel item = itemApi.getByItemId(tenantId, itemId).getData();
            Y9Page<ActRuDetailModel> itemPage =
                    itemDoingApi.findByUserIdAndSystemName(tenantId, positionId, item.getSystemName(), page, rows);
            List<ActRuDetailModel> list = itemPage.getRows();
            ObjectMapper objectMapper = new ObjectMapper();
            List<ActRuDetailModel> taslList = objectMapper.convertValue(list, new TypeReference<>() {
            });
            List<Map<String, Object>> items = new ArrayList<>();
            int serialNumber = (page - 1) * rows;
            Map<String, Object> mapTemp;
            ProcessParamModel processParam;
            String processInstanceId;
            Map<String, Object> formData;
            for (ActRuDetailModel ardModel : taslList) {
                mapTemp = new HashMap<>(16);
                String taskId = ardModel.getTaskId();
                processInstanceId = ardModel.getProcessInstanceId();
                try {
                    String processSerialNumber = ardModel.getProcessSerialNumber();
                    mapTemp.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber);
                    processParam = processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                    List<TaskModel> taskList = taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                    if (!taskList.isEmpty()) {
                        mapTemp.put("taskName", taskList.get(0).getName());
                        List<String> listTemp = getAssigneeIdsAndAssigneeNames(taskList);
                        String assigneeNames = listTemp.get(2);
                        mapTemp.put("taskAssignee", assigneeNames);
                    } else {
                        mapTemp.put("taskName", "会签");
                        mapTemp.put("taskAssignee", "会签办理");
                    }
                    mapTemp.put("systemCNName", processParam.getSystemCnName());
                    mapTemp.put("bureauName",
                            orgUnitApi.getBureau(tenantId, processParam.getStartor()).getData().getName());
                    mapTemp.put("itemId", processParam.getItemId());
                    mapTemp.put("processInstanceId", processInstanceId);
                    mapTemp.put("taskId", taskId);
                    /**
                     * 暂时取表单所有字段数据
                     */
                    formData = formDataApi.getData(tenantId, itemId, processSerialNumber).getData();
                    mapTemp.putAll(formData);

                    mapTemp.put(SysVariables.ITEMBOX, ItemBoxTypeEnum.DOING.getValue());
                } catch (Exception e) {
                    LOGGER.error("获取待办列表失败" + processInstanceId, e);
                }
                mapTemp.put("serialNumber", serialNumber + 1);
                serialNumber += 1;
                items.add(mapTemp);
            }
            return Y9Page.success(page, itemPage.getTotalPages(), itemPage.getTotal(), items, "获取列表成功");
        } catch (Exception e) {
            LOGGER.error("获取待办异常", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取列表失败");
    }

    @Override
    public Y9Page<Map<String, Object>> doneList(String itemId, Integer page, Integer rows) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
            ItemModel item = itemApi.getByItemId(tenantId, itemId).getData();
            Y9Page<ActRuDetailModel> itemPage =
                    itemDoneApi.findByUserIdAndSystemName(tenantId, positionId, item.getSystemName(), page, rows);
            List<ActRuDetailModel> list = itemPage.getRows();
            ObjectMapper objectMapper = new ObjectMapper();
            List<ActRuDetailModel> taslList = objectMapper.convertValue(list, new TypeReference<>() {
            });
            List<Map<String, Object>> items = new ArrayList<>();
            int serialNumber = (page - 1) * rows;
            Map<String, Object> mapTemp;
            ProcessParamModel processParam;
            String processInstanceId;
            Map<String, Object> formData;
            for (ActRuDetailModel ardModel : taslList) {
                mapTemp = new HashMap<>(16);
                String taskId = ardModel.getTaskId();
                processInstanceId = ardModel.getProcessInstanceId();
                try {
                    String processSerialNumber = ardModel.getProcessSerialNumber();
                    mapTemp.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber);
                    processParam = processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                    String completer =
                            StringUtils.isBlank(processParam.getCompleter()) ? "无" : processParam.getCompleter();
                    mapTemp.put("systemCNName", processParam.getSystemCnName());
                    mapTemp.put("bureauName",
                            orgUnitApi.getBureau(tenantId, processParam.getStartor()).getData().getName());
                    mapTemp.put("taskName", "已办结");
                    mapTemp.put("itemId", processParam.getItemId());
                    mapTemp.put("processInstanceId", processInstanceId);
                    mapTemp.put("completer", completer);
                    mapTemp.put("taskId", taskId);
                    /**
                     * 暂时取表单所有字段数据
                     */
                    formData = formDataApi.getData(tenantId, itemId, processSerialNumber).getData();
                    mapTemp.putAll(formData);
                    mapTemp.put(SysVariables.ITEMBOX, ItemBoxTypeEnum.DONE.getValue());
                } catch (Exception e) {
                    LOGGER.error("获取待办列表失败" + processInstanceId, e);
                }
                mapTemp.put("serialNumber", serialNumber + 1);
                serialNumber += 1;
                items.add(mapTemp);
            }
            return Y9Page.success(page, itemPage.getTotalPages(), itemPage.getTotal(), items, "获取列表成功");
        } catch (Exception e) {
            LOGGER.error("获取待办异常", e);
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
                        OrgUnit personTemp = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, assignee).getData();
                        if (personTemp != null) {
                            assigneeNames = personTemp.getName();
                            i += 1;
                        }
                    } else {// 处理单实例未签收的当前办理人显示
                        List<IdentityLinkModel> iList =
                                identityApi.getIdentityLinksForTask(tenantId, task.getId()).getData();
                        if (!iList.isEmpty()) {
                            int j = 0;
                            for (IdentityLinkModel identityLink : iList) {
                                String assigneeId = identityLink.getUserId();
                                OrgUnit ownerUser = orgUnitApi
                                        .getOrgUnitPersonOrPosition(Y9LoginUserHolder.getTenantId(), assigneeId).getData();
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
                            OrgUnit personTemp = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, assignee).getData();
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

    /**
     * 当并行的时候，会获取到多个task，为了并行时当前办理人显示多人，而不是显示多条记录，需要分开分别进行处理
     *
     * @return List<String>
     */
    private String getAssigneeNames(String taskId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String assigneeNames = "";
        List<IdentityLinkModel> iList =
                identityApi.getIdentityLinksForTask(tenantId, taskId).getData();
        int j = 0;
        for (IdentityLinkModel identityLink : iList) {
            String assigneeId = identityLink.getUserId();
            OrgUnit ownerUser = orgUnitApi
                    .getOrgUnitPersonOrPosition(Y9LoginUserHolder.getTenantId(), assigneeId).getData();
            if (j < 5) {
                assigneeNames = Y9Util.genCustomStr(assigneeNames, ownerUser.getName(), "、");
            } else {
                assigneeNames = assigneeNames + "等，共" + iList.size() + "人";
                break;
            }
            j++;
        }
        return assigneeNames;
    }

    @Override
    public Y9Page<Map<String, Object>> pageSearchList(String itemId, String tableName, String searchMapStr,
                                                      Integer page, Integer rows) {
        Y9Page<ActRuDetailModel> itemPage;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
            ItemModel item = itemApi.getByItemId(tenantId, itemId).getData();
            String systemName = item.getSystemName(), itemName = item.getName();
            if (StringUtils.isBlank(searchMapStr)) {
                itemPage = itemTodoApi.findByUserIdAndSystemName(tenantId, positionId, systemName, page, rows);
            } else {
                itemPage = itemTodoApi.searchByUserIdAndSystemName(tenantId, positionId, systemName, tableName,
                        searchMapStr, page, rows);
            }
            List<ActRuDetailModel> list = itemPage.getRows();
            ObjectMapper objectMapper = new ObjectMapper();
            List<ActRuDetailModel> taslList = objectMapper.convertValue(list, new TypeReference<>() {
            });
            List<Map<String, Object>> items = new ArrayList<>();
            int serialNumber = (page - 1) * rows;
            Map<String, Object> vars;
            Collection<String> keys;
            Map<String, Object> mapTemp;
            Map<String, Object> formDataMap;
            ProcessParamModel processParam;
            String processInstanceId;
            for (ActRuDetailModel ardModel : taslList) {
                mapTemp = new HashMap<>(16);
                String taskId = ardModel.getTaskId();
                processInstanceId = ardModel.getProcessInstanceId();
                try {
                    String processSerialNumber = ardModel.getProcessSerialNumber();
                    Date taskCreateTime = ardModel.getCreateTime();
                    mapTemp.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber);
                    mapTemp.put("itemId", itemId);
                    mapTemp.put("itemName", itemName);
                    mapTemp.put("taskCreateTime", sdf.format(taskCreateTime));
                    TaskModel task = taskApi.findById(tenantId, taskId).getData();
                    mapTemp.put("taskId", taskId);
                    String processDefinitionId = task.getProcessDefinitionId();
                    String taskAssignee = ardModel.getAssignee();
                    String taskDefinitionKey = task.getTaskDefinitionKey();
                    String taskName = task.getName();
                    int priority = task.getPriority();
                    keys = new ArrayList<>();
                    keys.add(SysVariables.TASKSENDER);
                    vars = variableApi.getVariablesByProcessInstanceId(tenantId, processInstanceId, keys).getData();
                    String taskSender = Strings.nullToEmpty((String) vars.get(SysVariables.TASKSENDER));
                    int isNewTodo = StringUtils.isBlank(task.getFormKey()) ? 1 : Integer.parseInt(task.getFormKey());
                    Boolean isReminder = String.valueOf(priority).contains("8");// 催办的时候任务的优先级+5
                    processParam = processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                    mapTemp.put("processDefinitionKey", processDefinitionId.split(":")[0]);
                    mapTemp.put("processDefinitionId", processDefinitionId);
                    mapTemp.put("description", "");
                    mapTemp.put("taskDefinitionKey", taskDefinitionKey);
                    mapTemp.put("taskName", taskName);
                    mapTemp.put("taskAssignee", taskAssignee);
                    mapTemp.put(SysVariables.TASKSENDER, taskSender);
                    mapTemp.put(SysVariables.ISNEWTODO, isNewTodo);
                    mapTemp.put(SysVariables.ISREMINDER, isReminder);
                    String multiInstance = processDefinitionApi
                            .getNodeType(tenantId, task.getProcessDefinitionId(), task.getTaskDefinitionKey()).getData();
                    mapTemp.put("isZhuBan", "");
                    if (multiInstance.equals(SysVariables.PARALLEL)) {
                        mapTemp.put("isZhuBan", "false");
                        String sponsorGuid = processParam.getSponsorGuid();
                        if (StringUtils.isNotBlank(sponsorGuid)) {
                            if (task.getAssignee().equals(sponsorGuid)) {
                                mapTemp.put("isZhuBan", "true");
                            }
                        }
                        String obj = variableApi.getVariableByProcessInstanceId(tenantId, task.getExecutionId(),
                                SysVariables.NROFACTIVEINSTANCES).getData();
                        int nrOfActiveInstances = obj != null ? Integer.parseInt(obj) : 0;
                        if (nrOfActiveInstances == 1) {
                            mapTemp.put("isZhuBan", "true");
                        }
                        if (StringUtils.isNotBlank(task.getOwner()) && !task.getOwner().equals(task.getAssignee())) {
                            mapTemp.put("isZhuBan", "");
                        }
                    }
                    mapTemp.put("isForwarding", false);
                    TaskVariableModel taskVariableModel =
                            taskvariableApi.findByTaskIdAndKeyName(tenantId, taskId, "isForwarding").getData();
                    if (taskVariableModel != null) {// 是否正在发送标识
                        mapTemp.put("isForwarding", taskVariableModel.getText().contains("true"));
                    }
                    formDataMap = formDataApi.getData(tenantId, itemId, processSerialNumber).getData();
                    /*if (formDataMap.get("leaveType") != null) {
                        String leaveType = (String)formDataMap.get("leaveType");
                        ItemLeaveTypeEnum[] arr = ItemLeaveTypeEnum.values();
                        for (ItemLeaveTypeEnum leaveTypeEnum : arr) {
                            if (leaveType.equals(leaveTypeEnum.getValue())) {
                                formDataMap.put("leaveType", leaveTypeEnum.getName());
                                break;
                            }
                        }
                    }*/
                    mapTemp.putAll(formDataMap);
                    mapTemp.put("processInstanceId", processInstanceId);
                    int speakInfoNum = speakInfoApi
                            .getNotReadCount(tenantId, Y9LoginUserHolder.getPersonId(), processInstanceId).getData();
                    mapTemp.put("speakInfoNum", speakInfoNum);
                    mapTemp.put("remindSetting", false);
                    RemindInstanceModel remindInstanceModel = remindInstanceApi
                            .getRemindInstance(tenantId, Y9LoginUserHolder.getPersonId(), processInstanceId).getData();
                    if (remindInstanceModel != null) {// 流程实例是否设置消息提醒
                        mapTemp.put("remindSetting", true);
                    }

                    int countFollow =
                            officeFollowApi.countByProcessInstanceId(tenantId, positionId, processInstanceId).getData();
                    mapTemp.put("follow", countFollow > 0);

                    String rollBack = variableApi.getVariableLocal(tenantId, taskId, SysVariables.ROLLBACK).getData();
                    if (Boolean.parseBoolean(rollBack)) {// 退回件
                        mapTemp.put("rollBack", true);
                    }
                } catch (Exception e) {
                    LOGGER.error("获取待办列表失败" + processInstanceId, e);
                }
                mapTemp.put("serialNumber", serialNumber + 1);
                serialNumber += 1;
                items.add(mapTemp);
            }
            return Y9Page.success(page, itemPage.getTotalPages(), itemPage.getTotal(), items, "获取列表成功");
        } catch (Exception e) {
            LOGGER.error("获取待办列表失败", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取列表失败");
    }

    @Override
    public Y9Page<Map<String, Object>> recycleList(String itemId, Integer page, Integer rows) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
            ItemModel item = itemApi.getByItemId(tenantId, itemId).getData();
            Y9Page<ActRuDetailModel> itemPage =
                    itemRecycleApi.findByUserIdAndSystemName(tenantId, positionId, item.getSystemName(), page, rows);
            List<ActRuDetailModel> list = itemPage.getRows();
            ObjectMapper objectMapper = new ObjectMapper();
            List<ActRuDetailModel> taslList = objectMapper.convertValue(list, new TypeReference<>() {
            });
            List<Map<String, Object>> items = new ArrayList<>();
            int serialNumber = (page - 1) * rows;
            Map<String, Object> mapTemp;
            ProcessParamModel processParam;
            String processInstanceId;
            Map<String, Object> formData;
            for (ActRuDetailModel ardModel : taslList) {
                mapTemp = new HashMap<>(16);
                String taskId = ardModel.getTaskId();
                processInstanceId = ardModel.getProcessInstanceId();
                try {
                    String processSerialNumber = ardModel.getProcessSerialNumber();
                    mapTemp.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber);
                    processParam = processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                    String completer =
                            StringUtils.isBlank(processParam.getCompleter()) ? "无" : processParam.getCompleter();
                    mapTemp.put("systemCNName", processParam.getSystemCnName());
                    mapTemp.put("bureauName",
                            orgUnitApi.getBureau(tenantId, processParam.getStartor()).getData().getName());
                    mapTemp.put("taskName", "已办结");
                    mapTemp.put("itemId", processParam.getItemId());
                    mapTemp.put("processInstanceId", processInstanceId);
                    mapTemp.put("completer", completer);
                    mapTemp.put("taskId", taskId);
                    /**
                     * 暂时取表单所有字段数据
                     */
                    formData = formDataApi.getData(tenantId, itemId, processSerialNumber).getData();
                    mapTemp.putAll(formData);
                    mapTemp.put(SysVariables.ITEMBOX, ItemBoxTypeEnum.DONE.getValue());
                } catch (Exception e) {
                    LOGGER.error("获取待办列表失败" + processInstanceId, e);
                }
                mapTemp.put("serialNumber", serialNumber + 1);
                serialNumber += 1;
                items.add(mapTemp);
            }
            return Y9Page.success(page, itemPage.getTotalPages(), itemPage.getTotal(), items, "获取列表成功");
        } catch (Exception e) {
            LOGGER.error("获取待办异常", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取列表失败");
    }

    @Override
    public Y9Page<Map<String, Object>> todoList(String itemId, Integer page, Integer rows) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
            ItemModel item = itemApi.getByItemId(tenantId, itemId).getData();
            Y9Page<ActRuDetailModel> itemPage =
                    itemTodoApi.findByUserIdAndSystemName(tenantId, positionId, item.getSystemName(), page, rows);
            List<ActRuDetailModel> list = itemPage.getRows();
            ObjectMapper objectMapper = new ObjectMapper();
            List<ActRuDetailModel> taslList = objectMapper.convertValue(list, new TypeReference<>() {
            });
            List<Map<String, Object>> items = new ArrayList<>();
            int serialNumber = (page - 1) * rows;
            Map<String, Object> mapTemp;
            ProcessParamModel processParam;
            String processInstanceId;
            Map<String, Object> formData;
            for (ActRuDetailModel ardModel : taslList) {
                mapTemp = new HashMap<>(16);
                String taskId = ardModel.getTaskId();
                processInstanceId = ardModel.getProcessInstanceId();
                try {
                    String processSerialNumber = ardModel.getProcessSerialNumber();
                    mapTemp.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber);
                    TaskModel task = taskApi.findById(tenantId, taskId).getData();
                    String taskAssignee = ardModel.getAssigneeName();
                    if (StringUtils.isBlank(task.getAssignee())) {
                        taskAssignee = getAssigneeNames(taskId);
                    }
                    String taskName = task.getName();
                    int priority = task.getPriority();
                    int isNewTodo = StringUtils.isBlank(task.getFormKey()) ? 1 : Integer.parseInt(task.getFormKey());
                    Boolean isReminder = String.valueOf(priority).contains("8");
                    processParam = processParamApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
                    mapTemp.put("systemCNName", processParam.getSystemCnName());
                    mapTemp.put("bureauName",
                            orgUnitApi.getBureau(tenantId, processParam.getStartor()).getData().getName());
                    mapTemp.put("taskName", taskName);
                    mapTemp.put("itemId", processParam.getItemId());
                    mapTemp.put("processInstanceId", processInstanceId);
                    mapTemp.put("processDefinitionId", task.getProcessDefinitionId());
                    mapTemp.put("taskId", taskId);
                    mapTemp.put("taskAssignee", taskAssignee);
                    /**
                     * 暂时取表单所有字段数据
                     */
                    formData = formDataApi.getData(tenantId, itemId, processSerialNumber).getData();
                    mapTemp.putAll(formData);

                    mapTemp.put(SysVariables.ITEMBOX, ItemBoxTypeEnum.TODO.getValue());
                    mapTemp.put(SysVariables.ISNEWTODO, isNewTodo);
                    mapTemp.put(SysVariables.ISREMINDER, isReminder);
                    String rollBack = variableApi.getVariableLocal(tenantId, taskId, SysVariables.ROLLBACK).getData();
                    // 退回件
                    if (Boolean.parseBoolean(rollBack)) {
                        mapTemp.put("rollBack", true);
                    }
                    List<TaskRelatedModel> taskRelatedList = taskRelatedApi.findByTaskId(tenantId, taskId).getData();
                    mapTemp.put(SysVariables.TASKRELATEDLIST, taskRelatedList);
                    /**
                     * 红绿灯
                     */
                    LightColorModel lightColorModel = new LightColorModel();
                    if (null != ardModel.getDueDate()) {
                        lightColorModel = workDayService.getLightColor(new Date(), ardModel.getDueDate());
                    }
                    mapTemp.put("lightColor", lightColorModel);
                } catch (Exception e) {
                    LOGGER.error("获取待办列表失败" + processInstanceId, e);
                }
                mapTemp.put("serialNumber", serialNumber + 1);
                serialNumber += 1;
                items.add(mapTemp);
            }
            return Y9Page.success(page, itemPage.getTotalPages(), itemPage.getTotal(), items, "获取列表成功");
        } catch (Exception e) {
            LOGGER.error("获取待办异常", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取列表失败");
    }
}
