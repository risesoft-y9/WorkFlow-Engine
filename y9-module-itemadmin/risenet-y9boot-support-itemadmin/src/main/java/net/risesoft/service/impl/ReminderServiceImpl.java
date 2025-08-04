package net.risesoft.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.processadmin.HistoricTaskApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.consts.processadmin.SysVariables;
import net.risesoft.entity.Reminder;
import net.risesoft.enums.ItemUrgeTypeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.ReminderModel;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.processadmin.HistoricTaskInstanceModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.repository.jpa.ReminderRepository;
import net.risesoft.service.ReminderService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Slf4j
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Service
@RequiredArgsConstructor
public class ReminderServiceImpl implements ReminderService {

    private static final FastDateFormat DATE_TIME_FORMAT = FastDateFormat.getInstance("yy/MM/dd HH:mm");

    private final ReminderRepository reminderRepository;

    private final OrgUnitApi orgUnitApi;

    private final TaskApi taskApi;

    private final HistoricTaskApi historictaskApi;

    @Override
    @Transactional
    public void deleteList(String[] ids) {
        Reminder r = null;
        for (String id : ids) {
            r = this.findById(id);
            reminderRepository.delete(r);
        }
    }

    @Override
    public Reminder findById(String id) {
        return reminderRepository.findById(id).orElse(null);
    }

    @Override
    public Reminder findByTaskId(String taskId) {
        return reminderRepository.findByTaskId(taskId);
    }

    @Override
    public Reminder findByTaskIdAndSenderId(String taskId, String senderId) {
        return reminderRepository.findByTaskIdAndSenderId(taskId, senderId);
    }

    @Override
    @Transactional
    public String handleReminder(String msgContent, String procInstId, Integer reminderAutomatic, String remType,
        String taskId, String taskAssigneeId, String documentTitle) {
        String smsErr = "";
        String emailErr = "";
        OrgUnit person = Y9LoginUserHolder.getOrgUnit();
        String[] procInstIds = procInstId.split(SysVariables.COMMA);
        String[] taskIds = taskId.split(SysVariables.COMMA);
        // String[] taskAssigneeIds = taskAssigneeId.split(SysVariables.COMMA);
        List<Reminder> list = new ArrayList<>();
        for (int i = 0; i < procInstIds.length; i++) {
            Reminder reminder = new Reminder();
            reminder.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            reminder.setMsgContent(msgContent);
            reminder.setProcInstId(procInstIds[i]);
            reminder.setReminderMakeTyle(reminderAutomatic);
            reminder.setReminderSendType(ItemUrgeTypeEnum.TODOINFO.getValue());
            reminder.setSenderId(person.getId());
            reminder.setSenderName(person.getName());
            reminder.setTaskId(taskIds[i]);
            reminder.setCreateTime(new Date());
            reminder.setModifyTime(new Date());
            list.add(reminder);

            // 发送短信
            if (remType.contains(ItemUrgeTypeEnum.SMS.getValue())) {

            }
            // 发送邮件
            LOGGER.info("starttime--{}", new Date());
            if (remType.contains(ItemUrgeTypeEnum.EMAIL.getValue())) {
                try {
                } catch (Exception e) {
                    LOGGER.error("email error", e.getMessage());
                    // Person errEmployee = personApi.getPerson(Y9LoginUserHolder.getTenantId(),taskAssigneeIds[i]);
                    // emailErr += errEmployee.getName() + "、";
                }
            }
            LOGGER.info("endtime--{}", new Date());
        }
        saveReminder(list);
        if ("".equals(smsErr)) {
            return "";
        } else {
            return smsErr + SysVariables.SEMICOLON + emailErr;
        }
    }

    @Override
    public List<Reminder> listByTaskId(Collection<String> taskIds) {
        List<Reminder> list = new ArrayList<>();
        if (taskIds != null && !taskIds.isEmpty()) {
            list = reminderRepository.findAllByTastId(taskIds);
        }
        return list;
    }

    @Override
    public List<Reminder> listByTaskIdsAndSenderId(Collection<String> taskIds, String senderId) {
        return reminderRepository.findAllByTaskIdsAndSenderId(taskIds, senderId);
    }

    @Override
    public List<Reminder> listByTastIdAndReminderSendType(String taskId, String reminderSendType) {
        return reminderRepository.findByTastIdAndReminderSendType(taskId, reminderSendType);
    }

