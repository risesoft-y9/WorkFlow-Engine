package net.risesoft.service.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.flowable.engine.HistoryService;
import org.flowable.engine.IdentityService;
import org.flowable.engine.ManagementService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.ErrorLogApi;
import net.risesoft.api.itemadmin.OfficeDoneInfoApi;
import net.risesoft.api.itemadmin.core.ActRuDetailApi;
import net.risesoft.api.itemadmin.core.ProcessParamApi;
import net.risesoft.command.RecoveryTodoCommand;
import net.risesoft.consts.processadmin.SysVariables;
import net.risesoft.enums.ItemProcessStateTypeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.ErrorLogModel;
import net.risesoft.model.itemadmin.OfficeDoneInfoModel;
import net.risesoft.model.itemadmin.core.ProcessParamModel;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.service.CustomProcessDefinitionService;
import net.risesoft.service.CustomRuntimeService;
import net.risesoft.service.DeleteProcessService;
import net.risesoft.util.Y9DateTimeUtils;
import net.risesoft.y9.Y9FlowableHolder;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.sqlddl.DbMetaDataUtil;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@Slf4j
@Transactional(readOnly = true)
@Service(value = "customRuntimeService")
public class CustomRuntimeServiceImpl implements CustomRuntimeService {

    private final RuntimeService runtimeService;

    private final HistoryService historyService;

    private final IdentityService identityService;

    private final ManagementService managementService;

    private final CustomProcessDefinitionService customProcessDefinitionService;

    private final OfficeDoneInfoApi officeDoneInfoApi;

    private final ErrorLogApi errorLogApi;

    private final DeleteProcessService deleteProcessService;

    private final ActRuDetailApi actRuDetailApi;

    private final ProcessParamApi processParamApi;

    @jakarta.annotation.Resource(name = "jdbcTemplate4Tenant")
    private JdbcTemplate jdbcTemplate;

    public CustomRuntimeServiceImpl(
        RuntimeService runtimeService,
        HistoryService historyService,
        IdentityService identityService,
        ManagementService managementService,
        CustomProcessDefinitionService customProcessDefinitionService,
        OfficeDoneInfoApi officeDoneInfoApi,
        ErrorLogApi errorLogApi,
        DeleteProcessService deleteProcessService,
        ActRuDetailApi actRuDetailApi,
        ProcessParamApi processParamApi) {
        this.runtimeService = runtimeService;
        this.historyService = historyService;
        this.identityService = identityService;
        this.managementService = managementService;
        this.customProcessDefinitionService = customProcessDefinitionService;
        this.officeDoneInfoApi = officeDoneInfoApi;
        this.errorLogApi = errorLogApi;
        this.deleteProcessService = deleteProcessService;
        this.actRuDetailApi = actRuDetailApi;
        this.processParamApi = processParamApi;
    }

    @Override
    @Transactional
    public Execution addMultiInstanceExecution(String activityId, String parentExecutionId, Map<String, Object> map) {
        return runtimeService.addMultiInstanceExecution(activityId, parentExecutionId, map);
    }

    @Override
    @Transactional
    public void deleteMultiInstanceExecution(String executionId) {
        runtimeService.deleteMultiInstanceExecution(executionId, true);
    }

    private String getActGeBytearraySql(String year, String processInstanceId) {
        return "INSERT INTO ACT_GE_BYTEARRAY (" + " ID_," + " REV_," + " NAME_," + " DEPLOYMENT_ID_," + " BYTES_,"
            + " GENERATED_" + " ) SELECT " + " b.ID_," + " b.REV_," + " b.NAME_," + " b.DEPLOYMENT_ID_," + " b.BYTES_,"
            + " b.GENERATED_" + " FROM" + " ACT_GE_BYTEARRAY_" + year + " b" + " LEFT JOIN ACT_HI_VARINST_" + year
            + " v ON v.BYTEARRAY_ID_ = b.ID_" + " WHERE" + " v.PROC_INST_ID_ = '" + processInstanceId + "'"
            + " AND v.NAME_ = 'users'";
    }

