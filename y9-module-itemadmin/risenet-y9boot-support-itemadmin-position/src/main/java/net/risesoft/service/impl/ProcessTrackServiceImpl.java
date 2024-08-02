package net.risesoft.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.processadmin.HistoricActivityApi;
import net.risesoft.api.processadmin.HistoricTaskApi;
import net.risesoft.api.processadmin.HistoricVariableApi;
import net.risesoft.api.processadmin.IdentityApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.entity.Opinion;
import net.risesoft.entity.ProcessParam;
import net.risesoft.entity.ProcessTrack;
import net.risesoft.entity.TransactionHistoryWord;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.HistoricActivityInstanceModel;
import net.risesoft.model.itemadmin.HistoryProcessModel;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.processadmin.HistoricTaskInstanceModel;
import net.risesoft.model.processadmin.HistoricVariableInstanceModel;
import net.risesoft.model.processadmin.IdentityLinkModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.nosql.elastic.entity.OfficeDoneInfo;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.jpa.OpinionRepository;
import net.risesoft.repository.jpa.ProcessTrackRepository;
import net.risesoft.service.OfficeDoneInfoService;
import net.risesoft.service.ProcessParamService;
import net.risesoft.service.ProcessTrackService;
import net.risesoft.service.TransactionHistoryWordService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;
import net.risesoft.y9.util.Y9Util;

