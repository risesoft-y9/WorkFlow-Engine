package net.risesoft.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.risesoft.api.org.OrgUnitApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.model.OrgUnit;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.service.FlowableReminderService;
import net.risesoft.y9.Y9LoginUserHolder;

@Service(value = "flowableReminderService")
public class FlowableReminderServiceImpl implements FlowableReminderService {

    @Autowired
    private TaskApi taskManager;

    @Autowired
    private OrgUnitApi orgUnitApi;

    @SuppressWarnings("unchecked")
    @Override
    public Y9Page<Map<String, Object>> findTaskListByProcessInstanceId(String processInstanceId, int page, int rows) {
        Map<String, Object> retMap = new HashMap<>(16);
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            retMap = taskManager.findListByProcessInstanceId(tenantId, processInstanceId, page, rows);
            List<TaskModel> list = (List<TaskModel>)retMap.get("rows");
            ObjectMapper objectMapper = new ObjectMapper();
            List<TaskModel> taskList = objectMapper.convertValue(list, new TypeReference<List<TaskModel>>() {});
            List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
            int serialNumber = (page - 1) * rows;
            Map<String, Object> mapTemp = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date currentTime = new Date();
            for (TaskModel task : taskList) {
                mapTemp = new HashMap<String, Object>(16);
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
            return Y9Page.success(page, Integer.parseInt(retMap.get("totalpages").toString()),
                Integer.parseInt(retMap.get("total").toString()), items, "获取列表成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Page.success(page, 0, 0, new ArrayList<Map<String, Object>>(), "获取列表失败");
    }

    private String longTime(Date startTime, Date endTime) {
        if (endTime == null) {
            return "";
        } else {
            Date d1 = endTime;
            Date d2 = startTime;
            long time = d1.getTime() - d2.getTime();
            time = time / 1000;
            int s = (int)(time % 60);
            int m = (int)(time / 60 % 60);
            int h = (int)(time / 3600 % 24);
            int d = (int)(time / 86400);
            String str = d + " 天  " + h + " 小时 " + m + " 分 " + s + " 秒 ";
            return str;
        }
    }

}
