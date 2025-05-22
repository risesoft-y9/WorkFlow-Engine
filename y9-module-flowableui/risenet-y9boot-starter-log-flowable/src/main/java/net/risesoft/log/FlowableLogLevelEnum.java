package net.risesoft.log;

import lombok.Getter;

/**
 * 办件日志级别枚举
 *
 * @author qinman
 * @date 2025/05/20
 */
@Getter
public enum FlowableLogLevelEnum {
    /** 普通用户 */
    COMMON("普通用户"),
    /** 部门/委办局管理员 */
    DEPT("部门/委办局管理员"),
    /** 部门/委办局管理员 */
    DU_BAN("督办管理员"),
    /** 全局管理员 */
    ALL("全局管理员");

    private final String value;

    FlowableLogLevelEnum(String value) {
        this.value = value;
    }

}
