package net.risesoft.controller.vo;

import java.io.Serializable;

import lombok.Data;

/**
 * Y9表单与事项流程任务定义信息
 *
 * @author mengjuhua
 * @date 2024/07/12
 */
@Data
public class Y9FormItemBinVO implements Serializable {
    private static final long serialVersionUID = 1692947675114605922L;

    /**
     * 任务名称（线上名称存在这里就是线的名字）
     */
    private String taskDefName;

    /** PC端表单绑定名称 */
    private String eformNames;

    /** 手机端绑定表单名称 */
    private String mobileFormName;

    /** 节点key */
    private String taskDefKey;

}
