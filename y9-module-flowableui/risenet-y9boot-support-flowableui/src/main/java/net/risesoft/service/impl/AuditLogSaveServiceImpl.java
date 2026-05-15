package net.risesoft.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.Y9WordApi;
import net.risesoft.enums.FlowableUiAuditLogEnum;
import net.risesoft.model.itemadmin.DocumentWpsModel;
import net.risesoft.pojo.AuditLogEvent;
import net.risesoft.service.AuditLogSaveService;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.util.Y9StringUtil;
import net.risesoft.y9public.entity.Y9FileStore;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuditLogSaveServiceImpl implements AuditLogSaveService {

    private static final String SAVE_ERROR = "保存正文信息失败";
    private final Y9WordApi y9WordApi;

    @Override
    @Transactional
    public void wordUploadLog(String title, String processSerialNumber, Y9FileStore y9FileStore) {
        try {
            AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                .action(FlowableUiAuditLogEnum.WORD_UPLOAD.getAction())
                .description(Y9StringUtil.format(FlowableUiAuditLogEnum.WORD_UPLOAD.getDescription(), title))
                .objectId(processSerialNumber)
                .oldObject(y9FileStore)
                .currentObject(null)
                .build();
            Y9Context.publishEvent(auditLogEvent);
        } catch (Exception e) {
            LOGGER.error(SAVE_ERROR, e);
        }
    }

    @Override
    @Transactional
    public void wpsUploadLog(String title, String processSerialNumber, Y9FileStore y9FileStore, String type) {
        try {
            String action = FlowableUiAuditLogEnum.WPS_UPLOAD.getAction();
            String description = Y9StringUtil.format(FlowableUiAuditLogEnum.WPS_UPLOAD.getDescription(), title);
            if (type.equals("mobile")) {
                action = FlowableUiAuditLogEnum.WPS_UPLOAD_MOBILE.getAction();
                description = Y9StringUtil.format(FlowableUiAuditLogEnum.WPS_UPLOAD_MOBILE.getDescription(), title);
            }
            AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                .action(action)
                .description(description)
                .objectId(processSerialNumber)
                .oldObject(y9FileStore)
                .currentObject(null)
                .build();
            Y9Context.publishEvent(auditLogEvent);
        } catch (Exception e) {
            LOGGER.error(SAVE_ERROR, e);
        }
    }

    @Override
    @Transactional
    public void downLoadWordLog(String fileStoreId, String title, String downloadType) {
        try {
            String action = "", description = "";
            if ("downloadHis".equals(downloadType)) {
                action = FlowableUiAuditLogEnum.WORD_DOWNLOAD_HISTORY.getAction();
                description = Y9StringUtil.format(FlowableUiAuditLogEnum.WORD_DOWNLOAD_HISTORY.getDescription(), title);
            } else if ("downloadCs".equals(downloadType)) {
                action = FlowableUiAuditLogEnum.WORD_DOWNLOAD_CS.getAction();
                description = Y9StringUtil.format(FlowableUiAuditLogEnum.WORD_DOWNLOAD_CS.getDescription(), title);
            } else {
                action = FlowableUiAuditLogEnum.WORD_DOWNLOAD.getAction();
                description = Y9StringUtil.format(FlowableUiAuditLogEnum.WORD_DOWNLOAD.getDescription(), title);
            }
            AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                .action(action)
                .description(description)
                .objectId(fileStoreId)
                .oldObject(title)
                .currentObject(null)
                .build();
            Y9Context.publishEvent(auditLogEvent);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public void downloadWpsLog(DocumentWpsModel documentWpsModel) {
        try {
            AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                .action(FlowableUiAuditLogEnum.WPS_DOWNLOAD.getAction())
                .description(Y9StringUtil.format(FlowableUiAuditLogEnum.WPS_DOWNLOAD.getDescription(),
                    documentWpsModel.getFileName()))
                .objectId(documentWpsModel.getId())
                .oldObject(documentWpsModel)
                .currentObject(null)
                .build();
            Y9Context.publishEvent(auditLogEvent);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public void downloadWpsLog(String id, String title, String type) {
        try {
            String action = "", description = "";
            if ("mobile".equals(type)) {
                action = FlowableUiAuditLogEnum.WPS_DOWNLOAD_MOBILE.getAction();
                description = Y9StringUtil.format(FlowableUiAuditLogEnum.WPS_DOWNLOAD_MOBILE.getDescription(), title);
            } else {
                action = FlowableUiAuditLogEnum.WPS_DOWNLOAD.getAction();
                description = Y9StringUtil.format(FlowableUiAuditLogEnum.WPS_DOWNLOAD.getDescription(), title);
            }
            AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                .action(action)
                .description(description)
                .objectId(id)
                .oldObject(title)
                .currentObject(null)
                .build();
            Y9Context.publishEvent(auditLogEvent);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public void revokeRedHeaderLog(String id, String title, String type) {
        try {
            AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                .action(FlowableUiAuditLogEnum.WPS_REVOKE_MOBILE.getAction())
                .description(Y9StringUtil.format(FlowableUiAuditLogEnum.WPS_REVOKE_MOBILE.getDescription(), title))
                .objectId(id)
                .oldObject(title)
                .currentObject(null)
                .build();
            Y9Context.publishEvent(auditLogEvent);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
