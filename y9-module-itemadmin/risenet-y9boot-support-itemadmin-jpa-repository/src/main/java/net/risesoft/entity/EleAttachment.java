package net.risesoft.entity;

import java.io.Serializable;

import javax.persistence.*;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qinman
 * @date 2024/11/11
 */
@NoArgsConstructor
@Data
@Entity
@Table(name = "FF_ATTACHMENT_ELE")
@org.hibernate.annotations.Table(comment = "电子附件信息表", appliesTo = "FF_ATTACHMENT_ELE")
public class EleAttachment implements Serializable {

    private static final long serialVersionUID = -6828867920921527416L;

    @Id
    @Comment("主键")
    @Column(name = "ID", length = 38)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("流程实例编号")
    @Column(name = "PROCESSSERIALNUMBER", length = 50)
    private String processSerialNumber;

    @Comment("文件仓库Id")
    @Column(name = "FILESTOREID", length = 50)
    private String fileStoreId;

    @Comment("文件名称")
    @Column(name = "FILENAME", length = 255)
    private String name;

    @Comment("密级")
    @Column(name = "MIJI", length = 20)
    private String miJi;

    @Comment("附件类型")
    @Column(name = "ATTACHMENTTYPE", length = 20)
    private String attachmentType;

    @Comment("上传时间")
    @Column(name = "UPLOADTIME", length = 100)
    private String uploadTime;

    @Comment("上传人")
    @Column(name = "PERSONNAME", length = 50)
    private String personName;

    @Comment("上传人员Id")
    @Column(name = "PERSONID", length = 50)
    private String personId;

    @Comment("文件索引")
    @Column(name = "TABINDEX", length = 10)
    private Integer tabIndex;
}
