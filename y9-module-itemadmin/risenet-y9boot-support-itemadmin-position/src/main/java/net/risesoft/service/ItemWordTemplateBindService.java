package net.risesoft.service;

import java.util.List;
import java.util.Map;

import net.risesoft.entity.ItemWordTemplateBind;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface ItemWordTemplateBindService {

    /**
     * Description: 删除绑定正文模板
     * 
     * @param id
     * @return
     */
    Map<String, Object> deleteBind(String id);

    /**
     * 根据事项Id和流程定义Id获取绑定的正文模板
     *
     * @param itemId
     * @param processDefinitionId
     * @return
     */
    ItemWordTemplateBind findByItemIdAndProcessDefinitionId(String itemId, String processDefinitionId);

    /**
     * 根据事项Id获取绑定的正文模板
     * 
     * @param ItemId
     * @return
     */
    List<ItemWordTemplateBind> findByItemIdOrderByBindValueAsc(String ItemId);

    /**
     * Description: 绑定正文模板
     * 
     * @param itemId
     * @param processDefinitionId
     * @param templateId
     * @return
     */
    Map<String, Object> save(String itemId, String processDefinitionId, String templateId);

    /**
     * Description: 批量绑定正文模板
     *
     * @param itemId
     * @param processDefinitionId
     * @param templateId
     * @return
     */
    Map<String, Object> save(String itemId, String processDefinitionId, String[] templateId);

    /**
     * Description: 保存绑定值
     *
     * @param id
     * @param bindValue
     * @return
     */
    Map<String, Object> saveTemplateValue(String id, String bindValue);

    /**
     * 更新绑定状态
     * 
     * @param id
     * @param itemId
     * @param processDefinitionId
     * @return
     */
    void updateBindStatus(String id, String itemId, String processDefinitionId);

    /**
     * 清空绑定状态
     * 
     * @param itemId
     * @param processDefinitionId
     */
    void clearBindStatus(String itemId, String processDefinitionId);

    /**
     * 复制正文模板绑定信息
     * 
     * @param itemId
     * @param newItemId
     * @param lastVersionPid
     */
    void copyBindInfo(String itemId, String newItemId, String lastVersionPid);

    /**
     * 删除正文模板绑定信息
     * 
     * @param itemId
     */
    void deleteBindInfo(String itemId);
}
