package net.risesoft.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.risesoft.api.itemadmin.ChaoSongInfoApi;
import net.risesoft.api.itemadmin.ItemApi;
import net.risesoft.api.itemadmin.OfficeDoneInfoApi;
import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.api.org.PersonApi;
import net.risesoft.api.processadmin.IdentityApi;
import net.risesoft.api.processadmin.MonitorApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.model.Person;
import net.risesoft.model.itemadmin.ItemModel;
import net.risesoft.model.itemadmin.OfficeDoneInfoModel;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.model.processadmin.HistoricProcessInstanceModel;
import net.risesoft.model.processadmin.IdentityLinkModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Page;
import net.risesoft.service.MonitorService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9Util;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@Service(value = "monitorService")
@Transactional(readOnly = true)
public class MonitorServiceImpl implements MonitorService {

    @Autowired
    private MonitorApi monitorManager;

    @Autowired
    private TaskApi taskManager;

    @Autowired
    private ItemApi itemManager;

    @Autowired
    private PersonApi personApi;

    @Autowired
    private ProcessParamApi processParamManager;

    @Autowired
    private OfficeDoneInfoApi officeDoneInfoManager;

    @Autowired
    private ChaoSongInfoApi chaoSongInfoManager;

    @Autowired
    private IdentityApi identityManager;

