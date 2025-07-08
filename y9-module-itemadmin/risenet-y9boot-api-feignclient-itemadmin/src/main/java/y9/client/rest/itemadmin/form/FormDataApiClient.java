package y9.client.rest.itemadmin.form;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.itemadmin.form.FormDataApi;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "FormDataApiClient", name = "${y9.service.itemAdmin.name:itemAdmin}",
    url = "${y9.service.itemAdmin.directUrl:}",
    path = "/${y9.service.itemAdmin.name:server-itemadmin}/services/rest/formData")
public interface FormDataApiClient extends FormDataApi {

}
