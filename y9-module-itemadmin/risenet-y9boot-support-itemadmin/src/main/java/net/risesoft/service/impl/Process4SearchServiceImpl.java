package net.risesoft.service.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.entity.ErrorLog;
import net.risesoft.entity.ProcessParam;
import net.risesoft.enums.DialectEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.ErrorLogModel;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.nosql.elastic.entity.OfficeDoneInfo;
import net.risesoft.service.ErrorLogService;
import net.risesoft.service.OfficeDoneInfoService;
import net.risesoft.service.Process4SearchService;
import net.risesoft.util.Y9DateTimeUtils;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.sqlddl.DbMetaDataUtil;
import net.risesoft.y9.util.Y9Util;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class Process4SearchServiceImpl implements Process4SearchService {

    private final OfficeDoneInfoService officeDoneInfoService;

    private final OrgUnitApi orgUnitApi;

    private final ErrorLogService errorLogService;

    @Resource(name = "jdbcTemplate4Tenant")
    private JdbcTemplate jdbcTemplate;

    @Override
    public void saveToDataCenter(final String tenantId, final ProcessParam processParam, final OrgUnit orgUnit) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setOrgUnit(orgUnit);
        String processInstanceId = processParam.getProcessInstanceId();
        try {
            // 获取流程实例基本信息
            ProcessInstanceInfo processInfo = getProcessInstanceInfo(processInstanceId);
            // 构建办件信息
            OfficeDoneInfo officeDoneInfo = buildOfficeDoneInfo(tenantId, processParam, orgUnit, processInfo);
            // 保存到数据中心
            officeDoneInfoService.saveOfficeDone(officeDoneInfo);
        } catch (Exception e) {
            handleSaveDataCenterException(e, processInstanceId, "启动流程保存流程信息失败", "4Search1");
        }
    }

    /**
     * 获取流程实例基本信息
     */
    private ProcessInstanceInfo getProcessInstanceInfo(String processInstanceId) {
        String sql =
            "SELECT P.PROC_INST_ID_, TO_CHAR(P.START_TIME_,'yyyy-MM-dd HH:mi:ss') as START_TIME_, P.PROC_DEF_ID_"
                + " FROM ACT_HI_PROCINST P WHERE P.PROC_INST_ID_ = ?";

        DataSource dataSource = jdbcTemplate.getDataSource();
        String dialectName = DbMetaDataUtil.getDatabaseDialectName(dataSource);
        if (DialectEnum.MYSQL.getValue().equals(dialectName) || DialectEnum.KINGBASE.getValue().equals(dialectName)) {
            sql = "SELECT P.PROC_INST_ID_, SUBSTRING(P.START_TIME_,1,19) as START_TIME_, P.PROC_DEF_ID_"
                + " FROM ACT_HI_PROCINST P WHERE P.PROC_INST_ID_ = ?";
        }

        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, processInstanceId);
        Map<String, Object> map = list.get(0);

        ProcessInstanceInfo info = new ProcessInstanceInfo();
        info.processDefinitionId = (String)map.get("PROC_DEF_ID_");
        info.startTime = (String)map.get("START_TIME_");
        return info;
    }

    /**
     * 构建办件信息对象
     */
    private OfficeDoneInfo buildOfficeDoneInfo(String tenantId, ProcessParam processParam, OrgUnit orgUnit,
        ProcessInstanceInfo processInfo) {
        OfficeDoneInfo officeDoneInfo = new OfficeDoneInfo();
        officeDoneInfo.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        // 设置流程参数相关信息
        setProcessParamInfo(officeDoneInfo, processParam);
        // 设置组织机构信息
        setOrganizationInfo(officeDoneInfo, tenantId, orgUnit);
        // 设置流程实例信息
        officeDoneInfo.setProcessDefinitionId(processInfo.processDefinitionId);
        officeDoneInfo.setProcessDefinitionKey(processInfo.processDefinitionId.split(":")[0]);
        officeDoneInfo.setProcessInstanceId(processParam.getProcessInstanceId());
        officeDoneInfo.setStartTime(processInfo.startTime);
        officeDoneInfo.setTenantId(tenantId);
        officeDoneInfo.setTarget(processParam.getTarget());

        return officeDoneInfo;
    }

    /**
     * 设置流程参数相关信息
     */
    private void setProcessParamInfo(OfficeDoneInfo officeDoneInfo, ProcessParam processParam) {
        if (StringUtils.isNotBlank(processParam.getId())) {
            officeDoneInfo.setDocNumber(
                StringUtils.isNotBlank(processParam.getCustomNumber()) ? processParam.getCustomNumber() : "");
            officeDoneInfo.setItemId(StringUtils.isNotBlank(processParam.getItemId()) ? processParam.getItemId() : "");
            officeDoneInfo
                .setItemName(StringUtils.isNotBlank(processParam.getItemName()) ? processParam.getItemName() : "");
            officeDoneInfo.setProcessSerialNumber(StringUtils.isNotBlank(processParam.getProcessSerialNumber())
                ? processParam.getProcessSerialNumber() : "");
            officeDoneInfo.setSystemCnName(
                StringUtils.isNotBlank(processParam.getSystemCnName()) ? processParam.getSystemCnName() : "");
            officeDoneInfo.setSystemName(
                StringUtils.isNotBlank(processParam.getSystemName()) ? processParam.getSystemName() : "");
            officeDoneInfo.setTitle(StringUtils.isNotBlank(processParam.getTitle()) ? processParam.getTitle() : "");
            officeDoneInfo
                .setUrgency(StringUtils.isNotBlank(processParam.getCustomLevel()) ? processParam.getCustomLevel() : "");
            officeDoneInfo
                .setCreatUserId(StringUtils.isNotBlank(processParam.getStartor()) ? processParam.getStartor() : "");
            officeDoneInfo.setCreatUserName(
                StringUtils.isNotBlank(processParam.getStartorName()) ? processParam.getStartorName() : "");
        }
        officeDoneInfo.setUserComplete("");
        officeDoneInfo.setAllUserId(processParam.getStartor());
        officeDoneInfo.setEndTime(null);
        officeDoneInfo.setEntrustUserId("");
    }

    /**
     * 设置组织机构信息
     */
    private void setOrganizationInfo(OfficeDoneInfo officeDoneInfo, String tenantId, OrgUnit orgUnit) {
        try {
            OrgUnit bureau = orgUnitApi.getBureau(tenantId, orgUnit.getParentId()).getData();
            officeDoneInfo.setBureauId(bureau != null ? bureau.getId() : "");
            officeDoneInfo.setDeptId(orgUnit.getParentId());

            OrgUnit dept = orgUnitApi.getOrgUnit(tenantId, orgUnit.getParentId()).getData();
            officeDoneInfo.setDeptName(dept != null ? dept.getName() : "");
        } catch (Exception e) {
            LOGGER.warn("获取组织机构信息失败", e);
        }
    }

    /**
     * 处理保存数据中心异常
     */
    private void handleSaveDataCenterException(Exception e, String processInstanceId, String errorMsg,
        String errorFlag) {
        final Writer result = new StringWriter();
        final PrintWriter print = new PrintWriter(result);
        e.printStackTrace(print);
        String msg = result.toString();
        String time = Y9DateTimeUtils.formatCurrentDateTime();

        ErrorLog errorLogModel = new ErrorLog();
        errorLogModel.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        errorLogModel.setCreateTime(time);
        errorLogModel.setErrorFlag(ErrorLogModel.ERROR_FLAG_SAVE_OFFICE_DONE + errorFlag);
        errorLogModel.setErrorType(ErrorLogModel.ERROR_PROCESS_INSTANCE);
        errorLogModel.setExtendField(errorMsg);
        errorLogModel.setProcessInstanceId(processInstanceId);
        errorLogModel.setTaskId("");
        errorLogModel.setText(msg);
        errorLogModel.setUpdateTime(time);
        try {
            errorLogService.saveErrorLog(errorLogModel);
        } catch (Exception e1) {
            LOGGER.warn("保存错误日志失败", e1);
        }
        LOGGER.warn("#################保存办结件数据到数据中心失败#################", e);
    }

    @Async
    @Override
    public void saveToDataCenter1(final String tenantId, final String taskId, final ProcessParam processParam) {
        Y9LoginUserHolder.setTenantId(tenantId);
        String processInstanceId = processParam.getProcessInstanceId();
        try {
            // 获取或创建办件信息
            OfficeDoneInfo officeDoneInfo = getOrCreateOfficeDoneInfo(tenantId, processInstanceId, processParam);
            // 设置办件基本信息
            setOfficeDoneBasicInfo(officeDoneInfo, processParam);
            // 处理参与人信息
            handleParticipantInfo(officeDoneInfo, tenantId, processInstanceId);
            // 保存到数据中心
            officeDoneInfoService.saveOfficeDone(officeDoneInfo);
        } catch (Exception e) {
            handleSaveDataCenterException(e, processInstanceId, "发送保存流程信息失败", "4Search2");
        }
    }

    /**
     * 获取或创建办件信息
     */
    private OfficeDoneInfo getOrCreateOfficeDoneInfo(String tenantId, String processInstanceId,
        ProcessParam processParam) {
        OfficeDoneInfo officeDoneInfo = officeDoneInfoService.findByProcessInstanceId(processInstanceId);
        if (officeDoneInfo == null) {
            // 复用已有的方法获取流程实例信息
            ProcessInstanceInfo processInfo = getProcessInstanceInfo(processInstanceId);
            officeDoneInfo = new OfficeDoneInfo();
            officeDoneInfo.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            // 设置流程参数信息
            setProcessParamInfo(officeDoneInfo, processParam);
            // 设置流程实例信息
            officeDoneInfo.setStartTime(processInfo.startTime);
            officeDoneInfo.setProcessDefinitionId(processInfo.processDefinitionId);
            officeDoneInfo.setProcessDefinitionKey(processInfo.processDefinitionId.split(":")[0]);
            officeDoneInfo.setProcessInstanceId(processInstanceId);
            officeDoneInfo.setTenantId(tenantId);
        }
        return officeDoneInfo;
    }

    /**
     * 设置办件基本信息
     */
    private void setOfficeDoneBasicInfo(OfficeDoneInfo officeDoneInfo, ProcessParam processParam) {
        officeDoneInfo.setTitle(StringUtils.isNotBlank(processParam.getTitle()) ? processParam.getTitle() : "");
        officeDoneInfo
            .setUrgency(StringUtils.isNotBlank(processParam.getCustomLevel()) ? processParam.getCustomLevel() : "");
        officeDoneInfo.setUserComplete("");
        officeDoneInfo.setBureauId(processParam.getBureauIds());
        officeDoneInfo.setEndTime(null);
        officeDoneInfo.setTarget(processParam.getTarget());
    }

    /**
     * 处理参与人信息
     */
    private void handleParticipantInfo(OfficeDoneInfo officeDoneInfo, String tenantId, String processInstanceId) {
        String sql = "SELECT i.USER_ID_ from ACT_HI_IDENTITYLINK i where i.PROC_INST_ID_ = ?";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, processInstanceId);
        String allUserId = "";
        String deptIds = "";

        for (Map<String, Object> m : list) {
            String userId = m.get("USER_ID_") != null ? (String)m.get("USER_ID_") : "";
            if (userId.contains(":")) {
                userId = userId.split(":")[0];
            }
            if (StringUtils.isNotEmpty(userId) && !allUserId.contains(userId)) {
                allUserId = Y9Util.genCustomStr(allUserId, userId);
            }
            if (StringUtils.isNotEmpty(userId)) {
                OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, userId).getData();
                if (orgUnit != null && orgUnit.getId() != null) {
                    if (!deptIds.contains(orgUnit.getParentId())) {
                        deptIds = Y9Util.genCustomStr(deptIds, orgUnit.getParentId());
                    }
                }
            }
        }
        officeDoneInfo.setDeptId(deptIds);
        officeDoneInfo.setAllUserId(allUserId);
    }

    // 内部类用于封装流程实例信息
    private static class ProcessInstanceInfo {
        String processDefinitionId;
        String startTime;
    }
}
