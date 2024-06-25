package net.risesoft.service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.datacenter.OfficeInfoApi;
import net.risesoft.api.itemadmin.ActRuDetailApi;
import net.risesoft.api.itemadmin.ChaoSongInfoApi;
import net.risesoft.api.itemadmin.ErrorLogApi;
import net.risesoft.api.itemadmin.OfficeFollowApi;
import net.risesoft.api.itemadmin.ProcessInstanceApi;
import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.api.itemadmin.position.ChaoSong4PositionApi;
import net.risesoft.api.itemadmin.position.OfficeFollow4PositionApi;
import net.risesoft.api.msgremind.MsgRemindInfoApi;
import net.risesoft.api.todo.TodoTaskApi;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.ErrorLogModel;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@EnableAsync
@Slf4j
@Service(value = "deleteProcessUtilService")
public class DeleteProcessUtilService {

    @javax.annotation.Resource(name = "jdbcTemplate4Tenant")
    private JdbcTemplate jdbcTemplate;

    private final TodoTaskApi rpcTodoTaskManager;

    private final ProcessInstanceApi processInstanceApi;

    private final ChaoSongInfoApi chaoSongInfoApi;

    private final ChaoSong4PositionApi chaoSong4PositionApi;

    private final OfficeInfoApi officeInfoApi;

    private final ProcessParamApi processParamManager;

    private final OfficeFollowApi officeFollowApi;

    private final OfficeFollow4PositionApi officeFollow4PositionApi;

    private final ErrorLogApi errorLogManager;

    private final MsgRemindInfoApi msgRemindInfoManager;

    private final ActRuDetailApi actRuDetailApi;

    public DeleteProcessUtilService(TodoTaskApi rpcTodoTaskManager, ProcessInstanceApi processInstanceApi,
        ChaoSongInfoApi chaoSongInfoApi, ChaoSong4PositionApi chaoSong4PositionApi, OfficeInfoApi officeInfoApi,
        ProcessParamApi processParamManager, OfficeFollowApi officeFollowApi,
        OfficeFollow4PositionApi officeFollow4PositionApi, ErrorLogApi errorLogManager,
        MsgRemindInfoApi msgRemindInfoManager, ActRuDetailApi actRuDetailApi) {
        this.rpcTodoTaskManager = rpcTodoTaskManager;
        this.processInstanceApi = processInstanceApi;
        this.chaoSongInfoApi = chaoSongInfoApi;
        this.chaoSong4PositionApi = chaoSong4PositionApi;
        this.officeInfoApi = officeInfoApi;
        this.processParamManager = processParamManager;
        this.officeFollowApi = officeFollowApi;
        this.officeFollow4PositionApi = officeFollow4PositionApi;
        this.errorLogManager = errorLogManager;
        this.msgRemindInfoManager = msgRemindInfoManager;
        this.actRuDetailApi = actRuDetailApi;
    }

    /**
     * 彻底删除流程实例相关数据，包括，流程自定义变量，统一待办，协作状态，抄送件，关注件，数据中心全文检索数据
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     */
    @Async
    public void deleteProcess(final String tenantId, final String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        FlowableTenantInfoHolder.setTenantId(tenantId);
        try {
            processParamManager.deleteByPprocessInstanceId(tenantId, processInstanceId);
        } catch (Exception e3) {
            LOGGER.warn("**********删除流程实例", e3);
        }
        try {
            rpcTodoTaskManager.deleteByProcessInstanceId(tenantId, processInstanceId);
        } catch (Exception e1) {
            LOGGER.warn("************************************删除待办事宜数据失败", e1);
            final Writer result = new StringWriter();
            final PrintWriter print = new PrintWriter(result);
            e1.printStackTrace(print);
            String msg = result.toString();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = sdf.format(new Date());
            ErrorLogModel errorLogModel = new ErrorLogModel();
            errorLogModel.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            errorLogModel.setCreateTime(time);
            errorLogModel.setErrorFlag(ErrorLogModel.ERROR_FLAG_DELETE_TODO);
            errorLogModel.setErrorType(ErrorLogModel.ERROR_PROCESS_INSTANCE);
            errorLogModel.setExtendField("删除流程实例，删除统一待办失败");
            errorLogModel.setProcessInstanceId(processInstanceId);
            errorLogModel.setTaskId("");
            errorLogModel.setText(msg);
            errorLogModel.setUpdateTime(time);
            errorLogManager.saveErrorLog(Y9LoginUserHolder.getTenantId(), errorLogModel);
        }
        try {
            processInstanceApi.deleteProcessInstance(tenantId, processInstanceId);
        } catch (Exception e1) {
            LOGGER.warn("************************************删除协作状态数据失败", e1);
        }
        try {
            chaoSongInfoApi.deleteByProcessInstanceId(tenantId, processInstanceId);
        } catch (Exception e1) {
            LOGGER.warn("************************************删除抄送数据失败", e1);
        }
        try {
            officeFollowApi.deleteByProcessInstanceId(tenantId, processInstanceId);
        } catch (Exception e1) {
            LOGGER.warn("************************************删除关注数据失败", e1);
        }
        try {
            officeInfoApi.deleteOfficeInfo(tenantId, processInstanceId);
        } catch (Exception e) {
            LOGGER.warn("************************************删除数据中心数据失败", e);
        }
        try {
            actRuDetailApi.removeByProcessInstanceId(tenantId, processInstanceId);
        } catch (Exception e) {
            LOGGER.warn("************************************删除办理情况数据失败", e);
        }
        try {
            msgRemindInfoManager.deleteMsgRemindInfo(tenantId, processInstanceId);
        } catch (Exception e) {
            LOGGER.warn("************************************删除消息提醒数据失败", e);
        }
    }

