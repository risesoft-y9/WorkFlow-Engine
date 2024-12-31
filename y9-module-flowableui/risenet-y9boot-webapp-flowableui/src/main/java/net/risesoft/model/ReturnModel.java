package net.risesoft.model;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * 待办、在办等所有列表返回对象详情
 *
 * @author qinman
 * @date 2024/12/31
 */
@Data
public class ReturnModel implements Serializable {

    private static final long serialVersionUID = 282111383004244513L;

    /**
     * 序号
     */
    private int serialNumber;
    /**
     * 任务id
     */
    private String taskId;
    /**
     * 流程实例
     */
    private String processInstanceId;
    /**
     * 流程编号
     */
    private String processSerialNumber;
    /**
     * 事项系统中文名称
     */
    private String systemCNName;
    /**
     * 事项id
     */
    private String itemId;
    /**
     * 表单数据json
     */
    private String formData;
    /**
     * 列表类型
     */
    private String itemBox;
    /**
     * 当前任务名称
     */
    private String taskName;
    /**
     * 当前办理人
     */
    private String taskAssignee;

    private List<ReturnModel> children;
}
