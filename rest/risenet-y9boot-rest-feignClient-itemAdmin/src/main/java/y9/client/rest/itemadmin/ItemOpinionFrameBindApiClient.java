package y9.client.rest.itemadmin;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.itemadmin.ItemOpinionFrameBindApi;
import net.risesoft.model.itemadmin.ItemOpinionFrameBindModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "ItemOpinionFrameBindApiClient", name = "itemAdmin", url = "${y9.common.itemAdminBaseUrl}", path = "/services/rest/itemOpinionFrameBind")
public interface ItemOpinionFrameBindApiClient extends ItemOpinionFrameBindApi {

    /**
     * 根据事项id获取所有绑定意见框
     *
     * @param tenantId 租户id
     * @param itemId 事项id
     * @return List&lt;OpinionFrameTaskRoleBindModel&gt;
     */
    @Override
    @GetMapping("/findByItemId")
    public List<ItemOpinionFrameBindModel> findByItemId(@RequestParam("tenantId") String tenantId, @RequestParam("itemId") String itemId);

    /**
     * 根据事项id和任务id获取绑定意见框
     *
     * @param tenantId
     * @param userId
     * @param itemId
     * @param processDefinitionId
     * @param taskDefKey
     * @return
     */
    @Override
    @GetMapping("/findByItemIdAndProcessDefinitionIdAndTaskDefKey")
    public List<ItemOpinionFrameBindModel> findByItemIdAndProcessDefinitionIdAndTaskDefKey(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId, @RequestParam("itemId") String itemId, @RequestParam("processDefinitionId") String processDefinitionId,
        @RequestParam("taskDefKey") String taskDefKey);

    /**
     * 根据事项id和任务id获取绑定意见框（包含角色信息）
     *
     * @param tenantId
     * @param userId
     * @param itemId
     * @param processDefinitionId
     * @param taskDefKey
     * @return
     */
    @Override
    @GetMapping("/findByItemIdAndProcessDefinitionIdAndTaskDefKeyContainRole")
    public List<ItemOpinionFrameBindModel> findByItemIdAndProcessDefinitionIdAndTaskDefKeyContainRole(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId, @RequestParam("itemId") String itemId, @RequestParam("processDefinitionId") String processDefinitionId,
        @RequestParam("taskDefKey") String taskDefKey);
}
