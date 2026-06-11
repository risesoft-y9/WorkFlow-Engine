package net.risesoft.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.ButtonOperationApi;
import net.risesoft.api.processadmin.HistoricTaskApi;
import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.RuntimeApi;
import net.risesoft.api.processadmin.SpecialOperationApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.api.processadmin.VariableApi;
import net.risesoft.consts.processadmin.SysVariables;
import net.risesoft.dto.itemadmin.ForwardingDTO;
import net.risesoft.entity.ActRuDetail;
import net.risesoft.model.platform.org.Position;
import net.risesoft.model.processadmin.FlowElementModel;
import net.risesoft.model.processadmin.HistoricTaskInstanceModel;
import net.risesoft.model.processadmin.ProcessInstanceModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.AsyncUtilService;
import net.risesoft.service.MultiInstanceService;
import net.risesoft.service.core.ActRuDetailService;
import net.risesoft.service.core.DocumentService;
import net.risesoft.util.CommonOpt;
import net.risesoft.y9.Y9FlowableHolder;
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

    private final TaskApi taskApi;

    private final VariableApi variableApi;

    private final ProcessDefinitionApi processDefinitionApi;

    private final HistoricTaskApi historictaskApi;

    private final RuntimeApi runtimeApi;

    private final SpecialOperationApi specialOperationApi;

    private final ActRuDetailService actRuDetailService;

    private final AsyncUtilService asyncUtilService;

    /**
     * 加签
     *
     * @param activityId 活动Id
     * @param parentExecutionId 父执行实例id
     * @param taskId 任务id
     * @param elementUser 选择人id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> addMultiInstanceExecution(@RequestParam String activityId,
        @RequestParam String parentExecutionId, @RequestParam String taskId, @RequestParam String elementUser) {
        multiInstanceService.addMultiInstanceExecution(activityId, parentExecutionId, taskId, elementUser);
        return Y9Result.success();
    }

    /**
     * 加签
     *
     * @param activityId 活动Id
     * @param parentExecutionId 父执行实例id
     * @param elementUser 选择人id
     * @return {@code Y9Result<ExecutionModel>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> addMultiInstanceExecutionByActivityId(@RequestParam String activityId,
        @RequestParam String parentExecutionId, @RequestParam String elementUser) {
        return multiInstanceService.addMultiInstanceExecution(activityId, parentExecutionId, elementUser);
    }

    /**
     * 减签
     *
     * @param executionId 执行实例id
     * @param taskId 任务id
     * @param elementUser 选择人id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> deleteMultiInstanceExecution(@RequestParam String executionId, @RequestParam String taskId,
        @RequestParam String elementUser) {
        multiInstanceService.deleteMultiInstanceExecution(executionId, taskId, elementUser);
        return Y9Result.success();
    }

    /**
     * 直接发送至流程启动人
     *
     * @param taskId 任务id
     * @param routeToTask 任务key
     * @param processInstanceId 流程实例ID
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> directSend(@RequestParam String taskId, @RequestParam String routeToTask,
        @RequestParam String processInstanceId) {
        ProcessInstanceModel processInstance = runtimeApi.getProcessInstance(processInstanceId).getData();
        String startUserId = "6" + SysVariables.COLON + processInstance.getStartUserId();
        ForwardingDTO forwardingDTO = new ForwardingDTO();
        forwardingDTO.setTaskId(taskId);
        forwardingDTO.setRouteToTaskId(routeToTask);
        forwardingDTO.setUserChoice(startUserId);
        forwardingDTO.setSponsorHandle("true");
        forwardingDTO.setSponsorGuid("");
        Y9Result<String> y9Result = documentService.forwarding(forwardingDTO);
        if (y9Result.isSuccess()) {
            return Y9Result.success();
        }
        return Y9Result.failure(y9Result.getMsg());
    }

    /**
     * 最后一人拒签退回
     *
     * @param taskId 任务id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> refuseClaimRollback(@RequestParam String taskId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String orgUnitId = Y9FlowableHolder.getPositionId();
        try {
            taskApi.claim(tenantId, orgUnitId, taskId);
            TaskModel currentTask = taskApi.findById(tenantId, taskId).getData();
            List<String> userAndDeptIdList = new ArrayList<>();
            // 获取当前任务的前一个任务
            HistoricTaskInstanceModel hti = historictaskApi.getThePreviousTask(taskId).getData();
            // 前一任务的受让人，标题
            String assignee = hti.getAssignee();
            userAndDeptIdList.add(assignee);
            Position position = Y9FlowableHolder.getPosition();
            FlowElementModel flowElementModel =
                processDefinitionApi.getNode(hti.getProcessDefinitionId(), hti.getTaskDefinitionKey()).getData();
            Map<String, Object> variables = CommonOpt.setVariables(orgUnitId, position.getName(),
                hti.getTaskDefinitionKey(), userAndDeptIdList, flowElementModel);
            Map<String, Object> val = new HashMap<>();
            val.put("val", SysVariables.REFUSE_CLAIM_ROLLBACK);
            variableApi.setVariableLocal(taskId, SysVariables.REFUSE_CLAIM_ROLLBACK, val);
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
                    variableApi.setVariableLocal(taskModelNext.getId(), SysVariables.PARALLEL_SPONSOR, val1);
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
     * @param taskId 任务id
     * @param repositionToTaskId 重定位任务key
     * @param userChoice 选择人id
     * @param reason 原因
     * @param sponsorGuid 主办人id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> reposition(@RequestParam String taskId, @RequestParam String repositionToTaskId,
        @RequestParam("userChoice") List<String> userChoice, String reason, String sponsorGuid) {
        specialOperationApi.reposition(taskId, repositionToTaskId, userChoice, reason, sponsorGuid);
        return Y9Result.success();
    }

    /**
     * 退回操作
     *
     * @param taskId 任务id
     * @param reason 原因
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> rollBack(@RequestParam String taskId, String reason) {
        return Y9Result.success(specialOperationApi.rollBack(taskId, reason).isSuccess());
    }

    /**
     * 退回至流转过的节点
     *
     * @param taskId 任务id
     * @param routeToTaskId 任务key
     * @param userChoice 岗位id集合
     * @param reason 退回原因
     * @param sponsorGuid 主办人id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.8
     */
    @Override
    public Y9Result<Object> rollBack2History(@RequestParam String taskId, @RequestParam String routeToTaskId,
        @RequestParam("userChoice") List<String> userChoice, String reason, String sponsorGuid) {
        specialOperationApi.rollBack2History(taskId, routeToTaskId, userChoice, reason, sponsorGuid);
        return Y9Result.success();
    }

    /**
     * 发回给上一步的发送人
     *
     * @param taskId 任务id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> rollbackToSender(@RequestParam String taskId) {
        return Y9Result.success(specialOperationApi.rollbackToSender(taskId).isSuccess());
    }

    /**
     * 退回操作，直接退回到办件登记人
     *
     * @param taskId 任务id
     * @param reason 原因
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> rollbackToStartor(@RequestParam String taskId, String reason) {
        return specialOperationApi.rollbackToStartor(taskId, reason);
    }

    /**
     * 特殊办结
     *
     * @param taskId 任务id
     * @param reason 原因
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> specialComplete(@RequestParam String taskId, String reason) {
        return Y9Result.success(specialOperationApi.specialComplete(taskId, reason).isSuccess());
    }

    /**
     * 收回操作
     *
     * @param taskId 任务id
     * @param reason 原因
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    @Transactional
    public Y9Result<Object> takeBack2TaskDefKey(@RequestParam String taskId, String reason) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String orgUnitId = Y9FlowableHolder.getPositionId();
        TaskModel task = taskApi.findById(tenantId, taskId).getData();
        ActRuDetail actRuDetail = actRuDetailService
            .findByProcessInstanceIdAndAssigneeAndStatusEquals1(task.getProcessInstanceId(), orgUnitId);
        boolean isSuccess =
            specialOperationApi.takeBack2TaskDefKey(taskId, actRuDetail.getTaskDefKey(), reason).isSuccess();
        asyncUtilService.takeBackTwoTaskDefKeyAuditLog(tenantId, orgUnitId, taskId, actRuDetail.getTaskDefKey());
        return Y9Result.success(isSuccess);
    }

    /**
     * 收回操作
     *
     * @param taskId 任务id
     * @param reason 原因
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> takeback(@RequestParam String taskId, String reason) {
        return Y9Result.success(specialOperationApi.takeBack(taskId, reason).isSuccess());
    }
}
