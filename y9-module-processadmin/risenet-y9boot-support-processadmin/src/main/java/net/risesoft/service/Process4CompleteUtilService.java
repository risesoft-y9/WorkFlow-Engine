package net.risesoft.service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.DataCenterApi;
import net.risesoft.api.itemadmin.ErrorLogApi;
import net.risesoft.api.itemadmin.OfficeDoneInfoApi;
import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.enums.DialectEnum;
import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.ErrorLogModel;
import net.risesoft.model.itemadmin.OfficeDoneInfoModel;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.FlowableTenantInfoHolder;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9.sqlddl.DbMetaDataUtil;
import net.risesoft.y9.util.Y9Util;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@EnableAsync
@Service(value = "process4CompleteUtilService")
@Slf4j
public class Process4CompleteUtilService {

    private final OfficeDoneInfoApi officeDoneInfoApi;

    private final JdbcTemplate jdbcTemplate;

    private final DataCenterApi dataCenterApi;

    private final OrgUnitApi orgUnitApi;

    private final ProcessParamApi processParamApi;

    private final ErrorLogApi errorLogApi;

    private final Y9Properties y9Conf;

    private final Process4MsgRemindService process4MsgRemindService;

    public Process4CompleteUtilService(@Qualifier("jdbcTemplate4Tenant") JdbcTemplate jdbcTemplate,
        OfficeDoneInfoApi officeDoneInfoApi, DataCenterApi dataCenterApi, OrgUnitApi orgUnitApi,
        ProcessParamApi processParamApi, ErrorLogApi errorLogApi, Y9Properties y9Conf,
        Process4MsgRemindService process4MsgRemindService) {
        this.officeDoneInfoApi = officeDoneInfoApi;
        this.jdbcTemplate = jdbcTemplate;
        this.dataCenterApi = dataCenterApi;
        this.orgUnitApi = orgUnitApi;
        this.processParamApi = processParamApi;
        this.errorLogApi = errorLogApi;
        this.y9Conf = y9Conf;
        this.process4MsgRemindService = process4MsgRemindService;
    }

    /**
     * 删除历史数据
     *
     * @param processInstanceId 流程实例id
     */
    public void deleteDoneData(String processInstanceId) {
        String sql3 = "DELETE from ACT_HI_TASKINST where PROC_INST_ID_ = '" + processInstanceId + "'";
        jdbcTemplate.execute(sql3);

        sql3 = "DELETE" + " FROM" + "	ACT_GE_BYTEARRAY" + " WHERE" + "	ID_ IN (" + "		SELECT" + "			*"
            + "		FROM ( " + "         SELECT" + "			b.ID_" + "		  FROM" + "			 ACT_GE_BYTEARRAY b"
            + "		  LEFT JOIN ACT_HI_VARINST v ON v.BYTEARRAY_ID_ = b.ID_" + "		  WHERE"
            + "			 v.PROC_INST_ID_ = '" + processInstanceId + "'" + "		  AND v.NAME_ = 'users'" + "       ) TT"
            + "	)";
        jdbcTemplate.execute(sql3);

        sql3 = "DELETE from ACT_HI_VARINST where PROC_INST_ID_ = '" + processInstanceId + "'";
        jdbcTemplate.execute(sql3);

        sql3 = "DELETE from ACT_HI_IDENTITYLINK where PROC_INST_ID_ = '" + processInstanceId + "'";
        jdbcTemplate.execute(sql3);

        sql3 = "DELETE from ACT_HI_ACTINST where PROC_INST_ID_ = '" + processInstanceId + "'";
        jdbcTemplate.execute(sql3);

        sql3 = "DELETE from ACT_HI_PROCINST where PROC_INST_ID_ = '" + processInstanceId + "'";
        jdbcTemplate.execute(sql3);
    }

    private final String getActGeBytearraySql(String year, String processInstanceId) {
        String sql = "INSERT INTO ACT_GE_BYTEARRAY_" + year + " (" + "	ID_," + "	REV_," + "	NAME_,"
            + "	DEPLOYMENT_ID_," + "	BYTES_," + "	GENERATED_" + " ) SELECT " + "	b.ID_," + "	b.REV_,"
            + "	b.NAME_," + "	b.DEPLOYMENT_ID_," + "	b.BYTES_," + "	b.GENERATED_" + " FROM" + "	ACT_GE_BYTEARRAY b"
            + " LEFT JOIN ACT_HI_VARINST v ON v.BYTEARRAY_ID_ = b.ID_" + " WHERE" + "	v.PROC_INST_ID_ = '"
            + processInstanceId + "'" + " AND v.NAME_ = 'users'";
        return sql;
    }

