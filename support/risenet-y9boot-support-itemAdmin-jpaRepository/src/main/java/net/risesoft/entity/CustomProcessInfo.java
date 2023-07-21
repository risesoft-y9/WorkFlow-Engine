package net.risesoft.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
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
@Table(name = "FF_CustomProcessInfo")
@org.hibernate.annotations.Table(comment = "定制流程信息表", appliesTo = "FF_CustomProcessInfo")
public class CustomProcessInfo implements Serializable {

    private static final long serialVersionUID = -3115139234478159745L;

    @Id
    @org.hibernate.annotations.Comment("主键")
    @Column(name = "ID", length = 38, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @org.hibernate.annotations.Comment("任务key")
    @Column(name = "TASKKEY", length = 50, nullable = false)
    private String taskKey;

    @org.hibernate.annotations.Comment("任务名称")
    @Column(name = "TASKNAME", length = 100, nullable = false)
    private String taskName;

    @org.hibernate.annotations.Comment("节点类型")
    @Column(name = "TASKTYPE", length = 50, nullable = false)
    private String taskType;

    @org.hibernate.annotations.Comment("当前运行节点")
    @Column(name = "CURRENTTASK", nullable = false)
    @ColumnDefault("0")
    private Boolean currentTask = false;

    @org.hibernate.annotations.Comment("事项id")
    @Column(name = "ITEMID", length = 50, nullable = false)
    private String itemId;

    @org.hibernate.annotations.Comment("流程编号")
    @Column(name = "PROCESSSERIALNUMBER", length = 50, nullable = false)
    private String processSerialNumber;

    @org.hibernate.annotations.Comment("办理人id")
    @Column(name = "ORGID", length = 2000)
    private String orgId;

    @org.hibernate.annotations.Comment("排序号")
    @Column(name = "TABINDEX")
    private Integer tabIndex;

}
