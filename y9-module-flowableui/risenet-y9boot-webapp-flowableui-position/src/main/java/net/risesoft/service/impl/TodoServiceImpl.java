package net.risesoft.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.FormDataApi;
import net.risesoft.api.itemadmin.ItemTodoApi;
import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.api.itemadmin.RemindInstanceApi;
import net.risesoft.api.itemadmin.SpeakInfoApi;
import net.risesoft.api.itemadmin.TaskVariableApi;
import net.risesoft.api.itemadmin.position.ChaoSong4PositionApi;
import net.risesoft.api.itemadmin.position.Item4PositionApi;
import net.risesoft.api.itemadmin.position.OfficeFollow4PositionApi;
import net.risesoft.api.processadmin.HistoricTaskApi;
import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.ProcessTodoApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.api.processadmin.VariableApi;
import net.risesoft.enums.ItemLeaveTypeEnum;
import net.risesoft.model.itemadmin.ActRuDetailModel;
import net.risesoft.model.itemadmin.ItemModel;
import net.risesoft.model.itemadmin.ItemPage;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.model.itemadmin.RemindInstanceModel;
import net.risesoft.model.itemadmin.TaskVariableModel;
import net.risesoft.model.processadmin.HistoricTaskInstanceModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.service.TodoService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;

@RequiredArgsConstructor
@Service(value = "todoService")
@Transactional(readOnly = true)
public class TodoServiceImpl implements TodoService {

    private final ProcessTodoApi processTodoApi;

    private final Item4PositionApi item4PositionApi;

    private final VariableApi variableApi;

    private final HistoricTaskApi historicTaskApi;

    private final ProcessParamApi processParamApi;

    private final ProcessDefinitionApi processDefinitionApi;

    private final ChaoSong4PositionApi chaoSong4PositionApi;

    private final FormDataApi formDataApi;

    private final TaskVariableApi taskvariableApi;

    private final SpeakInfoApi speakInfoApi;

    private final RemindInstanceApi remindInstanceApi;

    private final OfficeFollow4PositionApi officeFollow4PositionApi;

    private final ItemTodoApi itemTodoApi;

