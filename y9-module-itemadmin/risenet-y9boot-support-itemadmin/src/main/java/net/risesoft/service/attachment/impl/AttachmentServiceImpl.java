package net.risesoft.service.attachment.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.entity.attachment.Attachment;
import net.risesoft.exception.GlobalErrorCodeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.AttachmentModel;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.pojo.Y9Page;
import net.risesoft.repository.attachment.AttachmentRepository;
import net.risesoft.service.attachment.AttachmentService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;
import net.risesoft.y9public.service.Y9FileStoreService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class AttachmentServiceImpl implements AttachmentService {

    private final AttachmentRepository attachmentRepository;

    private final Y9FileStoreService y9FileStoreService;

    private final OrgUnitApi orgUnitApi;

    @Override
    @Transactional
    public void delBatchByProcessSerialNumbers(List<String> processSerialNumbers) {
        List<Attachment> list = attachmentRepository.findByProcessSerialNumbers(processSerialNumbers);
        for (Attachment file : list) {
            try {
                attachmentRepository.delete(file);
                y9FileStoreService.deleteFile(file.getFileStoreId());
            } catch (Exception e) {
                LOGGER.error("删除文件失败", e);
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
        return attachmentRepository.findById(id).orElse(null);
    }

    @Override
    public Attachment getFileInfoByFileName(String fileName, String processSerialNumber) {
        return attachmentRepository.getFileInfoByFileName(fileName, processSerialNumber);
    }

    @Override
    public Integer getAttachmentCount(String processSerialNumber, String fileSource, String fileType) {
        if (StringUtils.isBlank(fileType)) {
            return attachmentRepository.getAttachmentCount(processSerialNumber, fileSource);
        } else {
            return attachmentRepository.getAttachmentCountByFileType(processSerialNumber, fileSource, fileType);
        }
    }

    @Override
    public List<Attachment> listByProcessSerialNumber(String processSerialNumber) {
        return attachmentRepository.findByProcessSerialNumber(processSerialNumber);
    }

    @Override
    @Transactional
    public Y9Page<AttachmentModel> pageByProcessSerialNumber(String processSerialNumber, String fileSource, int page,
        int rows) {
        List<AttachmentModel> item = new ArrayList<>();
        try {
            PageRequest pageable =
                PageRequest.of(page < 1 ? 0 : page - 1, rows, Sort.by(Sort.Direction.ASC, "tabIndex"));
            Page<Attachment> attachmentList;
            if (StringUtils.isBlank(fileSource)) {
                attachmentList = attachmentRepository.getAttachmentList(processSerialNumber, pageable);
            } else {
                attachmentList = attachmentRepository.getAttachmentList(processSerialNumber, fileSource, pageable);
            }
            attachmentList.forEach(attachment -> {
                AttachmentModel model = new AttachmentModel();
                Y9BeanUtil.copyProperties(attachment, model);
                item.add(model);
            });
            return Y9Page.success(page, attachmentList.getTotalPages(), attachmentList.getTotalElements(), item);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return Y9Page.failure(page, 0, 0, new ArrayList<>(), "获取失败", GlobalErrorCodeEnum.FAILURE.getCode());
    }

    @Override
    @Transactional
    public void save(Attachment file) {
        attachmentRepository.save(file);
    }

    @Transactional
    @Override
    public void update(String processSerialNumber, String processInstanceId, String taskId) {
        try {
            attachmentRepository.update(processInstanceId, taskId, processSerialNumber);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Transactional
    @Override
    public void uploadRest(String fileName, String fileSize, String processInstanceId, String taskId,
        String processSerialNumber, String describes, String fileSource, String y9FileStoreId) {
        String[] types = fileName.split("\\.");
        String type = types[types.length - 1].toLowerCase();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Attachment attachment = new Attachment();
        attachment.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        attachment.setName(fileName);
        attachment.setFileSize(fileSize);
        attachment.setFileSource(fileSource);
        attachment.setProcessInstanceId(processInstanceId);
        attachment.setProcessSerialNumber(processSerialNumber);
        attachment.setTaskId(taskId);
        attachment.setUploadTime(sdf.format(new Date()));
        attachment.setDescribes(describes);
        attachment.setPersonName(Y9LoginUserHolder.getUserInfo().getName());
        attachment.setPersonId(Y9LoginUserHolder.getPersonId());
        OrgUnit department =
            orgUnitApi.getOrgUnit(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getUserInfo().getParentId())
                .getData();
        attachment.setDeptId(department != null ? department.getId() : "");
        attachment.setDeptName(department != null ? department.getName() : "");
        attachment.setFileStoreId(y9FileStoreId);
        attachment.setFileType(type);
        Integer index = attachmentRepository.getMaxTabIndex(processSerialNumber);
        attachment.setTabIndex(index == null ? 1 : index + 1);
        attachmentRepository.save(attachment);
    }

    @Transactional
    @Override
    public void uploadRestModel(Attachment attachment) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        attachment.setUploadTime(sdf.format(new Date()));
        Integer index = attachmentRepository.getMaxTabIndex(attachment.getProcessSerialNumber());
        attachment.setTabIndex(index == null ? 1 : index + 1);
        attachmentRepository.save(attachment);
    }
}
