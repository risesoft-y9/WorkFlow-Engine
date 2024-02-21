package net.risesoft.entity;

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
@Table(name = "FF_TabEntity")
@org.hibernate.annotations.Table(comment = "自定义页签表", appliesTo = "FF_TabEntity")
public class TabEntity implements Serializable {

    private static final long serialVersionUID = -1231641987535504621L;

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
    @Column(name = "TENANTID", length = 50)
    private String tenantId;

    /**
     * 页签名称
     */
    @Comment("页签名称")
    @Column(name = "NAME", length = 50)
    private String name;

    /**
     * 页签对应的url
     */
    @Comment("页签对应的url")
    @Column(name = "URL", length = 50)
    private String url;

    /**
     * 创建/修改人员的名称
     */
    @Comment("人员名称")
    @Column(name = "USERNAME", length = 50)
    private String userName;

    /**
     * 创建/修改的人员的唯一标示
     */
    @Comment("人员id")
    @Column(name = "USERID", length = 50)
    private String userId;

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