    private String getActHiActinstSql(String year, String processInstanceId) {
        return "INSERT INTO ACT_HI_ACTINST (" + " ID_," + " REV_," + " PROC_DEF_ID_," + " PROC_INST_ID_,"
            + " EXECUTION_ID_," + " ACT_ID_," + " TASK_ID_," + " CALL_PROC_INST_ID_," + " ACT_NAME_," + " ACT_TYPE_,"
            + " ASSIGNEE_," + " START_TIME_," + " END_TIME_," + " DURATION_," + " DELETE_REASON_," + " TENANT_ID_"
            + " ) SELECT" + " ID_," + " REV_," + " PROC_DEF_ID_," + " PROC_INST_ID_," + " EXECUTION_ID_," + " ACT_ID_,"
            + " TASK_ID_," + " CALL_PROC_INST_ID_," + " ACT_NAME_," + " ACT_TYPE_," + " ASSIGNEE_," + " START_TIME_,"
            + " END_TIME_," + " DURATION_," + " DELETE_REASON_," + " TENANT_ID_" + " FROM" + " ACT_HI_ACTINST_" + year
            + " A" + " WHERE" + " A.PROC_INST_ID_ = '" + processInstanceId + "'";
    }

    private String getActHiIdentiyLinkSql(String year, String processInstanceId) {
        return "INSERT INTO ACT_HI_IDENTITYLINK (" + " ID_," + " GROUP_ID_," + " TYPE_," + " USER_ID_," + " TASK_ID_,"
            + " CREATE_TIME_," + " PROC_INST_ID_," + " SCOPE_ID_," + " SCOPE_TYPE_," + " SCOPE_DEFINITION_ID_"
            + " ) SELECT" + " ID_," + " GROUP_ID_," + " TYPE_," + " USER_ID_," + " TASK_ID_," + " CREATE_TIME_,"
            + " PROC_INST_ID_," + " SCOPE_ID_," + " SCOPE_TYPE_," + " SCOPE_DEFINITION_ID_" + " FROM"
            + " ACT_HI_IDENTITYLINK_" + year + " i" + " WHERE" + " i.PROC_INST_ID_ = '" + processInstanceId + "'";
    }

    private String getActHiProcinstSql(String year, String processInstanceId) {
        return "INSERT INTO ACT_HI_PROCINST (" + " ID_," + " REV_," + " PROC_INST_ID_," + " BUSINESS_KEY_,"
            + " PROC_DEF_ID_," + " START_TIME_," + " END_TIME_," + " DURATION_," + " START_USER_ID_,"
            + " START_ACT_ID_," + " END_ACT_ID_," + " SUPER_PROCESS_INSTANCE_ID_," + " DELETE_REASON_," + " TENANT_ID_,"
            + " NAME_," + " CALLBACK_ID_," + " CALLBACK_TYPE_" + ") SELECT" + " ID_," + " REV_," + " PROC_INST_ID_,"
            + " BUSINESS_KEY_," + " PROC_DEF_ID_," + " START_TIME_," + " END_TIME_," + " DURATION_,"
            + " START_USER_ID_," + " START_ACT_ID_," + " END_ACT_ID_," + " SUPER_PROCESS_INSTANCE_ID_,"
            + " DELETE_REASON_," + " TENANT_ID_," + " NAME_," + " CALLBACK_ID_," + " CALLBACK_TYPE_" + " FROM"
            + " ACT_HI_PROCINST_" + year + " RES" + " WHERE" + " RES.PROC_INST_ID_ = '" + processInstanceId + "'";
    }

