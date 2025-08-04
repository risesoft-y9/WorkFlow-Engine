package net.risesoft.entity;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import net.risesoft.enums.ActRuDetailSignStatusEnum;
import net.risesoft.enums.ActRuDetailStatusEnum;
import net.risesoft.persistence.ItemEnumConverter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

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
@Comment("流转信息表")
public class ActRuDetail implements Serializable {

    private static final long serialVersionUID = 1276030927802869240L;

    @Id
    @Comment("主键")
    @Column(name = "ID", length = 50, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("序列号")
    @Column(name = "PROCESSSERIALNUMBER", length = 50)
    private String processSerialNumber;

    @Comment("流程实例Id")
    @Column(name = "PROCESSINSTANCEID", length = 50)
    private String processInstanceId;

    @Comment("执行实例Id")
    @Column(name = "EXECUTIONID", length = 50)
    private String executionId;

    @Comment("任务Id")
    @Column(name = "TASKID", length = 50)
    private String taskId;

    @Comment("流程定义id")
    @Column(name = "PROCESSDEFINITIONID", length = 100)
    private String processDefinitionId;

    @Comment("流程定义key")
    @Column(name = "PROCESSDEFINITIONKEY", length = 100)
    private String processDefinitionKey;

    @Comment("任务key")
    @Column(name = "TASKDEFKEY", length = 100)
    private String taskDefKey;

    @Comment("任务节点名称")
    @Column(name = "TASKDEFName", length = 100)
    private String taskDefName;

    @Comment("事项Id")
    @Column(name = "ITEMID", length = 50)
    private String itemId;

    @Comment("事项所属系统英文名称")
    @Column(name = "SYSTEMNAME", length = 100)
    private String systemName;

    @Comment("流程启动时间")
    @Column(name = "STARTTIME", length = 50)
    private String startTime;

    @Comment("状态")
    @Column(name = "STATUS", length = 100)
    @Convert(converter = ItemEnumConverter.ActRuDetailStatusEnumConverter.class)
    private ActRuDetailStatusEnum status = ActRuDetailStatusEnum.TODO;

    @Comment("签收状态")
    @Column(name = "SIGNSTATUS", length = 100)
    @Convert(converter = ItemEnumConverter.ActRuDetailSignStatusEnumConverter.class)
    private ActRuDetailSignStatusEnum signStatus = ActRuDetailSignStatusEnum.TODO;

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

    @Comment("发送人ID")
    @Column(name = "SENDUSERID", length = 50)
    private String sendUserId;

    @Comment("发送人姓名")
    @Column(name = "SENDUSERNAME", length = 50)
    private String sendUserName;

    @Comment("发送人部门ID")
    @Column(name = "SENDDEPTID", length = 50)
    private String sendDeptId;

    @Comment("发送人部门名称")
    @Column(name = "SENDDEPTNAME", length = 50)
    private String sendDeptName;

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

    @Convert(converter = org.hibernate.type.NumericBooleanConverter.class)
    @ColumnDefault("1")
    @Comment("流程是否开始")
    @Column(name = "STARTED")
    private boolean started;

    @Convert(converter = org.hibernate.type.NumericBooleanConverter.class)
    @ColumnDefault("0")
    @Comment("流程是否结束")
    @Column(name = "ENDED")
    private boolean ended;

    @Convert(converter = org.hibernate.type.NumericBooleanConverter.class)
    @ColumnDefault("0")
    @Comment("是否删除")
    @Column(name = "DELETED")
    private boolean deleted;

    @Convert(converter = org.hibernate.type.NumericBooleanConverter.class)
    @ColumnDefault("0")
    @Comment("是否子流程的节点任务")
    @Column(name = "SUB")
    private boolean sub;

    @Convert(converter = org.hibernate.type.NumericBooleanConverter.class)
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