package net.risesoft.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.flowable.engine.HistoryService;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@Service
@DependsOn({"runtimeService", "repositoryService", "historyService", "taskService", "workflowTaskService"})
public class WorkflowHistoryTaskService {

    @Autowired
    protected HistoryService historyService;

    public HistoricTaskInstance getTaskByProcessInstanceId(String processInstanceId) {
        HistoricTaskInstance historicTaskInstance = null;
        if (StringUtils.isNotBlank(processInstanceId)) {
            historicTaskInstance = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId)
                .orderByTaskCreateTime().desc().list().get(0);
        }
        return historicTaskInstance;
    }

    public List<HistoricTaskInstance> getTaskListByProcessInstanceId(String processInstanceId) {
        List<HistoricTaskInstance> listTask = new ArrayList<HistoricTaskInstance>();
        if (StringUtils.isNotBlank(processInstanceId)) {
            listTask = historyService.createHistoricTaskInstanceQuery().orderByTaskCreateTime().desc()
                .processInstanceId(processInstanceId).list();
        }
        return listTask;
    }

}
