package net.risesoft.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qinman
 * @date 2025/08/22
 */
@NoArgsConstructor
@Data
@Entity
@Table(name = "FF_SMS_DETAIL")
@Comment("短信详情")
public class SmsDetail implements Serializable {

    private static final long serialVersionUID = 8594290794735213206L;

    @Id
    @Comment("主键")
    @Column(name = "ID", length = 50, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("流程编号")
    @Column(name = "PROCESSSERIALNUMBER", length = 50, nullable = false)
    private String processSerialNumber;

    @Comment("岗位ID")
    @Column(name = "POSITIONID", length = 50, nullable = false)
    private String positionId;

    @Comment("岗位名称")
    @Column(name = "POSITIONNAME", length = 50, nullable = false)
    private String positionName;

    @Convert(converter = org.hibernate.type.NumericBooleanConverter.class)
    @ColumnDefault("0")
    @Comment("是否发送短信")
    @Column(name = "SEND", length = 50)
    private boolean send;

    @Convert(converter = org.hibernate.type.NumericBooleanConverter.class)
    @ColumnDefault("0")
    @Comment("是否署名")
    @Column(name = "SIGN", length = 50)
    private boolean sign;

    @Comment("发送短信内容")
    @Column(name = "CONTENT", length = 2000)
    private String content;

    @Comment("接收短信岗位id")
    @Lob
    @Column(name = "POSITIONIDS")
    private String positionIds;

    @Comment("生成时间")
    @Column(name = "CREATEDATE", length = 50)
    private String createDate;

    @Comment("修改时间")
    @Column(name = "MODIFYDATE", length = 50)
    private String modifyDate;
}
