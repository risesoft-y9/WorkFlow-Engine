package net.risesoft.controller;

import java.util.Map;

import javax.validation.constraints.NotBlank;


import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.ReminderApi;
import net.risesoft.log.FlowableOperationTypeEnum;
import net.risesoft.log.annotation.FlowableLog;
import net.risesoft.model.itemadmin.ReminderModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.FlowableReminderService;

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
    @FlowableLog(operationName = "删除催办信息", operationType = FlowableOperationTypeEnum.DELETE)
    @PostMapping(value = "/deleteList")
    public Y9Result<String> deleteList(@RequestParam String[] ids) {
        reminderApi.deleteList(ids);
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
        return reminderApi.findByTaskId(taskId, page, rows);
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
        Y9Page<ReminderModel> map;
        if ("my".equals(type)) {
            map = reminderApi.findBySenderIdAndProcessInstanceIdAndActive(processInstanceId, page, rows);
        } else {
            map = reminderApi.findByProcessInstanceId(processInstanceId, page, rows);
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
    @FlowableLog(operationName = "保存催办信息", operationType = FlowableOperationTypeEnum.SAVE)
    @PostMapping(value = "/saveReminder")
    public Y9Result<String> saveReminder(@RequestParam @NotBlank String processInstanceId,
        @RequestParam String[] taskIds, @RequestParam @NotBlank String msgContent) {
        return reminderApi.saveReminder(processInstanceId, taskIds, msgContent);
    }

    /**
     * 批量设置催办阅读时间
     *
     * @param ids 催办id
     * @return Y9Result<String>
     */
    @FlowableLog(operationName = "批量设置催办阅读时间", operationType = FlowableOperationTypeEnum.SAVE)
    @PostMapping(value = "/setReadTime")
    public Y9Result<Object> setReadTime(@RequestParam String[] ids) {
        return reminderApi.setReadTime(ids);
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
    @FlowableLog(operationName = "更新催办信息", operationType = FlowableOperationTypeEnum.SAVE)
    @PostMapping(value = "/updateReminder")
    public Y9Result<String> updateReminder(@RequestParam @NotBlank String id,
        @RequestParam @NotBlank String msgContent) {
        return reminderApi.updateReminder(id, msgContent);
    }
}
