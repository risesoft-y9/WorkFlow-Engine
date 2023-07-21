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
@Table(name = "FF_Reminder")
@org.hibernate.annotations.Table(comment = "催办信息表", appliesTo = "FF_Reminder")
public class Reminder implements Serializable {

    private static final long serialVersionUID = 879932521397158651L;

    @Id
    @org.hibernate.annotations.Comment("主键")
    @Column(name = "ID", length = 38)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @org.hibernate.annotations.Comment("租户Id")
    @Column(name = "TENANTID", length = 50, nullable = false)
    private String tenantId;

    @org.hibernate.annotations.Comment("催办发送类型")
    @Column(name = "REMINDER_SEND_TYPE", length = 50, nullable = false)
    private String reminderSendType;

    @org.hibernate.annotations.Comment("提醒类型")
    @Column(name = "REMINDER_MAKE_TYPE", length = 2, nullable = false)
    private Integer reminderMakeTyle;

    @org.hibernate.annotations.Comment("流程实例id")
    @Column(name = "PROCINSTID", length = 64, nullable = false)
    private String procInstId;

    @org.hibernate.annotations.Comment("任务id")
    @Column(name = "TASKID", length = 64, nullable = false)
    private String taskId;

    @org.hibernate.annotations.Comment("发送人id")
    @Column(name = "SENDERID", length = 38, nullable = false)
    private String senderId;

    @org.hibernate.annotations.Comment("发送人名称")
    @Column(name = "SENDERNAME", length = 50, nullable = false)
    private String senderName;

    @org.hibernate.annotations.Comment("消息内容")
    @Column(name = "MSGCONTENT", length = 1000, nullable = false)
    private String msgContent;

    @org.hibernate.annotations.Comment("创建时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATETIME", nullable = false)
    private Date createTime;

    @org.hibernate.annotations.Comment("修改时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "MODIFYTIME", nullable = false)
    private Date modifyTime;

    @org.hibernate.annotations.Comment("阅读时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "READTIME")
    private Date readTime;
}
