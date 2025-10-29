package net.risesoft.service;

import java.util.List;

import net.risesoft.entity.ProcessParam;
import net.risesoft.model.processadmin.FlowElementModel;
import net.risesoft.model.processadmin.TaskModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface AsyncForwardingHandleService {

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
}
