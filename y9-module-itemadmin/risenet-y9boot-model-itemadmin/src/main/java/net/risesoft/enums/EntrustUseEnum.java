package net.risesoft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EntrustUseEnum implements ValuedEnum<Integer> {
    /** 待使用 */
    TODO(0, "待使用"),
    /** 正在使用 */
    DOING(1, "正在使用"),
    /** 已使用 */
    DONE(2, "已使用");

    private final Integer value;
    private final String description;
}
