package net.risesoft.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.TransactionWord;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.user.UserInfo;
import net.risesoft.repository.jpa.TransactionWordRepository;
import net.risesoft.service.TransactionWordService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9public.service.Y9FileStoreService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class TransactionWordServiceImpl implements TransactionWordService {

    private final TransactionWordRepository transactionWordRepository;

    private final Y9FileStoreService y9FileStoreService;

    @Override
    @Transactional
    public void delBatchByProcessSerialNumbers(List<String> processSerialNumbers) {
        List<TransactionWord> list = transactionWordRepository.findByProcessSerialNumbers(processSerialNumbers);
        for (TransactionWord file : list) {
            try {
                transactionWordRepository.delete(file);
                y9FileStoreService.deleteFile(file.getFileStoreId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public TransactionWord getByProcessSerialNumber(String processSerialNumber) {
        TransactionWord fileDocument = new TransactionWord();
        if (StringUtils.isNotBlank(processSerialNumber)) {
            List<TransactionWord> list = transactionWordRepository.findByProcessSerialNumber(processSerialNumber);
            if (!list.isEmpty()) {
                fileDocument = list.get(0);
            }
        }
        return fileDocument;
    }

    @Override
    public List<TransactionWord> listByProcessSerialNumber(String processSerialNumber) {
        return transactionWordRepository.findByProcessSerialNumber(processSerialNumber);
    }

    @Override
    public List<TransactionWord> listByProcessSerialNumberAndDocCategory(String processSerialNumber,
        String docCategory) {
        return transactionWordRepository.findByProcessSerialNumberAndDocCategory(processSerialNumber, docCategory);
    }

    @Override
    public List<TransactionWord> listByProcessSerialNumberAndIstaohong(String processSerialNumber, String taohong) {
        List<TransactionWord> list =
            transactionWordRepository.findByProcessSerialNumberAndIstaohong(processSerialNumber, taohong);
        return list;
    }

    @Override
    @Transactional
    public void save(TransactionWord tw) {
        transactionWordRepository.save(tw);
    }

    @Transactional
    @Override
    public void saveTransactionWord(String fileStoreId, String fileSize, String documenttitle, String fileType,
        String processSerialNumber, String istaohong, String docCategory) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId();
        String tenantId = Y9LoginUserHolder.getTenantId();
        TransactionWord transactionWord = new TransactionWord();
        transactionWord.setDeleted("0");
        transactionWord.setFileType(fileType);
        transactionWord.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        transactionWord.setIstaohong(istaohong);
        transactionWord.setSaveDate(sdf.format(new Date()));
        transactionWord.setTenantId(tenantId);
        transactionWord.setTitle(documenttitle);
        transactionWord.setFileName(StringUtils.isNotBlank(documenttitle) ? documenttitle + fileType : "正文" + fileType);
        transactionWord.setFileStoreId(fileStoreId);
        transactionWord.setFileSize(fileSize);
        transactionWord.setUserId(userId);
        transactionWord.setProcessSerialNumber(processSerialNumber);
        if (StringUtils.isNotBlank(docCategory)) {
            transactionWord.setDocCategory(docCategory);
        }
        transactionWordRepository.save(transactionWord);
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @Override
    public Boolean saveWord(String docjson, String processSerialNumber) {
        boolean checkSave = false;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Map<String, Object> documentMap = Y9JsonUtil.readValue(docjson, Map.class);
            List<Map<String, Object>> documentList = (List<Map<String, Object>>)documentMap.get("document");
            for (Map<String, Object> dMap : documentList) {
                TransactionWord tw = new TransactionWord();
                tw.setId(dMap.get("id").toString());
                tw.setFileName(dMap.get("fileName").toString());
                tw.setFileStoreId(dMap.get("filePath").toString());
                tw.setFileType(dMap.get("fileType").toString());
                tw.setProcessSerialNumber(processSerialNumber);
                tw.setSaveDate(sdf.format(new Date()));
                tw.setTenantId(dMap.get("tenantId").toString());
                tw.setUserId(dMap.get("userId").toString());
                tw.setTitle(dMap.get("title") == null ? "" : dMap.get("title").toString());
                transactionWordRepository.save(tw);
                checkSave = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            checkSave = false;
        }
        return checkSave;
    }

    @Transactional
    @Override
    public void updateTransactionWordById(String fileStoreId, String fileType, String fileName, String fileSize,
        String isTaoHong, String userId, String id) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (StringUtils.isNotBlank(id)) {
            transactionWordRepository.updateTransactionWordById(fileStoreId, fileType, fileName, fileSize,
                sdf.format(new Date()), isTaoHong, userId, id);
        }
    }

}
