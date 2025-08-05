package net.risesoft.service.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.ActRuDetailApi;
import net.risesoft.api.itemadmin.ChaoSongApi;
import net.risesoft.api.itemadmin.ErrorLogApi;
import net.risesoft.api.itemadmin.OfficeFollowApi;
import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.ErrorLogModel;
import net.risesoft.service.DeleteProcessService;
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
            String sql3 = "DELETE from ACT_HI_TASKINST_" + year + " where PROC_INST_ID_ = ?";
            jdbcTemplate.update(sql3, processInstanceId);

            sql3 = "DELETE FROM ACT_GE_BYTEARRAY_" + year + " WHERE ID_ IN ( SELECT * FROM SELECT"
                + "	b.ID_ FROM ACT_GE_BYTEARRAY_" + year + " b LEFT JOIN ACT_HI_VARINST_" + year
                + " v ON v.BYTEARRAY_ID_ = b.ID_ WHERE v.PROC_INST_ID_ = '" + processInstanceId + "'"
                + "		  AND v.NAME_ = 'users') TT)";
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
            errorLogApi.saveErrorLog(Y9LoginUserHolder.getTenantId(), errorLogModel);
            LOGGER.error("**********删除年度表数据失败", e);
        }
    }

}
