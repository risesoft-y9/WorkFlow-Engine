package net.risesoft.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.DocumentApi;
import net.risesoft.api.itemadmin.ItemApi;
import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.model.itemadmin.ItemModel;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.model.itemadmin.StartProcessResultModel;
import net.risesoft.model.platform.Department;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.AsyncUtilService;
import net.risesoft.service.ProcessParamService;
import net.risesoft.y9.Y9LoginUserHolder;

@Slf4j
@RequiredArgsConstructor
@Service(value = "processParamService")
@Transactional(readOnly = true)
public class ProcessParamServiceImpl implements ProcessParamService {

    private final ItemApi itemApi;

    private final ProcessParamApi processParamApi;

    private final AsyncUtilService asyncUtilService;

    private final DocumentApi documentApi;

    private final OrgUnitApi orgUnitApi;

    @Override
    public Y9Result<String> saveOrUpdate(String itemId, String processSerialNumber, String processInstanceId,
        String documentTitle, String number, String level, Boolean customItem) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            ItemModel item = itemApi.getByItemId(tenantId, itemId).getData();
            ProcessParamModel processParamModel =
                processParamApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
            if (StringUtils.isNotBlank(processInstanceId)) {
                if (StringUtils.isNotBlank(documentTitle) && (StringUtils.isBlank(processParamModel.getTitle())
                    || !processParamModel.getTitle().equals(documentTitle))) {
                    asyncUtilService.updateTitle(tenantId, processInstanceId, documentTitle);
                }
            }
            ProcessParamModel pp = new ProcessParamModel();
            pp.setDeptIds("");
            pp.setBureauIds("");
            pp.setCustomLevel(level);
            pp.setCustomNumber(number);
            pp.setItemId(itemId);
            pp.setItemName(item.getName());
            pp.setProcessInstanceId(processInstanceId);
            pp.setProcessSerialNumber(processSerialNumber);
            pp.setSystemName(item.getSystemName());
            pp.setSystemCnName(item.getSysLevel());
            pp.setTitle(documentTitle);
            pp.setSponsorGuid(processParamModel != null ? processParamModel.getSponsorGuid() : "");
            pp.setSended(processParamModel != null ? processParamModel.getSended() : "");
            pp.setStartor(processParamModel != null ? processParamModel.getStartor() : "");
            pp.setStartorName(processParamModel != null ? processParamModel.getStartorName() : "");
            pp.setTodoTaskUrlPrefix(item.getTodoTaskUrlPrefix());
            pp.setCustomItem(processParamModel != null ? processParamModel.getCustomItem() : customItem);
            pp.setSearchTerm(documentTitle + "|" + number + "|" + level + "|" + item.getName());
            processParamApi.saveOrUpdate(tenantId, pp);
            return Y9Result.successMsg("保存成功");
        } catch (Exception e) {
            LOGGER.error("保存失败", e);
        }
        return Y9Result.failure("保存失败");
    }

    @Override
    public Y9Result<StartProcessResultModel> saveOrUpdate(String itemId, String processSerialNumber,
        String processInstanceId, String documentTitle, String number, String level, Boolean customItem,
        String startTaskDefKey) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            ItemModel item = itemApi.getByItemId(tenantId, itemId).getData();
            ProcessParamModel oldProcessParam =
                processParamApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
            if (StringUtils.isNotBlank(processInstanceId)) {
                if (StringUtils.isNotBlank(documentTitle) && (StringUtils.isBlank(oldProcessParam.getTitle())
                    || !oldProcessParam.getTitle().equals(documentTitle))) {
                    asyncUtilService.updateTitle(tenantId, processInstanceId, documentTitle);
                }
            }
            ProcessParamModel pp = new ProcessParamModel();
            pp.setDeptIds("");
            pp.setBureauIds("");
            pp.setCustomLevel(level);
            pp.setCustomNumber(number);
            pp.setItemId(itemId);
            pp.setItemName(item.getName());
            pp.setProcessInstanceId(processInstanceId);
            pp.setProcessSerialNumber(processSerialNumber);
            pp.setSystemName(item.getSystemName());
            pp.setSystemCnName(item.getSysLevel());
            pp.setTitle(documentTitle);
            pp.setTodoTaskUrlPrefix(item.getTodoTaskUrlPrefix());
            pp.setSearchTerm(documentTitle + "|" + number + "|" + level + "|" + item.getName());
            if (null != oldProcessParam) {
                pp.setSponsorGuid(oldProcessParam.getSponsorGuid());
                pp.setSended(oldProcessParam.getSended());
                pp.setStartor(oldProcessParam.getStartor());
                pp.setStartorName(oldProcessParam.getStartorName());
                pp.setCustomItem(oldProcessParam.getCustomItem());
                pp.setHostDeptId(oldProcessParam.getHostDeptId());
                pp.setHostDeptName(oldProcessParam.getHostDeptName());
            } else {
                pp.setCustomItem(customItem);
                OrgUnit bureau = orgUnitApi.getBureau(tenantId, Y9LoginUserHolder.getPositionId()).getData();
                pp.setHostDeptId(bureau.getId());
                if (bureau instanceof Department) {
                    Department department = (Department)bureau;
                    pp.setHostDeptName(
                        StringUtils.isBlank(department.getAliasName()) ? bureau.getName() : department.getAliasName());
                } else {
                    pp.setHostDeptName(bureau.getName());
                }
            }
            processParamApi.saveOrUpdate(tenantId, pp);

            if (StringUtils.isBlank(processInstanceId)) {
                return documentApi.startProcessByTheTaskKey(tenantId, Y9LoginUserHolder.getPositionId(), itemId,
                    processSerialNumber, item.getWorkflowGuid(), startTaskDefKey, "");
            }
            return Y9Result.successMsg("保存成功");
        } catch (Exception e) {
            LOGGER.error("保存失败", e);
        }
        return Y9Result.failure("保存失败");
    }
}
