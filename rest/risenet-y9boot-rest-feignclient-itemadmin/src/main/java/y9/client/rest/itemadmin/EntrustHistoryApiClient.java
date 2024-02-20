package y9.client.rest.itemadmin;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.itemadmin.EntrustHistoryApi;
import net.risesoft.model.itemadmin.EntrustHistoryModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "EntrustHistoryApiClient", name = "${y9.service.itemAdmin.name:itemAdmin}", url = "${y9.service.itemAdmin.directUrl:}",
    path = "/${y9.service.itemAdmin.name:itemAdmin}/services/rest/entrustHistory")
public interface EntrustHistoryApiClient extends EntrustHistoryApi {

    /**
     * 获取某个用户的某个事项委托历史对象集合
     *
     * @param tenantId 租户id
     * @param userId 人员滴
     * @param ownerId 委托人id
     * @param itemId 事项粒度
     * @return List&lt;EntrustHistoryModel&gt;
     */
    @Override
    @GetMapping("/findByOwnerIdAndItemId")
    public List<EntrustHistoryModel> findByOwnerIdAndItemId(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("ownerId") String ownerId,
        @RequestParam("itemId") String itemId);

    /**
     * 获取某个用户的委托历史对象集合
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param ownerId 委托人id
     * @return List&lt;EntrustHistoryModel&gt;
     */
    @Override
    @GetMapping("/findOneByOwnerId")
    public List<EntrustHistoryModel> findOneByOwnerId(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("ownerId") String ownerId);
}
