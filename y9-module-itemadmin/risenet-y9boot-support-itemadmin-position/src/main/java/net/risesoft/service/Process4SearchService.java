package net.risesoft.service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.platform.org.DepartmentApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.entity.ErrorLog;
import net.risesoft.entity.ProcessParam;
import net.risesoft.enums.DialectEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.ErrorLogModel;
import net.risesoft.model.platform.Department;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Position;
import net.risesoft.nosql.elastic.entity.OfficeDoneInfo;
import net.risesoft.util.form.DbMetaDataUtil;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9Util;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@EnableAsync
@Service(value = "process4SearchService")
@Slf4j
@RequiredArgsConstructor
public class Process4SearchService {

    private final OfficeDoneInfoService officeDoneInfoService;

    private final PositionApi positionManager;

    private final DepartmentApi departmentManager;

    private final OrgUnitApi orgUnitApi;

    private final ErrorLogService errorLogService;

    @Resource(name = "jdbcTemplate4Tenant")
    private JdbcTemplate jdbcTemplate;

    /**
     * 启动流程保存流程信息
     *
     * @param tenantId
     * @param processParam
     * @param position
     */
    public void saveToDataCenter(final String tenantId, final ProcessParam processParam, final Position position) {
        Y9LoginUserHolder.setTenantId(tenantId);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String processInstanceId = processParam.getProcessInstanceId();
        Connection connection = null;
        try {
            String sql0 = "SELECT" + "	P .PROC_INST_ID_,"
                + "	TO_CHAR(P .START_TIME_,'yyyy-MM-dd HH:mi:ss') as START_TIME_," + "	P .PROC_DEF_ID_" + " FROM"
                + "	ACT_HI_PROCINST P" + " WHERE" + "	P .PROC_INST_ID_ = '" + processInstanceId + "'";
            DataSource dataSource = jdbcTemplate.getDataSource();
            DbMetaDataUtil dbMetaDataUtil = new DbMetaDataUtil();
            connection = dataSource.getConnection();
            String dialectName = dbMetaDataUtil.getDatabaseDialectName(connection);
            if (DialectEnum.MYSQL.getValue().equals(dialectName)) {
                sql0 = "SELECT" + "	P .PROC_INST_ID_,SUBSTRING(P.START_TIME_,1,19) as START_TIME_,P.PROC_DEF_ID_"
                    + " FROM" + "	ACT_HI_PROCINST P" + " WHERE" + " P.PROC_INST_ID_ = '" + processInstanceId + "'";
            }
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql0);
            Map<String, Object> map = list.get(0);
            String processDefinitionId = (String)map.get("PROC_DEF_ID_");
            String startTime = (String)map.get("START_TIME_");

            /**********************************
             * 保存流程数据到数据中心，用于综合搜索列表查询
             *********************************************/
            OfficeDoneInfo officeDoneInfo = new OfficeDoneInfo();
            officeDoneInfo.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            if (processParam != null && StringUtils.isNotBlank(processParam.getId())) {
                officeDoneInfo.setDocNumber(
                    StringUtils.isNotBlank(processParam.getCustomNumber()) ? processParam.getCustomNumber() : "");
                officeDoneInfo
                    .setItemId(StringUtils.isNotBlank(processParam.getItemId()) ? processParam.getItemId() : "");
                officeDoneInfo
                    .setItemName(StringUtils.isNotBlank(processParam.getItemName()) ? processParam.getItemName() : "");
                officeDoneInfo.setProcessSerialNumber(StringUtils.isNotBlank(processParam.getProcessSerialNumber())
                    ? processParam.getProcessSerialNumber() : "");
                officeDoneInfo.setSystemCnName(
                    StringUtils.isNotBlank(processParam.getSystemCnName()) ? processParam.getSystemCnName() : "");
                officeDoneInfo.setSystemName(
                    StringUtils.isNotBlank(processParam.getSystemName()) ? processParam.getSystemName() : "");
                officeDoneInfo.setTitle(StringUtils.isNotBlank(processParam.getTitle()) ? processParam.getTitle() : "");
                officeDoneInfo.setUrgency(
                    StringUtils.isNotBlank(processParam.getCustomLevel()) ? processParam.getCustomLevel() : "");
                officeDoneInfo
                    .setCreatUserId(StringUtils.isNotBlank(processParam.getStartor()) ? processParam.getStartor() : "");
                officeDoneInfo.setCreatUserName(
                    StringUtils.isNotBlank(processParam.getStartorName()) ? processParam.getStartorName() : "");
            }
            officeDoneInfo.setUserComplete("");
            OrgUnit bureau = orgUnitApi.getBureau(tenantId, position.getParentId()).getData();
            officeDoneInfo.setBureauId(bureau != null ? bureau.getId() : "");
            officeDoneInfo.setDeptId(position.getParentId());
            Department dept = departmentManager.get(tenantId, position.getParentId()).getData();
            officeDoneInfo.setDeptName(dept != null ? dept.getName() : "");
            officeDoneInfo.setEntrustUserId("");
            officeDoneInfo.setAllUserId(processParam.getStartor());
            officeDoneInfo.setEndTime(null);
            officeDoneInfo.setProcessDefinitionId(processDefinitionId);
            officeDoneInfo.setProcessDefinitionKey(processDefinitionId.split(":")[0]);
            officeDoneInfo.setProcessInstanceId(processInstanceId);
            officeDoneInfo.setStartTime(startTime);
            officeDoneInfo.setTenantId(tenantId);
            officeDoneInfoService.saveOfficeDone(officeDoneInfo);
        } catch (Exception e) {
            final Writer result = new StringWriter();
            final PrintWriter print = new PrintWriter(result);
            e.printStackTrace(print);
            String msg = result.toString();
            String time = sdf.format(new Date());
            ErrorLog errorLogModel = new ErrorLog();
            errorLogModel.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            errorLogModel.setCreateTime(time);
            errorLogModel.setErrorFlag(ErrorLogModel.ERROR_FLAG_SAVE_OFFICE_DONE + "4Search1");
            errorLogModel.setErrorType(ErrorLogModel.ERROR_PROCESS_INSTANCE);
            errorLogModel.setExtendField("启动流程保存流程信息失败");
            errorLogModel.setProcessInstanceId(processInstanceId);
            errorLogModel.setTaskId("");
            errorLogModel.setText(msg);
            errorLogModel.setUpdateTime(time);
            try {
                errorLogService.saveErrorLog(errorLogModel);
            } catch (Exception e1) {
            }
            LOGGER.warn("#################保存办结件数据到数据中心失败#################", e);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e) {
                LOGGER.warn("关闭数据库连接异常", e);
            }
        }
    }

    /**
     * 发送修改办件流程信息
     *
     * @param tenantId
     * @param processParam
     * @return
     */
    @Async
    public void saveToDataCenter1(final String tenantId, final String taskId, final ProcessParam processParam) {
        Y9LoginUserHolder.setTenantId(tenantId);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String processInstanceId = processParam.getProcessInstanceId();
        Connection connection = null;
        try {
            OfficeDoneInfo officeDoneInfo = officeDoneInfoService.findByProcessInstanceId(processInstanceId);
            if (officeDoneInfo == null) {
                String sql0 = "SELECT" + "	P .PROC_INST_ID_,"
                    + "	TO_CHAR(P .START_TIME_,'yyyy-MM-dd HH:mi:ss') as START_TIME_," + "	P .PROC_DEF_ID_" + " FROM"
                    + "	ACT_HI_PROCINST P" + " WHERE" + "	P .PROC_INST_ID_ = '" + processInstanceId + "'";
                DataSource dataSource = jdbcTemplate.getDataSource();
                DbMetaDataUtil dbMetaDataUtil = new DbMetaDataUtil();
                connection = dataSource.getConnection();
                String dialectName = dbMetaDataUtil.getDatabaseDialectName(connection);
                if (DialectEnum.MYSQL.getValue().equals(dialectName)) {
                    sql0 = "SELECT" + "	P .PROC_INST_ID_,SUBSTRING(P.START_TIME_,1,19) as START_TIME_,P.PROC_DEF_ID_"
                        + " FROM" + "	ACT_HI_PROCINST P" + " WHERE" + " P.PROC_INST_ID_ = '" + processInstanceId
                        + "'";
                }
                List<Map<String, Object>> list = jdbcTemplate.queryForList(sql0);
                Map<String, Object> map = list.get(0);
                String processDefinitionId = (String)map.get("PROC_DEF_ID_");
                String startTime = (String)map.get("START_TIME_");
                officeDoneInfo = new OfficeDoneInfo();
                officeDoneInfo.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                officeDoneInfo.setDocNumber(
                    StringUtils.isNotBlank(processParam.getCustomNumber()) ? processParam.getCustomNumber() : "");
                officeDoneInfo
                    .setItemId(StringUtils.isNotBlank(processParam.getItemId()) ? processParam.getItemId() : "");
                officeDoneInfo
                    .setItemName(StringUtils.isNotBlank(processParam.getItemName()) ? processParam.getItemName() : "");
                officeDoneInfo.setProcessSerialNumber(StringUtils.isNotBlank(processParam.getProcessSerialNumber())
                    ? processParam.getProcessSerialNumber() : "");
                officeDoneInfo.setSystemCnName(
                    StringUtils.isNotBlank(processParam.getSystemCnName()) ? processParam.getSystemCnName() : "");
                officeDoneInfo.setSystemName(
                    StringUtils.isNotBlank(processParam.getSystemName()) ? processParam.getSystemName() : "");
                officeDoneInfo
                    .setCreatUserId(StringUtils.isNotBlank(processParam.getStartor()) ? processParam.getStartor() : "");
                officeDoneInfo.setCreatUserName(
                    StringUtils.isNotBlank(processParam.getStartorName()) ? processParam.getStartorName() : "");
                officeDoneInfo.setStartTime(startTime);
                officeDoneInfo.setProcessDefinitionId(processDefinitionId);
                officeDoneInfo.setProcessDefinitionKey(processDefinitionId.split(":")[0]);
                officeDoneInfo.setProcessInstanceId(processInstanceId);
                officeDoneInfo.setTenantId(tenantId);
            }
            officeDoneInfo.setTitle(StringUtils.isNotBlank(processParam.getTitle()) ? processParam.getTitle() : "");
            officeDoneInfo
                .setUrgency(StringUtils.isNotBlank(processParam.getCustomLevel()) ? processParam.getCustomLevel() : "");
            officeDoneInfo.setUserComplete("");
            officeDoneInfo.setBureauId(processParam.getBureauIds());
            officeDoneInfo.setEndTime(null);

            // 处理委托人
            String sql = "";
            // "SELECT e.OWNERID from FF_ENTRUSTDETAIL e where e.PROCESSINSTANCEID = '" + processInstanceId + "'";
            // List<Map<String, Object>> list2 = jdbcTemplate.queryForList(sql);
            // String entrustUserId = "";
            // for (Map<String, Object> m : list2) {
            // String userId = (String)m.get("OWNERID");
            // if (!entrustUserId.contains(userId)) {
            // entrustUserId = Y9Util.genCustomStr(entrustUserId, userId);
            // }
            // }
            // officeDoneInfo.setEntrustUserId(entrustUserId);

            // 处理参与人
            sql = "SELECT i.USER_ID_ from ACT_HI_IDENTITYLINK i where i.PROC_INST_ID_ = '" + processInstanceId + "'";
            List<Map<String, Object>> list3 = jdbcTemplate.queryForList(sql);
            String allUserId = "";
            String deptIds = "";
            for (Map<String, Object> m : list3) {
                String userId = m.get("USER_ID_") != null ? (String)m.get("USER_ID_") : "";
                if (userId.contains(":")) {
                    userId = userId.split(":")[0];
                }
                if (StringUtils.isNotEmpty(userId) && !allUserId.contains(userId)) {
                    allUserId = Y9Util.genCustomStr(allUserId, userId);
                }
                if (StringUtils.isNotEmpty(userId)) {
                    Position position = positionManager.get(tenantId, userId).getData();
                    if (position != null && position.getId() != null) {
                        if (!deptIds.contains(position.getParentId())) {
                            deptIds = Y9Util.genCustomStr(deptIds, position.getParentId());
                        }
                    }
                }
            }
            officeDoneInfo.setDeptId(deptIds);
            officeDoneInfo.setAllUserId(allUserId);
            officeDoneInfoService.saveOfficeDone(officeDoneInfo);
        } catch (Exception e) {
            final Writer result = new StringWriter();
            final PrintWriter print = new PrintWriter(result);
            e.printStackTrace(print);
            String msg = result.toString();
            String time = sdf.format(new Date());
            ErrorLog errorLogModel = new ErrorLog();
            errorLogModel.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            errorLogModel.setCreateTime(time);
            errorLogModel.setErrorFlag(ErrorLogModel.ERROR_FLAG_SAVE_OFFICE_DONE + "4Search2");
            errorLogModel.setErrorType(ErrorLogModel.ERROR_PROCESS_INSTANCE);
            errorLogModel.setExtendField("发送保存流程信息失败");
            errorLogModel.setProcessInstanceId(processInstanceId);
            errorLogModel.setTaskId(taskId);
            errorLogModel.setText(msg);
            errorLogModel.setUpdateTime(time);
            try {
                errorLogService.saveErrorLog(errorLogModel);
            } catch (Exception e1) {
            }
            LOGGER.warn("#################保存办结件数据到数据中心失败#################", e);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e) {
                LOGGER.warn("关闭数据库连接异常", e);
            }
        }
    }

}
