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

@NoArgsConstructor
@Data
@Entity
@Table(name = "FF_ATTACHMENT_TYPE")
@org.hibernate.annotations.Table(comment = "附件类型信息表", appliesTo = "FF_ATTACHMENT_TYPE")
public class AttachmentType implements Serializable {

    private static final long serialVersionUID = -3668629910203969082L;

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
     * 附件类型标识
     */
    @Comment("附件类型标识")
    @Column(name = "MARK", length = 50, nullable = false, unique = true)
    private String mark;

    /**
     * 附件类型名称
     */
    @Comment("附件类型名称")
    @Column(name = "NAME", length = 100, nullable = false)
    private String name;

    /**
     * 录入附件类型的人员的名称
     */
    @Comment("人员名称")
    @Column(name = "USERNAME", length = 50)
    private String userName;

    /**
     * 录入附件类型的人员的Id
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
