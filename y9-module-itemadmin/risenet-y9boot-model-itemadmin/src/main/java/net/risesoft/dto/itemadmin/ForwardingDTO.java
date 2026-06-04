package net.risesoft.dto.itemadmin;

import java.io.Serializable;
import java.util.Map;

import jakarta.validation.constraints.NotBlank;

import lombok.Data;

/**
 * 发送传输对象
 */
@Data
public class ForwardingDTO implements Serializable {

    private static final long serialVersionUID = 6446368653243126468L;

    @NotBlank(message = "流程编号不能为空")
    private String processSerialNumber;

    @NotBlank(message = "事项ID不能为空")
    private String itemId;

    @NotBlank(message = "用户选择不能为空")
    private String userChoice;

    @NotBlank(message = "目标任务ID不能为空")
    private String routeToTaskId;

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 主办处理
     */
    private String sponsorHandle;

    /**
     * 主办人GUID
     */
    private String sponsorGuid;

    /**
     * 是否发送短信
     */
    private String isSendSms;

    /**
     * 是否署名
     */
    private String isShuMing;

    /**
     * 短信内容
     */
    private String smsContent;

    /**
     * 流程变量
     */
    private Map<String, Object> variables;
}