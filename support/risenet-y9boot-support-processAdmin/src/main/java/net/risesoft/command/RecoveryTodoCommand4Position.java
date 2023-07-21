package net.risesoft.command;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.flowable.common.engine.impl.interceptor.Command;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.engine.impl.persistence.entity.HistoricProcessInstanceEntity;
import org.flowable.engine.impl.persistence.entity.HistoricProcessInstanceEntityManager;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.identitylink.service.HistoricIdentityLinkService;
import org.flowable.identitylink.service.IdentityLinkService;
import org.flowable.identitylink.service.impl.persistence.entity.HistoricIdentityLinkEntity;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.service.TaskService;
import org.flowable.task.service.impl.persistence.entity.TaskEntityImpl;

import net.risesoft.model.Position;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
public class RecoveryTodoCommand4Position implements Command<Void> {
    /**
     * 要恢复办结的历史任务
     */
    protected HistoricTaskInstance hisTask;

    /**
     * 流程变量
     */
    protected Map<String, Object> pVarMap;

    /**
     * 任务变量
     */
    protected Map<String, Object> tVarMap;

    public RecoveryTodoCommand4Position(HistoricTaskInstance hisTask, Map<String, Object> pVarMap, Map<String, Object> tVarMap) {
        this.hisTask = hisTask;
        this.pVarMap = pVarMap;
        this.tVarMap = tVarMap;
    }

    @Override
    public Void execute(CommandContext commandContext) {
        TaskService taskService = CommandContextUtil.getTaskService();
        HistoricIdentityLinkService historicIdentityLinkService = CommandContextUtil.getHistoricIdentityLinkService();
        IdentityLinkService identityLinkService = CommandContextUtil.getIdentityLinkService();
        HistoricProcessInstanceEntityManager historicProcessInstanceEntityManager = CommandContextUtil.getHistoricProcessInstanceEntityManager();

        Position position = Y9LoginUserHolder.getPosition();
        String userId = position.getId();

        String processInstanceId = hisTask.getProcessInstanceId(), taskId = hisTask.getId();
        String assignee = hisTask.getAssignee();
        /**
         * 1-copytask
         */
        TaskEntityImpl taskEntity = new TaskEntityImpl();
        taskEntity.setId(taskId);
        taskEntity.setRevision(1);
        taskEntity.setExecutionId(hisTask.getExecutionId());
        taskEntity.setProcessInstanceId(hisTask.getProcessInstanceId());
        taskEntity.setProcessDefinitionId(hisTask.getProcessDefinitionId());
        taskEntity.setName(hisTask.getName());
        taskEntity.setTaskDefinitionKey(hisTask.getTaskDefinitionKey());
        // 谁恢复待办，件就回到谁手上
        taskEntity.setAssignee(userId);
        taskEntity.setPriority(hisTask.getPriority());
        taskEntity.setCreateTime(new Date());
        taskEntity.setSuspensionState(1);
        taskEntity.setCountEnabled(true);
        taskEntity.setCategory(hisTask.getCategory());
        taskEntity.setVariableCount(0);
        taskEntity.setIdentityLinkCount(hisTask.getIdentityLinks().size());
        taskEntity.setSubTaskCount(0);
        // 当不是办结人恢复待办时，用来存放任务的初始受让人，历程显示Owner和Assignee同时有值时，显示Owner
        if (!assignee.equals(userId) || StringUtils.isNotBlank(hisTask.getOwner())) {
            if (StringUtils.isNotBlank(hisTask.getOwner())) {
                // 当历史任务已存在Owner时，使用历史的Owner
                taskEntity.setOwner(hisTask.getOwner());
            } else {
                taskEntity.setOwner(assignee);
            }
        }
        taskService.insertTask(taskEntity, true);

        taskEntity.setVariables(pVarMap);
        taskEntity.setVariablesLocal(tVarMap);
        taskService.updateTask(taskEntity, false);

        /**
         * 触发任务产生事件
         */
        ProcessEngineConfigurationImpl processEngineConfiguration = org.flowable.engine.impl.util.CommandContextUtil.getProcessEngineConfiguration(commandContext);
        processEngineConfiguration.getListenerNotificationHelper().executeTaskListeners(taskEntity, TaskListener.EVENTNAME_CREATE);

        /**
         * 2-设置历史任务办结时间为null
         */
        /**
         * 3-历史act_hi_procinst结束时间改为null
         */
        HistoricProcessInstanceEntity historicProcessInstanceEntity = historicProcessInstanceEntityManager.findById(processInstanceId);
        if (null != historicProcessInstanceEntity) {
            historicProcessInstanceEntity.setEndTime(null);
            historicProcessInstanceEntity.setDurationInMillis(null);
            historicProcessInstanceEntity.setEndActivityId(null);
            historicProcessInstanceEntity.setDeleteReason(null);
            historicProcessInstanceEntityManager.update(historicProcessInstanceEntity);
        }
        /**
         * 4-copy participant(流程实例) candidate(单实例任务节点生成的任务会有)
         */
        List<HistoricIdentityLinkEntity> hilEntityList4P = historicIdentityLinkService.findHistoricIdentityLinksByProcessInstanceId(processInstanceId);
        for (HistoricIdentityLinkEntity hilEntity : hilEntityList4P) {
            identityLinkService.createProcessInstanceIdentityLink(processInstanceId, userId, null, hilEntity.getType());
        }

        List<HistoricIdentityLinkEntity> hilEntityList4T = historicIdentityLinkService.findHistoricIdentityLinksByTaskId(taskId);
        for (HistoricIdentityLinkEntity hilEntity : hilEntityList4T) {
            identityLinkService.createTaskIdentityLink(taskId, userId, null, hilEntity.getType());
        }
        return null;
    }
}
