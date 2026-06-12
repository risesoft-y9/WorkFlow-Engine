package net.risesoft.dto.itemadmin;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

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

    // @NotBlank(message = "选择的用户不能为空")
    // private String userChoice;

    @NotEmpty(message = "选择的用户不能为空")
    private List<UserChoiceDTO> userChoice;

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
     * 流程变量
     */
    private Map<String, Object> variables;
}