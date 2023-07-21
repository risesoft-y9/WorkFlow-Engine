package net.risesoft.service;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.api.org.DepartmentApi;
import net.risesoft.api.org.OrgUnitApi;
import net.risesoft.api.org.PersonApi;
import net.risesoft.api.org.PositionApi;
import net.risesoft.model.OrgUnit;
import net.risesoft.model.Person;
import net.risesoft.model.Position;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9Util;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@Service(value = "setDeptIdUtilService")
@Slf4j
public class SetDeptIdUtilService {

    @Autowired
    private ProcessParamApi processParamManager;

    @Autowired
    private PersonApi personManager;

    @Autowired
    private PositionApi positionApi;

    @Autowired
    private DepartmentApi departmentApi;

    @Autowired
    private OrgUnitApi orgUnitManager;

    /**
     * 保存委办局id,科室id
     *
     * @param taskEntity
     * @param map
     */
    public void setDeptId(final DelegateTask taskEntity, final Map<String, Object> map) {
        try {
            String assignee = taskEntity.getAssignee();
            String processSerialNumber = (String)map.get(SysVariables.PROCESSSERIALNUMBER);
            String tenantId = (String)map.get("tenantId");
            if (StringUtils.isNotBlank(tenantId)) {
                Y9LoginUserHolder.setTenantId(tenantId);
                Person person = personManager.getPerson(tenantId, assignee);
                OrgUnit bureau = null;
                OrgUnit orgUnit = null;
                if (person == null || StringUtils.isBlank(person.getId())) {
                    Position position = positionApi.getPosition(tenantId, assignee);
                    bureau = departmentApi.getBureau(tenantId, position.getParentId());
                    orgUnit = orgUnitManager.getOrgUnit(tenantId, position.getParentId());
                } else {// 人员
                    bureau = personManager.getBureau(tenantId, person.getId());
                    orgUnit = orgUnitManager.getOrgUnit(tenantId, person.getParentId());
                }
                ProcessParamModel processParamModel = processParamManager.findByProcessSerialNumber(tenantId, processSerialNumber);
                String oldDeptId = StringUtils.isBlank(processParamModel.getDeptIds()) ? "" : processParamModel.getDeptIds();
                String oldDeptId1 = StringUtils.isBlank(processParamModel.getDeptIds()) ? "" : processParamModel.getDeptIds();
                if (!oldDeptId.contains(orgUnit.getId())) {
                    oldDeptId = Y9Util.genCustomStr(oldDeptId, orgUnit.getId());
                }
                String oldBureauId = StringUtils.isBlank(processParamModel.getBureauIds()) ? "" : processParamModel.getBureauIds();
                String oldBureauId1 = StringUtils.isBlank(processParamModel.getBureauIds()) ? "" : processParamModel.getBureauIds();
                if (bureau != null) {
                    if (!oldBureauId.contains(bureau.getId())) {
                        oldBureauId = Y9Util.genCustomStr(oldBureauId, bureau.getId());
                    }
                }
                if (!oldBureauId.equals(oldBureauId1)) {
                    processParamModel.setBureauIds(oldBureauId);
                    processParamModel.setDeptIds(oldDeptId);
                    processParamModel.setProcessInstanceId(taskEntity.getProcessInstanceId());
                    processParamManager.saveOrUpdate(tenantId, processParamModel);
                } else {
                    if (!oldDeptId.equals(oldDeptId1)) {
                        processParamModel.setDeptIds(oldDeptId);
                        processParamModel.setProcessInstanceId(taskEntity.getProcessInstanceId());
                        processParamManager.saveOrUpdate(tenantId, processParamModel);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.warn("##########################保存科室id失败-taskId:{}##########################", taskEntity.getId(), e);
        }
    }

}
