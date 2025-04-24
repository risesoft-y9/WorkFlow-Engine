package net.risesoft.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.MergeFile;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.jpa.MergeFileRepository;
import net.risesoft.service.MergeFileService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MergeFileServiceImpl implements MergeFileService {

    private final MergeFileRepository mergeFileRepository;

    @Override
    @Transactional
    public Y9Result<Object> delMergeFile(String[] ids) {
        for (String id : ids) {
            mergeFileRepository.deleteById(id);
        }
        return Y9Result.success();
    }

    @Override
    public List<MergeFile> getMergeFileList(String personId, String processSerialNumber, String listType,
        String fileType) {
        Sort sort = Sort.by(Sort.Direction.ASC, "createTime");
        return mergeFileRepository.findAll(new Specification<MergeFile>() {
            @Override
            public Predicate toPredicate(Root<MergeFile> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                List<Predicate> list = new ArrayList<>();
                list.add(builder.equal(root.get("listType"), listType));
                list.add(builder.equal(root.get("fileType"), fileType));
                if (StringUtils.isNotBlank(processSerialNumber)) {
                    list.add(builder.equal(root.get("processSerialNumber"), processSerialNumber));
                }
                if (listType.equals("2")) {
                    list.add(builder.equal(root.get("personId"), personId));
                }
                Predicate[] predicates = new Predicate[list.size()];
                list.toArray(predicates);
                return builder.and(predicates);
            }
        }, sort);
    }

    @Override
    @Transactional
    public void saveMergeFile(MergeFile mergeFile) {
        mergeFileRepository.save(mergeFile);
    }
}
