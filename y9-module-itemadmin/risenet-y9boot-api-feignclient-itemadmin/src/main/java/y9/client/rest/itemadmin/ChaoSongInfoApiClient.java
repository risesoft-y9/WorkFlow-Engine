package y9.client.rest.itemadmin;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.itemadmin.ChaoSongInfoApi;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "ChaoSongInfoApiClient", name = "${y9.service.itemAdmin.name:itemAdmin}", url = "${y9.service.itemAdmin.directUrl:}", path = "/${y9.service.itemAdmin.name:itemAdmin}/services/rest/chaoSongInfo")
public interface ChaoSongInfoApiClient extends ChaoSongInfoApi {

}
