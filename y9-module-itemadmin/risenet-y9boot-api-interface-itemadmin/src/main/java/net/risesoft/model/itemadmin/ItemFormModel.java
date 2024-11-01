package net.risesoft.model.itemadmin;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 流程详情数据
 *
 * @author qinman
 * @date 2024/11/01
 */
@Data
public class ItemFormModel implements Serializable {

    private static final long serialVersionUID = 4115564591151087066L;

    /**
     * 手机端表单id
     */
    private String formId;

    /**
     * 手机端表单名称
     */
    private String formName;

    /**
     * 表单json数据(手机端需要)
     */
    private String formJson;
}
