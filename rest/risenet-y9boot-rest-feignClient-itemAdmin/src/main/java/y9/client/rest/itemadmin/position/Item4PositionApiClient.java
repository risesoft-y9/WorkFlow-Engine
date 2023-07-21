package y9.client.rest.itemadmin.position;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.itemadmin.position.Item4PositionApi;
import net.risesoft.model.itemadmin.ItemMappingConfModel;
import net.risesoft.model.itemadmin.ItemModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "Item4PositionApiClient", name = "itemAdmin", url = "${y9.common.itemAdminBaseUrl}", path = "/services/rest/item4Position")
public interface Item4PositionApiClient extends Item4PositionApi {

    /**
     * 
     * Description:查找所有事项
     * 
     * @param tenantId
     * @param systemName
     * @return
     */
    @Override
    @GetMapping("/findAll")
    public List<ItemModel> findAll(@RequestParam("tenantId") String tenantId, @RequestParam("systemName") String systemName);

    /**
     * 根据流程的定义Key查找对应的事项
     *
     * @param tenantId 租户Id
     * @param processDefinitionKey 流程定义Key
     * @return ItemModel
     */
    @Override
    @GetMapping("/findByProcessDefinitionKey")
    public ItemModel findByProcessDefinitionKey(@RequestParam("tenantId") String tenantId, @RequestParam("processDefinitionKey") String processDefinitionKey);

    /**
     * 获取所有事项列表
     *
     * @param tenantId 租户id
     * @return Listt&lt;ItemModel&gt;
     */
    @Override
    @GetMapping("/getAllItem")
    public List<ItemModel> getAllItem(@RequestParam("tenantId") String tenantId);

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
     * @param positionId
     * @return
     */
    @Override
    @GetMapping("/getFirstItem")
    String getFirstItem(@RequestParam("tenantId") String tenantId, @RequestParam("positionId") String positionId);

    /**
     * 根据事项id获取绑定的表单id
     *
     * @param tenantId 租户Id
     * @param itemId 事项id
     * @param processDefinitionKey 流程定义Key
     * @return
     */
    @Override
    @GetMapping("/getFormIdByItemId")
    public String getFormIdByItemId(@RequestParam("tenantId") String tenantId, @RequestParam("itemId") String itemId, @RequestParam("processDefinitionKey") String processDefinitionKey);

    /**
     * 获取新建事项列表
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @return List&lt;Map&lt;String, Object&gt;&gt;
     */
    @Override
    @GetMapping("/getItemList")
    public List<Map<String, Object>> getItemList(@RequestParam("tenantId") String tenantId, @RequestParam("positionId") String positionId);

    /**
     * 获取事项系统字段映射配置
     *
     * @param tenantId 租户Id
     * @param itemId 事项id
     * @param mappingId 系统标识
     * @return
     */
    @Override
    @GetMapping("/getItemMappingConf")
    public List<ItemMappingConfModel> getItemMappingConf(@RequestParam("tenantId") String tenantId, @RequestParam("itemId") String itemId, @RequestParam("mappingId") String mappingId);

    /**
     * 获取事项系统
     *
     * @param tenantId 租户id
     * @return List&lt;Map&lt;String, Object&gt;&gt;
     */
    @Override
    @GetMapping("/getItemSystem")
    public List<Map<String, Object>> getItemSystem(@RequestParam("tenantId") String tenantId);

    /**
     * 获取个人有权限事项列表
     *
     * @param tenantId
     * @param positionId
     * @return
     */
    @Override
    @GetMapping("/getMyItemList")
    public List<Map<String, Object>> getMyItemList(@RequestParam("tenantId") String tenantId, @RequestParam("positionId") String positionId);

    /**
     * 判断该租户是否有流程定义
     *
     * @param tenantId 租户Id
     * @param processDefinitionKey 流程定义Key
     * @return Boolean
     */
    @Override
    @GetMapping("/hasProcessDefinitionByKey")
    public Boolean hasProcessDefinitionByKey(@RequestParam("tenantId") String tenantId, @RequestParam("processDefinitionKey") String processDefinitionKey);

}
