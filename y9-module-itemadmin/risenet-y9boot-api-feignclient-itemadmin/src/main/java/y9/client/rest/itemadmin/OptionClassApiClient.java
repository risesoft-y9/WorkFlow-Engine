package y9.client.rest.itemadmin;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.itemadmin.OptionClassApi;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "OptionClassApiClient", name = "${y9.service.itemAdmin.name:itemAdmin}",
    url = "${y9.service.itemAdmin.directUrl:}",
    path = "/${y9.service.itemAdmin.name:itemAdmin}/services/rest/optionClass")
public interface OptionClassApiClient extends OptionClassApi {

    /**
     * 获取数据字典列表
     *
     * @param tenantId
     * @param type
     * @return
     */
    @Override
    @GetMapping("/getOptionValueList")
    List<Map<String, Object>> getOptionValueList(@RequestParam("tenantId") String tenantId,
        @RequestParam("type") String type);

}
