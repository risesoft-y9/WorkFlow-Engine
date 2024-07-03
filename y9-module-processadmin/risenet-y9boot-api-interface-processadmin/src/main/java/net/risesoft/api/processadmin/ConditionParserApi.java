package net.risesoft.api.processadmin;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.pojo.Y9Result;

public interface ConditionParserApi {

    /**
     * 解析表达式条件是否满足
     *
     * @param tenantId
     * @param conditionExpression 网关上的表达式
     * @param variables 流程变量
     * @return Y9Result<Boolean>
     */
    @GetMapping("/parser")
    Y9Result<Boolean> parser(@RequestParam("tenantId") String tenantId,
        @RequestParam("conditionExpression") String conditionExpression, @RequestBody Map<String, Object> variables);
}
