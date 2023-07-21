package net.risesoft.api.itemadmin.position;

import java.util.List;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface Entrust4PositionApi {

    /**
     * 根据用户唯一标示，查找当前时间所有委托对象的所有岗位Id集合
     *
     * @param tenantId 租户Id
     * @param assigneeId 被委托人Id
     * @param currentTime 当前时间
     * @return
     */
    public List<String> getPositionIdsByAssigneeIdAndTime(String tenantId, String assigneeId, String currentTime);
}
