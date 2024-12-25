package net.risesoft.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.api.itemadmin.SignDeptDetailApi;
import net.risesoft.api.itemadmin.UrgeInfoApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.model.itemadmin.SignDeptDetailModel;
import net.risesoft.model.itemadmin.UrgeInfoModel;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.FlowableUrgeInfoService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author : qinman
 * @date : 2024-12-24
 * @since 9.6.8
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class FlowableUrgeInfoServiceImpl implements FlowableUrgeInfoService {

    private final UrgeInfoApi urgeInfoApi;

    private final ProcessParamApi processParamApi;

    private final SignDeptDetailApi signDeptDetailApi;

    private final OrgUnitApi orgUnitApi;

    private final TaskApi taskApi;

    private final ProcessDefinitionApi processDefinitionApi;

    @Override
    public Y9Result<Object> deleteById(String id) {
        return urgeInfoApi.deleteById(Y9LoginUserHolder.getTenantId(), id);
    }

    @Override
    public List<UrgeInfoModel> findByProcessSerialNumber(String processSerialNumber) {
        String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
        List<UrgeInfoModel> urgeList =
            urgeInfoApi.findByProcessSerialNumber(Y9LoginUserHolder.getTenantId(), processSerialNumber).getData();
        List<SignDeptDetailModel> signDeptDetailList =
            signDeptDetailApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
        OrgUnit bureau = orgUnitApi.getBureau(tenantId, positionId).getData();
        boolean isSub = signDeptDetailList.stream().anyMatch(sdd -> sdd.getDeptId().equals(bureau.getId()));
        /*
         * 会签只看自己会签的催办信息
         */
        if (isSub) {
            List<String> executorIds = new ArrayList<>();
            signDeptDetailList.stream().filter(sdd -> sdd.getDeptId().equals(bureau.getId()))
                .forEach(sdd -> executorIds.add(sdd.getExecutionId()));
            urgeList.removeIf(urge -> !executorIds.contains(urge.getExecutionId()));
        } else {
            ProcessParamModel processParamModel =
                processParamApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
            if (StringUtils.isNotBlank(processParamModel.getCompleter())) {
                /*
                 * 已办结：主办看所有主办的催办信息
                 */
                urgeList.removeIf(UrgeInfoModel::isSub);
            } else {
                /*
                 * 未办结，看当前办理是主流程还是会签流程
                 * 1、当前是会签流程，主办查看所有的催办信息
                 * 2、当前是主流程，主办查看主流程的催办信息
                 *
                 */
                String processInstanceId = processParamModel.getProcessInstanceId();
                List<TaskModel> taskList = taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                boolean isSubNode = processDefinitionApi.isSubProcessChildNode(tenantId,
                    taskList.get(0).getProcessDefinitionId(), taskList.get(0).getTaskDefinitionKey()).getData();
                if (!isSubNode) {
                    urgeList.removeIf(UrgeInfoModel::isSub);
                }
            }
        }
        return urgeList;
    }

    @Override
    public Y9Result<Object> save(String[] processSerialNumbers, String msgContent) {
        String tenantId = Y9LoginUserHolder.getTenantId(), userId = Y9LoginUserHolder.getPersonId();
        AtomicInteger errorCount = new AtomicInteger();
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger doneCount = new AtomicInteger();
        Arrays.stream(processSerialNumbers).forEach(processSerialNumber -> {
            ProcessParamModel processParamModel =
                processParamApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
            if (StringUtils.isBlank(processParamModel.getCompleter())) {
                if (urgeInfoApi.save(tenantId, userId, processSerialNumber, msgContent).isSuccess()) {
                    successCount.getAndIncrement();
                } else {
                    errorCount.getAndIncrement();
                }
            } else {
                doneCount.getAndIncrement();
            }
        });
        return Y9Result.successMsg("共" + processSerialNumbers.length + "条记录，成功" + successCount.get() + "条，失败"
            + errorCount.get() + "条，已办结" + doneCount.get() + "条");
    }
}
