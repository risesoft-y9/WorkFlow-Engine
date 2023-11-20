package net.risesoft.service.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.org.DepartmentApi;
import net.risesoft.api.org.OrganizationApi;
import net.risesoft.api.org.PersonApi;
import net.risesoft.api.processadmin.HistoricProcessApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.api.sms.SmsHttpApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.ChaoSong;
import net.risesoft.entity.ProcessParam;
import net.risesoft.enums.DialectEnum;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.platform.Department;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Person;
import net.risesoft.model.processadmin.HistoricProcessInstanceModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.nosql.elastic.entity.OfficeDoneInfo;
import net.risesoft.repository.jpa.ChaoSongRepository;
import net.risesoft.service.AsyncHandleService;
import net.risesoft.service.ChaoSongService;
import net.risesoft.service.DocumentService;
import net.risesoft.service.OfficeDoneInfoService;
import net.risesoft.service.ProcessParamService;
import net.risesoft.service.SpmApproveItemService;
import net.risesoft.util.SysVariables;
import net.risesoft.util.form.DbMetaDataUtil;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.configuration.Y9Properties;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Service(value = "chaoSongService")
@Slf4j
public class ChaoSongServiceImpl implements ChaoSongService {

    @Autowired
    private ChaoSongRepository chaoSongRepository;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private SpmApproveItemService spmApproveitemService;

    @Autowired
    private ProcessParamService processParamService;

    @Autowired
    private TaskApi taskManager;

    @Autowired
    private HistoricProcessApi historicProcessManager;

    @Autowired
    private DepartmentApi departmentManager;

    @Autowired
    private OrganizationApi organizationManager;

    @Autowired
    private PersonApi personManager;

    @Autowired
    private SmsHttpApi smsHttpManager;

    @Autowired
    private OfficeDoneInfoService officeDoneInfoService;

    @javax.annotation.Resource(name = "jdbcTemplate4Tenant")
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private Y9Properties y9Conf;

    @Autowired
    private AsyncHandleService asyncHandleService;