    @Override
    public Y9Page<ReminderModel> pageByProcessInstanceId(String processInstanceId, int page, int rows) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        Page<Reminder> pageList = reminderRepository.findByprocInstId(processInstanceId, pageable);
        List<Reminder> reminderList = pageList.getContent();
        int num = (page - 1) * rows;
        List<ReminderModel> listMap = new ArrayList<>();
        HistoricTaskInstanceModel historicTaskTemp = null;
        OrgUnit pTemp = null;
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

            historicTaskTemp = historictaskApi.getById(tenantId, reminder.getTaskId()).getData();
            if (null != historicTaskTemp) {
                model.setTaskName(historicTaskTemp.getName());
                if (StringUtils.isNotBlank(historicTaskTemp.getAssignee())) {
                    pTemp = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, historicTaskTemp.getAssignee()).getData();
                    if (null != pTemp) {
                        model.setUserName(pTemp.getName() + (Boolean.TRUE.equals(pTemp.getDisabled()) ? "(已禁用)" : ""));
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
    public Y9Page<ReminderModel> pageBySenderIdAndProcessInstanceIdAndActive(String senderId, String processInstanceId,
        int page, int rows) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        List<TaskModel> taskList = taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
        List<String> taskIds = new ArrayList<>();
        for (TaskModel task : taskList) {
            taskIds.add(task.getId());
        }
        Page<Reminder> pageList = reminderRepository.findBySenderIdAndTaskIdIn(senderId, taskIds, pageable);
        List<Reminder> reminderList = pageList.getContent();
        int num = (page - 1) * rows;
        List<ReminderModel> listMap = new ArrayList<>();
        TaskModel taskTemp = null;
        OrgUnit pTemp = null;
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
            taskTemp = taskApi.findById(tenantId, reminder.getTaskId()).getData();
            if (null != taskTemp) {
                model.setTaskName(taskTemp.getName());
                if (StringUtils.isNotBlank(taskTemp.getAssignee())) {
                    pTemp = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, taskTemp.getAssignee()).getData();
                    if (null != pTemp) {
                        model.setUserName(pTemp.getName() + (Boolean.TRUE.equals(pTemp.getDisabled()) ? "(已禁用)" : ""));
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
    public Y9Page<ReminderModel> pageByTaskId(String taskId, int page, int rows) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        Page<Reminder> pageList = reminderRepository.findByTaskId(taskId, pageable);
        List<Reminder> reminderList = pageList.getContent();
        int num = (page - 1) * rows;
        List<ReminderModel> listMap = new ArrayList<>();
        TaskModel taskTemp = taskApi.findById(tenantId, taskId).getData();
        OrgUnit pTemp = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, taskTemp.getAssignee()).getData();
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
            model.setUserName(pTemp.getName());
            model.setTaskName(taskTemp.getName());
            model.setSerialNumber(num + 1);
            num += 1;
            listMap.add(model);
        }
        return Y9Page.success(page, pageList.getTotalPages(), pageList.getTotalElements(), listMap);
    }

    @Override
    @Transactional
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
        OrgUnit orgUnit = Y9LoginUserHolder.getOrgUnit();
        Reminder r = new Reminder();
        r.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        r.setCreateTime(new Date());
        r.setModifyTime(new Date());
        r.setReminderMakeTyle(1);
        r.setReminderSendType(ItemUrgeTypeEnum.TODOINFO.getValue());
        r.setSenderId(orgUnit.getId());
        r.setSenderName(orgUnit.getName());
        r.setTenantId(tenantId);
        r.setTaskId(reminder.getTaskId());
        r.setProcInstId(reminder.getProcInstId());
        r.setMsgContent(reminder.getMsgContent());
        reminderRepository.save(r);
        return r;
    }

    @Override
    @Transactional
    public void saveReminder(List<Reminder> list) {
        reminderRepository.saveAll(list);
    }

    @Override
    @Transactional
    public void saveReminder(Reminder reminder) {
        reminderRepository.save(reminder);
    }

    @Override
    @Transactional
    public void setReadTime(Date readTime, String taskId, String type) {
        reminderRepository.updateReadTime(readTime, taskId, type);
    }

    @Override
    @Transactional
    public void setReadTime(String[] ids) {
        Reminder r = null;
        for (String id : ids) {
            r = reminderRepository.findById(id).orElse(null);
            assert r != null;
            if (null == r.getReadTime()) {
                r.setReadTime(new Date());
                reminderRepository.save(r);
            }
        }
    }
}
