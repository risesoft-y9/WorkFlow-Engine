package net.risesoft.service.config;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/21
 */
public interface ItemInterfaceTaskBindService {

    /**
     * 复制绑定
     *
     * @param itemId 事项id
     * @param interfaceId 接口id
     * @param processDefinitionId 流程定义id
     */
    void copyBind(String itemId, String interfaceId, String processDefinitionId);

    /**
     * 保存绑定
     *
     * @param itemId 事项id
     * @param interfaceId 接口id
     * @param processDefinitionId 流程定义id
     * @param elementKey 元素key
     * @param condition 条件
     */
    void saveBind(String itemId, String interfaceId, String processDefinitionId, String elementKey, String condition);
}
