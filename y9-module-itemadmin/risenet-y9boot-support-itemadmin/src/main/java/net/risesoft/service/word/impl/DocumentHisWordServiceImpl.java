package net.risesoft.service.word.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.documentword.DocumentHistoryWord;
import net.risesoft.repository.documentword.DocumentHistoryWordRepository;
import net.risesoft.service.word.DocumentHisWordService;

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
