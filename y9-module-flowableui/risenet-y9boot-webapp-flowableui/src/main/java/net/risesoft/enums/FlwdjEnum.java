package net.risesoft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 非联网登记
 * 
 * @author : qinman
 * @date : 2025-02-20
 * @since 9.6.8
 **/
@Getter
@AllArgsConstructor
public enum FlwdjEnum {

    /** 是 */
    YES("[\"1\"]");

    private final String value;
}
