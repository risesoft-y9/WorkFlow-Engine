package net.risesoft.service.impl;

import lombok.RequiredArgsConstructor;
import net.risesoft.service.CustomConditionParser;
import org.flowable.common.engine.impl.el.VariableContainerWrapper;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service(value = "customConditionParser")
public class CustomConditionParserImpl implements CustomConditionParser {

    private final ProcessEngineConfigurationImpl processEngineConfiguration;

    @Override
    public Boolean parser(String conditionExpression, Map<String, Object> variables) {
        try {
            VariableContainerWrapper variableContainer = new VariableContainerWrapper(variables);
            Object object = processEngineConfiguration.getExpressionManager().createExpression(conditionExpression)
                .getValue(variableContainer);
            return Boolean.parseBoolean(String.valueOf(object));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
