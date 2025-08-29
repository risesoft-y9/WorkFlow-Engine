package net.risesoft.service.opinion.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.api.platform.permission.cache.PersonRoleApi;
import net.risesoft.api.processadmin.HistoricProcessApi;
import net.risesoft.api.processadmin.HistoricTaskApi;
import net.risesoft.api.processadmin.RepositoryApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.api.processadmin.VariableApi;
import net.risesoft.consts.processadmin.SysVariables;
import net.risesoft.entity.Item;
import net.risesoft.entity.ProcessParam;
import net.risesoft.entity.ProcessTrack;
import net.risesoft.entity.opinion.ItemOpinionFrameBind;
import net.risesoft.entity.opinion.Opinion;
import net.risesoft.entity.opinion.OpinionHistory;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.OpinionFrameModel;
import net.risesoft.model.itemadmin.OpinionFrameOneClickSetModel;
import net.risesoft.model.itemadmin.OpinionHistoryModel;
import net.risesoft.model.itemadmin.OpinionListModel;
import net.risesoft.model.itemadmin.OpinionModel;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.model.platform.org.PersonExt;
import net.risesoft.model.platform.org.Position;
import net.risesoft.model.processadmin.HistoricProcessInstanceModel;
import net.risesoft.model.processadmin.HistoricTaskInstanceModel;
import net.risesoft.model.processadmin.ProcessDefinitionModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.repository.opinion.OpinionHistoryRepository;
import net.risesoft.repository.opinion.OpinionRepository;
import net.risesoft.service.AsyncHandleService;
import net.risesoft.service.ProcessTrackService;
import net.risesoft.service.config.ItemOpinionFrameBindService;
import net.risesoft.service.core.ItemService;
import net.risesoft.service.core.ProcessParamService;
import net.risesoft.service.opinion.OpinionFrameOneClickSetService;
import net.risesoft.service.opinion.OpinionService;
import net.risesoft.service.setting.ItemSettingService;
import net.risesoft.util.CommentUtil;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Slf4j
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

    private final ItemService itemService;

    private final RepositoryApi repositoryApi;

    private final OrgUnitApi orgUnitApi;

    private final HistoricProcessApi historicProcessApi;

    private final ProcessParamService processParamService;

    private final AsyncHandleService asyncHandleService;

    private final OpinionHistoryRepository opinionHistoryRepository;

    private final PositionApi positionApi;

    private final VariableApi variableApi;

    private final ItemSettingService itemSettingService;

    @Override
    public Boolean checkSignOpinion(String processSerialNumber, String taskId) {
        boolean isSign = false;
        int count;
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
    public int countOpinionHistory(String processSerialNumber, String opinionFrameMark) {
        return opinionHistoryRepository.countByProcessSerialNumberAndOpinionFrameMark(processSerialNumber,
            opinionFrameMark);
    }

    @Override
    @Transactional
    public void delete(String id) {
        Optional<Opinion> oldOpinion = opinionRepository.findById(id);
        if (oldOpinion.isPresent()) {
            opinionRepository.delete(oldOpinion.get());
            asyncHandleService.saveOpinionHistory(Y9LoginUserHolder.getTenantId(), oldOpinion.get(), "2");
        }
    }

    @Override
    public int findByProcSerialNumber(String processSerialNumber) {
        return opinionRepository.findByProcSerialNumber(processSerialNumber);
    }

    @Override
    public Opinion getById(String id) {
        return opinionRepository.findById(id).orElse(null);
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
            resList.sort((o1, o2) -> {
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
                        } else if (modifyDate1.equals(modifyDate2)) {
                            return 0;
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
                    LOGGER.error("意见的CreateDate或者ModifyDate格式化错误！", e);
                }
                return -1;
            });
        } catch (BeansException e) {
            e.printStackTrace();
        }
        return resList;
    }

    @Override
    public List<OpinionListModel> listPersonComment(String processSerialNumber, String taskId, String itembox,
        String opinionFrameMark, String itemId, String taskDefinitionKey) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        OpinionListModel opinionListModel = new OpinionListModel();
        opinionListModel.setAddable(true);
        opinionListModel.setOpinionFrameMark(opinionFrameMark);
        opinionListModel.setOneClickSetList(new ArrayList<>());
        List<Opinion> opinionList =
            opinionRepository.findByProcSerialNumberAndOpinionFrameMark(processSerialNumber, opinionFrameMark);
        // 按岗位排序号排序
        boolean opinionOrderBy = itemSettingService.getConfSetting().isOpinionOrderBy();
        if (opinionOrderBy && opinionList.size() > 1) {
            for (Opinion opinion : opinionList) {
                String positionId = opinion.getPositionId();
                Position position = positionApi.get(tenantId, positionId).getData();
                opinion.setOrderStr(
                    (position != null && position.getOrderedPath() != null) ? position.getOrderedPath() : "");
            }
            opinionList = opinionList.stream().sorted().collect(Collectors.toList());
        }
        switch (ItemBoxTypeEnum.fromString(itembox)) {
            case DRAFT:
            case ADD:
                return this.listPersonComment4AddOrDraft(itemId, opinionListModel, opinionList, opinionFrameMark,
                    taskDefinitionKey);
            case TODO:
                return this.listPersonComment4Todo(itemId, opinionListModel, opinionList, opinionFrameMark, taskId);
            case YUE_JIAN:
                return this.listPersonComment4ChaoSong(processSerialNumber, itemId, opinionListModel, opinionList,
                    opinionFrameMark, taskId, taskDefinitionKey);
            default:
                return this.listPersonComment4Other(opinionListModel, opinionList);
        }
    }

    @Override
    public OpinionFrameModel listPersonCommentNew(String processSerialNumber, String taskId, String itembox,
        String opinionFrameMark, String itemId, String taskDefinitionKey) {
        OpinionFrameModel opinionFrameModel = new OpinionFrameModel();
        opinionFrameModel.setAddable(true);
        opinionFrameModel.setMark(opinionFrameMark);
        opinionFrameModel.setOneClickSetList(new ArrayList<>());
        boolean opinionOrderBy = itemSettingService.getConfSetting().isOpinionOrderBy();
        List<Opinion> opinionList =
            opinionRepository.findByProcSerialNumberAndOpinionFrameMark(processSerialNumber, opinionFrameMark);
        switch (ItemBoxTypeEnum.fromString(itembox)) {
            case DRAFT:
            case ADD:
                this.listPersonComment4AddOrDraftNew(itemId, opinionFrameModel, opinionList, opinionFrameMark,
                    taskDefinitionKey, opinionOrderBy);
                break;
            case TODO:
                this.listPersonComment4TodoNew(itemId, opinionFrameModel, opinionList, opinionFrameMark, taskId,
                    opinionOrderBy);
                break;
            case YUE_JIAN:
                this.listPersonComment4ChaoSongNew(processSerialNumber, itemId, opinionFrameModel, opinionList,
                    opinionFrameMark, taskId, taskDefinitionKey, opinionOrderBy);
                break;
            default:
                this.listPersonComment4OtherNew(opinionFrameModel, opinionList, opinionOrderBy);
                break;
        }
        return opinionFrameModel;
    }

    private List<OpinionListModel> listPersonComment4AddOrDraft(String itemId, OpinionListModel opinionListModel,
        List<Opinion> opinionList, String opinionFrameMark, String taskDefKey) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId(), personId = person.getPersonId();
        List<OpinionListModel> resList = new ArrayList<>();
        if (!opinionList.isEmpty()) {
            for (Opinion opinion : opinionList) {
                this.format(opinion);
                OpinionListModel modelTemp = new OpinionListModel();
                modelTemp.setEditable(personId.equals(opinion.getUserId()));
                modelTemp.setOpinion(this.getOpinionModel(opinion));
                resList.add(modelTemp);
            }
            opinionListModel
                .setAddable(opinionList.stream().noneMatch(opinion -> opinion.getUserId().equals(personId)));
            resList.add(opinionListModel);
            return resList;
        }
        /*
         * 当前意见框,不存在意见，则判断是否可以签写意见
         */
        Item item = itemService.findById(itemId);
        String proDefKey = item.getWorkflowGuid();
        ProcessDefinitionModel processDefinition =
            repositoryApi.getLatestProcessDefinitionByKey(tenantId, proDefKey).getData();
        String processDefinitionId = processDefinition.getId();
        ItemOpinionFrameBind itemOpinionFrameBind =
            itemOpinionFrameBindService.findByItemIdAndProcessDefinitionIdAndTaskDefKeyAndOpinionFrameMark(itemId,
                processDefinitionId, taskDefKey, opinionFrameMark);
        this.setAddable(opinionListModel, itemOpinionFrameBind);
        resList.add(opinionListModel);
        return resList;
    }

    private void listPersonComment4AddOrDraftNew(String itemId, OpinionFrameModel opinionFrameModel,
        List<Opinion> opinionList, String opinionFrameMark, String taskDefKey, boolean opinionOrderBy) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId(), personId = person.getPersonId();
        if (!opinionList.isEmpty()) {
            List<OpinionModel> modelList = new ArrayList<>();
            for (Opinion opinion : opinionList) {
                this.format(opinion);
                OpinionModel model = new OpinionModel();
                model.setEditable(opinion.getUserId().equals(personId));
                modelList.add(this.getOpinionModelNew(opinion, model));
            }
            modelList = this.order(modelList, opinionOrderBy);
            opinionFrameModel.setOpinionList(modelList);
            opinionFrameModel.setAddable(modelList.stream().noneMatch(model -> model.getUserId().equals(personId)));
        } else {
            /*
             * 当前意见框,不存在意见，则判断是否可以签写意见
             */
            Item item = itemService.findById(itemId);
            String proDefKey = item.getWorkflowGuid();
            ProcessDefinitionModel processDefinition =
                repositoryApi.getLatestProcessDefinitionByKey(tenantId, proDefKey).getData();
            String processDefinitionId = processDefinition.getId();
            ItemOpinionFrameBind itemOpinionFrameBind =
                itemOpinionFrameBindService.findByItemIdAndProcessDefinitionIdAndTaskDefKeyAndOpinionFrameMark(itemId,
                    processDefinitionId, taskDefKey, opinionFrameMark);
            this.setAddableNew(opinionFrameModel, itemOpinionFrameBind);
        }
    }

    private List<OpinionModel> order(List<OpinionModel> modelList, boolean opinionOrderBy) {
        // 按岗位排序号排序
        if (opinionOrderBy && modelList.size() > 1) {
            for (OpinionModel model : modelList) {
                String positionId = model.getPositionId();
                Position position = positionApi.get(Y9LoginUserHolder.getTenantId(), positionId).getData();
                model.setOrderStr(
                    (position != null && position.getOrderedPath() != null) ? position.getOrderedPath() : "");
            }
            modelList = modelList.stream().sorted().collect(Collectors.toList());
        }
        return modelList;
    }

    private void format(Opinion opinion) {
        SimpleDateFormat sdfSource = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdfTarget = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        opinion.setContent(CommentUtil.replaceEnter2Br(opinion.getContent()));
        try {
            opinion.setModifyDate(sdfTarget.format(sdfSource.parse(opinion.getModifyDate())));
            opinion.setCreateDate(sdfTarget.format(sdfSource.parse(opinion.getCreateDate())));
        } catch (ParseException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private List<OpinionListModel> listPersonComment4Todo(String itemId, OpinionListModel opinionListModel,
        List<Opinion> opinionList, String opinionFrameMark, String taskId) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId(), personId = person.getPersonId();
        List<OpinionListModel> resList = new ArrayList<>();
        /*
         * 用户未签收前打开公文时(办理人为空)，只读所有意见
         */
        TaskModel task = taskApi.findById(tenantId, taskId).getData();
        if (StringUtils.isBlank(task.getAssignee())) {
            opinionListModel.setAddable(false);
            for (Opinion opinion : opinionList) {
                this.format(opinion);
                OpinionListModel modelTemp = new OpinionListModel();
                modelTemp.setEditable(false);
                modelTemp.setOpinion(this.getOpinionModel(opinion));
                resList.add(modelTemp);
            }
            resList.add(opinionListModel);
            return resList;
        }
        opinionListModel.setAddable(true);
        String takeBack = variableApi.getVariableLocal(tenantId, taskId, SysVariables.TAKEBACK).getData();
        List<HistoricTaskInstanceModel> hisTaskList = new ArrayList<>();
        for (Opinion opinion : opinionList) {
            this.format(opinion);
            OpinionListModel modelTemp = new OpinionListModel();
            modelTemp.setOpinion(this.getOpinionModel(opinion));
            modelTemp.setEditable(false);
            if (taskId.equals(opinion.getTaskId())) {
                if (personId.equals(opinion.getUserId())) {
                    modelTemp.setEditable(true);
                    opinionListModel.setAddable(false);
                }
            } else {
                // 收回件可编辑意见
                if (Boolean.parseBoolean(takeBack) && personId.equals(opinion.getUserId())) {
                    if (hisTaskList.isEmpty()) {
                        hisTaskList = historicTaskApi
                            .findTaskByProcessInstanceIdOrByEndTimeAsc(tenantId, task.getProcessInstanceId(), "")
                            .getData();
                    }
                    for (int i = hisTaskList.size() - 1; i >= 0; i--) {
                        HistoricTaskInstanceModel htiModel = hisTaskList.get(i);
                        if (htiModel.getEndTime() != null && htiModel.getId().equals(opinion.getTaskId())) {// 找到收回前的上一个任务
                            modelTemp.setEditable(true);
                            opinionListModel.setAddable(false);
                            break;
                        }
                    }
                }
            }
            resList.add(modelTemp);
        }
        ItemOpinionFrameBind itemOpinionFrameBind =
            itemOpinionFrameBindService.findByItemIdAndProcessDefinitionIdAndTaskDefKeyAndOpinionFrameMark(itemId,
                task.getProcessDefinitionId(), task.getTaskDefinitionKey(), opinionFrameMark);
        if (null != itemOpinionFrameBind) {
            List<OpinionFrameOneClickSetModel> oneClickSetList =
                opinionFrameOneClickSetService.findByBindIdModel(itemOpinionFrameBind.getId());
            if (null != oneClickSetList && !oneClickSetList.isEmpty()) {
                opinionListModel.setOneClickSetList(oneClickSetList);
            }
        }
        this.setAddable(opinionListModel, itemOpinionFrameBind);
        resList.add(opinionListModel);
        return resList;
    }

    private void listPersonComment4TodoNew(String itemId, OpinionFrameModel opinionFrameModel,
        List<Opinion> opinionList, String opinionFrameMark, String taskId, boolean opinionOrderBy) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId(), personId = person.getPersonId();
        List<OpinionModel> modelList = new ArrayList<>();
        /*
         * 用户未签收前打开公文时(办理人为空)，只读所有意见
         */
        TaskModel task = taskApi.findById(tenantId, taskId).getData();
        if (StringUtils.isBlank(task.getAssignee())) {
            opinionFrameModel.setAddable(false);
            for (Opinion opinion : opinionList) {
                this.format(opinion);
                OpinionModel model = new OpinionModel();
                model.setEditable(false);
                modelList.add(this.getOpinionModelNew(opinion, model));
            }
            modelList = this.order(modelList, opinionOrderBy);
            opinionFrameModel.setOpinionList(modelList);
        } else {
            opinionFrameModel.setAddable(true);
            String takeBack = variableApi.getVariableLocal(tenantId, taskId, SysVariables.TAKEBACK).getData();
            List<HistoricTaskInstanceModel> hisTaskList = new ArrayList<>();
            for (Opinion opinion : opinionList) {
                this.format(opinion);
                OpinionModel model = new OpinionModel();
                if (taskId.equals(opinion.getTaskId())) {
                    if (personId.equals(opinion.getUserId())) {
                        model.setEditable(true);
                        opinionFrameModel.setAddable(false);
                    }
                } else {
                    // 收回件可编辑意见
                    if (Boolean.parseBoolean(takeBack) && personId.equals(opinion.getUserId())) {
                        if (hisTaskList.isEmpty()) {
                            hisTaskList = historicTaskApi
                                .findTaskByProcessInstanceIdOrByEndTimeAsc(tenantId, task.getProcessInstanceId(), "")
                                .getData();
                        }
                        for (int i = hisTaskList.size() - 1; i >= 0; i--) {
                            HistoricTaskInstanceModel htiModel = hisTaskList.get(i);
                            if (htiModel.getEndTime() != null && htiModel.getId().equals(opinion.getTaskId())) {// 找到收回前的上一个任务
                                model.setEditable(true);
                                opinionFrameModel.setAddable(false);
                                break;
                            }
                        }
                    }
                }
                modelList.add(this.getOpinionModelNew(opinion, model));
            }
            ItemOpinionFrameBind itemOpinionFrameBind =
                itemOpinionFrameBindService.findByItemIdAndProcessDefinitionIdAndTaskDefKeyAndOpinionFrameMark(itemId,
                    task.getProcessDefinitionId(), task.getTaskDefinitionKey(), opinionFrameMark);
            if (null != itemOpinionFrameBind) {
                List<OpinionFrameOneClickSetModel> oneClickSetList =
                    opinionFrameOneClickSetService.findByBindIdModel(itemOpinionFrameBind.getId());
                if (null != oneClickSetList && !oneClickSetList.isEmpty()) {
                    opinionFrameModel.setOneClickSetList(oneClickSetList);
                }
            }
            this.setAddableNew(opinionFrameModel, itemOpinionFrameBind);
            modelList = this.order(modelList, opinionOrderBy);
            opinionFrameModel.setOpinionList(modelList);
        }
    }

    private void setAddable(OpinionListModel opinionListModel, ItemOpinionFrameBind itemOpinionFrameBind) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId(), personId = person.getPersonId();
        /*
         * 当前意见框,当前人员可以新增意见时，要判断当前人员是否有在该意见框签意见的权限
         */
        if (opinionListModel.getAddable()) {
            opinionListModel.setAddable(false);
            if (null != itemOpinionFrameBind) {
                // 是否必填意见，与addable一起判定，都为true时提示必填。
                opinionListModel.setSignOpinion(itemOpinionFrameBind.isSignOpinion());
                List<String> roleIds = itemOpinionFrameBind.getRoleIds();
                if (roleIds.isEmpty()) {
                    opinionListModel.setAddable(true);
                } else {
                    for (String roleId : roleIds) {
                        Boolean hasRole = personRoleApi.hasRole(tenantId, roleId, personId).getData();
                        if (Boolean.TRUE.equals(hasRole)) {
                            opinionListModel.setAddable(true);
                            break;
                        }
                    }
                }
            }
        }
    }

    private void setAddableNew(OpinionFrameModel opinionFrameModel, ItemOpinionFrameBind itemOpinionFrameBind) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId(), personId = person.getPersonId();
        /*
         * 当前意见框,当前人员可以新增意见时，要判断当前人员是否有在该意见框签意见的权限
         */
        if (opinionFrameModel.getAddable()) {
            opinionFrameModel.setAddable(false);
            if (null != itemOpinionFrameBind) {
                // 是否必填意见，与addable一起判定，都为true时提示必填。
                opinionFrameModel.setSignOpinion(itemOpinionFrameBind.isSignOpinion());
                List<String> roleIds = itemOpinionFrameBind.getRoleIds();
                if (roleIds.isEmpty()) {
                    opinionFrameModel.setAddable(true);
                } else {
                    for (String roleId : roleIds) {
                        Boolean hasRole = personRoleApi.hasRole(tenantId, roleId, personId).getData();
                        if (Boolean.TRUE.equals(hasRole)) {
                            opinionFrameModel.setAddable(true);
                            break;
                        }
                    }
                }
            }
        }
    }

    private OpinionModel getOpinionModel(Opinion opinion) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        OpinionModel opinionModel = new OpinionModel();
        Y9BeanUtil.copyProperties(opinion, opinionModel);
        if (StringUtils.isNotBlank(opinion.getPositionId()) && StringUtils.isBlank(opinion.getPositionName())) {
            OrgUnit user = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, opinion.getPositionId()).getData();
            opinionModel.setPositionName(user != null ? user.getName() : "");
        }
        PersonExt personExt = personApi.getPersonExtByPersonId(tenantId, opinionModel.getUserId()).getData();
        opinionModel.setSign(personExt != null ? personExt.getSign() : null);
        return opinionModel;
    }

    private OpinionModel getOpinionModelNew(Opinion opinion, OpinionModel opinionModel) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId(), personId = person.getPersonId();
        Y9BeanUtil.copyProperties(opinion, opinionModel);
        if (StringUtils.isNotBlank(opinion.getPositionId()) && StringUtils.isBlank(opinion.getPositionName())) {
            OrgUnit user = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, opinion.getPositionId()).getData();
            opinionModel.setPositionName(user != null ? user.getName() : "");
        }
        PersonExt personExt = personApi.getPersonExtByPersonId(tenantId, opinionModel.getUserId()).getData();
        opinionModel.setSign(personExt != null ? personExt.getSign() : null);
        return opinionModel;
    }

    private void listPersonComment4ChaoSongNew(String processSerialNumber, String itemId,
        OpinionFrameModel opinionFrameModel, List<Opinion> opinionList, String opinionFrameMark, String taskId,
        String taskDefinitionKey, boolean opinionOrderBy) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId(), personId = person.getPersonId();
        List<OpinionModel> modelList = new ArrayList<>();
        // 是否已办结
        boolean isEnd = false;
        ProcessParam processParam = processParamService.findByProcessSerialNumber(processSerialNumber);
        // 办结件，阅件不可填写意见
        if (processParam != null) {
            HistoricProcessInstanceModel historicProcessInstanceModel =
                historicProcessApi.getById(tenantId, processParam.getProcessInstanceId()).getData();
            boolean b = historicProcessInstanceModel == null || historicProcessInstanceModel.getEndTime() != null;
            if (b) {
                opinionFrameModel.setAddable(false);
                isEnd = true;
            }
        }
        for (Opinion opinion : opinionList) {
            this.format(opinion);
            OpinionModel model = new OpinionModel();
            if (personId.equals(opinion.getUserId()) && !isEnd) {
                model.setEditable(true);
                opinionFrameModel.setAddable(false);
            }
            modelList.add(this.getOpinionModelNew(opinion, model));
        }
        modelList = this.order(modelList, opinionOrderBy);
        opinionFrameModel.setOpinionList(modelList);
        /*
         * 当前意见框,当前人员可以新增意见时，要判断当前人员是否有在该意见框签意见的权限
         */
        if (Boolean.TRUE.equals(opinionFrameModel.getAddable())) {
            opinionFrameModel.setAddable(false);
            TaskModel task = taskApi.findById(tenantId, taskId).getData();
            ItemOpinionFrameBind bind =
                itemOpinionFrameBindService.findByItemIdAndProcessDefinitionIdAndTaskDefKeyAndOpinionFrameMark(itemId,
                    task.getProcessDefinitionId(), taskDefinitionKey, opinionFrameMark);
            if (null != bind) {
                List<String> roleIds = bind.getRoleIds();
                if (roleIds.isEmpty()) {
                    opinionFrameModel.setAddable(true);
                } else {
                    for (String roleId : roleIds) {
                        Boolean hasRole = personRoleApi.hasRole(tenantId, roleId, personId).getData();
                        if (Boolean.TRUE.equals(hasRole)) {
                            opinionFrameModel.setAddable(true);
                            break;
                        }
                    }
                }
            }
        }
    }

    private List<OpinionListModel> listPersonComment4ChaoSong(String processSerialNumber, String itemId,
        OpinionListModel opinionListModel, List<Opinion> opinionList, String opinionFrameMark, String taskId,
        String taskDefinitionKey) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId(), personId = person.getPersonId();
        List<OpinionListModel> resList = new ArrayList<>();
        // 是否已办结
        boolean isEnd = false;
        try {
            ProcessParam processParam = processParamService.findByProcessSerialNumber(processSerialNumber);
            // 办结件，阅件不可填写意见
            if (processParam != null) {
                HistoricProcessInstanceModel historicProcessInstanceModel =
                    historicProcessApi.getById(tenantId, processParam.getProcessInstanceId()).getData();
                boolean b = historicProcessInstanceModel == null || historicProcessInstanceModel.getEndTime() != null;
                if (b) {
                    opinionListModel.setAddable(false);
                    isEnd = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (Opinion opinion : opinionList) {
            this.format(opinion);
            OpinionListModel modelTemp = new OpinionListModel();
            modelTemp.setOpinion(this.getOpinionModel(opinion));
            modelTemp.setEditable(false);
            if (personId.equals(opinion.getUserId()) && !isEnd) {
                modelTemp.setEditable(true);
                opinionListModel.setAddable(false);
            }
            resList.add(modelTemp);
        }
        /*
         * 当前意见框,当前人员可以新增意见时，要判断当前人员是否有在该意见框签意见的权限
         */
        Boolean addableTemp = opinionListModel.getAddable();
        if (Boolean.TRUE.equals(addableTemp)) {
            opinionListModel.setAddable(false);
            TaskModel task = taskApi.findById(tenantId, taskId).getData();
            ItemOpinionFrameBind bind =
                itemOpinionFrameBindService.findByItemIdAndProcessDefinitionIdAndTaskDefKeyAndOpinionFrameMark(itemId,
                    task.getProcessDefinitionId(), taskDefinitionKey, opinionFrameMark);
            if (null != bind) {
                List<String> roleIds = bind.getRoleIds();
                if (roleIds.isEmpty()) {
                    opinionListModel.setAddable(true);
                } else {
                    for (String roleId : roleIds) {
                        Boolean hasRole = personRoleApi.hasRole(tenantId, roleId, personId).getData();
                        if (Boolean.TRUE.equals(hasRole)) {
                            opinionListModel.setAddable(true);
                            break;
                        }
                    }
                }
            }
        }
        resList.add(opinionListModel);
        return resList;
    }

    /**
     * 只读模式
     * 
     * @param opinionListModel
     * @param opinionList
     * @return List<OpinionListModel>
     */
    private List<OpinionListModel> listPersonComment4Other(OpinionListModel opinionListModel,
        List<Opinion> opinionList) {
        List<OpinionListModel> resList = new ArrayList<>();
        opinionListModel.setAddable(false);
        for (Opinion opinion : opinionList) {
            this.format(opinion);
            OpinionListModel modelTemp = new OpinionListModel();
            modelTemp.setEditable(false);
            modelTemp.setOpinion(this.getOpinionModel(opinion));
            resList.add(modelTemp);
        }
        resList.add(opinionListModel);
        return resList;
    }

    private void listPersonComment4OtherNew(OpinionFrameModel opinionFrameModel, List<Opinion> opinionList,
        boolean opinionOrderBy) {
        List<OpinionModel> modelList = new ArrayList<>();
        opinionFrameModel.setAddable(false);
        for (Opinion opinion : opinionList) {
            this.format(opinion);
            OpinionModel model = new OpinionModel();
            model.setEditable(false);
            modelList.add(this.getOpinionModelNew(opinion, model));
        }
        modelList = this.order(modelList, opinionOrderBy);
        opinionFrameModel.setOpinionList(modelList);
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
                    HistoricTaskInstanceModel historicTaskInstanceModel = historicTaskApi.getById(tenantId, entity.getTaskId());
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
        assert opinion != null;
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
                HistoricTaskInstanceModel historicTaskInstanceModel = historicTaskApi.getById(tenantId, entity.getTaskId());
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
