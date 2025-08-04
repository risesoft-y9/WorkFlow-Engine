package net.risesoft.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.service.FlowableReminderService;
import net.risesoft.y9.Y9LoginUserHolder;

@Slf4j
@RequiredArgsConstructor
@Service(value = "flowableReminderService")
public class FlowableReminderServiceImpl implements FlowableReminderService {

    private final TaskApi taskApi;

    private final OrgUnitApi orgUnitApi;

    private String longTime(Date startTime, Date endTime) {
        if (endTime == null) {
            return "";
        } else {
            long time = endTime.getTime() - startTime.getTime();
            time = time / 1000;
            int s = (int)(time % 60);
            int m = (int)(time / 60 % 60);
            int h = (int)(time / 3600 % 24);
            int d = (int)(time / 86400);
            return d + " 天  " + h + " 小时 " + m + " 分 " + s + " 秒 ";
        }
    }

    @Override
    public Y9Page<Map<String, Object>> pageTaskListByProcessInstanceId(String processInstanceId, int page, int rows) {
        Y9Page<TaskModel> taskPage;
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            taskPage = taskApi.findListByProcessInstanceId(tenantId, processInstanceId, page, rows);
            List<TaskModel> list = taskPage.getRows();
            ObjectMapper objectMapper = new ObjectMapper();
            List<TaskModel> taskList = objectMapper.convertValue(list, new TypeReference<>() {});
            List<Map<String, Object>> items = new ArrayList<>();
            int serialNumber = (page - 1) * rows;
            Map<String, Object> mapTemp;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date currentTime = new Date();
            for (TaskModel task : taskList) {
                mapTemp = new HashMap<>(16);
                String taskId = task.getId();
                String taskName = task.getName();
                mapTemp.put("taskId", taskId);
                OrgUnit orgUnit = orgUnitApi.getOrgUnit(tenantId, task.getAssignee()).getData();
                mapTemp.put("userName", StringUtils.isBlank(task.getAssignee()) ? "" : orgUnit.getName());
                mapTemp.put("taskName", taskName);
                mapTemp.put("createTime", sdf.format(task.getCreateTime()));
                mapTemp.put("duration", longTime(task.getCreateTime(), currentTime));
                mapTemp.put("serialNumber", serialNumber + 1);
                serialNumber += 1;
                items.add(mapTemp);
            }
            return Y9Page.success(page, taskPage.getTotalPages(), taskPage.getTotal(), items, "获取列表成功");
        } catch (Exception e) {
            LOGGER.error("获取列表失败", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取列表失败");
    }

}
