package net.risesoft.dto.itemadmin;

import jakarta.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class OrderEntityDTO {

    @NotBlank(message = "ID不能为空")
    private String id;

    @NotBlank(message = "tabIndex不能为空")
    private Integer tabIndex;
}
