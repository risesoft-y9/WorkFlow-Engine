package net.risesoft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 事项按钮类型类型
 * 
 * @author qinman
 * @date 2022/12/20
 */
@Getter
@AllArgsConstructor
public enum ItemButtonTypeEnum implements ValuedEnum<Integer> {
    /** 普通按钮 */
    COMMON(1, "普通按钮"),
    /** 发送按钮 */
    SEND(2, "发送按钮"),
    /** 退回按钮 */
    ROLLBACK(3, "退回按钮"),
    /** 重定位按钮 */
    REPOSITION(4, "重定位按钮");

    private final Integer value;
    private final String name;
}
