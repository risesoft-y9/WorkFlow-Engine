package net.risesoft.dto.itemadmin;

import java.io.Serializable;
import java.util.List;

import jakarta.validation.constraints.NotEmpty;

import lombok.Data;

@Data
public class IdsDTO implements Serializable {

    private static final long serialVersionUID = 4702426412988814209L;

    @NotEmpty(message = "ID列表不能为空")
    private List<String> ids;
}
