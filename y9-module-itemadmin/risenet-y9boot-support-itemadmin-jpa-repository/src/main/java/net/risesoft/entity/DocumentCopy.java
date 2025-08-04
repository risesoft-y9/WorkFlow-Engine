package net.risesoft.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.enums.DocumentCopyStatusEnum;
import net.risesoft.persistence.ItemEnumConverter;

/**
 * @author qinman
 * @date 2025/02/10
 */
@NoArgsConstructor
@Data
@Entity
@Table(name = "FF_DOCUMENT_COPY",
    indexes = {@Index(name = "ff_document_copy_001", columnList = "senderId"),
        @Index(name = "ff_document_copy_002", columnList = "opinionCopyId")})
@Comment("办件副本信息表")
public class DocumentCopy implements Serializable {

    private static final long serialVersionUID = 7926782499224720763L;

    @Id
    @Comment("主键")
    @Column(name = "ID", length = 50, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("传签意见唯一标示")
    @Column(name = "OPINIONCOPYID", length = 50, nullable = false)
    private String opinionCopyId;

    @Comment("抄送的流程实例")
    @Column(name = "PROCESSSERIALNUMBER", length = 50, nullable = false)
    private String processSerialNumber;

    @Comment("抄送的流程实例")
    @Column(name = "PROCESSINSTANCEID", length = 50, nullable = false)
    private String processInstanceId;

    @Comment("抄送目标人员名称")
    @Column(name = "USERNAME", length = 50, nullable = false)
    private String userName;

    @Comment("抄送目标人员Id")
    @Column(name = "USERID", length = 50, nullable = false)
    private String userId;

    @Comment("操作人名称")
    @Column(name = "SENDERNAME", length = 50)
    private String senderName;

    @Comment("操作人Id")
    @Column(name = "SENDERID", length = 100)
    private String senderId;

    @Comment("传阅状态")
    @Column(name = "STATUS", length = 2)
    @Convert(converter = ItemEnumConverter.DocumentCopyStatusEnumConverter.class)
    private DocumentCopyStatusEnum status = DocumentCopyStatusEnum.TODO_SIGN;

    @Comment("系统英文名称")
    @Column(name = "SYSTEMNAME", length = 50, nullable = false)
    private String systemName;

    @Comment("生成时间")
    @Column(name = "CREATETIME", length = 100)
    private String createTime;

    @Comment("更新时间")
    @Column(name = "UPDATETIME", length = 100)
    private String updateTime;
}
