package net.risesoft.model.processadmin;

import java.io.Serializable;

import lombok.Data;

/**
 * 任务目标节点模型
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Data
public class TargetModel implements Serializable {

    private static final long serialVersionUID = -259787849157514873L;

    /**
     * 节点key
     */
    private String taskDefKey;
    /**
     * 任务名称（线上名称存在这里就是线的名字）
     */
    private String taskDefName;
    /**
     * 多实例类型
     */
    private String multiInstance;

    /**
     * 节点类型
     */
    private String type;

    /**
     * 条件表达式
     */
    private String conditionExpression;

    /**
     * 实际任务名称
     */
    private String realTaskDefName;

    /**
     * 节点绑定的普通按钮
     */
    private String commonButtonNames;

    /**
     * 节点绑定的发送按钮
     */
    private String sendButtonNames;

    /**
     * 节点绑定的意见框名称
     */
    private String opinionFrameNames;

    /**
     * 节点绑定的编号信息
     */
    private String bindNames;

    /**
     * 节点绑定的角色信息
     */
    private String roleNames;
}
