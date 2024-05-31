package net.risesoft.exception;

import lombok.AllArgsConstructor;

import static net.risesoft.exception.FlowableUIErrorCodeConsts.PARAM_MODULE_CODE;

/**
 * 全局的错误代码
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/14
 */
@AllArgsConstructor
public enum FlowableUIErrorCodeEnum implements ErrorCode {

    AUTH_TENANTID_NOT_FOUND(PARAM_MODULE_CODE, 1, "请求头中未找到[auth-tenantId]"),

    AUTH_USERID_NOT_FOUND(PARAM_MODULE_CODE, 1, "请求头中未找到[auth-userId]"),

    AUTH_POSITIONID_NOT_FOUND(PARAM_MODULE_CODE, 1, "请求头中未找到[auth-positionId]"),

    PERSON_NOT_FOUND(PARAM_MODULE_CODE, 1, "根据请求头中的[auth-userId]未找到用户信息"),

    POSITION_NOT_FOUND(PARAM_MODULE_CODE, 1, "根据请求头中的[auth-positionId]未找到岗位信息"),

    ;

    private final int moduleCode;
    private final int moduleErrorCode;
    private final String description;

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public int moduleCode() {
        return this.moduleCode;
    }

    @Override
    public int moduleErrorCode() {
        return this.moduleErrorCode;
    }

    @Override
    public int systemCode() {
        return GlobalErrorCodeConsts.SYSTEM_CODE;
    }
}
