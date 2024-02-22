package y9.client.rest.itemadmin;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.itemadmin.DataCenterApi;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "DataCenterApiClient", name = "${y9.service.itemAdmin.name:itemAdmin}", url = "${y9.service.itemAdmin.directUrl:}",
    path = "/${y9.service.itemAdmin.name:itemAdmin}/services/rest/dataCenter")
public interface DataCenterApiClient extends DataCenterApi {

    /**
     * 保存办结数据到数据中心
     *
     * @param processInstanceId 流程实例id
     * @param tenantId 租户id
     * @param userId 人员id
     * @return boolean
     */
    @Override
    @PostMapping("/saveToDateCenter")
    public boolean saveToDateCenter(@RequestParam("processInstanceId") String processInstanceId,
        @RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId);

}
