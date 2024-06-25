package net.risesoft.model.itemadmin;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 协作状态模型
 * 
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Data
public class ProcessInstanceDetailsModel implements Serializable {

    private static final long serialVersionUID = 6067789541304948656L;

    /**
     * 主键
     */
    private String id;
    /**
     * 流程实例id
     */
    private String processInstanceId;
    /**
     * 流程编号
     */
    private String processSerialNumber;
    /**
     * 任务id
     */
    private String taskId;
    /**
     * 任务名称
     */
    private String taskName;
    /**
     * 意见内容
     */
    private String opinionContent;
    /**
     * 系统英文名称
     */
    private String systemName;
    /**
     * 系统中文名称
     */
    private String systemCnName;
    /**
     * 事项id
     */
    private String itemId;
    /**
     * 应用名称
     */
    private String appName;
    /**
     * 应用中文名称
     */
    private String appCnName;
    /**
     * 发送人id
     */
    private String senderId;
    /**
     * 发送人名称
     */
    private String senderName;
    /**
     * 办理人id
     */
    private String assigneeId;
    /**
     * 办理人名称
     */
    private String assigneeName;
    /**
     * 开始时间
     */
    private Date startTime;
    /**
     * 结束时间
     */
    private Date endTime;
    /**
     * 编号
     */
    private String serialNumber;
    /**
     * 文件标题
     */
    private String title;
    /**
     * 发起人
     */
    private String userName;
    /**
     * 办件状态
     */
    private String itembox;// todo待办，doing在办，done办结
    /**
     * 详情链接
     */
    private String url;

}
