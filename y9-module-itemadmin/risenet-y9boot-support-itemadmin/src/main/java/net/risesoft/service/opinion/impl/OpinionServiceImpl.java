package net.risesoft.service.opinion.impl;

import java.util.ArrayList;
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
import net.risesoft.util.Y9DateTimeUtils;
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
            resList.sort((o1, o2) -> {
                try {
                    String startTime1 = o1.getCreateDate();
                    String startTime2 = o2.getCreateDate();
                    long time1 = Y9DateTimeUtils.parseDateTime(startTime1).getTime();
                    long time2 = Y9DateTimeUtils.parseDateTime(startTime2).getTime();
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
                            long time11 = Y9DateTimeUtils.parseDateTime(modifyDate1).getTime();
                            long time22 = Y9DateTimeUtils.parseDateTime(modifyDate2).getTime();
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
        opinion.setContent(CommentUtil.replaceEnter2Br(opinion.getContent()));
        try {
            opinion.setModifyDate(
                Y9DateTimeUtils.formatDateTimeMinute(Y9DateTimeUtils.parseDateTime(opinion.getModifyDate())));
            opinion.setCreateDate(
                Y9DateTimeUtils.formatDateTimeMinute(Y9DateTimeUtils.parseDateTime(opinion.getCreateDate())));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private List<OpinionListModel> listPersonComment4Todo(String itemId, OpinionListModel opinionListModel,
        List<Opinion> opinionList, String opinionFrameMark, String taskId) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId(), personId = person.getPersonId();
        List<OpinionListModel> resList = new ArrayList<>();
        TaskModel task = taskApi.findById(tenantId, taskId).getData();
        // 用户未签收前打开公文时(办理人为空)，只读所有意见
        if (StringUtils.isBlank(task.getAssignee())) {
            return handleUnassignedTask(opinionListModel, opinionList, resList);
        }
        return handleAssignedTask(itemId, opinionFrameMark, opinionListModel, opinionList, taskId, task, tenantId,
            personId, resList);
    }

    private List<OpinionListModel> handleUnassignedTask(OpinionListModel opinionListModel, List<Opinion> opinionList,
        List<OpinionListModel> resList) {
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

    private List<OpinionListModel> handleAssignedTask(String itemId, String opinionFrameMark,
        OpinionListModel opinionListModel, List<Opinion> opinionList, String taskId, TaskModel task, String tenantId,
        String personId, List<OpinionListModel> resList) {
        opinionListModel.setAddable(true);
        String takeBack = variableApi.getVariableLocal(tenantId, taskId, SysVariables.TAKEBACK).getData();
        List<HistoricTaskInstanceModel> hisTaskList = new ArrayList<>();
        processOpinionsForAssignedTask(opinionList, opinionListModel, taskId, personId, takeBack, tenantId, task,
            hisTaskList, resList);
        handleOpinionFrameBind(itemId, opinionListModel, task, opinionFrameMark);
        resList.add(opinionListModel);
        return resList;
    }

    private void processOpinionsForAssignedTask(List<Opinion> opinionList, OpinionListModel opinionListModel,
        String taskId, String personId, String takeBack, String tenantId, TaskModel task,
        List<HistoricTaskInstanceModel> hisTaskList, List<OpinionListModel> resList) {
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
                handleTakeBackOpinion(modelTemp, opinionListModel, opinion, takeBack, personId, hisTaskList, tenantId,
                    task);
            }
            resList.add(modelTemp);
        }
    }

    private void handleTakeBackOpinion(OpinionListModel modelTemp, OpinionListModel opinionListModel, Opinion opinion,
        String takeBack, String personId, List<HistoricTaskInstanceModel> hisTaskList, String tenantId,
        TaskModel task) {
        if (Boolean.parseBoolean(takeBack) && personId.equals(opinion.getUserId())) {
            if (hisTaskList.isEmpty()) {
                hisTaskList =
                    historicTaskApi.findTaskByProcessInstanceIdOrByEndTimeAsc(tenantId, task.getProcessInstanceId(), "")
                        .getData();
            }
            hisTaskList.stream()
                .filter(htiModel -> htiModel.getEndTime() != null && htiModel.getId().equals(opinion.getTaskId()))
                .findFirst()
                .ifPresent(htiModel -> {
                    modelTemp.setEditable(true);
                    opinionListModel.setAddable(false);
                });
        }
    }

    private void handleOpinionFrameBind(String itemId, OpinionListModel opinionListModel, TaskModel task,
        String opinionFrameMark) {
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
    }

    private void listPersonComment4TodoNew(String itemId, OpinionFrameModel opinionFrameModel,
        List<Opinion> opinionList, String opinionFrameMark, String taskId, boolean opinionOrderBy) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId(), personId = person.getPersonId();
        List<OpinionModel> modelList = new ArrayList<>();

        TaskModel task = taskApi.findById(tenantId, taskId).getData();

        // 用户未签收前打开公文时(办理人为空)，只读所有意见
        if (StringUtils.isBlank(task.getAssignee())) {
            handleUnassignedTaskNew(opinionFrameModel, opinionList, modelList);
        } else {
            handleAssignedTaskNew(itemId, opinionFrameMark, opinionFrameModel, opinionList, taskId, task, tenantId,
                personId, modelList);
        }

        modelList = this.order(modelList, opinionOrderBy);
        opinionFrameModel.setOpinionList(modelList);
    }

    private void handleTakeBackOpinionNew(OpinionModel model, OpinionFrameModel opinionFrameModel, Opinion opinion,
        String takeBack, String personId, List<HistoricTaskInstanceModel> hisTaskList, String tenantId,
        TaskModel task) {
        if (Boolean.parseBoolean(takeBack) && personId.equals(opinion.getUserId())) {
            if (hisTaskList.isEmpty()) {
                hisTaskList =
                    historicTaskApi.findTaskByProcessInstanceIdOrByEndTimeAsc(tenantId, task.getProcessInstanceId(), "")
                        .getData();
            }
            // 使用Stream API优化历史任务查找
            hisTaskList.stream()
                .filter(htiModel -> htiModel.getEndTime() != null && htiModel.getId().equals(opinion.getTaskId()))
                .findFirst()
                .ifPresent(htiModel -> {
                    model.setEditable(true);
                    opinionFrameModel.setAddable(false);
                });
        }
    }

    private void handleOpinionFrameBindNew(String itemId, OpinionFrameModel opinionFrameModel, TaskModel task,
        String opinionFrameMark) {
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
    }

    private void handleUnassignedTaskNew(OpinionFrameModel opinionFrameModel, List<Opinion> opinionList,
        List<OpinionModel> modelList) {
        opinionFrameModel.setAddable(false);
        for (Opinion opinion : opinionList) {
            this.format(opinion);
            OpinionModel model = new OpinionModel();
            model.setEditable(false);
            modelList.add(this.getOpinionModelNew(opinion, model));
        }
    }

    private void handleAssignedTaskNew(String itemId, String opinionFrameMark, OpinionFrameModel opinionFrameModel,
        List<Opinion> opinionList, String taskId, TaskModel task, String tenantId, String personId,
        List<OpinionModel> modelList) {
        opinionFrameModel.setAddable(true);
        String takeBack = variableApi.getVariableLocal(tenantId, taskId, SysVariables.TAKEBACK).getData();
        List<HistoricTaskInstanceModel> hisTaskList = new ArrayList<>();

        processOpinionsForAssignedTaskNew(opinionList, opinionFrameModel, taskId, personId, takeBack, tenantId, task,
            hisTaskList, modelList);

        handleOpinionFrameBindNew(itemId, opinionFrameModel, task, opinionFrameMark);
    }

    private void processOpinionsForAssignedTaskNew(List<Opinion> opinionList, OpinionFrameModel opinionFrameModel,
        String taskId, String personId, String takeBack, String tenantId, TaskModel task,
        List<HistoricTaskInstanceModel> hisTaskList, List<OpinionModel> modelList) {
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
                handleTakeBackOpinionNew(model, opinionFrameModel, opinion, takeBack, personId, hisTaskList, tenantId,
                    task);
            }
            modelList.add(this.getOpinionModelNew(opinion, model));
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
        String tenantId = Y9LoginUserHolder.getTenantId();
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
        // 处理办结状态检查
        boolean isEnd = checkProcessEndStatus(processSerialNumber, opinionFrameModel);
        // 处理意见列表
        processChaoSongOpinions(opinionList, modelList, personId, isEnd, opinionFrameModel);
        modelList = this.order(modelList, opinionOrderBy);
        opinionFrameModel.setOpinionList(modelList);
        // 处理新增权限检查
        handleChaoSongAddablePermission(opinionFrameModel, itemId, taskId, taskDefinitionKey, tenantId,
            opinionFrameMark);
    }

    private boolean checkProcessEndStatus(String processSerialNumber, OpinionFrameModel opinionFrameModel) {
        boolean isEnd = false;
        ProcessParam processParam = processParamService.findByProcessSerialNumber(processSerialNumber);
        // 办结件，阅件不可填写意见
        if (processParam != null) {
            HistoricProcessInstanceModel historicProcessInstanceModel =
                historicProcessApi.getById(Y9LoginUserHolder.getTenantId(), processParam.getProcessInstanceId())
                    .getData();
            boolean b = historicProcessInstanceModel == null || historicProcessInstanceModel.getEndTime() != null;
            if (b) {
                opinionFrameModel.setAddable(false);
                isEnd = true;
            }
        }
        return isEnd;
    }

    private void processChaoSongOpinions(List<Opinion> opinionList, List<OpinionModel> modelList, String personId,
        boolean isEnd, OpinionFrameModel opinionFrameModel) {
        for (Opinion opinion : opinionList) {
            this.format(opinion);
            OpinionModel model = new OpinionModel();
            if (personId.equals(opinion.getUserId()) && !isEnd) {
                model.setEditable(true);
                opinionFrameModel.setAddable(false);
            }
            modelList.add(this.getOpinionModelNew(opinion, model));
        }
    }

    private void handleChaoSongAddablePermission(OpinionFrameModel opinionFrameModel, String itemId, String taskId,
        String taskDefinitionKey, String tenantId, String opinionFrameMark) {
        if (Boolean.TRUE.equals(opinionFrameModel.getAddable())) {
            opinionFrameModel.setAddable(false);
            TaskModel task = taskApi.findById(tenantId, taskId).getData();
            ItemOpinionFrameBind bind =
                itemOpinionFrameBindService.findByItemIdAndProcessDefinitionIdAndTaskDefKeyAndOpinionFrameMark(itemId,
                    task.getProcessDefinitionId(), taskDefinitionKey, opinionFrameMark);
            this.setAddableNew(opinionFrameModel, bind);
        }
    }

    private List<OpinionListModel> listPersonComment4ChaoSong(String processSerialNumber, String itemId,
        OpinionListModel opinionListModel, List<Opinion> opinionList, String opinionFrameMark, String taskId,
        String taskDefinitionKey) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId(), personId = person.getPersonId();
        List<OpinionListModel> resList = new ArrayList<>();
        // 处理办结状态检查
        boolean isEnd = checkProcessEndStatusOld(processSerialNumber, opinionListModel);
        // 处理意见列表
        processChaoSongOpinionsOld(opinionList, resList, personId, isEnd, opinionListModel);
        // 处理新增权限检查
        handleChaoSongAddablePermissionOld(opinionListModel, itemId, taskId, taskDefinitionKey, tenantId,
            opinionFrameMark);
        resList.add(opinionListModel);
        return resList;
    }

    private boolean checkProcessEndStatusOld(String processSerialNumber, OpinionListModel opinionListModel) {
        boolean isEnd = false;
        try {
            ProcessParam processParam = processParamService.findByProcessSerialNumber(processSerialNumber);
            // 办结件，阅件不可填写意见
            if (processParam != null) {
                HistoricProcessInstanceModel historicProcessInstanceModel =
                    historicProcessApi.getById(Y9LoginUserHolder.getTenantId(), processParam.getProcessInstanceId())
                        .getData();
                boolean b = historicProcessInstanceModel == null || historicProcessInstanceModel.getEndTime() != null;
                if (b) {
                    opinionListModel.setAddable(false);
                    isEnd = true;
                }
            }
        } catch (Exception e) {
            LOGGER.error("检查流程结束状态时发生异常", e);
        }
        return isEnd;
    }

    private void processChaoSongOpinionsOld(List<Opinion> opinionList, List<OpinionListModel> resList, String personId,
        boolean isEnd, OpinionListModel opinionListModel) {
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
    }

    private void handleChaoSongAddablePermissionOld(OpinionListModel opinionListModel, String itemId, String taskId,
        String taskDefinitionKey, String tenantId, String opinionFrameMark) {
        Boolean addableTemp = opinionListModel.getAddable();
        if (Boolean.TRUE.equals(addableTemp)) {
            opinionListModel.setAddable(false);
            TaskModel task = taskApi.findById(tenantId, taskId).getData();
            ItemOpinionFrameBind bind =
                itemOpinionFrameBindService.findByItemIdAndProcessDefinitionIdAndTaskDefKeyAndOpinionFrameMark(itemId,
                    task.getProcessDefinitionId(), taskDefinitionKey, opinionFrameMark);
            this.setAddable(opinionListModel, bind);
        }
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
        if (StringUtils.isBlank(entity.getId())) {
            return createOpinion(entity);
        } else {
            return updateOpinion(entity);
        }
    }

    private Opinion createOpinion(Opinion entity) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId();
        OrgUnit user = Y9LoginUserHolder.getOrgUnit();
        String orgUnitId = Y9LoginUserHolder.getOrgUnitId();

        Opinion opinion = new Opinion();
        opinion.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        opinion.setUserId(person.getPersonId());
        opinion.setUserName(person.getName());
        opinion.setDeptId(user.getParentId());

        setOrgUnitInfo(opinion, tenantId, user.getParentId());

        opinion.setProcessSerialNumber(entity.getProcessSerialNumber());
        opinion.setProcessInstanceId(entity.getProcessInstanceId());
        opinion.setTaskId(entity.getTaskId());
        opinion.setOpinionFrameMark(entity.getOpinionFrameMark());
        opinion.setTenantId(StringUtils.isNotBlank(entity.getTenantId()) ? entity.getTenantId() : tenantId);
        opinion.setContent(entity.getContent());
        opinion.setCreateDate(Y9DateTimeUtils.formatCurrentDateTime());
        opinion.setModifyDate(Y9DateTimeUtils.formatCurrentDateTime());
        opinion.setPositionId(orgUnitId);
        opinion.setPositionName(user.getName());

        handleProcessTrack(opinion, entity.getTaskId());

        opinionRepository.save(opinion);
        asyncHandleService.sendMsgRemind(tenantId, orgUnitId, entity.getProcessSerialNumber(), entity.getContent());

        return opinion;
    }

    private Opinion updateOpinion(Opinion entity) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId();
        OrgUnit user = Y9LoginUserHolder.getOrgUnit();
        String orgUnitId = Y9LoginUserHolder.getOrgUnitId();
        Opinion opinion = opinionRepository.findById(entity.getId()).orElse(null);
        if (opinion == null) {
            return null;
        }
        Opinion oldOpinion = new Opinion();
        Y9BeanUtil.copyProperties(opinion, oldOpinion);
        opinion.setUserId(person.getPersonId());
        opinion.setUserName(person.getName());
        opinion.setTaskId(entity.getTaskId());
        opinion.setModifyDate(Y9DateTimeUtils.formatCurrentDateTime());
        opinion.setContent(entity.getContent());
        opinion.setProcessInstanceId(entity.getProcessInstanceId());
        opinion.setTenantId(StringUtils.isNotBlank(entity.getTenantId()) ? entity.getTenantId() : tenantId);
        setOrgUnitInfo(opinion, tenantId, user.getParentId());
        opinion.setPositionId(orgUnitId);
        opinion.setPositionName(user.getName());
        opinionRepository.save(opinion);
        asyncHandleService.sendMsgRemind(tenantId, orgUnitId, entity.getProcessSerialNumber(), entity.getContent());
        // 修改意见保存历史记录
        asyncHandleService.saveOpinionHistory(Y9LoginUserHolder.getTenantId(), oldOpinion, "1");
        return opinion;
    }

    private void setOrgUnitInfo(Opinion opinion, String tenantId, String parentId) {
        try {
            OrgUnit orgUnit = orgUnitApi.getOrgUnit(tenantId, parentId).getData();
            if (orgUnit != null) {
                opinion.setDeptName(orgUnit.getName());
            }
        } catch (Exception e) {
            LOGGER.error("获取组织单位信息失败", e);
        }
    }

    private void handleProcessTrack(Opinion opinion, String taskId) {
        if (StringUtils.isNotBlank(taskId)) {
            List<ProcessTrack> list = processTrackService.listByTaskIdAndEndTimeIsNull(taskId);
            // 处理恢复待办后,填写意见错位问题,意见显示在自定义历程上
            if (!list.isEmpty()) {
                opinion.setProcessTrackId(list.get(0).getId());
            }
        }
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
