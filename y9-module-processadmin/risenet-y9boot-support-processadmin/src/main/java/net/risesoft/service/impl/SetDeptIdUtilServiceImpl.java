package net.risesoft.service.impl;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.core.ProcessParamApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.consts.processadmin.SysVariables;
import net.risesoft.model.itemadmin.core.ProcessParamModel;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.service.SetDeptIdUtilService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9Util;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SetDeptIdUtilServiceImpl implements SetDeptIdUtilService {

    private final ProcessParamApi processParamApi;

    private final OrgUnitApi orgUnitApi;

    @Override
    public void setDeptId(final DelegateTask taskEntity, final Map<String, Object> map) {
        try {
            String assignee = taskEntity.getAssignee();
            String processSerialNumber = (String)map.get(SysVariables.PROCESS_SERIAL_NUMBER);
            String tenantId = (String)map.get("tenantId");
            if (StringUtils.isNotBlank(tenantId)) {
                Y9LoginUserHolder.setTenantId(tenantId);
                OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, assignee).getData();
                OrgUnit bureau, parent;
                bureau = orgUnitApi.getBureau(tenantId, orgUnit.getParentId()).getData();
                parent = orgUnitApi.getOrgUnit(tenantId, orgUnit.getParentId()).getData();
                ProcessParamModel processParamModel =
                    processParamApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
                String oldDeptId =
                    StringUtils.isBlank(processParamModel.getDeptIds()) ? "" : processParamModel.getDeptIds();
                String oldDeptId1 =
                    StringUtils.isBlank(processParamModel.getDeptIds()) ? "" : processParamModel.getDeptIds();
                if (!oldDeptId.contains(parent.getId())) {
                    oldDeptId = Y9Util.genCustomStr(oldDeptId, parent.getId());
                }
                String oldBureauId =
                    StringUtils.isBlank(processParamModel.getBureauIds()) ? "" : processParamModel.getBureauIds();
                String oldBureauId1 =
                    StringUtils.isBlank(processParamModel.getBureauIds()) ? "" : processParamModel.getBureauIds();
                if (bureau != null) {
                    if (!oldBureauId.contains(bureau.getId())) {
                        oldBureauId = Y9Util.genCustomStr(oldBureauId, bureau.getId());
                    }
                }
                if (!oldBureauId.equals(oldBureauId1)) {
                    processParamModel.setBureauIds(oldBureauId);
                    processParamModel.setDeptIds(oldDeptId);
                    processParamModel.setProcessInstanceId(taskEntity.getProcessInstanceId());
                    processParamApi.saveOrUpdate(tenantId, processParamModel);
                } else {
                    if (!oldDeptId.equals(oldDeptId1)) {
                        processParamModel.setDeptIds(oldDeptId);
                        processParamModel.setProcessInstanceId(taskEntity.getProcessInstanceId());
                        processParamApi.saveOrUpdate(tenantId, processParamModel);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.warn("#################保存科室id失败-taskId:{}#################", taskEntity.getId(), e);
        }
    }
}