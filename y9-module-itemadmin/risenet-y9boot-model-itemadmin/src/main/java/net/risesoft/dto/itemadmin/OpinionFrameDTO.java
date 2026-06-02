package net.risesoft.dto.itemadmin;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class OpinionFrameDTO implements Serializable {

    private static final long serialVersionUID = -5370199683918476579L;

    @NotBlank(message = "流程编号不能为空")
    private String processSerialNumber;

    @NotBlank(message = "意见框标识不能为空")
    private String opinionFrameMark;

    @NotBlank(message = "列表类型不能为空")
    private String itembox;

    @NotBlank(message = "事项id不能为空")
    private String itemId;

    private String taskId;

    private String taskDefinitionKey;
}
