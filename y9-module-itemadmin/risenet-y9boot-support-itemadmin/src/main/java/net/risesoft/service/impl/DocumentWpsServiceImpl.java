package net.risesoft.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.documentword.DocumentWps;
import net.risesoft.repository.documentword.DocumentWpsRepository;
import net.risesoft.service.DocumentWpsService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class DocumentWpsServiceImpl implements DocumentWpsService {

    private final DocumentWpsRepository documentWpsRepository;

    @Override
    public DocumentWps findById(String id) {
        return documentWpsRepository.findById(id).orElse(null);
    }

    @Override
    public DocumentWps findByProcessSerialNumber(String processSerialNumber) {
        return documentWpsRepository.findByProcessSerialNumber(processSerialNumber);
    }

    @Override
    @Transactional
    public void saveDocumentWps(DocumentWps documentWps) {
        DocumentWps wps = documentWpsRepository.findByProcessSerialNumber(documentWps.getProcessSerialNumber());
        if (wps != null) {
            wps.setFileName(documentWps.getFileName());
            wps.setFileId(documentWps.getFileId());
            wps.setFileType(documentWps.getFileType());
            wps.setHasContent(documentWps.getHasContent());
            wps.setIstaohong(documentWps.getIstaohong());
            wps.setProcessInstanceId(documentWps.getProcessInstanceId());
            wps.setSaveDate(documentWps.getSaveDate());
            wps.setTenantId(documentWps.getTenantId());
            wps.setUserId(documentWps.getUserId());
            wps.setVolumeId(documentWps.getVolumeId());
            documentWpsRepository.save(wps);
        } else {
            documentWpsRepository.save(documentWps);
        }
    }

    @Override
    @Transactional
    public void saveWpsContent(String processSerialNumber, String hasContent) {
        documentWpsRepository.updateHasContent(processSerialNumber, hasContent);
    }

    @Override
    @Transactional
    public void updateProcessInstanceId(String processSerialNumber, String processInstanceId) {
        try {
            documentWpsRepository.updateProcessInstanceId(processSerialNumber, processInstanceId);
        } catch (Exception e) {
            LOGGER.error("Failed to update processInstanceId for processSerialNumber: " + processSerialNumber, e);
        }
    }

}
