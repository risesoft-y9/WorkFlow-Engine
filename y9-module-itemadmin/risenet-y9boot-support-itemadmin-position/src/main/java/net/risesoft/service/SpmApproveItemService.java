package net.risesoft.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import net.risesoft.entity.SpmApproveItem;
import net.risesoft.model.itemadmin.ItemModel;
import net.risesoft.pojo.Y9Result;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface SpmApproveItemService {

    /**
     * Description:复制事项
     *
     * @param id
     * @return
     */
    Y9Result<String> copyItem(String id);

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
    List<SpmApproveItem> list();

    /**
     * Description: 查询不包含当前事项id和事项name模糊匹配的事项列表
     *
     * @param id
     * @param name
     * @return
     */
    List<SpmApproveItem> listByIdNotAndNameLike(String id, String name);

    /**
     * Description:
     *
     * @param systemName
     * @return
     */
    List<SpmApproveItem> listBySystemName(String systemName);

    /**
     * 分页获取事项列表
     *
     * @param page
     * @param rows
     * @return
     */
    Page<SpmApproveItem> page(Integer page, Integer rows);

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
    Y9Result<SpmApproveItem> save(SpmApproveItem item);

    /**
     * 更新事项排序
     *
     * @param idAndTabIndexs
     */
    void updateOrder(String[] idAndTabIndexs);
}
