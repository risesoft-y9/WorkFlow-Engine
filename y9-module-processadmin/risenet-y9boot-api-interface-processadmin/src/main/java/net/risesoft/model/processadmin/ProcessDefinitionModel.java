package net.risesoft.model.processadmin;

import java.io.Serializable;

import lombok.Data;

/**
 * 流程定义模型类
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Data
public class ProcessDefinitionModel implements Serializable {

    private static final long serialVersionUID = -2797024298337897009L;
    /**
     * 主键
     */
    private String Id;
    /**
     * 类别
     */
    private String category;
    /**
     * 名称
     */
    private String name;
    /**
     * key
     */
    private String key;
    /**
     * 描述
     */
    private String description;
    /**
     * 版本
     */
    private int version;
    /**
     * 资源名称
     */
    private String resourceName;
    /**
     * 部署id
     */
    private String deploymentId;
    /**
     * 图表资源名称
     */
    private String diagramResourceName;
    /**
     * 是否挂起
     */
    private boolean suspended;
    /**
     * 引擎版本
     */
    private String engineVersion;

}
