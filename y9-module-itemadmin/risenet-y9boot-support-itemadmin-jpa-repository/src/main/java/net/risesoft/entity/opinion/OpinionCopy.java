package net.risesoft.entity.opinion;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.entity.base.ItemAdminBaseEntity;

/**
 * @author qinman
 * @date 2025/02/10
 */
@NoArgsConstructor
@Data
@Entity
@Table(name = "FF_OPINION_COPY", indexes = {@Index(name = "ff_opinion_copy_001", columnList = "processSerialNumber")})
@Comment("传签意见信息表")
public class OpinionCopy extends ItemAdminBaseEntity implements Serializable {

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

    @Convert(converter = org.hibernate.type.NumericBooleanConverter.class)
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
}
