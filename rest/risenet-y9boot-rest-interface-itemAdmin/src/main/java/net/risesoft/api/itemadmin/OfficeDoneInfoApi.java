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
     * @param tenantId
     * @param itemId
     * @return
     */
    int countByItemId(String tenantId, String itemId);

    /**
     * 统计个人办结件
     *
     * @param tenantId
     * @param userId
     * @param itemId
     * @return
     */
    int countByUserId(String tenantId, String userId, String itemId);

    /**
     * 监控在办统计
     *
     * @param tenantId
     * @param itemId
     * @return
     */
    long countDoingByItemId(String tenantId, String itemId);

    /**
     * 根据流程实例id删除办结信息
     *
     * @param tenantId
     * @param processInstanceId
     * @return
     */
    boolean deleteOfficeDoneInfo(String tenantId, String processInstanceId);

    /**
     * 根据流程实例id获取办结信息
     *
     * @param tenantId
     * @param processInstanceId
     * @return
     */
    OfficeDoneInfoModel findByProcessInstanceId(String tenantId, String processInstanceId);

    /**
     * 保存办结信息,不经过kafka消息队列，直接保存
     *
     * @param tenantId
     * @param info
     * @throws Exception
     */
    void saveOfficeDone(String tenantId, OfficeDoneInfoModel info) throws Exception;

    /**
     * 科室所有件列表
     *
     * @param tenantId
     * @param deptId
     * @param title
     * @param itemId
     * @param userName
     * @param state
     * @param year
     * @param page
     * @param rows
     * @return
     */
    Map<String, Object> searchAllByDeptId(String tenantId, String deptId, String title, String itemId, String userName, String state, String year, Integer page, Integer rows);

    /**
     * 
     * Description: 个人所有件搜索
     * 
     * @param tenantId
     * @param userId
     * @param title
     * @param itemId
     * @param userName
     * @param state
     * @param year
     * @param page
     * @param rows
     * @return
     */
    Map<String, Object> searchAllByUserId(String tenantId, String userId, String title, String itemId, String userName, String state, String year, Integer page, Integer rows);

    /**
     * 监控办件列表
     *
     * @param tenantId
     * @param searchName
     * @param itemId
     * @param userName
     * @param state
     * @param year
     * @param page
     * @param rows
     * @return
     */
    Map<String, Object> searchAllList(String tenantId, String searchName, String itemId, String userName, String state, String year, Integer page, Integer rows);

    /**
     * 获取监控在办，办结件列表
     *
     * @param tenantId
     * @param title
     * @param itemId
     * @param state
     * @param startdate
     * @param enddate
     * @param page
     * @param rows
     * @return
     */
    Map<String, Object> searchByItemId(String tenantId, String title, String itemId, String state, String startdate, String enddate, Integer page, Integer rows);

    /**
     * 获取个人办结件列表
     *
     * @param tenantId
     * @param userId
     * @param title
     * @param itemId
     * @param startdate
     * @param enddate
     * @param page
     * @param rows
     * @return
     */
    Map<String, Object> searchByUserId(String tenantId, String userId, String title, String itemId, String startdate, String enddate, Integer page, Integer rows);
}