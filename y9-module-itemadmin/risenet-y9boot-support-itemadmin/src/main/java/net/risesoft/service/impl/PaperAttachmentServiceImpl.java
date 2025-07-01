package net.risesoft.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.PaperAttachment;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.repository.jpa.PaperAttachmentRepository;
import net.risesoft.service.PaperAttachmentService;

/**
 * @author : qinman
 * @date : 2024-11-07
 **/
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class PaperAttachmentServiceImpl implements PaperAttachmentService {

    private final PaperAttachmentRepository paperAttachmentRepository;

    @Override
    public PaperAttachment findById(String id) {
        return paperAttachmentRepository.findById(id).orElse(null);
    }

    @Override
    public List<PaperAttachment> findbyProcessSerialNumber(String processSerialNumber) {
        return paperAttachmentRepository.findByProcessSerialNumberOrderByTabIndexAsc(processSerialNumber);
    }

    @Override
    @Transactional
    public void saveOrUpdate(PaperAttachment paperAttachment) {
        String id = paperAttachment.getId();
        if (StringUtils.isNotBlank(id)) {
            PaperAttachment oldPa = this.findById(id);
            oldPa.setName(paperAttachment.getName());
            oldPa.setCount(paperAttachment.getCount());
            oldPa.setPages(paperAttachment.getPages());
            oldPa.setMiJi(paperAttachment.getMiJi());
            paperAttachmentRepository.save(oldPa);
            return;
        }
        paperAttachment.setId(Y9IdGenerator.genId());
        Integer tabIndex = paperAttachmentRepository.getMaxTabIndex(paperAttachment.getProcessSerialNumber());
        if (null == tabIndex) {
            tabIndex = 1;
        }
        paperAttachment.setTabIndex(tabIndex);
        paperAttachmentRepository.save(paperAttachment);
    }

    @Override
    @Transactional
    public void delFile(String ids) {
        String[] idArr = ids.split(",");
        for (String id : idArr) {
            paperAttachmentRepository.deleteById(id);
        }
    }
}
