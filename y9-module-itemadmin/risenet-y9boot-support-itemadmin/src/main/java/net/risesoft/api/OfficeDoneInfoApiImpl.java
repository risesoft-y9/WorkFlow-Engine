package net.risesoft.api;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.OfficeDoneInfoApi;
import net.risesoft.model.itemadmin.OfficeDoneInfoModel;
import net.risesoft.nosql.elastic.entity.OfficeDoneInfo;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.OfficeDoneInfoService;
import net.risesoft.y9.Y9FlowableHolder;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * 办结信息接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/officeDoneInfo", produces = MediaType.APPLICATION_JSON_VALUE)
public class OfficeDoneInfoApiImpl implements OfficeDoneInfoApi {

    private final OfficeDoneInfoService officeDoneInfoService;

    /**
     * 取消上会（定制）
     *
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> cancelMeeting(@RequestParam String processInstanceId) {
        officeDoneInfoService.cancelMeeting(processInstanceId);
        return Y9Result.success();
    }

    /**
     * 监控办结统计数量
     *
     * @param itemId 事项id
     * @return {@code Y9Result<Integer>} 通用请求返回对象 - data 是监控办结统计
     * @since 9.6.6
     */
    @Override
    public Y9Result<Integer> countByItemId(String itemId) {
        int count = officeDoneInfoService.countByItemId(itemId);
        return Y9Result.success(count);
    }

    /**
     * 统计个人办结件数量
     *
     * @param itemId 事项id
     * @return {@code Y9Result<Integer>} 通用请求返回对象 - data 是个人办结件数量
     * @since 9.6.6
     */
    @Override
    public Y9Result<Integer> countByUserId(String itemId) {
        int count = officeDoneInfoService.countByUserId(Y9FlowableHolder.getPositionId(), itemId);
        return Y9Result.success(count);
    }

    /**
     * 根据系统名称统计个人办结件数量
     *
     * @param systemName 系统名称
     * @return {@code Y9Result<Integer>} 通用请求返回对象 - data 是个人办结件数量
     * @since 9.6.6
     */
    @Override
    public Y9Result<Integer> countByUserIdAndSystemName(String systemName) {
        int count = officeDoneInfoService.countByUserIdAndSystemName(Y9FlowableHolder.getPositionId(), systemName);
        return Y9Result.success(count);
    }

    /**
     * 监控在办统计数量
     *
     * @param itemId 事项id
     * @return {@code Y9Result<Long>} 通用请求返回对象 - data 是监控在办统计
     * @since 9.6.6
     */
    @Override
    public Y9Result<Long> countDoingByItemId(String itemId) {
        long count = officeDoneInfoService.countDoingByItemId(itemId);
        return Y9Result.success(count);
    }

    /**
     * 根据流程实例id删除办件信息
     *
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> deleteOfficeDoneInfo(@RequestParam String processInstanceId) {
        officeDoneInfoService.deleteOfficeDoneInfo(processInstanceId);
        return Y9Result.success();
    }

    /**
     * 根据流程实例id获取办件信息
     *
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<OfficeDoneInfoModel>} 通用请求返回对象 - data 是办结信息
     * @since 9.6.6
     */
    @Override
    public Y9Result<OfficeDoneInfoModel> findByProcessInstanceId(@RequestParam String processInstanceId) {
        OfficeDoneInfo officeDoneInfo = officeDoneInfoService.findByProcessInstanceId(processInstanceId);
        OfficeDoneInfoModel officeDoneInfoModel = null;
        if (officeDoneInfo != null) {
            officeDoneInfoModel = new OfficeDoneInfoModel();
            Y9BeanUtil.copyProperties(officeDoneInfo, officeDoneInfoModel);
        }
        return Y9Result.success(officeDoneInfoModel);
    }

    /**
     * 根据流程实例编号获取办件信息
     *
     * @param processSerialNumber 流程实例编号
     * @return {@code Y9Result<OfficeDoneInfoModel>} 通用请求返回对象 - data 是办结信息
     * @since 9.6.6
     */
    @Override
    public Y9Result<OfficeDoneInfoModel> findByProcessSerialNumber(@RequestParam String processSerialNumber) {
        OfficeDoneInfo officeDoneInfo = officeDoneInfoService.findByProcessSerialNumber(processSerialNumber);
        OfficeDoneInfoModel officeDoneInfoModel = null;
        if (officeDoneInfo != null) {
            officeDoneInfoModel = new OfficeDoneInfoModel();
            Y9BeanUtil.copyProperties(officeDoneInfo, officeDoneInfoModel);
        }
        return Y9Result.success(officeDoneInfoModel);
    }

    /**
     * 上会台账列表（定制）
     *
     * @param userName 申请人
     * @param deptName 部门名称
     * @param title 标题
     * @param meetingType 会议类型
     * @param page 页码
     * @param rows 条数
     * @return {@code Y9Page<OfficeDoneInfoModel>} 通用分页请求返回对象 - rows 是办结信息
     * @since 9.6.6
     */
    @Override
    public Y9Page<OfficeDoneInfoModel> getMeetingList(String userName, String deptName, String title,
        String meetingType, @RequestParam Integer page, @RequestParam Integer rows) {
        return officeDoneInfoService.pageMeetingList(userName, deptName, title, meetingType, page, rows);
    }

