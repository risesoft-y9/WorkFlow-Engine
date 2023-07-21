package net.risesoft.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 协作状态实例表
 */
@Entity
@Table(name = "FF_PROCESSINSTANCE")
@org.hibernate.annotations.Table(comment = "协作状态实例表", appliesTo = "FF_PROCESSINSTANCE")
@NoArgsConstructor
@Data
public class ProcessInstance implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -5997531254893797966L;

    @Id
    @Column(name = "ID", length = 38)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @org.hibernate.annotations.Comment("流程实例id")
    @Column(name = "PROCESSINSTANCEID", length = 50)
    private String processInstanceId;

    @org.hibernate.annotations.Comment("流程编号")
    @Column(name = "PROCESSSERIALNUMBER", length = 50)
    private String processSerialNumber;

    @org.hibernate.annotations.Comment("系统英文名称")
    @Column(name = "SYSTEMNAME", length = 100)
    private String systemName;

    @org.hibernate.annotations.Comment("系统中文名称")
    @Column(name = "SYSTEMCNNAME", length = 100)
    private String systemCnName;

    @org.hibernate.annotations.Comment("事项id")
    @Column(name = "ITEMID", length = 50)
    private String itemId;

    @org.hibernate.annotations.Comment("应用名称")
    @Column(name = "APPNAME", length = 50)
    private String appName;

    @org.hibernate.annotations.Comment("应用中文名称")
    @Column(name = "APPCNNAME", length = 100)
    private String appCnName;

    @org.hibernate.annotations.Comment("开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "STARTTIME")
    private Date startTime;

    @org.hibernate.annotations.Comment("结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ENDTIME")
    private Date endTime;

    @org.hibernate.annotations.Comment("编号")
    @Column(name = "SERIALNUMBER", length = 50)
    private String serialNumber;

    @org.hibernate.annotations.Comment("文件标题")
    @Column(name = "TITLE", length = 1000)
    private String title;

    @org.hibernate.annotations.Comment("发起人")
    @Column(name = "USERNAME", length = 100)
    private String userName;

    @org.hibernate.annotations.Comment("详情链接")
    @Column(name = "URL", length = 200)
    private String url;

    @org.hibernate.annotations.Comment("参与人id")
    @Lob
    @Column(name = "ASSIGNEE")
    private String assignee;// 参与人id ,人员id:科室id

    @org.hibernate.annotations.Comment("删除标记")
    @Column(name = "ISDELETED", length = 10)
    private Integer isDeleted = 0;// 软删除标记 1：删除

}
