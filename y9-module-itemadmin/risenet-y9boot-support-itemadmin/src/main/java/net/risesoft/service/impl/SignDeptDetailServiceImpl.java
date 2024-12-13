package net.risesoft.service.impl;

import java.util.Date;
import java.util.List;

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
    public void saveOrUpdate(SignDeptDetail signDeptDetail) {
        String executionId = signDeptDetail.getExecutionId();
        String deptId = signDeptDetail.getDeptId();
        SignDeptDetail oldDetail = signDeptDetailRepository.findByExecutionIdAndDeptId(executionId, deptId);
        if (null != oldDetail) {
            oldDetail.setUserName(signDeptDetail.getUserName());
            oldDetail.setMobile(signDeptDetail.getMobile());
            oldDetail.setFileStoreId(signDeptDetail.getFileStoreId());
            oldDetail.setDeptManager(signDeptDetail.getDeptManager());
            oldDetail.setStatus(signDeptDetail.getStatus());
            signDeptDetailRepository.save(oldDetail);
            return;
        }

        SignDeptDetail newDetail = new SignDeptDetail();
        newDetail.setId(Y9IdGenerator.genId());
        newDetail.setProcessSerialNumber(signDeptDetail.getProcessSerialNumber());
        newDetail.setProcessInstanceId(signDeptDetail.getProcessInstanceId());
        newDetail.setExecutionId(executionId);
        newDetail.setTaskId(signDeptDetail.getTaskId());
        newDetail.setDeptId(deptId);
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
        List<SignDeptDetail> list =
            signDeptDetailRepository.findByProcessSerialNumberAndDeptIdAndStatusOrderByCreateTimeDesc(
                signDeptDetail.getProcessSerialNumber(), deptId, 2);
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
    public SignDeptDetail findByProcessSerialNumberAndDeptId4Latest(String processSerialNumber, String deptId) {
        List<SignDeptDetail> list = signDeptDetailRepository
            .findByProcessSerialNumberAndDeptIdOrderByCreateTimeDesc(processSerialNumber, deptId);
        if (!list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public List<SignDeptDetail> findByProcessSerialNumberAndDeptId(String processSerialNumber, String deptId) {
        return signDeptDetailRepository.findByProcessSerialNumberAndDeptIdOrderByCreateTimeDesc(processSerialNumber,
            deptId);
    }

    @Override
    public List<SignDeptDetail> findByProcessSerialNumber(String processSerialNumber) {
        return signDeptDetailRepository.findByProcessSerialNumberOrderByCreateTimeDesc(processSerialNumber);
    }

    @Override
    public List<SignDeptDetail> findByTaskId(String processInstanceId, String taskId) {
        return signDeptDetailRepository.findByTaskIdOrderByCreateTimeAsc(taskId);
    }

    @Override
    public List<SignDeptDetail> findByProcessInstanceIdAndStatus(String processInstanceId, int status) {
        return signDeptDetailRepository.findByProcessInstanceIdAndStatusOrderByCreateTimeDesc(processInstanceId,
            status);
    }

    @Override
    public boolean isSignDept(String processSerialNumber, String deptId) {
        List<SignDeptDetail> list = signDeptDetailRepository
            .findByProcessSerialNumberAndDeptIdOrderByCreateTimeDesc(processSerialNumber, deptId);
        return !list.isEmpty();
    }
}