/*
 * @author qinman
 *
 * @author zhangchongjie
 *
 * @date 2022/12/20
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class ProcessTrackServiceImpl implements ProcessTrackService {

    private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

    private final ProcessTrackRepository processTrackRepository;

    private final OpinionRepository opinionRepository;

    private final TransactionHistoryWordService transactionHistoryWordService;

    private final HistoricVariableApi historicVariableApi;

    private final OrgUnitApi orgUnitApi;

    private final HistoricTaskApi historicTaskManager;

    private final TaskApi taskManager;

    private final IdentityApi identityManager;

    private final OfficeDoneInfoService officeDoneInfoService;

    private final ProcessParamService processParamService;

    private final HistoricActivityApi historicActivityApi;

    @Override
    @Transactional
    public void deleteById(String id) {
        processTrackRepository.deleteById(id);
    }

    @Override
    public ProcessTrack findOne(String id) {
        return processTrackRepository.findById(id).orElse(null);
    }

    @Override
    public Y9Result<List<HistoricActivityInstanceModel>> getTaskList(String processInstanceId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<HistoricActivityInstanceModel> list = new ArrayList<>();
        try {
            List<net.risesoft.model.processadmin.HistoricActivityInstanceModel> list1 =
                historicActivityApi.getByProcessInstanceIdAndYear(tenantId, processInstanceId, "").getData();
            String year = "";
            if (list1 == null || list1.isEmpty()) {
                OfficeDoneInfo info = officeDoneInfoService.findByProcessInstanceId(processInstanceId);
                year = info.getStartTime().substring(0, 4);
                list1 = historicActivityApi.getByProcessInstanceIdAndYear(tenantId, processInstanceId, year).getData();
            }
            for (net.risesoft.model.processadmin.HistoricActivityInstanceModel task : list1) {
                String assignee = task.getAssignee();
                task.setExecutionId("");
                task.setCalledProcessInstanceId("");
                if (assignee != null) {
                    // 意见
                    List<Opinion> opinion = opinionRepository.findByTaskIdAndPositionIdAndProcessTrackIdIsNull(
                        task.getTaskId(), StringUtils.isBlank(assignee) ? "" : assignee);
                    task.setTenantId(!opinion.isEmpty() ? opinion.get(0).getContent() : "");
                    OrgUnit employee =
                        orgUnitApi.getOrgUnitPersonOrPosition(Y9LoginUserHolder.getTenantId(), assignee).getData();
                    if (employee != null) {
                        String employeeName = employee.getName();
                        HistoricVariableInstanceModel zhuBan = null;
                        try {
                            zhuBan = historicVariableApi.getByTaskIdAndVariableName(tenantId, task.getTaskId(),
                                SysVariables.PARALLELSPONSOR, year).getData();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (zhuBan != null) {// 办理人
                            task.setCalledProcessInstanceId(employeeName + "(主办)");
                        } else {
                            task.setCalledProcessInstanceId(employeeName);
                        }
                    }
                    if (task.getStartTime() != null && task.getEndTime() != null) {// 办理时长
                        task.setExecutionId(longTime(task.getStartTime(), task.getEndTime()));
                    }
                }
                HistoricActivityInstanceModel task1 = new HistoricActivityInstanceModel();
                Y9BeanUtil.copyProperties(task, task1);
                list.add(task1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.success(list, "获取成功");
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<HistoryProcessModel> listByProcessInstanceId(String processInstanceId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<HistoryProcessModel> items = new ArrayList<>();
        String tenantId = Y9LoginUserHolder.getTenantId();
        // 由于需要获取call Activity类型的节点，将查询方法改为如下
        List<HistoricTaskInstanceModel> results =
            historicTaskManager.getByProcessInstanceId(tenantId, processInstanceId, "").getData();
        String year = "";
        if (results == null || results.isEmpty()) {
            OfficeDoneInfo officeDoneInfoModel = officeDoneInfoService.findByProcessInstanceId(processInstanceId);
            if (officeDoneInfoModel != null && officeDoneInfoModel.getProcessInstanceId() != null) {
                year = officeDoneInfoModel.getStartTime().substring(0, 4);
                results = historicTaskManager.getByProcessInstanceId(tenantId, processInstanceId, year).getData();
            } else {
                ProcessParam processParam = processParamService.findByProcessInstanceId(processInstanceId);
                year = processParam != null ? processParam.getCreateTime().substring(0, 4) : "";
                results = historicTaskManager.getByProcessInstanceId(tenantId, processInstanceId, year).getData();
            }
        }
        for (int i = 0; i < results.size(); i++) {
            HistoricTaskInstanceModel hai = results.get(i);
            if (hai == null) {
                continue;
            }
            String id = hai.getId();
            String taskId = hai.getId();

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
                OrgUnit employee =
                    orgUnitApi.getOrgUnitPersonOrPosition(Y9LoginUserHolder.getTenantId(), assignee).getData();
                if (employee != null) {

                    model.setAssigneeId(assignee);
                    // 承办人id,用于数据中心保存
                    model.setUndertakerId(assignee);

                    String ownerId = hai.getOwner();
                    String employeeName = employee.getName();
                    // 恢复待办，如不是办结人恢复，Owner有值，需显示Owner
                    if (StringUtils.isNotBlank(ownerId)) {
                        OrgUnit ownerUser =
                            orgUnitApi.getOrgUnitPersonOrPosition(Y9LoginUserHolder.getTenantId(), ownerId).getData();
                        employeeName = ownerUser.getName();
                        model.setUndertakerId(ownerUser.getId());
                    }
                    /*EntrustDetail entrustDetail = entrustDetailService.findByTaskId(taskId);
                    // 出差委托标识
                    if (entrustDetail != null) {
                        String owner4Entrust = entrustDetail.getOwnerId();
                        Position owner = positionApi.getPosition(tenantId, owner4Entrust);
                        employeeName = employeeName + "(" + owner.getName() + "委托)";
                    }*/
                    HistoricVariableInstanceModel zhuBan = null;
                    try {
                        zhuBan = historicVariableApi
                            .getByTaskIdAndVariableName(tenantId, taskId, SysVariables.PARALLELSPONSOR, year).getData();
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
                List<IdentityLinkModel> iList = null;
                try {
                    iList = identityManager.getIdentityLinksForTask(tenantId, taskId).getData();
                } catch (Exception e) {
                    LOGGER.error("获取任务的用户信息失败", e);
                }
                if (null != iList && !iList.isEmpty()) {
                    StringBuilder assignees = new StringBuilder();
                    int j = 0;
                    for (IdentityLinkModel identityLink : iList) {
                        String assigneeId = identityLink.getUserId();
                        OrgUnit ownerUser = orgUnitApi
                            .getOrgUnitPersonOrPosition(Y9LoginUserHolder.getTenantId(), assigneeId).getData();
                        if (j < 5) {
                            assignees = Y9Util.genCustomStr(assignees, ownerUser.getName(), "、");
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
                TaskModel taskModel = taskManager.findById(tenantId, taskId).getData();
                newToDo = (taskModel == null || StringUtils.isBlank(taskModel.getFormKey())) ? 1
                    : (Integer.parseInt(taskModel.getFormKey()));
            }

            model.setNewToDo(newToDo);

            // 是否被强制办结任务标识
            model.setEndFlag(StringUtils.isBlank(hai.getTenantId()) ? "" : hai.getTenantId());

            // 描述
            String description = hai.getDeleteReason();
            if (null != description && !(description.equals("MI_END"))) {
                model.setDescription(description);
                if (description.contains("Delete MI execution")) {
                    HistoricVariableInstanceModel taskSenderModel = historicVariableApi
                        .getByTaskIdAndVariableName(tenantId, hai.getId(), SysVariables.TASKSENDER, year).getData();
                    if (taskSenderModel != null) {
                        String taskSender =
                            taskSenderModel.getValue() == null ? "" : (String)taskSenderModel.getValue();
                        model.setDescription("该任务由" + taskSender + "删除");
                        // 并行退回以减签的方式退回，需获取退回原因,替换减签的描述
                        HistoricVariableInstanceModel rollBackReason = historicVariableApi
                            .getByTaskIdAndVariableName(tenantId, hai.getId(), "rollBackReason", year).getData();
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
            List<Opinion> opinion = opinionRepository.findByTaskIdAndPositionIdAndProcessTrackIdIsNull(taskId,
                StringUtils.isBlank(assignee) ? "" : assignee);
            model.setStartTime(hai.getStartTime() == null ? "" : sdf.format(hai.getStartTime()));
            model.setOpinion(!opinion.isEmpty() ? opinion.get(0).getContent() : "");

            try {
                model.setStartTimes(
                    hai.getStartTime() == null ? 0 : sdf.parse(DATE_FORMAT.format(hai.getStartTime())).getTime());
            } catch (Exception e2) {
                e2.printStackTrace();
            }

            /*
             * 手动设置流程办结的时候, 流程最后一个任务结束的时间就是第一个手动设置的流程跟踪的时间
             */
            Date endTime1 = hai.getEndTime();
            List<ProcessTrack> ptList = this.listByTaskId(taskId);
            if (ptList.size() >= 1) {
                model.setEndTime(endTime1 == null ? "" : DATE_FORMAT.format(endTime1));
                try {
                    model.setEndTimes(endTime1 == null ? 0 : DATE_FORMAT.parse(sdf.format(endTime1)).getTime());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                model.setTime(longTime(hai.getStartTime(), endTime1));
            } else {
                model.setEndTime(endTime1 == null ? "" : DATE_FORMAT.format(endTime1));
                try {
                    model.setEndTimes(endTime1 == null ? 0 : DATE_FORMAT.parse(sdf.format(endTime1)).getTime());
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
        String seq = "串行办理";
        if (seq.equals(name)) {
            HistoricVariableInstanceModel users = historicVariableApi
                .getByProcessInstanceIdAndVariableName(tenantId, processInstanceId, SysVariables.USERS, "").getData();
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
                        OrgUnit employee =
                            orgUnitApi.getOrgUnitPersonOrPosition(Y9LoginUserHolder.getTenantId(), user).getData();

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
    public List<HistoryProcessModel> listByProcessInstanceId4Simple(String processInstanceId) {
        List<HistoryProcessModel> items = new ArrayList<>();
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<HistoricTaskInstanceModel> results =
            historicTaskManager.getByProcessInstanceId(tenantId, processInstanceId, "").getData();
        String year = "";
        if (results == null || results.isEmpty()) {
            OfficeDoneInfo officeDoneInfoModel = officeDoneInfoService.findByProcessInstanceId(processInstanceId);
            if (officeDoneInfoModel != null && officeDoneInfoModel.getProcessInstanceId() != null) {
                year = officeDoneInfoModel.getStartTime().substring(0, 4);
                results = historicTaskManager.getByProcessInstanceId(tenantId, processInstanceId, year).getData();
            } else {
                ProcessParam processParam = processParamService.findByProcessInstanceId(processInstanceId);
                year = processParam != null ? processParam.getCreateTime().substring(0, 4) : "";
                results = historicTaskManager.getByProcessInstanceId(tenantId, processInstanceId, year).getData();
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
                OrgUnit employee =
                    orgUnitApi.getOrgUnitPersonOrPosition(Y9LoginUserHolder.getTenantId(), assignee).getData();
                if (employee != null) {
                    String ownerId = hai.getOwner();
                    String employeeName = employee.getName();
                    // 恢复待办，如不是办结人恢复，Owner有值，需显示Owner
                    if (StringUtils.isNotBlank(ownerId)) {
                        OrgUnit ownerUser =
                            orgUnitApi.getOrgUnitPersonOrPosition(Y9LoginUserHolder.getTenantId(), ownerId).getData();
                        employeeName = ownerUser.getName();
                    }
                    HistoricVariableInstanceModel zhuBan = historicVariableApi
                        .getByTaskIdAndVariableName(tenantId, taskId, SysVariables.PARALLELSPONSOR, year).getData();
                    if (zhuBan != null) {
                        history.setAssignee(employeeName + "(主办)");
                    } else {
                        history.setAssignee(employeeName);
                    }
                    history.setAssigneeId(assignee);
                }
            } else {// 处理单实例未签收的办理人显示
                List<IdentityLinkModel> iList = identityManager.getIdentityLinksForTask(tenantId, taskId).getData();
                if (!iList.isEmpty()) {
                    StringBuilder assignees = new StringBuilder();
                    int j = 0;
                    for (IdentityLinkModel identityLink : iList) {
                        String assigneeId = identityLink.getUserId();
                        OrgUnit ownerUser = orgUnitApi
                            .getOrgUnitPersonOrPosition(Y9LoginUserHolder.getTenantId(), assigneeId).getData();
                        if (j < 5) {
                            assignees = Y9Util.genCustomStr(assignees, ownerUser.getName(), "、");
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
            /*
             * 手动设置流程办结的时候, 流程最后一个任务结束的时间就是第一个手动设置的流程跟踪的时间
             */
            Date endTime1 = hai.getEndTime();
            List<ProcessTrack> ptList = this.listByTaskId(taskId);
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
        String seq = "串行办理";
        if (name.equals(seq)) {
            HistoricVariableInstanceModel users = historicVariableApi
                .getByProcessInstanceIdAndVariableName(tenantId, processInstanceId, SysVariables.USERS, "").getData();
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
                        OrgUnit employee =
                            orgUnitApi.getOrgUnitPersonOrPosition(Y9LoginUserHolder.getTenantId(), user).getData();
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

    @Override
    public List<ProcessTrack> listByTaskId(String taskId) {
        return processTrackRepository.findByTaskId(taskId);
    }

    @Override
    public List<ProcessTrack> listByTaskIdAndEndTimeIsNull(String taskId) {
        return processTrackRepository.findByTaskIdAndEndTimeIsNull(taskId, "");
    }

    @Override
    public List<ProcessTrack> listByTaskIdAsc(String taskId) {
        return processTrackRepository.findByTaskIdAsc(taskId);
    }

    private final String longTime(Date startTime, Date endTime) {
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
            return d + "天" + h + "小时" + m + "分" + s + "秒";
        }
    }

    @Override
    @Transactional
    public ProcessTrack saveOrUpdate(ProcessTrack pt) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String id = pt.getId();
        if (StringUtils.isNotEmpty(id)) {
            Optional<ProcessTrack> oldptOption = processTrackRepository.findById(id);
            if (oldptOption.isPresent()) {
                ProcessTrack oldpt = oldptOption.get();
                oldpt.setEndTime(sdf.format(new Date()));
                oldpt.setDescribed(pt.getDescribed());
                return processTrackRepository.save(oldpt);
            }
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
