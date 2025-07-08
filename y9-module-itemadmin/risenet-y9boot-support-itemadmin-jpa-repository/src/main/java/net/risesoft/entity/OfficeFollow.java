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

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Data
@Entity
@Table(name = "FF_OFFICE_FOLLOW")
@org.hibernate.annotations.Table(comment = "办件关注信息表", appliesTo = "FF_OFFICE_FOLLOW")
public class OfficeFollow implements Serializable {
    private static final long serialVersionUID = -2749619146231540189L;

    /**
     * 唯一标示
     */
    @Id
    @Comment("主键")
    @Column(name = "GUID", length = 38, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String guid;

    /**
     * 工作流流程编号
     */
    @Comment("工作流流程编号")
    @Column(name = "PROCESSSERIALNUMBER", length = 50, nullable = false)
    private String processSerialNumber;

    /**
     * 事项id
     */
    @Comment("事项id")
    @Column(name = "ITEMID", length = 50, nullable = true)
    private String itemId;

    /**
     * 流程实例id
     */
    @Comment("流程实例id")
    @Column(name = "PROCESSINSTANCEID", length = 50, nullable = false)
    private String processInstanceId;

    /**
     * 紧急程度
     */
    @Comment("紧急程度")
    @Column(name = "JINJICHENGDU", length = 50, nullable = true)
    private String jinjichengdu;

    /**
     * 文件编号
     */
    @Comment("文件编号")
    @Column(name = "NUMBERS", length = 50, nullable = true)
    private String numbers;

    /**
     * 来文单位/拟稿单位
     */
    @Comment("来文单位/拟稿单位")
    @Column(name = "sendDept", length = 150, nullable = true)
    private String sendDept;

    /**
     * 文件类型
     */
    @Comment("文件类型")
    @Column(name = "FILETYPE", length = 50, nullable = true)
    private String fileType;

    /**
     * 系统名称
     */
    @Comment("系统名称")
    @Column(name = "SYSTEMNAME", length = 50, nullable = true)
    private String systemName;

    /**
     * 标题
     */
    @Comment("标题")
    @Column(name = "DOCUMENTTITLE", length = 500, nullable = true)
    private String documentTitle;

    /**
     * 办理期限
     */
    @Comment("办理期限")
    @Column(name = "handleTerm", length = 50, nullable = true)
    private String handleTerm;

    /**
     * 委办局id
     */
    @Comment("委办局id")
    @Column(name = "BUREAUID", length = 50, nullable = true)
    private String bureauId;

    /**
     * 委办局名称
     */
    @Comment("委办局名称")
    @Column(name = "BUREAUNAME", length = 200, nullable = true)
    private String bureauName;

    /**
     * 关注人ID
     */
    @Comment("关注人ID")
    @Column(name = "USERID", length = 50, nullable = false)
    private String userId;

    /**
     * 关注人姓名
     */
    @Comment("关注人姓名")
    @Column(name = "USERNAME", length = 50, nullable = false)
    private String userName;

    /**
     * 流程启动时间
     */
    @Comment("流程启动时间")
    @Column(name = "STARTTIME")
    private String startTime;

    /**
     * 关注时间
     */
    @Comment("关注时间")
    @Column(name = "CREATETIME")
    private String createTime;

    /**
     * 任务id
     */
    @Transient
    private String taskId;

    /**
     * 办件状态
     */
    @Transient
    private String itembox;

    /**
     * 任务环节
     */
    @Transient
    private String taskName;

    /**
     * 任务创建时间
     */
    @Transient
    private String taskCreateTime;

    /**
     * 当前任务办理人
     */
    @Transient
    private String taskAssignee;

    /**
     * 是否有抄送
     */
    @Transient
    private boolean chaosong;

    /**
     * 是否有消息提醒
     */
    @Transient
    private boolean msgremind;

}
