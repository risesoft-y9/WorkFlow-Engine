package net.risesoft.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.flowable.engine.HistoryService;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.model.processadmin.IdentityLinkModel;
import net.risesoft.service.CustomHistoricTaskService;
import net.risesoft.service.CustomProcessDefinitionService;
import net.risesoft.util.SysVariables;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service(value = "customHistoricTaskService")
public class CustomHistoricTaskServiceImpl implements CustomHistoricTaskService {

    private final HistoryService historyService;

    private final CustomProcessDefinitionService customProcessDefinitionService;

    @Resource(name = "jdbcTemplate4Tenant")
    private JdbcTemplate jdbcTemplate;

    @Override
    public HistoricTaskInstance getById(String taskId) {
        return historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
    }

    @Override
    public HistoricTaskInstance getByIdAndYear(String taskId, String year) {
        if (StringUtils.isBlank(year)) {
            return historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        } else {
            String sql = "SELECT * FROM ACT_HI_TASKINST_" + year + " WHERE ID_ = '" + taskId + "'";
            return historyService.createNativeHistoricTaskInstanceQuery().sql(sql).singleResult();
        }
    }

    @Override
    public long getFinishedCountByExecutionId(String executionId) {
        return historyService.createHistoricTaskInstanceQuery().executionId(executionId).finished().count();
    }

    @Override
    public HistoricTaskInstance getThePreviousTask(String taskId) {
        try {
            String currentTaskDefKey, currentExecutionId, processDefineId, currentMultiInstance;
            HistoricTaskInstance currentHti =
                historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
            String processInstanceId = currentHti.getProcessInstanceId();
            List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(processInstanceId).orderByHistoricTaskInstanceStartTime().desc().list();

            currentTaskDefKey = currentHti.getTaskDefinitionKey();
            currentExecutionId = currentHti.getExecutionId();
            processDefineId = currentHti.getProcessDefinitionId();
            currentMultiInstance =
                customProcessDefinitionService.getNode(processDefineId, currentTaskDefKey).getMultiInstance();
            long currentTime = currentHti.getCreateTime().getTime(), tempTime;
            if (currentMultiInstance.equals(SysVariables.PARALLEL)) {
                for (HistoricTaskInstance htiTemp : list) {
                    tempTime = htiTemp.getCreateTime().getTime();
                    if (!htiTemp.getTaskDefinitionKey().equals(currentHti.getTaskDefinitionKey())) {
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
            LOGGER.error("获取前一个任务失败", e);
        }
        return null;
    }

    @Override
    public List<HistoricTaskInstance> listByProcessInstanceId(String processInstanceId, String year) {
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
    public List<HistoricTaskInstance> listByProcessInstanceIdOrderByEndTimeAsc(String processInstanceId, String year) {
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
    public List<HistoricTaskInstance> listByProcessInstanceIdOrderByEndTimeDesc(String processInstanceId, String year) {
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
    public List<HistoricTaskInstance> listByProcessInstanceIdOrderByStartTimeAsc(String processInstanceId,
        String year) {
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
    public List<HistoricTaskInstance> listThePreviousTasksByTaskId(String taskId) {
        List<HistoricTaskInstance> returnList = new ArrayList<>();
        try {
            String currentTaskDefKey, currentExecutionId, processDefineId, currentMultiInstance;
            String processInstanceId =
                historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult().getProcessInstanceId();
            List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(processInstanceId).orderByHistoricTaskInstanceStartTime().desc().list();

            HistoricTaskInstance current =
                historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
            currentTaskDefKey = current.getTaskDefinitionKey();
            currentExecutionId = current.getExecutionId();
            processDefineId = current.getProcessDefinitionId();
            currentMultiInstance =
                customProcessDefinitionService.getNode(processDefineId, currentTaskDefKey).getMultiInstance();
            /*
             * 查找当前任务的前一个节点的任务
             */
            HistoricTaskInstance historicTaskInstance = null;
            long currentTime = current.getCreateTime().getTime(), tempTime;
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
            /*
             * 查找前一个任务节点的任务的兄弟任务
             */
            assert historicTaskInstance != null;
            String taskDefKey = historicTaskInstance.getTaskDefinitionKey();
            String executionId = historicTaskInstance.getExecutionId();
            String multiInstance =
                customProcessDefinitionService.getNode(processDefineId, taskDefKey).getMultiInstance();
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
            LOGGER.error("获取前一个任务失败", e);
        }
        return returnList;
    }

    @Override
    @Transactional
    public void setTenantId(String taskId) {
        String updateSql = "UPDATE ACT_HI_TASKINST T SET T.TENANT_ID_ = #{TENANT_ID_} WHERE T.ID_=#{taskId}";
        historyService.createNativeHistoricTaskInstanceQuery().sql(updateSql).parameter("TENANT_ID_", "1")
            .parameter("taskId", taskId).singleResult();
    }

    @Override
    public List<IdentityLinkModel> listIdentityLinksForTaskByTaskId(String taskId) {
        String sql =
            "SELECT TASK_ID_ as taskId,TYPE_ as type ,user_id_ as userId FROM ACT_HI_IDENTITYLINK WHERE TASK_ID_ = '"
                + taskId + "'";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(IdentityLinkModel.class));
    }
}
