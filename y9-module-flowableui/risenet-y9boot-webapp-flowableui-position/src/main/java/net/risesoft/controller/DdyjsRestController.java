package net.risesoft.controller;

import java.util.Map;

import javax.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.position.OfficeDoneInfo4PositionApi;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.WorkList4ddyjsService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 *
 *
 * @author zhangchongjie
 * @date 2023/11/20
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/vue/ddyjs")
public class DdyjsRestController {

    private final OfficeDoneInfo4PositionApi officeDoneInfo4PositionApi;

    private final WorkList4ddyjsService workList4ddyjsService;

    /**
     * 取消上会
     *
     * @param processInstanceId 流程实例id
     * @return
     */
    @RequestMapping(value = "/cancelMeeting", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> cancelMeeting(@RequestParam @NotBlank String processInstanceId) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            officeDoneInfo4PositionApi.cancelMeeting(tenantId, processInstanceId);
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
    @RequestMapping(value = "/getMeetingList", method = RequestMethod.GET, produces = "application/json")
    public Y9Page<Map<String, Object>> getMeetingList(@RequestParam String meetingType, @RequestParam String userName, @RequestParam String deptName, @RequestParam String title, @RequestParam @NotBlank Integer page, @RequestParam @NotBlank Integer rows) {
        return workList4ddyjsService.getMeetingList(userName, deptName, title, meetingType, page, rows);
    }

    /**
     * 上会
     *
     * @param processInstanceId 流程实例id
     * @param meetingType 会议类型
     * @return
     */
    @RequestMapping(value = "/setMeeting", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> setMeeting(@RequestParam @NotBlank String processInstanceId, @RequestParam @NotBlank String meetingType) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            officeDoneInfo4PositionApi.setMeeting(tenantId, processInstanceId, meetingType);
            return Y9Result.successMsg("上会成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("上会失败");
    }
}