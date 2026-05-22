package net.risesoft.service.impl;

import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.consts.processadmin.SysVariables;
import net.risesoft.enums.ItemAdminAuditLogEnum;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.pojo.AuditLogEvent;
import net.risesoft.service.AsyncPublishEventService;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.util.Y9StringUtil;

/**
 * @author qinman
 * @author yihong
 * @date 2025/05/15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncPublishEventServiceImpl implements AsyncPublishEventService {

    private final OrgUnitApi orgUnitApi;

    private final TaskApi taskApi;

    @Async
    @Override
    @Transactional
    public void sendAuditLog(String tenantId, String title, String userChoice) {
        try {
            String[] userChoices = userChoice.split(SysVariables.SEMICOLON);
            for (String choice : userChoices) {
                String[] parts = choice.split(SysVariables.COLON);
                String userId = choice;
                if (parts.length == 2) {
                    userId = parts[1];
                }
                OrgUnit orgUnit = orgUnitApi.getOrgUnit(tenantId, userId).getData();
                AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                    .action(ItemAdminAuditLogEnum.DOCUMENT_SEND.getAction())
                    .description(Y9StringUtil.format(ItemAdminAuditLogEnum.DOCUMENT_SEND.getDescription(), title,
                        orgUnit.getName()))
                    .objectId(userId)
                    .oldObject(orgUnit)
                    .currentObject(null)
                    .build();
                Y9Context.publishEvent(auditLogEvent);
            }
        } catch (Exception e) {
            LOGGER.error("sendAuditLog error", e);
        }
    }

    @Async
    @Transactional
    @Override
    public void sendAuditLog(String tenantId, String title, List<String> userIdList) {
        try {
            for (String userId : userIdList) {
                OrgUnit orgUnit = orgUnitApi.getOrgUnit(tenantId, userId).getData();
                AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                    .action(ItemAdminAuditLogEnum.DOCUMENT_SEND.getAction())
                    .description(Y9StringUtil.format(ItemAdminAuditLogEnum.DOCUMENT_SEND.getDescription(), title,
                        orgUnit.getName()))
                    .objectId(userId)
                    .oldObject(orgUnit)
                    .currentObject(null)
                    .build();
                Y9Context.publishEvent(auditLogEvent);
            }
        } catch (Exception e) {
            LOGGER.error("sendAuditLog-userIdList error", e);
        }
    }

    @Override
    public void submitSendAuditLog(String tenantId, String title, List<String> userIdList) {
        try {
            for (String userId : userIdList) {
                OrgUnit orgUnit = orgUnitApi.getOrgUnit(tenantId, userId).getData();
                AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                    .action(ItemAdminAuditLogEnum.DOCUMENT_SUBMIT_SEND.getAction())
                    .description(Y9StringUtil.format(ItemAdminAuditLogEnum.DOCUMENT_SUBMIT_SEND.getDescription(), title,
                        orgUnit.getName()))
                    .objectId(userId)
                    .oldObject(orgUnit)
                    .currentObject(null)
                    .build();
                Y9Context.publishEvent(auditLogEvent);
            }
        } catch (Exception e) {
            LOGGER.error("submitSendAuditLog-userIdList error", e);
        }
    }
}