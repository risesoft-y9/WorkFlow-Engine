package net.risesoft.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.Y9FlowableHolder;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.api.processadmin.VariableApi;
import net.risesoft.consts.processadmin.SysVariables;
import net.risesoft.entity.ProcessParam;
import net.risesoft.entity.ProcessTrack;
import net.risesoft.entity.SignDeptDetail;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.model.processadmin.FlowElementModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.repository.jpa.ProcessTrackRepository;
import net.risesoft.service.AsyncForwardingHandleService;
import net.risesoft.service.SignDeptDetailService;
import net.risesoft.util.Y9DateTimeUtils;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Slf4j
@Service
@Transactional(value = "rsTenantTransactionManager", rollbackFor = Exception.class)
public class AsyncForwardingHandleServiceImpl implements AsyncForwardingHandleService {

    private final OrgUnitApi orgUnitApi;

    private final TaskApi taskApi;

    private final ProcessDefinitionApi processDefinitionApi;

    private final VariableApi variableApi;

    private final ProcessTrackRepository processTrackRepository;

    private final SignDeptDetailService signDeptDetailService;

    public AsyncForwardingHandleServiceImpl(
        OrgUnitApi orgUnitApi,
        TaskApi taskApi,
        ProcessDefinitionApi processDefinitionApi,
        VariableApi variableApi,
        ProcessTrackRepository processTrackRepository,
        SignDeptDetailService signDeptDetailService) {
        this.orgUnitApi = orgUnitApi;
        this.taskApi = taskApi;
        this.processDefinitionApi = processDefinitionApi;
        this.variableApi = variableApi;
        this.processTrackRepository = processTrackRepository;
        this.signDeptDetailService = signDeptDetailService;
    }

