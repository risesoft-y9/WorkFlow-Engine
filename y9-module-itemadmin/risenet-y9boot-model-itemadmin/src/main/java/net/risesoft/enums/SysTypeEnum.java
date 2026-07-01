package net.risesoft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 事项对接系统类型枚举
 * 
 * @author qinman
 * @date 2026/07/01
 */
@Getter
@AllArgsConstructor
public enum SysTypeEnum implements ValuedEnum<Integer> {
    IN("1", "内部系统"),
    OUT("2", "外部系统");

    private final String value;
    private final String name;
}
