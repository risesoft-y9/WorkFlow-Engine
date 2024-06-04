package net.risesoft.service.impl;

import lombok.RequiredArgsConstructor;
import net.risesoft.service.CustomHistoricTaskService;
import net.risesoft.service.WorkflowProcessDefinitionService;
import net.risesoft.util.SysVariables;
import org.apache.commons.lang3.StringUtils;
import org.flowable.engine.HistoryService;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service(value = "customHistoricTaskService")
public class CustomHistoricTaskServiceImpl implements CustomHistoricTaskService {

    private final  HistoryService historyService;

    private final  WorkflowProcessDefinitionService workflowProcessDefinitionService;

    @Override
    public HistoricTaskInstance getById(String taskId) {
        return historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
    }

    @Override
    public List<HistoricTaskInstance> getByProcessInstanceId(String processInstanceId, String year) {
        if (StringUtils.isBlank(year)) {
            return historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId)
                .orderByHistoricTaskInstanceStartTime().asc().list();
        } else {
            String sql = "SELECT DISTINCT" + "	RES.*" + " FROM" + "	ACT_HI_TASKINST_" + year + " RES" + " WHERE"
                + "	RES.PROC_INST_ID_ = '" + processInstanceId + "'" + " ORDER BY" + "	RES.START_TIME_ ASC";
            return historyService.createNativeHistoricTaskInstanceQuery().sql(sql).list();
        }
    }

    @Override
    public List<HistoricTaskInstance> getByProcessInstanceIdOrderByEndTimeAsc(String processInstanceId, String year) {
        if (StringUtils.isNotEmpty(year)) {
            String sql = "SELECT DISTINCT" + "	RES.*" + " FROM" + "	ACT_HI_TASKINST_" + year + " RES" + " WHERE"
                + "	RES.PROC_INST_ID_ = '" + processInstanceId + "'" + " ORDER BY" + "	RES.END_TIME_ ASC";
            return historyService.createNativeHistoricTaskInstanceQuery().sql(sql).list();
        } else {
            return historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId)
                .orderByHistoricTaskInstanceEndTime().asc().list();
        }
    }

    @Override
    public List<HistoricTaskInstance> getByProcessInstanceIdOrderByEndTimeDesc(String processInstanceId, String year) {
        if (StringUtils.isNotEmpty(year)) {
            String sql = "SELECT DISTINCT" + "	RES.*" + " FROM" + "	ACT_HI_TASKINST_" + year + " RES" + " WHERE"
                + "	RES.PROC_INST_ID_ = '" + processInstanceId + "'" + " ORDER BY" + "	RES.END_TIME_ DESC";
            return historyService.createNativeHistoricTaskInstanceQuery().sql(sql).list();
        } else {
            return historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId)
                .orderByHistoricTaskInstanceEndTime().desc().list();
        }
    }

    @Override
    public List<HistoricTaskInstance> getByProcessInstanceIdOrderByStartTimeAsc(String processInstanceId, String year) {
        if (StringUtils.isNotEmpty(year)) {
            String sql = "SELECT DISTINCT" + "  RES.*" + " FROM" + "    ACT_HI_TASKINST_" + year + " RES" + " WHERE"
                + "    RES.PROC_INST_ID_ = '" + processInstanceId + "'" + " ORDER BY" + "  RES.START_TIME_ ASC";
            return historyService.createNativeHistoricTaskInstanceQuery().sql(sql).list();
        } else {
            return historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId)
                .orderByHistoricTaskInstanceStartTime().asc().list();
        }
    }

    @Override
    public long getFinishedCountByExecutionId(String executionId) {
        return historyService.createHistoricTaskInstanceQuery().executionId(executionId).finished().count();
    }

    @Override
    public HistoricTaskInstance getThePreviousTask(String taskId) {
        try {
            String currentTaskDefKey = "", currentExecutionId = "", processDefineId = "", currentMultiInstance = "";
            HistoricTaskInstance currenthti =
                historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
            String processInstanceId = currenthti.getProcessInstanceId();
            List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(processInstanceId).orderByHistoricTaskInstanceStartTime().desc().list();

            currentTaskDefKey = currenthti.getTaskDefinitionKey();
            currentExecutionId = currenthti.getExecutionId();
            processDefineId = currenthti.getProcessDefinitionId();
            currentMultiInstance =
                workflowProcessDefinitionService.getMultiinstanceType(processDefineId, currentTaskDefKey);
            long currentTime = currenthti.getCreateTime().getTime(), tempTime = 0;
            if (currentMultiInstance.equals(SysVariables.PARALLEL)) {
                for (HistoricTaskInstance htiTemp : list) {
                    tempTime = htiTemp.getCreateTime().getTime();
                    if (!htiTemp.getTaskDefinitionKey().equals(currenthti.getTaskDefinitionKey())) {
                        return htiTemp;
                    } else {
                        if ((-1000 > (currentTime - tempTime) || (currentTime - tempTime) > 1000)) {
                            return htiTemp;
                        }
                    }
                }
            } else if (currentMultiInstance.equals(SysVariables.SEQUENTIAL)) {
                for (HistoricTaskInstance htiTemp : list) {
                    if (!currentExecutionId.equals(htiTemp.getExecutionId())) {
                        return htiTemp;
                    }
                }
            } else {
                for (HistoricTaskInstance htiTemp : list) {
                    if (null != htiTemp.getEndTime()) {
                        return htiTemp;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<HistoricTaskInstance> getThePreviousTasks(String taskId) {
        List<HistoricTaskInstance> returnList = new ArrayList<>();
        try {
            String currentTaskDefKey = "", currentExecutionId = "", processDefineId = "", currentMultiInstance = "";
            String processInstanceId =
                historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult().getProcessInstanceId();
            List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(processInstanceId).orderByHistoricTaskInstanceStartTime().desc().list();

            HistoricTaskInstance currenthti =
                historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
            currentTaskDefKey = currenthti.getTaskDefinitionKey();
            currentExecutionId = currenthti.getExecutionId();
            processDefineId = currenthti.getProcessDefinitionId();
            currentMultiInstance =
                workflowProcessDefinitionService.getMultiinstanceType(processDefineId, currentTaskDefKey);
            /**
             * 查找当前任务的前一个节点的任务
             */
            HistoricTaskInstance historicTaskInstance = null;
            long currentTime = currenthti.getCreateTime().getTime(), tempTime = 0;
            if (currentMultiInstance.equals(SysVariables.PARALLEL)) {
                for (HistoricTaskInstance htiTemp : list) {
                    tempTime = htiTemp.getCreateTime().getTime();
                    if (-1000 > (currentTime - tempTime) || (currentTime - tempTime) > 1000) {
                        historicTaskInstance = htiTemp;
                        break;
                    }
                }
            } else if (currentMultiInstance.equals(SysVariables.SEQUENTIAL)) {
                for (HistoricTaskInstance htiTemp : list) {
                    if (!currentExecutionId.equals(htiTemp.getExecutionId())) {
                        historicTaskInstance = htiTemp;
                        break;
                    }
                }
            } else {
                for (HistoricTaskInstance htiTemp : list) {
                    if (null != htiTemp.getEndTime()) {
                        historicTaskInstance = htiTemp;
                        break;
                    }
                }
            }

            /**
             * 查找前一个任务节点的任务的兄弟任务
             */
            String taskDefKey = historicTaskInstance.getTaskDefinitionKey();
            String executionId = historicTaskInstance.getExecutionId();
            String multiInstance = workflowProcessDefinitionService.getMultiinstanceType(processDefineId, taskDefKey);
            long time = historicTaskInstance.getCreateTime().getTime();
            if (multiInstance.equals(SysVariables.PARALLEL)) {
                for (HistoricTaskInstance htiTemp : list) {
                    long timeTemp = htiTemp.getCreateTime().getTime();
                    if (-1000 <= (time - timeTemp) && (time - timeTemp) <= 1000) {
                        returnList.add(htiTemp);
                    }
                }
            } else if (multiInstance.equals(SysVariables.SEQUENTIAL)) {
                returnList = historyService.createHistoricTaskInstanceQuery().executionId(executionId).list();
            } else {
                returnList.add(historicTaskInstance);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnList;
    }

    @Override
    @Transactional(readOnly = false)
    public void setTenantId(String taskId) {
        String updateSql = "UPDATE ACT_HI_TASKINST T SET T.TENANT_ID_ = #{TENANT_ID_} WHERE T.ID_=#{taskId}";
        historyService.createNativeHistoricTaskInstanceQuery().sql(updateSql).parameter("TENANT_ID_", "1")
            .parameter("taskId", taskId).singleResult();
    }
}
