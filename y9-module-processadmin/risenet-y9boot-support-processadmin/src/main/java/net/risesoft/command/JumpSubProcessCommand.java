package net.risesoft.command;

import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.Process;
import org.flowable.common.engine.impl.interceptor.Command;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.FlowableEngineAgenda;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.persistence.entity.ExecutionEntityManager;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.engine.impl.util.ProcessDefinitionUtil;
import org.flowable.task.service.TaskService;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;

import java.util.List;
import java.util.Map;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
public class JumpSubProcessCommand implements Command<Void> {

    protected String taskId;

    protected String targetNodeId;

    protected Map<String, Object> vars;

    protected String positionId;

    protected List<String> users;

    public JumpSubProcessCommand(String taskId, String positionId, Map<String, Object> vars, String targetNodeId,
        List<String> users) {
        this.taskId = taskId;
        this.targetNodeId = targetNodeId;
        this.vars = vars;
        this.positionId = positionId;
        this.users = users;
    }

    @Override
    public Void execute(CommandContext commandContext) {
        ExecutionEntityManager executionEntityManager =
            org.flowable.engine.impl.util.CommandContextUtil.getExecutionEntityManager();
        TaskService taskService = CommandContextUtil.getTaskService();
        TaskEntity parentTask = taskService.getTask(taskId);
        ExecutionEntity parentExecutionEntity = executionEntityManager.findById(parentTask.getExecutionId());
        ExecutionEntity childExecution = executionEntityManager.createChildExecution(parentExecutionEntity);
        if (users.size() == 1) {
            vars.put("user", users.get(0));
        }
        vars.put("users", users);
        childExecution.setVariables(vars);
        Process process = ProcessDefinitionUtil.getProcess(parentExecutionEntity.getProcessDefinitionId());
        FlowElement targetFlowElement = process.getFlowElement(targetNodeId);
        FlowableEngineAgenda flowableEngineAgenda = org.flowable.engine.impl.util.CommandContextUtil.getAgenda();
        childExecution.setCurrentFlowElement(targetFlowElement);
        flowableEngineAgenda.planContinueProcessInCompensation(childExecution);
        // }

        return null;
    }
}
