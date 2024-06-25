package net.risesoft.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.ActRuDetailApi;
import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.api.itemadmin.position.Item4PositionApi;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.ActRuDetailModel;
import net.risesoft.model.itemadmin.ItemModel;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.ActRuDetailService;
import net.risesoft.y9.Y9LoginUserHolder;

@RequiredArgsConstructor
@Service(value = "actRuDetailService")
@Slf4j
@Transactional(readOnly = true)
public class ActRuDetailServiceImpl implements ActRuDetailService {

    private final Item4PositionApi item4PositionApi;

    private final ProcessParamApi processParamApi;

    private final ActRuDetailApi actRuDetailApi;

    @Override
    public Y9Result<String> complete(String processSerialNumber) {
        Y9Result<Object> y9Result =
            actRuDetailApi.endByProcessSerialNumber(Y9LoginUserHolder.getTenantId(), processSerialNumber);
        if (y9Result.isSuccess()) {
            return Y9Result.successMsg("办结成功");
        }
        return Y9Result.failure("办结失败");
    }

    @Override
    public Y9Result<String> saveOrUpdate(String itemId, String processSerialNumber) {
        try {
            UserInfo person = Y9LoginUserHolder.getUserInfo();
            String tenantId = Y9LoginUserHolder.getTenantId(), personId = person.getPersonId(),
                personName = person.getName();
            List<ActRuDetailModel> ardmList =
                actRuDetailApi.findByProcessSerialNumberAndStatus(tenantId, processSerialNumber, 0).getData();
            if (!ardmList.isEmpty()) {
                return Y9Result.successMsg("已设置办理人信息");
            }
            ProcessParamModel processParamModel =
                processParamApi.findByProcessSerialNumber(tenantId, processSerialNumber);
            ItemModel item = item4PositionApi.getByItemId(tenantId, itemId);
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
            actRuDetailApi.saveOrUpdate(tenantId, actRuDetailModel);
            return Y9Result.successMsg("设置办理人信息正常");
        } catch (Exception e) {
            LOGGER.error("设置办理人信息异常", e);
        }
        return Y9Result.failure("设置办理人信息异常");
    }
}
