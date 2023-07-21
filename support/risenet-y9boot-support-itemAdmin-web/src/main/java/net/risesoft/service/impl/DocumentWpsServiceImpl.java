package net.risesoft.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.DocumentWps;
import net.risesoft.repository.jpa.DocumentWpsRepository;
import net.risesoft.service.DocumentWpsService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Service(value = "documentWpsService")
public class DocumentWpsServiceImpl implements DocumentWpsService {

    @Autowired
    private DocumentWpsRepository documentWpsRepository;

    @Override
    public DocumentWps findById(String id) {
        return documentWpsRepository.findById(id).orElse(null);
    }

    @Override
    public DocumentWps findByProcessSerialNumber(String processSerialNumber) {
        return documentWpsRepository.findByProcessSerialNumber(processSerialNumber);
    }

    @Override
    @Transactional(readOnly = false)
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
    @Transactional(readOnly = false)
    public void saveWpsContent(String processSerialNumber, String hasContent) {
        documentWpsRepository.updateHasContent(processSerialNumber, hasContent);
    }

    @Override
    @Transactional(readOnly = false)
    public void updateProcessInstanceId(String processSerialNumber, String processInstanceId) {
        try {
            documentWpsRepository.updateProcessInstanceId(processSerialNumber, processInstanceId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
