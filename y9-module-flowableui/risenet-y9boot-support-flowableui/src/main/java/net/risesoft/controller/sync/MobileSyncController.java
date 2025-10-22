package net.risesoft.controller.sync;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.OfficeDoneInfoApi;
import net.risesoft.api.itemadmin.core.ProcessParamApi;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.OfficeDoneInfoModel;
import net.risesoft.model.itemadmin.core.ProcessParamModel;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.sqlddl.DbMetaDataUtil;
import net.risesoft.y9.util.Y9Util;

/**
 * 同步办结件至数据中心
 */
@RestController
@RequestMapping("/mobile/sync")
@Slf4j
@Validated
@RequiredArgsConstructor
public class MobileSyncController {

    private static final String PROC_INST_ID_KEY = "PROC_INST_ID_";
    private static final String WHERE_PROC_INST_ID_KEY = " where PROC_INST_ID_ = ?";
    private static final String MYSQL_KEY = "mysql";
    private static final String START_TIME_KEY = "START_TIME_";
    private static final List<String> ACT_HI_TABLES =
        List.of("ACT_HI_TASKINST", "ACT_HI_VARINST", "ACT_HI_IDENTITYLINK", "ACT_HI_ACTINST", "ACT_HI_PROCINST");
    private final OfficeDoneInfoApi officeDoneInfoApi;
    private final ProcessParamApi processParamApi;
    @Resource(name = "jdbcTemplate4Tenant")
    private JdbcTemplate jdbcTemplate;

    /**
     * 删除历史数据
     *
     * @param processInstanceId 流程实例id
     */
    public void deleteDoneData(String processInstanceId) {
        ACT_HI_TABLES.forEach(tableName -> executeDelete(tableName, processInstanceId));
        executeDeleteActGeBytearray(processInstanceId);
    }

    /**
     * 执行删除操作（ACT_HI_PROCINST）
     * 
     * @param tableName 表名
     * @param processInstanceId 流程实例ID
     */
    @SuppressWarnings("java:S2077") // 表名来源于内部白名单，processInstanceId使用参数化查询，无SQL注入风险
    private void executeDelete(String tableName, String processInstanceId) {
        String sql = "DELETE FROM " + tableName + " WHERE PROC_INST_ID_ = ?";
        jdbcTemplate.update(sql, processInstanceId);
    }

    /**
     * 执行删除操作（包含用户关联数据）
     *
     * @param processInstanceId 流程实例ID
     */
    private void executeDeleteActGeBytearray(String processInstanceId) {
        String sql = "DELETE FROM ACT_GE_BYTEARRAY WHERE ID_ IN (SELECT * FROM ( SELECT b.ID_ FROM "
            + "ACT_GE_BYTEARRAY"
            + " b LEFT JOIN ACT_HI_VARINST v ON v.BYTEARRAY_ID_ = b.ID_ WHERE v.PROC_INST_ID_ = ? AND v.NAME_ = 'users' ) TT )";
        jdbcTemplate.update(sql, processInstanceId);
    }

    /**
     * 删除正在运行的数据
     *
     * @param tenantId 租户id
     */

