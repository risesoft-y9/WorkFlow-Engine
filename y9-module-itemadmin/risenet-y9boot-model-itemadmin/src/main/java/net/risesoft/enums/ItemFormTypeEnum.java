package net.risesoft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 事项表单类型
 *
 * @author qinman
 * @date 2022/12/20
 */
@Getter
@AllArgsConstructor
public enum ItemFormTypeEnum implements ValuedEnum<Integer> {
    /** 主表单 */
    MAINFORM(1, "mainForm"),
    /** 前置表单 */
    PREFORM(2, "preForm");

    private final Integer value;
    private final String name;
}
