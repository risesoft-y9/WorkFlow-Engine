package net.risesoft.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
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
        signDeptDetail.setStatus(SignDeptDetailStatusEnum.DELETED.getValue());
        signDeptDetailRepository.save(signDeptDetail);
    }

    @Override
    public SignDeptDetail findById(String id) {
        return signDeptDetailRepository.findById(id).orElse(null);
    }

    @Override
    public List<SignDeptDetail> findByProcessSerialNumber(String processSerialNumber) {
        return signDeptDetailRepository.findByProcessSerialNumberOrderByCreateTimeDesc(processSerialNumber);
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
    public List<SignDeptDetail> findByProcessSerialNumberAndStatus(String processInstanceId, int status) {
        return signDeptDetailRepository.findByProcessSerialNumberAndStatusOrderByCreateTimeDesc(processInstanceId,
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
    public void saveOrUpdate(SignDeptDetail signDeptDetail) {
        String id = signDeptDetail.getId();
        if (StringUtils.isNotBlank(id)) {
            SignDeptDetail oldDetail = signDeptDetailRepository.findById(id).orElse(null);
            assert oldDetail != null;
            oldDetail.setUserName(signDeptDetail.getUserName());
            oldDetail.setMobile(signDeptDetail.getMobile());
            oldDetail.setFileStoreId(signDeptDetail.getFileStoreId());
            oldDetail.setDeptManager(signDeptDetail.getDeptManager());
            if (null != signDeptDetail.getStatus()) {
                oldDetail.setStatus(signDeptDetail.getStatus());
            }
            signDeptDetailRepository.save(oldDetail);
            return;
        }
        SignDeptDetail newDetail = new SignDeptDetail();
        newDetail.setId(Y9IdGenerator.genId());
        newDetail.setProcessSerialNumber(signDeptDetail.getProcessSerialNumber());
        newDetail.setProcessInstanceId(signDeptDetail.getProcessInstanceId());
        newDetail.setExecutionId(signDeptDetail.getExecutionId());
        newDetail.setTaskId(signDeptDetail.getTaskId());
        newDetail.setDeptId(signDeptDetail.getDeptId());
        newDetail.setDeptName(signDeptDetail.getDeptName());
        newDetail.setUserName(signDeptDetail.getUserName());
        newDetail.setMobile(signDeptDetail.getMobile());
        newDetail.setFileStoreId(signDeptDetail.getFileStoreId());
        newDetail.setDeptManager(signDeptDetail.getDeptManager());
        newDetail.setStatus(signDeptDetail.getStatus());
        newDetail.setCreateTime(new Date());
        newDetail.setNewed(false);
        newDetail.setStatus(null == signDeptDetail.getStatus() ? SignDeptDetailStatusEnum.DOING.getValue()
            : signDeptDetail.getStatus());
        List<SignDeptDetail> list = signDeptDetailRepository
            .findByProcessSerialNumberAndDeptIdAndStatusOrderByCreateTimeDesc(signDeptDetail.getProcessSerialNumber(),
                signDeptDetail.getDeptId(), SignDeptDetailStatusEnum.DONE.getValue());
        if (!list.isEmpty()) {
            list.forEach(detail -> {
                detail.setNewed(false);
                signDeptDetailRepository.save(detail);
            });
            newDetail.setNewed(true);
        }
        signDeptDetailRepository.save(newDetail);
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
