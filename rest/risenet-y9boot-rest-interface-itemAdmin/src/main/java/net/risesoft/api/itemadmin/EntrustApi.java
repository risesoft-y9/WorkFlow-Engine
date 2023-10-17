package net.risesoft.api.itemadmin;

import java.util.Map;

import net.risesoft.model.itemadmin.EntrustModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface EntrustApi {

    /**
     * 销假:删除ownerId所有的正在使用中的、或者已经过期的出差委托，并放入委托历史表
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param ownerId 委托人id
     * @throws Exception Exception
     */
    void destroyEntrust(String tenantId, String userId, String ownerId) throws Exception;

    /**
     * 销假:根据唯一标示删除正在使用中的、或者已经过期的出差委托，并放入委托历史表
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param id 唯一标识
     * @throws Exception Exception
     */
    void destroyEntrustById(String tenantId, String userId, String id) throws Exception;

    /**
     * 销假:删除某个人的某个事项的正在使用中的、或者已经过期的出差委托，并放入委托历史表
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param ownerId 委托人id
     * @param itemId 事项id
     * @throws Exception Exception
     */
    void destroyEntrustByOwnerIdAndItemId(String tenantId, String userId, String ownerId, String itemId) throws Exception;

    /**
     * 根据用户唯一标示和事项唯一标示查找委托对象
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param ownerId 委托人id
     * @param itemId 事项id
     * @return EntrustModel
     * @throws Exception Exception
     */
    EntrustModel findOneByOwnerIdAndItemId(String tenantId, String userId, String ownerId, String itemId) throws Exception;

    /**
     * 根据用户唯一标示和事项唯一标示查找委托对象
     *
     * @param tenantId 租户id
     * @param ownerId 委托人id
     * @param itemId 事项id
     * @param currentTime 当前时间
     * @return EntrustModel
     * @throws Exception Exception
     */
    EntrustModel findOneByOwnerIdAndItemIdAndTime(String tenantId, String ownerId, String itemId, String currentTime) throws Exception;

    /**
     * 通过唯一标示获取委托对象
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param id id
     * @return EntrustModel
     * @throws Exception Exception
     */
    EntrustModel getById(String tenantId, String userId, String id) throws Exception;

    /**
     * 获取任务委托人id
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @return String
     */
    String getEntrustOwnerId(String tenantId, String taskId);

    /**
     * 获取事项列表
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param ownerId 委托人id
     * @param page page
     * @param rows rows
     * @return Map&lt;String, Object&gt;
     * @throws Exception Exception
     */
    Map<String, Object> getItemList(String tenantId, String userId, String ownerId, Integer page, Integer rows) throws Exception;

    /**
     * 查询任务是否有委托
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @return boolean
     */
    boolean haveEntrustDetail(String tenantId, String taskId);

    /**
     * 删除id出差委托，不会放入委托历史表
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param id 唯一标识
     * @throws Exception Exception
     */
    void removeEntrust(String tenantId, String userId, String id) throws Exception;

    /**
     * 保存委托详情
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param taskId 任务id
     * @param ownerId 委托人Id
     * @param assigneeId 委托对象Id
     */
    void saveEntrustDetail(String tenantId, String processInstanceId, String taskId, String ownerId, String assigneeId);

    /**
     * 保存或者更新委托对象
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param entrustModel 实体类（EntrustModel）
     * @throws Exception Exception
     */
    void saveOrUpdate(String tenantId, String userId, EntrustModel entrustModel) throws Exception;
}
