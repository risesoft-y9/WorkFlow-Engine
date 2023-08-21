package net.risesoft.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;

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
@Table(name = "FF_RejectReason")
@Comment("退回收回原因表")
public class RejectReason implements Serializable {

    private static final long serialVersionUID = -9026235125209828600L;

    @Id
    @Comment("主键")
    @Column(name = "ID", length = 50, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("原因")
    @Column(name = "REASON", length = 1000)
    private String reason;

    @Comment("人员id")
    @Column(name = "USERID", length = 100)
    private String userId;

    @Comment("人员名称")
    @Column(name = "USERNAME")
    private String userName;

    @Comment("联系电话")
    @Column(name = "USERMOBILE")
    private String userMobile;

    @Comment("任务id")
    @Column(name = "TASKID", length = 50)
    private String taskId;

    /**
     * 动作标记：例如：退回原因标识（1）、 收回原因标识（2）、重定向原因标识（3）、特殊办结原因标识（4）
     */
    @Comment("动作标记")
    @Column(name = "ACTION", length = 10)
    private Integer action;
}
