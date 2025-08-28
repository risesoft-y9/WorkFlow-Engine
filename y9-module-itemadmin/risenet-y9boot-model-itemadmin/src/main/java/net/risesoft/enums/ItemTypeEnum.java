package net.risesoft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 事项类型
 * 
 * @author qinman
 * @date 2025/08/28
 */
@Getter
@AllArgsConstructor
public enum ItemTypeEnum {

    NONE("", "无"),

    MAIN("main", "主"),

    SUB("sub", "子");

    private final String value;
    private final String name;

    public static ItemTypeEnum fromString(String itemType) {
        if (itemType == null) {
            return ItemTypeEnum.NONE;
        }
        for (ItemTypeEnum itemTypeEnum : ItemTypeEnum.values()) {
            if (itemTypeEnum.value.equalsIgnoreCase(itemType)) {
                return itemTypeEnum;
            }
        }
        return ItemTypeEnum.NONE;
    }
}
