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
@org.hibernate.annotations.Table(comment = "任务变量信息表", appliesTo = "FF_TASKVARIABLE")
@Table(name = "FF_TASKVARIABLE")
public class TaskVariable implements Serializable {
    private static final long serialVersionUID = 4795951592230702404L;

    @Id
    @Comment("主键")
    @Column(name = "ID", length = 50, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("流程实例")
    @Column(name = "PROCESSINSTANCEID", length = 50, nullable = false)
    private String processInstanceId;

    @Comment("任务id")
    @Column(name = "TASKID", length = 50, nullable = false)
    private String taskId;

    @Comment("变量名称")
    @Column(name = "KEYNAME", length = 100, nullable = false)
    private String keyName;

    @Comment("变量值")
    @Lob
    @Column(name = "TEXT")
    private String text;

    @Comment("创建时间")
    @Column(name = "CREATETIME", length = 50)
    private String createTime;

    @Comment("更新时间")
    @Column(name = "UPDATETIME", length = 50)
    private String updateTime;

}
