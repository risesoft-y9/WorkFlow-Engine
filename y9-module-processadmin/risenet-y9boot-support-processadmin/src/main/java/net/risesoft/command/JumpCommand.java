package net.risesoft.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.flowable.bpmn.model.Activity;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.FlowElementsContainer;
import org.flowable.bpmn.model.SubProcess;
import org.flowable.bpmn.model.UserTask;
import org.flowable.common.engine.impl.interceptor.Command;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.FlowableEngineAgenda;
import org.flowable.engine.impl.bpmn.behavior.MultiInstanceActivityBehavior;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.persistence.entity.ExecutionEntityManager;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.engine.impl.util.ProcessDefinitionUtil;
import org.flowable.identitylink.service.IdentityLinkService;
import org.flowable.task.service.TaskService;
import org.flowable.task.service.delegate.BaseTaskListener;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.flowable.variable.service.VariableService;

import lombok.Getter;

import net.risesoft.consts.processadmin.SysVariables;
import net.risesoft.y9.Y9FlowableHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
public class JumpCommand implements Command<Void> {
    /**
     * 当前任务id
     */
    protected String taskId;

    /**
     * 目标任务节点id
     */
    protected String targetNodeId;

    /**
     * 办理人:如果是普通任务，users只能是一个岗位，否则会出现多个岗位签收的情况
     */
    protected List<String> users;

    /**
     * 原因
     */
    protected String reason;

    public JumpCommand(String taskId, String targetNodeId, List<String> users, String reason) {
        this.taskId = taskId;
        this.targetNodeId = targetNodeId;
        this.users = users;
        this.reason = reason;
    }

    @Override
    public Void execute(CommandContext commandContext) {
        // 获取必要的服务组件
        TaskEntity taskEntity = getTaskEntity();
        ExecutionEntity executionEntity = getExecutionEntity(taskEntity);
        org.flowable.bpmn.model.Process process = getProcessDefinition(executionEntity);
        // 处理当前节点信息
        ActivityInfo currentActivityInfo = processCurrentActivity(process, taskEntity);
        Activity currentActivity = currentActivityInfo.getActivity();
        boolean isSubProcessNode = currentActivityInfo.isSubProcessNode();
        // 执行当前任务的清理操作
        cleanupCurrentTask(taskEntity, executionEntity, currentActivity);
        // 触发任务删除事件
        triggerTaskDeleteEvent(commandContext, taskEntity);
        // 处理目标节点信息
        FlowElement targetFlowElement = processTargetNode(process, isSubProcessNode, currentActivityInfo.getSubList());
        // 设置新任务的变量
        Map<String, Object> taskVariables = createTaskVariables();
        // 执行跳转操作
        executeJump(executionEntity, targetFlowElement, taskVariables, currentActivity);
        return null;
    }

    /**
     * 获取任务实体
     */
    private TaskEntity getTaskEntity() {
        TaskService taskService = CommandContextUtil.getTaskService();
        return taskService.getTask(taskId);
    }

    /**
     * 获取执行实体
     */
    private ExecutionEntity getExecutionEntity(TaskEntity taskEntity) {
        ExecutionEntityManager executionEntityManager = CommandContextUtil.getExecutionEntityManager();
        String executionId = taskEntity.getExecutionId();
        return executionEntityManager.findById(executionId);
    }

    /**
     * 获取流程定义
     */
    private org.flowable.bpmn.model.Process getProcessDefinition(ExecutionEntity executionEntity) {
        return ProcessDefinitionUtil.getProcess(executionEntity.getProcessDefinitionId());
    }

    /**
     * 处理当前活动节点信息
     */
    private ActivityInfo processCurrentActivity(org.flowable.bpmn.model.Process process, TaskEntity taskEntity) {
        Activity currentActivity = (Activity)process.getFlowElement(taskEntity.getTaskDefinitionKey());
        List<FlowElement> subList = getSubProcessUserTasks(process);

        boolean isSubProcessNode = false;

        // 处理子流程内部节点
        if (currentActivity == null) {
            Optional<FlowElement> feOptional = subList.stream()
                .filter(flowElement -> taskEntity.getTaskDefinitionKey().equals(flowElement.getId()))
                .findFirst();
            if (feOptional.isPresent()) {
                currentActivity = (Activity)feOptional.get();
                isSubProcessNode = true;
            }
        } else {
            isSubProcessNode =
                subList.stream().anyMatch(flowElement -> taskEntity.getTaskDefinitionKey().equals(flowElement.getId()));
        }

        assert currentActivity != null;
        return new ActivityInfo(currentActivity, isSubProcessNode, subList);
    }

    /**
     * 获取子流程中的用户任务列表
     */
    private List<FlowElement> getSubProcessUserTasks(org.flowable.bpmn.model.Process process) {
        Collection<FlowElement> list4Cache = process.getFlowElements();
        List<FlowElement> list = new ArrayList<>(list4Cache);
        List<FlowElement> subList = new ArrayList<>();

        list.stream().filter(flowElement -> flowElement instanceof SubProcess).forEach(flowElement -> {
            SubProcess subProcess = (SubProcess)flowElement;
            subList.addAll(subProcess.getFlowElements()
                .stream()
                .filter(subFlowElement -> subFlowElement instanceof UserTask)
                .collect(Collectors.toList()));
        });

        return subList;
    }

    /**
     * 清理当前任务
     */
    private void cleanupCurrentTask(TaskEntity taskEntity, ExecutionEntity executionEntity, Activity currentActivity) {
        Object currentBehavior = currentActivity.getBehavior();

        if (currentBehavior instanceof MultiInstanceActivityBehavior) {
            // 处理多实例任务
            ExecutionEntityManager executionEntityManager = CommandContextUtil.getExecutionEntityManager();
            ExecutionEntity parentExecutionEntity = executionEntity.getParent();
            executionEntityManager.deleteChildExecutions(parentExecutionEntity, reason, false);
        } else {
            // 处理普通任务
            cleanupRegularTask(taskEntity, executionEntity);
        }
    }

