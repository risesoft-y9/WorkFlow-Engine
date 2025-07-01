package net.risesoft.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Comment;
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
@Table(name = "FF_ProcessTrack")
@org.hibernate.annotations.Table(comment = "历程信息表", appliesTo = "FF_ProcessTrack")
public class ProcessTrack implements Serializable {

    private static final long serialVersionUID = -4491937506668447623L;

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
     * 流程实例Id
     */
    @Comment("流程实例Id")
    @Column(name = "PROCESSINSTANCEID", length = 50, nullable = false)
    private String processInstanceId;

    /**
     * 任务节点Id
     */
    @Comment("任务节点Id")
    @Column(name = "TASKID", length = 50, nullable = false)
    private String taskId;

    /**
     * 发送人/操作人
     */
    @Comment("发送人")
    @Column(name = "SENDERNAME", nullable = false)
    private String senderName;

    /**
     * 接收人
     */
    @Comment("接收人")
    @Column(name = "RECEIVERNAME", length = 50)
    private String receiverName;

    /**
     * 任务节点名称
     */
    @Comment("任务节点名称")
    @Column(name = "TASKDEFNAME", length = 50)
    private String taskDefName;

    /**
     * 是否有抄送
     */
    @Transient
    private Boolean isChaoSong;

    /**
     * 意见
     */
    @Transient
    private String opinion;

    /**
     * 正文版本
     */
    @Transient
    private Integer docVersion;

    /**
     * 开始时间
     */
    @Comment("开始时间")
    @Column(name = "STARTTIME", length = 50)
    private String startTime;

    /**
     * 结束时间
     */
    @Comment("结束时间")
    @Column(name = "ENDTIME", length = 50)
    private String endTime;

    /**
     * 办理用时
     */
    @Transient
    private String handlingTime;

    /**
     * 描述
     */
    @Comment("描述")
    @Column(name = "DESCRIBED", length = 500)
    private String described;

}
