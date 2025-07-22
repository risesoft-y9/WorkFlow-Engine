package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

/**
 * 任务变量模型类
 * 
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Data
public class TaskVariableModel implements Serializable {

    private static final long serialVersionUID = -1972054094376070120L;
    /**
     * 主键
     */
    private String id;

    /**
     * 流程实例
     */
    private String processInstanceId;

    /**
     * 任务id
     */
    private String taskId;

    /**
     * 变量名称
     */
    private String keyName;

    /**
     * 变量值
     */
    private String text;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 更新时间
     */
    private String updateTime;

}
