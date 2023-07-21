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
@Table(name = "FF_ACT_RU_EXECUTION_BACKUP")
@org.hibernate.annotations.Table(comment = "执行实例备份表", appliesTo = "FF_ACT_RU_EXECUTION_BACKUP")
public class ActRuExecutionBackup implements Serializable {

    private static final long serialVersionUID = -4491931506668447623L;

    /**
     * 主键
     */
    @Id
    @org.hibernate.annotations.Comment("主键")
    @Column(name = "ID_", length = 50, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @org.hibernate.annotations.Comment("版本")
    @Column(name = "REV_", length = 10)
    private Integer revision = 1;

    @org.hibernate.annotations.Comment("流程实例Id")
    @Column(name = "PROC_INST_ID_", length = 50, nullable = false)
    private String processInstanceId;

    @org.hibernate.annotations.Comment("流程业务key")
    @Column(name = "BUSINESS_KEY_", length = 255)
    private String businessKey;

    @org.hibernate.annotations.Comment("父流程实例Id")
    @Column(name = "PARENT_ID_", length = 64)
    private String parentId;

    @org.hibernate.annotations.Comment("流程定义Id")
    @Column(name = "PROC_DEF_ID_", length = 64)
    private String processDefinitionId;

    @org.hibernate.annotations.Comment("父执行实例Id")
    @Column(name = "SUPER_EXEC_", length = 64)
    private String superExecutionId;

    @org.hibernate.annotations.Comment("根流程实例Id")
    @Column(name = "ROOT_PROC_INST_ID_", length = 64)
    private String rootProcessInstanceId;

    @org.hibernate.annotations.Comment("节点Id")
    @Column(name = "ACT_ID_", length = 64)
    private String actId;

    @org.hibernate.annotations.Comment("是否活着")
    @Column(name = "IS_ACTIVE_", length = 64)
    private boolean actived = true;

    @org.hibernate.annotations.Comment("是否是并发的")
    @Column(name = "IS_CONCURRENT_", length = 64)
    private boolean concurrent = false;

    @org.hibernate.annotations.Comment("范围")
    @Column(name = "IS_SCOPE_", length = 64)
    private boolean scope = false;

    @org.hibernate.annotations.Comment("事件范围")
    @Column(name = "IS_EVENT_SCOPE_", length = 64)
    private boolean eventScope = false;

    @org.hibernate.annotations.Comment("是否是流程实例Id")
    @Column(name = "IS_MI_ROOT_", length = 64)
    private boolean miRoot = false;

    @org.hibernate.annotations.Comment("挂起状态")
    @Column(name = "SUSPENSION_STATE_", length = 10)
    private Integer suspensionState = 1;

    @org.hibernate.annotations.Comment("缓存状态")
    @Column(name = "CACHED_ENT_STATE_", length = 10)
    private Integer cachedEntState;

    @org.hibernate.annotations.Comment("租户Id")
    @Column(name = "TENANT_ID_", length = 255)
    private String tenantId;

    @org.hibernate.annotations.Comment("节点名称")
    @Column(name = "NAME_", length = 255)
    private String name;

    @org.hibernate.annotations.Comment("开始任务节点Id")
    @Column(name = "START_ACT_ID_", length = 255)
    private String startActId;

    @Temporal(TemporalType.TIMESTAMP)
    @org.hibernate.annotations.Comment("创建时间")
    @Column(name = "START_TIME_")
    private Date startTime;

    @org.hibernate.annotations.Comment("流程启动人Id")
    @Column(name = "START_USER_ID_", length = 255)
    private String startUserId;

    @Temporal(TemporalType.TIMESTAMP)
    @org.hibernate.annotations.Comment("加锁时间")
    @Column(name = "LOCK_TIME_")
    private Date lockTime;

    @org.hibernate.annotations.Comment("开启计数")
    @Column(name = "IS_COUNT_ENABLED_", length = 10)
    private boolean countEnabled = true;

    @org.hibernate.annotations.Comment("evtSubscrCount")
    @Column(name = "EVT_SUBSCR_COUNT_", length = 10)
    private Integer evtSubscrCount = 0;

    @org.hibernate.annotations.Comment("任务数量")
    @Column(name = "TASK_COUNT_", length = 10)
    private Integer taskCount = 1;

    @org.hibernate.annotations.Comment("工作数量")
    @Column(name = "JOB_COUNT_", length = 10)
    private Integer jobCount = 0;

    @org.hibernate.annotations.Comment("定时器数量")
    @Column(name = "TIMER_JOB_COUNT_", length = 10)
    private Integer timerJobCount = 0;

    @org.hibernate.annotations.Comment("挂起的工作数量")
    @Column(name = "SUSP_JOB_COUNT_", length = 10)
    private Integer suspJobCount = 0;

    @org.hibernate.annotations.Comment("死信工作计数")
    @Column(name = "DEADLETTER_JOB_COUNT_", length = 10)
    private Integer deadletterJobCount = 0;

    @org.hibernate.annotations.Comment("流程变量数量")
    @Column(name = "VAR_COUNT_", length = 10)
    private Integer varCount = 0;

    @org.hibernate.annotations.Comment("idLinkCount")
    @Column(name = "ID_LINK_COUNT_", length = 10)
    private Integer idLinkCount = 0;

    @org.hibernate.annotations.Comment("callBackId")
    @Column(name = "CALLBACK_ID_", length = 255)
    private String callBackId;

    @org.hibernate.annotations.Comment("callBackType")
    @Column(name = "CALLBACK_TYPE_", length = 255)
    private String callBackType;
}
