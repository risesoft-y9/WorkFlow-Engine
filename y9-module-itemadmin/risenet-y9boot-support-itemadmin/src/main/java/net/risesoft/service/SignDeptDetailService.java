package net.risesoft.service;

import java.util.List;

import net.risesoft.entity.SignDeptDetail;

public interface SignDeptDetailService {

    void saveOrUpdate(SignDeptDetail signDeptDetail);

    SignDeptDetail findByProcessSerialNumberAndDeptId4Latest(String processSerialNumber, String deptId);

    List<SignDeptDetail> findByProcessSerialNumberAndDeptId(String processSerialNumber, String deptId);

    List<SignDeptDetail> findByProcessSerialNumber(String processSerialNumber);

    List<SignDeptDetail> findByTaskId(String processInstanceId, String taskId);

    List<SignDeptDetail> findByProcessInstanceIdAndStatus(String processInstanceId, int status);

    boolean isSignDept(String processSerialNumber, String deptId);
}
