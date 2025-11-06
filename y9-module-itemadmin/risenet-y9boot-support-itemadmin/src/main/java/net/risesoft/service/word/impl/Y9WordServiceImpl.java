package net.risesoft.service.word.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.documentword.Y9Word;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.user.UserInfo;
import net.risesoft.repository.documentword.Y9WordRepository;
import net.risesoft.service.word.Y9WordService;
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
public class Y9WordServiceImpl implements Y9WordService {

    private final Y9WordRepository y9WordRepository;

    private final Y9FileStoreService y9FileStoreService;

    @Override
    @Transactional
    public void delBatchByProcessSerialNumbers(List<String> processSerialNumbers) {
        List<Y9Word> list = y9WordRepository.findByProcessSerialNumbers(processSerialNumbers);
        for (Y9Word file : list) {
            try {
                y9WordRepository.delete(file);
                y9FileStoreService.deleteFile(file.getFileStoreId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Y9Word getByProcessSerialNumber(String processSerialNumber) {
        Y9Word fileDocument = new Y9Word();
        if (StringUtils.isNotBlank(processSerialNumber)) {
            List<Y9Word> list = y9WordRepository.findByProcessSerialNumber(processSerialNumber);
            if (!list.isEmpty()) {
                fileDocument = list.get(0);
            }
        }
        return fileDocument;
    }

    @Override
    public List<Y9Word> listByProcessSerialNumber(String processSerialNumber) {
        return y9WordRepository.findByProcessSerialNumber(processSerialNumber);
    }

    @Override
    public List<Y9Word> listByProcessSerialNumberAndDocCategory(String processSerialNumber, String docCategory) {
        return y9WordRepository.findByProcessSerialNumberAndDocCategory(processSerialNumber, docCategory);
    }

    @Override
    public List<Y9Word> listByProcessSerialNumberAndIstaohong(String processSerialNumber, String taohong) {
        return y9WordRepository.findByProcessSerialNumberAndIstaohong(processSerialNumber, taohong);
    }

    @Override
    @Transactional
    public void save(Y9Word tw) {
        y9WordRepository.save(tw);
    }

    @Transactional
    @Override
    public void saveWord(String fileStoreId, String fileSize, String documenttitle, String fileType,
        String processSerialNumber, String istaohong, String docCategory) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId();
        String tenantId = Y9LoginUserHolder.getTenantId();
        Y9Word y9Word = new Y9Word();
        y9Word.setDeleted("0");
        y9Word.setFileType(fileType);
        y9Word.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        y9Word.setIstaohong(istaohong);
        y9Word.setTenantId(tenantId);
        y9Word.setTitle(documenttitle);
        y9Word.setFileName(StringUtils.isNotBlank(documenttitle) ? documenttitle + fileType : "正文" + fileType);
        y9Word.setFileStoreId(fileStoreId);
        y9Word.setFileSize(fileSize);
        y9Word.setUserId(userId);
        y9Word.setProcessSerialNumber(processSerialNumber);
        if (StringUtils.isNotBlank(docCategory)) {
            y9Word.setDocCategory(docCategory);
        }
        y9WordRepository.save(y9Word);
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @Override
    public Boolean saveWord(String docjson, String processSerialNumber) {
        boolean checkSave = false;
        try {
            Map<String, Object> documentMap = Y9JsonUtil.readValue(docjson, Map.class);
            List<Map<String, Object>> documentList = (List<Map<String, Object>>)documentMap.get("document");
            for (Map<String, Object> dMap : documentList) {
                Y9Word tw = new Y9Word();
                tw.setId(dMap.get("id").toString());
                tw.setFileName(dMap.get("fileName").toString());
                tw.setFileStoreId(dMap.get("filePath").toString());
                tw.setFileType(dMap.get("fileType").toString());
                tw.setProcessSerialNumber(processSerialNumber);
                tw.setTenantId(dMap.get("tenantId").toString());
                tw.setUserId(dMap.get("userId").toString());
                tw.setTitle(dMap.get("title") == null ? "" : dMap.get("title").toString());
                y9WordRepository.save(tw);
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
    public void updateById(String fileStoreId, String fileType, String fileName, String fileSize, String isTaoHong,
        String userId, String id) {
        if (StringUtils.isNotBlank(id)) {
            y9WordRepository.updateById(fileStoreId, fileType, fileName, fileSize, isTaoHong, userId, id);
        }
    }

}
