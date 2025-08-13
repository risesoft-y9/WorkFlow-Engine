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
public enum ActRuDetailSignStatusEnum implements ValuedEnum<Integer> {
    /** 不需要签收 */
    NONE(0, "不需要签收"),
    /** 待签收 */
    TODO(1, "待签收"),
    /** 已签收 */
    DONE(2, "已签收"),
    /** 拒绝签收 */
    REFUSE(3, "拒绝签收");

    private final Integer value;
    private final String name;
}
