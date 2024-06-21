package y9.client.rest.itemadmin;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.itemadmin.CalendarConfigApi;
import net.risesoft.model.itemadmin.CalendarConfigModel;
import net.risesoft.pojo.Y9Result;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "CalendarConfigApiClient", name = "${y9.service.itemAdmin.name:itemAdmin}", url = "${y9.service.itemAdmin.directUrl:}", path = "/${y9.service.itemAdmin.name:itemAdmin}/services/rest/calendarConfig")
public interface CalendarConfigApiClient extends CalendarConfigApi {

    /**
     * 获取年节假日配置信息
     *
     * @param tenantId
     * @param year
     * @return
     */
    @Override
    @GetMapping("/findByYear")
    public Y9Result<CalendarConfigModel> findByYear(@RequestParam("tenantId") String tenantId, @RequestParam("year") String year);

}
