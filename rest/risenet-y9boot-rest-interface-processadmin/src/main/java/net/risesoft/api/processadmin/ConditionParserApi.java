package net.risesoft.api.processadmin;

import java.util.Map;

public interface ConditionParserApi {

    /**
     * 解析表达式条件是否满足
     * 
     * @param tenantId
     * @param conditionExpression 网关上的表达式
     * @param variables 流程变量
     * @return
     */
    Boolean parser(String tenantId, String conditionExpression, Map<String, Object> variables);
}
