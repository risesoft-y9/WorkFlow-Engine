package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

/**
 * 催办信息详情
 *
 * @author mengjuhua
 * @date 2024/06/25
 */
@Data
public class ReminderModel implements Serializable {
    private static final long serialVersionUID = -2007663540431278959L;

    /** 主键 */
    private String id;

    /** 租户Id */
    private String tenantId;

    /** 催办发送类型 */
    private String reminderSendType;

    /** 提醒类型 */
    private Integer reminderMakeTyle;

    /** 流程实例id */
    private String procInstId;

    /** 任务id */
    private String taskId;

    /** 发送人id */
    private String senderId;

    /** 发送人名称 */
    private String senderName;

    /** 消息内容 */
    private String msgContent;

    /** 创建时间 */
    private String createTime;

    /** 修改时间 */
    private String modifyTime;

    /** "阅读时间" */
    private String readTime;

    /** 序号 */
    private Integer serialNumber;

    /** 用户名称 */
    private String userName;

    /** 任务名称 */
    private String taskName;

}
