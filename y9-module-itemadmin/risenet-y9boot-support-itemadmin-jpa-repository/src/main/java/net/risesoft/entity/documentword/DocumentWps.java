package net.risesoft.entity.documentword;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;

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
@Table(name = "FF_DOCUMENTWPS")
@Comment("正文信息表")
public class DocumentWps implements Serializable {

    private static final long serialVersionUID = -8485514305692770264L;

    /**
     * 主键
     */
    @Id
    @Comment("主键")
    @Column(name = "ID", length = 38, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("文件Id")
    @Column(name = "FILEID", length = 50, nullable = false)
    private String fileId;

    @Comment("卷Id")
    @Column(name = "VOLUMEID", length = 50, nullable = false)
    private String volumeId;

    /**
     * 租户Id
     */
    @Comment("租户Id")
    @Column(name = "TENANTID", length = 50, nullable = false)
    private String tenantId;

    /**
     * 文件类型
     */
    @Comment("文件类型")
    @Column(name = "FILETYPE", length = 50, nullable = true)
    private String fileType;

    /**
     * 包括文件名+后缀
     */
    @Comment("文件名")
    @Column(name = "FILENAME", length = 1000, nullable = true)
    private String fileName;

    /**
     * 上传人员id
     */
    @Comment("上传人员id")
    @Column(name = "USERID", length = 38, nullable = false)
    private String userId;

    /**
     * 是否套红、1为套红word，0为word
     */
    @Comment("是否套红")
    @Column(name = "ISTAOHONG", length = 10)
    private String istaohong;

    /**
     * 保存时间
     */
    @Comment("保存时间")
    @Column(name = "SAVEDATE", length = 100)
    private String saveDate;

    /**
     * 流程序号
     */
    @Comment("流程编号")
    @Column(name = "PROCESSSERIALNUMBER", length = 38)
    private String processSerialNumber;

    /**
     * 流程实例id
     */
    @Comment("流程实例id")
    @Column(name = "PROCESSINSTANCEID", length = 64)
    private String processInstanceId;

    /**
     * 是否有内容，1为是，0为否
     */
    @Comment("是否有内容")
    @Column(name = "HASCONTENT", length = 2)
    private String hasContent = "0";

}
