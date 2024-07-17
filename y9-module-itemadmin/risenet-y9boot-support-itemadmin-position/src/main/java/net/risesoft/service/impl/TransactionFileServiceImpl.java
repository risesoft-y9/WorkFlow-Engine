package net.risesoft.service.impl;

import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.org.DepartmentApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.TransactionFile;
import net.risesoft.exception.GlobalErrorCodeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.AttachmentModel;
import net.risesoft.model.platform.Department;
import net.risesoft.model.platform.Position;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Page;
import net.risesoft.repository.jpa.TransactionFileRepository;
import net.risesoft.service.TransactionFileService;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9public.entity.Y9FileStore;
import net.risesoft.y9public.service.Y9FileStoreService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class TransactionFileServiceImpl implements TransactionFileService {

    private final TransactionFileRepository transactionFileRepository;

    private final Y9FileStoreService y9FileStoreService;

    private final DepartmentApi departmentManager;

    private final PositionApi positionManager;

    private final Y9Properties y9Config;

    @Override
    @Transactional
    public void delBatchByProcessSerialNumbers(List<String> processSerialNumbers) {
        List<TransactionFile> list = transactionFileRepository.findByProcessSerialNumbers(processSerialNumbers);
        for (TransactionFile file : list) {
            try {
                transactionFileRepository.delete(file);
                y9FileStoreService.deleteFile(file.getFileStoreId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Transactional
    @Override
    public void delFile(String ids) {
        String[] id = ids.split(",");
        for (String str : id) {
            TransactionFile file = transactionFileRepository.findById(str).orElse(null);
            transactionFileRepository.deleteById(str);
            assert file != null;
            y9FileStoreService.deleteFile(file.getFileStoreId());
        }
    }

    @Override
    public Integer fileCounts(String processSerialNumber) {
        return transactionFileRepository.fileCounts(processSerialNumber);
    }

    @Override
    @Transactional
    public TransactionFile findById(String id) {
        TransactionFile transactionFile = transactionFileRepository.findById(id).orElse(null);
        return transactionFile;
    }

    @Override
    public TransactionFile getFileInfoByFileName(String fileName, String processSerialNumber) {
        return transactionFileRepository.getFileInfoByFileName(fileName, processSerialNumber);
    }

    @Override
    public Integer getTransactionFileCount(String processSerialNumber, String fileSource, String fileType) {
        if (StringUtils.isBlank(fileType)) {
            return transactionFileRepository.getTransactionFileCount(processSerialNumber, fileSource);
        } else {
            return transactionFileRepository.getTransactionFileCountByFileType(processSerialNumber, fileSource,
                fileType);
        }
    }

    @Override
    public TransactionFile getUpFileInfoByTabIndexOrProcessSerialNumber(Integer tabIndex, String processSerialNumber) {
        return transactionFileRepository.getUpFileInfoByTabIndexOrProcessSerialNumber(tabIndex, processSerialNumber);
    }

    @Override
    public List<TransactionFile> listByProcessSerialNumber(String processSerialNumber) {
        return transactionFileRepository.findByProcessSerialNumber(processSerialNumber);
    }

    @Override
    public List<TransactionFile> listByProcessSerialNumberAndFileSource(String processSerialNumber, String fileSource) {
        return transactionFileRepository.findByProcessSerialNumberAndFileSource(processSerialNumber, fileSource);
    }

    @Override
    public List<TransactionFile> listSearchByProcessSerialNumber(String processSerialNumber, String fileSource) {
        List<TransactionFile> transactionFileList = new ArrayList<>();
        if (StringUtils.isBlank(fileSource)) {
            transactionFileList = transactionFileRepository.getAttachmentList(processSerialNumber);
        } else {
            transactionFileList = transactionFileRepository.getAttachmentList(processSerialNumber, fileSource);
        }
        return transactionFileList;
    }

    @Override
    @Transactional
    public Y9Page<AttachmentModel> pageByProcessSerialNumber(String processSerialNumber, String fileSource, int page,
        int rows) {
        List<AttachmentModel> item = new ArrayList<>();
        try {
            SimpleDateFormat sdfymdhms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat sdfymdhm = new SimpleDateFormat("yyyy/MM/dd HH:mm");
            PageRequest pageable =
                PageRequest.of(page < 1 ? 0 : page - 1, rows, Sort.by(Sort.Direction.DESC, "uploadTime"));
            Page<TransactionFile> transactionFileList = null;
            if (StringUtils.isBlank(fileSource)) {
                transactionFileList = transactionFileRepository.getAttachmentList(processSerialNumber, pageable);
            } else {
                transactionFileList =
                    transactionFileRepository.getAttachmentList(processSerialNumber, fileSource, pageable);
            }
            int number = (page - 1) * rows;
            for (TransactionFile transactionFile : transactionFileList) {
                AttachmentModel model = new AttachmentModel();
                model.setSerialNumber(number + 1);
                model.setName(transactionFile.getName());
                model.setFileSize(transactionFile.getFileSize());
                model.setId(transactionFile.getId());
                model.setPersonId(transactionFile.getPersonId());
                model.setPersonName(transactionFile.getPersonName());
                model.setPositionId(transactionFile.getPositionId());
                Position position =
                    positionManager.get(Y9LoginUserHolder.getTenantId(), transactionFile.getPositionId()).getData();
                model.setPositionName(position != null ? position.getName() : "");
                model.setDeptId(transactionFile.getDeptId());
                model.setDeptName(transactionFile.getDeptName());
                model.setDescribes(transactionFile.getDescribes());
                model.setUploadTime(sdfymdhm.format(sdfymdhms.parse(transactionFile.getUploadTime())));
                model.setFileType(transactionFile.getFileType());
                model.setFileSource(transactionFile.getFileSource());
                model.setFileStoreId(transactionFile.getFileStoreId());
                model.setFilePath(transactionFile.getFileStoreId());
                String downloadUrl = y9Config.getCommon().getItemAdminBaseUrl() + "/s/"
                    + transactionFile.getFileStoreId() + "." + transactionFile.getFileType();
                model.setDownloadUrl(downloadUrl);
                model.setProcessInstanceId(transactionFile.getProcessInstanceId());
                model.setProcessSerialNumber(transactionFile.getProcessSerialNumber());
                model.setTaskId(transactionFile.getTaskId());
                model.setJodconverterURL(y9Config.getCommon().getJodconverterBaseUrl());
                item.add(model);
                number += 1;
            }
            return Y9Page.success(page, transactionFileList.getTotalPages(), transactionFileList.getTotalElements(),
                item);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Page.failure(page, 0, 0, new ArrayList<>(), "获取失败", GlobalErrorCodeEnum.FAILURE.getCode());
    }

    @Override
    @Transactional
    public void save(TransactionFile file) {
        transactionFileRepository.save(file);
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @Override
    public Boolean saveAttachment(String attachjson, String processSerialNumber) {
        boolean checkSave = false;
        try {
            SimpleDateFormat sdfymdhms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Map<String, Object> attachmentjson = Y9JsonUtil.readValue(attachjson, Map.class);
            List<Map<String, Object>> attachmentList = (List<Map<String, Object>>)attachmentjson.get("attachment");
            for (Map<String, Object> map : attachmentList) {
                TransactionFile file = new TransactionFile();
                file.setDescribes(map.get("describes") == null ? "" : map.get("describes").toString());
                file.setFileStoreId(map.get("filePath").toString());
                file.setFileSize(map.get("fileSize") == null ? "" : map.get("fileSize").toString());
                file.setFileSource(map.get("fileSource") == null ? "" : map.get("fileSource").toString());
                file.setFileType(map.get("fileType") == null ? "" : map.get("fileType").toString());
                file.setId(map.get("id").toString());
                file.setName(map.get("fileName").toString());
                file.setPersonId(map.get("personId") == null ? "" : map.get("personId").toString());
                file.setPersonName(map.get("personName") == null ? "" : map.get("personName").toString());
                Department department =
                    departmentManager.get(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getDeptId()).getData();
                file.setDeptId(Y9LoginUserHolder.getDeptId());
                file.setDeptName(department != null ? department.getName() : "");
                file.setProcessSerialNumber(processSerialNumber);
                file.setUploadTime(sdfymdhms.format(new Date()));
                transactionFileRepository.save(file);
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
    public void update(String processSerialNumber, String processInstanceId, String taskId) {
        try {
            transactionFileRepository.update(processInstanceId, taskId, processSerialNumber);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Transactional
    @Override
    public Map<String, Object> upload(MultipartFile multipartFile, String processInstanceId, String taskId,
        String processSerialNumber, String describes, String fileSource) {
        Map<String, Object> map = new HashMap<>(16);
        TransactionFile transactionFile = new TransactionFile();
        try {
            SimpleDateFormat sdfymdhms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat sdfymd = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdfhms = new SimpleDateFormat("HH-mm-ss");
            if (StringUtils.isNotEmpty(describes)) {
                describes = URLDecoder.decode(describes, "UTF-8");
            }
            String originalFilename = multipartFile.getOriginalFilename();
            String fileName = FilenameUtils.getName(originalFilename);
            String fileType = FilenameUtils.getExtension(fileName);

            Date nowDate = new Date();
            String fullPath = Y9FileStore.buildPath(Y9Context.getSystemName(), "transaction", sdfymd.format(nowDate),
                sdfhms.format(nowDate), processSerialNumber);
            Y9FileStore y9FileStore = y9FileStoreService.uploadFile(multipartFile, fullPath, fileName);

            UserInfo person = Y9LoginUserHolder.getUserInfo();

            if (StringUtils.isNotBlank(processSerialNumber)) {
                transactionFile.setProcessSerialNumber(processSerialNumber);
            }
            transactionFile.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            transactionFile.setName(fileName);
            transactionFile.setFileSize(y9FileStore.getDisplayFileSize());
            transactionFile.setFileSource(fileSource);
            transactionFile.setProcessInstanceId(processInstanceId);
            transactionFile.setTaskId(taskId);
            transactionFile.setUploadTime(sdfymdhms.format(new Date()));
            transactionFile.setDescribes(describes);
            transactionFile.setPersonName(person.getName());
            transactionFile.setPersonId(person.getPersonId());
            transactionFile.setFileStoreId(y9FileStore.getId());
            transactionFile.setFileType(fileType);
            Department department =
                departmentManager.get(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getDeptId()).getData();
            transactionFile.setDeptId(Y9LoginUserHolder.getDeptId());
            transactionFile.setDeptName(department != null ? department.getName() : "");
            transactionFileRepository.save(transactionFile);
            map.put(UtilConsts.SUCCESS, true);
            map.put("msg", "上传附件成功");
        } catch (Exception e) {
            e.printStackTrace();
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "上传附件失败");
        }
        return map;
    }

    @Transactional
    @Override
    public void uploadRest(String fileName, String fileSize, String processInstanceId, String taskId,
        String processSerialNumber, String describes, String fileSource, String y9FileStoreId) {
        String[] types = fileName.split("\\.");
        String type = types[types.length - 1].toLowerCase();
        SimpleDateFormat sdfymdhms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        TransactionFile transactionFile = new TransactionFile();
        transactionFile.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        transactionFile.setName(fileName);
        transactionFile.setFileSize(fileSize);
        transactionFile.setFileSource(fileSource);
        transactionFile.setProcessInstanceId(processInstanceId);
        transactionFile.setProcessSerialNumber(processSerialNumber);
        transactionFile.setTaskId(taskId);
        transactionFile.setUploadTime(sdfymdhms.format(new Date()));
        transactionFile.setDescribes(describes);
        transactionFile.setPersonName(Y9LoginUserHolder.getUserInfo().getName());
        transactionFile.setPersonId(Y9LoginUserHolder.getPersonId());
        transactionFile.setPositionId(Y9LoginUserHolder.getPositionId());
        Department department = departmentManager
            .get(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPosition().getParentId()).getData();
        transactionFile.setDeptId(department != null ? department.getId() : "");
        transactionFile.setDeptName(department != null ? department.getName() : "");
        transactionFile.setFileStoreId(y9FileStoreId);
        transactionFile.setFileType(type);
        transactionFileRepository.save(transactionFile);
    }

    @Transactional
    @Override
    public TransactionFile uploadRestModel(TransactionFile transactionFile) {
        SimpleDateFormat sdfymdhms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        transactionFile.setUploadTime(sdfymdhms.format(new Date()));
        transactionFileRepository.save(transactionFile);
        return transactionFile;
    }
}
