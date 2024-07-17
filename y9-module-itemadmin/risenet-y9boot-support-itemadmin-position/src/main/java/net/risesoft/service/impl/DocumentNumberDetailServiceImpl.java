package net.risesoft.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.DocumentNumberDetail;
import net.risesoft.repository.jpa.DocumentNumberDetailRepository;
import net.risesoft.service.DocumentNumberDetailService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class DocumentNumberDetailServiceImpl implements DocumentNumberDetailService {

    private final DocumentNumberDetailRepository documentNumberDetailRepository;

    @Override
    public DocumentNumberDetail getById(String id) {
        return documentNumberDetailRepository.findById(id).orElse(null);
    }

    @Override
    public List<DocumentNumberDetail> listAll() {
        return documentNumberDetailRepository.findAll();
    }

    @Override
    @Transactional
    public DocumentNumberDetail saveDocumentNumberDetail(DocumentNumberDetail documentNumberDetail) {
        return documentNumberDetailRepository.save(documentNumberDetail);
    }

}
