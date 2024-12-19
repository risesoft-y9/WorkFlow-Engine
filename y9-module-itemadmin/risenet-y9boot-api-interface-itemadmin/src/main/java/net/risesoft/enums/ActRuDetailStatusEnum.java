package net.risesoft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 会签状态
 * 
 * @author qinman
 * @date 2024/12/12
 */
@Getter
@AllArgsConstructor
public enum ActRuDetailStatusEnum {
    /** 待办 */
    TODO(0, "待办"),
    /** 在办 */
    DOING(1, "在办");

    private final Integer value;
    private final String name;
}
