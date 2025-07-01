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
@Table(name = "FF_TASK_TIMECONF")
@org.hibernate.annotations.Table(comment = "任务时间配置表", appliesTo = "FF_TASK_TIMECONF")
public class TaskTimeConf implements Serializable {

    private static final long serialVersionUID = -8704659328665914802L;
    @Id
    @Comment("主键")
    @Column(name = "ID", length = 38, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("事项Id")
    @Column(name = "ITEMID", length = 50, nullable = false)
    private String itemId;

    @Comment("流程定义Id")
    @Column(name = "PROCESSDEFINITIONID", length = 100, nullable = false)
    private String processDefinitionId;

    @Comment("任务key")
    @Column(name = "TASKDEFKEY", length = 100)
    private String taskDefKey;

    @Comment("超时打断时长")
    @Column(name = "timeoutInterrupt", length = 10)
    private Integer timeoutInterrupt;

    @Comment("最短时长")
    @Column(name = "leastTime", length = 10)
    private Integer leastTime;

    @Transient
    private String taskDefName;

    @Transient
    private String taskType;
}
