package net.risesoft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 接口参数类型
 *
 * @author qinman
 * @date 2022/12/20
 */
@Getter
@AllArgsConstructor
public enum ItemInterfaceTypeEnum implements ValuedEnum<String> {

    // 参数类型
    /** Params */
    PARAMS("Params"),
    /** Headers */
    HEADERS("Headers"),
    /** Body */
    BODY("Body"),

    // 绑定类型
    /** Response */
    INTERFACE_RESPONSE("Response"),
    /** Request */
    INTERFACE_REQUEST("Request"),

    // 方法类型
    /** GET */
    METHOD_GET("GET"),
    /** POST */
    METHOD_POST("POST");

    private final String value;
}
