package net.risesoft.model.processadmin;

import java.io.Serializable;

import lombok.Data;

/**
 * 执行模型
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Data
public class ExecutionModel implements Serializable {

    private static final long serialVersionUID = 2555371070475673717L;
    /**
     * 主键
     */
    private String id;
    /**
     * 是否挂起
     */
    private boolean isSuspended;
    /**
     * 是否结束
     */
    private boolean isEnded;
    /**
     * 活动id
     */
    private String activityId;
    /**
     * 流程实例id
     */
    private String processInstanceId;
    /**
     * 父活动id
     */
    private String parentId;
    /**
     * 超级执行实例id
     */
    private String superExecutionId;
    /**
     * 根流程实例id
     */
    private String rootProcessInstanceId;
    /**
     * 租户id
     */
    private String tenantId;
    /**
     * 名称
     */
    private String name;
    /**
     * 描述
     */
    private String description;

}
