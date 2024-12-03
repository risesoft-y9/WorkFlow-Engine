package net.risesoft.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

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
@org.hibernate.annotations.Table(comment = "任务相关信息", appliesTo = "FF_TASK_RELATED")
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
