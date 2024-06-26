package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

/**
 * 收发单位
 *
 * @author mengjuhua
 * @date 2024/06/26
 */
@Data
public class ReceiveOrgUnit implements Serializable {
    private static final long serialVersionUID = -8452051156924106126L;

    /**
     * 唯一标识
     */
    private String id;

    /**
     * 父节点ID
     */
    private String parentId;

    /**
     * 是否禁用
     */
    private Boolean disabled = false;

    /**
     * 名称
     */
    private String name;

    /**
     * 节点类型
     */
    private String orgType;

    /** 是否父节点 */
    private Boolean isParent;

    /** 带委办局的名称 */
    private String nameWithBureau;

}
