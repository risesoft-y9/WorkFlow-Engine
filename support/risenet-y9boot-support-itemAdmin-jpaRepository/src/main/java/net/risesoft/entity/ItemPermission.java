package net.risesoft.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

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
@Table(name = "FF_ITEM_PERMISSION")
@org.hibernate.annotations.Table(comment = "事项权限绑定", appliesTo = "FF_ITEM_PERMISSION")
public class ItemPermission implements Serializable {

    private static final long serialVersionUID = -2591411629315187196L;

    @Id
    @org.hibernate.annotations.Comment("主键")
    @Column(name = "ID")
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;
    /**
     * 租户Id
     */
    @org.hibernate.annotations.Comment("租户Id")
    @Column(name = "TENANTID", length = 50)
    private String tenantId;
    /**
     * 角色Id
     */
    @org.hibernate.annotations.Comment("角色Id")
    @Column(name = "ROLEID")
    private String roleId;
    /**
     * 1=角色，2=部门，6=岗位，4=动态角色
     */
    @org.hibernate.annotations.Comment("角色类型")
    @Column(name = "ROLETYPE")
    private Integer roleType;

    @org.hibernate.annotations.Comment("事项Id")
    @Column(name = "ITEMID", length = 55, nullable = false)
    private String itemId;

    @org.hibernate.annotations.Comment("流程定义Id")
    @Column(name = "PROCESSDEFINITIONID", length = 255, nullable = false)
    private String processDefinitionId;

    /**
     * taskDefKey为空表示是流程的缺省表单
     */
    @org.hibernate.annotations.Comment("任务key")
    @Column(name = "TASKDEFKEY", length = 255)
    private String taskDefKey;

    @org.hibernate.annotations.Comment("排序号")
    @Column(name = "TABINDEX", length = 10)
    private Integer tabIndex;

    @org.hibernate.annotations.Comment("生成时间")
    @Column(name = "CREATDATE", length = 50)
    private String creatDate;

    /**
     * 角色名称
     */
    @Transient
    private String roleName;

    /**
     * 事项名称
     */
    @Transient
    private String itemName;

    /**
     * 流程定义名称
     */
    @Transient
    private String processDefName;

    /**
     * 任务节点名称
     */
    @Transient
    private String taskDefName;
}
