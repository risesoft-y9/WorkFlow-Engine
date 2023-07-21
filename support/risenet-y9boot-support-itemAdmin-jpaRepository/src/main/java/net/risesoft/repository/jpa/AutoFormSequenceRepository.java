package net.risesoft.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.AutoFormSequence;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface AutoFormSequenceRepository extends JpaRepository<AutoFormSequence, String>, JpaSpecificationExecutor<AutoFormSequence> {

    @Query("from AutoFormSequence t where t.tenantId=?1 and t.labelName=?2 and t.characterValue=?3 and t.calendarYear=?4")
    public AutoFormSequence findOne(String tenantId, String labelName, String character, Integer calendarYear);

    @Transactional(readOnly = false)
    @Modifying
    @Query("update AutoFormSequence t set t.sequenceValue=t.sequenceValue+1 where t.tenantId=?1 and t.labelName=?2 and t.characterValue=?3 and t.calendarYear=?4")
    public int updateSequence(String tenantId, String labelName, String character, Integer calendarYear);
}