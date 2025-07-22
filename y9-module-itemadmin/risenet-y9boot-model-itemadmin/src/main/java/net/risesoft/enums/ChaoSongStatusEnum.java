package net.risesoft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChaoSongStatusEnum implements ValuedEnum<Integer> {
    /** 新 */
    NEW(0, "新"),
    /** 已阅 */
    READ(1, "已阅"),
    /** 未阅 */
    UNREAD(2, "未阅");

    private final Integer value;
    private final String description;

    public static ChaoSongStatusEnum fromStatus(int status) {
        for (ChaoSongStatusEnum value : ChaoSongStatusEnum.values()) {
            if (value.getValue() == status) {
                return value;
            }
        }
        return ChaoSongStatusEnum.NEW;
    }
}
