package net.risesoft.service.impl;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.ChaoSongApi;
import net.risesoft.api.itemadmin.OfficeFollowApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.processadmin.HistoricTaskApi;
import net.risesoft.enums.FlowableUiAuditLogEnum;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.model.processadmin.HistoricTaskInstanceModel;
import net.risesoft.pojo.AuditLogEvent;
import net.risesoft.service.AsyncUtilService;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.util.Y9StringUtil;

@Slf4j
@RequiredArgsConstructor
@Service
public class AsyncUtilServiceImpl implements AsyncUtilService {

    private final ChaoSongApi chaoSongApi;

    private final OfficeFollowApi officeFollowApi;

    private final HistoricTaskApi historicTaskApi;

    private final OrgUnitApi orgUnitApi;

    @Async
    @Override
    public void updateTitle(final String tenantId, final String processInstanceId, final String documentTitle) {
        try {
            chaoSongApi.updateTitle(tenantId, processInstanceId, documentTitle);
            officeFollowApi.updateTitle(tenantId, processInstanceId, documentTitle);
        } catch (Exception e) {
            LOGGER.error("更新统一待办，抄送件标题", e);
        }
    }

    @Async
    @Override
    @Transactional
    public void resumeToDoAuditLog(String tenantId, String processInstanceId, String title) {
        try {
            AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                .action(FlowableUiAuditLogEnum.DOCUMENT_RESUMETODO.getAction())
                .description(Y9StringUtil.format(FlowableUiAuditLogEnum.DOCUMENT_RESUMETODO.getDescription(), title))
                .objectId(processInstanceId)
                .oldObject(processInstanceId)
                .currentObject(null)
                .build();
            Y9Context.publishEvent(auditLogEvent);
        } catch (Exception e) {
            LOGGER.error("保存恢复待办审计日志失败", e);
        }
    }

    @Async
    @Override
    @Transactional
    public void claimAuditLog(String tenantId, String orgUnitId, String taskId) {
        try {
            OrgUnit orgUnit = orgUnitApi.getOrgUnit(tenantId, orgUnitId).getData();
            HistoricTaskInstanceModel historicTaskInstanceModel = historicTaskApi.getById(tenantId, taskId).getData();
            AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                .action(FlowableUiAuditLogEnum.DOCUMENT_CLAIM.getAction())
                .description(Y9StringUtil.format(FlowableUiAuditLogEnum.DOCUMENT_CLAIM.getDescription(),
                    orgUnit.getName(), historicTaskInstanceModel.getName()))
                .objectId(taskId)
                .oldObject(historicTaskInstanceModel)
                .currentObject(null)
                .build();
            Y9Context.publishEvent(auditLogEvent);
        } catch (Exception e) {
            LOGGER.error("保存签收任务审计日志失败", e);
        }
    }

    @Async
    @Override
    @Transactional
    public void sendStarterAuditLog(String tenantId, String orgUnitId, String taskId) {
        try {
            OrgUnit orgUnit = orgUnitApi.getOrgUnit(tenantId, orgUnitId).getData();
            HistoricTaskInstanceModel historicTaskInstanceModel = historicTaskApi.getById(tenantId, taskId).getData();
            AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                .action(FlowableUiAuditLogEnum.BUTTON_SEND_TO_STARTOR.getAction())
                .description(Y9StringUtil.format(FlowableUiAuditLogEnum.BUTTON_SEND_TO_STARTOR.getDescription(),
                    orgUnit.getName(), historicTaskInstanceModel.getName()))
                .objectId(taskId)
                .oldObject(historicTaskInstanceModel)
                .currentObject(null)
                .build();
            Y9Context.publishEvent(auditLogEvent);
        } catch (Exception e) {
            LOGGER.error("保存发送拟稿人审计日志失败", e);
        }
    }
}
