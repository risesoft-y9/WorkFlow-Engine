package y9.client.rest.itemadmin;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.itemadmin.BookMarkBindApi;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "BookMarkBindApiClient", name = "itemAdmin", url = "${y9.common.itemAdminBaseUrl}", path = "/services/rest/bookMarkBind")
public interface BookMarkBindApiClient extends BookMarkBindApi {

    /**
     * 根据模板和流程序列号查询模板的书签对应的值
     *
     * @param tenantId
     * @param wordTemplateId
     * @param processSerialNumber
     * @return
     */
    @Override
    @GetMapping("/getBookMarkData")
    Map<String, Object> getBookMarkData(@RequestParam("tenantId") String tenantId, @RequestParam("wordTemplateId") String wordTemplateId, @RequestParam("processSerialNumber") String processSerialNumber);
}
