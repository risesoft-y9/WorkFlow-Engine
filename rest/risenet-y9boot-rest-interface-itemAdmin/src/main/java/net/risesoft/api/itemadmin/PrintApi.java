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
     * @param tenantId 租户id
     * @param userId 人员id
     * @param itemId 事项id
     * @return String
     */
    String openDocument(String tenantId, String userId, String itemId);

}
