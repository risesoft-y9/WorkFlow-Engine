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
     * @return {@code Y9Result<Object>} 通用请求返回对象
     */
    @PostMapping("/cancelMeeting")
    Y9Result<Object> cancelMeeting(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 监控办结统计
     *
     * @param tenantId 租户id
     * @param itemId 事项id
     * @return {@code Y9Result<Integer>} 通用请求返回对象 - data 是监控办结统计
     */
    @GetMapping("/countByItemId")
    Y9Result<Integer> countByItemId(@RequestParam("tenantId") String tenantId,
        @RequestParam(value = "itemId", required = false) String itemId);

    /**
     * 统计个人办结件
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param itemId 事项id
     * @return {@code Y9Result<Integer>} 通用请求返回对象 - data 是个人办结件数量
     */
    @GetMapping("/countByPositionId")
    Y9Result<Integer> countByPositionId(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam(value = "itemId", required = false) String itemId);

    /**
     * 根据系统名称统计个人办结件
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param systemName 系统名称
     * @return {@code Y9Result<Integer>} 通用请求返回对象 - data 是个人办结件数量
     */
    @GetMapping("/countByPositionIdAndSystemName")
    Y9Result<Integer> countByPositionIdAndSystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId,
        @RequestParam(value = "systemName", required = false) String systemName);

    /**
     * 监控在办统计
     *
     * @param tenantId 租户id
     * @param itemId 事项id
     * @return {@code Y9Result<Long>} 通用请求返回对象 - data 是监控在办统计
     */
    @GetMapping("/countDoingByItemId")
    Y9Result<Long> countDoingByItemId(@RequestParam("tenantId") String tenantId,
        @RequestParam(value = "itemId", required = false) String itemId);

    /**
     * 根据流程实例id删除办结信息
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     */
    @PostMapping("/deleteOfficeDoneInfo")
    Y9Result<Object> deleteOfficeDoneInfo(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 根据流程实例id获取办结信息
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<OfficeDoneInfoModel>} 通用请求返回对象 - data 是办结信息
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
     * @return {@code Y9Page<OfficeDoneInfoModel>} 通用分页请求返回对象 - rows 是办结信息
     */
    @GetMapping("/getMeetingList")
    Y9Page<OfficeDoneInfoModel> getMeetingList(@RequestParam("tenantId") String tenantId,
        @RequestParam(value = "userName", required = false) String userName,
        @RequestParam(value = "deptName", required = false) String deptName,
        @RequestParam(value = "title", required = false) String title,
        @RequestParam(value = "meetingType", required = false) String meetingType, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows);

    /**
     * 保存办结信息,不经过kafka消息队列，直接保存
     *
     * @param tenantId 租户id
     * @param info 办结信息
     * @return {@code Y9Result<Object>} 通用请求返回对象
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
     * @return {@code Y9Page<OfficeDoneInfoModel>} 通用分页请求返回对象 - rows 是办结信息
     */
    @GetMapping("/searchAllByDeptId")
    Y9Page<OfficeDoneInfoModel> searchAllByDeptId(@RequestParam("tenantId") String tenantId,
        @RequestParam("deptId") String deptId, @RequestParam(value = "title", required = false) String title,
        @RequestParam(value = "itemId", required = false) String itemId,
        @RequestParam(value = "userName", required = false) String userName,
        @RequestParam(value = "state", required = false) String state,
        @RequestParam(value = "year", required = false) String year, @RequestParam("page") Integer page,
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
     * @return {@code Y9Page<OfficeDoneInfoModel>} 通用分页请求返回对象 - rows 是办结信息
     */
    @GetMapping("/searchAllByPositionId")
    Y9Page<OfficeDoneInfoModel> searchAllByPositionId(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam(value = "title", required = false) String title,
        @RequestParam(value = "itemId", required = false) String itemId,
        @RequestParam(value = "userName", required = false) String userName,
        @RequestParam(value = "state", required = false) String state,
        @RequestParam(value = "year", required = false) String year,
        @RequestParam(value = "startDate", required = false) String startDate,
        @RequestParam(value = "endDate", required = false) String endDate, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows);

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
     * @return {@code Y9Page<OfficeDoneInfoModel>} 通用分页请求返回对象 - rows 是办结信息
     */
    @GetMapping("/searchAllList")
    Y9Page<OfficeDoneInfoModel> searchAllList(@RequestParam("tenantId") String tenantId,
        @RequestParam(value = "searchName", required = false) String searchName,
        @RequestParam(value = "itemId", required = false) String itemId,
        @RequestParam(value = "userName", required = false) String userName,
        @RequestParam(value = "state", required = false) String state,
        @RequestParam(value = "year", required = false) String year, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows);

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
     * @return {@code Y9Page<OfficeDoneInfoModel>} 通用分页请求返回对象 - rows 是办结信息
     */
    @GetMapping("/searchByItemId")
    Y9Page<OfficeDoneInfoModel> searchByItemId(@RequestParam("tenantId") String tenantId,
        @RequestParam(value = "title", required = false) String title,
        @RequestParam(value = "itemId", required = false) String itemId,
        @RequestParam(value = "state", required = false) String state,
        @RequestParam(value = "startdate", required = false) String startdate,
        @RequestParam(value = "enddate", required = false) String enddate, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows);

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
     * @return {@code Y9Page<OfficeDoneInfoModel>} 通用分页请求返回对象 - rows 是办结信息
     */
    @GetMapping("/searchByPositionId")
    Y9Page<OfficeDoneInfoModel> searchByPositionId(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam(value = "title", required = false) String title,
        @RequestParam(value = "itemId", required = false) String itemId,
        @RequestParam(value = "startdate", required = false) String startdate,
        @RequestParam(value = "enddate", required = false) String enddate, @RequestParam("page") Integer page,
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
     * @return {@code Y9Page<OfficeDoneInfoModel>} 通用分页请求返回对象 - rows 是办结信息
     */
    @GetMapping("/searchByPositionIdAndSystemName")
    Y9Page<OfficeDoneInfoModel> searchByPositionIdAndSystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam(value = "title", required = false) String title,
        @RequestParam(value = "systemName", required = false) String systemName,
        @RequestParam(value = "startdate", required = false) String startdate,
        @RequestParam(value = "enddate", required = false) String enddate, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows);

    /**
     * 上会，当代研究所
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param meetingType 会议类型
     * @return {@code Y9Result<Object>} 通用请求返回对象
     */
    @PostMapping("/setMeeting")
    Y9Result<Object> setMeeting(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId, @RequestParam("meetingType") String meetingType);

}