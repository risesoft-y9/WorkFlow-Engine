package net.risesoft.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.Transient;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

/**
 * @author qinman
 * @date 2024/11/07
 */
@NoArgsConstructor
@Data
@Entity
@Table(name = "FF_ATTACHMENT_PAPER")
@org.hibernate.annotations.Table(comment = "纸质附件信息表", appliesTo = "FF_ATTACHMENT_PAPER")
public class PaperAttachment implements Serializable {

    private static final long serialVersionUID = 8483283215824280471L;

    @Id
    @Comment("主键")
    @Column(name = "ID", length = 38)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("流程实例编号")
    @Column(name = "PROCESSSERIALNUMBER", length = 50)
    private String processSerialNumber;

    @Comment("文件名称")
    @Column(name = "FILENAME", length = 255)
    private String name;

    @Comment("份数")
    @Column(name = "COUNT", length = 10)
    private Integer count;

    @Comment("页数")
    @Column(name = "PAGES", length = 10)
    private Integer pages;

    @Comment("密级")
    @Column(name = "MIJI", length = 20)
    private String miJi;

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
