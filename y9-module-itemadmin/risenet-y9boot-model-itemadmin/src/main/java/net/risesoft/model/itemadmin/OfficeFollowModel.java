package net.risesoft.model.itemadmin;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * 关注模型类
 * 
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Data
public class OfficeFollowModel implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 4812604593843826041L;
    /**
     * 创建时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    protected Date createTime;
    /**
     * 更新时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    protected Date updateTime;
    /**
     * 唯一标示
     */
    private String guid;
    /**
     * 工作流流程编号
     */
    private String processSerialNumber;
    /**
     * 事项id
     */
    private String itemId;
    /**
     * 流程实例id
     */
    private String processInstanceId;
    /**
     * 紧急程度
     */
    private String jinjichengdu;
    /**
     * 文件编号
     */
    private String numbers;
    /**
     * 来文单位/拟稿单位
     */
    private String sendDept;
    /**
     * 文件类型
     */
    private String fileType;
    /**
     * 系统名称
     */
    private String systemName;
    /**
     * 标题
     */
    private String documentTitle;
    /**
     * 办理期限
     */
    private String handleTerm;
    /**
     * 委办局id
     */
    private String bureauId;
    /**
     * 委办局名称
     */
    private String bureauName;
    /**
     * 关注人ID
     */
    private String userId;
    /**
     * 关注人姓名
     */
    private String userName;
    /**
     * 流程启动时间
     */
    private String startTime;
    /**
     * 任务id
     */
    private String taskId;

    /**
     * 任务环节
     */
    private String taskName;

    /**
     * 任务创建时间
     */
    private String taskCreateTime;

    /**
     * 当前任务办理人
     */
    private String taskAssignee;

    /**
     * 办件状态
     */
    private String itembox;

    /**
     * 是否有抄送
     */
    private boolean chaosong;

    /**
     * 是否有消息提醒
     */
    private boolean msgremind;
}
