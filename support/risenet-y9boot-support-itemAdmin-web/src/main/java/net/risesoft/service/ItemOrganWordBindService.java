package net.risesoft.service;

import java.util.List;

import net.risesoft.entity.ItemOrganWordBind;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
public interface ItemOrganWordBindService {

    /**
     * Description: 复制当前事项绑定的流程定义上一个版本流程定义对应的编号绑定
     * 
     * @param itemId
     * @param processDefinitionId
     */
    public void copyBind(String itemId, String processDefinitionId);

    /**
     * 根据id查询一条数据
     * 
     * @param id
     * @return
     */
    public ItemOrganWordBind findById(String id);

    /**
     * Description:
     * 
     * @param itemId
     * @return
     */
    public List<ItemOrganWordBind> findByItemId(String itemId);

    /**
     * Description: 根据事项Id、流程定义Id查找
     * 
     * @param itemId
     * @param processDefinitionId
     * @return
     */
    public List<ItemOrganWordBind> findByItemIdAndProcessDefinitionId(String itemId, String processDefinitionId);

    /**
     * Description: 根据事项Id、流程定义Id、任务定义key查找
     * 
     * @param itemId
     * @param processDefinitionId
     * @param taskDefKey
     * @return
     */
    public List<ItemOrganWordBind> findByItemIdAndProcessDefinitionIdAndTaskDefKey(String itemId, String processDefinitionId, String taskDefKey);

    /**
     * Description:
     * 
     * @param itemId
     * @param processDefinitionId
     * @param taskDefKey
     * @param custom
     * @return
     */
    public ItemOrganWordBind findByItemIdAndProcessDefinitionIdAndTaskDefKeyAndOrganWordCustom(String itemId, String processDefinitionId, String taskDefKey, String custom);

    /**
     * 移除单个
     * 
     * @param id
     */
    public void remove(String id);

    /**
     * 批量移除
     * 
     * @param ids
     */
    public void remove(String[] ids);

    /**
     * 保存一条数据
     * 
     * @param taskRoleBind
     */
    public void save(ItemOrganWordBind taskRoleBind);

    /**
     * Description: 保存
     * 
     * @param id
     * @param name
     * @param custom
     */
    public void save(String id, String name, String custom);

    /**
     * Description: 保存数据
     * 
     * @param custom
     * @param itemId
     * @param processDefinitionId
     * @param taskDefKey
     */
    public void save(String custom, String itemId, String processDefinitionId, String taskDefKey);
}
