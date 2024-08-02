package net.risesoft.controller.vo;

import java.io.Serializable;

import lombok.Data;

/**
 * Y9表单与事项流程任务定义信息
 *
 * @author mengjuhua
 * @date 2024/07/12
 */
@Data
public class ItemNodeLinkBindVO implements Serializable {
    private static final long serialVersionUID = 1692947675114605922L;

    /**
     * 任务名称（线上名称存在这里就是线的名字）
     */
    private String taskDefName;

    /** 节点key */
    private String taskDefKey;

    /** 链接绑定id */
    private String linkBindId;

    /** 链接名称 */
    private String linkName;

    /** 角色名称 */
    private String roleNames;

    /** 角色绑定id */
    private String roleBindIds;

}
