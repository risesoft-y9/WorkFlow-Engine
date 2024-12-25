package net.risesoft.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.DocumentHistoryWord;
import net.risesoft.entity.DocumentWord;
import net.risesoft.model.itemadmin.DocumentWordModel;
import net.risesoft.repository.jpa.DocumentHistoryWordRepository;
import net.risesoft.repository.jpa.DocumentWordRepository;
import net.risesoft.service.DocumentWordService;
import net.risesoft.y9.util.Y9BeanUtil;
import net.risesoft.y9public.service.Y9FileStoreService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class DocumentWordServiceImpl implements DocumentWordService {

    private final DocumentWordRepository documentWordRepository;

    private final DocumentHistoryWordRepository documentHistoryWordRepository;

    private final Y9FileStoreService y9FileStoreService;

    @Override
    public List<DocumentWordModel> findByProcessSerialNumberAndWordType(String processSerialNumber, String wordType) {
        List<DocumentWord> list =
            documentWordRepository.findByProcessSerialNumberAndWordTypeOderByTypeDesc(processSerialNumber, wordType);
        List<DocumentWordModel> resultList = new ArrayList<>();
        for (DocumentWord documentWord : list) {
            DocumentWordModel documentWordModel = new DocumentWordModel();
            Y9BeanUtil.copyProperties(documentWord, documentWordModel);
            resultList.add(documentWordModel);
        }
        return resultList;
    }

    @Override
    public DocumentWord findWordById(String id) {
        return documentWordRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void replaceWord(DocumentWord documentWord, String oldId, String taskId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        documentWordRepository.save(documentWord);
        DocumentWord oldDocumentWord = documentWordRepository.findById(oldId).orElse(null);
        DocumentHistoryWord documentHistoryWord = new DocumentHistoryWord();
        Y9BeanUtil.copyProperties(oldDocumentWord, documentHistoryWord);
        documentHistoryWord.setTaskId(taskId);
        documentHistoryWord.setUpdateDate(sdf.format(new Date()));
        documentHistoryWordRepository.save(documentHistoryWord);
        documentWordRepository.deleteById(oldId);
    }

    @Override
    @Transactional
    public DocumentWord saveWord(DocumentWord documentWord) {
        DocumentWord oldDocumentWord = documentWordRepository.findByProcessSerialNumberAndWordTypeAndType(
            documentWord.getProcessSerialNumber(), documentWord.getWordType(), documentWord.getType());
        if (oldDocumentWord != null) {// 存在当前type则更新
            documentWord.setId(oldDocumentWord.getId());
        }
        documentWordRepository.save(documentWord);
        List<DocumentWord> list = documentWordRepository.findByProcessSerialNumberAndWordTypeOderByTypeDesc(
            documentWord.getProcessSerialNumber(), documentWord.getWordType());
        for (DocumentWord word : list) {
            if (word.getType() > documentWord.getType()) {// 大于当前type则删除，撤销套红，撤销pdf等
                documentWordRepository.delete(word);
            }
        }
        return documentWord;
    }
}
