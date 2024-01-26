package net.risesoft.command;

import java.util.Date;

import org.flowable.bpmn.model.Activity;
import org.flowable.bpmn.model.Process;
import org.flowable.common.engine.impl.interceptor.Command;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.impl.bpmn.behavior.MultiInstanceActivityBehavior;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.persistence.entity.ExecutionEntityManager;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.engine.impl.util.ProcessDefinitionUtil;
import org.flowable.identitylink.service.IdentityLinkService;
import org.flowable.task.service.TaskService;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.flowable.variable.service.VariableService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
public class DeleteTaskCommand implements Command<Void> {
    /**
     * 当前任务id
     */
    protected String taskId;

    /**
     * 原因
     */
    protected String reason;

    public DeleteTaskCommand(String taskId, String reason) {
        this.taskId = taskId;
        this.reason = reason;
    }

    @Override
    public Void execute(CommandContext commandContext) {
        ExecutionEntityManager executionEntityManager = CommandContextUtil.getExecutionEntityManager();
        TaskService taskService = CommandContextUtil.getTaskService();
        IdentityLinkService identityLinkService = CommandContextUtil.getIdentityLinkService();
        VariableService variableService = CommandContextUtil.getVariableService();
        /**
         * 根据taskId获取执行实例信息
         */
        TaskEntity taskEntity = taskService.getTask(taskId);
        String executionId = taskEntity.getExecutionId();
        ExecutionEntity executionEntity = executionEntityManager.findById(executionId);
        /**
         * 获取当前流程信息
         */
        Process process = ProcessDefinitionUtil.getProcess(executionEntity.getProcessDefinitionId());
        /**
         * 获取当前节点信息
         */
        Activity flowElement = (Activity)process.getFlowElement(taskEntity.getTaskDefinitionKey());
        Object currentBehavior = flowElement.getBehavior();
        /**
         * 根据是否是多实例，执行对应的跳转操作,多实例，一定存在parentExecutionEntity
         */
        if ((currentBehavior instanceof MultiInstanceActivityBehavior)) {
            ExecutionEntity parentExecutionEntity = executionEntity.getParent();
            executionEntityManager.deleteChildExecutions(parentExecutionEntity, reason, false);
        } else {
            identityLinkService.deleteIdentityLinksByTaskId(taskId);
            variableService.deleteVariablesByExecutionId(executionId);
            taskService.deleteTask(taskEntity, true);
            CommandContextUtil.getHistoryManager().recordTaskEnd(taskEntity, executionEntity, reason, new Date());
            CommandContextUtil.getActivityInstanceEntityManager().recordActivityEnd(executionEntity, reason);
        }
        return null;
    }
}
