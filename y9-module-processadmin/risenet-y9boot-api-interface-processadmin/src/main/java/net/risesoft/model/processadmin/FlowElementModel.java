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
public class FlowElementModel implements Serializable {

    private static final long serialVersionUID = -8776578221382137135L;

    /**
     * 元素key
     */
    private String elementKey;
    /**
     * 元素名称
     */
    private String elementName;
    /**
     * 元素类型 UserTask SequenceFlow
     */
    private String multiInstance;

    /**
     * 节点类型
     */
    private String type;

    private boolean bind;

    private String condition;
}
