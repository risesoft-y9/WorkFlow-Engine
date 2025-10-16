package net.risesoft.service.opinion.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.platform.org.DepartmentApi;
import net.risesoft.api.processadmin.HistoricTaskApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.api.processadmin.VariableApi;
import net.risesoft.consts.processadmin.SysVariables;
import net.risesoft.entity.ProcessParam;
import net.risesoft.entity.SignDeptDetail;
import net.risesoft.entity.opinion.ItemOpinionFrameBind;
import net.risesoft.entity.opinion.OpinionSign;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.enums.platform.org.DepartmentPropCategoryEnum;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.OpinionFrameOneClickSetModel;
import net.risesoft.model.itemadmin.OpinionSignListModel;
import net.risesoft.model.itemadmin.OpinionSignModel;
import net.risesoft.model.platform.org.DepartmentProp;
import net.risesoft.model.processadmin.HistoricTaskInstanceModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.repository.opinion.OpinionSignRepository;
import net.risesoft.service.SignDeptDetailService;
import net.risesoft.service.config.ItemOpinionFrameBindService;
import net.risesoft.service.core.ProcessParamService;
import net.risesoft.service.opinion.OpinionFrameOneClickSetService;
import net.risesoft.service.opinion.OpinionSignService;
import net.risesoft.util.CommentUtil;
import net.risesoft.util.Y9DateTimeUtils;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * @author : qinman
 * @date : 2024-12-16
 **/
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class OpinionSignServiceImpl implements OpinionSignService {

    private final OpinionSignRepository opinionSignRepository;

    private final SignDeptDetailService signDeptDetailService;

    private final TaskApi taskApi;

    private final VariableApi variableApi;

    private final HistoricTaskApi historicTaskApi;

    private final ItemOpinionFrameBindService itemOpinionFrameBindService;

    private final ProcessParamService processParamService;

    private final OpinionFrameOneClickSetService opinionFrameOneClickSetService;

    private final DepartmentApi departmentApi;

    @Override
    @Transactional
    public OpinionSign saveOrUpdate(OpinionSign opinionSign) {
        String id = opinionSign.getId();
        if (StringUtils.isNotBlank(id)) {
            OpinionSign old = this.findById(id);
            old.setContent(opinionSign.getContent());
            old.setModifyDate(Y9DateTimeUtils.formatCurrentDateTime());
            return opinionSignRepository.save(old);
        }
        OpinionSign newOs = new OpinionSign();
        newOs.setId(Y9IdGenerator.genId());
        newOs.setSignDeptDetailId(opinionSign.getSignDeptDetailId());
        newOs.setTaskId(opinionSign.getTaskId());
        newOs.setContent(opinionSign.getContent());
        newOs.setOpinionFrameMark(opinionSign.getOpinionFrameMark());
        newOs.setUserId(Y9LoginUserHolder.getUserInfo().getPersonId());
        newOs.setUserName(Y9LoginUserHolder.getUserInfo().getName());
        SignDeptDetail signDeptDetail = signDeptDetailService.findById(opinionSign.getSignDeptDetailId());
        newOs.setDeptId(signDeptDetail.getDeptId());
        newOs.setDeptName(signDeptDetail.getDeptName());
        newOs.setCreateDate(Y9DateTimeUtils.formatCurrentDateTime());
        newOs.setModifyDate(Y9DateTimeUtils.formatCurrentDateTime());
        return opinionSignRepository.save(newOs);
    }

    @Override
    public List<OpinionSignListModel> list(String processSerialNumber, String signDeptDetailId, String itemBox,
        String taskId, String opinionFrameMark) {
        List<OpinionSignListModel> resList = new ArrayList<>();
        try {
            UserInfo person = Y9LoginUserHolder.getUserInfo();
            String tenantId = Y9LoginUserHolder.getTenantId(), personId = person.getPersonId();
            OpinionSignListModel model = new OpinionSignListModel();
            List<OpinionFrameOneClickSetModel> oneClickSetList = new ArrayList<>();
            model.setAddable(true);
            model.setOpinionFrameMark(opinionFrameMark);
            model.setOneClickSetList(oneClickSetList);
            List<OpinionSign> list = opinionSignRepository
                .findBySignDeptDetailIdAndOpinionFrameMarkOrderByCreateDateAsc(signDeptDetailId, opinionFrameMark);
            if (itemBox.equalsIgnoreCase(ItemBoxTypeEnum.TODO.getValue())) {
                handleTodoBox(resList, model, list, tenantId, taskId, personId, person, opinionFrameMark);
            } else if (isProcessedBox(itemBox)) {
                handleProcessedBox(resList, model, list, itemBox);
            }
            resList.add(model);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resList;
    }

    private void handleTodoBox(List<OpinionSignListModel> resList, OpinionSignListModel model, List<OpinionSign> list,
        String tenantId, String taskId, String personId, UserInfo person, String opinionFrameMark) {
        TaskModel task = taskApi.findById(tenantId, taskId).getData();
        if (StringUtils.isBlank(task.getAssignee())) {
            model.setAddable(false);
            processOpinionSigns(list, resList);
            return;
        }
        String takeBack = variableApi.getVariableLocal(tenantId, taskId, SysVariables.TAKEBACK).getData();
        processTodoOpinionSigns(resList, model, list, taskId, personId, tenantId, task, takeBack);
        ProcessParam processParam = processParamService.findByProcessInstanceId(task.getProcessInstanceId());
        ItemOpinionFrameBind itemOpinionFrameBind =
            itemOpinionFrameBindService.findByItemIdAndProcessDefinitionIdAndTaskDefKeyAndOpinionFrameMark(
                processParam.getItemId(), task.getProcessDefinitionId(), task.getTaskDefinitionKey(), opinionFrameMark);
        if (null != itemOpinionFrameBind) {
            List<OpinionFrameOneClickSetModel> oneClickSetList =
                opinionFrameOneClickSetService.findByBindIdModel(itemOpinionFrameBind.getId());
            if (null != oneClickSetList && !oneClickSetList.isEmpty()) {
                model.setOneClickSetList(oneClickSetList);
            }
        }
        checkAddPermission(model, person, tenantId, opinionFrameMark);
    }

    private void processOpinionSigns(List<OpinionSign> list, List<OpinionSignListModel> resList) {
        for (OpinionSign opinionSign : list) {
            OpinionSignListModel opinionSignListModel = createOpinionSignListModel(opinionSign);
            opinionSignListModel.setEditable(false);
            resList.add(opinionSignListModel);
        }
    }

    private OpinionSignListModel createOpinionSignListModel(OpinionSign opinionSign) {
        OpinionSignListModel opinionSignListModel = new OpinionSignListModel();
        opinionSign.setContent(CommentUtil.replaceEnter2Br(opinionSign.getContent()));
        try {
            opinionSign.setModifyDate(
                Y9DateTimeUtils.formatDateTimeMinute(Y9DateTimeUtils.parseDateTime(opinionSign.getModifyDate())));
            opinionSign.setCreateDate(
                Y9DateTimeUtils.formatDateTimeMinute(Y9DateTimeUtils.parseDateTime(opinionSign.getCreateDate())));
        } catch (Exception e) {
            LOGGER.warn(e.getMessage());
        }
        OpinionSignModel opinionSignModel = new OpinionSignModel();
        Y9BeanUtil.copyProperties(opinionSign, opinionSignModel);
        opinionSignListModel.setOpinionSignModel(opinionSignModel);
        return opinionSignListModel;
    }

    private void processTodoOpinionSigns(List<OpinionSignListModel> resList, OpinionSignListModel model,
        List<OpinionSign> list, String taskId, String personId, String tenantId, TaskModel task, String takeBack) {
        for (OpinionSign opinionSign : list) {
            OpinionSignListModel opinionSignListModel = createOpinionSignListModel(opinionSign);
            opinionSignListModel.setEditable(false);

            if (taskId.equals(opinionSign.getTaskId())) {
                if (personId.equals(opinionSign.getUserId())) {
                    opinionSignListModel.setEditable(true);
                    model.setAddable(false);
                }
            } else {
                handleTakeBackOpinion(model, opinionSignListModel, opinionSign, takeBack, tenantId, task);
            }
            resList.add(opinionSignListModel);
        }
    }

    private void handleTakeBackOpinion(OpinionSignListModel model, OpinionSignListModel opinionSignListModel,
        OpinionSign opinionSign, String takeBack, String tenantId, TaskModel task) {
        if (Boolean.parseBoolean(takeBack) && Y9LoginUserHolder.getPersonId().equals(opinionSign.getUserId())) {
            List<HistoricTaskInstanceModel> tlist =
                historicTaskApi.findTaskByProcessInstanceIdOrByEndTimeAsc(tenantId, task.getProcessInstanceId(), "")
                    .getData();
            tlist.stream()
                .filter(hisTask -> hisTask.getEndTime() != null && hisTask.getId().equals(opinionSign.getTaskId()))
                .findFirst()
                .ifPresent(hisTask -> {
                    opinionSignListModel.setEditable(true);
                    model.setAddable(false);
                });
        }
    }

    private boolean isProcessedBox(String itemBox) {
        return itemBox.equalsIgnoreCase(ItemBoxTypeEnum.DONE.getValue())
            || itemBox.equalsIgnoreCase(ItemBoxTypeEnum.DOING.getValue())
            || itemBox.equalsIgnoreCase(ItemBoxTypeEnum.RECYCLE.getValue())
            || itemBox.equalsIgnoreCase(ItemBoxTypeEnum.MONITOR_DOING.getValue())
            || itemBox.equalsIgnoreCase(ItemBoxTypeEnum.MONITOR_DONE.getValue())
            || itemBox.equalsIgnoreCase(ItemBoxTypeEnum.COPY.getValue());
    }

    private void handleProcessedBox(List<OpinionSignListModel> resList, OpinionSignListModel model,
        List<OpinionSign> list, String itemBox) {
        model.setAddable(false);
        processOpinionSigns(list, resList);
        if (itemBox.equalsIgnoreCase(ItemBoxTypeEnum.MONITOR_DOING.getValue())
            || itemBox.equalsIgnoreCase(ItemBoxTypeEnum.MONITOR_DONE.getValue())) {
            // 设置监控箱中的意见为可编辑
            for (OpinionSignListModel opinionSignListModel : resList) {
                opinionSignListModel.setEditable(true);
            }
        }
    }

    private void checkAddPermission(OpinionSignListModel model, UserInfo person, String tenantId,
        String opinionFrameMark) {
        Boolean addableTemp = model.getAddable();
        if (addableTemp) {
            model.setAddable(false);
            if (opinionFrameMark.equals("chuzhang")) {
                checkLeaderPermission(model, person, tenantId);
            } else {
                checkManagerPermission(model, person, tenantId);
            }
        }
    }

    private void checkLeaderPermission(OpinionSignListModel model, UserInfo person, String tenantId) {
        try {
            List<DepartmentProp> leaders =
                departmentApi
                    .listDepartmentPropByOrgUnitIdAndCategory(tenantId, person.getParentId(),
                        DepartmentPropCategoryEnum.LEADER)
                    .getData();
            boolean isLeader =
                leaders.stream().anyMatch(dp -> dp.getOrgBaseId().equals(Y9LoginUserHolder.getOrgUnitId()));
            if (isLeader) {
                model.setAddable(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkManagerPermission(OpinionSignListModel model, UserInfo person, String tenantId) {
        try {
            List<DepartmentProp> managers =
                departmentApi
                    .listDepartmentPropByOrgUnitIdAndCategory(tenantId, person.getParentId(),
                        DepartmentPropCategoryEnum.MANAGER)
                    .getData();
            boolean isManager =
                managers.stream().anyMatch(dp -> dp.getOrgBaseId().equals(Y9LoginUserHolder.getOrgUnitId()));
            if (isManager) {
                model.setAddable(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<OpinionSign> findBySignDeptDetailIdAndOpinionFrameMark(String signDeptDetailId,
        String opinionFrameMark) {
        return opinionSignRepository.findBySignDeptDetailIdAndOpinionFrameMarkOrderByCreateDateAsc(signDeptDetailId,
            opinionFrameMark);
    }

    @Override
    public List<OpinionSign> findBySignDeptDetailId(String signDeptDetailId) {
        return opinionSignRepository.findBySignDeptDetailIdOrderByCreateDateAsc(signDeptDetailId);
    }

    @Override
    public OpinionSign findById(String id) {
        return opinionSignRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        opinionSignRepository.deleteById(id);
    }
}
