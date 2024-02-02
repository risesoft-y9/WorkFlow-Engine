package net.risesoft.service;

/**
 * 按钮操作实现
 * 
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
public interface ButtonOperationService {
    /**
     * Description: 办结
     * 
     * @param taskId
     * @param taskDefName
     * @param desc
     * @param infoOvert
     * @throws Exception
     */
    void complete(String taskId, String taskDefName, String desc, String infoOvert) throws Exception;

    /**
     * Description: 批量恢复待办
     * 
     * @param processInstanceIds
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
