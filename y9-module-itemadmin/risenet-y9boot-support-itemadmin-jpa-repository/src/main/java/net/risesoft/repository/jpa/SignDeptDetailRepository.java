package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import net.risesoft.entity.SignDeptDetail;

/**
 * @author qinman
 */
public interface SignDeptDetailRepository
    extends JpaRepository<SignDeptDetail, String>, JpaSpecificationExecutor<SignDeptDetail> {
    List<SignDeptDetail> findByProcessSerialNumberAndDeptIdOrderByCreateTimeDesc(String processSerialNumber,
        String deptId);

    List<SignDeptDetail> findByProcessSerialNumberOrderByCreateTimeDesc(String processSerialNumber);

    List<SignDeptDetail> findByProcessSerialNumberAndStatusOrderByCreateTimeDesc(String processSerialNumber,
        int status);

    List<SignDeptDetail> findByProcessInstanceIdAndStatusOrderByCreateTimeDesc(String processInstanceId, int status);

    List<SignDeptDetail> findByTaskIdOrderByCreateTimeAsc(String taskId);

    SignDeptDetail findByExecutionIdAndDeptId(String executionId, String deptId);

    List<SignDeptDetail> findByProcessSerialNumberAndDeptIdAndStatusOrderByCreateTimeDesc(String processSerialNumber,
        String deptId, int status);
}
