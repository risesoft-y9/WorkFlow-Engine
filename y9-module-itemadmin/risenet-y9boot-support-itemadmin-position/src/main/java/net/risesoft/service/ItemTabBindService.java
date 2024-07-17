package net.risesoft.service;

import java.util.List;

import net.risesoft.entity.ItemTabBind;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface ItemTabBindService {

    /**
     * 复制该事项绑定的指定的流程定义的上一个版本流程定义绑定的页签到该版本
     *
     * @param itemId
     * @param processDefinitionId
     */
    void copyTabItemBind(String itemId, String processDefinitionId);

    /**
     * 根据唯一标示查找页签和事项绑定关系
     *
     * @param id
     * @return
     */
    ItemTabBind getById(String id);

    /**
     * 根据事项唯一标示和绑定的流程定义查找所有的绑定
     *
     * @param itemId
     * @param processDefinitionId
     * @return
     */
    List<ItemTabBind> listByItemIdAndProcessDefinitionId(String itemId, String processDefinitionId);

    /**
     * 删除绑定关系
     *
     * @param tabItemBindIds
     */
    void removeTabItemBinds(String[] tabItemBindIds);

    /**
     * 保存绑定关系
     *
     * @param tabItemBind
     * @return
     */
    void save(ItemTabBind tabItemBind);

    /**
     * 排序
     *
     * @param idAndTabIndexs "id:tabIndex"形式的数组
     */
    void saveOrder(String[] idAndTabIndexs);

    /**
     * Description: 保存绑定关系
     *
     * @param tabId
     * @param itemId
     * @param processDefinitionId
     * @return
     */
    ItemTabBind saveTabBind(String tabId, String itemId, String processDefinitionId);
}
