package net.risesoft.service;

import java.util.Map;

import org.flowable.engine.delegate.event.impl.FlowableSequenceFlowTakenEventImpl;
import org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.flowable.task.service.delegate.DelegateTask;

import net.risesoft.model.itemadmin.InterfaceModel;

/**
 * @author zhangchongjie
 * @date 2024/05/29
 */
public interface InterfaceUtilService {

    /**
     * 流程启动，办结接口调用
     *
     * @param executionEntity
     * @param variables
     * @param condition
     * @throws Exception
     */
    void interfaceCallByProcess(ExecutionEntityImpl executionEntity, Map<String, Object> variables, String condition)
        throws Exception;

    /**
     * 路由经过接口调用
     *
     * @param flow
     * @param condition
     * @throws Exception
     */
    void interfaceCallBySequenceFlow(FlowableSequenceFlowTakenEventImpl flow, String condition) throws Exception;

    /**
     * 任务创建，完成接口调用
     *
     * @param task
     * @param variables
     * @param condition
     * @throws Exception
     */
    void interfaceCallByTask(DelegateTask task, Map<String, Object> variables, String condition) throws Exception;

    /**
     * 同步调用接口
     *
     * @param processSerialNumber 流程编号
     * @param itemId 事项id
     * @param info 接口信息
     * @param processInstanceId 流程实例id
     * @param processDefinitionId 流程定义id
     * @param taskId 任务id
     * @param taskKey 任务key
     * @param loopCounter 循环次数
     * @throws Exception
     */
    void syncInterface(final String processSerialNumber, final String itemId, final InterfaceModel info,
        final String processInstanceId, final String processDefinitionId, final String taskId, final String taskKey,
        final Integer loopCounter) throws Exception;
}
