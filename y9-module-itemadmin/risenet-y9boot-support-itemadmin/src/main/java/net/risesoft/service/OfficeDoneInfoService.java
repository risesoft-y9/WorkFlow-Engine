package net.risesoft.service;

import org.springframework.stereotype.Service;

import net.risesoft.model.itemadmin.OfficeDoneInfoModel;
import net.risesoft.nosql.elastic.entity.OfficeDoneInfo;
import net.risesoft.pojo.Y9Page;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
public interface OfficeDoneInfoService {

    /**
     * 取消上会
     *
     * @param processInstanceId
     */
    void cancelMeeting(String processInstanceId);

    /**
     * 监控办结统计
     *
     * @param itemId
     * @return
     */
    int countByItemId(String itemId);

    /**
     * 统计个人办结件
     *
     * @param userId
     * @param itemId
     * @return
     */
    int countByUserId(String userId, String itemId);

    /**
     * 根据系统名称统计个人办结件
     *
     * @param orgUnitId
     * @param systemName
     * @return
     */
    int countByUserIdAndSystemName(String orgUnitId, String systemName);

    /**
     * 监控在办统计
     *
     * @param itemId
     * @return
     */
    long countDoingByItemId(String itemId);

    /**
     * 根据流程实例id删除办件信息
     *
     * @param processInstanceId
     * @return
     */
    boolean deleteOfficeDoneInfo(String processInstanceId);

    /**
     * 据流程实例id获取办件信息
     *
     * @param processInstanceId
     * @return
     */
    OfficeDoneInfo findByProcessInstanceId(String processInstanceId);

    /**
     * 根据流程编号获取办件信息
     *
     * @param processSerialNumber
     * @return
     */
    OfficeDoneInfo findByProcessSerialNumber(String processSerialNumber);

    /**
     * 上会台账列表
     *
     * @param userName
     * @param deptName
     * @param title
     * @param meetingType
     * @param page
     * @param rows
     * @return Y9Page<OfficeDoneInfoModel>
     */
    Y9Page<OfficeDoneInfoModel> pageMeetingList(String userName, String deptName, String title, String meetingType,
        Integer page, Integer rows);

    /**
     * 保存办件信息,不经过kafka消息队列，直接保存
     *
     * @param info
     * @throws Exception
     */
    void saveOfficeDone(OfficeDoneInfo info) throws Exception;

    /**
     * 科室所有件
     *
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
    Y9Page<OfficeDoneInfoModel> searchAllByDeptId(String deptId, String title, String itemId, String userName,
        String state, String year, Integer page, Integer rows);

    /**
     * 个人所有件搜索
     *
     * @param userId
     * @param title
     * @param itemId
     * @param userName
     * @param state
     * @param year
     * @param startDate
     * @param endDate
     * @param page
     * @param rows
     * @return
     */
    Y9Page<OfficeDoneInfoModel> searchAllByUserId(String userId, String title, String itemId, String userName,
        String state, String year, String startDate, String endDate, Integer page, Integer rows);

    /**
     * 根据系统，个人所有件搜索
     *
     * @param orgUnitId 人员、岗位id
     * @param title 标题
     * @param systemName 系统名称
     * @param itemId 事项id
     * @param state 状态
     * @param year 年份
     * @param startdate 开始日期
     * @param enddate 结束日期
     * @param page 页码
     * @param rows 条数
     * @return
     */
    Y9Page<OfficeDoneInfoModel> searchAllByUserIdAndSystemName(String orgUnitId, String title, String systemName,
        String itemId, String state, String year, String startdate, String enddate, Integer page, Integer rows);

    /**
     * 根据系统，个人所有件搜索
     *
     * @param orgUnitId 人员、岗位id
     * @param title 标题
     * @param systemName 系统名称
     * @param itemId 事项id
     * @param target 目标
     * @param state 状态
     * @param year 年份
     * @param startdate 开始日期
     * @param enddate 结束日期
     * @param page 页码
     * @param rows 条数
     * @return
     */
    Y9Page<OfficeDoneInfoModel> searchAllByUserIdAndSystemName4xxx(String orgUnitId, String title, String systemName,
        String itemId, String target, String state, String year, String startdate, String enddate, Integer page,
        Integer rows);

    /**
     * 监控办件列表
     *
     * @param searchName
     * @param itemId
     * @param userName
     * @param state
     * @param year
     * @param page
     * @param rows
     * @return
     */
    Y9Page<OfficeDoneInfoModel> searchAllList(String searchName, String itemId, String userName, String state,
        String year, Integer page, Integer rows);

    /**
     * 获取监控在办，办结件列表
     *
     * @param title
     * @param itemId
     * @param state
     * @param startdate
     * @param enddate
     * @param page
     * @param rows
     * @return
     */
    Y9Page<OfficeDoneInfoModel> searchByItemId(String title, String itemId, String state, String startdate,
        String enddate, Integer page, Integer rows);

    /**
     * 获取个人办结件列表
     *
     * @param userId
     * @param title
     * @param itemId
     * @param startdate
     * @param enddate
     * @param page
     * @param rows
     * @return
     */
    Y9Page<OfficeDoneInfoModel> searchByUserId(String userId, String title, String itemId, String startdate,
        String enddate, Integer page, Integer rows);

    /**
     * 根据岗位id,系统名称，获取个人办结件列表
     *
     * @param orgUnitId
     * @param title
     * @param systemName
     * @param startdate
     * @param enddate
     * @param page
     * @param rows
     * @return
     */
    Y9Page<OfficeDoneInfoModel> searchByUserIdAndSystemName(String orgUnitId, String title, String systemName,
        String startdate, String enddate, Integer page, Integer rows);

    /**
     * 上会
     *
     * @param processInstanceId
     * @param meetingType
     */
    void setMeeting(String processInstanceId, String meetingType);

}
