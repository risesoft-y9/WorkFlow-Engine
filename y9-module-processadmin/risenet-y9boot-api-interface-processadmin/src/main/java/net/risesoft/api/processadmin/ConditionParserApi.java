package net.risesoft.api.processadmin;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.pojo.Y9Result;

/**
 * 解析表达式条件接口
 *
 * @author qinman
 * @date 2023/11/01
 */
public interface ConditionParserApi {

    /**
     * 解析表达式条件是否满足
     *
     * @param tenantId 租户id
     * @param conditionExpression 网关上的表达式
     * @param variables 流程变量
     * @return {@code Y9Result<Boolean>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @GetMapping("/parser")
    Y9Result<Boolean> parser(@RequestParam("tenantId") String tenantId,
        @RequestParam("conditionExpression") String conditionExpression, @RequestBody Map<String, Object> variables);
}
