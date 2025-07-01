package net.risesoft.entity;

import java.io.Serializable;

import javax.persistence.*;

import org.hibernate.annotations.Comment;
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

    @Comment("岗位id")
    @Column(name = "POSITIONID", length = 50)
    private String positionId;

    @Transient
    private String positionName;

    @Comment("文件描述")
    @Column(name = "DESCRIBES", length = 255)
    private String describes;

    @Comment("文件索引")
    @Column(name = "TABINDEX", length = 10)
    private Integer tabIndex;

    @Comment("软删除标记")
    @Column
    private Integer deleted = 0;

    @Comment("删除时间")
    @Column(name = "DELETETIME", length = 100)
    private String deleteTime;

    @Comment("删除操作的人员id")
    @Column(name = "DETELEUSERID", length = 38)
    private String deteleUserId = "";

    @Column(name = "FULLPATH", length = 255)
    @Comment("绝对路径")
    private String fullPath;

    @Column(name = "REALFILENAME", length = 100)
    @Comment("存放的文件名称")
    private String realFileName;

    @Transient
    private Integer serialNumber;

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