    @SuppressWarnings("unchecked")
    @Override
    public Y9Page<Map<String, Object>> deptList(String itemId, String searchName, String userName, String state,
        String year, Integer page, Integer rows) {
        Map<String, Object> retMap = new HashMap<String, Object>(16);
        try {
            UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
            String tenantId = Y9LoginUserHolder.getTenantId();
            retMap = officeDoneInfoManager.searchAllByDeptId(tenantId, userInfo.getParentId(), searchName, itemId,
                userName, state, year, page, rows);
            List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
            List<OfficeDoneInfoModel> list = (List<OfficeDoneInfoModel>)retMap.get("rows");
            ObjectMapper objectMapper = new ObjectMapper();
            List<OfficeDoneInfoModel> hpiModelList =
                objectMapper.convertValue(list, new TypeReference<List<OfficeDoneInfoModel>>() {});
            Map<String, Object> mapTemp = null;
            for (OfficeDoneInfoModel hpim : hpiModelList) {
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
                        List<TaskModel> taskList = taskManager.findByProcessInstanceId(tenantId, processInstanceId);
                        List<String> listTemp = getAssigneeIdsAndAssigneeNames1(taskList);
                        String taskIds = listTemp.get(0), assigneeIds = listTemp.get(1),
                            assigneeNames = listTemp.get(2);
                        mapTemp.put("taskDefinitionKey", taskList.get(0).getTaskDefinitionKey());
                        mapTemp.put("taskId",
                            listTemp.get(3).equals(new HashMap<String, String>(16)) ? taskIds : listTemp.get(4));
                        mapTemp.put("taskAssigneeId", assigneeIds);
                        mapTemp.put("taskAssignee", assigneeNames);
                        mapTemp.put("itembox", new HashMap<String, String>(16));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                items.add(mapTemp);
            }
            return Y9Page.success(page, Integer.parseInt(retMap.get("totalpages").toString()),
                Integer.parseInt(retMap.get("total").toString()), items, "获取列表成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Page.success(page, 0, 0, new ArrayList<Map<String, Object>>(), "获取列表失败");
    }

    /**
     * 当并行的时候，会获取到多个task，为了并行时当前办理人显示多人，而不是显示多条记录，需要分开分别进行处理
     *
     * @return
     */
    private List<String> getAssigneeIdsAndAssigneeNames(List<TaskModel> taskList) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String taskIds = "", assigneeIds = "";
        StringBuffer assigneeNames = new StringBuffer();
        List<String> list = new ArrayList<String>();
        int i = 0;
        if (taskList.size() > 0) {
            for (TaskModel task : taskList) {
                if (StringUtils.isEmpty(taskIds)) {
                    taskIds = task.getId();
                    String assignee = task.getAssignee();
                    if (StringUtils.isNotBlank(assignee)) {
                        assigneeIds = assignee;
                        Person personTemp = personApi.getPerson(tenantId, assignee);
                        if (personTemp != null) {
                            assigneeNames.append(personTemp.getName());
                            i += 1;
                        }
                    } else {
                        // 处理单实例未签收的当前办理人显示
                        List<IdentityLinkModel> iList = identityManager.getIdentityLinksForTask(tenantId, task.getId());
                        if (!iList.isEmpty()) {
                            int j = 0;
                            for (IdentityLinkModel identityLink : iList) {
                                String assigneeId = identityLink.getUserId();
                                Person ownerUser = personApi.getPerson(Y9LoginUserHolder.getTenantId(), assigneeId);
                                if (j < 5) {
                                    assigneeNames = Y9Util.genCustomStr(assigneeNames,
                                        ownerUser.getName() + (ownerUser.getDisabled() ? "(已禁用)" : ""), "、");
                                    assigneeIds = Y9Util.genCustomStr(assigneeIds, assigneeId, SysVariables.COMMA);
                                } else {
                                    assigneeNames.append("等，共" + iList.size() + "人");
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
                            assigneeIds = Y9Util.genCustomStr(assigneeIds, task.getAssignee(), SysVariables.COMMA);
                            Person personTemp = personApi.getPerson(tenantId, assignee);
                            if (personTemp != null) {
                                assigneeNames = Y9Util.genCustomStr(assigneeNames, personTemp.getName(), "、");
                                i += 1;
                            }
                        }
                    }
                }
            }
            boolean b = taskList.size() > 5;
            if (b) {
                assigneeNames.append("等，共" + taskList.size() + "人");
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
        list.add(assigneeNames.toString());
        return list;
    }

    /**
     * 当并行的时候，会获取到多个task，为了并行时当前办理人显示多人，而不是显示多条记录，需要分开分别进行处理
     *
     * @return
     */
    private List<String> getAssigneeIdsAndAssigneeNames1(List<TaskModel> taskList) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String userId = Y9LoginUserHolder.getPersonId();
        String taskIds = "", assigneeIds = "", itembox = ItemBoxTypeEnum.DOING.getValue(), taskId = "";
        StringBuffer assigneeNames = new StringBuffer();
        List<String> list = new ArrayList<String>();
        int i = 0;
        if (taskList.size() > 0) {
            for (TaskModel task : taskList) {
                if (StringUtils.isEmpty(taskIds)) {
                    taskIds = task.getId();
                    String assignee = task.getAssignee();
                    if (StringUtils.isNotBlank(assignee)) {
                        assigneeIds = assignee;
                        Person personTemp = personApi.getPerson(tenantId, assignee);
                        if (personTemp != null) {
                            assigneeNames.append(personTemp.getName());
                        }
                        i += 1;
                        if (assignee.contains(userId)) {
                            itembox = ItemBoxTypeEnum.TODO.getValue();
                            taskId = task.getId();
                        }
                    } else {// 处理单实例未签收的当前办理人显示
                        List<IdentityLinkModel> iList = identityManager.getIdentityLinksForTask(tenantId, task.getId());
                        if (!iList.isEmpty()) {
                            int j = 0;
                            for (IdentityLinkModel identityLink : iList) {
                                String assigneeId = identityLink.getUserId();
                                Person ownerUser = personApi.getPerson(Y9LoginUserHolder.getTenantId(), assigneeId);
                                if (j < 5) {
                                    assigneeNames = Y9Util.genCustomStr(assigneeNames,
                                        ownerUser.getName() + (ownerUser.getDisabled() ? "(已禁用)" : ""), "、");
                                    assigneeIds = Y9Util.genCustomStr(assigneeIds, assigneeId, SysVariables.COMMA);
                                } else {
                                    assigneeNames.append("等，共" + iList.size() + "人");
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
                            Person personTemp = personApi.getPerson(tenantId, assignee);
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
            boolean b = taskList.size() > 5;
            if (b) {
                assigneeNames.append("等，共" + taskList.size() + "人");
            }
        }
        list.add(taskIds);
        list.add(assigneeIds);
        list.add(assigneeNames.toString());
        list.add(itembox);
        list.add(taskId);
        return list;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Y9Page<Map<String, Object>> monitorBanjianList(String searchName, String itemId, String userName,
        String state, String year, Integer page, Integer rows) {
        Map<String, Object> retMap = new HashMap<String, Object>(16);
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            retMap =
                officeDoneInfoManager.searchAllList(tenantId, searchName, itemId, userName, state, year, page, rows);
            List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
            List<OfficeDoneInfoModel> hpiModelList = (List<OfficeDoneInfoModel>)retMap.get("rows");
            ObjectMapper objectMapper = new ObjectMapper();
            List<OfficeDoneInfoModel> hpiList =
                objectMapper.convertValue(hpiModelList, new TypeReference<List<OfficeDoneInfoModel>>() {});
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
                        List<TaskModel> taskList = taskManager.findByProcessInstanceId(tenantId, processInstanceId);
                        List<String> listTemp = getAssigneeIdsAndAssigneeNames1(taskList);
                        String taskIds = listTemp.get(0), assigneeIds = listTemp.get(1),
                            assigneeNames = listTemp.get(2);
                        mapTemp.put("taskDefinitionKey", taskList.get(0).getTaskDefinitionKey());
                        mapTemp.put("taskId",
                            listTemp.get(3).equals(new HashMap<String, String>(16)) ? taskIds : listTemp.get(4));
                        mapTemp.put("taskAssigneeId", assigneeIds);
                        mapTemp.put("taskAssignee", assigneeNames);
                        mapTemp.put("itembox", listTemp.get(3));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mapTemp.put("serialNumber", serialNumber + 1);
                serialNumber += 1;
                items.add(mapTemp);
            }
            return Y9Page.success(page, Integer.parseInt(retMap.get("totalpages").toString()),
                Integer.parseInt(retMap.get("total").toString()), items, "获取列表成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Page.success(page, 0, 0, new ArrayList<Map<String, Object>>(), "获取列表失败");
    }

    @SuppressWarnings("unchecked")
    @Override
    public Y9Page<Map<String, Object>> monitorChaosongList(String searchName, String itemId, String senderName,
        String userName, String state, String year, Integer page, Integer rows) {
        Map<String, Object> retMap = new HashMap<String, Object>(16);
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            retMap = chaoSongInfoManager.searchAllList(tenantId, searchName, itemId, senderName, userName, state, year,
                page, rows);
            return Y9Page.success(page, Integer.parseInt(retMap.get("totalpages").toString()),
                Integer.parseInt(retMap.get("total").toString()), (List<Map<String, Object>>)retMap.get("rows"),
                "获取列表成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Page.success(page, 0, 0, new ArrayList<Map<String, Object>>(), "获取列表失败");
    }

    @SuppressWarnings("unchecked")
    @Override
    public Y9Page<Map<String, Object>> monitorDoingList(String itemId, String searchTerm, Integer page, Integer rows) {
        Map<String, Object> retMap = new HashMap<>(16);
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            ItemModel item = itemManager.getByItemId(tenantId, itemId);
            String processDefinitionKey = item.getWorkflowGuid(), itemName = item.getName();
            retMap = officeDoneInfoManager.searchByItemId(tenantId, searchTerm, itemId,
                ItemBoxTypeEnum.DOING.getValue(), "", "", page, rows);
            List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
            List<OfficeDoneInfoModel> hpiModelList = (List<OfficeDoneInfoModel>)retMap.get("rows");
            ObjectMapper objectMapper = new ObjectMapper();
            List<OfficeDoneInfoModel> hpiList =
                objectMapper.convertValue(hpiModelList, new TypeReference<List<OfficeDoneInfoModel>>() {});
            int serialNumber = (page - 1) * rows;
            Map<String, Object> mapTemp = null;
            for (OfficeDoneInfoModel hpim : hpiList) {
                mapTemp = new HashMap<String, Object>(16);
                String processInstanceId = hpim.getProcessInstanceId();
                try {
                    String processDefinitionId = hpim.getProcessDefinitionId();
                    mapTemp.put("itemName", itemName);
                    mapTemp.put("processInstanceId", processInstanceId);
                    mapTemp.put("processDefinitionKey", processDefinitionKey);
                    String processSerialNumber = hpim.getProcessSerialNumber();
                    String documentTitle = StringUtils.isBlank(hpim.getTitle()) ? "无标题" : hpim.getTitle();
                    String level = hpim.getUrgency();
                    String number = hpim.getDocNumber();
                    mapTemp.put("creatUserName", hpim.getCreatUserName());
                    mapTemp.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber);
                    mapTemp.put("processDefinitionId", processDefinitionId);
                    mapTemp.put(SysVariables.DOCUMENTTITLE, documentTitle);
                    mapTemp.put("itemId", itemId);
                    mapTemp.put("level", level == null ? "" : level);
                    mapTemp.put("number", number == null ? "" : number);
                    mapTemp.put("status", 1);
                    mapTemp.put("taskDueDate", "");

                    List<TaskModel> taskList = taskManager.findByProcessInstanceId(tenantId, processInstanceId);
                    List<String> listTemp = getAssigneeIdsAndAssigneeNames(taskList);
                    String taskIds = listTemp.get(0), assigneeIds = listTemp.get(1), assigneeNames = listTemp.get(2);
                    Boolean isReminder = String.valueOf(taskList.get(0).getPriority()).contains("5");
                    mapTemp.put("taskDefinitionKey", taskList.get(0).getTaskDefinitionKey());
                    mapTemp.put("taskName", taskList.get(0).getName());
                    mapTemp.put("taskCreateTime",
                        taskList.get(0).getCreateTime() == null ? "" : sdf.format(taskList.get(0).getCreateTime()));
                    mapTemp.put("taskId", taskIds);
                    mapTemp.put("taskAssigneeId", assigneeIds);
                    mapTemp.put("taskAssignee", assigneeNames);
                    mapTemp.put("isReminder", isReminder);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mapTemp.put("serialNumber", serialNumber + 1);
                serialNumber += 1;
                items.add(mapTemp);
            }
            return Y9Page.success(page, Integer.parseInt(retMap.get("totalpages").toString()),
                Integer.parseInt(retMap.get("total").toString()), items, "获取列表成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Page.success(page, 0, 0, new ArrayList<Map<String, Object>>(), "获取列表失败");
    }

    @SuppressWarnings("unchecked")
    @Override
    public Y9Page<Map<String, Object>> monitorDoneList(String itemId, String searchTerm, Integer page, Integer rows) {
        Map<String, Object> retMap = new HashMap<>(16);
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            ItemModel item = itemManager.getByItemId(tenantId, itemId);
            String processDefinitionKey = item.getWorkflowGuid(), itemName = item.getName();
            retMap = officeDoneInfoManager.searchByItemId(tenantId, searchTerm, itemId, ItemBoxTypeEnum.DONE.getValue(),
                "", "", page, rows);
            List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
            List<OfficeDoneInfoModel> hpiModelList = (List<OfficeDoneInfoModel>)retMap.get("rows");
            ObjectMapper objectMapper = new ObjectMapper();
            List<OfficeDoneInfoModel> hpiList =
                objectMapper.convertValue(hpiModelList, new TypeReference<List<OfficeDoneInfoModel>>() {});
            int serialNumber = (page - 1) * rows;
            Map<String, Object> mapTemp = null;
            for (OfficeDoneInfoModel hpim : hpiList) {
                mapTemp = new HashMap<String, Object>(16);
                String processInstanceId = hpim.getProcessInstanceId();
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
                    mapTemp.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber);
                    mapTemp.put(SysVariables.DOCUMENTTITLE, documentTitle);
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
                    e.printStackTrace();
                }
                mapTemp.put("serialNumber", serialNumber + 1);
                serialNumber += 1;
                items.add(mapTemp);
            }
            return Y9Page.success(page, Integer.parseInt(retMap.get("totalpages").toString()),
                Integer.parseInt(retMap.get("total").toString()), items, "获取列表成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Page.success(page, 0, 0, new ArrayList<Map<String, Object>>(), "获取列表失败");
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> monitorRecycleList(String itemId, String searchTerm, Integer page, Integer rows) {
        Map<String, Object> retMap = new HashMap<>(16);
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            ItemModel item = itemManager.getByItemId(tenantId, itemId);
            String processDefinitionKey = item.getWorkflowGuid(), itemName = item.getName();
            if (StringUtils.isBlank(searchTerm)) {
                retMap =
                    monitorManager.getRecycleListByProcessDefinitionKey(tenantId, processDefinitionKey, page, rows);
            } else {
                retMap = monitorManager.searchRecycleListByProcessDefinitionKey(tenantId, processDefinitionKey,
                    searchTerm, page, rows);
            }
            List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
            List<HistoricProcessInstanceModel> hpiModelList = (List<HistoricProcessInstanceModel>)retMap.get("rows");
            int serialNumber = (page - 1) * rows;
            Map<String, Object> mapTemp = null;
            ProcessParamModel processParam = null;
            for (HistoricProcessInstanceModel hpim : hpiModelList) {
                mapTemp = new HashMap<String, Object>(16);
                String processInstanceId = hpim.getId();
                processParam = processParamManager.findByProcessInstanceId(tenantId, processInstanceId);
                String documentTitle = StringUtils.isBlank(processParam.getTitle()) ? "无标题" : processParam.getTitle();
                String level = processParam.getCustomLevel();
                String number = processParam.getCustomNumber();
                String completer = StringUtils.isBlank(processParam.getCompleter()) ? "无" : processParam.getCompleter();
                mapTemp.put("itemName", itemName);
                mapTemp.put("processInstanceId", processInstanceId);
                mapTemp.put(SysVariables.DOCUMENTTITLE, documentTitle);
                mapTemp.put("level", level == null ? "" : level);
                mapTemp.put("number", number == null ? "" : number);
                mapTemp.put("itemId", itemId);
                mapTemp.put("serialNumber", serialNumber + 1);
                serialNumber += 1;
                mapTemp.put("taskCreateTime", sdf.format(hpim.getStartTime()));
                if (hpim.getEndTime() != null) {
                    mapTemp.put("taskName", "已办结");
                    mapTemp.put("taskAssignee", completer);
                } else {
                    List<TaskModel> taskList = taskManager.findByProcessInstanceId(tenantId, processInstanceId);
                    List<String> listTemp = getAssigneeIdsAndAssigneeNames(taskList);
                    String assigneeNames = listTemp.get(2);
                    mapTemp.put("taskName", taskList.get(0).getName());
                    mapTemp.put("taskAssignee", assigneeNames);
                }
                items.add(mapTemp);
            }
            retMap.put("rows", items);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retMap;
    }

}
