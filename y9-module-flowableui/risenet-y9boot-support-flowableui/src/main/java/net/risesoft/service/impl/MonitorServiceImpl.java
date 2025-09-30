package net.risesoft.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

import net.risesoft.api.itemadmin.ChaoSongApi;
import net.risesoft.api.itemadmin.OfficeDoneInfoApi;
import net.risesoft.api.itemadmin.core.ItemApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.processadmin.IdentityApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.consts.processadmin.SysVariables;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.model.itemadmin.ChaoSongModel;
import net.risesoft.model.itemadmin.OfficeDoneInfoModel;
import net.risesoft.model.itemadmin.core.ItemModel;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.model.platform.org.Position;
import net.risesoft.model.processadmin.IdentityLinkModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.service.MonitorService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9Util;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class MonitorServiceImpl implements MonitorService {

    private final TaskApi taskApi;

    private final ItemApi itemApi;

    private final OrgUnitApi orgUnitApi;

    private final OfficeDoneInfoApi officeDoneInfoApi;

    private final ChaoSongApi chaoSongApi;

    private final IdentityApi identityApi;

    /**
     * 当并行的时候，会获取到多个task，为了并行时当前办理人显示多人，而不是显示多条记录，需要分开分别进行处理
     *
     * @return List<String>
     */
    private List<String> getAssigneeIdsAndAssigneeNames(List<TaskModel> taskList) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String taskIds = "", assigneeNames = "";
        List<String> list = new ArrayList<>();
        int i = 0;
        if (!taskList.isEmpty()) {
            for (TaskModel task : taskList) {
                if (StringUtils.isEmpty(taskIds)) {
                    taskIds = task.getId();
                    String assignee = task.getAssignee();
                    if (StringUtils.isNotBlank(assignee)) {
                        OrgUnit personTemp = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, assignee).getData();
                        if (personTemp != null) {
                            assigneeNames = personTemp.getName();
                            i += 1;
                        }
                    } else {
                        // 处理单实例未签收的当前办理人显示
                        List<IdentityLinkModel> iList =
                            identityApi.getIdentityLinksForTask(tenantId, task.getId()).getData();
                        if (!iList.isEmpty()) {
                            int j = 0;
                            for (IdentityLinkModel identityLink : iList) {
                                String assigneeId = identityLink.getUserId();
                                OrgUnit ownerUser =
                                    orgUnitApi.getOrgUnitPersonOrPosition(Y9LoginUserHolder.getTenantId(), assigneeId)
                                        .getData();
                                if (j < 5) {
                                    assigneeNames = Y9Util.genCustomStr(assigneeNames, ownerUser.getName(), "、");
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
                            // 并行时，领导选取时存在顺序，因此这里也存在顺序
                            OrgUnit personTemp = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, assignee).getData();
                            if (personTemp != null) {
                                // 并行时，领导选取时存在顺序，因此这里也存在顺序
                                assigneeNames = Y9Util.genCustomStr(assigneeNames, personTemp.getName(), "、");
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
        list.add(assigneeNames);
        return list;
    }

    /**
     * 当并行的时候，会获取到多个task，为了并行时当前办理人显示多人，而不是显示多条记录，需要分开分别进行处理
     *
     * @return List<String>
     */
    private List<String> getAssigneeIdsAndAssigneeNames1(List<TaskModel> taskList) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String userId = Y9LoginUserHolder.getPersonId();
        String taskIds = "", assigneeIds = "", assigneeNames = "", itembox = ItemBoxTypeEnum.DOING.getValue(),
            taskId = "";
        List<String> list = new ArrayList<>();
        int i = 0;
        for (TaskModel task : taskList) {
            if (StringUtils.isEmpty(taskIds)) {
                taskIds = task.getId();
                String assignee = task.getAssignee();
                if (StringUtils.isNotBlank(assignee)) {
                    assigneeIds = assignee;
                    OrgUnit personTemp = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, assignee).getData();
                    if (personTemp != null) {
                        assigneeNames = personTemp.getName();
                    }
                    i += 1;
                    if (assignee.contains(userId)) {
                        itembox = ItemBoxTypeEnum.TODO.getValue();
                        taskId = task.getId();
                    }
                } else {// 处理单实例未签收的当前办理人显示
                    List<IdentityLinkModel> iList =
                        identityApi.getIdentityLinksForTask(tenantId, task.getId()).getData();
                    if (!iList.isEmpty()) {
                        int j = 0;
                        for (IdentityLinkModel identityLink : iList) {
                            String assigneeId = identityLink.getUserId();
                            OrgUnit ownerUser =
                                orgUnitApi.getOrgUnitPersonOrPosition(Y9LoginUserHolder.getTenantId(), assigneeId)
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
                String assignee = task.getAssignee();
                if (StringUtils.isNotBlank(assignee)) {
                    if (i < 5) {
                        assigneeIds = Y9Util.genCustomStr(assigneeIds, assignee, SysVariables.COMMA);
                        OrgUnit personTemp = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, assignee).getData();
                        if (personTemp != null) {
                            assigneeNames = Y9Util.genCustomStr(assigneeNames, personTemp.getName(), "、");
                        }
                        i += 1;
                    }
                    if (assignee.contains(userId)) {
                        itembox = ItemBoxTypeEnum.TODO.getValue();
                        taskId = task.getId();
                    }
                }
            }
        }
        if (taskList.size() > 5) {
            assigneeNames += "等，共" + taskList.size() + "人";
        }
        list.add(taskIds);
        list.add(assigneeIds);
        list.add(assigneeNames);
        list.add(itembox);
        list.add(taskId);
        return list;
    }

    @Override
    public Y9Page<Map<String, Object>> pageDeptList(String itemId, String searchName, String userName, String state,
        String year, Integer page, Integer rows) {
        Y9Page<OfficeDoneInfoModel> y9Page;
        try {
            Position position = Y9LoginUserHolder.getPosition();
            String tenantId = Y9LoginUserHolder.getTenantId();
            y9Page = officeDoneInfoApi.searchAllByDeptId(tenantId, position.getParentId(), searchName, itemId, userName,
                state, year, page, rows);
            List<Map<String, Object>> items = new ArrayList<>();
            List<OfficeDoneInfoModel> list = y9Page.getRows();
            ObjectMapper objectMapper = new ObjectMapper();
            List<OfficeDoneInfoModel> hpiModelList = objectMapper.convertValue(list, new TypeReference<>() {});
            Map<String, Object> mapTemp;
            String processInstanceId, processDefinitionId, processSerialNumber, documentTitle, level, number;
            for (OfficeDoneInfoModel model : hpiModelList) {
                mapTemp = new HashMap<>(16);
                processInstanceId = model.getProcessInstanceId();
                try {
                    processDefinitionId = model.getProcessDefinitionId();
                    String startTime = model.getStartTime().substring(0, 16);
                    processSerialNumber = model.getProcessSerialNumber();
                    documentTitle = StringUtils.isBlank(model.getTitle()) ? "无标题" : model.getTitle();
                    level = model.getUrgency();
                    number = model.getDocNumber();
                    String completer = model.getUserComplete();
                    mapTemp.put("itemName", model.getItemName());
                    mapTemp.put(SysVariables.PROCESS_SERIAL_NUMBER, processSerialNumber);
                    mapTemp.put(SysVariables.DOCUMENT_TITLE, documentTitle);
                    mapTemp.put("processInstanceId", processInstanceId);
                    mapTemp.put("processDefinitionId", processDefinitionId);
                    mapTemp.put("processDefinitionKey", model.getProcessDefinitionKey());
                    mapTemp.put("startTime", startTime);
                    mapTemp.put("endTime",
                        StringUtils.isBlank(model.getEndTime()) ? "--" : model.getEndTime().substring(0, 16));
                    mapTemp.put("taskDefinitionKey", "");
                    mapTemp.put("taskAssignee", completer);
                    mapTemp.put("creatUserName", model.getCreatUserName());
                    mapTemp.put("itemId", model.getItemId());
                    mapTemp.put("level", StringUtils.defaultString(level));
                    mapTemp.put("number", StringUtils.defaultString(number));
                    mapTemp.put("itembox", ItemBoxTypeEnum.DONE.getValue());
                    if (StringUtils.isBlank(model.getEndTime())) {
                        List<TaskModel> taskList =
                            taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                        List<String> listTemp = getAssigneeIdsAndAssigneeNames1(taskList);
                        String taskIds = listTemp.get(0), assigneeNames = listTemp.get(2);
                        mapTemp.put("taskDefinitionKey", taskList.get(0).getTaskDefinitionKey());
                        mapTemp.put("taskId",
                            listTemp.get(3).equals(ItemBoxTypeEnum.DOING.getValue()) ? taskIds : listTemp.get(4));
                        mapTemp.put("taskAssignee", assigneeNames);
                        mapTemp.put("itembox", new HashMap<String, String>(16));
                    }
                } catch (Exception e) {
                    LOGGER.error("获取列表失败{}", processInstanceId, e);
                }
                items.add(mapTemp);
            }
            return Y9Page.success(page, y9Page.getTotalPages(), y9Page.getTotal(), items, "获取列表成功");
        } catch (Exception e) {
            LOGGER.error("获取列表失败", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取列表失败");
    }

    @Override
    public Y9Page<Map<String, Object>> pageMonitorBanjianList(String searchName, String itemId, String userName,
        String state, String year, Integer page, Integer rows) {
        Y9Page<OfficeDoneInfoModel> y9Page;
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            y9Page = officeDoneInfoApi.searchAllList(tenantId, searchName, itemId, userName, state, year, page, rows);
            List<Map<String, Object>> items = new ArrayList<>();
            List<OfficeDoneInfoModel> hpiModelList = y9Page.getRows();
            ObjectMapper objectMapper = new ObjectMapper();
            List<OfficeDoneInfoModel> hpiList = objectMapper.convertValue(hpiModelList, new TypeReference<>() {});
            int serialNumber = (page - 1) * rows;
            Map<String, Object> mapTemp;
            String processInstanceId;
            for (OfficeDoneInfoModel hpim : hpiList) {
                mapTemp = new HashMap<>(16);
                processInstanceId = hpim.getProcessInstanceId();
                try {
                    String processDefinitionId = hpim.getProcessDefinitionId();
                    String startTime = hpim.getStartTime().substring(0, 16);
                    String processSerialNumber = hpim.getProcessSerialNumber();
                    String documentTitle = StringUtils.isBlank(hpim.getTitle()) ? "无标题" : hpim.getTitle();
                    String level = hpim.getUrgency();
                    String number = hpim.getDocNumber();
                    String completer = hpim.getUserComplete();
                    mapTemp.put("itemName", hpim.getItemName());
                    mapTemp.put(SysVariables.PROCESS_SERIAL_NUMBER, processSerialNumber);
                    mapTemp.put(SysVariables.DOCUMENT_TITLE, documentTitle);
                    mapTemp.put("processInstanceId", processInstanceId);
                    mapTemp.put("processDefinitionId", processDefinitionId);
                    mapTemp.put("processDefinitionKey", hpim.getProcessDefinitionKey());
                    mapTemp.put("startTime", startTime);
                    mapTemp.put("endTime",
                        StringUtils.isBlank(hpim.getEndTime()) ? "--" : hpim.getEndTime().substring(0, 16));
                    mapTemp.put("taskDefinitionKey", "");
                    mapTemp.put("taskAssignee", completer);
                    mapTemp.put("creatUserName", hpim.getCreatUserName());
                    mapTemp.put("itemId", hpim.getItemId());
                    mapTemp.put("level", level == null ? "" : level);
                    mapTemp.put("number", number == null ? "" : number);
                    mapTemp.put("itembox", ItemBoxTypeEnum.DONE.getValue());
                    if (StringUtils.isBlank(hpim.getEndTime())) {
                        List<TaskModel> taskList =
                            taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                        List<String> listTemp = getAssigneeIdsAndAssigneeNames1(taskList);
                        String taskIds = listTemp.get(0), assigneeNames = listTemp.get(2);
                        mapTemp.put("taskDefinitionKey", taskList.get(0).getTaskDefinitionKey());
                        mapTemp.put("taskId",
                            listTemp.get(3).equals(ItemBoxTypeEnum.DOING.getValue()) ? taskIds : listTemp.get(4));
                        mapTemp.put("taskAssignee", assigneeNames);
                        mapTemp.put("itembox", listTemp.get(3));
                    }
                } catch (Exception e) {
                    LOGGER.error("获取任务信息失败" + processInstanceId, e);
                }
                mapTemp.put("serialNumber", serialNumber + 1);
                serialNumber += 1;
                items.add(mapTemp);
            }
            return Y9Page.success(page, y9Page.getTotalPages(), y9Page.getTotal(), items, "获取列表成功");
        } catch (Exception e) {
            LOGGER.error("获取列表失败", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取列表失败");
    }

    @Override
    public Y9Page<ChaoSongModel> pageMonitorChaosongList(String searchName, String itemId, String senderName,
        String userName, String state, String year, Integer page, Integer rows) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            return chaoSongApi.searchAllList(tenantId, searchName, itemId, senderName, userName, state, year, page,
                rows);
        } catch (Exception e) {
            LOGGER.error("获取列表失败", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取列表失败");
    }

    @Override
    public Y9Page<Map<String, Object>> pageMonitorDoingList(String itemId, String searchTerm, Integer page,
        Integer rows) {
        Y9Page<OfficeDoneInfoModel> y9Page;
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            ItemModel item = itemApi.getByItemId(tenantId, itemId).getData();
            String processDefinitionKey = item.getWorkflowGuid(), itemName = item.getName();
            // if (StringUtils.isBlank(searchTerm)) {
            // retMap = monitorApi.getDoingListByProcessDefinitionKey(tenantId, processDefinitionKey, page, rows);
            // } else {
            // retMap = monitorApi.searchDoingListByProcessDefinitionKey(tenantId, processDefinitionKey,
            // searchTerm, page, rows);
            // }
            y9Page = officeDoneInfoApi.searchByItemId(tenantId, searchTerm, itemId, ItemBoxTypeEnum.TODO.getValue(), "",
                "", page, rows);
            List<Map<String, Object>> items = new ArrayList<>();
            List<OfficeDoneInfoModel> hpiModelList = y9Page.getRows();
            ObjectMapper objectMapper = new ObjectMapper();
            List<OfficeDoneInfoModel> hpiList = objectMapper.convertValue(hpiModelList, new TypeReference<>() {});
            // List<HistoricProcessInstanceModel> list = (List<HistoricProcessInstanceModel>)retMap.get("rows");
            // ObjectMapper objectMapper = new ObjectMapper();
            // List<HistoricProcessInstanceModel> hpiModelList = objectMapper.convertValue(list, new
            // TypeReference<List<HistoricProcessInstanceModel>>() {});
            int serialNumber = (page - 1) * rows;
            Map<String, Object> mapTemp;
            String processInstanceId;
            // ProcessParamModel processParam = null;
            for (OfficeDoneInfoModel model : hpiList) {
                mapTemp = new HashMap<>(16);
                processInstanceId = model.getProcessInstanceId();
                try {
                    String processDefinitionId = model.getProcessDefinitionId();
                    mapTemp.put("itemName", itemName);
                    mapTemp.put("processInstanceId", processInstanceId);
                    mapTemp.put("processDefinitionKey", processDefinitionKey);
                    // processParam = processParamApi.findByProcessInstanceId(tenantId, processInstanceId);
                    String processSerialNumber = model.getProcessSerialNumber();
                    String documentTitle = StringUtils.isBlank(model.getTitle()) ? "无标题" : model.getTitle();
                    String level = model.getUrgency();
                    String number = model.getDocNumber();
                    mapTemp.put("creatUserName", model.getCreatUserName());
                    mapTemp.put(SysVariables.PROCESS_SERIAL_NUMBER, processSerialNumber);
                    mapTemp.put("processDefinitionId", processDefinitionId);
                    mapTemp.put(SysVariables.DOCUMENT_TITLE, documentTitle);
                    mapTemp.put("itemId", itemId);
                    mapTemp.put("level", level == null ? "" : level);
                    mapTemp.put("number", number == null ? "" : number);
                    mapTemp.put("status", 1);
                    mapTemp.put("taskDueDate", "");

                    List<TaskModel> taskList = taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                    List<String> listTemp = getAssigneeIdsAndAssigneeNames(taskList);
                    String taskIds = listTemp.get(0), assigneeNames = listTemp.get(1);
                    Boolean isReminder = String.valueOf(taskList.get(0).getPriority()).contains("5");
                    mapTemp.put("taskDefinitionKey", taskList.get(0).getTaskDefinitionKey());
                    mapTemp.put("taskName", taskList.get(0).getName());
                    mapTemp.put("taskCreateTime", model.getStartTime().substring(0, 16));
                    mapTemp.put("taskId", taskIds);
                    mapTemp.put("taskAssignee", assigneeNames);
                    mapTemp.put("isReminder", isReminder);
                } catch (Exception e) {
                    LOGGER.error("获取列表失败{}", processInstanceId, e);
                }
                mapTemp.put("serialNumber", serialNumber + 1);
                serialNumber += 1;
                items.add(mapTemp);
            }
            return Y9Page.success(page, y9Page.getTotalPages(), y9Page.getTotal(), items, "获取列表成功");
        } catch (Exception e) {
            LOGGER.error("获取列表失败", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取列表失败");
    }

    @Override
    public Y9Page<Map<String, Object>> pageMonitorDoneList(String itemId, String searchTerm, Integer page,
        Integer rows) {
        Y9Page<OfficeDoneInfoModel> y9Page;
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            ItemModel item = itemApi.getByItemId(tenantId, itemId).getData();
            String processDefinitionKey = item.getWorkflowGuid(), itemName = item.getName();
            y9Page = officeDoneInfoApi.searchByItemId(tenantId, searchTerm, itemId, ItemBoxTypeEnum.DONE.getValue(), "",
                "", page, rows);
            List<Map<String, Object>> items = new ArrayList<>();
            List<OfficeDoneInfoModel> hpiModelList = y9Page.getRows();
            ObjectMapper objectMapper = new ObjectMapper();
            List<OfficeDoneInfoModel> hpiList = objectMapper.convertValue(hpiModelList, new TypeReference<>() {});
            int serialNumber = (page - 1) * rows;
            Map<String, Object> mapTemp;
            String processInstanceId;
            for (OfficeDoneInfoModel hpim : hpiList) {
                mapTemp = new HashMap<>(16);
                processInstanceId = hpim.getProcessInstanceId();
                try {
                    String processDefinitionId = hpim.getProcessDefinitionId();
                    String startTime = hpim.getStartTime().substring(0, 16),
                        endTime = hpim.getEndTime().substring(0, 16);
                    String processSerialNumber = hpim.getProcessSerialNumber();
                    String documentTitle = StringUtils.isBlank(hpim.getTitle()) ? "无标题" : hpim.getTitle();
                    String level = hpim.getUrgency();
                    String number = hpim.getDocNumber();
                    String completer = hpim.getUserComplete();
                    mapTemp.put("itemName", itemName);
                    mapTemp.put(SysVariables.PROCESS_SERIAL_NUMBER, processSerialNumber);
                    mapTemp.put(SysVariables.DOCUMENT_TITLE, documentTitle);
                    mapTemp.put("processInstanceId", processInstanceId);
                    mapTemp.put("processDefinitionId", processDefinitionId);
                    mapTemp.put("processDefinitionKey", processDefinitionKey);
                    mapTemp.put("creatUserName", hpim.getCreatUserName());
                    mapTemp.put("startTime", startTime);
                    mapTemp.put("endTime", endTime);
                    mapTemp.put("taskDefinitionKey", "");
                    mapTemp.put("user4Complete", completer);
                    mapTemp.put("itemId", itemId);
                    mapTemp.put("level", level);
                    mapTemp.put("number", number);
                } catch (Exception e) {
                    LOGGER.error("获取列表失败" + processInstanceId, e);
                }
                mapTemp.put("serialNumber", serialNumber + 1);
                serialNumber += 1;
                items.add(mapTemp);
            }
            return Y9Page.success(page, y9Page.getTotalPages(), y9Page.getTotal(), items, "获取列表成功");
        } catch (Exception e) {
            LOGGER.error("获取列表失败", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取列表失败");
    }
}