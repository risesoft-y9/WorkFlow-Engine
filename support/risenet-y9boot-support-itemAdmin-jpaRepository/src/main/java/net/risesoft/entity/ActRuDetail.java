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

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

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
@Table(name = "FF_ACT_RU_DETAIL")
@org.hibernate.annotations.Table(comment = "流转信息表", appliesTo = "FF_ACT_RU_DETAIL")
public class ActRuDetail implements Serializable {

    private static final long serialVersionUID = 1276030927802869240L;

    public static final Integer DOING = 1;

    @Id
    @org.hibernate.annotations.Comment("主键")
    @Column(name = "ID", length = 50, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @org.hibernate.annotations.Comment("事项Id")
    @Column(name = "ITEMID", length = 50)
    private String itemId;

    @org.hibernate.annotations.Comment("流程实例Id")
    @Column(name = "PROCESSINSTANCEID", length = 50)
    private String processInstanceId;

    @org.hibernate.annotations.Comment("序列号")
    @Column(name = "PROCESSSERIALNUMBER", length = 50)
    private String processSerialNumber;

    @org.hibernate.annotations.Comment("任务Id")
    @Column(name = "TASKID", length = 50)
    private String taskId;

    @org.hibernate.annotations.Comment("流程定义key")
    @Column(name = "PROCESSDEFINITIONKEY", length = 100)
    private String processDefinitionKey;

    @org.hibernate.annotations.Comment("事项所属系统英文名称")
    @Column(name = "SYSTEMNAME", length = 100)
    private String systemName;

    /** 1是在办、0是待办 */
    @org.hibernate.annotations.Comment("状态")
    @Column(name = "STATUS", length = 100)
    private Integer status;

    @org.hibernate.annotations.Comment("办理人")
    @Column(name = "ASSIGNEE", length = 50)
    private String assignee;

    @org.hibernate.annotations.Comment("办理人姓名")
    @Column(name = "ASSIGNEENAME", length = 50)
    private String assigneeName;

    @org.hibernate.annotations.Comment("办理部门")
    @Column(name = "DEPTID", length = 50)
    private String deptId;

    @org.hibernate.annotations.Comment("办理部门")
    @Column(name = "DEPTNAME", length = 50)
    private String deptName;

    @org.hibernate.annotations.Comment("办理部门所在委办局")
    @Column(name = "BUREAUID", length = 50)
    private String bureauId;

    @org.hibernate.annotations.Comment("办理部门所在委办局")
    @Column(name = "BUREAUNAME", length = 50)
    private String bureauName;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    @org.hibernate.annotations.Comment("生成时间")
    @Column(name = "CREATETIME", length = 50)
    private Date createTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    @org.hibernate.annotations.Comment("最后办理时间")
    @Column(name = "LASTTIME", length = 50)
    private Date lastTime;

    @ColumnDefault("true")
    @org.hibernate.annotations.Comment("流程是否开始")
    @Column(name = "STARTED")
    private boolean started;

    @ColumnDefault("false")
    @org.hibernate.annotations.Comment("流程是否结束")
    @Column(name = "ENDED")
    private boolean ended;

    @ColumnDefault("false")
    @org.hibernate.annotations.Comment("是否删除")
    @Column(name = "DELETED")
    private boolean deleted;

    @ColumnDefault("false")
    @org.hibernate.annotations.Comment("是否归档")
    @Column(name = "PLACEONFILE")
    private boolean placeOnFile;
}