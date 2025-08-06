package net.risesoft.api.itemadmin.core;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.ItemListModel;
import net.risesoft.model.itemadmin.ItemMappingConfModel;
import net.risesoft.model.itemadmin.ItemSystemListModel;
import net.risesoft.model.itemadmin.core.ItemModel;
import net.risesoft.pojo.Y9Result;

/**
 * 事项接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface ItemApi {

    /**
     * 根据系统名称获取事项
     *
     * @param tenantId 租户id
     * @param systemName 系统名称
     * @return {@code Y9Result<List<ItemModel>>} 通用请求返回对象 - data 是事项列表
     * @since 9.6.6
     */
    @GetMapping("/findAll")
    Y9Result<List<ItemModel>> findAll(@RequestParam("tenantId") String tenantId,
        @RequestParam(value = "systemName") String systemName);

    /**
     * 根据流程的定义Key查找对应的事项
     *
     * @param tenantId 租户Id
     * @param processDefinitionKey 流程定义Key
     * @return {@code Y9Result<ItemModel>} 通用请求返回对象 - data 是事项信息
     * @since 9.6.6
     */
    @GetMapping("/findByProcessDefinitionKey")
    Y9Result<ItemModel> findByProcessDefinitionKey(@RequestParam("tenantId") String tenantId,
        @RequestParam("processDefinitionKey") String processDefinitionKey);

    /**
     * 获取所有事项列表
     *
     * @param tenantId 租户id
     * @return {@code Y9Result<List<ItemModel>>} 通用请求返回对象 - data 是事项列表
     * @since 9.6.6
     */
    @GetMapping("/getAllItem")
    Y9Result<List<ItemModel>> getAllItem(@RequestParam("tenantId") String tenantId);

    /**
     * 获取所有事项
     *
     * @param tenantId 租户id
     * @return {@code Y9Result<List<ItemModel>>} 通用请求返回对象 - data 是事项列表
     * @since 9.6.6
     */
    @GetMapping("/getAllItemList")
    Y9Result<List<ItemModel>> getAllItemList(@RequestParam("tenantId") String tenantId);

    /**
     * 根据事项id获取事项
     *
     * @param tenantId 租户id
     * @param itemId 事项id
     * @return {@code Y9Result<ItemModel>} 通用请求返回对象 - data 是事项信息
     * @since 9.6.6
     */
    @GetMapping("/getByItemId")
    Y9Result<ItemModel> getByItemId(@RequestParam("tenantId") String tenantId, @RequestParam("itemId") String itemId);

    /**
     * 获取有权限的首个事项id
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @return {@code Y9Result<String>} 通用请求返回对象 - data 是事项id
     * @since 9.6.6
     */
    @GetMapping("/getFirstItem")
    Y9Result<String> getFirstItem(@RequestParam("tenantId") String tenantId,
        @RequestParam("orgUnitId") String orgUnitId);

    /**
     * 根据事项id获取绑定的表单id
     *
     * @param tenantId 租户Id
     * @param itemId 事项id
     * @param processDefinitionKey 流程定义Key
     * @return {@code Y9Result<String>} 通用请求返回对象 - data 是表单id
     * @since 9.6.6
     */
    @GetMapping("/getFormIdByItemId")
    Y9Result<String> getFormIdByItemId(@RequestParam("tenantId") String tenantId, @RequestParam("itemId") String itemId,
        @RequestParam("processDefinitionKey") String processDefinitionKey);

    /**
     * 获取新建事项列表
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @return {@code Y9Result<List<ItemListModel>>} 通用请求返回对象 - data 是事项列表
     * @since 9.6.6
     */
    @GetMapping("/getItemList")
    Y9Result<List<ItemListModel>> getItemList(@RequestParam("tenantId") String tenantId,
        @RequestParam("orgUnitId") String orgUnitId);

    /**
     * 获取事项系统字段映射配置
     *
     * @param tenantId 租户Id
     * @param itemId 事项id
     * @param mappingId 系统标识
     * @return {@code Y9Result<List<ItemMappingConfModel>>} 通用请求返回对象 - data 是事项映射列表
     * @since 9.6.6
     */
    @GetMapping("/getItemMappingConf")
    Y9Result<List<ItemMappingConfModel>> getItemMappingConf(@RequestParam("tenantId") String tenantId,
        @RequestParam("itemId") String itemId, @RequestParam("mappingId") String mappingId);

    /**
     * 获取事项系统
     *
     * @param tenantId 租户id
     * @return {@code Y9Result<List<ItemSystemListModel>>} 通用请求返回对象 - data 是事项系统列表
     * @since 9.6.6
     */
    @GetMapping("/getItemSystem")
    Y9Result<List<ItemSystemListModel>> getItemSystem(@RequestParam("tenantId") String tenantId);

    /**
     * 获取个人有权限事项列表
     *
     * @param tenantId 租户Id
     * @param orgUnitId 人员、岗位id
     * @return {@code Y9Result<List<ItemListModel>>} 通用请求返回对象 - data 是新建事项列表
     * @since 9.6.6
     */
    @GetMapping("/getMyItemList")
    Y9Result<List<ItemListModel>> getMyItemList(@RequestParam("tenantId") String tenantId,
        @RequestParam("orgUnitId") String orgUnitId);

    /**
     * 判断该租户是否有流程定义
     *
     * @param tenantId 租户Id
     * @param processDefinitionKey 流程定义Key
     * @return {@code Y9Result<Boolean>} 通用请求返回对象
     * @since 9.6.6
     */
    @GetMapping("/hasProcessDefinitionByKey")
    Y9Result<Boolean> hasProcessDefinitionByKey(@RequestParam("tenantId") String tenantId,
        @RequestParam("processDefinitionKey") String processDefinitionKey);

}
