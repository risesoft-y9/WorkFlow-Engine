package net.risesoft.service.impl;

import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import net.risesoft.api.platform.org.DepartmentApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.TransactionFile;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.platform.Department;
import net.risesoft.model.user.UserInfo;
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
 * @date 2022/12/22
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Service(value = "transactionFileService")
public class TransactionFileServiceImpl implements TransactionFileService {

    private static SimpleDateFormat sdf_yMd = new SimpleDateFormat("yyyy-MM-dd");

    private static SimpleDateFormat sdf_Hms = new SimpleDateFormat("HH-mm-ss");

    @Autowired
    private TransactionFileRepository transactionFileRepository;

    @Autowired
    private Y9FileStoreService y9FileStoreService;

    @Autowired
    private DepartmentApi departmentManager;
    @Autowired
    private Y9Properties y9Config;

    @Override
    @Transactional(readOnly = false)
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

    @Transactional(readOnly = false)
    @Override
    public Map<String, Object> delFile(String ids) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put(UtilConsts.SUCCESS, true);
        try {
            String[] id = ids.split(",");
            for (String str : id) {
                try {
                    TransactionFile file = transactionFileRepository.findById(str).orElse(null);
                    transactionFileRepository.deleteById(str);
                    y9FileStoreService.deleteFile(file.getFileStoreId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
        }
        return map;
    }

    @Override
    @Transactional(readOnly = false)
    public Map<String, Object> download(String id) {
        Optional<TransactionFile> transactionFile = transactionFileRepository.findById(id);
        Map<String, Object> map = new HashMap<>(16);
        map.put("fileSize", transactionFile.get().getFileSize());
        map.put("filename", transactionFile.get().getName());
        map.put("fileStoreId", transactionFile.get().getFileStoreId());
        return map;
    }

    @Override
    public Integer fileCounts(String processSerialNumber) {
        return transactionFileRepository.fileCounts(processSerialNumber);
    }

    @Override
    @Transactional(readOnly = false)
    public Map<String, Object> getAttachmentList(String processSerialNumber, String fileSource, int page, int rows) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        List<Map<String, Object>> item = new ArrayList<Map<String, Object>>();
        try {
            SimpleDateFormat ymdhms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat ymdhm = new SimpleDateFormat("yyyy/MM/dd HH:mm");
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
                Map<String, Object> m = new HashMap<String, Object>(16);
                m.put("serialNumber", number + 1);
                m.put("name", transactionFile.getName());
                m.put("fileSize", transactionFile.getFileSize());
                m.put("id", transactionFile.getId());
                m.put("personId", transactionFile.getPersonId());
                m.put("personName", transactionFile.getPersonName());
                m.put("deptId", transactionFile.getDeptId());
                m.put("deptName", transactionFile.getDeptName());
                m.put("describes", transactionFile.getDescribes());
                m.put("uploadTime", ymdhm.format(ymdhms.parse(transactionFile.getUploadTime())));
                m.put("serialNumber", number + 1);
                m.put("fileType", transactionFile.getFileType());
                m.put("fileSource", transactionFile.getFileSource());
                m.put("filePath", transactionFile.getFileStoreId());
                Y9FileStore y9FileStore = y9FileStoreService.getById(transactionFile.getFileStoreId());
                m.put("downloadUrl", y9FileStore != null ? y9FileStore.getUrl() : "");
                m.put("processInstanceId", transactionFile.getProcessInstanceId());
                m.put("processSerialNumber", transactionFile.getProcessSerialNumber());
                m.put("taskId", transactionFile.getTaskId());
                m.put("jodconverterURL", y9Config.getCommon().getJodconverterBaseUrl());
                item.add(m);
                number += 1;
            }
            map.put("rows", item);
            map.put("totalpage", transactionFileList.getTotalPages());
            map.put("currpage", page);
            map.put("total", transactionFileList.getTotalElements());
            map.put(UtilConsts.SUCCESS, true);
            map.put("msg", "附件列表获取成功");
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "附件列表获取失败");
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public List<TransactionFile> getAttachmentModelList(String processSerialNumber, String fileSource) {
        List<TransactionFile> transactionFileList = new ArrayList<>();
        if (StringUtils.isBlank(fileSource)) {
            transactionFileList = transactionFileRepository.getAttachmentList(processSerialNumber);
        } else {
            transactionFileList = transactionFileRepository.getAttachmentList(processSerialNumber, fileSource);
        }
        return transactionFileList;
    }

    @Override
    public TransactionFile getFileInfoByFileName(String fileName, String processSerialNumber) {
        return transactionFileRepository.getFileInfoByFileName(fileName, processSerialNumber);
    }

    @Override
    public List<TransactionFile> getListByProcessSerialNumber(String processSerialNumber) {
        List<TransactionFile> list = transactionFileRepository.findByProcessSerialNumber(processSerialNumber);
        return list;
    }

    @Override
    public List<TransactionFile> getListByProcessSerialNumberAndFileSource(String processSerialNumber,
        String fileSource) {
        List<TransactionFile> list =
            transactionFileRepository.findByProcessSerialNumberAndFileSource(processSerialNumber, fileSource);
        return list;
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
    @Transactional(readOnly = false)
    public void save(TransactionFile file) {
        transactionFileRepository.save(file);
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = false)
    @Override
    public Boolean saveAttachment(String attachjson, String processSerialNumber) {
        Boolean checkSave = false;
        try {
            SimpleDateFormat ymdhms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
                Department department = departmentManager
                    .getDepartment(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getDeptId()).getData();
                file.setDeptId(Y9LoginUserHolder.getDeptId());
                file.setDeptName(department != null ? department.getName() : "");
                file.setProcessSerialNumber(processSerialNumber);
                file.setUploadTime(ymdhms.format(new Date()));
                transactionFileRepository.save(file);
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
    public void update(String processSerialNumber, String processInstanceId, String taskId) {
        try {
            transactionFileRepository.update(processInstanceId, taskId, processSerialNumber);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Transactional(readOnly = false)
    @Override
    public Map<String, Object> upload(MultipartFile multipartFile, String processInstanceId, String taskId,
        String processSerialNumber, String describes, String fileSource) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        TransactionFile transactionFile = new TransactionFile();
        try {
            SimpleDateFormat ymdhms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (StringUtils.isNotEmpty(describes)) {
                describes = URLDecoder.decode(describes, "UTF-8");
            }
            String originalFilename = multipartFile.getOriginalFilename();
            String fileName = FilenameUtils.getName(originalFilename);
            String fileType = FilenameUtils.getExtension(fileName);

            Date nowDate = new Date();
            String fullPath = Y9FileStore.buildFullPath(Y9Context.getSystemName(), "transaction",
                sdf_yMd.format(nowDate), sdf_Hms.format(nowDate), processSerialNumber);
            Y9FileStore y9FileStore = y9FileStoreService.uploadFile(multipartFile, fullPath, fileName);

            UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
            if (StringUtils.isNotBlank(processSerialNumber)) {
                transactionFile.setProcessSerialNumber(processSerialNumber);
            }
            transactionFile.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            transactionFile.setName(fileName);
            transactionFile.setFileSize(y9FileStore.getDisplayFileSize());
            transactionFile.setFileSource(fileSource);
            transactionFile.setProcessInstanceId(processInstanceId);
            transactionFile.setTaskId(taskId);
            transactionFile.setUploadTime(ymdhms.format(new Date()));
            transactionFile.setDescribes(describes);
            transactionFile.setPersonName(userInfo.getName());
            transactionFile.setPersonId(userInfo.getPersonId());
            transactionFile.setFileStoreId(y9FileStore.getId());
            transactionFile.setFileType(fileType);
            Department department = departmentManager
                .getDepartment(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getDeptId()).getData();
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

    @Transactional(readOnly = false)
    @Override
    public Map<String, Object> uploadRest(String fileName, String fileSize, String processInstanceId, String taskId,
        String processSerialNumber, String describes, String fileSource, String y9FileStoreId) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put(UtilConsts.SUCCESS, false);
        map.put("msg", "保存附件信息失败");
        String[] types = fileName.split("\\.");
        String type = types[types.length - 1].toLowerCase();
        try {
            SimpleDateFormat ymdhms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
            TransactionFile transactionFile = new TransactionFile();
            transactionFile.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            transactionFile.setName(fileName);
            transactionFile.setFileSize(fileSize);
            transactionFile.setFileSource(fileSource);
            transactionFile.setProcessInstanceId(processInstanceId);
            transactionFile.setProcessSerialNumber(processSerialNumber);
            transactionFile.setTaskId(taskId);
            transactionFile.setUploadTime(ymdhms.format(new Date()));
            transactionFile.setDescribes(describes);
            transactionFile.setPersonName(userInfo.getName());
            transactionFile.setPersonId(userInfo.getPersonId());
            Department department = departmentManager
                .getDepartment(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getDeptId()).getData();
            transactionFile.setDeptId(Y9LoginUserHolder.getDeptId());
            transactionFile.setDeptName(department != null ? department.getName() : "");
            transactionFile.setFileStoreId(y9FileStoreId);
            transactionFile.setFileType(type);
            transactionFileRepository.save(transactionFile);
            map.put(UtilConsts.SUCCESS, true);
            map.put("msg", "保存附件信息成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    @Transactional(readOnly = false)
    @Override
    public TransactionFile uploadRestModel(TransactionFile transactionFile) throws ParseException {
        SimpleDateFormat ymdhms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        transactionFile.setUploadTime(ymdhms.format(new Date()));
        transactionFileRepository.save(transactionFile);
        return transactionFile;
    }
}
