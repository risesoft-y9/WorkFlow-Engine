package net.risesoft.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * 接口返回值结构
 *
 * @date 2023-08-02
 */
@Setter
@Getter
public class ReturnResponse<T> implements Serializable {
    public static final int SUCCESS_CODE = 0;
    public static final int FAILURE_CODE = 1;
    public static final String SUCCESS_MSG = "SUCCESS";
    public static final String FAILURE_MSG = "FAILURE";
    private static final long serialVersionUID = 313975329998789878L;
    /**
     * 返回状态 0. 成功 1. 失败
     */
    private int code;

    /**
     * 返回状态描述 XXX成功 XXX失败
     */
    private String msg;

    private transient T content;

    public ReturnResponse(int code, String msg, T content) {
        this.code = code;
        this.msg = msg;
        this.content = content;
    }

    public static ReturnResponse<Object> failure(String errMsg) {
        return new ReturnResponse<>(FAILURE_CODE, errMsg, null);
    }

    public static ReturnResponse<Object> failure() {
        return failure(FAILURE_MSG);
    }

    public static ReturnResponse<Object> success() {
        return success(null);
    }

    public static ReturnResponse<Object> success(Object content) {
        return new ReturnResponse<>(SUCCESS_CODE, SUCCESS_MSG, content);
    }

    public boolean isSuccess() {
        return SUCCESS_CODE == code;
    }

    public boolean isFailure() {
        return !isSuccess();
    }

}
