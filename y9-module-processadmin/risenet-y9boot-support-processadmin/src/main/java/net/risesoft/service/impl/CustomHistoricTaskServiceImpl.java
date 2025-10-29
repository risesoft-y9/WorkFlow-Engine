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

import net.risesoft.consts.processadmin.SysVariables;
import net.risesoft.model.processadmin.IdentityLinkModel;
import net.risesoft.service.CustomHistoricTaskService;
import net.risesoft.service.CustomProcessDefinitionService;

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
            HistoricTaskInstance currentTask = getCurrentTask(taskId);
            if (currentTask == null) {
                return null;
            }
            List<HistoricTaskInstance> taskList = getTaskListByProcessInstanceId(currentTask.getProcessInstanceId());
            String multiInstanceType = getMultiInstanceType(currentTask);
            return findPreviousTask(currentTask, taskList, multiInstanceType);
        } catch (Exception e) {
            LOGGER.error("获取前一个任务失败", e);
            return null;
        }
    }

    /**
     * 获取当前任务实例
     */
    private HistoricTaskInstance getCurrentTask(String taskId) {
        return historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
    }

    /**
     * 根据流程实例ID获取任务列表
     */
    private List<HistoricTaskInstance> getTaskListByProcessInstanceId(String processInstanceId) {
        return historyService.createHistoricTaskInstanceQuery()
            .processInstanceId(processInstanceId)
            .orderByHistoricTaskInstanceStartTime()
            .desc()
            .list();
    }

    /**
     * 获取任务的多实例类型
     */
    private String getMultiInstanceType(HistoricTaskInstance task) {
        String processDefinitionId = task.getProcessDefinitionId();
        String taskDefinitionKey = task.getTaskDefinitionKey();
        return customProcessDefinitionService.getNode(processDefinitionId, taskDefinitionKey).getMultiInstance();
    }

    /**
     * 根据多实例类型查找前一个任务
     */
    private HistoricTaskInstance findPreviousTask(HistoricTaskInstance currentTask, List<HistoricTaskInstance> taskList,
        String multiInstanceType) {
        if (SysVariables.PARALLEL.equals(multiInstanceType)) {
            return findPreviousTaskForParallel(currentTask, taskList);
        } else if (SysVariables.SEQUENTIAL.equals(multiInstanceType)) {
            return findPreviousTaskForSequential(currentTask, taskList);
        } else {
            return findPreviousTaskForCommon(taskList);
        }
    }

    /**
     * 查找并行多实例的前一个任务
     */
    private HistoricTaskInstance findPreviousTaskForParallel(HistoricTaskInstance currentTask,
        List<HistoricTaskInstance> taskList) {
        String currentTaskDefKey = currentTask.getTaskDefinitionKey();
        long currentTime = currentTask.getCreateTime().getTime();

        for (HistoricTaskInstance task : taskList) {
            if (!task.getTaskDefinitionKey().equals(currentTaskDefKey)) {
                return task;
            } else {
                long tempTime = task.getCreateTime().getTime();
                if ((-1000 > (currentTime - tempTime) || (currentTime - tempTime) > 1000)) {
                    return task;
                }
            }
        }
        return null;
    }

    /**
     * 查找串行多实例的前一个任务
     */
    private HistoricTaskInstance findPreviousTaskForSequential(HistoricTaskInstance currentTask,
        List<HistoricTaskInstance> taskList) {
        String currentExecutionId = currentTask.getExecutionId();

        for (HistoricTaskInstance task : taskList) {
            if (!currentExecutionId.equals(task.getExecutionId())) {
                return task;
            }
        }
        return null;
    }

    /**
     * 查找普通任务的前一个任务
     */
    private HistoricTaskInstance findPreviousTaskForCommon(List<HistoricTaskInstance> taskList) {
        for (HistoricTaskInstance task : taskList) {
            if (task.getEndTime() != null) {
                return task;
            }
        }
        return null;
    }

    @Override
    public List<HistoricTaskInstance> listByProcessInstanceId(String processInstanceId, String year) {
        if (StringUtils.isBlank(year)) {
            return historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(processInstanceId)
                .orderByHistoricTaskInstanceStartTime()
                .asc()
                .list();
        } else {
            String sql = "SELECT DISTINCT" + " RES.*" + " FROM" + " ACT_HI_TASKINST_" + year + " RES" + " WHERE"
                + " RES.PROC_INST_ID_ = '" + processInstanceId + "'" + " ORDER BY" + " RES.START_TIME_ ASC";
            return historyService.createNativeHistoricTaskInstanceQuery().sql(sql).list();
        }
    }

    @Override
    public List<HistoricTaskInstance> listByProcessInstanceIdOrderByEndTimeAsc(String processInstanceId, String year) {
        if (StringUtils.isNotEmpty(year)) {
            String sql = "SELECT DISTINCT" + " RES.*" + " FROM" + " ACT_HI_TASKINST_" + year + " RES" + " WHERE"
                + " RES.PROC_INST_ID_ = '" + processInstanceId + "'" + " ORDER BY" + " RES.END_TIME_ ASC";
            return historyService.createNativeHistoricTaskInstanceQuery().sql(sql).list();
        } else {
            return historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(processInstanceId)
                .orderByHistoricTaskInstanceEndTime()
                .asc()
                .list();
        }
    }

    @Override
    public List<HistoricTaskInstance> listByProcessInstanceIdOrderByEndTimeDesc(String processInstanceId, String year) {
        if (StringUtils.isNotEmpty(year)) {
            String sql = "SELECT DISTINCT" + " RES.*" + " FROM" + " ACT_HI_TASKINST_" + year + " RES" + " WHERE"
                + " RES.PROC_INST_ID_ = '" + processInstanceId + "'" + " ORDER BY" + " RES.END_TIME_ DESC";
            return historyService.createNativeHistoricTaskInstanceQuery().sql(sql).list();
        } else {
            return historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(processInstanceId)
                .orderByHistoricTaskInstanceEndTime()
                .desc()
                .list();
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
            return historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(processInstanceId)
                .orderByHistoricTaskInstanceStartTime()
                .asc()
                .list();
        }
    }

    @Override
    public List<HistoricTaskInstance> listThePreviousTasksByTaskId(String taskId) {
        List<HistoricTaskInstance> returnList = new ArrayList<>();
        try {
            HistoricTaskInstance currentTask = getCurrentTask(taskId);
            if (currentTask == null) {
                return returnList;
            }

            String processInstanceId = currentTask.getProcessInstanceId();
            List<HistoricTaskInstance> taskList = getTaskListByProcessInstanceId(processInstanceId);

            // 查找当前任务的前一个节点的任务
            HistoricTaskInstance previousTask = findPreviousTaskNode(currentTask, taskList);
            if (previousTask == null) {
                return returnList;
            }

            // 查找前一个任务节点的任务的兄弟任务
            return findSiblingTasks(previousTask, taskList, currentTask.getProcessDefinitionId());

        } catch (Exception e) {
            LOGGER.error("获取前一个任务失败", e);
            return returnList;
        }
    }

    /**
     * 查找前一个任务节点
     */
    private HistoricTaskInstance findPreviousTaskNode(HistoricTaskInstance currentTask,
        List<HistoricTaskInstance> taskList) {
        String multiInstanceType = getMultiInstanceType(currentTask);
        if (SysVariables.PARALLEL.equals(multiInstanceType)) {
            return findPreviousTaskForParallel(currentTask, taskList);
        } else if (SysVariables.SEQUENTIAL.equals(multiInstanceType)) {
            return findPreviousTaskForSequential(currentTask, taskList);
        } else {
            return findPreviousTaskForCommon(taskList);
        }
    }

    /**
     * 查找兄弟任务
     */
    private List<HistoricTaskInstance> findSiblingTasks(HistoricTaskInstance previousTask,
        List<HistoricTaskInstance> taskList, String processDefinitionId) {
        String multiInstance = getMultiInstanceType(previousTask, processDefinitionId);
        List<HistoricTaskInstance> siblingTasks = new ArrayList<>();
        if (SysVariables.PARALLEL.equals(multiInstance)) {
            long time = previousTask.getCreateTime().getTime();
            for (HistoricTaskInstance task : taskList) {
                long timeTemp = task.getCreateTime().getTime();
                if (-1000 <= (time - timeTemp) && (time - timeTemp) <= 1000) {
                    siblingTasks.add(task);
                }
            }
        } else if (SysVariables.SEQUENTIAL.equals(multiInstance)) {
            siblingTasks =
                historyService.createHistoricTaskInstanceQuery().executionId(previousTask.getExecutionId()).list();
        } else {
            siblingTasks.add(previousTask);
        }

        return siblingTasks;
    }

    /**
     * 获取任务的多实例类型（重载方法）
     */
    private String getMultiInstanceType(HistoricTaskInstance task, String processDefinitionId) {
        String taskDefinitionKey = task.getTaskDefinitionKey();
        return customProcessDefinitionService.getNode(processDefinitionId, taskDefinitionKey).getMultiInstance();
    }

    @Override
    @Transactional
    public void setTenantId(String taskId) {
        String updateSql = "UPDATE ACT_HI_TASKINST T SET T.TENANT_ID_ = #{TENANT_ID_} WHERE T.ID_=#{taskId}";
        historyService.createNativeHistoricTaskInstanceQuery()
            .sql(updateSql)
            .parameter("TENANT_ID_", "1")
            .parameter("taskId", taskId)
            .singleResult();
    }

    @Override
    public List<IdentityLinkModel> listIdentityLinksForTaskByTaskId(String taskId) {
        String sql =
            "SELECT TASK_ID_ as taskId,TYPE_ as type ,user_id_ as userId FROM ACT_HI_IDENTITYLINK WHERE TASK_ID_ = ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(IdentityLinkModel.class), taskId);
    }
}
