package y9.client.rest.processadmin;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.processadmin.ProcessDataCopyApi;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "ProcessDataCopyApiClient", name = "processAdmin", url = "${y9.common.processAdminBaseUrl}",
    path = "/services/rest/processDataCopy")
public interface ProcessDataCopyApiClient extends ProcessDataCopyApi {

    /**
     * 复制拷贝流程定义数据
     *
     * @param sourceTenantId 源租户id
     * @param targetTenantId 目标租户id
     * @param modelKey 定义key
     * @return
     */
    @Override
    @PostMapping("/copyModel")
    void copyModel(@RequestParam("sourceTenantId") String sourceTenantId,
        @RequestParam("targetTenantId") String targetTenantId, @RequestParam("modelKey") String modelKey);

}
