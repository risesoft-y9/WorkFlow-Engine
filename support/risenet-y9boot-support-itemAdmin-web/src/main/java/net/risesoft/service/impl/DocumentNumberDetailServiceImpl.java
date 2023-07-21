package net.risesoft.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.DocumentNumberDetail;
import net.risesoft.repository.jpa.DocumentNumberDetailRepository;
import net.risesoft.service.DocumentNumberDetailService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@Service(value = "documentNumberDetailService")
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class DocumentNumberDetailServiceImpl implements DocumentNumberDetailService {

    @Autowired
    private DocumentNumberDetailRepository documentNumberDetailRepository;

    @Override
    public List<DocumentNumberDetail> findAll() {
        return documentNumberDetailRepository.findAll();
    }

    @Override
    public DocumentNumberDetail findOne(String id) {
        return documentNumberDetailRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = false)
    public DocumentNumberDetail saveDocumentNumberDetail(DocumentNumberDetail documentNumberDetail) {
        return documentNumberDetailRepository.save(documentNumberDetail);
    }

}
