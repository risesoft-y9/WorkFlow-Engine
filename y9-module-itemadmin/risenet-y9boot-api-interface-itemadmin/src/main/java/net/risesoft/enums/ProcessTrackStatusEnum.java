package net.risesoft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 历程状态
 *
 * @author qinman
 * @date 2022/12/23
 */
@Getter
@AllArgsConstructor
public enum ProcessTrackStatusEnum {
    /**
     * 待签收
     */
    CLAIM(0, "待签收"),
    /**
     * 未阅
     */
    UNOPENED(1, "未阅"),
    /**
     * 待办-已签收
     */
    TODO_CLAIM(2, "待办-已签收"),
    /**
     * 待办
     */
    TODO(3, "待办"),
    /**
     * 已办
     */
    DONE(4, "已办"),
    /**
     * 取回
     */
    TAKE(5, "取回"),
    /**
     * 未签收
     */
    UNCLAIMED(6, "未签收");

    private final Integer value;
    private final String name;
}
