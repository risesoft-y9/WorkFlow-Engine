package net.risesoft.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.documentword.TransactionHistoryWord;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.user.UserInfo;
import net.risesoft.repository.documentword.TransactionHistoryWordRepository;
import net.risesoft.service.TransactionHistoryWordService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9public.service.Y9FileStoreService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class TransactionHistoryWordServiceImpl implements TransactionHistoryWordService {

    private final TransactionHistoryWordRepository transactionHistoryWordRepository;

    private final Y9FileStoreService y9FileStoreService;

    @Override
    @Transactional
    public void delBatchByProcessSerialNumbers(List<String> processSerialNumbers) {
        List<TransactionHistoryWord> list =
            transactionHistoryWordRepository.findByProcessSerialNumbers(processSerialNumbers);
        for (TransactionHistoryWord file : list) {
            try {
                transactionHistoryWordRepository.delete(file);
                y9FileStoreService.deleteFile(file.getFileStoreId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Transactional
    @Override
    public void deleteHistoryWordByIsTaoHong(String processSerialNumber, String isTaoHong) {
        List<TransactionHistoryWord> list = new ArrayList<>();
        if (StringUtils.isNotBlank(processSerialNumber) && StringUtils.isNotBlank(isTaoHong)) {
            list =
                transactionHistoryWordRepository.findByProcessSerialNumberAndIsTaoHong(processSerialNumber, isTaoHong);
        }
        for (TransactionHistoryWord historyWord : list) {
            transactionHistoryWordRepository.delete(historyWord);
            try {
                y9FileStoreService.deleteFile(historyWord.getFileStoreId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public TransactionHistoryWord getByProcessSerialNumber(String processSerialNumber) {
        TransactionHistoryWord fileDocument = new TransactionHistoryWord();
        if (StringUtils.isNotBlank(processSerialNumber)) {
            List<TransactionHistoryWord> list =
                transactionHistoryWordRepository.findByProcessSerialNumber(processSerialNumber);
            if (!list.isEmpty()) {
                fileDocument = list.get(0);
            }
        }
        return fileDocument;
    }

    @Override
    public TransactionHistoryWord getTransactionHistoryWordByTaskId(String taskId) {
        List<TransactionHistoryWord> list = transactionHistoryWordRepository.getTransactionHistoryWordByTaskId(taskId);
        if (!list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public List<TransactionHistoryWord> listByProcessSerialNumber(String processSerialNumber) {
        return transactionHistoryWordRepository.findByProcessSerialNumber(processSerialNumber);
    }

    @Override
    public List<TransactionHistoryWord> listByTaskId(String taskId) {
        return transactionHistoryWordRepository.findListByTaskId(taskId);
    }

    @Transactional
    @Override
    public void saveTransactionHistoryWord(String fileStoreId, String fileSize, String documenttitle, String fileType,
        String processSerialNumber, String isTaoHong, String taskId, String docCategory) {
        SimpleDateFormat sdfymdhms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId();
        String tenantId = Y9LoginUserHolder.getTenantId();
        TransactionHistoryWord transactionHistoryWord = new TransactionHistoryWord();
        transactionHistoryWord.setDeleted("0");
        transactionHistoryWord.setFileType(fileType);
        transactionHistoryWord.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        transactionHistoryWord.setIstaohong(isTaoHong);
        transactionHistoryWord.setTaskId(taskId);
        Integer version = transactionHistoryWordRepository.getMaxHistoryVersion(processSerialNumber);
        transactionHistoryWord.setVersion(version != null ? version + 1 : 1);
        transactionHistoryWord.setSaveDate(sdfymdhms.format(new Date()));
        transactionHistoryWord.setTenantId(tenantId);
        transactionHistoryWord.setTitle(documenttitle);
        transactionHistoryWord
            .setFileName(StringUtils.isNotBlank(documenttitle) ? documenttitle + fileType : "正文" + fileType);
        transactionHistoryWord.setFileStoreId(fileStoreId);
        transactionHistoryWord.setFileSize(fileSize);
        transactionHistoryWord.setUserId(userId);
        transactionHistoryWord.setProcessSerialNumber(processSerialNumber);
        if (StringUtils.isNotBlank(docCategory)) {
            transactionHistoryWord.setDocCategory(docCategory);
        }
        transactionHistoryWordRepository.save(transactionHistoryWord);
    }

    @Transactional
    @Override
    public int update(String taskId, String processSerialNumber) {
        try {
            if (StringUtils.isNotBlank(processSerialNumber)) {
                List<TransactionHistoryWord> list =
                    transactionHistoryWordRepository.findByProcessSerialNumber(processSerialNumber);
                if (!list.isEmpty() && StringUtils.isNotBlank(taskId)) {
                    transactionHistoryWordRepository.update(taskId, processSerialNumber);
                    return 1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return -1;
    }

    @Transactional
    @Override
    public void updateTransactionHistoryWordById(String fileStoreId, String fileType, String fileName, String fileSize,
        String isTaoHong, String docCategory, String userId, String id) {
        SimpleDateFormat sdfymdhms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (StringUtils.isNotBlank(id)) {
            transactionHistoryWordRepository.updateTransactionHistoryWordById(fileStoreId, fileSize, isTaoHong,
                docCategory, sdfymdhms.format(new Date()), userId, id);
        }
    }

}
