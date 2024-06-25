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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.FormDataApi;
import net.risesoft.api.itemadmin.ItemDoingApi;
import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.api.itemadmin.RemindInstanceApi;
import net.risesoft.api.itemadmin.SpeakInfoApi;
import net.risesoft.api.itemadmin.position.ChaoSong4PositionApi;
import net.risesoft.api.itemadmin.position.Item4PositionApi;
import net.risesoft.api.itemadmin.position.OfficeFollow4PositionApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.api.processadmin.DoingApi;
import net.risesoft.api.processadmin.IdentityApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.enums.ItemLeaveTypeEnum;
import net.risesoft.model.itemadmin.ActRuDetailModel;
import net.risesoft.model.itemadmin.ItemModel;
import net.risesoft.model.itemadmin.ItemPage;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.model.itemadmin.RemindInstanceModel;
import net.risesoft.model.platform.Position;
import net.risesoft.model.processadmin.IdentityLinkModel;
import net.risesoft.model.processadmin.ProcessInstanceModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.service.DoingService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9Util;

@Slf4j
@RequiredArgsConstructor
@Service(value = "doingService")
@Transactional(readOnly = true)
public class DoingServiceImpl implements DoingService {

    private final DoingApi doingApi;

    private final TaskApi taskApi;

    private final Item4PositionApi item4PositionApi;

    private final PositionApi positionApi;

    private final ProcessParamApi processParamApi;

    private final ChaoSong4PositionApi chaoSong4PositionApi;

    private final FormDataApi formDataApi;

    private final SpeakInfoApi speakInfoApi;

    private final RemindInstanceApi remindInstanceApi;

    private final OfficeFollow4PositionApi officeFollow4PositionApi;

    private final IdentityApi identityApi;

