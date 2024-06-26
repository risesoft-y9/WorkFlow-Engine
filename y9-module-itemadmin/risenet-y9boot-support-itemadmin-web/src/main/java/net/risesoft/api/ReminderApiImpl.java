package net.risesoft.api;

import java.net.URLDecoder;

import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.ReminderApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.entity.Reminder;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.model.itemadmin.ReminderModel;
import net.risesoft.model.platform.Person;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.ReminderService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.exception.Y9BusinessException;

import y9.client.rest.processadmin.TaskApiClient;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/services/rest/reminder", produces = MediaType.APPLICATION_JSON_VALUE)
public class ReminderApiImpl implements ReminderApi {
    private static final FastDateFormat DATE_TIME_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

    private final ReminderService reminderService;

    private final PersonApi personManager;

    private final TaskApiClient taskManager;

    @Override
    public Y9Result<Object> deleteList(String tenantId, @RequestBody String[] ids) {
        Y9LoginUserHolder.setTenantId(tenantId);
        reminderService.deleteList(ids);
        return Y9Result.success();
    }

    @Override
    public Y9Result<ReminderModel> findById(String tenantId, String id) {
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

    @Override
    public Y9Page<ReminderModel> findByProcessInstanceId(String tenantId, String processInstanceId, int page,
        int rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return reminderService.findByProcessInstanceId(processInstanceId, page, rows);
    }

    @Override
    public Y9Page<ReminderModel> findBySenderIdAndProcessInstanceIdAndActive(String tenantId, String senderId,
        String processInstanceId, int page, int rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return reminderService.findBySenderIdAndProcessInstanceIdAndActive(senderId, processInstanceId, page, rows);
    }

    @Override
    public Y9Page<ReminderModel> findByTaskId(String tenantId, String taskId, int page, int rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return reminderService.findByTaskId(taskId, page, rows);
    }

    @Override
    public Y9Result<ReminderModel> getReminder(String tenantId, String userId, String taskId, String type) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        taskId = taskId.contains(SysVariables.COMMA) ? taskId.split(SysVariables.COMMA)[0] : taskId;
        Reminder reminder = new Reminder();
        if (ItemBoxTypeEnum.DOING.getValue().equals(type)) {
            reminder = reminderService.findByTaskIdAndSenderId(taskId, person.getId());
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

    @Override
    public Y9Result<String> saveReminder(String tenantId, String userId, String processInstanceId,
        @RequestBody String[] taskIds, String msgContent) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        try {
            Reminder reminder = null;
            for (String taskId : taskIds) {
                reminder = new Reminder();
                reminder.setMsgContent(URLDecoder.decode(msgContent, "utf-8"));
                reminder.setProcInstId(processInstanceId);
                reminder.setTaskId(taskId);
                reminderService.saveOrUpdate(reminder);

                TaskModel task = taskManager.findById(tenantId, taskId);
                if (!String.valueOf(task.getPriority()).contains("8")) {
                    taskManager.setPriority(tenantId, taskId, task.getPriority() + 8);
                }
            }
            return Y9Result.successMsg("保存成功!");
        } catch (Exception e) {
            e.printStackTrace();
            throw new Y9BusinessException(500, "保存催办信息失败,错误信息为：" + e.getMessage());
        }
    }

    @Override
    public Y9Result<String> sendReminderMessage(String tenantId, String userId, String remType, String procInstId,
        String processInstanceId, String documentTitle, String taskId, String taskAssigneeId, String msgContent) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
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
                    if ("".equals(errMsg)) {
                        errMsg = errs[1] + "邮件未发送成功，请联系相关人员。";
                    } else {
                        errMsg = "," + errs[1] + "邮件未发送成功，请联系相关人员。";
                    }

                }
                return Y9Result.failure(errMsg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Y9BusinessException(500, "发送催办信息异常,错误信息为：" + e.getMessage());
        }
    }

    @Override
    public Y9Result<Object> setReadTime(String tenantId, @RequestBody String[] ids) {
        Y9LoginUserHolder.setTenantId(tenantId);
        reminderService.setReadTime(ids);
        return Y9Result.successMsg("设置为查看状态成功!");
    }

    @Override
    public Y9Result<String> updateReminder(String tenantId, String id, String msgContent) {
        Y9LoginUserHolder.setTenantId(tenantId);
        try {
            Reminder reminder = new Reminder();
            reminder.setId(id);
            reminder.setMsgContent(URLDecoder.decode(msgContent, "utf-8"));
            reminderService.saveOrUpdate(reminder);
            return Y9Result.successMsg("保存成功!");
        } catch (Exception e) {
            e.printStackTrace();
            throw new Y9BusinessException(500, "上传文件异常,错误信息为：" + e.getMessage());
        }
    }
}
