package net.risesoft.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.FormDataApi;
import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.api.itemadmin.TaskVariableApi;
import net.risesoft.api.itemadmin.position.ChaoSong4PositionApi;
import net.risesoft.api.itemadmin.position.Item4PositionApi;
import net.risesoft.api.itemadmin.position.OfficeDoneInfo4PositionApi;
import net.risesoft.api.itemadmin.position.OfficeFollow4PositionApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.api.processadmin.DoingApi;
import net.risesoft.api.processadmin.HistoricTaskApi;
import net.risesoft.api.processadmin.IdentityApi;
import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.ProcessTodoApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.api.processadmin.VariableApi;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.model.itemadmin.ChaoSongModel;
import net.risesoft.model.itemadmin.ItemModel;
import net.risesoft.model.itemadmin.OfficeDoneInfoModel;
import net.risesoft.model.itemadmin.OfficeFollowModel;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.model.itemadmin.TaskVariableModel;
import net.risesoft.model.platform.Position;
import net.risesoft.model.processadmin.HistoricTaskInstanceModel;
import net.risesoft.model.processadmin.IdentityLinkModel;
import net.risesoft.model.processadmin.ProcessInstanceModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.service.WorkList4ddyjsService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;
import net.risesoft.y9.util.Y9Util;

@Slf4j
@RequiredArgsConstructor
@Service(value = "workList4ddyjsService")
@Transactional(readOnly = true)
public class WorkList4ddyjsServiceImpl implements WorkList4ddyjsService {

    private final DoingApi doingApi;

    private final TaskApi taskApi;

    private final Item4PositionApi item4PositionApi;

    private final HistoricTaskApi historicTaskApi;

    private final PositionApi positionApi;

    private final ProcessParamApi processParamApi;

    private final OfficeFollow4PositionApi officeFollow4PositionApi;

    private final IdentityApi identityApi;

    private final OfficeDoneInfo4PositionApi officeDoneInfo4PositionApi;

    private final ProcessTodoApi processTodoApi;

    private final VariableApi variableApi;

    private final ProcessDefinitionApi processDefinitionApi;

    private final TaskVariableApi taskvariableApi;

    private final FormDataApi formDataApi;
    private final ChaoSong4PositionApi chaoSong4PositionApi;
    @Value("${y9.common.flowableBaseUrl}")
    private String flowableBaseUrl;

