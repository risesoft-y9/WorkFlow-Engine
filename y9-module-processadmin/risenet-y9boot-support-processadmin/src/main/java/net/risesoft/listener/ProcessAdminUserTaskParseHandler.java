package net.risesoft.listener;

import java.util.ArrayList;
import java.util.List;

import org.flowable.bpmn.model.FlowableListener;
import org.flowable.bpmn.model.ImplementationType;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.engine.impl.bpmn.parser.BpmnParse;
import org.flowable.engine.impl.bpmn.parser.handler.UserTaskParseHandler;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
public class ProcessAdminUserTaskParseHandler extends UserTaskParseHandler {

    @Override
    protected void executeParse(BpmnParse bpmnParse, UserTask userTask) {
        super.executeParse(bpmnParse, userTask);

        List<FlowableListener> taskListeners = new ArrayList<>();

        /*
         * 任务监听器，监听UserTask所有事件
         */
        TaskListener4AllEvents taskListener4AllEvents = new TaskListener4AllEvents();
        taskListener4AllEvents.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_CLASS);
        taskListener4AllEvents.setImplementation("net.risesoft.listener.TaskListener4AllEvents");
        taskListener4AllEvents.setEvent(TaskListener.EVENTNAME_ALL_EVENTS);
        taskListeners.add(taskListener4AllEvents);
        userTask.setTaskListeners(taskListeners);
    }
}
