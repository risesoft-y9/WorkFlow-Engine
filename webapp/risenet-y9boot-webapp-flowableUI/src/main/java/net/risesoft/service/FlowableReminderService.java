package net.risesoft.service;

import java.util.Map;

import net.risesoft.pojo.Y9Page;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
public interface FlowableReminderService {

    /**
     * Description:
     * 
     * @param processInstanceId
     * @param page
     * @param rows
     * @return
     */
    Y9Page<Map<String, Object>> findTaskListByProcessInstanceId(String processInstanceId, int page, int rows);
}
