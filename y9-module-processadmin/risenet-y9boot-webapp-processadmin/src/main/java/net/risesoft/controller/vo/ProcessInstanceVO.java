package net.risesoft.controller.vo;

import java.io.Serializable;

import lombok.Data;

/**
 * 流程实例变量信息
 *
 * @author mengjuhua
 * @author qinman
 * @author zhangchongjie
 * @date 2024/07/22
 */
@Data
public class ProcessInstanceVO implements Serializable {
    private static final long serialVersionUID = 3271161425137470938L;

    /** 流程业务key */
    private String businessKey;

    /** 流程实例id */
    private String processInstanceId;

    /** 流程定义名称 */
    private String processDefName;

    /** 是否挂起 */
    private Boolean suspended;

    /** 开始时间 */
    private String startTime;

    /** 启动用户名称 */
    private String startUserName;

}
