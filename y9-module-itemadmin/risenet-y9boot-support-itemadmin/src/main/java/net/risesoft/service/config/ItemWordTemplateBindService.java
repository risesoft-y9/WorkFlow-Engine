package net.risesoft.service.config;

import java.util.List;

import net.risesoft.entity.template.ItemWordTemplateBind;
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
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     */
    void clearBindStatus(String itemId, String processDefinitionId);

    /**
     * 复制正文模板绑定信息
     *
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     */
    void copyBind(String itemId, String processDefinitionId);

    /**
     * 复制正文模板绑定信息
     *
     * @param itemId 事项id
     * @param newItemId 新事项id
     * @param lastVersionPid 上一个版本流程定义id
     */
    void copyBindInfo(String itemId, String newItemId, String lastVersionPid);

    /**
     * 删除绑定正文模板
     *
     * @param id 唯一标识
     */
    void deleteBind(String id);

    /**
     * 删除正文模板绑定信息
     *
     * @param itemId 事项id
     */
    void deleteBindInfo(String itemId);

    /**
     * 根据事项Id和流程定义Id获取绑定的正文模板
     *
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @return ItemWordTemplateBind
     */
    ItemWordTemplateBind findByItemIdAndProcessDefinitionId(String itemId, String processDefinitionId);

    /**
     * 根据事项Id获取绑定的正文模板
     *
     * @param itemId 事项id
     * @return List<ItemWordTemplateBind>
     */
    List<ItemWordTemplateBind> listByItemIdOrderByBindValueAsc(String itemId);

    /**
     * 绑定正文模板
     *
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @param templateId 正文模板id
     * @return Y9Result<String>
     */
    Y9Result<String> save(String itemId, String processDefinitionId, String templateId);

    /**
     * 批量绑定正文模板
     *
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @param templateId 正文模板id
     * @return Y9Result<String>
     */
    Y9Result<String> save(String itemId, String processDefinitionId, String[] templateId);

    /**
     * 保存绑定值
     *
     * @param id 唯一标识
     * @param bindValue 绑定值
     * @return Y9Result<String>
     */
    Y9Result<String> saveTemplateValue(String id, String itemId, String bindValue);

    /**
     * 更新绑定状态
     *
     * @param id 唯一标识
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     */
    void updateBindStatus(String id, String itemId, String processDefinitionId);
}
