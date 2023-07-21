package net.risesoft.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

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
@Table(name = "FF_ChaoSong")
@org.hibernate.annotations.Table(comment = "抄送信息表", appliesTo = "FF_ChaoSong")
public class ChaoSong implements Serializable {

    private static final long serialVersionUID = -4235779237483037821L;

    /**
     * 主键
     */
    @Id
    @org.hibernate.annotations.Comment("主键")
    @Column(name = "ID", length = 50, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    /**
     * 租户Id
     */
    @org.hibernate.annotations.Comment("租户Id")
    @Column(name = "TENANTID", length = 50, nullable = false)
    private String tenantId;

    /**
     * 抄送的标题
     */
    @org.hibernate.annotations.Comment("抄送的标题")
    @Column(name = "TITLE", length = 1000, nullable = false)
    private String title;

    /**
     * 抄送节点的任务Id
     */
    @org.hibernate.annotations.Comment("抄送节点的任务Id")
    @Column(name = "TASKID", length = 50)
    private String taskId;

    /**
     * 抄送的流程实例
     */
    @org.hibernate.annotations.Comment("抄送的流程实例")
    @Column(name = "PROCESSINSTANCEID", length = 50, nullable = false)
    private String processInstanceId;

    /**
     * 抄送的流程序列号
     */
    @org.hibernate.annotations.Comment("抄送的流程实例")
    @Column(name = "PROCESSSERIALNUMBER", length = 50)
    private String processSerialNumber;

    /**
     * 抄送目标人员名称
     */
    @org.hibernate.annotations.Comment("抄送目标人员名称")
    @Column(name = "USERNAME", length = 50, nullable = false)
    private String userName;

    /**
     * 抄送目标人员Id
     */
    @org.hibernate.annotations.Comment("抄送目标人员Id")
    @Column(name = "USERID", length = 100, nullable = false)
    private String userId;

    /**
     * 抄送目标人员部门名称
     */
    @org.hibernate.annotations.Comment("抄送目标人员部门名称")
    @Column(name = "USERDEPTNAME", length = 150, nullable = false)
    private String userDeptName;

    /**
     * 抄送目标人员部门Id
     */
    @org.hibernate.annotations.Comment("抄送目标人员部门Id")
    @Column(name = "USERDEPTID", length = 100, nullable = false)
    private String userDeptId;

    /**
     * 操作人的名称
     */
    @org.hibernate.annotations.Comment("操作人名称")
    @Column(name = "SENDERNAME", length = 50)
    private String senderName;

    /**
     * 操作人的Id
     */
    @org.hibernate.annotations.Comment("操作人Id")
    @Column(name = "SENDERID", length = 100)
    private String senderId;

    /**
     * 操作人员部门名称
     */
    @org.hibernate.annotations.Comment("操作人员部门名称")
    @Column(name = "SENDDEPTNAME", length = 150)
    private String sendDeptName;

    /**
     * 操作人员部门Id
     */
    @org.hibernate.annotations.Comment("操作人员部门Id")
    @Column(name = "SENDDEPTID", length = 100)
    private String sendDeptId;

    /**
     * 传阅的状态,0未阅,1已阅,2新件
     */
    @org.hibernate.annotations.Comment("传阅状态")
    @Column(name = "STATUS", length = 2)
    private Integer status = 0;

    /**
     * 抄送时间
     */
    @org.hibernate.annotations.Comment("抄送时间")
    @Column(name = "CREATETIME", length = 100)
    private String createTime;

    /**
     * 抄送时间
     */
    @org.hibernate.annotations.Comment("阅读时间")
    @Column(name = "READTIME", length = 100)
    private String readTime;

    @org.hibernate.annotations.Comment("事项唯一标示")
    @Column(name = "ITEMID", length = 50, nullable = false)
    private String itemId;

    @org.hibernate.annotations.Comment("系统英文名称")
    @Column(name = "SYSTEMNAME", length = 50, nullable = false)
    private String systemName;

    @org.hibernate.annotations.Comment("事项名称")
    @Column(name = "ITEMNAME", length = 100, nullable = false)
    private String itemName;

    @org.hibernate.annotations.Comment("意见状态")
    @Column(name = "opinionState", length = 10)
    private String opinionState;

    @org.hibernate.annotations.Comment("知会意见")
    @Column(name = "opinionContent", length = 500)
    private String opinionContent;

    @org.hibernate.annotations.Comment("知会群组")
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
