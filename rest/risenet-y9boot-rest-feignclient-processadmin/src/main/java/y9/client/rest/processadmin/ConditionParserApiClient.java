package y9.client.rest.processadmin;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.processadmin.ConditionParserApi;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "ConditionParserApiClient", name = "${y9.service.processAdmin.name:processAdmin}", url = "${y9.service.processAdmin.directUrl:}", path = "/${y9.service.processAdmin.name:processAdmin}/services/rest/conditionParser")
public interface ConditionParserApiClient extends ConditionParserApi {

    /**
     * 解析表达式条件是否满足
     * 
     * @param tenantId
     * @param conditionExpression 网关上的表达式
     * @param variables 流程变量
     * @return
     */
    @Override
    @GetMapping("/parser")
    Boolean parser(@RequestParam("tenantId") String tenantId, @RequestParam("conditionExpression") String conditionExpression, @RequestBody Map<String, Object> variables);
}