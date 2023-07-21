package net.risesoft.api.itemadmin;

import net.risesoft.model.itemadmin.ActRuDetailModel;
import net.risesoft.model.itemadmin.ItemPage;

public interface ItemTodoApi {

    /**
     * 查询待办任务数量
     *
     * @param tenantId
     * @param userId
     * @param systemName
     * @return
     * @throws Exception
     */
    int countByUserIdAndSystemName(String tenantId, String userId, String systemName) throws Exception;

    /**
     * 查询待办任务，以发送时间排序
     *
     * @param tenantId
     * @param userId
     * @param systemName
     * @param page
     * @param rows
     * @return
     * @throws Exception
     */
    ItemPage<ActRuDetailModel> findByUserIdAndSystemName(String tenantId, String userId, String systemName, Integer page, Integer rows) throws Exception;

    /**
     * 查询待办任务，以发送时间排序
     *
     * @param tenantId
     * @param userId
     * @param systemName
     * @param page
     * @param rows
     * @return
     * @throws Exception
     */
    ItemPage<ActRuDetailModel> searchByUserIdAndSystemName(String tenantId, String userId, String systemName, String tableName, String searchMapStr, Integer page, Integer rows) throws Exception;

}
