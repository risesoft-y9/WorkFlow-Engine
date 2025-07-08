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
@Table(name = "FF_ORGANWORD_USEHISTORY")
@org.hibernate.annotations.Table(comment = "公文编号使用历史表", appliesTo = "FF_ORGANWORD_USEHISTORY")
public class OrganWordUseHistory implements Serializable {

    /**
     * 机关代字使用详情
     */
    private static final long serialVersionUID = 2407357932952334903L;

    /**
     * 主键
     */
    @Id
    @Comment("主键")
    @Column(name = "ID", length = 50, nullable = false)
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
     * 机关代字标志
     */
    @Comment("机关代字标志")
    @Column(name = "CUSTOM", length = 50, nullable = false)
    private String custom;

    /**
     * 事项Id
     */
    @Comment("事项Id")
    @Column(name = "ITEMID", length = 50, nullable = false)
    private String itemId;

    /**
     * 流程编号
     */
    @Comment("流程编号")
    @Column(name = "PROCESSSERIALNUMBER", length = 50, nullable = false)
    private String processSerialNumber;

    /**
     * 编号
     */
    @Comment("编号")
    @Column(name = "NUMBERSTRING", length = 50, nullable = false)
    private String numberString;

    /**
     * 编号时人员Id
     */
    @Comment("人员Id")
    @Column(name = "USERID", length = 50, nullable = false)
    private String userId;

    /**
     * 编号时的人员名称
     */
    @Comment("人员名称")
    @Column(name = "USERNAME", length = 50, nullable = false)
    private String userName;

    /**
     * 编号时间
     */
    @Comment("编号时间")
    @Column(name = "CREATETIME", nullable = false)
    private String createTime;

}
