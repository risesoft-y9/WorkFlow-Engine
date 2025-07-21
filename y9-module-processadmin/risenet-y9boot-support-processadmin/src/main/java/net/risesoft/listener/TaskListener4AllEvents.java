package net.risesoft.listener;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.FlowableListener;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.service.InterfaceUtilService;
import net.risesoft.service.Task4ActRuDetaillService;
import net.risesoft.service.Task4ListenerService;
import net.risesoft.y9.Y9Context;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
public class TaskListener4AllEvents extends FlowableListener implements TaskListener {

    private static final long serialVersionUID = 7977766892823478492L;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void notify(DelegateTask task) {
        String eventName = task.getEventName();
        if (TaskListener.EVENTNAME_ASSIGNMENT.equals(eventName)) {
            Map<String, Object> variables = task.getVariables();
            // 异步处理,自定义变量科室id保存
            Task4ListenerService task4ListenerService = Y9Context.getBean(Task4ListenerService.class);
            task4ListenerService.task4AssignmentListener(task, variables);
            /*
             * 1、签收的时候保存待办详情 2、委托的时候更改assignee的时候
             */
            if (task.getCandidates().size() > 1) {
                Task4ActRuDetaillService task4ActRuDetaillService = Y9Context.getBean(Task4ActRuDetaillService.class);
                if (StringUtils.isNotEmpty(task.getAssignee())) {
                    /*
                     * 签收的情况
                     */
                    task4ActRuDetaillService.claim(task);
                } else {
                    /*
                     * 撤销签收的情况
                     */
                    task4ActRuDetaillService.unClaim(task);
                }
            }

        } else if (TaskListener.EVENTNAME_CREATE.equals(eventName)) {
            Map<String, Object> variables = task.getVariables();
            Map<String, Object> localVariables = task.getVariablesLocal();

            // 接口调用
            InterfaceUtilService interfaceUtilService = Y9Context.getBean(InterfaceUtilService.class);
            try {
                interfaceUtilService.interfaceCallByTask(task, variables, "创建");
            } catch (Exception e) {
                throw new RuntimeException("调用接口失败 TaskListener4AllEvents_EVENTNAME_CREATE");
            }
            /*
             * 保存待办详情
             */
            Task4ActRuDetaillService task4ActRuDetaillService = Y9Context.getBean(Task4ActRuDetaillService.class);
            if (null != task.getAssignee()) {
                task4ActRuDetaillService.createTodo(task);
            } else {
                task4ActRuDetaillService.createTodo4Claim(task);
            }
        } else if (TaskListener.EVENTNAME_DELETE.equals(eventName)) {
            Map<String, Object> variables = task.getVariables();
            // 接口调用
            InterfaceUtilService interfaceUtilService = Y9Context.getBean(InterfaceUtilService.class);
            try {
                interfaceUtilService.interfaceCallByTask(task, variables, "完成");
            } catch (Exception e) {
                throw new RuntimeException("调用接口失败 TaskListener4AllEvents_EVENTNAME_DELETE");
            }

            // 异步处理，记录岗位/人员名称
            Task4ListenerService task4ListenerService = Y9Context.getBean(Task4ListenerService.class);
            task4ListenerService.task4DeleteListener(task, variables);

            String assigneeHti = task.getAssignee();
            if (StringUtils.isNotBlank(assigneeHti)) {
                task.removeVariable(assigneeHti);
            }
            /*
             * 任务删除的时候，待办-->在办
             */
            Task4ActRuDetaillService task4ActRuDetaillService = Y9Context.getBean(Task4ActRuDetaillService.class);
            if (null != task.getAssignee()) {
                task4ActRuDetaillService.todo2doing(task);
            } else {
                task4ActRuDetaillService.todo2doing4Jump(task);
            }
        }
    }
}
