package y9.client.rest.itemadmin.position;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.itemadmin.position.OfficeDoneInfo4PositionApi;
import net.risesoft.model.itemadmin.OfficeDoneInfoModel;

/**
 * 工作流办件信息列表接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "OfficeDoneInfo4PositionApiClient", name = "itemAdmin", url = "${y9.common.itemAdminBaseUrl}", path = "/services/rest/officeDoneInfo4Position")
public interface OfficeDoneInfo4PositionApiClient extends OfficeDoneInfo4PositionApi {

    /**
     * 取消上会，当代研究所
     *
     * @param tenantId
     * @param processInstanceId
     */
    @Override
    @PostMapping("/cancelMeeting")
    void cancelMeeting(@RequestParam("tenantId") String tenantId, @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 监控办结统计
     *
     * @param tenantId
     * @param itemId
     * @return
     */
    @Override
    @GetMapping("/countByItemId")
    int countByItemId(@RequestParam("tenantId") String tenantId, @RequestParam("itemId") String itemId);

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
    int countByPositionId(@RequestParam("tenantId") String tenantId, @RequestParam("positionId") String positionId, @RequestParam("itemId") String itemId);

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
    int countByPositionIdAndSystemName(@RequestParam("tenantId") String tenantId, @RequestParam("positionId") String positionId, @RequestParam("systemName") String systemName);

    /**
     * 监控在办统计
     *
     * @param tenantId
     * @param itemId
     * @return
     */
    @Override
    @GetMapping("/countDoingByItemId")
    long countDoingByItemId(@RequestParam("tenantId") String tenantId, @RequestParam("itemId") String itemId);

    /**
     * 根据流程实例id删除办结信息
     *
     * @param tenantId
     * @param processInstanceId
     * @return
     */
    @Override
    @PostMapping("/deleteOfficeDoneInfo")
    boolean deleteOfficeDoneInfo(@RequestParam("tenantId") String tenantId, @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 根据流程实例id获取办结信息
     *
     * @param tenantId
     * @param processInstanceId
     * @return
     */
    @Override
    @GetMapping("/findByProcessInstanceId")
    OfficeDoneInfoModel findByProcessInstanceId(@RequestParam("tenantId") String tenantId, @RequestParam("processInstanceId") String processInstanceId);

    /**
     *
     */
    @Override
    @GetMapping("/getMeetingList")
    Map<String, Object> getMeetingList(@RequestParam("tenantId") String tenantId, @RequestParam("userName") String userName, @RequestParam("deptName") String deptName, @RequestParam("title") String title, @RequestParam("meetingType") String meetingType, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows);

    /**
     * 保存办结信息,不经过kafka消息队列，直接保存
     *
     * @param tenantId
     * @param info
     * @throws Exception
     */
    @Override
    @PostMapping(value = "/saveOfficeDone", consumes = MediaType.APPLICATION_JSON_VALUE)
    void saveOfficeDone(@RequestParam("tenantId") String tenantId, @RequestBody OfficeDoneInfoModel info) throws Exception;

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
    Map<String, Object> searchAllByDeptId(@RequestParam("tenantId") String tenantId, @RequestParam("deptId") String deptId, @RequestParam("title") String title, @RequestParam("itemId") String itemId, @RequestParam("userName") String userName, @RequestParam("state") String state,
        @RequestParam("year") String year, @RequestParam("page") Integer page, @RequestParam("rows") Integer rows);

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
    @Override
    @GetMapping("/searchAllByPositionId")
    Map<String, Object> searchAllByPositionId(@RequestParam("tenantId") String tenantId, @RequestParam("positionId") String positionId, @RequestParam("title") String title, @RequestParam("itemId") String itemId, @RequestParam("userName") String userName, @RequestParam("state") String state,
        @RequestParam("year") String year, @RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate, @RequestParam("page") Integer page, @RequestParam("rows") Integer rows);

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
    Map<String, Object> searchAllList(@RequestParam("tenantId") String tenantId, @RequestParam("searchName") String searchName, @RequestParam("itemId") String itemId, @RequestParam("userName") String userName, @RequestParam("state") String state, @RequestParam("year") String year,
        @RequestParam("page") Integer page, @RequestParam("rows") Integer rows);

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
    Map<String, Object> searchByItemId(@RequestParam("tenantId") String tenantId, @RequestParam("title") String title, @RequestParam("itemId") String itemId, @RequestParam("state") String state, @RequestParam("startdate") String startdate, @RequestParam("enddate") String enddate,
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
    Map<String, Object> searchByPositionId(@RequestParam("tenantId") String tenantId, @RequestParam("positionId") String positionId, @RequestParam("title") String title, @RequestParam("itemId") String itemId, @RequestParam("startdate") String startdate, @RequestParam("enddate") String enddate,
        @RequestParam("page") Integer page, @RequestParam("rows") Integer rows);

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
    Map<String, Object> searchByPositionIdAndSystemName(@RequestParam("tenantId") String tenantId, @RequestParam("positionId") String positionId, @RequestParam("title") String title, @RequestParam("systemName") String systemName, @RequestParam("startdate") String startdate,
        @RequestParam("enddate") String enddate, @RequestParam("page") Integer page, @RequestParam("rows") Integer rows);

    /**
     * 上会，当代研究所
     *
     * @param tenantId
     * @param processInstanceId
     * @param meetingType
     */
    @Override
    @PostMapping("/setMeeting")
    void setMeeting(@RequestParam("tenantId") String tenantId, @RequestParam("processInstanceId") String processInstanceId, @RequestParam("meetingType") String meetingType);

}