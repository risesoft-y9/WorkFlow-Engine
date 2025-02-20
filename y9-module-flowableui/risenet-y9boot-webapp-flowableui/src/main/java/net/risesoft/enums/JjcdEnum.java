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
public enum JjcdEnum {

    /** 无 */
    NONE(0),
    /** 加急 */
    JIAJI(1),
    /** 特急 */
    TEJI(2);

    private final Integer value;
}
