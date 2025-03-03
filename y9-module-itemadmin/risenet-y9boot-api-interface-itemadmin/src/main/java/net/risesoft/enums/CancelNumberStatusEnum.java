package net.risesoft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 编号状态
 * 
 * @author qinman
 * @date 2024/12/12
 */
@Getter
@AllArgsConstructor
public enum CancelNumberStatusEnum {
    /** 编号 */
    NUMBER(0, "编号"),
    /** 取消编号 */
    CANCEL(1, "取消编号");

    private final Integer value;
    private final String name;
}
