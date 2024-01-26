package net.risesoft.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

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
@Table(name = "FF_Remind_Instance")
@org.hibernate.annotations.Table(comment = "消息提醒实例表", appliesTo = "FF_Remind_Instance")
public class RemindInstance implements Serializable {

    private static final long serialVersionUID = 1614771486879038754L;

    @Id
    @Comment("主键")
    @Column(name = "ID", length = 38)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("租户Id")
    @Column(name = "TENANTID", length = 50, nullable = false)
    private String tenantId;

    @Comment("消息提醒类型")
    @Column(name = "REMIND_TYPE", length = 50, nullable = false)
    private String remindType;

    @Comment("流程实例id")
    @Column(name = "PROCESSINSTANCEID", length = 64, nullable = false)
    private String processInstanceId;

    @Lob
    @Comment("任务id")
    @Column(name = "TASKID")
    private String taskId;

    @Comment("节点到达任务key")
    @Column(name = "arriveTaskKey", length = 500)
    private String arriveTaskKey;

    @Comment("节点完成任务key")
    @Column(name = "completeTaskKey", length = 500)
    private String completeTaskKey;

    @Comment("人员id")
    @Column(name = "USERID", length = 38, nullable = false)
    private String userId;

    @Comment("创建时间")
    @Column(name = "CREATETIME", nullable = false)
    private String createTime;
}
