package net.risesoft.model.processadmin;

import java.io.Serializable;

import lombok.Data;

/**
 * 参与者模型
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Data
public class IdentityLinkModel implements Serializable {

    private static final long serialVersionUID = 4650268040263457370L;
    /**
     * 参与者类型
     */
    private String type;
    /**
     * 参与者ID
     */
    private String userId;
    /**
     * 参与者组ID
     */
    private String groupId;
    /**
     * 任务ID
     */
    private String taskId;
    /**
     * 流程实例ID
     */
    private String processInstanceId;
    /**
     * 流程定义ID
     */
    private String processDefinitionId;

}
