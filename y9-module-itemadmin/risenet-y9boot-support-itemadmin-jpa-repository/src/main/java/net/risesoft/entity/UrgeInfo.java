package net.risesoft.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qinman
 * @date 2024/12/24
 */
@NoArgsConstructor
@Data
@Entity
@org.hibernate.annotations.Table(comment = "催办信息表", appliesTo = "FF_URGEINFO")
@Table(name = "FF_URGEINFO", indexes = {@Index(name = "ff_urgeinfo_index_001", columnList = "processSerialNumber")})
public class UrgeInfo implements Serializable {

    @Id
    @Comment("主键")
    @Column(name = "ID", length = 38)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("流程序列号")
    @Column(name = "PROCESSSERIALNUMBER", length = 50, nullable = false)
    private String processSerialNumber;

    @Comment("流程实例id")
    @Column(name = "PROCESSINSTANCEID", length = 50, nullable = false)
    private String processInstanceId;

    @Comment("流程执行实例Id")
    @Column(name = "EXECUTIONID", length = 50, nullable = false)
    private String executionId;

    @Type(type = "numeric_boolean")
    @ColumnDefault("0")
    @Comment("是否是对子流程的催办信息")
    @Column(name = "ISSUB")
    private boolean isSub;

    @Comment("催办人员唯一标识")
    @Column(name = "USERID", length = 50)
    private String userId;

    @Comment("催办人员姓名")
    @Column(name = "USERNAME", length = 20)
    private String userName;

    @Comment("催办内容")
    @Column(name = "MSGCONTENT", length = 1000, nullable = false)
    private String msgContent;

    @Comment("创建时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATETIME")
    private Date createTime;
}
