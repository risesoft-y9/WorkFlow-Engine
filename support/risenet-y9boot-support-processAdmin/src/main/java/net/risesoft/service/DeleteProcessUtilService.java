package net.risesoft.service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
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
@Service(value = "deleteProcessUtilService")
@Slf4j
public class DeleteProcessUtilService {

    @Autowired
    private TodoTaskApi rpcTodoTaskManager;

    @Autowired
    private ProcessInstanceApi processInstanceApi;

    @Autowired
    private ChaoSongInfoApi chaoSongInfoApi;

    @Autowired
    private ChaoSong4PositionApi chaoSong4PositionApi;

    @Autowired
    private OfficeInfoApi officeInfoApi;

    @javax.annotation.Resource(name = "jdbcTemplate4Tenant")
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ProcessParamApi processParamManager;

    @Autowired
    private OfficeFollowApi officeFollowApi;

    @Autowired
    private OfficeFollow4PositionApi officeFollow4PositionApi;

    @Autowired
    private ErrorLogApi errorLogManager;

    @Autowired
    private MsgRemindInfoApi msgRemindInfoManager;

    @Autowired
    private ActRuDetailApi actRuDetailApi;

    /**
     * 彻底删除流程实例相关数据，包括，流程自定义变量，统一待办，协作状态，抄送件，关注件，数据中心全文检索数据
     *
     * @param tenantId
     * @param processInstanceId
     * @return
     */
    @Async
    public Future<Boolean> deleteProcess(final String tenantId, final String processInstanceId, final String year) {
        Y9LoginUserHolder.setTenantId(tenantId);
        FlowableTenantInfoHolder.setTenantId(tenantId);
        try {
            try {
                processParamManager.deleteByPprocessInstanceId(tenantId, processInstanceId);
            } catch (Exception e3) {
                e3.printStackTrace();
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
            return new AsyncResult<>(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new AsyncResult<>(false);
    }

    @Async
    public Future<Boolean> deleteProcess4Position(final String tenantId, final String processInstanceId,
        final String year) {
        Y9LoginUserHolder.setTenantId(tenantId);
        FlowableTenantInfoHolder.setTenantId(tenantId);
        try {
            try {
                processParamManager.deleteByPprocessInstanceId(tenantId, processInstanceId);
            } catch (Exception e3) {
                e3.printStackTrace();
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
                chaoSong4PositionApi.deleteByProcessInstanceId(tenantId, processInstanceId);
            } catch (Exception e1) {
                LOGGER.warn("************************************删除抄送数据失败", e1);
            }
            try {
                officeFollow4PositionApi.deleteByProcessInstanceId(tenantId, processInstanceId);
            } catch (Exception e1) {
                LOGGER.warn("************************************删除关注数据失败", e1);
            }
            try {
                officeInfoApi.deleteOfficeInfo(tenantId, processInstanceId);
            } catch (Exception e) {
                LOGGER.warn("************************************删除数据中心数据失败", e);
            }

            try {
                msgRemindInfoManager.deleteMsgRemindInfo(tenantId, processInstanceId);
            } catch (Exception e) {
                LOGGER.warn("************************************删除消息提醒数据失败", e);
            }
            return new AsyncResult<>(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new AsyncResult<>(false);
    }

    /**
     * 删除年度数据
     *
     * @param tenantId
     * @param year
     * @param processInstanceId
     * @return
     */
    @Async
    public Future<Boolean> deleteYearData(final String tenantId, final String year, final String processInstanceId) {
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
            e.printStackTrace();
        }
        return new AsyncResult<>(false);
    }

}
