package net.risesoft.service;

import java.util.List;

import net.risesoft.entity.SignDeptDetail;

/**
 * @author qinman
 */
public interface SignDeptDetailService {

    void deleteById(String id);

    void recoverById(String id);

    SignDeptDetail findById(String id);

    List<SignDeptDetail> findByProcessSerialNumber(String processSerialNumber);

    List<SignDeptDetail> findByProcessSerialNumberAndDeptId(String processSerialNumber, String deptId);

    SignDeptDetail findByProcessSerialNumberAndDeptId4Latest(String processSerialNumber, String deptId);

    List<SignDeptDetail> findByProcessSerialNumberAndStatus(String processInstanceId, int status);

    List<SignDeptDetail> findByTaskId(String processInstanceId, String taskId);

    boolean isSignDept(String processSerialNumber, String deptId);

    void saveOrUpdate(SignDeptDetail signDeptDetail);

    void updateFileStoreId(String signId, String fileStoreId);
}
