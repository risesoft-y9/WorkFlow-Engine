package net.risesoft.service.core;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import net.risesoft.entity.Item;
import net.risesoft.model.itemadmin.core.ItemModel;
import net.risesoft.pojo.Y9Result;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface ItemService {

    /**
     * Description:复制事项
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
     * Description:
     *
     * @param ids
     * @return
     */
    Y9Result<String> delete(String ids);

    /**
     * Description:
     *
     * @param id
     * @return
     */
    Item findById(String id);

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
     * 根据流程定义key和租户Id判断当前租户是否存在事项
     *
     * @param processDefinitionKey
     * @return
     */
    Boolean hasProcessDefinitionByKey(String processDefinitionKey);

    /**
     * Description:
     *
     * @return
     */
    List<Item> list();

    /**
     * Description: 查询不包含当前事项id和事项name模糊匹配的事项列表
     *
     * @param id
     * @param name
     * @return
     */
    List<Item> listByIdNotAndNameLike(String id, String name);

    /**
     * Description:
     *
     * @param systemName
     * @return
     */
    List<Item> listBySystemName(String systemName);

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
     * Description:
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
