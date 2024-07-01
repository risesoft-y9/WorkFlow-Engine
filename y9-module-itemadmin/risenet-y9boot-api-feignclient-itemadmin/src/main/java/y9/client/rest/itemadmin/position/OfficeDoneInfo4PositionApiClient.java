package y9.client.rest.itemadmin.position;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.itemadmin.position.OfficeDoneInfo4PositionApi;
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
@FeignClient(contextId = "OfficeDoneInfo4PositionApiClient", name = "${y9.service.itemAdmin.name:itemAdmin}",
    url = "${y9.service.itemAdmin.directUrl:}",
    path = "/${y9.service.itemAdmin.name:itemAdmin}/services/rest/officeDoneInfo4Position")
public interface OfficeDoneInfo4PositionApiClient extends OfficeDoneInfo4PositionApi {

    /**
     * 取消上会，当代研究所
     *
     * @param tenantId
     * @param processInstanceId
     * @return
     */
    @Override
    @PostMapping("/cancelMeeting")
    Y9Result<Object> cancelMeeting(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 监控办结统计
     *
     * @param tenantId
     * @param itemId
     * @return
     */
    @Override
    @GetMapping("/countByItemId")
    Y9Result<Integer> countByItemId(@RequestParam("tenantId") String tenantId, @RequestParam("itemId") String itemId);

    /**
     * 统计个人办结件
     *
     * @param tenantId
     * @param positionId
     * @param itemId
     * @return
     */
    @Override
    @GetMapping("/countByPositionId")
    Y9Result<Integer> countByPositionId(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("itemId") String itemId);

    /**
     * 根据系统名称统计个人办结件
     *
     * @param tenantId
     * @param positionId
     * @param systemName
     * @return
     */
    @Override
    @GetMapping("/countByPositionIdAndSystemName")
    Y9Result<Integer> countByPositionIdAndSystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("systemName") String systemName);

    /**
     * 监控在办统计
     *
     * @param tenantId
     * @param itemId
     * @return
     */
    @Override
    @GetMapping("/countDoingByItemId")
    Y9Result<Long> countDoingByItemId(@RequestParam("tenantId") String tenantId, @RequestParam("itemId") String itemId);

    /**
     * 根据流程实例id删除办结信息
     *
     * @param tenantId
     * @param processInstanceId
     * @return
     */
    @Override
    @PostMapping("/deleteOfficeDoneInfo")
    Y9Result<Object> deleteOfficeDoneInfo(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 根据流程实例id获取办结信息
     *
     * @param tenantId
     * @param processInstanceId
     * @return
     */
    @Override
    @GetMapping("/findByProcessInstanceId")
    Y9Result<OfficeDoneInfoModel> findByProcessInstanceId(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId);

    /**
     *
     */
    @Override
    @GetMapping("/getMeetingList")
    Y9Page<OfficeDoneInfoModel> getMeetingList(@RequestParam("tenantId") String tenantId,
        @RequestParam("userName") String userName, @RequestParam("deptName") String deptName,
        @RequestParam("title") String title, @RequestParam("meetingType") String meetingType,
        @RequestParam("page") Integer page, @RequestParam("rows") Integer rows);

    /**
     * 保存办结信息,不经过kafka消息队列，直接保存
     *
     * @param tenantId
     * @param info
     * @return
     * @throws Exception
     */
    @Override
    @PostMapping(value = "/saveOfficeDone", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Object> saveOfficeDone(@RequestParam("tenantId") String tenantId, @RequestBody OfficeDoneInfoModel info)
        throws Exception;

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
    @Override
    @GetMapping("/searchAllByDeptId")
    Y9Page<OfficeDoneInfoModel> searchAllByDeptId(@RequestParam("tenantId") String tenantId,
        @RequestParam("deptId") String deptId, @RequestParam("title") String title,
        @RequestParam("itemId") String itemId, @RequestParam("userName") String userName,
        @RequestParam("state") String state, @RequestParam("year") String year, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows);

    /**
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
    @Override
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
    @Override
    @GetMapping("/searchAllList")
    Y9Page<OfficeDoneInfoModel> searchAllList(@RequestParam("tenantId") String tenantId,
        @RequestParam("searchName") String searchName, @RequestParam("itemId") String itemId,
        @RequestParam("userName") String userName, @RequestParam("state") String state,
        @RequestParam("year") String year, @RequestParam("page") Integer page, @RequestParam("rows") Integer rows);

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
    @Override
    @GetMapping("/searchByItemId")
    Y9Page<OfficeDoneInfoModel> searchByItemId(@RequestParam("tenantId") String tenantId,
        @RequestParam("title") String title, @RequestParam("itemId") String itemId, @RequestParam("state") String state,
        @RequestParam("startdate") String startdate, @RequestParam("enddate") String enddate,
        @RequestParam("page") Integer page, @RequestParam("rows") Integer rows);

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
    @Override
    @GetMapping("/searchByPositionId")
    Y9Page<OfficeDoneInfoModel> searchByPositionId(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("title") String title,
        @RequestParam("itemId") String itemId, @RequestParam("startdate") String startdate,
        @RequestParam("enddate") String enddate, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows);

    /**
     * 根据岗位id,系统名称，获取个人办结件列表
     *
     * @param tenantId
     * @param positionId
     * @param title
     * @param systemName
     * @param startdate
     * @param enddate
     * @param page
     * @param rows
     * @return
     */
    @Override
    @GetMapping("/searchByPositionIdAndSystemName")
    Y9Page<OfficeDoneInfoModel> searchByPositionIdAndSystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("title") String title,
        @RequestParam("systemName") String systemName, @RequestParam("startdate") String startdate,
        @RequestParam("enddate") String enddate, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows);

    /**
     * 上会，当代研究所
     *
     * @param tenantId
     * @param processInstanceId
     * @param meetingType
     * @return
     */
    @Override
    @PostMapping("/setMeeting")
    Y9Result<Object> setMeeting(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId, @RequestParam("meetingType") String meetingType);

}