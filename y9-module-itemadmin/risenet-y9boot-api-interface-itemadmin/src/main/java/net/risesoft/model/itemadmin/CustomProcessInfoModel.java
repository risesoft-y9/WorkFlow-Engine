package net.risesoft.model.itemadmin;

import lombok.Data;

import java.io.Serializable;

/**
 * 自定义流程信息模型
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Data
public class CustomProcessInfoModel implements Serializable {

    private static final long serialVersionUID = 2620414732616268131L;

    /**
     * 主键
     */
    private String id;

    /**
     * 任务key
     */
    private String taskKey;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 任务类型
     */
    private String taskType;

    /**
     * 当前运行节点
     */
    private Boolean currentTask;
    /**
     * 事项id
     */
    private String itemId;
    /**
     * 流程编号
     */
    private String processSerialNumber;
    /**
     * 办理人id
     */
    private String orgId;
    /**
     * 排序号
     */
    private Integer tabIndex;
}
