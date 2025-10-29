package net.risesoft.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.flowable.task.api.DelegationState;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.Y9FlowableHolder;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.processadmin.HistoricTaskApi;
import net.risesoft.api.processadmin.IdentityApi;
import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.RuntimeApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.api.processadmin.VariableApi;
import net.risesoft.consts.ItemConsts;
import net.risesoft.consts.processadmin.SysVariables;
import net.risesoft.entity.CustomProcessInfo;
import net.risesoft.entity.Item;
import net.risesoft.entity.ItemTaskConf;
import net.risesoft.entity.ProcessParam;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.enums.ItemButtonTypeEnum;
import net.risesoft.model.itemadmin.ItemButtonModel;
import net.risesoft.model.itemadmin.core.DocumentDetailModel;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.model.processadmin.HistoricTaskInstanceModel;
import net.risesoft.model.processadmin.IdentityLinkModel;
import net.risesoft.model.processadmin.ProcessInstanceModel;
import net.risesoft.model.processadmin.TargetModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.service.ButtonService;
import net.risesoft.service.CustomProcessInfoService;
import net.risesoft.service.ProcInstanceRelationshipService;
import net.risesoft.service.config.ItemTaskConfService;
import net.risesoft.service.core.ItemService;
import net.risesoft.service.core.ProcessParamService;
import net.risesoft.util.ItemButton;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2025/08/08
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ButtonServiceImpl implements ButtonService {

    private final ProcInstanceRelationshipService procInstanceRelationshipService;
    private final TaskApi taskApi;
    private final OrgUnitApi orgUnitApi;
    private final VariableApi variableApi;
    private final RuntimeApi runtimeApi;
    private final ProcessDefinitionApi processDefinitionApi;
    private final IdentityApi identityApi;
    private final HistoricTaskApi historictaskApi;
    private final CustomProcessInfoService customProcessInfoService;
    private final ItemService itemService;
    private final ItemTaskConfService itemTaskConfService;
    private final ProcessParamService processParamService;
    private List<TargetModel> nodeList = new ArrayList<>();

    @Override
    public Map<String, Object> showButton(String itemId, String taskId, String itemBox) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String orgUnitId = Y9FlowableHolder.getOrgUnitId();
        Map<String, Object> result = initializeResultMap();
        // 获取任务和相关变量信息
        TaskContext taskContext = buildTaskContext(tenantId, itemId, taskId, itemBox);
        // 根据不同的itemBox类型处理按钮显示逻辑
        handleItemBoxType(result, taskContext, itemId, taskId, itemBox, tenantId, orgUnitId);
        return result;
    }

    /**
     * 初始化结果Map
     *
     * @return 结果Map
     */
    private Map<String, Object> initializeResultMap() {
        Map<String, Object> map = new HashMap<>(16);
        String[] buttonIds = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15",
            "16", "17", "18", "19", "20", "21"};
        String[] buttonNames = {"保存", "发送", "返回", "退回", "委托", "协商", "完成", "送下一人", "办理完成", "签收", "撤销签收", "办结", "收回",
            "拒签", "特殊办结", "重定位", "打印", "抄送", "加减签", "恢复待办", "提交"};
        int[] buttonOrders = {3, 15, 10, 11, 1, 2, 21, 4, 5, 6, 7, 8, 9, 12, 19, 13, 14, 16, 18, 20, 17};
        boolean[] isButtonShow = new boolean[buttonIds.length];
        map.put("buttonIds", buttonIds);
        map.put("buttonNames", buttonNames);
        map.put("isButtonShow", isButtonShow);
        map.put("buttonOrders", buttonOrders);
        return map;
    }

    /**
     * 构建任务上下文
     * 
     * @param tenantId 租户ID
     * @param itemId 事项ID
     * @param taskId 任务ID
     * @param itemBox 列表类型
     * @return TaskContext
     */
    private TaskContext buildTaskContext(String tenantId, String itemId, String taskId, String itemBox) {
        TaskContext context = new TaskContext();
        if (ItemBoxTypeEnum.TODO.getValue().equals(itemBox) || ItemBoxTypeEnum.DOING.getValue().equals(itemBox)) {
            context.task = taskApi.findById(tenantId, taskId).getData();
        }
        Item item = itemService.findById(itemId);
        context.customItem = (null != item.getCustomItem()) ? item.getCustomItem() : false;
        context.showSubmitButton = item.isShowSubmitButton();

        if (context.task != null) {
            context.vars = variableApi.getVariables(tenantId, taskId).getData();
            context.varsUsers = (List<String>)context.vars.get(SysVariables.USERS);
            context.varsSponsorGuid = String.valueOf(context.vars.get(SysVariables.PARALLEL_SPONSOR));
            context.taskSenderId = String.valueOf(context.vars.get(SysVariables.TASK_SENDER_ID));
            context.multiInstance = processDefinitionApi
                .getNodeType(tenantId, context.task.getProcessDefinitionId(), context.task.getTaskDefinitionKey())
                .getData();
            // 处理串行和并行状态
            handleMultiInstanceStatus(context, tenantId);
        }
        return context;
    }

    /**
     * 处理多实例状态
     * 
     * @param context 任务上下文
     * @param tenantId 租户ID
     */
    private void handleMultiInstanceStatus(TaskContext context, String tenantId) {
        String multiInstance = context.multiInstance;
        String orgUnitId = Y9FlowableHolder.getOrgUnitId();

        if (SysVariables.SEQUENTIAL.equals(multiInstance)) {
            handleSequentialStatus(context, tenantId, orgUnitId);
        } else if (SysVariables.PARALLEL.equals(multiInstance)) {
            handleParallelStatus(context, orgUnitId);
        }
    }

    /**
     * 处理串行状态
     * 
     * @param context 任务上下文
     * @param tenantId 租户ID
     * @param orgUnitId 组织机构ID
     */
    private void handleSequentialStatus(TaskContext context, String tenantId, String orgUnitId) {
        context.isSequential = true;
        Map<String, Object> vars = context.vars;

        int nrOfInstances = (Integer)vars.get(SysVariables.NR_OF_INSTANCES);
        int nrOfActiveInstances = (Integer)vars.get(SysVariables.NR_OF_ACTIVE_INSTANCES);
        long nrOfCompletedInstances = (Integer)vars.get(SysVariables.NR_OF_COMPLETED_INSTANCES);
        long loopCounter = (Integer)vars.get(SysVariables.LOOP_COUNTER);
        int usersSize = context.varsUsers.size();

        if (usersSize > 1) {
            long finishedCount = adjustSequentialVariablesIfNeeded(context, tenantId, nrOfInstances,
                nrOfCompletedInstances, nrOfActiveInstances, loopCounter, usersSize);

            if (nrOfInstances == (finishedCount + 1)
                && orgUnitId.equals(context.varsUsers.get(context.varsUsers.size() - 1))) {
                context.isLastSequential = true;
            }
        } else {
            context.isLastSequential = true;
        }
    }

    /**
     * 调整串行变量
     * 
     * @param context 任务上下文
     * @param tenantId 租户ID
     * @param nrOfInstances 实例数量
     * @param nrOfCompletedInstances 已完成实例数量
     * @param nrOfActiveInstances 激活实例数量
     * @param loopCounter 循环计数器
     * @param usersSize 用户数量
     * @return 已完成数量
     */
    private long adjustSequentialVariablesIfNeeded(TaskContext context, String tenantId, int nrOfInstances,
        long nrOfCompletedInstances, int nrOfActiveInstances, long loopCounter, int usersSize) {
        if (usersSize != nrOfInstances || nrOfCompletedInstances != loopCounter || 1 != nrOfActiveInstances) {
            long finishedCount =
                historictaskApi.getFinishedCountByExecutionId(tenantId, context.task.getExecutionId()).getData();
            Map<String, Object> varMapTemp = new HashMap<>(16);
            varMapTemp.put(SysVariables.NR_OF_INSTANCES, usersSize);
            varMapTemp.put(SysVariables.NR_OF_COMPLETED_INSTANCES, finishedCount);
            varMapTemp.put(SysVariables.LOOP_COUNTER, finishedCount);
            varMapTemp.put(SysVariables.NR_OF_ACTIVE_INSTANCES, 1);
            runtimeApi.setVariables(tenantId, context.task.getExecutionId(), varMapTemp);
            return finishedCount;
        }
        return nrOfCompletedInstances;
    }

    /**
     * 处理并行状态
     * 
     * @param context 任务上下文
     * @param orgUnitId 组织机构ID
     */
    private void handleParallelStatus(TaskContext context, String orgUnitId) {
        context.isParallel = true;

        if (StringUtils.isNotBlank(context.varsSponsorGuid) && orgUnitId.equals(context.varsSponsorGuid)) {
            context.isParallelSponsor = true;
        }

        int nrOfInstances = (Integer)context.vars.get(SysVariables.NR_OF_INSTANCES);
        long nrOfCompletedInstances = (Integer)context.vars.get(SysVariables.NR_OF_COMPLETED_INSTANCES);

        if (nrOfInstances > 0) {
            if (nrOfInstances == 1) {
                context.isLastParallel = true;
            } else {
                if (nrOfInstances == (nrOfCompletedInstances + 1)) {
                    context.isLastParallel = true;
                    context.isParallelSponsor = true;
                }
            }
        }
    }

    /**
     * 处理ItemBox类型
     * 
     * @param result 待返回结果
     * @param taskContext 任务上下文
     * @param itemId 事项ID
     * @param taskId 任务ID
     * @param itemBox 类型
     * @param tenantId 租户ID
     * @param orgUnitId 组织机构ID
     */
    private void handleItemBoxType(Map<String, Object> result, TaskContext taskContext, String itemId, String taskId,
        String itemBox, String tenantId, String orgUnitId) {
        boolean[] isButtonShow = (boolean[])result.get("isButtonShow");
        switch (ItemBoxTypeEnum.fromString(itemBox)) {
            case TODO:
                handleTodoBox(result, isButtonShow, taskContext, itemId, taskId, tenantId, orgUnitId);
                break;
            case DOING:
                handleDoingBox(isButtonShow, taskContext, taskId, tenantId, orgUnitId);
                break;
            case DONE:
                handleDoneBox(isButtonShow);
                break;
            case ADD:
                handleAddBox(isButtonShow, taskContext);
                break;
            case DRAFT:
                handleDraftBox(isButtonShow, taskContext);
                break;
            case MONITOR_DOING:
                handleMonitorDoingBox(isButtonShow);
                break;
            case PAUSE:
                handlePauseBox(isButtonShow);
                break;
            default:
                // 默认情况，至少显示返回和打印按钮
                isButtonShow[2] = true; // 返回
                isButtonShow[16] = true; // 打印
                break;
        }
        // 所有情况都显示打印按钮
        isButtonShow[16] = true;
    }

    private void handleTodoBox(Map<String, Object> result, boolean[] isButtonShow, TaskContext taskContext,
        String itemId, String taskId, String tenantId, String orgUnitId) {
        // 设置默认值
        result.put(ItemConsts.NEXTNODE_KEY, false);
        result.put("multiInstance", taskContext.multiInstance);

        if (taskContext.task != null) {
            handleTaskButtons(result, isButtonShow, taskContext, itemId, taskId, tenantId, orgUnitId);
        } else {
            // task为null，此时是新增
            handleNewTaskButtons(isButtonShow, taskContext);
        }
        // 抄送按钮总是显示
        isButtonShow[17] = true;
        // 处理加减签按钮
        handleSignButtons(isButtonShow, taskContext, orgUnitId);
    }

    private void handleTaskButtons(Map<String, Object> result, boolean[] isButtonShow, TaskContext taskContext,
        String itemId, String taskId, String tenantId, String orgUnitId) {
        String assignee = taskContext.task.getAssignee();
        boolean isAssignee = StringUtils.isNotBlank(assignee);
        // 保存按钮
        if (isAssignee) {
            isButtonShow[0] = true;
        }
        // 发送按钮
        handleSendButton(result, isButtonShow, taskContext, itemId, taskId, tenantId);
        // 返回按钮
        isButtonShow[2] = true;
        // 退回按钮
        handleReturnButton(isButtonShow, taskContext, taskId, tenantId, orgUnitId);
        // 协办状态下的完成按钮
        handleDelegationCompleteButton(isButtonShow, taskContext);
        // 送下一人按钮
        handleSendNextButton(isButtonShow, taskContext);
        // 并行处理时办理完成按钮
        handleParallelCompleteButton(isButtonShow, taskContext, itemId, taskId, tenantId);
        // 签收按钮
        handleClaimButton(result, isButtonShow, taskContext, taskId, tenantId);
        // 撤销签收按钮
        handleUnclaimButton(isButtonShow, taskContext, taskId, tenantId, orgUnitId);
        // 办结按钮
        handleEndButton(result, isButtonShow, taskContext, itemId, taskId, tenantId);
    }

    private void handleReturnButton(boolean[] isButtonShow, TaskContext taskContext, String taskId, String tenantId,
        String orgUnitId) {
        boolean isAssignee = StringUtils.isNotBlank(taskContext.task.getAssignee());
        if (isAssignee && !taskContext.customItem) {
            String returnDoc = variableApi.getVariableLocal(tenantId, taskId, SysVariables.ROLLBACK).getData();
            String takeBackDoc = variableApi.getVariableLocal(tenantId, taskId, SysVariables.TAKEBACK).getData();
            String repositionDoc = variableApi.getVariableLocal(tenantId, taskId, SysVariables.REPOSITION).getData();

            // 当前任务为退回件,或者收回件都不能再退回，不显示退回按钮
            if (returnDoc != null || takeBackDoc != null || repositionDoc != null) {
                isButtonShow[3] = false;
            } else {
                if (taskContext.isParallel) {
                    // 并行状态下都可以退回，退回只退回自己的任务，不影响其他人
                    isButtonShow[3] = true;
                } else if (taskContext.isSequential) {
                    isButtonShow[3] = true;
                } else {
                    // 当前任务不是退出任务且发送人不等于当前人的时候才可以显示退回
                    if (!taskContext.taskSenderId.equals(orgUnitId)) {
                        isButtonShow[3] = true;
                    }
                }
            }
        }
    }

    private void handleDelegationCompleteButton(boolean[] isButtonShow, TaskContext taskContext) {
        boolean isAssignee = StringUtils.isNotBlank(taskContext.task.getAssignee());
        if (isAssignee) {
            // 此时说明用户在协办状态，发送按钮不再显示，完成按钮显示
            if (DelegationState.PENDING == taskContext.task.getDelegationState()) {
                // 协办状态下，不再允许转办
                isButtonShow[4] = false;
                // 协办状态下，不再允许协办
                isButtonShow[5] = false;
                isButtonShow[6] = true;
            }
        }
    }

    private void handleSendNextButton(boolean[] isButtonShow, TaskContext taskContext) {
        boolean isAssignee = StringUtils.isNotBlank(taskContext.task.getAssignee());
        if (taskContext.isSequential && !taskContext.isLastSequential && isAssignee
            && DelegationState.PENDING != taskContext.task.getDelegationState()) {
            isButtonShow[7] = true;
            isButtonShow[12] = false;
            // 定制流程处理
            if (taskContext.customItem) {
                // 不显示送下一人
                isButtonShow[7] = false;
                // 定制流程的任务都以办理完成往下走
                isButtonShow[8] = true;
            }
        }
    }

    private void handleParallelCompleteButton(boolean[] isButtonShow, TaskContext taskContext, String itemId,
        String taskId, String tenantId) {
        // 基本前提条件检查
        if (!shouldShowParallelCompleteButton(taskContext)) {
            return;
        }

        // 如果不是主办人，并且不是最后一个处理人，显示办理完成按钮
        if (!taskContext.isParallelSponsor && !taskContext.isLastParallel) {
            handleParallelCompleteButtonLogic(isButtonShow, taskContext, itemId, taskId, tenantId);
        }
    }

    /**
     * 检查是否应该显示并行处理完成按钮
     *
     * @param taskContext 任务上下文
     * @return true表示应该显示，false表示不应该显示
     */
    private boolean shouldShowParallelCompleteButton(TaskContext taskContext) {
        boolean isAssignee = StringUtils.isNotBlank(taskContext.task.getAssignee());
        boolean isDelegationPending = DelegationState.PENDING == taskContext.task.getDelegationState();

        // 办理完成，并行状态下存在人员已经指定，在并行状态下并且不是并行状态下主办，同时用户设置了存在主协办
        return isAssignee && taskContext.isParallel && !isDelegationPending;
    }

    /**
     * 处理并行处理完成按钮的逻辑
     *
     * @param isButtonShow 按钮显示数组
     * @param taskContext 任务上下文
     * @param itemId 事项ID
     * @param taskId 任务ID
     * @param tenantId 租户ID
     */
    private void handleParallelCompleteButtonLogic(boolean[] isButtonShow, TaskContext taskContext, String itemId,
        String taskId, String tenantId) {
        ItemTaskConf itemTaskConf = itemTaskConfService.findByItemIdAndProcessDefinitionIdAndTaskDefKey4Own(itemId,
            taskContext.task.getProcessDefinitionId(), taskContext.task.getTaskDefinitionKey());

        if (isSignTask(itemTaskConf)) {
            handleSignTaskCompleteButton(isButtonShow, taskContext, taskId, tenantId);
        } else {
            // 显示办理完成按钮
            isButtonShow[8] = true;
        }
    }

    /**
     * 判断是否为主协办任务
     *
     * @param itemTaskConf 事项任务配置
     * @return true表示为主协办任务，false表示不是
     */
    private boolean isSignTask(ItemTaskConf itemTaskConf) {
        return null != itemTaskConf && itemTaskConf.getSignTask();
    }

    /**
     * 处理主协办任务的完成按钮显示逻辑
     *
     * @param isButtonShow 按钮显示数组
     * @param taskContext 任务上下文
     * @param taskId 任务ID
     * @param tenantId 租户ID
     */
    private void handleSignTaskCompleteButton(boolean[] isButtonShow, TaskContext taskContext, String taskId,
        String tenantId) {
        int outPutNodeCount = processDefinitionApi.getOutPutNodeCount(tenantId, taskId).getData();
        if (outPutNodeCount > 0) {
            if (taskContext.showSubmitButton) {
                isButtonShow[20] = true; // 提交按钮
            } else {
                isButtonShow[1] = true; // 发送按钮
            }
        } else {
            isButtonShow[8] = false; // 隐藏办理完成按钮
        }
    }

    private void handleClaimButton(Map<String, Object> result, boolean[] isButtonShow, TaskContext taskContext,
        String taskId, String tenantId) {
        // 签收、拒签按钮
        // 当user变量没有指定人员时，显示签收
        if (!StringUtils.isNotBlank(taskContext.task.getAssignee())) {
            isButtonShow[9] = true;
            isButtonShow[12] = false;
            isButtonShow[13] = true;
            // 是否是最后一个拒签人员，如果是则提示是否拒签，如果拒签，则退回任务给发送人，发送人重新选择人员进行签收办理
            List<IdentityLinkModel> identityLinkList = identityApi.getIdentityLinksForTask(tenantId, taskId).getData();
            if (identityLinkList.size() <= 1) {
                result.put("isLastPerson4RefuseClaim", true);
            }
        }
    }

    private void handleUnclaimButton(boolean[] isButtonShow, TaskContext taskContext, String taskId, String tenantId,
        String orgUnitId) {
        // 撤销签收 - 只有已签收的用户才能操作
        if (!StringUtils.isNotBlank(taskContext.task.getAssignee())) {
            return;
        }
        // 判断当前流程实例经过的任务节点数和当前流程实例是否存在父流程实例
        // 如果任务节点数为1且存在父流程实例，则是流程调用，此时显示拒签按钮
        // 否则是流程中发给多人等情况，不显示拒签按钮
        if (isSingleTaskInstance(tenantId, taskContext)) {
            handleSingleTaskInstanceCase(isButtonShow, taskContext);
        } else {
            handleMultipleTaskInstanceCase(isButtonShow, tenantId, taskId, orgUnitId);
        }
    }

    /**
     * 判断是否为单任务实例（父子流程调用情况）
     *
     * @param tenantId 租户ID
     * @param taskContext 任务上下文
     * @return true表示是单任务实例，false表示不是
     */
    private boolean isSingleTaskInstance(String tenantId, TaskContext taskContext) {
        int count = historictaskApi.getByProcessInstanceId(tenantId, taskContext.task.getProcessInstanceId(), "")
            .getData()
            .size();
        return count == 1;
    }

    /**
     * 处理单任务实例情况（父子流程调用）
     *
     * @param isButtonShow 按钮显示数组
     * @param taskContext 任务上下文
     */
    private void handleSingleTaskInstanceCase(boolean[] isButtonShow, TaskContext taskContext) {
        String superProcessInstanceId =
            procInstanceRelationshipService.getParentProcInstanceId(taskContext.task.getProcessInstanceId());
        // 是父子流程，显示撤销签收按钮
        if (StringUtils.isNotBlank(superProcessInstanceId)) {
            isButtonShow[10] = true;
        }
    }

    /**
     * 处理多任务实例情况
     *
     * @param isButtonShow 按钮显示数组
     * @param tenantId 租户ID
     * @param taskId 任务ID
     * @param orgUnitId 组织单元ID
     */
    private void handleMultipleTaskInstanceCase(boolean[] isButtonShow, String tenantId, String taskId,
        String orgUnitId) {
        List<IdentityLinkModel> identityLinkList = identityApi.getIdentityLinksForTask(tenantId, taskId).getData();
        // 只有当候选人员数量大于2时才显示撤销签收按钮
        if (identityLinkList.size() > 2) {
            for (IdentityLinkModel identityLink : identityLinkList) {
                if (identityLink.getUserId().contains(orgUnitId) && "candidate".equals(identityLink.getType())) {
                    isButtonShow[10] = true;
                    break;
                }
            }
        }
    }

    private void handleEndButton(Map<String, Object> result, boolean[] isButtonShow, TaskContext taskContext,
        String itemId, String taskId, String tenantId) {
        // 基本前提条件检查
        if (!shouldShowEndButton(taskContext, tenantId, taskId)) {
            return;
        }

        // 处理办结按钮显示逻辑
        processEndButtonDisplay(isButtonShow, taskContext, itemId);

        // 处理收回按钮状态
        isButtonShow[12] = false;

        // 处理定制流程的特殊逻辑
        if (taskContext.customItem) {
            handleCustomItemEndButtonLogic(isButtonShow, result, taskContext);
        }
    }

    /**
     * 检查是否应该显示办结按钮
     *
     * @param taskContext 任务上下文
     * @param tenantId 租户ID
     * @param taskId 任务ID
     * @return true表示应该显示办结按钮，false表示不应该显示
     */
    private boolean shouldShowEndButton(TaskContext taskContext, String tenantId, String taskId) {
        boolean isAssignee = StringUtils.isNotBlank(taskContext.task.getAssignee());
        Boolean isContainEndEvent = processDefinitionApi.isContainEndEvent(tenantId, taskId).getData();

        // 办结 - 当前节点的目标节点存在ENDEVENT类型节点时，显示办结按钮
        return isAssignee && Boolean.TRUE.equals(isContainEndEvent);
    }

    /**
     * 处理办结按钮显示逻辑
     *
     * @param isButtonShow 按钮显示数组
     * @param taskContext 任务上下文
     * @param itemId 事项ID
     */
    private void processEndButtonDisplay(boolean[] isButtonShow, TaskContext taskContext, String itemId) {
        if (taskContext.isParallel) {
            handleParallelEndButton(isButtonShow, taskContext, itemId);
        } else if (taskContext.isSequential) {
            handleSequentialEndButton(isButtonShow, taskContext);
        } else {
            // 如果既不是并行也不是串行
            isButtonShow[11] = true;
        }
    }

    /**
     * 处理并行状态下的办结按钮显示
     *
     * @param isButtonShow 按钮显示数组
     * @param taskContext 任务上下文
     * @param itemId 事项ID
     */
    private void handleParallelEndButton(boolean[] isButtonShow, TaskContext taskContext, String itemId) {
        ItemTaskConf itemTaskConf = itemTaskConfService.findByItemIdAndProcessDefinitionIdAndTaskDefKey4Own(itemId,
            taskContext.task.getProcessDefinitionId(), taskContext.task.getTaskDefinitionKey());

        if (isSignTask(itemTaskConf)) {
            isButtonShow[11] = true;
        } else {
            if (taskContext.isParallelSponsor || taskContext.isLastParallel) {
                isButtonShow[11] = true;
            }
        }
    }

    /**
     * 处理串行状态下的办结按钮显示
     *
     * @param isButtonShow 按钮显示数组
     * @param taskContext 任务上下文
     */
    private void handleSequentialEndButton(boolean[] isButtonShow, TaskContext taskContext) {
        // 如果在串行状态下，那么就要看是不是最后一个用户，如果是则显示办结按钮，否则不显示
        if (taskContext.isLastSequential) {
            isButtonShow[11] = true;
        }
    }

    /**
     * 处理定制流程的办结按钮逻辑
     *
     * @param isButtonShow 按钮显示数组
     * @param result 结果Map
     * @param taskContext 任务上下文
     */
    private void handleCustomItemEndButtonLogic(boolean[] isButtonShow, Map<String, Object> result,
        TaskContext taskContext) {
        CustomProcessInfo info = customProcessInfoService
            .getCurrentTaskNextNode((String)taskContext.vars.get(SysVariables.PROCESS_SERIAL_NUMBER));

        // 如果当前运行任务的下一个节点是办结,且显示办结按钮,则隐藏办理完成按钮
        if (info.getTaskType().equals(SysVariables.END_EVENT)) {
            if (isButtonShow[11]) {
                // 办理完成按钮隐藏
                isButtonShow[8] = false;
                // 用于判定是否需要发送下一个节点
                result.put(ItemConsts.NEXTNODE_KEY, true);
            }
        } else {
            // 如果当前运行任务的下一个节点不是办结,隐藏办结按钮
            isButtonShow[11] = false;
        }
    }

    private void handleSendButton(Map<String, Object> result, boolean[] isButtonShow, TaskContext taskContext,
        String itemId, String taskId, String tenantId) {
        // 基本前提条件检查
        if (!shouldShowSendButton(taskContext, tenantId, taskId)) {
            return;
        }

        // 处理发送按钮显示逻辑
        boolean showSendButton = determineSendButtonVisibility(taskContext, itemId);

        if (showSendButton) {
            displaySendButton(isButtonShow, result, taskContext);
        }

        // 处理定制流程逻辑
        handleCustomItemSendLogic(isButtonShow, result, taskContext);

        // 处理加减签按钮显示逻辑
        handleSignButtonDisplay(isButtonShow, taskContext);
    }

    /**
     * 检查是否应该显示发送按钮
     *
     * @param taskContext 任务上下文
     * @param tenantId 租户ID
     * @param taskId 任务ID
     * @return true表示应该显示发送按钮，false表示不应该显示
     */
    private boolean shouldShowSendButton(TaskContext taskContext, String tenantId, String taskId) {
        boolean isAssignee = StringUtils.isNotBlank(taskContext.task.getAssignee());
        int outPutNodeCount = processDefinitionApi.getOutPutNodeCount(tenantId, taskId).getData();

        // DelegationState.PENDING != task.getDelegationState()：表示当前用户是在协办状态，发送按钮不再显示，完成按钮显示
        // outPutNodeCount>0表示存在发送节点
        return DelegationState.PENDING != taskContext.task.getDelegationState() && isAssignee && outPutNodeCount > 0;
    }

    /**
     * 确定发送按钮是否可见
     *
     * @param taskContext 任务上下文
     * @param itemId 事项ID
     * @return true表示发送按钮可见，false表示不可见
     */
    private boolean determineSendButtonVisibility(TaskContext taskContext, String itemId) {
        if (taskContext.isParallel) {
            return handleParallelSendButton(taskContext, itemId);
        } else if (taskContext.isSequential) {
            return taskContext.isLastSequential;
        } else {
            // 如果既不是并行也不是串行
            return true;
        }
    }

    /**
     * 处理并行状态下的发送按钮显示逻辑
     *
     * @param taskContext 任务上下文
     * @param itemId 事项ID
     * @return true表示应该显示发送按钮，false表示不应该显示
     */
    private boolean handleParallelSendButton(TaskContext taskContext, String itemId) {
        // 如果是在并行状态下，那么就要看是不是并行状态主办人，如果是则显示发送按钮，否则不显示
        if (taskContext.isParallelSponsor || taskContext.isLastParallel) {
            return true;
        } else {
            ItemTaskConf itemTaskConf = itemTaskConfService.findByItemIdAndProcessDefinitionIdAndTaskDefKey4Own(itemId,
                taskContext.task.getProcessDefinitionId(), taskContext.task.getTaskDefinitionKey());
            return isSignTask(itemTaskConf);
        }
    }

    /**
     * 显示发送按钮
     *
     * @param isButtonShow 按钮显示数组
     * @param result 结果Map
     * @param taskContext 任务上下文
     */
    private void displaySendButton(boolean[] isButtonShow, Map<String, Object> result, TaskContext taskContext) {
        if (taskContext.showSubmitButton) {
            isButtonShow[20] = true; // 提交
        } else {
            isButtonShow[1] = true; // 发送
        }

        // 主办办理
        if (taskContext.isParallelSponsor) {
            result.put("sponsorHandle", "true");
        }
    }

    /**
     * 处理定制流程的发送逻辑
     *
     * @param isButtonShow 按钮显示数组
     * @param result 结果Map
     * @param taskContext 任务上下文
     */
    private void handleCustomItemSendLogic(boolean[] isButtonShow, Map<String, Object> result,
        TaskContext taskContext) {
        // 定制流程处理,有发送按钮说明需要进入下一个定制任务
        if (taskContext.customItem) {
            isButtonShow[8] = true; // 办理完成
            if (isButtonShow[1]) {
                isButtonShow[1] = false; // 不显示发送
                result.put(ItemConsts.NEXTNODE_KEY, true);
            }
        }
    }

    /**
     * 处理加减签按钮显示逻辑
     *
     * @param isButtonShow 按钮显示数组
     * @param taskContext 任务上下文
     */
    private void handleSignButtonDisplay(boolean[] isButtonShow, TaskContext taskContext) {
        // 没有发送按钮的时候，串并行显示加减签按钮
        boolean showSignButton = (taskContext.multiInstance.equals(SysVariables.PARALLEL)
            || taskContext.multiInstance.equals(SysVariables.SEQUENTIAL)) && !isButtonShow[1];
        if (showSignButton) {
            isButtonShow[18] = true;
        }
    }

    private void handleDoingBox(boolean[] isButtonShow, TaskContext taskContext, String taskId, String tenantId,
        String orgUnitId) {
        isButtonShow[2] = true; // 返回

        // 收回按钮
        isButtonShow[12] = false;
        String takeBackObj = variableApi.getVariableLocal(tenantId, taskId, SysVariables.TAKEBACK).getData();
        String rollbackObj = variableApi.getVariableLocal(tenantId, taskId, SysVariables.ROLLBACK).getData();
        String repositionObj = variableApi.getVariableLocal(tenantId, taskId, SysVariables.REPOSITION).getData();

        if (StringUtils.isNotBlank(taskContext.taskSenderId) && taskContext.taskSenderId.contains(orgUnitId)
            && takeBackObj == null && rollbackObj == null && repositionObj == null) {
            isButtonShow[12] = true;
        }

        // 加减签按钮
        boolean showSignButton = (taskContext.multiInstance.equals(SysVariables.PARALLEL)
            || taskContext.multiInstance.equals(SysVariables.SEQUENTIAL))
            && StringUtils.isNotBlank(taskContext.taskSenderId) && taskContext.taskSenderId.contains(orgUnitId);
        if (showSignButton) {
            isButtonShow[18] = true;
        }

        // 抄送
        isButtonShow[17] = true;

        ProcessInstanceModel processInstanceModel =
            runtimeApi.getProcessInstance(tenantId, taskContext.task.getProcessInstanceId()).getData();

        // 重定位按钮
        isButtonShow[15] = true;

        if (orgUnitId.equals(processInstanceModel.getStartUserId())) {
            isButtonShow[14] = true; // 特殊办结
        }
    }

    private void handleDoneBox(boolean[] isButtonShow) {
        isButtonShow[2] = true; // 返回
        isButtonShow[19] = true; // 恢复待办
    }

    private void handleAddBox(boolean[] isButtonShow, TaskContext taskContext) {
        isButtonShow[0] = true; // 保存
        if (taskContext.showSubmitButton) {
            isButtonShow[20] = true; // 提交
        } else {
            isButtonShow[1] = true; // 发送
        }
        isButtonShow[17] = true; // 抄送
    }

    private void handleDraftBox(boolean[] isButtonShow, TaskContext taskContext) {
        isButtonShow[0] = true; // 保存
        if (taskContext.showSubmitButton) {
            isButtonShow[20] = true; // 提交
        } else {
            isButtonShow[1] = true; // 发送
        }
        isButtonShow[2] = true; // 返回
        isButtonShow[17] = true; // 抄送
    }

    private void handleMonitorDoingBox(boolean[] isButtonShow) {
        isButtonShow[15] = true; // 重定位
        isButtonShow[14] = true; // 特殊办结
        isButtonShow[2] = true; // 返回
    }

    private void handlePauseBox(boolean[] isButtonShow) {
        isButtonShow[2] = true; // 返回
    }

    private void handleNewTaskButtons(boolean[] isButtonShow, TaskContext taskContext) {
        isButtonShow[0] = true; // 保存
        if (taskContext.showSubmitButton) {
            isButtonShow[20] = true; // 提交
        } else {
            isButtonShow[1] = true; // 发送
        }
    }

    private void handleSignButtons(boolean[] isButtonShow, TaskContext taskContext, String orgUnitId) {
        boolean showSignButton = (taskContext.multiInstance.equals(SysVariables.PARALLEL)
            || taskContext.multiInstance.equals(SysVariables.SEQUENTIAL)) && !taskContext.customItem
            && StringUtils.isNotBlank(taskContext.taskSenderId) && taskContext.taskSenderId.contains(orgUnitId)
            || (StringUtils.isNotBlank(taskContext.varsSponsorGuid) && orgUnitId.equals(taskContext.varsSponsorGuid));
        if (showSignButton) {
            isButtonShow[18] = true;
        }
    }

    @Override
    public List<ItemButtonModel> showButton4Add(String itemId) {
        List<ItemButtonModel> buttonModelList = new ArrayList<>();
        buttonModelList.add(ItemButton.baoCun);
        buttonModelList.add(ItemButton.chaoSong);
        Item item = itemService.findById(itemId);
        boolean showSubmitButton = item.isShowSubmitButton();
        if (showSubmitButton) {
            buttonModelList.add(ItemButton.tiJiao);
        } else {
            buttonModelList.add(ItemButton.faSong);
        }
        return buttonModelList.stream()
            .sorted(Comparator.comparing(ItemButtonModel::getTabIndex))
            .collect(Collectors.toList());
    }

    @Override
    public List<ItemButtonModel> showButton4Draft(String itemId) {
        List<ItemButtonModel> buttonModelList = new ArrayList<>();
        buttonModelList.add(ItemButton.baoCun);
        buttonModelList.add(ItemButton.chaoSong);
        Item item = itemService.findById(itemId);
        boolean showSubmitButton = item.isShowSubmitButton();
        if (showSubmitButton) {
            buttonModelList.add(ItemButton.tiJiao);
        } else {
            buttonModelList.add(ItemButton.faSong);
        }
        return buttonModelList.stream()
            .sorted(Comparator.comparing(ItemButtonModel::getTabIndex))
            .collect(Collectors.toList());
    }

    @Override
    public List<ItemButtonModel> showButton4ChaoSong(DocumentDetailModel model) {
        List<ItemButtonModel> buttonModelList = new ArrayList<>();
        buttonModelList.add(ItemButton.daYin);
        if (!ItemBoxTypeEnum.MONITOR_CHAOSONG.equals(ItemBoxTypeEnum.fromString(model.getItembox()))) {
            buttonModelList.add(ItemButton.chaoSong);
            buttonModelList.add(ItemButton.follow);
        }
        return buttonModelList.stream()
            .sorted(Comparator.comparing(ItemButtonModel::getTabIndex))
            .collect(Collectors.toList());
    }

    @Override
    public List<ItemButtonModel> showButton4Doing(DocumentDetailModel model) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String orgUnitId = Y9FlowableHolder.getOrgUnitId();
        String taskId = model.getTaskId();

        List<ItemButtonModel> buttonModelList = new ArrayList<>();
        buttonModelList.add(ItemButton.chaoSong);
        buttonModelList.add(ItemButton.daYin);

        TaskModel task = taskApi.findById(tenantId, taskId).getData();
        if (task != null) {
            // 处理加减签按钮
            handleSignButtons4Doing(buttonModelList, tenantId, taskId, orgUnitId, task);

            // 添加重定向按钮
            buttonModelList.add(ItemButton.chongDingWei);

            // 添加流程节点按钮
            addProcessNodeButtons(buttonModelList, tenantId, task);

            // 处理特殊办结按钮
            handleSpecialCompletionButton(buttonModelList, tenantId, task, orgUnitId);

            // 处理收回按钮
            handleTakeBackButton(buttonModelList, tenantId, taskId, orgUnitId, task);
        }

        return buttonModelList.stream()
            .sorted(Comparator.comparing(ItemButtonModel::getTabIndex))
            .collect(Collectors.toList());
    }

    /**
     * 处理加减签按钮显示逻辑
     *
     * @param buttonModelList 按钮列表
     * @param tenantId 租户ID
     * @param taskId 任务ID
     * @param orgUnitId 组织单元ID
     * @param task 任务模型
     */
    private void handleSignButtons4Doing(List<ItemButtonModel> buttonModelList, String tenantId, String taskId,
        String orgUnitId, TaskModel task) {
        Map<String, Object> vars = variableApi.getVariables(tenantId, taskId).getData();
        String taskSenderId = String.valueOf(vars.get(SysVariables.TASK_SENDER_ID));

        String multiInstance =
            processDefinitionApi.getNodeType(tenantId, task.getProcessDefinitionId(), task.getTaskDefinitionKey())
                .getData();

        boolean showSignButton =
            (multiInstance.equals(SysVariables.PARALLEL) || multiInstance.equals(SysVariables.SEQUENTIAL))
                && StringUtils.isNotBlank(taskSenderId) && taskSenderId.contains(orgUnitId);

        if (showSignButton) {
            buttonModelList.add(ItemButton.jiaJianQian);
        }
    }

    /**
     * 添加流程节点按钮
     *
     * @param buttonModelList 按钮列表
     * @param tenantId 租户ID
     * @param task 任务模型
     */
    private void addProcessNodeButtons(List<ItemButtonModel> buttonModelList, String tenantId, TaskModel task) {
        List<TargetModel> taskNodes = processDefinitionApi.getNodes(tenantId, task.getProcessDefinitionId()).getData();
        AtomicInteger index = new AtomicInteger(100);

        taskNodes.stream()
            .filter(node -> StringUtils.isNotBlank(node.getTaskDefKey()))
            .forEach(node -> buttonModelList.add(new ItemButtonModel(node.getTaskDefKey(), node.getTaskDefName(),
                ItemButtonTypeEnum.REPOSITION, index.getAndIncrement())));
    }

    /**
     * 处理特殊办结按钮显示逻辑
     *
     * @param buttonModelList 按钮列表
     * @param tenantId 租户ID
     * @param task 任务模型
     * @param orgUnitId 组织单元ID
     */
    private void handleSpecialCompletionButton(List<ItemButtonModel> buttonModelList, String tenantId, TaskModel task,
        String orgUnitId) {
        ProcessInstanceModel processInstanceModel =
            runtimeApi.getProcessInstance(tenantId, task.getProcessInstanceId()).getData();

        if (orgUnitId.equals(processInstanceModel.getStartUserId())) {
            buttonModelList.add(ItemButton.teShuBanJie);
        }
    }

    /**
     * 处理收回按钮显示逻辑
     *
     * @param buttonModelList 按钮列表
     * @param tenantId 租户ID
     * @param taskId 任务ID
     * @param orgUnitId 组织单元ID
     * @param task 任务模型
     */
    private void handleTakeBackButton(List<ItemButtonModel> buttonModelList, String tenantId, String taskId,
        String orgUnitId, TaskModel task) {
        Map<String, Object> vars = variableApi.getVariables(tenantId, taskId).getData();
        String taskSenderId = String.valueOf(vars.get(SysVariables.TASK_SENDER_ID));

        String takeBackObj = variableApi.getVariableLocal(tenantId, taskId, SysVariables.TAKEBACK).getData();
        String rollbackObj = variableApi.getVariableLocal(tenantId, taskId, SysVariables.ROLLBACK).getData();
        String repositionObj = variableApi.getVariableLocal(tenantId, taskId, SysVariables.REPOSITION).getData();

        if (StringUtils.isNotBlank(taskSenderId) && taskSenderId.contains(orgUnitId) && takeBackObj == null
            && rollbackObj == null && repositionObj == null) {

            Boolean isSub4Current = processDefinitionApi
                .isSubProcessChildNode(tenantId, task.getProcessDefinitionId(), task.getTaskDefinitionKey())
                .getData();

            if (Boolean.TRUE.equals(isSub4Current)) {
                handleSubProcessTakeBack(buttonModelList, tenantId, orgUnitId, task);
            } else {
                handleNormalProcessTakeBack(buttonModelList, tenantId, taskId, task);
            }
        }
    }

    /**
     * 处理子流程的收回按钮逻辑
     *
     * @param buttonModelList 按钮列表
     * @param tenantId 租户ID
     * @param orgUnitId 组织单元ID
     * @param task 任务模型
     */
    private void handleSubProcessTakeBack(List<ItemButtonModel> buttonModelList, String tenantId, String orgUnitId,
        TaskModel task) {
        OrgUnit sendBureau = orgUnitApi.getBureau(tenantId, orgUnitId).getData();
        OrgUnit currentBureau = orgUnitApi.getBureau(tenantId, task.getAssignee()).getData();

        if (currentBureau.getId().equals(sendBureau.getId())) {
            buttonModelList.add(ItemButton.shouHui);
        }
    }

    /**
     * 处理普通流程的收回按钮逻辑
     *
     * @param buttonModelList 按钮列表
     * @param tenantId 租户ID
     * @param taskId 任务ID
     * @param task 任务模型
     */
    private void handleNormalProcessTakeBack(List<ItemButtonModel> buttonModelList, String tenantId, String taskId,
        TaskModel task) {
        List<HistoricTaskInstanceModel> hisTaskList = historictaskApi.getThePreviousTasks(tenantId, taskId).getData();

        if (!hisTaskList.isEmpty()) {
            Boolean isSubProcess4Send =
                processDefinitionApi
                    .isSubProcessChildNode(tenantId, task.getProcessDefinitionId(),
                        hisTaskList.get(0).getTaskDefinitionKey())
                    .getData();

            if (Boolean.FALSE.equals(isSubProcess4Send)) {
                buttonModelList.add(ItemButton.shouHui);
            }
        }
    }

    @Override
    public List<ItemButtonModel> showButton4DoingAdmin(DocumentDetailModel model) {
        String tenantId = Y9LoginUserHolder.getTenantId(), taskId = model.getTaskId();
        List<ItemButtonModel> buttonModelList = new ArrayList<>();
        buttonModelList.add(ItemButton.chaoSong);
        buttonModelList.add(ItemButton.daYin);
        buttonModelList.add(ItemButton.teShuBanJie);
        TaskModel task = taskApi.findById(tenantId, taskId).getData();
        if (task != null) {
            String multiInstance =
                processDefinitionApi.getNodeType(tenantId, task.getProcessDefinitionId(), task.getTaskDefinitionKey())
                    .getData();
            boolean b = (multiInstance.equals(SysVariables.PARALLEL) || multiInstance.equals(SysVariables.SEQUENTIAL));
            if (b) {
                buttonModelList.add(ItemButton.jiaJianQian);
            }
            buttonModelList.add(ItemButton.chongDingWei);
            List<TargetModel> taskNodes =
                processDefinitionApi.getNodes(tenantId, task.getProcessDefinitionId()).getData();
            AtomicInteger index = new AtomicInteger(100);
            taskNodes.stream()
                .filter(node -> StringUtils.isNotBlank(node.getTaskDefKey()))
                .forEach(node -> buttonModelList.add(new ItemButtonModel(node.getTaskDefKey(), node.getTaskDefName(),
                    ItemButtonTypeEnum.REPOSITION, index.getAndIncrement())));
        }
        return buttonModelList.stream()
            .sorted(Comparator.comparing(ItemButtonModel::getTabIndex))
            .collect(Collectors.toList());
    }

    @Override
    public List<ItemButtonModel> showButton4Done(DocumentDetailModel model) {
        List<ItemButtonModel> buttonModelList = new ArrayList<>();
        ItemBoxTypeEnum itemBox = ItemBoxTypeEnum.fromString(model.getItembox());
        switch (itemBox) {
            case DONE:
                ProcessParam processParam =
                    processParamService.findByProcessSerialNumber(model.getProcessSerialNumber());
                String year = processParam != null ? processParam.getCreateTime().substring(0, 4) : "";
                List<HistoricTaskInstanceModel> list =
                    historictaskApi
                        .getByProcessInstanceIdOrderByEndTimeDesc(Y9LoginUserHolder.getTenantId(),
                            model.getProcessInstanceId(), year)
                        .getData();
                HistoricTaskInstanceModel hisTaskModelTemp = list != null && !list.isEmpty() ? list.get(0) : null;
                if (hisTaskModelTemp != null
                    && hisTaskModelTemp.getAssignee().equals(Y9FlowableHolder.getOrgUnit().getId())) {
                    buttonModelList.add(ItemButton.huiFuDaiBan);
                }
                break;
            case MONITOR_DONE:
                buttonModelList.add(ItemButton.huiFuDaiBan);
                break;
            default:
                LOGGER.warn("未定义的ItemBoxTypeEnum:{}", itemBox);
                break;
        }
        buttonModelList.add(ItemButton.chaoSong);
        buttonModelList.add(ItemButton.daYin);
        return buttonModelList.stream()
            .sorted(Comparator.comparing(ItemButtonModel::getTabIndex))
            .collect(Collectors.toList());
    }

    @Override
    public List<ItemButtonModel> showButton4Recycle() {
        List<ItemButtonModel> buttonModelList = new ArrayList<>();
        buttonModelList.add(ItemButton.huiFu);
        buttonModelList.add(ItemButton.cheDiShanChu);
        return buttonModelList.stream()
            .sorted(Comparator.comparing(ItemButtonModel::getTabIndex))
            .collect(Collectors.toList());
    }

    @Override
    public List<ItemButtonModel> showButton4Todo(DocumentDetailModel model) {
        String itemId = model.getItemId();
        String taskId = model.getTaskId();
        String tenantId = Y9LoginUserHolder.getTenantId();
        String orgUnitId = Y9FlowableHolder.getOrgUnitId();

        List<ItemButtonModel> buttonList = new ArrayList<>();
        TaskModel task = taskApi.findById(tenantId, taskId).getData();

        if (task == null) {
            return buttonList;
        }

        // 构建任务上下文
        TodoTaskContext context = buildTodoTaskContext(itemId, taskId, tenantId, orgUnitId, task);

        // 处理保存按钮
        handleSaveButton(buttonList, context);

        // 处理发送相关按钮
        handleSendButtons(buttonList, context);

        // 处理送下一人按钮
        handleSendNextButton(buttonList, context);

        // 处理并行完成按钮
        handleParallelCompleteButton(buttonList, context);

        // 处理签收按钮
        handleClaimButtons(buttonList, context);

        // 处理退签按钮
        handleUnclaimButton(buttonList, context, tenantId, taskId);

        // 处理退回按钮
        handleReturnButton(buttonList, context, tenantId, taskId);

        // 处理办结按钮
        handleEndButton(buttonList, context);

        // 处理回收站按钮
        handleRecycleBinButton(buttonList, context, tenantId);

        // 添加基础按钮
        buttonList.add(ItemButton.chaoSong);
        buttonList.add(ItemButton.daYin);

        return buttonList.stream()
            .sorted(Comparator.comparing(ItemButtonModel::getTabIndex))
            .collect(Collectors.toList());
    }

    /**
     * 构建待办任务上下文
     */
    private TodoTaskContext buildTodoTaskContext(String itemId, String taskId, String tenantId, String orgUnitId,
        TaskModel task) {
        TodoTaskContext context = new TodoTaskContext();
        context.itemId = itemId;
        context.taskId = taskId;
        context.tenantId = tenantId;
        context.orgUnitId = orgUnitId;
        context.task = task;

        Item item = itemService.findById(itemId);
        context.customItem = (null != item.getCustomItem()) ? item.getCustomItem() : false;
        context.showSubmitButton = item.isShowSubmitButton();

        context.vars = variableApi.getVariables(tenantId, taskId).getData();
        context.varsUsers = (List<String>)context.vars.get(SysVariables.USERS);
        context.varsSponsorGuid = String.valueOf(context.vars.get(SysVariables.PARALLEL_SPONSOR));
        context.taskSenderId = String.valueOf(context.vars.get(SysVariables.TASK_SENDER_ID));
        context.multiInstance =
            processDefinitionApi.getNodeType(tenantId, task.getProcessDefinitionId(), task.getTaskDefinitionKey())
                .getData();

        // 处理多实例状态
        handleMultiInstanceStatus(context, orgUnitId);

        context.assignee = task.getAssignee();
        context.isAssignee = StringUtils.isNotBlank(context.assignee);
        context.endNode = processDefinitionApi.getEndNode(tenantId, taskId).getData();
        context.outPutNodeCount = processDefinitionApi.getOutPutNodeCount(tenantId, taskId).getData();
        context.processDefinitionId = task.getProcessDefinitionId();
        context.taskDefKey = task.getTaskDefinitionKey();

        return context;
    }

    /**
     * 处理多实例状态
     */
    private void handleMultiInstanceStatus(TodoTaskContext context, String orgUnitId) {
        if (SysVariables.SEQUENTIAL.equals(context.multiInstance)) {
            handleSequentialStatus(context, orgUnitId);
        } else if (SysVariables.PARALLEL.equals(context.multiInstance)) {
            handleParallelStatus(context, orgUnitId);
        }
    }

    /**
     * 处理串行状态
     */
    private void handleSequentialStatus(TodoTaskContext context, String orgUnitId) {
        context.isSequential = true;

        int nrOfInstances = (Integer)context.vars.get(SysVariables.NR_OF_INSTANCES);
        int nrOfActiveInstances = (Integer)context.vars.get(SysVariables.NR_OF_ACTIVE_INSTANCES);
        long nrOfCompletedInstances = (Integer)context.vars.get(SysVariables.NR_OF_COMPLETED_INSTANCES);
        long loopCounter = (Integer)context.vars.get(SysVariables.LOOP_COUNTER);
        int usersSize = context.varsUsers.size();

        if (usersSize > 1) {
            if (usersSize != nrOfInstances || nrOfCompletedInstances != loopCounter || 1 != nrOfActiveInstances) {
                long finishedCount =
                    historictaskApi.getFinishedCountByExecutionId(context.tenantId, context.task.getExecutionId())
                        .getData();
                nrOfCompletedInstances = finishedCount;
                Map<String, Object> varMapTemp = new HashMap<>(16);
                varMapTemp.put(SysVariables.NR_OF_INSTANCES, usersSize);
                varMapTemp.put(SysVariables.NR_OF_COMPLETED_INSTANCES, finishedCount);
                varMapTemp.put(SysVariables.LOOP_COUNTER, finishedCount);
                varMapTemp.put(SysVariables.NR_OF_ACTIVE_INSTANCES, 1);
                runtimeApi.setVariables(context.tenantId, context.task.getExecutionId(), varMapTemp);
            }

            if (nrOfInstances == (nrOfCompletedInstances + 1)
                && orgUnitId.equals(context.varsUsers.get(context.varsUsers.size() - 1))) {
                context.isLastSequential = true;
            }
        } else {
            context.isLastSequential = true;
        }
    }

    /**
     * 处理并行状态
     */
    private void handleParallelStatus(TodoTaskContext context, String orgUnitId) {
        context.isParallel = true;

        if (StringUtils.isNotBlank(context.varsSponsorGuid) && orgUnitId.equals(context.varsSponsorGuid)) {
            context.isParallelSponsor = true;
        }

        int nrOfInstances = (Integer)context.vars.get(SysVariables.NR_OF_INSTANCES);
        long nrOfCompletedInstances = (Integer)context.vars.get(SysVariables.NR_OF_COMPLETED_INSTANCES);

        if (nrOfInstances > 0) {
            if (nrOfInstances == 1) {
                context.isLastParallel = true;
            } else {
                if (nrOfInstances == (nrOfCompletedInstances + 1)) {
                    context.isLastParallel = true;
                    context.isParallelSponsor = true;
                }
            }
        }
    }

    /**
     * 处理保存按钮
     */
    private void handleSaveButton(List<ItemButtonModel> buttonList, TodoTaskContext context) {
        if (context.isAssignee) {
            buttonList.add(ItemButton.baoCun);
        }
    }

    /**
     * 处理发送相关按钮
     */
    private void handleSendButtons(List<ItemButtonModel> buttonList, TodoTaskContext context) {
        if (DelegationState.PENDING != context.task.getDelegationState() && context.isAssignee
            && context.outPutNodeCount > 0) {
            boolean showSendButton = determineSendButtonVisibility(context);
            if (showSendButton) {
                if (context.showSubmitButton) {
                    buttonList.add(ItemButton.tiJiao);
                } else {
                    buttonList.add(ItemButton.faSong);
                }
            }
            // 定制流程处理
            if (context.customItem) {
                handleCustomItemSendLogic(buttonList);
            }
            // 处理加减签按钮
            handleSignButtonDisplay(buttonList, context);
        }
    }

    /**
     * 确定发送按钮是否可见
     */
    private boolean determineSendButtonVisibility(TodoTaskContext context) {
        if (context.isParallel) {
            return context.isParallelSponsor || context.isLastParallel || isSignTask(context);
        } else if (context.isSequential) {
            return context.isLastSequential;
        } else {
            return true;
        }
    }

    /**
     * 判断是否为主协办任务
     */
    private boolean isSignTask(TodoTaskContext context) {
        ItemTaskConf itemTaskConf = itemTaskConfService.findByItemIdAndProcessDefinitionIdAndTaskDefKey4Own(
            context.itemId, context.processDefinitionId, context.taskDefKey);
        return null != itemTaskConf && itemTaskConf.getSignTask();
    }

    /**
     * 处理定制流程的发送逻辑
     */
    private void handleCustomItemSendLogic(List<ItemButtonModel> buttonList) {
        buttonList.add(ItemButton.banLiWanCheng);
        buttonList.removeIf(button -> button.getKey().equals(ItemButton.faSong.getKey()));
    }

    /**
     * 处理加减签按钮显示逻辑
     */
    private void handleSignButtonDisplay(List<ItemButtonModel> buttonList, TodoTaskContext context) {
        boolean showSignButton = (context.multiInstance.equals(SysVariables.PARALLEL)
            || context.multiInstance.equals(SysVariables.SEQUENTIAL)) && !buttonList.contains(ItemButton.faSong);

        if (showSignButton) {
            buttonList.add(ItemButton.jiaJianQian);
        }
    }

    /**
     * 处理送下一人按钮
     */
    private void handleSendNextButton(List<ItemButtonModel> buttonList, TodoTaskContext context) {
        if (context.isSequential && !context.isLastSequential && context.isAssignee
            && DelegationState.PENDING != context.task.getDelegationState()) {

            if (context.customItem) {
                buttonList.add(ItemButton.banLiWanCheng);
            } else {
                buttonList.add(ItemButton.songXiaYiRen);
            }
        }
    }

    /**
     * 处理并行完成按钮
     */
    private void handleParallelCompleteButton(List<ItemButtonModel> buttonList, TodoTaskContext context) {
        // 基本前提条件检查
        if (!shouldHandleParallelComplete(context)) {
            return;
        }
        // 处理主协办任务逻辑
        if (isSignTask(context)) {
            handleSignTaskParallelComplete(buttonList, context);
        } else {
            // 普通并行任务逻辑
            handleNormalParallelComplete(buttonList);
        }
    }

    /**
     * 检查是否应该处理并行完成按钮
     *
     * @param context 待办任务上下文
     * @return true表示应该处理，false表示不应该处理
     */
    private boolean shouldHandleParallelComplete(TodoTaskContext context) {
        return context.isAssignee && context.isParallel && DelegationState.PENDING != context.task.getDelegationState()
            && !context.isParallelSponsor && !context.isLastParallel;
    }

    /**
     * 处理主协办任务的并行完成逻辑
     *
     * @param buttonList 按钮列表
     * @param context 待办任务上下文
     */
    private void handleSignTaskParallelComplete(List<ItemButtonModel> buttonList, TodoTaskContext context) {
        if (context.outPutNodeCount <= 0) {
            return;
        }

        if (context.showSubmitButton) {
            buttonList.add(ItemButton.tiJiao);
        } else {
            buttonList.add(ItemButton.faSong);
        }
    }

    /**
     * 处理普通并行任务的完成逻辑
     *
     * @param buttonList 按钮列表
     */
    private void handleNormalParallelComplete(List<ItemButtonModel> buttonList) {
        buttonList.add(ItemButton.banLiWanCheng);
    }

    /**
     * 处理签收按钮
     */
    private void handleClaimButtons(List<ItemButtonModel> buttonList, TodoTaskContext context) {
        if (!context.isAssignee) {
            buttonList.add(ItemButton.qianShou);
        }
    }

    /**
     * 处理退签按钮
     */
    /**
     * 处理退签按钮
     */
    private void handleUnclaimButton(List<ItemButtonModel> buttonList, TodoTaskContext context, String tenantId,
        String taskId) {
        // 只有已签收的用户才能操作退签按钮
        if (!context.isAssignee) {
            return;
        }

        // 根据任务实例数量判断处理方式
        if (isSingleTaskInstance(tenantId, context)) {
            handleSingleTaskInstanceUnclaim(buttonList, context);
        } else {
            handleMultipleTaskInstanceUnclaim(buttonList, context, tenantId, taskId);
        }
    }

    /**
     * 判断是否为单任务实例
     */
    private boolean isSingleTaskInstance(String tenantId, TodoTaskContext context) {
        int count =
            historictaskApi.getByProcessInstanceId(tenantId, context.task.getProcessInstanceId(), "").getData().size();
        return count == 1;
    }

    /**
     * 处理单任务实例的退签逻辑（父子流程调用情况）
     */
    private void handleSingleTaskInstanceUnclaim(List<ItemButtonModel> buttonList, TodoTaskContext context) {
        String superProcessInstanceId =
            procInstanceRelationshipService.getParentProcInstanceId(context.task.getProcessInstanceId());

        // 是父子流程，显示退签按钮
        if (StringUtils.isNotBlank(superProcessInstanceId)) {
            buttonList.add(ItemButton.tuiQian);
        }
    }

    /**
     * 处理多任务实例的退签逻辑
     */
    private void handleMultipleTaskInstanceUnclaim(List<ItemButtonModel> buttonList, TodoTaskContext context,
        String tenantId, String taskId) {
        List<IdentityLinkModel> identityLinkList = identityApi.getIdentityLinksForTask(tenantId, taskId).getData();

        // 只有当候选人员数量大于2时才显示退签按钮
        if (identityLinkList.size() > 2) {
            addUnclaimButtonIfCandidate(buttonList, identityLinkList, context.orgUnitId);
        }
    }

    /**
     * 如果当前用户是候选人，则添加退签按钮
     */
    private void addUnclaimButtonIfCandidate(List<ItemButtonModel> buttonList, List<IdentityLinkModel> identityLinkList,
        String orgUnitId) {
        for (IdentityLinkModel identityLink : identityLinkList) {
            if (identityLink.getUserId().contains(orgUnitId) && "candidate".equals(identityLink.getType())) {
                buttonList.add(ItemButton.tuiQian);
                break;
            }
        }
    }

    /**
     * 处理退回按钮
     */
    private void handleReturnButton(List<ItemButtonModel> buttonList, TodoTaskContext context, String tenantId,
        String taskId) {
        if (context.isAssignee && !context.customItem && !containsTuiQian(buttonList)) {
            Boolean isSub4Current =
                processDefinitionApi.isSubProcessChildNode(tenantId, context.processDefinitionId, context.taskDefKey)
                    .getData();

            if (Boolean.TRUE.equals(isSub4Current)) {
                handleSubProcessReturn(buttonList, context, tenantId);
            } else {
                handleNormalProcessReturn(buttonList, context, tenantId, taskId);
            }
        }
    }

    /**
     * 检查是否包含退签按钮
     */
    private boolean containsTuiQian(List<ItemButtonModel> buttonList) {
        return buttonList.stream().anyMatch(button -> button.getKey().equals(ItemButton.tuiQian.getKey()));
    }

    /**
     * 处理子流程退回
     */
    private void handleSubProcessReturn(List<ItemButtonModel> buttonList, TodoTaskContext context, String tenantId) {
        OrgUnit currentBureau = orgUnitApi.getBureau(tenantId, context.orgUnitId).getData();
        OrgUnit sendBureau = orgUnitApi.getBureau(tenantId, context.taskSenderId).getData();

        if (currentBureau.getId().equals(sendBureau.getId())) {
            buttonList.add(ItemButton.tuiHui);
        }
    }

    /**
     * 处理普通流程退回
     */
    private void handleNormalProcessReturn(List<ItemButtonModel> buttonList, TodoTaskContext context, String tenantId,
        String taskId) {
        List<HistoricTaskInstanceModel> hisTaskList = historictaskApi.getThePreviousTasks(tenantId, taskId).getData();

        if (!hisTaskList.isEmpty()) {
            Boolean isSubProcess4Send = processDefinitionApi
                .isSubProcessChildNode(tenantId, context.processDefinitionId, hisTaskList.get(0).getTaskDefinitionKey())
                .getData();

            if (Boolean.FALSE.equals(isSubProcess4Send)) {
                buttonList.add(ItemButton.tuiHui);
            }
        }
    }

    /**
     * 处理办结按钮
     */
    private void handleEndButton(List<ItemButtonModel> buttonList, TodoTaskContext context) {
        if (context.isAssignee && null != context.endNode) {
            if (StringUtils.isNotBlank(context.endNode.getTaskDefName())) {
                ItemButton.banJie.setName(context.endNode.getTaskDefName());
            }
            boolean showEndButton = determineEndButtonVisibility(context);
            if (showEndButton) {
                buttonList.add(ItemButton.banJie);
            }
            // 定制流程处理
            if (context.customItem) {
                handleCustomItemEndLogic(buttonList, context);
            }
        }
    }

    /**
     * 确定办结按钮是否可见
     */
    private boolean determineEndButtonVisibility(TodoTaskContext context) {
        if (context.isParallel) {
            if (isSignTask(context)) {
                return true;
            } else {
                return context.isParallelSponsor || context.isLastParallel;
            }
        } else if (context.isSequential) {
            return context.isLastSequential;
        } else {
            return true;
        }
    }

    /**
     * 处理定制流程的办结逻辑
     */
    private void handleCustomItemEndLogic(List<ItemButtonModel> buttonList, TodoTaskContext context) {
        CustomProcessInfo info = customProcessInfoService
            .getCurrentTaskNextNode((String)context.vars.get(SysVariables.PROCESS_SERIAL_NUMBER));

        if (info.getTaskType().equals(SysVariables.END_EVENT)) {
            if (buttonList.stream().anyMatch(button -> button.getKey().equals(ItemButton.banJie.getKey()))) {
                buttonList.removeIf(button -> button.getKey().equals(ItemButton.banLiWanCheng.getKey()));
            }
        }
    }

    /**
     * 处理回收站按钮
     */
    private void handleRecycleBinButton(List<ItemButtonModel> buttonList, TodoTaskContext context, String tenantId) {
        if (context.isAssignee) {
            // 目前注释掉的逻辑，可根据需要启用
            if (nodeList.isEmpty()) {
                String startNode = processDefinitionApi
                    .getStartNodeKeyByProcessDefinitionId(tenantId, context.task.getProcessDefinitionId())
                    .getData();
                nodeList =
                    processDefinitionApi.getTargetNodes(tenantId, context.task.getProcessDefinitionId(), startNode)
                        .getData();
            }
        }
    }

    /**
     * 待办任务上下文内部类
     */
    private static class TodoTaskContext {
        // 基础信息
        String itemId;
        String taskId;
        String tenantId;
        String orgUnitId;
        TaskModel task;

        // 业务配置
        boolean customItem;
        boolean showSubmitButton;

        // 流程变量
        Map<String, Object> vars;
        List<String> varsUsers;
        String varsSponsorGuid;
        String taskSenderId;
        String multiInstance;

        // 多实例状态
        boolean isSequential = false;
        boolean isLastSequential = false;
        boolean isParallel = false;
        boolean isParallelSponsor = false;
        boolean isLastParallel = false;

        // 任务状态
        String assignee;
        boolean isAssignee;
        TargetModel endNode;
        int outPutNodeCount;
        String processDefinitionId;
        String taskDefKey;
    }

    private static class TaskContext {
        TaskModel task;
        Map<String, Object> vars = new HashMap<>(16);
        String taskSenderId = "";
        List<String> varsUsers = new ArrayList<>();
        String multiInstance = "";
        String varsSponsorGuid = "";
        boolean customItem = false;
        boolean showSubmitButton;

        // 串行状态相关
        boolean isSequential = false;
        boolean isLastSequential = false;

        // 并行状态相关
        boolean isParallel = false;
        boolean isParallelSponsor = false;
        boolean isLastParallel = false;
    }
}