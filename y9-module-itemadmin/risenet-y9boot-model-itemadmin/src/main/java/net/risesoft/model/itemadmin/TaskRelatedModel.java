package net.risesoft.model.itemadmin;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.enums.TaskRelatedEnum;

/**
 * @author qinman
 * @date 2024/12/03
 */
@Data
@NoArgsConstructor
public class TaskRelatedModel implements Serializable {

    private static final long serialVersionUID = 879932521397158651L;

    /**
     * 主键
     */
    private String id;

    /**
     * 流程序列号
     */
    private String processSerialNumber;

    /**
     * 流程实例id
     */
    private String processInstanceId;
    /**
     * 流程执行实例Id
     */
    private String executionId;
    /**
     * 是否子流程的节点任务
     */
    private boolean sub;
    /**
     * 任务唯一标示
     */
    private String taskId;
    /**
     * 信息类型 {@link TaskRelatedEnum}
     */
    private String infoType;
    /**
     * 消息内容
     */
    private String msgContent;
    /**
     * 发送人id
     */
    private String senderId;
    /**
     * 发送人名称
     */
    private String senderName;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 修改时间
     */
    private Date modifyTime;
    /**
     * 阅读时间
     */
    private Date readTime;

    public TaskRelatedModel(String infoType, String msgContent) {
        this.infoType = infoType;
        this.msgContent = msgContent;
    }
}
