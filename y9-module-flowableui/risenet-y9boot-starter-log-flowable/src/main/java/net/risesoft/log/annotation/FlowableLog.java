package net.risesoft.log.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.risesoft.log.FlowableLogLevelEnum;
import net.risesoft.log.FlowableOperationTypeEnum;

/**
 * 办件日志注解
 *
 * @author qinman
 * @date 2025/05/20
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface FlowableLog {

    /**
     * 是否记录日志
     * 
     * @return boolean 是否记录日志
     */
    boolean enable() default true;

    /**
     * 日志级别 {@link FlowableLogLevelEnum}
     * 
     * @return {@code FlowableLogLevelEnum} 日志级别
     */
    FlowableLogLevelEnum logLevel() default FlowableLogLevelEnum.COMMON;

    /**
     * 模块名称
     * 
     * @return String 模块名称
     */
    String moduleName() default "";

    /**
     * 操作名称
     * 
     * @return String 操作名称
     */
    String operationName() default "";

    /**
     * 操作类型
     * 
     * @return {@code OperationTypeEnum} 操作类型
     */
    FlowableOperationTypeEnum operationType() default FlowableOperationTypeEnum.BROWSE;

}