    @Override
    @Transactional(readOnly = false)
    public void changeChaoSongState(String id, String type) {
        String opinionState = "";
        if (ItemBoxTypeEnum.ADD.getValue().equals(type)) {
            opinionState = "1";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        String year = sdf.format(new Date());
        String sql = "select count(t.ID) from FF_ChaoSong t where t.ID = '" + id + "'";
        int totalCount = jdbcTemplate.queryForObject(sql, Integer.class);
        if (totalCount > 0) {
            sql = "update FF_ChaoSong t set t.opinionState='" + opinionState + "' where t.ID = '" + id + "'";
            jdbcTemplate.execute(sql);
        } else {
            sql = "select count(t.ID) from FF_ChaoSong_" + year + " t where t.ID = '" + id + "'";
            totalCount = jdbcTemplate.queryForObject(sql, Integer.class);
            if (totalCount > 0) {
                sql = "update FF_ChaoSong_" + year + " t set t.opinionState='" + opinionState + "' where t.ID = '" + id
                    + "'";
                jdbcTemplate.execute(sql);
            } else {
                sql = "select count(t.ID) from FF_ChaoSong_" + String.valueOf(Integer.parseInt(year) - 1)
                    + " t where t.ID = '" + id + "'";
                totalCount = jdbcTemplate.queryForObject(sql, Integer.class);
                if (totalCount > 0) {
                    sql = "update FF_ChaoSong_" + String.valueOf(Integer.parseInt(year) - 1) + " t set t.opinionState='"
                        + opinionState + "' where t.ID = '" + id + "'";
                    jdbcTemplate.execute(sql);
                }
            }
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void changeStatus(String id, Integer status) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ChaoSong chaoSong = chaoSongRepository.findById(id).orElse(null);
        String year = chaoSong.getCreateTime().substring(0, 4);
        String opinionState = StringUtils.isBlank(chaoSong.getOpinionState()) ? "" : chaoSong.getOpinionState();
        String opinionContent = StringUtils.isBlank(chaoSong.getOpinionContent()) ? "" : chaoSong.getOpinionContent();
        String opinionGroup = StringUtils.isBlank(chaoSong.getOpinionGroup()) ? "" : chaoSong.getOpinionGroup();
        String processSerialNumber =
            StringUtils.isBlank(chaoSong.getProcessSerialNumber()) ? "" : chaoSong.getProcessSerialNumber();
        String taskId = StringUtils.isBlank(chaoSong.getTaskId()) ? "" : chaoSong.getTaskId();
        String sql = "INSERT INTO FF_CHAOSONG_" + year + " (" + "	ID," + "	CREATETIME," + "	ITEMID,"
            + "	ITEMNAME," + "	PROCESSINSTANCEID," + "	READTIME," + "	SENDDEPTID," + "	SENDDEPTNAME,"
            + "	SENDERID," + "	SENDERNAME," + "	STATUS," + "	SYSTEMNAME," + "	TASKID," + "	TENANTID,"
            + "	TITLE," + "	USERID," + "	USERNAME," + "	USERDEPTID," + "	USERDEPTNAME," + "	opinionState,"
            + "	opinionContent," + "	opinionGroup," + "	PROCESSSERIALNUMBER" + " )" + " VALUES" + "	(" + "		'"
            + chaoSong.getId() + "'," + "		'" + chaoSong.getCreateTime() + "'," + "		'"
            + chaoSong.getItemId() + "'," + "		'" + chaoSong.getItemName() + "'," + "		'"
            + chaoSong.getProcessInstanceId() + "'," + "		'" + sdf.format(new Date()) + "'," + "		'"
            + chaoSong.getSendDeptId() + "'," + "		'" + chaoSong.getSendDeptName() + "'," + "		'"
            + chaoSong.getSenderId() + "'," + "		'" + chaoSong.getSenderName() + "'," + "		'1'," + "		'"
            + chaoSong.getSystemName() + "'," + "		'" + taskId + "'," + "		'" + chaoSong.getTenantId() + "',"
            + "		'" + chaoSong.getTitle() + "'," + "		'" + chaoSong.getUserId() + "'," + "		'"
            + chaoSong.getUserName() + "'," + "		'" + chaoSong.getUserDeptId() + "'," + "		'"
            + chaoSong.getUserDeptName() + "'," + "		'" + opinionState + "'," + "		'" + opinionContent + "',"
            + "		'" + opinionGroup + "'," + "		'" + processSerialNumber + "'" + "	)";
        jdbcTemplate.execute(sql);
        chaoSongRepository.delete(chaoSong);
    }

    @Override
    @Transactional(readOnly = false)
    public void changeStatus(String[] ids, Integer status) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (String id : ids) {
            ChaoSong chaoSong = chaoSongRepository.findById(id).orElse(null);
            String year = chaoSong.getCreateTime().substring(0, 4);
            String opinionState = StringUtils.isBlank(chaoSong.getOpinionState()) ? "" : chaoSong.getOpinionState();
            String opinionContent =
                StringUtils.isBlank(chaoSong.getOpinionContent()) ? "" : chaoSong.getOpinionContent();
            String opinionGroup = StringUtils.isBlank(chaoSong.getOpinionGroup()) ? "" : chaoSong.getOpinionGroup();
            // String processSerialNumber = StringUtils.isBlank(chaoSong.getProcessSerialNumber()) ? "" :
            // chaoSong.getProcessSerialNumber();
            String taskId = StringUtils.isBlank(chaoSong.getTaskId()) ? "" : chaoSong.getTaskId();
            String sql = "INSERT INTO FF_CHAOSONG_" + year + " (" + "	ID," + "	CREATETIME," + "	ITEMID,"
                + "	ITEMNAME," + "	PROCESSINSTANCEID," + "	READTIME," + "	SENDDEPTID," + "	SENDDEPTNAME,"
                + "	SENDERID," + "	SENDERNAME," + "	STATUS," + "	SYSTEMNAME," + "	TASKID," + "	TENANTID,"
                + "	TITLE," + "	USERID," + "	USERNAME," + "	USERDEPTID," + "	USERDEPTNAME," + "	opinionState,"
                + "	opinionContent," + "	opinionGroup," + "	PROCESSSERIALNUMBER" + " )" + " VALUES" + "	("
                + "		'" + chaoSong.getId() + "'," + "		'" + chaoSong.getCreateTime() + "'," + "		'"
                + chaoSong.getItemId() + "'," + "		'" + chaoSong.getItemName() + "'," + "		'"
                + chaoSong.getProcessInstanceId() + "'," + "		'" + sdf.format(new Date()) + "'," + "		'"
                + chaoSong.getSendDeptId() + "'," + "		'" + chaoSong.getSendDeptName() + "'," + "		'"
                + chaoSong.getSenderId() + "'," + "		'" + chaoSong.getSenderName() + "'," + "		'1',"
                + "		'" + chaoSong.getSystemName() + "'," + "		'" + taskId + "'," + "		'"
                + chaoSong.getTenantId() + "'," + "		'" + chaoSong.getTitle() + "'," + "		'"
                + chaoSong.getUserId() + "'," + "		'" + chaoSong.getUserName() + "'," + "		'"
                + chaoSong.getUserDeptId() + "'," + "		'" + chaoSong.getUserDeptName() + "'," + "		'"
                + opinionState + "'," + "		'" + opinionContent + "'," + "		'" + opinionGroup + "',"
                + "		'" + taskId + "'" + "	)";
            jdbcTemplate.execute(sql);
            chaoSongRepository.delete(chaoSong);
        }
    }

    @Override
    public int countByProcessInstanceId(String userId, String processInstanceId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        String tenantId = Y9LoginUserHolder.getTenantId();
        String year = "";
        HistoricProcessInstanceModel historicProcessInstanceModel =
            historicProcessManager.getById(tenantId, processInstanceId);
        if (historicProcessInstanceModel != null) {
            year = sdf.format(historicProcessInstanceModel.getStartTime());
        } else {
            OfficeDoneInfo officeDoneInfoModel = officeDoneInfoService.findByProcessInstanceId(processInstanceId);
            if (officeDoneInfoModel != null && officeDoneInfoModel.getProcessInstanceId() != null) {
                year = officeDoneInfoModel.getStartTime().substring(0, 4);
            } else {
                ProcessParam processParam = processParamService.findByProcessInstanceId(processInstanceId);
                year = processParam.getCreateTime().substring(0, 4);
                HistoricProcessInstanceModel hpi =
                    historicProcessManager.getByIdAndYear(tenantId, processInstanceId, year);
                if (hpi == null) {
                    year = String.valueOf(Integer.parseInt(year) + 1);
                }
            }
        }
        String countyearSql0 = " SELECT" + "			count(ID)" + "		FROM" + "			FF_CHAOSONG_" + year
            + "		WHERE" + "			processInstanceId = '" + processInstanceId + "' "
            + "			AND SENDERID != '" + userId + "'";
        int countyear0 = jdbcTemplate.queryForObject(countyearSql0, Integer.class);

        String countyearSql1 = " SELECT" + "			count(ID)" + "		FROM" + "			FF_CHAOSONG_"
            + String.valueOf((Integer.parseInt(year) + 1)) + "		WHERE" + "			processInstanceId = '"
            + processInstanceId + "' " + "			AND SENDERID != '" + userId + "'";
        int countyear1 = 0;
        try {
            countyear1 = jdbcTemplate.queryForObject(countyearSql1, Integer.class);
        } catch (Exception e) {
        }
        String countyearSql2 = " SELECT" + "			count(ID)" + "		FROM" + "			FF_CHAOSONG"
            + "		WHERE" + "			processInstanceId = '" + processInstanceId + "' "
            + "			AND SENDERID != '" + userId + "'";
        int totalCount = jdbcTemplate.queryForObject(countyearSql2, Integer.class);
        return totalCount + countyear0 + countyear1;
    }

    @Override
    public int countByUserIdAndProcessInstanceId(String userId, String processInstanceId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        String tenantId = Y9LoginUserHolder.getTenantId();
        String year = "";
        HistoricProcessInstanceModel historicProcessInstanceModel =
            historicProcessManager.getById(tenantId, processInstanceId);
        if (historicProcessInstanceModel != null) {
            year = sdf.format(historicProcessInstanceModel.getStartTime());
        } else {
            OfficeDoneInfo officeDoneInfoModel = officeDoneInfoService.findByProcessInstanceId(processInstanceId);
            if (officeDoneInfoModel != null && officeDoneInfoModel.getProcessInstanceId() != null) {
                year = officeDoneInfoModel.getStartTime().substring(0, 4);
            } else {
                ProcessParam processParam = processParamService.findByProcessInstanceId(processInstanceId);
                year = processParam.getCreateTime().substring(0, 4);
                HistoricProcessInstanceModel hpi =
                    historicProcessManager.getByIdAndYear(tenantId, processInstanceId, year);
                if (hpi == null) {
                    year = String.valueOf(Integer.parseInt(year) + 1);
                }
            }
        }
        String countyearSql0 = " SELECT" + "			count(ID)" + "		FROM" + "			FF_CHAOSONG_" + year
            + "		WHERE" + "			processInstanceId = '" + processInstanceId + "' "
            + "			AND SENDERID = '" + userId + "'";
        int countyear0 = jdbcTemplate.queryForObject(countyearSql0, Integer.class);

        String countyearSql1 = " SELECT" + "			count(ID)" + "		FROM" + "			FF_CHAOSONG_"
            + String.valueOf((Integer.parseInt(year) + 1)) + "		WHERE" + "			processInstanceId = '"
            + processInstanceId + "' " + "			AND SENDERID = '" + userId + "'";
        int countyear1 = 0;
        try {
            countyear1 = jdbcTemplate.queryForObject(countyearSql1, Integer.class);
        } catch (Exception e) {
        }
        String countyearSql2 = " SELECT" + "			count(ID)" + "		FROM" + "			FF_CHAOSONG"
            + "		WHERE" + "			processInstanceId = '" + processInstanceId + "' "
            + "			AND SENDERID = '" + userId + "'";
        int totalCount = jdbcTemplate.queryForObject(countyearSql2, Integer.class);
        return totalCount + countyear0 + countyear1;
    }

    @Override
    @Transactional(readOnly = false)
    public boolean deleteByProcessInstanceId(String processInstanceId, String year) {
        try {
            List<ChaoSong> list = chaoSongRepository.findByProcessInstanceId(processInstanceId);
            if (list.size() > 0) {
                chaoSongRepository.deleteAll(list);
            }
            if (StringUtils.isBlank(year)) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
                year = sdf.format(new Date());
            }
            String countSql = "SELECT" + "			count(ID)" + "		FROM" + "			FF_CHAOSONG_" + year
                + "		WHERE" + "			PROCESSINSTANCEID = '" + processInstanceId + "' ";
            int totalCount = jdbcTemplate.queryForObject(countSql, Integer.class);
            if (totalCount > 0) {
                countSql = "delete" + "		FROM" + "			FF_CHAOSONG_" + year + "		WHERE"
                    + "			PROCESSINSTANCEID = '" + processInstanceId + "' ";
                jdbcTemplate.execute(countSql);
            } else {
                try {
                    countSql = "SELECT" + "			count(ID)" + "		FROM" + "			FF_CHAOSONG_"
                        + String.valueOf((Integer.parseInt(year) + 1)) + "		WHERE"
                        + "			PROCESSINSTANCEID = '" + processInstanceId + "' ";
                    totalCount = jdbcTemplate.queryForObject(countSql, Integer.class);
                    if (totalCount > 0) {
                        countSql = "delete" + "		FROM" + "			FF_CHAOSONG_"
                            + String.valueOf((Integer.parseInt(year) + 1)) + "		WHERE"
                            + "			PROCESSINSTANCEID = '" + processInstanceId + "' ";
                        jdbcTemplate.execute(countSql);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteList(String[] ids, String processInstanceId) {
        for (String id : ids) {
            ChaoSong cs = this.findOne(id);
            if (cs != null) {
                chaoSongRepository.delete(cs);
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
                String tenantId = Y9LoginUserHolder.getTenantId();
                String year = "";
                HistoricProcessInstanceModel historicProcessInstanceModel =
                    historicProcessManager.getById(tenantId, processInstanceId);
                if (historicProcessInstanceModel != null) {
                    year = sdf.format(historicProcessInstanceModel.getStartTime());
                } else {
                    OfficeDoneInfo officeDoneInfoModel =
                        officeDoneInfoService.findByProcessInstanceId(processInstanceId);
                    if (officeDoneInfoModel != null && officeDoneInfoModel.getProcessInstanceId() != null) {
                        year = officeDoneInfoModel.getStartTime().substring(0, 4);
                    } else {
                        ProcessParam processParam = processParamService.findByProcessInstanceId(processInstanceId);
                        year = processParam.getCreateTime().substring(0, 4);
                        HistoricProcessInstanceModel hpi =
                            historicProcessManager.getByIdAndYear(tenantId, processInstanceId, year);
                        if (hpi == null) {
                            year = String.valueOf(Integer.parseInt(year) + 1);
                        }
                    }
                }
                String countSql = "SELECT" + "			count(ID)" + "		FROM" + "			FF_CHAOSONG_" + year
                    + "		WHERE" + "			ID = '" + id + "' ";
                int totalCount = jdbcTemplate.queryForObject(countSql, Integer.class);
                if (totalCount > 0) {
                    countSql = "delete" + "		FROM" + "			FF_CHAOSONG_" + year + "		WHERE"
                        + "			ID = '" + id + "' ";
                    jdbcTemplate.execute(countSql);
                } else {
                    countSql = "SELECT" + "			count(ID)" + "		FROM" + "			FF_CHAOSONG_"
                        + String.valueOf((Integer.parseInt(year) + 1)) + "		WHERE" + "			ID = '" + id + "' ";
                    totalCount = jdbcTemplate.queryForObject(countSql, Integer.class);
                    if (totalCount > 0) {
                        countSql = "delete" + "		FROM" + "			FF_CHAOSONG_"
                            + String.valueOf((Integer.parseInt(year) + 1)) + "		WHERE" + "			ID = '" + id
                            + "' ";
                        jdbcTemplate.execute(countSql);
                    }
                }
            }
        }
    }

    @Override
    public Map<String, Object> detail(String processInstanceId, Integer status, boolean mobile) {
        Map<String, Object> returnMap = new HashMap<>(16);
        String tenantId = Y9LoginUserHolder.getTenantId();
        String itembox = ItemBoxTypeEnum.DOING.getValue(), taskId = "";
        List<TaskModel> taskList = taskManager.findByProcessInstanceId(tenantId, processInstanceId);
        if (taskList.size() <= 0) {
            itembox = ItemBoxTypeEnum.DONE.getValue();
        }
        if (ItemBoxTypeEnum.DOING.getValue().equals(itembox)) {
            taskId = taskList.get(0).getId();
            TaskModel task = taskManager.findById(tenantId, taskId);
            processInstanceId = task.getProcessInstanceId();
        }
        String processSerialNumber = "", processDefinitionId = "", taskDefinitionKey = "", processDefinitionKey = "",
            activitiUser = "";
        String itemboxStr = itembox;
        String startor = "";
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        ProcessParam processParam = processParamService.findByProcessInstanceId(processInstanceId);
        HistoricProcessInstanceModel hpi = historicProcessManager.getById(tenantId, processInstanceId);
        if (hpi == null) {
            OfficeDoneInfo officeDoneInfo = officeDoneInfoService.findByProcessInstanceId(processInstanceId);
            if (officeDoneInfo == null) {
                String year = processParam.getCreateTime().substring(0, 4);
                hpi = historicProcessManager.getByIdAndYear(tenantId, processInstanceId, year);
                processDefinitionId = hpi.getProcessDefinitionId();
                processDefinitionKey = processDefinitionId.split(SysVariables.COLON)[0];
            } else {
                processDefinitionId = officeDoneInfo.getProcessDefinitionId();
                processDefinitionKey = officeDoneInfo.getProcessDefinitionKey();
            }
        } else {
            processDefinitionId = hpi.getProcessDefinitionId();
            processDefinitionKey = processDefinitionId.split(SysVariables.COLON)[0];
        }
        startor = processParam.getStartor();
        processSerialNumber = processParam.getProcessSerialNumber();
        if (StringUtils.isNotEmpty(taskId)) {
            if (taskId.contains(SysVariables.COMMA)) {
                taskId = taskId.split(SysVariables.COMMA)[0];
            }
            TaskModel taskTemp = taskManager.findById(tenantId, taskId);
            taskDefinitionKey = taskTemp.getTaskDefinitionKey();
        }
        returnMap.put("startor", startor);
        returnMap.put("itembox", itembox);
        returnMap.put("control", itemboxStr);
        returnMap.put("currentUser", person.getName());
        returnMap.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber);
        returnMap.put("processDefinitionKey", processDefinitionKey);
        returnMap.put("processDefinitionId", processDefinitionId);
        returnMap.put("processInstanceId", processInstanceId);
        returnMap.put("taskDefKey", taskDefinitionKey);
        returnMap.put("taskId", taskId);
        returnMap.put(SysVariables.ACTIVITIUSER, activitiUser);
        returnMap = spmApproveitemService.findById(processParam.getItemId(), returnMap);
        returnMap = documentService.genDocumentModel(processParam.getItemId(), processDefinitionKey,
            processDefinitionId, taskDefinitionKey, mobile, returnMap);
        String menuName = "打印,抄送,返回";
        String menuKey = "17,18,03";
        if (status == 1) {
            menuName = "打印,抄送,返回";
            menuKey = "17,18,03";
        }
        returnMap.put("menuName", menuName);
        returnMap.put("menuKey", menuKey);
        return returnMap;
    }

    @Override
    public ChaoSong findOne(String id) {
        return chaoSongRepository.findById(id).orElse(null);
    }

    @Override
    public int getDone4OpinionCountByUserId(String userId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        String year = sdf.format(new Date());
        String sqlCount = "SELECT" + "			count(ID)" + "		FROM" + "			FF_ChaoSong_" + year
            + "		WHERE" + "			USERID ='" + userId + "' and opinionState = '1'";
        int totalCount = jdbcTemplate.queryForObject(sqlCount, Integer.class);
        return totalCount;
    }

    @Override
    public int getDoneCountByUserId(String userId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        String year = sdf.format(new Date());
        String sqlCount = "SELECT" + "			count(ID)" + "		FROM" + "			FF_ChaoSong_" + year
            + "		WHERE" + "			USERID ='" + userId + "'";
        int totalCount = jdbcTemplate.queryForObject(sqlCount, Integer.class);
        return totalCount;
    }

    @Override
    public int getDoneCountByUserIdAndItemId(String userId, String itemId) {
        return chaoSongRepository.getDoneCountByUserIdAndItemId(userId, itemId);
    }

    @Override
    public int getDoneCountByUserIdAndSystemName(String userId, String systemName) {
        return chaoSongRepository.getDoneCountByUserIdAndSystemName(userId, systemName);
    }

    @Override
    public Map<String, Object> getDoneListByUserId(String userId, String year, String documentTitle, int rows,
        int page) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Map<String, Object> retMap = new HashMap<String, Object>(16);
        if (StringUtils.isBlank(year)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
            year = sdf.format(new Date());
        }
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        int num = (page - 1) * rows;
        int totalCount = 0;
        int startRow = (page - 1) * rows;
        String rowstr = " LIMIT " + startRow + "," + (startRow + rows);
        String sql0 = "";
        if (StringUtils.isNotBlank(documentTitle)) {
            sql0 += " and TITLE like '%" + documentTitle + "%'";
        }
        String sql = "		SELECT" + "			* " + "		FROM" + "			FF_ChaoSong_" + year + "		WHERE"
            + "			USERID ='" + userId + "'" + sql0 + "		ORDER BY" + "			CREATETIME DESC" + rowstr;
        Connection connection = null;
        try {
            connection = jdbcTemplate.getDataSource().getConnection();
            DbMetaDataUtil dbMetaDataUtil = new DbMetaDataUtil();
            String dialect = dbMetaDataUtil.getDatabaseDialectName(connection);
            if (DialectEnum.ORACLE.getValue().equals(dialect)) {
                rowstr = " WHERE rnum <= " + (startRow + rows) + " and rnum >" + startRow;
                sql = "SELECT * from (" + " SELECT" + "	b.* ,ROWNUM rnum " + " FROM" + "	(" + "		SELECT"
                    + "			* " + "		FROM" + "			FF_ChaoSong_" + year + "		WHERE"
                    + "			USERID ='" + userId + "'" + sql0 + "		ORDER BY" + "			CREATETIME DESC"
                    + "	) b " + ")" + rowstr;
            } else if (DialectEnum.DM.getValue().equals(dialect)) {
                rowstr = " WHERE rnum <= " + (startRow + rows) + " and rnum >" + startRow;
                sql = "SELECT * from (" + " SELECT" + "	b.* ,ROWNUM rnum " + " FROM" + "	(" + "		SELECT"
                    + "			* " + "		FROM" + "			FF_ChaoSong_" + year + "		WHERE"
                    + "			USERID ='" + userId + "'" + sql0 + "		ORDER BY" + "			CREATETIME DESC"
                    + "	) b " + ")" + rowstr;
            }
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
            String sqlCount = "SELECT" + "			count(ID)" + "		FROM" + "			FF_ChaoSong_" + year
                + "		WHERE" + "			USERID ='" + userId + "'" + sql0;
            totalCount = jdbcTemplate.queryForObject(sqlCount, Integer.class);
            SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd HH:mm");
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            HistoricProcessInstanceModel hpi = null;
            ProcessParam processParam = null;
            for (Map<String, Object> m : list) {
                Map<String, Object> map = new HashMap<String, Object>(16);
                try {
                    map.put("id", m.get("ID"));
                    String processInstanceId = (String)m.get("PROCESSINSTANCEID");
                    map.put("createTime", sdf.format(sdf1.parse((String)m.get("CREATETIME"))));
                    map.put("processInstanceId", processInstanceId);
                    map.put("senderName", m.get("SENDERNAME"));
                    map.put("sendDeptId", m.get("SENDDEPTID"));
                    map.put("sendDeptName", m.get("SENDDEPTNAME"));
                    map.put("readTime", sdf.format(sdf1.parse((String)m.get("READTIME"))));
                    map.put("title", m.get("TITLE"));
                    map.put("status", 1);
                    map.put("banjie", false);
                    map.put("itemId", m.get("ITEMID"));
                    map.put("itemName", m.get("ITEMNAME"));
                    processParam = processParamService.findByProcessInstanceId(processInstanceId);
                    String processSerialNumber = processParam.getProcessSerialNumber();
                    String level = processParam.getCustomLevel();
                    map.put("processSerialNumber", processSerialNumber);
                    map.put("number", processParam.getCustomNumber() == null ? "" : processParam.getCustomNumber());
                    map.put("level", level);
                    int chaosongNum = chaoSongRepository.countByUserIdAndProcessInstanceId(userId, processInstanceId);
                    map.put("chaosongNum", chaosongNum);
                    hpi = historicProcessManager.getById(tenantId, processInstanceId);
                    boolean banjie = hpi == null || (hpi != null && hpi.getEndTime() != null);
                    if (banjie) {
                        map.put("banjie", true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                map.put("serialNumber", num + 1);
                num += 1;
                listMap.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        // 获取总页数
        Integer totalpages = (int)Math.round(totalCount / rows);
        retMap.put("currpage", page);
        retMap.put("totalpages", totalpages);
        retMap.put("total", totalCount);
        retMap.put("rows", listMap);
        return retMap;
    }

    @Override
    public Map<String, Object> getDoneListByUserIdAndItemId(String userId, String itemId, int rows, int page) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Map<String, Object> retMap = new HashMap<String, Object>(16);
        List<ChaoSong> csList = new ArrayList<ChaoSong>();
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        Page<ChaoSong> pageList = chaoSongRepository.getDoneListByUserIdAndItemId(userId, itemId, pageable);
        csList = pageList.getContent();
        int num = (page - 1) * rows;
        SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd HH:mm");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        HistoricProcessInstanceModel hpi = null;
        ProcessParam processParam = null;
        for (ChaoSong cs : csList) {
            Map<String, Object> map = new HashMap<String, Object>(16);
            try {
                map.put("id", cs.getId());
                String processInstanceId = cs.getProcessInstanceId();
                map.put("createTime", sdf.format(sdf1.parse(cs.getCreateTime())));
                if (StringUtils.isBlank(cs.getReadTime())) {
                    map.put("readTime", "");
                } else {
                    map.put("readTime", sdf.format(sdf1.parse(cs.getReadTime())));
                }
                processParam = processParamService.findByProcessInstanceId(processInstanceId);
                map.put("processInstanceId", processInstanceId);
                map.put("senderName", cs.getSenderName());
                map.put("sendDeptId", cs.getSendDeptId());
                map.put("sendDeptName", cs.getSendDeptName());
                map.put("title", cs.getTitle());
                map.put("status", cs.getStatus());
                map.put("banjie", false);
                map.put("itemId", cs.getItemId());
                map.put("itemName", cs.getItemName());
                map.put("processSerialNumber", processParam.getProcessSerialNumber());
                map.put("number", processParam.getCustomNumber());
                map.put("level", processParam.getCustomLevel());
                hpi = historicProcessManager.getById(tenantId, processInstanceId);
                boolean banjie = hpi == null || (hpi != null && hpi.getEndTime() != null);
                if (banjie) {
                    map.put("banjie", true);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            map.put("serialNumber", num + 1);
            num += 1;
            listMap.add(map);
        }
        retMap.put("currpage", page);
        retMap.put("totalpages", pageList.getTotalPages());
        retMap.put("total", pageList.getTotalElements());
        retMap.put("rows", listMap);
        return retMap;
    }

    @Override
    public Map<String, Object> getDoneListByUserIdAndSystemName(String userId, String systemName, int rows, int page) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Map<String, Object> retMap = new HashMap<String, Object>(16);
        List<ChaoSong> csList = new ArrayList<ChaoSong>();
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        Page<ChaoSong> pageList = chaoSongRepository.getDoneListByUserIdAndSystemName(userId, systemName, pageable);
        csList = pageList.getContent();
        for (ChaoSong cs : csList) {
            cs.setEnded(false);
            HistoricProcessInstanceModel hpi = historicProcessManager.getById(tenantId, cs.getProcessInstanceId());
            boolean banjie = hpi == null || (hpi != null && hpi.getEndTime() != null);
            if (banjie) {
                cs.setEnded(true);
            }
        }
        retMap.put("currpage", page);
        retMap.put("totalpages", pageList.getTotalPages());
        retMap.put("total", pageList.getTotalElements());
        retMap.put("rows", csList);
        return retMap;
    }

    @Override
    public Map<String, Object> getListByProcessInstanceId(String processInstanceId, String userName, int rows,
        int page) {
        Map<String, Object> retMap = new HashMap<String, Object>(16);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        String tenantId = Y9LoginUserHolder.getTenantId();
        String year = "";
        int totalCount = 0;
        Connection connection = null;
        String senderId = Y9LoginUserHolder.getPersonId();
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        try {
            HistoricProcessInstanceModel historicProcessInstanceModel =
                historicProcessManager.getById(tenantId, processInstanceId);
            if (historicProcessInstanceModel != null) {
                year = sdf.format(historicProcessInstanceModel.getStartTime());
            } else {
                OfficeDoneInfo officeDoneInfoModel = officeDoneInfoService.findByProcessInstanceId(processInstanceId);
                if (officeDoneInfoModel != null && officeDoneInfoModel.getProcessInstanceId() != null) {
                    year = officeDoneInfoModel.getStartTime().substring(0, 4);
                } else {
                    ProcessParam processParam = processParamService.findByProcessInstanceId(processInstanceId);
                    year = processParam.getCreateTime().substring(0, 4);
                    HistoricProcessInstanceModel hpi =
                        historicProcessManager.getByIdAndYear(tenantId, processInstanceId, year);
                    if (hpi == null) {
                        year = String.valueOf(Integer.parseInt(year) + 1);
                    }
                }
            }
            int startRow = (page - 1) * rows;
            String rowstr = " LIMIT " + startRow + "," + (startRow + rows);
            String sql = "";
            String sql0 = "";
            if (StringUtils.isNotBlank(userName)) {
                sql0 = " and USERNAME like '%" + userName + "%'";
            }
            String countsql = "";
            String yearSql = " UNION ALL SELECT" + "			ID," + "			CREATETIME,"
                + "			SENDERNAME," + "			SENDDEPTID," + "			SENDDEPTNAME,"
                + "			USERNAME," + "			USERDEPTNAME," + "			READTIME," + "			STATUS"
                + "		FROM" + "			FF_CHAOSONG_" + year + "		WHERE" + "			processInstanceId = '"
                + processInstanceId + "'" + sql0 + "			AND SENDERID != '" + senderId + "'";

            String yearSql1 = " UNION ALL SELECT" + "			ID," + "			CREATETIME,"
                + "			SENDERNAME," + "			SENDDEPTID," + "			SENDDEPTNAME,"
                + "			USERNAME," + "			USERDEPTNAME," + "			READTIME," + "			STATUS"
                + "		FROM" + "			FF_CHAOSONG_" + String.valueOf((Integer.parseInt(year) + 1))
                + "		WHERE" + "			processInstanceId = '" + processInstanceId + "'" + sql0
                + "			AND SENDERID != '" + senderId + "'";

            String countyearSql1 = " SELECT" + "			count(ID)" + "		FROM" + "			FF_CHAOSONG_"
                + String.valueOf((Integer.parseInt(year) + 1)) + "		WHERE" + "			processInstanceId = '"
                + processInstanceId + "' " + sql0 + "			AND SENDERID != '" + senderId + "'";
            try {
                int countyear = jdbcTemplate.queryForObject(countyearSql1, Integer.class);
                if (countyear == 0) {
                    yearSql1 = "";
                }
            } catch (Exception e) {
                yearSql1 = "";
            }
            connection = jdbcTemplate.getDataSource().getConnection();
            DbMetaDataUtil dbMetaDataUtil = new DbMetaDataUtil();
            String dialect = dbMetaDataUtil.getDatabaseDialectName(connection);
            sql = "	SELECT" + "	t.* " + "	FROM" + "		(" + "		SELECT" + "			ID,"
                + "			CREATETIME," + "			SENDERNAME," + "			SENDDEPTID,"
                + "			SENDDEPTNAME," + "			USERNAME," + "			USERDEPTNAME," + "			READTIME,"
                + "			STATUS" + "		FROM" + "			FF_ChaoSong " + "		WHERE"
                + "			processInstanceId = '" + processInstanceId + "' " + sql0 + "			AND SENDERID != '"
                + senderId + "' " + yearSql + yearSql1 + "		) t " + "	ORDER BY" + "		t.CREATETIME DESC"
                + rowstr;
            if (DialectEnum.ORACLE.getValue().equals(dialect)) {
                rowstr = " WHERE rnum <= " + (startRow + rows) + " and rnum >" + startRow;
                sql = " SELECT * from (" + " SELECT" + "	b.* ,ROWNUM rnum " + " FROM" + "	(" + "	SELECT"
                    + "		* " + "	FROM" + "		(" + "		SELECT" + "			ID," + "			CREATETIME,"
                    + "			SENDERNAME," + "			SENDDEPTID," + "			SENDDEPTNAME,"
                    + "			USERNAME," + "			USERDEPTNAME," + "			READTIME," + "			STATUS"
                    + "		FROM" + "			FF_ChaoSong " + "		WHERE" + "			processInstanceId = '"
                    + processInstanceId + "' " + sql0 + "			AND SENDERID != '" + senderId + "' " + yearSql
                    + yearSql1 + "		) " + "	ORDER BY" + "		CREATETIME DESC" + "	) b" + " )" + rowstr;
            } else if (DialectEnum.DM.getValue().equals(dialect)) {
                rowstr = " WHERE rnum <= " + (startRow + rows) + " and rnum >" + startRow;
                sql = " SELECT * from (" + " SELECT" + "	b.* ,ROWNUM rnum " + " FROM" + "	(" + "	SELECT"
                    + "		* " + "	FROM" + "		(" + "		SELECT" + "			ID," + "			CREATETIME,"
                    + "			SENDERNAME," + "			SENDDEPTID," + "			SENDDEPTNAME,"
                    + "			USERNAME," + "			USERDEPTNAME," + "			READTIME," + "			STATUS"
                    + "		FROM" + "			FF_ChaoSong " + "		WHERE" + "			processInstanceId = '"
                    + processInstanceId + "' " + sql0 + "			AND SENDERID != '" + senderId + "' " + yearSql
                    + yearSql1 + "		) " + "	ORDER BY" + "		CREATETIME DESC" + "	) b" + " )" + rowstr;
            }
            countsql = "	SELECT" + "		count(t.ID) " + "	FROM" + "		(" + "		SELECT" + "			ID,"
                + "			CREATETIME," + "			SENDERNAME," + "			SENDDEPTID,"
                + "			SENDDEPTNAME," + "			USERNAME," + "			USERDEPTNAME," + "			READTIME,"
                + "			STATUS" + "		FROM" + "			FF_ChaoSong " + "		WHERE"
                + "			processInstanceId = '" + processInstanceId + "' " + sql0 + "			AND SENDERID != '"
                + senderId + "' " + yearSql + yearSql1 + "		) t";
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
            totalCount = jdbcTemplate.queryForObject(countsql, Integer.class);
            SimpleDateFormat sdf0 = new SimpleDateFormat("yy/MM/dd HH:mm");
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (Map<String, Object> m : list) {
                Map<String, Object> map = new HashMap<String, Object>(16);
                try {
                    map.put("id", m.get("ID"));
                    map.put("createTime", sdf0.format(sdf1.parse((String)m.get("CREATETIME"))));
                    map.put("processInstanceId", processInstanceId);
                    map.put("senderName", m.get("SENDERNAME"));
                    map.put("sendDeptId", m.get("SENDDEPTID"));
                    map.put("sendDeptName", m.get("SENDDEPTNAME"));
                    if (m.get("READTIME") == null) {
                        map.put("readTime", "");
                    } else {
                        map.put("readTime", sdf0.format(sdf1.parse((String)m.get("READTIME"))));
                    }
                    map.put("userName", m.get("USERNAME"));
                    map.put("userDeptName", m.get("USERDEPTNAME"));
                    map.put("title", m.get("TITLE"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                map.put("serialNumber", startRow + 1);
                startRow += 1;
                listMap.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        // 获取总页数
        Integer totalpages = (int)Math.round(totalCount / rows);
        retMap.put("currpage", page);
        retMap.put("totalpages", totalpages);
        retMap.put("total", totalCount);
        retMap.put("rows", listMap);
        return retMap;
    }

    @Override
    public Map<String, Object> getListBySenderIdAndProcessInstanceId(String senderId, String processInstanceId,
        String userName, int rows, int page) {
        Map<String, Object> retMap = new HashMap<String, Object>(16);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        String tenantId = Y9LoginUserHolder.getTenantId();
        String year = "";
        int totalCount = 0;
        Connection connection = null;
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        try {
            HistoricProcessInstanceModel historicProcessInstanceModel =
                historicProcessManager.getById(tenantId, processInstanceId);
            if (historicProcessInstanceModel != null) {
                year = sdf.format(historicProcessInstanceModel.getStartTime());
            } else {
                OfficeDoneInfo officeDoneInfoModel = officeDoneInfoService.findByProcessInstanceId(processInstanceId);
                if (officeDoneInfoModel != null && officeDoneInfoModel.getProcessInstanceId() != null) {
                    year = officeDoneInfoModel.getStartTime().substring(0, 4);
                } else {
                    ProcessParam processParam = processParamService.findByProcessInstanceId(processInstanceId);
                    year = processParam.getCreateTime().substring(0, 4);
                    HistoricProcessInstanceModel hpi =
                        historicProcessManager.getByIdAndYear(tenantId, processInstanceId, year);
                    if (hpi == null) {
                        year = String.valueOf(Integer.parseInt(year) + 1);
                    }
                }
            }
            int startRow = (page - 1) * rows;
            String rowstr = " LIMIT " + startRow + "," + (startRow + rows);
            String sql = "";
            String sql0 = "";
            if (StringUtils.isNotBlank(userName)) {
                sql0 = " and USERNAME like '%" + userName + "%'";
            }
            String countsql = "";
            String yearSql = " UNION ALL SELECT" + "			ID," + "			CREATETIME,"
                + "			SENDERNAME," + "			SENDDEPTID," + "			SENDDEPTNAME,"
                + "			USERNAME," + "			USERDEPTNAME," + "			READTIME," + "			STATUS"
                + "		FROM" + "			FF_CHAOSONG_" + year + "		WHERE" + "			processInstanceId = '"
                + processInstanceId + "' " + sql0 + "			AND SENDERID = '" + senderId + "'";

            String yearSql1 = " UNION ALL SELECT" + "			ID," + "			CREATETIME,"
                + "			SENDERNAME," + "			SENDDEPTID," + "			SENDDEPTNAME,"
                + "			USERNAME," + "			USERDEPTNAME," + "			READTIME," + "			STATUS"
                + "		FROM" + "			FF_CHAOSONG_" + String.valueOf((Integer.parseInt(year) + 1))
                + "		WHERE" + "			processInstanceId = '" + processInstanceId + "'" + sql0
                + "			AND SENDERID = '" + senderId + "'";

            String countyearSql1 = " SELECT" + "			count(ID)" + "		FROM" + "			FF_CHAOSONG_"
                + String.valueOf((Integer.parseInt(year) + 1)) + "		WHERE" + "			processInstanceId = '"
                + processInstanceId + "' " + sql0 + "			AND SENDERID = '" + senderId + "'";
            try {
                int countyear = jdbcTemplate.queryForObject(countyearSql1, Integer.class);
                if (countyear == 0) {
                    yearSql1 = "";
                }
            } catch (Exception e) {
                yearSql1 = "";
            }
            connection = jdbcTemplate.getDataSource().getConnection();
            DbMetaDataUtil dbMetaDataUtil = new DbMetaDataUtil();
            String dialect = dbMetaDataUtil.getDatabaseDialectName(connection);
            sql = "	SELECT" + "	t.* " + "	FROM" + "		(" + "		SELECT" + "			ID,"
                + "			CREATETIME," + "			SENDERNAME," + "			SENDDEPTID,"
                + "			SENDDEPTNAME," + "			USERNAME," + "			USERDEPTNAME," + "			READTIME,"
                + "			STATUS" + "		FROM" + "			FF_ChaoSong " + "		WHERE"
                + "			processInstanceId = '" + processInstanceId + "' " + sql0 + "			AND SENDERID = '"
                + senderId + "' " + yearSql + yearSql1 + "		) t " + "	ORDER BY" + "		t.CREATETIME DESC"
                + rowstr;
            if (DialectEnum.ORACLE.getValue().equals(dialect)) {
                rowstr = " WHERE rnum <= " + (startRow + rows) + " and rnum >" + startRow;
                sql = " SELECT * from (" + " SELECT" + "	b.* ,ROWNUM rnum " + " FROM" + "	(" + "	SELECT"
                    + "		* " + "	FROM" + "		(" + "		SELECT" + "			ID," + "			CREATETIME,"
                    + "			SENDERNAME," + "			SENDDEPTID," + "			SENDDEPTNAME,"
                    + "			USERNAME," + "			USERDEPTNAME," + "			READTIME," + "			STATUS"
                    + "		FROM" + "			FF_ChaoSong " + "		WHERE" + "			processInstanceId = '"
                    + processInstanceId + "' " + sql0 + "			AND SENDERID = '" + senderId + "' " + yearSql
                    + yearSql1 + "		) " + "	ORDER BY" + "		CREATETIME DESC" + "	) b" + " )" + rowstr;
            } else if (DialectEnum.DM.getValue().equals(dialect)) {
                rowstr = " WHERE rnum <= " + (startRow + rows) + " and rnum >" + startRow;
                sql = " SELECT * from (" + " SELECT" + "	b.* ,ROWNUM rnum " + " FROM" + "	(" + "	SELECT"
                    + "		* " + "	FROM" + "		(" + "		SELECT" + "			ID," + "			CREATETIME,"
                    + "			SENDERNAME," + "			SENDDEPTID," + "			SENDDEPTNAME,"
                    + "			USERNAME," + "			USERDEPTNAME," + "			READTIME," + "			STATUS"
                    + "		FROM" + "			FF_ChaoSong " + "		WHERE" + "			processInstanceId = '"
                    + processInstanceId + "' " + sql0 + "			AND SENDERID = '" + senderId + "' " + yearSql
                    + yearSql1 + "		) " + "	ORDER BY" + "		CREATETIME DESC" + "	) b" + " )" + rowstr;
            }
            countsql = "	SELECT" + "		count(t.ID) " + "	FROM" + "		(" + "		SELECT" + "			ID,"
                + "			CREATETIME," + "			SENDERNAME," + "			SENDDEPTID,"
                + "			SENDDEPTNAME," + "			USERNAME," + "			USERDEPTNAME," + "			READTIME,"
                + "			STATUS" + "		FROM" + "			FF_ChaoSong " + "		WHERE"
                + "			processInstanceId = '" + processInstanceId + "' " + sql0 + "			AND SENDERID = '"
                + senderId + "' " + yearSql + yearSql1 + "		) t";
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
            totalCount = jdbcTemplate.queryForObject(countsql, Integer.class);
            SimpleDateFormat sdf0 = new SimpleDateFormat("yy/MM/dd HH:mm");
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (Map<String, Object> m : list) {
                Map<String, Object> map = new HashMap<String, Object>(16);
                try {
                    map.put("id", m.get("ID"));
                    map.put("createTime", sdf0.format(sdf1.parse((String)m.get("CREATETIME"))));
                    map.put("processInstanceId", processInstanceId);
                    map.put("senderName", m.get("SENDERNAME"));
                    map.put("sendDeptId", m.get("SENDDEPTID"));
                    map.put("sendDeptName", m.get("SENDDEPTNAME"));
                    if (m.get("READTIME") == null) {
                        map.put("readTime", "");
                    } else {
                        map.put("readTime", sdf0.format(sdf1.parse((String)m.get("READTIME"))));
                    }
                    map.put("userName", m.get("USERNAME"));
                    map.put("userDeptName", m.get("USERDEPTNAME"));
                    map.put("title", m.get("TITLE"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                map.put("serialNumber", startRow + 1);
                startRow += 1;
                listMap.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        // 获取总页数
        Integer totalpages = (int)Math.round(totalCount / rows);
        retMap.put("currpage", page);
        retMap.put("totalpages", totalpages);
        retMap.put("total", totalCount);
        retMap.put("rows", listMap);
        return retMap;
    }

    @Override
    public Map<String, Object> getOpinionChaosongByUserId(String userId, String year, String documentTitle, int rows,
        int page) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Map<String, Object> retMap = new HashMap<String, Object>(16);
        if (StringUtils.isBlank(year)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
            year = sdf.format(new Date());
        }
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        int num = (page - 1) * rows;
        int totalCount = 0;
        int startRow = (page - 1) * rows;
        String rowstr = " LIMIT " + startRow + "," + (startRow + rows);
        String sql0 = "";
        if (StringUtils.isNotBlank(documentTitle)) {
            sql0 += " and TITLE like '%" + documentTitle + "%'";
        }
        String sql = "		SELECT" + "			* " + "		FROM" + "			FF_ChaoSong_" + year + "		WHERE"
            + "			USERID ='" + userId + "' and opinionState = '1' " + sql0 + "		ORDER BY"
            + "			CREATETIME DESC" + rowstr;
        Connection connection = null;
        try {
            connection = jdbcTemplate.getDataSource().getConnection();
            DbMetaDataUtil dbMetaDataUtil = new DbMetaDataUtil();
            String dialect = dbMetaDataUtil.getDatabaseDialectName(connection);
            if (DialectEnum.ORACLE.getValue().equals(dialect)) {
                rowstr = " WHERE rnum <= " + (startRow + rows) + " and rnum >" + startRow;
                sql = "SELECT * from (" + " SELECT" + "	b.* ,ROWNUM rnum " + " FROM" + "	(" + "		SELECT"
                    + "			* " + "		FROM" + "			FF_ChaoSong_" + year + "		WHERE"
                    + "			USERID ='" + userId + "' and opinionState = '1' " + sql0 + "		ORDER BY"
                    + "			CREATETIME DESC" + "	) b " + ")" + rowstr;
            }
            if (DialectEnum.DM.getValue().equals(dialect)) {
                rowstr = " WHERE rnum <= " + (startRow + rows) + " and rnum >" + startRow;
                sql = "SELECT * from (" + " SELECT" + "	b.* ,ROWNUM rnum " + " FROM" + "	(" + "		SELECT"
                    + "			* " + "		FROM" + "			FF_ChaoSong_" + year + "		WHERE"
                    + "			USERID ='" + userId + "' and opinionState = '1' " + sql0 + "		ORDER BY"
                    + "			CREATETIME DESC" + "	) b " + ")" + rowstr;
            }
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
            String sqlCount = "SELECT" + "			count(ID)" + "		FROM" + "			FF_ChaoSong_" + year
                + "		WHERE" + "			USERID ='" + userId + "' and opinionState = '1' " + sql0;
            totalCount = jdbcTemplate.queryForObject(sqlCount, Integer.class);
            SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd HH:mm");
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            HistoricProcessInstanceModel hpi = null;
            ProcessParam processParam = null;
            for (Map<String, Object> m : list) {
                Map<String, Object> map = new HashMap<String, Object>(16);
                try {
                    map.put("id", m.get("ID"));
                    String processInstanceId = (String)m.get("PROCESSINSTANCEID");
                    map.put("createTime", sdf.format(sdf1.parse((String)m.get("CREATETIME"))));
                    map.put("processInstanceId", processInstanceId);
                    map.put("senderName", m.get("SENDERNAME"));
                    map.put("sendDeptId", m.get("SENDDEPTID"));
                    map.put("sendDeptName", m.get("SENDDEPTNAME"));
                    map.put("readTime", sdf.format(sdf1.parse((String)m.get("READTIME"))));
                    map.put("title", m.get("TITLE"));
                    map.put("status", 1);
                    map.put("banjie", false);
                    map.put("itemId", m.get("ITEMID"));
                    map.put("itemName", m.get("ITEMNAME"));
                    processParam = processParamService.findByProcessInstanceId(processInstanceId);
                    String processSerialNumber = processParam.getProcessSerialNumber();
                    String level = processParam.getCustomLevel();
                    map.put("processSerialNumber", processSerialNumber);
                    map.put("number", processParam.getCustomNumber() == null ? "" : processParam.getCustomNumber());
                    map.put("level", level);
                    int chaosongNum = chaoSongRepository.countByUserIdAndProcessInstanceId(userId, processInstanceId);
                    map.put("chaosongNum", chaosongNum);
                    hpi = historicProcessManager.getById(tenantId, processInstanceId);
                    boolean banjie = hpi == null || (hpi != null && hpi.getEndTime() != null);
                    if (banjie) {
                        map.put("banjie", true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                map.put("serialNumber", num + 1);
                num += 1;
                listMap.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        // 获取总页数
        Integer totalpages = (int)Math.round(totalCount / rows);
        retMap.put("currpage", page);
        retMap.put("totalpages", totalpages);
        retMap.put("total", totalCount);
        retMap.put("rows", listMap);
        return retMap;
    }

    @Override
    public int getTodoCount4NewByUserId(String userId) {
        return chaoSongRepository.getTodoCount4NewByUserId(userId);
    }

    @Override
    public int getTodoCountByUserId(String userId) {
        return chaoSongRepository.getTodoCountByUserId(userId);
    }

    @Override
    public int getTodoCountByUserIdAndItemId(String userId, String itemId) {
        return chaoSongRepository.getTodoCountByUserIdAndItemId(userId, itemId);
    }

    @Override
    public int getTodoCountByUserIdAndSystemName(String userId, String systemName) {
        return chaoSongRepository.getTodoCountByUserIdAndSystemName(userId, systemName);
    }

    @Override
    public Map<String, Object> getTodoListByUserId(String userId, String documentTitle, int rows, int page) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Map<String, Object> retMap = new HashMap<String, Object>(16);
        List<ChaoSong> csList = new ArrayList<ChaoSong>();
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        Page<ChaoSong> pageList = null;
        if (StringUtils.isNotBlank(documentTitle)) {
            pageList = chaoSongRepository.getTodoByUserIdAndTitleLike(userId, "%" + documentTitle + "%", pageable);
        } else {
            pageList = chaoSongRepository.getTodoListByUserId(userId, pageable);
        }
        csList = pageList.getContent();
        SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd HH:mm");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int num = (page - 1) * rows;
        HistoricProcessInstanceModel hpi = null;
        ProcessParam processParam = null;
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        for (ChaoSong cs : csList) {
            Map<String, Object> map = new HashMap<String, Object>(16);
            try {
                map.put("id", cs.getId());
                String processInstanceId = cs.getProcessInstanceId();
                map.put("createTime", sdf.format(sdf1.parse(cs.getCreateTime())));
                processParam = processParamService.findByProcessInstanceId(processInstanceId);
                map.put("processInstanceId", processInstanceId);
                map.put("senderName", cs.getSenderName());
                map.put("sendDeptId", cs.getSendDeptId());
                map.put("sendDeptName", cs.getSendDeptName());
                map.put("readTime", cs.getReadTime());
                map.put("title", processParam.getTitle());
                map.put("status", cs.getStatus());
                map.put("banjie", false);
                map.put("itemId", cs.getItemId());
                map.put("itemName", cs.getItemName());
                map.put("processSerialNumber", processParam.getProcessSerialNumber());
                map.put("number", processParam.getCustomNumber());
                map.put("level", processParam.getCustomLevel());
                int chaosongNum = chaoSongRepository.countByUserIdAndProcessInstanceId(userId, processInstanceId);
                map.put("chaosongNum", chaosongNum);
                hpi = historicProcessManager.getById(tenantId, processInstanceId);
                boolean banjie = hpi == null || (hpi != null && hpi.getEndTime() != null);
                if (banjie) {
                    map.put("banjie", true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            map.put("serialNumber", num + 1);
            num += 1;
            listMap.add(map);
        }
        retMap.put("currpage", page);
        retMap.put("totalpages", pageList.getTotalPages());
        retMap.put("total", pageList.getTotalElements());
        retMap.put("rows", listMap);
        return retMap;
    }

    @Override
    public Map<String, Object> getTodoListByUserIdAndItemId(String userId, String itemId, int rows, int page) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Map<String, Object> retMap = new HashMap<String, Object>(16);
        List<ChaoSong> csList = new ArrayList<ChaoSong>();
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        Page<ChaoSong> pageList = chaoSongRepository.getTodoListByUserIdAndItemId(userId, itemId, pageable);
        csList = pageList.getContent();
        SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd HH:mm");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int num = (page - 1) * rows;
        HistoricProcessInstanceModel hpi = null;
        ProcessParam processParam = null;
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        for (ChaoSong cs : csList) {
            Map<String, Object> map = new HashMap<String, Object>(16);
            try {
                map.put("id", cs.getId());
                String processInstanceId = cs.getProcessInstanceId();
                map.put("createTime", sdf.format(sdf1.parse(cs.getCreateTime())));
                processParam = processParamService.findByProcessInstanceId(processInstanceId);
                map.put("processInstanceId", processInstanceId);
                map.put("senderName", cs.getSenderName());
                map.put("sendDeptId", cs.getSendDeptId());
                map.put("sendDeptName", cs.getSendDeptName());
                map.put("readTime", cs.getReadTime());
                map.put("title", processParam.getTitle());
                map.put("status", cs.getStatus());
                map.put("banjie", false);
                map.put("itemId", cs.getItemId());
                map.put("itemName", cs.getItemName());
                map.put("processSerialNumber", processParam.getProcessSerialNumber());
                map.put("number", processParam.getCustomNumber());
                map.put("level", processParam.getCustomLevel());
                hpi = historicProcessManager.getById(tenantId, processInstanceId);
                boolean banjie = hpi == null || (hpi != null && hpi.getEndTime() != null);
                if (banjie) {
                    map.put("banjie", true);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            map.put("serialNumber", num + 1);
            num += 1;
            listMap.add(map);
        }
        retMap.put("currpage", page);
        retMap.put("totalpages", pageList.getTotalPages());
        retMap.put("total", pageList.getTotalElements());
        retMap.put("rows", listMap);
        return retMap;
    }

    @Override
    public Map<String, Object> getTodoListByUserIdAndItemIdAndTitle(String userId, String itemId, String title,
        int rows, int page) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Map<String, Object> retMap = new HashMap<String, Object>(16);
        List<ChaoSong> csList = new ArrayList<ChaoSong>();
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        Page<ChaoSong> pageList =
            chaoSongRepository.getTodoListByUserIdAndItemIdAndTitle(userId, itemId, "%" + title + "%", pageable);
        csList = pageList.getContent();
        for (ChaoSong cs : csList) {
            cs.setEnded(false);
            HistoricProcessInstanceModel hpi = historicProcessManager.getById(tenantId, cs.getProcessInstanceId());
            boolean banjie = hpi == null || (hpi != null && hpi.getEndTime() != null);
            if (banjie) {
                cs.setEnded(true);
            }
        }
        retMap.put("currpage", page);
        retMap.put("totalpages", pageList.getTotalPages());
        retMap.put("total", pageList.getTotalElements());
        retMap.put("rows", csList);
        return retMap;
    }

    @Override
    public Map<String, Object> getTodoListByUserIdAndSystemName(String userId, String systemName, int rows, int page) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Map<String, Object> retMap = new HashMap<String, Object>(16);
        List<ChaoSong> csList = new ArrayList<ChaoSong>();
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        Page<ChaoSong> pageList = chaoSongRepository.getTodoListByUserIdAndSystemName(userId, systemName, pageable);
        csList = pageList.getContent();
        for (ChaoSong cs : csList) {
            cs.setEnded(false);
            HistoricProcessInstanceModel hpi = historicProcessManager.getById(tenantId, cs.getProcessInstanceId());
            boolean banjie = hpi == null || (hpi != null && hpi.getEndTime() != null);
            if (banjie) {
                cs.setEnded(true);
            }
        }
        retMap.put("currpage", page);
        retMap.put("totalpages", pageList.getTotalPages());
        retMap.put("total", pageList.getTotalElements());
        retMap.put("rows", csList);
        return retMap;
    }

    @Override
    public Map<String, Object> getTodoListByUserIdAndSystemNameAndTitle(String userId, String systemName, String title,
        int rows, int page) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Map<String, Object> retMap = new HashMap<String, Object>(16);
        List<ChaoSong> csList = new ArrayList<ChaoSong>();
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        Page<ChaoSong> pageList = chaoSongRepository.getTodoListByUserIdAndSystemNameAndTitle(userId, systemName,
            "%" + title + "%", pageable);
        csList = pageList.getContent();
        for (ChaoSong cs : csList) {
            cs.setEnded(false);
            HistoricProcessInstanceModel hpi = historicProcessManager.getById(tenantId, cs.getProcessInstanceId());
            boolean banjie = hpi == null || (hpi != null && hpi.getEndTime() != null);
            if (banjie) {
                cs.setEnded(true);
            }
        }
        retMap.put("currpage", page);
        retMap.put("totalpages", pageList.getTotalPages());
        retMap.put("total", pageList.getTotalElements());
        retMap.put("rows", csList);
        return retMap;
    }

    @Override
    @Transactional(readOnly = false)
    public ChaoSong save(ChaoSong chaoSong) {
        return chaoSongRepository.save(chaoSong);
    }

    @Override
    @Transactional(readOnly = false)
    public void save(List<ChaoSong> chaoSongList) {
        chaoSongRepository.saveAll(chaoSongList);
    }

    @Override
    @Transactional(readOnly = false)
    public Map<String, Object> save(String processInstanceId, String users, String isSendSms, String isShuMing,
        String smsContent, String smsPersonId) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put(UtilConsts.SUCCESS, false);
        map.put("msg", "抄送失败");
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            UserInfo person = Y9LoginUserHolder.getUserInfo();
            String tenantId = Y9LoginUserHolder.getTenantId(), personId = person.getPersonId(),
                personName = person.getName();
            ProcessParam processParam = processParamService.findByProcessInstanceId(processInstanceId);
            String title = processParam.getTitle(), itemId = processParam.getItemId(),
                itemName = processParam.getItemName(), systemName = processParam.getSystemName();
            List<String> orgUnitList = Arrays.asList(users.split(";"));
            List<ChaoSong> csList = new ArrayList<ChaoSong>();
            List<String> userIdListAdd = new ArrayList<String>();
            // 添加的人员
            for (String orgUnitStr : orgUnitList) {
                String[] orgUnitArr = orgUnitStr.split(":");
                Integer type = Integer.valueOf(orgUnitArr[0]);
                String orgUnitId = orgUnitArr[1];
                List<Person> personListTemp = new ArrayList<Person>();
                if (2 == type) {
                    personListTemp = departmentManager.listAllPersonsByDisabled(tenantId, orgUnitId, false).getData();
                    for (Person personTemp : personListTemp) {
                        userIdListAdd.add(personTemp.getId() + ":" + orgUnitId);
                    }
                } else if (3 == type) {
                    userIdListAdd.add(orgUnitId + ":" + orgUnitArr[2]);
                } else if (7 == type) {
                    // TODO
                }
            }
            // 保存抄送
            OrgUnit dept = departmentManager.getDepartment(tenantId, person.getParentId()).getData();
            if (null == dept || null == dept.getId()) {
                dept = organizationManager.getOrganization(tenantId, person.getParentId()).getData();
            }
            List<String> mobile = new ArrayList<String>();
            for (String userIds : userIdListAdd) {
                String[] id = userIds.split(SysVariables.COLON);
                Person personTemp = personManager.getPerson(tenantId, id[0]).getData();
                ChaoSong cs = new ChaoSong();
                cs.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                cs.setCreateTime(sdf.format(new Date()));
                cs.setProcessInstanceId(processInstanceId);
                cs.setSenderId(personId);
                cs.setSenderName(personName);
                cs.setSendDeptId(dept.getId());
                cs.setSendDeptName(dept.getName());
                cs.setStatus(2);
                cs.setTenantId(tenantId);
                cs.setTitle(title);
                cs.setUserId(personTemp.getId());
                cs.setUserName(personTemp.getName());
                Department department = departmentManager.getDepartment(tenantId, id[1]).getData();
                cs.setUserDeptId(department.getId());
                cs.setUserDeptName(department.getName());
                cs.setItemId(itemId);
                cs.setItemName(itemName);
                cs.setSystemName(systemName);
                csList.add(cs);
                if (StringUtils.isNotEmpty(personTemp.getMobile())) {
                    if (StringUtils.isNotBlank(smsPersonId)) {
                        if (smsPersonId.contains(personTemp.getId())) {
                            mobile.add(personTemp.getMobile());
                        }
                    } else {
                        mobile.add(personTemp.getMobile());
                    }
                }
            }
            this.save(csList);
            if (StringUtils.isNotBlank(isSendSms) && isSendSms.equals(UtilConsts.TRUE)) {
                smsContent += "--" + person.getName();
                Boolean smsSwitch = y9Conf.getApp().getItemAdmin().getSmsSwitch();
                if (smsSwitch) {
                    smsHttpManager.sendSmsHttpList(tenantId, personId, mobile, smsContent, systemName + "抄送");
                } else {
                    LOGGER
                        .info("*********************y9.app.itemAdmin.smsSwitch开关未打开**********************************");
                }
            }
            asyncHandleService.weiXinRemind(tenantId, personId, processParam.getProcessSerialNumber(), csList);
            map.put(UtilConsts.SUCCESS, true);
            map.put("msg", "抄送成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    @Override
    @Transactional(readOnly = false)
    public void updateTitle(String processInstanceId, String documentTitle) {
        try {
            chaoSongRepository.updateTitle(processInstanceId, documentTitle);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
            String year = sdf.format(new Date());
            String sql = "select count(t.ID) from FF_ChaoSong_" + year + " t where t.PROCESSINSTANCEID = '"
                + processInstanceId + "'";
            int totalCount = jdbcTemplate.queryForObject(sql, Integer.class);
            if (totalCount > 0) {
                sql = "update FF_ChaoSong_" + year + " t set t.title='" + documentTitle
                    + "' where t.PROCESSINSTANCEID = '" + processInstanceId + "'";
                jdbcTemplate.execute(sql);
            } else {
                sql = "select count(t.ID) from FF_ChaoSong_" + String.valueOf(Integer.parseInt(year) - 1)
                    + " t where t.PROCESSINSTANCEID = '" + processInstanceId + "'";
                totalCount = jdbcTemplate.queryForObject(sql, Integer.class);
                if (totalCount > 0) {
                    sql = "update FF_ChaoSong_" + String.valueOf(Integer.parseInt(year) - 1) + " t set t.title='"
                        + documentTitle + "' where t.PROCESSINSTANCEID = '" + processInstanceId + "'";
                    jdbcTemplate.execute(sql);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
