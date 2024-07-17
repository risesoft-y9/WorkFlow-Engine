package net.risesoft.service;

import java.util.List;

import net.risesoft.entity.ItemWordTemplateBind;
import net.risesoft.pojo.Y9Result;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface ItemWordTemplateBindService {

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
     * Description: 删除绑定正文模板
     *
     * @param id
     * @return
     */
    void deleteBind(String id);

    /**
     * 删除正文模板绑定信息
     *
     * @param itemId
     */
    void deleteBindInfo(String itemId);

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
    List<ItemWordTemplateBind> listByItemIdOrderByBindValueAsc(String ItemId);

    /**
     * Description: 绑定正文模板
     *
     * @param itemId
     * @param processDefinitionId
     * @param templateId
     * @return
     */
    Y9Result<String> save(String itemId, String processDefinitionId, String templateId);

    /**
     * Description: 批量绑定正文模板
     *
     * @param itemId
     * @param processDefinitionId
     * @param templateId
     * @return
     */
    Y9Result<String> save(String itemId, String processDefinitionId, String[] templateId);

    /**
     * Description: 保存绑定值
     *
     * @param id
     * @param bindValue
     * @return
     */
    Y9Result<String> saveTemplateValue(String id, String bindValue);

    /**
     * 更新绑定状态
     *
     * @param id
     * @param itemId
     * @param processDefinitionId
     * @return
     */
    void updateBindStatus(String id, String itemId, String processDefinitionId);
}
