package net.risesoft.service.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
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
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.service.SetDeptIdUtilService;
import net.risesoft.service.Task4ListenerService;
import net.risesoft.util.Y9DateTimeUtils;
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

    @jakarta.annotation.Resource(name = "jdbcTemplate4Tenant")
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
    public void task4DeleteListener(final DelegateTask task, final Map<String, Object> variables) {
        String tenantId = (String)variables.get("tenantId");
        Y9LoginUserHolder.setTenantId(tenantId);
        FlowableTenantInfoHolder.setTenantId(tenantId);
        String orgUnitName = "";
        try {
            if (StringUtils.isNotBlank(task.getAssignee())) {
                OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, task.getAssignee()).getData();
                orgUnitName = orgUnit != null ? orgUnit.getName() : "";
                updateTaskInstScopeType(task.getId(), orgUnitName);
                updateActInstTenantId(task.getId(), orgUnitName);
            }
        } catch (Exception e) {
            handleTaskDeleteException(e, task, orgUnitName, tenantId);
        }
    }

    /**
     * 更新ACT_HI_TASKINST表的SCOPE_TYPE_字段，记录岗位/人员名称，历程显示该字段名称，避免岗位换人或人员删除历程显示问题
     *
     * @param taskId 任务ID
     * @param orgUnitName 组织单元名称
     */
    private void updateTaskInstScopeType(String taskId, String orgUnitName) {
        String updateSql = "UPDATE ACT_HI_TASKINST T SET T.SCOPE_TYPE_ = ? WHERE T.ID_= ?";
        jdbcTemplate.update(updateSql, orgUnitName, taskId);
    }

    /**
     * 更新ACT_HI_ACTINST表的TENANT_ID_字段，记录岗位/人员名称，历程显示该字段名称，避免岗位换人或人员删除历程显示问题
     *
     * @param taskId 任务ID
     * @param orgUnitName 组织单元名称
     */
    private void updateActInstTenantId(String taskId, String orgUnitName) {
        String updateSql = "UPDATE ACT_HI_ACTINST T SET T.TENANT_ID_ = ? WHERE T.TASK_ID_= ?";
        jdbcTemplate.update(updateSql, orgUnitName, taskId);
    }

    /**
     * 处理任务删除监听器异常
     *
     * @param e 异常对象
     * @param task 委托任务
     * @param orgUnitName 组织单元名称
     * @param tenantId 租户ID
     */
    private void handleTaskDeleteException(Exception e, DelegateTask task, String orgUnitName, String tenantId) {
        final Writer msgResult = getWriter();
        final PrintWriter print = getPrintWriter(msgResult);
        extracted(e, print);
        String msg = getMsg(msgResult);
        String time = Y9DateTimeUtils.formatCurrentDateTime();
        ErrorLogModel errorLogModel = new ErrorLogModel();
        errorLogModel.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        errorLogModel.setCreateTime(time);
        errorLogModel.setErrorFlag("update orgUnitName");
        errorLogModel.setErrorType(ErrorLogModel.ERROR_TASK);
        errorLogModel.setExtendField(
            "更新SCOPE_TYPE_,TENANT_ID_字段失败:任务key【" + task.getTaskDefinitionKey() + "】,orgUnitName【" + orgUnitName + "】");
        errorLogModel.setProcessInstanceId(task.getProcessInstanceId());
        errorLogModel.setTaskId(task.getId());
        errorLogModel.setText(msg);
        errorLogModel.setUpdateTime(time);
        errorLogApi.saveErrorLog(tenantId, errorLogModel);
    }
}
