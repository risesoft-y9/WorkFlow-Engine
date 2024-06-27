package net.risesoft.service.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.entity.EntrustDetail;
import net.risesoft.entity.Opinion;
import net.risesoft.entity.ProcessParam;
import net.risesoft.entity.ProcessTrack;
import net.risesoft.entity.TransactionHistoryWord;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.HistoryProcessModel;
import net.risesoft.model.platform.Person;
import net.risesoft.model.processadmin.HistoricTaskInstanceModel;
import net.risesoft.model.processadmin.HistoricVariableInstanceModel;
import net.risesoft.model.processadmin.IdentityLinkModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.nosql.elastic.entity.OfficeDoneInfo;
import net.risesoft.repository.jpa.OpinionRepository;
import net.risesoft.repository.jpa.ProcessTrackRepository;
import net.risesoft.service.EntrustDetailService;
import net.risesoft.service.OfficeDoneInfoService;
import net.risesoft.service.ProcessParamService;
import net.risesoft.service.ProcessTrackService;
import net.risesoft.service.TransactionHistoryWordService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9Util;

import y9.client.rest.processadmin.HistoricTaskApiClient;
import y9.client.rest.processadmin.HistoricVariableApiClient;
import y9.client.rest.processadmin.IdentityApiClient;
import y9.client.rest.processadmin.TaskApiClient;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Service(value = "processTrackService")
public class ProcessTrackServiceImpl implements ProcessTrackService {

    private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private ProcessTrackRepository processTrackRepository;

    @Autowired
    private OpinionRepository opinionRepository;

    @Autowired
    private TransactionHistoryWordService transactionHistoryWordService;

    @Autowired
    private HistoricVariableApiClient historicVariableManager;

    @Autowired
    private PersonApi personManager;

    @Autowired
    private HistoricTaskApiClient historicTaskManager;

    @Autowired
    private TaskApiClient taskManager;

    @Autowired
    private IdentityApiClient identityManager;

    @Autowired
    private EntrustDetailService entrustDetailService;

    @Autowired
    private OfficeDoneInfoService officeDoneInfoService;

    @Autowired
    private ProcessParamService processParamService;

    @Override
    @Transactional(readOnly = false)
    public void deleteById(String id) {
        processTrackRepository.deleteById(id);
    }

    @Override
    public List<ProcessTrack> findByTaskId(String taskId) {
        return processTrackRepository.findByTaskId(taskId);
    }

    @Override
    public List<ProcessTrack> findByTaskIdAndEndTimeIsNull(String taskId) {
        return processTrackRepository.findByTaskIdAndEndTimeIsNull(taskId, "");
    }

    @Override
    public List<ProcessTrack> findByTaskIdAsc(String taskId) {
        return processTrackRepository.findByTaskIdAsc(taskId);
    }

