package net.risesoft.service;

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
     * Description: 绑定正文模板
     * 
     * @param itemId
     * @param processDefinitionId
     * @param templateId
     * @return
     */
    Map<String, Object> save(String itemId, String processDefinitionId, String templateId);
}
