package net.risesoft.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

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
import net.risesoft.model.itemadmin.ItemModel;
import net.risesoft.model.itemadmin.OfficeDoneInfoModel;
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
import net.risesoft.y9.util.Y9Util;

@Service(value = "workList4ddyjsService")
@Transactional(readOnly = true)
public class WorkList4ddyjsServiceImpl implements WorkList4ddyjsService {

    @Autowired
    private DoingApi doingApi;

    @Autowired
    private TaskApi taskApi;

    @Autowired
    private Item4PositionApi item4PositionApi;

    @Autowired
    private HistoricTaskApi historicTaskApi;

    @Autowired
    private PositionApi positionApi;

    @Autowired
    private ProcessParamApi processParamApi;

    @Autowired
    private OfficeFollow4PositionApi officeFollow4PositionApi;

    @Autowired
    private IdentityApi identityApi;

    @Autowired
    private OfficeDoneInfo4PositionApi officeDoneInfo4PositionApi;

    @Autowired
    private ProcessTodoApi processTodoApi;

    @Autowired
    private VariableApi variableApi;

    @Autowired
    private ProcessDefinitionApi processDefinitionApi;

    @Autowired
    private TaskVariableApi taskvariableApi;

    @Autowired
    private FormDataApi formDataApi;

    @Value("${y9.common.flowableBaseUrl}")
    private String flowableBaseUrl;

    @Autowired
    private ChaoSong4PositionApi chaoSong4PositionApi;

