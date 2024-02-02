package y9.client.rest.itemadmin;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.itemadmin.ItemApi;
import net.risesoft.model.itemadmin.ItemMappingConfModel;
import net.risesoft.model.itemadmin.ItemModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "ItemApiClient", name = "itemAdmin", url = "${y9.common.itemAdminBaseUrl}",
    path = "/services/rest/item")
public interface ItemApiClient extends ItemApi {

    /**
     * 查找所有事项
     * 
     * @param tenantId 租户id
     * @param userId 人员id
     * @param systemName 系统名称
     * @return Listt&lt;ItemModel&gt;
     */
    @Override
    @GetMapping("/findAll")
    public List<ItemModel> findAll(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("systemName") String systemName);

    /**
     * 根据流程的定义Key查找对应的事项
     *
     * @param tenantId 租户Id
     * @param processDefinitionKey 流程定义Key
     * @return ItemModel
     */
    @Override
    @GetMapping("/findByProcessDefinitionKey")
    public ItemModel findByProcessDefinitionKey(@RequestParam("tenantId") String tenantId,
        @RequestParam("processDefinitionKey") String processDefinitionKey);

    /**
     * 获取所有事项列表
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @return Listt&lt;ItemModel&gt;
     */
    @Override
    @GetMapping("/getAllItem")
    public List<ItemModel> getAllItem(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId);

    /**
     * 获取所有事项
     *
     * @param tenantId
     * @return
     */
    @Override
    @GetMapping("/getAllItemList")
    public List<ItemModel> getAllItemList(@RequestParam("tenantId") String tenantId);

    /**
     * 根据事项id获取事项
     *
     * @param tenantId 租户id
     * @param itemId 事项id
     * @return ItemModel
     */
    @Override
    @GetMapping("/getByItemId")
    public ItemModel getByItemId(@RequestParam("tenantId") String tenantId, @RequestParam("itemId") String itemId);

    /**
     * 获取有权限的首个事项id
     *
     * @param tenantId
     * @param userId
     * @return
     */
    @Override
    @GetMapping("/getFirstItem")
    String getFirstItem(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId);

    /**
     * 
     * Description: 根据表单唯一标示和流程定义KEY获取事项
     * 
     * @param tenantId
     * @param itemId
     * @param processDefinitionKey
     * @return
     */
    @Override
    @GetMapping("/getFormIdByItemId")
    public String getFormIdByItemId(@RequestParam("tenantId") String tenantId, @RequestParam("itemId") String itemId,
        @RequestParam("processDefinitionKey") String processDefinitionKey);

    /**
     * 获取新建事项列表
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @return List&lt;Map&lt;String, Object&gt;&gt;
     */
    @Override
    @GetMapping("/getItemList")
    public List<Map<String, Object>> getItemList(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId);

    /**
     * 分页获取事项列表
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @param page page
     * @param rows rows
     * @param name 事项名称
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @GetMapping("/getItemPageList")
    public Map<String, Object> getItemList(@RequestParam("tenantId") String tenantId,
        @RequestParam("personID") String personId, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows, @RequestParam("name") String name);

    /**
     * 
     * Description: 获取事项配置
     * 
     * @param tenantId
     * @param itemId
     * @param mappingId
     * @return
     */
    @Override
    @GetMapping("/getItemMappingConf")
    public List<ItemMappingConfModel> getItemMappingConf(@RequestParam("tenantId") String tenantId,
        @RequestParam("itemId") String itemId, @RequestParam("mappingId") String mappingId);

    /**
     * 获取事项系统
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @return List&lt;Map&lt;String, Object&gt;&gt;
     */
    @Override
    @GetMapping("/getItemSystem")
    public List<Map<String, Object>> getItemSystem(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId);

    /**
     * 获取个人有权限事项列表
     *
     * @param tenantId
     * @param id
     * @return
     */
    @Override
    @GetMapping("/getMyItemList")
    public List<Map<String, Object>> getMyItemList(@RequestParam("tenantId") String tenantId,
        @RequestParam("id") String id);

    /**
     * 判断该租户是否有流程定义
     *
     * @param tenantId 租户Id
     * @param processDefinitionKey 流程定义Key
     * @return Boolean
     */
    @Override
    @GetMapping("/hasProcessDefinitionByKey")
    public Boolean hasProcessDefinitionByKey(@RequestParam("tenantId") String tenantId,
        @RequestParam("processDefinitionKey") String processDefinitionKey);

}
