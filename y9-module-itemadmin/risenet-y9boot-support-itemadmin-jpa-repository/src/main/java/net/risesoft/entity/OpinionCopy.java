package net.risesoft.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qinman
 * @date 2025/02/10
 */
@NoArgsConstructor
@Data
@Entity
@Table(name = "FF_OPINION_COPY", indexes = {@Index(name = "ff_opinion_copy_001", columnList = "processSerialNumber")})
@org.hibernate.annotations.Table(comment = "传签意见信息表", appliesTo = "FF_OPINION_COPY")
public class OpinionCopy implements Serializable {

    private static final long serialVersionUID = -7912583687012196515L;

    @Id
    @Comment("主键")
    @Column(name = "ID", length = 50, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("抄送的流程实例")
    @Column(name = "PROCESSSERIALNUMBER", length = 50, nullable = false)
    private String processSerialNumber;

    @Comment("意见内容")
    @Column(name = "CONTENT", nullable = false, length = 1000)
    private String content;

    @Type(type = "numeric_boolean")
    @ColumnDefault("0")
    @Comment("是否是发送意见")
    @Column(name = "SEND")
    private boolean send;

    @Comment("抄送目标人员Id")
    @Column(name = "USERID", length = 50, nullable = false)
    private String userId;

    @Comment("人员名称")
    @Column(name = "USERNAME", length = 50, nullable = false)
    private String userName;

    @Comment("生成时间")
    @Column(name = "CREATETIME", length = 100)
    private String createTime;

    @Comment("更新时间")
    @Column(name = "UPDATETIME", length = 100)
    private String updateTime;
}
