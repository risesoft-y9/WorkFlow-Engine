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
     * @param itemId
     * @param interfaceId
     * @param processDefinitionId
     */
    void copyBind(String itemId, String interfaceId, String processDefinitionId);

    /**
     * 保存绑定
     *
     * @param itemId
     * @param interfaceId
     * @param processDefinitionId
     * @param elementKey
     * @param condition
     */
    void saveBind(String itemId, String interfaceId, String processDefinitionId, String elementKey, String condition);
}
