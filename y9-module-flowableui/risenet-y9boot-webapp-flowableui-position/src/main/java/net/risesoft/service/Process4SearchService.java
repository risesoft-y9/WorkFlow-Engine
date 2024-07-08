package net.risesoft.service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.ErrorLogApi;
import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.api.itemadmin.position.OfficeDoneInfo4PositionApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.ErrorLogModel;
import net.risesoft.model.itemadmin.OfficeDoneInfoModel;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.model.platform.Position;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9Util;

/**
 * 流程数据进入数据中心，用于综合搜索
 *
 * @author 10858
 */
@RequiredArgsConstructor
@EnableAsync
@Service(value = "process4SearchService")
@Slf4j
public class Process4SearchService {

    private final OfficeDoneInfo4PositionApi officeDoneInfo4PositionApi;
    private final PositionApi positionApi;
    private final ErrorLogApi errorLogApi;
    private final ProcessParamApi processParamApi;
    @Resource(name = "jdbcTemplate4Tenant")
    private JdbcTemplate jdbcTemplate;

    /**
     * 重定位，串行送下一人，修改办件信息
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param processInstanceId 流程实例id
     */
    @Async
    public void saveToDataCenter(final String tenantId, final String taskId, final String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            ProcessParamModel processParam =
                processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
            OfficeDoneInfoModel officeDoneInfo =
                officeDoneInfo4PositionApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
            if (officeDoneInfo != null) {
                officeDoneInfo.setTitle(StringUtils.isNotBlank(processParam.getTitle()) ? processParam.getTitle() : "");
                officeDoneInfo.setUrgency(
                    StringUtils.isNotBlank(processParam.getCustomLevel()) ? processParam.getCustomLevel() : "");
                officeDoneInfo.setUserComplete("");
                officeDoneInfo.setBureauId(processParam.getBureauIds());
                officeDoneInfo.setEndTime(null);

                // 处理委托人
                String sql;
                /*    "SELECT e.OWNERID from FF_ENTRUSTDETAIL e where e.PROCESSINSTANCEID = '" + processInstanceId + "'";
                List<Map<String, Object>> list2 = jdbcTemplate.queryForList(sql);
                String entrustUserId = "";
                for (Map<String, Object> m : list2) {
                    String USER_ID_ = (String)m.get("OWNERID");
                    if (!entrustUserId.contains(USER_ID_)) {
                        entrustUserId = Y9Util.genCustomStr(entrustUserId, USER_ID_);
                    }
                }
                officeDoneInfo.setEntrustUserId(entrustUserId);*/

                // 处理参与人
                sql =
                    "SELECT i.USER_ID_ from ACT_HI_IDENTITYLINK i where i.PROC_INST_ID_ = '" + processInstanceId + "'";
                List<Map<String, Object>> list3 = jdbcTemplate.queryForList(sql);
                String allUserId = "";
                String deptIds = "";
                for (Map<String, Object> m : list3) {
                    String USER_ID_ = m.get("USER_ID_") != null ? (String)m.get("USER_ID_") : "";
                    if (USER_ID_.contains(":")) {
                        USER_ID_ = USER_ID_.split(":")[0];
                    }
                    if (!USER_ID_.isEmpty() && !allUserId.contains(USER_ID_)) {
                        allUserId = Y9Util.genCustomStr(allUserId, USER_ID_);
                    }
                    if (!USER_ID_.isEmpty()) {
                        Position position = positionApi.get(tenantId, USER_ID_).getData();
                        if (position != null && position.getId() != null) {
                            if (!deptIds.contains(position.getParentId())) {
                                deptIds = Y9Util.genCustomStr(deptIds, position.getParentId());
                            }
                        }
                    }
                }
                officeDoneInfo.setDeptId(deptIds);
                officeDoneInfo.setAllUserId(allUserId);
                officeDoneInfo4PositionApi.saveOfficeDone(tenantId, officeDoneInfo);// 保存到数据中心
            }
        } catch (Exception e) {
            final Writer result = new StringWriter();
            final PrintWriter print = new PrintWriter(result);
            e.printStackTrace(print);
            String msg = result.toString();
            String time = sdf.format(new Date());
            ErrorLogModel errorLogModel = new ErrorLogModel();
            errorLogModel.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            errorLogModel.setCreateTime(time);
            errorLogModel.setErrorFlag(ErrorLogModel.ERROR_FLAG_SAVE_OFFICE_DONE + "4Search2");
            errorLogModel.setErrorType(ErrorLogModel.ERROR_PROCESS_INSTANCE);
            errorLogModel.setExtendField("重定位，串行送下一人保存流程信息失败");
            errorLogModel.setProcessInstanceId(processInstanceId);
            errorLogModel.setTaskId(taskId);
            errorLogModel.setText(msg);
            errorLogModel.setUpdateTime(time);
            try {
                errorLogApi.saveErrorLog(tenantId, errorLogModel);
            } catch (Exception e1) {
                LOGGER.warn("#################保存错误日志失败#################", e1);
            }
            LOGGER.warn("#################保存办结件数据到数据中心失败#################", e);
        }
    }

    /**
     * 并行加签，修改办件信息
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param processParam 流程参数
     */
    @Async
    public void saveToDataCenter1(final String tenantId, final String taskId, final ProcessParamModel processParam) {
        Y9LoginUserHolder.setTenantId(tenantId);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String processInstanceId = processParam.getProcessInstanceId();
        try {
            OfficeDoneInfoModel officeDoneInfo =
                officeDoneInfo4PositionApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
            if (officeDoneInfo != null) {
                officeDoneInfo.setTitle(StringUtils.isNotBlank(processParam.getTitle()) ? processParam.getTitle() : "");
                officeDoneInfo.setUrgency(
                    StringUtils.isNotBlank(processParam.getCustomLevel()) ? processParam.getCustomLevel() : "");
                officeDoneInfo.setUserComplete("");
                officeDoneInfo.setBureauId(processParam.getBureauIds());
                officeDoneInfo.setEndTime(null);

                // 处理委托人
                String sql =
                    "SELECT e.OWNERID from FF_ENTRUSTDETAIL e where e.PROCESSINSTANCEID = '" + processInstanceId + "'";
                List<Map<String, Object>> list2 = jdbcTemplate.queryForList(sql);
                String entrustUserId = "";
                for (Map<String, Object> m : list2) {
                    String USER_ID_ = (String)m.get("OWNERID");
                    if (!entrustUserId.contains(USER_ID_)) {
                        entrustUserId = Y9Util.genCustomStr(entrustUserId, USER_ID_);
                    }
                }
                officeDoneInfo.setEntrustUserId(entrustUserId);

                // 处理参与人
                sql =
                    "SELECT i.USER_ID_ from ACT_HI_IDENTITYLINK i where i.PROC_INST_ID_ = '" + processInstanceId + "'";
                List<Map<String, Object>> list3 = jdbcTemplate.queryForList(sql);
                String allUserId = "";
                String deptIds = "";
                for (Map<String, Object> m : list3) {
                    String USER_ID_ = m.get("USER_ID_") != null ? (String)m.get("USER_ID_") : "";
                    if (USER_ID_.contains(":")) {
                        USER_ID_ = USER_ID_.split(":")[0];
                    }
                    if (!USER_ID_.isEmpty() && !allUserId.contains(USER_ID_)) {
                        allUserId = Y9Util.genCustomStr(allUserId, USER_ID_);
                    }
                    if (!USER_ID_.isEmpty()) {
                        Position position = positionApi.get(tenantId, USER_ID_).getData();
                        if (position != null && position.getId() != null) {
                            if (!deptIds.contains(position.getParentId())) {
                                deptIds = Y9Util.genCustomStr(deptIds, position.getParentId());
                            }
                        }
                    }
                }
                officeDoneInfo.setDeptId(deptIds);
                officeDoneInfo.setAllUserId(allUserId);
                officeDoneInfo4PositionApi.saveOfficeDone(tenantId, officeDoneInfo);// 保存到数据中心
            }
        } catch (Exception e) {
            final Writer result = new StringWriter();
            final PrintWriter print = new PrintWriter(result);
            e.printStackTrace(print);
            String msg = result.toString();
            String time = sdf.format(new Date());
            ErrorLogModel errorLogModel = new ErrorLogModel();
            errorLogModel.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            errorLogModel.setCreateTime(time);
            errorLogModel.setErrorFlag(ErrorLogModel.ERROR_FLAG_SAVE_OFFICE_DONE + "4Search2");
            errorLogModel.setErrorType(ErrorLogModel.ERROR_PROCESS_INSTANCE);
            errorLogModel.setExtendField("并行加签保存流程信息失败");
            errorLogModel.setProcessInstanceId(processInstanceId);
            errorLogModel.setTaskId(taskId);
            errorLogModel.setText(msg);
            errorLogModel.setUpdateTime(time);
            try {
                errorLogApi.saveErrorLog(tenantId, errorLogModel);
            } catch (Exception e1) {
                LOGGER.warn("#################保存错误日志失败#################", e1);
            }
            LOGGER.warn("#################保存办结件数据到数据中心失败#################", e);
        }
    }
}
