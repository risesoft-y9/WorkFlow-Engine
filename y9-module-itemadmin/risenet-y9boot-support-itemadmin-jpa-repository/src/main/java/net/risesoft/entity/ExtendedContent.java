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
@Table(name = "FF_EXTENDED_CONTENT")
@org.hibernate.annotations.Table(comment = "扩展内容", appliesTo = "FF_EXTENDED_CONTENT")
public class ExtendedContent implements Serializable {

    private static final long serialVersionUID = 8399151618865623680L;

    @Id
    @Comment("主键")
    @Column(name = "ID", length = 38, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("租户id")
    @Column(name = "TENANTID", length = 50, nullable = false)
    private String tenantId;

    /**
     * 表单中内容框元素id，以此来区分类型，来存储不同的内容，如：每个人任务都需要填写处理结果，处理要求等不同内容
     */
    @Comment("内容标识")
    @Column(name = "CATEGORY", length = 50, nullable = false)
    private String category;

    @Comment("内容")
    @Column(name = "CONTENT", length = 4000, nullable = false)
    private String content;

    @Comment("创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATEDATE")
    private Date createDate;

    @Comment("修改时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "MODIFYDATE")
    private Date modifyDate;

    @Comment("人员id")
    @Column(name = "USERID", length = 38, nullable = false)
    private String userId;

    @Comment("人员姓名")
    @Column(name = "USERNAME", length = 50)
    private String userName;

    @Comment("部门id")
    @Column(name = "DEPARTMENTID", length = 38)
    private String departmentId;

    @Comment("部门名称")
    @Column(name = "DEPARTMENTNAME", length = 50)
    private String departmentName;

    @Comment("流程实例id")
    @Column(name = "PROCESSINSTANCEID", length = 64)
    private String processInstanceId;

    @Comment("任务id")
    @Column(name = "TASKID", length = 64)
    private String taskId;

    @Comment("流程编号")
    @Column(name = "PROCESSSERIALNUMBER", length = 38, nullable = false)
    private String processSerialNumber;

    @Comment("序号")
    @Column(name = "TABINDEX", length = 11)
    private Integer tabIndex;
}