    private final ItemDoingApi itemDoingApi;

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
                        Position personTemp = positionApi.get(tenantId, assignee).getData();
                        if (personTemp != null) {
                            assigneeNames = personTemp.getName();
                            i += 1;
                        }
                    } else {// 处理单实例未签收的当前办理人显示
                        List<IdentityLinkModel> iList = identityApi.getIdentityLinksForTask(tenantId, task.getId());
                        if (!iList.isEmpty()) {
                            int j = 0;
                            for (IdentityLinkModel identityLink : iList) {
                                String assigneeId = identityLink.getUserId();
                                Position ownerUser =
                                    positionApi.get(Y9LoginUserHolder.getTenantId(), assigneeId).getData();
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
                            Position personTemp = positionApi.get(tenantId, assignee).getData();
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

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> list(String itemId, String searchTerm, Integer page, Integer rows) {
        Map<String, Object> retMap = new HashMap<>(16);
        try {
            List<Map<String, Object>> items = new ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String positionId = Y9LoginUserHolder.getPositionId(), tenantId = Y9LoginUserHolder.getTenantId();
            ItemModel item = item4PositionApi.getByItemId(tenantId, itemId);
            String processDefinitionKey = item.getWorkflowGuid(), itemName = item.getName();
            if (StringUtils.isBlank(searchTerm)) {
                // retMap = doingApi.getListByUserIdAndProcessDefinitionKey(tenantId,
                // userId, processDefinitionKey, page, rows);
                retMap = doingApi.getListByUserIdAndProcessDefinitionKeyOrderBySendTime(tenantId, positionId,
                    processDefinitionKey, page, rows);
                List<Map<String, Object>> list = (List<Map<String, Object>>)retMap.get("rows");
                ObjectMapper objectMapper = new ObjectMapper();
                List<Map<String, Object>> hpiModelList = objectMapper.convertValue(list, new TypeReference<>() {});
                int serialNumber = (page - 1) * rows;
                Map<String, Object> mapTemp;
                ProcessParamModel processParam;
                SimpleDateFormat sdfT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                for (Map<String, Object> hpim : hpiModelList) {// 以办理时间排序
                    mapTemp = new HashMap<>(16);
                    try {
                        String processInstanceId = (String)hpim.get("processInstanceId");
                        String processDefinitionId = (String)hpim.get("processDefinitionId");
                        Date endTime = sdfT.parse(hpim.get("endTime").toString());
                        endTime.setTime(endTime.getTime() + 8 * 60 * 60 * 1000);
                        String taskCreateTime = hpim.get("endTime") != null ? sdf.format(endTime) : "";
                        List<TaskModel> taskList = taskApi.findByProcessInstanceId(tenantId, processInstanceId);
                        List<String> listTemp = getAssigneeIdsAndAssigneeNames(taskList);
                        String taskIds = listTemp.get(0), assigneeIds = listTemp.get(1),
                            assigneeNames = listTemp.get(2);
                        Boolean isReminder = String.valueOf(taskList.get(0).getPriority()).contains("5");
                        processParam = processParamApi.findByProcessInstanceId(tenantId, processInstanceId);
                        String processSerialNumber = processParam.getProcessSerialNumber();
                        String documentTitle =
                            StringUtils.isBlank(processParam.getTitle()) ? "无标题" : processParam.getTitle();
                        String level = processParam.getCustomLevel();
                        String number = processParam.getCustomNumber();
                        mapTemp.put("itemName", itemName);
                        mapTemp.put("processInstanceId", processInstanceId);
                        mapTemp.put("processDefinitionKey", processDefinitionKey);
                        mapTemp.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber);
                        mapTemp.put("processDefinitionId", processDefinitionId);
                        mapTemp.put(SysVariables.DOCUMENTTITLE, documentTitle);
                        mapTemp.put("taskDefinitionKey", taskList.get(0).getTaskDefinitionKey());
                        mapTemp.put("taskName", taskList.get(0).getName());
                        mapTemp.put("taskCreateTime", taskCreateTime);
                        mapTemp.put("taskId", taskIds);
                        mapTemp.put("taskAssigneeId", assigneeIds);
                        mapTemp.put("taskAssignee", assigneeNames);
                        mapTemp.put(SysVariables.ITEMID, itemId);
                        mapTemp.put(SysVariables.LEVEL, level);
                        mapTemp.put(SysVariables.NUMBER, number);
                        mapTemp.put("isReminder", isReminder);
                        int chaosongNum = chaoSong4PositionApi
                            .countByUserIdAndProcessInstanceId(tenantId, positionId, processInstanceId).getData();
                        mapTemp.put("chaosongNum", chaosongNum);
                        mapTemp.put("status", 1);
                        mapTemp.put("taskDueDate", "");
                    } catch (Exception e) {
                        LOGGER.error("获取待办列表失败", e);
                    }
                    mapTemp.put("serialNumber", serialNumber + 1);
                    serialNumber += 1;
                    items.add(mapTemp);
                }
            } else {
                retMap = doingApi.searchListByUserIdAndProcessDefinitionKey(tenantId, positionId, processDefinitionKey,
                    searchTerm, page, rows);
                List<ProcessInstanceModel> hpiModelList = (List<ProcessInstanceModel>)retMap.get("rows");
                int serialNumber = (page - 1) * rows;
                Map<String, Object> mapTemp;
                ProcessParamModel processParam;
                for (ProcessInstanceModel hpim : hpiModelList) {
                    mapTemp = new HashMap<>(16);
                    try {
                        String processInstanceId = hpim.getId();
                        String processDefinitionId = hpim.getProcessDefinitionId();
                        List<TaskModel> taskList = taskApi.findByProcessInstanceId(tenantId, processInstanceId);
                        List<String> listTemp = getAssigneeIdsAndAssigneeNames(taskList);
                        String taskIds = listTemp.get(0), assigneeIds = listTemp.get(1),
                            assigneeNames = listTemp.get(2);
                        Boolean isReminder = String.valueOf(taskList.get(0).getPriority()).contains("5");
                        processParam = processParamApi.findByProcessInstanceId(tenantId, processInstanceId);
                        String processSerialNumber = processParam.getProcessSerialNumber();
                        String documentTitle =
                            StringUtils.isBlank(processParam.getTitle()) ? "无标题" : processParam.getTitle();
                        String level = processParam.getCustomLevel();
                        String number = processParam.getCustomNumber();
                        mapTemp.put("itemName", itemName);
                        mapTemp.put("processInstanceId", processInstanceId);
                        mapTemp.put("processDefinitionKey", processDefinitionKey);
                        mapTemp.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber);
                        mapTemp.put("processDefinitionId", processDefinitionId);
                        mapTemp.put(SysVariables.DOCUMENTTITLE, documentTitle);
                        mapTemp.put("taskDefinitionKey", taskList.get(0).getTaskDefinitionKey());
                        mapTemp.put("taskName", taskList.get(0).getName());
                        mapTemp.put("taskCreateTime", sdf.format(taskList.get(0).getCreateTime()));
                        mapTemp.put("taskId", taskIds);
                        mapTemp.put("taskAssigneeId", assigneeIds);
                        mapTemp.put("taskAssignee", assigneeNames);
                        mapTemp.put(SysVariables.ITEMID, itemId);
                        mapTemp.put(SysVariables.LEVEL, level);
                        mapTemp.put(SysVariables.NUMBER, number);
                        mapTemp.put("isReminder", isReminder);
                        int chaosongNum = chaoSong4PositionApi
                            .countByUserIdAndProcessInstanceId(tenantId, positionId, processInstanceId).getData();
                        mapTemp.put("chaosongNum", chaosongNum);
                        mapTemp.put("status", 1);
                        mapTemp.put("taskDueDate", "");
                    } catch (Exception e) {
                        LOGGER.error("获取待办列表失败", e);
                    }
                    mapTemp.put("serialNumber", serialNumber + 1);
                    serialNumber += 1;
                    items.add(mapTemp);
                }
            }
            retMap.put("rows", items);
        } catch (Exception e) {
            LOGGER.error("获取待办列表失败", e);
        }
        return retMap;
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public Y9Page<Map<String, Object>> listNew(String itemId, String searchTerm, Integer page, Integer rows) {
        Map<String, Object> retMap;
        try {
            List<Map<String, Object>> items = new ArrayList<>();
            SimpleDateFormat sdfT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String positionId = Y9LoginUserHolder.getPositionId(), tenantId = Y9LoginUserHolder.getTenantId();
            ItemModel item = item4PositionApi.getByItemId(tenantId, itemId);
            String processDefinitionKey = item.getWorkflowGuid(), itemName = item.getName();
            if (StringUtils.isBlank(searchTerm)) {
                retMap = doingApi.getListByUserIdAndProcessDefinitionKeyOrderBySendTime(tenantId, positionId,
                    processDefinitionKey, page, rows);
                List<Map<String, Object>> list = (List<Map<String, Object>>)retMap.get("rows");
                ObjectMapper objectMapper = new ObjectMapper();
                List<Map<String, Object>> hpiModelList = objectMapper.convertValue(list, new TypeReference<>() {});
                int serialNumber = (page - 1) * rows;
                Map<String, Object> mapTemp;
                Map<String, Object> formDataMap;
                ProcessParamModel processParam;
                ItemLeaveTypeEnum[] arr = ItemLeaveTypeEnum.values();
                String processInstanceId = "";
                for (Map<String, Object> hpim : hpiModelList) {// 以办理时间排序
                    mapTemp = new HashMap<>(16);
                    try {
                        processInstanceId = (String)hpim.get("processInstanceId");
                        String processDefinitionId = (String)hpim.get("processDefinitionId");
                        Date endTime = sdfT.parse(hpim.get("endTime").toString());
                        endTime.setTime(endTime.getTime() + 8 * 60 * 60 * 1000);
                        String taskCreateTime = hpim.get("endTime") != null ? sdf.format(endTime) : "";
                        List<TaskModel> taskList = taskApi.findByProcessInstanceId(tenantId, processInstanceId);
                        List<String> listTemp = getAssigneeIdsAndAssigneeNames(taskList);
                        String taskIds = listTemp.get(0), assigneeIds = listTemp.get(1),
                            assigneeNames = listTemp.get(2);
                        Boolean isReminder = String.valueOf(taskList.get(0).getPriority()).contains("5");
                        processParam = processParamApi.findByProcessInstanceId(tenantId, processInstanceId);
                        String processSerialNumber = processParam.getProcessSerialNumber();
                        String documentTitle =
                            StringUtils.isBlank(processParam.getTitle()) ? "无标题" : processParam.getTitle();
                        String level = processParam.getCustomLevel();
                        String number = processParam.getCustomNumber();
                        mapTemp.put("itemName", itemName);
                        mapTemp.put("processDefinitionKey", processDefinitionKey);
                        mapTemp.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber);
                        mapTemp.put("processDefinitionId", processDefinitionId);
                        mapTemp.put(SysVariables.DOCUMENTTITLE, documentTitle);
                        mapTemp.put("taskDefinitionKey", taskList.get(0).getTaskDefinitionKey());
                        mapTemp.put("taskName", taskList.get(0).getName());
                        mapTemp.put("taskCreateTime", taskCreateTime);
                        mapTemp.put("taskId", taskIds);
                        mapTemp.put("taskAssigneeId", assigneeIds);
                        mapTemp.put("taskAssignee", assigneeNames);
                        mapTemp.put(SysVariables.ITEMID, itemId);
                        mapTemp.put(SysVariables.LEVEL, level);
                        mapTemp.put(SysVariables.NUMBER, number);
                        mapTemp.put("isReminder", isReminder);
                        int chaosongNum = chaoSong4PositionApi
                            .countByUserIdAndProcessInstanceId(tenantId, positionId, processInstanceId).getData();
                        mapTemp.put("chaosongNum", chaosongNum);
                        mapTemp.put("status", 1);
                        mapTemp.put("taskDueDate", "");
                        formDataMap = formDataApi.getData(tenantId, itemId, processSerialNumber);
                        if (formDataMap.get("leaveType") != null) {
                            String leaveType = (String)formDataMap.get("leaveType");
                            for (ItemLeaveTypeEnum leaveTypeEnum : arr) {
                                if (leaveType.equals(leaveTypeEnum.getValue())) {
                                    formDataMap.put("leaveType", leaveTypeEnum.getName());
                                    break;
                                }
                            }
                        }
                        mapTemp.putAll(formDataMap);
                        mapTemp.put("processInstanceId", processInstanceId);
                        int speakInfoNum = speakInfoApi
                            .getNotReadCount(tenantId, Y9LoginUserHolder.getPersonId(), processInstanceId).getData();
                        mapTemp.put("speakInfoNum", speakInfoNum);

                        mapTemp.put("remindSetting", false);
                        RemindInstanceModel remindInstanceModel = remindInstanceApi.getRemindInstance(tenantId,
                            Y9LoginUserHolder.getPersonId(), processInstanceId);
                        if (remindInstanceModel != null) {// 流程实例是否设置消息提醒
                            mapTemp.put("remindSetting", true);
                        }

                        int countFollow =
                            officeFollow4PositionApi.countByProcessInstanceId(tenantId, positionId, processInstanceId);
                        mapTemp.put("follow", countFollow > 0);
                    } catch (Exception e) {
                        LOGGER.error("获取待办列表失败" + processInstanceId, e);
                    }
                    mapTemp.put("serialNumber", serialNumber + 1);
                    serialNumber += 1;
                    items.add(mapTemp);
                }
            } else {
                retMap = doingApi.searchListByUserIdAndProcessDefinitionKey(tenantId, positionId, processDefinitionKey,
                    searchTerm, page, rows);
                List<ProcessInstanceModel> list = (List<ProcessInstanceModel>)retMap.get("rows");
                ObjectMapper objectMapper = new ObjectMapper();
                List<ProcessInstanceModel> hpiModelList = objectMapper.convertValue(list, new TypeReference<>() {});
                int serialNumber = (page - 1) * rows;
                Map<String, Object> mapTemp;
                Map<String, Object> formDataMap;
                ProcessParamModel processParam;
                String processInstanceId = "";
                for (ProcessInstanceModel hpim : hpiModelList) {
                    mapTemp = new HashMap<>(16);
                    try {
                        processInstanceId = hpim.getId();
                        String processDefinitionId = hpim.getProcessDefinitionId();
                        List<TaskModel> taskList = taskApi.findByProcessInstanceId(tenantId, processInstanceId);
                        List<String> listTemp = getAssigneeIdsAndAssigneeNames(taskList);
                        String taskIds = listTemp.get(0), assigneeIds = listTemp.get(1),
                            assigneeNames = listTemp.get(2);
                        Boolean isReminder = String.valueOf(taskList.get(0).getPriority()).contains("5");
                        processParam = processParamApi.findByProcessInstanceId(tenantId, processInstanceId);
                        String processSerialNumber = processParam.getProcessSerialNumber();
                        String documentTitle =
                            StringUtils.isBlank(processParam.getTitle()) ? "无标题" : processParam.getTitle();
                        String level = processParam.getCustomLevel();
                        String number = processParam.getCustomNumber();
                        mapTemp.put("itemName", itemName);
                        mapTemp.put("processInstanceId", processInstanceId);
                        mapTemp.put("processDefinitionKey", processDefinitionKey);
                        mapTemp.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber);
                        mapTemp.put("processDefinitionId", processDefinitionId);
                        mapTemp.put(SysVariables.DOCUMENTTITLE, documentTitle);
                        mapTemp.put("taskDefinitionKey", taskList.get(0).getTaskDefinitionKey());
                        mapTemp.put("taskName", taskList.get(0).getName());
                        mapTemp.put("taskCreateTime", sdf.format(taskList.get(0).getCreateTime()));
                        mapTemp.put("taskId", taskIds);
                        mapTemp.put("taskAssigneeId", assigneeIds);
                        mapTemp.put("taskAssignee", assigneeNames);
                        mapTemp.put(SysVariables.ITEMID, itemId);
                        mapTemp.put(SysVariables.LEVEL, level);
                        mapTemp.put(SysVariables.NUMBER, number);
                        mapTemp.put("isReminder", isReminder);
                        int chaosongNum = chaoSong4PositionApi
                            .countByUserIdAndProcessInstanceId(tenantId, positionId, processInstanceId).getData();
                        mapTemp.put("chaosongNum", chaosongNum);
                        mapTemp.put("status", 1);
                        mapTemp.put("taskDueDate", "");
                        formDataMap = formDataApi.getData(tenantId, itemId, processSerialNumber);
                        mapTemp.putAll(formDataMap);
                        mapTemp.put("processInstanceId", processInstanceId);
                        int speakInfoNum = speakInfoApi
                            .getNotReadCount(tenantId, Y9LoginUserHolder.getPersonId(), processInstanceId).getData();
                        mapTemp.put("speakInfoNum", speakInfoNum);
                    } catch (Exception e) {
                        LOGGER.error("获取待办列表失败" + processInstanceId, e);
                    }
                    mapTemp.put("serialNumber", serialNumber + 1);
                    serialNumber += 1;
                    items.add(mapTemp);
                }
            }
            return Y9Page.success(page, Integer.parseInt(retMap.get("totalpages").toString()),
                Integer.parseInt(retMap.get("total").toString()), items, "获取列表成功");
        } catch (Exception e) {
            LOGGER.error("获取待办列表失败", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取列表失败");
    }

    @Override
    public Y9Page<Map<String, Object>> searchList(String itemId, String tableName, String searchMapStr, Integer page,
        Integer rows) {
        ItemPage<ActRuDetailModel> itemPage;
        try {
            List<Map<String, Object>> items = new ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String positionId = Y9LoginUserHolder.getPositionId(), tenantId = Y9LoginUserHolder.getTenantId();
            ItemModel item = item4PositionApi.getByItemId(tenantId, itemId);
            String processDefinitionKey = item.getWorkflowGuid(), itemName = item.getName();
            if (StringUtils.isBlank(searchMapStr)) {
                itemPage =
                    itemDoingApi.findByUserIdAndSystemName(tenantId, positionId, item.getSystemName(), page, rows);
            } else {
                itemPage = itemDoingApi.searchByUserIdAndSystemName(tenantId, positionId, item.getSystemName(),
                    tableName, searchMapStr, page, rows);
            }
            List<ActRuDetailModel> list = itemPage.getRows();
            ObjectMapper objectMapper = new ObjectMapper();
            List<ActRuDetailModel> hpiModelList = objectMapper.convertValue(list, new TypeReference<>() {});
            int serialNumber = (page - 1) * rows;
            Map<String, Object> mapTemp;
            Map<String, Object> formDataMap;
            // ProcessParamModel processParam = null;
            // ItemLeaveTypeEnum[] arr = ItemLeaveTypeEnum.values();
            String processInstanceId = "";
            for (ActRuDetailModel ardModel : hpiModelList) {
                mapTemp = new HashMap<>(16);
                try {
                    processInstanceId = ardModel.getProcessInstanceId();
                    String processSerialNumber = ardModel.getProcessSerialNumber();
                    mapTemp.put("processDefinitionKey", processDefinitionKey);
                    mapTemp.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber);
                    mapTemp.put("itemName", itemName);
                    mapTemp.put("taskCreateTime", sdf.format(ardModel.getLastTime()));
                    mapTemp.put(SysVariables.ITEMID, itemId);

                    List<TaskModel> taskList = taskApi.findByProcessInstanceId(tenantId, processInstanceId);
                    List<String> listTemp = getAssigneeIdsAndAssigneeNames(taskList);
                    String taskIds = listTemp.get(0), assigneeIds = listTemp.get(1), assigneeNames = listTemp.get(2);
                    Boolean isReminder = String.valueOf(taskList.get(0).getPriority()).contains("5");
                    mapTemp.put("processDefinitionId", taskList.get(0).getProcessDefinitionId());
                    mapTemp.put("taskDefinitionKey", taskList.get(0).getTaskDefinitionKey());
                    mapTemp.put("taskName", taskList.get(0).getName());
                    mapTemp.put("taskId", taskIds);
                    mapTemp.put("taskAssigneeId", assigneeIds);
                    mapTemp.put("taskAssignee", assigneeNames);
                    mapTemp.put("isReminder", isReminder);
                    int chaosongNum = chaoSong4PositionApi
                        .countByUserIdAndProcessInstanceId(tenantId, positionId, processInstanceId).getData();
                    mapTemp.put("chaosongNum", chaosongNum);
                    mapTemp.put("status", 1);
                    mapTemp.put("taskDueDate", "");
                    formDataMap = formDataApi.getData(tenantId, itemId, processSerialNumber);
                    /*if (formDataMap.get("leaveType") != null) {
                        String leaveType = (String)formDataMap.get("leaveType");
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
                    RemindInstanceModel remindInstanceModel = remindInstanceApi.getRemindInstance(tenantId,
                        Y9LoginUserHolder.getPersonId(), processInstanceId);
                    if (remindInstanceModel != null) {// 流程实例是否设置消息提醒
                        mapTemp.put("remindSetting", true);
                    }
                    int countFollow =
                        officeFollow4PositionApi.countByProcessInstanceId(tenantId, positionId, processInstanceId);
                    mapTemp.put("follow", countFollow > 0);
                } catch (Exception e) {
                    LOGGER.error("获取待办列表失败" + processInstanceId, e);
                }
                mapTemp.put("serialNumber", serialNumber + 1);
                serialNumber += 1;
                items.add(mapTemp);
            }
            return Y9Page.success(page, itemPage.getTotalpages(), itemPage.getTotal(), items, "获取列表成功");
        } catch (Exception e) {
            LOGGER.error("获取待办列表失败", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取列表失败");
    }

}
