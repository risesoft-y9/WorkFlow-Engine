package net.risesoft.api.itemadmin.position;

import java.util.Map;

import net.risesoft.model.itemadmin.OfficeDoneInfoModel;

/**
 * 工作流办件信息列表接口
 * 
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface OfficeDoneInfo4PositionApi {

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
     * @param positionId
     * @param itemId
     * @return
     */
    int countByPositionId(String tenantId, String positionId, String itemId);

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
    Map<String, Object> searchAllByDeptId(String tenantId, String deptId, String title, String itemId, String userName,
        String state, String year, Integer page, Integer rows);

    /**
     * 
     * Description: 个人所有件搜索
     * 
     * @param tenantId
     * @param positionId
     * @param title
     * @param itemId
     * @param userName
     * @param state
     * @param year
     * @param page
     * @param rows
     * @return
     */
    Map<String, Object> searchAllByPositionId(String tenantId, String positionId, String title, String itemId,
        String userName, String state, String year, Integer page, Integer rows);

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
    Map<String, Object> searchAllList(String tenantId, String searchName, String itemId, String userName, String state,
        String year, Integer page, Integer rows);

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
    Map<String, Object> searchByItemId(String tenantId, String title, String itemId, String state, String startdate,
        String enddate, Integer page, Integer rows);

    /**
     * 获取个人办结件列表
     *
     * @param tenantId
     * @param positionId
     * @param title
     * @param itemId
     * @param startdate
     * @param enddate
     * @param page
     * @param rows
     * @return
     */
    Map<String, Object> searchByPositionId(String tenantId, String positionId, String title, String itemId,
        String startdate, String enddate, Integer page, Integer rows);

}