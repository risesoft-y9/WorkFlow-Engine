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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

import net.risesoft.api.itemadmin.ChaoSongInfoApi;
import net.risesoft.api.itemadmin.FormDataApi;
import net.risesoft.api.itemadmin.ItemApi;
import net.risesoft.api.itemadmin.OfficeFollowApi;
import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.api.itemadmin.RemindInstanceApi;
import net.risesoft.api.itemadmin.SpeakInfoApi;
import net.risesoft.api.itemadmin.TaskVariableApi;
import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.ProcessTodoApi;
import net.risesoft.api.processadmin.VariableApi;
import net.risesoft.enums.ItemLeaveTypeEnum;
import net.risesoft.model.itemadmin.ItemModel;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.model.itemadmin.RemindInstanceModel;
import net.risesoft.model.itemadmin.TaskVariableModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Page;
import net.risesoft.service.TodoService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@Service(value = "todoService")
@Transactional(readOnly = true)
public class TodoServiceImpl implements TodoService {

    @Autowired
    private ProcessTodoApi todoManager;

    @Autowired
    private ItemApi itemManager;

    @Autowired
    private VariableApi variableManager;

    @Autowired
    private ProcessParamApi processParamManager;

    @Autowired
    private ProcessDefinitionApi processDefinitionManager;

    @Autowired
    private ChaoSongInfoApi chaoSongInfoManager;

    @Autowired
    private FormDataApi formDataManager;

    @Autowired
    private TaskVariableApi taskVariableManager;

    @Autowired
    private SpeakInfoApi speakInfoManager;

    @Autowired
    private RemindInstanceApi remindInstanceManager;