    private final String getActHiActinstSql(String year, String processInstanceId) {
        String sql3 = "INSERT INTO ACT_HI_ACTINST_" + year + " (" + "	ID_," + "	REV_," + "	PROC_DEF_ID_,"
            + "	PROC_INST_ID_," + "	EXECUTION_ID_," + "	ACT_ID_," + "	TASK_ID_," + "	CALL_PROC_INST_ID_,"
            + "	ACT_NAME_," + "	ACT_TYPE_," + "	ASSIGNEE_," + "	START_TIME_," + "	END_TIME_," + "	DURATION_,"
            + "	DELETE_REASON_," + "	TENANT_ID_" + " ) SELECT" + "	ID_," + "	REV_," + "	PROC_DEF_ID_,"
            + "	PROC_INST_ID_," + "	EXECUTION_ID_," + "	ACT_ID_," + "	TASK_ID_," + "	CALL_PROC_INST_ID_,"
            + "	ACT_NAME_," + "	ACT_TYPE_," + "	ASSIGNEE_," + "	START_TIME_," + "	END_TIME_," + "	DURATION_,"
            + "	DELETE_REASON_," + "	TENANT_ID_" + " FROM" + "	ACT_HI_ACTINST A" + " WHERE"
            + "	A.PROC_INST_ID_ = '" + processInstanceId + "'";
        return sql3;
    }

    private final String getActHiIdentiyLinkSql(String year, String processInstanceId) {
        String sql3 = "INSERT INTO ACT_HI_IDENTITYLINK_" + year + " (" + "	ID_," + "	GROUP_ID_," + "	TYPE_,"
            + "	USER_ID_," + "	TASK_ID_," + "	CREATE_TIME_," + "	PROC_INST_ID_," + "	SCOPE_ID_," + "	SCOPE_TYPE_,"
            + "	SCOPE_DEFINITION_ID_" + " ) SELECT" + "	ID_," + "	GROUP_ID_," + "	TYPE_," + "	USER_ID_,"
            + "	TASK_ID_," + "	CREATE_TIME_," + "	PROC_INST_ID_," + "	SCOPE_ID_," + "	SCOPE_TYPE_,"
            + "	SCOPE_DEFINITION_ID_" + " FROM" + "	ACT_HI_IDENTITYLINK i" + " WHERE" + "	i.PROC_INST_ID_ = '"
            + processInstanceId + "'";
        return sql3;
    }

    private final String getActHiProcinstSql(String year, String processInstanceId) {
        String sql = "INSERT INTO ACT_HI_PROCINST_" + year + " (" + "	ID_," + "	REV_," + "	PROC_INST_ID_,"
            + "	BUSINESS_KEY_," + "	PROC_DEF_ID_," + "	START_TIME_," + "	END_TIME_," + "	DURATION_,"
            + "	START_USER_ID_," + "	START_ACT_ID_," + "	END_ACT_ID_," + "	SUPER_PROCESS_INSTANCE_ID_,"
            + "	DELETE_REASON_," + "	TENANT_ID_," + "	NAME_," + "	CALLBACK_ID_," + "	CALLBACK_TYPE_" + ") SELECT"
            + "	ID_," + "	REV_," + "	PROC_INST_ID_," + "	BUSINESS_KEY_," + "	PROC_DEF_ID_," + "	START_TIME_,"
            + "	END_TIME_," + "	DURATION_," + "	START_USER_ID_," + "	START_ACT_ID_," + "	END_ACT_ID_,"
            + "	SUPER_PROCESS_INSTANCE_ID_," + "	DELETE_REASON_," + "	TENANT_ID_," + "	NAME_,"
            + "	CALLBACK_ID_," + "	CALLBACK_TYPE_" + " FROM" + "	ACT_HI_PROCINST RES" + " WHERE"
            + "	RES.PROC_INST_ID_ = '" + processInstanceId + "'";
        return sql;
    }

