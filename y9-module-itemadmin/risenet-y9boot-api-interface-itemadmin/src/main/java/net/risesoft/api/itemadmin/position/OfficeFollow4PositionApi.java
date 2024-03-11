package net.risesoft.api.itemadmin.position;

import java.util.Map;

import net.risesoft.model.itemadmin.OfficeFollowModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface OfficeFollow4PositionApi {

    /**
     * 根据流程实例id获取是否有关注
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param processInstanceId 流程实例id
     * @return int
     */
    int countByProcessInstanceId(String tenantId, String positionId, String processInstanceId);

    /**
     * 根据流程实例id删除关注
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     */
    void deleteByProcessInstanceId(String tenantId, String processInstanceId);

    /**
     * 取消关注
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param processInstanceIds 流程实例ids
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> delOfficeFollow(String tenantId, String positionId, String processInstanceIds);

    /**
     * 获取我的关注数量
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @return int
     */
    int getFollowCount(String tenantId, String positionId);

    /**
     * 根据系统名获取关注列表
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param systemName 系统名称
     * @param searchName 搜索内容
     * @param page
     * @param rows
     * @return
     */
    Map<String, Object> getFollowListBySystemName(String tenantId, String positionId, String systemName,
        String searchName, int page, int rows);

    /**
     * 获取关注列表
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param searchName 搜索内容
     * @param page page
     * @param rows rows
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> getOfficeFollowList(String tenantId, String positionId, String searchName, int page, int rows);

    /**
     * 保存办件关注信息
     *
     * @param tenantId 租户id
     * @param officeFollow 办件关注信息
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> saveOfficeFollow(String tenantId, OfficeFollowModel officeFollow);

    /**
     * 更新标题
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param documentTitle 文档标题
     */
    void updateTitle(String tenantId, String processInstanceId, String documentTitle);

}