    @SuppressWarnings({"unchecked"})
    @Override
    public Y9Page<Map<String, Object>> doingList(String itemId, String searchItemId, String searchTerm, Integer page,
        Integer rows) {
        Map<String, Object> retMap;
        try {
            List<Map<String, Object>> items = new ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String positionId = Y9LoginUserHolder.getPositionId(), tenantId = Y9LoginUserHolder.getTenantId();
            ItemModel item = item4PositionApi.getByItemId(tenantId, itemId).getData();
            if (StringUtils.isBlank(searchTerm)) {
                if (StringUtils.isNotBlank(searchItemId)) {
                    ItemModel item1 = item4PositionApi.getByItemId(tenantId, searchItemId).getData();
                    retMap = doingApi.getListByUserIdAndProcessDefinitionKey(tenantId, positionId,
                        item1.getWorkflowGuid(), page, rows);
                } else {
                    retMap =
                        doingApi.getListByUserIdAndSystemName(tenantId, positionId, item.getSystemName(), page, rows);
                }
                List<ProcessInstanceModel> list = (List<ProcessInstanceModel>)retMap.get("rows");
                ObjectMapper objectMapper = new ObjectMapper();
                List<ProcessInstanceModel> hpiModelList = objectMapper.convertValue(list, new TypeReference<>() {});
                int serialNumber = (page - 1) * rows;
                Map<String, Object> mapTemp;
                ProcessParamModel processParam;
                String processInstanceId = "";
                for (ProcessInstanceModel hpim : hpiModelList) {// 以办理时间排序
                    mapTemp = new HashMap<>(16);
                    try {
                        processInstanceId = hpim.getId();
                        String processDefinitionId = hpim.getProcessDefinitionId();
                        String taskCreateTime = sdf.format(hpim.getStartTime());
                        List<TaskModel> taskList =
                            taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                        List<String> listTemp = getAssigneeIdsAndAssigneeNames(taskList);
                        String taskIds = listTemp.get(0), assigneeIds = listTemp.get(1),
                            assigneeNames = listTemp.get(2);
                        Boolean isReminder = String.valueOf(taskList.get(0).getPriority()).contains("5");
                        processParam = processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                        String processSerialNumber = processParam.getProcessSerialNumber();
                        String documentTitle =
                            StringUtils.isBlank(processParam.getTitle()) ? "无标题" : processParam.getTitle();
                        String level = processParam.getCustomLevel();
                        String number = processParam.getCustomNumber();
                        mapTemp.put("itemId", processParam.getItemId());
                        mapTemp.put("itemName", processParam.getItemName());
                        mapTemp.put("processDefinitionKey", hpim.getProcessDefinitionKey());
                        mapTemp.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber);
                        mapTemp.put("processDefinitionId", processDefinitionId);
                        mapTemp.put("title", documentTitle);
                        mapTemp.put("taskDefinitionKey", taskList.get(0).getTaskDefinitionKey());
                        mapTemp.put("taskName", taskList.get(0).getName());
                        mapTemp.put("taskCreateTime", taskCreateTime);
                        mapTemp.put("taskId", taskIds);
                        mapTemp.put("taskAssigneeId", assigneeIds);
                        mapTemp.put("taskAssignee", assigneeNames);
                        mapTemp.put(SysVariables.LEVEL, level);
                        mapTemp.put(SysVariables.NUMBER, number);
                        mapTemp.put("isReminder", isReminder);
                        mapTemp.put("chaosongNum", 0);
                        mapTemp.put("status", 1);
                        mapTemp.put("taskDueDate", "");
                        mapTemp.put("processInstanceId", processInstanceId);
                        mapTemp.put("speakInfoNum", 0);
                        mapTemp.put("remindSetting", false);
                        mapTemp.put("meeting", false);
                        if ("gongwenguanli".equals(item.getSystemName())) {
                            OfficeDoneInfoModel officeDoneInfo = officeDoneInfo4PositionApi
                                .findByProcessInstanceId(tenantId, processInstanceId).getData();
                            mapTemp.put("meeting",
                                officeDoneInfo.getMeeting() != null && "1".equals(officeDoneInfo.getMeeting()));
                        }
                    } catch (Exception e) {
                        LOGGER.error("获取待办列表失败" + processInstanceId, e);
                    }
                    mapTemp.put("serialNumber", serialNumber + 1);
                    serialNumber += 1;
                    items.add(mapTemp);
                }
            } else {
                if (StringUtils.isNotBlank(searchItemId)) {
                    ItemModel item1 = item4PositionApi.getByItemId(tenantId, searchItemId).getData();
                    retMap = doingApi.searchListByUserIdAndProcessDefinitionKey(tenantId, positionId,
                        item1.getWorkflowGuid(), searchTerm, page, rows);
                } else {
                    retMap = doingApi.searchListByUserIdAndSystemName(tenantId, positionId, item.getSystemName(),
                        searchTerm, page, rows);
                }
                List<ProcessInstanceModel> list = (List<ProcessInstanceModel>)retMap.get("rows");
                ObjectMapper objectMapper = new ObjectMapper();
                List<ProcessInstanceModel> hpiModelList = objectMapper.convertValue(list, new TypeReference<>() {});
                int serialNumber = (page - 1) * rows;
                Map<String, Object> mapTemp;
                ProcessParamModel processParam;
                String processInstanceId = "";
                for (ProcessInstanceModel hpim : hpiModelList) {
                    mapTemp = new HashMap<>(16);
                    try {
                        processInstanceId = hpim.getId();
                        String processDefinitionId = hpim.getProcessDefinitionId();
                        List<TaskModel> taskList =
                            taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                        List<String> listTemp = getAssigneeIdsAndAssigneeNames(taskList);
                        String taskIds = listTemp.get(0), assigneeIds = listTemp.get(1),
                            assigneeNames = listTemp.get(2);
                        Boolean isReminder = String.valueOf(taskList.get(0).getPriority()).contains("5");
                        processParam = processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                        String processSerialNumber = processParam.getProcessSerialNumber();
                        String documentTitle =
                            StringUtils.isBlank(processParam.getTitle()) ? "无标题" : processParam.getTitle();
                        String level = processParam.getCustomLevel();
                        String number = processParam.getCustomNumber();
                        mapTemp.put("itemId", processParam.getItemId());
                        mapTemp.put("itemName", processParam.getItemName());
                        mapTemp.put("processInstanceId", processInstanceId);
                        mapTemp.put("processDefinitionKey", hpim.getProcessDefinitionKey());
                        mapTemp.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber);
                        mapTemp.put("processDefinitionId", processDefinitionId);
                        mapTemp.put(SysVariables.DOCUMENTTITLE, documentTitle);
                        mapTemp.put("taskDefinitionKey", taskList.get(0).getTaskDefinitionKey());
                        mapTemp.put("taskName", taskList.get(0).getName());
                        mapTemp.put("taskCreateTime", sdf.format(hpim.getStartTime()));
                        mapTemp.put("taskId", taskIds);
                        mapTemp.put("taskAssigneeId", assigneeIds);
                        mapTemp.put("taskAssignee", assigneeNames);
                        mapTemp.put(SysVariables.LEVEL, level);
                        mapTemp.put(SysVariables.NUMBER, number);
                        mapTemp.put("isReminder", isReminder);
                        mapTemp.put("chaosongNum", 0);
                        mapTemp.put("status", 1);
                        mapTemp.put("taskDueDate", "");
                        mapTemp.put("speakInfoNum", 0);

                        mapTemp.put("meeting", false);
                        if ("gongwenguanli".equals(item.getSystemName())) {
                            OfficeDoneInfoModel officeDoneInfo = officeDoneInfo4PositionApi
                                .findByProcessInstanceId(tenantId, processInstanceId).getData();
                            mapTemp.put("meeting",
                                officeDoneInfo.getMeeting() != null && officeDoneInfo.getMeeting().equals("1"));
                        }
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
    public Y9Page<Map<String, Object>> doneList(String itemId, String searchItemId, String searchTerm, Integer page,
        Integer rows) {
        Y9Page<OfficeDoneInfoModel> y9Page;
        String userId = Y9LoginUserHolder.getPositionId(), tenantId = Y9LoginUserHolder.getTenantId();
        ItemModel item = item4PositionApi.getByItemId(tenantId, itemId).getData();
        if (StringUtils.isNotBlank(searchItemId)) {
            y9Page = officeDoneInfo4PositionApi.searchByPositionId(tenantId, userId, searchTerm, searchItemId, "", "",
                page, rows);
        } else {
            y9Page = officeDoneInfo4PositionApi.searchByPositionIdAndSystemName(tenantId, userId, searchTerm,
                item.getSystemName(), "", "", page, rows);
        }
        List<Map<String, Object>> items = new ArrayList<>();
        List<OfficeDoneInfoModel> list = y9Page.getRows();
        ObjectMapper objectMapper = new ObjectMapper();
        List<OfficeDoneInfoModel> hpiModelList = objectMapper.convertValue(list, new TypeReference<>() {});
        int serialNumber = (page - 1) * rows;
        Map<String, Object> mapTemp;
        String processInstanceId;
        for (OfficeDoneInfoModel hpim : hpiModelList) {
            mapTemp = new HashMap<>(16);
            processInstanceId = hpim.getProcessInstanceId();
            try {
                String processDefinitionId = hpim.getProcessDefinitionId();
                String startTime = hpim.getStartTime().substring(0, 16), endTime = hpim.getEndTime().substring(0, 16);
                String processSerialNumber = hpim.getProcessSerialNumber();
                String documentTitle = StringUtils.isBlank(hpim.getTitle()) ? "无标题" : hpim.getTitle();
                String level = hpim.getUrgency();
                String number = hpim.getDocNumber();
                String completer = StringUtils.isBlank(hpim.getUserComplete()) ? "无" : hpim.getUserComplete();
                mapTemp.put("itemId", hpim.getItemId());
                mapTemp.put("itemName", hpim.getItemName());
                mapTemp.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber);
                mapTemp.put("title", documentTitle);
                mapTemp.put("processDefinitionId", processDefinitionId);
                mapTemp.put("processDefinitionKey", hpim.getProcessDefinitionId());
                mapTemp.put("startTime", startTime);
                mapTemp.put("endTime", endTime);
                mapTemp.put("taskDefinitionKey", "");
                mapTemp.put("user4Complete", completer);
                mapTemp.put("level", level);
                mapTemp.put("number", number);
                mapTemp.put("chaosongNum", 0);
                mapTemp.put("processInstanceId", processInstanceId);
                // int countFollow = officeFollow4PositionApi.countByProcessInstanceId(tenantId, userId,
                // processInstanceId);
                // mapTemp.put("follow", countFollow > 0 ? true : false);
                mapTemp.put("meeting", false);
                if (item.getSystemName().equals("gongwenguanli")) {
                    mapTemp.put("meeting", hpim.getMeeting() != null && hpim.getMeeting().equals("1"));
                }
            } catch (Exception e) {
                LOGGER.error("获取待办列表失败" + processInstanceId, e);
            }
            mapTemp.put("serialNumber", serialNumber + 1);
            serialNumber += 1;
            items.add(mapTemp);
        }
        return Y9Page.success(page, y9Page.getTotalPages(), y9Page.getTotal(), items, "获取列表成功");
    }

    @Override
    public Y9Page<OfficeFollowModel> followList(String itemId, String searchTerm, Integer page, Integer rows) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        ItemModel item = item4PositionApi.getByItemId(tenantId, itemId).getData();
        return officeFollow4PositionApi.getFollowListBySystemName(tenantId, Y9LoginUserHolder.getPositionId(),
            item.getSystemName(), searchTerm, page, rows);
    }

    /**
     * 当并行的时候，会获取到多个task，为了并行时当前办理人显示多人，而不是显示多条记录，需要分开分别进行处理
     *
     * @return List<String>
     */
    private List<String> getAssigneeIdsAndAssigneeNames(List<TaskModel> taskList) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String taskIds = "";
        String assigneeIds = "";
        String assigneeNames = "";
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
                        List<IdentityLinkModel> iList =
                            identityApi.getIdentityLinksForTask(tenantId, task.getId()).getData();
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

    private List<String> getAssigneeIdsAndAssigneeNames1(List<TaskModel> taskList) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String userId = Y9LoginUserHolder.getPositionId();
        String taskIds = "";
        String assigneeIds = "";
        String assigneeNames = "";
        String itembox = ItemBoxTypeEnum.DOING.getValue();
        String taskId = "";
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
                    String assignee = task.getAssignee();
                    if (StringUtils.isNotBlank(assignee)) {
                        if (i < 5) {
                            assigneeIds = Y9Util.genCustomStr(assigneeIds, assignee, SysVariables.COMMA);
                            Position personTemp = positionApi.get(tenantId, assignee).getData();
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
        }
        list.add(taskIds);
        list.add(assigneeIds);
        list.add(assigneeNames);
        list.add(itembox);
        list.add(taskId);
        return list;
    }

    @Override
    public Y9Page<Map<String, Object>> getMeetingList(String userName, String deptName, String title,
        String meetingType, Integer page, Integer rows) {
        Y9Page<OfficeDoneInfoModel> y9Page;
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            y9Page =
                officeDoneInfo4PositionApi.getMeetingList(tenantId, userName, deptName, title, meetingType, page, rows);
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
                    String startTime = hpim.getStartTime().substring(0, 10);
                    String processSerialNumber = hpim.getProcessSerialNumber();
                    String documentTitle = StringUtils.isBlank(hpim.getTitle()) ? "无标题" : hpim.getTitle();
                    String level = hpim.getUrgency();
                    String number = hpim.getDocNumber();
                    String completer = hpim.getUserComplete();
                    mapTemp.put("itemName", hpim.getItemName());
                    mapTemp.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber);
                    mapTemp.put(SysVariables.DOCUMENTTITLE, documentTitle);
                    mapTemp.put("processInstanceId", processInstanceId);
                    mapTemp.put("processDefinitionId", processDefinitionId);
                    mapTemp.put("processDefinitionKey", hpim.getProcessDefinitionKey());
                    mapTemp.put("startTime", startTime);
                    mapTemp.put("endTime",
                        StringUtils.isBlank(hpim.getEndTime()) ? "--" : hpim.getEndTime().substring(0, 16));
                    mapTemp.put("taskDefinitionKey", "");
                    mapTemp.put("taskAssignee", completer);

                    mapTemp.put("deptName", hpim.getDeptName());
                    mapTemp.put("meetingType", hpim.getMeetingType());

                    mapTemp.put("creatUserName", hpim.getCreatUserName());
                    mapTemp.put("itemId", hpim.getItemId());
                    mapTemp.put("level", level == null ? "" : level);
                    mapTemp.put("number", number == null ? "" : number);
                    mapTemp.put("itembox", ItemBoxTypeEnum.DONE.getValue());
                    if (StringUtils.isBlank(hpim.getEndTime())) {
                        List<TaskModel> taskList =
                            taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                        List<String> listTemp = getAssigneeIdsAndAssigneeNames1(taskList);
                        String taskIds = listTemp.get(0), assigneeIds = listTemp.get(1),
                            assigneeNames = listTemp.get(2);
                        mapTemp.put("taskDefinitionKey", taskList.get(0).getTaskDefinitionKey());
                        mapTemp.put("taskId",
                            listTemp.get(3).equals(ItemBoxTypeEnum.DOING.getValue()) ? taskIds : listTemp.get(4));
                        mapTemp.put("taskAssigneeId", assigneeIds);
                        mapTemp.put("taskAssignee", assigneeNames);
                        mapTemp.put("itembox", listTemp.get(3));
                    }
                    mapTemp.put("beizhu", "");
                    Map<String, Object> formDataMap =
                        formDataApi.getData(tenantId, hpim.getItemId(), processSerialNumber).getData();
                    if (formDataMap.get("beizhu") != null) {
                        mapTemp.put("beizhu", formDataMap.get("beizhu"));
                    }
                } catch (Exception e) {
                    LOGGER.error("获取会议列表失败" + processInstanceId, e);
                }
                mapTemp.put("serialNumber", serialNumber + 1);
                serialNumber += 1;
                items.add(mapTemp);
            }
            return Y9Page.success(page, y9Page.getTotalPages(), y9Page.getTotal(), items, "获取列表成功");
        } catch (Exception e) {
            LOGGER.error("获取会议列表失败", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取列表失败");
    }

    @SuppressWarnings("unchecked")
    @Override
    public Y9Page<Map<String, Object>> homeDoingList(Integer page, Integer rows) {
        try {
            List<Map<String, Object>> items = new ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String positionId = Y9LoginUserHolder.getPositionId();
            String tenantId = Y9LoginUserHolder.getTenantId();
            Map<String, Object> retMap = doingApi.getListByUserId(tenantId, positionId, page, rows);
            if (retMap == null) {
                return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取列表失败");
            }
            List<ProcessInstanceModel> list = (List<ProcessInstanceModel>)retMap.get("rows");
            ObjectMapper objectMapper = new ObjectMapper();
            List<ProcessInstanceModel> hpiModelList = objectMapper.convertValue(list, new TypeReference<>() {});
            int serialNumber = (page - 1) * rows;
            Map<String, Object> mapTemp;
            ProcessParamModel processParam;
            String processInstanceId = "";
            for (ProcessInstanceModel hpim : hpiModelList) {// 以办理时间排序
                mapTemp = new HashMap<>(16);
                try {
                    processInstanceId = hpim.getId();
                    String processDefinitionId = hpim.getProcessDefinitionId();
                    String taskCreateTime = sdf.format(hpim.getStartTime());
                    List<TaskModel> taskList = taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                    List<String> listTemp = getAssigneeIdsAndAssigneeNames(taskList);
                    String taskIds = listTemp.get(0);
                    String assigneeIds = listTemp.get(1);
                    String assigneeNames = listTemp.get(2);
                    Boolean isReminder = String.valueOf(taskList.get(0).getPriority()).contains("5");
                    processParam = processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                    String processSerialNumber = processParam.getProcessSerialNumber();
                    String documentTitle =
                        StringUtils.isBlank(processParam.getTitle()) ? "无标题" : processParam.getTitle();
                    String level = processParam.getCustomLevel();
                    String number = processParam.getCustomNumber();
                    mapTemp.put("itemId", processParam.getItemId());
                    mapTemp.put("itemName", processParam.getItemName());
                    mapTemp.put("processDefinitionKey", hpim.getProcessDefinitionKey());
                    mapTemp.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber);
                    mapTemp.put("processDefinitionId", processDefinitionId);
                    mapTemp.put("title", documentTitle);
                    mapTemp.put("taskDefinitionKey", taskList.get(0).getTaskDefinitionKey());
                    mapTemp.put("taskName", taskList.get(0).getName());
                    mapTemp.put("taskCreateTime", taskCreateTime);
                    mapTemp.put("taskId", taskIds);
                    mapTemp.put("taskAssigneeId", assigneeIds);
                    mapTemp.put("taskAssignee", assigneeNames);
                    mapTemp.put(SysVariables.LEVEL, level);
                    mapTemp.put(SysVariables.NUMBER, number);
                    mapTemp.put("isReminder", isReminder);
                    String url =
                        flowableBaseUrl + "/index/edit?itemId=" + processParam.getItemId() + "&processSerialNumber="
                            + processSerialNumber + "&itembox=doing&taskId=" + taskIds + "&processInstanceId="
                            + processInstanceId + "&listType=searchList&systemName=" + processParam.getSystemName();
                    mapTemp.put("url", url);
                    mapTemp.put("chaosongNum", 0);
                    mapTemp.put("status", 1);
                    mapTemp.put("taskDueDate", "");
                    mapTemp.put("processInstanceId", processInstanceId);
                    mapTemp.put("speakInfoNum", 0);
                    mapTemp.put("remindSetting", false);
                    mapTemp.put("follow", false);
                    mapTemp.put("meeting", false);
                    OfficeDoneInfoModel officeDoneInfo =
                        officeDoneInfo4PositionApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                    mapTemp.put("meeting",
                        officeDoneInfo.getMeeting() != null && officeDoneInfo.getMeeting().equals("1"));
                } catch (Exception e) {
                    LOGGER.error("获取列表失败" + processInstanceId, e);
                }
                mapTemp.put("serialNumber", serialNumber + 1);
                serialNumber += 1;
                items.add(mapTemp);
            }
            return Y9Page.success(page, Integer.parseInt(retMap.get("totalpages").toString()),
                Integer.parseInt(retMap.get("total").toString()), items, "获取列表成功");
        } catch (Exception e) {
            LOGGER.error("获取列表失败", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取列表失败");
    }

    @Override
    public Y9Page<Map<String, Object>> homeDoneList(Integer page, Integer rows) {
        Y9Page<OfficeDoneInfoModel> y9Page;
        try {
            String positionId = Y9LoginUserHolder.getPositionId();
            String tenantId = Y9LoginUserHolder.getTenantId();
            y9Page = officeDoneInfo4PositionApi.searchAllByPositionId(tenantId, positionId, "", "", "", "done", "", "",
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
                    String startTime = hpim.getStartTime().substring(0, 16);
                    String processSerialNumber = hpim.getProcessSerialNumber();
                    String documentTitle = StringUtils.isBlank(hpim.getTitle()) ? "无标题" : hpim.getTitle();
                    String level = hpim.getUrgency();
                    String number = hpim.getDocNumber();
                    String completer = hpim.getUserComplete();
                    mapTemp.put("itemName", hpim.getItemName());
                    mapTemp.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber);
                    mapTemp.put(SysVariables.DOCUMENTTITLE, documentTitle);
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
                    String url = flowableBaseUrl + "/index/edit?itemId=" + hpim.getItemId() + "&processSerialNumber="
                        + processSerialNumber + "&itembox=done&processInstanceId=" + processInstanceId
                        + "&listType=done&systemName=" + hpim.getSystemName();
                    mapTemp.put("url", url);
                    mapTemp.put("meeting", hpim.getMeeting() != null && hpim.getMeeting().equals("1"));
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

    @Override
    public Y9Page<net.risesoft.model.ChaoSongModel> myChaoSongList(String searchName, String itemId, String userName,
        String state, String year, Integer page, Integer rows) {
        String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
        Y9Page<ChaoSongModel> y9page = chaoSong4PositionApi.myChaoSongList(tenantId, positionId, searchName, itemId,
            userName, state, year, page, rows);
        List<ChaoSongModel> list = y9page.getRows();
        List<net.risesoft.model.ChaoSongModel> list1 = new ArrayList<>();
        for (ChaoSongModel model : list) {
            net.risesoft.model.ChaoSongModel newmodel = new net.risesoft.model.ChaoSongModel();
            Y9BeanUtil.copyProperties(model, newmodel);
            String itemId1 = newmodel.getItemId();
            String processSerialNumber = newmodel.getProcessSerialNumber();
            String processInstanceId = newmodel.getProcessInstanceId();
            String systemName = newmodel.getSystemName();
            boolean banjie = newmodel.isBanjie();
            newmodel.setItembox("done");
            String taskId = "";
            String itembox = "done";
            if (!banjie) {
                List<TaskModel> taskList = taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                List<String> listTemp = getAssigneeIdsAndAssigneeNames1(taskList);
                String taskIds = listTemp.get(0), assigneeIds = listTemp.get(1), assigneeNames = listTemp.get(2);
                newmodel.setTaskDefinitionKey(taskList.get(0).getTaskDefinitionKey());
                newmodel
                    .setTaskId(listTemp.get(3).equals(ItemBoxTypeEnum.DOING.getValue()) ? taskIds : listTemp.get(4));
                newmodel.setTaskAssignee(assigneeNames);
                newmodel.setTaskAssigneeId(assigneeIds);
                newmodel.setItembox(listTemp.get(3));
                itembox = listTemp.get(3);
                taskId = listTemp.get(3).equals(ItemBoxTypeEnum.DOING.getValue()) ? taskIds : listTemp.get(4);
            }
            String url = flowableBaseUrl + "/index/edit?itemId=" + itemId1 + "&processSerialNumber="
                + processSerialNumber + "&itembox=" + itembox + "&taskId=" + taskId + "&processInstanceId="
                + processInstanceId + "&listType=chuanyueList&systemName=" + systemName;
            newmodel.setUrl(url);
            list1.add(newmodel);
        }
        return Y9Page.success(y9page.getCurrPage(), y9page.getTotalPages(), y9page.getTotal(), list1, "获取成功");
    }

    @Override
    public Y9Page<Map<String, Object>> todoList(String itemId, String searchItemId, String searchTerm, Integer page,
        Integer rows) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
            ItemModel item = item4PositionApi.getByItemId(tenantId, itemId).getData();
            Y9Page<TaskModel> taskPage;
            if (StringUtils.isBlank(searchTerm)) {
                if (StringUtils.isNotBlank(searchItemId)) {
                    ItemModel item1 = item4PositionApi.getByItemId(tenantId, searchItemId).getData();
                    taskPage = processTodoApi.getListByUserIdAndProcessDefinitionKey(tenantId, positionId,
                        item1.getWorkflowGuid(), page, rows);
                } else {
                    taskPage = processTodoApi.getListByUserIdAndSystemName(tenantId, positionId, item.getSystemName(),
                        page, rows);
                }
            } else {
                if (StringUtils.isNotBlank(searchItemId)) {
                    ItemModel item1 = item4PositionApi.getByItemId(tenantId, searchItemId).getData();
                    taskPage = processTodoApi.searchListByUserIdAndProcessDefinitionKey(tenantId, positionId,
                        item1.getWorkflowGuid(), searchTerm, page, rows);
                } else {
                    taskPage = processTodoApi.searchListByUserIdAndSystemName(tenantId, positionId,
                        item.getSystemName(), searchTerm, page, rows);
                }
            }
            List<TaskModel> list = taskPage.getRows();
            ObjectMapper objectMapper = new ObjectMapper();
            List<TaskModel> taslList = objectMapper.convertValue(list, new TypeReference<>() {});
            List<Map<String, Object>> items = new ArrayList<>();
            int serialNumber = (page - 1) * rows;
            Map<String, Object> vars;
            Collection<String> keys;
            Map<String, Object> mapTemp;
            ProcessParamModel processParam;
            String taskId;
            for (TaskModel task : taslList) {
                mapTemp = new HashMap<>(16);
                taskId = task.getId();
                String processInstanceId = task.getProcessInstanceId();
                String processDefinitionId = task.getProcessDefinitionId();
                try {
                    Date taskCreateTime = task.getCreateTime();
                    String taskAssignee = task.getAssignee();
                    String description = task.getDescription();
                    String taskDefinitionKey = task.getTaskDefinitionKey();
                    String taskName = task.getName();
                    int priority = task.getPriority();
                    keys = new ArrayList<>();
                    keys.add(SysVariables.TASKSENDER);
                    vars = variableApi.getVariablesByProcessInstanceId(tenantId, processInstanceId, keys).getData();
                    String taskSender = Strings.nullToEmpty((String)vars.get(SysVariables.TASKSENDER));
                    int isNewTodo = StringUtils.isBlank(task.getFormKey()) ? 1 : Integer.parseInt(task.getFormKey());
                    Boolean isReminder = String.valueOf(priority).contains("8");// 催办的时候任务的优先级+5
                    processParam = processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                    String processSerialNumber = processParam.getProcessSerialNumber();
                    String level = processParam.getCustomLevel();
                    String number = processParam.getCustomNumber();
                    mapTemp.put("itemId", processParam.getItemId());
                    mapTemp.put("itemName", processParam.getItemName());
                    mapTemp.put("processDefinitionKey", task.getProcessDefinitionId().split(":")[0]);
                    mapTemp.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber);
                    mapTemp.put("title", processParam.getTitle());
                    mapTemp.put("processDefinitionId", processDefinitionId);
                    mapTemp.put("taskId", taskId);
                    mapTemp.put("description", description);
                    mapTemp.put("taskDefinitionKey", taskDefinitionKey);
                    mapTemp.put("taskName", taskName);
                    mapTemp.put("taskCreateTime", sdf.format(taskCreateTime));
                    mapTemp.put("taskAssignee", taskAssignee);
                    mapTemp.put(SysVariables.TASKSENDER, taskSender);
                    mapTemp.put(SysVariables.ISNEWTODO, isNewTodo);
                    mapTemp.put(SysVariables.ISREMINDER, isReminder);
                    mapTemp.put(SysVariables.NUMBER, number);
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
                    // 是否正在发送标识
                    if (taskVariableModel != null) {
                        mapTemp.put("isForwarding", taskVariableModel.getText().contains("true"));
                    }
                    mapTemp.put(SysVariables.LEVEL, level);
                    mapTemp.put("processInstanceId", processInstanceId);
                    mapTemp.put("speakInfoNum", 0);
                    mapTemp.put("remindSetting", false);

                    String rollBack = variableApi.getVariableLocal(tenantId, taskId, SysVariables.ROLLBACK).getData();
                    // 退回件
                    if (Boolean.parseBoolean(rollBack)) {
                        mapTemp.put("rollBack", true);
                    }
                    try {
                        String takeBack =
                            variableApi.getVariableLocal(tenantId, taskId, SysVariables.TAKEBACK).getData();
                        if (Boolean.parseBoolean(takeBack)) {// 收回件
                            List<HistoricTaskInstanceModel> hlist = historicTaskApi
                                .findTaskByProcessInstanceIdOrderByStartTimeAsc(tenantId, processInstanceId, "")
                                .getData();
                            if (hlist.get(0).getTaskDefinitionKey().equals(task.getTaskDefinitionKey())) {// 起草收回件，可删除
                                mapTemp.put("takeBack", true);
                            }
                        }
                    } catch (Exception e) {
                        LOGGER.error("收回件异常", e);
                    }
                    mapTemp.put("meeting", false);
                    OfficeDoneInfoModel officeDoneInfo =
                        officeDoneInfo4PositionApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                    mapTemp.put("meeting",
                        officeDoneInfo.getMeeting() != null && officeDoneInfo.getMeeting().equals("1"));
                } catch (Exception e) {
                    LOGGER.error("获取待办信息异常" + taskId, e);
                }
                mapTemp.put("serialNumber", serialNumber + 1);
                serialNumber += 1;
                items.add(mapTemp);
            }
            return Y9Page.success(page, taskPage.getTotalPages(), taskPage.getTotal(), items, "获取列表成功");
        } catch (Exception e) {
            LOGGER.error("获取待办信息异常", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取列表失败");
    }

}
