package net.risesoft.repository.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.ProcessInstance;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface ProcessInstanceRepository extends JpaRepository<ProcessInstance, String>, JpaSpecificationExecutor<ProcessInstance> {

    @Modifying
    @Transactional(readOnly = false)
    @Query("delete ProcessInstance t where t.processInstanceId=?1")
    void deleteByProcessInstanceId(String processInstanceId);

    ProcessInstance findByProcessInstanceId(String processInstanceId);

    @Query("from ProcessInstance t where t.assignee like ?1 and isDeleted = 0")
    Page<ProcessInstance> findByUserId(String userId, PageRequest pageable);

    @Query("from ProcessInstance t where t.assignee like ?1 and (serialNumber like ?2 or title like ?2) and isDeleted = 0")
    Page<ProcessInstance> findByUserIdAndTitle(String userId, String title, PageRequest pageable);

}
