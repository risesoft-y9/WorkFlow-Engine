package net.risesoft.dto.itemadmin;

import java.util.List;

import javax.validation.constraints.NotEmpty;

import lombok.Data;

@Data
public class IdsDTO {
    @NotEmpty(message = "ID列表不能为空")
    private List<String> ids;
}
