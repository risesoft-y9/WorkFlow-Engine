package net.risesoft.service.impl;

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
import net.risesoft.api.itemadmin.OfficeFollowApi;
import net.risesoft.api.itemadmin.core.ItemApi;
import net.risesoft.api.itemadmin.core.ProcessParamApi;
import net.risesoft.api.itemadmin.form.FormDataApi;
import net.risesoft.api.itemadmin.worklist.ItemDoneApi;
import net.risesoft.consts.processadmin.SysVariables;
import net.risesoft.enums.ItemLeaveTypeEnum;
import net.risesoft.model.itemadmin.OfficeDoneInfoModel;
import net.risesoft.model.itemadmin.core.ItemModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.service.DoneService;
import net.risesoft.y9.Y9LoginUserHolder;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional(readOnly = true)
public class DoneServiceImpl implements DoneService {

    private final ItemApi itemApi;

    private final ChaoSongApi chaoSongApi;

    private final FormDataApi formDataApi;

    private final OfficeDoneInfoApi officeDoneInfoApi;

    private final OfficeFollowApi officeFollowApi;

    private final ItemDoneApi itemDoneApi;

    private final ProcessParamApi processParamApi;

    @Override
    public Y9Page<Map<String, Object>> list(String itemId, String searchTerm, Integer page, Integer rows) {
        Y9Page<OfficeDoneInfoModel> y9Page;
        String userId = Y9LoginUserHolder.getPositionId(), tenantId = Y9LoginUserHolder.getTenantId();
        ItemModel item = itemApi.getByItemId(tenantId, itemId).getData();
        String processDefinitionKey = item.getWorkflowGuid(), itemName = item.getName();
        y9Page = officeDoneInfoApi.searchByUserId(tenantId, userId, searchTerm, itemId, "", "", page, rows);
        List<Map<String, Object>> items = new ArrayList<>();
        List<OfficeDoneInfoModel> list = y9Page.getRows();
        ObjectMapper objectMapper = new ObjectMapper();
        List<OfficeDoneInfoModel> hpiModelList = objectMapper.convertValue(list, new TypeReference<>() {});
        int serialNumber = (page - 1) * rows;
        Map<String, Object> mapTemp;
        Map<String, Object> formDataMap;
        ItemLeaveTypeEnum[] arr = ItemLeaveTypeEnum.values();
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
                mapTemp.put("itemName", itemName);
                mapTemp.put(SysVariables.PROCESS_SERIAL_NUMBER, processSerialNumber);
                mapTemp.put(SysVariables.DOCUMENT_TITLE, documentTitle);
                mapTemp.put("processDefinitionId", processDefinitionId);
                mapTemp.put("processDefinitionKey", processDefinitionKey);
                mapTemp.put("startTime", startTime);
                mapTemp.put("endTime", endTime);
                mapTemp.put("taskDefinitionKey", "");
                mapTemp.put("user4Complete", completer);
                mapTemp.put("itemId", itemId);
                mapTemp.put("level", level);
                mapTemp.put("number", number);
                int chaosongNum =
                    chaoSongApi.countByUserIdAndProcessInstanceId(tenantId, userId, processInstanceId).getData();
                mapTemp.put("chaosongNum", chaosongNum);
                formDataMap = formDataApi.getData(tenantId, itemId, processSerialNumber).getData();
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

                int countFollow =
                    officeFollowApi.countByProcessInstanceId(tenantId, userId, processInstanceId).getData();
                mapTemp.put("follow", countFollow > 0);
            } catch (Exception e) {
                LOGGER.error("获取列表失败{}", processInstanceId, e);
            }
            mapTemp.put("serialNumber", serialNumber + 1);
            serialNumber += 1;
            items.add(mapTemp);
        }
        return Y9Page.success(page, y9Page.getTotalPages(), y9Page.getTotal(), items, "获取列表成功");
    }

    @Override
    public Y9Page<Map<String, Object>> list4Mobile(String itemId, String searchTerm, Integer page, Integer rows) {
        Y9Page<OfficeDoneInfoModel> y9Page;
        String userId = Y9LoginUserHolder.getPositionId(), tenantId = Y9LoginUserHolder.getTenantId();
        ItemModel item = itemApi.getByItemId(tenantId, itemId).getData();
        String processDefinitionKey = item.getWorkflowGuid(), itemName = item.getName();
        y9Page = officeDoneInfoApi.searchByUserId(tenantId, userId, searchTerm, itemId, "", "", page, rows);
        List<Map<String, Object>> items = new ArrayList<>();
        List<OfficeDoneInfoModel> hpiModelList = y9Page.getRows();
        ObjectMapper objectMapper = new ObjectMapper();
        List<OfficeDoneInfoModel> hpiList = objectMapper.convertValue(hpiModelList, new TypeReference<>() {});
        int serialNumber = (page - 1) * rows;
        Map<String, Object> mapTemp;
        for (OfficeDoneInfoModel hpim : hpiList) {
            mapTemp = new HashMap<>(16);
            try {
                String processInstanceId = hpim.getProcessInstanceId();
                String processDefinitionId = hpim.getProcessDefinitionId();
                String startTime = hpim.getStartTime().substring(0, 16), endTime = hpim.getEndTime().substring(0, 16);
                String processSerialNumber = hpim.getProcessSerialNumber();
                String documentTitle = StringUtils.isBlank(hpim.getTitle()) ? "无标题" : hpim.getTitle();
                String level = hpim.getUrgency();
                String number = hpim.getDocNumber();
                String completer = StringUtils.isBlank(hpim.getUserComplete()) ? "无" : hpim.getUserComplete();
                mapTemp.put("itemName", itemName);
                mapTemp.put(SysVariables.PROCESS_SERIAL_NUMBER, processSerialNumber);
                mapTemp.put(SysVariables.DOCUMENT_TITLE, documentTitle);
                mapTemp.put("processInstanceId", processInstanceId);
                mapTemp.put("processDefinitionId", processDefinitionId);
                mapTemp.put("processDefinitionKey", processDefinitionKey);
                mapTemp.put("startTime", startTime);
                mapTemp.put("endTime", endTime);
                mapTemp.put("taskDefinitionKey", "");
                mapTemp.put("user4Complete", completer);
                mapTemp.put("itemId", itemId);
                mapTemp.put("level", level);
                mapTemp.put("number", number);
                int chaosongNum =
                    chaoSongApi.countByUserIdAndProcessInstanceId(tenantId, userId, processInstanceId).getData();
                mapTemp.put("chaosongNum", chaosongNum);
            } catch (Exception e) {
                LOGGER.error("获取列表失败", e);
            }
            mapTemp.put("serialNumber", serialNumber + 1);
            serialNumber += 1;
            items.add(mapTemp);
        }
        return Y9Page.success(page, y9Page.getTotalPages(), y9Page.getTotal(), items, "获取列表成功");
    }
}