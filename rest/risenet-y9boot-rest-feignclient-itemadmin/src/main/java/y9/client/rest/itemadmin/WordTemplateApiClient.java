package y9.client.rest.itemadmin;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.itemadmin.WordTemplateApi;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "WordTemplateApiClient", name = "${y9.service.itemAdmin.name:itemAdmin}", url = "${y9.service.itemAdmin.directUrl:}",
    path = "/${y9.service.itemAdmin.name:itemAdmin}/services/rest/wordTemplate")
public interface WordTemplateApiClient extends WordTemplateApi {

    /**
     * 
     * Description: 根据唯一标示获取模板路径
     * 
     * @param id
     * @return
     */
    @Override
    @GetMapping("/getFilePathById")
    String getFilePathById(@RequestParam("id") String id);
}
