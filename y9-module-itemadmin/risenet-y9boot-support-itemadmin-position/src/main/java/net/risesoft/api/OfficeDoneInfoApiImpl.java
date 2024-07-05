package net.risesoft.api;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.position.OfficeDoneInfo4PositionApi;
import net.risesoft.model.itemadmin.OfficeDoneInfoModel;
import net.risesoft.nosql.elastic.entity.OfficeDoneInfo;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.OfficeDoneInfoService;
import net.risesoft.y9.Y9LoginUserHolder;
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
@RequestMapping(value = "/services/rest/officeDoneInfo4Position")
public class OfficeDoneInfoApiImpl implements OfficeDoneInfo4PositionApi {

    private final OfficeDoneInfoService officeDoneInfoService;

    /**
     * 取消上会（定制）
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> cancelMeeting(@RequestParam String tenantId, @RequestParam String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        officeDoneInfoService.cancelMeeting(processInstanceId);
        return Y9Result.success();
    }

    /**
     * 监控办结统计
     *
     * @param tenantId 租户id
     * @param itemId 事项id
     * @return {@code Y9Result<Integer>} 通用请求返回对象 - data 是监控办结统计
     * @since 9.6.6
     */
    @Override
    public Y9Result<Integer> countByItemId(@RequestParam String tenantId, String itemId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        int count = officeDoneInfoService.countByItemId(itemId);
        return Y9Result.success(count);
    }

    /**
     * 统计个人办结件
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param itemId 事项id
     * @return {@code Y9Result<Integer>} 通用请求返回对象 - data 是个人办结件数量
     * @since 9.6.6
     */
    @Override
    public Y9Result<Integer> countByPositionId(@RequestParam String tenantId, @RequestParam String positionId,
        String itemId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        int count = officeDoneInfoService.countByUserId(positionId, itemId);
        return Y9Result.success(count);
    }

    /**
     * 根据系统名称统计个人办结件
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param systemName 系统名称
     * @return {@code Y9Result<Integer>} 通用请求返回对象 - data 是个人办结件数量
     * @since 9.6.6
     */
    @Override
    public Y9Result<Integer> countByPositionIdAndSystemName(@RequestParam String tenantId,
        @RequestParam String positionId, String systemName) {
        Y9LoginUserHolder.setTenantId(tenantId);
        int count = officeDoneInfoService.countByPositionIdAndSystemName(positionId, systemName);
        return Y9Result.success(count);
    }

    /**
     * 监控在办统计
     *
     * @param tenantId 租户id
     * @param itemId 事项id
     * @return {@code Y9Result<Long>} 通用请求返回对象 - data 是监控在办统计
     * @since 9.6.6
     */
    @Override
    public Y9Result<Long> countDoingByItemId(@RequestParam String tenantId, String itemId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        long count = officeDoneInfoService.countDoingByItemId(itemId);
        return Y9Result.success(count);
    }

