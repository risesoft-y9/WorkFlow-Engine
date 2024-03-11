package net.risesoft.api.itemadmin;

import java.util.Map;

import net.risesoft.model.itemadmin.OfficeDoneInfoModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface OfficeDoneInfoApi {

    /**
     * 监控办结统计
     *
     * @param tenantId 租户id
     * @param itemId 事项id
     * @return int
     */
    int countByItemId(String tenantId, String itemId);

    /**
     * 统计个人办结件
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param itemId 事项id
     * @return int
     */
    int countByUserId(String tenantId, String userId, String itemId);

    /**
     * 监控在办统计
     *
     * @param tenantId 租户id
     * @param itemId 事项id
     * @return long
     */
    long countDoingByItemId(String tenantId, String itemId);

    /**
     * 根据流程实例id删除办结信息
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return boolean
     */
    boolean deleteOfficeDoneInfo(String tenantId, String processInstanceId);

    /**
     * 根据流程实例id获取办结信息
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return OfficeDoneInfoModel
     */
    OfficeDoneInfoModel findByProcessInstanceId(String tenantId, String processInstanceId);

    /**
     * 保存办结信息,不经过kafka消息队列，直接保存
     *
     * @param tenantId 租户id
     * @param info 办结信息
     * @throws Exception Exception
     */
    void saveOfficeDone(String tenantId, OfficeDoneInfoModel info) throws Exception;

    /**
     * 科室所有件列表
     *
     * @param tenantId 租户id
     * @param deptId 部门id
     * @param title 标题
     * @param itemId 事项id
     * @param userName 用户名称
     * @param state 状态
     * @param year 年份
     * @param page page
     * @param rows rows
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> searchAllByDeptId(String tenantId, String deptId, String title, String itemId, String userName,
        String state, String year, Integer page, Integer rows);

    /**
     *
     * Description: 个人所有件搜索
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param title 标题
     * @param itemId 事项id
     * @param userName 用户名称
     * @param state 状态
     * @param year 年份
     * @param page page
     * @param rows rows
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> searchAllByUserId(String tenantId, String userId, String title, String itemId, String userName,
        String state, String year, Integer page, Integer rows);

    /**
     * 监控办件列表
     *
     * @param tenantId 租户id
     * @param searchName 搜索名称
     * @param itemId 事项id
     * @param userName 用户名称
     * @param state 状态
     * @param year 年份
     * @param page page
     * @param rows rows
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> searchAllList(String tenantId, String searchName, String itemId, String userName, String state,
        String year, Integer page, Integer rows);

    /**
     * 获取监控在办，办结件列表
     *
     * @param tenantId 租户id
     * @param title 标题
     * @param itemId 事项id
     * @param state 状态id
     * @param startdate 开始时间
     * @param enddate 结束时间
     * @param page page
     * @param rows rows
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> searchByItemId(String tenantId, String title, String itemId, String state, String startdate,
        String enddate, Integer page, Integer rows);

    /**
     * 获取个人办结件列表
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param title 标题
     * @param itemId 事项id
     * @param startdate 开始时间
     * @param enddate 结束时间
     * @param page page
     * @param rows rows
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> searchByUserId(String tenantId, String userId, String title, String itemId, String startdate,
        String enddate, Integer page, Integer rows);

}