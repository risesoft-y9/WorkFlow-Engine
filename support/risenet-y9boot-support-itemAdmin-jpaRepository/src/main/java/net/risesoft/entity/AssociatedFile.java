package net.risesoft.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

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
@Table(name = "FF_AssociatedFile")
@org.hibernate.annotations.Table(comment = "关联文件表", appliesTo = "FF_AssociatedFile")
public class AssociatedFile implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -7634277136028658011L;

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
     * 流程编号
     */
    @Comment("流程编号")
    @Column(name = "PROCESSSERIALNUMBER", nullable = false)
    private String processSerialNumber;

    /**
     * 流程实例id
     */
    @Lob
    @Comment("流程实例id")
    @Column(name = "PROCESSINSTANCEID")
    private String processInstanceId;

    /**
     * 关联流程实例id
     */
    @Lob
    @Comment("关联流程实例id")
    @Column(name = "ASSOCIATEDID")
    private String associatedId;

    /**
     * 关联时间
     */
    @Comment("关联时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATETIME")
    private Date createTime;

    /**
     * 创建人id
     */
    @Comment("创建人id")
    @Column(name = "USERID", length = 50)
    private String userId;

    /**
     * 创建人姓名
     */
    @Comment("创建人姓名")
    @Column(name = "USERNAME", length = 50)
    private String userName;
}
