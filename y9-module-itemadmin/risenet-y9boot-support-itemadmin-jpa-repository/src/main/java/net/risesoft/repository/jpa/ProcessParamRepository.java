package net.risesoft.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.ProcessParam;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface ProcessParamRepository
    extends JpaRepository<ProcessParam, String>, JpaSpecificationExecutor<ProcessParam> {

    @Modifying
    @Transactional(readOnly = false)
    @Query("delete ProcessParam t where t.processInstanceId=?1")
    void deleteByPprocessInstanceId(String processInstanceId);

    @Modifying
    @Transactional(readOnly = false)
    @Query("delete ProcessParam t where t.processSerialNumber=?1")
    void deleteByProcessSerialNumber(String processSerialNumber);

    ProcessParam findByProcessInstanceId(String processInstanceId);

    ProcessParam findByProcessSerialNumber(String processSerialNumber);

    @Modifying
    @Transactional(readOnly = false)
    @Query("update ProcessParam t set t.customItem=?2 where t.processSerialNumber=?1")
    void updateCustomItem(String processSerialNumber, boolean b);
}
