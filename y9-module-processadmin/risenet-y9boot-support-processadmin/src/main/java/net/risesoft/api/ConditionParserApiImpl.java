package net.risesoft.api;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.processadmin.ConditionParserApi;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.CustomConditionParser;
import net.risesoft.y9.FlowableTenantInfoHolder;

/**
 * 解析表达式条件接口
 *
 * @author qinman
 * @date 2023/11/01
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/conditionParser", produces = MediaType.APPLICATION_JSON_VALUE)
public class ConditionParserApiImpl implements ConditionParserApi {

    private final CustomConditionParser customConditionParser;

    /**
     * 解析表达式条件是否满足
     *
     * @param tenantId 租户id
     * @param conditionExpression 网关上的表达式
     * @param variables 流程变量
     * @return {@code Y9Result<Boolean>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Boolean> parser(@RequestParam String tenantId, @RequestParam String conditionExpression,
        @RequestBody Map<String, Object> variables) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        return Y9Result.success(customConditionParser.parser(conditionExpression, variables));
    }
}
