package net.risesoft.dto.itemadmin;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;

import lombok.Data;

/**
 * 意见模型类
 * 
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Data
public class OpinionDTO implements Serializable {

    private static final long serialVersionUID = -4486342774483521065L;

    /**
     * 唯一标示
     */
    private String id;

    @NotBlank(message = "流程系列号不能为空")
    private String processSerialNumber;

    @NotBlank(message = "意见框标识不能为空")
    private String opinionFrameMark;

    @NotBlank(message = "意见内容不能为空")
    private String content;

    private String processInstanceId;

    private String taskId;

    private boolean mobile = false;
}