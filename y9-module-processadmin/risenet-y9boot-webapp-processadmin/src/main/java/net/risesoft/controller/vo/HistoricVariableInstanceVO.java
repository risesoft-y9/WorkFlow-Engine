package net.risesoft.controller.vo;

import java.io.Serializable;

import lombok.Data;

/**
 * 历史流程变量信息
 *
 * @author mengjuhua
 * @author qinman
 * @author zhangchongjie
 * @date 2024/07/22
 */
@Data
public class HistoricVariableInstanceVO implements Serializable {

    private static final long serialVersionUID = 2911335036839911830L;

    /** 流程实例id */
    private String processInstanceId;

    /** 任务id */
    private String taskId;

    /** 名称 */
    private String name;

    /** 变量值 */
    private Object value;

    /** 变量类型 */
    private String type;
}
