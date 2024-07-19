package net.risesoft.service;

import java.util.Map;

import net.risesoft.pojo.Y9Page;

public interface FlowableReminderService {

    Y9Page<Map<String, Object>> pageTaskListByProcessInstanceId(String processInstanceId, int page, int rows);
}
