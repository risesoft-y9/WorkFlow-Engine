package net.risesoft.model.itemadmin.core;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

import net.risesoft.enums.ActRuDetailSignStatusEnum;
import net.risesoft.enums.ActRuDetailStatusEnum;

/**
 * 流转详细信息
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Data
public class ActRuDetailModel implements Serializable {

    public static final Integer TODO = 0;
    public static final Integer DOING = 1;
    private static final long serialVersionUID = 3387901751275051825L;
    /**
     * 唯一标示
     */
    private String id;

    /**
     * 事项Id
     */
    private String itemId;

    /**
     * 流程编号
     */
    private String processSerialNumber;

    /**
     * 流程实例
     */
    private String processInstanceId;

    /**
     * 任务Id
     */
    private String taskId;

    /**
     * 所属事项绑定的流程定义
     */
    private String processDefinitionKey;

    /**
     * 执行实例Id
     */
    private String executionId;

    /**
     * 流程定义id
     */
    private String processDefinitionId;

    /**
     * 任务key
     */
    private String taskDefKey;

    /**
     * 任务节点名称
     */
    private String taskDefName;

    /**
     * 流程启动时间
     */
    private String startTime;

    /**
     * 所属事项的系统英文名称
     */
    private String systemName;

    private ActRuDetailStatusEnum status;

    private ActRuDetailSignStatusEnum signStatus;

    /**
     * 办理人Id
     */
    private String assignee;

    /**
     * 办理部门
     */
    private String deptId;

    /**
     * 办理部门名称
     */
    private String deptName;

    /**
     * 办理部门所在委办局
     */
    private String bureauId;

    /**
     * 办理部门所在委办局
     */
    private String bureauName;

    /**
     * 发送人ID
     */
    private String sendUserId;

    /**
     * 发送人姓名
     */
    private String sendUserName;

    /**
     * 发送人部门ID
     */
    private String sendDeptId;

    /**
     * 发送人部门名称
     */
    private String sendDeptName;

    /**
     * 办理人姓名
     */
    private String assigneeName;

    /**
     * 生成的时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 最后一次的办理时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastTime;

    /**
     * 是否启动流程
     */
    private boolean started;

    /**
     * 流程是否办结
     */
    private boolean ended;

    /**
     * 是否删除
     */
    private boolean deleted;

    /**
     * 是否归档
     */
    private boolean placeOnFile;

    /**
     * 是否子流程的节点任务
     */
    private boolean sub;

    /**
     * 到期时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dueDate;
}
