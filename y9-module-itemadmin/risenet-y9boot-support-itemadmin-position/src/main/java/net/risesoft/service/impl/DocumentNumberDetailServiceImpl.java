package net.risesoft.service.impl;

import lombok.RequiredArgsConstructor;
import net.risesoft.entity.DocumentNumberDetail;
import net.risesoft.repository.jpa.DocumentNumberDetailRepository;
import net.risesoft.service.DocumentNumberDetailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    public List<DocumentNumberDetail> findAll() {
        return documentNumberDetailRepository.findAll();
    }

    @Override
    public DocumentNumberDetail findOne(String id) {
        return documentNumberDetailRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional()
    public DocumentNumberDetail saveDocumentNumberDetail(DocumentNumberDetail documentNumberDetail) {
        return documentNumberDetailRepository.save(documentNumberDetail);
    }

}
