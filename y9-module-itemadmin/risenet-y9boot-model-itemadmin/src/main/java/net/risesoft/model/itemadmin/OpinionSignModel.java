package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

/**
 * @author qinman
 * @date 2024/12/16
 */
@Data
public class OpinionSignModel implements Serializable {

    private static final long serialVersionUID = 3138038471575680005L;
    /**
     * 唯一标示
     */
    private String id;

    /**
     * 会签部门信息id
     */
    private String signDeptDetailId;

    /**
     * 意见框标识
     */
    private String opinionFrameMark;

    /**
     * 任务实例Id
     */
    private String taskId;

    /**
     * 意见内容
     */
    private String content;

    /**
     * 填写意见人员id
     */
    private String userId;

    /**
     * 填写意见的人员名称
     */
    private String userName;

    /**
     * 填写意见人部门id
     */
    private String deptId;

    /**
     * 填写意见的人员部门名称
     */
    private String deptName;

    /**
     * 意见生成时间
     */
    private String createDate;

    /**
     * 意见最后的修改时间
     */
    private String modifyDate;
}
