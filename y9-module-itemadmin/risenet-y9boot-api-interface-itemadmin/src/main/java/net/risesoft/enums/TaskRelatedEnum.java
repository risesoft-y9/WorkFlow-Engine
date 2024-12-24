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
public enum TaskRelatedEnum {
    /** 办文说明 */
    BANWENSHUOMING("1", "办文说明"),
    /** 操作名称 */
    ACTIONNAME("2", "操作名称"),
    /** 子流程信息 */
    SUBINFO("3", "子流程信息"),
    /** 多步退回 */
    ROLLBACK("4", "多步退回");

    private final String value;
    private final String name;
}
