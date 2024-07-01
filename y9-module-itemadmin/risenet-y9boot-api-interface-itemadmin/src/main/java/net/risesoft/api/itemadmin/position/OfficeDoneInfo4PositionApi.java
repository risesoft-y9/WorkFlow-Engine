package net.risesoft.api.itemadmin.position;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

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
     * @return Y9Result<Object>
     */
    @PostMapping("/cancelMeeting")
    Y9Result<Object> cancelMeeting(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 监控办结统计
     *
     * @param tenantId 租户id
     * @param itemId 事项id
     * @return Y9Result<Integer>
     */
    @GetMapping("/countByItemId")
    Y9Result<Integer> countByItemId(@RequestParam("tenantId") String tenantId, @RequestParam("itemId") String itemId);

    /**
     * 统计个人办结件
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param itemId 事项id
     * @return Y9Result<Integer>
     */
    @GetMapping("/countByPositionId")
    Y9Result<Integer> countByPositionId(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("itemId") String itemId);

    /**
     * 根据系统名称统计个人办结件
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param systemName 系统名称
     * @return Y9Result<Integer>
     */
    @GetMapping("/countByPositionIdAndSystemName")
    Y9Result<Integer> countByPositionIdAndSystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("systemName") String systemName);

    /**
     * 监控在办统计
     *
     * @param tenantId 租户id
     * @param itemId 事项id
     * @return Y9Result<Long>
     */
    @GetMapping("/countDoingByItemId")
    Y9Result<Long> countDoingByItemId(@RequestParam("tenantId") String tenantId, @RequestParam("itemId") String itemId);

    /**
     * 根据流程实例id删除办结信息
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return Y9Result<Object>
     */
    @PostMapping("/deleteOfficeDoneInfo")
    Y9Result<Object> deleteOfficeDoneInfo(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 根据流程实例id获取办结信息
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return Y9Result<OfficeDoneInfoModel>
     */
    @GetMapping("/findByProcessInstanceId")
    Y9Result<OfficeDoneInfoModel> findByProcessInstanceId(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId);

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
     * @return Y9Page<OfficeDoneInfoModel>
     */
    @GetMapping("/getMeetingList")
    Y9Page<OfficeDoneInfoModel> getMeetingList(@RequestParam("tenantId") String tenantId,
        @RequestParam("userName") String userName, @RequestParam("deptName") String deptName,
        @RequestParam("title") String title, @RequestParam("meetingType") String meetingType,
        @RequestParam("page") Integer page, @RequestParam("rows") Integer rows);

    /**
     * 保存办结信息,不经过kafka消息队列，直接保存
     *
     * @param tenantId 租户id
     * @param info 办结信息
     * @return Y9Result<Object>
     * @throws Exception Exception
     */
    @PostMapping(value = "/saveOfficeDone", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Object> saveOfficeDone(@RequestParam("tenantId") String tenantId, @RequestBody OfficeDoneInfoModel info)
        throws Exception;

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
     * @return Y9Page<OfficeDoneInfoModel>
     */
    @GetMapping("/searchAllByDeptId")
    Y9Page<OfficeDoneInfoModel> searchAllByDeptId(@RequestParam("tenantId") String tenantId,
        @RequestParam("deptId") String deptId, @RequestParam("title") String title,
        @RequestParam("itemId") String itemId, @RequestParam("userName") String userName,
        @RequestParam("state") String state, @RequestParam("year") String year, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows);

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
     * @return Y9Page<OfficeDoneInfoModel>
     */
    @GetMapping("/searchAllByPositionId")
    Y9Page<OfficeDoneInfoModel> searchAllByPositionId(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("title") String title,
        @RequestParam("itemId") String itemId, @RequestParam("userName") String userName,
        @RequestParam("state") String state, @RequestParam("year") String year,
        @RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate,
        @RequestParam("page") Integer page, @RequestParam("rows") Integer rows);

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
     * @return Y9Page<OfficeDoneInfoModel>
     */
    @GetMapping("/searchAllList")
    Y9Page<OfficeDoneInfoModel> searchAllList(@RequestParam("tenantId") String tenantId,
        @RequestParam("searchName") String searchName, @RequestParam("itemId") String itemId,
        @RequestParam("userName") String userName, @RequestParam("state") String state,
        @RequestParam("year") String year, @RequestParam("page") Integer page, @RequestParam("rows") Integer rows);

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
     * @return Y9Page<OfficeDoneInfoModel>
     */
    @GetMapping("/searchByItemId")
    Y9Page<OfficeDoneInfoModel> searchByItemId(@RequestParam("tenantId") String tenantId,
        @RequestParam("title") String title, @RequestParam("itemId") String itemId, @RequestParam("state") String state,
        @RequestParam("startdate") String startdate, @RequestParam("enddate") String enddate,
        @RequestParam("page") Integer page, @RequestParam("rows") Integer rows);

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
     * @return Y9Page<OfficeDoneInfoModel>
     */
    @GetMapping("/searchByPositionId")
    Y9Page<OfficeDoneInfoModel> searchByPositionId(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("title") String title,
        @RequestParam("itemId") String itemId, @RequestParam("startdate") String startdate,
        @RequestParam("enddate") String enddate, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows);

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
     * @return Y9Page<OfficeDoneInfoModel>
     */
    @GetMapping("/searchByPositionIdAndSystemName")
    Y9Page<OfficeDoneInfoModel> searchByPositionIdAndSystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("title") String title,
        @RequestParam("systemName") String systemName, @RequestParam("startdate") String startdate,
        @RequestParam("enddate") String enddate, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows);

    /**
     * 上会，当代研究所
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param meetingType 会议类型
     * @return Y9Result<Object>
     */
    @PostMapping("/setMeeting")
    Y9Result<Object> setMeeting(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId, @RequestParam("meetingType") String meetingType);

}