    private final TaskApi taskApi;

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> list(String itemId, String searchTerm, Integer page, Integer rows) {
        Map<String, Object> retMap = new HashMap<String, Object>(16);
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
            ItemModel item = item4PositionApi.getByItemId(tenantId, itemId);
            String processDefinitionKey = item.getWorkflowGuid(), itemName = item.getName();
            if (StringUtils.isBlank(searchTerm)) {
                retMap = processTodoApi.getListByUserIdAndProcessDefinitionKey(tenantId, positionId, processDefinitionKey, page, rows);
            } else {
                retMap = processTodoApi.searchListByUserIdAndProcessDefinitionKey(tenantId, positionId, processDefinitionKey, searchTerm, page, rows);
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
                try {
                    String taskId = task.getId();
                    String processInstanceId = task.getProcessInstanceId();
                    String processDefinitionId = task.getProcessDefinitionId();
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
                    String documentTitle = StringUtils.isBlank(processParam.getTitle()) ? "无标题" : processParam.getTitle();
                    String level = processParam.getCustomLevel();
                    String number = processParam.getCustomNumber();
                    mapTemp.put("itemId", itemId);
                    mapTemp.put("itemName", itemName);
                    mapTemp.put("processInstanceId", processInstanceId);
                    mapTemp.put("processDefinitionKey", processDefinitionKey);
                    mapTemp.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber);
                    mapTemp.put("processDefinitionId", processDefinitionId);
                    mapTemp.put("taskId", taskId);
                    mapTemp.put("description", description);
                    mapTemp.put("taskDefinitionKey", taskDefinitionKey);
                    mapTemp.put("taskName", taskName);
                    mapTemp.put("taskCreateTime", sdf.format(taskCreateTime));
                    mapTemp.put("taskAssignee", taskAssignee);
                    mapTemp.put(SysVariables.TASKSENDER, taskSender);
                    mapTemp.put(SysVariables.DOCUMENTTITLE, documentTitle);
                    mapTemp.put(SysVariables.ISNEWTODO, isNewTodo);
                    mapTemp.put(SysVariables.ISREMINDER, isReminder);
                    mapTemp.put(SysVariables.LEVEL, level);
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
                    int chaosongNum = chaoSong4PositionApi.countByUserIdAndProcessInstanceId(tenantId, positionId, processInstanceId);
                    mapTemp.put("chaosongNum", chaosongNum);
                    /**
                     * 红黄绿灯的情况判断，这里先不考虑
                     */
                    mapTemp.put("status", 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mapTemp.put("serialNumber", serialNumber + 1);
                serialNumber += 1;
                items.add(mapTemp);
            }
            retMap.put("rows", items);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retMap;
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public Y9Page<Map<String, Object>> listNew(String itemId, String searchTerm, Integer page, Integer rows) {
        Map<String, Object> retMap = new HashMap<String, Object>(16);
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
            ItemModel item = item4PositionApi.getByItemId(tenantId, itemId);
            String processDefinitionKey = item.getWorkflowGuid(), itemName = item.getName();
            if (StringUtils.isBlank(searchTerm)) {
                retMap = processTodoApi.getListByUserIdAndProcessDefinitionKey(tenantId, positionId, processDefinitionKey, page, rows);
            } else {
                retMap = processTodoApi.searchListByUserIdAndProcessDefinitionKey(tenantId, positionId, processDefinitionKey, searchTerm, page, rows);
            }
            List<TaskModel> list = (List<TaskModel>)retMap.get("rows");
            ObjectMapper objectMapper = new ObjectMapper();
            List<TaskModel> taslList = objectMapper.convertValue(list, new TypeReference<List<TaskModel>>() {});
            List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
            int serialNumber = (page - 1) * rows;
            Map<String, Object> vars = null;
            Collection<String> keys = null;
            Map<String, Object> mapTemp = null;
            Map<String, Object> formDataMap = null;
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
                    mapTemp.put("itemId", itemId);
                    mapTemp.put("itemName", itemName);
                    mapTemp.put("processDefinitionKey", processDefinitionKey);
                    mapTemp.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber);
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
                    formDataMap = formDataApi.getData(tenantId, itemId, processSerialNumber);
                    if (formDataMap.get("leaveType") != null) {
                        String leaveType = (String)formDataMap.get("leaveType");
                        ItemLeaveTypeEnum[] arr = ItemLeaveTypeEnum.values();
                        for (ItemLeaveTypeEnum leaveTypeEnum : arr) {
                            if (leaveType.equals(leaveTypeEnum.getValue())) {
                                formDataMap.put("leaveType", leaveTypeEnum.getName());
                                break;
                            }
                        }
                    }
                    mapTemp.put(SysVariables.LEVEL, level);
                    mapTemp.putAll(formDataMap);
                    mapTemp.put("processInstanceId", processInstanceId);
                    int speakInfoNum = speakInfoApi.getNotReadCount(tenantId, Y9LoginUserHolder.getPersonId(), processInstanceId);
                    mapTemp.put("speakInfoNum", speakInfoNum);
                    mapTemp.put("remindSetting", false);
                    RemindInstanceModel remindInstanceModel = remindInstanceApi.getRemindInstance(tenantId, Y9LoginUserHolder.getPersonId(), processInstanceId);
                    if (remindInstanceModel != null) {// 流程实例是否设置消息提醒
                        mapTemp.put("remindSetting", true);
                    }

                    int countFollow = officeFollow4PositionApi.countByProcessInstanceId(tenantId, positionId, processInstanceId);
                    mapTemp.put("follow", countFollow > 0 ? true : false);

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
    public Y9Page<Map<String, Object>> searchList(String itemId, String tableName, String searchMapStr, Integer page, Integer rows) {
        ItemPage<ActRuDetailModel> itemPage = new ItemPage<ActRuDetailModel>();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
            ItemModel item = item4PositionApi.getByItemId(tenantId, itemId);
            String systemName = item.getSystemName(), itemName = item.getName();
            if (StringUtils.isBlank(searchMapStr)) {
                itemPage = itemTodoApi.findByUserIdAndSystemName(tenantId, positionId, systemName, page, rows);
            } else {
                itemPage = itemTodoApi.searchByUserIdAndSystemName(tenantId, positionId, systemName, tableName, searchMapStr, page, rows);
            }
            List<ActRuDetailModel> list = itemPage.getRows();
            ObjectMapper objectMapper = new ObjectMapper();
            List<ActRuDetailModel> taslList = objectMapper.convertValue(list, new TypeReference<List<ActRuDetailModel>>() {});
            List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
            int serialNumber = (page - 1) * rows;
            Map<String, Object> vars = null;
            Collection<String> keys = null;
            Map<String, Object> mapTemp = null;
            Map<String, Object> formDataMap = null;
            ProcessParamModel processParam = null;
            for (ActRuDetailModel ardModel : taslList) {
                mapTemp = new HashMap<String, Object>(16);
                String taskId = ardModel.getTaskId();
                String processInstanceId = ardModel.getProcessInstanceId();
                try {
                    String processSerialNumber = ardModel.getProcessSerialNumber();
                    Date taskCreateTime = ardModel.getCreateTime();
                    mapTemp.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber);
                    mapTemp.put("itemId", itemId);
                    mapTemp.put("itemName", itemName);
                    mapTemp.put("taskCreateTime", sdf.format(taskCreateTime));
                    TaskModel task = taskApi.findById(tenantId, taskId);
                    mapTemp.put("taskId", taskId);
                    String processDefinitionId = task.getProcessDefinitionId();
                    String taskAssignee = ardModel.getAssignee();
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
                    mapTemp.put("processDefinitionKey", processDefinitionId.split(":")[0]);
                    mapTemp.put("processDefinitionId", processDefinitionId);
                    mapTemp.put("description", "");
                    mapTemp.put("taskDefinitionKey", taskDefinitionKey);
                    mapTemp.put("taskName", taskName);
                    mapTemp.put("taskAssignee", taskAssignee);
                    mapTemp.put(SysVariables.TASKSENDER, taskSender);
                    mapTemp.put(SysVariables.ISNEWTODO, isNewTodo);
                    mapTemp.put(SysVariables.ISREMINDER, isReminder);
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
                    formDataMap = formDataApi.getData(tenantId, itemId, processSerialNumber);
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
                    int speakInfoNum = speakInfoApi.getNotReadCount(tenantId, Y9LoginUserHolder.getPersonId(), processInstanceId);
                    mapTemp.put("speakInfoNum", speakInfoNum);
                    mapTemp.put("remindSetting", false);
                    RemindInstanceModel remindInstanceModel = remindInstanceApi.getRemindInstance(tenantId, Y9LoginUserHolder.getPersonId(), processInstanceId);
                    if (remindInstanceModel != null) {// 流程实例是否设置消息提醒
                        mapTemp.put("remindSetting", true);
                    }

                    int countFollow = officeFollow4PositionApi.countByProcessInstanceId(tenantId, positionId, processInstanceId);
                    mapTemp.put("follow", countFollow > 0 ? true : false);

                    String rollBack = variableApi.getVariableLocal(tenantId, taskId, SysVariables.ROLLBACK);
                    if (rollBack != null && Boolean.valueOf(rollBack)) {// 退回件
                        mapTemp.put("rollBack", true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mapTemp.put("serialNumber", serialNumber + 1);
                serialNumber += 1;
                items.add(mapTemp);
            }
            return Y9Page.success(page, itemPage.getTotalpages(), itemPage.getTotal(), items, "获取列表成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Page.success(page, 0, 0, new ArrayList<Map<String, Object>>(), "获取列表失败");
    }
}
