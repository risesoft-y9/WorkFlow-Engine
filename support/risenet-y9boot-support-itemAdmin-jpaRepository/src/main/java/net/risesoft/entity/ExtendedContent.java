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
@Table(name = "FF_Extended_Content")
@org.hibernate.annotations.Table(comment = "扩展内容", appliesTo = "FF_Extended_Content")
public class ExtendedContent implements Serializable {

    private static final long serialVersionUID = 8399151618865623680L;

    @Id
    @org.hibernate.annotations.Comment("主键")
    @Column(name = "ID", length = 38, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @org.hibernate.annotations.Comment("租户id")
    @Column(name = "TENANTID", length = 50, nullable = false)
    private String tenantId;

    /**
     * 表单中内容框元素id，以此来区分类型，来存储不同的内容，如：每个人任务都需要填写处理结果，处理要求等不同内容
     */
    @org.hibernate.annotations.Comment("内容标识")
    @Column(name = "CATEGORY", length = 50, nullable = false)
    private String category;

    @org.hibernate.annotations.Comment("内容")
    @Column(name = "CONTENT", length = 4000, nullable = false)
    private String content;

    @org.hibernate.annotations.Comment("创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATEDATE")
    private Date createDate;

    @org.hibernate.annotations.Comment("修改时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "MODIFYDATE")
    private Date modifyDate;

    @org.hibernate.annotations.Comment("人员id")
    @Column(name = "USERID", length = 38, nullable = false)
    private String userId;

    @org.hibernate.annotations.Comment("人员姓名")
    @Column(name = "USERNAME", length = 50)
    private String userName;

    @org.hibernate.annotations.Comment("部门id")
    @Column(name = "DEPARTMENTID", length = 38)
    private String departmentId;

    @org.hibernate.annotations.Comment("部门名称")
    @Column(name = "DEPARTMENTNAME", length = 50)
    private String departmentName;

    @org.hibernate.annotations.Comment("流程实例id")
    @Column(name = "PROCESSINSTANCEID", length = 64)
    private String processInstanceId;

    @org.hibernate.annotations.Comment("任务id")
    @Column(name = "TASKID", length = 64)
    private String taskId;

    @org.hibernate.annotations.Comment("流程编号")
    @Column(name = "PROCESSSERIALNUMBER", length = 38, nullable = false)
    private String processSerialNumber;

    @org.hibernate.annotations.Comment("序号")
    @Column(name = "TABINDEX", length = 11)
    private Integer tabIndex;
}
