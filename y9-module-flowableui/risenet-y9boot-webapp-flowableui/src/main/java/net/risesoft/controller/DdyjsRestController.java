package net.risesoft.controller;

import java.util.Map;

import javax.validation.constraints.NotBlank;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.OfficeDoneInfoApi;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.WorkList4ddyjsService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author zhangchongjie
 * @date 2023/11/20
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/vue/ddyjs", produces = MediaType.APPLICATION_JSON_VALUE)
public class DdyjsRestController {

    private final OfficeDoneInfoApi officeDoneInfoApi;

    private final WorkList4ddyjsService workList4ddyjsService;

    /**
     * 取消上会
     *
     * @param processInstanceId 流程实例id
     * @return Y9Result<String>
     */
    @PostMapping(value = "/cancelMeeting")
    public Y9Result<String> cancelMeeting(@RequestParam @NotBlank String processInstanceId) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            officeDoneInfoApi.cancelMeeting(tenantId, processInstanceId);
            return Y9Result.successMsg("取消上会成功");
        } catch (Exception e) {
            LOGGER.error("取消上会失败", e);
        }
        return Y9Result.failure("取消上会失败");
    }

    /**
     * 获取上会台账
     *
     * @param meetingType 会议类型
     * @param userName 人员名称
     * @param deptName 部门名称
     * @param title 标题
     * @param page 页码
     * @param rows 每页条数
     * @return Y9Page<Map < String, Object>>
     */
    @GetMapping(value = "/getMeetingList")
    public Y9Page<Map<String, Object>> getMeetingList(@RequestParam(required = false) String meetingType,
        @RequestParam(required = false) String userName, @RequestParam(required = false) String deptName,
        @RequestParam(required = false) String title, @RequestParam Integer page, @RequestParam Integer rows) {
        return workList4ddyjsService.pageMeetingList(userName, deptName, title, meetingType, page, rows);
    }

    /**
     * 上会
     *
     * @param processInstanceId 流程实例id
     * @param meetingType 会议类型
     * @return Y9Result<String>
     */
    @PostMapping(value = "/setMeeting")
    public Y9Result<String> setMeeting(@RequestParam @NotBlank String processInstanceId,
        @RequestParam @NotBlank String meetingType) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            officeDoneInfoApi.setMeeting(tenantId, processInstanceId, meetingType);
            return Y9Result.successMsg("上会成功");
        } catch (Exception e) {
            LOGGER.error("上会失败", e);
        }
        return Y9Result.failure("上会失败");
    }
}