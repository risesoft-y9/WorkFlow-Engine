package net.risesoft.service;

import java.util.Map;

import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@EnableAsync
@Service(value = "task4ListenerService")
public class Task4ListenerService {

    @Autowired
    private TodoTaskService todoTaskService;

    @Autowired
    private ProcessInstanceDetailsService processInstanceDetailsService;

    @Autowired
    private PushNormalToAndroidService pushNormalToAndroidService;

    @Autowired
    private WeiXinRemindService weiXinRemindService;

    @Autowired
    private SmsRemindService smsRemindService;

    @Autowired
    private SetDeptIdUtilService setDeptIdUtilService;

    @Autowired
    private Process4MsgRemindService process4MsgRemindService;

    /**
     * 异步处理,自定义变量科室id保存
     *
     * @param task
     */
    @Async
    public void task4AssignmentListener(final DelegateTask task, final Map<String, Object> variables) {

        setDeptIdUtilService.setDeptId(task, variables);

        /**
         * 消息提醒：节点到达
         */
        process4MsgRemindService.taskAssignment(task, variables);
    }

    /**
     * 异步处理,统一待办，微信提醒，消息推送提醒，短信提醒，协作状态
     *
     * @param task
     */
    @Async
    public void task4CreateListener(final DelegateTask task, final Map<String, Object> variables,
        final Map<String, Object> localvariables) {

        /**
         * 统一待办
         */
        boolean b = !"xinjian".equals(task.getTaskDefinitionKey()) && !"faqiren".equals(task.getTaskDefinitionKey())
            && !"qicao".equals(task.getTaskDefinitionKey()) && !"fenpei".equals(task.getTaskDefinitionKey());
        if (b) {
            // 新建这一步不使用异步方式保存
            todoTaskService.saveTodoTask(task, variables);
        }

        /**
         * 微信提醒
         */
        weiXinRemindService.weiXinRemind(task, variables, localvariables);

        /**
         * 消息推送提醒
         */
        pushNormalToAndroidService.pushNormalToAndroid(task, variables);

        /**
         * 地灾协会，短信提醒
         */
        smsRemindService.dzxhSmsRemind(task, variables, localvariables);

        /**
         * 协作状态
         */
        processInstanceDetailsService.saveProcessInstanceDetails(task, variables);

        /**
         * 短信提醒
         */
        smsRemindService.smsRemind(task, variables, localvariables);
    }

    /**
     * 异步处理,删除统一待办，更新协作状态,消息提醒
     *
     * @param task
     */
    @Async
    public void task4DeleteListener(final DelegateTask task, final Map<String, Object> variables) {

        /**
         * 删除统一待办
         */
        todoTaskService.deleteTodo(task, variables);

        /**
         * 消息提醒：任务完成，节点完成
         */
        process4MsgRemindService.taskComplete(task, variables);

        /**
         * 更新协作状态
         */
        processInstanceDetailsService.updateProcessInstanceDetails(task, variables);
    }
}