    private String getActHiTaskinstSql(String year, String processInstanceId) {
        return "INSERT INTO ACT_HI_TASKINST (" + " ID_," + " REV_," + " PROC_DEF_ID_," + " TASK_DEF_ID_,"
            + " TASK_DEF_KEY_," + " PROC_INST_ID_," + " EXECUTION_ID_," + " SCOPE_ID_," + " SUB_SCOPE_ID_,"
            + " SCOPE_TYPE_," + " SCOPE_DEFINITION_ID_," + " PARENT_TASK_ID_," + " NAME_," + " DESCRIPTION_,"
            + " OWNER_," + " ASSIGNEE_," + " START_TIME_," + " CLAIM_TIME_," + " END_TIME_," + " DURATION_,"
            + " DELETE_REASON_," + " PRIORITY_," + " DUE_DATE_," + " FORM_KEY_," + " CATEGORY_," + " TENANT_ID_,"
            + " LAST_UPDATED_TIME_" + " ) SELECT" + " ID_," + " REV_," + " PROC_DEF_ID_," + " TASK_DEF_ID_,"
            + " TASK_DEF_KEY_," + " PROC_INST_ID_," + " EXECUTION_ID_," + " SCOPE_ID_," + " SUB_SCOPE_ID_,"
            + " SCOPE_TYPE_," + " SCOPE_DEFINITION_ID_," + " PARENT_TASK_ID_," + " NAME_," + " DESCRIPTION_,"
            + " OWNER_," + " ASSIGNEE_," + " START_TIME_," + " CLAIM_TIME_," + " END_TIME_," + " DURATION_,"
            + " DELETE_REASON_," + " PRIORITY_," + " DUE_DATE_," + " FORM_KEY_," + " CATEGORY_," + " TENANT_ID_,"
            + " LAST_UPDATED_TIME_" + " FROM" + " ACT_HI_TASKINST_" + year + " T" + " WHERE" + " T .PROC_INST_ID_ = '"
            + processInstanceId + "'";
    }

    private String getActHiVarinstSql(String year, String processInstanceId) {
        return "INSERT INTO ACT_HI_VARINST (" + " ID_," + " REV_," + " PROC_INST_ID_," + " EXECUTION_ID_,"
            + " TASK_ID_," + " NAME_," + " VAR_TYPE_," + " SCOPE_ID_," + " SUB_SCOPE_ID_," + " SCOPE_TYPE_,"
            + " BYTEARRAY_ID_," + " DOUBLE_," + " LONG_," + " TEXT_," + " TEXT2_," + " CREATE_TIME_,"
            + " LAST_UPDATED_TIME_" + " ) SELECT" + " ID_," + " REV_," + " PROC_INST_ID_," + " EXECUTION_ID_,"
            + " TASK_ID_," + " NAME_," + " VAR_TYPE_," + " SCOPE_ID_," + " SUB_SCOPE_ID_," + " SCOPE_TYPE_,"
            + " BYTEARRAY_ID_," + " DOUBLE_," + " LONG_," + " TEXT_," + " TEXT2_," + " CREATE_TIME_,"
            + " LAST_UPDATED_TIME_" + " FROM" + " ACT_HI_VARINST_" + year + " v" + " WHERE" + " v.PROC_INST_ID_ = '"
            + processInstanceId + "'";
    }

    @Override
    public List<String> getActiveActivityIds(String executionId) {
        return runtimeService.getActiveActivityIds(executionId);
    }

    @Override
    public Execution getExecutionById(String executionId) {
        return runtimeService.createExecutionQuery().executionId(executionId).singleResult();
    }

    @Override
    public ProcessInstance getProcessInstance(String processInstanceId) {
        return runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
    }

    @Override
    public List<ProcessInstance> listBySuperProcessInstanceId(String superProcessInstanceId) {
        return runtimeService.createProcessInstanceQuery().superProcessInstanceId(superProcessInstanceId).list();
    }

    @Override
    public List<ProcessInstance> listProcessInstancesByKey(String processDefinitionKey) {
        return runtimeService.createProcessInstanceQuery().processDefinitionKey(processDefinitionKey).list();
    }

    @Override
    @Transactional
    public void recovery4SetUpCompleted(String processInstanceId) {
        runtimeService.activateProcessInstanceById(processInstanceId);
        String updateSql =
            "UPDATE ACT_HI_PROCINST T SET T.END_TIME_ = #{END_TIME_,jdbcType=DATE} WHERE T.PROC_INST_ID_=#{processInstanceId}";
        historyService.createNativeHistoricProcessInstanceQuery()
            .sql(updateSql)
            .parameter("END_TIME_", null)
            .parameter(SysVariables.PROCESSINSTANCEID, processInstanceId)
            .singleResult();
    }

