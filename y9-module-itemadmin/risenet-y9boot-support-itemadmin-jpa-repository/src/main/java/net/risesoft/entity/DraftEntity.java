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
import org.hibernate.annotations.Type;
import org.springframework.format.annotation.DateTimeFormat;

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
@Table(name = "FF_DRAFTENTITY")
@org.hibernate.annotations.Table(comment = "草稿信息表", appliesTo = "FF_DRAFTENTITY")
public class DraftEntity implements Serializable {

    private static final long serialVersionUID = 7730918053550120284L;

    @Id
    @Comment("主键")
    @Column(name = "ID", length = 38)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    /**
     * 事项id
     */
    @Comment("事项id")
    @Column(name = "ITEMID", length = 50)
    private String itemId;

    /**
     * 实例id
     */
    @Comment("实例id")
    @Column(name = "PROCESSINSTANCEID", length = 50)
    private String processInstanceId;

    /**
     * 流程定义key
     */
    @Comment("流程定义key")
    @Column(name = "PROCESSDEFINITIONKEY", length = 100)
    private String processDefinitionKey;

    /**
     * 流程编号，草稿编号
     */
    @Comment("流程编号")
    @Column(name = "PROCESSSERIALNUMBER", length = 50)
    private String processSerialNumber;

    /**
     * 编号
     */
    @Comment("编号")
    @Column(name = "DOCNUMBER", length = 50)
    private String docNumber;

    /**
     * 类型
     */
    @Comment("类型")
    @Column(name = "TYPE", length = 100)
    private String type;

    /**
     * 文件标题
     */
    @Comment("文件标题")
    @Column(name = "TITLE", length = 1500)
    private String title;

    /**
     * 创建人Id
     */
    @Comment("创建人Id")
    @Column(name = "CREATERID", length = 50)
    private String createrId;

    /**
     * 创建人
     */
    @Comment("创建人")
    @Column(name = "CREATER", length = 200)
    private String creater;

    /**
     * 紧急程度
     */
    @Comment("紧急程度")
    @Column(name = "URGENCY", length = 20)
    private String urgency;

    /**
     * 起草时间
     */
    @Comment("起草时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DRAFTTIME")
    private Date draftTime;

    /**
     * 系统名称
     */
    @Comment("系统名称")
    @Column(name = "SYSTEMNAME", length = 50)
    private String systemName;

    /**
     * 排序号
     */
    @Comment("排序号")
    @Column(name = "SERIALNUMBER", length = 11)
    private Integer serialNumber;

    /**
     * 是否删除
     */
    @Type(type = "numeric_boolean")
    @Comment("是否删除")
    @Column(name = "DELFLAG", length = 5)
    private boolean delFlag = false;

    /**
     * 是否内部文件
     */
    @Comment("是否内部文件")
    @Column(name = "NEIBU", length = 50)
    private String neibu;
}
