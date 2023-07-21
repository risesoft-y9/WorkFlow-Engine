package net.risesoft.api.itemadmin;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface PrintApi {

    /**
     * 打开打印模板
     * 
     * @param tenantId
     * @param userId
     * @param itemId
     * @return
     */
    public String openDocument(String tenantId, String userId, String itemId);

}
