package y9.client.rest.processadmin;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.processadmin.BpmnModelApi;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "BpmnModelApiClient", name = "processAdmin", url = "${y9.common.processAdminBaseUrl}",
    path = "/services/rest/bpmnModel")
public interface BpmnModelApiClient extends BpmnModelApi {

    /**
     * 生成流程图
     *
     * @param tenantId 租户id
     * @param processId processId
     * @return byte[]
     * @throws Exception Exception
     */
    @Override
    @PostMapping("/genProcessDiagram")
    byte[] genProcessDiagram(@RequestParam("tenantId") String tenantId, @RequestParam("processId") String processId)
        throws Exception;

    /**
     * 获取流程图模型
     *
     * @param tenantId 租户id
     * @param processInstanceId processInstanceId
     * @return Map
     * @throws Exception Exception
     */
    @Override
    @GetMapping("/getBpmnModel")
    Map<String, Object> getBpmnModel(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId) throws Exception;

    /**
     * 获取流程图数据
     *
     * @param tenantId
     * @param processInstanceId
     * @return
     * @throws Exception
     */
    @Override
    @GetMapping("/getFlowChart")
    Map<String, Object> getFlowChart(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId) throws Exception;

}