    /**
     * 1、恢复执行实例
     */
    @SuppressWarnings("java:S2077")
    private void restoreProcessInstance(String processInstanceId, String year) throws SQLException {
        String dialectName;
        assert jdbcTemplate.getDataSource() != null;
        try (Connection connection = jdbcTemplate.getDataSource().getConnection();
            Statement stmt = connection.createStatement()) {
            DataSource dataSource = jdbcTemplate.getDataSource();
            dialectName = DbMetaDataUtil.getDatabaseDialectName(dataSource);
            if (!"kingbase".equals(dialectName)) {
                stmt.addBatch("SET FOREIGN_KEY_CHECKS=0");
            }
            String sql =
                "INSERT INTO ACT_RU_EXECUTION (ID_,REV_,PROC_INST_ID_,BUSINESS_KEY_,PARENT_ID_,PROC_DEF_ID_,SUPER_EXEC_,ROOT_PROC_INST_ID_,ACT_ID_,IS_ACTIVE_,"
                    + "IS_CONCURRENT_,IS_SCOPE_,IS_EVENT_SCOPE_,IS_MI_ROOT_,SUSPENSION_STATE_,CACHED_ENT_STATE_,TENANT_ID_,NAME_,START_ACT_ID_,START_TIME_,"
                    + "START_USER_ID_,LOCK_TIME_,IS_COUNT_ENABLED_,EVT_SUBSCR_COUNT_,TASK_COUNT_,JOB_COUNT_,TIMER_JOB_COUNT_,SUSP_JOB_COUNT_,DEADLETTER_JOB_COUNT_,"
                    + "VAR_COUNT_,ID_LINK_COUNT_,CALLBACK_ID_,CALLBACK_TYPE_) SELECT ID_,REV_,PROC_INST_ID_,BUSINESS_KEY_,PARENT_ID_,PROC_DEF_ID_,SUPER_EXEC_,"
                    + "ROOT_PROC_INST_ID_,ACT_ID_,IS_ACTIVE_,IS_CONCURRENT_,IS_SCOPE_,IS_EVENT_SCOPE_,IS_MI_ROOT_,SUSPENSION_STATE_,CACHED_ENT_STATE_,"
                    + "TENANT_ID_,NAME_,START_ACT_ID_,START_TIME_,START_USER_ID_,LOCK_TIME_,IS_COUNT_ENABLED_,EVT_SUBSCR_COUNT_,TASK_COUNT_,JOB_COUNT_,"
                    + "TIMER_JOB_COUNT_,SUSP_JOB_COUNT_,DEADLETTER_JOB_COUNT_,VAR_COUNT_,ID_LINK_COUNT_,CALLBACK_ID_,CALLBACK_TYPE_ FROM FF_ACT_RU_EXECUTION_"
                    + year + " T WHERE T.PROC_INST_ID_ = :processInstanceId";
            NamedParameterJdbcTemplate namedJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put(SysVariables.PROCESSINSTANCEID, processInstanceId);
            namedJdbcTemplate.update(sql, paramMap);
            LOGGER.info("**************复制数据到ACT_RU_EXECUTION成功****************");
        }
    }

    /**
     * 2恢复流程变量数据
     * <p>
     * 先查询出来，再删除。因为调接口保存的时候，会再向历史表里面插入一份
     * <p>
     * 3执行恢复命令代码
     * 
     * @param processInstanceId 流程实例ID
     * @param year 年份
     */
    private void restoreProcessVariablesAndExecuteCommand(String processInstanceId, String year) {
        // 1. 获取历史任务实例
        HistoricTaskInstance hti = getLatestHistoricTaskInstance(processInstanceId, year);
        // 2. 获取执行实例集合
        Set<String> executionSet = getExecutionIds(processInstanceId);
        // 3. 获取节点类型
        String nodeType = getNodeMultiInstanceType(hti);
        // 4. 复制年度历史数据到正在运行历史表
        saveYearData(year, processInstanceId);
        // 5. 获取流程变量和任务变量
        VariableMaps variableMaps = getProcessAndTaskVariables(executionSet, hti.getId(), nodeType);
        // 6. 清理历史变量数据
        cleanupHistoricVariables(processInstanceId, hti.getId(), nodeType);
        // 7. 执行数据恢复命令
        managementService.executeCommand(new RecoveryTodoCommand(hti, variableMaps.pVarMap, variableMaps.tVarMap));
    }

