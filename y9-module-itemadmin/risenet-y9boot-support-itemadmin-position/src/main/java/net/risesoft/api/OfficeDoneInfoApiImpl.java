package net.risesoft.api;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
     * 取消上会(ddyjs)
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return Y9Result<Object>
     */
    @Override
    @PostMapping(value = "/cancelMeeting", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Object> cancelMeeting(String tenantId, String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        officeDoneInfoService.cancelMeeting(processInstanceId);
        return Y9Result.success();
    }

    /**
     * 监控办结统计
     *
     * @param tenantId 租户id
     * @param itemId 事项id
     * @return Y9Result<Integer>
     */
    @Override
    @GetMapping(value = "/countByItemId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Integer> countByItemId(String tenantId, String itemId) {
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
     * @return Y9Result<Integer>
     */
    @Override
    @GetMapping(value = "/countByPositionId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Integer> countByPositionId(String tenantId, String positionId, String itemId) {
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
     * @return Y9Result<Integer>
     */
    @Override
    @GetMapping(value = "/countByPositionIdAndSystemName", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Integer> countByPositionIdAndSystemName(String tenantId, String positionId, String systemName) {
        Y9LoginUserHolder.setTenantId(tenantId);
        int count = officeDoneInfoService.countByPositionIdAndSystemName(positionId, systemName);
        return Y9Result.success(count);
    }

    /**
     * @param tenantId 租户id
     * @param itemId 事项id
     * @return Y9Result<Long>
     */
    @Override
    @GetMapping(value = "/countDoingByItemId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Long> countDoingByItemId(String tenantId, String itemId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        long count = officeDoneInfoService.countDoingByItemId(itemId);
        return Y9Result.success(count);
    }

    /**
     * 根据流程实例id删除办结信息
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return Y9Result<Object>
     */
    @Override
    @PostMapping(value = "/deleteOfficeDoneInfo", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Object> deleteOfficeDoneInfo(String tenantId, String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        officeDoneInfoService.deleteOfficeDoneInfo(processInstanceId);
        return Y9Result.success();
    }

    /**
     * 根据流程实例id获取办结信息
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return Y9Result<OfficeDoneInfoModel>
     */
    @Override
    @GetMapping(value = "/findByProcessInstanceId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<OfficeDoneInfoModel> findByProcessInstanceId(String tenantId, String processInstanceId) {
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
     * 上会台账列表(ddyjs)
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
    @Override
    @GetMapping(value = "/getMeetingList", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Page<OfficeDoneInfoModel> getMeetingList(String tenantId, String userName, String deptName, String title,
        String meetingType, Integer page, Integer rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return officeDoneInfoService.getMeetingList(userName, deptName, title, meetingType, page, rows);
    }

    /**
     * 保存办结信息,不经过kafka消息队列，直接保存
     *
     * @param tenantId 租户id
     * @param info 办结信息
     * @return Y9Result<Object>
     * @throws Exception 异常
     */
    @Override
    @PostMapping(value = "/saveOfficeDone", produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Object> saveOfficeDone(String tenantId, @RequestBody OfficeDoneInfoModel info) throws Exception {
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
     * @return Y9Page<OfficeDoneInfoModel>
     */
    @Override
    @GetMapping(value = "/searchAllByDeptId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Page<OfficeDoneInfoModel> searchAllByDeptId(String tenantId, String deptId, String title, String itemId,
        String userName, String state, String year, Integer page, Integer rows) {
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
     * @return Y9Page<OfficeDoneInfoModel>
     */
    @Override
    @GetMapping(value = "/searchAllByPositionId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Page<OfficeDoneInfoModel> searchAllByPositionId(String tenantId, String positionId, String title,
        String itemId, String userName, String state, String year, String startDate, String endDate, Integer page,
        Integer rows) {
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
     * @return Y9Page<OfficeDoneInfoModel>
     */
    @Override
    @GetMapping(value = "/searchAllList", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Page<OfficeDoneInfoModel> searchAllList(String tenantId, String searchName, String itemId, String userName,
        String state, String year, Integer page, Integer rows) {
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
     * @return Y9Page<OfficeDoneInfoModel>
     */
    @Override
    @GetMapping(value = "/searchByItemId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Page<OfficeDoneInfoModel> searchByItemId(String tenantId, String title, String itemId, String state,
        String startdate, String enddate, Integer page, Integer rows) {
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
     * @return Y9Page<OfficeDoneInfoModel>
     */
    @Override
    @GetMapping(value = "/searchByPositionId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Page<OfficeDoneInfoModel> searchByPositionId(String tenantId, String positionId, String title,
        String itemId, String startdate, String enddate, Integer page, Integer rows) {
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
     * @return Y9Page<OfficeDoneInfoModel>
     */
    @Override
    @GetMapping(value = "/searchByPositionIdAndSystemName", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Page<OfficeDoneInfoModel> searchByPositionIdAndSystemName(String tenantId, String positionId, String title,
        String systemName, String startdate, String enddate, Integer page, Integer rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return officeDoneInfoService.searchByPositionIdAndSystemName(positionId, title, systemName, startdate, enddate,
            page, rows);
    }

    /**
     * 设置会议类型
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param meetingType 会议类型
     * @return Y9Result<Object>
     */
    @Override
    @PostMapping(value = "/setMeeting", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Object> setMeeting(String tenantId, String processInstanceId, String meetingType) {
        Y9LoginUserHolder.setTenantId(tenantId);
        officeDoneInfoService.setMeeting(processInstanceId, meetingType);
        return Y9Result.successMsg("设置成功");
    }

}
