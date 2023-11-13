package net.risesoft.service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.ErrorLogApi;
import net.risesoft.api.itemadmin.OfficeDoneInfoApi;
import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.api.org.PersonApi;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.platform.Person;
import net.risesoft.model.itemadmin.ErrorLogModel;
import net.risesoft.model.itemadmin.OfficeDoneInfoModel;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9Util;

/**
 * 流程数据进入数据中心，用于综合搜索
 * 
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@EnableAsync
@Service(value = "process4SearchService")
@Slf4j
public class Process4SearchService {

    @Autowired
    private OfficeDoneInfoApi officeDoneInfoManager;

    @javax.annotation.Resource(name = "jdbcTemplate4Tenant")
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PersonApi personApi;

    @Autowired
    private ErrorLogApi errorLogManager;

    @Autowired
    private ProcessParamApi processParamManager;

    /**
     * 重定位，串行送下一人，修改办件信息
     *
     * @param tenantId
     * @param taskId
     * @param processInstanceId
     */
    @Async
    public void saveToDataCenter(final String tenantId, final String taskId, final String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            ProcessParamModel processParam = processParamManager.findByProcessInstanceId(tenantId, processInstanceId);
            OfficeDoneInfoModel officeDoneInfo =
                officeDoneInfoManager.findByProcessInstanceId(tenantId, processInstanceId);
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
                    String ownerId = (String)m.get("OWNERID");
                    if (!entrustUserId.contains(ownerId)) {
                        entrustUserId = Y9Util.genCustomStr(entrustUserId, ownerId);
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
                    String userId = m.get("USER_ID_") != null ? (String)m.get("USER_ID_") : "";
                    if (userId.contains(":")) {
                        userId = userId.split(":")[0];
                    }
                    if (StringUtils.isNotEmpty(userId) && !allUserId.contains(userId)) {
                        allUserId = Y9Util.genCustomStr(allUserId, userId);
                    }
                    if (StringUtils.isNotEmpty(userId)) {
                        Person person = personApi.getPerson(tenantId, userId).getData();
                        if (person != null && person.getId() != null) {
                            if (!deptIds.contains(person.getParentId())) {
                                deptIds = Y9Util.genCustomStr(deptIds, person.getParentId());
                            }
                        }
                    }
                }
                officeDoneInfo.setDeptId(deptIds);
                officeDoneInfo.setAllUserId(allUserId);
                officeDoneInfoManager.saveOfficeDone(tenantId, officeDoneInfo);
            }
            return;
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
                errorLogManager.saveErrorLog(tenantId, errorLogModel);
            } catch (Exception e1) {
            }

            LOGGER.warn("#################保存办结件数据到数据中心失败#################", e);
        }
    }

    /**
     * 并行加签，修改办件信息
     *
     * @param tenantId
     * @param taskId
     * @param processParam
     */
    @Async
    public void saveToDataCenter1(final String tenantId, final String taskId, final ProcessParamModel processParam) {
        Y9LoginUserHolder.setTenantId(tenantId);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String processInstanceId = processParam.getProcessInstanceId();
        try {
            OfficeDoneInfoModel officeDoneInfo =
                officeDoneInfoManager.findByProcessInstanceId(tenantId, processInstanceId);
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
                    String userId = (String)m.get("OWNERID");
                    if (!entrustUserId.contains(userId)) {
                        entrustUserId = Y9Util.genCustomStr(entrustUserId, userId);
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
                    String userId = m.get("USER_ID_") != null ? (String)m.get("USER_ID_") : "";
                    if (userId.contains(":")) {
                        userId = userId.split(":")[0];
                    }
                    if (!StringUtils.isNotEmpty(userId) && !allUserId.contains(userId)) {
                        allUserId = Y9Util.genCustomStr(allUserId, userId);
                    }
                    if (StringUtils.isNotEmpty(userId)) {
                        Person person = personApi.getPerson(tenantId, userId).getData();
                        if (person != null && person.getId() != null) {
                            if (!deptIds.contains(person.getParentId())) {
                                deptIds = Y9Util.genCustomStr(deptIds, person.getParentId());
                            }
                        }
                    }
                }
                officeDoneInfo.setDeptId(deptIds);
                officeDoneInfo.setAllUserId(allUserId);
                officeDoneInfoManager.saveOfficeDone(tenantId, officeDoneInfo);
            }
            return;
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
                errorLogManager.saveErrorLog(tenantId, errorLogModel);
            } catch (Exception e1) {
            }
            LOGGER.warn("#################保存办结件数据到数据中心失败#################", e);
        }
    }
}
