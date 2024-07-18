package net.risesoft.service;

import java.util.Map;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
public interface CustomVariableService {
    /**
     * 删除流程变量
     *
     * @param taskId
     * @param key
     * @return
     */
    void deleteVariable(String taskId, String key);

    /**
     * 获取流程变量
     *
     * @param taskId
     * @param key
     * @return
     */
    Object getVariable(String taskId, String key);

    /**
     * 获取任务变量
     *
     * @param taskId
     * @param key
     * @return
     */
    Object getVariableLocal(String taskId, String key);

    /**
     * 获取多个流程变量
     *
     * @param taskId
     * @return
     */
    Map<String, Object> getVariables(String taskId);

    /**
     * 获取所有任务变量
     *
     * @param taskId
     * @return
     */
    Map<String, Object> getVariablesLocal(String taskId);

    /**
     * Description:
     *
     * @param taskId
     * @param key
     */
    void removeVariableLocal(String taskId, String key);

    /**
     * 设置流程变量
     *
     * @param taskId
     * @param key
     * @param val
     */
    void setVariable(String taskId, String key, Object val);

    /**
     * 设置任务变量
     *
     * @param taskId
     * @param key
     * @param val
     */
    void setVariableLocal(String taskId, String key, Object val);

    /**
     * 这是保存多个流程变量
     *
     * @param taskId
     * @param map
     */
    void setVariables(String taskId, Map<String, Object> map);

    /**
     * 设置多个任务变量
     *
     * @param taskId
     * @param map
     */
    void setVariablesLocal(String taskId, Map<String, Object> map);
}
