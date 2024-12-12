package net.risesoft.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.SignDeptDetail;
import net.risesoft.entity.SignDeptInfo;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.repository.jpa.SignDeptDetailRepository;
import net.risesoft.service.SignDeptDetailService;
import net.risesoft.service.SignDeptInfoService;

/**
 * @author : qinman
 * @date : 2024-12-11
 **/
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class SignDeptDetailServiceImpl implements SignDeptDetailService {

    private final SignDeptDetailRepository signDeptDetailRepository;

    private final SignDeptInfoService signDeptInfoService;

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
    public List<SignDeptDetail> findByTaskId(String processInstanceId, String taskId) {
        List<SignDeptInfo> infoList = signDeptInfoService.getSignDeptList(processInstanceId, "0");
        List<SignDeptDetail> detailList = signDeptDetailRepository.findByTaskIdOrderByCreateTimeDesc(taskId);
        List<SignDeptDetail> detailListTemp = new ArrayList<>();
        for (SignDeptInfo signDeptInfo : infoList) {
            detailList.forEach(detail -> {
                if (detail.getDeptId().equals(signDeptInfo.getDeptId()) && taskId.equals(detail.getTaskId())) {
                    detailListTemp.add(detail);
                }
            });
        }
        if (detailListTemp.isEmpty()) {
            return detailList;
        }
        return detailListTemp;
    }

    @Override
    public List<SignDeptDetail> findByProcessInstanceIdAndStatus(String processInstanceId, int status) {
        List<SignDeptInfo> infoList = signDeptInfoService.getSignDeptList(processInstanceId, "0");
        List<SignDeptDetail> detailList =
            signDeptDetailRepository.findByProcessInstanceIdAndStatusOrderByCreateTimeDesc(processInstanceId, status);
        List<SignDeptDetail> detailListTemp = new ArrayList<>();
        for (SignDeptInfo signDeptInfo : infoList) {
            detailList.forEach(detail -> {
                if (detail.getDeptId().equals(signDeptInfo.getDeptId())) {
                    detailListTemp.add(detail);
                }
            });
        }
        return detailListTemp;
    }
}