    /**
     * 获取最新的历史任务实例
     */
    private HistoricTaskInstance getLatestHistoricTaskInstance(String processInstanceId, String year) {
        String sql = "SELECT DISTINCT RES.* FROM ACT_HI_TASKINST_" + year + " RES WHERE RES.PROC_INST_ID_ = '"
            + processInstanceId + "' ORDER BY RES.END_TIME_ DESC";
        List<HistoricTaskInstance> taskInstances =
            historyService.createNativeHistoricTaskInstanceQuery().sql(sql).list();

        if (taskInstances == null || taskInstances.isEmpty()) {
            throw new IllegalStateException("未找到流程实例的历史任务信息: " + processInstanceId);
        }

        return taskInstances.get(0);
    }

    /**
     * 获取执行实例ID集合
     */
    private Set<String> getExecutionIds(String processInstanceId) {
        List<Execution> executionList =
            runtimeService.createExecutionQuery().processInstanceId(processInstanceId).list();
        Set<String> executionSet = new HashSet<>();
        for (Execution execution : executionList) {
            executionSet.add(execution.getId());
        }
        return executionSet;
    }

    /**
     * 获取节点的多实例类型
     */
    private String getNodeMultiInstanceType(HistoricTaskInstance hti) {
        return customProcessDefinitionService.getNode(hti.getProcessDefinitionId(), hti.getTaskDefinitionKey())
            .getMultiInstance();
    }

    /**
     * 获取流程变量和任务变量
     */
    private VariableMaps getProcessAndTaskVariables(Set<String> executionSet, String taskId, String nodeType) {
        List<HistoricVariableInstance> pVarList = historyService.createHistoricVariableInstanceQuery()
            .executionIds(executionSet)
            .excludeTaskVariables()
            .list();
        List<HistoricVariableInstance> tVarList =
            historyService.createHistoricVariableInstanceQuery().taskId(taskId).list();

        Map<String, Object> pVarMap = new HashMap<>(Math.max(16, pVarList.size() + 8));
        Map<String, Object> tVarMap = new HashMap<>(Math.max(16, tVarList.size() + 8));
        OrgUnit orgUnit = Y9FlowableHolder.getOrgUnit();
        String orgUnitId = orgUnit.getId();
        // 处理流程变量
        processProcessVariables(pVarList, pVarMap, nodeType, orgUnitId);
        // 处理任务变量
        processTaskVariables(tVarList, tVarMap, nodeType, orgUnitId);
        return new VariableMaps(pVarMap, tVarMap);
    }

    /**
     * 处理流程变量
     */
    private void processProcessVariables(List<HistoricVariableInstance> pVarList, Map<String, Object> pVarMap,
        String nodeType, String orgUnitId) {
        if (nodeType.equals(SysVariables.SEQUENTIAL)) {
            handleSequentialProcessVariables(pVarList, pVarMap, orgUnitId);
        } else if (nodeType.equals(SysVariables.PARALLEL)) {
            handleParallelProcessVariables(pVarList, pVarMap, orgUnitId);
        } else {
            handleNormalProcessVariables(pVarList, pVarMap);
        }
    }

    /**
     * 处理串行多实例的流程变量
     */
    private void handleSequentialProcessVariables(List<HistoricVariableInstance> pVarList, Map<String, Object> pVarMap,
        String orgUnitId) {
        for (HistoricVariableInstance pVar : pVarList) {
            String key = pVar.getVariableName();
            Object val = pVar.getValue();
            if (key.equals(SysVariables.ELEMENT_USER) || key.equals(SysVariables.LOOP_COUNTER)
                || key.equals(SysVariables.ROUTE_TO_TASK_ID) || key.equals(SysVariables.USER)
                || key.equals(SysVariables.USERS)) {
                continue;
            }
            pVarMap.put(key, val);
        }
        Map<String, Object> multiInstanceVars = initializeMultiInstanceVariables(orgUnitId);
        pVarMap.putAll(multiInstanceVars);
    }

    /**
     * 初始化多实例用户变量
     *
     * @param orgUnitId 组织单元ID
     * @return 包含初始化变量的Map
     */
    private Map<String, Object> initializeMultiInstanceVariables(String orgUnitId) {
        Map<String, Object> variables = new HashMap<>();
        List<String> usersList = new ArrayList<>();
        usersList.add(orgUnitId);
        variables.put(SysVariables.USERS, usersList);
        variables.put(SysVariables.NR_OF_INSTANCES, 1);
        variables.put(SysVariables.NR_OF_COMPLETED_INSTANCES, 0);
        variables.put(SysVariables.NR_OF_ACTIVE_INSTANCES, 1);
        variables.put(SysVariables.LOOP_COUNTER, 0);
        return variables;
    }

