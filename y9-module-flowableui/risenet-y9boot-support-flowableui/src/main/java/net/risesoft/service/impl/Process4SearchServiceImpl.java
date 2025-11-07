package net.risesoft.service.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.ErrorLogApi;
import net.risesoft.api.itemadmin.OfficeDoneInfoApi;
import net.risesoft.api.itemadmin.core.ProcessParamApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.consts.FlowableUiConsts;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.ErrorLogModel;
import net.risesoft.model.itemadmin.OfficeDoneInfoModel;
import net.risesoft.model.itemadmin.core.ProcessParamModel;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.service.Process4SearchService;
import net.risesoft.util.Y9DateTimeUtils;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9Util;

/**
 * 流程数据进入数据中心，用于综合搜索
 *
 * @author 10858
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class Process4SearchServiceImpl implements Process4SearchService {

    private final OfficeDoneInfoApi officeDoneInfoApi;
    private final OrgUnitApi orgUnitApi;
    private final ErrorLogApi errorLogApi;
    private final ProcessParamApi processParamApi;
    @Resource(name = "jdbcTemplate4Tenant")
    private JdbcTemplate jdbcTemplate;

    @Async
    @Override
    public void saveToDataCenter(final String tenantId, final String taskId, final String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        try {
            ProcessParamModel processParam =
                processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
            OfficeDoneInfoModel officeDoneInfo =
                officeDoneInfoApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
            if (officeDoneInfo != null) {
                updateOfficeDoneInfo(officeDoneInfo, processParam, tenantId, processInstanceId);
                officeDoneInfoApi.saveOfficeDone(tenantId, officeDoneInfo);
            }
        } catch (Exception e) {
            handleError(tenantId, taskId, processInstanceId, e, "重定位，串行送下一人保存流程信息失败");
        }
    }

    @Async
    @Override
    public void saveToDataCenter1(final String tenantId, final String taskId, final ProcessParamModel processParam) {
        Y9LoginUserHolder.setTenantId(tenantId);
        String processInstanceId = processParam.getProcessInstanceId();
        try {
            OfficeDoneInfoModel officeDoneInfo =
                officeDoneInfoApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
            if (officeDoneInfo != null) {
                updateOfficeDoneInfo(officeDoneInfo, processParam, tenantId, processInstanceId);
                officeDoneInfoApi.saveOfficeDone(tenantId, officeDoneInfo);
            }
        } catch (Exception e) {
            handleError(tenantId, taskId, processInstanceId, e, "并行加签保存流程信息失败");
        }
    }

    private void updateOfficeDoneInfo(OfficeDoneInfoModel officeDoneInfo, ProcessParamModel processParam,
        String tenantId, String processInstanceId) {
        officeDoneInfo.setTitle(StringUtils.isNotBlank(processParam.getTitle()) ? processParam.getTitle() : "");
        officeDoneInfo
            .setUrgency(StringUtils.isNotBlank(processParam.getCustomLevel()) ? processParam.getCustomLevel() : "");
        officeDoneInfo.setUserComplete("");
        officeDoneInfo.setBureauId(processParam.getBureauIds());
        officeDoneInfo.setEndTime(null);
        // 处理参与人
        processParticipantInfo(officeDoneInfo, tenantId, processInstanceId);
        officeDoneInfo.setTarget(processParam.getTarget());
    }

    private void processParticipantInfo(OfficeDoneInfoModel officeDoneInfo, String tenantId, String processInstanceId) {
        String sql = "SELECT i.USER_ID_ from ACT_HI_IDENTITYLINK i where i.PROC_INST_ID_ = ?";
        List<Map<String, Object>> list3 = jdbcTemplate.queryForList(sql, processInstanceId);
        String allUserId = "";
        String deptIds = "";
        for (Map<String, Object> m : list3) {
            String userId =
                m.get(FlowableUiConsts.USER_ID_KEY) != null ? (String)m.get(FlowableUiConsts.USER_ID_KEY) : "";
            if (!userId.isEmpty()) {
                if (!allUserId.contains(userId)) {
                    allUserId = Y9Util.genCustomStr(allUserId, userId);
                }
                OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, userId).getData();
                if (orgUnit != null && !deptIds.contains(orgUnit.getParentId())) {
                    deptIds = Y9Util.genCustomStr(deptIds, orgUnit.getParentId());
                }
            }
        }
        officeDoneInfo.setDeptId(deptIds);
        officeDoneInfo.setAllUserId(allUserId);
    }

    private void handleError(String tenantId, String taskId, String processInstanceId, Exception e, String errorField) {
        final Writer result = new StringWriter();
        final PrintWriter print = new PrintWriter(result);
        e.printStackTrace(print);
        String msg = result.toString();
        String time = Y9DateTimeUtils.formatCurrentDateTime();
        ErrorLogModel errorLogModel = new ErrorLogModel();
        errorLogModel.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        errorLogModel.setErrorFlag(ErrorLogModel.ERROR_FLAG_SAVE_OFFICE_DONE + "4Search2");
        errorLogModel.setErrorType(ErrorLogModel.ERROR_PROCESS_INSTANCE);
        errorLogModel.setExtendField(errorField);
        errorLogModel.setProcessInstanceId(processInstanceId);
        errorLogModel.setTaskId(taskId);
        errorLogModel.setText(msg);
        try {
            errorLogApi.saveErrorLog(tenantId, errorLogModel);
        } catch (Exception e1) {
            LOGGER.warn("#################保存错误日志失败#################", e1);
        }
        LOGGER.warn("#################保存办结件数据到数据中心失败#################", e);
    }

}
