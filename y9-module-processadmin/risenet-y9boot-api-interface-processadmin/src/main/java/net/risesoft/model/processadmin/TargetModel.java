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
     * 节点名称
     */
    private String taskDefName;
    /**
     * 多实例类型
     */
    private String multiInstance;
}
