package net.risesoft.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.entity.Reminder;
import net.risesoft.enums.ItemUrgeTypeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.ReminderModel;
import net.risesoft.model.platform.Person;
import net.risesoft.model.processadmin.HistoricTaskInstanceModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Page;
import net.risesoft.repository.jpa.ReminderRepository;
import net.risesoft.service.ReminderService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;

import y9.client.rest.processadmin.HistoricTaskApiClient;
import y9.client.rest.processadmin.TaskApiClient;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Service(value = "reminderService")
public class ReminderServiceImpl implements ReminderService {

    private static final FastDateFormat DATE_TIME_FORMAT = FastDateFormat.getInstance("yy/MM/dd HH:mm");

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ReminderRepository reminderRepository;

    @Autowired
    private PersonApi personManager;

    @Autowired
    private TaskApiClient taskManager;

    @Autowired
    private HistoricTaskApiClient historicTaskManager;

    @Override
    @Transactional(readOnly = false)
    public void deleteList(String[] ids) {
        Reminder r = null;
        for (String id : ids) {
            r = reminderRepository.findById(id).orElse(null);
            reminderRepository.delete(r);
        }
    }

    @Override
    public List<Reminder> findAllByTaskId(Collection<String> taskIds) {
        List<Reminder> list = new ArrayList<>();
        if (taskIds != null && !taskIds.isEmpty()) {
            list = reminderRepository.findAllByTastId(taskIds);
        }
        return list;
    }

    @Override
    public List<Reminder> findAllByTaskIdsAndSenderId(Collection<String> taskIds, String senderId) {
        return reminderRepository.findAllByTaskIdsAndSenderId(taskIds, senderId);
    }

    @Override
    public Reminder findById(String id) {
        return reminderRepository.findById(id).orElse(null);
    }

