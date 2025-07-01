package net.risesoft.controller;

import java.util.Map;

import javax.validation.constraints.NotBlank;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.ReminderApi;
import net.risesoft.model.itemadmin.ReminderModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.FlowableReminderService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 催办
 *
 * @author zhangchongjie
 * @date 2024/06/05
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/vue/reminder", produces = MediaType.APPLICATION_JSON_VALUE)
public class ReminderRestController {

    private final ReminderApi reminderApi;

    private final FlowableReminderService flowableReminderService;

    /**
     * 删除催办信息
     *
     * @param ids 催办id
     * @return Y9Result<String>
     */
    @PostMapping(value = "/deleteList")
    public Y9Result<String> deleteList(@RequestParam String[] ids) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        reminderApi.deleteList(tenantId, ids);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 获取我的任务催办信息
     *
     * @param taskId 任务id
     * @param rows 条数
     * @param page 页码
     * @return Y9Page<ReminderModel>
     */
    @GetMapping(value = "/reminderMeList")
    public Y9Page<ReminderModel> getReminderList(@RequestParam @NotBlank String taskId, @RequestParam int rows,
        @RequestParam int page) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        return reminderApi.findByTaskId(tenantId, taskId, page, rows);
    }

    /**
     * 根据类型和流程实例id获取催办列表信息
     *
     * @param type 类型
     * @param processInstanceId 流程实例id
     * @param rows 条数
     * @param page 页码
     * @return Y9Page<ReminderModel>
     */
    @GetMapping(value = "/reminderList")
    public Y9Page<ReminderModel> myReminder(@RequestParam @NotBlank String type,
        @RequestParam @NotBlank String processInstanceId, @RequestParam int rows, @RequestParam int page) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String userId = Y9LoginUserHolder.getPositionId();
        Y9Page<ReminderModel> map;
        if ("my".equals(type)) {
            map = reminderApi.findBySenderIdAndProcessInstanceIdAndActive(tenantId, userId, processInstanceId, page,
                rows);
        } else {
            map = reminderApi.findByProcessInstanceId(tenantId, processInstanceId, page, rows);
        }
        return map;
    }

    /**
     * 保存催办信息
     *
     * @param processInstanceId 流程实例id
     * @param taskIds 催办任务ids
     * @param msgContent 催办信息
     * @return Y9Result<String>
     */
    @PostMapping(value = "/saveReminder")
    public Y9Result<String> saveReminder(@RequestParam @NotBlank String processInstanceId,
        @RequestParam String[] taskIds, @RequestParam @NotBlank String msgContent) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String userId = Y9LoginUserHolder.getPositionId();
        return reminderApi.saveReminder(tenantId, userId, processInstanceId, taskIds, msgContent);
    }

    /**
     * 批量设置催办阅读时间
     *
     * @param ids 催办id
     * @return Y9Result<String>
     */
    @PostMapping(value = "/setReadTime")
    public Y9Result<String> setReadTime(@RequestParam String[] ids) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        reminderApi.setReadTime(tenantId, ids);
        return Y9Result.successMsg("操作成功");
    }

    /**
     * 获取催办任务列表
     *
     * @param processInstanceId 流程实例id
     * @param page 页码
     * @param rows 条数
     * @return Y9Page<Map < String, Object>>
     */
    @GetMapping(value = "/taskList")
    public Y9Page<Map<String, Object>> taskList(@RequestParam @NotBlank String processInstanceId,
        @RequestParam int page, @RequestParam int rows) {
        return flowableReminderService.pageTaskListByProcessInstanceId(processInstanceId, page, rows);
    }

    /**
     * 更新催办信息
     *
     * @param id 催办is
     * @param msgContent 催办信息
     * @return Y9Result<String>
     */
    @PostMapping(value = "/updateReminder")
    public Y9Result<String> updateReminder(@RequestParam @NotBlank String id,
        @RequestParam @NotBlank String msgContent) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        return reminderApi.updateReminder(tenantId, id, msgContent);
    }
}
