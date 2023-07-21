package net.risesoft.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.Transient;

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
@org.hibernate.annotations.Table(comment = "附件信息表", appliesTo = "FF_ATTACHMENT")
public class TransactionFile implements Serializable {
    private static final long serialVersionUID = 3241197746615642199L;

    @Id
    @org.hibernate.annotations.Comment("主键")
    @Column(name = "ID", length = 38)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @org.hibernate.annotations.Comment("流程实例编号")
    @Column(name = "PROCESSSERIALNUMBER", length = 50)
    private String processSerialNumber;

    @org.hibernate.annotations.Comment("文件仓库Id")
    @Column(name = "FILESTOREID", length = 50)
    private String fileStoreId;

    @org.hibernate.annotations.Comment("流程实例id")
    @Column(name = "PROCESSINSTANCEID", length = 38)
    private String processInstanceId;

    @org.hibernate.annotations.Comment("任务id")
    @Column(name = "TASKID", length = 38)
    private String taskId;

    @org.hibernate.annotations.Comment("文件名称")
    @Column(name = "FILENAME", length = 100)
    private String name;

    @org.hibernate.annotations.Comment("附件来源")
    @Column(name = "FILESOURCE", length = 50)
    private String fileSource;

    @org.hibernate.annotations.Comment("文件大小")
    @Column(name = "FILESIZE", length = 20)
    private String fileSize;

    @org.hibernate.annotations.Comment("文件类型")
    @Column(name = "FILETYPE", length = 20)
    private String fileType;

    @org.hibernate.annotations.Comment("上传时间")
    @Column(name = "UPLOADTIME", length = 100)
    private String uploadTime;

    @org.hibernate.annotations.Comment("上传人")
    @Column(name = "PERSONNAME", length = 100)
    private String personName;

    @org.hibernate.annotations.Comment("上传人员Id")
    @Column(name = "PERSONID", length = 50)
    private String personId;

    @org.hibernate.annotations.Comment("上传人员部门Id")
    @Column(name = "DEPTID", length = 50)
    private String deptId;

    @org.hibernate.annotations.Comment("上传人员部门名称")
    @Column(name = "DEPTNAME", length = 100)
    private String deptName;

    @org.hibernate.annotations.Comment("岗位id")
    @Column(name = "POSITIONID", length = 50)
    private String positionId;

    @Transient
    private String positionName;

    @org.hibernate.annotations.Comment("文件描述")
    @Column(name = "DESCRIBES", length = 255)
    private String describes;

    @org.hibernate.annotations.Comment("文件索引")
    @Column(name = "TABINDEX", length = 10)
    private Integer tabIndex;

    @org.hibernate.annotations.Comment("软删除标记")
    @Column
    private Integer deleted = 0;

    @org.hibernate.annotations.Comment("删除时间")
    @Column(name = "DELETETIME", length = 100)
    private String deleteTime;

    @org.hibernate.annotations.Comment("删除操作的人员id")
    @Column(name = "DETELEUSERID", length = 38)
    private String deteleUserId = "";

    @Column(name = "FULLPATH", length = 255)
    @org.hibernate.annotations.Comment("绝对路径")
    private String fullPath;

    @Column(name = "REALFILENAME", length = 100)
    @org.hibernate.annotations.Comment("存放的文件名称")
    private String realFileName;

    @Transient
    private Integer serialNumber;

}
