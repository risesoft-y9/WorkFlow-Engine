package net.risesoft.api.itemadmin.position;

import net.risesoft.model.itemadmin.OfficeDoneInfoModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;

/**
 * 工作流办件信息列表接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface OfficeDoneInfo4PositionApi {

    /**
     * 取消上会，当代研究所
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return 9Result<Object>
     */
    Y9Result<Object> cancelMeeting(String tenantId, String processInstanceId);

    /**
     * 监控办结统计
     *
     * @param tenantId 租户id
     * @param itemId 事项id
     * @return Y9Result<Integer>
     */
    Y9Result<Integer> countByItemId(String tenantId, String itemId);

    /**
     * 统计个人办结件
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param itemId 事项id
     * @return Y9Result<Integer>
     */
    Y9Result<Integer> countByPositionId(String tenantId, String positionId, String itemId);

    /**
     * 根据系统名称统计个人办结件
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param systemName 系统名称
     * @return Y9Result<Integer>
     */
    Y9Result<Integer> countByPositionIdAndSystemName(String tenantId, String positionId, String systemName);

    /**
     * 监控在办统计
     *
     * @param tenantId 租户id
     * @param itemId 事项id
     * @return Y9Result<Long>
     */
    Y9Result<Long> countDoingByItemId(String tenantId, String itemId);

    /**
     * 根据流程实例id删除办结信息
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return Y9Result<Object>
     */
    Y9Result<Object> deleteOfficeDoneInfo(String tenantId, String processInstanceId);

    /**
     * 根据流程实例id获取办结信息
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return Y9Result<OfficeDoneInfoModel>
     */
    Y9Result<OfficeDoneInfoModel> findByProcessInstanceId(String tenantId, String processInstanceId);

    /**
     * 上会台账列表，当代研究所
     *
     * @param tenantId 租户id
     * @param userName 申请人
     * @param deptName 部门名称
     * @param title 标题
     * @param meetingType 会议类型
     * @param page 页码
     * @param rows 条数
     * @return
     */
    Y9Page<OfficeDoneInfoModel> getMeetingList(String tenantId, String userName, String deptName, String title,
        String meetingType, Integer page, Integer rows);

    /**
     * 保存办结信息,不经过kafka消息队列，直接保存
     *
     * @param tenantId 租户id
     * @param info 办结信息
     * @return
     * @throws Exception Exception
     */
    Y9Result<Object> saveOfficeDone(String tenantId, OfficeDoneInfoModel info) throws Exception;

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
    Y9Page<OfficeDoneInfoModel> searchAllByDeptId(String tenantId, String deptId, String title, String itemId,
        String userName, String state, String year, Integer page, Integer rows);

    /**
     * Description: 个人所有件搜索
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param title 标题
     * @param itemId 事项id
     * @param userName 用户名称
     * @param state 状态
     * @param year 年份
     * @param startDate 查询开始日期
     * @param endDate 查询结束日期
     * @param page page
     * @param rows rows
     * @return Map&lt;String, Object&gt;
     */
    Y9Page<OfficeDoneInfoModel> searchAllByPositionId(String tenantId, String positionId, String title, String itemId,
        String userName, String state, String year, String startDate, String endDate, Integer page, Integer rows);

    /**
     * 监控办件列表
     *
     * @param tenantId 租户id
     * @param searchName 搜索条件
     * @param itemId 事项id
     * @param userName 用户名称
     * @param state 状态
     * @param year 年份
     * @param page page
     * @param rows rows
     * @return Map&lt;String, Object&gt;
     */
    Y9Page<OfficeDoneInfoModel> searchAllList(String tenantId, String searchName, String itemId, String userName,
        String state, String year, Integer page, Integer rows);

    /**
     * 获取监控在办，办结件列表
     *
     * @param tenantId 租户id
     * @param title 标题
     * @param itemId 事项id
     * @param state 状态
     * @param startdate 开始日期
     * @param enddate 结束日期
     * @param page page
     * @param rows rows
     * @return Map&lt;String, Object&gt;
     */
    Y9Page<OfficeDoneInfoModel> searchByItemId(String tenantId, String title, String itemId, String state,
        String startdate, String enddate, Integer page, Integer rows);

    /**
     * 获取个人办结件列表
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param title 标题
     * @param itemId 事项id
     * @param startdate 开始日期
     * @param enddate 结束日期
     * @param page page
     * @param rows rows
     * @return Map&lt;String, Object&gt;
     */
    Y9Page<OfficeDoneInfoModel> searchByPositionId(String tenantId, String positionId, String title, String itemId,
        String startdate, String enddate, Integer page, Integer rows);

    /**
     * 根据岗位id,系统名称，获取个人办结件列表
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param title 标题
     * @param systemName 系统名称
     * @param startdate 开始日期
     * @param enddate 结束日期
     * @param page page
     * @param rows rows
     * @return
     */
    Y9Page<OfficeDoneInfoModel> searchByPositionIdAndSystemName(String tenantId, String positionId, String title,
        String systemName, String startdate, String enddate, Integer page, Integer rows);

    /**
     * 上会，当代研究所
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param meetingType 会议类型
     * @return
     */
    Y9Result<Object> setMeeting(String tenantId, String processInstanceId, String meetingType);

}