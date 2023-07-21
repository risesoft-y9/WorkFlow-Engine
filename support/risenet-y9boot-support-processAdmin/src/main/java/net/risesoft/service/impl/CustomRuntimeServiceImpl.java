package net.risesoft.service.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.flowable.engine.HistoryService;
import org.flowable.engine.IdentityService;
import org.flowable.engine.ManagementService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.ActRuDetailApi;
import net.risesoft.api.itemadmin.ErrorLogApi;
import net.risesoft.api.itemadmin.OfficeDoneInfoApi;
import net.risesoft.api.itemadmin.position.OfficeDoneInfo4PositionApi;
import net.risesoft.command.RecoveryTodoCommand;
import net.risesoft.command.RecoveryTodoCommand4Position;
import net.risesoft.enums.ItemProcessStateTypeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.Position;
import net.risesoft.model.itemadmin.ErrorLogModel;
import net.risesoft.model.itemadmin.OfficeDoneInfoModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.service.CustomProcessDefinitionService;
import net.risesoft.service.CustomRuntimeService;
import net.risesoft.service.DeleteProcessUtilService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@Transactional(readOnly = true)
@Service(value = "customRuntimeService")
@Slf4j
public class CustomRuntimeServiceImpl implements CustomRuntimeService {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private IdentityService identityService;

    @Autowired
    private ManagementService managementService;

    @Autowired
    private CustomProcessDefinitionService customProcessDefinitionService;

    @javax.annotation.Resource(name = "jdbcTemplate4Tenant")
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private OfficeDoneInfoApi officeDoneInfoApi;

    @Autowired
    private OfficeDoneInfo4PositionApi officeDoneInfo4PositionApi;

    @Autowired
    private ErrorLogApi errorLogManager;

    @Autowired
    private DeleteProcessUtilService deleteProcessUtilService;

    @Autowired
    private ActRuDetailApi actRuDetailApi;

