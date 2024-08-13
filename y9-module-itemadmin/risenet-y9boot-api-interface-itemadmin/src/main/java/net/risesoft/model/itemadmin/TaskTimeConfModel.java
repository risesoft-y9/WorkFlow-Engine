package net.risesoft.model.itemadmin;

import java.io.Serializable;


import lombok.Data;

/**
 * 任务时间配置
 *
 * @author zhangchongjie
 * @date 2024/05/27
 */
@Data
public class TaskTimeConfModel implements Serializable {


    private static final long serialVersionUID = 810056046275295446L;
    /**
     * 主键
     */
    private String id;
    /**
     * 事项Id
     */
    private String itemId;
    /**
     * 流程定义Id
     */
    private String processDefinitionId;

    /**
     * 任务key
     */
    private String taskDefKey;

    /**
     * 超时打断时长
     */
    private Integer timeoutInterrupt;
    /**
     * 最短时长
     */
    private Integer leastTime;
    /**
     * 任务名称
     */
    private String taskDefName;

}
