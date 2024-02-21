package net.risesoft.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.TransactionHistoryWord;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.user.UserInfo;
import net.risesoft.repository.jpa.TransactionHistoryWordRepository;
import net.risesoft.service.TransactionHistoryWordService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9public.service.Y9FileStoreService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Service(value = "transactionHistoryWordService")
public class TransactionHistoryWordServiceImpl implements TransactionHistoryWordService {

    private static SimpleDateFormat sdfymdhms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private TransactionHistoryWordRepository transactionHistoryWordRepository;

    @Autowired
    private Y9FileStoreService y9FileStoreService;

    @Override
    @Transactional(readOnly = false)
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

    @Transactional(readOnly = false)
    @Override
    public void deleteHistoryWordByIsTaoHong(String processSerialNumber, String isTaoHong) {
        List<TransactionHistoryWord> list = new ArrayList<TransactionHistoryWord>();

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
    public List<TransactionHistoryWord> findByProcessSerialNumber(String processSerialNumber) {
        List<TransactionHistoryWord> list =
            transactionHistoryWordRepository.findByProcessSerialNumber(processSerialNumber);
        return list;
    }

    @Override
    public TransactionHistoryWord getByProcessSerialNumber(String processSerialNumber) {
        TransactionHistoryWord fileDocument = new TransactionHistoryWord();
        if (StringUtils.isNotBlank(processSerialNumber)) {
            List<TransactionHistoryWord> list =
                transactionHistoryWordRepository.findByProcessSerialNumber(processSerialNumber);
            if (list.size() > 0 && !list.isEmpty()) {
                fileDocument = list.get(0);
            }
        }
        return fileDocument;
    }

    @Override
    public List<TransactionHistoryWord> getListByTaskId(String taskId) {
        return transactionHistoryWordRepository.findListByTaskId(taskId);
    }

    @Override
    public TransactionHistoryWord getTransactionHistoryWordByTaskId(String taskId) {
        List<TransactionHistoryWord> list = transactionHistoryWordRepository.getTransactionHistoryWordByTaskId(taskId);
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    @Transactional(readOnly = false)
    @Override
    public void saveTransactionHistoryWord(String fileStoreId, String fileSize, String documenttitle, String fileType,
        String processSerialNumber, String isTaoHong, String taskId) {
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
        transactionHistoryWordRepository.save(transactionHistoryWord);
    }

    @Transactional(readOnly = false)
    @Override
    public int update(String taskId, String processSerialNumber) {
        try {
            if (StringUtils.isNotBlank(processSerialNumber)) {
                List<TransactionHistoryWord> list =
                    transactionHistoryWordRepository.findByProcessSerialNumber(processSerialNumber);
                if (list.size() > 0 && !list.isEmpty()) {
                    if (StringUtils.isNotBlank(taskId)) {
                        transactionHistoryWordRepository.update(taskId, processSerialNumber);
                        return 1;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return -1;
    }

    @Transactional(readOnly = false)
    @Override
    public void updateTransactionHistoryWordById(String fileStoreId, String fileType, String fileName, String fileSize,
        String isTaoHong, String userId, String id) {
        if (StringUtils.isNotBlank(id)) {
            transactionHistoryWordRepository.updateTransactionHistoryWordById(fileStoreId, fileSize, isTaoHong,
                sdfymdhms.format(new Date()), userId, id);
        }
    }

}
