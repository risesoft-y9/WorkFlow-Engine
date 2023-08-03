package net.risesoft.api.itemadmin;

import java.util.Map;

import net.risesoft.model.itemadmin.OfficeFollowModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface OfficeFollowApi {

    /**
     * 根据流程实例id获取是否有关注
     * 
     * @param tenantId
     * @param userId
     * @param processInstanceId
     * @return
     */
    public int countByProcessInstanceId(String tenantId, String userId, String processInstanceId);

    /**
     * 根据流程实例id删除关注
     * 
     * @param tenantId
     * @param processInstanceId
     */
    public void deleteByProcessInstanceId(String tenantId, String processInstanceId);

    /**
     * 
     * Description: 取消关注
     * 
     * @param tenantId
     * @param userId
     * @param processInstanceIds
     * @return
     */
    public Map<String, Object> delOfficeFollow(String tenantId, String userId, String processInstanceIds);

    /**
     * 获取我的关注数量
     * 
     * @param tenantId
     * @param userId
     * @return
     */
    public int getFollowCount(String tenantId, String userId);

    /**
     * 获取关注列表
     * 
     * @param tenantId
     * @param userId
     * @param searchName
     * @param page
     * @param rows
     * @return
     */
    public Map<String, Object> getOfficeFollowList(String tenantId, String userId, String searchName, int page,
        int rows);

    /**
     * 保存办件关注信息
     * 
     * @param tenantId
     * @param userId
     * @param officeFollow
     * @return
     */
    public Map<String, Object> saveOfficeFollow(String tenantId, String userId, OfficeFollowModel officeFollow);

    /**
     * 更新标题
     * 
     * @param tenantId
     * @param processInstanceId
     * @param documentTitle
     */
    public void updateTitle(String tenantId, String processInstanceId, String documentTitle);

}
