package net.risesoft.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author zhangchongjie
 * @date 2024/05/23
 */
@NoArgsConstructor
@Data
@Entity
@Table(name = "FF_MERGE_FILE")
@org.hibernate.annotations.Table(comment = "合并文件信息表", appliesTo = "FF_MERGE_FILE")
public class MergeFile implements Serializable {

    private static final long serialVersionUID = 804566467206254844L;

    @Id
    @Comment("主键")
    @Column(name = "ID", length = 50, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("文件名称")
    @Column(name = "FILENAME", length = 50)
    private String fileName;

    @Comment("源文件id,版式文件的源文件id")
    @Column(name = "SOURCEFILEID", length = 50)
    private String sourceFileId;

    @Comment("文件类型,1为合并文件,2为合并版式文件")
    @Column(name = "FILETYPE", length = 10)
    private String fileType;

    @Comment("列表类型,1为附件合并,2为文件合并")
    @Column(name = "LISTTYPE", length = 10)
    private String listType;

    @Comment("文件仓库Id")
    @Column(name = "FILESTOREID", length = 50)
    private String fileStoreId;

    @Comment("流程实例编号")
    @Column(name = "PROCESSSERIALNUMBER", length = 50)
    private String processSerialNumber;

    @Comment("合并人")
    @Column(name = "PERSONNAME", length = 100)
    private String personName;

    @Comment("合并人员Id")
    @Column(name = "PERSONID", length = 50)
    private String personId;

    @Comment("合并时间")
    @Column(name = "CREATETIME", length = 50)
    private String createTime;

}
