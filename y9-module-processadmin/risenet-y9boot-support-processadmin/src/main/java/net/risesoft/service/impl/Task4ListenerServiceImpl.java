package net.risesoft.service.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.ErrorLogApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.ErrorLogModel;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.service.SetDeptIdUtilService;
import net.risesoft.service.Task4ListenerService;
import net.risesoft.y9.FlowableTenantInfoHolder;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@RequiredArgsConstructor
@Service
public class Task4ListenerServiceImpl implements Task4ListenerService {

    private final SetDeptIdUtilService setDeptIdUtilService;

    private final OrgUnitApi orgUnitApi;

    private final ErrorLogApi errorLogApi;

    @javax.annotation.Resource(name = "jdbcTemplate4Tenant")
    private JdbcTemplate jdbcTemplate;

    private static Writer getWriter() {
        return new StringWriter();
    }

    private static PrintWriter getPrintWriter(Writer msgResult) {
        return new PrintWriter(msgResult);
    }

    private static void extracted(Exception e, PrintWriter print) {
        e.printStackTrace(print);
    }

    private static String getMsg(Writer msgResult) {
        return msgResult.toString();
    }

    @Override
    @Async
    public void task4AssignmentListener(final DelegateTask task, final Map<String, Object> variables) {
        setDeptIdUtilService.setDeptId(task, variables);
    }

    @Override
    @Async
    public void task4CreateListener(final DelegateTask task, final Map<String, Object> variables,
        final Map<String, Object> localVariables) {

    }

    @Override
    @Async
    public void task4DeleteListener(final DelegateTask task, final Map<String, Object> variables) {
        String tenantId = (String)variables.get("tenantId");
        Y9LoginUserHolder.setTenantId(tenantId);
        FlowableTenantInfoHolder.setTenantId(tenantId);
        /*
         * "ACT_HI_TASKINST"的SCOPE_TYPE_字段记录岗位/人员名称，历程显示该字段名称，避免岗位换人或人员删除历程显示问题
         * ACT_HI_ACTINST的TENANT_ID_字段记录岗位/人员名称，历程显示该字段名称，避免岗位换人或人员删除历程显示问题
         */
        String orgUnitName = "";
        try {
            if (StringUtils.isNotBlank(task.getAssignee())) {
                OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, task.getAssignee()).getData();
                orgUnitName = orgUnit != null ? orgUnit.getName() : "";
                String updateSql = "UPDATE ACT_HI_TASKINST T SET T.SCOPE_TYPE_ = '" + orgUnitName + "' WHERE T.ID_= '"
                    + task.getId() + "'";
                jdbcTemplate.execute(updateSql);

                String updateSql0 = "UPDATE ACT_HI_ACTINST T SET T.TENANT_ID_ = '" + orgUnitName
                    + "' WHERE T.TASK_ID_= '" + task.getId() + "'";
                jdbcTemplate.execute(updateSql0);
            }
        } catch (Exception e) {
            final Writer msgResult = getWriter();
            final PrintWriter print = getPrintWriter(msgResult);
            extracted(e, print);
            String msg = getMsg(msgResult);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = sdf.format(new Date());
            ErrorLogModel errorLogModel = new ErrorLogModel();
            errorLogModel.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            errorLogModel.setCreateTime(time);
            errorLogModel.setErrorFlag("update orgUnitName");
            errorLogModel.setErrorType(ErrorLogModel.ERROR_TASK);
            errorLogModel.setExtendField("更新SCOPE_TYPE_,TENANT_ID_字段失败:任务key【" + task.getTaskDefinitionKey()
                + "】,orgUnitName【" + orgUnitName + "】");
            errorLogModel.setProcessInstanceId(task.getProcessInstanceId());
            errorLogModel.setTaskId(task.getId());
            errorLogModel.setText(msg);
            errorLogModel.setUpdateTime(time);
            errorLogApi.saveErrorLog(tenantId, errorLogModel);
        }
    }
}