    /**
     * 处理并行多实例的流程变量
     */
    private void handleParallelProcessVariables(List<HistoricVariableInstance> pVarList, Map<String, Object> pVarMap,
        String orgUnitId) {
        for (HistoricVariableInstance pVar : pVarList) {
            String key = pVar.getVariableName();
            Object val = pVar.getValue();
            if (key.equals(SysVariables.ELEMENT_USER) || key.equals(SysVariables.LOOP_COUNTER)
                || key.equals(SysVariables.ROUTE_TO_TASK_ID) || key.equals(SysVariables.USER)) {
                continue;
            }
            pVarMap.put(key, val);
        }
        Map<String, Object> multiInstanceVars = initializeMultiInstanceVariables(orgUnitId);
        pVarMap.putAll(multiInstanceVars);
    }

    /**
     * 处理普通节点的流程变量
     */
    private void handleNormalProcessVariables(List<HistoricVariableInstance> pVarList, Map<String, Object> pVarMap) {
        for (HistoricVariableInstance pVar : pVarList) {
            String key = pVar.getVariableName();
            Object val = pVar.getValue();
            if (key.equals(SysVariables.ELEMENT_USER) || key.equals(SysVariables.LOOP_COUNTER)
                || key.equals(SysVariables.ROUTE_TO_TASK_ID)) {
                continue;
            }
            pVarMap.put(key, val);
        }
    }

    /**
     * 处理任务变量
     */
    private void processTaskVariables(List<HistoricVariableInstance> tVarList, Map<String, Object> tVarMap,
        String nodeType, String orgUnitId) {
        for (HistoricVariableInstance tVar : tVarList) {
            tVarMap.put(tVar.getVariableName(), tVar.getValue());
            // 串行恢复待办,任务变量users需要重新设置,避免打开办件时nrOfInstances与usersSize不一致
            if (nodeType.equals(SysVariables.SEQUENTIAL) && tVar.getVariableName().equals(SysVariables.USERS)) {
                List<String> usersList = new ArrayList<>();
                usersList.add(orgUnitId);
                tVarMap.put(SysVariables.USERS, usersList);
            }
        }
    }

    /**
     * 清理历史变量数据
     */
    private void cleanupHistoricVariables(String processInstanceId, String taskId, String nodeType) {
        if (nodeType.equals(SysVariables.PARALLEL) || nodeType.equals(SysVariables.SEQUENTIAL)) {
            cleanupMultiInstanceHistoricVariables(processInstanceId, taskId);
        } else {
            cleanupNormalHistoricVariables(processInstanceId, taskId);
        }
    }

    /**
     * 清理多实例的历史变量数据
     */
    private void cleanupMultiInstanceHistoricVariables(String processInstanceId, String taskId) {
        String sql00 = "SELECT * FROM ACT_RU_EXECUTION WHERE PROC_INST_ID_ = #{PROC_INST_ID_} AND IS_MI_ROOT_=1";
        Execution miRootExecution = runtimeService.createNativeExecutionQuery()
            .sql(sql00)
            .parameter(SysVariables.PROC_INST_ID_KEY, processInstanceId)
            .singleResult();

        String sql01 =
            "DELETE FROM ACT_HI_VARINST WHERE EXECUTION_ID_=#{PROC_INST_ID_} OR EXECUTION_ID_=#{MIROOTEXECUTION_ID_} OR TASK_ID_=#{TASK_ID_}";
        historyService.createNativeHistoricVariableInstanceQuery()
            .sql(sql01)
            .parameter(SysVariables.PROC_INST_ID_KEY, processInstanceId)
            .parameter("MIROOTEXECUTION_ID_", miRootExecution.getId())
            .parameter("TASK_ID_", taskId)
            .list();

        runtimeService.setVariablesLocal(miRootExecution.getId(), new HashMap<>(16));
    }