    /**
     * 清理普通任务
     */
    private void cleanupRegularTask(TaskEntity taskEntity, ExecutionEntity executionEntity) {
        IdentityLinkService identityLinkService = CommandContextUtil.getIdentityLinkService();
        VariableService variableService = CommandContextUtil.getVariableService();
        TaskService taskService = CommandContextUtil.getTaskService();

        String executionId = taskEntity.getExecutionId();

        identityLinkService.deleteIdentityLinksByTaskId(taskId);
        variableService.deleteVariablesByExecutionId(executionId);
        taskService.deleteTask(taskEntity, true);

        org.flowable.engine.impl.util.CommandContextUtil.getHistoryManager()
            .recordTaskEnd(taskEntity, executionEntity, reason, new Date());
        org.flowable.engine.impl.util.CommandContextUtil.getActivityInstanceEntityManager()
            .recordActivityEnd(executionEntity, reason);
    }

    /**
     * 触发任务删除事件
     */
    private void triggerTaskDeleteEvent(CommandContext commandContext, TaskEntity taskEntity) {
        ProcessEngineConfigurationImpl processEngineConfiguration =
            org.flowable.engine.impl.util.CommandContextUtil.getProcessEngineConfiguration(commandContext);
        processEngineConfiguration.getListenerNotificationHelper()
            .executeTaskListeners(taskEntity, BaseTaskListener.EVENTNAME_DELETE);
    }

    /**
     * 处理目标节点
     */
    private FlowElement processTargetNode(org.flowable.bpmn.model.Process process, boolean isSubProcessNode,
        List<FlowElement> subList) {
        FlowElement targetFlowElement = process.getFlowElement(targetNodeId);

        // 子流程内部节点处理
        if (targetFlowElement == null) {
            targetFlowElement = handleTargetInSubProcess(subList, isSubProcessNode);
        } else {
            // 父节点是SubProcess
            targetFlowElement = handleTargetInParentSubProcess(targetFlowElement, isSubProcessNode);
        }

        return targetFlowElement;
    }

    /**
     * 处理目标节点在子流程中的情况
     */
    private FlowElement handleTargetInSubProcess(List<FlowElement> subList, boolean isSubProcessNode) {
        Optional<FlowElement> targetFeOptional =
            subList.stream().filter(flowElement -> targetNodeId.equals(flowElement.getId())).findFirst();

        if (targetFeOptional.isPresent()) {
            FlowElement targetElement = targetFeOptional.get();
            if (!isSubProcessNode) {
                return (FlowElement)targetElement.getParentContainer();
            }
            return targetElement;
        }
        return null;
    }

    /**
     * 处理目标节点在父级子流程中的情况
     */
    private FlowElement handleTargetInParentSubProcess(FlowElement targetFlowElement, boolean isSubProcessNode) {
        FlowElementsContainer flowElementsContainer = targetFlowElement.getParentContainer();
        if (flowElementsContainer instanceof SubProcess && !isSubProcessNode) {
            return (FlowElement)flowElementsContainer;
        }
        return targetFlowElement;
    }

    /**
     * 创建任务变量
     */
    private Map<String, Object> createTaskVariables() {
        Map<String, Object> vars = new HashMap<>(16);
        vars.put(SysVariables.TASK_SENDER, Y9FlowableHolder.getOrgUnit().getName());
        vars.put(SysVariables.TASK_SENDER_ID, Y9FlowableHolder.getOrgUnitId());
        vars.put(SysVariables.TASK_SENDER_POSITION_ID, Y9FlowableHolder.getOrgUnitId());

        String user = null;
        if (users.size() == 1) {
            user = users.get(0);
        }

        // 重定向到子流程内的节点
        vars.put("elementUser", user);
        vars.put(SysVariables.USER, user);
        vars.put(SysVariables.USERS, users);

        return vars;
    }

    /**
     * 执行跳转操作
     */
    private void executeJump(ExecutionEntity executionEntity, FlowElement targetFlowElement,
        Map<String, Object> taskVariables, Activity currentActivity) {
        FlowableEngineAgenda flowableEngineAgenda = org.flowable.engine.impl.util.CommandContextUtil.getAgenda();
        Object currentBehavior = currentActivity.getBehavior();

        if (currentBehavior instanceof MultiInstanceActivityBehavior) {
            ExecutionEntityManager executionEntityManager = CommandContextUtil.getExecutionEntityManager();
            ExecutionEntity parentExecutionEntity = executionEntity.getParent();
            parentExecutionEntity.setCurrentFlowElement(targetFlowElement);
            parentExecutionEntity.setActive(true);
            parentExecutionEntity.setMultiInstanceRoot(false);
            parentExecutionEntity.setVariables(taskVariables);
            executionEntityManager.update(parentExecutionEntity);
            flowableEngineAgenda.planContinueProcessInCompensation(parentExecutionEntity);
        } else {
            executionEntity.setCurrentFlowElement(targetFlowElement);
            executionEntity.setVariables(taskVariables);
            flowableEngineAgenda.planContinueProcessInCompensation(executionEntity);
        }
    }

    /**
     * 活动节点信息封装类
     */
    @Getter
    private static class ActivityInfo {
        private final Activity activity;
        private final boolean subProcessNode;
        private final List<FlowElement> subList;

        public ActivityInfo(Activity activity, boolean subProcessNode, List<FlowElement> subList) {
            this.activity = activity;
            this.subProcessNode = subProcessNode;
            this.subList = subList;
        }

    }
}
