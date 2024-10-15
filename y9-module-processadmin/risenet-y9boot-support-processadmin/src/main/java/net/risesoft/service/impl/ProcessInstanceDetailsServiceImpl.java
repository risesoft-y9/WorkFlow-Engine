package net.risesoft.service.impl;

import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.ProcessInstanceApi;
import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.model.itemadmin.ProcessInstanceDetailsModel;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.service.ProcessInstanceDetailsService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.configuration.app.y9processadmin.Y9ProcessAdminProperties;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@Slf4j
@RequiredArgsConstructor
@Service(value = "processInstanceDetailsService")
public class ProcessInstanceDetailsServiceImpl implements ProcessInstanceDetailsService {

    private final OrgUnitApi orgUnitApi;

    private final ProcessParamApi processParamApi;

    private final ProcessInstanceApi processInstanceApi;

    private final Y9ProcessAdminProperties y9ProcessAdminProperties;

    @Override
    public void saveProcessInstanceDetails(final DelegateTask task, final Map<String, Object> map) {
        Boolean cooperationStateSwitch = y9ProcessAdminProperties.getCooperationStateSwitch();
        if (cooperationStateSwitch == null || !cooperationStateSwitch) {
            LOGGER.info("######################协作状态开关已关闭,如需保存数据到协作状态请更改配置文件######################");
            return;
        }
        try {
            String assigneeId = task.getAssignee();
            String tenantId = (String)map.get("tenantId");
            String processSerialNumber = (String)map.get("processSerialNumber");

            ProcessParamModel processParamModel =
                processParamApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();

            String itemId = processParamModel.getItemId();
            String number = processParamModel.getCustomNumber();
            String systemCnName = processParamModel.getSystemCnName();
            String systemName = processParamModel.getSystemName();
            String documentTitle = processParamModel.getTitle();
            String todoTaskUrlPrefix = processParamModel.getTodoTaskUrlPrefix();

            String userId = map.get(SysVariables.TASKSENDERID).toString();
            String assigneeName = "", senderName = "";
            OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, assigneeId).getData();
            OrgUnit sendOrgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, userId).getData();
            if (orgUnit != null && StringUtils.isNotBlank(orgUnit.getId())) {
                assigneeName = orgUnit.getName();
            }
            if (sendOrgUnit != null && StringUtils.isNotBlank(sendOrgUnit.getId())) {
                senderName = sendOrgUnit.getName();
            }
            String processInstanceId = task.getProcessInstanceId();
            String sponsorGuid = processParamModel.getSponsorGuid();
            if (StringUtils.isNotBlank(sponsorGuid) && sponsorGuid.equals(assigneeId)) {
                assigneeName = assigneeName + "(主办)";
            }
            String url = todoTaskUrlPrefix + "?itemId=" + itemId + "&processInstanceId=" + processInstanceId
                + "&type=fromCplane";
            ProcessInstanceDetailsModel processInstanceDetails = new ProcessInstanceDetailsModel();
            processInstanceDetails.setAssigneeId(assigneeId);
            processInstanceDetails.setAssigneeName(assigneeName);
            processInstanceDetails.setItembox(ItemBoxTypeEnum.DOING.getValue());
            processInstanceDetails.setItemId(itemId);
            processInstanceDetails.setProcessInstanceId(processInstanceId);
            processInstanceDetails.setProcessSerialNumber(processSerialNumber);
            processInstanceDetails.setSenderId(userId);
            processInstanceDetails.setSenderName(senderName);
            processInstanceDetails.setSerialNumber(number);
            processInstanceDetails.setStartTime(task.getCreateTime());
            processInstanceDetails.setSystemCnName(systemCnName);
            processInstanceDetails.setSystemName(systemName);
            processInstanceDetails.setTaskId(task.getId());
            processInstanceDetails.setTaskName(task.getName());
            processInstanceDetails.setTitle(documentTitle);
            processInstanceDetails.setUrl(url);
            processInstanceDetails.setUserName(processParamModel.getStartorName());
            boolean b = processInstanceApi.saveProcessInstanceDetails(tenantId, processInstanceDetails).getData();
            if (b) {
                LOGGER.info("#################协作状态保存成功-TASK_ASSIGNED####任务id:{}{}#################", task.getAssignee(),
                    task.getId());
            } else {
                LOGGER.error("#################协作状态保存失败-TASK_ASSIGNED####任务id:{}{}#################",
                    task.getAssignee(), task.getId());
            }
        } catch (Exception e) {
            LOGGER.error("#################保存事件办理情况失败:1-TASK_ASSIGNED：{} 错误信息：{}#################", task.getAssignee(),
                e.getMessage());
        }
    }

    @Override
    public void updateProcessInstanceDetails(final DelegateTask taskEntityHti, final Map<String, Object> map) {
        Boolean cooperationStateSwitch = y9ProcessAdminProperties.getCooperationStateSwitch();
        if (cooperationStateSwitch == null || !cooperationStateSwitch) {
            LOGGER.info("######################协作状态开关已关闭,如需保存数据到协作状态请更改配置文件######################");
            return;
        }
        try {
            String assigneeId = taskEntityHti.getAssignee() != null ? taskEntityHti.getAssignee() : "";
            String tenantId = (String)map.get("tenantId");
            boolean b = processInstanceApi
                .updateProcessInstanceDetails(tenantId, assigneeId, taskEntityHti.getProcessInstanceId(),
                    taskEntityHti.getId(), ItemBoxTypeEnum.DONE.getValue(), new Date())
                .getData();
            if (b) {
                LOGGER.info("#################更新协作状态成功-TASK_COMPLETED####任务id:{}{}################",
                    taskEntityHti.getName(), taskEntityHti.getId());
            } else {
                LOGGER.error("#################更新协作状态失败-TASK_COMPLETED####任务id:{}{}#################",
                    taskEntityHti.getName(), taskEntityHti.getId());
            }
        } catch (Exception e) {
            LOGGER.error("#################更新协作状态失败-TASK_COMPLETED#################");
        }
    }

}
