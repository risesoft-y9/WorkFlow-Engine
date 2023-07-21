package net.risesoft.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.api.itemadmin.ActRuDetailApi;
import net.risesoft.api.itemadmin.ItemApi;
import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.ActRuDetailModel;
import net.risesoft.model.itemadmin.ItemModel;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.ActRuDetailService;
import net.risesoft.y9.Y9LoginUserHolder;

@Service(value = "actRuDetailService")
@Transactional(readOnly = true)
public class ActRuDetailServiceImpl implements ActRuDetailService {

    @Autowired
    private ItemApi itemManager;

    @Autowired
    private ProcessParamApi processParamManager;

    @Autowired
    private ActRuDetailApi actRuDetailManager;

    @Override
    public Y9Result<String> complete(String processSerialNumber) {
        boolean b = actRuDetailManager.endByProcessSerialNumber(Y9LoginUserHolder.getTenantId(), processSerialNumber);
        if (b) {
            return Y9Result.successMsg("办结成功");
        }
        return Y9Result.failure("办结失败");
    }

    @Override
    public Y9Result<String> saveOrUpdate(String itemId, String processSerialNumber) {
        try {
            UserInfo person = Y9LoginUserHolder.getUserInfo();
            String tenantId = Y9LoginUserHolder.getTenantId(), personId = person.getPersonId(), personName = person.getName();
            List<ActRuDetailModel> ardmList = actRuDetailManager.findByProcessSerialNumberAndStatus(tenantId, processSerialNumber, 0);
            if (!ardmList.isEmpty()) {
                return Y9Result.successMsg("已设置办理人信息");
            }
            ProcessParamModel processParamModel = processParamManager.findByProcessSerialNumber(tenantId, processSerialNumber);
            ItemModel item = itemManager.getByItemId(tenantId, itemId);
            ActRuDetailModel actRuDetailModel = new ActRuDetailModel();
            actRuDetailModel.setCreateTime(new Date());
            actRuDetailModel.setEnded(false);
            actRuDetailModel.setItemId(itemId);
            actRuDetailModel.setLastTime(new Date());
            actRuDetailModel.setProcessDefinitionKey(item.getWorkflowGuid());
            actRuDetailModel.setProcessInstanceId(processParamModel.getProcessInstanceId());
            actRuDetailModel.setProcessSerialNumber(processSerialNumber);
            actRuDetailModel.setStarted(StringUtils.isNotBlank(processParamModel.getProcessInstanceId()));
            actRuDetailModel.setSystemName(item.getSystemName());
            actRuDetailModel.setTaskId("");
            actRuDetailModel.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            actRuDetailModel.setStatus(0);
            actRuDetailModel.setAssignee(personId);
            actRuDetailModel.setAssigneeName(personName);
            actRuDetailModel.setDeptId(person.getParentId());
            actRuDetailManager.saveOrUpdate(tenantId, actRuDetailModel);
            return Y9Result.successMsg("设置办理人信息正常");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("设置办理人信息异常");
    }
}
