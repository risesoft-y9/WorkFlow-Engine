package net.risesoft.service.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.ChaoSongApi;
import net.risesoft.api.itemadmin.ErrorLogApi;
import net.risesoft.api.itemadmin.OfficeFollowApi;
import net.risesoft.api.itemadmin.core.ActRuDetailApi;
import net.risesoft.api.itemadmin.core.ProcessParamApi;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.ErrorLogModel;
import net.risesoft.service.DeleteProcessService;
import net.risesoft.util.Y9DateTimeUtils;
import net.risesoft.y9.FlowableTenantInfoHolder;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 删除流程实例相关
 * 
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@Slf4j
@RequiredArgsConstructor
@Service(value = "deleteProcessService")
public class DeleteProcessServiceImpl implements DeleteProcessService {

    private final ChaoSongApi chaoSongApi;

    private final ProcessParamApi processParamApi;

    private final OfficeFollowApi officeFollowApi;

    private final ErrorLogApi errorLogApi;

    private final ActRuDetailApi actRuDetailApi;

    @Resource(name = "jdbcTemplate4Tenant")
    private JdbcTemplate jdbcTemplate;

    @Async
    @Override
    public void deleteProcess(final String tenantId, final String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        FlowableTenantInfoHolder.setTenantId(tenantId);
        try {
            processParamApi.deleteByPprocessInstanceId(tenantId, processInstanceId);
        } catch (Exception e3) {
            LOGGER.error("**********删除流程实例", e3);
        }
        try {
            chaoSongApi.deleteByProcessInstanceId(tenantId, processInstanceId);
        } catch (Exception e1) {
            LOGGER.error("************************************删除抄送数据失败", e1);
        }
        try {
            officeFollowApi.deleteByProcessInstanceId(tenantId, processInstanceId);
        } catch (Exception e1) {
            LOGGER.error("************************************删除关注数据失败", e1);
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
    @Override
    public void deleteYearData(final String tenantId, final String year, final String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        FlowableTenantInfoHolder.setTenantId(tenantId);
        try {
            deleteFromTableByProcessInstanceId("ACT_HI_TASKINST_" + year, processInstanceId);
            deleteByteArrayYearData(year, processInstanceId);
            deleteFromTableByProcessInstanceId("ACT_HI_VARINST_" + year, processInstanceId);
            deleteFromTableByProcessInstanceId("ACT_HI_IDENTITYLINK_" + year, processInstanceId);
            deleteFromTableByProcessInstanceId("ACT_HI_ACTINST_" + year, processInstanceId);
            deleteFromTableByProcessInstanceId("ACT_HI_PROCINST_" + year, processInstanceId);
        } catch (Exception e) {
            handleDeleteYearDataException(e, processInstanceId);
        }
    }

    /**
     * 根据流程实例ID删除指定表中的数据
     *
     * @param tableName 表名
     * @param processInstanceId 流程实例ID
     */
    @SuppressWarnings("java:S2077")
    private void deleteFromTableByProcessInstanceId(String tableName, String processInstanceId) {
        String sql = "DELETE FROM " + tableName + " WHERE PROC_INST_ID_ = ?";
        jdbcTemplate.update(sql, processInstanceId);
    }

    /**
     * 删除年度字节数组数据
     *
     * @param year 年度
     * @param processInstanceId 流程实例ID
     */
    @SuppressWarnings("java:S2077")
    private void deleteByteArrayYearData(String year, String processInstanceId) {
        String sql =
            "DELETE FROM ACT_GE_BYTEARRAY_" + year + " WHERE ID_ IN (SELECT * FROM (SELECT b.ID_ FROM ACT_GE_BYTEARRAY_"
                + year + " b LEFT JOIN ACT_HI_VARINST_" + year
                + " v ON v.BYTEARRAY_ID_ = b.ID_ WHERE v.PROC_INST_ID_ = ? AND v.NAME_ = 'users') TT)";
        jdbcTemplate.update(sql, processInstanceId);
    }

    /**
     * 处理删除年度数据异常
     *
     * @param e 异常对象
     * @param processInstanceId 流程实例ID
     */
    private void handleDeleteYearDataException(Exception e, String processInstanceId) {
        final Writer result = new StringWriter();
        final PrintWriter print = new PrintWriter(result);
        e.printStackTrace(print);
        String msg = result.toString();
        String time = Y9DateTimeUtils.formatCurrentDateTime();
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
        errorLogApi.saveErrorLog(Y9LoginUserHolder.getTenantId(), errorLogModel);
        LOGGER.error("**********删除年度表数据失败", e);
    }
}