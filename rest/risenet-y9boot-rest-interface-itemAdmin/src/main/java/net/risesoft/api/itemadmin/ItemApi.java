package net.risesoft.api.itemadmin;

import java.util.List;
import java.util.Map;

import net.risesoft.model.itemadmin.ItemMappingConfModel;
import net.risesoft.model.itemadmin.ItemModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface ItemApi {

    /**
     *
     * Description: 根据系统名称查找所哟事项
     *
     * @param tenantId 租户Id
     * @param userId 用户id
     * @param systemName 系统名称
     * @return List&lt;ItemModel&gt;
     */
    List<ItemModel> findAll(String tenantId, String userId, String systemName);

    /**
     * 根据流程的定义Key查找对应的事项
     *
     * @param tenantId 租户Id
     * @param processDefinitionKey 流程定义Key
     * @return ItemModel
     */
    ItemModel findByProcessDefinitionKey(String tenantId, String processDefinitionKey);

    /**
     * 获取所有事项列表
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @return Listt&lt;ItemModel&gt;
     */
    List<ItemModel> getAllItem(String tenantId, String userId);

    /**
     * 获取所有事项
     *
     * @param tenantId 租户Id
     * @return List&lt;ItemModel&gt;
     */
    List<ItemModel> getAllItemList(String tenantId);

    /**
     * 根据事项id获取事项
     *
     * @param tenantId 租户id
     * @param itemId 事项id
     * @return ItemModel
     */
    ItemModel getByItemId(String tenantId, String itemId);

    /**
     * 获取有权限的首个事项id
     *
     * @param tenantId 租户Id
     * @param userId 用户id
     * @return String
     */
    String getFirstItem(String tenantId, String userId);

    /**
     * 根据事项id获取绑定的表单id
     *
     * @param tenantId 租户Id
     * @param itemId 事项id
     * @param processDefinitionKey 流程定义Key
     * @return String
     */
    String getFormIdByItemId(String tenantId, String itemId, String processDefinitionKey);

    /**
     * 获取新建事项列表
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @return List&lt;Map&lt;String, Object&gt;&gt;
     */
    List<Map<String, Object>> getItemList(String tenantId, String userId);

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
    Map<String, Object> getItemList(String tenantId, String personId, Integer page, Integer rows, String name);

    /**
     * 获取事项系统字段映射配置
     *
     * @param tenantId 租户Id
     * @param itemId 事项id
     * @param mappingId 系统标识
     * @return List&lt;ItemMappingConfModel&gt;
     */
    List<ItemMappingConfModel> getItemMappingConf(String tenantId, String itemId, String mappingId);

    /**
     * 获取事项系统
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @return List&lt;Map&lt;String, Object&gt;&gt;
     */
    List<Map<String, Object>> getItemSystem(String tenantId, String userId);

    /**
     * 获取个人有权限事项列表
     *
     * @param tenantId 租户Id
     * @param id id
     * @return List&lt;Map&lt;String, Object&gt;&gt;
     */
    List<Map<String, Object>> getMyItemList(String tenantId, String id);

    /**
     * 判断该租户是否有流程定义
     *
     * @param tenantId 租户Id
     * @param processDefinitionKey 流程定义Key
     * @return Boolean
     */
    Boolean hasProcessDefinitionByKey(String tenantId, String processDefinitionKey);

}
