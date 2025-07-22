package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

/**
 * 历程信息
 *
 * @author mengjuhua
 * @date 2024/06/26
 */
@Data
public class HistorySubProcessModel implements Serializable {

    private static final long serialVersionUID = -915011932260839215L;

    /** 执行示例 */
    private String id;

    /** 任务名称 */
    private String actionName;
}
