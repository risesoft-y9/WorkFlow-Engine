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
@Table(name = "FF_ACT_RU_EXECUTION_BACKUP")
@org.hibernate.annotations.Table(comment = "执行实例备份表", appliesTo = "FF_ACT_RU_EXECUTION_BACKUP")
public class ActRuExecutionBackup implements Serializable {

    private static final long serialVersionUID = -4491931506668447623L;

    /**
     * 主键
     */
    @Id
    @Comment("主键")
    @Column(name = "ID_", length = 50, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("版本")
    @Column(name = "REV_", length = 10)
    private Integer revision = 1;

    @Comment("流程实例Id")
    @Column(name = "PROC_INST_ID_", length = 50, nullable = false)
    private String processInstanceId;

    @Comment("流程业务key")
    @Column(name = "BUSINESS_KEY_", length = 255)
    private String businessKey;

    @Comment("父流程实例Id")
    @Column(name = "PARENT_ID_", length = 64)
    private String parentId;

    @Comment("流程定义Id")
    @Column(name = "PROC_DEF_ID_", length = 64)
    private String processDefinitionId;

    @Comment("父执行实例Id")
    @Column(name = "SUPER_EXEC_", length = 64)
    private String superExecutionId;

    @Comment("根流程实例Id")
    @Column(name = "ROOT_PROC_INST_ID_", length = 64)
    private String rootProcessInstanceId;

    @Comment("节点Id")
    @Column(name = "ACT_ID_", length = 64)
    private String actId;

    @Type(type = "numeric_boolean")
    @Comment("是否活着")
    @Column(name = "IS_ACTIVE_", length = 64)
    private boolean actived = true;

    @Type(type = "numeric_boolean")
    @Comment("是否是并发的")
    @Column(name = "IS_CONCURRENT_", length = 64)
    private boolean concurrent = false;

    @Type(type = "numeric_boolean")
    @Comment("范围")
    @Column(name = "IS_SCOPE_", length = 64)
    private boolean scope = false;

    @Type(type = "numeric_boolean")
    @Comment("事件范围")
    @Column(name = "IS_EVENT_SCOPE_", length = 64)
    private boolean eventScope = false;

    @Type(type = "numeric_boolean")
    @Comment("是否是流程实例Id")
    @Column(name = "IS_MI_ROOT_", length = 64)
    private boolean miRoot = false;

    @Comment("挂起状态")
    @Column(name = "SUSPENSION_STATE_", length = 10)
    private Integer suspensionState = 1;

    @Comment("缓存状态")
    @Column(name = "CACHED_ENT_STATE_", length = 10)
    private Integer cachedEntState;

    @Comment("租户Id")
    @Column(name = "TENANT_ID_", length = 255)
    private String tenantId;

    @Comment("节点名称")
    @Column(name = "NAME_", length = 255)
    private String name;

    @Comment("开始任务节点Id")
    @Column(name = "START_ACT_ID_", length = 255)
    private String startActId;

    @Temporal(TemporalType.TIMESTAMP)
    @Comment("创建时间")
    @Column(name = "START_TIME_")
    private Date startTime;

    @Comment("流程启动人Id")
    @Column(name = "START_USER_ID_", length = 255)
    private String startUserId;

    @Temporal(TemporalType.TIMESTAMP)
    @Comment("加锁时间")
    @Column(name = "LOCK_TIME_")
    private Date lockTime;

    @Type(type = "numeric_boolean")
    @Comment("开启计数")
    @Column(name = "IS_COUNT_ENABLED_", length = 10)
    private boolean countEnabled = true;

    @Comment("evtSubscrCount")
    @Column(name = "EVT_SUBSCR_COUNT_", length = 10)
    private Integer evtSubscrCount = 0;

    @Comment("任务数量")
    @Column(name = "TASK_COUNT_", length = 10)
    private Integer taskCount = 1;

    @Comment("工作数量")
    @Column(name = "JOB_COUNT_", length = 10)
    private Integer jobCount = 0;

    @Comment("定时器数量")
    @Column(name = "TIMER_JOB_COUNT_", length = 10)
    private Integer timerJobCount = 0;

    @Comment("挂起的工作数量")
    @Column(name = "SUSP_JOB_COUNT_", length = 10)
    private Integer suspJobCount = 0;

    @Comment("死信工作计数")
    @Column(name = "DEADLETTER_JOB_COUNT_", length = 10)
    private Integer deadletterJobCount = 0;

    @Comment("流程变量数量")
    @Column(name = "VAR_COUNT_", length = 10)
    private Integer varCount = 0;

    @Comment("idLinkCount")
    @Column(name = "ID_LINK_COUNT_", length = 10)
    private Integer idLinkCount = 0;

    @Comment("callBackId")
    @Column(name = "CALLBACK_ID_", length = 255)
    private String callBackId;

    @Comment("callBackType")
    @Column(name = "CALLBACK_TYPE_", length = 255)
    private String callBackType;
}
