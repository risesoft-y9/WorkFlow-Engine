package net.risesoft.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

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
@Table(name = "FF_CUSTOMPROCESSINFO")
@org.hibernate.annotations.Table(comment = "定制流程信息表", appliesTo = "FF_CUSTOMPROCESSINFO")
public class CustomProcessInfo implements Serializable {

    private static final long serialVersionUID = -3115139234478159745L;

    @Id
    @Comment("主键")
    @Column(name = "ID", length = 38, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("任务key")
    @Column(name = "TASKKEY", length = 50, nullable = false)
    private String taskKey;

    @Comment("任务名称")
    @Column(name = "TASKNAME", length = 100, nullable = false)
    private String taskName;

    @Comment("节点类型")
    @Column(name = "TASKTYPE", length = 50, nullable = false)
    private String taskType;

    @Type(type = "numeric_boolean")
    @Comment("当前运行节点")
    @Column(name = "CURRENTTASK", nullable = false)
    @ColumnDefault("0")
    private Boolean currentTask = false;

    @Comment("事项id")
    @Column(name = "ITEMID", length = 50, nullable = false)
    private String itemId;

    @Comment("流程编号")
    @Column(name = "PROCESSSERIALNUMBER", length = 50, nullable = false)
    private String processSerialNumber;

    @Comment("办理人id")
    @Column(name = "ORGID", length = 2000)
    private String orgId;

    @Comment("排序号")
    @Column(name = "TABINDEX")
    private Integer tabIndex;

}
