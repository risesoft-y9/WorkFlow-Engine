package net.risesoft.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.api.platform.permission.PersonRoleApi;
import net.risesoft.api.processadmin.*;
import net.risesoft.entity.*;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.OpinionFrameOneClickSetModel;
import net.risesoft.model.itemadmin.OpinionHistoryModel;
import net.risesoft.model.itemadmin.OpinionListModel;
import net.risesoft.model.itemadmin.OpinionModel;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.PersonExt;
import net.risesoft.model.platform.Position;
import net.risesoft.model.processadmin.HistoricProcessInstanceModel;
import net.risesoft.model.processadmin.HistoricTaskInstanceModel;
import net.risesoft.model.processadmin.ProcessDefinitionModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.repository.jpa.OpinionHistoryRepository;
import net.risesoft.repository.jpa.OpinionRepository;
import net.risesoft.service.*;
import net.risesoft.service.config.ItemOpinionFrameBindService;
import net.risesoft.util.CommentUtil;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class OpinionServiceImpl implements OpinionService {

    private final OpinionRepository opinionRepository;

    private final ItemOpinionFrameBindService itemOpinionFrameBindService;

    private final OpinionFrameOneClickSetService opinionFrameOneClickSetService;

    private final ProcessTrackService processTrackService;

    private final PersonRoleApi personRoleApi;

    private final PersonApi personApi;

    private final TaskApi taskApi;

    private final HistoricTaskApi historicTaskApi;

    private final SpmApproveItemService spmApproveItemService;

    private final RepositoryApi repositoryApi;

    private final OrgUnitApi orgUnitApi;

    private final HistoricProcessApi historicProcessApi;

    private final ProcessParamService processParamService;

    private final AsyncHandleService asyncHandleService;

    private final OpinionHistoryRepository opinionHistoryRepository;

    private final PositionApi positionApi;

    private final VariableApi variableApi;

    @Override
    public Boolean checkSignOpinion(String processSerialNumber, String taskId) {
        boolean isSign = false;
        int count = 0;
        if (StringUtils.isEmpty(taskId)) {
            count = this.findByProcSerialNumber(processSerialNumber);
            if (count > 0) {
                isSign = true;
            }
            return isSign;
        }

        count = this.getCountByTaskId(taskId);
        if (count > 0) {
            isSign = true;
        }
        return isSign;
    }

    @Override
    @Transactional
    public void copy(String oldProcessSerialNumber, String oldOpinionFrameMark, String newProcessSerialNumber,
        String newOpinionFrameMark, String newProcessInstanceId, String newTaskId) throws Exception {
        try {
            List<Opinion> oldOpinionList = this.listByProcessSerialNumber(oldProcessSerialNumber);
            for (Opinion oldOpinion : oldOpinionList) {
                oldOpinion.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                oldOpinion.setOpinionFrameMark(newOpinionFrameMark);
                oldOpinion.setProcessSerialNumber(newProcessSerialNumber);
                this.save(oldOpinion);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public int countOpinionHistory(String processSerialNumber, String opinionFrameMark) {
        return opinionHistoryRepository.countByProcessSerialNumberAndOpinionFrameMark(processSerialNumber,
            opinionFrameMark);
    }

    @Override
    @Transactional
    public void delete(String id) {
        Opinion oldOpinion = opinionRepository.findById(id).orElse(null);
        opinionRepository.delete(oldOpinion);
        asyncHandleService.saveOpinionHistory(Y9LoginUserHolder.getTenantId(), oldOpinion, "2");
    }

    @Override
    public int findByProcSerialNumber(String processSerialNumber) {
        return opinionRepository.findByProcSerialNumber(processSerialNumber);
    }

    @Override
    public Opinion findByPsnsAndTaskIdAndOfidAndUserId(String processSerialNumber, String taskId, String opinionFrameId,
        String userId) {
        return opinionRepository.findByPsnsAndTaskIdAndOfidAndUserId(processSerialNumber, taskId, opinionFrameId,
            userId);
    }

    @Override
    public Opinion getById(String id) {
        return opinionRepository.findById(id).orElse(null);
    }

    @Override
    public Integer getCount4Personal(String processSerialNumber, String category, String userId) {
        return opinionRepository.getCount4Personal(processSerialNumber, category, userId);
    }

    @Override
    public Integer getCount4Personal(String processSerialNumber, String taskId, String opinionFrameId, String userId) {
        return opinionRepository.getCount4Personal(processSerialNumber, taskId, opinionFrameId, userId);
    }

    @Override
    public int getCountByTaskId(String taskId) {
        return opinionRepository.getCountByTaskId(taskId);
    }

    @Override
    public List<Opinion> listByProcessSerialNumber(String processSerialNumber) {
        return opinionRepository.findByProcessSerialNumber(processSerialNumber);
    }

    @Override
    public List<Opinion> listByTaskId(String taskId) {
        return opinionRepository.findByTaskId(taskId);
    }

    @Override
    public List<Opinion> listByTaskIdAndPositionIdAndProcessTrackIdIsNull(String taskId, String positionId) {
        return opinionRepository.findByTaskIdAndPositionIdAndProcessTrackIdIsNull(taskId, positionId);
    }

    @Override
    public List<Opinion> listByTaskIdAndProcessTrackId(String taskId, String processTrackId) {
        return opinionRepository.findByTaskIdAndProcessTrackIdOrderByCreateDateDesc(taskId, processTrackId);
    }

    @Override
    public List<Opinion> listByTaskIdAndUserIdAndProcessTrackIdIsNull(String taskId, String userId) {
        return opinionRepository.findByTaskIdAndUserIdAndProcessTrackIdIsNull(taskId, userId);
    }

    @Override
    public List<OpinionHistoryModel> listOpinionHistory(String processSerialNumber, String opinionFrameMark) {
        List<OpinionHistoryModel> resList = new ArrayList<>();
        try {
            List<OpinionHistory> list = opinionHistoryRepository
                .findByProcessSerialNumberAndOpinionFrameMark(processSerialNumber, opinionFrameMark);
            List<Opinion> list1 =
                opinionRepository.findByProcSerialNumberAndOpinionFrameMark(processSerialNumber, opinionFrameMark);
            for (OpinionHistory his : list) {
                OpinionHistoryModel historyModel = new OpinionHistoryModel();
                Y9BeanUtil.copyProperties(his, historyModel);
                resList.add(historyModel);
            }
            for (Opinion opinion : list1) {
                OpinionHistoryModel history = new OpinionHistoryModel();
                history.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                history.setContent(opinion.getContent());
                history.setCreateDate(opinion.getCreateDate());
                history.setSaveDate("");
                history.setDeptId(opinion.getDeptId());
                history.setDeptName(opinion.getDeptName());
                history.setModifyDate(opinion.getModifyDate());
                history.setOpinionFrameMark(opinion.getOpinionFrameMark());
                history.setOpinionType("");
                history.setProcessInstanceId(opinion.getProcessInstanceId());
                history.setProcessSerialNumber(opinion.getProcessSerialNumber());
                history.setTaskId(opinion.getTaskId());
                history.setTenantId(opinion.getTenantId());
                history.setUserId(opinion.getUserId());
                history.setUserName(opinion.getUserName());
                resList.add(history);
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Collections.sort(resList, new Comparator<OpinionHistoryModel>() {
                @Override
                public int compare(OpinionHistoryModel o1, OpinionHistoryModel o2) {
                    try {
                        String startTime1 = o1.getCreateDate();
                        String startTime2 = o2.getCreateDate();
                        long time1 = sdf.parse(startTime1).getTime();
                        long time2 = sdf.parse(startTime2).getTime();
                        if (time1 > time2) {
                            return 1;
                        } else if (time1 == time2) {
                            String modifyDate1 = o1.getModifyDate();
                            String modifyDate2 = o2.getModifyDate();
                            if (StringUtils.isBlank(modifyDate1)) {
                                return -1;
                            } else if (StringUtils.isBlank(modifyDate2)) {
                                return 1;
                            } else {
                                long time11 = sdf.parse(modifyDate1).getTime();
                                long time22 = sdf.parse(modifyDate2).getTime();
                                if (time11 > time22) {
                                    return 1;
                                } else {
                                    return -1;
                                }
                            }
                        } else {
                            return -1;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return -1;
                }
            });
        } catch (BeansException e) {
            e.printStackTrace();
        }
        return resList;
    }

    @Override
    public List<OpinionListModel> listPersonComment(String processSerialNumber, String taskId, String itembox,
        String opinionFrameMark, String itemId, String taskDefinitionKey, String activitiUser, String orderByUser) {
        List<OpinionListModel> resList = new ArrayList<>();
        try {
            UserInfo person = Y9LoginUserHolder.getUserInfo();
            String tenantId = Y9LoginUserHolder.getTenantId(), personId = person.getPersonId();
            OpinionListModel model = new OpinionListModel();
            List<OpinionFrameOneClickSetModel> oneClickSetList = new ArrayList<>();
            // 代录意见权限
            model.setAddable(true);
            model.setAddAgent(false);
            model.setOpinionFrameMark(opinionFrameMark);
            model.setOneClickSetList(oneClickSetList);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            List<Opinion> list =
                opinionRepository.findByProcSerialNumberAndOpinionFrameMark(processSerialNumber, opinionFrameMark);
            // 按岗位排序号排序
            if (StringUtils.isNotBlank(orderByUser) && orderByUser.equals("1") && list.size() > 1) {
                for (Opinion opinion : list) {
                    String positionId = opinion.getPositionId();
                    Position position = positionApi.get(tenantId, positionId).getData();
                    opinion.setOrderStr(
                        (position != null && position.getOrderedPath() != null) ? position.getOrderedPath() : "");
                }
                list = list.stream().sorted().collect(Collectors.toList());
            }
            if (itembox.equalsIgnoreCase(ItemBoxTypeEnum.DRAFT.getValue())
                || itembox.equalsIgnoreCase(ItemBoxTypeEnum.ADD.getValue())) {
                if (!list.isEmpty()) {
                    model.setAddable(true);
                    for (Opinion opinion : list) {
                        OpinionListModel model0 = new OpinionListModel();
                        opinion.setContent(CommentUtil.replaceEnter2Br(opinion.getContent()));
                        opinion.setModifyDate(sdf1.format(sdf.parse(opinion.getModifyDate())));
                        opinion.setCreateDate(sdf1.format(sdf.parse(opinion.getCreateDate())));
                        if (personId.equals(opinion.getUserId())) {
                            model0.setEditable(true);
                            model.setAddable(false);
                        }
                        OpinionModel opinionModel = new OpinionModel();
                        Y9BeanUtil.copyProperties(opinion, opinionModel);
                        if (StringUtils.isNotBlank(opinion.getPositionId())
                            && StringUtils.isBlank(opinion.getPositionName())) {
                            OrgUnit user =
                                orgUnitApi.getOrgUnitPersonOrPosition(tenantId, opinion.getPositionId()).getData();
                            opinionModel.setPositionName(user != null ? user.getName() : "");
                        }
                        PersonExt personExt =
                            personApi.getPersonExtByPersonId(tenantId, opinionModel.getUserId()).getData();
                        opinionModel.setSign(personExt != null ? personExt.getSign() : null);
                        model0.setOpinion(opinionModel);
                        resList.add(model0);
                    }
                    boolean addable = model.getAddable();
                    if (!addable) {
                        // 没有意见框编辑权限时，增加代录权限
                        // boolean hasRole1 = personRoleApi.hasRole(Y9LoginUserHolder.getTenantId(), "itemAdmin", "",
                        // "代录意见角色", person.getPersonId()).getData();
                        // if (hasRole1) {
                        // addableMap.put("addAgent", true);
                        // }
                    }
                    resList.add(model);
                    return resList;
                }
                /**
                 * 当前意见框,不存在意见，则判断是否可以签写意见
                 */
                model.setAddable(false);
                SpmApproveItem item = spmApproveItemService.findById(itemId);
                String proDefKey = item.getWorkflowGuid();
                ProcessDefinitionModel latestpd =
                    repositoryApi.getLatestProcessDefinitionByKey(tenantId, proDefKey).getData();
                String processDefinitionId = latestpd.getId();
                ItemOpinionFrameBind bind =
                    itemOpinionFrameBindService.findByItemIdAndProcessDefinitionIdAndTaskDefKeyAndOpinionFrameMark(
                        itemId, processDefinitionId, taskDefinitionKey, opinionFrameMark);
                if (null != bind) {
                    // 是否必填意见，与addable一起判定，都为true时提示必填。
                    model.setSignOpinion(bind.isSignOpinion());
                    List<String> roleIds = bind.getRoleIds();
                    if (!roleIds.isEmpty()) {
                        for (String roleId : roleIds) {
                            Boolean hasRole = personRoleApi.hasRole(tenantId, roleId, personId).getData();
                            if (Boolean.TRUE.equals(hasRole)) {
                                model.setAddable(true);
                                break;
                            }
                        }
                    } else {
                        model.setAddable(true);
                    }
                }
                boolean addable = model.getAddable();
                if (!addable) {
                    // 没有意见框编辑权限时，增加代录权限
                    // boolean hasRole1 = personRoleApi.hasRole(Y9LoginUserHolder.getTenantId(), "itemAdmin", "",
                    // "代录意见角色", person.getPersonId()).getData();
                    // if (hasRole1) {
                    // addableMap.put("addAgent", true);
                    // }
                }
            } else if (itembox.equalsIgnoreCase(ItemBoxTypeEnum.TODO.getValue())) {
                /**
                 * 用户未签收前打开公文时(办理人为空)，只读所有意见
                 */
                if (StringUtils.isBlank(activitiUser)) {
                    model.setAddable(false);
                    for (Opinion opinion : list) {
                        OpinionListModel model0 = new OpinionListModel();
                        opinion.setContent(CommentUtil.replaceEnter2Br(opinion.getContent()));
                        if (!opinion.getCreateDate().equals(opinion.getModifyDate())) {
                            model0.setIsEdit(true);
                        }
                        opinion.setModifyDate(sdf1.format(sdf.parse(opinion.getModifyDate())));
                        opinion.setCreateDate(sdf1.format(sdf.parse(opinion.getCreateDate())));
                        model0.setEditable(false);
                        OpinionModel opinionModel = new OpinionModel();
                        Y9BeanUtil.copyProperties(opinion, opinionModel);
                        if (StringUtils.isNotBlank(opinion.getPositionId())
                            && StringUtils.isBlank(opinion.getPositionName())) {
                            OrgUnit user =
                                orgUnitApi.getOrgUnitPersonOrPosition(tenantId, opinion.getPositionId()).getData();
                            opinionModel.setPositionName(user != null ? user.getName() : "");
                        }
                        PersonExt personExt =
                            personApi.getPersonExtByPersonId(tenantId, opinionModel.getUserId()).getData();
                        opinionModel.setSign(personExt != null ? personExt.getSign() : null);
                        model0.setOpinion(opinionModel);
                        resList.add(model0);
                    }
                    resList.add(model);
                    return resList;
                }
                TaskModel task = taskApi.findById(tenantId, taskId).getData();
                String takeBack = variableApi.getVariableLocal(tenantId, taskId, SysVariables.TAKEBACK).getData();
                for (Opinion opinion : list) {
                    OpinionListModel model0 = new OpinionListModel();
                    opinion.setContent(CommentUtil.replaceEnter2Br(opinion.getContent()));
                    if (!opinion.getCreateDate().equals(opinion.getModifyDate())) {
                        model0.setIsEdit(true);
                    }
                    opinion.setModifyDate(sdf1.format(sdf.parse(opinion.getModifyDate())));
                    opinion.setCreateDate(sdf1.format(sdf.parse(opinion.getCreateDate())));
                    OpinionModel opinionModel = new OpinionModel();
                    Y9BeanUtil.copyProperties(opinion, opinionModel);
                    if (StringUtils.isNotBlank(opinion.getPositionId())
                        && StringUtils.isBlank(opinion.getPositionName())) {
                        OrgUnit user =
                            orgUnitApi.getOrgUnitPersonOrPosition(tenantId, opinion.getPositionId()).getData();
                        opinionModel.setPositionName(user != null ? user.getName() : "");
                    }
                    PersonExt personExt =
                        personApi.getPersonExtByPersonId(tenantId, opinionModel.getUserId()).getData();
                    opinionModel.setSign(personExt != null ? personExt.getSign() : null);
                    model0.setOpinion(opinionModel);
                    model0.setEditable(false);

                    if (taskId.equals(opinion.getTaskId())) {
                        if (personId.equals(opinion.getUserId())) {
                            model0.setEditable(true);
                            model.setAddable(false);
                        }
                    } else {// 收回件可编辑意见
                        if (takeBack != null && Boolean.valueOf(takeBack)
                            && Y9LoginUserHolder.getPersonId().equals(opinion.getUserId())) {
                            List<HistoricTaskInstanceModel> tlist = historicTaskApi
                                .findTaskByProcessInstanceIdOrByEndTimeAsc(tenantId, task.getProcessInstanceId(), "")
                                .getData();
                            for (int i = tlist.size() - 1; i >= 0; i--) {
                                HistoricTaskInstanceModel htimodel = tlist.get(i);
                                if (htimodel.getEndTime() != null && htimodel.getId().equals(opinion.getTaskId())) {// 找到收回前的上一个任务
                                    model0.setEditable(true);
                                    model.setAddable(false);
                                    break;
                                }
                            }
                        }
                    }
                    resList.add(model0);
                }
                ItemOpinionFrameBind setBind =
                    itemOpinionFrameBindService.findByItemIdAndProcessDefinitionIdAndTaskDefKeyAndOpinionFrameMark(
                        itemId, task.getProcessDefinitionId(), taskDefinitionKey, opinionFrameMark);
                if (null != setBind) {
                    oneClickSetList = opinionFrameOneClickSetService.findByBindIdModel(setBind.getId());
                    if (null != oneClickSetList && !oneClickSetList.isEmpty()) {
                        model.setOneClickSetList(oneClickSetList);
                    }
                }
                /**
                 * 当前意见框,当前人员可以新增意见时，要判断当前人员是否有在该意见框签意见的权限
                 */
                Boolean addableTemp = model.getAddable();
                if (addableTemp) {
                    model.setAddable(false);
                    // ItemOpinionFrameBind bind =
                    // itemOpinionFrameBindService.findByItemIdAndProcessDefinitionIdAndTaskDefKeyAndOpinionFrameMark(
                    // itemId, task.getProcessDefinitionId(), taskDefinitionKey, opinionFrameMark);
                    if (null != setBind) {
                        // 是否必填意见，与addable一起判定，都为true时提示必填。
                        model.setSignOpinion(setBind.isSignOpinion());
                        List<String> roleIds = setBind.getRoleIds();
                        if (roleIds.isEmpty()) {
                            model.setAddable(true);
                        } else {
                            for (String roleId : roleIds) {
                                Boolean hasRole = false;
                                /**
                                 * 处理todo时，当前任务为委托产生时的情况-开始
                                 */
                                if (itembox.equalsIgnoreCase(ItemBoxTypeEnum.TODO.getValue())) {
                                    /*boolean isEntrust = entrustDetailService.haveEntrustDetailByTaskId(taskId);
                                    if (isEntrust) {
                                        EntrustDetail entrustDetail = entrustDetailService.findByTaskId(taskId);
                                        String ownerId = entrustDetail.getOwnerId();
                                        *//**
                                            * 把当前人换为委托改任务的人，委托人有意见签写意见，当前人就有签写意见的权限
                                            *//*
                                              hasRole = positionRoleApi.hasRole(tenantId, roleId, ownerId);
                                              if (hasRole) {
                                               addableMap.put("addable", true);
                                               continue;
                                              }
                                              } else {*/
                                    hasRole = personRoleApi.hasRole(tenantId, roleId, personId).getData();
                                    if (Boolean.TRUE.equals(hasRole)) {
                                        model.setAddable(true);
                                        continue;
                                    }
                                    // }
                                } else {
                                    hasRole = personRoleApi.hasRole(tenantId, roleId, personId).getData();
                                    if (Boolean.TRUE.equals(hasRole)) {
                                        model.setAddable(true);
                                        continue;
                                    }
                                }
                            }
                        }
                    }
                }
                // 代录权限控制
                if (StringUtils.isNotBlank(taskId)) {
                    boolean hasRole = personRoleApi
                        .hasRole(Y9LoginUserHolder.getTenantId(), "itemAdmin", "", "代录意见角色", person.getPersonId())
                        .getData();
                    if (hasRole) {
                        // 没有意见框编辑权限时，增加代录权限
                        Boolean addable = model.getAddable();
                        if (!addable) {
                            model.setAddAgent(true);
                        }
                    }
                }
            } else if (itembox.equalsIgnoreCase(ItemBoxTypeEnum.DONE.getValue())
                || itembox.equalsIgnoreCase(ItemBoxTypeEnum.DOING.getValue())
                || itembox.equalsIgnoreCase(ItemBoxTypeEnum.RECYCLE.getValue())
                || itembox.equalsIgnoreCase(ItemBoxTypeEnum.COPY.getValue())
                || itembox.equalsIgnoreCase(ItemBoxTypeEnum.MONITOR_DOING.getValue())
                || itembox.equalsIgnoreCase(ItemBoxTypeEnum.MONITOR_DONE.getValue())) {
                model.setAddable(false);
                for (Opinion opinion : list) {
                    OpinionListModel model0 = new OpinionListModel();
                    opinion.setContent(CommentUtil.replaceEnter2Br(opinion.getContent()));
                    if (!opinion.getCreateDate().equals(opinion.getModifyDate())) {
                        model0.setIsEdit(true);
                    }
                    opinion.setModifyDate(sdf1.format(sdf.parse(opinion.getModifyDate())));
                    opinion.setCreateDate(sdf1.format(sdf.parse(opinion.getCreateDate())));
                    OpinionModel opinionModel = new OpinionModel();
                    Y9BeanUtil.copyProperties(opinion, opinionModel);
                    if (StringUtils.isNotBlank(opinion.getPositionId())
                        && StringUtils.isBlank(opinion.getPositionName())) {
                        OrgUnit user =
                            orgUnitApi.getOrgUnitPersonOrPosition(tenantId, opinion.getPositionId()).getData();
                        opinionModel.setPositionName(user != null ? user.getName() : "");
                    }
                    PersonExt personExt =
                        personApi.getPersonExtByPersonId(tenantId, opinionModel.getUserId()).getData();
                    opinionModel.setSign(personExt != null ? personExt.getSign() : null);
                    model0.setOpinion(opinionModel);
                    model0.setEditable(false);
                    resList.add(model0);
                }
            } else if (itembox.equalsIgnoreCase(ItemBoxTypeEnum.YUEJIAN.getValue())) {
                // 是否已办结
                boolean isEnd = false;
                try {
                    ProcessParam processParam = processParamService.findByProcessSerialNumber(processSerialNumber);
                    // 办结件，阅件不可填写意见
                    if (processParam != null) {
                        HistoricProcessInstanceModel historicProcessInstanceModel =
                            historicProcessApi.getById(tenantId, processParam.getProcessInstanceId()).getData();
                        boolean b = historicProcessInstanceModel == null || (historicProcessInstanceModel != null
                            && historicProcessInstanceModel.getEndTime() != null);
                        if (b) {
                            model.setAddable(false);
                            isEnd = true;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                for (Opinion opinion : list) {
                    OpinionListModel model0 = new OpinionListModel();
                    opinion.setContent(CommentUtil.replaceEnter2Br(opinion.getContent()));
                    if (!opinion.getCreateDate().equals(opinion.getModifyDate())) {
                        model0.setIsEdit(true);
                    }
                    opinion.setModifyDate(sdf1.format(sdf.parse(opinion.getModifyDate())));
                    opinion.setCreateDate(sdf1.format(sdf.parse(opinion.getCreateDate())));
                    OpinionModel opinionModel = new OpinionModel();
                    Y9BeanUtil.copyProperties(opinion, opinionModel);
                    if (StringUtils.isNotBlank(opinion.getPositionId())
                        && StringUtils.isBlank(opinion.getPositionName())) {
                        OrgUnit user =
                            orgUnitApi.getOrgUnitPersonOrPosition(tenantId, opinion.getPositionId()).getData();
                        opinionModel.setPositionName(user != null ? user.getName() : "");
                    }
                    PersonExt personExt =
                        personApi.getPersonExtByPersonId(tenantId, opinionModel.getUserId()).getData();
                    opinionModel.setSign(personExt != null ? personExt.getSign() : null);
                    model0.setOpinion(opinionModel);
                    model0.setEditable(false);
                    if (personId.equals(opinion.getUserId()) && !isEnd) {
                        model0.setEditable(true);
                        model.setAddable(false);
                    }
                    resList.add(model0);
                }
                /**
                 * 当前意见框,当前人员可以新增意见时，要判断当前人员是否有在该意见框签意见的权限
                 */
                Boolean addableTemp = model.getAddable();
                if (Boolean.TRUE.equals(addableTemp)) {
                    model.setAddable(false);
                    TaskModel task = taskApi.findById(tenantId, taskId).getData();
                    ItemOpinionFrameBind bind =
                        itemOpinionFrameBindService.findByItemIdAndProcessDefinitionIdAndTaskDefKeyAndOpinionFrameMark(
                            itemId, task.getProcessDefinitionId(), taskDefinitionKey, opinionFrameMark);
                    if (null != bind) {
                        List<String> roleIds = bind.getRoleIds();
                        if (roleIds.isEmpty()) {
                            model.setAddable(true);
                        } else {
                            for (String roleId : roleIds) {
                                Boolean hasRole = personRoleApi.hasRole(tenantId, roleId, personId).getData();
                                if (Boolean.TRUE.equals(hasRole)) {
                                    model.setAddable(true);
                                    continue;
                                }
                            }
                        }
                    }
                }
            }
            resList.add(model);
        } catch (

        ParseException e) {
            e.printStackTrace();
        }
        return resList;
    }

    @Override
    @Transactional
    public void save(List<Opinion> entities) {
        opinionRepository.saveAll(entities);
    }

    @Override
    @Transactional
    public void save(Opinion entity) {
        opinionRepository.save(entity);
    }

    @Override
    @Transactional
    public Opinion saveOrUpdate(Opinion entity) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId();
        OrgUnit user = Y9LoginUserHolder.getOrgUnit();
        String orgUnitId = Y9LoginUserHolder.getOrgUnitId();
        String userName = person.getName();
        String personId = person.getPersonId();
        String id = entity.getId();
        if (StringUtils.isBlank(id)) {
            Opinion o = new Opinion();
            o.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            o.setUserId(personId);
            o.setUserName(userName);
            o.setDeptId(user.getParentId());
            OrgUnit orgUnit = orgUnitApi.getOrgUnit(tenantId, user.getParentId()).getData();
            o.setDeptName(orgUnit.getName());
            o.setProcessSerialNumber(entity.getProcessSerialNumber());
            o.setProcessInstanceId(entity.getProcessInstanceId());
            o.setTaskId(entity.getTaskId());
            o.setOpinionFrameMark(entity.getOpinionFrameMark());
            o.setTenantId(StringUtils.isNotBlank(entity.getTenantId()) ? entity.getTenantId() : tenantId);
            o.setContent(entity.getContent());
            o.setCreateDate(sdf.format(new Date()));
            o.setModifyDate(sdf.format(new Date()));
            o.setPositionId(orgUnitId);
            o.setPositionName(user.getName());
            if (StringUtils.isNotBlank(entity.getTaskId())) {
                try {
                    List<ProcessTrack> list = processTrackService.listByTaskIdAndEndTimeIsNull(entity.getTaskId());
                    // 处理恢复待办后,填写意见错位问题,意见显示在自定义历程上
                    if (!list.isEmpty()) {
                        o.setProcessTrackId(list.get(0).getId());
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                /*try {
                    HistoricTaskInstanceModel historicTaskInstanceModel = historictaskApi.getById(tenantId, entity.getTaskId());
                    EntrustDetail entrustDetail = entrustDetailService.findByTaskId(historicTaskInstanceModel.getId());
                    if (entrustDetail != null) {
                        if (historicTaskInstanceModel.getAssignee().contains(positionId)) {
                            String idTemp = entrustDetail.getOwnerId();
                            Person p = personApi.getPerson(tenantId, idTemp);
                            if (isAgent != 1) {
                                o.setUserName(userName + "(" + p.getName() + "委托)");
                            } else {
                                o.setAgentUserName(userName + "(" + p.getName() + "委托)");
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
            }
            opinionRepository.save(o);
            asyncHandleService.sendMsgRemind(tenantId, orgUnitId, entity.getProcessSerialNumber(), entity.getContent());
            return o;
        }
        Opinion opinion = opinionRepository.findById(id).orElse(null);
        Opinion oldOpinion = new Opinion();
        Y9BeanUtil.copyProperties(opinion, oldOpinion);
        opinion.setUserId(person.getPersonId());
        opinion.setUserName(userName);
        opinion.setTaskId(entity.getTaskId());
        opinion.setModifyDate(sdf.format(new Date()));
        opinion.setContent(entity.getContent());
        opinion.setProcessInstanceId(entity.getProcessInstanceId());
        opinion.setTenantId(StringUtils.isNotBlank(entity.getTenantId()) ? entity.getTenantId() : tenantId);
        OrgUnit orgUnit0 = orgUnitApi.getOrgUnit(tenantId, user.getParentId()).getData();
        opinion.setDeptId(user.getParentId());
        opinion.setDeptName(orgUnit0.getName());
        opinion.setPositionId(orgUnitId);
        opinion.setPositionName(user.getName());
        /*if (StringUtils.isNotBlank(entity.getTaskId())) {
            try {
                HistoricTaskInstanceModel historicTaskInstanceModel = historictaskApi.getById(tenantId, entity.getTaskId());
                EntrustDetail entrustDetail = entrustDetailService.findByTaskId(historicTaskInstanceModel.getId());
                if (entrustDetail != null) {
                    if (historicTaskInstanceModel.getAssignee().contains(positionId)) {
                        String idTemp = entrustDetail.getOwnerId();
                        Person p = personApi.getPerson(tenantId, idTemp);
                        if (isAgent != 1) {
                            opinion.setUserName(userName + "(" + p.getName() + "委托)");
                        } else {
                            opinion.setAgentUserName(userName + "(" + p.getName() + "委托)");
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
        opinionRepository.save(opinion);
        asyncHandleService.sendMsgRemind(tenantId, orgUnitId, entity.getProcessSerialNumber(), entity.getContent());
        // 修改意见保存历史记录
        asyncHandleService.saveOpinionHistory(Y9LoginUserHolder.getTenantId(), oldOpinion, "1");
        return opinion;
    }

    @Override
    @Transactional
    public void update(String processSerialNumber, String processInstanceId, String taskId) {
        opinionRepository.update(processInstanceId, taskId, processSerialNumber);
    }

    @Override
    @Transactional
    public void updateOpinion(String id, String content) {
        Opinion opinion = opinionRepository.findById(id).orElse(null);
        if (opinion != null) {
            opinion.setContent(content);
            opinionRepository.save(opinion);
        }
    }

}
