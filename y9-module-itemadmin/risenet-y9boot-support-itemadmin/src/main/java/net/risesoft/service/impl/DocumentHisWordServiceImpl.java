package net.risesoft.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.DocumentHistoryWord;
import net.risesoft.repository.jpa.DocumentHistoryWordRepository;
import net.risesoft.service.DocumentHisWordService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class DocumentHisWordServiceImpl implements DocumentHisWordService {

    private final DocumentHistoryWordRepository documentHistoryWordRepository;

    @Override
    public DocumentHistoryWord findWordById(String id) {
        return documentHistoryWordRepository.findById(id).orElse(null);
    }

}
