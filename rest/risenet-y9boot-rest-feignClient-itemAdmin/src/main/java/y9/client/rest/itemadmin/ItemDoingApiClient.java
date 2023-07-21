package y9.client.rest.itemadmin;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.itemadmin.ItemDoingApi;
import net.risesoft.model.itemadmin.ActRuDetailModel;
import net.risesoft.model.itemadmin.ItemPage;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "ItemDoingApiClient", name = "itemAdmin", url = "${y9.common.itemAdminBaseUrl}", path = "/services/rest/itemDoing")
public interface ItemDoingApiClient extends ItemDoingApi {

    /**
     * 查询在办任务数量
     *
     * @param tenantId
     * @param userId
     * @param systemName
     * @return
     * @throws Exception
     */
    @Override
    @GetMapping("/countByUserIdAndSystemName")
    int countByUserIdAndSystemName(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId, @RequestParam("systemName") String systemName) throws Exception;

    /**
     * 查询已办任务，以办理时间排序，即任务的结束时间(监控在办)
     *
     * @param tenantId
     * @param systemName
     * @param page
     * @param rows
     * @return
     * @throws Exception
     */
    @Override
    @GetMapping("/findBySystemName")
    ItemPage<ActRuDetailModel> findBySystemName(@RequestParam("tenantId") String tenantId, @RequestParam("systemName") String systemName, @RequestParam("page") Integer page, @RequestParam("rows") Integer rows) throws Exception;

    /**
     * 查询已办任务，以办理时间排序，即任务的结束时间(个人在办)
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
    ItemPage<ActRuDetailModel> findByUserIdAndSystemName(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId, @RequestParam("systemName") String systemName, @RequestParam("page") Integer page, @RequestParam("rows") Integer rows) throws Exception;

    /**
     * 查询已办任务，以办理时间排序，即任务的结束时间(监控在办搜索)
     *
     * @param tenantId
     * @param systemName
     * @param page
     * @param rows
     * @return
     * @throws Exception
     */
    @Override
    @GetMapping("/searchBySystemName")
    ItemPage<ActRuDetailModel> searchBySystemName(@RequestParam("tenantId") String tenantId, @RequestParam("systemName") String systemName, @RequestParam("tableName") String tableName, @RequestParam("searchMapStr") String searchMapStr, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows) throws Exception;

    /**
     * 查询已办任务，以办理时间排序，即任务的结束时间(个人在办搜索)
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
    ItemPage<ActRuDetailModel> searchByUserIdAndSystemName(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId, @RequestParam("systemName") String systemName, @RequestParam("tableName") String tableName, @RequestParam("searchMapStr") String searchMapStr,
        @RequestParam("page") Integer page, @RequestParam("rows") Integer rows) throws Exception;

}
