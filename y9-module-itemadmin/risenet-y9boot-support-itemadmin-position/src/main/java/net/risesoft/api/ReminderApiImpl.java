package net.risesoft.api;

import lombok.RequiredArgsConstructor;
import net.risesoft.api.itemadmin.ReminderApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.Reminder;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.model.platform.Position;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.service.ReminderService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 催办提醒接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/reminder")
public class ReminderApiImpl implements ReminderApi {

    private final ReminderService reminderService;

    private final PositionApi positionApi;

    private final TaskApi taskManager;

    /**
     * 删除催办
     *
     * @param tenantId 租户id
     * @param ids 催办ids
     */
    @Override
    @PostMapping(value = "/deleteList", produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public void deleteList(String tenantId, @RequestBody String[] ids) {
        Y9LoginUserHolder.setTenantId(tenantId);
        reminderService.deleteList(ids);
    }

    /**
     * 根据id获取催办
     *
     * @param tenantId 租户id
     * @param id 催办id
     * @return Map<String, Object>
     */
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

    /**
     * 获取流程实例的催办信息
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param page 页码
     * @param rows 条数
     * @return Map<String, Object>
     */
    @Override
    @GetMapping(value = "/findByProcessInstanceId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> findByProcessInstanceId(String tenantId, String processInstanceId, int page, int rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map = new HashMap<String, Object>(16);
        map = reminderService.findByProcessInstanceId(processInstanceId, page, rows);
        return map;
    }

    /**
     * 获取当前催办人的在办任务的催办信息
     *
     * @param tenantId 租户id
     * @param senderId 人员di
     * @param processInstanceId 流程实例id
     * @param page 页码
     * @param rows 条数
     * @return Map<String, Object>
     */
    @Override
    @GetMapping(value = "/findBySenderIdAndProcessInstanceIdAndActive", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> findBySenderIdAndProcessInstanceIdAndActive(String tenantId, String senderId,
        String processInstanceId, int page, int rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map = new HashMap<String, Object>(16);
        map = reminderService.findBySenderIdAndProcessInstanceIdAndActive(senderId, processInstanceId, page, rows);
        return map;
    }

    /**
     * 获取待办的提醒数据
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param page 页码
     * @param rows 条数
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @GetMapping(value = "/findByTaskId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> findByTaskId(String tenantId, String taskId, int page, int rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map = new HashMap<String, Object>(16);
        map = reminderService.findByTaskId(taskId, page, rows);
        return map;
    }

    /**
     * 查看催办信息
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param taskId 任务id
     * @param type 类型，todo（待办），doing（在办），done（办结）
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @GetMapping(value = "/getReminder", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getReminder(String tenantId, String userId, String taskId, String type) {
        Y9LoginUserHolder.setTenantId(tenantId);
        taskId = taskId.contains(SysVariables.COMMA) ? taskId.split(SysVariables.COMMA)[0] : taskId;
        Map<String, Object> map = new HashMap<String, Object>(16);
        Reminder reminder = new Reminder();
        if (ItemBoxTypeEnum.DOING.getValue().equals(type)) {
            reminder = reminderService.findByTaskIdAndSenderId(taskId, userId);
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

    /**
     * 保存催办信息
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processInstanceId 流程实例id
     * @param taskIds taskIds
     * @param msgContent 催办信息
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @PostMapping(value = "/saveReminder", produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> saveReminder(String tenantId, String userId, String processInstanceId,
        @RequestBody String[] taskIds, String msgContent) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPosition(position);
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

    /**
     * 发送催办信息
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param remType 催办类型，"1":短信,"2":邮件",3":站内信",4":待办列表中
     * @param procInstId procInstId
     * @param processInstanceId 流程实例id
     * @param documentTitle 文档标题
     * @param taskId 任务id
     * @param taskAssigneeId 任务受让人Id
     * @param msgContent 催办信息
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @PostMapping(value = "/sendReminderMessage", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> sendReminderMessage(String tenantId, String userId, String remType, String procInstId,
        String processInstanceId, String documentTitle, String taskId, String taskAssigneeId, String msgContent) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPosition(position);
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

    /**
     * 设置为查看状态
     *
     * @param tenantId 租户id
     * @param ids 催办ids
     */
    @Override
    @PostMapping(value = "/setReadTime", produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public void setReadTime(String tenantId, @RequestBody String[] ids) {
        Y9LoginUserHolder.setTenantId(tenantId);
        reminderService.setReadTime(ids);
    }

    /**
     * 更新催办信息
     *
     * @param tenantId 租户id
     * @param id 催办id
     * @param msgContent 催办信息
     * @return Map&lt;String, Object&gt;
     */
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
