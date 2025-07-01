package net.risesoft.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.runtime.Execution;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import net.risesoft.util.SysVariables;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@Service
@RequiredArgsConstructor
@DependsOn({"runtimeService", "repositoryService", "historyService", "taskService"})
public class WorkflowTaskService {

    private final RuntimeService runtimeService;

    private final HistoryService historyService;

    private final TaskService taskService;

    private final CustomProcessDefinitionService customProcessDefinitionService;

    /**
     * 获取当前运行的taskDefKey
     */
    public List<String> getActiveTaskDefinitionKeys(String processInstanceId) {
        List<String> taskDefinitionKeyList = new ArrayList<>();
        List<Execution> executionList =
            runtimeService.createExecutionQuery().processInstanceId(processInstanceId).list();
        for (Execution execution : executionList) {
            if (execution != null) {
                taskDefinitionKeyList = runtimeService.getActiveActivityIds(execution.getId());
            }
        }
        return taskDefinitionKeyList;
    }

    /**
     * Description: 根据当前的task获取当前活动节点的Id
     *
     * @param task
     * @return
     */
    public String getActivitiIdByTask(Task task) {
        if (task != null) {
            String excId = task.getExecutionId();
            ExecutionEntity execution =
                (ExecutionEntity)runtimeService.createExecutionQuery().executionId(excId).singleResult();
            return execution.getActivityId();
        }
        return "";
    }

    /**
     * Description: 根据当前的taskId获取当前活动节点的Id，例如outerflow
     *
     * @param taskId
     * @return
     */
    public String getActivitiIdByTaskId(String taskId) {
        Task task = getTaskByTaskId(taskId);
        return getActivitiIdByTask(task);
    }

    /**
     * 根据创建时间，查找所有Task
     *
     * @param createTime 创建时间
     * @return
     */
    public List<Task> getListTaskByCreateTime(Date createTime) {
        return taskService.createTaskQuery().taskCreatedOn(createTime).list();
    }

