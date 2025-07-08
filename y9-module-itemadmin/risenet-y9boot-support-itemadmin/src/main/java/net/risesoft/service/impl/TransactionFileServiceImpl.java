package net.risesoft.service.impl;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.entity.attachment.Attachment;
import net.risesoft.exception.GlobalErrorCodeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.AttachmentModel;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.attachment.AttachmentRepository;
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

    private final AttachmentRepository attachmentRepository;

    private final Y9FileStoreService y9FileStoreService;

    private final OrgUnitApi orgUnitApi;

    private final Y9Properties y9Config;

    @Override
    @Transactional
    public void delBatchByProcessSerialNumbers(List<String> processSerialNumbers) {
        List<Attachment> list = attachmentRepository.findByProcessSerialNumbers(processSerialNumbers);
        for (Attachment file : list) {
            try {
                attachmentRepository.delete(file);
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
            Attachment file = attachmentRepository.findById(str).orElse(null);
            attachmentRepository.deleteById(str);
            assert file != null;
            y9FileStoreService.deleteFile(file.getFileStoreId());
        }
    }

    @Override
    public Integer fileCounts(String processSerialNumber) {
        return attachmentRepository.fileCounts(processSerialNumber);
    }

    @Override
    @Transactional
    public Attachment findById(String id) {
        Attachment attachment = attachmentRepository.findById(id).orElse(null);
        return attachment;
    }

    @Override
    public Attachment getFileInfoByFileName(String fileName, String processSerialNumber) {
        return attachmentRepository.getFileInfoByFileName(fileName, processSerialNumber);
    }

    @Override
    public Integer getTransactionFileCount(String processSerialNumber, String fileSource, String fileType) {
        if (StringUtils.isBlank(fileType)) {
            return attachmentRepository.getTransactionFileCount(processSerialNumber, fileSource);
        } else {
            return attachmentRepository.getTransactionFileCountByFileType(processSerialNumber, fileSource, fileType);
        }
    }

    @Override
    public Attachment getUpFileInfoByTabIndexOrProcessSerialNumber(Integer tabIndex, String processSerialNumber) {
        return attachmentRepository.getUpFileInfoByTabIndexOrProcessSerialNumber(tabIndex, processSerialNumber);
    }

    @Override
    public List<Attachment> listByProcessSerialNumber(String processSerialNumber) {
        return attachmentRepository.findByProcessSerialNumber(processSerialNumber);
    }

    @Override
    public List<Attachment> listByProcessSerialNumberAndFileSource(String processSerialNumber, String fileSource) {
        return attachmentRepository.findByProcessSerialNumberAndFileSource(processSerialNumber, fileSource);
    }

    @Override
    public List<Attachment> listSearchByProcessSerialNumber(String processSerialNumber, String fileSource) {
        List<Attachment> attachmentList = new ArrayList<>();
        if (StringUtils.isBlank(fileSource)) {
            attachmentList = attachmentRepository.getAttachmentList(processSerialNumber);
        } else {
            attachmentList = attachmentRepository.getAttachmentList(processSerialNumber, fileSource);
        }
        return attachmentList;
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
            Page<Attachment> transactionFileList = null;
            if (StringUtils.isBlank(fileSource)) {
                transactionFileList = attachmentRepository.getAttachmentList(processSerialNumber, pageable);
            } else {
                transactionFileList = attachmentRepository.getAttachmentList(processSerialNumber, fileSource, pageable);
            }
            int number = (page - 1) * rows;
            for (Attachment attachment : transactionFileList) {
                AttachmentModel model = new AttachmentModel();
                model.setSerialNumber(number + 1);
                model.setName(attachment.getName());
                model.setFileSize(attachment.getFileSize());
                model.setId(attachment.getId());
                model.setPersonId(attachment.getPersonId());
                model.setPersonName(attachment.getPersonName());
                model.setPositionId(attachment.getPositionId());
                OrgUnit user = orgUnitApi
                    .getOrgUnitPersonOrPosition(Y9LoginUserHolder.getTenantId(), attachment.getPositionId()).getData();
                model.setPositionName(user != null ? user.getName() : "");
                model.setDeptId(attachment.getDeptId());
                model.setDeptName(attachment.getDeptName());
                model.setDescribes(attachment.getDescribes());
                model.setUploadTime(sdfymdhm.format(sdfymdhms.parse(attachment.getUploadTime())));
                model.setFileType(attachment.getFileType());
                model.setFileSource(attachment.getFileSource());
                model.setFileStoreId(attachment.getFileStoreId());
                model.setFilePath(attachment.getFileStoreId());
                String downloadUrl = y9Config.getCommon().getItemAdminBaseUrl() + "/s/" + attachment.getFileStoreId()
                    + "." + attachment.getFileType();
                model.setDownloadUrl(downloadUrl);
                model.setProcessInstanceId(attachment.getProcessInstanceId());
                model.setProcessSerialNumber(attachment.getProcessSerialNumber());
                model.setTaskId(attachment.getTaskId());
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
    public void save(Attachment file) {
        attachmentRepository.save(file);
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
                Attachment file = new Attachment();
                file.setDescribes(map.get("describes") == null ? "" : map.get("describes").toString());
                file.setFileStoreId(map.get("filePath").toString());
                file.setFileSize(map.get("fileSize") == null ? "" : map.get("fileSize").toString());
                file.setFileSource(map.get("fileSource") == null ? "" : map.get("fileSource").toString());
                file.setFileType(map.get("fileType") == null ? "" : map.get("fileType").toString());
                file.setId(map.get("id").toString());
                file.setName(map.get("fileName").toString());
                file.setPersonId(map.get("personId") == null ? "" : map.get("personId").toString());
                file.setPersonName(map.get("personName") == null ? "" : map.get("personName").toString());
                OrgUnit department =
                    orgUnitApi.getOrgUnit(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getDeptId()).getData();
                file.setDeptId(Y9LoginUserHolder.getDeptId());
                file.setDeptName(department != null ? department.getName() : "");
                file.setProcessSerialNumber(processSerialNumber);
                file.setUploadTime(sdfymdhms.format(new Date()));
                attachmentRepository.save(file);
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
            attachmentRepository.update(processInstanceId, taskId, processSerialNumber);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Transactional
    @Override
    public Y9Result<Object> upload(MultipartFile multipartFile, String processInstanceId, String taskId,
        String processSerialNumber, String describes, String fileSource) {

        try {
            SimpleDateFormat sdfymdhms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat sdfymd = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdfhms = new SimpleDateFormat("HH-mm-ss");
            if (StringUtils.isNotEmpty(describes)) {
                describes = URLDecoder.decode(describes, StandardCharsets.UTF_8);
            }
            String originalFilename = multipartFile.getOriginalFilename();
            String fileName = FilenameUtils.getName(originalFilename);
            String fileType = FilenameUtils.getExtension(fileName);

            Date nowDate = new Date();
            String fullPath = Y9FileStore.buildPath(Y9Context.getSystemName(), "transaction", sdfymd.format(nowDate),
                sdfhms.format(nowDate), processSerialNumber);
            Y9FileStore y9FileStore = y9FileStoreService.uploadFile(multipartFile, fullPath, fileName);
            UserInfo person = Y9LoginUserHolder.getUserInfo();

            Attachment attachment = new Attachment();
            if (StringUtils.isNotBlank(processSerialNumber)) {
                attachment.setProcessSerialNumber(processSerialNumber);
            }
            attachment.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            attachment.setName(fileName);
            attachment.setFileSize(y9FileStore.getDisplayFileSize());
            attachment.setFileSource(fileSource);
            attachment.setProcessInstanceId(processInstanceId);
            attachment.setTaskId(taskId);
            attachment.setUploadTime(sdfymdhms.format(new Date()));
            attachment.setDescribes(describes);
            attachment.setPersonName(person.getName());
            attachment.setPersonId(person.getPersonId());
            attachment.setFileStoreId(y9FileStore.getId());
            attachment.setFileType(fileType);
            OrgUnit department =
                orgUnitApi.getOrgUnit(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getDeptId()).getData();
            attachment.setDeptId(Y9LoginUserHolder.getDeptId());
            attachment.setDeptName(department != null ? department.getName() : "");
            attachmentRepository.save(attachment);
            return Y9Result.success(y9FileStore.getId(), "上传附件成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Y9Result.failure("上传附件失败");
        }
    }

    @Transactional
    @Override
    public void uploadRest(String fileName, String fileSize, String processInstanceId, String taskId,
        String processSerialNumber, String describes, String fileSource, String y9FileStoreId) {
        String[] types = fileName.split("\\.");
        String type = types[types.length - 1].toLowerCase();
        SimpleDateFormat sdfymdhms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Attachment attachment = new Attachment();
        attachment.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        attachment.setName(fileName);
        attachment.setFileSize(fileSize);
        attachment.setFileSource(fileSource);
        attachment.setProcessInstanceId(processInstanceId);
        attachment.setProcessSerialNumber(processSerialNumber);
        attachment.setTaskId(taskId);
        attachment.setUploadTime(sdfymdhms.format(new Date()));
        attachment.setDescribes(describes);
        attachment.setPersonName(Y9LoginUserHolder.getUserInfo().getName());
        attachment.setPersonId(Y9LoginUserHolder.getPersonId());
        attachment.setPositionId(Y9LoginUserHolder.getOrgUnitId());
        OrgUnit department = orgUnitApi
            .getOrgUnit(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getOrgUnit().getParentId()).getData();
        attachment.setDeptId(department != null ? department.getId() : "");
        attachment.setDeptName(department != null ? department.getName() : "");
        attachment.setFileStoreId(y9FileStoreId);
        attachment.setFileType(type);
        attachmentRepository.save(attachment);
    }

    @Transactional
    @Override
    public Attachment uploadRestModel(Attachment attachment) {
        SimpleDateFormat sdfymdhms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        attachment.setUploadTime(sdfymdhms.format(new Date()));
        attachmentRepository.save(attachment);
        return attachment;
    }
}
