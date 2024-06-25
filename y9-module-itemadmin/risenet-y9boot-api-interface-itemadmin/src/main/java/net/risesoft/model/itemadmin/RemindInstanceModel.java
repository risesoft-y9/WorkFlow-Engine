package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

/**
 * 消息提醒实例
 * 
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Data
public class RemindInstanceModel implements Serializable {

    private static final long serialVersionUID = 6170620339097452197L;

    public static String processComplete = "processComplete";// 流程办结提醒

    public static String taskComplete = "taskComplete";// 任务完成提醒

    public static String nodeArrive = "nodeArrive";// 节点到达提醒

    public static String nodeComplete = "nodeComplete";// 节点完成提醒

    /**
     * 主键
     */
    private String id;

    /**
     * 租户Id
     */
    private String tenantId;

    /**
     * 消息提醒类型
     */
    private String remindType;

    /**
     * 流程实例id
     */
    private String processInstanceId;

    /**
     * 任务id
     */
    private String taskId;

    /**
     * 节点到达任务key
     */
    private String arriveTaskKey;

    /**
     * 节点完成任务key
     */
    private String completeTaskKey;

    /**
     * 人员id
     */
    private String userId;

    /**
     * 创建时间
     */
    private String createTime;
}
