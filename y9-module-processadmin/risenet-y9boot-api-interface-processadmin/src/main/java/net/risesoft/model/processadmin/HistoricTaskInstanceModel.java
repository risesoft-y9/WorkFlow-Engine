package net.risesoft.model.processadmin;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 历史任务实例模型类
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Data
public class HistoricTaskInstanceModel implements Serializable {

    private static final long serialVersionUID = 5536307402841240381L;
    /**
     * 主键
     */
    private String id;
    /**
     * 名称
     */
    private String name;
    /**
     * 描述
     */
    private String description;
    /**
     * 拥有者ID
     */
    private String owner;
    /**
     * 办理者ID
     */
    private String assignee;
    /**
     * 流程实例ID
     */
    private String processInstanceId;
    /**
     * 执行实例ID
     */
    private String executionId;
    /**
     * 流程定义ID
     */
    private String processDefinitionId;
    /**
     * 任务节点KEY
     */
    private String taskDefinitionKey;
    /**
     * 过期时间
     */
    private Date dueDate;
    /**
     * 父任务ID
     */
    private String parentTaskId;
    /**
     * 签收时间
     */
    private Date claimTime;
    /**
     * 删除原因
     */
    private String deleteReason;
    /**
     * 开始时间
     */
    private Date startTime;
    /**
     * 结束时间
     */
    private Date endTime;
    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 存岗位，人员名称，避免岗位换人，或人员删除，名称变化
     */
    private String scopeType;
}
