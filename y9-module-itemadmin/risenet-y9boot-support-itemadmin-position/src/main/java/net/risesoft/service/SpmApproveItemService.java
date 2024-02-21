package net.risesoft.service;

import java.util.List;
import java.util.Map;

import net.risesoft.entity.SpmApproveItem;
import net.risesoft.model.itemadmin.ItemModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface SpmApproveItemService {

    /**
     * Description:
     * 
     * @param ids
     * @return
     */
    Map<String, Object> delete(String ids);

    /**
     * Description:
     * 
     * @param id
     * @return
     */
    SpmApproveItem findById(String id);

    /**
     * Description:
     * 
     * @param itemId
     * @param map
     * @return
     */
    Map<String, Object> findById(String itemId, Map<String, Object> map);

    /**
     * 根据流程定义Key查找对应的事项
     *
     * @param tenantId
     * @param processDefinitionKey
     * @return
     */
    ItemModel findByProcessDefinitionKey(String tenantId, String processDefinitionKey);

    /**
     * Description:
     * 
     * @param systemName
     * @return
     */
    List<SpmApproveItem> findBySystemName(String systemName);

    /**
     * 根据流程定义key和租户Id判断当前租户是否存在事项
     *
     * @param tenantId
     * @param userId
     * @param processDefinitionKey
     * @return
     */
    Boolean hasProcessDefinitionByKey(String processDefinitionKey);

    /**
     * Description:
     * 
     * @return
     */
    Map<String, Object> list();

    /**
     * 分页获取事项列表
     *
     * @param page
     * @param rows
     * @return
     */
    Map<String, Object> list(Integer page, Integer rows);

    /**
     * 发布事项为应用
     *
     * @param itemId
     * @return
     */
    Map<String, Object> publishToSystemApp(String itemId);

    /**
     * Description:
     * 
     * @param item
     * @return
     */
    Map<String, Object> save(SpmApproveItem item);
}
