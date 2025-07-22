package net.risesoft.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.enums.ChaoSongStatusEnum;
import net.risesoft.persistence.ItemEnumConverter;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@NoArgsConstructor
@Data
@Entity
@Table(name = "FF_CHAOSONG")
@org.hibernate.annotations.Table(comment = "抄送信息表", appliesTo = "FF_CHAOSONG")
public class ChaoSong implements Serializable {

    private static final long serialVersionUID = -4235779237483037821L;

    /**
     * 主键
     */
    @Id
    @Comment("主键")
    @Column(name = "ID", length = 50, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    /**
     * 租户Id
     */
    @Comment("租户Id")
    @Column(name = "TENANTID", length = 50, nullable = false)
    private String tenantId;

    /**
     * 抄送的标题
     */
    @Comment("抄送的标题")
    @Column(name = "TITLE", length = 1000, nullable = false)
    private String title;

    /**
     * 抄送节点的任务Id
     */
    @Comment("抄送节点的任务Id")
    @Column(name = "TASKID", length = 50)
    private String taskId;

    /**
     * 抄送的流程实例
     */
    @Comment("抄送的流程实例")
    @Column(name = "PROCESSINSTANCEID", length = 50, nullable = false)
    private String processInstanceId;

    /**
     * 抄送的流程编号
     */
    @Comment("抄送的流程实例")
    @Column(name = "PROCESSSERIALNUMBER", length = 50)
    private String processSerialNumber;

    /**
     * 抄送目标人员名称
     */
    @Comment("抄送目标人员名称")
    @Column(name = "USERNAME", length = 50, nullable = false)
    private String userName;

    /**
     * 抄送目标人员Id
     */
    @Comment("抄送目标人员Id")
    @Column(name = "USERID", length = 100, nullable = false)
    private String userId;

    /**
     * 抄送目标人员部门名称
     */
    @Comment("抄送目标人员部门名称")
    @Column(name = "USERDEPTNAME", length = 150, nullable = false)
    private String userDeptName;

    /**
     * 抄送目标人员部门Id
     */
    @Comment("抄送目标人员部门Id")
    @Column(name = "USERDEPTID", length = 100, nullable = false)
    private String userDeptId;

    /**
     * 操作人的名称
     */
    @Comment("操作人名称")
    @Column(name = "SENDERNAME", length = 50)
    private String senderName;

    /**
     * 操作人的Id
     */
    @Comment("操作人Id")
    @Column(name = "SENDERID", length = 100)
    private String senderId;

    /**
     * 操作人员部门名称
     */
    @Comment("操作人员部门名称")
    @Column(name = "SENDDEPTNAME", length = 150)
    private String sendDeptName;

    /**
     * 操作人员部门Id
     */
    @Comment("操作人员部门Id")
    @Column(name = "SENDDEPTID", length = 100)
    private String sendDeptId;

    /**
     * 传阅的状态
     */
    @Comment("传阅状态")
    @Column(name = "STATUS", length = 2)
    @Convert(converter = ItemEnumConverter.ChaoSongStatusEnumConverter.class)
    private ChaoSongStatusEnum status = ChaoSongStatusEnum.NEW;

    /**
     * 抄送时间
     */
    @Comment("抄送时间")
    @Column(name = "CREATETIME", length = 100)
    private String createTime;

    /**
     * 抄送时间
     */
    @Comment("阅读时间")
    @Column(name = "READTIME", length = 100)
    private String readTime;

    @Comment("事项唯一标示")
    @Column(name = "ITEMID", length = 50, nullable = false)
    private String itemId;

    @Comment("系统英文名称")
    @Column(name = "SYSTEMNAME", length = 50, nullable = false)
    private String systemName;

    @Comment("事项名称")
    @Column(name = "ITEMNAME", length = 100, nullable = false)
    private String itemName;

    @Comment("意见状态")
    @Column(name = "opinionState", length = 10)
    private String opinionState;

    @Comment("知会意见")
    @Column(name = "opinionContent", length = 500)
    private String opinionContent;

    @Comment("知会群组")
    @Column(name = "opinionGroup", length = 38)
    private String opinionGroup;

    /**
     * 起件时间
     */
    @Transient
    private String startTime;

    @Transient
    private boolean ended;

    @Transient
    private String taskName;

    @Transient
    private String neibu;
}