    @Override
    public ProcessTrack findOne(String id) {
        return processTrackRepository.findById(id).orElse(null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<HistoryProcessModel> getListMap(String processInstanceId) {
        List<HistoryProcessModel> items = new ArrayList<>();
        String tenantId = Y9LoginUserHolder.getTenantId();
        // 由于需要获取call Activity类型的节点，将查询方法改为如下
        List<HistoricTaskInstanceModel> results =
            historicTaskManager.getByProcessInstanceId(tenantId, processInstanceId, "");
        String year = "";
        if (results == null || results.isEmpty()) {
            OfficeDoneInfo officeDoneInfoModel = officeDoneInfoService.findByProcessInstanceId(processInstanceId);
            if (officeDoneInfoModel != null && officeDoneInfoModel.getProcessInstanceId() != null) {
                year = officeDoneInfoModel.getStartTime().substring(0, 4);
                results = historicTaskManager.getByProcessInstanceId(tenantId, processInstanceId, year);
            } else {
                ProcessParam processParam = processParamService.findByProcessInstanceId(processInstanceId);
                year = processParam != null ? processParam.getCreateTime().substring(0, 4) : "";
                results = historicTaskManager.getByProcessInstanceId(tenantId, processInstanceId, year);
            }
        }
        for (int i = 0; i < results.size(); i++) {
            HistoricTaskInstanceModel hai = results.get(i);
            if (hai == null) {
                continue;
            }
            String id = hai.getId(), taskId = hai.getId();
            HistoryProcessModel model = new HistoryProcessModel();
            model.setId(id);
            // 收件人
            model.setAssignee("");
            // 任务名称
            model.setName(hai.getName());
            // 描述
            model.setDescription("");
            // 意见
            model.setOpinion("");
            // 历史正文版本
            TransactionHistoryWord hword = transactionHistoryWordService.getTransactionHistoryWordByTaskId(taskId);
            if (null != hword) {
                model.setHistoryVersion(hword.getVersion());
            }
            model.setTaskId(taskId);
            // 收件人
            String assignee = hai.getAssignee();
            if (StringUtils.isNotBlank(assignee)) {
                Person employee = personManager.get(Y9LoginUserHolder.getTenantId(), assignee).getData();
                if (employee != null) {
                    model.setAssigneeId(assignee);
                    // 承办人id,用于数据中心保存
                    model.setUndertakerId(assignee);
                    String ownerId = hai.getOwner();
                    String employeeName = employee.getName();
                    // 恢复待办，如不是办结人恢复，Owner有值，需显示Owner
                    if (StringUtils.isNotBlank(ownerId)) {
                        Person ownerUser = personManager.get(Y9LoginUserHolder.getTenantId(), ownerId).getData();
                        employeeName =
                            ownerUser.getName() + (Boolean.TRUE.equals(ownerUser.getDisabled()) ? "(已禁用)" : "");
                        model.setUndertakerId(ownerUser.getId());
                    }
                    EntrustDetail entrustDetail = entrustDetailService.findByTaskId(taskId);
                    // 出差委托标识
                    if (entrustDetail != null) {
                        String owner4Entrust = entrustDetail.getOwnerId();
                        Person owner = personManager.get(tenantId, owner4Entrust).getData();
                        employeeName = employeeName + "(" + owner.getName()
                            + (Boolean.TRUE.equals(owner.getDisabled()) ? "(已禁用)" : "") + "委托)";
                    }
                    HistoricVariableInstanceModel zhuBan = null;
                    try {
                        zhuBan = historicVariableManager.getByTaskIdAndVariableName(tenantId, taskId,
                            SysVariables.PARALLELSPONSOR, year);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (zhuBan != null) {
                        model.setAssignee(employeeName + "(主办)");
                    } else {
                        model.setAssignee(employeeName);
                    }
                }
            } else {// 处理单实例未签收的办理人显示
                List<IdentityLinkModel> iList = identityManager.getIdentityLinksForTask(tenantId, taskId);
                if (!iList.isEmpty()) {
                    StringBuilder assignees = new StringBuilder();
                    int j = 0;
                    for (IdentityLinkModel identityLink : iList) {
                        String assigneeId = identityLink.getUserId();
                        Person ownerUser = personManager.get(Y9LoginUserHolder.getTenantId(), assigneeId).getData();
                        if (j < 5) {
                            assignees = Y9Util.genCustomStr(assignees,
                                ownerUser.getName() + (Boolean.TRUE.equals(ownerUser.getDisabled()) ? "(已禁用)" : ""),
                                "、");

                        } else {
                            assignees.append("等，共" + iList.size() + "人");
                            break;
                        }
                        j++;
                    }
                    model.setAssignee(assignees.toString());
                }
            }
            Integer newToDo = 0;
            if (hai.getEndTime() == null) {
                TaskModel taskModel = taskManager.findById(tenantId, taskId);
                newToDo = StringUtils.isBlank(taskModel.getFormKey()) ? 1 : (Integer.parseInt(taskModel.getFormKey()));
            }
            model.setNewToDo(newToDo);

            // 是否被强制办结任务标识
            model.setEndFlag(StringUtils.isBlank(hai.getTenantId()) ? "" : hai.getTenantId());
            // 描述
            String description = hai.getDeleteReason();
            if (null != description && !("MI_END".equals(description))) {
                model.setDescription(description);
                if (description.contains("Delete MI execution")) {
                    HistoricVariableInstanceModel taskSenderModel = historicVariableManager
                        .getByTaskIdAndVariableName(tenantId, hai.getId(), SysVariables.TASKSENDER, year);
                    if (taskSenderModel != null) {
                        String taskSender =
                            taskSenderModel.getValue() == null ? "" : (String)taskSenderModel.getValue();
                        model.setDescription("该任务由" + taskSender + "删除");
                        // 并行退回以减签的方式退回，需获取退回原因,替换减签的描述
                        HistoricVariableInstanceModel rollBackReason = historicVariableManager
                            .getByTaskIdAndVariableName(tenantId, hai.getId(), "rollBackReason", year);
                        if (rollBackReason != null) {
                            model.setDescription(rollBackReason.getValue());
                        }
                        // 发送办结协办任务使用减签方式办结，需要设置description为空
                        if (StringUtils.isNotBlank(hai.getTenantId())) {
                            model.setDescription("");
                        }
                    }
                }
            }
            // 意见
            List<Opinion> opinion = opinionRepository.findByTaskIdAndUserIdAndProcessTrackIdIsNull(taskId,
                (assignee == null || assignee == "") ? "" : assignee);
            model.setOpinion(!opinion.isEmpty() ? opinion.get(0).getContent() : "");
            model.setStartTime(hai.getStartTime() == null ? "" : DATE_FORMAT.format(hai.getStartTime()));
            model.setStartTimes(hai.getStartTime() == null ? 0 : hai.getStartTime().getTime());
            /**
             * 手动设置流程办结的时候, 流程最后一个任务结束的时间就是第一个手动设置的流程跟踪的时间
             */
            List<ProcessTrack> ptList = new ArrayList<>();
            Date endTime1 = hai.getEndTime();
            ptList = this.findByTaskId(taskId);
            if (ptList.size() >= 1) {
                model.setEndTime(endTime1 == null ? "" : DATE_FORMAT.format(endTime1));
                model.setEndTimes(endTime1 == null ? 0 : endTime1.getTime());
                model.setTime(longTime(hai.getStartTime(), endTime1));
            } else {
                model.setEndTime(endTime1 == null ? "" : DATE_FORMAT.format(endTime1));
                model.setEndTimes(endTime1 == null ? 0 : endTime1.getTime());
                model.setTime(longTime(hai.getStartTime(), endTime1));
            }
            items.add(model);

            for (ProcessTrack pt : ptList) {
                HistoryProcessModel modelTrack = new HistoryProcessModel();
                modelTrack.setId(id);
                modelTrack.setAssignee(pt.getReceiverName() == null ? "" : pt.getReceiverName());
                modelTrack.setName(pt.getTaskDefName() == null ? "" : pt.getTaskDefName());
                modelTrack.setDescription(pt.getDescribed() == null ? "" : pt.getDescribed());
                List<Opinion> opinionProcessTrack =
                    opinionRepository.findByTaskIdAndProcessTrackIdOrderByCreateDateDesc(taskId, pt.getId());
                modelTrack.setOpinion(opinionProcessTrack.isEmpty() ? "" : opinionProcessTrack.get(0).getContent());
                modelTrack.setHistoryVersion(pt.getDocVersion() == null ? null : pt.getDocVersion());
                modelTrack.setTaskId(taskId);
                modelTrack.setIsChaoSong(pt.getIsChaoSong() != null && pt.getIsChaoSong());

                modelTrack.setStartTime(pt.getStartTime() == null ? "" : pt.getStartTime());
                modelTrack.setEndTime(pt.getEndTime() == null ? "" : pt.getEndTime());

                try {
                    modelTrack.setStartTimes(DATE_FORMAT.parse(pt.getStartTime()).getTime());
                    modelTrack.setEndTimes(
                        StringUtils.isBlank(pt.getEndTime()) ? 0 : DATE_FORMAT.parse(pt.getEndTime()).getTime());
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                try {
                    if (StringUtils.isBlank(pt.getEndTime())) {
                        modelTrack.setTime("");
                    } else {
                        modelTrack.setTime(
                            longTime(DATE_FORMAT.parse(pt.getStartTime()), DATE_FORMAT.parse(pt.getEndTime())));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                items.add(modelTrack);
            }
        }
        Collections.sort(items);
        String name = items.get(items.size() - 1).getName();
        boolean b = "串行办理".equals(name);
        if (b) {
            HistoricVariableInstanceModel users = historicVariableManager
                .getByProcessInstanceIdAndVariableName(tenantId, processInstanceId, SysVariables.USERS, "");
            List<String> list = users != null ? (ArrayList<String>)users.getValue() : new ArrayList<String>();
            boolean start = false;
            String assigneeId = items.get(items.size() - 1).getAssigneeId();
            for (Object obj : list) {
                String user = obj.toString();
                if (StringUtils.isNotBlank(assigneeId)) {
                    if (user.contains(assigneeId)) {
                        start = true;
                        continue;
                    }
                    if (start) {
                        Person employee = personManager.get(Y9LoginUserHolder.getTenantId(), user).getData();

                        HistoryProcessModel history = new HistoryProcessModel();
                        history.setAssignee(employee.getName());
                        history.setName("串行办理");
                        history.setDescription("");
                        history.setOpinion("");
                        history.setStartTime("未开始");
                        history.setEndTime("");
                        history.setTime("");

                        items.add(history);
                    }
                }
            }
        }
        return items;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<HistoryProcessModel> getListMap4Simple(String processInstanceId) {
        List<HistoryProcessModel> items = new ArrayList<>();
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<HistoricTaskInstanceModel> results =
            historicTaskManager.getByProcessInstanceId(tenantId, processInstanceId, "");
        String year = "";
        if (results == null || results.isEmpty()) {
            OfficeDoneInfo officeDoneInfoModel = officeDoneInfoService.findByProcessInstanceId(processInstanceId);
            if (officeDoneInfoModel != null && officeDoneInfoModel.getProcessInstanceId() != null) {
                year = officeDoneInfoModel.getStartTime().substring(0, 4);
                results = historicTaskManager.getByProcessInstanceId(tenantId, processInstanceId, year);
            } else {
                ProcessParam processParam = processParamService.findByProcessInstanceId(processInstanceId);
                year = processParam != null ? processParam.getCreateTime().substring(0, 4) : "";
                results = historicTaskManager.getByProcessInstanceId(tenantId, processInstanceId, year);
            }
        }
        for (int i = 0; i < results.size(); i++) {
            HistoricTaskInstanceModel hai = results.get(i);
            if (hai == null) {
                continue;
            }
            String taskId = hai.getId();
            HistoryProcessModel history = new HistoryProcessModel();
            // 收件人
            history.setAssignee("");
            // 任务名称
            history.setName(hai.getName());
            // 收件人
            String assignee = hai.getAssignee();
            if (StringUtils.isNotBlank(assignee)) {
                Person employee = personManager.get(Y9LoginUserHolder.getTenantId(), assignee).getData();
                String ownerId = hai.getOwner();
                String employeeName = employee.getName();
                // 恢复待办，如不是办结人恢复，Owner有值，需显示Owner
                if (StringUtils.isNotBlank(ownerId)) {
                    Person ownerUser = personManager.get(Y9LoginUserHolder.getTenantId(), ownerId).getData();
                    employeeName = ownerUser.getName() + (Boolean.TRUE.equals(ownerUser.getDisabled()) ? "(已禁用)" : "");
                }
                if (employee != null) {
                    HistoricVariableInstanceModel zhuBan = historicVariableManager.getByTaskIdAndVariableName(tenantId,
                        taskId, SysVariables.PARALLELSPONSOR, year);
                    if (zhuBan != null) {
                        history.setAssignee(employeeName + "(主办)");
                    } else {
                        history.setAssignee(employeeName);
                    }
                    history.setAssigneeId(assignee);
                }
            } else {// 处理单实例未签收的办理人显示
                List<IdentityLinkModel> iList = identityManager.getIdentityLinksForTask(tenantId, taskId);
                if (!iList.isEmpty()) {
                    StringBuilder assignees = new StringBuilder();
                    int j = 0;
                    for (IdentityLinkModel identityLink : iList) {
                        String assigneeId = identityLink.getUserId();
                        Person ownerUser = personManager.get(Y9LoginUserHolder.getTenantId(), assigneeId).getData();
                        if (j < 5) {
                            assignees = Y9Util.genCustomStr(assignees,
                                ownerUser.getName() + (Boolean.TRUE.equals(ownerUser.getDisabled()) ? "(已禁用)" : ""),
                                "、");
                        } else {
                            assignees.append("等，共" + iList.size() + "人");
                            break;
                        }
                        j++;
                    }
                    history.setAssignee(assignees.toString());
                }
            }
            history.setStartTime(hai.getStartTime() == null ? "" : DATE_FORMAT.format(hai.getStartTime()));
            // 是否被强制办结任务标识
            history.setEndFlag(StringUtils.isBlank(hai.getTenantId()) ? "" : hai.getTenantId());
            /**
             * 手动设置流程办结的时候, 流程最后一个任务结束的时间就是第一个手动设置的流程跟踪的时间
             */
            List<ProcessTrack> ptList = new ArrayList<>();
            Date endTime1 = hai.getEndTime();
            ptList = this.findByTaskId(taskId);
            if (ptList.size() >= 1) {
                history.setEndTime(endTime1 == null ? "" : DATE_FORMAT.format(endTime1));
                history.setTime(longTime(hai.getStartTime(), endTime1));
            } else {
                history.setEndTime(endTime1 == null ? "" : DATE_FORMAT.format(endTime1));
                history.setTime(longTime(hai.getStartTime(), endTime1));
            }
            items.add(history);

            for (ProcessTrack pt : ptList) {
                HistoryProcessModel process = new HistoryProcessModel();
                process.setAssignee(pt.getReceiverName() == null ? "" : pt.getReceiverName());
                process.setName(pt.getTaskDefName() == null ? "" : pt.getTaskDefName());
                process.setStartTime(pt.getStartTime() == null ? "" : pt.getStartTime());
                process.setEndTime(pt.getEndTime() == null ? "" : pt.getEndTime());
                try {
                    if (StringUtils.isBlank(pt.getEndTime())) {
                        process.setTime("");
                    } else {
                        process.setTime(
                            longTime(DATE_FORMAT.parse(pt.getStartTime()), DATE_FORMAT.parse(pt.getEndTime())));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                items.add(process);
            }
        }
        Collections.sort(items);
        String name = items.get(items.size() - 1).getName();
        boolean b = "串行办理".equals(name);
        if (b) {
            HistoricVariableInstanceModel users = historicVariableManager
                .getByProcessInstanceIdAndVariableName(tenantId, processInstanceId, SysVariables.USERS, "");
            List<String> list = users != null ? (ArrayList<String>)users.getValue() : new ArrayList<>();
            boolean start = false;
            String assigneeId = items.get(items.size() - 1).getAssigneeId();
            for (Object obj : list) {
                String user = obj.toString();
                if (StringUtils.isNotBlank(assigneeId)) {
                    if (user.contains(assigneeId)) {
                        start = true;
                        continue;
                    }
                    if (start) {
                        Person employee = personManager.get(Y9LoginUserHolder.getTenantId(), user).getData();
                        HistoryProcessModel history2 = new HistoryProcessModel();
                        history2.setAssignee(employee.getName());
                        history2.setName("串行办理");
                        history2.setDescription("");
                        history2.setOpinion("");
                        history2.setStartTime("未开始");
                        history2.setEndTime("");
                        history2.setTime("");
                        items.add(history2);
                    }
                }
            }
        }
        return items;
    }

    private String longTime(Date startTime, Date endTime) {
        if (endTime == null) {
            return "";
        } else {
            Date d1 = endTime;
            Date d2 = startTime;
            long time = d1.getTime() - d2.getTime();
            time = time / 1000;
            int s = (int)(time % 60);
            int m = (int)(time / 60 % 60);
            int h = (int)(time / 3600 % 24);
            int d = (int)(time / 86400);
            String str = d + "天" + h + "小时" + m + "分" + s + "秒";
            return str;
        }
    }

    @Override
    @Transactional(readOnly = false)
    public ProcessTrack saveOrUpdate(ProcessTrack pt) {
        String id = pt.getId();
        if (StringUtils.isNotEmpty(id)) {
            ProcessTrack oldpt = processTrackRepository.findById(id).orElse(null);
            oldpt.setEndTime(DATE_FORMAT.format(new Date()));
            oldpt.setDescribed(pt.getDescribed());
            return processTrackRepository.save(oldpt);
        }
        ProcessTrack newpt = new ProcessTrack();
        newpt.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        newpt.setProcessInstanceId(pt.getProcessInstanceId());
        newpt.setTaskId(pt.getTaskId());
        newpt.setTaskDefName(pt.getTaskDefName());
        newpt.setSenderName(pt.getSenderName());
        newpt.setReceiverName(pt.getReceiverName());
        newpt.setTaskDefName(pt.getTaskDefName());
        newpt.setStartTime(pt.getStartTime());
        newpt.setEndTime(pt.getEndTime());
        newpt.setDescribed(pt.getDescribed());
        processTrackRepository.save(newpt);
        return newpt;
    }
}
