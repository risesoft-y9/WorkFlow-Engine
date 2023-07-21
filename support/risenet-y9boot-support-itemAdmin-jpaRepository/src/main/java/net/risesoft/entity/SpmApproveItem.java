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

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonFormat;

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
@Table(name = "FF_APPROVEITEM")
@org.hibernate.annotations.Table(comment = "事项定义信息表", appliesTo = "FF_APPROVEITEM")
public class SpmApproveItem implements Serializable {

    private static final long serialVersionUID = -2923177835926495218L;

    @Id
    @org.hibernate.annotations.Comment("主键")
    @Column(name = "ID", length = 50, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @org.hibernate.annotations.Comment("事项名称")
    @Column(name = "NAME", length = 200, nullable = false)
    private String name;

    @org.hibernate.annotations.Comment("事项图标")
    @Column(name = "ICONID", length = 200)
    private String iconId;

    @Lob
    @org.hibernate.annotations.Comment("图标内容")
    @Column(name = "ICONDATA")
    private String iconData;

    @org.hibernate.annotations.Comment("部门id")
    @Column(name = "DEPARTMENTID")
    private String departmentId;

    @org.hibernate.annotations.Comment("部门名称")
    @Column(name = "DEPARTMENTNAME")
    private String departmentName;

    @org.hibernate.annotations.Comment("事项类型")
    @Column(name = "TYPE", length = 50)
    private String type;

    @org.hibernate.annotations.Comment("事项责任制")
    @Column(name = "ACCOUNTABILITY", length = 200)
    private String accountability;

    @org.hibernate.annotations.Comment("事项性质")
    @Column(name = "NATURE", length = 200)
    private String nature;

    @org.hibernate.annotations.Comment("首办角色id")
    @Column(name = "STARTERID", length = 50)
    private String starterId;

    @org.hibernate.annotations.Comment("首办角色名称")
    @Column(name = "STARTER", length = 50)
    private String starter;

    @org.hibernate.annotations.Comment("创建人id")
    @Column(name = "CREATERID")
    private String createrId;

    @org.hibernate.annotations.Comment("创建人姓名")
    @Column(name = "CREATERNAME")
    private String createrName;

    @org.hibernate.annotations.Comment("创建时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATEDATE")
    private Date createDate;

    @org.hibernate.annotations.Comment("修改人id")
    @Column(name = "REVISERID", length = 50)
    private String reviserId;

    @org.hibernate.annotations.Comment("修改人姓名")
    @Column(name = "REVISERNAME", length = 50)
    private String reviserName;

    @org.hibernate.annotations.Comment("修改时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "REVISEDATE")
    private Date reviseDate = new Date();

    @org.hibernate.annotations.Comment("系统级别")
    @Column(name = "SYSLEVEL", length = 50)
    private String sysLevel;

    @org.hibernate.annotations.Comment("法定期限")
    @Column(name = "LEGALLIMIT")
    private Integer legalLimit;

    @org.hibernate.annotations.Comment("承诺时限")
    @Column(name = "EXPIRED")
    private Integer expired;

    @org.hibernate.annotations.Comment("工作流GUID")
    @Column(name = "WORKFLOWGUID", length = 100)
    private String workflowGuid;

    @org.hibernate.annotations.Comment("基本信息表单id")
    @Column(name = "BASICFORMID", length = 50)
    private String basicformId;

    @org.hibernate.annotations.Comment("办理表单id")
    @Column(name = "HANDELFORMID", length = 50)
    private String handelformId;

    @org.hibernate.annotations.Comment("是否网上申办")
    @Column(name = "ISONLINE", length = 50)
    private String isOnline;

    @org.hibernate.annotations.Comment("是否对接")
    @Column(name = "ISDOCKING", length = 50)
    private String isDocking;

    @org.hibernate.annotations.Comment("对接外部系统标识")
    @Column(name = "DOCKINGSYSTEM", length = 50)
    private String dockingSystem;

    @org.hibernate.annotations.Comment("对接事项id")
    @Column(name = "DOCKINGITEMID", length = 50)
    private String dockingItemId;

    @org.hibernate.annotations.Comment("系统名称")
    @Column(name = "SYSTEMNAME", length = 50)
    private String systemName;

    @org.hibernate.annotations.Comment("使用表单类型")
    @Column(name = "FORMTYPE")
    private Integer formType = 2;

    @org.hibernate.annotations.Comment("应用url")
    @Column(name = "APPURL", length = 200)
    private String appUrl;

    @org.hibernate.annotations.Comment("统一待办url前缀")
    @Column(name = "TODOTASKURLPREFIX", length = 200)
    private String todoTaskUrlPrefix;

    @org.hibernate.annotations.Comment("是否可定制事项")
    @Column(name = "CUSTOMITEM", nullable = false)
    @ColumnDefault("0")
    private Boolean customItem = false;
}