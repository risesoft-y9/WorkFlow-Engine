package net.risesoft.dto.itemadmin;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import lombok.Data;

/**
 * 发送选择的主题
 * 
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Data
public class UserChoiceDTO implements Serializable {

    private static final long serialVersionUID = -6022193332291668234L;

    @NotBlank(message = "唯一标识")
    private String id;

    @NotBlank(message = "类型")
    private String type;
}