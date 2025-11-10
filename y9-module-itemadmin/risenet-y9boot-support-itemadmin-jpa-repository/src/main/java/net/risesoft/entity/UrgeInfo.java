package net.risesoft.entity;

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
 * @date 2024/12/24
 */
@NoArgsConstructor
@Data
@Entity
@Comment("催办信息表")
@Table(name = "FF_URGEINFO", indexes = {@Index(name = "ff_urgeinfo_index_001", columnList = "processSerialNumber")})
public class UrgeInfo extends ItemAdminBaseEntity implements Serializable {

    private static final long serialVersionUID = -4825747252393459230L;

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

    @Convert(converter = org.hibernate.type.NumericBooleanConverter.class)
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
}
