package net.risesoft.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.ChaoSongApi;
import net.risesoft.api.itemadmin.OfficeDoneInfoApi;
import net.risesoft.api.itemadmin.OfficeFollowApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.consts.processadmin.SysVariables;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.model.itemadmin.ChaoSongModel;
import net.risesoft.model.itemadmin.OfficeDoneInfoModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.service.SearchService;
import net.risesoft.service.UtilService;
import net.risesoft.y9.Y9LoginUserHolder;

@Slf4j
@RequiredArgsConstructor
@Service
public class SearchServiceImpl implements SearchService {

    private final ChaoSongApi chaoSongApi;

    private final OfficeFollowApi officeFollowApi;

    private final OfficeDoneInfoApi officeDoneInfoApi;

    private final TaskApi taskApi;

    private final UtilService utilService;

    @Override
    public Y9Page<Map<String, Object>> pageSearchList(String searchTerm, String itemId, String userName, String state,
        String year, String startDate, String endDate, Integer page, Integer rows) {
        try {
            String positionId = Y9LoginUserHolder.getPositionId();
            String tenantId = Y9LoginUserHolder.getTenantId();
            Y9Page<OfficeDoneInfoModel> y9Page = officeDoneInfoApi.searchAllByUserId(tenantId, positionId, searchTerm,
                itemId, userName, state, year, startDate, endDate, page, rows);
            List<Map<String, Object>> items = new ArrayList<>();
            List<OfficeDoneInfoModel> officeDoneInfoList = y9Page.getRows();
            int serialNumber = (page - 1) * rows;
            for (OfficeDoneInfoModel model : officeDoneInfoList) {
                Map<String, Object> itemMap = buildSearchListItem(model, tenantId, positionId);
                itemMap.put("serialNumber", serialNumber + 1);
                serialNumber++;
                items.add(itemMap);
            }
            return Y9Page.success(page, y9Page.getTotalPages(), y9Page.getTotal(), items, "获取列表成功");
        } catch (Exception e) {
            LOGGER.error("获取个人所有件列表失败，异常：", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取个人所有件列表失败！");
    }

    private Map<String, Object> buildSearchListItem(OfficeDoneInfoModel model, String tenantId, String positionId) {
        Map<String, Object> mapTemp = new HashMap<>(16);
        String processInstanceId = model.getProcessInstanceId();
        try {
            String processDefinitionId = model.getProcessDefinitionId();
            // 添加安全检查防止字符串截取异常
            String startTime = model.getStartTime();
            String formattedStartTime =
                StringUtils.isNotBlank(startTime) && startTime.length() >= 16 ? startTime.substring(0, 16) : startTime;
            String processSerialNumber = model.getProcessSerialNumber();
            String documentTitle = StringUtils.isBlank(model.getTitle()) ? "无标题" : model.getTitle();
            String level = model.getUrgency();
            String number = model.getDocNumber();
            String completer = model.getUserComplete();
            mapTemp.put("itemName", model.getItemName());
            mapTemp.put(SysVariables.PROCESS_SERIAL_NUMBER, processSerialNumber);
            mapTemp.put(SysVariables.DOCUMENT_TITLE, documentTitle);
            mapTemp.put("processInstanceId", processInstanceId);
            mapTemp.put("processDefinitionId", processDefinitionId);
            mapTemp.put("processDefinitionKey", model.getProcessDefinitionKey());
            mapTemp.put("startTime", formattedStartTime);
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
                List<TaskModel> taskList = taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                List<String> listTemp = utilService.getItemBoxAndTaskId(taskList);
                String assigneeNames = utilService.getAssigneeNames(taskList, null);
                mapTemp.put("taskDefinitionKey", taskList.get(0).getTaskDefinitionKey());
                mapTemp.put("taskId", listTemp.get(0).equals(ItemBoxTypeEnum.TODO.getValue()) ? listTemp.get(1) : "");
                mapTemp.put("taskAssignee", assigneeNames);
                mapTemp.put("itembox", listTemp.get(2));
            }
            int countFollow =
                officeFollowApi.countByProcessInstanceId(tenantId, positionId, processInstanceId).getData();
            mapTemp.put("follow", countFollow > 0);
        } catch (Exception e) {
            LOGGER.error("获取任务信息失败{}", processInstanceId, e);
        }
        return mapTemp;
    }

    @Override
    public Y9Page<ChaoSongModel> pageYuejianList(String searchName, String itemId, String userName, String state,
        String year, Integer page, Integer rows) {
        try {
            String positionId = Y9LoginUserHolder.getPositionId(), tenantId = Y9LoginUserHolder.getTenantId();
            return chaoSongApi.searchAllByUserId(tenantId, positionId, searchName, itemId, userName, state, year, page,
                rows);
        } catch (Exception e) {
            LOGGER.error("获取阅件列表失败，异常：", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取阅件列表失败");
    }

}
