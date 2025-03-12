package net.risesoft.entity;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
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
@Table(name = "FF_TASK_RELATED",
        indexes = {@Index(name = "ff_task_related_001_taskId", columnList = "taskId")})
@Comment("任务相关信息")
public class TaskRelated implements Serializable {

    private static final long serialVersionUID = 879932521397158651L;

    @Id
    @Comment("主键")
    @Column(name = "ID", length = 38)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("流程序列号")
    @Column(name = "PROCESSSERIALNUMBER", length = 50, nullable = false)
    private String processSerialNumber;

    @Comment("流程实例id")
    @Column(name = "PROCESSINSTANCEID", length = 50, nullable = false)
    private String processInstanceId;

    @Comment("流程执行实例Id")
    @Column(name = "EXECUTIONID", length = 50, nullable = false)
    private String executionId;

    @Convert(converter = org.hibernate.type.NumericBooleanConverter.class)
    @ColumnDefault("0")
    @Comment("是否子流程的节点任务")
    @Column(name = "SUB")
    private boolean sub;

    @Comment("任务唯一标示")
    @Column(name = "TASKID", length = 50, nullable = false)
    private String taskId;

    @Comment("信息类型")
    @Column(name = "INFOTYPE", length = 20, nullable = false)
    private String infoType;

    @Comment("消息内容")
    @Column(name = "MSGCONTENT", length = 500)
    private String msgContent;

    @Comment("发送人id")
    @Column(name = "SENDERID", length = 50)
    private String senderId;

    @Comment("发送人名称")
    @Column(name = "SENDERNAME", length = 20)
    private String senderName;

    @Comment("创建时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATETIME", nullable = false)
    private Date createTime;

    @Comment("修改时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "MODIFYTIME", nullable = false)
    private Date modifyTime;

    @Comment("阅读时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "READTIME")
    private Date readTime;
}
