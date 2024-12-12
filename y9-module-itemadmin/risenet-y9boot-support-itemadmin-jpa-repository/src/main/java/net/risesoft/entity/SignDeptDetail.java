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
import org.springframework.format.annotation.DateTimeFormat;

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
@Table(name = "FF_SIGN_DEPT_DETAIL")
@org.hibernate.annotations.Table(comment = "会签部门详情", appliesTo = "FF_SIGN_DEPT_DETAIL")
public class SignDeptDetail implements Serializable {

    private static final long serialVersionUID = -832654023448013336L;

    @Id
    @Comment("主键")
    @Column(name = "ID", length = 50, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("流程序列号")
    @Column(name = "PROCESSSERIALNUMBER", length = 50, nullable = false)
    private String processSerialNumber;

    @Comment("流程实例Id")
    @Column(name = "PROCESSINSTANCEID", length = 50, nullable = false)
    private String processInstanceId;

    @Comment("流程执行实例Id")
    @Column(name = "EXECUTIONID", length = 50, nullable = false)
    private String executionId;

    @Comment("任务Id")
    @Column(name = "TASKID", length = 50, nullable = false)
    private String taskId;

    @Comment("会签单位唯一标示")
    @Column(name = "DEPTID", length = 50)
    private String deptId;

    @Comment("会签单位名称")
    @Column(name = "DEPTNAME", length = 100)
    private String deptName;

    @Comment("签注人")
    @Column(name = "USERNAME", length = 20)
    private String userName;

    @Comment("签注人电话")
    @Column(name = "MOBILE", length = 20)
    private String mobile;

    @Comment("正文文件id")
    @Column(name = "FILESTOREID", length = 50)
    private String fileStoreId;

    @Comment("单位负责人")
    @Column(name = "DEPTMANAGER", length = 50)
    private String deptManager;

    /** 1是在办、2是正常办结、3是退回办结、4是减签 */
    @Comment("状态")
    @Column(name = "STATUS", length = 2)
    private Integer status;

    @Comment("是否是新的")
    @Column(name = "NEWED", length = 2)
    private boolean newed;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    @Comment("生成时间")
    @Column(name = "CREATETIME", length = 50)
    private Date createTime;
}