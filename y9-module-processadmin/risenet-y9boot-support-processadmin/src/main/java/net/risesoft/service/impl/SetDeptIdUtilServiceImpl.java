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
import net.risesoft.model.platform.org.OrgUnit;
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
            if (StringUtils.isBlank(tenantId)) {
                return;
            }
            processDepartmentInfo(taskEntity, assignee, processSerialNumber, tenantId);
        } catch (Exception e) {
            LOGGER.warn("#################保存科室id失败-taskId:{}#################", taskEntity.getId(), e);
        }
    }

    /**
     * 处理部门信息
     */
    private void processDepartmentInfo(DelegateTask taskEntity, String assignee, String processSerialNumber,
        String tenantId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, assignee).getData();
        OrgUnit bureau = orgUnitApi.getBureau(tenantId, orgUnit.getParentId()).getData();
        OrgUnit parent = orgUnitApi.getOrgUnit(tenantId, orgUnit.getParentId()).getData();
        ProcessParamModel processParamModel =
            processParamApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
        boolean isUpdated = updateDepartmentIds(processParamModel, parent, bureau);
        if (isUpdated) {
            processParamModel.setProcessInstanceId(taskEntity.getProcessInstanceId());
            processParamApi.saveOrUpdate(tenantId, processParamModel);
        }
    }

    /**
     * 更新部门ID信息
     * 
     * @return 是否有更新
     */
    private boolean updateDepartmentIds(ProcessParamModel processParamModel, OrgUnit parent, OrgUnit bureau) {
        boolean deptUpdated = updateDeptId(processParamModel, parent);
        boolean bureauUpdated = updateBureauId(processParamModel, bureau);
        return deptUpdated || bureauUpdated;
    }

    /**
     * 更新科室ID
     * 
     * @return 是否有更新
     */
    private boolean updateDeptId(ProcessParamModel processParamModel, OrgUnit parent) {
        String oldDeptId = StringUtils.defaultString(processParamModel.getDeptIds());
        String newDeptId;
        if (!oldDeptId.contains(parent.getId())) {
            newDeptId = Y9Util.genCustomStr(oldDeptId, parent.getId());
            processParamModel.setDeptIds(newDeptId);
            return true;
        }
        return false;
    }

    /**
     * 更新司局ID
     * 
     * @return 是否有更新
     */
    private boolean updateBureauId(ProcessParamModel processParamModel, OrgUnit bureau) {
        String oldBureauId = StringUtils.defaultString(processParamModel.getBureauIds());
        String newBureauId;
        if (bureau != null && !oldBureauId.contains(bureau.getId())) {
            newBureauId = Y9Util.genCustomStr(oldBureauId, bureau.getId());
            processParamModel.setBureauIds(newBureauId);
            return true;
        }
        return false;
    }
}