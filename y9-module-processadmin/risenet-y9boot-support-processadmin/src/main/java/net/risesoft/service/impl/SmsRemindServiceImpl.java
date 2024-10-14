package net.risesoft.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.api.sms.SmsHttpApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Person;
import net.risesoft.service.SmsRemindService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.configuration.app.y9processadmin.Y9ProcessAdminProperties;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@Slf4j
@RequiredArgsConstructor
@Service(value = "smsRemindService")
public class SmsRemindServiceImpl implements SmsRemindService {

    private final OrgUnitApi orgUnitApi;

    private final PositionApi positionApi;

    private final ProcessParamApi processParamApi;

    private final Y9ProcessAdminProperties y9ProcessAdminProperties;

    private final SmsHttpApi smsHttpApi;

    /**
     * 短信提醒
     */
    @Override
    public void smsRemind(final DelegateTask task, final Map<String, Object> map, final Map<String, Object> local) {

        Boolean smsSwitch = y9ProcessAdminProperties.getSmsSwitch();
        if (smsSwitch == null || !smsSwitch) {
            LOGGER.info("######################短信提醒开关已关闭,如需短信提醒请更改配置文件######################");
            return;
        }
        try {
            String assignee = task.getAssignee();
            String tenantId = (String)map.get("tenantId");
            String processInstanceId = task.getProcessInstanceId();
            ProcessParamModel processParamModel =
                processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
            String isSendSms = processParamModel.getIsSendSms();
            String isShuMing = processParamModel.getIsShuMing();
            String smsContent = processParamModel.getSmsContent();
            String smsPersonId = processParamModel.getSmsPersonId();
            if (StringUtils.isBlank(isSendSms) || UtilConsts.FALSE.equals(isSendSms)) {
                LOGGER.info("######################短信提醒已取消######################");
                return;
            }
            if (StringUtils.isNotBlank(smsPersonId)) {
                if (!smsPersonId.contains(assignee)) {
                    LOGGER.info("######################不在指定人员内不发短信######################");
                    return;
                }
            }
            // 收回或者退回产生的任务不进行短信提醒，主要针对串行的收回或者退回，串行时的收回退回是办结所有串行任务，因此产生的新任务无需提醒
            if (local.get(SysVariables.TAKEBACK) != null) {
                return;
            }
            String send = processParamModel.getSended();
            // 第一步新建产生的任务，不发送提醒
            if (StringUtils.isBlank(send) || UtilConsts.FALSE.equals(send)) {
                return;
            }
            List<String> list = new ArrayList<>();
            String userId = map.get(SysVariables.TASKSENDERID).toString();
            OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, userId).getData();
            if (UtilConsts.TRUE.equals(isShuMing)) {
                smsContent = smsContent + "--" + orgUnit.getName();
            }
            OrgUnit orgUnit0 = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, assignee).getData();
            if (orgUnit0.getOrgType().equals(OrgTypeEnum.POSITION)) {
                List<Person> plist = positionApi.listPersonsByPositionId(tenantId, assignee).getData();
                for (Person p : plist) {
                    list.add(p.getMobile());
                }
            } else {
                list.add(((Person)orgUnit0).getMobile());
            }
            smsHttpApi.sendSmsHttpList(tenantId, userId, list, smsContent, processParamModel.getSystemCnName());
        } catch (Exception e) {
            LOGGER.warn("##########################短信提醒时发生异常-taskId:{}##########################", task.getId(), e);
        }
    }

}
