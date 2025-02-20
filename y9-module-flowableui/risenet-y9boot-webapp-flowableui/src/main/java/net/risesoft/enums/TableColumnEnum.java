package net.risesoft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : qinman
 * @date : 2025-02-20
 * @since 9.6.8
 **/
@Getter
@AllArgsConstructor
public enum TableColumnEnum {

    /** 限时督办 */
    DBSX("dbsx"),
    /** 条码号 */
    TMH("tmh"),
    /** 紧急程度 */
    JJCD("jjcd"),
    /** 非联网登记 */
    FLWDJ("flwdj");

    private final String value;
}
