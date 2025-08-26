package net.risesoft.service.word.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.documentword.Y9WordHistory;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.user.UserInfo;
import net.risesoft.repository.documentword.Y9WordHistoryRepository;
import net.risesoft.service.word.Y9WordHistoryService;
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
public class Y9WordHistoryServiceImpl implements Y9WordHistoryService {

    private final Y9WordHistoryRepository y9WordHistoryRepository;

    private final Y9FileStoreService y9FileStoreService;

    @Override
    @Transactional
    public void delBatchByProcessSerialNumbers(List<String> processSerialNumbers) {
        List<Y9WordHistory> list = y9WordHistoryRepository.findByProcessSerialNumbers(processSerialNumbers);
        for (Y9WordHistory file : list) {
            try {
                y9WordHistoryRepository.delete(file);
                y9FileStoreService.deleteFile(file.getFileStoreId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Transactional
    @Override
    public void deleteHistoryWordByIsTaoHong(String processSerialNumber, String isTaoHong) {
        List<Y9WordHistory> list = new ArrayList<>();
        if (StringUtils.isNotBlank(processSerialNumber) && StringUtils.isNotBlank(isTaoHong)) {
            list = y9WordHistoryRepository.findByProcessSerialNumberAndIsTaoHong(processSerialNumber, isTaoHong);
        }
        for (Y9WordHistory historyWord : list) {
            y9WordHistoryRepository.delete(historyWord);
            try {
                y9FileStoreService.deleteFile(historyWord.getFileStoreId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Y9WordHistory getByProcessSerialNumber(String processSerialNumber) {
        Y9WordHistory fileDocument = new Y9WordHistory();
        if (StringUtils.isNotBlank(processSerialNumber)) {
            List<Y9WordHistory> list = y9WordHistoryRepository.findByProcessSerialNumber(processSerialNumber);
            if (!list.isEmpty()) {
                fileDocument = list.get(0);
            }
        }
        return fileDocument;
    }

    @Override
    public Y9WordHistory findByTaskId(String taskId) {
        List<Y9WordHistory> list = y9WordHistoryRepository.findByTaskId(taskId);
        if (!list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public List<Y9WordHistory> listByProcessSerialNumber(String processSerialNumber) {
        return y9WordHistoryRepository.findByProcessSerialNumber(processSerialNumber);
    }

    @Override
    public List<Y9WordHistory> listByTaskId(String taskId) {
        return y9WordHistoryRepository.findListByTaskId(taskId);
    }

    @Transactional
    @Override
    public void save(String fileStoreId, String fileSize, String documentTitle, String fileType,
        String processSerialNumber, String isTaoHong, String taskId, String docCategory) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId();
        String tenantId = Y9LoginUserHolder.getTenantId();
        Y9WordHistory y9WordHistory = new Y9WordHistory();
        y9WordHistory.setDeleted("0");
        y9WordHistory.setFileType(fileType);
        y9WordHistory.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        y9WordHistory.setIstaohong(isTaoHong);
        y9WordHistory.setTaskId(taskId);
        Integer version = y9WordHistoryRepository.getMaxHistoryVersion(processSerialNumber);
        y9WordHistory.setVersion(version != null ? version + 1 : 1);
        y9WordHistory.setSaveDate(sdf.format(new Date()));
        y9WordHistory.setTenantId(tenantId);
        y9WordHistory.setTitle(documentTitle);
        y9WordHistory.setFileName(StringUtils.isNotBlank(documentTitle) ? documentTitle + fileType : "正文" + fileType);
        y9WordHistory.setFileStoreId(fileStoreId);
        y9WordHistory.setFileSize(fileSize);
        y9WordHistory.setUserId(userId);
        y9WordHistory.setProcessSerialNumber(processSerialNumber);
        if (StringUtils.isNotBlank(docCategory)) {
            y9WordHistory.setDocCategory(docCategory);
        }
        y9WordHistoryRepository.save(y9WordHistory);
    }

    @Transactional
    @Override
    public int update(String taskId, String processSerialNumber) {
        try {
            if (StringUtils.isNotBlank(processSerialNumber)) {
                List<Y9WordHistory> list = y9WordHistoryRepository.findByProcessSerialNumber(processSerialNumber);
                if (!list.isEmpty() && StringUtils.isNotBlank(taskId)) {
                    y9WordHistoryRepository.update(taskId, processSerialNumber);
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
    public void updateById(String fileStoreId, String fileType, String fileName, String fileSize, String isTaoHong,
        String docCategory, String userId, String id) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (StringUtils.isNotBlank(id)) {
            y9WordHistoryRepository.updateById(fileStoreId, fileSize, isTaoHong, docCategory, sdf.format(new Date()),
                userId, id);
        }
    }

}
