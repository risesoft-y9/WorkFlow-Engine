package net.risesoft.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.entity.base.ItemAdminBaseEntity;

/**
 * @author qinman
 * @date 2025/08/22
 */
@NoArgsConstructor
@Data
@Entity
@Table(name = "FF_SMS_DETAIL")
@org.hibernate.annotations.Table(comment = "短信详情", appliesTo = "FF_SMS_DETAIL")
public class SmsDetail extends ItemAdminBaseEntity implements Serializable {

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

    @Type(type = "numeric_boolean")
    @ColumnDefault("0")
    @Comment("是否发送短信")
    @Column(name = "SEND", length = 50)
    private boolean send;

    @Type(type = "numeric_boolean")
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
}
