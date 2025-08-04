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
     * 未签收,等待签收
     */
    WAITCLAIM(0, "未签收"),
    /**
     * 未阅
     */
    UNOPENED(1, "未阅"),
    /**
     * 已签收
     */
    CLAIMED(2, "已签收"),
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
     * 已被其他人签收或者被取回导致不能进行签收
     */
    UNCLAIMED(6, "-");

    private final Integer value;
    private final String name;
}
