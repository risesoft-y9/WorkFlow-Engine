package net.risesoft.api;

import java.net.URLDecoder;

import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.ReminderApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.entity.Reminder;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.model.itemadmin.ReminderModel;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.ReminderService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.exception.Y9BusinessException;

/**
 * 催办提醒接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/reminder", produces = MediaType.APPLICATION_JSON_VALUE)
public class ReminderApiImpl implements ReminderApi {

    private static final FastDateFormat DATE_TIME_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

    private final ReminderService reminderService;

    private final OrgUnitApi orgUnitApi;

    private final TaskApi taskManager;

    /**
     * 删除催办
     *
     * @param tenantId 租户id
     * @param ids 催办ids
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> deleteList(@RequestParam String tenantId, @RequestBody String[] ids) {
        Y9LoginUserHolder.setTenantId(tenantId);
        reminderService.deleteList(ids);
        return Y9Result.success();
    }

    /**
     * 根据id获取催办
     *
     * @param tenantId 租户id
     * @param id 催办id
     * @return {@code Y9Result<ReminderModel>} 通用请求返回对象 - rows 是待办的催办信息
     * @since 9.6.6
     */
    @Override
    public Y9Result<ReminderModel> findById(@RequestParam String tenantId, @RequestParam String id) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Reminder reminder = reminderService.findById(id);
        if (reminder != null && reminder.getId() != null) {
            ReminderModel model = new ReminderModel();
            model.setId(reminder.getId());
            model.setReminderMakeTyle(reminder.getReminderMakeTyle());
            model.setReminderSendType(reminder.getReminderSendType());
            model.setSenderId(reminder.getSenderId());
            model.setSenderName(reminder.getSenderName());
            model.setTaskId(reminder.getTaskId());
            model.setProcInstId(reminder.getProcInstId());
            model.setMsgContent(reminder.getMsgContent());
            return Y9Result.success(model);
        }
        return Y9Result.failure("查询失败，没有对应的催办信息");
    }

    /**
     * 获取流程实例的催办信息
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param page 页码
     * @param rows 条数
     * @return {@code Y9Page<ReminderModel>} 通用分页请求返回对象 - rows 是催办信息
     * @since 9.6.6
     */
    @Override
    public Y9Page<ReminderModel> findByProcessInstanceId(@RequestParam String tenantId,
        @RequestParam String processInstanceId, @RequestParam int page, @RequestParam int rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return reminderService.pageByProcessInstanceId(processInstanceId, page, rows);
    }

    /**
     * 获取当前催办人的在办任务的催办信息
     *
     * @param tenantId 租户id
     * @param senderId 人员di
     * @param processInstanceId 流程实例id
     * @param page 页码
     * @param rows 条数
     * @return {@code Y9Page<ReminderModel>} 通用分页请求返回对象 - rows 是催办信息
     * @since 9.6.6
     */
    @Override
    public Y9Page<ReminderModel> findBySenderIdAndProcessInstanceIdAndActive(@RequestParam String tenantId,
        @RequestParam String senderId, @RequestParam String processInstanceId, @RequestParam int page,
        @RequestParam int rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return reminderService.pageBySenderIdAndProcessInstanceIdAndActive(senderId, processInstanceId, page, rows);
    }

    /**
     * 获取待办的提醒数据
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param page 页码
     * @param rows 条数
     * @return {@code Y9Page<ReminderModel>} 通用分页请求返回对象 - rows 是待办的催办信息
     * @since 9.6.6
     */
    @Override
    public Y9Page<ReminderModel> findByTaskId(@RequestParam String tenantId, @RequestParam String taskId,
        @RequestParam int page, @RequestParam int rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return reminderService.pageByTaskId(taskId, page, rows);
    }

    /**
     * 查看催办信息
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param taskId 任务id
     * @param type 类型，todo（待办），doing（在办），done（办结）
     * @return {@code Y9Result<ReminderModel>} 通用请求返回对象 -data 是催办信息
     * @since 9.6.6
     */
    @Override
    public Y9Result<ReminderModel> getReminder(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam String taskId, @RequestParam String type) {
        Y9LoginUserHolder.setTenantId(tenantId);
        taskId = taskId.contains(SysVariables.COMMA) ? taskId.split(SysVariables.COMMA)[0] : taskId;
        Reminder reminder = new Reminder();
        if (ItemBoxTypeEnum.DOING.getValue().equals(type)) {
            reminder = reminderService.findByTaskIdAndSenderId(taskId, userId);
        }
        if (ItemBoxTypeEnum.TODO.getValue().equals(type)) {
            reminder = reminderService.findByTaskId(taskId);
        }
        if (reminder != null && reminder.getId() != null) {
            ReminderModel model = new ReminderModel();
            model.setId(reminder.getId());
            model.setCreateTime(DATE_TIME_FORMAT.format(reminder.getCreateTime()));
            model.setModifyTime(
                reminder.getModifyTime() != null ? DATE_TIME_FORMAT.format(reminder.getModifyTime()) : "");
            model.setReminderMakeTyle(reminder.getReminderMakeTyle());
            model.setReminderSendType(reminder.getReminderSendType());
            model.setSenderId(reminder.getSenderId());
            model.setSenderName(reminder.getSenderName());
            model.setTenantId(reminder.getTenantId());
            model.setTaskId(reminder.getTaskId());
            model.setProcInstId(reminder.getProcInstId());
            model.setMsgContent(reminder.getMsgContent());
            return Y9Result.success(model);
        }
        return Y9Result.failure("查询失败，没有对应的催办信息");
    }

    /**
     * 保存催办信息
     *
     * @param tenantId 租户id
     * @param userId 人员、岗位id
     * @param processInstanceId 流程实例id
     * @param taskIds taskIds
     * @param msgContent 催办信息
     * @return {@code Y9Result<String>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<String> saveReminder(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam String processInstanceId, @RequestBody String[] taskIds, @RequestParam String msgContent) {
        Y9LoginUserHolder.setTenantId(tenantId);
        OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, userId).getData();
        Y9LoginUserHolder.setOrgUnit(orgUnit);
        try {
            Reminder reminder;
            for (String taskId : taskIds) {
                reminder = new Reminder();
                reminder.setMsgContent(URLDecoder.decode(msgContent, "utf-8"));
                reminder.setProcInstId(processInstanceId);
                reminder.setTaskId(taskId);
                reminderService.saveOrUpdate(reminder);

                TaskModel task = taskManager.findById(tenantId, taskId).getData();
                if (!String.valueOf(task.getPriority()).contains("8")) {
                    taskManager.setPriority(tenantId, taskId, task.getPriority() + 8);
                }
            }
            return Y9Result.successMsg("保存成功!");
        } catch (Exception e) {
            LOGGER.error("saveReminder error", e);
            throw new Y9BusinessException(500, "保存催办信息失败,错误信息为：" + e.getMessage());
        }
    }

    /**
     * 发送催办信息
     *
     * @param tenantId 租户id
     * @param userId 人员、岗位id
     * @param remType 催办类型，"1":短信,"2":邮件",3":站内信",4":待办列表中
     * @param procInstId procInstId
     * @param processInstanceId 流程实例id
     * @param documentTitle 文档标题
     * @param taskId 任务id
     * @param taskAssigneeId 任务受让人Id
     * @param msgContent 催办信息
     * @return {@code Y9Result<String>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<String> sendReminderMessage(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam String remType, @RequestParam String procInstId, @RequestParam String processInstanceId,
        @RequestParam String documentTitle, @RequestParam String taskId, @RequestParam String taskAssigneeId,
        @RequestParam String msgContent) {
        Y9LoginUserHolder.setTenantId(tenantId);
        OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, userId).getData();
        Y9LoginUserHolder.setOrgUnit(orgUnit);
        try {
            // 催办信息处理
            String err = reminderService.handleReminder(URLDecoder.decode(msgContent, "utf-8"), procInstId, 1, remType,
                taskId, taskAssigneeId, URLDecoder.decode(documentTitle, "utf-8"));
            if ("".equals(err)) {

                return Y9Result.successMsg("催办发送成功!");
            } else {
                String errMsg = "";
                String[] errs = err.split(";");
                if (!"".equals(errs[0])) {
                    errs[0] = errs[0].substring(0, errs[0].length() - 1);
                    errMsg = errs[0] + "短信未发送成功，请联系相关人员。";
                }
                if (!"".equals(errs[1])) {
                    errs[1] = errs[1].substring(0, errs[1].length() - 1);
                    if (errMsg.isEmpty()) {
                        errMsg = errs[1] + "邮件未发送成功，请联系相关人员。";
                    } else {
                        errMsg = "," + errs[1] + "邮件未发送成功，请联系相关人员。";
                    }

                }
                return Y9Result.failure(errMsg);
            }
        } catch (Exception e) {
            LOGGER.error("sendReminderMessage error", e);
            throw new Y9BusinessException(500, "发送催办信息异常,错误信息为：" + e.getMessage());
        }
    }

    /**
     * 设置为查看状态
     *
     * @param tenantId 租户id
     * @param ids 催办ids
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> setReadTime(@RequestParam String tenantId, @RequestBody String[] ids) {
        Y9LoginUserHolder.setTenantId(tenantId);
        reminderService.setReadTime(ids);
        return Y9Result.successMsg("设置为查看状态成功!");
    }

    /**
     * 更新催办信息
     *
     * @param tenantId 租户id
     * @param id 催办id
     * @param msgContent 催办信息
     * @return {@code Y9Result<String>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<String> updateReminder(@RequestParam String tenantId, @RequestParam String id,
        @RequestParam String msgContent) {
        Y9LoginUserHolder.setTenantId(tenantId);
        try {
            Reminder reminder = new Reminder();
            reminder.setId(id);
            reminder.setMsgContent(URLDecoder.decode(msgContent, "utf-8"));
            reminderService.saveOrUpdate(reminder);
            return Y9Result.successMsg("保存成功!");
        } catch (Exception e) {
            LOGGER.error("updateReminder error", e);
            throw new Y9BusinessException(500, "上传文件异常,错误信息为：" + e.getMessage());
        }
    }
}
