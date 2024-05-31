package net.risesoft.model.processadmin;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * 流程实例模型类
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Data
public class ProcessInstanceModel implements Serializable {

    private static final long serialVersionUID = 6326703125881185732L;
    /**
     * 主键
     */
    private String Id;
    /**
     * 是否挂起
     */
    private boolean suspended;
    /**
     * 是否结束
     */
    private boolean ended;
    /**
     * 名称
     */
    private String name;
    /**
     * 描述
     */
    private String description;
    /**
     * 流程定义id
     */
    private String processDefinitionId;
    /**
     * 流程定义名称
     */
    private String processDefinitionName;
    /**
     * 流程定义key
     */
    private String processDefinitionKey;
    /**
     * 流程定义版本
     */
    private Integer processDefinitionVersion;
    /**
     * 部署id
     */
    private String deploymentId;
    /**
     * 开始时间
     */
    private Date startTime;
    /**
     * 开始用户id
     */
    private String startUserId;
    /**
     * 流程变量
     */
    private Map<String, Object> variables;
}
