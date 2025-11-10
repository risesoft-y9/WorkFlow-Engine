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

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.entity.base.ItemAdminBaseEntity;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@NoArgsConstructor
@Data
@Entity
@Table(name = "FF_REMINDER")
@org.hibernate.annotations.Table(comment = "催办信息表", appliesTo = "FF_REMINDER")
public class Reminder extends ItemAdminBaseEntity implements Serializable {

    private static final long serialVersionUID = 879932521397158651L;

    @Id
    @Comment("主键")
    @Column(name = "ID", length = 38)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("租户Id")
    @Column(name = "TENANTID", length = 50, nullable = false)
    private String tenantId;

    @Comment("流程实例id")
    @Column(name = "PROCINSTID", length = 64, nullable = false)
    private String procInstId;

    @Comment("任务id")
    @Column(name = "TASKID", length = 64, nullable = false)
    private String taskId;

    @Comment("发送人id")
    @Column(name = "SENDERID", length = 38, nullable = false)
    private String senderId;

    @Comment("发送人名称")
    @Column(name = "SENDERNAME", length = 50, nullable = false)
    private String senderName;

    @Comment("消息内容")
    @Column(name = "MSGCONTENT", length = 1000, nullable = false)
    private String msgContent;

    @Comment("阅读时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "READTIME")
    private Date readTime;
}
