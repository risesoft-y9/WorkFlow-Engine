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

import net.risesoft.api.itemadmin.OfficeDoneInfoApi;
import net.risesoft.api.itemadmin.OfficeFollowApi;
import net.risesoft.api.itemadmin.core.ItemApi;
import net.risesoft.api.itemadmin.worklist.QueryListApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.consts.FlowableUiConsts;
import net.risesoft.consts.processadmin.SysVariables;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.model.ItemBoxAndTaskIdModel;
import net.risesoft.model.itemadmin.OfficeDoneInfoModel;
import net.risesoft.model.itemadmin.core.ActRuDetailModel;
import net.risesoft.model.itemadmin.core.ItemModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.service.HandleFormDataService;
import net.risesoft.service.QueryListService;
import net.risesoft.service.UtilService;
import net.risesoft.y9.Y9FlowableHolder;
import net.risesoft.y9.Y9LoginUserHolder;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class QueryListServiceImpl implements QueryListService {

    private final ItemApi itemApi;

    private final HandleFormDataService handleFormDataService;

    private final OfficeFollowApi officeFollowApi;

    private final QueryListApi queryListApi;

    private final TaskApi taskApi;

    private final OfficeDoneInfoApi officeDoneInfoApi;

    private final UtilService utilService;

    @Override
    public Y9Page<Map<String, Object>> pageQueryList(String itemId, String state, String createDate, String tableName,
        String searchMapStr, Integer page, Integer rows) {
        Y9Page<ActRuDetailModel> itemPage;
        String userId = Y9FlowableHolder.getPositionId(), tenantId = Y9LoginUserHolder.getTenantId();
        try {
            ItemModel item = itemApi.getByItemId(tenantId, itemId).getData();
            itemPage = queryListApi.getQueryList(tenantId, userId, item.getSystemName(), state, createDate, tableName,
                searchMapStr, page, rows);
            List<Map<String, Object>> items = new ArrayList<>();
            List<ActRuDetailModel> list = itemPage.getRows();
            ObjectMapper objectMapper = new ObjectMapper();
            List<ActRuDetailModel> hpiModelList = objectMapper.convertValue(list, new TypeReference<>() {});
            int serialNumber = (page - 1) * rows;
            Map<String, Object> mapTemp;
            String processInstanceId;
            String title = "";
            List<String> processSerialNumbers = new ArrayList<>();
            for (ActRuDetailModel actRuDetail : hpiModelList) {
                processSerialNumbers.add(actRuDetail.getProcessSerialNumber());
                mapTemp = new HashMap<>(16);
                processInstanceId = actRuDetail.getProcessInstanceId();
                String processSerialNumber = actRuDetail.getProcessSerialNumber();
                try {
                    OfficeDoneInfoModel officeDoneInfo =
                        officeDoneInfoApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                    title = officeDoneInfo.getTitle();
                    String startTime = officeDoneInfo.getStartTime().substring(0, 16);
                    mapTemp.put(SysVariables.PROCESS_SERIAL_NUMBER, processSerialNumber);
                    mapTemp.put("processInstanceId", processInstanceId);
                    mapTemp.put("processDefinitionKey", actRuDetail.getProcessDefinitionKey());
                    mapTemp.put("startTime", startTime);
                    mapTemp.put("endTime", StringUtils.isBlank(officeDoneInfo.getEndTime()) ? "--"
                        : officeDoneInfo.getEndTime().substring(0, 16));
                    mapTemp.put("taskDefinitionKey", "");
                    mapTemp.put("taskAssignee", officeDoneInfo.getUserComplete());
                    mapTemp.put("creatUserName", officeDoneInfo.getCreatUserName());
                    mapTemp.put("itemId", actRuDetail.getItemId());
                    mapTemp.put("level", StringUtils.defaultString(officeDoneInfo.getUrgency()));
                    mapTemp.put("number", StringUtils.defaultString(officeDoneInfo.getDocNumber()));
                    mapTemp.put("itembox", ItemBoxTypeEnum.DONE.getValue());
                    if (StringUtils.isBlank(officeDoneInfo.getEndTime())) {
                        List<TaskModel> taskList =
                            taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                        String assigneeNames = utilService.getAssigneeNames(taskList, null);
                        mapTemp.put("taskAssignee", assigneeNames);
                        mapTemp.put("taskDefinitionKey", taskList.get(0).getTaskDefinitionKey());
                        ItemBoxAndTaskIdModel itemBoxAndTaskId = utilService.getItemBoxAndTaskId(taskList);
                        mapTemp.put(FlowableUiConsts.ITEMBOX_KEY, itemBoxAndTaskId.getItemBox());
                        mapTemp.put(FlowableUiConsts.TASKID_KEY, itemBoxAndTaskId.getTaskId());
                    }
                    mapTemp.put("processInstanceId", processInstanceId);
                    int countFollow =
                        officeFollowApi.countByProcessInstanceId(tenantId, userId, processInstanceId).getData();
                    mapTemp.put("follow", countFollow > 0);
                } catch (Exception e) {
                    LOGGER.error("获取流程实例信息失败title:{},processInstanceId:{}", title, processInstanceId, e);
                }
                mapTemp.put("serialNumber", serialNumber + 1);
                serialNumber += 1;
                items.add(mapTemp);
            }
            handleFormDataService.execute(itemId, items, processSerialNumbers);
            return Y9Page.success(page, itemPage.getTotalPages(), itemPage.getTotal(), items, "获取列表成功");
        } catch (Exception e) {
            LOGGER.error("获取列表失败", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取列表失败");
    }
}
