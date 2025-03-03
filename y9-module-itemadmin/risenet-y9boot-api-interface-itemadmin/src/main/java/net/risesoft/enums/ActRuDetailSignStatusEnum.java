package net.risesoft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 任务的签收状态
 * 
 * @author qinman
 * @date 2024/12/12
 */
@Getter
@AllArgsConstructor
public enum ActRuDetailSignStatusEnum {
    /** 不需要签收 */
    NONE(-1, "不需要签收"),
    /** 待签收 */
    TODO(0, "待签收"),
    /** 已签收 */
    DONE(1, "已签收");

    private final Integer value;
    private final String name;
}
