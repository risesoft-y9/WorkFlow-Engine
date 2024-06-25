package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

/**
 * 启动流程返回信息
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Data
public class StartProcessResultModel implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -2451099058363077071L;

    /**
     * 流程序列号
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
     * 任务key
     */
    private String taskDefKey;

    /**
     * 流程定义id
     */
    private String processDefinitionId;

}
