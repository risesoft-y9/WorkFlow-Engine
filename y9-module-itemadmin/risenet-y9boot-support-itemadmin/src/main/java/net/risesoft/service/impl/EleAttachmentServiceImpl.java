package net.risesoft.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.EleAttachment;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.repository.jpa.EleAttachmentRepository;
import net.risesoft.service.EleAttachmentService;
import net.risesoft.y9public.service.Y9FileStoreService;

/**
 * @author qinman
 * @date 2024/11/11
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class EleAttachmentServiceImpl implements EleAttachmentService {

    private final EleAttachmentRepository eleAttachmentRepository;

    private final Y9FileStoreService y9FileStoreService;

    @Override
    @Transactional
    public void delBatchByProcessSerialNumbers(List<String> processSerialNumbers) {
        List<EleAttachment> list = eleAttachmentRepository.findByProcessSerialNumberIn(processSerialNumbers);
        for (EleAttachment file : list) {
            try {
                eleAttachmentRepository.delete(file);
                y9FileStoreService.deleteFile(file.getFileStoreId());
            } catch (Exception e) {
                LOGGER.error("删除文件失败", e);
            }
        }
    }

    @Transactional
    @Override
    public void delFile(String ids) {
        String[] id = ids.split(",");
        for (String str : id) {
            EleAttachment eleAttachment = eleAttachmentRepository.findById(str).orElse(null);
            eleAttachmentRepository.deleteById(str);
            assert eleAttachment != null;
            y9FileStoreService.deleteFile(eleAttachment.getFileStoreId());
        }
    }

    @Override
    @Transactional
    public EleAttachment findById(String id) {
        return eleAttachmentRepository.findById(id).orElse(null);
    }

    @Override
    public List<EleAttachment> findByProcessSerialNumberAndAttachmentType(String processSerialNumber,
        String attachmentType) {
        return eleAttachmentRepository.findByProcessSerialNumberAndAttachmentTypeOrderByTabIndexAsc(processSerialNumber,
            attachmentType);
    }

    @Override
    public List<EleAttachment> findByProcessSerialNumberAndTypeOrderByTime(String processSerialNumber,
        String attachmentType) {
        return eleAttachmentRepository
            .findByProcessSerialNumberAndAttachmentTypeOrderByUploadTimeDesc(processSerialNumber, attachmentType);
    }

    @Override
    @Transactional
    public void saveOrUpdate(EleAttachment eleAttachment) {
        String id = eleAttachment.getId();
        if (StringUtils.isNotBlank(id)) {
            EleAttachment old = this.findById(id);
            old.setName(eleAttachment.getName());
            old.setMiJi(eleAttachment.getMiJi());
            old.setFileStoreId(eleAttachment.getFileStoreId());
            eleAttachmentRepository.save(old);
            return;
        }
        eleAttachment.setId(Y9IdGenerator.genId());
        Integer tabIndex = eleAttachmentRepository.getMaxTabIndex(eleAttachment.getProcessSerialNumber(),
            eleAttachment.getAttachmentType());
        eleAttachment.setTabIndex(null == tabIndex ? 1 : tabIndex + 1);
        eleAttachmentRepository.save(eleAttachment);
    }

    @Override
    @Transactional
    public void saveOrder(String id1, String id2) {
        EleAttachment eleAttachment1 = eleAttachmentRepository.findById(id1).orElse(null);
        EleAttachment eleAttachment2 = eleAttachmentRepository.findById(id2).orElse(null);
        if (eleAttachment1 != null && eleAttachment2 != null) {
            Integer tabIndex1 = eleAttachment1.getTabIndex();
            Integer tabIndex2 = eleAttachment2.getTabIndex();
            eleAttachment1.setTabIndex(tabIndex2);
            eleAttachment2.setTabIndex(tabIndex1);
            eleAttachmentRepository.save(eleAttachment1);
            eleAttachmentRepository.save(eleAttachment2);
        }
    }
}
