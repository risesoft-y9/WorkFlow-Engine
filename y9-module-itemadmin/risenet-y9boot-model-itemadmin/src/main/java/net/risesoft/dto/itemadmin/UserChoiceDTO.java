package net.risesoft.dto.itemadmin;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;
import lombok.experimental.Accessors;

import net.risesoft.enums.ItemUserChoiceEnum;

/**
 * 发送选择的主题
 * 
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Data
@Accessors(chain = true)
public class UserChoiceDTO implements Serializable {

    private static final long serialVersionUID = -6022193332291668234L;

    @NotBlank(message = "唯一标识")
    private String id;

    @NotNull(message = "类型")
    private ItemUserChoiceEnum type;
}