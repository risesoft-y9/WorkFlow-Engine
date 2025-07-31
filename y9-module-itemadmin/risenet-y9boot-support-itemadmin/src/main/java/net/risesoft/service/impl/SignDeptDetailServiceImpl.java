package net.risesoft.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.SignDeptDetail;
import net.risesoft.enums.SignDeptDetailStatusEnum;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.repository.jpa.SignDeptDetailRepository;
import net.risesoft.service.SignDeptDetailService;

/**
 * @author : qinman
 * @date : 2024-12-11
 **/
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class SignDeptDetailServiceImpl implements SignDeptDetailService {

    private final SignDeptDetailRepository signDeptDetailRepository;

    @Override
    @Transactional
    public void deleteById(String id) {
        SignDeptDetail signDeptDetail = signDeptDetailRepository.findById(id).orElse(null);
        assert signDeptDetail != null;
        signDeptDetail.setStatus(SignDeptDetailStatusEnum.DELETED);
        signDeptDetailRepository.save(signDeptDetail);
    }

    @Override
    public SignDeptDetail findById(String id) {
        return signDeptDetailRepository.findById(id).orElse(null);
    }

    @Override
    public List<SignDeptDetail> findByProcessSerialNumber(String processSerialNumber) {
        Sort sort = Sort.by(Sort.Order.asc("tabIndex"), Sort.Order.desc("createTime"));
        return signDeptDetailRepository.findByProcessSerialNumber(processSerialNumber, sort);
    }

    @Override
    public List<SignDeptDetail> findByProcessSerialNumberAndDeptId(String processSerialNumber, String deptId) {
        return signDeptDetailRepository.findByProcessSerialNumberAndDeptIdOrderByCreateTimeDesc(processSerialNumber,
            deptId);
    }

    @Override
    public SignDeptDetail findByProcessSerialNumberAndDeptId4Latest(String processSerialNumber, String deptId) {
        List<SignDeptDetail> list = signDeptDetailRepository
            .findByProcessSerialNumberAndDeptIdOrderByCreateTimeDesc(processSerialNumber, deptId);
        if (!list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public List<SignDeptDetail> findByProcessSerialNumberAndStatus(String processSerialNumber,
        SignDeptDetailStatusEnum status) {
        return signDeptDetailRepository.findByProcessSerialNumberAndStatusOrderByCreateTimeDesc(processSerialNumber,
            status);
    }

    @Override
    public List<SignDeptDetail> findByTaskId(String processInstanceId, String taskId) {
        return signDeptDetailRepository.findByTaskIdOrderByCreateTimeAsc(taskId);
    }

    @Override
    public boolean isSignDept(String processSerialNumber, String deptId) {
        List<SignDeptDetail> list = signDeptDetailRepository
            .findByProcessSerialNumberAndDeptIdOrderByCreateTimeDesc(processSerialNumber, deptId);
        return !list.isEmpty();
    }

    @Override
    @Transactional
    public void recoverById(String id) {
        SignDeptDetail signDeptDetail = signDeptDetailRepository.findById(id).orElse(null);
        assert signDeptDetail != null;
        signDeptDetail.setStatus(SignDeptDetailStatusEnum.DOING);
        signDeptDetailRepository.save(signDeptDetail);
    }

    @Override
    @Transactional
    public void saveOrUpdate(SignDeptDetail signDeptDetail) {
        String id = signDeptDetail.getId();
        if (StringUtils.isNotBlank(id)) {
            SignDeptDetail oldDetail = signDeptDetailRepository.findById(id).orElse(null);
            assert oldDetail != null;
            oldDetail.setUserName(signDeptDetail.getUserName());
            oldDetail.setMobile(signDeptDetail.getMobile());
            oldDetail.setDeptManager(signDeptDetail.getDeptManager());
            oldDetail.setStatus(
                null == signDeptDetail.getStatus() ? SignDeptDetailStatusEnum.DOING : signDeptDetail.getStatus());
            // 当前为正常办结，则将之前已办结的置为非最新，当前的置为最新
            if (oldDetail.getStatus().equals(SignDeptDetailStatusEnum.DONE)) {
                List<SignDeptDetail> list =
                    signDeptDetailRepository.findByProcessSerialNumberAndDeptIdAndStatusOrderByCreateTimeDesc(
                        signDeptDetail.getProcessSerialNumber(), signDeptDetail.getDeptId(),
                        SignDeptDetailStatusEnum.DONE);
                if (!list.isEmpty()) {
                    oldDetail.setNewed(true);
                    list.forEach(detail -> {
                        detail.setNewed(false);
                        signDeptDetailRepository.save(detail);
                    });
                }
            }
            signDeptDetailRepository.save(oldDetail);
            return;
        }
        signDeptDetail.setId(Y9IdGenerator.genId());
        signDeptDetail.setCreateTime(new Date());
        signDeptDetail.setNewed(false);
        signDeptDetail.setStatus(
            null == signDeptDetail.getStatus() ? SignDeptDetailStatusEnum.DOING : signDeptDetail.getStatus());
        signDeptDetailRepository.save(signDeptDetail);
    }

    @Override
    @Transactional
    public void updateFileStoreId(String signId, String fileStoreId) {
        SignDeptDetail signDeptDetail = signDeptDetailRepository.findById(signId).orElse(null);
        if (signDeptDetail != null) {
            signDeptDetail.setFileStoreId(fileStoreId);
            signDeptDetailRepository.save(signDeptDetail);
        }
    }
}
