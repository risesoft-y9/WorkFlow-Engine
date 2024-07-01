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
public class FlowableBpmnModel implements Serializable {

    private static final long serialVersionUID = 8816719678642100925L;

    /**
     * 流程模型唯一标示
     */
    private String id;

    /**
     * 流程模型key
     */
    private String key;

    /**
     * 流程模型名称
     */
    private String name;

    /**
     * 版本
     */
    private int version;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 最后修改时间
     */
    private String lastUpdateTime;
    /**
     * 模型xml信息
     */
    private String xml;
}
