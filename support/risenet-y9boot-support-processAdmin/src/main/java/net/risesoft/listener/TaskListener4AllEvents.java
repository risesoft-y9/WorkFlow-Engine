package net.risesoft.listener;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.FlowableListener;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;

import net.risesoft.service.Task4ActRuDetaillService;
import net.risesoft.service.Task4ListenerService;
import net.risesoft.service.TaskEntrustService;
import net.risesoft.service.TodoTaskService;
import net.risesoft.y9.Y9Context;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
public class TaskListener4AllEvents extends FlowableListener implements TaskListener {

    private static final long serialVersionUID = 7977766892823478492L;

    @Override
    public void notify(DelegateTask task) {
        String eventName = task.getEventName();
        if (TaskListener.EVENTNAME_ASSIGNMENT.equals(eventName)) {

            Map<String, Object> variables = task.getVariables();

            //////////////// 异步处理,自定义变量科室id保存，消息提醒
            Task4ListenerService task4ListenerService = Y9Context.getBean(Task4ListenerService.class);
            task4ListenerService.task4AssignmentListener(task, variables);

            /**
             * 1、签收的时候保存已办详情 2、委托的时候更改assignee的时候
             */
            if (task.getCandidates().size() > 1) {
                Task4ActRuDetaillService task4ActRuDetaillService = Y9Context.getBean(Task4ActRuDetaillService.class);
                if (StringUtils.isNotEmpty(task.getAssignee())) {
                    /**
                     * 签收的情况
                     */
                    task4ActRuDetaillService.saveOrUpdate4DoSign(task);
                } else {
                    /**
                     * 撤销签收的情况
                     */
                    task4ActRuDetaillService.saveOrUpdate4Sign(task, 0);
                }
            }

        } else if (TaskListener.EVENTNAME_CREATE.equals(eventName)) {
            Map<String, Object> variables = task.getVariables();
            Map<String, Object> localvariables = task.getVariablesLocal();
            /**
             * 出差委托
             */
            TaskEntrustService taskEntrustService = Y9Context.getBean(TaskEntrustService.class);
            task = taskEntrustService.entrust(task, variables);

            /**
             * 保存已办件详情
             */
            Task4ActRuDetaillService task4ActRuDetaillService = Y9Context.getBean(Task4ActRuDetaillService.class);
            if (null != task.getAssignee()) {
                task4ActRuDetaillService.saveOrUpdate(task, 0);
            } else {
                task4ActRuDetaillService.saveOrUpdate4Sign(task, 0);
            }

            /**
             * 统一待办-新建这一步不使用异步方式保存
             */
            boolean b = "xinjian".equals(task.getTaskDefinitionKey()) || "faqiren".equals(task.getTaskDefinitionKey())
                || "qicao".equals(task.getTaskDefinitionKey()) || "fenpei".equals(task.getTaskDefinitionKey());
            if (b) {
                TodoTaskService todoTaskService = Y9Context.getBean(TodoTaskService.class);
                todoTaskService.saveTodoTask(task, variables);
            }

            ///////////////// 异步处理,统一待办,微信提醒,消息推送提醒,短信提醒,协作状态
            Task4ListenerService task4ListenerService = Y9Context.getBean(Task4ListenerService.class);
            task4ListenerService.task4CreateListener(task, variables, localvariables);

        } else if (TaskListener.EVENTNAME_COMPLETE.equals(eventName)) {

        } else if (TaskListener.EVENTNAME_DELETE.equals(eventName)) {
            Map<String, Object> variables = task.getVariables();

            ///////////////// 异步处理,删除统一待办,更新协作状态,消息提醒
            Task4ListenerService task4ListenerService = Y9Context.getBean(Task4ListenerService.class);
            task4ListenerService.task4DeleteListener(task, variables);

            String assigneeHti = task.getAssignee();
            if (StringUtils.isNotBlank(assigneeHti)) {
                task.removeVariable(assigneeHti);
            }

            /**
             * 任务删除的时候，改变已办详情状态为已办
             */
            if (null != task.getAssignee()) {
                Task4ActRuDetaillService task4ActRuDetaillService = Y9Context.getBean(Task4ActRuDetaillService.class);
                task4ActRuDetaillService.saveOrUpdate(task, 1);
            } else {
                Task4ActRuDetaillService task4ActRuDetaillService = Y9Context.getBean(Task4ActRuDetaillService.class);
                task4ActRuDetaillService.saveOrUpdate4Reposition(task);
            }
        }
    }
}
