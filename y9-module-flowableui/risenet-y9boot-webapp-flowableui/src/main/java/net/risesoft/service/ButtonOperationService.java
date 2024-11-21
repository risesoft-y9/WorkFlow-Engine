package net.risesoft.service;

import net.risesoft.pojo.Y9Result;

/**
 * 按钮操作实现
 *
 * @author hewj2010
 */
public interface ButtonOperationService {
    /**
     * 办结
     *
     * @param taskId 任务id
     * @param taskDefName 任务定义名称
     * @param desc 描述
     */
    void complete(String taskId, String taskDefName, String desc, String infoOvert) throws Exception;

    /**
     * 批量恢复待办
     *
     * @param processInstanceIds 流程实例ids
     * @param desc 描述
     */
    void multipleResumeToDo(String processInstanceIds, String desc) throws Exception;

    /**
     * 恢复待办
     *
     * @param processInstanceId 流程实例id
     * @param desc 描述
     */
    void resumeToDo(String processInstanceId, String desc) throws Exception;

    Y9Result<String> deleteTodos(String[] taskIdAndProcessSerialNumbers);

    Y9Result<String> recoverTodos(String[] processSerialNumbers);


    Y9Result<String> removeTodos(String[] processSerialNumbers);
}
