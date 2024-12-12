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
public enum SignStatusEnum {
    /** 未开始 */
    NOTSTART(0, "未开始"),
    /** 主办司局 */
    MAIN(1, "主办司局"),
    /** 会签司局 */
    SUB(2, "会签司局");

    private final Integer value;
    private final String name;
}
