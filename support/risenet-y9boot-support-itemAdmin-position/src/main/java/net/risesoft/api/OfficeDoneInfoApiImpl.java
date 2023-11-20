package net.risesoft.api;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.position.OfficeDoneInfo4PositionApi;
import net.risesoft.model.itemadmin.OfficeDoneInfoModel;
import net.risesoft.nosql.elastic.entity.OfficeDoneInfo;
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
@RequestMapping(value = "/services/rest/officeDoneInfo4Position")
public class OfficeDoneInfoApiImpl implements OfficeDoneInfo4PositionApi {

    @Autowired
    private OfficeDoneInfoService officeDoneInfoService;

    /**
     * 监控办结统计
     *
     * @param tenantId 租户id
     * @param itemId 事项id
     * @return int
     */
    @Override
    @GetMapping(value = "/countByItemId", produces = MediaType.APPLICATION_JSON_VALUE)
    public int countByItemId(String tenantId, String itemId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return officeDoneInfoService.countByItemId(itemId);
    }

    /**
     * 统计个人办结件
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param itemId 事项id
     * @return int
     */
    @Override
    @GetMapping(value = "/countByPositionId", produces = MediaType.APPLICATION_JSON_VALUE)
    public int countByPositionId(String tenantId, String positionId, String itemId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return officeDoneInfoService.countByUserId(positionId, itemId);
    }

    /**
     * 根据系统名称统计个人办结件
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param systemName 系统名称
     * @return int
     */
    @Override
    @GetMapping(value = "/countByPositionIdAndSystemName", produces = MediaType.APPLICATION_JSON_VALUE)
    public int countByPositionIdAndSystemName(String tenantId, String positionId, String systemName) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return officeDoneInfoService.countByPositionIdAndSystemName(positionId, systemName);
    }

    /**
     * @param tenantId 租户id
     * @param itemId 事项id
     * @return long
     */
    @Override
    @GetMapping(value = "/countDoingByItemId", produces = MediaType.APPLICATION_JSON_VALUE)
    public long countDoingByItemId(String tenantId, String itemId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return officeDoneInfoService.countDoingByItemId(itemId);
    }

    /**
     * 根据流程实例id删除办结信息
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return boolean
     */
    @Override
    @PostMapping(value = "/deleteOfficeDoneInfo", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean deleteOfficeDoneInfo(String tenantId, String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return officeDoneInfoService.deleteOfficeDoneInfo(processInstanceId);
    }

    /**
     * 根据流程实例id获取办结信息
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return OfficeDoneInfoModel
     */
    @Override
    @GetMapping(value = "/findByProcessInstanceId", produces = MediaType.APPLICATION_JSON_VALUE)
    public OfficeDoneInfoModel findByProcessInstanceId(String tenantId, String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        OfficeDoneInfo officeDoneInfo = officeDoneInfoService.findByProcessInstanceId(processInstanceId);
        OfficeDoneInfoModel officeDoneInfoModel = null;
        if (officeDoneInfo != null) {
            officeDoneInfoModel = new OfficeDoneInfoModel();
            Y9BeanUtil.copyProperties(officeDoneInfo, officeDoneInfoModel);
        }
        return officeDoneInfoModel;
    }

    /**
     * 保存办结信息,不经过kafka消息队列，直接保存
     *
     * @param tenantId 租户id
     * @param info 办结信息
     * @throws Exception
     */
    @Override
    @PostMapping(value = "/saveOfficeDone", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void saveOfficeDone(String tenantId, @RequestBody OfficeDoneInfoModel info) throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        OfficeDoneInfo officeInfo = new OfficeDoneInfo();
        Y9BeanUtil.copyProperties(info, officeInfo);
        officeDoneInfoService.saveOfficeDone(officeInfo);
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
     * @param rows条数
     * @return Map<String, Object>
     */
    @Override
    @GetMapping(value = "/searchAllByDeptId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> searchAllByDeptId(String tenantId, String deptId, String title, String itemId, String userName, String state, String year, Integer page, Integer rows) {
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
     * @param page 页码
     * @param rows条数
     * @return Map<String, Object>
     */
    @Override
    @GetMapping(value = "/searchAllByPositionId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> searchAllByPositionId(String tenantId, String positionId, String title, String itemId, String userName, String state, String year, Integer page, Integer rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return officeDoneInfoService.searchAllByUserId(positionId, title, itemId, userName, state, year, page, rows);
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
     * @param rows条数
     * @return Map<String, Object>
     */
    @Override
    @GetMapping(value = "/searchAllList", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> searchAllList(String tenantId, String searchName, String itemId, String userName, String state, String year, Integer page, Integer rows) {
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
     * @param rows条数
     * @return Map<String, Object
     */
    @Override
    @GetMapping(value = "/searchByItemId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> searchByItemId(String tenantId, String title, String itemId, String state, String startdate, String enddate, Integer page, Integer rows) {
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
     * @param rows条数
     * @return
     */
    @Override
    @GetMapping(value = "/searchByPositionId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> searchByPositionId(String tenantId, String positionId, String title, String itemId, String startdate, String enddate, Integer page, Integer rows) {
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
     * @param rows条数
     * @return
     */
    @Override
    @GetMapping(value = "/searchByPositionIdAndSystemName", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> searchByPositionIdAndSystemName(String tenantId, String positionId, String title, String systemName, String startdate, String enddate, Integer page, Integer rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return officeDoneInfoService.searchByPositionIdAndSystemName(positionId, title, systemName, startdate, enddate, page, rows);
    }

}
