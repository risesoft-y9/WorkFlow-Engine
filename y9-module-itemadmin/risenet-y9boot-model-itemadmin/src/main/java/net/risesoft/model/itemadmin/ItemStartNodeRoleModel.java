package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

/**
 * @author qinman
 * @date 2024/10/16
 */
@Data
public class ItemStartNodeRoleModel implements Serializable {

    private static final long serialVersionUID = -7639886416862609210L;

    /**
     * 主键
     */
    private String id;

    /**
     * 事项Id
     */
    private String itemId;

    /**
     * 流程定义Id
     */
    private String processDefinitionId;

    /**
     * 任务节点Key
     */
    private String taskDefKey;

    /**
     * 任务节点名称
     */
    private String taskDefName;

    /**
     * 序号
     */
    private Integer tabIndex;
}