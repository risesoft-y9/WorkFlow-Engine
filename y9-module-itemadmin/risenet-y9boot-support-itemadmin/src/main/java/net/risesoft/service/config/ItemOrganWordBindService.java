package net.risesoft.service.config;

import java.util.List;

import net.risesoft.entity.organword.ItemOrganWordBind;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface ItemOrganWordBindService {

    /**
     * 复制当前事项绑定的流程定义上一个版本流程定义对应的编号绑定
     *
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     */
    void copyBind(String itemId, String processDefinitionId);

    /**
     * 复制编号绑定信息
     *
     * @param itemId 事项id
     * @param newItemId 新事项id
     * @param lastVersionPid 上一个版本流程定义id
     */
    void copyBindInfo(String itemId, String newItemId, String lastVersionPid);

    /**
     * 删除编号绑定信息
     *
     * @param itemId 事项id
     */
    void deleteBindInfo(String itemId);

    /**
     * 根据id查询一条数据
     *
     * @param id 唯一标识
     * @return ItemOrganWordBind
     */
    ItemOrganWordBind findById(String id);

    /**
     * 查询一条数据
     *
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务定义key
     * @param custom 自定义字段
     * @return ItemOrganWordBind
     */
    ItemOrganWordBind findByItemIdAndProcessDefinitionIdAndTaskDefKeyAndOrganWordCustom(String itemId,
        String processDefinitionId, String taskDefKey, String custom);

    /**
     *
     * @param itemId 事项id
     * @return List<ItemOrganWordBind>
     */
    List<ItemOrganWordBind> listByItemId(String itemId);

    /**
     * 根据事项Id、流程定义Id查找
     *
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @return List<ItemOrganWordBind>
     */
    List<ItemOrganWordBind> listByItemIdAndProcessDefinitionId(String itemId, String processDefinitionId);

    /**
     * 根据事项Id、流程定义Id、任务定义key查找
     *
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务定义key
     * @return List<ItemOrganWordBind>
     */
    List<ItemOrganWordBind> listByItemIdAndProcessDefinitionIdAndTaskDefKey(String itemId, String processDefinitionId,
        String taskDefKey);

    /**
     * 移除单个
     *
     * @param id 唯一标识
     */
    void remove(String id);

    /**
     * 批量移除
     *
     * @param ids 唯一标识
     */
    void remove(String[] ids);

    /**
     * 保存一条数据
     *
     * @param taskRoleBind 实体对象
     */
    void save(ItemOrganWordBind taskRoleBind);

    /**
     * 保存
     *
     * @param id 唯一标识
     * @param name 名称
     * @param custom 自定义字段
     */
    void save(String id, String name, String custom);

    /**
     * 保存数据
     *
     * @param custom 自定义字段
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务定义key
     */
    void save(String custom, String itemId, String processDefinitionId, String taskDefKey);
}
