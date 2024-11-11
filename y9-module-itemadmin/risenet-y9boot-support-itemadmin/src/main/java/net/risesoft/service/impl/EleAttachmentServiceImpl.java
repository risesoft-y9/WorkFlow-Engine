package net.risesoft.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.entity.EleAttachment;
import net.risesoft.entity.TransactionFile;
import net.risesoft.exception.GlobalErrorCodeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.AttachmentModel;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.jpa.EleAttachmentRepository;
import net.risesoft.repository.jpa.TransactionFileRepository;
import net.risesoft.service.EleAttachmentService;
import net.risesoft.service.TransactionFileService;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9public.entity.Y9FileStore;
import net.risesoft.y9public.service.Y9FileStoreService;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author qinman
 * @date 2024/11/11
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class EleAttachmentServiceImpl implements EleAttachmentService {

    private final EleAttachmentRepository eleAttachmentRepository;

    private final Y9FileStoreService y9FileStoreService;

    private final OrgUnitApi orgUnitApi;

    private final Y9Properties y9Config;

    @Override
    @Transactional
    public void delBatchByProcessSerialNumbers(List<String> processSerialNumbers) {
        List<EleAttachment> list = eleAttachmentRepository.findByProcessSerialNumberIn(processSerialNumbers);
        for (EleAttachment file : list) {
            try {
                eleAttachmentRepository.delete(file);
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
            EleAttachment eleAttachment = eleAttachmentRepository.findById(str).orElse(null);
            eleAttachmentRepository.deleteById(str);
            assert eleAttachment != null;
            y9FileStoreService.deleteFile(eleAttachment.getFileStoreId());
        }
    }

    @Override
    @Transactional
    public EleAttachment findById(String id) {
        return eleAttachmentRepository.findById(id).orElse(null);
    }

    @Override
    public List<EleAttachment> findByProcessSerialNumberAndAttachmentType(String processSerialNumber, String attachmentType) {
        return eleAttachmentRepository.findByProcessSerialNumberAndAttachmentTypeOrderByTabIndexAsc(processSerialNumber, attachmentType);
    }

    @Override
    @Transactional
    public void saveOrUpdate(EleAttachment eleAttachment) {
        String id = eleAttachment.getId();
        if (StringUtils.isNotBlank(id)) {
            EleAttachment old = this.findById(id);
            old.setName(eleAttachment.getName());
            old.setMiJi(eleAttachment.getMiJi());
            old.setFileStoreId(eleAttachment.getFileStoreId());
            eleAttachmentRepository.save(old);
            return;
        }
        eleAttachment.setId(Y9IdGenerator.genId());
        Integer tabIndex = eleAttachmentRepository.getMaxTabIndex(eleAttachment.getProcessSerialNumber(), eleAttachment.getAttachmentType());
        eleAttachment.setTabIndex(null == tabIndex ? 1 : tabIndex + 1);
        eleAttachmentRepository.save(eleAttachment);
    }
}
