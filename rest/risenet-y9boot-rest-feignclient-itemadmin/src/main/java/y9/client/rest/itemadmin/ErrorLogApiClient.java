package y9.client.rest.itemadmin;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.itemadmin.ErrorLogApi;
import net.risesoft.model.itemadmin.ErrorLogModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "ErrorLogApiClient", name = "itemAdmin", url = "${y9.common.itemAdminBaseUrl}",
    path = "/services/rest/errorLog")
public interface ErrorLogApiClient extends ErrorLogApi {

    /**
     * 保存错误日志
     *
     * @param tenantId
     * @param errorLogModel
     */
    @Override
    @PostMapping(value = "/saveErrorLog", consumes = MediaType.APPLICATION_JSON_VALUE)
    void saveErrorLog(@RequestParam("tenantId") String tenantId, @RequestBody ErrorLogModel errorLogModel);

}
