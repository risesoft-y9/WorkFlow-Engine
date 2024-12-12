package net.risesoft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 会签状态
 * 
 * @author qinman
 * @date 2024/12/12
 */
@Getter
@AllArgsConstructor
public enum SignDeptDetailStatusEnum {
    /** 在办 */
    DOING(1, "在办"),
    /** 正常办结 */
    DONE(2, "正常办结"),
    /** 退回办结 */
    ROLLBACK(3, "退回办结"),
    /** 减签 */
    DELETED(4, "减签");

    private final Integer value;
    private final String name;
}
