package net.risesoft.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.flowable.engine.HistoryService;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@Service
@RequiredArgsConstructor
@DependsOn({"runtimeService", "repositoryService", "historyService", "taskService", "workflowTaskService"})
public class WorkflowHistoryTaskService {

    private final HistoryService historyService;

    public HistoricTaskInstance getTaskByProcessInstanceId(String processInstanceId) {
        HistoricTaskInstance historicTaskInstance = null;
        if (StringUtils.isNotBlank(processInstanceId)) {
            historicTaskInstance = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId)
                .orderByTaskCreateTime().desc().list().get(0);
        }
        return historicTaskInstance;
    }

    public List<HistoricTaskInstance> getTaskListByProcessInstanceId(String processInstanceId) {
        List<HistoricTaskInstance> listTask = new ArrayList<>();
        if (StringUtils.isNotBlank(processInstanceId)) {
            listTask = historyService.createHistoricTaskInstanceQuery().orderByTaskCreateTime().desc()
                .processInstanceId(processInstanceId).list();
        }
        return listTask;
    }

}
