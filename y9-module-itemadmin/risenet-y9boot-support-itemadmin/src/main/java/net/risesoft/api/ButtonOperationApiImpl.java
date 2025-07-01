package net.risesoft.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.ButtonOperationApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.processadmin.HistoricTaskApi;
import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.RuntimeApi;
import net.risesoft.api.processadmin.SpecialOperationApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.api.processadmin.VariableApi;
import net.risesoft.entity.ActRuDetail;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.processadmin.FlowElementModel;
import net.risesoft.model.processadmin.HistoricTaskInstanceModel;
import net.risesoft.model.processadmin.ProcessInstanceModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.ActRuDetailService;
import net.risesoft.service.DocumentService;
import net.risesoft.service.MultiInstanceService;
import net.risesoft.util.CommonOpt;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 按钮操作接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/buttonOperation", produces = MediaType.APPLICATION_JSON_VALUE)
public class ButtonOperationApiImpl implements ButtonOperationApi {

    private final DocumentService documentService;

    private final MultiInstanceService multiInstanceService;

    private final OrgUnitApi orgUnitApi;

    private final TaskApi taskApi;

    private final VariableApi variableApi;

    private final ProcessDefinitionApi processDefinitionApi;

    private final HistoricTaskApi historictaskApi;

    private final RuntimeApi runtimeApi;

    private final SpecialOperationApi specialOperationApi;

    private final ActRuDetailService actRuDetailService;

