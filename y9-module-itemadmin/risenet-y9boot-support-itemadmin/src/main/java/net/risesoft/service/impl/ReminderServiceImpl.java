package net.risesoft.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.Y9FlowableHolder;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.processadmin.HistoricTaskApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.consts.ItemConsts;
import net.risesoft.consts.processadmin.SysVariables;
import net.risesoft.entity.Reminder;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.ReminderModel;
import net.risesoft.model.platform.org.OrgUnit;
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
        Arrays.stream(ids).forEach(id -> {
            Optional<Reminder> reminderOptional = reminderRepository.findById(id);
            reminderOptional.ifPresent(reminderRepository::delete);
        });
    }

    @Override
    public Reminder findById(String id) {
        return reminderRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public String handleReminder(String msgContent, String procInstId, Integer reminderAutomatic, String remType,
        String taskId, String documentTitle) {
        OrgUnit person = Y9FlowableHolder.getOrgUnit();
        String[] procInstIds = procInstId.split(SysVariables.COMMA);
        String[] taskIds = taskId.split(SysVariables.COMMA);
        List<Reminder> list = new ArrayList<>();
        for (int i = 0; i < procInstIds.length; i++) {
            Reminder reminder = new Reminder();
            reminder.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            reminder.setMsgContent(msgContent);
            reminder.setProcInstId(procInstIds[i]);
            reminder.setSenderId(person.getId());
            reminder.setSenderName(person.getName());
            reminder.setTaskId(taskIds[i]);
            reminder.setCreateTime(new Date());
            reminder.setModifyTime(new Date());
            list.add(reminder);
        }
        reminderRepository.saveAll(list);
        return "";
    }

    @Override
    public Y9Page<ReminderModel> pageByProcessInstanceId(String processInstanceId, int page, int rows) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        PageRequest pageable = createPageRequest(page, rows);
        Page<Reminder> reminderPage = reminderRepository.findByProcInstId(processInstanceId, pageable);

        List<ReminderModel> reminderModels = convertToReminderModels(reminderPage.getContent(), tenantId);

        return Y9Page.success(page, reminderPage.getTotalPages(), reminderPage.getTotalElements(), reminderModels);
    }

    /**
     * 创建分页请求对象
     */
    private PageRequest createPageRequest(int page, int rows) {
        Sort sort = Sort.by(Sort.Direction.DESC, ItemConsts.CREATETIME_KEY);
        return PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
    }

    /**
     * 将Reminder列表转换为ReminderModel列表
     */
    private List<ReminderModel> convertToReminderModels(List<Reminder> reminders, String tenantId) {
        List<ReminderModel> models = new ArrayList<>();

        for (int i = 0; i < reminders.size(); i++) {
            Reminder reminder = reminders.get(i);
            ReminderModel model = new ReminderModel();

            // 基本信息设置
            populateBasicInfo(model, reminder, i);

            // 任务相关信息设置
            populateTaskInfo(model, reminder, tenantId);

            models.add(model);
        }

        return models;
    }

    /**
     * 填充提醒基本信息
     */
    private void populateBasicInfo(ReminderModel model, Reminder reminder, int index) {
        model.setId(reminder.getId());
        model.setMsgContent(reminder.getMsgContent());
        model.setCreateTime(DATE_TIME_FORMAT.format(reminder.getCreateTime()));
        model.setReadTime(formatReadTime(reminder.getReadTime()));
        model.setSenderName(reminder.getSenderName());
        model.setUserName("无");
        model.setTaskName("无");
        model.setSerialNumber(index + 1);
    }

    /**
     * 格式化阅读时间
     */
    private String formatReadTime(Date readTime) {
        return readTime == null ? "" : DATE_TIME_FORMAT.format(readTime);
    }

    /**
     * 填充任务相关信息
     */
    private void populateTaskInfo(ReminderModel model, Reminder reminder, String tenantId) {
        try {
            HistoricTaskInstanceModel historicTask = historictaskApi.getById(tenantId, reminder.getTaskId()).getData();
            if (historicTask != null) {
                model.setTaskName(historicTask.getName());
                setAssigneeInfo(model, historicTask.getAssignee(), tenantId);
            }
        } catch (Exception e) {
            LOGGER.warn("获取历史任务信息失败，taskId: {}", reminder.getTaskId(), e);
        }
    }

    /**
     * 设置任务处理人信息
     */
    private void setAssigneeInfo(ReminderModel model, String assignee, String tenantId) {
        if (StringUtils.isNotBlank(assignee)) {
            try {
                OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, assignee).getData();
                if (orgUnit != null) {
                    String userName = orgUnit.getName();
                    if (Boolean.TRUE.equals(orgUnit.getDisabled())) {
                        userName += "(已禁用)";
                    }
                    model.setUserName(userName);
                }
            } catch (Exception e) {
                LOGGER.warn("获取处理人信息失败，assignee: {}", assignee, e);
            }
        }
    }

    @Override
    public Y9Page<ReminderModel> pageBySenderIdAndProcessInstanceIdAndActive(String senderId, String processInstanceId,
        int page, int rows) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        PageRequest pageable = createPageRequest(page, rows);

        // 获取任务ID列表
        List<String> taskIds = getTaskIds(tenantId, processInstanceId);

        // 分页查询提醒信息
        Page<Reminder> reminderPage = reminderRepository.findBySenderIdAndTaskIdIn(senderId, taskIds, pageable);

        // 转换为ReminderModel列表
        List<ReminderModel> reminderModels =
            convertToActiveReminderModels(reminderPage.getContent(), tenantId, page, rows);

        return Y9Page.success(page, reminderPage.getTotalPages(), reminderPage.getTotalElements(), reminderModels);
    }

    /**
     * 获取流程实例的任务ID列表
     */
    private List<String> getTaskIds(String tenantId, String processInstanceId) {
        List<String> taskIds = new ArrayList<>();
        try {
            List<TaskModel> taskList = taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
            for (TaskModel task : taskList) {
                taskIds.add(task.getId());
            }
        } catch (Exception e) {
            LOGGER.warn("获取任务列表失败，processInstanceId: {}", processInstanceId, e);
        }
        return taskIds;
    }

    /**
     * 将Reminder列表转换为活跃状态的ReminderModel列表
     */
    private List<ReminderModel> convertToActiveReminderModels(List<Reminder> reminders, String tenantId, int page,
        int rows) {
        List<ReminderModel> models = new ArrayList<>();
        int startIndex = (page - 1) * rows;

        for (int i = 0; i < reminders.size(); i++) {
            Reminder reminder = reminders.get(i);
            ReminderModel model = new ReminderModel();

            // 基本信息设置
            populateBasicInfo(model, reminder, startIndex + i);

            // 活跃任务相关信息设置
            populateActiveTaskInfo(model, reminder, tenantId);

            models.add(model);
        }

        return models;
    }

    /**
     * 填充活跃任务相关信息
     */
    private void populateActiveTaskInfo(ReminderModel model, Reminder reminder, String tenantId) {
        try {
            TaskModel task = taskApi.findById(tenantId, reminder.getTaskId()).getData();
            if (task != null) {
                model.setTaskName(task.getName());
                setAssigneeInfo(model, task.getAssignee(), tenantId);
            }
        } catch (Exception e) {
            LOGGER.warn("获取任务信息失败，taskId: {}", reminder.getTaskId(), e);
        }
    }

    @Override
    public Y9Page<ReminderModel> pageByTaskId(String taskId, int page, int rows) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Sort sort = Sort.by(Sort.Direction.DESC, ItemConsts.CREATETIME_KEY);
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
            model.setReadTime(null == reminder.getReadTime() ? "" : DATE_TIME_FORMAT.format(reminder.getReadTime()));
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
        OrgUnit orgUnit = Y9FlowableHolder.getOrgUnit();
        Reminder r = new Reminder();
        r.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        r.setCreateTime(new Date());
        r.setModifyTime(new Date());
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
    public void setReadTime(String[] ids) {
        Arrays.stream(ids).forEach(id -> {
            Optional<Reminder> reminderOptional = reminderRepository.findById(id);
            if (reminderOptional.isPresent()) {
                Reminder reminder = reminderOptional.get();
                reminder.setReadTime(new Date());
                reminderRepository.save(reminder);
            }
        });
    }
}
