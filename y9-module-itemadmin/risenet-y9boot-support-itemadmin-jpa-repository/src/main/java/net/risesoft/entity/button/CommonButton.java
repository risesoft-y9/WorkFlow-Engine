package net.risesoft.entity.button;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

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
@Table(name = "FF_COMMONBUTTON")
@org.hibernate.annotations.Table(comment = "按钮信息表", appliesTo = "FF_COMMONBUTTON")
public class CommonButton implements Serializable {

    private static final long serialVersionUID = -4794987057156388177L;

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
     * 按钮名称
     */
    @Comment("按钮名称")
    @Column(name = "NAME", length = 50, nullable = false)
    private String name;

    /**
     * 租户Id
     */
    @Comment("租户Id")
    @Column(name = "TENANTID", length = 50, nullable = false)
    private String tenantId;

    /**
     * 租户Id
     */
    @Comment("按钮标识")
    @Column(name = "CUSTOMID", length = 50, nullable = false, unique = true)
    private String customId;

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
     * 生成时间
     */
    @Comment("创建时间")
    @Column(name = "CREATETIME")
    private String createTime;

    /**
     * 生成时间
     */
    @Comment("修改时间")
    @Column(name = "UPDATETIME")
    private String updateTime;
}