    /**
     * 根据流程实例id删除办结信息
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> deleteOfficeDoneInfo(@RequestParam String tenantId,
        @RequestParam String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        officeDoneInfoService.deleteOfficeDoneInfo(processInstanceId);
        return Y9Result.success();
    }

    /**
     * 根据流程实例id获取办结信息
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<OfficeDoneInfoModel>} 通用请求返回对象 - data 是办结信息
     * @since 9.6.6
     */
    @Override
    public Y9Result<OfficeDoneInfoModel> findByProcessInstanceId(@RequestParam String tenantId,
        @RequestParam String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        OfficeDoneInfo officeDoneInfo = officeDoneInfoService.findByProcessInstanceId(processInstanceId);
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
     * @param tenantId 租户id
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
    public Y9Page<OfficeDoneInfoModel> getMeetingList(@RequestParam String tenantId, String userName, String deptName,
        String title, String meetingType, @RequestParam Integer page, @RequestParam Integer rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return officeDoneInfoService.getMeetingList(userName, deptName, title, meetingType, page, rows);
    }

    /**
     * 保存办结信息,不经过kafka消息队列，直接保存
     *
     * @param tenantId 租户id
     * @param info 办结信息
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @throws Exception Exception
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> saveOfficeDone(@RequestParam String tenantId, @RequestBody OfficeDoneInfoModel info)
        throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        OfficeDoneInfo officeInfo = new OfficeDoneInfo();
        Y9BeanUtil.copyProperties(info, officeInfo);
        officeDoneInfoService.saveOfficeDone(officeInfo);
        return Y9Result.success();
    }

    /**
     * 科室所有件列表
     *
     * @param tenantId 租户id
     * @param deptId 部门id
     * @param title 标题
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
    public Y9Page<OfficeDoneInfoModel> searchAllByDeptId(@RequestParam String tenantId, @RequestParam String deptId,
        String title, String itemId, String userName, String state, String year, @RequestParam Integer page,
        @RequestParam Integer rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return officeDoneInfoService.searchAllByDeptId(deptId, title, itemId, userName, state, year, page, rows);
    }

    /**
     * 个人所有件搜索
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param title 标题
     * @param itemId 事项id
     * @param userName 人员名称
     * @param state 状态
     * @param year 年份
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param page 页码
     * @param rows 条数
     * @return {@code Y9Page<OfficeDoneInfoModel>} 通用分页请求返回对象 - rows 是办结信息
     * @since 9.6.6
     */
    @Override
    public Y9Page<OfficeDoneInfoModel> searchAllByPositionId(@RequestParam String tenantId,
        @RequestParam String positionId, String title, String itemId, String userName, String state, String year,
        String startDate, String endDate, @RequestParam Integer page, @RequestParam Integer rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return officeDoneInfoService.searchAllByUserId(positionId, title, itemId, userName, state, year, startDate,
            endDate, page, rows);
    }

    /**
     * 监控办件列表
     *
     * @param tenantId 租户id
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
    public Y9Page<OfficeDoneInfoModel> searchAllList(@RequestParam String tenantId, String searchName, String itemId,
        String userName, String state, String year, @RequestParam Integer page, @RequestParam Integer rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return officeDoneInfoService.searchAllList(searchName, itemId, userName, state, year, page, rows);
    }

    /**
     * 获取监控在办，办结件列表
     *
     * @param tenantId 租户id
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
    public Y9Page<OfficeDoneInfoModel> searchByItemId(@RequestParam String tenantId, String title, String itemId,
        String state, String startdate, String enddate, @RequestParam Integer page, @RequestParam Integer rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return officeDoneInfoService.searchByItemId(title, itemId, state, startdate, enddate, page, rows);
    }

    /**
     * 获取个人办结件列表
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
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
    public Y9Page<OfficeDoneInfoModel> searchByPositionId(@RequestParam String tenantId,
        @RequestParam String positionId, String title, String itemId, String startdate, String enddate,
        @RequestParam Integer page, @RequestParam Integer rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return officeDoneInfoService.searchByUserId(positionId, title, itemId, startdate, enddate, page, rows);
    }

    /**
     * 根据岗位id,系统名称，获取个人办结件列表
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
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
    public Y9Page<OfficeDoneInfoModel> searchByPositionIdAndSystemName(@RequestParam String tenantId,
        @RequestParam String positionId, String title, String systemName, String startdate, String enddate,
        @RequestParam Integer page, @RequestParam Integer rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return officeDoneInfoService.searchByPositionIdAndSystemName(positionId, title, systemName, startdate, enddate,
            page, rows);
    }

    /**
     * 设置会议类型(上会)
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param meetingType 会议类型
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> setMeeting(@RequestParam String tenantId, @RequestParam String processInstanceId,
        @RequestParam String meetingType) {
        Y9LoginUserHolder.setTenantId(tenantId);
        officeDoneInfoService.setMeeting(processInstanceId, meetingType);
        return Y9Result.successMsg("设置成功");
    }

}