    /**
     * 获取当前正在运行task并行的所有task
     *
     * @param taskId 当前正在运行的任务的Id
     * @return
     */
    public List<HistoricTaskInstance> getParallelTask(String taskId) {
        List<HistoricTaskInstance> list = new ArrayList<>();
        try {
            Task currentTask = getTaskByTaskId(taskId);
            if (currentTask != null) {
                String multinstance = customProcessDefinitionService
                    .getNode(currentTask.getProcessDefinitionId(), currentTask.getTaskDefinitionKey())
                    .getMultiInstance();
                if (multinstance.equals(SysVariables.PARALLEL)) {
                    List<HistoricTaskInstance> hisTaskList = historyService.createHistoricTaskInstanceQuery()
                        .processInstanceId(currentTask.getProcessInstanceId())
                        .taskCreatedOn(currentTask.getCreateTime()).list();
                    // 由于并行任务的创建时间可能会有延迟，所以这里创建时间相差不超过2秒的，即为当前任务的所有并行任务
                    for (HistoricTaskInstance entity : hisTaskList) {
                        if (entity.getCreateTime().getTime() - currentTask.getCreateTime().getTime() > -2
                            && entity.getCreateTime().getTime() - currentTask.getCreateTime().getTime() < 2) {
                            list.add(entity);
                        }
                    }
                }
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return list;
        }

    }

    /**
     * 获取当前任务（task）的前一个任务（task），当前任务可以是正在运行的任务，也可以是历史任务
     *
     * //@param processInstanceId 流程实例Id
     */
    public HistoricTaskInstance getPreviousTask(String taskId) {
        try {
            String taskDefKey = "";
            Task task = getTaskByTaskId(taskId);
            if (task != null) {
                if (taskId.equals(task.getId())) {
                    taskDefKey = task.getTaskDefinitionKey();
                }
                List<HistoricTaskInstance> list =
                    historyService.createHistoricTaskInstanceQuery().processInstanceId(task.getProcessInstanceId())
                        .includeTaskLocalVariables().orderByHistoricTaskInstanceStartTime().desc().list();
                if (!list.isEmpty()) {
                    for (int i = 0; i < list.size(); i++) {
                        HistoricTaskInstance historicTaskInstance = list.get(i);
                        String multiInstance =
                            customProcessDefinitionService.getNode(historicTaskInstance.getProcessDefinitionId(),
                                historicTaskInstance.getTaskDefinitionKey()).getMultiInstance();
                        if (multiInstance.equals(SysVariables.PARALLEL)) {
                            if (StringUtils.isNotBlank(taskDefKey)) {
                                if (!taskId.equals(historicTaskInstance.getId())) {
                                    // 判定是否属于同一并行任务，由于并行任务的开始时间可能会出现一点延迟，所以，这里判定时间相差不超过2秒，即为同一并行任务
                                    if (-2 <= historicTaskInstance.getCreateTime().getTime()
                                        - task.getCreateTime().getTime()
                                        && historicTaskInstance.getCreateTime().getTime()
                                            - task.getCreateTime().getTime() <= 2) {
                                        // 得到当前任务的前一个任务节点的multiInstance，PARALLEL表示并行，SEQUENTIAL表示串行
                                        Map<String, Object> localMap = historicTaskInstance.getTaskLocalVariables();
                                        // 前一个任务节点如果是并行，则要获取主办人的guid，如果主办人的guid不为空，则返回historicTaskInstance，如果不是并行，则直接返回historicTaskInstance
                                        String parallelSponsor = (String)localMap.get(SysVariables.PARALLELSPONSOR);
                                        if (StringUtils.isNotBlank(parallelSponsor)) {
                                            return historicTaskInstance;
                                        }
                                        // return historicTaskInstance;
                                    }
                                }
                            }
                        } else if (multiInstance.equals(SysVariables.SEQUENTIAL)) {
                            if (StringUtils.isNotBlank(taskDefKey)) {
                                if (taskDefKey.equals(historicTaskInstance.getTaskDefinitionKey())) {
                                    if (historicTaskInstance.getEndTime() != null) {
                                        return historicTaskInstance;
                                    }
                                } else {
                                    return historicTaskInstance;
                                }
                            }
                        } else {
                            if (!taskDefKey.equals(historicTaskInstance.getTaskDefinitionKey())) {
                                return historicTaskInstance;
                            }
                        }

                    }
                }
            } else {// 当前task已办理完成，results.size()>1时，才查出当前任务（task）的前一个任务（task）
                HistoricTaskInstance histask =
                    historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
                List<HistoricTaskInstance> list =
                    historyService.createHistoricTaskInstanceQuery().processInstanceId(histask.getProcessInstanceId())
                        .includeTaskLocalVariables().orderByHistoricTaskInstanceStartTime().desc().list();
                if (list.size() > 1) {
                    for (int i = 0; i < list.size(); i++) {
                        HistoricTaskInstance historicTaskInstance = list.get(i);
                        if (taskId.equals(historicTaskInstance.getId())) {
                            taskDefKey = historicTaskInstance.getTaskDefinitionKey();
                            continue;
                        }
                        if (StringUtils.isNotBlank(taskDefKey)) {
                            if (!taskDefKey.equals(historicTaskInstance.getTaskDefinitionKey())) {
                                // 得到当前任务的前一个任务节点的multiInstance，PARALLEL表示并行，SEQUENTIAL表示串行
                                String multiInstance = customProcessDefinitionService
                                    .getNode(historicTaskInstance.getProcessDefinitionId(),
                                        historicTaskInstance.getTaskDefinitionKey())
                                    .getMultiInstance();
                                // 前一个任务节点如果是并行，则要获取主办人的guid，如果主办人的guid不为空，则返回historicTaskInstance，如果不是并行，则直接返回historicTaskInstance
                                if (multiInstance.equals(SysVariables.PARALLEL)) {
                                    Map<String, Object> localMap = historicTaskInstance.getTaskLocalVariables();
                                    String parallelSponsor = (String)localMap.get(SysVariables.PARALLELSPONSOR);
                                    if (StringUtils.isNotBlank(parallelSponsor)) {
                                        return historicTaskInstance;
                                    }
                                } else {
                                    return historicTaskInstance;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取上一任务的发送人，这里之所以从上一步任务获取而不是从变量中的taskSenderId获取，是因为如果当前任务节点是并行串行， 那么就会找到上一发送人而不是上一任务的发送人
     *
     * @param historicTaskInstance
     * @return
     */
    public String getSendUserId(HistoricTaskInstance historicTaskInstance) {
        String sendUserId = "";
        if (historicTaskInstance != null) {
            sendUserId = historicTaskInstance.getAssignee();
        }
        return sendUserId;
    }

    /**
     * 获取上一任务的发送人，这里之所以从上一步任务获取而不是从变量中的taskSenderId获取，是因为如果当前任务节点是并行串行， 那么就会找到上一发送人而不是上一任务的发送人
     *
     * //@param historicTaskInstance
     *
     * @return
     */
    public String getSendUserId(String taskId) {
        HistoricTaskInstance historicTaskInstance = getPreviousTask(taskId);
        return getSendUserId(historicTaskInstance);
    }

    /**
     * 根据流程实例Id查询所有任务
     *
     * @param processInstanceId
     * @return
     */
    public List<Task> getTaskByProcessInstanceId(String processInstanceId) {
        List<Task> list = new ArrayList<>();
        if (StringUtils.isNotBlank(processInstanceId)) {
            list = taskService.createTaskQuery().processInstanceId(processInstanceId).orderByTaskId().desc().list();
        }
        return list;
    }

    /**
     *
     * @param processInstanceId
     * @param assgness
     * @return
     */
    public Task getTaskByProcessInstanceIdAndAssgness(String processInstanceId, String assgness) {
        return taskService.createTaskQuery().processInstanceId(processInstanceId).taskAssignee(assgness).singleResult();
    }

    /**
     * 根据任务ID获得任务实例
     *
     * @param taskId 任务ID
     * @return
     * @throws Exception
     */
    public TaskEntity getTaskByTaskId(String taskId) {
        TaskEntity task = null;
        if (StringUtils.isNotBlank(taskId)) {
            task = (TaskEntity)taskService.createTaskQuery().taskId(taskId).singleResult();
        }
        return task;
    }

    /**
     * 获取上一任务节点的Key
     *
     * @param historicTaskInstance
     * @return
     */
    public String getTaskDefKey(HistoricTaskInstance historicTaskInstance) {
        String taskDefKey = "";
        if (historicTaskInstance != null) {
            taskDefKey = historicTaskInstance.getTaskDefinitionKey();
        }
        return taskDefKey;
    }

    /**
     * 查询经历过的用户类型节点（即userTask）的条数
     */
    public long getUserTaskCount(String processInstanceId) {
        if (StringUtils.isNotBlank(processInstanceId)) {
            return historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).count();
        }
        return -1;
    }

    /**
     * 并行办理状态下是否有任务办理完成
     *
     * @param taskId 当前正在运行的任务的Id
     * @return 1表示存在，0表示不存在，-1表示错误
     */
    public int isHandledParallel(String taskId) {
        List<HistoricTaskInstance> list = getParallelTask(taskId);
        if (!list.isEmpty()) {
            int count = 0;
            for (HistoricTaskInstance task : list) {
                if (task.getEndTime() != null) {
                    count++;
                }
            }
            if (count > 0) {
                return 1;
            }
            return 0;
        }
        return -1;
    }

    /**
     * 获取流程启动人
     *
     * //@param historicTaskInstance //@return
     */
    public String startProUser(String processDefinitionId, String processInstanceId) {
        String sendUserId = "";
        if (StringUtils.isNotBlank(processDefinitionId) && StringUtils.isNotBlank(processInstanceId)) {
            List<HistoricTaskInstance> list =
                historyService.createHistoricTaskInstanceQuery().processDefinitionId(processDefinitionId)
                    .processInstanceId(processInstanceId).orderByTaskCreateTime().asc().list();
            if (list != null && !list.isEmpty()) {
                sendUserId = list.get(0).getAssignee();
            }
        }
        return sendUserId;
    }

    /**
     * 获取流程启动节点taskDefKey
     *
     * @param processDefinitionId
     * @param processInstanceId
     * @return
     */
    public String startTaskDefKey(String processDefinitionId, String processInstanceId) {
        String startTaskDefKey = "";
        if (StringUtils.isNotBlank(processDefinitionId) && StringUtils.isNotBlank(processInstanceId)) {
            List<HistoricTaskInstance> list =
                historyService.createHistoricTaskInstanceQuery().processDefinitionId(processDefinitionId)
                    .processInstanceId(processInstanceId).orderByTaskCreateTime().asc().list();
            if (list != null && !list.isEmpty()) {
                startTaskDefKey = list.get(0).getTaskDefinitionKey();
            }
        }
        return startTaskDefKey;
    }
}
