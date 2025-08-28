package net.risesoft.service.core;

import java.util.List;

import org.springframework.data.domain.Page;

import net.risesoft.entity.Item;
import net.risesoft.model.itemadmin.ItemSystemListModel;
import net.risesoft.model.itemadmin.core.ItemModel;
import net.risesoft.pojo.Y9Result;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface ItemService {

    /**
     * 复制事项
     *
     * @param id
     * @return
     */
    Y9Result<String> copyItem(String id);

    /**
     * 复制事项和流程定义版本相关的绑定
     *
     * @param itemId 事项唯一标识
     * @param processDefinitionId 流程定义
     * @return Y9Result<String>
     */
    Y9Result<String> copyAllBind(String itemId, String processDefinitionId);

    /**
     *
     *
     * @param ids
     * @return
     */
    Y9Result<String> delete(String ids);

    /**
     *
     *
     * @param id
     * @return
     */
    Item findById(String id);

    /**
     * 根据流程定义Key查找对应的事项
     *
     * @param tenantId
     * @param processDefinitionKey
     * @return
     */
    ItemModel findByProcessDefinitionKey(String tenantId, String processDefinitionKey);

    /**
     * 根据流程定义key和租户Id判断当前租户是否存在事项
     *
     * @param processDefinitionKey
     * @return
     */
    Boolean hasProcessDefinitionByKey(String processDefinitionKey);

    /**
     * 获取所有系统
     * 
     * @return List<ItemSystemListModel>
     */
    List<ItemSystemListModel> getItemSystem();

    /**
     *
     *
     * @return
     */
    List<Item> list();

    /**
     *
     * @param systemName
     * @return
     */
    List<Item> findBySystemName(String systemName);

    /**
     * 分页获取事项列表
     *
     * @param page
     * @param rows
     * @return
     */
    Page<Item> page(Integer page, Integer rows);

    /**
     * 发布事项为应用
     *
     * @param itemId
     * @return
     */
    Y9Result<String> publishToSystemApp(String itemId);

    /**
     *
     *
     * @param item
     * @return
     */
    Y9Result<Item> save(Item item);

    /**
     * 更新事项排序
     *
     * @param idAndTabIndexs
     */
    void updateOrder(String[] idAndTabIndexs);
}
