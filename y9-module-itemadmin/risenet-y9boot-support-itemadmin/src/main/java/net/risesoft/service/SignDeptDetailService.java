package net.risesoft.service;

import java.util.List;

import net.risesoft.entity.SignDeptDetail;

public interface SignDeptDetailService {

    void saveOrUpdate(SignDeptDetail signDeptDetail);

    SignDeptDetail findByProcessSerialNumberAndDeptId4Latest(String processSerialNumber, String deptId);

    SignDeptDetail findById(String id);

    List<SignDeptDetail> findByProcessSerialNumberAndDeptId(String processSerialNumber, String deptId);

    List<SignDeptDetail> findByProcessSerialNumber(String processSerialNumber);

    List<SignDeptDetail> findByTaskId(String processInstanceId, String taskId);

    List<SignDeptDetail> findByProcessSerialNumberAndStatus(String processInstanceId, int status);

    boolean isSignDept(String processSerialNumber, String deptId);

    void deleteById(String id);
}
