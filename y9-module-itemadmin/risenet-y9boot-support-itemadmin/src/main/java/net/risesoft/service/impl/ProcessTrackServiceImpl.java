package net.risesoft.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.api.processadmin.HistoricActivityApi;
import net.risesoft.api.processadmin.HistoricIdentityApi;
import net.risesoft.api.processadmin.HistoricTaskApi;
import net.risesoft.api.processadmin.HistoricVariableApi;
import net.risesoft.api.processadmin.IdentityApi;
import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.consts.processadmin.SysVariables;
import net.risesoft.entity.ProcessParam;
import net.risesoft.entity.ProcessTrack;
import net.risesoft.entity.SignDeptDetail;
import net.risesoft.entity.TaskRelated;
import net.risesoft.entity.documentword.Y9WordHistory;
import net.risesoft.entity.opinion.Opinion;
import net.risesoft.enums.ProcessTrackStatusEnum;
import net.risesoft.enums.TaskRelatedEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.HistoricActivityInstanceModel;
import net.risesoft.model.itemadmin.HistoryProcessModel;
import net.risesoft.model.itemadmin.TaskRelatedModel;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.model.platform.org.Person;
import net.risesoft.model.processadmin.HistoricTaskInstanceModel;
import net.risesoft.model.processadmin.HistoricVariableInstanceModel;
import net.risesoft.model.processadmin.IdentityLinkModel;
import net.risesoft.model.processadmin.IdentityLinkType;
import net.risesoft.model.processadmin.TargetModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.nosql.elastic.entity.OfficeDoneInfo;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.jpa.ProcessTrackRepository;
import net.risesoft.repository.opinion.OpinionRepository;
import net.risesoft.service.OfficeDoneInfoService;
import net.risesoft.service.ProcessTrackService;
import net.risesoft.service.SignDeptDetailService;
import net.risesoft.service.TaskRelatedService;
import net.risesoft.service.core.ProcessParamService;
import net.risesoft.service.word.Y9WordHistoryService;
import net.risesoft.util.Y9DateTimeUtils;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;
import net.risesoft.y9.util.Y9Util;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class ProcessTrackServiceImpl implements ProcessTrackService {

    private final ProcessTrackRepository processTrackRepository;

    private final OpinionRepository opinionRepository;

    private final Y9WordHistoryService y9WordHistoryService;

    private final HistoricVariableApi historicVariableApi;

    private final OrgUnitApi orgUnitApi;

    private final HistoricTaskApi historictaskApi;

    private final TaskApi taskApi;

    private final HistoricIdentityApi historicIdentityApi;

    private final IdentityApi identityApi;

    private final OfficeDoneInfoService officeDoneInfoService;

    private final ProcessParamService processParamService;

    private final HistoricActivityApi historicActivityApi;

    private final TaskRelatedService taskRelatedService;

    private final ProcessDefinitionApi processDefinitionApi;

    private final SignDeptDetailService signDeptDetailService;

    private final PositionApi positionApi;

    @Override
    @Transactional
    public void deleteById(String id) {
        this.processTrackRepository.deleteById(id);
    }

    @Override
    public ProcessTrack findOne(String id) {
        return this.processTrackRepository.findById(id).orElse(null);
    }

    /**
     * 获取历史流程模型
     */
    private HistoryProcessModel getHistoryProcessModel(HistoricTaskInstanceModel hai, int tabIndex) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        HistoryProcessModel model = createBaseHistoryModel(hai, tabIndex);
        String taskId = hai.getId();
        try {
            // 设置任务相关信息
            setTaskRelatedInfo(model, taskId);
            // 设置待办状态
            setNewToDoStatus(model, tenantId, taskId, hai);
            // 设置办理人信息
            setAssignee(model, hai, tenantId, taskId);
            // 设置时间信息
            setTimeInfo(model, hai);
        } catch (Exception e) {
            LOGGER.error("构建历史流程模型失败，taskId: {}", taskId, e);
        }

        return model;
    }

    /**
     * 创建基础历史模型
     */
    private HistoryProcessModel createBaseHistoryModel(HistoricTaskInstanceModel hai, int tabIndex) {
        HistoryProcessModel model = new HistoryProcessModel();
        model.setChildren(new ArrayList<>());
        model.setTabIndex(tabIndex);
        model.setPersonList(new ArrayList<>());
        model.setId(hai.getId());
        model.setTaskId(hai.getId());
        model.setAssignee("");
        model.setName(hai.getName());
        model.setActionName("");
        model.setStartTime(hai.getStartTime() == null ? "" : Y9DateTimeUtils.formatDateTime(hai.getStartTime()));
        return model;
    }

    /**
     * 设置任务相关信息
     */
    private void setTaskRelatedInfo(HistoryProcessModel model, String taskId) {
        List<TaskRelated> trList = this.taskRelatedService.findByTaskId(taskId);

        // 处理动作名称
        List<TaskRelated> actionNameList = trList.stream()
            .filter(tr -> tr.getInfoType().equals(TaskRelatedEnum.ACTIONNAME.getValue()))
            .collect(Collectors.toList());

        if (!actionNameList.isEmpty() && StringUtils.isNotBlank(actionNameList.get(0).getMsgContent())) {
            model.setActionName(actionNameList.get(0).getMsgContent());
            if ("减签".equals(actionNameList.get(0).getMsgContent())) {
                return;
            }
        }

        // 转换任务相关模型列表
        List<TaskRelatedModel> trModelList = trList.stream().map(tr -> {
            TaskRelatedModel trModel = new TaskRelatedModel();
            Y9BeanUtil.copyProperties(tr, trModel);
            return trModel;
        }).collect(Collectors.toList());
        model.setTaskRelatedList(trModelList);
    }

    /**
     * 设置待办状态
     */
    private void setNewToDoStatus(HistoryProcessModel model, String tenantId, String taskId,
        HistoricTaskInstanceModel hai) {
        if (hai.getEndTime() == null) {
            TaskModel taskModel = this.taskApi.findById(tenantId, taskId).getData();
            int newToDo = (taskModel == null || StringUtils.isBlank(taskModel.getFormKey())) ? 1
                : (Integer.parseInt(taskModel.getFormKey()));
            model.setNewToDo(newToDo);
        } else {
            model.setNewToDo(0);
        }
    }

    /**
     * 设置办理人信息
     */
    private void setAssignee(HistoryProcessModel model, HistoricTaskInstanceModel hai, String tenantId, String taskId) {
        String assignee = hai.getAssignee();
        if (StringUtils.isNotBlank(assignee)) {
            setAssignedInfo(model, hai, tenantId, taskId, assignee);
        } else {
            setUnassignedInfo(model, hai, tenantId, taskId);
        }
    }

    /**
     * 设置已分配用户信息
     */
    private void setAssignedInfo(HistoryProcessModel model, HistoricTaskInstanceModel hai, String tenantId,
        String taskId, String assignee) {
        OrgUnit employee =
            this.orgUnitApi.getOrgUnitPersonOrPosition(Y9LoginUserHolder.getTenantId(), assignee).getData();
        model.setAssigneeId(assignee);
        model.setAssignee(null == employee ? "" : employee.getName());
        // 处理系统自动办结情况
        List<TaskRelated> trList = this.taskRelatedService.findByTaskId(taskId);
        List<TaskRelated> newtrList = trList.stream()
            .filter(tr -> tr.getInfoType().equals(TaskRelatedEnum.COMPLETEDTYPE.getValue()))
            .collect(Collectors.toList());
        if (!newtrList.isEmpty()) {
            model.setAssignee(model.getAssignee() + newtrList.get(0).getMsgContent());
        }
        // 设置人员列表
        List<Person> personList = getAssignedPersonList(hai, tenantId, taskId, assignee);
        model.setPersonList(personList);
    }

    /**
     * 获取已分配人员列表
     */
    private List<Person> getAssignedPersonList(HistoricTaskInstanceModel hai, String tenantId, String taskId,
        String assignee) {
        List<Person> personList;

        if (null != hai.getClaimTime()) {
            personList = getClaimedPersonList(hai, tenantId, taskId, assignee);
        } else {
            personList = getUnclaimedPersonList(hai, assignee);
        }

        return personList;
    }

    /**
     * 获取已签收人员列表
     */
    private List<Person> getClaimedPersonList(HistoricTaskInstanceModel hai, String tenantId, String taskId,
        String assignee) {
        List<Person> personList = new ArrayList<>();
        try {
            List<IdentityLinkModel> iList =
                this.historicIdentityApi.getIdentityLinksForTask(tenantId, taskId).getData();
            iList.stream().filter(i -> i.getType().equals(IdentityLinkType.CANDIDATE)).forEach(i -> {
                List<Person> personListTemp =
                    this.positionApi.listPersonsByPositionId(Y9LoginUserHolder.getTenantId(), i.getUserId()).getData();
                if (assignee.equals(i.getUserId())) {
                    personListTemp.forEach(p -> {
                        if (null == hai.getEndTime()) {
                            p.setTabIndex(ProcessTrackStatusEnum.CLAIMED.getValue());
                        } else {
                            p.setTabIndex(ProcessTrackStatusEnum.DONE.getValue());
                        }
                    });
                } else {
                    personListTemp.forEach(p -> p.setTabIndex(ProcessTrackStatusEnum.UNCLAIMED.getValue()));
                }
                personList.addAll(personListTemp);
            });
        } catch (Exception e) {
            LOGGER.warn("获取已签收人员列表失败", e);
        }
        return personList;
    }

    /**
     * 获取未签收人员列表
     */
    private List<Person> getUnclaimedPersonList(HistoricTaskInstanceModel hai, String assignee) {
        try {
            List<Person> personList =
                this.positionApi.listPersonsByPositionId(Y9LoginUserHolder.getTenantId(), assignee).getData();
            int newToDo = hai.getEndTime() == null ? 1 : 0;
            personList.forEach(p -> {
                if (null == hai.getEndTime()) {
                    p.setTabIndex(1 == newToDo ? ProcessTrackStatusEnum.UNOPENED.getValue()
                        : ProcessTrackStatusEnum.TODO.getValue());
                } else {
                    p.setTabIndex(ProcessTrackStatusEnum.DONE.getValue());
                }
            });
            return personList;
        } catch (Exception e) {
            LOGGER.warn("获取未签收人员列表失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 设置未分配用户信息
     */
    private void setUnassignedInfo(HistoryProcessModel model, HistoricTaskInstanceModel hai, String tenantId,
        String taskId) {
        try {
            List<IdentityLinkModel> iList =
                this.historicIdentityApi.getIdentityLinksForTask(tenantId, taskId).getData();
            List<IdentityLinkModel> candidateList =
                iList.stream().filter(i -> i.getType().equals(IdentityLinkType.CANDIDATE)).collect(Collectors.toList());
            if (!candidateList.isEmpty()) {
                setCandidateAssigneeInfo(model, tenantId, candidateList);
            }
            // 设置候选人列表
            List<Person> personList = new ArrayList<>();
            candidateList.forEach(candidate -> {
                try {
                    List<Person> personListTemp =
                        this.positionApi.listPersonsByPositionId(Y9LoginUserHolder.getTenantId(), candidate.getUserId())
                            .getData();
                    personListTemp.forEach(p -> {
                        if (null != hai.getEndTime()) {
                            p.setTabIndex(ProcessTrackStatusEnum.UNCLAIMED.getValue());
                        } else {
                            p.setTabIndex(ProcessTrackStatusEnum.WAITCLAIM.getValue());
                        }
                    });
                    personList.addAll(personListTemp);
                } catch (Exception e) {
                    LOGGER.warn("获取候选人信息失败，userId: {}", candidate.getUserId(), e);
                }
            });
            model.setPersonList(personList);
        } catch (Exception e) {
            LOGGER.error("获取任务的用户信息失败", e);
        }
    }

    /**
     * 设置候选人办理人信息
     */
    private void setCandidateAssigneeInfo(HistoryProcessModel model, String tenantId,
        List<IdentityLinkModel> candidateList) {
        String positionId = candidateList.get(0).getUserId();
        OrgUnit orgUnit = this.orgUnitApi.getOrgUnit(tenantId, positionId).getData();

        if (candidateList.size() > 1) {
            String sb = (null == orgUnit ? "岗位已删除" : orgUnit.getName()) + "等" + candidateList.size() + "人";
            model.setAssignee(sb);
        } else {
            model.setAssignee(null == orgUnit ? "岗位已删除" : orgUnit.getName());
        }
    }

    private List<HistoryProcessModel> getHistoryProcessModel(HistoryProcessModel model) {
        AtomicInteger tabIndex = new AtomicInteger(model.getTabIndex() + 1);
        List<HistoryProcessModel> list = new ArrayList<>();
        List<ProcessTrack> processTrackList = processTrackRepository.findByTaskId(model.getTaskId());
        processTrackList.forEach(pt -> {
            HistoryProcessModel endModel = new HistoryProcessModel();
            Y9BeanUtil.copyProperties(model, endModel);
            endModel.setChildren(new ArrayList<>());
            endModel.setTabIndex(tabIndex.getAndIncrement());
            endModel.setId(pt.getId());
            endModel.setName(pt.getTaskDefName());
            endModel.setActionName(pt.getTaskDefName());
            endModel.setEndTime(pt.getEndTime());
            endModel.setTime("");
            list.add(endModel);
        });
        return list;
    }

    @Override
    public Y9Result<List<HistoricActivityInstanceModel>> getTaskList(String processInstanceId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<HistoricActivityInstanceModel> list = new ArrayList<>();
        try {
            // 获取历史活动实例列表
            List<net.risesoft.model.processadmin.HistoricActivityInstanceModel> activityList =
                getActivityListByProcessInstanceId(tenantId, processInstanceId);
            if (activityList == null || activityList.isEmpty()) {
                return Y9Result.success(list, "获取成功");
            }
            // 处理每个活动实例
            for (net.risesoft.model.processadmin.HistoricActivityInstanceModel task : activityList) {
                processActivityInstance(task, tenantId, processInstanceId);
                // 转换并添加到结果列表
                HistoricActivityInstanceModel taskModel = new HistoricActivityInstanceModel();
                Y9BeanUtil.copyProperties(task, taskModel);
                list.add(taskModel);
            }
        } catch (Exception e) {
            LOGGER.error("获取任务列表失败，processInstanceId: {}", processInstanceId, e);
            return Y9Result.failure("获取任务列表失败");
        }
        return Y9Result.success(list, "获取成功");
    }

    /**
     * 根据流程实例ID获取活动列表
     */
    private List<net.risesoft.model.processadmin.HistoricActivityInstanceModel>
        getActivityListByProcessInstanceId(String tenantId, String processInstanceId) {

        List<net.risesoft.model.processadmin.HistoricActivityInstanceModel> list =
            this.historicActivityApi.getByProcessInstanceIdAndYear(tenantId, processInstanceId, "").getData();

        if (list == null || list.isEmpty()) {
            // 尝试从办结信息中获取年份
            String year = getYearFromOfficeDoneInfo(processInstanceId);
            if (StringUtils.isNotBlank(year)) {
                list =
                    this.historicActivityApi.getByProcessInstanceIdAndYear(tenantId, processInstanceId, year).getData();
            }
        }

        return list;
    }

    /**
     * 从办结信息中获取年份
     */
    private String getYearFromOfficeDoneInfo(String processInstanceId) {
        try {
            OfficeDoneInfo info = this.officeDoneInfoService.findByProcessInstanceId(processInstanceId);
            if (info != null && StringUtils.isNotBlank(info.getStartTime())) {
                return info.getStartTime().substring(0, 4);
            }
        } catch (Exception e) {
            LOGGER.warn("获取办结信息失败，processInstanceId: {}", processInstanceId, e);
        }
        return "";
    }

    /**
     * 处理单个活动实例
     */
    private void processActivityInstance(net.risesoft.model.processadmin.HistoricActivityInstanceModel task,
        String tenantId, String processInstanceId) {

        // 初始化字段
        task.setExecutionId("");
        task.setCalledProcessInstanceId("");

        String assignee = task.getAssignee();
        if (StringUtils.isBlank(assignee)) {
            return;
        }

        try {
            // 获取年份用于变量查询
            String year = getYearFromOfficeDoneInfo(processInstanceId);

            // 获取办理人信息
            String employeeName = getAssigneeName(task, assignee);

            // 获取意见信息
            List<Opinion> opinion =
                this.opinionRepository.findByTaskIdAndPositionIdAndProcessTrackIdIsNull(task.getTaskId(), assignee);
            task.setTenantId(!opinion.isEmpty() ? opinion.get(0).getContent() : "");

            // 检查是否为主办人
            boolean isSponsor = isSponsor(tenantId, task.getTaskId(), year);
            task.setCalledProcessInstanceId(isSponsor ? employeeName + "(主办)" : employeeName);

            // 计算办理时长
            if (task.getStartTime() != null && task.getEndTime() != null) {
                task.setExecutionId(this.longTime(task.getStartTime(), task.getEndTime()));
            }
        } catch (Exception e) {
            LOGGER.warn("处理活动实例失败，taskId: {}", task.getTaskId(), e);
        }
    }

    /**
     * 获取办理人名称
     */
    private String getAssigneeName(net.risesoft.model.processadmin.HistoricActivityInstanceModel task,
        String assignee) {
        String employeeName = "";

        try {
            OrgUnit employee =
                this.orgUnitApi.getOrgUnitPersonOrPosition(Y9LoginUserHolder.getTenantId(), assignee).getData();
            if (employee != null) {
                employeeName = employee.getName();
            }

            // 如果tenantId字段存有岗位/人员名称，优先显示
            if (StringUtils.isNotBlank(task.getTenantId())) {
                employeeName = task.getTenantId();
            }
        } catch (Exception e) {
            LOGGER.warn("获取办理人信息失败，assignee: {}", assignee, e);
        }

        return employeeName;
    }

    /**
     * 检查是否为主办人
     */
    private boolean isSponsor(String tenantId, String taskId, String year) {
        try {
            HistoricVariableInstanceModel zhuBan = this.historicVariableApi
                .getByTaskIdAndVariableName(tenantId, taskId, SysVariables.PARALLEL_SPONSOR, year)
                .getData();
            return zhuBan != null;
        } catch (Exception e) {
            LOGGER.warn("检查主办人身份失败，taskId: {}", taskId, e);
            return false;
        }
    }

    @Override
    public List<HistoryProcessModel> listByProcessInstanceId(String processInstanceId) {
        String tenantId = Y9LoginUserHolder.getTenantId();

        try {
            // 获取历史任务实例列表
            HistoricTaskInstanceResult result = getHistoricTaskInstances(tenantId, processInstanceId);
            List<HistoricTaskInstanceModel> results = result.getTaskInstances();
            String year = result.getYear();
            if (results == null || results.isEmpty()) {
                return new ArrayList<>();
            }
            List<HistoryProcessModel> items = new ArrayList<>();
            // 处理每个历史任务实例
            for (HistoricTaskInstanceModel hai : results) {
                if (hai == null) {
                    continue;
                }
                HistoryProcessModel model = processHistoricTaskInstance(hai, tenantId, year);
                items.add(model);
                // 处理流程跟踪信息
                List<ProcessTrack> ptList = this.listByTaskId(hai.getId());
                items.addAll(processProcessTracks(ptList, hai.getId()));
            }

            // 排序
            Collections.sort(items);

            // 处理串行办理情况
            handleSequentialProcessing(items, tenantId, processInstanceId);

            return items;
        } catch (Exception e) {
            LOGGER.error("获取流程实例历史记录失败，processInstanceId: {}", processInstanceId, e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取历史任务实例结果
     */
    private HistoricTaskInstanceResult getHistoricTaskInstances(String tenantId, String processInstanceId) {
        List<HistoricTaskInstanceModel> results =
            this.historictaskApi.getByProcessInstanceId(tenantId, processInstanceId, "").getData();
        String year = "";

        if (results == null || results.isEmpty()) {
            year = getProcessYear(processInstanceId);
            results = this.historictaskApi.getByProcessInstanceId(tenantId, processInstanceId, year).getData();
        }

        return new HistoricTaskInstanceResult(results, year);
    }

    /**
     * 获取流程年份
     */
    private String getProcessYear(String processInstanceId) {
        try {
            OfficeDoneInfo officeDoneInfoModel = this.officeDoneInfoService.findByProcessInstanceId(processInstanceId);
            if (officeDoneInfoModel != null && officeDoneInfoModel.getProcessInstanceId() != null) {
                return officeDoneInfoModel.getStartTime().substring(0, 4);
            }

            ProcessParam processParam = this.processParamService.findByProcessInstanceId(processInstanceId);
            return processParam != null ? processParam.getCreateTime().substring(0, 4) : "";
        } catch (Exception e) {
            LOGGER.warn("获取流程年份失败，processInstanceId: {}", processInstanceId, e);
            return "";
        }
    }

    /**
     * 处理历史任务实例
     */
    private HistoryProcessModel processHistoricTaskInstance(HistoricTaskInstanceModel hai, String tenantId,
        String year) {
        HistoryProcessModel model = createBaseHistoryModel(hai);

        try {
            // 设置历史正文版本
            setHistoryVersion(model, hai.getId());

            // 设置办理人信息
            setAssigneeInfo(model, hai, tenantId, year);

            // 设置待办状态
            setNewToDoStatus(model, hai, tenantId);

            // 设置结束标识
            model.setEndFlag(StringUtils.isBlank(hai.getTenantId()) ? "" : hai.getTenantId());

            // 设置描述信息
            setDescriptionInfo(model, hai, tenantId, year);

            // 设置意见信息
            setOpinionInfo(model, hai);

            // 设置时间信息
            setTimeInfo(model, hai);

        } catch (Exception e) {
            LOGGER.warn("处理历史任务实例失败，taskId: {}", hai.getId(), e);
        }

        return model;
    }

    /**
     * 创建基础历史模型
     */
    private HistoryProcessModel createBaseHistoryModel(HistoricTaskInstanceModel hai) {
        HistoryProcessModel model = new HistoryProcessModel();
        model.setId(hai.getId());
        model.setTaskId(hai.getId());
        model.setAssignee("");
        model.setName(hai.getName());
        model.setDescription("");
        model.setOpinion("");
        return model;
    }

    /**
     * 设置历史版本信息
     */
    private void setHistoryVersion(HistoryProcessModel model, String taskId) {
        try {
            Y9WordHistory y9WordHistory = this.y9WordHistoryService.findByTaskId(taskId);
            if (y9WordHistory != null) {
                model.setHistoryVersion(y9WordHistory.getVersion());
            }
        } catch (Exception e) {
            LOGGER.warn("获取历史版本信息失败，taskId: {}", taskId, e);
        }
    }

    /**
     * 设置办理人信息
     */
    private void setAssigneeInfo(HistoryProcessModel model, HistoricTaskInstanceModel hai, String tenantId,
        String year) {
        String assignee = hai.getAssignee();

        if (StringUtils.isNotBlank(assignee)) {
            setAssignedUserInfo(model, hai, tenantId, year, assignee);
        } else {
            setUnassignedUserInfo(model, hai, tenantId);
        }
    }

    /**
     * 设置已分配用户信息
     */
    private void setAssignedUserInfo(HistoryProcessModel model, HistoricTaskInstanceModel hai, String tenantId,
        String year, String assignee) {
        try {
            OrgUnit employee =
                this.orgUnitApi.getOrgUnitPersonOrPosition(Y9LoginUserHolder.getTenantId(), assignee).getData();
            model.setAssigneeId(assignee);
            // 承办人id,用于数据中心保存
            model.setUndertakerId(assignee);

            HistoricVariableInstanceModel zhuBan = getZhuBanInfo(tenantId, hai.getId(), year);
            String employeeName = getEmployeeName(employee, hai);

            if (zhuBan != null) {
                model.setAssignee(employeeName + "(主办)");
            } else {
                model.setAssignee(employeeName);
            }
        } catch (Exception e) {
            LOGGER.warn("设置已分配用户信息失败，taskId: {}, assignee: {}", hai.getId(), assignee, e);
        }
    }

    /**
     * 获取主办人信息
     */
    private HistoricVariableInstanceModel getZhuBanInfo(String tenantId, String taskId, String year) {
        try {
            return this.historicVariableApi
                .getByTaskIdAndVariableName(tenantId, taskId, SysVariables.PARALLEL_SPONSOR, year)
                .getData();
        } catch (Exception e) {
            LOGGER.warn("获取主办人信息失败，taskId: {}", taskId, e);
            return null;
        }
    }

    /**
     * 获取员工名称
     */
    private String getEmployeeName(OrgUnit employee, HistoricTaskInstanceModel hai) {
        String employeeName = employee != null ? employee.getName() : "";

        // 恢复待办，如不是办结人恢复，Owner有值，需显示Owner
        String ownerId = hai.getOwner();
        if (StringUtils.isNotBlank(ownerId)) {
            try {
                OrgUnit ownerUser =
                    this.orgUnitApi.getOrgUnitPersonOrPosition(Y9LoginUserHolder.getTenantId(), ownerId).getData();
                if (ownerUser != null) {
                    employeeName = ownerUser.getName();
                    // 更新承办人ID
                }
            } catch (Exception e) {
                LOGGER.warn("获取所有者信息失败，ownerId: {}", ownerId, e);
            }
        }

        // ScopeType存的是岗位/人员名称，优先显示这个名称
        if (StringUtils.isNotBlank(hai.getScopeType())) {
            employeeName = hai.getScopeType();
        }

        return employeeName;
    }

    /**
     * 设置未分配用户信息
     */
    private void setUnassignedUserInfo(HistoryProcessModel model, HistoricTaskInstanceModel hai, String tenantId) {
        try {
            List<IdentityLinkModel> iList = this.identityApi.getIdentityLinksForTask(tenantId, hai.getId()).getData();
            if (iList != null && !iList.isEmpty()) {
                StringBuilder assignees = new StringBuilder();
                int j = 0;
                for (IdentityLinkModel identityLink : iList) {
                    String assigneeId = identityLink.getUserId();
                    OrgUnit ownerUser =
                        this.orgUnitApi.getOrgUnitPersonOrPosition(Y9LoginUserHolder.getTenantId(), assigneeId)
                            .getData();
                    if (j < 5) {
                        Y9Util.genCustomStr(assignees, ownerUser == null ? "办理人对应的岗位不存在" : ownerUser.getName(), "、");
                    } else {
                        assignees.append("等，共").append(iList.size()).append("人");
                        break;
                    }
                    j++;
                }
                model.setAssignee(assignees.toString());
            }
        } catch (Exception e) {
            LOGGER.error("获取任务的用户信息失败", e);
        }
    }

    /**
     * 设置待办状态
     */
    private void setNewToDoStatus(HistoryProcessModel model, HistoricTaskInstanceModel hai, String tenantId) {
        try {
            int newToDo = 0;
            if (hai.getEndTime() == null) {
                TaskModel taskModel = this.taskApi.findById(tenantId, hai.getId()).getData();
                newToDo = (taskModel == null || StringUtils.isBlank(taskModel.getFormKey())) ? 1
                    : (Integer.parseInt(taskModel.getFormKey()));
            }
            model.setNewToDo(newToDo);
        } catch (Exception e) {
            LOGGER.warn("设置待办状态失败，taskId: {}", hai.getId(), e);
        }
    }

    /**
     * 设置描述信息
     */
    private void setDescriptionInfo(HistoryProcessModel model, HistoricTaskInstanceModel hai, String tenantId,
        String year) {
        String description = hai.getDeleteReason();
        if (StringUtils.isBlank(description) || "MI_END".equals(description)) {
            return;
        }

        model.setDescription(description);

        // 只有当描述包含特定内容时才进行进一步处理
        if (!description.contains("Delete MI execution")) {
            return;
        }

        try {
            handleDeleteMIExecutionDescription(model, hai, tenantId, year);
        } catch (Exception e) {
            LOGGER.warn("设置任务删除描述信息失败，taskId: {}", hai.getId(), e);
        }
    }

    /**
     * 处理Delete MI execution类型的描述信息
     */
    private void handleDeleteMIExecutionDescription(HistoryProcessModel model, HistoricTaskInstanceModel hai,
        String tenantId, String year) {
        // 获取任务发送者信息
        String taskSender = getTaskSender(tenantId, hai.getId(), year);
        if (StringUtils.isNotBlank(taskSender)) {
            model.setDescription("该任务由" + taskSender + "删除");
        }

        // 获取回退原因并更新描述
        String rollBackReason = getRollBackReason(tenantId, hai.getId(), year);
        if (StringUtils.isNotBlank(rollBackReason)) {
            model.setDescription(rollBackReason);
        }

        // 特殊处理：发送办结协办任务使用减签方式办结的情况
        if (StringUtils.isNotBlank(hai.getTenantId())) {
            model.setDescription("");
        }
    }

    /**
     * 获取任务发送者信息
     */
    private String getTaskSender(String tenantId, String taskId, String year) {
        try {
            HistoricVariableInstanceModel taskSenderModel =
                this.historicVariableApi.getByTaskIdAndVariableName(tenantId, taskId, SysVariables.TASK_SENDER, year)
                    .getData();
            if (taskSenderModel != null) {
                return taskSenderModel.getValue() == null ? "" : (String)taskSenderModel.getValue();
            }
        } catch (Exception e) {
            LOGGER.debug("获取任务发送者信息失败，taskId: {}, tenantId: {}", taskId, tenantId, e);
        }
        return "";
    }

    /**
     * 获取回退原因
     */
    private String getRollBackReason(String tenantId, String taskId, String year) {
        try {
            HistoricVariableInstanceModel rollBackReason =
                this.historicVariableApi.getByTaskIdAndVariableName(tenantId, taskId, "rollBackReason", year).getData();
            if (rollBackReason != null) {
                return String.valueOf(rollBackReason.getValue());
            }
        } catch (Exception e) {
            LOGGER.debug("获取回退原因失败，taskId: {}, tenantId: {}", taskId, tenantId, e);
        }
        return "";
    }

    /**
     * 设置意见信息
     */
    private void setOpinionInfo(HistoryProcessModel model, HistoricTaskInstanceModel hai) {
        try {
            String assignee = hai.getAssignee();
            List<Opinion> opinion = this.opinionRepository.findByTaskIdAndPositionIdAndProcessTrackIdIsNull(hai.getId(),
                StringUtils.isBlank(assignee) ? "" : assignee);
            model.setOpinion(!opinion.isEmpty() ? opinion.get(0).getContent() : "");
        } catch (Exception e) {
            LOGGER.warn("设置意见信息失败，taskId: {}", hai.getId(), e);
        }
    }

    /**
     * 设置时间信息
     */
    private void setTimeInfo(HistoryProcessModel model, HistoricTaskInstanceModel hai) {
        model.setStartTime(hai.getStartTime() == null ? "" : Y9DateTimeUtils.formatDateTime(hai.getStartTime()));

        try {
            model.setStartTimes(hai.getStartTime() == null ? 0
                : Y9DateTimeUtils.parseDateTime(Y9DateTimeUtils.formatDateTime(hai.getStartTime())).getTime());
        } catch (Exception e) {
            LOGGER.warn("解析开始时间失败", e);
        }

        Date endTime = hai.getEndTime();
        model.setEndTime(endTime == null ? "" : Y9DateTimeUtils.formatDateTime(endTime));

        try {
            model.setEndTimes(
                endTime == null ? 0 : Y9DateTimeUtils.parseDateTime(Y9DateTimeUtils.formatDateTime(endTime)).getTime());
        } catch (Exception e) {
            LOGGER.warn("解析结束时间失败", e);
        }

        model.setTime(this.longTime(hai.getStartTime(), endTime));
    }

    /**
     * 处理流程跟踪信息
     */
    private List<HistoryProcessModel> processProcessTracks(List<ProcessTrack> ptList, String taskId) {
        List<HistoryProcessModel> trackModels = new ArrayList<>();

        if (ptList == null || ptList.isEmpty()) {
            return trackModels;
        }

        for (ProcessTrack pt : ptList) {
            try {
                HistoryProcessModel modelTrack = createProcessTrackModel(pt, taskId);
                trackModels.add(modelTrack);
            } catch (Exception e) {
                LOGGER.warn("处理流程跟踪信息失败，processTrackId: {}", pt.getId(), e);
            }
        }

        return trackModels;
    }

    /**
     * 创建流程跟踪模型
     */
    private HistoryProcessModel createProcessTrackModel(ProcessTrack pt, String taskId) {
        HistoryProcessModel modelTrack = new HistoryProcessModel();

        // 基本信息设置
        modelTrack.setId(pt.getId());
        modelTrack.setAssignee(StringUtils.defaultString(pt.getReceiverName()));
        modelTrack.setName(StringUtils.defaultString(pt.getTaskDefName()));
        modelTrack.setDescription(StringUtils.defaultString(pt.getDescribed()));
        modelTrack.setTaskId(taskId);
        modelTrack.setIsChaoSong(pt.getIsChaoSong() != null && pt.getIsChaoSong());

        // 设置历史版本
        modelTrack.setHistoryVersion(pt.getDocVersion());

        // 时间信息设置
        setTimeInfoForProcessTrack(modelTrack, pt);

        // 意见信息设置
        setOpinionInfoForProcessTrack(modelTrack, taskId, pt.getId());

        return modelTrack;
    }

    /**
     * 为流程跟踪设置时间信息
     */
    private void setTimeInfoForProcessTrack(HistoryProcessModel modelTrack, ProcessTrack pt) {
        modelTrack.setStartTime(StringUtils.defaultString(pt.getStartTime()));
        modelTrack.setEndTime(StringUtils.defaultString(pt.getEndTime()));

        try {
            // 设置时间戳
            if (StringUtils.isNotBlank(pt.getStartTime())) {
                modelTrack.setStartTimes(Y9DateTimeUtils.parseDateTime(pt.getStartTime()).getTime());
            }

            if (StringUtils.isNotBlank(pt.getEndTime())) {
                modelTrack.setEndTimes(Y9DateTimeUtils.parseDateTime(pt.getEndTime()).getTime());
            } else {
                modelTrack.setEndTimes(0L);
            }

            // 设置时长
            if (StringUtils.isBlank(pt.getEndTime())) {
                modelTrack.setTime("");
            } else {
                modelTrack.setTime(this.longTime(Y9DateTimeUtils.parseDateTime(pt.getStartTime()),
                    Y9DateTimeUtils.parseDateTime(pt.getEndTime())));
            }
        } catch (Exception e) {
            LOGGER.warn("解析流程跟踪时间信息失败，processTrackId: {}", pt.getId(), e);
            // 设置默认值
            modelTrack.setStartTimes(0L);
            modelTrack.setEndTimes(0L);
            modelTrack.setTime("");
        }
    }

    /**
     * 为流程跟踪设置意见信息
     */
    private void setOpinionInfoForProcessTrack(HistoryProcessModel modelTrack, String taskId, String processTrackId) {
        try {
            List<Opinion> opinionProcessTrack =
                this.opinionRepository.findByTaskIdAndProcessTrackIdOrderByCreateTimeDesc(taskId, processTrackId);
            modelTrack.setOpinion(opinionProcessTrack.isEmpty() ? "" : opinionProcessTrack.get(0).getContent());
        } catch (Exception e) {
            LOGGER.warn("获取流程跟踪意见信息失败，taskId: {}, processTrackId: {}", taskId, processTrackId, e);
            modelTrack.setOpinion("");
        }
    }

    /**
     * 处理串行办理情况
     */
    private void handleSequentialProcessing(List<HistoryProcessModel> items, String tenantId,
        String processInstanceId) {
        if (items == null || items.isEmpty()) {
            return;
        }

        try {
            String lastName = items.get(items.size() - 1).getName();
            String seq = "串行办理";

            if (!seq.equals(lastName)) {
                return;
            }

            processSequentialUsers(items, tenantId, processInstanceId);
        } catch (Exception e) {
            LOGGER.warn("处理串行办理情况失败，processInstanceId: {}", processInstanceId, e);
        }
    }

    /**
     * 处理串行用户列表
     */
    private void processSequentialUsers(List<HistoryProcessModel> items, String tenantId, String processInstanceId) {
        try {
            HistoricVariableInstanceModel users = this.historicVariableApi
                .getByProcessInstanceIdAndVariableName(tenantId, processInstanceId, SysVariables.USERS, "")
                .getData();

            List<String> userList = users != null ? (ArrayList<String>)users.getValue() : new ArrayList<>();
            if (userList.isEmpty()) {
                return;
            }

            String currentAssigneeId = items.get(items.size() - 1).getAssigneeId();
            boolean startProcessing = false;

            for (String user : userList) {
                if (StringUtils.isBlank(user)) {
                    continue;
                }

                if (StringUtils.isNotBlank(currentAssigneeId) && user.contains(currentAssigneeId)) {
                    startProcessing = true;
                    continue;
                }

                if (startProcessing) {
                    addSequentialProcessModel(items, user);
                }
            }
        } catch (Exception e) {
            LOGGER.warn("处理串行用户列表失败，processInstanceId: {}", processInstanceId, e);
        }
    }

    /**
     * 添加串行处理模型
     */
    private void addSequentialProcessModel(List<HistoryProcessModel> items, String user) {
        try {
            OrgUnit employee =
                this.orgUnitApi.getOrgUnitPersonOrPosition(Y9LoginUserHolder.getTenantId(), user).getData();
            HistoryProcessModel history = new HistoryProcessModel();
            history.setAssignee(employee != null ? employee.getName() : "不存在办理人岗位数据");
            history.setName("串行办理");
            history.setDescription("");
            history.setOpinion("");
            history.setStartTime("未开始");
            history.setEndTime("");
            history.setTime("");
            items.add(history);
        } catch (Exception e) {
            LOGGER.warn("添加串行处理模型失败，userId: {}", user, e);
        }
    }

    @Override
    public List<HistoryProcessModel> listByProcessInstanceId4Simple(String processInstanceId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            // 获取历史任务实例结果
            HistoricTaskInstanceResult result = getHistoricTaskInstances(tenantId, processInstanceId);
            List<HistoricTaskInstanceModel> results = result.getTaskInstances();
            String year = result.getYear();
            if (results == null || results.isEmpty()) {
                return new ArrayList<>();
            }
            List<HistoryProcessModel> items = new ArrayList<>();

            // 处理每个历史任务实例
            for (HistoricTaskInstanceModel hai : results) {
                if (hai == null) {
                    continue;
                }

                HistoryProcessModel history = processSimpleHistoricTaskInstance(hai, tenantId, year);
                items.add(history);

                // 处理流程跟踪信息
                List<ProcessTrack> ptList = this.listByTaskId(hai.getId());
                items.addAll(processSimpleProcessTracks(ptList));
            }

            // 排序
            Collections.sort(items);

            // 处理串行办理情况
            handleSequentialProcessing(items, tenantId, processInstanceId);

            return items;
        } catch (Exception e) {
            LOGGER.error("获取简单流程实例历史记录失败，processInstanceId: {}", processInstanceId, e);
            return new ArrayList<>();
        }
    }

    /**
     * 处理简单历史任务实例
     */
    private HistoryProcessModel processSimpleHistoricTaskInstance(HistoricTaskInstanceModel hai, String tenantId,
        String year) {
        HistoryProcessModel history = createSimpleBaseModel(hai);

        try {
            // 设置办理人信息
            setSimpleAssigneeInfo(history, hai, tenantId, year);

            // 设置时间信息
            setSimpleTimeInfo(history, hai);

            // 设置结束标识
            history.setEndFlag(StringUtils.isBlank(hai.getTenantId()) ? "" : hai.getTenantId());
        } catch (Exception e) {
            LOGGER.warn("处理简单历史任务实例失败，taskId: {}", hai.getId(), e);
        }

        return history;
    }

    /**
     * 创建简单基础模型
     */
    private HistoryProcessModel createSimpleBaseModel(HistoricTaskInstanceModel hai) {
        HistoryProcessModel history = new HistoryProcessModel();
        history.setAssignee("");
        history.setName(hai.getName());
        return history;
    }

    /**
     * 设置简单办理人信息
     */
    private void setSimpleAssigneeInfo(HistoryProcessModel history, HistoricTaskInstanceModel hai, String tenantId,
        String year) {
        String assignee = hai.getAssignee();

        if (StringUtils.isNotBlank(assignee)) {
            setSimpleAssignedUserInfo(history, hai, tenantId, year, assignee);
        } else {
            setSimpleUnassignedUserInfo(history, hai, tenantId);
        }
    }

    /**
     * 设置简单已分配用户信息
     */
    private void setSimpleAssignedUserInfo(HistoryProcessModel history, HistoricTaskInstanceModel hai, String tenantId,
        String year, String assignee) {
        try {
            String employeeName = "";
            OrgUnit employee =
                this.orgUnitApi.getOrgUnitPersonOrPosition(Y9LoginUserHolder.getTenantId(), assignee).getData();

            if (employee != null) {
                String ownerId = hai.getOwner();
                employeeName = employee.getName();
                // 恢复待办，如不是办结人恢复，Owner有值，需显示Owner
                if (StringUtils.isNotBlank(ownerId)) {
                    OrgUnit ownerUser =
                        this.orgUnitApi.getOrgUnitPersonOrPosition(Y9LoginUserHolder.getTenantId(), ownerId).getData();
                    employeeName = ownerUser != null ? ownerUser.getName() : employeeName;
                }
            }

            // ScopeType存的是岗位/人员名称，优先显示这个名称
            if (StringUtils.isNotBlank(hai.getScopeType())) {
                employeeName = hai.getScopeType();
            }

            history.setAssigneeId(assignee);
            HistoricVariableInstanceModel zhuBan = getZhuBanInfo(tenantId, hai.getId(), year);

            if (zhuBan != null) {
                history.setAssignee(employeeName + "(主办)");
            } else {
                history.setAssignee(employeeName);
            }
        } catch (Exception e) {
            LOGGER.warn("设置简单已分配用户信息失败，taskId: {}, assignee: {}", hai.getId(), assignee, e);
        }
    }

    /**
     * 设置简单未分配用户信息
     */
    private void setSimpleUnassignedUserInfo(HistoryProcessModel history, HistoricTaskInstanceModel hai,
        String tenantId) {
        try {
            List<IdentityLinkModel> iList = this.identityApi.getIdentityLinksForTask(tenantId, hai.getId()).getData();
            if (iList != null && !iList.isEmpty()) {
                StringBuilder assignees = new StringBuilder();
                int j = 0;
                for (IdentityLinkModel identityLink : iList) {
                    String assigneeId = identityLink.getUserId();
                    OrgUnit ownerUser =
                        this.orgUnitApi.getOrgUnitPersonOrPosition(Y9LoginUserHolder.getTenantId(), assigneeId)
                            .getData();
                    if (j < 5) {
                        Y9Util.genCustomStr(assignees, ownerUser != null ? ownerUser.getName() : "岗位数据不存在", "、");
                    } else {
                        assignees.append("等，共").append(iList.size()).append("人");
                        break;
                    }
                    j++;
                }
                history.setAssignee(assignees.toString());
            }
        } catch (Exception e) {
            LOGGER.error("获取简单未分配用户信息失败", e);
        }
    }

    /**
     * 设置简单时间信息
     */
    private void setSimpleTimeInfo(HistoryProcessModel history, HistoricTaskInstanceModel hai) {
        history.setStartTime(hai.getStartTime() == null ? "" : Y9DateTimeUtils.formatDateTime(hai.getStartTime()));

        Date endTime = hai.getEndTime();
        history.setEndTime(endTime == null ? "" : Y9DateTimeUtils.formatDateTime(endTime));
        history.setTime(this.longTime(hai.getStartTime(), endTime));
    }

    /**
     * 处理简单流程跟踪信息
     */
    private List<HistoryProcessModel> processSimpleProcessTracks(List<ProcessTrack> ptList) {
        List<HistoryProcessModel> trackModels = new ArrayList<>();

        for (ProcessTrack pt : ptList) {
            try {
                HistoryProcessModel process = new HistoryProcessModel();
                process.setAssignee(pt.getReceiverName() == null ? "" : pt.getReceiverName());
                process.setName(pt.getTaskDefName() == null ? "" : pt.getTaskDefName());
                process.setStartTime(pt.getStartTime() == null ? "" : pt.getStartTime());
                process.setEndTime(pt.getEndTime() == null ? "" : pt.getEndTime());

                if (StringUtils.isBlank(pt.getEndTime())) {
                    process.setTime("");
                } else {
                    process.setTime(this.longTime(Y9DateTimeUtils.parseDateTime(pt.getStartTime()),
                        Y9DateTimeUtils.parseDateTime(pt.getEndTime())));
                }

                trackModels.add(process);
            } catch (Exception e) {
                LOGGER.warn("处理简单流程跟踪信息失败，processTrackId: {}", pt.getId(), e);
            }
        }

        return trackModels;
    }

    @Override
    public List<HistoryProcessModel> listByProcessInstanceIdWithActionName(String processInstanceId) {
        String tenantId = Y9LoginUserHolder.getTenantId();

        try {
            // 获取历史任务实例结果
            HistoricTaskInstanceResult result = getHistoricTaskInstances(tenantId, processInstanceId);
            List<HistoricTaskInstanceModel> results = result.getTaskInstances();
            String year = result.getYear();

            if (results.isEmpty()) {
                return new ArrayList<>();
            }

            // 获取子流程节点列表
            List<TargetModel> subNodeList = getSubProcessNodeList(tenantId, results.get(0).getId(), year);

            // 分离主流程和子流程任务
            TaskSeparationResult separationResult = separateMainAndSubTasks(results, subNodeList);
            List<HistoricTaskInstanceModel> mainResults = separationResult.getMainResults();
            List<HistoricTaskInstanceModel> subResults = separationResult.getSubResults();

            List<HistoryProcessModel> items = new ArrayList<>();
            int tabIndex = 1;

            // 处理主流程任务
            for (HistoricTaskInstanceModel htiMain : mainResults) {
                HistoryProcessModel historyProcessModel = this.getHistoryProcessModel(htiMain, tabIndex++);
                items.add(historyProcessModel);
                items.addAll(this.getHistoryProcessModel(historyProcessModel));

                // 处理会签子流程
                items.addAll(processSignSubProcesses(processInstanceId, htiMain.getId(), subResults, tabIndex));
                // 更新tabIndex
                tabIndex += countSignSubProcesses(processInstanceId, htiMain.getId());
            }

            Collections.sort(items);
            return items;
        } catch (Exception e) {
            LOGGER.error("获取带动作名称的流程实例历史记录失败，processInstanceId: {}", processInstanceId, e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取子流程节点列表
     */
    private List<TargetModel> getSubProcessNodeList(String tenantId, String taskId, String year) {
        try {
            HistoricTaskInstanceModel historicTaskInstanceModel =
                this.historictaskApi.getById(tenantId, taskId, year).getData();
            return this.processDefinitionApi
                .getSubProcessChildNode(tenantId, historicTaskInstanceModel.getProcessDefinitionId())
                .getData();
        } catch (Exception e) {
            LOGGER.warn("获取子流程节点列表失败，taskId: {}", taskId, e);
            return new ArrayList<>();
        }
    }

    /**
     * 分离主流程和子流程任务
     */
    private TaskSeparationResult separateMainAndSubTasks(List<HistoricTaskInstanceModel> results,
        List<TargetModel> subNodeList) {
        List<HistoricTaskInstanceModel> mainResults = new ArrayList<>();
        List<HistoricTaskInstanceModel> subResults = new ArrayList<>();

        for (HistoricTaskInstanceModel hai : results) {
            boolean isSignDeptTemp =
                subNodeList.stream().anyMatch(t -> t.getTaskDefKey().equals(hai.getTaskDefinitionKey()));
            if (isSignDeptTemp) {
                subResults.add(hai);
            } else {
                mainResults.add(hai);
            }
        }

        return new TaskSeparationResult(mainResults, subResults);
    }

    /**
     * 处理会签子流程
     */
    private List<HistoryProcessModel> processSignSubProcesses(String processInstanceId, String taskId,
        List<HistoricTaskInstanceModel> subResults, int startTabIndex) {
        List<HistoryProcessModel> signProcessModels = new ArrayList<>();

        try {
            List<SignDeptDetail> signDeptDetailList =
                this.signDeptDetailService.findByTaskId(processInstanceId, taskId);

            int tabIndex = startTabIndex;
            for (SignDeptDetail signDeptDetail : signDeptDetailList) {
                HistoryProcessModel oneModel = new HistoryProcessModel();
                oneModel.setId(signDeptDetail.getId());
                oneModel.setPersonList(List.of());
                oneModel.setActionName("并行会签【" + signDeptDetail.getDeptName() + "】");

                List<HistoryProcessModel> twoProcessList = new ArrayList<>();
                int subTabIndex = 1;
                for (HistoricTaskInstanceModel hti : subResults) {
                    if (hti.getExecutionId().equals(signDeptDetail.getExecutionId())) {
                        twoProcessList.add(this.getHistoryProcessModel(hti, subTabIndex++));
                    }
                }

                oneModel.setTabIndex(tabIndex++);
                oneModel.setChildren(twoProcessList);
                oneModel.setStartTime(!twoProcessList.isEmpty() ? twoProcessList.get(0).getStartTime() : "");
                signProcessModels.add(oneModel);
            }
        } catch (Exception e) {
            LOGGER.warn("处理会签子流程失败，processInstanceId: {}, taskId: {}", processInstanceId, taskId, e);
        }

        return signProcessModels;
    }

    /**
     * 计算会签子流程数量
     */
    private int countSignSubProcesses(String processInstanceId, String taskId) {
        try {
            List<SignDeptDetail> signDeptDetailList =
                this.signDeptDetailService.findByTaskId(processInstanceId, taskId);
            return signDeptDetailList.size();
        } catch (Exception e) {
            LOGGER.warn("计算会签子流程数量失败，processInstanceId: {}, taskId: {}", processInstanceId, taskId, e);
            return 0;
        }
    }

    @Override
    public List<ProcessTrack> listByTaskId(String taskId) {
        return this.processTrackRepository.findByTaskId(taskId);
    }

    @Override
    public List<ProcessTrack> listByTaskIdAndEndTimeIsNull(String taskId) {
        return this.processTrackRepository.findByTaskIdAndEndTimeIsNull(taskId, "");
    }

    @Override
    public List<ProcessTrack> listByTaskIdAsc(String taskId) {
        return this.processTrackRepository.findByTaskIdAsc(taskId);
    }

    private String longTime(Date startTime, Date endTime) {
        if (endTime == null) {
            return "";
        } else {
            long time = endTime.getTime() - startTime.getTime();
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
        String id = pt.getId();
        if (StringUtils.isNotEmpty(id)) {
            Optional<ProcessTrack> option = this.processTrackRepository.findById(id);
            if (option.isPresent()) {
                ProcessTrack oldProcessTrack = option.get();
                oldProcessTrack.setEndTime(Y9DateTimeUtils.formatCurrentDateTime());
                oldProcessTrack.setDescribed(pt.getDescribed());
                return this.processTrackRepository.save(oldProcessTrack);
            }
        }
        ProcessTrack newProcessTrack = new ProcessTrack();
        newProcessTrack.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        newProcessTrack.setProcessInstanceId(pt.getProcessInstanceId());
        newProcessTrack.setTaskId(pt.getTaskId());
        newProcessTrack.setTaskDefName(pt.getTaskDefName());
        newProcessTrack.setSenderName(pt.getSenderName());
        newProcessTrack.setReceiverName(pt.getReceiverName());
        newProcessTrack.setTaskDefName(pt.getTaskDefName());
        newProcessTrack.setStartTime(pt.getStartTime());
        newProcessTrack.setEndTime(pt.getEndTime());
        newProcessTrack.setDescribed(pt.getDescribed());
        this.processTrackRepository.save(newProcessTrack);
        return newProcessTrack;
    }

    /**
     * 任务分离结果封装类
     */
    @Getter
    private static class TaskSeparationResult {
        private final List<HistoricTaskInstanceModel> mainResults;
        private final List<HistoricTaskInstanceModel> subResults;

        public TaskSeparationResult(
            List<HistoricTaskInstanceModel> mainResults,
            List<HistoricTaskInstanceModel> subResults) {
            this.mainResults = mainResults;
            this.subResults = subResults;
        }

    }

    /**
     * 历史任务实例结果封装类
     */
    @Getter
    private static class HistoricTaskInstanceResult {
        private final List<HistoricTaskInstanceModel> taskInstances;
        private final String year;

        public HistoricTaskInstanceResult(List<HistoricTaskInstanceModel> taskInstances, String year) {
            this.taskInstances = taskInstances;
            this.year = year;
        }

    }
}
