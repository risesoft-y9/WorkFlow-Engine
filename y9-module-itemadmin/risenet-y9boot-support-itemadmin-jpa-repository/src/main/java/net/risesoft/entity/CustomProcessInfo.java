package net.risesoft.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
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
@Table(name = "FF_CUSTOMPROCESSINFO")
@Comment("定制流程信息表")
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

    @Convert(converter = org.hibernate.type.NumericBooleanConverter.class)
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
