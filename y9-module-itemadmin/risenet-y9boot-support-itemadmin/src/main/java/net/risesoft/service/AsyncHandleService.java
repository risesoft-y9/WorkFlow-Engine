package net.risesoft.service;

import java.util.List;
import java.util.Map;

import org.springframework.scheduling.annotation.Async;

import net.risesoft.entity.ProcessParam;
import net.risesoft.entity.opinion.Opinion;
import net.risesoft.model.platform.org.OrgUnit;
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
     * @param tenantId 租户id
     * @param orgUnit 组织
     * @param processInstanceId 流程实例id
     * @param processParam 流程参数
     * @param sponsorHandle 发起人处理
     * @param sponsorGuid 发起人id
     * @param taskId 任务id
     * @param variables 流程变量
     * @param userAndDeptIdList 用户和部门id列表
     */
    @Async
    void forwarding(final String tenantId, final OrgUnit orgUnit, final String processInstanceId,
        final ProcessParam processParam, final String sponsorHandle, final String sponsorGuid, final String taskId,
        final FlowElementModel flowElementModel, final Map<String, Object> variables,
        final List<String> userAndDeptIdList);

    /**
     * 启动流程后数据处理
     * 
     * @param processInstanceId 流程实例id
     * @param processParam 流程参数
     * @param sponsorHandle 发起人处理
     * @param sponsorGuid 发起人id
     * @param taskId 任务id
     * @param flowElementModel 流程元素
     * @param variables 流程变量
     * @param userList 用户列表
     */
    void forwarding4Gfg(String processInstanceId, ProcessParam processParam, String sponsorHandle, String sponsorGuid,
        String taskId, FlowElementModel flowElementModel, Map<String, Object> variables, List<String> userList);

    /**
     * 发送后异步处理
     * 
     * @param processInstanceId 流程实例id
     * @param processParam 流程参数
     * @param sponsorHandle 发起人处理
     * @param sponsorGuid 发起人id
     * @param taskId 任务id
     * @param flowElementModel 流程元素
     * @param variables 流程变量
     * @param userList 用户列表
     */
    void forwarding4Task(String processInstanceId, ProcessParam processParam, String sponsorHandle, String sponsorGuid,
        String taskId, FlowElementModel flowElementModel, Map<String, Object> variables, List<String> userList)
        throws Exception;

    /**
     * 发送后异步处理
     * 
     * @param tenantId 租户id
     * @param orgUnitId 组织id
     * @param task 任务
     * @param executionId 执行id
     * @param processInstanceId 流程实例id
     * @param flowElementModel 流程元素
     * @param sponsorGuid 发起人id
     * @param processParam 流程参数
     * @param userList 用户列表
     */
    void forwardingHandle(final String tenantId, final String orgUnitId, final TaskModel task, final String executionId,
        final String processInstanceId, final FlowElementModel flowElementModel, final String sponsorGuid,
        final ProcessParam processParam, List<String> userList);

    /**
     * 保存意见历史记录
     * 
     * @param tenantId 租户id
     * @param oldOpinion 旧意见
     * @param opinionType 意见类型
     */
    void saveOpinionHistory(final String tenantId, final Opinion oldOpinion, final String opinionType);

    /**
     * 发送意见填写消息提醒
     *
     * @param processSerialNumber 流程编号
     */
    void sendMsgRemind(final String tenantId, final String userId, final String processSerialNumber,
        final String content);

    /**
     * 启动流程后数据处理
     *
     * @param processSerialNumber 流程编号
     * @param taskId 任务id
     * @param processInstanceId 流程实例id
     * @param searchTerm 搜索条件
     */
    void startProcessHandle(final String tenantId, final String processSerialNumber, final String taskId,
        final String processInstanceId, final String searchTerm);
}