    private final String getActHiTaskinstSql(String year, String processInstanceId) {
        String sql = "INSERT INTO ACT_HI_TASKINST_" + year + " (" + "	ID_," + "	REV_," + "	PROC_DEF_ID_,"
            + "	TASK_DEF_ID_," + "	TASK_DEF_KEY_," + "	PROC_INST_ID_," + "	EXECUTION_ID_," + "	SCOPE_ID_,"
            + "	SUB_SCOPE_ID_," + "	SCOPE_TYPE_," + "	SCOPE_DEFINITION_ID_," + "	PARENT_TASK_ID_," + "	NAME_,"
            + "	DESCRIPTION_," + "	OWNER_," + "	ASSIGNEE_," + "	START_TIME_," + "	CLAIM_TIME_," + "	END_TIME_,"
            + "	DURATION_," + "	DELETE_REASON_," + "	PRIORITY_," + "	DUE_DATE_," + "	FORM_KEY_," + "	CATEGORY_,"
            + "	TENANT_ID_," + "	LAST_UPDATED_TIME_" + " ) SELECT" + "	ID_," + "	REV_," + "	PROC_DEF_ID_,"
            + "	TASK_DEF_ID_," + "	TASK_DEF_KEY_," + "	PROC_INST_ID_," + "	EXECUTION_ID_," + "	SCOPE_ID_,"
            + "	SUB_SCOPE_ID_," + "	SCOPE_TYPE_," + "	SCOPE_DEFINITION_ID_," + "	PARENT_TASK_ID_," + "	NAME_,"
            + "	DESCRIPTION_," + "	OWNER_," + "	ASSIGNEE_," + "	START_TIME_," + "	CLAIM_TIME_," + "	END_TIME_,"
            + "	DURATION_," + "	DELETE_REASON_," + "	PRIORITY_," + "	DUE_DATE_," + "	FORM_KEY_," + "	CATEGORY_,"
            + "	TENANT_ID_," + "	LAST_UPDATED_TIME_" + " FROM" + "	ACT_HI_TASKINST T" + " WHERE"
            + "	T .PROC_INST_ID_ = '" + processInstanceId + "'";
        return sql;
    }

    private final String getActHiVarinstSql(String year, String processInstanceId) {
        String sql3 = "INSERT INTO ACT_HI_VARINST_" + year + " (" + "	ID_," + "	REV_," + "	PROC_INST_ID_,"
            + "	EXECUTION_ID_," + "	TASK_ID_," + "	NAME_," + "	VAR_TYPE_," + "	SCOPE_ID_," + "	SUB_SCOPE_ID_,"
            + "	SCOPE_TYPE_," + "	BYTEARRAY_ID_," + "	DOUBLE_," + "	LONG_," + "	TEXT_," + "	TEXT2_,"
            + "	CREATE_TIME_," + "	LAST_UPDATED_TIME_" + " ) SELECT" + "	ID_," + "	REV_," + "	PROC_INST_ID_,"
            + "	EXECUTION_ID_," + "	TASK_ID_," + "	NAME_," + "	VAR_TYPE_," + "	SCOPE_ID_," + "	SUB_SCOPE_ID_,"
            + "	SCOPE_TYPE_," + "	BYTEARRAY_ID_," + "	DOUBLE_," + "	LONG_," + "	TEXT_," + "	TEXT2_,"
            + "	CREATE_TIME_," + "	LAST_UPDATED_TIME_" + " FROM" + "	ACT_HI_VARINST v" + " WHERE"
            + "	v.PROC_INST_ID_ = '" + processInstanceId
            + "' and v.NAME_ not in ('nrOfActiveInstances','nrOfCompletedInstances','nrOfInstances','loopCounter','elementUser')";
        return sql3;
    }

