package net.risesoft.model.processadmin;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * 历史流程实例模型类
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Data
public class HistoricProcessInstanceModel implements Serializable {

    private static final long serialVersionUID = 1809384368949513536L;
    /**
     * 主键
     */
    private String Id;
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
     * 结束时间
     */
    private Date endTime;
    /**
     * 持续时间
     */
    private Long durationInMillis;
    /**
     * 结束节点id
     */
    private String endActivityId;
    /**
     * 启动用户id
     */
    private String startUserId;
    /**
     * 启动节点id
     */
    private String startActivityId;
    /**
     * 删除原因
     */
    private String deleteReason;
    /**
     * 超级流程实例id
     */
    private String superProcessInstanceId;
    /**
     * 名称
     */
    private String name;
    /**
     * 描述
     */
    private String description;
    /**
     * 变量
     */
    private Map<String, Object> variables;
}
