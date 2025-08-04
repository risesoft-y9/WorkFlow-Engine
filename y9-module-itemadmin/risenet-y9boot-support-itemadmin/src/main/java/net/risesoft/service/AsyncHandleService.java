package net.risesoft.service;

import java.util.List;
import java.util.Map;

import org.springframework.scheduling.annotation.Async;

import net.risesoft.entity.ProcessParam;
import net.risesoft.entity.opinion.Opinion;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.processadmin.FlowElementModel;
import net.risesoft.model.processadmin.TaskModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface AsyncHandleService {

    /**
     * 异步发送
     *
     * @param tenantId
     * @param orgUnit
     * @param processInstanceId
     * @param processParam
     * @param sponsorHandle
     * @param sponsorGuid
     * @param taskId
     * @param variables
     * @param userAndDeptIdList
     * @return
     */
    @Async
    void forwarding(final String tenantId, final OrgUnit orgUnit, final String processInstanceId,
        final ProcessParam processParam, final String sponsorHandle, final String sponsorGuid, final String taskId,
        final FlowElementModel flowElementModel, final Map<String, Object> variables,
        final List<String> userAndDeptIdList);

    void forwarding4Gfg(String processInstanceId, ProcessParam processParam, String sponsorHandle, String sponsorGuid,
        String taskId, FlowElementModel flowElementModel, Map<String, Object> variables, List<String> userList);

    void forwarding4Task(String processInstanceId, ProcessParam processParam, String sponsorHandle, String sponsorGuid,
        String taskId, FlowElementModel flowElementModel, Map<String, Object> variables, List<String> userList)
        throws Exception;

    /**
     * 发送后异步处理
     *
     * @param tenantId
     * @param task
     * @param processInstanceId
     * @param flowElementModel
     * @param sponsorGuid
     * @param processParam
     */
    void forwardingHandle(final String tenantId, final String orgUnitId, final TaskModel task, final String executionId,
        final String processInstanceId, final FlowElementModel flowElementModel, final String sponsorGuid,
        final ProcessParam processParam, List<String> userList);

    /**
     * 保存意见历史记录
     *
     * @param oldOpinion
     * @param opinionType
     */
    void saveOpinionHistory(final String tenantId, final Opinion oldOpinion, final String opinionType);

    /**
     * 发送意见填写消息提醒
     *
     * @param processSerialNumber
     */
    void sendMsgRemind(final String tenantId, final String userId, final String processSerialNumber,
        final String content);

    /**
     * 启动流程后数据处理
     *
     * @param processSerialNumber
     * @param taskId
     * @param processInstanceId
     * @param searchTerm
     */
    void startProcessHandle(final String tenantId, final String processSerialNumber, final String taskId,
        final String processInstanceId, final String searchTerm);
}
