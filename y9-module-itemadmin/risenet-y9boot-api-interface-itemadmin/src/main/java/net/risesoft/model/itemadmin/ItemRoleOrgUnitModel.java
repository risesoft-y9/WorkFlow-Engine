package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

/**
 * 发送选人组织架构模型
 *
 * @author mengjuhua
 * @date 2024/06/26
 */
@Data
public class ItemRoleOrgUnitModel implements Serializable {

    private static final long serialVersionUID = 8518359792106513369L;
    /**
     * 唯一标识
     */
    private String id;

    /**
     * 父节点ID
     */
    private String parentId;

    /**
     * 名称
     */
    private String name;

    /**
     * 节点类型
     */
    private String orgType;

    /**
     * 是否父节点
     */
    private Boolean isParent;

    /**
     * 选项类型
     */
    private Integer principalType;

    /**
     * 选项id
     */
    private String person;

}
