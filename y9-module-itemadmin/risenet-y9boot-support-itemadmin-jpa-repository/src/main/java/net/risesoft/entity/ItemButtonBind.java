package net.risesoft.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@NoArgsConstructor
@Data
@Entity
@Table(name = "FF_ITEM_BUTTONBIND")
@org.hibernate.annotations.Table(comment = "事项按钮绑定表", appliesTo = "FF_ITEM_BUTTONBIND")
public class ItemButtonBind implements Serializable {

    private static final long serialVersionUID = 8492379510656608228L;

    /**
     * 唯一标示
     */
    @Id
    @Comment("主键")
    @Column(name = "ID", length = 38, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    /**
     * 租户Id
     */
    @Comment("租户Id")
    @Column(name = "TENANTID", length = 50, nullable = false)
    private String tenantId;

    /**
     * 按钮唯一标示
     */
    @Comment("按钮id")
    @Column(name = "BUTTONID", length = 38, nullable = false)
    private String buttonId;

    /**
     * 按钮类型 1为普通按钮，2为发送下面的按钮
     */
    @Comment("按钮类型")
    @Column(name = "BUTTONTYPE", length = 2, nullable = false)
    private Integer buttonType;

    /**
     * 按钮名称
     */
    @Comment("按钮名称")
    @Transient
    private String buttonName;

    @Comment("按钮标识")
    @Transient
    private String buttonCustomId;

    /**
     * 事项Id
     */
    @Comment("事项Id")
    @Column(name = "ITEMID", length = 100, nullable = false)
    private String itemId;

    /**
     * 流程定义Id
     */
    @Comment("流程定义Id")
    @Column(name = "PROCESSDEFINITIONID", length = 100, nullable = false)
    private String processDefinitionId;

    /**
     * 流程节点Key
     */
    @Comment("流程节点Key")
    @Column(name = "TASKDEFKEY", length = 100, nullable = false)
    private String taskDefKey;

    /**
     * 角色名称
     */
    @Transient
    private String roleNames;

    /**
     * 角色Id
     */
    @Transient
    private List<String> roleIds;

    /**
     * 创建/修改人员的名称
     */
    @Comment("创建人名称")
    @Column(name = "USERNAME", length = 50)
    private String userName;

    /**
     * 创建/修改的人员的唯一标示
     */
    @Comment("创建人id")
    @Column(name = "USERID", length = 50)
    private String userId;

    /**
     * 序号
     */
    @Comment("序号")
    @Column(name = "TABINDEX", length = 3)
    private Integer tabIndex;

    /**
     * 生成时间
     */
    @Comment("生成时间")
    @Column(name = "CREATETIME", length = 50)
    private String createTime;

    /**
     * 更新时间
     */
    @Comment("更新时间")
    @Column(name = "UPDATETIME", length = 50)
    private String updateTime;
}