    @Async
    @Override
    public void forwardingHandle(final String tenantId, final String orgUnitId, final TaskModel task,
        final String executionId, final String processInstanceId, final FlowElementModel flowElementModel,
        final String sponsorGuid, final ProcessParam processParam, List<String> userList) {
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, orgUnitId).getData();
            Y9FlowableHolder.setOrgUnit(orgUnit);
            // 更新自定义历程结束时间
            updateProcessTrackEndTime(task);
            // 处理任务变量和子流程信息
            handleTaskAndSubProcess(tenantId, orgUnit, task, executionId, processInstanceId, flowElementModel,
                sponsorGuid, processParam);
        } catch (Exception e) {
            LOGGER.warn("*****forwardingHandle发送发生异常*****", e);
        }
    }

    /**
     * 更新流程跟踪结束时间
     */
    private void updateProcessTrackEndTime(TaskModel task) {
        try {
            List<ProcessTrack> ptModelList = processTrackRepository.findByTaskId(task.getId());
            for (ProcessTrack ptModel : ptModelList) {
                if (StringUtils.isBlank(ptModel.getEndTime())) {
                    ptModel.setEndTime(Y9DateTimeUtils.formatCurrentDateTime());
                    processTrackRepository.save(ptModel);
                }
            }
        } catch (Exception e) {
            LOGGER.warn("更新流程跟踪结束时间失败, taskId: {}", task.getId(), e);
        }
    }

    /**
     * 处理任务变量和子流程信息
     */
    private void handleTaskAndSubProcess(String tenantId, OrgUnit orgUnit, TaskModel task, String executionId,
        String processInstanceId, FlowElementModel flowElementModel, String sponsorGuid, ProcessParam processParam) {
        boolean isSubProcess = isSubProcessElement(flowElementModel);
        List<TaskModel> nextTaskList = taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
        List<SignDeptDetail> detailList = new ArrayList<>();
        for (TaskModel taskNext : nextTaskList) {
            // 设置任务变量
            setTaskVariables(tenantId, orgUnit, taskNext, flowElementModel, sponsorGuid, executionId);
            // 处理子流程信息
            if (isSubProcess) {
                SignDeptDetail signDeptDetail = buildSignDeptDetail(tenantId, orgUnit, task, taskNext, processParam);
                if (signDeptDetail != null) {
                    detailList.add(signDeptDetail);
                }
            }
        }
        // 保存子流程详情
        if (isSubProcess && !detailList.isEmpty()) {
            detailList.forEach(signDeptDetailService::saveOrUpdate);
        }
    }

    /**
     * 构建签署部门详情
     */
    private SignDeptDetail buildSignDeptDetail(String tenantId, OrgUnit orgUnit, TaskModel task, TaskModel taskNext,
        ProcessParam processParam) {
        try {
            OrgUnit bureau = orgUnitApi.getBureau(tenantId, taskNext.getAssignee()).getData();
            if (bureau == null) {
                return null;
            }
            SignDeptDetail signDeptDetail = new SignDeptDetail();
            signDeptDetail.setProcessSerialNumber(processParam.getProcessSerialNumber());
            signDeptDetail.setProcessInstanceId(processParam.getProcessInstanceId());
            signDeptDetail.setExecutionId(taskNext.getExecutionId());
            signDeptDetail.setTaskId(task.getId());
            signDeptDetail.setTaskName(task.getName());
            signDeptDetail.setSenderId(orgUnit.getId());
            signDeptDetail.setSenderName(orgUnit.getName());
            signDeptDetail.setDeptId(bureau.getId());
            signDeptDetail.setDeptName(bureau.getName());
            signDeptDetail.setTabIndex(bureau.getTabIndex());
            return signDeptDetail;
        } catch (Exception e) {
            LOGGER.warn("构建签署部门详情失败, taskId: {}", task.getId(), e);
            return null;
        }
    }

    /**
     * 设置任务变量
     */
    private void setTaskVariables(String tenantId, OrgUnit orgUnit, TaskModel taskNext,
        FlowElementModel flowElementModel, String sponsorGuid, String executionId) {
        Map<String, Object> vars = createTaskVariables(orgUnit, flowElementModel, taskNext, sponsorGuid);
        Boolean isSubProcessChildNode = processDefinitionApi
            .isSubProcessChildNode(tenantId, taskNext.getProcessDefinitionId(), taskNext.getTaskDefinitionKey())
            .getData();
        boolean isSubProcess = isSubProcessElement(flowElementModel);
        if (isSubProcessChildNode && !isSubProcess) {
            // 不是发送子流程，且taskNext是子流程节点，只更新对应的子流程任务变量
            if (executionId.equals(taskNext.getExecutionId())) {
                variableApi.setVariablesLocal(tenantId, taskNext.getId(), vars);
            }
        } else {
            // 发送子流程，或其他子流程外的节点，更新所有任务变量
            variableApi.setVariablesLocal(tenantId, taskNext.getId(), vars);
        }
    }

    /**
     * 判断是否为子流程元素
     */
    private boolean isSubProcessElement(FlowElementModel flowElementModel) {
        return null != flowElementModel && SysVariables.SUBPROCESS.equals(flowElementModel.getType());
    }

    /**
     * 创建任务变量
     */
    private Map<String, Object> createTaskVariables(OrgUnit orgUnit, FlowElementModel flowElementModel,
        TaskModel taskNext, String sponsorGuid) {
        Map<String, Object> vars = new HashMap<>(16);
        vars.put(SysVariables.TASK_SENDER, orgUnit.getName());
        vars.put(SysVariables.TASK_SENDER_ID, orgUnit.getId());
        // 并行状态且区分主协办情况下，如果受让人是主办人，则将主办人guid设为任务变量
        if (SysVariables.PARALLEL.equals(flowElementModel.getMultiInstance())) {
            if (taskNext.getAssignee().equals(sponsorGuid)) {
                vars.put(SysVariables.PARALLEL_SPONSOR, sponsorGuid);
            }
        }
        return vars;
    }
}