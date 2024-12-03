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

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
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

    public static final Integer DOING = 1;

    private static final long serialVersionUID = 1276030927802869240L;

    @Id
    @Comment("主键")
    @Column(name = "ID", length = 50, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("事项Id")
    @Column(name = "ITEMID", length = 50)
    private String itemId;

    @Comment("流程实例Id")
    @Column(name = "PROCESSINSTANCEID", length = 50)
    private String processInstanceId;

    @Comment("序列号")
    @Column(name = "PROCESSSERIALNUMBER", length = 50)
    private String processSerialNumber;

    @Comment("任务Id")
    @Column(name = "TASKID", length = 50)
    private String taskId;

    @Comment("流程定义key")
    @Column(name = "PROCESSDEFINITIONKEY", length = 100)
    private String processDefinitionKey;

    @Comment("事项所属系统英文名称")
    @Column(name = "SYSTEMNAME", length = 100)
    private String systemName;

    @Comment("流程启动时间")
    @Column(name = "STARTTIME", length = 50)
    private String startTime;

    /** 1是在办、0是待办 */
    @Comment("状态")
    @Column(name = "STATUS", length = 100)
    private Integer status;

    @Comment("办理人")
    @Column(name = "ASSIGNEE", length = 50)
    private String assignee;

    @Comment("办理人姓名")
    @Column(name = "ASSIGNEENAME", length = 50)
    private String assigneeName;

    @Comment("办理部门")
    @Column(name = "DEPTID", length = 50)
    private String deptId;

    @Comment("办理部门")
    @Column(name = "DEPTNAME", length = 50)
    private String deptName;

    @Comment("办理部门所在委办局")
    @Column(name = "BUREAUID", length = 50)
    private String bureauId;

    @Comment("办理部门所在委办局")
    @Column(name = "BUREAUNAME", length = 50)
    private String bureauName;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    @Comment("生成时间")
    @Column(name = "CREATETIME", length = 50)
    private Date createTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    @Comment("最后办理时间")
    @Column(name = "LASTTIME", length = 50)
    private Date lastTime;

    @Type(type = "numeric_boolean")
    @ColumnDefault("1")
    @Comment("流程是否开始")
    @Column(name = "STARTED")
    private boolean started;

    @Type(type = "numeric_boolean")
    @ColumnDefault("0")
    @Comment("流程是否结束")
    @Column(name = "ENDED")
    private boolean ended;

    @Type(type = "numeric_boolean")
    @ColumnDefault("0")
    @Comment("是否删除")
    @Column(name = "DELETED")
    private boolean deleted;

    @Type(type = "numeric_boolean")
    @ColumnDefault("0")
    @Comment("是否归档")
    @Column(name = "PLACEONFILE")
    private boolean placeOnFile;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Temporal(TemporalType.DATE)
    @Column(name = "DUEDATE")
    @Comment("到期时间")
    private Date dueDate;
}