    @GetMapping(value = "/deleteTableData")
    public void deleteTableData(String tenantId, HttpServletResponse response) {
        Map<String, Object> resMap = new HashMap<>(16);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            String sql = "SELECT P.PROC_INST_ID_ FROM ACT_HI_PROCINST P WHERE"
                + " (P.END_TIME_ is not null or P.DELETE_REASON_ = '已删除') ORDER BY P.START_TIME_ DESC";
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
            LOGGER.info("*********************删除正在运行的数据,共{}条数据***************************", list.size());
            int i = 0;
            for (Map<String, Object> map : list) {
                try {
                    deleteDoneData((String)map.get(PROC_INST_ID_KEY));
                } catch (Exception e) {
                    i = i + 1;
                    LOGGER.error("删除历史数据失败", e);
                }
            }
            LOGGER.info("********************删除历史数据失败{}条数据***************************", i);
            resMap.put("总数", list.size());
            resMap.put("同步失败", i);
        } catch (Exception e) {
            LOGGER.error("同步失败", e);
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(resMap));
    }

    private String getActGeBytearraySql(String year) {
        return "INSERT INTO ACT_GE_BYTEARRAY_" + year + " ( ID_, REV_, NAME_, DEPLOYMENT_ID_, BYTES_,GENERATED_"
            + " ) SELECT b.ID_,b.REV_,b.NAME_,b.DEPLOYMENT_ID_,b.BYTES_,b.GENERATED_ FROM ACT_GE_BYTEARRAY b"
            + " LEFT JOIN ACT_HI_VARINST v ON v.BYTEARRAY_ID_ = b.ID_ WHERE v.PROC_INST_ID_ = ? AND v.NAME_ = 'users'";
    }

    private String getActHiActinstSql(String year) {
        return "INSERT INTO ACT_HI_ACTINST_" + year + " ( ID_, REV_,   PROC_DEF_ID_,   PROC_INST_ID_,"
            + " EXECUTION_ID_,  ACT_ID_, TASK_ID_,   CALL_PROC_INST_ID_,  ACT_NAME_,  ACT_TYPE_,"
            + " ASSIGNEE_,  START_TIME_, END_TIME_,  DURATION_,  DELETE_REASON_,   TENANT_ID_"
            + " ) SELECT ID_, REV_,   PROC_DEF_ID_,   PROC_INST_ID_,  EXECUTION_ID_,"
            + " ACT_ID_, TASK_ID_,   CALL_PROC_INST_ID_,  ACT_NAME_,  ACT_TYPE_,  ASSIGNEE_,"
            + " START_TIME_,  END_TIME_,  DURATION_,  DELETE_REASON_,   TENANT_ID_"
            + " FROM ACT_HI_ACTINST A  WHERE  A.PROC_INST_ID_ = ?";
    }

    private String getActHiIdentiyLinkSql(String year) {
        return "INSERT INTO ACT_HI_IDENTITYLINK_" + year + " (   ID_, GROUP_ID_,  TYPE_,  USER_ID_,"
            + "  TASK_ID_,   CREATE_TIME_,   PROC_INST_ID_,  SCOPE_ID_,  SCOPE_TYPE_,"
            + "   SCOPE_DEFINITION_ID_  ) SELECT  ID_, GROUP_ID_,  TYPE_,  USER_ID_,   TASK_ID_,"
            + "  CREATE_TIME_,   PROC_INST_ID_,  SCOPE_ID_,  SCOPE_TYPE_,    SCOPE_DEFINITION_ID_"
            + " FROM  ACT_HI_IDENTITYLINK i  WHERE i.PROC_INST_ID_ = ?";
    }

    private String getActHiProcinstSql(String year) {
        return "INSERT INTO ACT_HI_PROCINST_" + year + " ( ID_, REV_,   PROC_INST_ID_,  BUSINESS_KEY_,"
            + " PROC_DEF_ID_,   START_TIME_, END_TIME_,  DURATION_,  START_USER_ID_,  START_ACT_ID_,"
            + " END_ACT_ID_, SUPER_PROCESS_INSTANCE_ID_,  DELETE_REASON_,  TENANT_ID_,  NAME_,  CALLBACK_ID_,"
            + "  CALLBACK_TYPE_ ) SELECT   ID_, REV_,   PROC_INST_ID_,  BUSINESS_KEY_,"
            + " PROC_DEF_ID_,   START_TIME_,    END_TIME_,  DURATION_,  START_USER_ID_,"
            + " START_ACT_ID_,  END_ACT_ID_,"
            + "   SUPER_PROCESS_INSTANCE_ID_,  DELETE_REASON_,  TENANT_ID_,  NAME_,  CALLBACK_ID_,"
            + "  CALLBACK_TYPE_  FROM ACT_HI_PROCINST RES  WHERE  RES.PROC_INST_ID_ = ?";
    }

    private String getActHiTaskinstSql(String year) {
        return "INSERT INTO ACT_HI_TASKINST_" + year + " ( ID_, REV_,   PROC_DEF_ID_,   TASK_DEF_ID_,"
            + " TASK_DEF_KEY_,  PROC_INST_ID_,  EXECUTION_ID_,  SCOPE_ID_,  SUB_SCOPE_ID_,"
            + " SCOPE_TYPE_, SCOPE_DEFINITION_ID_,   PARENT_TASK_ID_,  NAME_,  DESCRIPTION_,"
            + " OWNER_,  ASSIGNEE_,  START_TIME_, CLAIM_TIME_, END_TIME_,  DURATION_,"
            + " DELETE_REASON_,  PRIORITY_,  DUE_DATE_,  FORM_KEY_,  CATEGORY_,"
            + " TENANT_ID_,  LAST_UPDATED_TIME_  ) SELECT  ID_, REV_,   PROC_DEF_ID_,   TASK_DEF_ID_,"
            + " TASK_DEF_KEY_,  PROC_INST_ID_,  EXECUTION_ID_,  SCOPE_ID_,  SUB_SCOPE_ID_,"
            + " SCOPE_TYPE_, SCOPE_DEFINITION_ID_,   PARENT_TASK_ID_, NAME_,  DESCRIPTION_,   OWNER_,"
            + " ASSIGNEE_,  START_TIME_, CLAIM_TIME_, END_TIME_,  DURATION_,  DELETE_REASON_,  PRIORITY_,"
            + " DUE_DATE_,  FORM_KEY_,  CATEGORY_,  TENANT_ID_,  LAST_UPDATED_TIME_"
            + " FROM ACT_HI_TASKINST T  WHERE  T .PROC_INST_ID_ = ？";
    }

    private String getActHiVarinstSql(String year) {
        return "INSERT INTO ACT_HI_VARINST_" + year + " ( ID_, REV_,   PROC_INST_ID_,  EXECUTION_ID_,"
            + " TASK_ID_,   NAME_,  VAR_TYPE_,  SCOPE_ID_,  SUB_SCOPE_ID_,"
            + " SCOPE_TYPE_, BYTEARRAY_ID_,  DOUBLE_, LONG_,  TEXT_,  TEXT2_,   CREATE_TIME_,"
            + " LAST_UPDATED_TIME_  ) SELECT ID_, REV_,   PROC_INST_ID_,  EXECUTION_ID_,  TASK_ID_,"
            + " NAME_,  VAR_TYPE_,  SCOPE_ID_,  SUB_SCOPE_ID_,  SCOPE_TYPE_, BYTEARRAY_ID_,"
            + " DOUBLE_, LONG_,  TEXT_,  TEXT2_,   CREATE_TIME_,   LAST_UPDATED_TIME_"
            + " FROM ACT_HI_VARINST v  WHERE   v.PROC_INST_ID_ = ? and v.NAME_ not in ('nrOfActiveInstances','nrOfCompletedInstances','nrOfInstances','loopCounter','elementUser')";
    }

    /**
     * 办结保存年度历史数据
     * <p>
     * ACT_HI_TASKINST ACT_HI_VARINST ACT_GE_BYTEARRAY
     * <p>
     * ACT_HI_IDENTITYLINK ACT_HI_ACTINST ACT_HI_PROCINST
     *
     * @param processInstanceId 流程实例id
     */
    public void saveYearData(String year, String processInstanceId) {
        // 处理 ACT_HI_TASKINST 表
        if (isTableDataEmpty("ACT_HI_TASKINST_" + year, processInstanceId)) {
            executeInsert(getActHiTaskinstSql(year), processInstanceId);
        }
        // 处理 ACT_HI_VARINST 表
        if (isTableDataEmpty("ACT_HI_VARINST_" + year, processInstanceId)) {
            executeInsert(getActHiVarinstSql(year), processInstanceId);
        }
        // 处理 ACT_GE_BYTEARRAY 表
        try {
            executeInsert(getActGeBytearraySql(year), processInstanceId);
        } catch (DataAccessException e) {
            LOGGER.error("保存历史数据失败", e);
        }
        // 处理 ACT_HI_IDENTITYLINK 表
        if (isTableDataEmpty("ACT_HI_IDENTITYLINK_" + year, processInstanceId)) {
            executeInsert(getActHiIdentiyLinkSql(year), processInstanceId);
        }
        // 处理 ACT_HI_ACTINST 表
        if (isTableDataEmpty("ACT_HI_ACTINST_" + year, processInstanceId)) {
            executeInsert(getActHiActinstSql(year), processInstanceId);
        }
        // 处理 ACT_HI_PROCINST 表
        if (isTableDataEmpty("ACT_HI_PROCINST_" + year, processInstanceId)) {
            executeInsert(getActHiProcinstSql(year), processInstanceId);
        }
    }

    /**
     * 检查指定表中是否存在指定流程实例的数据
     *
     * @param tableName 表名（包含年份后缀）
     * @param processInstanceId 流程实例ID
     * @return 如果表中没有数据返回true，否则返回false
     */
    @SuppressWarnings("java:S2077") // 表名来源于内部白名单，processInstanceId使用参数化查询，无SQL注入风险
    private boolean isTableDataEmpty(String tableName, String processInstanceId) {
        String selectSql = "SELECT * FROM " + tableName + WHERE_PROC_INST_ID_KEY;
        List<Map<String, Object>> list = jdbcTemplate.queryForList(selectSql, processInstanceId);
        return list.isEmpty();
    }

    /**
     * 执行插入操作
     *
     * @param sql 插入SQL语句
     * @param processInstanceId 流程实例ID
     */
    private void executeInsert(String sql, String processInstanceId) {
        jdbcTemplate.update(sql, processInstanceId);
    }

    /**
     * 同步年度办结件至数据中心
     *
     * @param tenantId 租户id
     */
    @GetMapping(value = "/sync2DataCenter")
    public void sync2DataCenter(String tenantId, HttpServletResponse response) {
        Map<String, Object> resMap = new HashMap<>(16);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            // 构建查询SQL
            String sql = buildProcInstQuerySql();
            DataSource dataSource = jdbcTemplate.getDataSource();
            String dialectName = DbMetaDataUtil.getDatabaseDialectName(dataSource);
            if (dialectName.equals(MYSQL_KEY)) {
                sql = buildProcInstQuerySqlForMySQL();
            }
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
            LOGGER.info("*********************同步年度办结件至数据中心,共{}条数据***************************", list.size());
            int failedCount = 0;
            for (Map<String, Object> map : list) {
                try {
                    processProcInstanceData(tenantId, map);
                } catch (Exception e) {
                    failedCount++;
                    LOGGER.error("同步单条数据失败, PROC_INST_ID_: {}", map.get(PROC_INST_ID_KEY), e);
                }
            }
            LOGGER.info("********************同步年度办结件至数据中心失败{}条数据***************************", failedCount);
            resMap.put("总数", list.size());
            resMap.put("同步失败", failedCount);
        } catch (Exception e) {
            LOGGER.error("同步年度办结件至数据中心失败", e);
            resMap.put("错误", e.getMessage());
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(resMap));
    }

    /**
     * 构建Oracle/PostgresSQL的查询SQL
     */
    private String buildProcInstQuerySql() {
        return "SELECT P.PROC_INST_ID_,TO_CHAR(P.START_TIME_,'yyyy-MM-dd HH:mi:ss') as START_TIME_,"
            + " TO_CHAR(P.END_TIME_,'yyyy-MM-dd HH:mi:ss') as END_TIME_,P.PROC_DEF_ID_ FROM"
            + " ACT_HI_PROCINST_2020 P WHERE P.END_TIME_ IS NOT NULL and P.DELETE_REASON_ is null"
            + " ORDER BY P.END_TIME_ DESC";
    }

    /**
     * 构建MySQL的查询SQL
     */
    private String buildProcInstQuerySqlForMySQL() {
        return "SELECT P.PROC_INST_ID_,SUBSTRING(P.START_TIME_,1,19) as START_TIME_,"
            + "SUBSTRING(P.END_TIME_,1,19) as END_TIME_,P.PROC_DEF_ID_"
            + " FROM ACT_HI_PROCINST_2020 P WHERE P.END_TIME_ IS NOT NULL and P.DELETE_REASON_ is null"
            + " ORDER BY P.END_TIME_ DESC";
    }

    /**
     * 处理单个流程实例数据
     */
    private void processProcInstanceData(String tenantId, Map<String, Object> map) throws Exception {
        String procInstId = (String)map.get(PROC_INST_ID_KEY);
        String procDefId = (String)map.get("PROC_DEF_ID_");
        String startTime = (String)map.get(START_TIME_KEY);
        String endTime = (String)map.get("END_TIME_");
        // 获取流程参数和办结信息
        ProcessParamModel processParamModel = processParamApi.findByProcessInstanceId(tenantId, procInstId).getData();
        OfficeDoneInfoModel officeDoneInfo = officeDoneInfoApi.findByProcessInstanceId(tenantId, procInstId).getData();
        // 初始化办结信息对象
        if (officeDoneInfo == null) {
            officeDoneInfo = new OfficeDoneInfoModel();
            officeDoneInfo.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        }
        // 填充办结信息
        populateOfficeDoneInfo(officeDoneInfo, processParamModel, tenantId, procInstId, procDefId, startTime, endTime);
        // 处理参与人信息
        String allUserId = processParticipantUsers(procInstId);
        officeDoneInfo.setAllUserId(allUserId);
        // 保存办结信息
        officeDoneInfoApi.saveOfficeDone(tenantId, officeDoneInfo);
    }

    /**
     * 填充办结信息
     */
    private void populateOfficeDoneInfo(OfficeDoneInfoModel officeDoneInfo, ProcessParamModel processParamModel,
        String tenantId, String procInstId, String procDefId, String startTime, String endTime) {
        if (processParamModel != null) {
            officeDoneInfo.setBureauId(StringUtils.defaultString(processParamModel.getBureauIds()));
            officeDoneInfo.setDeptId(StringUtils.defaultString(processParamModel.getDeptIds()));
            officeDoneInfo.setCreatUserId(StringUtils.defaultString(processParamModel.getStartor()));
            officeDoneInfo.setCreatUserName(StringUtils.defaultString(processParamModel.getStartorName()));
            officeDoneInfo.setDocNumber(StringUtils.defaultString(processParamModel.getCustomNumber()));
            officeDoneInfo.setItemId(StringUtils.defaultString(processParamModel.getItemId()));
            officeDoneInfo.setItemName(StringUtils.defaultString(processParamModel.getItemName()));
            officeDoneInfo
                .setProcessSerialNumber(StringUtils.defaultString(processParamModel.getProcessSerialNumber()));
            officeDoneInfo.setSystemCnName(StringUtils.defaultString(processParamModel.getSystemCnName()));
            officeDoneInfo.setSystemName(StringUtils.defaultString(processParamModel.getSystemName()));
            officeDoneInfo.setTitle(StringUtils.defaultString(processParamModel.getTitle()));
            officeDoneInfo.setUrgency(StringUtils.defaultString(processParamModel.getCustomLevel()));
            officeDoneInfo.setUserComplete(StringUtils.defaultString(processParamModel.getCompleter()));
            officeDoneInfo.setTarget(StringUtils.defaultString(processParamModel.getTarget()));
        }

        officeDoneInfo.setEndTime(endTime);
        officeDoneInfo.setProcessDefinitionId(procDefId);
        officeDoneInfo.setProcessDefinitionKey(procDefId.split(":")[0]);
        officeDoneInfo.setProcessInstanceId(procInstId);
        officeDoneInfo.setStartTime(startTime);
        officeDoneInfo.setTenantId(tenantId);
    }

    /**
     * 处理参与人信息
     */
    private String processParticipantUsers(String procInstId) {
        // 使用参数化查询避免SQL注入
        String sql = "SELECT i.USER_ID_ from ACT_HI_IDENTITYLINK_2020 i where i.PROC_INST_ID_ = ?";
        List<Map<String, Object>> userList = jdbcTemplate.queryForList(sql, procInstId);

        String allUserId = "";
        for (Map<String, Object> userMap : userList) {
            String userId = (String)userMap.get("USER_ID_");
            if (StringUtils.isNotBlank(userId) && !allUserId.contains(userId)) {
                allUserId = Y9Util.genCustomStr(allUserId, userId);
            }
        }
        return allUserId;
    }

    /**
     * 同步办结件至数据中心，办结截转数据失败的件
     *
     * @param tenantId 租户ID
     */

    @GetMapping(value = "/sync2DataCenter0")
    public void sync2DataCenter0(String tenantId, HttpServletResponse response) {
        Map<String, Object> resMap = new HashMap<>(16);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            String sql = "SELECT P.PROC_INST_ID_,TO_CHAR(P.START_TIME_,'yyyy-MM-dd HH:mi:ss') as START_TIME_,"
                + " TO_CHAR(P.END_TIME_,'yyyy-MM-dd HH:mi:ss') as END_TIME_,P.PROC_DEF_ID_ FROM"
                + " ACT_HI_PROCINST P WHERE P.END_TIME_ IS NOT NULL and P.DELETE_REASON_ is null"
                + " ORDER BY P.END_TIME_ DESC";
            DataSource dataSource = jdbcTemplate.getDataSource();
            String dialectName = DbMetaDataUtil.getDatabaseDialectName(dataSource);
            if (dialectName.equals(MYSQL_KEY) || dialectName.equals("kingbase")) {
                sql =
                    "SELECT P.PROC_INST_ID_,SUBSTRING(P.START_TIME_,1,19) as START_TIME_,SUBSTRING(P.END_TIME_,1,19) as END_TIME_,P.PROC_DEF_ID_"
                        + " FROM ACT_HI_PROCINST P WHERE P.END_TIME_ IS NOT NULL and P.DELETE_REASON_ is null ORDER BY P.END_TIME_ DESC";
            }
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
            LOGGER.info("*********************同步办结件至数据中心，办结截转数据失败的件,共{}条数据***************************", list.size());
            int i = 0;
            for (Map<String, Object> map : list) {
                try {
                    String processInstanceId = (String)map.get(PROC_INST_ID_KEY);
                    String START_TIME_ = (String)map.get(START_TIME_KEY);
                    String END_TIME_ = (String)map.get("END_TIME_");
                    ProcessParamModel processParamModel =
                        processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                    OfficeDoneInfoModel officeDoneInfo =
                        officeDoneInfoApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                    if (officeDoneInfo != null) {
                        if (processParamModel != null) {
                            officeDoneInfo.setUserComplete(StringUtils.isBlank(processParamModel.getCompleter()) ? ""
                                : processParamModel.getCompleter());
                        }
                        officeDoneInfo.setEndTime(END_TIME_);
                        officeDoneInfo.setTenantId(tenantId);
                        officeDoneInfoApi.saveOfficeDone(tenantId, officeDoneInfo);
                        String year = START_TIME_.substring(0, 4);
                        this.saveYearData(year, processInstanceId);
                        this.deleteDoneData(processInstanceId);
                    }
                } catch (Exception e) {
                    i = i + 1;
                    LOGGER.error("同步失败", e);
                }
            }
            LOGGER.info("********************办结件至数据中心，办结截转数据同步失败,共{}条数据***************************", i);
            resMap.put("总数", list.size());
            resMap.put("同步失败", i);
        } catch (Exception e) {
            LOGGER.error("同步失败", e);
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(resMap));
    }

    /**
     * 结转数据至年度表
     *
     * @param tenantId 租户id
     */

    @GetMapping(value = "/sync2YearTable")
    public void sync2YearTable(String tenantId, HttpServletResponse response) {
        Map<String, Object> resMap = new HashMap<>(16);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            String sql = "SELECT P.PROC_INST_ID_,TO_CHAR(P.START_TIME_,'yyyy-MM-dd HH:mi:ss') as START_TIME_"
                + " FROM ACT_HI_PROCINST P WHERE P.END_TIME_ IS NOT NULL"
                + " and P.DELETE_REASON_ is null ORDER BY P.END_TIME_ DESC";
            DataSource dataSource = jdbcTemplate.getDataSource();
            String dialectName = DbMetaDataUtil.getDatabaseDialectName(dataSource);
            if (dialectName.equals(MYSQL_KEY) || dialectName.equals("kingbase")) {
                sql = "SELECT P.PROC_INST_ID_,SUBSTRING(P.START_TIME_,1,19) as START_TIME_ FROM"
                    + "	ACT_HI_PROCINST P WHERE P.END_TIME_ IS NOT NULL and P.DELETE_REASON_ is null"
                    + " ORDER BY P.END_TIME_ DESC";
            }
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
            LOGGER.info("*********************结转数据至年度表,共{}条数据***************************", list.size());
            int i = 0;
            for (Map<String, Object> map : list) {
                try {
                    String processInstanceId = (String)map.get(PROC_INST_ID_KEY);
                    String START_TIME_ = (String)map.get(START_TIME_KEY);
                    String year = START_TIME_.substring(0, 4);
                    this.saveYearData(year, processInstanceId);
                    this.deleteDoneData(processInstanceId);
                } catch (Exception e) {
                    i = i + 1;
                    LOGGER.error("同步失败", e);
                }
            }
            LOGGER.info("********************同步结转数据至年度表失败，共{}条数据***************************", i);
            resMap.put("总数", list.size());
            resMap.put("同步失败", i);
        } catch (Exception e) {
            LOGGER.error("同步失败", e);
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(resMap));
    }
}
