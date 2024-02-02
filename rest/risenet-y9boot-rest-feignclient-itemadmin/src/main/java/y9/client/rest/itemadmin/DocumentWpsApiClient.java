package y9.client.rest.itemadmin;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.itemadmin.DocumentWpsApi;
import net.risesoft.model.itemadmin.DocumentWpsModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "DocumentWpsApiClient", name = "itemAdmin", url = "${y9.common.itemAdminBaseUrl}",
    path = "/services/rest/documentWps")
public interface DocumentWpsApiClient extends DocumentWpsApi {

    /**
     * 根据id获取正文信息
     *
     * @param tenantId
     * @param id
     * @return
     */
    @Override
    @GetMapping("/findById")
    DocumentWpsModel findById(@RequestParam("tenantId") String tenantId, @RequestParam("id") String id);

    /**
     * 根据流程编号获取正文
     *
     * @param tenantId
     * @param processSerialNumber
     * @return
     */
    @Override
    @GetMapping("/findByProcessSerialNumber")
    DocumentWpsModel findByProcessSerialNumber(@RequestParam("tenantId") String tenantId,
        @RequestParam("processSerialNumber") String processSerialNumber);

    /**
     * 保存正文信息
     *
     * @param tenantId
     * @param documentWps
     */
    @Override
    @PostMapping(value = "/saveDocumentWps", consumes = MediaType.APPLICATION_JSON_VALUE)
    void saveDocumentWps(@RequestParam("tenantId") String tenantId, @RequestBody DocumentWpsModel documentWps);

    /**
     * 保存正文内容状态
     *
     * @param tenantId
     * @param processSerialNumber
     * @param hasContent
     */
    @Override
    @PostMapping("/saveWpsContent")
    void saveWpsContent(@RequestParam("tenantId") String tenantId,
        @RequestParam("processSerialNumber") String processSerialNumber, @RequestParam("hasContent") String hasContent);
}
