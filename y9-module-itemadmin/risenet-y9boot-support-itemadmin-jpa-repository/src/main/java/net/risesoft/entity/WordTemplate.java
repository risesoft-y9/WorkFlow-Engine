package net.risesoft.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonFormat;

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
@Table(name = "FF_WORDTEMPLATE")
@org.hibernate.annotations.Table(comment = "正文模板信息表", appliesTo = "FF_WORDTEMPLATE")
public class WordTemplate implements Serializable {

    private static final long serialVersionUID = -7420288864269881175L;

    @Id
    @Comment("主键")
    @Column(name = "ID")
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("委办局Id")
    @Column(name = "BUREAUID", length = 50)
    private String bureauId;

    /**
     * 文档名称
     */
    @Comment("文档名称")
    @Column(name = "FILENAME", length = 50)
    private String fileName;

    /**
     * 文档路径
     */
    @Comment("文档路径")
    @Column(name = "FILEPATH", length = 2000)
    private String filePath;

    /**
     * 文件字节数
     */
    @Comment("文件大小")
    @Column(name = "FILESIZE", length = 20)
    private String fileSize;

    /**
     * 上传时间
     */
    @Comment("上传时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPLOADTIME")
    private Date uploadTime;

    /**
     * 上传人Id
     */
    @Comment("上传人Id")
    @Column(name = "PERSONID", length = 50)
    private String personId;

    /**
     * 上传人
     */
    @Comment("上传人")
    @Column(name = "PERSONNAME", length = 100)
    private String personName;

    /**
     * 文件描述
     */
    @Comment("文件描述")
    @Column(name = "DESCRIBES", length = 1000)
    private String describes;

}
