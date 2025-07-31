package net.risesoft.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.enums.DynamicRoleKindsEnum;
import net.risesoft.enums.DynamicRoleRangesEnum;
import net.risesoft.persistence.ItemEnumConverter;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@NoArgsConstructor
@Data
@Entity
@Table(name = "FF_DYNAMICROLE")
@org.hibernate.annotations.Table(comment = "动态角色表", appliesTo = "FF_DYNAMICROLE")
public class DynamicRole implements Serializable {

    private static final long serialVersionUID = 2662610252539539962L;

    @Id
    @Comment("主键")
    @Column(name = "ID", length = 255, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("租户Id")
    @Column(name = "TENANTID", length = 50, nullable = false)
    private String tenantId;

    @Comment("动态角色名称")
    @Column(name = "NAME", length = 255)
    private String name;

    @Comment("动态角色种类")
    @Column(name = "KINDS", length = 2)
    @Convert(converter = ItemEnumConverter.DynamicRoleKindsEnumConverter.class)
    private DynamicRoleKindsEnum kinds = DynamicRoleKindsEnum.NONE;

    @Comment("权限范围")
    @Column(name = "RANGES", length = 2)
    @Convert(converter = ItemEnumConverter.DynamicRoleRangesEnumConverter.class)
    private DynamicRoleRangesEnum ranges = DynamicRoleRangesEnum.NONE;

    @Comment("角色ID")
    @Column(name = "ROLEID", length = 50)
    private String roleId;

    @Transient
    private String roleName;

    @Comment("部门属性种类")
    @Column(name = "DEPTPROPCATEGORY", length = 2)
    private Integer deptPropCategory;

    @Transient
    private String deptPropCategoryName;

    @Comment("描述")
    @Column(name = "DESCRIPTION", length = 1000)
    private String description;

    @Comment("类全路径")
    @Column(name = "CLASSPATH", length = 500, nullable = false)
    private String classPath;

    @Comment("序号")
    @Column(name = "TABINDEX", length = 10)
    private Integer tabIndex;

    @Type(type = "numeric_boolean")
    @ColumnDefault("0")
    @Comment("是否使用流程实例")
    @Column(name = "USEPROCESSINSTANCEID")
    private boolean useProcessInstanceId = false;
}