    /**
     * 保存办件信息（不经过kafka消息队列，直接保存）
     *
     * @param info 办结信息
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @throws Exception Exception
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> saveOfficeDone(@RequestBody OfficeDoneInfoModel info) throws Exception {
        OfficeDoneInfo officeInfo = new OfficeDoneInfo();
        Y9BeanUtil.copyProperties(info, officeInfo);
        officeDoneInfoService.saveOfficeDone(officeInfo);
        return Y9Result.success();
    }

    /**
     * 获取科室所有件列表
     *
     * @param deptId 部门id
     * @param title 标题
     * @param itemId 事项id
     * @param userName 人员名称
     * @param state 状态
     * @param year 年份
     * @param page 页码
     * @param rows 条数
     * @return {@code Y9Page<OfficeDoneInfoModel>} 通用分页请求返回对象 - rows 是办结数据
     * @since 9.6.6
     */
    @Override
    public Y9Page<OfficeDoneInfoModel> searchAllByDeptId(@RequestParam String deptId, String title, String itemId,
        String userName, String state, String year, @RequestParam Integer page, @RequestParam Integer rows) {
        return officeDoneInfoService.searchAllByDeptId(deptId, title, itemId, userName, state, year, page, rows);
    }

    /**
     * 获取个人所有件列表
     *
     * @param title 标题
     * @param itemId 事项id
     * @param userName 人员名称
     * @param state 状态
     * @param year 年份
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param page 页码
     * @param rows 条数
     * @return {@code Y9Page<OfficeDoneInfoModel>} 通用分页请求返回对象 - rows 是办结数据
     * @since 9.6.6
     */
    @Override
    public Y9Page<OfficeDoneInfoModel> searchAllByUserId(String title, String itemId, String userName, String state,
        String year, String startDate, String endDate, @RequestParam Integer page, @RequestParam Integer rows) {
        return officeDoneInfoService.searchAllByUserId(Y9FlowableHolder.getPositionId(), title, itemId, userName, state,
            year, startDate, endDate, page, rows);
    }

    /**
     * 根据系统查询个人所有件列表
     *
     * @param title 标题
     * @param systemName 系统名称
     * @param itemId 事项id
     * @param state 状态
     * @param year 年份
     * @param startdate 开始日期
     * @param enddate 结束日期
     * @param page 页码
     * @param rows 条数
     * @return {@code Y9Page<OfficeDoneInfoModel>} 通用分页请求返回对象 - rows 办件信息
     * @since 9.6.6
     */
    @Override
    public Y9Page<OfficeDoneInfoModel> searchAllByUserIdAndSystemName(String title, String systemName, String itemId,
        String state, String year, String startdate, String enddate, @RequestParam Integer page,
        @RequestParam Integer rows) {
        return officeDoneInfoService.searchAllByUserIdAndSystemName(Y9FlowableHolder.getPositionId(), title, systemName,
            itemId, state, year, startdate, enddate, page, rows);
    }

    /**
     * 获取监控办件列表
     *
     * @param searchName 搜索词
     * @param itemId 事项id
     * @param userName 人员名称
     * @param state 状态
     * @param year 年份
     * @param page 页码
     * @param rows 条数
     * @return {@code Y9Page<OfficeDoneInfoModel>} 通用分页请求返回对象 - rows 是办结信息
     * @since 9.6.6
     */
    @Override
    public Y9Page<OfficeDoneInfoModel> searchAllList(String searchName, String itemId, String userName, String state,
        String year, @RequestParam Integer page, @RequestParam Integer rows) {
        return officeDoneInfoService.searchAllList(searchName, itemId, userName, state, year, page, rows);
    }

    /**
     * 获取监控在办、办结件列表
     *
     * @param title 搜索词
     * @param itemId 事项id
     * @param state 状态
     * @param startdate 开始日期
     * @param enddate 结束日期
     * @param page 页码
     * @param rows 条数
     * @return {@code Y9Page<OfficeDoneInfoModel>} 通用分页请求返回对象 - rows 是办结信息
     * @since 9.6.6
     */
    @Override
    public Y9Page<OfficeDoneInfoModel> searchByItemId(String title, String itemId, String state, String startdate,
        String enddate, @RequestParam Integer page, @RequestParam Integer rows) {
        return officeDoneInfoService.searchByItemId(title, itemId, state, startdate, enddate, page, rows);
    }

    /**
     * 获取个人办结件列表
     *
     * @param title 搜索词
     * @param itemId 事项id
     * @param startdate 开始日期
     * @param enddate 结束日期
     * @param page 页码
     * @param rows 条数
     * @return {@code Y9Page<OfficeDoneInfoModel>} 通用分页请求返回对象 - rows 是办结信息
     * @since 9.6.6
     */
    @Override
    public Y9Page<OfficeDoneInfoModel> searchByUserId(String title, String itemId, String startdate, String enddate,
        @RequestParam Integer page, @RequestParam Integer rows) {
        return officeDoneInfoService.searchByUserId(Y9FlowableHolder.getPositionId(), title, itemId, startdate, enddate,
            page, rows);
    }

    /**
     * 根据id、系统名称、获取个人办结件列表
     *
     * @param title 搜索词
     * @param systemName 系统名称
     * @param startdate 开始日期
     * @param enddate 结束日期
     * @param page 页码
     * @param rows 条数
     * @return {@code Y9Page<OfficeDoneInfoModel>} 通用分页请求返回对象 - rows 是办结信息
     * @since 9.6.6
     */
    @Override
    public Y9Page<OfficeDoneInfoModel> searchByUserIdAndSystemName(String title, String systemName, String startdate,
        String enddate, @RequestParam Integer page, @RequestParam Integer rows) {
        return officeDoneInfoService.searchByUserIdAndSystemName(Y9FlowableHolder.getPositionId(), title, systemName,
            startdate, enddate, page, rows);
    }

    /**
     * 设置会议类型(上会)
     *
     * @param processInstanceId 流程实例id
     * @param meetingType 会议类型
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> setMeeting(@RequestParam String processInstanceId, @RequestParam String meetingType) {
        officeDoneInfoService.setMeeting(processInstanceId, meetingType);
        return Y9Result.successMsg("设置成功");
    }

}
