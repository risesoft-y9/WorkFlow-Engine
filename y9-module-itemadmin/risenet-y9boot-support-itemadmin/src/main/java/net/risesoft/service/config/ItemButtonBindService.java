package net.risesoft.service.config;

import java.util.List;

import net.risesoft.entity.button.ItemButtonBind;
import net.risesoft.enums.ItemButtonTypeEnum;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface ItemButtonBindService {

    /**
     * 事项对应的流程节点和按钮绑定
     *
     * @param itemId 事项id
     * @param buttonId 按钮id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 流程定义节点
     * @param buttonType 按钮类型
     */
    void bindButton(String itemId, String buttonId, String processDefinitionId, String taskDefKey,
        ItemButtonTypeEnum buttonType);

    /**
     * 复制当前事项绑定的流程定义上一个版本的流程定义绑定的按钮
     *
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     */
    void copyBind(String itemId, String processDefinitionId);

    /**
     * 复制按钮配置绑定信息
     *
     * @param itemId 事项id
     * @param newItemId 新事项id
     * @param lastVersionPid 上一个流程定义id
     */
    void copyBindInfo(String itemId, String newItemId, String lastVersionPid);

    /**
     * 删除按钮配置绑定信息
     *
     * @param itemId 事项id
     */
    void deleteBindInfo(String itemId);

    /**
     * 根据唯一标示查找
     *
     * @param id 唯一标示
     * @return ItemButtonBind
     */
    ItemButtonBind getById(String id);

    /**
     * 
     *
     * @param buttonId 按钮id
     * @return List<ItemButtonBind>
     */
    List<ItemButtonBind> listByButtonId(String buttonId);

    /**
     * 根据事项Id、按钮类型、流程定义Id查找
     *
     * @param itemId 事项id
     * @param buttonType 按钮类型
     * @param processDefinitionId 流程定义id
     * @return List<ItemButtonBind>
     */
    List<ItemButtonBind> listByItemIdAndButtonTypeAndProcessDefinitionId(String itemId, ItemButtonTypeEnum buttonType,
        String processDefinitionId);

    /**
     * 根据事项Id、按钮类型、流程定义Key、任务节点查找
     *
     * @param itemId 事项id
     * @param buttonType 按钮类型
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务节点
     * @return List<ItemButtonBind>
     */
    List<ItemButtonBind> listByItemIdAndButtonTypeAndProcessDefinitionIdAndTaskDefKey(String itemId,
        ItemButtonTypeEnum buttonType, String processDefinitionId, String taskDefKey);

    /**
     *
     * 根据事项Id、按钮类型、流程定义Key、任务节点查找（包含授权的所有角色Id和角色名称）
     *
     * @param itemId 事项id
     * @param buttonType 按钮类型
     * @param processDefinitionId 流程定义id
     * @param taskDefineKey 任务节点
     * @return List<ItemButtonBind>
     */
    List<ItemButtonBind> listContainRole(String itemId, ItemButtonTypeEnum buttonType, String processDefinitionId,
        String taskDefineKey);

    /**
     * 根据事项Id、按钮类型、流程定义id、任务节点查找（包含授权的所有角色Id）
     *
     * @param itemId 事项id
     * @param buttonType 按钮类型
     * @param processDefinitionId 流程定义id
     * @param taskDefineKey 任务节点
     * @return List<ItemButtonBind>
     */
    List<ItemButtonBind> listContainRoleId(String itemId, ItemButtonTypeEnum buttonType, String processDefinitionId,
        String taskDefineKey);

    /**
     * TODO 根据事项Id、按钮类型、流程定义Key、任务节点查找（当前节点没有绑定则查找流程绑定的）
     *
     * @param itemId 事项id
     * @param buttonType 按钮类型
     * @param processDefinitionId 流程定义id
     * @param taskDefineKey 任务节点
     * @return List<ItemButtonBind>
     */
    List<ItemButtonBind> listExtra(String itemId, ItemButtonTypeEnum buttonType, String processDefinitionId,
        String taskDefineKey);

    /**
     * 删除绑定关系
     *
     * @param buttonItemBindIds 绑定关系唯一标识
     */
    void removeButtonItemBinds(String[] buttonItemBindIds);

    /**
     * 保存授权角色
     *
     * @param buttonItemBind 绑定实体
     * @return ItemButtonBind
     */
    ItemButtonBind save(ItemButtonBind buttonItemBind);

    /**
     * 保存排序
     *
     * @param idAndTabIndexs idAndTabIndexs
     */
    void saveOrder(String[] idAndTabIndexs);
}
