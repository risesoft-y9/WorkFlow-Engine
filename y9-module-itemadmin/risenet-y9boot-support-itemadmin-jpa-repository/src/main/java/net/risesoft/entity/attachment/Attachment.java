package net.risesoft.entity.attachment;

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
@Table(name = "FF_ATTACHMENT")
@Comment("附件信息表")
public class Attachment implements Serializable {
    private static final long serialVersionUID = 3241197746615642199L;

    @Id
    @Comment("主键")
    @Column(name = "ID", length = 38)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("文件仓库Id")
    @Column(name = "FILESTOREID", length = 50)
    private String fileStoreId;

    @Comment("流程实例编号")
    @Column(name = "PROCESSSERIALNUMBER", length = 50)
    private String processSerialNumber;

    @Comment("流程实例id")
    @Column(name = "PROCESSINSTANCEID", length = 38)
    private String processInstanceId;

    @Comment("任务id")
    @Column(name = "TASKID", length = 38)
    private String taskId;

    @Comment("文件名称")
    @Column(name = "FILENAME", length = 100)
    private String name;

    @Comment("附件来源")
    @Column(name = "FILESOURCE", length = 50)
    private String fileSource;

    @Comment("文件大小")
    @Column(name = "FILESIZE", length = 20)
    private String fileSize;

    @Comment("文件类型")
    @Column(name = "FILETYPE", length = 20)
    private String fileType;

    @Comment("上传时间")
    @Column(name = "UPLOADTIME", length = 100)
    private String uploadTime;

    @Comment("上传人")
    @Column(name = "PERSONNAME", length = 100)
    private String personName;

    @Comment("上传人员Id")
    @Column(name = "PERSONID", length = 50)
    private String personId;

    @Comment("上传人员部门Id")
    @Column(name = "DEPTID", length = 50)
    private String deptId;

    @Comment("上传人员部门名称")
    @Column(name = "DEPTNAME", length = 100)
    private String deptName;

    @Comment("文件描述")
    @Column(name = "DESCRIBES")
    private String describes;

    @Comment("文件索引")
    @Column(name = "TABINDEX", length = 10)
    private Integer tabIndex;

    @Column(name = "FIELDONE", length = 300)
    @Comment("备用字段一")
    private String fieldOne;

    @Column(name = "FIELDTWO", length = 300)
    @Comment("备用字段二")
    private String fieldTwo;

    @Column(name = "FIELDTHREE", length = 300)
    @Comment("备用字段三")
    private String fieldThree;

    @Column(name = "FIELDFOUR", length = 300)
    @Comment("备用字段四")
    private String fieldFour;

    @Column(name = "FIELDFIVE", length = 300)
    @Comment("备用字段五")
    private String fieldFive;

    @Column(name = "FIELDSIX", length = 300)
    @Comment("备用字段六")
    private String fieldSix;

}
