package net.risesoft.service.impl;

import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.ProcessInstanceApi;
import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.model.itemadmin.ProcessInstanceDetailsModel;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.model.platform.Person;
import net.risesoft.model.platform.Position;
import net.risesoft.service.ProcessInstanceDetailsService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.configuration.Y9Properties;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@Service(value = "processInstanceDetailsService")
@Slf4j
public class ProcessInstanceDetailsServiceImpl implements ProcessInstanceDetailsService {

    @Autowired
    private PersonApi personManager;

    @Autowired
    private PositionApi positionApi;

    @Autowired
    private ProcessParamApi processParamManager;

    @Autowired
    private ProcessInstanceApi processInstanceApi;

    @Autowired
    private Y9Properties y9Conf;

    @Override
    public void saveProcessInstanceDetails(final DelegateTask task, final Map<String, Object> map) {
        Boolean cooperationStateSwitch = y9Conf.getApp().getProcessAdmin().getCooperationStateSwitch();
        if (cooperationStateSwitch == null || !cooperationStateSwitch) {
            LOGGER.info("######################协作状态开关已关闭,如需保存数据到协作状态请更改配置文件######################");
            return;
        }
        try {
            String assigneeId = task.getAssignee();
            String tenantId = (String)map.get("tenantId");
            String processSerialNumber = (String)map.get("processSerialNumber");

            ProcessParamModel processParamModel =
                processParamManager.findByProcessSerialNumber(tenantId, processSerialNumber);

            String itemId = processParamModel.getItemId();
            String number = processParamModel.getCustomNumber();
            String systemCnName = processParamModel.getSystemCnName();
            String systemName = processParamModel.getSystemName();
            String documentTitle = processParamModel.getTitle();
            String todoTaskUrlPrefix = processParamModel.getTodoTaskUrlPrefix();

            String userId = map.get(SysVariables.TASKSENDERID).toString();
            String assigneeName = "";
            String senderName = "";
            Person person = personManager.getPerson(tenantId, assigneeId).getData();
            if (person == null || StringUtils.isBlank(person.getId())) {
                Position position = positionApi.getPosition(tenantId, assigneeId).getData();
                assigneeName = position.getName();

                position = positionApi.getPosition(tenantId, userId).getData();
                senderName = position.getName();
            } else {
                assigneeName = person.getName();
                person = personManager.getPerson(tenantId, userId).getData();
                senderName = person.getName();
            }
            String processInstanceId = task.getProcessInstanceId();
            try {
                String sponsorGuid = processParamModel.getSponsorGuid();
                if (StringUtils.isNotBlank(sponsorGuid) && sponsorGuid.equals(assigneeId)) {
                    assigneeName = assigneeName + "(主办)";
                }
            } catch (Exception e) {
                e.printStackTrace();
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
            boolean b = processInstanceApi.saveProcessInstanceDetails(tenantId, processInstanceDetails);
            if (b) {
                LOGGER.info("#################协作状态保存成功-TASK_ASSIGNED####任务id:{}{}#################", task.getName(),
                    task.getId());
            } else {
                LOGGER.info("#################协作状态保存失败-TASK_ASSIGNED####任务id:{}{}#################", task.getName(),
                    task.getId());
            }
            return;
        } catch (Exception e) {
            LOGGER.warn("#################保存事件办理情况失败:1-TASK_ASSIGNED{}#################", task.getName());
            e.printStackTrace();
        }
    }

    @Override
    @Async
    public void updateProcessInstanceDetails(final DelegateTask taskEntityHti, final Map<String, Object> map) {
        Boolean cooperationStateSwitch = y9Conf.getApp().getProcessAdmin().getCooperationStateSwitch();
        if (cooperationStateSwitch == null || !cooperationStateSwitch) {
            LOGGER.info("######################协作状态开关已关闭,如需保存数据到协作状态请更改配置文件######################");
            return;
        }
        try {
            String assigneeId = taskEntityHti.getAssignee() != null ? taskEntityHti.getAssignee() : "";
            String tenantId = (String)map.get("tenantId");
            boolean b = processInstanceApi.updateProcessInstanceDetails(tenantId, assigneeId,
                taskEntityHti.getProcessInstanceId(), taskEntityHti.getId(), ItemBoxTypeEnum.DONE.getValue(),
                new Date());
            if (b) {
                LOGGER.info("#################更新协作状态成功-TASK_COMPLETED####任务id:{}{}################",
                    taskEntityHti.getName(), taskEntityHti.getId());
            } else {
                LOGGER.info("#################更新协作状态失败-TASK_COMPLETED####任务id:{}{}#################",
                    taskEntityHti.getName(), taskEntityHti.getId());
            }
        } catch (Exception e) {
            LOGGER.warn("#################更新协作状态失败-TASK_COMPLETED#################");
            e.printStackTrace();
        }
    }

}
