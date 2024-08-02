package y9.client.rest.itemadmin;

import org.springframework.cloud.openfeign.FeignClient;

import net.risesoft.api.itemadmin.ChaoSongApi;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "ChaoSongApiClient", name = "${y9.service.itemAdmin.name:itemAdmin}",
    url = "${y9.service.itemAdmin.directUrl:}", path = "/${y9.service.itemAdmin.name:itemAdmin}/services/rest/chaoSong")
public interface ChaoSongApiClient extends ChaoSongApi {

}