    /**
     * 保存数据到数据中心，截转年度数据
     *
     * @param tenantId
     * @param year
     * @param userId
     * @param processInstanceId
     * @param personName
     */
    @Async
    public void saveToDataCenter(final String tenantId, final String year, final String userId,
        final String processInstanceId, final String personName) {
        Y9LoginUserHolder.setTenantId(tenantId);
        FlowableTenantInfoHolder.setTenantId(tenantId);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            ProcessParamModel processParamModel =
                processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
            try {
                String sql0 = "UPDATE ff_process_param f set f.COMPLETER = '" + personName
                    + "' where f.PROCESSINSTANCEID = '" + processInstanceId + "'";
                jdbcTemplate.execute(sql0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 消息提醒
            process4MsgRemindService.processComplete(processParamModel, personName);

            String sql0 = "SELECT" + "	P .PROC_INST_ID_,TO_CHAR(P .START_TIME_,'yyyy-MM-dd HH:mi:ss') as START_TIME_,"
                + " TO_CHAR(P .END_TIME_,'yyyy-MM-dd HH:mi:ss') as END_TIME_,P.PROC_DEF_ID_" + " FROM"
                + "	ACT_HI_PROCINST P" + " WHERE" + " P.PROC_INST_ID_ = '" + processInstanceId + "'";
            DataSource dataSource = jdbcTemplate.getDataSource();
            String dialectName = DbMetaDataUtil.getDatabaseDialectName(dataSource);
            if (DialectEnum.MYSQL.getValue().equals(dialectName)
                || DialectEnum.KINGBASE.getValue().equals(dialectName)) {
                sql0 = "SELECT"
                    + "	P .PROC_INST_ID_,SUBSTRING(P.START_TIME_,1,19) as START_TIME_,SUBSTRING(P .END_TIME_,1,19) as END_TIME_,P.PROC_DEF_ID_"
                    + " FROM" + "	ACT_HI_PROCINST P" + " WHERE" + " P.PROC_INST_ID_ = '" + processInstanceId + "'";
            }
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql0);
            Map<String, Object> map = list.get(0);
            String processDefinitionId = (String)map.get("PROC_DEF_ID_");
            String startTime = (String)map.get("START_TIME_");
            String endTime = map.get("END_TIME_") != null ? (String)map.get("END_TIME_") : sdf.format(new Date());
            try {
                Boolean dataCenterSwitch = y9Conf.getApp().getProcessAdmin().getDataCenterSwitch();
                if (dataCenterSwitch != null && dataCenterSwitch) {
                    Y9Result<Object> y9Result = dataCenterApi.saveToDateCenter(processInstanceId, tenantId, userId);
                    if (y9Result.isSuccess()) {
                        LOGGER
                            .info("#################保存办结数据到数据中心成功：2-HISTORIC_PROCESS_INSTANCE_ENDED#################");
                    } else {
                        LOGGER
                            .info("#################保存办结数据到数据中心失败：2-HISTORIC_PROCESS_INSTANCE_ENDED#################");
                    }
                } else {
                    LOGGER.info("######################数据中心开关已关闭,如需保存数据到数据中心请更改配置文件######################");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            /**********************************
             * 保存办结数据到数据中心，用于办结件列表查询
             *********************************************/

            OfficeDoneInfoModel officeDoneInfo = new OfficeDoneInfoModel();
            officeDoneInfo.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            if (processParamModel != null && StringUtils.isNotBlank(processParamModel.getId())) {
                // ----------------------------------------------------数据中心办结信息
                officeDoneInfo.setBureauId(
                    StringUtils.isNotBlank(processParamModel.getBureauIds()) ? processParamModel.getBureauIds() : "");
                officeDoneInfo.setDeptId(
                    StringUtils.isNotBlank(processParamModel.getDeptIds()) ? processParamModel.getDeptIds() : "");
                officeDoneInfo.setDocNumber(StringUtils.isNotBlank(processParamModel.getCustomNumber())
                    ? processParamModel.getCustomNumber() : "");
                officeDoneInfo.setItemId(
                    StringUtils.isNotBlank(processParamModel.getItemId()) ? processParamModel.getItemId() : "");
                officeDoneInfo.setItemName(
                    StringUtils.isNotBlank(processParamModel.getItemName()) ? processParamModel.getItemName() : "");
                officeDoneInfo.setProcessSerialNumber(StringUtils.isNotBlank(processParamModel.getProcessSerialNumber())
                    ? processParamModel.getProcessSerialNumber() : "");
                officeDoneInfo.setSystemCnName(StringUtils.isNotBlank(processParamModel.getSystemCnName())
                    ? processParamModel.getSystemCnName() : "");
                officeDoneInfo.setSystemName(
                    StringUtils.isNotBlank(processParamModel.getSystemName()) ? processParamModel.getSystemName() : "");
                officeDoneInfo
                    .setTitle(StringUtils.isNotBlank(processParamModel.getTitle()) ? processParamModel.getTitle() : "");
                officeDoneInfo.setUrgency(StringUtils.isNotBlank(processParamModel.getCustomLevel())
                    ? processParamModel.getCustomLevel() : "");
                officeDoneInfo.setUserComplete(personName);
                officeDoneInfo.setCreatUserId(
                    StringUtils.isNotBlank(processParamModel.getStartor()) ? processParamModel.getStartor() : "");
                officeDoneInfo.setCreatUserName(StringUtils.isNotBlank(processParamModel.getStartorName())
                    ? processParamModel.getStartorName() : "");
                officeDoneInfo
                    .setTarget(StringUtils.isBlank(processParamModel.getTarget()) ? "" : processParamModel.getTarget());
            }
            // 处理参与人
            String sql =
                "SELECT i.USER_ID_ from ACT_HI_IDENTITYLINK i where i.PROC_INST_ID_ = '" + processInstanceId + "'";
            List<Map<String, Object>> list3 = jdbcTemplate.queryForList(sql);
            String allUserId = "";
            for (Map<String, Object> m : list3) {
                String ownerId = m.get("USER_ID_") != null ? (String)m.get("USER_ID_") : "";
                if (!"".equals(ownerId) && !allUserId.contains(ownerId)) {
                    allUserId = Y9Util.genCustomStr(allUserId, ownerId);
                }
            }
            officeDoneInfo.setAllUserId(allUserId);
            officeDoneInfo.setEndTime(endTime);
            officeDoneInfo.setProcessDefinitionId(processDefinitionId);
            officeDoneInfo.setProcessDefinitionKey(processDefinitionId.split(":")[0]);
            officeDoneInfo.setProcessInstanceId(processInstanceId);
            officeDoneInfo.setStartTime(startTime);
            officeDoneInfo.setTenantId(tenantId);
            OrgUnit orgUnit = orgUnitApi.getOrgUnit(tenantId, userId).getData();
            if (orgUnit.getOrgType().equals(OrgTypeEnum.POSITION)) {
                officeDoneInfoApi.saveOfficeDone(tenantId, officeDoneInfo);
            }
            String year0 = year;
            if (StringUtils.isBlank(year)) {
                year0 = startTime.substring(0, 4);
            }
            Thread.sleep(3000);// 延时3秒执行截转和删除数据
            this.saveYearData(year0, processInstanceId);
            this.deleteDoneData(processInstanceId);
            LOGGER.info("#################保存办结件数据到数据中心成功#################");
        } catch (Exception e) {
            final Writer result = new StringWriter();
            final PrintWriter print = new PrintWriter(result);
            e.printStackTrace(print);
            String msg = result.toString();
            String time = sdf.format(new Date());
            ErrorLogModel errorLogModel = new ErrorLogModel();
            errorLogModel.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            errorLogModel.setCreateTime(time);
            errorLogModel.setErrorFlag(ErrorLogModel.ERROR_FLAG_SAVE_OFFICE_DONE);
            errorLogModel.setErrorType(ErrorLogModel.ERROR_PROCESS_INSTANCE);
            errorLogModel.setExtendField("办结截转数据失败");
            errorLogModel.setProcessInstanceId(processInstanceId);
            errorLogModel.setTaskId("");
            errorLogModel.setText(msg);
            errorLogModel.setUpdateTime(time);
            try {
                errorLogApi.saveErrorLog(tenantId, errorLogModel);
            } catch (Exception e1) {
                LOGGER.error("保存错误日志失败", e1);
            }
            LOGGER.warn("#################保存办结件数据到数据中心失败#################", e);
        }
    }

    /**
     * 办结保存年度历史数据
     *
     * @param year 年度
     * @param processInstanceId 流程实例ID
     */
    public void saveYearData(String year, String processInstanceId) {
        String sql3 = getActHiTaskinstSql(year, processInstanceId);
        jdbcTemplate.execute(sql3);

        sql3 = getActHiVarinstSql(year, processInstanceId);
        jdbcTemplate.execute(sql3);

        sql3 = getActGeBytearraySql(year, processInstanceId);
        jdbcTemplate.execute(sql3);

        sql3 = getActHiIdentiyLinkSql(year, processInstanceId);
        jdbcTemplate.execute(sql3);

        sql3 = getActHiActinstSql(year, processInstanceId);
        jdbcTemplate.execute(sql3);

        sql3 = getActHiProcinstSql(year, processInstanceId);
        jdbcTemplate.execute(sql3);
    }

}
