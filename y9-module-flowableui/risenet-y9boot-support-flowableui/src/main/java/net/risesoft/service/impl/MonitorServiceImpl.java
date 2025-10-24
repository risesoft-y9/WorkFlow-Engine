package net.risesoft.service.impl;

import static net.risesoft.consts.FlowableUiConsts.ITEMID_KEY;

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
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.consts.FlowableUiConsts;
import net.risesoft.consts.processadmin.SysVariables;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.model.itemadmin.ChaoSongModel;
import net.risesoft.model.itemadmin.OfficeDoneInfoModel;
import net.risesoft.model.itemadmin.core.ItemModel;
import net.risesoft.model.platform.org.Position;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.service.MonitorService;
import net.risesoft.service.UtilService;
import net.risesoft.y9.Y9LoginUserHolder;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class MonitorServiceImpl implements MonitorService {

    private final TaskApi taskApi;
    private final ItemApi itemApi;
    private final OfficeDoneInfoApi officeDoneInfoApi;
    private final ChaoSongApi chaoSongApi;
    private final UtilService utilService;

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
                    mapTemp.put(FlowableUiConsts.ITEMNAME_KEY, model.getItemName());
                    mapTemp.put(SysVariables.PROCESS_SERIAL_NUMBER, processSerialNumber);
                    mapTemp.put(SysVariables.DOCUMENT_TITLE, documentTitle);
                    mapTemp.put(FlowableUiConsts.PROCESSINSTANCEID_KEY, processInstanceId);
                    mapTemp.put(FlowableUiConsts.PROCESSDEFINITIONID_KEY, processDefinitionId);
                    mapTemp.put(FlowableUiConsts.PROCESSDEFINITIONKEY_KEY, model.getProcessDefinitionKey());
                    mapTemp.put(FlowableUiConsts.STARTTIME_KEY, startTime);
                    mapTemp.put(FlowableUiConsts.ENDTIME_KEY,
                        StringUtils.isBlank(model.getEndTime()) ? "--" : model.getEndTime().substring(0, 16));
                    mapTemp.put(FlowableUiConsts.TASKDEFINITIONKEY_KEY, "");
                    mapTemp.put(FlowableUiConsts.TASKASSIGNEE_KEY, completer);
                    mapTemp.put(FlowableUiConsts.CREATEUSERNAME_KEY, model.getCreatUserName());
                    mapTemp.put(ITEMID_KEY, model.getItemId());
                    mapTemp.put(FlowableUiConsts.LEVEL_KEY, StringUtils.defaultString(level));
                    mapTemp.put(FlowableUiConsts.NUMBER_KEY, StringUtils.defaultString(number));
                    mapTemp.put(FlowableUiConsts.ITEMBOX_KEY, ItemBoxTypeEnum.DONE.getValue());
                    if (StringUtils.isBlank(model.getEndTime())) {
                        List<TaskModel> taskList =
                            taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                        List<String> listTemp = utilService.getItemBoxAndTaskId(taskList);
                        String assigneeNames = utilService.getAssigneeNames(taskList, null);
                        mapTemp.put(FlowableUiConsts.TASKDEFINITIONKEY_KEY, taskList.get(0).getTaskDefinitionKey());
                        mapTemp.put(FlowableUiConsts.TASKID_KEY,
                            listTemp.get(0).equals(ItemBoxTypeEnum.TODO.getValue()) ? listTemp.get(1) : "");
                        mapTemp.put(FlowableUiConsts.TASKASSIGNEE_KEY, assigneeNames);
                        mapTemp.put(FlowableUiConsts.ITEMBOX_KEY, listTemp.get(0));
                    }
                } catch (Exception e) {
                    LOGGER.error("获取单位所有件列表失败，异常：{}", processInstanceId, e);
                }
                items.add(mapTemp);
            }
            return Y9Page.success(page, y9Page.getTotalPages(), y9Page.getTotal(), items, "获取单位所有件列表成功");
        } catch (Exception e) {
            LOGGER.error("获取单位所有件列表失败，异常如下：", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取单位所有件列表失败!");
    }

    @Override
    public Y9Page<Map<String, Object>> pageMonitorBanjianList(String searchName, String itemId, String userName,
        String state, String year, Integer page, Integer rows) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            Y9Page<OfficeDoneInfoModel> y9Page =
                officeDoneInfoApi.searchAllList(tenantId, searchName, itemId, userName, state, year, page, rows);
            List<Map<String, Object>> items = new ArrayList<>();
            List<OfficeDoneInfoModel> officeDoneInfoList = y9Page.getRows();
            int serialNumber = (page - 1) * rows;
            for (OfficeDoneInfoModel officeDoneInfo : officeDoneInfoList) {
                Map<String, Object> itemMap = buildMonitorBanJianItem(officeDoneInfo, tenantId);
                itemMap.put(FlowableUiConsts.SERIALNUMBER_KEY, serialNumber + 1);
                serialNumber++;
                items.add(itemMap);
            }
            return Y9Page.success(page, y9Page.getTotalPages(), y9Page.getTotal(), items, "获取监控办件列表成功");
        } catch (Exception e) {
            LOGGER.error("获取监控办件列表失败！出现异常：", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取监控办件列表失败！！！");
    }

    private Map<String, Object> buildMonitorBanJianItem(OfficeDoneInfoModel officeDoneInfo, String tenantId) {
        Map<String, Object> mapTemp = new HashMap<>(16);
        String processInstanceId = officeDoneInfo.getProcessInstanceId();

        try {
            buildCommonItemFields(mapTemp, officeDoneInfo, officeDoneInfo.getItemName());

            String startTime = officeDoneInfo.getStartTime();
            String formattedStartTime =
                StringUtils.isNotBlank(startTime) && startTime.length() >= 16 ? startTime.substring(0, 16) : startTime;

            mapTemp.put(FlowableUiConsts.PROCESSDEFINITIONKEY_KEY, officeDoneInfo.getProcessDefinitionKey());
            mapTemp.put(FlowableUiConsts.STARTTIME_KEY, formattedStartTime);
            mapTemp.put(FlowableUiConsts.ENDTIME_KEY,
                StringUtils.isBlank(officeDoneInfo.getEndTime()) ? "--" : officeDoneInfo.getEndTime().substring(0, 16));
            mapTemp.put(FlowableUiConsts.TASKDEFINITIONKEY_KEY, "");
            mapTemp.put(FlowableUiConsts.TASKASSIGNEE_KEY, officeDoneInfo.getUserComplete());
            mapTemp.put(FlowableUiConsts.CREATEUSERNAME_KEY, officeDoneInfo.getCreatUserName());
            mapTemp.put(FlowableUiConsts.ITEMBOX_KEY, ItemBoxTypeEnum.DONE.getValue());

            if (StringUtils.isBlank(officeDoneInfo.getEndTime())) {
                List<TaskModel> taskList = taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                List<String> listTemp = utilService.getItemBoxAndTaskId(taskList);
                String assigneeNames = utilService.getAssigneeNames(taskList, null);
                mapTemp.put(FlowableUiConsts.TASKDEFINITIONKEY_KEY, taskList.get(0).getTaskDefinitionKey());
                mapTemp.put(FlowableUiConsts.TASKID_KEY,
                    listTemp.get(0).equals(ItemBoxTypeEnum.TODO.getValue()) ? listTemp.get(1) : "");
                mapTemp.put(FlowableUiConsts.TASKASSIGNEE_KEY, assigneeNames);
                mapTemp.put(FlowableUiConsts.ITEMBOX_KEY, listTemp.get(0));
            }
        } catch (Exception e) {
            LOGGER.error("获取任务信息失败{}", processInstanceId, e);
        }

        return mapTemp;
    }

    private void buildCommonItemFields(Map<String, Object> mapTemp, OfficeDoneInfoModel model, String itemName) {
        String processInstanceId = model.getProcessInstanceId();
        String processDefinitionId = model.getProcessDefinitionId();
        String processSerialNumber = model.getProcessSerialNumber();
        String documentTitle = StringUtils.isBlank(model.getTitle()) ? "无标题" : model.getTitle();
        String level = model.getUrgency();
        String number = model.getDocNumber();
        mapTemp.put(FlowableUiConsts.ITEMNAME_KEY, itemName);
        mapTemp.put(SysVariables.PROCESS_SERIAL_NUMBER, processSerialNumber);
        mapTemp.put(SysVariables.DOCUMENT_TITLE, documentTitle);
        mapTemp.put(FlowableUiConsts.PROCESSINSTANCEID_KEY, processInstanceId);
        mapTemp.put(FlowableUiConsts.PROCESSDEFINITIONID_KEY, processDefinitionId);
        mapTemp.put(ITEMID_KEY, model.getItemId());
        mapTemp.put(FlowableUiConsts.LEVEL_KEY, StringUtils.defaultString(level));
        mapTemp.put(FlowableUiConsts.NUMBER_KEY, StringUtils.defaultString(number));
    }

    @Override
    public Y9Page<ChaoSongModel> pageMonitorChaosongList(String searchName, String itemId, String senderName,
        String userName, String state, String year, Integer page, Integer rows) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            return chaoSongApi.searchAllList(tenantId, searchName, itemId, senderName, userName, state, year, page,
                rows);
        } catch (Exception e) {
            LOGGER.error("获取监控抄送列表失败", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取监控抄送列表失败");
    }

    @Override
    public Y9Page<Map<String, Object>> pageMonitorDoingList(String itemId, String searchTerm, Integer page,
        Integer rows) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            ItemModel item = itemApi.getByItemId(tenantId, itemId).getData();
            String processDefinitionKey = item.getWorkflowGuid();
            String itemName = item.getName();
            Y9Page<OfficeDoneInfoModel> y9Page = officeDoneInfoApi.searchByItemId(tenantId, searchTerm, itemId,
                ItemBoxTypeEnum.TODO.getValue(), "", "", page, rows);
            List<Map<String, Object>> items = new ArrayList<>();
            List<OfficeDoneInfoModel> officeDoneInfoList = y9Page.getRows();
            int serialNumber = (page - 1) * rows;
            for (OfficeDoneInfoModel model : officeDoneInfoList) {
                Map<String, Object> itemMap = buildMonitorDoingItem(model, tenantId, itemName, processDefinitionKey);
                itemMap.put(FlowableUiConsts.SERIALNUMBER_KEY, serialNumber + 1);
                serialNumber++;
                items.add(itemMap);
            }
            return Y9Page.success(page, y9Page.getTotalPages(), y9Page.getTotal(), items, "获取监控在办列表成功");
        } catch (Exception e) {
            LOGGER.error("获取监控在办列表失败，异常：", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取监控在办列表失败");
    }

    private Map<String, Object> buildMonitorDoingItem(OfficeDoneInfoModel model, String tenantId, String itemName,
        String processDefinitionKey) {
        Map<String, Object> mapTemp = new HashMap<>(16);
        String processInstanceId = model.getProcessInstanceId();

        try {
            buildCommonItemFields(mapTemp, model, itemName);

            mapTemp.put(FlowableUiConsts.PROCESSDEFINITIONKEY_KEY, processDefinitionKey);
            mapTemp.put(FlowableUiConsts.CREATEUSERNAME_KEY, model.getCreatUserName());
            mapTemp.put("status", 1);
            mapTemp.put("taskDueDate", "");

            List<TaskModel> taskList = taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
            Boolean isReminder = String.valueOf(taskList.get(0).getPriority()).contains("5");

            mapTemp.put(FlowableUiConsts.TASKDEFINITIONKEY_KEY, taskList.get(0).getTaskDefinitionKey());
            mapTemp.put("taskName", taskList.get(0).getName());

            String startTime = model.getStartTime();
            mapTemp.put("taskCreateTime",
                StringUtils.isNotBlank(startTime) && startTime.length() >= 16 ? startTime.substring(0, 16) : startTime);
            mapTemp.put(FlowableUiConsts.TASKASSIGNEE_KEY, utilService.getAssigneeNames(taskList, null));
            mapTemp.put("isReminder", isReminder);
        } catch (Exception e) {
            LOGGER.error("获取列表失败{}", processInstanceId, e);
        }

        return mapTemp;
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
            for (OfficeDoneInfoModel officeDoneInfo : hpiList) {
                mapTemp = new HashMap<>(16);
                processInstanceId = officeDoneInfo.getProcessInstanceId();
                try {
                    String processDefinitionId = officeDoneInfo.getProcessDefinitionId();
                    String startTime = officeDoneInfo.getStartTime().substring(0, 16),
                        endTime = officeDoneInfo.getEndTime().substring(0, 16);
                    String processSerialNumber = officeDoneInfo.getProcessSerialNumber();
                    String documentTitle =
                        StringUtils.isBlank(officeDoneInfo.getTitle()) ? "无标题" : officeDoneInfo.getTitle();
                    String level = officeDoneInfo.getUrgency();
                    String number = officeDoneInfo.getDocNumber();
                    String completer = officeDoneInfo.getUserComplete();
                    mapTemp.put(FlowableUiConsts.ITEMNAME_KEY, itemName);
                    mapTemp.put(SysVariables.PROCESS_SERIAL_NUMBER, processSerialNumber);
                    mapTemp.put(SysVariables.DOCUMENT_TITLE, documentTitle);
                    mapTemp.put(FlowableUiConsts.PROCESSINSTANCEID_KEY, processInstanceId);
                    mapTemp.put(FlowableUiConsts.PROCESSDEFINITIONID_KEY, processDefinitionId);
                    mapTemp.put(FlowableUiConsts.PROCESSDEFINITIONKEY_KEY, processDefinitionKey);
                    mapTemp.put(FlowableUiConsts.CREATEUSERNAME_KEY, officeDoneInfo.getCreatUserName());
                    mapTemp.put(FlowableUiConsts.STARTTIME_KEY, startTime);
                    mapTemp.put(FlowableUiConsts.ENDTIME_KEY, endTime);
                    mapTemp.put(FlowableUiConsts.TASKDEFINITIONKEY_KEY, "");
                    mapTemp.put("user4Complete", completer);
                    mapTemp.put(FlowableUiConsts.ITEMID_KEY, itemId);
                    mapTemp.put(FlowableUiConsts.LEVEL_KEY, level);
                    mapTemp.put(FlowableUiConsts.NUMBER_KEY, number);
                } catch (Exception e) {
                    LOGGER.error("获取监控办结列表失败，processInstanceId：{}", processInstanceId, e);
                }
                mapTemp.put(FlowableUiConsts.SERIALNUMBER_KEY, serialNumber + 1);
                serialNumber += 1;
                items.add(mapTemp);
            }
            return Y9Page.success(page, y9Page.getTotalPages(), y9Page.getTotal(), items, "获取监控办结列表成功");
        } catch (Exception e) {
            LOGGER.error("获取监控办结列表失败，出现异常：", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取监控办结列表失败！");
    }
}