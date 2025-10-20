package net.risesoft.service;

import java.util.List;
import java.util.Map;

import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
public interface CustomRuntimeService {

    /**
     *
     * 加签
     *
     * @param activityId 执行实例id
     * @param parentExecutionId 父执行实例id
     * @param map 参数
     */
    Execution addMultiInstanceExecution(String activityId, String parentExecutionId, Map<String, Object> map);

    /**
     * 减签
     *
     * @param executionId
     */
    void deleteMultiInstanceExecution(String executionId);

    /**
     * 根据执行Id获取当前活跃的节点信息
     *
     * @param executionId
     * @return
     */
    List<String> getActiveActivityIds(String executionId);

    /**
     * 根据执行实例Id获取执行的实体
     *
     * @param executionId
     * @return
     */
    Execution getExecutionById(String executionId);

    /**
     * 根据流程实例Id获取流程实例
     *
     * @param processInstanceId
     * @return
     */
    ProcessInstance getProcessInstance(String processInstanceId);

    /**
     * 根据父流程实例获取子流程实例
     *
     * @param superProcessInstanceId
     * @return
     */
    List<ProcessInstance> listBySuperProcessInstanceId(String superProcessInstanceId);

    /**
     * 根据流程定义Key获取流程实例列表
     *
     * @param processDefinitionKey
     * @return
     */
    List<ProcessInstance> listProcessInstancesByKey(String processDefinitionKey);

    /**
     * 恢复设置办结的件，其实是先激活流程，再设置流程的结束时间为null
     *
     * @param processInstanceId
     */
    void recovery4SetUpCompleted(String processInstanceId);

    /**
     * 恢复待办
     *
     * @param processInstanceId
     * @param year
     * @throws Exception
     */
    void recoveryCompleted(String processInstanceId, String year) throws Exception;

    /**
     * 设置流程实例为办结的状态，其实是先暂停，再设置流程结束时间为当前时间
     *
     * @param processInstanceId
     */
    void setUpCompleted(String processInstanceId);

    /**
     * 根据流程实例id设置流程变量
     *
     * @param processInstanceId
     * @param key
     * @param val
     * @return
     */
    void setVariable(String processInstanceId, String key, Object val);

    /**
     * 根据流程实例id设置流程变量
     *
     * @param executionId
     * @param map
     * @return
     */
    void setVariables(String executionId, Map<String, Object> map);

    /**
     * Description:
     *
     * @param processDefinitionKey
     * @param systemName
     * @param map
     * @return
     */
    ProcessInstance startProcessInstanceByKey(String processDefinitionKey, String systemName, Map<String, Object> map);

    /**
     * 挂起或者激活流程实例
     *
     * @param processInstanceId
     * @param state
     */
    void switchSuspendOrActive(String processInstanceId, String state);
}
