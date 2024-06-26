package net.risesoft.controller;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.ReminderApi;
import net.risesoft.model.itemadmin.ReminderModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.FlowableReminderService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 催办
 * 
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@RestController
@RequestMapping(value = "/vue/reminder", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ReminderRestController {

    private final ReminderApi reminderManager;

    private final FlowableReminderService flowableReminderService;

    /**
     * 删除催办信息
     *
     * @param ids 催办id
     * @return
     */
    @PostMapping(value = "/deleteList")
    public Y9Result<String> deleteList(@RequestParam(required = true) String[] ids) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        reminderManager.deleteList(tenantId, ids);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 获取我的任务催办信息
     *
     * @param taskId 任务id
     * @param rows 条数
     * @param page 页码
     * @return
     */
    @GetMapping(value = "/reminderMeList")
    public Y9Page<ReminderModel> getReminderList(@RequestParam(required = true) String taskId,
        @RequestParam(required = true) int rows, @RequestParam(required = true) int page) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        return reminderManager.findByTaskId(tenantId, taskId, page, rows);
    }

    /**
     * 获取催办列表信息
     *
     * @param type 类型
     * @param processInstanceId 流程实例id
     * @param rows 条数
     * @param page 页码
     * @return
     */
    @GetMapping(value = "/reminderList")
    public Y9Page<ReminderModel> myReminder(@RequestParam(required = true) String type,
        @RequestParam(required = true) String processInstanceId, @RequestParam(required = true) int rows,
        @RequestParam(required = true) int page) {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId();
        String userId = userInfo.getPersonId();
        Y9Page<ReminderModel> y9page;
        boolean b = "my".equals(type);
        if (b) {
            y9page = reminderManager.findBySenderIdAndProcessInstanceIdAndActive(tenantId, userId, processInstanceId,
                page, rows);
        } else {
            y9page = reminderManager.findByProcessInstanceId(tenantId, processInstanceId, page, rows);
        }
        return y9page;
    }

    /**
     * 保存催办信息
     *
     * @param processInstanceId 流程实例id
     * @param taskIds 催办任务ids
     * @param msgContent 催办信息
     * @return
     */
    @PostMapping(value = "/saveReminder")
    public Y9Result<String> saveReminder(@RequestParam(required = true) String processInstanceId,
        @RequestParam(required = true) String[] taskIds, @RequestParam(required = true) String msgContent) {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId();
        String userId = userInfo.getPersonId();
        return reminderManager.saveReminder(tenantId, userId, processInstanceId, taskIds, msgContent);
    }

    /**
     * 设置阅读时间
     *
     * @param ids 催办id
     * @return
     */
    @PostMapping(value = "/setReadTime")
    public Y9Result<String> setReadTime(@RequestParam(required = true) String[] ids) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        reminderManager.setReadTime(tenantId, ids);
        return Y9Result.successMsg("操作成功");
    }

    /**
     * 获取催办任务
     *
     * @param processInstanceId 流程实例id
     * @param page 页码
     * @param rows 条数
     * @return
     */
    @GetMapping(value = "/taskList")
    public Y9Page<Map<String, Object>> taskList(@RequestParam(required = true) String processInstanceId,
        @RequestParam(required = true) int page, @RequestParam(required = true) int rows) {
        return flowableReminderService.findTaskListByProcessInstanceId(processInstanceId, page, rows);
    }

    /**
     * 更新催办信息
     *
     * @param id 催办is
     * @param msgContent 催办信息
     * @return
     */
    @PostMapping(value = "/updateReminder")
    public Y9Result<String> updateReminder(@RequestParam(required = true) String id,
        @RequestParam(required = true) String msgContent) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        return reminderManager.updateReminder(tenantId, id, msgContent);
    }
}