    @SuppressWarnings({"unchecked"})
    @Override
    public Y9Page<Map<String, Object>> doingList(String itemId, String searchItemId, String searchTerm, Integer page, Integer rows) {
        Map<String, Object> retMap = new HashMap<String, Object>(16);
        try {
            List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String positionId = Y9LoginUserHolder.getPositionId(), tenantId = Y9LoginUserHolder.getTenantId();
            ItemModel item = item4PositionApi.getByItemId(tenantId, itemId);
            if (StringUtils.isBlank(searchTerm)) {
                if (StringUtils.isNotBlank(searchItemId)) {
                    ItemModel item1 = item4PositionApi.getByItemId(tenantId, searchItemId);
                    retMap = doingApi.getListByUserIdAndProcessDefinitionKey(tenantId, positionId, item1.getWorkflowGuid(), page, rows);
                } else {
                    retMap = doingApi.getListByUserIdAndSystemName(tenantId, positionId, item.getSystemName(), page, rows);
                }
                List<ProcessInstanceModel> list = (List<ProcessInstanceModel>)retMap.get("rows");
                ObjectMapper objectMapper = new ObjectMapper();
                List<ProcessInstanceModel> hpiModelList = objectMapper.convertValue(list, new TypeReference<List<ProcessInstanceModel>>() {});
                int serialNumber = (page - 1) * rows;
                Map<String, Object> mapTemp = null;
                ProcessParamModel processParam = null;
                for (ProcessInstanceModel hpim : hpiModelList) {// 以办理时间排序
                    mapTemp = new HashMap<String, Object>(16);
                    try {
                        String processInstanceId = hpim.getId();
                        String processDefinitionId = hpim.getProcessDefinitionId();
                        String taskCreateTime = sdf.format(hpim.getStartTime());
                        List<TaskModel> taskList = taskApi.findByProcessInstanceId(tenantId, processInstanceId);
                        List<String> listTemp = getAssigneeIdsAndAssigneeNames(taskList);
                        String taskIds = listTemp.get(0), assigneeIds = listTemp.get(1), assigneeNames = listTemp.get(2);
                        Boolean isReminder = String.valueOf(taskList.get(0).getPriority()).contains("5");
                        processParam = processParamApi.findByProcessInstanceId(tenantId, processInstanceId);
                        String processSerialNumber = processParam.getProcessSerialNumber();
                        String documentTitle = StringUtils.isBlank(processParam.getTitle()) ? "无标题" : processParam.getTitle();
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
                        // int countFollow = officeFollow4PositionApi.countByProcessInstanceId(tenantId, positionId,
                        // processInstanceId);
                        // mapTemp.put("follow", countFollow > 0 ? true : false);

                        mapTemp.put("meeting", false);
                        if (item.getSystemName().equals("gongwenguanli")) {
                            OfficeDoneInfoModel officeDoneInfo = officeDoneInfo4PositionApi.findByProcessInstanceId(tenantId, processInstanceId);
                            mapTemp.put("meeting", (officeDoneInfo.getMeeting() != null && officeDoneInfo.getMeeting().equals("1")) ? true : false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mapTemp.put("serialNumber", serialNumber + 1);
                    serialNumber += 1;
                    items.add(mapTemp);
                }
            } else {
                if (StringUtils.isNotBlank(searchItemId)) {
                    ItemModel item1 = item4PositionApi.getByItemId(tenantId, searchItemId);
                    retMap = doingApi.searchListByUserIdAndProcessDefinitionKey(tenantId, positionId, item1.getWorkflowGuid(), searchTerm, page, rows);
                } else {
                    retMap = doingApi.searchListByUserIdAndSystemName(tenantId, positionId, item.getSystemName(), searchTerm, page, rows);
                }
                List<ProcessInstanceModel> list = (List<ProcessInstanceModel>)retMap.get("rows");
                ObjectMapper objectMapper = new ObjectMapper();
                List<ProcessInstanceModel> hpiModelList = objectMapper.convertValue(list, new TypeReference<List<ProcessInstanceModel>>() {});
                int serialNumber = (page - 1) * rows;
                Map<String, Object> mapTemp = null;
                ProcessParamModel processParam = null;
                for (ProcessInstanceModel hpim : hpiModelList) {
                    mapTemp = new HashMap<String, Object>(16);
                    try {
                        String processInstanceId = hpim.getId();
                        String processDefinitionId = hpim.getProcessDefinitionId();
                        List<TaskModel> taskList = taskApi.findByProcessInstanceId(tenantId, processInstanceId);
                        List<String> listTemp = getAssigneeIdsAndAssigneeNames(taskList);
                        String taskIds = listTemp.get(0), assigneeIds = listTemp.get(1), assigneeNames = listTemp.get(2);
                        Boolean isReminder = String.valueOf(taskList.get(0).getPriority()).contains("5");
                        processParam = processParamApi.findByProcessInstanceId(tenantId, processInstanceId);
                        String processSerialNumber = processParam.getProcessSerialNumber();
                        String documentTitle = StringUtils.isBlank(processParam.getTitle()) ? "无标题" : processParam.getTitle();
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
                        if (item.getSystemName().equals("gongwenguanli")) {
                            OfficeDoneInfoModel officeDoneInfo = officeDoneInfo4PositionApi.findByProcessInstanceId(tenantId, processInstanceId);
                            mapTemp.put("meeting", (officeDoneInfo.getMeeting() != null && officeDoneInfo.getMeeting().equals("1")) ? true : false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mapTemp.put("serialNumber", serialNumber + 1);
                    serialNumber += 1;
                    items.add(mapTemp);
                }
            }
            return Y9Page.success(page, Integer.parseInt(retMap.get("totalpages").toString()), Integer.parseInt(retMap.get("total").toString()), items, "获取列表成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Page.success(page, 0, 0, new ArrayList<Map<String, Object>>(), "获取列表失败");
    }

    @SuppressWarnings("unchecked")
    @Override
    public Y9Page<Map<String, Object>> doneList(String itemId, String searchItemId, String searchTerm, Integer page, Integer rows) {
        Map<String, Object> retMap = new HashMap<String, Object>(16);
        String userId = Y9LoginUserHolder.getPositionId(), tenantId = Y9LoginUserHolder.getTenantId();
        ItemModel item = item4PositionApi.getByItemId(tenantId, itemId);
        if (StringUtils.isNotBlank(searchItemId)) {
            retMap = officeDoneInfo4PositionApi.searchByPositionId(tenantId, userId, searchTerm, searchItemId, "", "", page, rows);
        } else {
            retMap = officeDoneInfo4PositionApi.searchByPositionIdAndSystemName(tenantId, userId, searchTerm, item.getSystemName(), "", "", page, rows);
        }
        List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
        List<OfficeDoneInfoModel> list = (List<OfficeDoneInfoModel>)retMap.get("rows");
        ObjectMapper objectMapper = new ObjectMapper();
        List<OfficeDoneInfoModel> hpiModelList = objectMapper.convertValue(list, new TypeReference<List<OfficeDoneInfoModel>>() {});
        int serialNumber = (page - 1) * rows;
        Map<String, Object> mapTemp = null;
        for (OfficeDoneInfoModel hpim : hpiModelList) {
            mapTemp = new HashMap<String, Object>(16);
            String processInstanceId = hpim.getProcessInstanceId();
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
                    mapTemp.put("meeting", (hpim.getMeeting() != null && hpim.getMeeting().equals("1")) ? true : false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            mapTemp.put("serialNumber", serialNumber + 1);
            serialNumber += 1;
            items.add(mapTemp);
        }
        return Y9Page.success(page, Integer.parseInt(retMap.get("totalpages").toString()), Integer.parseInt(retMap.get("total").toString()), items, "获取列表成功");
    }

    @SuppressWarnings("unchecked")
    @Override
    public Y9Page<Map<String, Object>> followList(String itemId, String searchTerm, Integer page, Integer rows) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        String tenantId = Y9LoginUserHolder.getTenantId();
        ItemModel item = item4PositionApi.getByItemId(tenantId, itemId);
        map = officeFollow4PositionApi.getFollowListBySystemName(tenantId, Y9LoginUserHolder.getPositionId(), item.getSystemName(), searchTerm, page, rows);
        return Y9Page.success(page, Integer.parseInt(map.get("totalpage").toString()), Integer.parseInt(map.get("total").toString()), (List<Map<String, Object>>)map.get("rows"), "获取列表成功");
    }

    /**
     * 当并行的时候，会获取到多个task，为了并行时当前办理人显示多人，而不是显示多条记录，需要分开分别进行处理
     *
     * @return
     */
    private List<String> getAssigneeIdsAndAssigneeNames(List<TaskModel> taskList) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String taskIds = "", assigneeIds = "", assigneeNames = "";
        List<String> list = new ArrayList<String>();
        int i = 0;
        if (taskList.size() > 0) {
            for (TaskModel task : taskList) {
                if (StringUtils.isEmpty(taskIds)) {
                    taskIds = task.getId();
                    String assignee = task.getAssignee();
                    if (StringUtils.isNotBlank(assignee)) {
                        assigneeIds = assignee;
                        Position personTemp = positionApi.getPosition(tenantId, assignee).getData();
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
                                Position ownerUser = positionApi.getPosition(Y9LoginUserHolder.getTenantId(), assigneeId).getData();
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
                            Position personTemp = positionApi.getPosition(tenantId, assignee).getData();
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
        String taskIds = "", assigneeIds = "", assigneeNames = "", itembox = ItemBoxTypeEnum.DOING.getValue(), taskId = "";
        List<String> list = new ArrayList<String>();
        int i = 0;
        if (taskList.size() > 0) {
            for (TaskModel task : taskList) {
                if (StringUtils.isEmpty(taskIds)) {
                    taskIds = task.getId();
                    String assignee = task.getAssignee();
                    if (StringUtils.isNotBlank(assignee)) {
                        assigneeIds = assignee;
                        Position personTemp = positionApi.getPosition(tenantId, assignee).getData();
                        if (personTemp != null) {
                            assigneeNames = personTemp.getName();
                        }
                        i += 1;
                        if (assignee.contains(userId)) {
                            itembox = ItemBoxTypeEnum.TODO.getValue();
                            taskId = task.getId();
                        }
                    } else {// 处理单实例未签收的当前办理人显示
                        List<IdentityLinkModel> iList = identityApi.getIdentityLinksForTask(tenantId, task.getId());
                        if (!iList.isEmpty()) {
                            int j = 0;
                            for (IdentityLinkModel identityLink : iList) {
                                String assigneeId = identityLink.getUserId();
                                Position ownerUser = positionApi.getPosition(Y9LoginUserHolder.getTenantId(), assigneeId).getData();
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
                            Position personTemp = positionApi.getPosition(tenantId, assignee).getData();
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

    @SuppressWarnings("unchecked")
    @Override
    public Y9Page<Map<String, Object>> getMeetingList(String userName, String deptName, String title, String meetingType, Integer page, Integer rows) {
        Map<String, Object> retMap = new HashMap<String, Object>(16);
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            retMap = officeDoneInfo4PositionApi.getMeetingList(tenantId, userName, deptName, title, meetingType, page, rows);
            List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
            List<OfficeDoneInfoModel> hpiModelList = (List<OfficeDoneInfoModel>)retMap.get("rows");
            ObjectMapper objectMapper = new ObjectMapper();
            List<OfficeDoneInfoModel> hpiList = objectMapper.convertValue(hpiModelList, new TypeReference<List<OfficeDoneInfoModel>>() {});
            int serialNumber = (page - 1) * rows;
            Map<String, Object> mapTemp = null;
            for (OfficeDoneInfoModel hpim : hpiList) {
                mapTemp = new HashMap<String, Object>(16);
                String processInstanceId = hpim.getProcessInstanceId();
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
                    mapTemp.put("endTime", StringUtils.isBlank(hpim.getEndTime()) ? "--" : hpim.getEndTime().substring(0, 16));
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
                        List<TaskModel> taskList = taskApi.findByProcessInstanceId(tenantId, processInstanceId);
                        List<String> listTemp = getAssigneeIdsAndAssigneeNames1(taskList);
                        String taskIds = listTemp.get(0), assigneeIds = listTemp.get(1), assigneeNames = listTemp.get(2);
                        mapTemp.put("taskDefinitionKey", taskList.get(0).getTaskDefinitionKey());
                        mapTemp.put("taskId", listTemp.get(3).equals(ItemBoxTypeEnum.DOING.getValue()) ? taskIds : listTemp.get(4));
                        mapTemp.put("taskAssigneeId", assigneeIds);
                        mapTemp.put("taskAssignee", assigneeNames);
                        mapTemp.put("itembox", listTemp.get(3));
                    }
                    mapTemp.put("beizhu", "");
                    Map<String, Object> formDataMap = formDataApi.getData(tenantId, hpim.getItemId(), processSerialNumber);
                    if (formDataMap.get("beizhu") != null) {
                        mapTemp.put("beizhu", formDataMap.get("beizhu"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mapTemp.put("serialNumber", serialNumber + 1);
                serialNumber += 1;
                items.add(mapTemp);
            }
            return Y9Page.success(page, Integer.parseInt(retMap.get("totalpages").toString()), Integer.parseInt(retMap.get("total").toString()), items, "获取列表成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Page.success(page, 0, 0, new ArrayList<Map<String, Object>>(), "获取列表失败");
    }

    @SuppressWarnings("unchecked")
    @Override
    public Y9Page<Map<String, Object>> homeDoingList(Integer page, Integer rows) {
        Map<String, Object> retMap = new HashMap<String, Object>(16);
        try {
            List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String positionId = Y9LoginUserHolder.getPositionId(), tenantId = Y9LoginUserHolder.getTenantId();
            retMap = doingApi.getListByUserId(tenantId, positionId, page, rows);
            List<ProcessInstanceModel> list = (List<ProcessInstanceModel>)retMap.get("rows");
            ObjectMapper objectMapper = new ObjectMapper();
            List<ProcessInstanceModel> hpiModelList = objectMapper.convertValue(list, new TypeReference<List<ProcessInstanceModel>>() {});
            int serialNumber = (page - 1) * rows;
            Map<String, Object> mapTemp = null;
            ProcessParamModel processParam = null;
            for (ProcessInstanceModel hpim : hpiModelList) {// 以办理时间排序
                mapTemp = new HashMap<String, Object>(16);
                try {
                    String processInstanceId = hpim.getId();
                    String processDefinitionId = hpim.getProcessDefinitionId();
                    String taskCreateTime = sdf.format(hpim.getStartTime());
                    List<TaskModel> taskList = taskApi.findByProcessInstanceId(tenantId, processInstanceId);
                    List<String> listTemp = getAssigneeIdsAndAssigneeNames(taskList);
                    String taskIds = listTemp.get(0), assigneeIds = listTemp.get(1), assigneeNames = listTemp.get(2);
                    Boolean isReminder = String.valueOf(taskList.get(0).getPriority()).contains("5");
                    processParam = processParamApi.findByProcessInstanceId(tenantId, processInstanceId);
                    String processSerialNumber = processParam.getProcessSerialNumber();
                    String documentTitle = StringUtils.isBlank(processParam.getTitle()) ? "无标题" : processParam.getTitle();
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
                    String url = flowableBaseUrl + "/index/edit?itemId=" + processParam.getItemId() + "&processSerialNumber=" + processSerialNumber + "&itembox=doing&taskId=" + taskIds + "&processInstanceId=" + processInstanceId + "&listType=doing&systemName=" + processParam.getSystemName();
                    mapTemp.put("url", url);
                    mapTemp.put("chaosongNum", 0);
                    mapTemp.put("status", 1);
                    mapTemp.put("taskDueDate", "");
                    mapTemp.put("processInstanceId", processInstanceId);
                    mapTemp.put("speakInfoNum", 0);
                    mapTemp.put("remindSetting", false);
                    mapTemp.put("follow", false);
                    mapTemp.put("meeting", false);
                    OfficeDoneInfoModel officeDoneInfo = officeDoneInfo4PositionApi.findByProcessInstanceId(tenantId, processInstanceId);
                    mapTemp.put("meeting", (officeDoneInfo.getMeeting() != null && officeDoneInfo.getMeeting().equals("1")) ? true : false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mapTemp.put("serialNumber", serialNumber + 1);
                serialNumber += 1;
                items.add(mapTemp);
            }
            return Y9Page.success(page, Integer.parseInt(retMap.get("totalpages").toString()), Integer.parseInt(retMap.get("total").toString()), items, "获取列表成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Page.success(page, 0, 0, new ArrayList<Map<String, Object>>(), "获取列表失败");
    }

    @SuppressWarnings("unchecked")
    @Override
    public Y9Page<Map<String, Object>> homeDoneList(Integer page, Integer rows) {
        Map<String, Object> retMap = new HashMap<String, Object>(16);
        try {
            String positionId = Y9LoginUserHolder.getPositionId(), tenantId = Y9LoginUserHolder.getTenantId();
            retMap = officeDoneInfo4PositionApi.searchAllByPositionId(tenantId, positionId, "", "", "", "done", "", "", "", page, rows);
            List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
            List<OfficeDoneInfoModel> hpiModelList = (List<OfficeDoneInfoModel>)retMap.get("rows");
            ObjectMapper objectMapper = new ObjectMapper();
            List<OfficeDoneInfoModel> hpiList = objectMapper.convertValue(hpiModelList, new TypeReference<List<OfficeDoneInfoModel>>() {});
            int serialNumber = (page - 1) * rows;
            Map<String, Object> mapTemp = null;
            for (OfficeDoneInfoModel hpim : hpiList) {
                mapTemp = new HashMap<String, Object>(16);
                String processInstanceId = hpim.getProcessInstanceId();
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
                    mapTemp.put("endTime", StringUtils.isBlank(hpim.getEndTime()) ? "--" : hpim.getEndTime().substring(0, 16));
                    mapTemp.put("taskDefinitionKey", "");
                    mapTemp.put("taskAssignee", completer);
                    mapTemp.put("creatUserName", hpim.getCreatUserName());
                    mapTemp.put("itemId", hpim.getItemId());
                    mapTemp.put("level", level == null ? "" : level);
                    mapTemp.put("number", number == null ? "" : number);
                    mapTemp.put("itembox", ItemBoxTypeEnum.DONE.getValue());
                    String url = flowableBaseUrl + "/index/edit?itemId=" + hpim.getItemId() + "&processSerialNumber=" + processSerialNumber + "&itembox=done&processInstanceId=" + processInstanceId + "&listType=done&systemName=" + hpim.getSystemName();
                    mapTemp.put("url", url);
                    mapTemp.put("meeting", (hpim.getMeeting() != null && hpim.getMeeting().equals("1")) ? true : false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mapTemp.put("serialNumber", serialNumber + 1);
                serialNumber += 1;
                items.add(mapTemp);
            }
            return Y9Page.success(page, Integer.parseInt(retMap.get("totalpages").toString()), Integer.parseInt(retMap.get("total").toString()), items, "获取列表成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Page.success(page, 0, 0, new ArrayList<Map<String, Object>>(), "获取列表失败");
    }

    @Override
    public Y9Page<Map<String, Object>> myChaoSongList(String searchName, String itemId, String userName, String state, String year, Integer page, Integer rows) {
        String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
        Y9Page<Map<String, Object>> y9page = chaoSong4PositionApi.myChaoSongList(tenantId, positionId, searchName, itemId, userName, state, year, page, rows);
        List<Map<String, Object>> list = y9page.getRows();
        for (Map<String, Object> map : list) {
            String itemId1 = (String)map.get("itemId");
            String processSerialNumber = (String)map.get("processSerialNumber");
            String processInstanceId = (String)map.get("processInstanceId");
            String systemName = (String)map.get("systemName");
            boolean banjie = (boolean)map.get("banjie");
            map.put("itembox", "done");
            String taskId = "";
            String itembox = "done";
            if (!banjie) {
                List<TaskModel> taskList = taskApi.findByProcessInstanceId(tenantId, processInstanceId);
                List<String> listTemp = getAssigneeIdsAndAssigneeNames1(taskList);
                String taskIds = listTemp.get(0), assigneeIds = listTemp.get(1), assigneeNames = listTemp.get(2);
                map.put("taskDefinitionKey", taskList.get(0).getTaskDefinitionKey());
                map.put("taskId", listTemp.get(3).equals(ItemBoxTypeEnum.DOING.getValue()) ? taskIds : listTemp.get(4));
                map.put("taskAssigneeId", assigneeIds);
                map.put("taskAssignee", assigneeNames);
                map.put("itembox", listTemp.get(3));
                itembox = listTemp.get(3);
                taskId = listTemp.get(3).equals(ItemBoxTypeEnum.DOING.getValue()) ? taskIds : listTemp.get(4);
            }
            String url = flowableBaseUrl + "/index/edit?itemId=" + itemId1 + "&processSerialNumber=" + processSerialNumber + "&itembox=" + itembox + "&taskId=" + taskId + "&processInstanceId=" + processInstanceId + "&listType=chuanyueList&systemName=" + systemName;
            map.put("url", url);
        }
        y9page.setRows(list);
        return y9page;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Y9Page<Map<String, Object>> todoList(String itemId, String searchItemId, String searchTerm, Integer page, Integer rows) {
        Map<String, Object> retMap = new HashMap<String, Object>(16);
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
            ItemModel item = item4PositionApi.getByItemId(tenantId, itemId);
            if (StringUtils.isBlank(searchTerm)) {
                if (StringUtils.isNotBlank(searchItemId)) {
                    ItemModel item1 = item4PositionApi.getByItemId(tenantId, searchItemId);
                    retMap = processTodoApi.getListByUserIdAndProcessDefinitionKey(tenantId, positionId, item1.getWorkflowGuid(), page, rows);
                } else {
                    retMap = processTodoApi.getListByUserIdAndSystemName(tenantId, positionId, item.getSystemName(), page, rows);
                }
            } else {
                if (StringUtils.isNotBlank(searchItemId)) {
                    ItemModel item1 = item4PositionApi.getByItemId(tenantId, searchItemId);
                    retMap = processTodoApi.searchListByUserIdAndProcessDefinitionKey(tenantId, positionId, item1.getWorkflowGuid(), searchTerm, page, rows);
                } else {
                    retMap = processTodoApi.searchListByUserIdAndSystemName(tenantId, positionId, item.getSystemName(), searchTerm, page, rows);
                }
            }
            List<TaskModel> list = (List<TaskModel>)retMap.get("rows");
            ObjectMapper objectMapper = new ObjectMapper();
            List<TaskModel> taslList = objectMapper.convertValue(list, new TypeReference<List<TaskModel>>() {});
            List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
            int serialNumber = (page - 1) * rows;
            Map<String, Object> vars = null;
            Collection<String> keys = null;
            Map<String, Object> mapTemp = null;
            ProcessParamModel processParam = null;
            for (TaskModel task : taslList) {
                mapTemp = new HashMap<String, Object>(16);
                String taskId = task.getId();
                String processInstanceId = task.getProcessInstanceId();
                String processDefinitionId = task.getProcessDefinitionId();
                try {
                    Date taskCreateTime = task.getCreateTime();
                    String taskAssignee = task.getAssignee();
                    String description = task.getDescription();
                    String taskDefinitionKey = task.getTaskDefinitionKey();
                    String taskName = task.getName();
                    int priority = task.getPriority();
                    keys = new ArrayList<String>();
                    keys.add(SysVariables.TASKSENDER);
                    vars = variableApi.getVariablesByProcessInstanceId(tenantId, processInstanceId, keys);
                    String taskSender = Strings.nullToEmpty((String)vars.get(SysVariables.TASKSENDER));
                    int isNewTodo = StringUtils.isBlank(task.getFormKey()) ? 1 : Integer.parseInt(task.getFormKey());
                    Boolean isReminder = String.valueOf(priority).contains("8");// 催办的时候任务的优先级+5
                    processParam = processParamApi.findByProcessInstanceId(tenantId, processInstanceId);
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
                    String multiInstance = processDefinitionApi.getNodeType(tenantId, task.getProcessDefinitionId(), task.getTaskDefinitionKey());
                    mapTemp.put("isZhuBan", "");
                    if (multiInstance.equals(SysVariables.PARALLEL)) {
                        mapTemp.put("isZhuBan", "false");
                        String sponsorGuid = processParam.getSponsorGuid();
                        if (StringUtils.isNotBlank(sponsorGuid)) {
                            if (task.getAssignee().equals(sponsorGuid)) {
                                mapTemp.put("isZhuBan", "true");
                            }
                        }
                        String obj = variableApi.getVariableByProcessInstanceId(tenantId, task.getExecutionId(), SysVariables.NROFACTIVEINSTANCES);
                        Integer nrOfActiveInstances = obj != null ? Integer.valueOf(obj) : 0;
                        if (nrOfActiveInstances == 1) {
                            mapTemp.put("isZhuBan", "true");
                        }
                        if (StringUtils.isNotBlank(task.getOwner()) && !task.getOwner().equals(task.getAssignee())) {
                            mapTemp.put("isZhuBan", "");
                        }
                    }
                    mapTemp.put("isForwarding", false);
                    TaskVariableModel taskVariableModel = taskvariableApi.findByTaskIdAndKeyName(tenantId, taskId, "isForwarding");
                    if (taskVariableModel != null) {// 是否正在发送标识
                        mapTemp.put("isForwarding", taskVariableModel.getText().contains("true") ? true : false);
                    }
                    mapTemp.put(SysVariables.LEVEL, level);
                    mapTemp.put("processInstanceId", processInstanceId);
                    mapTemp.put("speakInfoNum", 0);
                    mapTemp.put("remindSetting", false);

                    String rollBack = variableApi.getVariableLocal(tenantId, taskId, SysVariables.ROLLBACK);
                    if (rollBack != null && Boolean.valueOf(rollBack)) {// 退回件
                        mapTemp.put("rollBack", true);
                    }
                    try {
                        String takeBack = variableApi.getVariableLocal(tenantId, taskId, SysVariables.TAKEBACK);
                        if (takeBack != null && Boolean.valueOf(takeBack)) {// 收回件
                            List<HistoricTaskInstanceModel> hlist = historicTaskApi.findTaskByProcessInstanceIdOrderByStartTimeAsc(tenantId, processInstanceId, "");
                            if (hlist.get(0).getTaskDefinitionKey().equals(task.getTaskDefinitionKey())) {// 起草收回件，可删除
                                mapTemp.put("takeBack", true);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mapTemp.put("meeting", false);
                    OfficeDoneInfoModel officeDoneInfo = officeDoneInfo4PositionApi.findByProcessInstanceId(tenantId, processInstanceId);
                    mapTemp.put("meeting", (officeDoneInfo.getMeeting() != null && officeDoneInfo.getMeeting().equals("1")) ? true : false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mapTemp.put("serialNumber", serialNumber + 1);
                serialNumber += 1;
                items.add(mapTemp);
            }
            return Y9Page.success(page, Integer.parseInt(retMap.get("totalpages").toString()), Integer.parseInt(retMap.get("total").toString()), items, "获取列表成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Page.success(page, 0, 0, new ArrayList<Map<String, Object>>(), "获取列表失败");
    }

}
