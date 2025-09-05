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
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

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
public class Item implements Serializable {

    private static final long serialVersionUID = -2923177835926495218L;

    @Id
    @Comment("主键")
    @Column(name = "ID", length = 50, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("事项名称")
    @Column(name = "NAME", length = 200, nullable = false)
    private String name;

    @Comment("事项图标")
    @Column(name = "ICONID", length = 200)
    private String iconId;

    @Lob
    @Comment("图标内容")
    @Column(name = "ICONDATA")
    private String iconData;

    /**
     * {@link net.risesoft.enums.ItemTypeEnum}
     */
    @Comment("事项类型")
    @Column(name = "TYPE", length = 50)
    private String type;

    @Comment("事项管理员")
    @Column(name = "NATURE", length = 200)
    private String nature;

    @Comment("创建人id")
    @Column(name = "CREATERID")
    private String createrId;

    @Comment("创建人姓名")
    @Column(name = "CREATERNAME")
    private String createrName;

    @Comment("创建时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATEDATE")
    private Date createDate;

    @Comment("修改人id")
    @Column(name = "REVISERID", length = 50)
    private String reviserId;

    @Comment("修改人姓名")
    @Column(name = "REVISERNAME", length = 50)
    private String reviserName;

    @Comment("修改时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "REVISEDATE")
    private Date reviseDate = new Date();

    @Comment("系统级别")
    @Column(name = "SYSLEVEL", length = 50)
    private String sysLevel;

    @Comment("工作流GUID")
    @Column(name = "WORKFLOWGUID", length = 100)
    private String workflowGuid;

    @Comment("是否对接")
    @Column(name = "ISDOCKING", length = 50)
    private String isDocking;

    @Comment("对接外部系统标识")
    @Column(name = "DOCKINGSYSTEM", length = 50)
    private String dockingSystem;

    @Comment("对接事项id")
    @Column(name = "DOCKINGITEMID", length = 50)
    private String dockingItemId;

    @Comment("系统名称")
    @Column(name = "SYSTEMNAME", length = 50)
    private String systemName;

    @Comment("应用url")
    @Column(name = "APPURL", length = 200)
    private String appUrl;

    @Type(type = "numeric_boolean")
    @Comment("是否可定制事项")
    @Column(name = "CUSTOMITEM", nullable = false)
    @ColumnDefault("0")
    private Boolean customItem = false;

    @Type(type = "numeric_boolean")
    @Comment("显示提交按钮")
    @Column(name = "SHOWSUBMITBUTTON", nullable = false)
    @ColumnDefault("0")
    private boolean showSubmitButton = false;

    @Comment("排序")
    @Column(name = "TABINDEX")
    private Integer tabIndex;
}