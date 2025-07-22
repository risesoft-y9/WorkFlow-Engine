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
public enum DynamicRoleKindsEnum {
    /** 无 */
    NONE(0, "无"),
    /** 人员 */
    DEPT_PROP_CATEGORY(1, "部门配置分类"),
    /** 角色 */
    ROLE(2, "角色");

    private final Integer value;
    private final String name;
}
