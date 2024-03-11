package y9.client.rest.itemadmin;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.itemadmin.ItemTodoApi;
import net.risesoft.model.itemadmin.ActRuDetailModel;
import net.risesoft.model.itemadmin.ItemPage;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "ItemTodoApiClient", name = "${y9.service.itemAdmin.name:itemAdmin}",
    url = "${y9.service.itemAdmin.directUrl:}", path = "/${y9.service.itemAdmin.name:itemAdmin}/services/rest/itemTodo")
public interface ItemTodoApiClient extends ItemTodoApi {

    /**
     * 查询待办任务数量
     *
     * @param tenantId
     * @param userId
     * @param systemName
     * @return
     * @throws Exception
     */
    @Override
    @GetMapping("/countByUserIdAndSystemName")
    int countByUserIdAndSystemName(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("systemName") String systemName) throws Exception;

    /**
     * 获取待办任务，以发送时间排序
     *
     * @param tenantId
     * @param userId
     * @param systemName
     * @param page
     * @param rows
     * @return
     * @throws Exception
     */
    @Override
    @GetMapping("/findByUserIdAndSystemName")
    ItemPage<ActRuDetailModel> findByUserIdAndSystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("systemName") String systemName,
        @RequestParam("page") Integer page, @RequestParam("rows") Integer rows) throws Exception;

    /**
     * 查询待办任务，以发送时间排序
     *
     * @param tenantId
     * @param userId
     * @param systemName
     * @param page
     * @param rows
     * @return
     * @throws Exception
     */
    @Override
    @GetMapping("/searchByUserIdAndSystemName")
    ItemPage<ActRuDetailModel> searchByUserIdAndSystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("systemName") String systemName,
        @RequestParam("tableName") String tableName, @RequestParam("searchMapStr") String searchMapStr,
        @RequestParam("page") Integer page, @RequestParam("rows") Integer rows) throws Exception;

}
