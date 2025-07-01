package net.risesoft.entity;

import java.io.Serializable;

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
@Table(name = "FF_OpinionFrame")
@org.hibernate.annotations.Table(comment = "意见框信息表", appliesTo = "FF_OpinionFrame")
public class OpinionFrame implements Serializable {

    private static final long serialVersionUID = 743873889002635064L;

    /**
     * 意见框唯一标示
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
    @Column(name = "TENANTID", length = 50)
    private String tenantId;

    /**
     * 意见框标识
     */
    @Comment("意见框标识")
    @Column(name = "MARK", length = 50, nullable = false, unique = true)
    private String mark;

    /**
     * 意见框名称
     */
    @Comment("意见框名称")
    @Column(name = "NAME", length = 100, nullable = false)
    private String name;

    /**
     * 录入意见框的人员的名称
     */
    @Comment("人员名称")
    @Column(name = "USERNAME", length = 50)
    private String userName;

    /**
     * 录入意见框的人员的Id
     */
    @Comment("人员id")
    @Column(name = "USERID", length = 50, nullable = true)
    private String userId;

    /**
     * 是否删除
     */
    @Comment("是否删除")
    @Column(name = "DELETED")
    private Integer deleted = 0;

    /**
     * 生成时间
     */
    @Comment("生成时间")
    @Column(name = "CREATEDATE")
    private String createDate;

    /**
     * 最后的修改时间
     */
    @Comment("修改时间")
    @Column(name = "MODIFYDATE")
    private String modifyDate;

}
