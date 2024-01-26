package net.risesoft.service;

import java.util.Map;

public interface CustomConditionParser {

    public Boolean parser(String conditionExpression, Map<String, Object> variables);
}
