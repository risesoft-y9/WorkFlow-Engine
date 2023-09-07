package net.risesoft.controller.sync;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.org.OrgUnitApi;
import net.risesoft.api.org.PositionApi;
import net.risesoft.api.processadmin.HistoricTaskApi;
import net.risesoft.entity.ActRuDetail;
import net.risesoft.entity.ErrorLog;
import net.risesoft.entity.ProcessParam;
import net.risesoft.enums.DialectEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.OrgUnit;
import net.risesoft.model.Position;
import net.risesoft.model.itemadmin.ErrorLogModel;
import net.risesoft.model.processadmin.HistoricTaskInstanceModel;
import net.risesoft.nosql.elastic.entity.OfficeDoneInfo;
import net.risesoft.service.ActRuDetailService;
import net.risesoft.service.DataCenterService;
import net.risesoft.service.ErrorLogService;
import net.risesoft.service.OfficeDoneInfoService;
import net.risesoft.service.ProcessParamService;
import net.risesoft.util.form.DbMetaDataUtil;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.util.Y9Util;

/**
 * 同步办结数据到数据中心
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequestMapping("/services/rest/datacenter")
@Slf4j
public class Sync2DataCenterController {

    @Resource(name = "jdbcTemplate4Tenant")
    private JdbcTemplate jdbcTemplate;

    @Resource(name = "jdbcTemplate4Public")
    private JdbcTemplate jdbcTemplate4Public;

    @Autowired
    private DataCenterService dataCenterService;

    @Autowired
    private ErrorLogService errorLogService;

    @Autowired
    private ActRuDetailService actRuDetailService;

    @Autowired
    private ProcessParamService processParamService;

    @Autowired
    private HistoricTaskApi historicTaskManager;

    @Autowired
    private PositionApi positionApi;

    @Autowired
    private OrgUnitApi orgUnitApi;

    @Autowired
    private OfficeDoneInfoService officeDoneInfoService;

    @ResponseBody
    @RequestMapping(value = "/tongbu2DataCenter")
    public void tongbu2DataCenter(String tenantId, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> resMap = new HashMap<String, Object>(16);
        Connection connection = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Y9LoginUserHolder.setTenantId(tenantId);
            String sql = "SELECT" + "   P .PROC_INST_ID_," + "  TO_CHAR(P .START_TIME_,'yyyy-MM-dd HH:mi:ss') as START_TIME_," + "  P .PROC_DEF_ID_" + " FROM" + "  ACT_HI_PROCINST P" + " WHERE" + "   P .END_TIME_ IS NULL" + " AND P .DELETE_REASON_ IS NULL" + " ORDER BY" + "  P .START_TIME_ DESC";
            DataSource dataSource = jdbcTemplate.getDataSource();
            DbMetaDataUtil dbMetaDataUtil = new DbMetaDataUtil();
            connection = dataSource.getConnection();
            String dialectName = dbMetaDataUtil.getDatabaseDialectName(connection);
            if (DialectEnum.MYSQL.getValue().equals(dialectName)) {
                sql = "SELECT" + "  P .PROC_INST_ID_," + "  SUBSTRING(P.START_TIME_,1,19) as START_TIME_," + "  P .PROC_DEF_ID_" + " FROM" + "  ACT_HI_PROCINST_2023 P" + " WHERE" + "   P .END_TIME_ IS NOT NULL" + " AND P .DELETE_REASON_ IS NULL" + " ORDER BY" + "  P .START_TIME_ DESC";
            }
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
            LOGGER.info("*********************共{}条数据***************************", list.size());
            int i = 0;
            String processInstanceId = "";
            for (Map<String, Object> map : list) {
                try {
                    processInstanceId = (String)map.get("PROC_INST_ID_");
                    String processDefinitionId = (String)map.get("PROC_DEF_ID_");
                    dataCenterService.saveToDateCenter1(processInstanceId, processDefinitionId);
                } catch (Exception e) {
                    i = i + 1;
                    final Writer result = new StringWriter();
                    final PrintWriter print = new PrintWriter(result);
                    e.printStackTrace(print);
                    String msg = result.toString();
                    String time = sdf.format(new Date());
                    ErrorLog errorLogModel = new ErrorLog();
                    errorLogModel.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                    errorLogModel.setCreateTime(time);
                    errorLogModel.setErrorFlag(ErrorLogModel.ERROR_FLAG_SAVE_OFFICE_DONE);
                    errorLogModel.setErrorType(ErrorLogModel.ERROR_PROCESS_INSTANCE);
                    errorLogModel.setExtendField("同步办结数据到数据中心");
                    errorLogModel.setProcessInstanceId(processInstanceId);
                    errorLogModel.setTaskId("");
                    errorLogModel.setText(msg);
                    errorLogModel.setUpdateTime(time);
                    try {
                        errorLogService.saveErrorLog(errorLogModel);
                    } catch (Exception e1) {
                    }
                    e.printStackTrace();
                }
            }
            LOGGER.info("********************同步失败{}条数据***************************", i);
            resMap.put("总数", list.size());
            resMap.put("同步失败", i);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(resMap));
    }

    /**
     * 同步已办结办件详情
     *
     * @param tenantId
     * @param request
     * @param response
     */
    @ResponseBody
    @RequestMapping(value = "/tongbuActRuDetail")
    public void tongbuActRuDetail(String tenantId, String year, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> resMap = new HashMap<String, Object>(16);
        Connection connection = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Y9LoginUserHolder.setTenantId(tenantId);
            String sql = "SELECT" + "   P .PROC_INST_ID_," + "  TO_CHAR(P .START_TIME_,'yyyy-MM-dd HH:mi:ss') as START_TIME_," + "  P .PROC_DEF_ID_" + " FROM" + "  ACT_HI_PROCINST P" + " WHERE" + "   P .END_TIME_ IS NULL" + " AND P .DELETE_REASON_ IS NULL" + " ORDER BY" + "  P .START_TIME_ DESC";
            DataSource dataSource = jdbcTemplate.getDataSource();
            DbMetaDataUtil dbMetaDataUtil = new DbMetaDataUtil();
            connection = dataSource.getConnection();
            String dialectName = dbMetaDataUtil.getDatabaseDialectName(connection);
            if (DialectEnum.MYSQL.getValue().equals(dialectName)) {
                sql = "SELECT  P .PROC_INST_ID_, SUBSTRING(P.START_TIME_,1,19) as START_TIME_,  P .PROC_DEF_ID_ FROM  ACT_HI_PROCINST_" + year + " P WHERE P .END_TIME_ IS NOT NULL AND P .DELETE_REASON_ IS NULL ORDER BY P .START_TIME_ DESC";
            }
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
            LOGGER.info("*********************共{}条数据***************************", list.size());
            int i = 0;
            String processInstanceId = "";
            for (Map<String, Object> map : list) {
                try {
                    processInstanceId = (String)map.get("PROC_INST_ID_");
                    List<HistoricTaskInstanceModel> htiList = historicTaskManager.findTaskByProcessInstanceIdOrByEndTimeAsc(tenantId, processInstanceId, year);
                    ActRuDetail newActRuDetail = null;
                    String assignee = null;
                    String owner = null;
                    for (HistoricTaskInstanceModel hti : htiList) {
                        newActRuDetail = new ActRuDetail();
                        assignee = hti.getAssignee();
                        ProcessParam processParam = processParamService.findByProcessInstanceId(processInstanceId);
                        String systemName = processParam.getSystemName();
                        if (StringUtils.isNotBlank(assignee)) {
                            /**
                             * 1owner不为空，是恢复待办且恢复的人员不是办理人员的情况，要取出owner,并保存
                             * owner的Status为1，并判断当前taskId是不是正在运行，正在运行的话assignee的Status为0否则为1(因为恢复待办的时候，没有把历史任务的结束时间设为null)
                             */
                            owner = hti.getOwner();
                            if (StringUtils.isNotBlank(owner)) {
                                /** 先保存owner */
                                newActRuDetail.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                                newActRuDetail.setProcessSerialNumber(processParam.getProcessSerialNumber());
                                newActRuDetail.setAssignee(owner);
                                Position position = positionApi.getPosition(tenantId, owner);
                                newActRuDetail.setAssigneeName(position.getName());
                                newActRuDetail.setDeptId(position.getParentId());
                                OrgUnit orgUnit = orgUnitApi.getOrgUnit(tenantId, position.getParentId());
                                newActRuDetail.setDeptName(orgUnit.getName());
                                newActRuDetail.setCreateTime(hti.getStartTime());
                                newActRuDetail.setLastTime(hti.getEndTime());
                                newActRuDetail.setProcessDefinitionKey(hti.getProcessDefinitionId().split(":")[0]);
                                newActRuDetail.setSystemName(processParam.getSystemName());
                                newActRuDetail.setStatus(1);
                                newActRuDetail.setTaskId(hti.getId());
                                newActRuDetail.setStarted(true);
                                newActRuDetail.setEnded(true);
                                newActRuDetail.setStartTime(map.get("START_TIME_").toString());
                                newActRuDetail.setItemId(processParam.getItemId());
                                newActRuDetail.setProcessInstanceId(hti.getProcessInstanceId());
                                newActRuDetail.setItemId(processParam.getItemId());
                                newActRuDetail.setSystemName(systemName);
                                actRuDetailService.saveOrUpdate(newActRuDetail);
                            }
                            /** 再保存assignee */
                            newActRuDetail = new ActRuDetail();
                            newActRuDetail.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                            newActRuDetail.setProcessSerialNumber(processParam.getProcessSerialNumber());
                            newActRuDetail.setAssignee(assignee);
                            Position position1 = positionApi.getPosition(tenantId, assignee);
                            newActRuDetail.setAssigneeName(position1.getName());
                            newActRuDetail.setDeptId(position1.getParentId());
                            OrgUnit orgUnit1 = orgUnitApi.getOrgUnit(tenantId, position1.getParentId());
                            newActRuDetail.setDeptName(orgUnit1.getName());
                            newActRuDetail.setCreateTime(hti.getStartTime());
                            newActRuDetail.setLastTime(hti.getEndTime());
                            newActRuDetail.setProcessDefinitionKey(hti.getProcessDefinitionId().split(":")[0]);
                            newActRuDetail.setSystemName(processParam.getSystemName());
                            newActRuDetail.setStatus(1);
                            newActRuDetail.setTaskId(hti.getId());
                            newActRuDetail.setStarted(true);
                            newActRuDetail.setEnded(true);
                            newActRuDetail.setStartTime(map.get("START_TIME_").toString());
                            newActRuDetail.setItemId(processParam.getItemId());
                            newActRuDetail.setProcessInstanceId(hti.getProcessInstanceId());
                            newActRuDetail.setItemId(processParam.getItemId());
                            newActRuDetail.setSystemName(systemName);
                            actRuDetailService.saveOrUpdate(newActRuDetail);
                        }
                    }

                } catch (Exception e) {
                    i = i + 1;
                    final Writer result = new StringWriter();
                    final PrintWriter print = new PrintWriter(result);
                    e.printStackTrace(print);
                    String msg = result.toString();
                    String time = sdf.format(new Date());
                    ErrorLog errorLogModel = new ErrorLog();
                    errorLogModel.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                    errorLogModel.setCreateTime(time);
                    errorLogModel.setErrorFlag(ErrorLogModel.ERROR_FLAG_SAVE_OFFICE_DONE);
                    errorLogModel.setErrorType(ErrorLogModel.ERROR_PROCESS_INSTANCE);
                    errorLogModel.setExtendField("同步办结数据到数据中心");
                    errorLogModel.setProcessInstanceId(processInstanceId);
                    errorLogModel.setTaskId("");
                    errorLogModel.setText(msg);
                    errorLogModel.setUpdateTime(time);
                    try {
                        errorLogService.saveErrorLog(errorLogModel);
                    } catch (Exception e1) {
                    }
                    e.printStackTrace();
                }
            }
            LOGGER.info("********************同步失败{}条数据***************************", i);
            resMap.put("总数", list.size());
            resMap.put("同步失败", i);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(resMap));
    }

    /**
     * 同步未办结办件详情
     *
     * @param tenantId
     * @param request
     * @param response
     */
    @ResponseBody
    @RequestMapping(value = "/tongbuActRuDetail1")
    public void tongbuActRuDetail1(String tenantId, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> resMap = new HashMap<String, Object>(16);
        Connection connection = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Y9LoginUserHolder.setTenantId(tenantId);
            String sql = "SELECT" + "   P .PROC_INST_ID_," + "  TO_CHAR(P .START_TIME_,'yyyy-MM-dd HH:mi:ss') as START_TIME_," + "  P .PROC_DEF_ID_" + " FROM" + "  ACT_HI_PROCINST P" + " WHERE" + "   P .END_TIME_ IS NULL" + " AND P .DELETE_REASON_ IS NULL" + " ORDER BY" + "  P .START_TIME_ DESC";
            DataSource dataSource = jdbcTemplate.getDataSource();
            DbMetaDataUtil dbMetaDataUtil = new DbMetaDataUtil();
            connection = dataSource.getConnection();
            String dialectName = dbMetaDataUtil.getDatabaseDialectName(connection);
            if (DialectEnum.MYSQL.getValue().equals(dialectName)) {
                sql = "SELECT P .PROC_INST_ID_,  SUBSTRING(P.START_TIME_,1,19) as START_TIME_,  P .PROC_DEF_ID_ FROM  ACT_HI_PROCINST P WHERE P .DELETE_REASON_ IS NULL ORDER BY  P .START_TIME_ DESC";
            }
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
            LOGGER.info("*********************共{}条数据***************************", list.size());
            int i = 0;
            String processInstanceId = "";
            for (Map<String, Object> map : list) {
                try {
                    processInstanceId = (String)map.get("PROC_INST_ID_");
                    List<HistoricTaskInstanceModel> htiList = historicTaskManager.findTaskByProcessInstanceIdOrderByStartTimeAsc(tenantId, processInstanceId, "");
                    ActRuDetail newActRuDetail = null;
                    String assignee = null;
                    String owner = null;
                    for (HistoricTaskInstanceModel hti : htiList) {
                        newActRuDetail = new ActRuDetail();
                        assignee = hti.getAssignee();
                        ProcessParam processParam = processParamService.findByProcessInstanceId(processInstanceId);
                        String systemName = processParam.getSystemName();
                        if (StringUtils.isNotBlank(assignee)) {
                            /**
                             * 1owner不为空，是恢复待办且恢复的人员不是办理人员的情况，要取出owner,并保存
                             * owner的Status为1，并判断当前taskId是不是正在运行，正在运行的话assignee的Status为0否则为1(因为恢复待办的时候，没有把历史任务的结束时间设为null)
                             */
                            owner = hti.getOwner();
                            if (StringUtils.isNotBlank(owner)) {
                                /** 先保存owner */
                                newActRuDetail.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                                newActRuDetail.setProcessSerialNumber(processParam.getProcessSerialNumber());
                                newActRuDetail.setAssignee(owner);
                                Position position = positionApi.getPosition(tenantId, owner);
                                newActRuDetail.setAssigneeName(position.getName());
                                newActRuDetail.setDeptId(position.getParentId());
                                OrgUnit orgUnit = orgUnitApi.getOrgUnit(tenantId, position.getParentId());
                                newActRuDetail.setDeptName(orgUnit.getName());
                                newActRuDetail.setCreateTime(hti.getStartTime());
                                newActRuDetail.setLastTime(hti.getEndTime());
                                newActRuDetail.setProcessDefinitionKey(hti.getProcessDefinitionId().split(":")[0]);
                                newActRuDetail.setSystemName(processParam.getSystemName());
                                newActRuDetail.setStatus(1);
                                newActRuDetail.setTaskId(hti.getId());
                                newActRuDetail.setStarted(true);
                                newActRuDetail.setEnded(false);
                                newActRuDetail.setStartTime(map.get("START_TIME_").toString());
                                newActRuDetail.setItemId(processParam.getItemId());
                                newActRuDetail.setProcessInstanceId(hti.getProcessInstanceId());
                                newActRuDetail.setItemId(processParam.getItemId());
                                newActRuDetail.setSystemName(systemName);
                                actRuDetailService.saveOrUpdate(newActRuDetail);
                            }
                            /** 再保存assignee */
                            newActRuDetail = new ActRuDetail();
                            newActRuDetail.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                            newActRuDetail.setProcessSerialNumber(processParam.getProcessSerialNumber());
                            newActRuDetail.setAssignee(assignee);
                            Position position1 = positionApi.getPosition(tenantId, assignee);
                            newActRuDetail.setAssigneeName(position1.getName());
                            newActRuDetail.setDeptId(position1.getParentId());
                            OrgUnit orgUnit1 = orgUnitApi.getOrgUnit(tenantId, position1.getParentId());
                            newActRuDetail.setDeptName(orgUnit1.getName());
                            newActRuDetail.setCreateTime(hti.getStartTime());
                            newActRuDetail.setLastTime(hti.getEndTime());
                            newActRuDetail.setProcessDefinitionKey(hti.getProcessDefinitionId().split(":")[0]);
                            newActRuDetail.setSystemName(processParam.getSystemName());
                            newActRuDetail.setStatus(hti.getEndTime() == null ? 0 : 1);
                            newActRuDetail.setTaskId(hti.getId());
                            newActRuDetail.setStarted(true);
                            newActRuDetail.setEnded(false);
                            newActRuDetail.setStartTime(map.get("START_TIME_").toString());
                            newActRuDetail.setItemId(processParam.getItemId());
                            newActRuDetail.setProcessInstanceId(hti.getProcessInstanceId());
                            newActRuDetail.setItemId(processParam.getItemId());
                            newActRuDetail.setSystemName(systemName);
                            actRuDetailService.saveOrUpdate(newActRuDetail);
                        }
                    }

                } catch (Exception e) {
                    i = i + 1;
                    final Writer result = new StringWriter();
                    final PrintWriter print = new PrintWriter(result);
                    e.printStackTrace(print);
                    String msg = result.toString();
                    String time = sdf.format(new Date());
                    ErrorLog errorLogModel = new ErrorLog();
                    errorLogModel.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                    errorLogModel.setCreateTime(time);
                    errorLogModel.setErrorFlag(ErrorLogModel.ERROR_FLAG_SAVE_OFFICE_DONE);
                    errorLogModel.setErrorType(ErrorLogModel.ERROR_PROCESS_INSTANCE);
                    errorLogModel.setExtendField("同步办结数据到数据中心");
                    errorLogModel.setProcessInstanceId(processInstanceId);
                    errorLogModel.setTaskId("");
                    errorLogModel.setText(msg);
                    errorLogModel.setUpdateTime(time);
                    try {
                        errorLogService.saveErrorLog(errorLogModel);
                    } catch (Exception e1) {
                    }
                    e.printStackTrace();
                }
            }
            LOGGER.info("********************同步失败{}条数据***************************", i);
            resMap.put("总数", list.size());
            resMap.put("同步失败", i);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(resMap));
    }

    /**
     * 同步未办结办件详情
     *
     * @param tenantId
     * @param request
     * @param response
     */
    @ResponseBody
    @RequestMapping(value = "/tongbuActRuDetailStartTime")
    public void tongbuActRuDetailStartTime(String tenantId, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> resMap = new HashMap<String, Object>(16);
        Connection connection = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Y9LoginUserHolder.setTenantId(tenantId);
            String sql = "SELECT P.PROCESSINSTANCEID FROM  FF_ACT_RU_DETAIL P group by P.PROCESSINSTANCEID";
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
            LOGGER.info("*********************共{}条数据***************************", list.size());
            int i = 0;
            String processInstanceId = "";
            for (Map<String, Object> map : list) {
                try {
                    processInstanceId = (String)map.get("PROCESSINSTANCEID");
                    List<ActRuDetail> list1 = actRuDetailService.findByProcessInstanceId(processInstanceId);
                    for (ActRuDetail hti : list1) {
                        OfficeDoneInfo info = officeDoneInfoService.findByProcessInstanceId(processInstanceId);
                        hti.setStartTime(info.getStartTime());
                        actRuDetailService.saveOrUpdate(hti);
                    }
                } catch (Exception e) {
                    i = i + 1;
                    final Writer result = new StringWriter();
                    final PrintWriter print = new PrintWriter(result);
                    e.printStackTrace(print);
                    String msg = result.toString();
                    String time = sdf.format(new Date());
                    ErrorLog errorLogModel = new ErrorLog();
                    errorLogModel.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                    errorLogModel.setCreateTime(time);
                    errorLogModel.setErrorFlag(ErrorLogModel.ERROR_FLAG_SAVE_OFFICE_DONE);
                    errorLogModel.setErrorType(ErrorLogModel.ERROR_PROCESS_INSTANCE);
                    errorLogModel.setExtendField("同步办结数据到数据中心");
                    errorLogModel.setProcessInstanceId(processInstanceId);
                    errorLogModel.setTaskId("");
                    errorLogModel.setText(msg);
                    errorLogModel.setUpdateTime(time);
                    try {
                        errorLogService.saveErrorLog(errorLogModel);
                    } catch (Exception e1) {
                    }
                    e.printStackTrace();
                }
            }
            LOGGER.info("********************同步失败{}条数据***************************", i);
            resMap.put("总数", list.size());
            resMap.put("同步失败", i);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(resMap));
    }

}
