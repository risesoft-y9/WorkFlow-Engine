package net.risesoft.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.api.processadmin.HistoricTaskApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.entity.Reminder;
import net.risesoft.enums.ItemUrgeTypeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.platform.Position;
import net.risesoft.model.processadmin.HistoricTaskInstanceModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.repository.jpa.ReminderRepository;
import net.risesoft.service.ReminderService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Service(value = "reminderService")
@Slf4j
public class ReminderServiceImpl implements ReminderService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ReminderRepository reminderRepository;

    @Autowired
    private PositionApi positionApi;

    @Autowired
    private TaskApi taskManager;

    @Autowired
    private HistoricTaskApi historicTaskManager;

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
        List<Reminder> list = new ArrayList<Reminder>();
        if (taskIds.size() > 0) {
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
    public Map<String, Object> findByProcessInstanceId(String processInstanceId, int page, int rows) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Map<String, Object> retMap = new HashMap<String, Object>(16);
        List<Reminder> reminderList = new ArrayList<Reminder>();
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        Page<Reminder> pageList = reminderRepository.findByprocInstId(processInstanceId, pageable);
        reminderList = pageList.getContent();
        int num = (page - 1) * rows;
        SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd HH:mm");
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        HistoricTaskInstanceModel historicTaskTemp = null;
        Position pTemp = null;
        for (Reminder reminder : reminderList) {
            Map<String, Object> map = new HashMap<String, Object>(16);
            map.put("id", reminder.getId());
            map.put("msgContent", reminder.getMsgContent());
            map.put("createTime", sdf.format(reminder.getCreateTime()));
            if (null == reminder.getReadTime()) {
                map.put("readTime", "");
            } else {
                map.put("readTime", sdf.format(reminder.getReadTime()));
            }
            map.put("senderName", reminder.getSenderName());
            map.put("userName", "无");
            map.put("taskName", "无");
            historicTaskTemp = historicTaskManager.getById(tenantId, reminder.getTaskId());
            if (null != historicTaskTemp) {
                map.put("taskName", historicTaskTemp.getName());
                if (StringUtils.isNotBlank(historicTaskTemp.getAssignee())) {
                    pTemp = positionApi.getPosition(tenantId, historicTaskTemp.getAssignee()).getData();
                    if (null != pTemp) {
                        map.put("userName", pTemp.getName());
                    }
                }
            }
            map.put("serialNumber", num + 1);
            num += 1;
            listMap.add(map);
        }
        retMap.put("currpage", page);
        retMap.put("totalpages", pageList.getTotalPages());
        retMap.put("total", pageList.getTotalElements());
        retMap.put("rows", listMap);
        return retMap;
    }

    @Override
    public Map<String, Object> findBySenderIdAndProcessInstanceIdAndActive(String senderId, String processInstanceId,
        int page, int rows) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Map<String, Object> retMap = new HashMap<String, Object>(16);
        List<Reminder> reminderList = new ArrayList<Reminder>();
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        List<TaskModel> taskList = taskManager.findByProcessInstanceId(tenantId, processInstanceId);
        List<String> taskIds = new ArrayList<>();
        for (TaskModel task : taskList) {
            taskIds.add(task.getId());
        }
        Page<Reminder> pageList = reminderRepository.findBySenderIdAndTaskIdIn(senderId, taskIds, pageable);
        reminderList = pageList.getContent();
        int num = (page - 1) * rows;
        SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd HH:mm");
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        TaskModel taskTemp = null;
        Position pTemp = null;
        for (Reminder reminder : reminderList) {
            Map<String, Object> map = new HashMap<String, Object>(16);
            map.put("id", reminder.getId());
            map.put("msgContent", reminder.getMsgContent());
            map.put("createTime", sdf.format(reminder.getCreateTime()));
            if (null == reminder.getReadTime()) {
                map.put("readTime", "");
            } else {
                map.put("readTime", sdf.format(reminder.getReadTime()));
            }
            map.put("senderName", reminder.getSenderName());
            map.put("userName", "无");
            map.put("taskName", "无");
            taskTemp = taskManager.findById(tenantId, reminder.getTaskId());
            if (null != taskTemp) {
                map.put("taskName", taskTemp.getName());
                if (StringUtils.isNotBlank(taskTemp.getAssignee())) {
                    pTemp = positionApi.getPosition(tenantId, taskTemp.getAssignee()).getData();
                    if (null != pTemp) {
                        map.put("userName", pTemp.getName());
                    }
                }
            }
            map.put("serialNumber", num + 1);
            num += 1;
            listMap.add(map);
        }
        retMap.put("currpage", page);
        retMap.put("totalpages", pageList.getTotalPages());
        retMap.put("total", pageList.getTotalElements());
        retMap.put("rows", listMap);
        return retMap;
    }

    @Override
    public Reminder findByTaskId(String taskId) {
        return reminderRepository.findByTaskId(taskId);
    }

    @Override
    public Map<String, Object> findByTaskId(String taskId, int page, int rows) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Map<String, Object> retMap = new HashMap<String, Object>(16);
        List<Reminder> reminderList = new ArrayList<Reminder>();
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        Page<Reminder> pageList = reminderRepository.findByTaskId(taskId, pageable);
        reminderList = pageList.getContent();
        int num = (page - 1) * rows;
        SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd HH:mm");
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        TaskModel taskTemp = taskManager.findById(tenantId, taskId);
        Position pTemp = positionApi.getPosition(tenantId, taskTemp.getAssignee()).getData();
        for (Reminder reminder : reminderList) {
            Map<String, Object> map = new HashMap<String, Object>(16);
            map.put("id", reminder.getId());
            map.put("msgContent", reminder.getMsgContent());
            map.put("createTime", sdf.format(reminder.getCreateTime()));
            if (null == reminder.getReadTime()) {
                map.put("readTime", "");
            } else {
                map.put("readTime", sdf.format(reminder.getReadTime()));
            }
            map.put("senderName", reminder.getSenderName());
            map.put("userName", pTemp.getName());
            map.put("taskName", taskTemp.getName());
            map.put("serialNumber", num + 1);
            num += 1;
            listMap.add(map);
        }
        retMap.put("currpage", page);
        retMap.put("totalpages", pageList.getTotalPages());
        retMap.put("total", pageList.getTotalElements());
        retMap.put("rows", listMap);
        return retMap;
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
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String[] procInstIds = procInstId.split(SysVariables.COMMA);
        String[] taskIds = taskId.split(SysVariables.COMMA);
        // String[] taskAssigneeIds = taskAssigneeId.split(SysVariables.COMMA);
        List<Reminder> list = new ArrayList<Reminder>();
        for (int i = 0; i < procInstIds.length; i++) {
            Reminder reminder = new Reminder();
            reminder.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            reminder.setMsgContent(msgContent);
            reminder.setProcInstId(procInstIds[i]);
            reminder.setReminderMakeTyle(reminderAutomatic);
            reminder.setReminderSendType(ItemUrgeTypeEnum.TODOINFO.getValue());
            reminder.setSenderId(person.getPersonId());
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
                    logger.error("email error", e.getMessage());
                    // Person errEmployee = personManager.getPerson(Y9LoginUserHolder.getTenantId(),taskAssigneeIds[i]);
                    // emailErr += errEmployee.getName() + "、";
                }
            }
            LOGGER.info("endtime--{}", new Date());
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
            Reminder r = reminderRepository.findById(id).orElse(null);
            r.setMsgContent(reminder.getMsgContent());
            r.setModifyTime(new Date());
            r.setReadTime(null);
            reminderRepository.save(r);
            return r;
        }
        String tenantId = Y9LoginUserHolder.getTenantId();
        Position position = Y9LoginUserHolder.getPosition();
        Reminder r = new Reminder();
        r.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        r.setCreateTime(new Date());
        r.setModifyTime(new Date());
        r.setReminderMakeTyle(1);
        r.setReminderSendType(ItemUrgeTypeEnum.TODOINFO.getValue());
        r.setSenderId(position.getId());
        r.setSenderName(position.getName());
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
            r = reminderRepository.findById(id).orElse(null);
            if (null == r.getReadTime()) {
                r.setReadTime(new Date());
                reminderRepository.save(r);
            }
        }
    }
}
