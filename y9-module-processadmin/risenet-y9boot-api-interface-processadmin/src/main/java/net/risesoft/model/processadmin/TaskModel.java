package net.risesoft.model.processadmin;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import org.flowable.task.api.DelegationState;

import lombok.Data;

/**
 * 流程实例模型类
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Data
public class TaskModel implements Serializable {

    private static final long serialVersionUID = -8531935784821978703L;
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
     * 优先级
     */
    private int priority;
    /**
     * 拥有者
     */
    private String owner;
    /**
     * 办理人
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
     * 创建时间
     */
    private Date createTime;
    /**
     * 任务定义key
     */
    private String taskDefinitionKey;
    /**
     * 表单key(用于存放待办件已阅，未阅状态)
     */
    private String formKey;
    /**
     * 过期时间
     */
    private Date dueDate;
    /**
     * 签收时间
     */
    private Date claimTime;
    /**
     * 委派状态
     */
    private DelegationState delegationState;
    /**
     * 任务变量
     */
    private Map<String, Object> localVariables;
    /**
     * 流程变量
     */
    private Map<String, Object> variables;

}
