package net.risesoft.service.config;

import java.util.List;

import net.risesoft.entity.ItemButtonBind;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface ItemButtonBindService {

    /**
     * 事项对应的流程节点和按钮绑定
     *
     * @param itemId
     * @param buttonId
     * @param processDefinitionId
     * @param taskDefKey
     * @param buttonType
     * @return
     */
    ItemButtonBind bindButton(String itemId, String buttonId, String processDefinitionId, String taskDefKey,
        Integer buttonType);

    /**
     * Description: 复制当前事项绑定的流程定义上一个版本的流程定义绑定的按钮
     *
     * @param itemId
     * @param processDefinitionId
     */
    void copyBind(String itemId, String processDefinitionId);

    /**
     * Description: 复制按钮配置绑定信息
     *
     * @param itemId
     * @param newItemId
     * @param lastVersionPid
     */
    void copyBindInfo(String itemId, String newItemId, String lastVersionPid);

    /**
     * Description: 删除按钮配置绑定信息
     *
     * @param itemId
     */
    void deleteBindInfo(String itemId);

    /**
     * 根据唯一标示查找
     *
     * @param id
     * @return
     */
    ItemButtonBind getById(String id);

    /**
     * Description:
     *
     * @param buttonId
     * @return
     */
    List<ItemButtonBind> listByButtonId(String buttonId);

    /**
     * 根据事项Id、按钮类型、流程定义Id查找
     *
     * @param itemId
     * @param buttonType
     * @param processDefinitionId
     * @return
     */
    List<ItemButtonBind> listByItemIdAndButtonTypeAndProcessDefinitionId(String itemId, Integer buttonType,
        String processDefinitionId);

    /**
     * Description: 根据事项Id、按钮类型、流程定义Key、任务节点查找
     *
     * @param itemId
     * @param buttonType
     * @param processDefinitionId
     * @param taskDefKey
     * @return
     */
    List<ItemButtonBind> listByItemIdAndButtonTypeAndProcessDefinitionIdAndTaskDefKey(String itemId, Integer buttonType,
        String processDefinitionId, String taskDefKey);

    /**
     *
     * Description: 根据事项Id、按钮类型、流程定义Key、任务节点查找（包含授权的所有角色Id和角色名称）
     *
     * @param itemId
     * @param buttonType
     * @param processDefinitionId
     * @param taskDefineKey
     * @return
     */
    List<ItemButtonBind> listContainRole(String itemId, Integer buttonType, String processDefinitionId,
        String taskDefineKey);

    /**
     * Description: 根据事项Id、按钮类型、流程定义Key、任务节点查找（包含授权的所有角色Id）
     *
     * @param itemId
     * @param buttonType
     * @param processDefinitionId
     * @param taskDefineKey
     * @return
     */
    List<ItemButtonBind> listContainRoleId(String itemId, Integer buttonType, String processDefinitionId,
        String taskDefineKey);

    /**
     * Description: 根据事项Id、按钮类型、流程定义Key、任务节点查找（当前节点没有绑定则查找流程绑定的）
     *
     * @param itemId
     * @param buttonType
     * @param processDefinitionId
     * @param taskDefineKey
     * @return
     */
    List<ItemButtonBind> listExtra(String itemId, Integer buttonType, String processDefinitionId, String taskDefineKey);

    /**
     * Description: 删除绑定关系
     *
     * @param buttonItemBindIds
     */
    void removeButtonItemBinds(String[] buttonItemBindIds);

    /**
     * Description: 保存授权角色
     *
     * @param buttonItemBind
     * @return
     */
    ItemButtonBind save(ItemButtonBind buttonItemBind);

    /**
     * 保存排序
     *
     * @param idAndTabIndexs
     */
    void saveOrder(String[] idAndTabIndexs);
}
