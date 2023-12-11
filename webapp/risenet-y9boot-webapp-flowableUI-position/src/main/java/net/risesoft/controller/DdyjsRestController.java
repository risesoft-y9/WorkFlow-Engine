package net.risesoft.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.position.OfficeDoneInfo4PositionApi;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.WorkList4ddyjsService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 当代中国研究所使用
 *
 * @author zhangchongjie
 * @date 2023/11/20
 */
@RestController
@RequestMapping(value = "/vue/ddyjs")
public class DdyjsRestController {

    @Autowired
    private OfficeDoneInfo4PositionApi officeDoneInfoApi;

    @Autowired
    private WorkList4ddyjsService workList4ddyjsService;

    /**
     * 取消上会
     *
     * @param processInstanceId 流程实例id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/cancelMeeting", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> cancelMeeting(@RequestParam(required = true) String processInstanceId) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            officeDoneInfoApi.cancelMeeting(tenantId, processInstanceId);
            return Y9Result.successMsg("取消上会成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("取消上会失败");
    }

    /**
     * 获取上会台账
     *
     * @param meetingType
     * @param userName
     * @param deptName
     * @param title
     * @param page
     * @param rows
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getMeetingList", method = RequestMethod.GET, produces = "application/json")
    public Y9Page<Map<String, Object>> getMeetingList(@RequestParam(required = false) String meetingType, @RequestParam(required = false) String userName, @RequestParam(required = false) String deptName, @RequestParam(required = false) String title, @RequestParam(required = true) Integer page,
        @RequestParam(required = true) Integer rows) {
        return workList4ddyjsService.getMeetingList(userName, deptName, title, meetingType, page, rows);
    }

    /**
     * 上会
     *
     * @param processInstanceId 流程实例id
     * @param meetingType 会议类型
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/setMeeting", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> setMeeting(@RequestParam(required = true) String processInstanceId, @RequestParam(required = true) String meetingType) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            officeDoneInfoApi.setMeeting(tenantId, processInstanceId, meetingType);
            return Y9Result.successMsg("上会成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("上会失败");
    }
}