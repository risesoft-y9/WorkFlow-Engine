package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

/**
 * 关联流程模型类
 *
 * @author zhangchongjie
 * @date 2024/06/20
 */
@Data
public class AssociatedFileModel implements Serializable, Comparable<AssociatedFileModel> {

    private static final long serialVersionUID = 3241197746615642199L;

    /**
     * 主键
     */
    private String id;

    /**
     * 事项名称
     */
    private String itemName;

    /**
     * 事项id
     */
    private String itemId;

    /**
     * 标题
     */
    private String documentTitle;

    /**
     * 流程编号
     */
    private String processSerialNumber;

    /**
     * 流程实例id
     */
    private String processInstanceId;

    /**
     * 流程定义id
     */
    private String processDefinitionId;

    /**
     * 流程定义key
     */
    private String processDefinitionKey;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 当前任务key
     */
    private String taskDefinitionKey;

    /**
     * 当前任务办理人
     */
    private String taskAssignee;

    /**
     * 流程创建人
     */
    private String creatUserName;

    /**
     * 紧急程度
     */
    private String level;

    /**
     * 文件编号
     */
    private String number;

    /**
     * 办件状态
     */
    private String itembox;

    /**
     * 排序时间
     */
    private String startTimes;

    /**
     * 任务id
     */
    private String taskId;

    /**
     * 办理人id
     */
    private String taskAssigneeId;

    @Override
    public int compareTo(AssociatedFileModel o) {
        return this.startTimes.compareTo(o.getStartTimes());
    }

}
