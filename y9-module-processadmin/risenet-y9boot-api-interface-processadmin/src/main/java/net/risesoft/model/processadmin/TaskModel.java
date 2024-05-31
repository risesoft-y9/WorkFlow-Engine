package net.risesoft.model.processadmin;

import lombok.Data;
import org.flowable.task.api.DelegationState;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;


@Data
public class TaskModel implements Serializable {

    private static final long serialVersionUID = -8531935784821978703L;
    /**
     * 主键
     */
    private String Id;
    /**
     * 名称
     */
    private String Name;
    /**
     * 描述
     */
    private String Description;
    /**
     * 优先级
     */
    private int priority;
    /**
     * 拥有者
     */
    private String Owner;
    /**
     * 办理人
     */
    private String Assignee;
    /**
     * 流程实例ID
     */
    private String ProcessInstanceId;
    /**
     * 执行实例ID
     */
    private String ExecutionId;
    /**
     * 流程定义ID
     */
    private String ProcessDefinitionId;
    /**
     * 创建时间
     */
    private Date CreateTime;
    /**
     * 任务定义key
     */
    private String TaskDefinitionKey;
    /**
     * 表单key(用于存放待办件已阅，未阅状态)
     */
    private String formKey;
    /**
     * 过期时间
     */
    private Date DueDate;
    /**
     * 签收时间
     */
    private Date ClaimTime;
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
