package net.risesoft.service.config;

import java.util.List;

import net.risesoft.entity.documentword.ItemWordConf;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface ItemWordConfService {

    /**
     * 绑定角色
     * 
     * @param id 唯一标识
     * @param roleIds 角色id
     */
    void bindRole(String id, String roleIds);

    /**
     * 复制配置
     * 
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     */
    void copyWordConf(String itemId, String processDefinitionId);

    /**
     * 删除
     * 
     * @param id 唯一标识
     */
    void delete(String id);

    /**
     * 解绑角色
     * 
     * @param id 唯一标识
     * @param roleId 角色id
     */
    void deleteRole(String id, String roleId);

    /**
     * 获取权限
     *
     * @param positionId 岗位id
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务定义key
     * @param wordType 权限字类型
     * @return Boolean
     */
    Boolean getPermissionWord(String positionId, String itemId, String processDefinitionId, String taskDefKey,
        String wordType);

    /**
     * TODO 获取是否有权限
     * 
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务定义key
     * @return List<ItemWordConf>
     */
    List<ItemWordConf> listByItemIdAndProcessDefinitionIdAndTaskDefKey(String itemId, String processDefinitionId,
        String taskDefKey);

    /**
     * 保存
     * 
     * @param wordType word类型
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务定义key
     */
    void save(String wordType, String itemId, String processDefinitionId, String taskDefKey);
}
