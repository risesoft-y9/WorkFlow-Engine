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
@Table(name = "FF_SIGN_DEPT_INFO")
@org.hibernate.annotations.Table(comment = "会签信息表", appliesTo = "FF_SIGN_DEPT_INFO")
public class SignDeptInfo implements Serializable {

    private static final long serialVersionUID = 869230755677925186L;

    @Id
    @Comment("主键")
    @Column(name = "ID", length = 50, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("部门Id")
    @Column(name = "DEPTID", length = 50)
    private String deptId;

    @Comment("部门名称")
    @Column(name = "DEPTNAME", length = 100)
    private String deptName;

    @Comment("显示部门Id")
    @Column(name = "DISPLAY_DEPTID", length = 50)
    private String displayDeptId;

    @Comment("显示部门名称")
    @Column(name = "DISPLAY_DEPTNAME", length = 100)
    private String displayDeptName;

    @Comment("流程编号")
    @Column(name = "PROCESSSERIALNUMBER", length = 50, nullable = false)
    private String processSerialNumber;

    @Comment("签字人姓名")
    @Column(name = "USERNAME", length = 50)
    private String userName;

    @Comment("签字日期")
    @Column(name = "SIGNDATE", length = 20)
    private String signDate;

    @Comment("单位类型（0：委内，1：委外，2：联合发文）")
    @Column(name = "DEPTTYPE", length = 20)
    private String deptType;

    @Comment("录入时间")
    @Column(name = "RECORDTIME", length = 50)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date recordTime;

    @Comment("录入人")
    @Column(name = "INPUTPERSON", length = 50)
    private String inputPerson;

    @Comment("录入人id")
    @Column(name = "INPUTPERSONID", length = 50)
    private String inputPersonId;

    @Comment("部门排序")
    @Column(name = "ORDERINDEX", length = 10)
    private Integer orderIndex;

}