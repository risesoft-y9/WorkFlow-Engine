package net.risesoft.api.itemadmin.position;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.ItemListModel;
import net.risesoft.model.itemadmin.ItemMappingConfModel;
import net.risesoft.model.itemadmin.ItemModel;
import net.risesoft.model.itemadmin.ItemSystemListModel;
import net.risesoft.pojo.Y9Result;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface Item4PositionApi {

    /**
     * 根据系统名称获取事项
     *
     * @param tenantId 租户id
     * @param systemName 系统名称
     * @return Y9Result<List<ItemModel>>
     */
    @GetMapping("/findAll")
    Y9Result<List<ItemModel>> findAll(@RequestParam("tenantId") String tenantId,
        @RequestParam("systemName") String systemName);

    /**
     * 根据流程的定义Key查找对应的事项
     *
     * @param tenantId 租户Id
     * @param processDefinitionKey 流程定义Key
     * @return Y9Result<ItemModel>
     */
    @GetMapping("/findByProcessDefinitionKey")
    Y9Result<ItemModel> findByProcessDefinitionKey(@RequestParam("tenantId") String tenantId,
        @RequestParam("processDefinitionKey") String processDefinitionKey);

    /**
     * 获取所有事项列表
     *
     * @param tenantId 租户id
     * @return Y9Result<List<ItemModel>>
     */
    @GetMapping("/getAllItem")
    Y9Result<List<ItemModel>> getAllItem(@RequestParam("tenantId") String tenantId);

    /**
     * 获取所有事项
     *
     * @param tenantId 租户id
     * @return Y9Result<List<ItemModel>>
     */
    @GetMapping("/getAllItemList")
    Y9Result<List<ItemModel>> getAllItemList(@RequestParam("tenantId") String tenantId);

    /**
     * 根据事项id获取事项
     *
     * @param tenantId 租户id
     * @param itemId 事项id
     * @return Y9Result<ItemModel>
     */
    @GetMapping("/getByItemId")
    Y9Result<ItemModel> getByItemId(@RequestParam("tenantId") String tenantId, @RequestParam("itemId") String itemId);

    /**
     * 获取有权限的首个事项id
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @return Y9Result<String>
     */
    @GetMapping("/getFirstItem")
    Y9Result<String> getFirstItem(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId);

    /**
     * 根据事项id获取绑定的表单id
     *
     * @param tenantId 租户Id
     * @param itemId 事项id
     * @param processDefinitionKey 流程定义Key
     * @return Y9Result<String>
     */
    @GetMapping("/getFormIdByItemId")
    Y9Result<String> getFormIdByItemId(@RequestParam("tenantId") String tenantId, @RequestParam("itemId") String itemId,
        @RequestParam("processDefinitionKey") String processDefinitionKey);

    /**
     * 获取新建事项列表
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @return Y9Result<List<ItemListModel>>
     */
    @GetMapping("/getItemList")
    Y9Result<List<ItemListModel>> getItemList(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId);

    /**
     * 获取事项系统字段映射配置
     *
     * @param tenantId 租户Id
     * @param itemId 事项id
     * @param mappingId 系统标识
     * @return Y9Result<List<ItemMappingConfModel>>
     */
    @GetMapping("/getItemMappingConf")
    Y9Result<List<ItemMappingConfModel>> getItemMappingConf(@RequestParam("tenantId") String tenantId,
        @RequestParam("itemId") String itemId, @RequestParam("mappingId") String mappingId);

    /**
     * 获取事项系统
     *
     * @param tenantId 租户id
     * @return Y9Result<List<ItemSystemListModel>>
     */
    @GetMapping("/getItemSystem")
    Y9Result<List<ItemSystemListModel>> getItemSystem(@RequestParam("tenantId") String tenantId);

    /**
     * 获取个人有权限事项列表
     *
     * @param tenantId 租户Id
     * @param positionId 岗位id
     * @return Y9Result<List<ItemListModel>>
     */
    @GetMapping("/getMyItemList")
    Y9Result<List<ItemListModel>> getMyItemList(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId);

    /**
     * 判断该租户是否有流程定义
     *
     * @param tenantId 租户Id
     * @param processDefinitionKey 流程定义Key
     * @return Y9Result<Boolean>
     */
    @GetMapping("/hasProcessDefinitionByKey")
    Y9Result<Boolean> hasProcessDefinitionByKey(@RequestParam("tenantId") String tenantId,
        @RequestParam("processDefinitionKey") String processDefinitionKey);

}
