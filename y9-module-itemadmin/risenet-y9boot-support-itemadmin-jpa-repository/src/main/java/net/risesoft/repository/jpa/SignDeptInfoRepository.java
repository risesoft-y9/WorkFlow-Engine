package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.SignDeptInfo;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface SignDeptInfoRepository
    extends JpaRepository<SignDeptInfo, String>, JpaSpecificationExecutor<SignDeptInfo> {

    @Modifying
    @Transactional
    void deleteByProcessSerialNumberAndDeptType(String processSerialNumber, String deptType);

    @Modifying
    @Transactional
    void deleteByProcessSerialNumberAndDeptTypeAndDeptIdNotIn(String processSerialNumber, String deptType,
        List<String> deptIds);

    SignDeptInfo findByProcessSerialNumberAndDeptTypeAndDeptId(String processSerialNumber, String deptType,
        String deptId);

    List<SignDeptInfo> findByProcessSerialNumberAndDeptTypeOrderByOrderIndexAsc(String processSerialNumber,
        String deptType);

    @Query("select max(t.orderIndex) from SignDeptInfo t where t.processSerialNumber=?1 and t.deptType=?2")
    Integer getMaxTabIndex(String processSerialNumber, String deptType);
}
