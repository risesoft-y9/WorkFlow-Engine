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
@Table(name = "FF_DOCUMENT_WORD")
@Comment("正文信息表")
public class DocumentWord implements Serializable {

    private static final long serialVersionUID = -980880040385744365L;
    /**
     * 主键
     */
    @Id
    @Comment("主键")
    @Column(name = "ID", length = 38, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("文件仓库Id")
    @Column(name = "FILESTOREID", length = 50)
    private String fileStoreId;

    /**
     * 文件类型
     */
    @Comment("文件类型")
    @Column(name = "FILETYPE", length = 50)
    private String fileType;

    /**
     * 包括文件名+后缀
     */
    @Comment("文件名")
    @Column(name = "FILENAME", length = 200)
    private String fileName;

    /**
     * 文件大小
     */
    @Comment("文件大小")
    @Column(name = "FILESIZE", length = 20)
    private String fileSize;

    /**
     * 上传人员id
     */
    @Comment("上传人员id")
    @Column(name = "USERID", length = 38, nullable = false)
    private String userId;

    /**
     * 上传人员名称
     */
    @Comment("上传人员名称")
    @Column(name = "USERNAME", length = 38)
    private String userName;

    /**
     * 数据类型，1：word，2：套红word，3：pdf，4：odf
     */
    @Comment("数据类型")
    @Column(name = "TYPE", length = 10)
    private Integer type;

    /**
     * 保存时间
     */
    @Comment("保存时间")
    @Column(name = "SAVEDATE", length = 50)
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
     * 正文类别,1:办文要报，2：发文稿纸，3：发文单,4：签注意见
     */
    @Comment("正文类别")
    @Column(name = "WORDTYPE", length = 10)
    private String wordType;

}
