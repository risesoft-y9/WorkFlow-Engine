package net.risesoft.service.attachment.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.attachment.AttachmentConf;
import net.risesoft.entity.attachment.AttachmentType;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.user.UserInfo;
import net.risesoft.repository.attachment.AttachmentConfRepository;
import net.risesoft.repository.attachment.AttachmentTypeRepository;
import net.risesoft.service.attachment.AttachmentTypeService;
import net.risesoft.y9.Y9LoginUserHolder;

@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class AttachmentTypeServiceImpl implements AttachmentTypeService {

    private final AttachmentTypeRepository attachmentTypeRepository;
    private final AttachmentConfRepository attachmentConfRepository;

    @Override
    public AttachmentType getById(String id) {
        return attachmentTypeRepository.findById(id).orElse(null);
    }

    @Override
    public List<AttachmentType> listAll() {
        Sort sort = Sort.by(Sort.Direction.ASC, "createTime");
        return attachmentTypeRepository.findAll(sort);
    }

    @Override
    public Page<AttachmentType> pageAll(int page, int rows) {
        Sort sort = Sort.by(Sort.Direction.ASC, "createTime");
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        return attachmentTypeRepository.findAll(pageable);
    }

    @Override
    @Transactional()
    public void remove(String id) {
        AttachmentType AttachmentType = this.getById(id);
        if (AttachmentType != null) {
            List<AttachmentConf> list =
                attachmentConfRepository.findByAttachmentTypeOrderByTabIndexAsc(AttachmentType.getMark());
            for (AttachmentConf conf : list) {
                attachmentConfRepository.deleteById(conf.getId());
            }
        }
    }

    @Override
    @Transactional()
    public AttachmentType save(AttachmentType AttachmentType) {
        return attachmentTypeRepository.save(AttachmentType);
    }

    @Override
    @Transactional()
    public AttachmentType saveOrUpdate(AttachmentType AttachmentType) {
        try {
            UserInfo person = Y9LoginUserHolder.getUserInfo();
            String id = AttachmentType.getId();
            if (StringUtils.isNotEmpty(id)) {
                AttachmentType oldof = this.getById(id);
                if (null != oldof) {
                    oldof.setName(AttachmentType.getName());
                    oldof.setUserId(null == person ? "" : person.getPersonId());
                    oldof.setUserName(null == person ? "" : person.getName());
                    attachmentTypeRepository.save(oldof);
                    return oldof;
                } else {
                    return attachmentTypeRepository.save(AttachmentType);
                }
            }
            AttachmentType newof = new AttachmentType();
            newof.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            newof.setMark(AttachmentType.getMark());
            newof.setName(AttachmentType.getName());
            newof.setTenantId(person.getTenantId());
            newof.setUserId(person.getPersonId());
            newof.setUserName(person.getName());
            newof.setDeleted(0);
            attachmentTypeRepository.save(newof);
            return newof;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
