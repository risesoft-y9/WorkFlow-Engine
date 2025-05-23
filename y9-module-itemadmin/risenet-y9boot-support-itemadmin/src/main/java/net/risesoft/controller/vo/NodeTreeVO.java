package net.risesoft.controller.vo;

import java.io.Serializable;

import lombok.Data;

/**
 * 系统角色树节点
 *
 * @author mengjuhua
 * @date 2024/07/15
 */
@Data
public class NodeTreeVO implements Serializable {
    private static final long serialVersionUID = 2072767321247540184L;

    /** 主键 */
    private String id;

    /** 名称 */
    private String name;

    /** 性别 */
    private Integer sex;

    /** 职务 */
    private String duty;

    /** 父节点 */
    private String parentId;

    /** 是否父节点 */
    private Boolean isParent;

    /** 类型 */
    private String orgType;
    /**
     * 承继节点
     */
    private String guidPath;

    /**
     * 是否禁用
     */
    private Boolean chkDisabled;

    /**
     * 是否选中
     */
    private Boolean nocheck;

    /**
     * 节点路径
     */
    private String dn;

}