    /**
     * 清理普通实例的历史变量数据
     */
    private void cleanupNormalHistoricVariables(String processInstanceId, String taskId) {
        String sql01 = "DELETE FROM ACT_HI_VARINST WHERE EXECUTION_ID_=#{PROC_INST_ID_} OR TASK_ID_=#{TASK_ID_}";
        historyService.createNativeHistoricVariableInstanceQuery()
            .sql(sql01)
            .parameter(SysVariables.PROC_INST_ID_KEY, processInstanceId)
            .parameter("TASK_ID_", taskId)
            .list();
    }

    /**
     * 删除历史节点中办结任务到结束节点的数据
     *
     * @param processInstanceId 流程实例ID
     */
    private void removeHistoricActivitiesAfterCompletedTask(String processInstanceId) {
        List<HistoricActivityInstance> hisActivityList = historyService.createHistoricActivityInstanceQuery()
            .processInstanceId(processInstanceId)
            .orderByHistoricActivityInstanceEndTime()
            .desc()
            .list();

        for (HistoricActivityInstance hisActivity : hisActivityList) {
            if (hisActivity.getActivityType().equals(SysVariables.USER_TASK)) {
                String sql2 =
                    "UPDATE ACT_HI_ACTINST SET END_TIME_=NULL,DURATION_=NULL,DELETE_REASON_=NULL WHERE ID_ = :id";
                historyService.createNativeHistoricActivityInstanceQuery()
                    .sql(sql2)
                    .parameter("id", hisActivity.getId())
                    .list();
                break;
            } else {
                String sql2 = "DELETE FROM ACT_HI_ACTINST WHERE ID_ = :id";
                historyService.createNativeHistoricActivityInstanceQuery()
                    .sql(sql2)
                    .parameter("id", hisActivity.getId())
                    .list();
            }
        }
    }

    @Override
    @Transactional
    public void recoveryCompleted(String processInstanceId, String year) throws Exception {
        try {
            // 1:恢复执行实例数据
            restoreProcessInstance(processInstanceId, year);
            // 2和3:恢复流程变量数据并执行恢复命令
            restoreProcessVariablesAndExecuteCommand(processInstanceId, year);
            // 4:删除历史节点中办结任务到结束节点的数据
            removeHistoricActivitiesAfterCompletedTask(processInstanceId);
            // 5:恢复办件信息数据
            restoreOfficeDoneInfo(processInstanceId);
            // 6:恢复办件详情
            restoreActRuDetail(processInstanceId);
            // 7:删除年度数据
            deleteProcessService.deleteYearData(Y9LoginUserHolder.getTenantId(), year, processInstanceId);
        } catch (Exception e) {
            saveErrorLog(processInstanceId, "恢复待办失败", e);
            LOGGER.error("恢复待办失败", e);
            throw new Exception("CustomRuntimeServiceImpl recovery4Completed error");
        }
    }

    /**
     * 恢复办件信息数据
     *
     * @param processInstanceId 流程实例ID
     */
    private void restoreOfficeDoneInfo(String processInstanceId) {
        try {
            // 修改ES办件信息数据
            OfficeDoneInfoModel officeDoneInfo =
                officeDoneInfoApi.findByProcessInstanceId(Y9LoginUserHolder.getTenantId(), processInstanceId).getData();
            officeDoneInfo.setUserComplete("");
            officeDoneInfo.setEndTime(null);
            officeDoneInfoApi.saveOfficeDone(Y9LoginUserHolder.getTenantId(), officeDoneInfo);
            ProcessParamModel processParamModel =
                processParamApi.findByProcessInstanceId(Y9LoginUserHolder.getTenantId(), processInstanceId).getData();
            processParamModel.setCompleter("");
            processParamApi.saveOrUpdate(Y9LoginUserHolder.getTenantId(), processParamModel);
        } catch (Exception e) {
            saveErrorLog(processInstanceId, "恢复办件信息数据失败", e);
        }
    }

    /**
     * 恢复办件详情
     *
     * @param processInstanceId 流程实例ID
     */
    private void restoreActRuDetail(String processInstanceId) {
        try {
            // 恢复整个流程的办件详情,恢复为未办结
            actRuDetailApi.recoveryByProcessInstanceId(Y9LoginUserHolder.getTenantId(), processInstanceId);
        } catch (Exception e) {
            saveErrorLog(processInstanceId, "恢复办件详情失败", e);
        }
    }

