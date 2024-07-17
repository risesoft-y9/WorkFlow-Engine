package net.risesoft.service.config;

import java.util.List;

import net.risesoft.entity.ItemOrganWordBind;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface ItemOrganWordBindService {

    /**
     * Description: 复制当前事项绑定的流程定义上一个版本流程定义对应的编号绑定
     *
     * @param itemId
     * @param processDefinitionId
     */
    void copyBind(String itemId, String processDefinitionId);

    /**
     * Description: 复制编号绑定信息
     *
     * @param itemId
     * @param newItemId
     * @param lastVersionPid
     */
    void copyBindInfo(String itemId, String newItemId, String lastVersionPid);

    /**
     * Description: 删除编号绑定信息
     *
     * @param itemId
     */
    void deleteBindInfo(String itemId);

    /**
     * 根据id查询一条数据
     *
     * @param id
     * @return
     */
    ItemOrganWordBind findById(String id);

    /**
     * Description: 查询一条数据
     *
     * @param itemId
     * @param processDefinitionId
     * @param taskDefKey
     * @param custom
     * @return
     */
    ItemOrganWordBind findByItemIdAndProcessDefinitionIdAndTaskDefKeyAndOrganWordCustom(String itemId,
        String processDefinitionId, String taskDefKey, String custom);

    /**
     * Description:
     *
     * @param itemId
     * @return
     */
    List<ItemOrganWordBind> listByItemId(String itemId);

    /**
     * Description: 根据事项Id、流程定义Id查找
     *
     * @param itemId
     * @param processDefinitionId
     * @return
     */
    List<ItemOrganWordBind> listByItemIdAndProcessDefinitionId(String itemId, String processDefinitionId);

    /**
     * Description: 根据事项Id、流程定义Id、任务定义key查找
     *
     * @param itemId
     * @param processDefinitionId
     * @param taskDefKey
     * @return
     */
    List<ItemOrganWordBind> listByItemIdAndProcessDefinitionIdAndTaskDefKey(String itemId, String processDefinitionId,
        String taskDefKey);

    /**
     * 移除单个
     *
     * @param id
     */
    void remove(String id);

    /**
     * 批量移除
     *
     * @param ids
     */
    void remove(String[] ids);

    /**
     * 保存一条数据
     *
     * @param taskRoleBind
     */
    void save(ItemOrganWordBind taskRoleBind);

    /**
     * Description: 保存
     *
     * @param id
     * @param name
     * @param custom
     */
    void save(String id, String name, String custom);

    /**
     * Description: 保存数据
     *
     * @param custom
     * @param itemId
     * @param processDefinitionId
     * @param taskDefKey
     */
    void save(String custom, String itemId, String processDefinitionId, String taskDefKey);
}
