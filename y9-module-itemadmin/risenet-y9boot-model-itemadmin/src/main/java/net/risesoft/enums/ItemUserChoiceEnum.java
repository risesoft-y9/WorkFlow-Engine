package net.risesoft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 事项权限类型
 * 
 * @author qinman
 * @date 2022/12/20
 */
@Getter
@AllArgsConstructor
public enum ItemUserChoiceEnum implements ValuedEnum<Integer> {
    /** 角色 */
    ROLE(1, "角色"),
    /** 部门 */
    DEPARTMENT(2, "部门"),
    /**  */
    ROLE_DYNAMIC(4, "动态角色"),
    /**  */
    POSITION(6, "岗位"),
    /**  */
    GROUP_CUSTOM(7, "自定义用户组");

    private final Integer value;
    private final String name;

    public static ItemUserChoiceEnum valueOf(Integer value) {
        for (ItemUserChoiceEnum enumValue : ItemUserChoiceEnum.values()) {
            if (enumValue.getValue().equals(value)) {
                return enumValue;
            }
        }
        throw new IllegalArgumentException("No matching ItemPermissionEnum for value: " + value);
    }
}
