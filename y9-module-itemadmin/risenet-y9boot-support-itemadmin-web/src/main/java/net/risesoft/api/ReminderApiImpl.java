package net.risesoft.api;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.ReminderApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.Reminder;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.model.platform.Person;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.service.ReminderService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;

import y9.client.rest.processadmin.TaskApiClient;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@RestController
@RequestMapping(value = "/services/rest/reminder")
public class ReminderApiImpl implements ReminderApi {

    @Autowired
    private ReminderService reminderService;

    @Autowired
    private PersonApi personManager;

    @Autowired
    private TaskApiClient taskManager;

    @Override
    @PostMapping(value = "/deleteList", produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public void deleteList(String tenantId, @RequestBody String[] ids) {
        Y9LoginUserHolder.setTenantId(tenantId);
        reminderService.deleteList(ids);
    }

    @Override
    @GetMapping(value = "/findById", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> findById(String tenantId, String id) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map = new HashMap<String, Object>(16);
        Reminder reminder = reminderService.findById(id);
        if (reminder != null && reminder.getId() != null) {
            map.put("id", reminder.getId());
            map.put("msgContent", reminder.getMsgContent());
        } else {
            map.put("id", "");
            map.put("msgContent", "");
        }
        return map;
    }

    @Override
    @GetMapping(value = "/findByProcessInstanceId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> findByProcessInstanceId(String tenantId, String processInstanceId, int page, int rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map = new HashMap<String, Object>(16);
        map = reminderService.findByProcessInstanceId(processInstanceId, page, rows);
        return map;
    }

    @Override
    @GetMapping(value = "/findBySenderIdAndProcessInstanceIdAndActive", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> findBySenderIdAndProcessInstanceIdAndActive(String tenantId, String senderId,
        String processInstanceId, int page, int rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map = new HashMap<String, Object>(16);
        map = reminderService.findBySenderIdAndProcessInstanceIdAndActive(senderId, processInstanceId, page, rows);
        return map;
    }

    @Override
    @GetMapping(value = "/findByTaskId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> findByTaskId(String tenantId, String taskId, int page, int rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map = new HashMap<String, Object>(16);
        map = reminderService.findByTaskId(taskId, page, rows);
        return map;
    }

    @Override
    @GetMapping(value = "/getReminder", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getReminder(String tenantId, String userId, String taskId, String type) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        taskId = taskId.contains(SysVariables.COMMA) ? taskId.split(SysVariables.COMMA)[0] : taskId;
        Map<String, Object> map = new HashMap<String, Object>(16);
        Reminder reminder = new Reminder();
        if (ItemBoxTypeEnum.DOING.getValue().equals(type)) {
            reminder = reminderService.findByTaskIdAndSenderId(taskId, person.getId());
        }
        if (ItemBoxTypeEnum.TODO.getValue().equals(type)) {
            reminder = reminderService.findByTaskId(taskId);
        }
        if (reminder != null && reminder.getId() != null) {
            map.put("id", reminder.getId());
            map.put("createTime", reminder.getCreateTime());
            map.put("modifyTime", reminder.getModifyTime());
            map.put("reminderMakeTyle", reminder.getReminderMakeTyle());
            map.put("reminderSendType", reminder.getReminderSendType());
            map.put("senderId", reminder.getSenderId());
            map.put("senderName", reminder.getSenderName());
            map.put("tenantId", reminder.getTenantId());
            map.put("taskId", reminder.getTaskId());
            map.put("procInstId", reminder.getProcInstId());
            map.put("msgContent", reminder.getMsgContent());
        } else {
            map.put("id", "");
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "查询失败，没有对应的催办信息");
        }
        return map;
    }

    @Override
    @PostMapping(value = "/saveReminder", produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> saveReminder(String tenantId, String userId, String processInstanceId,
        @RequestBody String[] taskIds, String msgContent) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put(UtilConsts.SUCCESS, false);
        map.put("msg", "保存失败");
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
            map.put(UtilConsts.SUCCESS, true);
            map.put("msg", "保存成功!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    @Override
    @PostMapping(value = "/sendReminderMessage", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> sendReminderMessage(String tenantId, String userId, String remType, String procInstId,
        String processInstanceId, String documentTitle, String taskId, String taskAssigneeId, String msgContent) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            // 催办信息处理
            String err = reminderService.handleReminder(URLDecoder.decode(msgContent, "utf-8"), procInstId, 1, remType,
                taskId, taskAssigneeId, URLDecoder.decode(documentTitle, "utf-8"));
            if ("".equals(err)) {
                map.put(UtilConsts.SUCCESS, true);
                map.put("msg", "催办发送成功!");
            } else {
                map.put(UtilConsts.SUCCESS, false);
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
                map.put("msg", errMsg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    @Override
    @PostMapping(value = "/setReadTime", produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public void setReadTime(String tenantId, @RequestBody String[] ids) {
        Y9LoginUserHolder.setTenantId(tenantId);
        reminderService.setReadTime(ids);
    }

    @Override
    @PostMapping(value = "/updateReminder", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> updateReminder(String tenantId, String id, String msgContent) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put(UtilConsts.SUCCESS, false);
        map.put("msg", "保存失败");
        try {
            Reminder reminder = new Reminder();
            reminder.setId(id);
            reminder.setMsgContent(URLDecoder.decode(msgContent, "utf-8"));
            reminderService.saveOrUpdate(reminder);
            map.put(UtilConsts.SUCCESS, true);
            map.put("msg", "保存成功!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }
}
