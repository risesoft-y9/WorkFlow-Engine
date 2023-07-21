package y9.client.rest.processadmin;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.processadmin.HistoricActivityApi;
import net.risesoft.model.processadmin.HistoricActivityInstanceModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "HistoricActivityApiClient", name = "processAdmin", url = "${y9.common.processAdminBaseUrl}", path = "/services/rest/historicActivity")
public interface HistoricActivityApiClient extends HistoricActivityApi {

    /**
     * 根据流程实例获取历史节点实例
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return List&lt;HistoricActivityInstanceModel&gt;
     */
    @Override
    @GetMapping("/getByProcessInstanceId")
    public List<HistoricActivityInstanceModel> getByProcessInstanceId(@RequestParam("tenantId") String tenantId, @RequestParam("processInstanceId") String processInstanceId);
}
