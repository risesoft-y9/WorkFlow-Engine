package net.risesoft.controller.vo;

import java.io.Serializable;

import lombok.Data;

/**
 * 表单信息
 *
 * @author mengjuhua
 * @date 2024/07/12
 */
@Data
public class Y9FormVO implements Serializable {

    private static final long serialVersionUID = -2554600793070326169L;
    /** 表单id */
    private String formId;
    /** 表单名称 */
    private String formName;
}
