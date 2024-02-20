package y9.client.rest.itemadmin;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.itemadmin.EntrustApi;
import net.risesoft.model.itemadmin.EntrustModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "EntrustApiClient", name = "${y9.service.itemAdmin.name:itemAdmin}", url = "${y9.service.itemAdmin.directUrl:}",
    path = "/${y9.service.itemAdmin.name:itemAdmin}/services/rest/entrust")
public interface EntrustApiClient extends EntrustApi {

    /**
     * 销假:删除ownerId所有的正在使用中的、或者已经过期的出差委托，并放入委托历史表
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param ownerId 委托人id
     * @throws Exception Exception
     */
    @Override
    @PostMapping("/destroyEntrust")
    public void destroyEntrust(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("ownerId") String ownerId) throws Exception;

    /**
     * 销假:根据唯一标示删除正在使用中的、或者已经过期的出差委托，并放入委托历史表
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param id 唯一标识
     * @throws Exception Exception
     */
    @Override
    @PostMapping("/destroyEntrustById")
    public void destroyEntrustById(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("id") String id) throws Exception;

    /**
     * 销假:删除某个人的某个事项的正在使用中的、或者已经过期的出差委托，并放入委托历史表
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param ownerId 委托人id
     * @param itemId 事项id
     * @throws Exception Exception
     */
    @Override
    @PostMapping("/destroyEntrustByOwnerIdAndItemId")
    public void destroyEntrustByOwnerIdAndItemId(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("ownerId") String ownerId,
        @RequestParam("itemId") String itemId) throws Exception;

    /**
     * 根据用户唯一标示和事项唯一标示查找委托对象
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param ownerId 委托人id
     * @param itemId 事项id
     * @return EntrustModel
     * @throws Exception Exception
     */
    @Override
    @GetMapping("/findOneByOwnerIdAndItemId")
    public EntrustModel findOneByOwnerIdAndItemId(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("ownerId") String ownerId,
        @RequestParam("itemId") String itemId) throws Exception;

    /**
     * 根据用户唯一标示和事项唯一标示查找委托对象
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param ownerId 委托人id
     * @param itemId 事项id
     * @param currentTime 当前时间
     * @return EntrustModel
     * @throws Exception Exception
     */
    @Override
    @GetMapping("/findOneByOwnerIdAndItemIdAndTime")
    public EntrustModel findOneByOwnerIdAndItemIdAndTime(@RequestParam("tenantId") String tenantId,
        @RequestParam("ownerId") String ownerId, @RequestParam("itemId") String itemId,
        @RequestParam("currentTime") String currentTime) throws Exception;

    /**
     * 通过唯一标示获取委托对象
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param id id
     * @return EntrustModel
     * @throws Exception Exception
     */
    @Override
    @GetMapping("/getById")
    public EntrustModel getById(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("id") String id) throws Exception;

    /**
     * 获取任务委托人id
     *
     * @param tenantId
     * @param taskId
     * @return
     */
    @Override
    @GetMapping("/getEntrustOwnerId")
    public String getEntrustOwnerId(@RequestParam("tenantId") String tenantId, @RequestParam("taskId") String taskId);

    /**
     * 获取事项列表
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param ownerId 委托人id
     * @param page page
     * @param rows rows
     * @return Map&lt;String, Object&gt;
     * @throws Exception Exception
     */
    @Override
    @GetMapping("/getItemList")
    public Map<String, Object> getItemList(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("ownerId") String ownerId,
        @RequestParam("page") Integer page, @RequestParam("rows") Integer rows) throws Exception;

    /**
     * 查询任务是否有委托
     *
     * @param tenantId
     * @param taskId
     * @return
     */
    @Override
    @GetMapping("/haveEntrustDetail")
    boolean haveEntrustDetail(@RequestParam("tenantId") String tenantId, @RequestParam("taskId") String taskId);

    /**
     * 删除id出差委托，不会放入委托历史表
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param id 唯一标识
     * @throws Exception Exception
     */
    @Override
    @PostMapping("/removeEntrust")
    public void removeEntrust(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("id") String id) throws Exception;

    /**
     * 保存委托详情
     *
     * @param tenantId
     * @param processInstanceId
     * @param taskId
     * @param ownerId
     * @param assigneeId
     */
    @Override
    @PostMapping("/saveEntrustDetail")
    void saveEntrustDetail(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId, @RequestParam("taskId") String taskId,
        @RequestParam("ownerId") String ownerId, @RequestParam("assigneeId") String assigneeId);

    /**
     * 保存或者更新委托对象
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param entrustModel 实体类（EntrustModel）
     * @throws Exception Exception
     */
    @Override
    @PostMapping(value = "/saveOrUpdate", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void saveOrUpdate(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestBody EntrustModel entrustModel) throws Exception;
}