    /**
     * 加签
     *
     * @param tenantId 租户id
     * @param activityId 活动Id
     * @param parentExecutionId 父执行实例id
     * @param taskId 任务id
     * @param elementUser 选择人id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> addMultiInstanceExecution(@RequestParam String tenantId, @RequestParam String activityId,
        @RequestParam String parentExecutionId, @RequestParam String taskId, @RequestParam String elementUser) {
        Y9LoginUserHolder.setTenantId(tenantId);
        multiInstanceService.addMultiInstanceExecution(activityId, parentExecutionId, taskId, elementUser);
        return Y9Result.success();
    }

    /**
     * 加签
     *
     * @param tenantId 租户id
     * @param activityId 活动Id
     * @param parentExecutionId 父执行实例id
     * @param elementUser 选择人id
     * @return {@code Y9Result<ExecutionModel>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> addMultiInstanceExecutionByActivityId(@RequestParam String tenantId,
        @RequestParam String activityId, @RequestParam String parentExecutionId, @RequestParam String elementUser) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return multiInstanceService.addMultiInstanceExecution(activityId, parentExecutionId, elementUser);
    }

    /**
     * 减签
     *
     * @param tenantId 租户id
     * @param executionId 执行实例id
     * @param taskId 任务id
     * @param elementUser 选择人id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> deleteMultiInstanceExecution(@RequestParam String tenantId,
        @RequestParam String executionId, @RequestParam String taskId, @RequestParam String elementUser) {
        Y9LoginUserHolder.setTenantId(tenantId);
        multiInstanceService.deleteMultiInstanceExecution(executionId, taskId, elementUser);
        return Y9Result.success();
    }

    /**
     * 直接发送至流程启动人
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param taskId 任务id
     * @param routeToTask 任务key
     * @param processInstanceId 流程实例ID
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> directSend(@RequestParam String tenantId, @RequestParam String orgUnitId,
        @RequestParam String taskId, @RequestParam String routeToTask, @RequestParam String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, orgUnitId).getData();
        Y9LoginUserHolder.setOrgUnit(orgUnit);
        ProcessInstanceModel processInstance = runtimeApi.getProcessInstance(tenantId, processInstanceId).getData();
        String startUserId = "6" + SysVariables.COLON + processInstance.getStartUserId();
        Y9Result<String> y9Result = documentService.forwarding(taskId, "true", startUserId, routeToTask, "");
        if (y9Result.isSuccess()) {
            return Y9Result.success();
        }
        return Y9Result.failure(y9Result.getMsg());
    }

    /**
     * 最后一人拒签退回
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param taskId 任务id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> refuseClaimRollback(@RequestParam String tenantId, @RequestParam String orgUnitId,
        @RequestParam String taskId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        try {
            taskApi.claim(tenantId, orgUnitId, taskId);
            TaskModel currentTask = taskApi.findById(tenantId, taskId).getData();
            List<String> userAndDeptIdList = new ArrayList<>();
            // 获取当前任务的前一个任务
            HistoricTaskInstanceModel hti = historictaskApi.getThePreviousTask(tenantId, taskId).getData();
            // 前一任务的受让人，标题
            String assignee = hti.getAssignee();
            userAndDeptIdList.add(assignee);
            OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, orgUnitId).getData();
            Y9LoginUserHolder.setOrgUnit(orgUnit);
            FlowElementModel flowElementModel = processDefinitionApi
                .getNode(tenantId, hti.getProcessDefinitionId(), hti.getTaskDefinitionKey()).getData();
            Map<String, Object> variables = CommonOpt.setVariables(orgUnitId, orgUnit.getName(),
                hti.getTaskDefinitionKey(), userAndDeptIdList, flowElementModel);
            Map<String, Object> val = new HashMap<>();
            val.put("val", SysVariables.REFUSECLAIMROLLBACK);
            variableApi.setVariableLocal(tenantId, taskId, SysVariables.REFUSECLAIMROLLBACK, val);
            taskApi.completeWithVariables(tenantId, taskId, orgUnitId, variables);
            /*
             * 如果上一任务是并行，则回退时设置主办人
             */
            if (SysVariables.PARALLEL.equals(flowElementModel.getMultiInstance())) {
                List<TaskModel> taskNextList1 =
                    taskApi.findByProcessInstanceId(tenantId, currentTask.getProcessInstanceId()).getData();
                for (TaskModel taskModelNext : taskNextList1) {
                    Map<String, Object> val1 = new HashMap<>();
                    val1.put("val", assignee.split(SysVariables.COLON)[0]);
                    variableApi.setVariableLocal(tenantId, taskModelNext.getId(), SysVariables.PARALLELSPONSOR, val1);
                }
            }
        } catch (Exception e) {
            taskApi.unClaim(tenantId, taskId);
            LOGGER.error("退回失败", e);
            return Y9Result.failure("退回失败");
        }
        return Y9Result.success();
    }

    /**
     * 重定位
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param taskId 任务id
     * @param repositionToTaskId 重定位任务key
     * @param userChoice 选择人id
     * @param reason 原因
     * @param sponsorGuid 主办人id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> reposition(@RequestParam String tenantId, @RequestParam String orgUnitId,
        @RequestParam String taskId, @RequestParam String repositionToTaskId,
        @RequestParam("userChoice") List<String> userChoice, String reason, String sponsorGuid) {
        Y9LoginUserHolder.setTenantId(tenantId);
        specialOperationApi.reposition(tenantId, orgUnitId, taskId, repositionToTaskId, userChoice, reason,
            sponsorGuid);
        return Y9Result.success();
    }

    /**
     * 退回至流转过的节点
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param taskId 任务id
     * @param routeToTaskId 任务key
     * @param userChoice 岗位id集合
     * @param reason 退回原因
     * @param sponsorGuid 主办人id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.8
     */
    @Override
    public Y9Result<Object> rollBack2History(@RequestParam String tenantId, @RequestParam String orgUnitId,
        @RequestParam String taskId, @RequestParam String routeToTaskId,
        @RequestParam("userChoice") List<String> userChoice, String reason, String sponsorGuid) {
        Y9LoginUserHolder.setTenantId(tenantId);
        specialOperationApi.rollBack2History(tenantId, orgUnitId, taskId, routeToTaskId, userChoice, reason,
            sponsorGuid);
        return Y9Result.success();
    }

    /**
     * 退回操作
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param taskId 任务id
     * @param reason 原因
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> rollBack(@RequestParam String tenantId, @RequestParam String orgUnitId,
        @RequestParam String taskId, String reason) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return Y9Result.success(specialOperationApi.rollBack(tenantId, orgUnitId, taskId, reason).isSuccess());
    }

    /**
     * 发回给上一步的发送人
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param taskId 任务id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> rollbackToSender(@RequestParam String tenantId, @RequestParam String orgUnitId,
        @RequestParam String taskId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return Y9Result.success(specialOperationApi.rollbackToSender(tenantId, orgUnitId, taskId).isSuccess());
    }

    /**
     * 退回操作，直接退回到办件登记人
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param taskId 任务id
     * @param reason 原因
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> rollbackToStartor(@RequestParam String tenantId, @RequestParam String orgUnitId,
        @RequestParam String taskId, String reason) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return specialOperationApi.rollbackToStartor(tenantId, orgUnitId, taskId, reason);
    }

    /**
     * 特殊办结
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param taskId 任务id
     * @param reason 原因
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> specialComplete(@RequestParam String tenantId, @RequestParam String orgUnitId,
        @RequestParam String taskId, String reason) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return Y9Result.success(specialOperationApi.specialComplete(tenantId, orgUnitId, taskId, reason).isSuccess());
    }

    /**
     * 收回操作
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param taskId 任务id
     * @param reason 原因
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> takeback(@RequestParam String tenantId, @RequestParam String orgUnitId,
        @RequestParam String taskId, String reason) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return Y9Result.success(specialOperationApi.takeBack(tenantId, orgUnitId, taskId, reason).isSuccess());
    }

    /**
     * 收回操作
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param taskId 任务id
     * @param reason 原因
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> takeBack2TaskDefKey(@RequestParam String tenantId, @RequestParam String orgUnitId,
        @RequestParam String taskId, String reason) {
        Y9LoginUserHolder.setTenantId(tenantId);
        TaskModel task = taskApi.findById(tenantId, taskId).getData();
        ActRuDetail actRuDetail = actRuDetailService
            .findByProcessInstanceIdAndAssigneeAndStatusEquals1(task.getProcessInstanceId(), orgUnitId);
        return Y9Result.success(specialOperationApi
            .takeBack2TaskDefKey(tenantId, orgUnitId, taskId, actRuDetail.getTaskDefKey(), reason).isSuccess());
    }
}
