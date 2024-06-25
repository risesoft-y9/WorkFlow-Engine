package net.risesoft.model.processadmin;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 历史变量实例模型
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Data
public class HistoricVariableInstanceModel implements Serializable {

    private static final long serialVersionUID = 2764777788475295445L;
    /**
     * 主键
     */
    private String id;
    /**
     * 变量名称
     */
    private String variableName;
    /**
     * 变量类型名称
     */
    private String variableTypeName;
    /**
     * 变量值
     */
    private Object value;
    /**
     * 流程实例id
     */
    private String processInstanceId;
    /**
     * 任务id
     */
    private String taskId;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 最后更新时间
     */
    private Date lastUpdatedTime;

}
