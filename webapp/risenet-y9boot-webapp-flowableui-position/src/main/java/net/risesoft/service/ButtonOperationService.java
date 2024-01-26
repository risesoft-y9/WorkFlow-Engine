package net.risesoft.service;

/**
 * 按钮操作实现
 *
 * @author hewj2010
 */
public interface ButtonOperationService {
    /**
     * 办结
     *
     * @param taskId
     * @param taskDefName
     * @param desc
     * @throws Exception
     */
    void complete(String taskId, String taskDefName, String desc, String infoOvert) throws Exception;

    /**
     * 批量恢复待办
     *
     * @param processInstanceId
     * @param desc
     * @throws Exception
     */
    void multipleResumeToDo(String processInstanceIds, String desc) throws Exception;

    /**
     * 恢复待办
     *
     * @param processInstanceId
     * @param desc
     * @throws Exception
     */
    void resumeToDo(String processInstanceId, String desc) throws Exception;
}
