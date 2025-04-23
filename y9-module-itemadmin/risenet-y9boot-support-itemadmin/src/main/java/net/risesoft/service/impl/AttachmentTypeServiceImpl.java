package net.risesoft.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.AttachmentConf;
import net.risesoft.entity.AttachmentType;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.user.UserInfo;
import net.risesoft.repository.jpa.AttachmentConfRepository;
import net.risesoft.repository.jpa.AttachmentTypeRepository;
import net.risesoft.service.AttachmentTypeService;
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
    public AttachmentType getByMark(String mark) {
        return attachmentTypeRepository.findByMark(mark);
    }

    @Override
    public List<AttachmentType> listAll() {
        Sort sort = Sort.by(Sort.Direction.ASC, "createDate");
        return attachmentTypeRepository.findAll(sort);
    }

    @SuppressWarnings("serial")
    @Override
    public Page<AttachmentType> pageAll(int page, int rows) {
        Sort sort = Sort.by(Sort.Direction.ASC, "createDate");
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows, sort);
        return attachmentTypeRepository.findAll(new Specification<AttachmentType>() {

            @Override
            public Predicate toPredicate(Root<AttachmentType> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                List<Predicate> list = new ArrayList<Predicate>();
                Predicate[] predicates = new Predicate[list.size()];
                list.toArray(predicates);
                return builder.and(predicates);
            }
        }, pageable);
    }

    @Override
    @Transactional(readOnly = false)
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
    @Transactional(readOnly = false)
    public AttachmentType save(AttachmentType AttachmentType) {
        return attachmentTypeRepository.save(AttachmentType);
    }

    @Override
    @Transactional(readOnly = false)
    public AttachmentType saveOrUpdate(AttachmentType AttachmentType) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            UserInfo person = Y9LoginUserHolder.getUserInfo();
            String id = AttachmentType.getId();
            if (StringUtils.isNotEmpty(id)) {
                AttachmentType oldof = this.getById(id);
                if (null != oldof) {
                    oldof.setModifyDate(sdf.format(new Date()));
                    oldof.setName(AttachmentType.getName());
                    oldof.setUserId(null == person ? "" : person.getPersonId());
                    oldof.setUserName(null == person ? "" : person.getName());
                    this.save(oldof);
                    return oldof;
                } else {
                    return this.save(AttachmentType);
                }
            }

            AttachmentType newof = new AttachmentType();
            newof.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            newof.setMark(AttachmentType.getMark());
            newof.setCreateDate(sdf.format(new Date()));
            newof.setModifyDate(sdf.format(new Date()));
            newof.setName(AttachmentType.getName());
            newof.setTenantId(person.getTenantId());
            newof.setUserId(person.getPersonId());
            newof.setUserName(person.getName());
            newof.setDeleted(0);
            this.save(newof);

            return newof;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
