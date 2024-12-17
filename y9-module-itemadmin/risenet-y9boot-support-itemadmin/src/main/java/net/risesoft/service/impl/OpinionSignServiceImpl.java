package net.risesoft.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.org.DepartmentApi;
import net.risesoft.api.processadmin.HistoricProcessApi;
import net.risesoft.api.processadmin.HistoricTaskApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.api.processadmin.VariableApi;
import net.risesoft.entity.ItemOpinionFrameBind;
import net.risesoft.entity.OpinionSign;
import net.risesoft.entity.ProcessParam;
import net.risesoft.entity.SignDeptDetail;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.enums.platform.DepartmentPropCategoryEnum;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.OpinionFrameOneClickSetModel;
import net.risesoft.model.itemadmin.OpinionSignListModel;
import net.risesoft.model.itemadmin.OpinionSignModel;
import net.risesoft.model.platform.DepartmentProp;
import net.risesoft.model.processadmin.HistoricProcessInstanceModel;
import net.risesoft.model.processadmin.HistoricTaskInstanceModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.repository.jpa.OpinionSignRepository;
import net.risesoft.service.OpinionFrameOneClickSetService;
import net.risesoft.service.OpinionSignService;
import net.risesoft.service.ProcessParamService;
import net.risesoft.service.SignDeptDetailService;
import net.risesoft.service.config.ItemOpinionFrameBindService;
import net.risesoft.util.CommentUtil;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * @author : qinman
 * @date : 2024-12-16
 **/
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

    private final HistoricProcessApi historicProcessApi;

    @Override
    @Transactional
    public OpinionSign saveOrUpdate(OpinionSign opinionSign) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String id = opinionSign.getId();
        if (StringUtils.isNotBlank(id)) {
            OpinionSign old = this.findById(id);
            old.setContent(opinionSign.getContent());
            old.setModifyDate(sdf.format(new Date()));
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
        newOs.setCreateDate(sdf.format(new Date()));
        newOs.setModifyDate(sdf.format(new Date()));
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
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            List<OpinionSign> list = opinionSignRepository
                .findBySignDeptDetailIdAndOpinionFrameMarkOrderByCreateDateAsc(signDeptDetailId, opinionFrameMark);
            if (itemBox.equalsIgnoreCase(ItemBoxTypeEnum.TODO.getValue())) {
                TaskModel task = taskApi.findById(tenantId, taskId).getData();
                if (StringUtils.isBlank(task.getAssignee())) {
                    model.setAddable(false);
                    for (OpinionSign opinionSign : list) {
                        OpinionSignListModel opinionSignListModel = new OpinionSignListModel();
                        opinionSign.setContent(CommentUtil.replaceEnter2Br(opinionSign.getContent()));
                        if (!opinionSign.getCreateDate().equals(opinionSign.getModifyDate())) {
                            opinionSignListModel.setIsEdit(true);
                        }
                        opinionSign.setModifyDate(sdf1.format(sdf.parse(opinionSign.getModifyDate())));
                        opinionSign.setCreateDate(sdf1.format(sdf.parse(opinionSign.getCreateDate())));
                        opinionSignListModel.setEditable(false);
                        OpinionSignModel opinionSignModel = new OpinionSignModel();
                        Y9BeanUtil.copyProperties(opinionSign, opinionSignModel);
                        opinionSignListModel.setOpinionSignModel(opinionSignModel);
                        resList.add(opinionSignListModel);
                    }
                    resList.add(model);
                    return resList;
                }
                String takeBack = variableApi.getVariableLocal(tenantId, taskId, SysVariables.TAKEBACK).getData();
                for (OpinionSign opinionSign : list) {
                    OpinionSignListModel opinionSignListModel = new OpinionSignListModel();
                    opinionSign.setContent(CommentUtil.replaceEnter2Br(opinionSign.getContent()));
                    if (!opinionSign.getCreateDate().equals(opinionSign.getModifyDate())) {
                        opinionSignListModel.setIsEdit(true);
                    }
                    opinionSign.setModifyDate(sdf1.format(sdf.parse(opinionSign.getModifyDate())));
                    opinionSign.setCreateDate(sdf1.format(sdf.parse(opinionSign.getCreateDate())));
                    OpinionSignModel opinionModel = new OpinionSignModel();
                    Y9BeanUtil.copyProperties(opinionSign, opinionModel);
                    opinionSignListModel.setOpinionSignModel(opinionModel);
                    opinionSignListModel.setEditable(false);

                    if (taskId.equals(opinionSign.getTaskId())) {
                        if (personId.equals(opinionSign.getUserId())) {
                            opinionSignListModel.setEditable(true);
                            model.setAddable(false);
                        }
                    } else {// 收回件可编辑意见
                        if (takeBack != null && Boolean.valueOf(takeBack)
                            && Y9LoginUserHolder.getPersonId().equals(opinionSign.getUserId())) {
                            List<HistoricTaskInstanceModel> tlist = historicTaskApi
                                .findTaskByProcessInstanceIdOrByEndTimeAsc(tenantId, task.getProcessInstanceId(), "")
                                .getData();
                            for (int i = tlist.size() - 1; i >= 0; i--) {
                                HistoricTaskInstanceModel htimodel = tlist.get(i);
                                // 找到收回前的上一个任务
                                if (htimodel.getEndTime() != null && htimodel.getId().equals(opinionSign.getTaskId())) {
                                    opinionSignListModel.setEditable(true);
                                    model.setAddable(false);
                                    break;
                                }
                            }
                        }
                    }
                    resList.add(opinionSignListModel);
                }
                ProcessParam processParam = processParamService.findByProcessInstanceId(task.getProcessInstanceId());
                ItemOpinionFrameBind itemOpinionFrameBind = itemOpinionFrameBindService
                    .findByItemIdAndProcessDefinitionIdAndTaskDefKeyAndOpinionFrameMark(processParam.getItemId(),
                        task.getProcessDefinitionId(), task.getTaskDefinitionKey(), opinionFrameMark);
                if (null != itemOpinionFrameBind) {
                    oneClickSetList = opinionFrameOneClickSetService.findByBindIdModel(itemOpinionFrameBind.getId());
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
                    if (opinionFrameMark.equals("chuzhang")) {
                        /*
                         * 处长
                         */
                        List<DepartmentProp> leaders = departmentApi.listDepartmentPropByOrgUnitIdAndCategory(tenantId,
                            person.getParentId(), DepartmentPropCategoryEnum.LEADER).getData();
                        boolean isLeader =
                            leaders.stream().anyMatch(dp -> dp.getOrgBaseId().equals(Y9LoginUserHolder.getOrgUnitId()));
                        if (isLeader) {
                            model.setAddable(true);
                        }
                        model.setAddable(true);
                    } else {
                        /*
                         * 司长
                         */
                        List<DepartmentProp> managers = departmentApi.listDepartmentPropByOrgUnitIdAndCategory(tenantId,
                            person.getParentId(), DepartmentPropCategoryEnum.MANAGER).getData();
                        boolean isManager = managers.stream()
                            .anyMatch(dp -> dp.getOrgBaseId().equals(Y9LoginUserHolder.getOrgUnitId()));
                        if (isManager) {
                            model.setAddable(true);
                        }
                    }
                }
            } else if (itemBox.equalsIgnoreCase(ItemBoxTypeEnum.DONE.getValue())
                || itemBox.equalsIgnoreCase(ItemBoxTypeEnum.DOING.getValue())
                || itemBox.equalsIgnoreCase(ItemBoxTypeEnum.RECYCLE.getValue())) {
                model.setAddable(false);
                for (OpinionSign opinionSign : list) {
                    OpinionSignListModel opinionSignListModel = new OpinionSignListModel();
                    opinionSign.setContent(CommentUtil.replaceEnter2Br(opinionSign.getContent()));
                    if (!opinionSign.getCreateDate().equals(opinionSign.getModifyDate())) {
                        opinionSignListModel.setIsEdit(true);
                    }
                    opinionSign.setModifyDate(sdf1.format(sdf.parse(opinionSign.getModifyDate())));
                    opinionSign.setCreateDate(sdf1.format(sdf.parse(opinionSign.getCreateDate())));
                    OpinionSignModel opinionSignModel = new OpinionSignModel();
                    Y9BeanUtil.copyProperties(opinionSign, opinionSignModel);
                    opinionSignListModel.setOpinionSignModel(opinionSignModel);
                    opinionSignListModel.setEditable(false);
                    resList.add(opinionSignListModel);
                }
            } else if (itemBox.equalsIgnoreCase(ItemBoxTypeEnum.YUEJIAN.getValue())) {
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
                for (OpinionSign opinion : list) {
                    OpinionSignListModel opinionSignListModel = new OpinionSignListModel();
                    opinion.setContent(CommentUtil.replaceEnter2Br(opinion.getContent()));
                    if (!opinion.getCreateDate().equals(opinion.getModifyDate())) {
                        opinionSignListModel.setIsEdit(true);
                    }
                    opinion.setModifyDate(sdf1.format(sdf.parse(opinion.getModifyDate())));
                    opinion.setCreateDate(sdf1.format(sdf.parse(opinion.getCreateDate())));
                    OpinionSignModel opinionSignModel = new OpinionSignModel();
                    Y9BeanUtil.copyProperties(opinion, opinionSignModel);
                    opinionSignListModel.setOpinionSignModel(opinionSignModel);
                    opinionSignListModel.setEditable(false);
                    if (personId.equals(opinion.getUserId()) && !isEnd) {
                        opinionSignListModel.setEditable(true);
                        model.setAddable(false);
                    }
                    resList.add(opinionSignListModel);
                }
                /**
                 * 当前意见框,当前人员可以新增意见时，要判断当前人员是否有在该意见框签意见的权限
                 */
                Boolean addableTemp = model.getAddable();
                if (Boolean.TRUE.equals(addableTemp)) {
                    model.setAddable(false);

                }
            }
            resList.add(model);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return resList;
    }

    @Override
    public List<OpinionSign> findBySignDeptDetailIdAndOpinionFrameMark(String signDeptDetailId,
        String opinionFrameMark) {
        return opinionSignRepository.findBySignDeptDetailIdAndOpinionFrameMarkOrderByCreateDateAsc(signDeptDetailId,
            opinionFrameMark);
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
