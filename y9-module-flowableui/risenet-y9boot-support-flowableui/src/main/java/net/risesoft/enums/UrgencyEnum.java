package net.risesoft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 紧急程度
 * 
 * @author : qinman
 * @date : 2025-02-20
 * @since 9.6.8
 **/
@Getter
@AllArgsConstructor
public enum UrgencyEnum {

    /** 无 */
    NONE(0),
    /** 加急 */
    JIA_JI(1),
    /** 特急 */
    TE_JI(2);

    private final Integer value;
}