    @Async
    public void deleteProcess4Position(final String tenantId, final String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        FlowableTenantInfoHolder.setTenantId(tenantId);
        try {
            processParamManager.deleteByPprocessInstanceId(tenantId, processInstanceId);
        } catch (Exception e3) {
            LOGGER.error("**********删除流程实例", e3);
        }
        try {
            rpcTodoTaskManager.deleteByProcessInstanceId(tenantId, processInstanceId);
        } catch (Exception e1) {
            LOGGER.error("************************************删除待办事宜数据失败", e1);
            final Writer result = new StringWriter();
            final PrintWriter print = new PrintWriter(result);
            e1.printStackTrace(print);
            String msg = result.toString();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = sdf.format(new Date());
            ErrorLogModel errorLogModel = new ErrorLogModel();
            errorLogModel.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            errorLogModel.setCreateTime(time);
            errorLogModel.setErrorFlag(ErrorLogModel.ERROR_FLAG_DELETE_TODO);
            errorLogModel.setErrorType(ErrorLogModel.ERROR_PROCESS_INSTANCE);
            errorLogModel.setExtendField("删除流程实例，删除统一待办失败");
            errorLogModel.setProcessInstanceId(processInstanceId);
            errorLogModel.setTaskId("");
            errorLogModel.setText(msg);
            errorLogModel.setUpdateTime(time);
            errorLogManager.saveErrorLog(Y9LoginUserHolder.getTenantId(), errorLogModel);
        }
        try {
            processInstanceApi.deleteProcessInstance(tenantId, processInstanceId);
        } catch (Exception e1) {
            LOGGER.error("************************************删除协作状态数据失败", e1);
        }
        try {
            chaoSong4PositionApi.deleteByProcessInstanceId(tenantId, processInstanceId);
        } catch (Exception e1) {
            LOGGER.error("************************************删除抄送数据失败", e1);
        }
        try {
            officeFollow4PositionApi.deleteByProcessInstanceId(tenantId, processInstanceId);
        } catch (Exception e1) {
            LOGGER.error("************************************删除关注数据失败", e1);
        }
        try {
            officeInfoApi.deleteOfficeInfo(tenantId, processInstanceId);
        } catch (Exception e) {
            LOGGER.error("************************************删除数据中心数据失败", e);
        }

        try {
            msgRemindInfoManager.deleteMsgRemindInfo(tenantId, processInstanceId);
        } catch (Exception e) {
            LOGGER.error("************************************删除消息提醒数据失败", e);
        }

        try {
            actRuDetailApi.removeByProcessInstanceId(tenantId, processInstanceId);
        } catch (Exception e) {
            LOGGER.error("************************************删除办理情况数据失败", e);
        }
    }

    /**
     * 删除年度数据
     *
     * @param tenantId 租户ID
     * @param year 年度
     * @param processInstanceId 流程实例ID
     */
    @Async
    public void deleteYearData(final String tenantId, final String year, final String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        FlowableTenantInfoHolder.setTenantId(tenantId);
        try {
            String sql3 = "DELETE from ACT_HI_TASKINST_" + year + " where PROC_INST_ID_ = '" + processInstanceId + "'";
            jdbcTemplate.execute(sql3);

            sql3 = "DELETE" + " FROM" + "	ACT_GE_BYTEARRAY_" + year + "" + " WHERE" + "	ID_ IN (" + "		SELECT"
                + "			*" + "		FROM ( " + "         SELECT" + "			b.ID_" + "		  FROM"
                + "			 ACT_GE_BYTEARRAY_" + year + " b" + "		  LEFT JOIN ACT_HI_VARINST_" + year
                + " v ON v.BYTEARRAY_ID_ = b.ID_" + "		  WHERE" + "			 v.PROC_INST_ID_ = '"
                + processInstanceId + "'" + "		  AND v.NAME_ = 'users'" + "       ) TT" + "	)";
            jdbcTemplate.execute(sql3);

            sql3 = "DELETE from ACT_HI_VARINST_" + year + " where PROC_INST_ID_ = '" + processInstanceId + "'";
            jdbcTemplate.execute(sql3);

            sql3 = "DELETE from ACT_HI_IDENTITYLINK_" + year + " where PROC_INST_ID_ = '" + processInstanceId + "'";
            jdbcTemplate.execute(sql3);

            sql3 = "DELETE from ACT_HI_ACTINST_" + year + " where PROC_INST_ID_ = '" + processInstanceId + "'";
            jdbcTemplate.execute(sql3);

            sql3 = "DELETE from ACT_HI_PROCINST_" + year + " where PROC_INST_ID_ = '" + processInstanceId + "'";
            jdbcTemplate.execute(sql3);
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
            errorLogModel.setErrorFlag(ErrorLogModel.ERROR_FLAG_DELETE_YEARDATA);
            errorLogModel.setErrorType(ErrorLogModel.ERROR_PROCESS_INSTANCE);
            errorLogModel.setExtendField("删除流程实例，删除年度表数据失败");
            errorLogModel.setProcessInstanceId(processInstanceId);
            errorLogModel.setTaskId("");
            errorLogModel.setText(msg);
            errorLogModel.setUpdateTime(time);
            errorLogManager.saveErrorLog(Y9LoginUserHolder.getTenantId(), errorLogModel);
            LOGGER.error("**********删除年度表数据失败", e);
        }
    }

}
