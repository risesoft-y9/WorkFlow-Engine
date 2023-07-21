package net.risesoft.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
 * @date 2022/12/22
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Service(value = "transactionWordService")
public class TransactionWordServiceImpl implements TransactionWordService {

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private TransactionWordRepository transactionWordRepository;

    @Autowired
    private Y9FileStoreService y9FileStoreService;

    @Override
    @Transactional(readOnly = false)
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
    public List<TransactionWord> findByProcessSerialNumber(String processSerialNumber) {
        List<TransactionWord> list = transactionWordRepository.findByProcessSerialNumber(processSerialNumber);
        return list;
    }

    @Override
    public List<TransactionWord> findByProcessSerialNumberAndIstaohong(String processSerialNumber, String taohong) {
        List<TransactionWord> list = transactionWordRepository.findByProcessSerialNumberAndIstaohong(processSerialNumber, taohong);
        return list;
    }

    @Override
    public TransactionWord getByProcessSerialNumber(String processSerialNumber) {
        TransactionWord fileDocument = new TransactionWord();
        if (StringUtils.isNotBlank(processSerialNumber)) {
            List<TransactionWord> list = transactionWordRepository.findByProcessSerialNumber(processSerialNumber);
            if (list.size() > 0 && !list.isEmpty()) {
                fileDocument = list.get(0);
            }
        }
        return fileDocument;
    }

    @Override
    @Transactional(readOnly = false)
    public void save(TransactionWord tw) {
        transactionWordRepository.save(tw);
    }

    @Transactional(readOnly = false)
    @Override
    public void saveTransactionWord(String fileStoreId, String fileSize, String documenttitle, String fileType, String processSerialNumber, String istaohong) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String userId = userInfo.getPersonId();
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
        transactionWordRepository.save(transactionWord);
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = false)
    @Override
    public Boolean saveWord(String docjson, String processSerialNumber) {
        Boolean checkSave = false;
        try {
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

    @Transactional(readOnly = false)
    @Override
    public void updateTransactionWordById(String fileStoreId, String fileType, String fileName, String fileSize, String isTaoHong, String userId, String id) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (StringUtils.isNotBlank(id)) {
            transactionWordRepository.updateTransactionWordById(fileStoreId, fileType, fileName, fileSize, sdf.format(new Date()), isTaoHong, userId, id);
        }
    }

}
