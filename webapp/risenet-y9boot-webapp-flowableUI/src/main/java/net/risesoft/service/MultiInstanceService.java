package net.risesoft.service;

import java.util.List;
import java.util.Map;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
public interface MultiInstanceService {

    /**
     * 并行加签
     *
     * @param processInstanceId
     * @param taskId
     * @param userChoice
     * @param isSendSms
     * @param isShuMing
     * @param smsContent
     * @throws Exception
     */
    void addExecutionId(String processInstanceId, String taskId, String userChoice, String isSendSms, String isShuMing, String smsContent) throws Exception;

    /**
     * 串行加签
     *
     * @param executionId
     * @param taskId
     * @param userChoice
     * @param selectUserId
     * @param num
     * @throws Exception
     */
    void addExecutionId4Sequential(String executionId, String taskId, String userChoice, String selectUserId, int num) throws Exception;

    /**
     * 并行时获取办理人列表
     *
     * @param processInstanceId
     * @param taskId
     * @return
     */
    List<Map<String, Object>> assigneeList4Parallel(String processInstanceId);

    /**
     * 串行时获取办理人列表
     *
     * @param processInstanceId
     * @param taskId
     * @return
     */
    List<Map<String, Object>> assigneeList4Sequential(String taskId);

    /**
     * 获取人员树信息
     *
     * @param processInstanceId
     * @return
     */
    Map<String, Object> docUserChoise(String processInstanceId);

    /**
     * 减签
     *
     * @param executionId
     * @param taskId
     * @param elementUser
     * @throws Exception
     */
    void removeExecution(String executionId, String taskId, String elementUser) throws Exception;

    /**
     * 串行减签
     *
     * @param executionId
     * @param taskId
     * @param elementUser
     * @param num
     * @throws Exception
     */
    void removeExecution4Sequential(String executionId, String taskId, String elementUser, int num) throws Exception;
}
