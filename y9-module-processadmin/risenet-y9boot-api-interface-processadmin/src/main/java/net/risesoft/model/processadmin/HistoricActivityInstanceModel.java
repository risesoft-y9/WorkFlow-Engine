package net.risesoft.model.processadmin;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 历史活动实例模型
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Data
public class HistoricActivityInstanceModel implements Serializable {

    private static final long serialVersionUID = -264972095894876276L;
    /**
     * 主键
     */
    private String Id;
    /**
     * 节点id
     */
    private String activityId;
    /**
     * 节点名称
     */
    private String activityName;
    /**
     * 节点类型
     */
    private String activityType;
    /**
     * 流程定义id
     */
    private String processDefinitionId;
    /**
     * 流程实例id
     */
    private String processInstanceId;
    /**
     * 执行实例id
     */
    private String executionId;
    /**
     * 任务id
     */
    private String taskId;
    /**
     * 调用流程实例id
     */
    private String calledProcessInstanceId;
    /**
     * 办理人ID
     */
    private String assignee;
    /**
     * 开始时间
     */
    private Date startTime;
    /**
     * 结束时间
     */
    private Date endTime;
    /**
     * 持续时间
     */
    private Long durationInMillis;
    /**
     * 删除原因
     */
    private String deleteReason;
    /**
     * 租户id
     */
    private String tenantId;
}
