package net.risesoft.service;

import java.util.List;
import java.util.Map;

import net.risesoft.model.itemadmin.core.ProcessParamModel;

public interface MultiInstanceService {

    /**
     * 并行加签
     *
     * @param processInstanceId 流程实例id
     * @param taskId 任务id
     * @param userChoice 选择人
     * @param isSendSms 是否发送短信
     * @param isShuMing 是否署名
     * @param smsContent 短信内容
     */
    void addExecutionId(String processInstanceId, String taskId, String userChoice, String isSendSms, String isShuMing,
        String smsContent) throws Exception;

    /**
     * 并行加签
     *
     * @param processParamModel 流程对象
     * @param activityId 任务id
     * @param userChoice 选择人
     */
    void addExecutionId(ProcessParamModel processParamModel, String activityId, String userChoice);

    /**
     * 串行加签
     *
     * @param executionId 执行实例id
     * @param taskId 任务id
     * @param userChoice 选择人
     * @param selectUserId 选择人id
     * @param num 加签位置
     */
    void addExecutionId4Sequential(String executionId, String taskId, String userChoice, String selectUserId, int num)
        throws Exception;

    /**
     * 并行时获取办理人列表
     *
     * @param processInstanceId 流程实例id
     * @return List<Map < String, Object>>
     */
    List<Map<String, Object>> listAssignee4Parallel(String processInstanceId);

    /**
     * 串行时获取办理人列表
     *
     * @param taskId 任务id
     * @return List<Map < String, Object>>
     */
    List<Map<String, Object>> listAssignee4Sequential(String taskId);

    /**
     * 减签
     *
     * @param executionId 执行实例id
     * @param taskId 任务id
     * @param elementUser 人员id
     */
    void removeExecution(String executionId, String taskId, String elementUser) throws Exception;

    /**
     * 串行减签
     *
     * @param executionId 执行实例id
     * @param taskId 任务id
     * @param elementUser 人员id
     * @param num 减签位置
     */
    void removeExecution4Sequential(String executionId, String taskId, String elementUser, int num) throws Exception;
}