    @Override
    @Transactional(readOnly = false)
    public Execution addMultiInstanceExecution(String activityId, String parentExecutionId, Map<String, Object> map) {
        return runtimeService.addMultiInstanceExecution(activityId, parentExecutionId, map);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteMultiInstanceExecution(String executionId) {
        runtimeService.deleteMultiInstanceExecution(executionId, true);
    }

    private String getActGeBytearraySql(String year, String processInstanceId) {
        String sql = "INSERT INTO ACT_GE_BYTEARRAY (" + "	ID_," + "	REV_," + "	NAME_," + "	DEPLOYMENT_ID_," + "	BYTES_," + "	GENERATED_" + " ) SELECT " + "	b.ID_," + "	b.REV_," + "	b.NAME_," + "	b.DEPLOYMENT_ID_," + "	b.BYTES_," + "	b.GENERATED_" + " FROM" + "	ACT_GE_BYTEARRAY_"
            + year + " b" + " LEFT JOIN ACT_HI_VARINST_" + year + " v ON v.BYTEARRAY_ID_ = b.ID_" + " WHERE" + "	v.PROC_INST_ID_ = '" + processInstanceId + "'" + " AND v.NAME_ = 'users'";
        return sql;
    }

    private String getActHiActinstSql(String year, String processInstanceId) {
        String sql3 = "INSERT INTO ACT_HI_ACTINST (" + "	ID_," + "	REV_," + "	PROC_DEF_ID_," + "	PROC_INST_ID_," + "	EXECUTION_ID_," + "	ACT_ID_," + "	TASK_ID_," + "	CALL_PROC_INST_ID_," + "	ACT_NAME_," + "	ACT_TYPE_," + "	ASSIGNEE_," + "	START_TIME_," + "	END_TIME_," + "	DURATION_,"
            + "	DELETE_REASON_," + "	TENANT_ID_" + " ) SELECT" + "	ID_," + "	REV_," + "	PROC_DEF_ID_," + "	PROC_INST_ID_," + "	EXECUTION_ID_," + "	ACT_ID_," + "	TASK_ID_," + "	CALL_PROC_INST_ID_," + "	ACT_NAME_," + "	ACT_TYPE_," + "	ASSIGNEE_," + "	START_TIME_," + "	END_TIME_,"
            + "	DURATION_," + "	DELETE_REASON_," + "	TENANT_ID_" + " FROM" + "	ACT_HI_ACTINST_" + year + " A" + " WHERE" + "	A.PROC_INST_ID_ = '" + processInstanceId + "'";
        return sql3;
    }

    private String getActHiIdentiyLinkSql(String year, String processInstanceId) {
        String sql3 = "INSERT INTO ACT_HI_IDENTITYLINK (" + "	ID_," + "	GROUP_ID_," + "	TYPE_," + "	USER_ID_," + "	TASK_ID_," + "	CREATE_TIME_," + "	PROC_INST_ID_," + "	SCOPE_ID_," + "	SCOPE_TYPE_," + "	SCOPE_DEFINITION_ID_" + " ) SELECT" + "	ID_," + "	GROUP_ID_," + "	TYPE_,"
            + "	USER_ID_," + "	TASK_ID_," + "	CREATE_TIME_," + "	PROC_INST_ID_," + "	SCOPE_ID_," + "	SCOPE_TYPE_," + "	SCOPE_DEFINITION_ID_" + " FROM" + "	ACT_HI_IDENTITYLINK_" + year + " i" + " WHERE" + "	i.PROC_INST_ID_ = '" + processInstanceId + "'";
        return sql3;
    }

    private String getActHiProcinstSql(String year, String processInstanceId) {
        String sql =
            "INSERT INTO ACT_HI_PROCINST (" + "	ID_," + "	REV_," + "	PROC_INST_ID_," + "	BUSINESS_KEY_," + "	PROC_DEF_ID_," + "	START_TIME_," + "	END_TIME_," + "	DURATION_," + "	START_USER_ID_," + "	START_ACT_ID_," + "	END_ACT_ID_," + "	SUPER_PROCESS_INSTANCE_ID_," + "	DELETE_REASON_,"
                + "	TENANT_ID_," + "	NAME_," + "	CALLBACK_ID_," + "	CALLBACK_TYPE_" + ") SELECT" + "	ID_," + "	REV_," + "	PROC_INST_ID_," + "	BUSINESS_KEY_," + "	PROC_DEF_ID_," + "	START_TIME_," + "	END_TIME_," + "	DURATION_," + "	START_USER_ID_," + "	START_ACT_ID_,"
                + "	END_ACT_ID_," + "	SUPER_PROCESS_INSTANCE_ID_," + "	DELETE_REASON_," + "	TENANT_ID_," + "	NAME_," + "	CALLBACK_ID_," + "	CALLBACK_TYPE_" + " FROM" + "	ACT_HI_PROCINST_" + year + " RES" + " WHERE" + "	RES.PROC_INST_ID_ = '" + processInstanceId + "'";
        return sql;
    }

    private String getActHiTaskinstSql(String year, String processInstanceId) {
        String sql = "INSERT INTO ACT_HI_TASKINST (" + "	ID_," + "	REV_," + "	PROC_DEF_ID_," + "	TASK_DEF_ID_," + "	TASK_DEF_KEY_," + "	PROC_INST_ID_," + "	EXECUTION_ID_," + "	SCOPE_ID_," + "	SUB_SCOPE_ID_," + "	SCOPE_TYPE_," + "	SCOPE_DEFINITION_ID_," + "	PARENT_TASK_ID_," + "	NAME_,"
            + "	DESCRIPTION_," + "	OWNER_," + "	ASSIGNEE_," + "	START_TIME_," + "	CLAIM_TIME_," + "	END_TIME_," + "	DURATION_," + "	DELETE_REASON_," + "	PRIORITY_," + "	DUE_DATE_," + "	FORM_KEY_," + "	CATEGORY_," + "	TENANT_ID_," + "	LAST_UPDATED_TIME_" + " ) SELECT" + "	ID_,"
            + "	REV_," + "	PROC_DEF_ID_," + "	TASK_DEF_ID_," + "	TASK_DEF_KEY_," + "	PROC_INST_ID_," + "	EXECUTION_ID_," + "	SCOPE_ID_," + "	SUB_SCOPE_ID_," + "	SCOPE_TYPE_," + "	SCOPE_DEFINITION_ID_," + "	PARENT_TASK_ID_," + "	NAME_," + "	DESCRIPTION_," + "	OWNER_," + "	ASSIGNEE_,"
            + "	START_TIME_," + "	CLAIM_TIME_," + "	END_TIME_," + "	DURATION_," + "	DELETE_REASON_," + "	PRIORITY_," + "	DUE_DATE_," + "	FORM_KEY_," + "	CATEGORY_," + "	TENANT_ID_," + "	LAST_UPDATED_TIME_" + " FROM" + "	ACT_HI_TASKINST_" + year + " T" + " WHERE"
            + "	T .PROC_INST_ID_ = '" + processInstanceId + "'";
        return sql;
    }

    private String getActHiVarinstSql(String year, String processInstanceId) {
        String sql3 = "INSERT INTO ACT_HI_VARINST (" + "	ID_," + "	REV_," + "	PROC_INST_ID_," + "	EXECUTION_ID_," + "	TASK_ID_," + "	NAME_," + "	VAR_TYPE_," + "	SCOPE_ID_," + "	SUB_SCOPE_ID_," + "	SCOPE_TYPE_," + "	BYTEARRAY_ID_," + "	DOUBLE_," + "	LONG_," + "	TEXT_," + "	TEXT2_,"
            + "	CREATE_TIME_," + "	LAST_UPDATED_TIME_" + " ) SELECT" + "	ID_," + "	REV_," + "	PROC_INST_ID_," + "	EXECUTION_ID_," + "	TASK_ID_," + "	NAME_," + "	VAR_TYPE_," + "	SCOPE_ID_," + "	SUB_SCOPE_ID_," + "	SCOPE_TYPE_," + "	BYTEARRAY_ID_," + "	DOUBLE_," + "	LONG_," + "	TEXT_,"
            + "	TEXT2_," + "	CREATE_TIME_," + "	LAST_UPDATED_TIME_" + " FROM" + "	ACT_HI_VARINST_" + year + " v" + " WHERE" + "	v.PROC_INST_ID_ = '" + processInstanceId + "'";
        return sql3;
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
    public List<ProcessInstance> getListBySuperProcessInstanceId(String superProcessInstanceId) {
        return runtimeService.createProcessInstanceQuery().superProcessInstanceId(superProcessInstanceId).list();
    }

    @Override
    public ProcessInstance getProcessInstance(String processInstanceId) {
        return runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
    }

    @Override
    public List<ProcessInstance> getProcessInstancesByKey(String processDefinitionKey) {
        return runtimeService.createProcessInstanceQuery().processDefinitionKey(processDefinitionKey).list();
    }

    @Override
    @Transactional(readOnly = false)
    public void recovery4Completed(String processInstanceId, String year) throws Exception {
        try {
            /**
             * 1-恢复正在运行的流程实例
             */
            java.sql.Statement stmt = null;
            Connection connection = null;
            try {
                connection = jdbcTemplate.getDataSource().getConnection();
                stmt = connection.createStatement();
                stmt.addBatch("SET FOREIGN_KEY_CHECKS=0");
                stmt.addBatch(
                    "INSERT INTO ACT_RU_EXECUTION (ID_,REV_,PROC_INST_ID_,BUSINESS_KEY_,PARENT_ID_,PROC_DEF_ID_,SUPER_EXEC_,ROOT_PROC_INST_ID_,ACT_ID_,IS_ACTIVE_,IS_CONCURRENT_,IS_SCOPE_,IS_EVENT_SCOPE_,IS_MI_ROOT_,SUSPENSION_STATE_,CACHED_ENT_STATE_,TENANT_ID_,NAME_,START_ACT_ID_,START_TIME_,START_USER_ID_,LOCK_TIME_,IS_COUNT_ENABLED_,EVT_SUBSCR_COUNT_,TASK_COUNT_,JOB_COUNT_,TIMER_JOB_COUNT_,SUSP_JOB_COUNT_,DEADLETTER_JOB_COUNT_,VAR_COUNT_,ID_LINK_COUNT_,CALLBACK_ID_,CALLBACK_TYPE_) SELECT ID_,REV_,PROC_INST_ID_,BUSINESS_KEY_,PARENT_ID_,PROC_DEF_ID_,SUPER_EXEC_,ROOT_PROC_INST_ID_,ACT_ID_,IS_ACTIVE_,IS_CONCURRENT_,IS_SCOPE_,IS_EVENT_SCOPE_,IS_MI_ROOT_,SUSPENSION_STATE_,CACHED_ENT_STATE_,TENANT_ID_,NAME_,START_ACT_ID_,START_TIME_,START_USER_ID_,LOCK_TIME_,IS_COUNT_ENABLED_,EVT_SUBSCR_COUNT_,TASK_COUNT_,JOB_COUNT_,TIMER_JOB_COUNT_,SUSP_JOB_COUNT_,DEADLETTER_JOB_COUNT_,VAR_COUNT_,ID_LINK_COUNT_,CALLBACK_ID_,CALLBACK_TYPE_ FROM FF_ACT_RU_EXECUTION_"
                        + year + " T WHERE T.PROC_INST_ID_ = '" + processInstanceId + "'");
                stmt.executeBatch();
                LOGGER.info("**************复制数据到ACT_RU_EXECUTION成功****************");
            } finally {
                if (stmt != null) {
                    stmt.execute("SET FOREIGN_KEY_CHECKS=1");
                    stmt.close();
                }
                if (connection != null) {
                    connection.close();
                }
            }

            /**
             * 2-恢复正在运行的变量，先查询出来，再删除。因为调接口保存的时候，会再向历史表里面插入一份
             */
            String sql = "SELECT DISTINCT" + "	RES.*" + " FROM" + "	ACT_HI_TASKINST_" + year + " RES" + " WHERE" + "	RES.PROC_INST_ID_ = '" + processInstanceId + "'" + " ORDER BY" + "	RES.END_TIME_ DESC";
            HistoricTaskInstance hti = historyService.createNativeHistoricTaskInstanceQuery().sql(sql).list().get(0);
            List<Execution> executionList = runtimeService.createExecutionQuery().processInstanceId(processInstanceId).list();
            Set<String> executionSet = new HashSet<>();
            for (Execution execution : executionList) {
                executionSet.add(execution.getId());
            }
            String nodeType = customProcessDefinitionService.getNodeType(hti.getProcessDefinitionId(), hti.getTaskDefinitionKey());

            /**
             * 复制年度历史数据到正在运行历史表
             */
            saveYearData(year, processInstanceId);

            List<HistoricVariableInstance> pVarList = historyService.createHistoricVariableInstanceQuery().executionIds(executionSet).excludeTaskVariables().list();
            List<HistoricVariableInstance> tVarList = historyService.createHistoricVariableInstanceQuery().taskId(hti.getId()).list();
            Map<String, Object> pVarMap = new HashMap<>(16);
            Map<String, Object> eVarMap = new HashMap<>(16);
            Map<String, Object> tVarMap = new HashMap<>(16);
            /**
             * 多实例任务时的循环次数，假如选择三个人发送，并行则同时生成三个变量分别为loopCounter=0，loopCounter=1，loopCounter=2，串行的时候，只有一个变量，刚开始loopCounter=0，办理完成一个loopCounter就加1
             * 所以这里还原的时候，如果是串行的就-1
             */
            UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
            String userId = userInfo.getPersonId();
            if (nodeType.equals(SysVariables.SEQUENTIAL)) {
                for (HistoricVariableInstance pVar : pVarList) {
                    String key = pVar.getVariableName();
                    Object val = pVar.getValue();
                    if (key.equals(SysVariables.ELEMENTUSER) || key.equals(SysVariables.LOOPCOUNTER) || key.equals(SysVariables.ROUTETOTASKID) || key.equals(SysVariables.USER) || key.equals(SysVariables.USERS)) {
                        continue;
                    }
                    pVarMap.put(key, val);
                }
                List<String> usersList = new ArrayList<>();
                usersList.add(userId);
                pVarMap.put(SysVariables.USERS, usersList);
                pVarMap.put(SysVariables.NROFINSTANCES, 1);
                pVarMap.put(SysVariables.NROFCOMPLETEDINSTANCES, 0);
                pVarMap.put(SysVariables.NROFACTIVEINSTANCES, 1);
                pVarMap.put(SysVariables.LOOPCOUNTER, 0);
            } else if (nodeType.equals(SysVariables.PARALLEL)) {
                for (HistoricVariableInstance pVar : pVarList) {
                    String key = pVar.getVariableName();
                    Object val = pVar.getValue();
                    if (key.equals(SysVariables.ELEMENTUSER) || key.equals(SysVariables.LOOPCOUNTER) || key.equals(SysVariables.ROUTETOTASKID) || key.equals(SysVariables.USER)) {
                        continue;
                    }
                    pVarMap.put(key, val);
                }
                List<String> usersList = new ArrayList<>();
                usersList.add(userId);
                pVarMap.put(SysVariables.USERS, usersList);
                pVarMap.put(SysVariables.NROFINSTANCES, 1);
                pVarMap.put(SysVariables.NROFCOMPLETEDINSTANCES, 0);
                pVarMap.put(SysVariables.NROFACTIVEINSTANCES, 1);
                pVarMap.put(SysVariables.LOOPCOUNTER, 0);
            } else {
                for (HistoricVariableInstance pVar : pVarList) {
                    String key = pVar.getVariableName();
                    Object val = pVar.getValue();
                    if (key.equals(SysVariables.ELEMENTUSER) || key.equals(SysVariables.LOOPCOUNTER) || key.equals(SysVariables.ROUTETOTASKID)) {
                        continue;
                    }
                    pVarMap.put(key, val);
                }
            }
            for (HistoricVariableInstance tVar : tVarList) {
                tVarMap.put(tVar.getVariableName(), tVar.getValue());
                // 串行恢复待办,任务变量users需要重新设置,避免打开办件时nrOfInstances与usersSize不一致
                if (nodeType.equals(SysVariables.SEQUENTIAL) && tVar.getVariableName().equals(SysVariables.USERS)) {
                    List<String> usersList = new ArrayList<>();
                    usersList.add(userId);
                    tVarMap.put(SysVariables.USERS, usersList);
                }
            }

            if (nodeType.equals(SysVariables.PARALLEL) || nodeType.equals(SysVariables.SEQUENTIAL)) {
                String sql00 = "SELECT * FROM ACT_RU_EXECUTION WHERE PROC_INST_ID_ = #{PROC_INST_ID_} AND IS_MI_ROOT_=1";
                Execution miRootExecution = runtimeService.createNativeExecutionQuery().sql(sql00).parameter("PROC_INST_ID_", processInstanceId).singleResult();
                String sql01 = "DELETE FROM ACT_HI_VARINST WHERE EXECUTION_ID_=#{PROC_INST_ID_} OR EXECUTION_ID_=#{MIROOTEXECUTION_ID_} OR TASK_ID_=#{TASK_ID_}";
                historyService.createNativeHistoricVariableInstanceQuery().sql(sql01).parameter("PROC_INST_ID_", processInstanceId).parameter("MIROOTEXECUTION_ID_", miRootExecution.getId()).parameter("TASK_ID_", hti.getId()).list();
                runtimeService.setVariablesLocal(miRootExecution.getId(), eVarMap);
            } else {
                String sql01 = "DELETE FROM ACT_HI_VARINST WHERE EXECUTION_ID_=#{PROC_INST_ID_} OR TASK_ID_=#{TASK_ID_}";
                historyService.createNativeHistoricVariableInstanceQuery().sql(sql01).parameter("PROC_INST_ID_", processInstanceId).parameter("TASK_ID_", hti.getId()).list();
            }

            /**
             * 3-执行数据恢复
             */
            managementService.executeCommand(new RecoveryTodoCommand(hti, pVarMap, tVarMap));

            /**
             * 4-删除历史节点中办结任务到结束节点的数据
             */
            List<HistoricActivityInstance> hisActivityList = historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).orderByHistoricActivityInstanceEndTime().desc().list();
            for (HistoricActivityInstance hisActivity : hisActivityList) {
                if (hisActivity.getActivityType().equals(SysVariables.USERTASK)) {
                    String sql2 = "UPDATE ACT_HI_ACTINST SET END_TIME_=NULL,DURATION_=NULL,DELETE_REASON_=NULL WHERE ID_ = #{ID_}";
                    runtimeService.createNativeExecutionQuery().sql(sql2).parameter("ID_", hisActivity.getId()).list();
                    break;
                } else {
                    String sql2 = "DELETE FROM ACT_HI_ACTINST WHERE ID_ = #{ID_}";
                    runtimeService.createNativeExecutionQuery().sql(sql2).parameter("ID_", hisActivity.getId()).list();
                }
            }

            /**
             * 5-恢复成功后删除备份的数据
             */
            try {
                // 删除数据中心办结数据
                OfficeDoneInfoModel officeDoneInfo = officeDoneInfoApi.findByProcessInstanceId(Y9LoginUserHolder.getTenantId(), processInstanceId);
                officeDoneInfo.setUserComplete("");
                officeDoneInfo.setEndTime(null);
                officeDoneInfoApi.saveOfficeDone(Y9LoginUserHolder.getTenantId(), officeDoneInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 删除年度数据
            deleteProcessUtilService.deleteYearData(Y9LoginUserHolder.getTenantId(), year, processInstanceId);
        } catch (Exception e) {
            final Writer result = new StringWriter();
            final PrintWriter print = new PrintWriter(result);
            e.printStackTrace(print);
            String msg = result.toString();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = sdf.format(new Date());
            ErrorLogModel errorLogModel = new ErrorLogModel();
            errorLogModel.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            errorLogModel.setCreateTime(time);
            errorLogModel.setErrorFlag(ErrorLogModel.ERROR_FLAG_RECOVERY_COMLETED);
            errorLogModel.setErrorType(ErrorLogModel.ERROR_PROCESS_INSTANCE);
            errorLogModel.setExtendField("恢复待办失败");
            errorLogModel.setProcessInstanceId(processInstanceId);
            errorLogModel.setTaskId("");
            errorLogModel.setText(msg);
            errorLogModel.setUpdateTime(time);
            try {
                errorLogManager.saveErrorLog(Y9LoginUserHolder.getTenantId(), errorLogModel);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
            throw new Exception("CustomRuntimeServiceImpl recovery4Completed error");
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void recovery4SetUpCompleted(String processInstanceId) {
        runtimeService.activateProcessInstanceById(processInstanceId);

        String updateSql = "UPDATE ACT_HI_PROCINST T SET T.END_TIME_ = #{END_TIME_,jdbcType=DATE} WHERE T.PROC_INST_ID_=#{processInstanceId}";
        historyService.createNativeHistoricProcessInstanceQuery().sql(updateSql).parameter("END_TIME_", null).parameter("processInstanceId", processInstanceId).singleResult();
    }

    @Override
    @Transactional(readOnly = false)
    public void recoveryCompleted4Position(String processInstanceId, String year) throws Exception {
        try {
            /**
             * 1-恢复正在运行的流程实例
             */
            java.sql.Statement stmt = null;
            Connection connection = null;
            try {
                connection = jdbcTemplate.getDataSource().getConnection();
                stmt = connection.createStatement();
                stmt.addBatch("SET FOREIGN_KEY_CHECKS=0");
                stmt.addBatch(
                    "INSERT INTO ACT_RU_EXECUTION (ID_,REV_,PROC_INST_ID_,BUSINESS_KEY_,PARENT_ID_,PROC_DEF_ID_,SUPER_EXEC_,ROOT_PROC_INST_ID_,ACT_ID_,IS_ACTIVE_,IS_CONCURRENT_,IS_SCOPE_,IS_EVENT_SCOPE_,IS_MI_ROOT_,SUSPENSION_STATE_,CACHED_ENT_STATE_,TENANT_ID_,NAME_,START_ACT_ID_,START_TIME_,START_USER_ID_,LOCK_TIME_,IS_COUNT_ENABLED_,EVT_SUBSCR_COUNT_,TASK_COUNT_,JOB_COUNT_,TIMER_JOB_COUNT_,SUSP_JOB_COUNT_,DEADLETTER_JOB_COUNT_,VAR_COUNT_,ID_LINK_COUNT_,CALLBACK_ID_,CALLBACK_TYPE_) SELECT ID_,REV_,PROC_INST_ID_,BUSINESS_KEY_,PARENT_ID_,PROC_DEF_ID_,SUPER_EXEC_,ROOT_PROC_INST_ID_,ACT_ID_,IS_ACTIVE_,IS_CONCURRENT_,IS_SCOPE_,IS_EVENT_SCOPE_,IS_MI_ROOT_,SUSPENSION_STATE_,CACHED_ENT_STATE_,TENANT_ID_,NAME_,START_ACT_ID_,START_TIME_,START_USER_ID_,LOCK_TIME_,IS_COUNT_ENABLED_,EVT_SUBSCR_COUNT_,TASK_COUNT_,JOB_COUNT_,TIMER_JOB_COUNT_,SUSP_JOB_COUNT_,DEADLETTER_JOB_COUNT_,VAR_COUNT_,ID_LINK_COUNT_,CALLBACK_ID_,CALLBACK_TYPE_ FROM FF_ACT_RU_EXECUTION_"
                        + year + " T WHERE T.PROC_INST_ID_ = '" + processInstanceId + "'");
                stmt.executeBatch();
                LOGGER.info("**************复制数据到ACT_RU_EXECUTION成功****************");
            } finally {
                if (stmt != null) {
                    stmt.execute("SET FOREIGN_KEY_CHECKS=1");
                    stmt.close();
                }
                if (connection != null) {
                    connection.close();
                }
            }

            /**
             * 2-恢复正在运行的变量，先查询出来，再删除。因为调接口保存的时候，会再向历史表里面插入一份
             */
            String sql = "SELECT DISTINCT" + "	RES.*" + " FROM" + "	ACT_HI_TASKINST_" + year + " RES" + " WHERE" + "	RES.PROC_INST_ID_ = '" + processInstanceId + "'" + " ORDER BY" + "	RES.END_TIME_ DESC";
            HistoricTaskInstance hti = historyService.createNativeHistoricTaskInstanceQuery().sql(sql).list().get(0);
            List<Execution> executionList = runtimeService.createExecutionQuery().processInstanceId(processInstanceId).list();
            Set<String> executionSet = new HashSet<>();
            for (Execution execution : executionList) {
                executionSet.add(execution.getId());
            }
            String nodeType = customProcessDefinitionService.getNodeType(hti.getProcessDefinitionId(), hti.getTaskDefinitionKey());

            /**
             * 复制年度历史数据到正在运行历史表
             */
            saveYearData(year, processInstanceId);

            List<HistoricVariableInstance> pVarList = historyService.createHistoricVariableInstanceQuery().executionIds(executionSet).excludeTaskVariables().list();
            List<HistoricVariableInstance> tVarList = historyService.createHistoricVariableInstanceQuery().taskId(hti.getId()).list();
            Map<String, Object> pVarMap = new HashMap<>(16);
            Map<String, Object> eVarMap = new HashMap<>(16);
            Map<String, Object> tVarMap = new HashMap<>(16);
            /**
             * 多实例任务时的循环次数，假如选择三个人发送，并行则同时生成三个变量分别为loopCounter=0，loopCounter=1，loopCounter=2，串行的时候，只有一个变量，刚开始loopCounter=0，办理完成一个loopCounter就加1
             * 所以这里还原的时候，如果是串行的就-1
             */
            Position position = Y9LoginUserHolder.getPosition();
            String positionId = position.getId();
            if (nodeType.equals(SysVariables.SEQUENTIAL)) {
                for (HistoricVariableInstance pVar : pVarList) {
                    String key = pVar.getVariableName();
                    Object val = pVar.getValue();
                    if (key.equals(SysVariables.ELEMENTUSER) || key.equals(SysVariables.LOOPCOUNTER) || key.equals(SysVariables.ROUTETOTASKID) || key.equals(SysVariables.USER) || key.equals(SysVariables.USERS)) {
                        continue;
                    }
                    pVarMap.put(key, val);
                }
                List<String> usersList = new ArrayList<>();
                usersList.add(positionId);
                pVarMap.put(SysVariables.USERS, usersList);
                pVarMap.put(SysVariables.NROFINSTANCES, 1);
                pVarMap.put(SysVariables.NROFCOMPLETEDINSTANCES, 0);
                pVarMap.put(SysVariables.NROFACTIVEINSTANCES, 1);
                pVarMap.put(SysVariables.LOOPCOUNTER, 0);
            } else if (nodeType.equals(SysVariables.PARALLEL)) {
                for (HistoricVariableInstance pVar : pVarList) {
                    String key = pVar.getVariableName();
                    Object val = pVar.getValue();
                    if (key.equals(SysVariables.ELEMENTUSER) || key.equals(SysVariables.LOOPCOUNTER) || key.equals(SysVariables.ROUTETOTASKID) || key.equals(SysVariables.USER)) {
                        continue;
                    }
                    pVarMap.put(key, val);
                }
                List<String> usersList = new ArrayList<>();
                usersList.add(positionId);
                pVarMap.put(SysVariables.USERS, usersList);
                pVarMap.put(SysVariables.NROFINSTANCES, 1);
                pVarMap.put(SysVariables.NROFCOMPLETEDINSTANCES, 0);
                pVarMap.put(SysVariables.NROFACTIVEINSTANCES, 1);
                pVarMap.put(SysVariables.LOOPCOUNTER, 0);
            } else {
                for (HistoricVariableInstance pVar : pVarList) {
                    String key = pVar.getVariableName();
                    Object val = pVar.getValue();
                    if (key.equals(SysVariables.ELEMENTUSER) || key.equals(SysVariables.LOOPCOUNTER) || key.equals(SysVariables.ROUTETOTASKID)) {
                        continue;
                    }
                    pVarMap.put(key, val);
                }
            }
            for (HistoricVariableInstance tVar : tVarList) {
                tVarMap.put(tVar.getVariableName(), tVar.getValue());
                // 串行恢复待办,任务变量users需要重新设置,避免打开办件时nrOfInstances与usersSize不一致
                if (nodeType.equals(SysVariables.SEQUENTIAL) && tVar.getVariableName().equals(SysVariables.USERS)) {
                    List<String> usersList = new ArrayList<>();
                    usersList.add(positionId);
                    tVarMap.put(SysVariables.USERS, usersList);
                }
            }

            if (nodeType.equals(SysVariables.PARALLEL) || nodeType.equals(SysVariables.SEQUENTIAL)) {
                String sql00 = "SELECT * FROM ACT_RU_EXECUTION WHERE PROC_INST_ID_ = #{PROC_INST_ID_} AND IS_MI_ROOT_=1";
                Execution miRootExecution = runtimeService.createNativeExecutionQuery().sql(sql00).parameter("PROC_INST_ID_", processInstanceId).singleResult();
                String sql01 = "DELETE FROM ACT_HI_VARINST WHERE EXECUTION_ID_=#{PROC_INST_ID_} OR EXECUTION_ID_=#{MIROOTEXECUTION_ID_} OR TASK_ID_=#{TASK_ID_}";
                historyService.createNativeHistoricVariableInstanceQuery().sql(sql01).parameter("PROC_INST_ID_", processInstanceId).parameter("MIROOTEXECUTION_ID_", miRootExecution.getId()).parameter("TASK_ID_", hti.getId()).list();
                runtimeService.setVariablesLocal(miRootExecution.getId(), eVarMap);
            } else {
                String sql01 = "DELETE FROM ACT_HI_VARINST WHERE EXECUTION_ID_=#{PROC_INST_ID_} OR TASK_ID_=#{TASK_ID_}";
                historyService.createNativeHistoricVariableInstanceQuery().sql(sql01).parameter("PROC_INST_ID_", processInstanceId).parameter("TASK_ID_", hti.getId()).list();
            }

            /**
             * 3-执行数据恢复
             */
            managementService.executeCommand(new RecoveryTodoCommand4Position(hti, pVarMap, tVarMap));

            /**
             * 4-删除历史节点中办结任务到结束节点的数据
             */
            List<HistoricActivityInstance> hisActivityList = historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).orderByHistoricActivityInstanceEndTime().desc().list();
            for (HistoricActivityInstance hisActivity : hisActivityList) {
                if (hisActivity.getActivityType().equals(SysVariables.USERTASK)) {
                    String sql2 = "UPDATE ACT_HI_ACTINST SET END_TIME_=NULL,DURATION_=NULL,DELETE_REASON_=NULL WHERE ID_ = #{ID_}";
                    runtimeService.createNativeExecutionQuery().sql(sql2).parameter("ID_", hisActivity.getId()).list();
                    break;
                } else {
                    String sql2 = "DELETE FROM ACT_HI_ACTINST WHERE ID_ = #{ID_}";
                    runtimeService.createNativeExecutionQuery().sql(sql2).parameter("ID_", hisActivity.getId()).list();
                }
            }

            /**
             * 5-恢复成功后删除备份的数据
             */
            try {
                // 修改ES办件信息数据
                OfficeDoneInfoModel officeDoneInfo = officeDoneInfo4PositionApi.findByProcessInstanceId(Y9LoginUserHolder.getTenantId(), processInstanceId);
                officeDoneInfo.setUserComplete("");
                officeDoneInfo.setEndTime(null);
                officeDoneInfo4PositionApi.saveOfficeDone(Y9LoginUserHolder.getTenantId(), officeDoneInfo);
            } catch (Exception e) {
                final Writer result = new StringWriter();
                final PrintWriter print = new PrintWriter(result);
                e.printStackTrace(print);
                String msg = result.toString();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String time = sdf.format(new Date());
                ErrorLogModel errorLogModel = new ErrorLogModel();
                errorLogModel.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                errorLogModel.setCreateTime(time);
                errorLogModel.setErrorFlag(ErrorLogModel.ERROR_FLAG_RECOVERY_COMLETED);
                errorLogModel.setErrorType(ErrorLogModel.ERROR_PROCESS_INSTANCE);
                errorLogModel.setExtendField("恢复办件信息数据失败");
                errorLogModel.setProcessInstanceId(processInstanceId);
                errorLogModel.setTaskId("");
                errorLogModel.setText(msg);
                errorLogModel.setUpdateTime(time);
                try {
                    errorLogManager.saveErrorLog(Y9LoginUserHolder.getTenantId(), errorLogModel);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }

            /**
             * 恢复整个流程的办件详情,恢复为未办结
             */
            try {
                actRuDetailApi.recoveryByProcessInstanceId(Y9LoginUserHolder.getTenantId(), processInstanceId);
            } catch (Exception e) {
                final Writer result = new StringWriter();
                final PrintWriter print = new PrintWriter(result);
                e.printStackTrace(print);
                String msg = result.toString();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String time = sdf.format(new Date());
                ErrorLogModel errorLogModel = new ErrorLogModel();
                errorLogModel.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                errorLogModel.setCreateTime(time);
                errorLogModel.setErrorFlag(ErrorLogModel.ERROR_FLAG_RECOVERY_COMLETED);
                errorLogModel.setErrorType(ErrorLogModel.ERROR_PROCESS_INSTANCE);
                errorLogModel.setExtendField("恢复办件详情失败");
                errorLogModel.setProcessInstanceId(processInstanceId);
                errorLogModel.setTaskId("");
                errorLogModel.setText(msg);
                errorLogModel.setUpdateTime(time);
                try {
                    errorLogManager.saveErrorLog(Y9LoginUserHolder.getTenantId(), errorLogModel);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }

            // 删除年度数据
            deleteProcessUtilService.deleteYearData(Y9LoginUserHolder.getTenantId(), year, processInstanceId);
        } catch (Exception e) {
            final Writer result = new StringWriter();
            final PrintWriter print = new PrintWriter(result);
            e.printStackTrace(print);
            String msg = result.toString();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = sdf.format(new Date());
            ErrorLogModel errorLogModel = new ErrorLogModel();
            errorLogModel.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            errorLogModel.setCreateTime(time);
            errorLogModel.setErrorFlag(ErrorLogModel.ERROR_FLAG_RECOVERY_COMLETED);
            errorLogModel.setErrorType(ErrorLogModel.ERROR_PROCESS_INSTANCE);
            errorLogModel.setExtendField("恢复待办失败");
            errorLogModel.setProcessInstanceId(processInstanceId);
            errorLogModel.setTaskId("");
            errorLogModel.setText(msg);
            errorLogModel.setUpdateTime(time);
            try {
                errorLogManager.saveErrorLog(Y9LoginUserHolder.getTenantId(), errorLogModel);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
            throw new Exception("CustomRuntimeServiceImpl recovery4Completed error");
        }
    }

    /**
     * 恢复年度历史数据
     *
     * @param year
     * @param PROC_INST_ID_
     */
    public void saveYearData(String year, String processInstanceId) {
        String sql3 = getActHiTaskinstSql(year, processInstanceId);
        // 同步历史任务
        jdbcTemplate.execute(sql3);

        sql3 = getActHiVarinstSql(year, processInstanceId);
        // 同步历史变量
        jdbcTemplate.execute(sql3);

        sql3 = getActGeBytearraySql(year, processInstanceId);
        // 同步二进制数据表
        jdbcTemplate.execute(sql3);

        sql3 = getActHiIdentiyLinkSql(year, processInstanceId);
        // 同步历史参与人
        jdbcTemplate.execute(sql3);

        sql3 = getActHiActinstSql(year, processInstanceId);
        // 同步历史节点
        jdbcTemplate.execute(sql3);

        sql3 = getActHiProcinstSql(year, processInstanceId);
        // 同步流程实例
        jdbcTemplate.execute(sql3);
    }

    @Override
    @Transactional(readOnly = false)
    public void setUpCompleted(String processInstanceId) {
        runtimeService.suspendProcessInstanceById(processInstanceId);

        String updateSql = "UPDATE ACT_HI_PROCINST T SET T.END_TIME_ = #{END_TIME_} WHERE T.PROC_INST_ID_=#{processInstanceId}";
        historyService.createNativeHistoricProcessInstanceQuery().sql(updateSql).parameter("END_TIME_", new Date()).parameter("processInstanceId", processInstanceId).singleResult();
    }

    @Override
    public void setVariable(String processInstanceId, String key, Object val) {
        runtimeService.setVariable(processInstanceId, key, val);
    }

    @Override
    public void setVariables(String executionId, Map<String, Object> map) {
        runtimeService.setVariables(executionId, map);
    }

    /**
     * 这里流程的启动人为userId:deptId为了避免act_ru_identitylink的类型starter的数据对在办列表查询的干扰
     */
    @Override
    @Transactional(readOnly = false)
    public ProcessInstance startProcessInstanceByKey(String processDefinitionKey, String systemName, Map<String, Object> map) {
        try {
            UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
            String userIdAndDeptId = userInfo.getPersonId() + ":" + userInfo.getParentId();
            identityService.setAuthenticatedUserId(userIdAndDeptId);
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, systemName, map);
            return processInstance;
        } finally {
            identityService.setAuthenticatedUserId(null);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public ProcessInstance startProcessInstanceByKey4Position(String processDefinitionKey, String systemName, Map<String, Object> map) {
        try {
            identityService.setAuthenticatedUserId(Y9LoginUserHolder.getPositionId());
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, systemName, map);
            return processInstance;
        } finally {
            identityService.setAuthenticatedUserId(null);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void switchSuspendOrActive(String processInstanceId, String state) {
        if (ItemProcessStateTypeEnum.ACTIVE.getValue().equals(state)) {
            runtimeService.activateProcessInstanceById(processInstanceId);
        } else if (ItemProcessStateTypeEnum.SUSPEND.getValue().equals(state)) {
            runtimeService.suspendProcessInstanceById(processInstanceId);
        }
    }
}
