package net.risesoft.api.itemadmin.position;

import java.util.List;
import java.util.Map;

import net.risesoft.model.itemadmin.ItemMappingConfModel;
import net.risesoft.model.itemadmin.ItemModel;

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
     * @return List<ItemModel>
     */
    public List<ItemModel> findAll(String tenantId, String systemName);

    /**
     * 根据流程的定义Key查找对应的事项
     *
     * @param tenantId 租户Id
     * @param processDefinitionKey 流程定义Key
     * @return ItemModel
     */
    public ItemModel findByProcessDefinitionKey(String tenantId, String processDefinitionKey);

    /**
     * 获取所有事项列表
     *
     * @param tenantId 租户id
     * @return Listt&lt;ItemModel&gt;
     */
    public List<ItemModel> getAllItem(String tenantId);

    /**
     * 获取所有事项
     *
     * @param tenantId
     * @return
     */
    public List<ItemModel> getAllItemList(String tenantId);

    /**
     * 根据事项id获取事项
     *
     * @param tenantId 租户id
     * @param itemId 事项id
     * @return ItemModel
     */
    public ItemModel getByItemId(String tenantId, String itemId);

    /**
     * 获取有权限的首个事项id
     *
     * @param tenantId
     * @param positionId
     * @return
     */
    String getFirstItem(String tenantId, String positionId);

    /**
     * 根据事项id获取绑定的表单id
     *
     * @param tenantId 租户Id
     * @param itemId 事项id
     * @param processDefinitionKey 流程定义Key
     * @return
     */
    String getFormIdByItemId(String tenantId, String itemId, String processDefinitionKey);

    /**
     * 获取新建事项列表
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @return List&lt;Map&lt;String, Object&gt;&gt;
     */
    public List<Map<String, Object>> getItemList(String tenantId, String positionId);

    /**
     * 获取事项系统字段映射配置
     *
     * @param tenantId 租户Id
     * @param itemId 事项id
     * @param mappingId 系统标识
     * @return
     */
    public List<ItemMappingConfModel> getItemMappingConf(String tenantId, String itemId, String mappingId);

    /**
     * 获取事项系统
     *
     * @param tenantId 租户id
     * @return List&lt;Map&lt;String, Object&gt;&gt;
     */
    public List<Map<String, Object>> getItemSystem(String tenantId);

    /**
     * 获取个人有权限事项列表
     *
     * @param tenantId
     * @param positionId
     * @return
     */
    public List<Map<String, Object>> getMyItemList(String tenantId, String positionId);

    /**
     * 判断该租户是否有流程定义
     *
     * @param tenantId 租户Id
     * @param processDefinitionKey 流程定义Key
     * @return Boolean
     */
    public Boolean hasProcessDefinitionByKey(String tenantId, String processDefinitionKey);

}
