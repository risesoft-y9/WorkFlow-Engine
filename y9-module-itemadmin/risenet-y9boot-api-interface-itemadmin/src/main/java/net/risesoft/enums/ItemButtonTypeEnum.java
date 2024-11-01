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
public enum ItemButtonTypeEnum {
    /** 普通按钮 */
    COMMON(1, "普通按钮"),
    /** 发送按钮 */
    SEND(2, "发送按钮");

    private final Integer value;
    private final String name;
}