    @Override
    public Y9Page<ReminderModel> findByProcessInstanceId(String processInstanceId, int page, int rows) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        Page<Reminder> pageList = reminderRepository.findByprocInstId(processInstanceId, pageable);
        List<Reminder> reminderList = pageList.getContent();
        int num = (page - 1) * rows;
        List<ReminderModel> listMap = new ArrayList<>();
        HistoricTaskInstanceModel historicTaskTemp = null;
        Person personTemp = null;
        for (Reminder reminder : reminderList) {
            ReminderModel model = new ReminderModel();
            model.setId(reminder.getId());
            model.setMsgContent(reminder.getMsgContent());
            model.setCreateTime(DATE_TIME_FORMAT.format(reminder.getCreateTime()));
            if (null == reminder.getReadTime()) {
                model.setReadTime("");
            } else {
                model.setReadTime(DATE_TIME_FORMAT.format(reminder.getReadTime()));
            }
            model.setSenderName(reminder.getSenderName());
            model.setUserName("无");
            model.setTaskName("无");

            historicTaskTemp = historicTaskManager.getById(tenantId, reminder.getTaskId());
            if (null != historicTaskTemp) {
                model.setTaskName(historicTaskTemp.getName());
                if (StringUtils.isNotBlank(historicTaskTemp.getAssignee())) {
                    personTemp = personManager.get(tenantId, historicTaskTemp.getAssignee()).getData();
                    if (null != personTemp) {
                        model.setUserName(
                            personTemp.getName() + (Boolean.TRUE.equals(personTemp.getDisabled()) ? "(已禁用)" : ""));
                    }
                }
            }
            model.setSerialNumber(num + 1);
            num += 1;
            listMap.add(model);
        }
        return Y9Page.success(page, pageList.getTotalPages(), pageList.getTotalElements(), listMap);
    }

    @Override
    public Y9Page<ReminderModel> findBySenderIdAndProcessInstanceIdAndActive(String senderId, String processInstanceId,
        int page, int rows) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        List<TaskModel> taskList = taskManager.findByProcessInstanceId(tenantId, processInstanceId);
        List<String> taskIds = new ArrayList<>();
        for (TaskModel task : taskList) {
            taskIds.add(task.getId());
        }
        Page<Reminder> pageList = reminderRepository.findBySenderIdAndTaskIdIn(senderId, taskIds, pageable);
        List<Reminder> reminderList = pageList.getContent();
        int num = (page - 1) * rows;
        List<ReminderModel> listMap = new ArrayList<>();
        TaskModel taskTemp = null;
        Person personTemp = null;
        for (Reminder reminder : reminderList) {
            ReminderModel model = new ReminderModel();
            model.setId(reminder.getId());
            model.setMsgContent(reminder.getMsgContent());
            model.setCreateTime(DATE_TIME_FORMAT.format(reminder.getCreateTime()));
            if (null == reminder.getReadTime()) {
                model.setReadTime("");
            } else {
                model.setReadTime(DATE_TIME_FORMAT.format(reminder.getReadTime()));
            }
            model.setSenderName(reminder.getSenderName());
            model.setUserName("无");
            model.setTaskName("无");
            taskTemp = taskManager.findById(tenantId, reminder.getTaskId());
            if (null != taskTemp) {
                model.setTaskName(taskTemp.getName());
                if (StringUtils.isNotBlank(taskTemp.getAssignee())) {
                    personTemp = personManager.get(tenantId, taskTemp.getAssignee()).getData();
                    if (null != personTemp) {
                        model.setUserName(
                            personTemp.getName() + (Boolean.TRUE.equals(personTemp.getDisabled()) ? "(已禁用)" : ""));
                    }
                }
            }
            model.setSerialNumber(num + 1);
            num += 1;
            listMap.add(model);
        }
        return Y9Page.success(page, pageList.getTotalPages(), pageList.getTotalElements(), listMap);
    }

    @Override
    public Reminder findByTaskId(String taskId) {
        return reminderRepository.findByTaskId(taskId);
    }

    @Override
    public Y9Page<ReminderModel> findByTaskId(String taskId, int page, int rows) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        Page<Reminder> pageList = reminderRepository.findByTaskId(taskId, pageable);
        List<Reminder> reminderList = pageList.getContent();
        int num = (page - 1) * rows;
        List<ReminderModel> listMap = new ArrayList<>();
        TaskModel taskTemp = taskManager.findById(tenantId, taskId);
        Person personTemp = personManager.get(tenantId, taskTemp.getAssignee()).getData();
        for (Reminder reminder : reminderList) {
            ReminderModel model = new ReminderModel();
            model.setId(reminder.getId());
            model.setMsgContent(reminder.getMsgContent());
            model.setCreateTime(DATE_TIME_FORMAT.format(reminder.getCreateTime()));
            if (null == reminder.getReadTime()) {
                model.setReadTime("");
            } else {
                model.setReadTime(DATE_TIME_FORMAT.format(reminder.getReadTime()));
            }
            model.setSenderName(reminder.getSenderName());
            model.setUserName(personTemp.getName());
            model.setTaskName(taskTemp.getName());
            model.setSerialNumber(num + 1);
            num += 1;
            listMap.add(model);
        }
        return Y9Page.success(page, pageList.getTotalPages(), pageList.getTotalElements(), listMap);
    }

    @Override
    public Reminder findByTaskIdAndSenderId(String taskId, String senderId) {
        return reminderRepository.findByTaskIdAndSenderId(taskId, senderId);
    }

    @Override
    public List<Reminder> findByTastIdAndReminderSendType(String taskId, String reminderSendType) {
        return reminderRepository.findByTastIdAndReminderSendType(taskId, reminderSendType);
    }

    @Override
    @Transactional(readOnly = false)
    public String handleReminder(String msgContent, String procInstId, Integer reminderAutomatic, String remType,
        String taskId, String taskAssigneeId, String documentTitle) {
        String smsErr = "";
        String emailErr = "";
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String[] procInstIds = procInstId.split(SysVariables.COMMA);
        String[] taskIds = taskId.split(SysVariables.COMMA);
        String[] taskAssigneeIds = taskAssigneeId.split(SysVariables.COMMA);
        List<Reminder> list = new ArrayList<>();
        for (int i = 0; i < procInstIds.length; i++) {
            Reminder reminder = new Reminder();
            reminder.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            reminder.setMsgContent(msgContent);
            reminder.setProcInstId(procInstIds[i]);
            reminder.setReminderMakeTyle(reminderAutomatic);
            reminder.setReminderSendType(ItemUrgeTypeEnum.TODOINFO.getValue());
            reminder.setSenderId(userInfo.getPersonId());
            reminder.setSenderName(userInfo.getName());
            reminder.setTaskId(taskIds[i]);
            reminder.setCreateTime(new Date());
            reminder.setModifyTime(new Date());
            list.add(reminder);

            // 发送短信
            if (remType.contains(ItemUrgeTypeEnum.SMS.getValue())) {

            }
            // 发送邮件
            if (remType.contains(ItemUrgeTypeEnum.EMAIL.getValue())) {
                try {
                } catch (Exception e) {
                    logger.error("email error {}", e.getMessage());
                    Person errEmployee =
                        personManager.get(Y9LoginUserHolder.getTenantId(), taskAssigneeIds[i]).getData();
                    emailErr += errEmployee.getName() + "、";
                }
            }
        }
        saveReminder(list);
        if ("".equals(smsErr) && "".equals(emailErr)) {
            return "";
        } else {
            return smsErr + SysVariables.SEMICOLON + emailErr;
        }
    }

    @Override
    @Transactional(readOnly = false)
    public Reminder saveOrUpdate(Reminder reminder) {
        String id = reminder.getId();
        if (StringUtils.isNotBlank(id)) {
            Reminder r = this.findById(id);
            r.setMsgContent(reminder.getMsgContent());
            r.setModifyTime(new Date());
            r.setReadTime(null);
            reminderRepository.save(r);
            return r;
        }
        String tenantId = Y9LoginUserHolder.getTenantId();
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        Reminder r = new Reminder();
        r.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        r.setCreateTime(new Date());
        r.setModifyTime(new Date());
        r.setReminderMakeTyle(1);
        r.setReminderSendType(ItemUrgeTypeEnum.TODOINFO.getValue());
        r.setSenderId(userInfo.getPersonId());
        r.setSenderName(userInfo.getName());
        r.setTenantId(tenantId);
        r.setTaskId(reminder.getTaskId());
        r.setProcInstId(reminder.getProcInstId());
        r.setMsgContent(reminder.getMsgContent());
        reminderRepository.save(r);
        return r;
    }

    @Override
    @Transactional(readOnly = false)
    public void saveReminder(List<Reminder> list) {
        reminderRepository.saveAll(list);
    }

    @Override
    @Transactional(readOnly = false)
    public void saveReminder(Reminder reminder) {
        reminderRepository.save(reminder);
    }

    @Override
    @Transactional(readOnly = false)
    public void setReadTime(Date readTime, String taskId, String type) {
        reminderRepository.updateReadTime(readTime, taskId, type);
    }

    @Override
    @Transactional(readOnly = false)
    public void setReadTime(String[] ids) {
        Reminder r = null;
        for (String id : ids) {
            r = this.findById(id);
            if (null == r.getReadTime()) {
                r.setReadTime(new Date());
                reminderRepository.save(r);
            }
        }
    }
}
