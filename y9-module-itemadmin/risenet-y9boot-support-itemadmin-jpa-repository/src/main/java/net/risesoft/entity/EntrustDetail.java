package net.risesoft.entity;

import java.io.Serializable;

import javax.persistence.*;

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
@Table(name = "FF_ENTRUSTDETAIL")
@org.hibernate.annotations.Table(comment = "出差委托表", appliesTo = "FF_ENTRUSTDETAIL")
public class EntrustDetail implements Serializable {
    private static final long serialVersionUID = 3836935260567480966L;

    @Id
    @Comment("主键")
    @Column(name = "ID", length = 38)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("委托人Id")
    @Column(name = "OWNERID", length = 100, nullable = false)
    private String ownerId;

    @Comment("任务Id")
    @Column(name = "TASKID", length = 50, nullable = false)
    private String taskId;

    @Comment("流程实例Id")
    @Column(name = "PROCESSINSTANCEID", length = 50, nullable = false)
    private String processInstanceId;

}
