package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.ProcessInstanceDetails;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface ProcessInstanceDetailsRepository
    extends JpaRepository<ProcessInstanceDetails, String>, JpaSpecificationExecutor<ProcessInstanceDetails> {

    @Modifying
    @Transactional(readOnly = false)
    @Query("delete ProcessInstanceDetails t where t.processInstanceId=?1")
    void deleteByProcessInstanceId(String processInstanceId);

    List<ProcessInstanceDetails> findByProcessInstanceId(String processInstanceId);

    ProcessInstanceDetails findByProcessInstanceIdAndTaskId(String processInstanceId, String taskId);

}
