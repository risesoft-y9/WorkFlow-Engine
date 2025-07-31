package net.risesoft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 动态角色权限范围
 * 
 * @author qinman
 * @date 2022/12/20
 */
@Getter
@AllArgsConstructor
public enum DynamicRoleRangesEnum implements ValuedEnum<Integer> {
    /** 无限制 */
    NONE(0, "无限制"),
    /** 科室 */
    DEPT(1, "科室"),
    /** 委办局 */
    BUREAU(2, "委办局");

    private final Integer value;
    private final String name;
}