    @Autowired
    private OfficeFollowApi officeFollowManager;

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> list(String itemId, String searchTerm, Integer page, Integer rows) {
        Map<String, Object> retMap = new HashMap<String, Object>(16);
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
            String tenantId = Y9LoginUserHolder.getTenantId(), userId = userInfo.getPersonId();
            ItemModel item = itemManager.getByItemId(tenantId, itemId);
            String processDefinitionKey = item.getWorkflowGuid(), itemName = item.getName();
            if (StringUtils.isBlank(searchTerm)) {
                retMap = todoManager.getListByUserIdAndProcessDefinitionKey(tenantId, userId, processDefinitionKey,
                    page, rows);
            } else {
                retMap = todoManager.searchListByUserIdAndProcessDefinitionKey(tenantId, userId, processDefinitionKey,
                    searchTerm, page, rows);
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
                    vars = variableManager.getVariablesByProcessInstanceId(tenantId, processInstanceId, keys);
                    String taskSender = Strings.nullToEmpty((String)vars.get(SysVariables.TASKSENDER));
                    int isNewTodo = StringUtils.isBlank(task.getFormKey()) ? 1 : Integer.parseInt(task.getFormKey());
                    // 催办的时候任务的优先级+5
                    Boolean isReminder = String.valueOf(priority).contains("8");
                    processParam = processParamManager.findByProcessInstanceId(tenantId, processInstanceId);
                    String processSerialNumber = processParam.getProcessSerialNumber();
                    String documentTitle =
                        StringUtils.isBlank(processParam.getTitle()) ? "无标题" : processParam.getTitle();
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
                    String multiInstance = processDefinitionManager.getNodeType(tenantId, task.getProcessDefinitionId(),
                        task.getTaskDefinitionKey());
                    mapTemp.put("isZhuBan", "");
                    if (multiInstance.equals(SysVariables.PARALLEL)) {
                        mapTemp.put("isZhuBan", "false");
                        String sponsorGuid = processParam.getSponsorGuid();
                        if (StringUtils.isNotBlank(sponsorGuid)) {
                            if (task.getAssignee().equals(sponsorGuid)) {
                                mapTemp.put("isZhuBan", "true");
                            }
                        }
                        String obj = variableManager.getVariableByProcessInstanceId(tenantId, task.getExecutionId(),
                            SysVariables.NROFACTIVEINSTANCES);
                        Integer nrOfActiveInstances = obj != null ? Integer.valueOf(obj) : 0;
                        if (nrOfActiveInstances == 1) {
                            mapTemp.put("isZhuBan", "true");
                        }
                        if (StringUtils.isNotBlank(task.getOwner()) && !task.getOwner().equals(task.getAssignee())) {
                            mapTemp.put("isZhuBan", "");
                        }
                    }
                    int chaosongNum =
                        chaoSongInfoManager.countByUserIdAndProcessInstanceId(tenantId, userId, processInstanceId);
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

    @SuppressWarnings("unchecked")
    @Override
    public Y9Page<Map<String, Object>> listNew(String itemId, String searchTerm, Integer page, Integer rows) {
        Map<String, Object> retMap = new HashMap<String, Object>(16);
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
            String tenantId = Y9LoginUserHolder.getTenantId(), userId = userInfo.getPersonId();
            ItemModel item = itemManager.getByItemId(tenantId, itemId);
            String processDefinitionKey = item.getWorkflowGuid(), itemName = item.getName();
            if (StringUtils.isBlank(searchTerm)) {
                retMap = todoManager.getListByUserIdAndProcessDefinitionKey(tenantId, userId, processDefinitionKey,
                    page, rows);
            } else {
                retMap = todoManager.searchListByUserIdAndProcessDefinitionKey(tenantId, userId, processDefinitionKey,
                    searchTerm, page, rows);
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
            ItemLeaveTypeEnum[] arr = ItemLeaveTypeEnum.values();
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
                    vars = variableManager.getVariablesByProcessInstanceId(tenantId, processInstanceId, keys);
                    String taskSender = Strings.nullToEmpty((String)vars.get(SysVariables.TASKSENDER));
                    int isNewTodo = StringUtils.isBlank(task.getFormKey()) ? 1 : Integer.parseInt(task.getFormKey());
                    // 催办的时候任务的优先级+5
                    Boolean isReminder = String.valueOf(priority).contains("8");
                    processParam = processParamManager.findByProcessInstanceId(tenantId, processInstanceId);
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
                    String multiInstance = processDefinitionManager.getNodeType(tenantId, task.getProcessDefinitionId(),
                        task.getTaskDefinitionKey());
                    mapTemp.put("isZhuBan", "");
                    if (multiInstance.equals(SysVariables.PARALLEL)) {
                        mapTemp.put("isZhuBan", "false");
                        String sponsorGuid = processParam.getSponsorGuid();
                        if (StringUtils.isNotBlank(sponsorGuid)) {
                            if (task.getAssignee().equals(sponsorGuid)) {
                                mapTemp.put("isZhuBan", "true");
                            }
                        }
                        String obj = variableManager.getVariableByProcessInstanceId(tenantId, task.getExecutionId(),
                            SysVariables.NROFACTIVEINSTANCES);
                        Integer nrOfActiveInstances = obj != null ? Integer.valueOf(obj) : 0;
                        if (nrOfActiveInstances == 1) {
                            mapTemp.put("isZhuBan", "true");
                        }
                        if (StringUtils.isNotBlank(task.getOwner()) && !task.getOwner().equals(task.getAssignee())) {
                            mapTemp.put("isZhuBan", "");
                        }
                    }
                    mapTemp.put("isForwarding", false);
                    TaskVariableModel taskVariableModel =
                        taskVariableManager.findByTaskIdAndKeyName(tenantId, taskId, "isForwarding").getData();
                    // 是否正在发送标识
                    if (taskVariableModel != null) {
                        mapTemp.put("isForwarding", taskVariableModel.getText().contains("true"));
                    }
                    formDataMap = formDataManager.getData(tenantId, itemId, processSerialNumber);
                    if (formDataMap.get("leaveType") != null) {
                        String leaveType = (String)formDataMap.get("leaveType");
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
                    int speakInfoNum = speakInfoManager.getNotReadCount(tenantId, userId, processInstanceId).getData();
                    mapTemp.put("speakInfoNum", speakInfoNum);
                    mapTemp.put("remindSetting", false);
                    RemindInstanceModel remindInstanceModel =
                        remindInstanceManager.getRemindInstance(tenantId, userId, processInstanceId).getData();
                    // 流程实例是否设置消息提醒
                    if (remindInstanceModel != null) {
                        mapTemp.put("remindSetting", true);
                    }

                    int countFollow = officeFollowManager.countByProcessInstanceId(tenantId, userId, processInstanceId);
                    mapTemp.put("follow", countFollow > 0);

                    String rollBack = variableManager.getVariableLocal(tenantId, taskId, SysVariables.ROLLBACK);
                    if (rollBack != null && Boolean.valueOf(rollBack)) {
                        mapTemp.put("rollBack", true);
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
}