    /**
     * 保存错误日志的公共方法
     *
     * @param processInstanceId 流程实例ID
     * @param extendField 扩展字段信息
     * @param exception 错误信息
     */
    private void saveErrorLog(String processInstanceId, String extendField, Exception exception) {
        try {
            // 处理异常堆栈信息
            final Writer result = new StringWriter();
            final PrintWriter print = new PrintWriter(result);
            exception.printStackTrace(print);
            String errorMsg = result.toString();
            String time = Y9DateTimeUtils.formatCurrentDateTime();
            ErrorLogModel errorLogModel = new ErrorLogModel();
            errorLogModel.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            errorLogModel.setErrorFlag(ErrorLogModel.ERROR_FLAG_RECOVERY_COMLETED);
            errorLogModel.setErrorType(ErrorLogModel.ERROR_PROCESS_INSTANCE);
            errorLogModel.setExtendField(extendField);
            errorLogModel.setProcessInstanceId(processInstanceId);
            errorLogModel.setTaskId("");
            errorLogModel.setText(errorMsg);
            errorLogApi.saveErrorLog(Y9LoginUserHolder.getTenantId(), errorLogModel);
        } catch (Exception e) {
            LOGGER.error("保存错误日志失败", e);
        }
    }

    /**
     * 恢复年度历史数据
     *
     * @param year 年度
     * @param processInstanceId 流程实例ID
     */
    public void saveYearData(String year, String processInstanceId) {
        // 同步历史任务
        executeYearDataSql(getActHiTaskinstSql(year, processInstanceId));
        // 同步历史变量
        executeYearDataSql(getActHiVarinstSql(year, processInstanceId));
        // 同步二进制数据表
        executeYearDataSql(getActGeBytearraySql(year, processInstanceId));
        // 同步历史参与人
        executeYearDataSql(getActHiIdentiyLinkSql(year, processInstanceId));
        // 同步历史节点
        executeYearDataSql(getActHiActinstSql(year, processInstanceId));
        // 同步流程实例
        executeYearDataSql(getActHiProcinstSql(year, processInstanceId));
    }

    /**
     * 执行年度数据SQL
     *
     * @param sql SQL语句
     */
    private void executeYearDataSql(String sql) {
        jdbcTemplate.execute(sql);
    }

    @Override
    @Transactional
    public void setUpCompleted(String processInstanceId) {
        runtimeService.suspendProcessInstanceById(processInstanceId);

        String updateSql =
            "UPDATE ACT_HI_PROCINST T SET T.END_TIME_ = #{END_TIME_} WHERE T.PROC_INST_ID_=#{processInstanceId}";
        historyService.createNativeHistoricProcessInstanceQuery()
            .sql(updateSql)
            .parameter("END_TIME_", new Date())
            .parameter(SysVariables.PROCESSINSTANCEID, processInstanceId)
            .singleResult();
    }

    @Override
    public void setVariable(String processInstanceId, String key, Object val) {
        runtimeService.setVariable(processInstanceId, key, val);
    }

    @Override
    public void setVariables(String executionId, Map<String, Object> map) {
        runtimeService.setVariables(executionId, map);
    }

    @Override
    @Transactional
    public ProcessInstance startProcessInstanceByKey(String processDefinitionKey, String systemName,
        Map<String, Object> map) {
        try {
            identityService.setAuthenticatedUserId(Y9FlowableHolder.getOrgUnitId());
            return runtimeService.startProcessInstanceByKey(processDefinitionKey, systemName, map);
        } finally {
            identityService.setAuthenticatedUserId(null);
        }
    }

    @Override
    @Transactional
    public void switchSuspendOrActive(String processInstanceId, String state) {
        if (ItemProcessStateTypeEnum.ACTIVE.getValue().equals(state)) {
            runtimeService.activateProcessInstanceById(processInstanceId);
        } else if (ItemProcessStateTypeEnum.SUSPEND.getValue().equals(state)) {
            runtimeService.suspendProcessInstanceById(processInstanceId);
        }
    }

    /**
     * 变量映射封装类
     */
    private static class VariableMaps {
        Map<String, Object> pVarMap;
        Map<String, Object> tVarMap;

        VariableMaps(Map<String, Object> pVarMap, Map<String, Object> tVarMap) {
            this.pVarMap = pVarMap;
            this.tVarMap = tVarMap;
        }
    }
}
