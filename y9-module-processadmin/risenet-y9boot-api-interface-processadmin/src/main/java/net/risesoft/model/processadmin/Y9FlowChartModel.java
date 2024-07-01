package net.risesoft.model.processadmin;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * 流程图数据
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Data
public class Y9FlowChartModel implements Serializable {

    private static final long serialVersionUID = -6212213808390844069L;
    /**
     * 唯一标示
     */
    private String id;

    /**
     * 父节点唯一标示
     */
    private String parentId;

    /**
     * 名称
     */
    private String name;

    /**
     * 标题
     */
    private String title;

    /**
     * 填充颜色
     */
    private String className;

    /**
     * 序号
     */
    private int num;

    /**
     * 结束时间
     */
    private long endTime;

    private boolean collapsed;

    private List<Y9FlowChartModel> children;
}
