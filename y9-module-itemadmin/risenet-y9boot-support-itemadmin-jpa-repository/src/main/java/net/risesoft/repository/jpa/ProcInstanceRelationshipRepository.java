package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.ProcInstanceRelationship;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface ProcInstanceRelationshipRepository
    extends JpaRepository<ProcInstanceRelationship, String>, JpaSpecificationExecutor<ProcInstanceRelationship> {

    @Query("from ProcInstanceRelationship where parentProcInstanceId=?1")
    List<ProcInstanceRelationship> findByParentProcInstanceId(String parentProcInstanceId);

    @Query("from ProcInstanceRelationship where procDefinitionKey=?1")
    List<ProcInstanceRelationship> findByProcDefKey(String procDefinitionKey);

    @Query("select count(*) from ProcInstanceRelationship t where t.procInstanceId=?1")
    int getSubProcessInstanceIdCount(String processInstanceId);

}
