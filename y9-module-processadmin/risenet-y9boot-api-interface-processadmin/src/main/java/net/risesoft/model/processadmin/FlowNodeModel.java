package net.risesoft.model.processadmin;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 执行模型
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Data
@AllArgsConstructor
public class FlowNodeModel implements Serializable {

    private static final long serialVersionUID = 2725030569008818532L;
    /**
     * 节点key
     */
    private String key;
    /**
     * 类别
     */
    private String category;

    /**
     * 节点名称
     */
    private String text;

    /**
     * 图形
     */
    private String figure;

    /**
     * 填充颜色
     */
    private String fill;

    /**
     * stepType
     */
    private String stepType;

    /**
     * 位置
     */
    private String loc;

    /**
     * 评论
     */
    private String remark;
}
