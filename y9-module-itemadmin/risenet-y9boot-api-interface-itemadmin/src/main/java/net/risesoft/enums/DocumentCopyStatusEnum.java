package net.risesoft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 办件副本状态枚举
 * 
 * @author qinman
 * @date 2025/02/12
 */
@Getter
@AllArgsConstructor
public enum DocumentCopyStatusEnum {
    /** 待填写意见 */
    TODO_SIGN(1, "待填写意见"),
    /** 已填写意见 */
    SIGN(2, "已填写意见"),
    /** 已取消 */
    CANCEL(8, "已取消"),
    /** 已删除 */
    DELETE(9, "已删除");

    private final Integer value;
    private final String name;
}
