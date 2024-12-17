package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.OpinionSign;

/**
 * @author qinman
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface OpinionSignRepository
    extends JpaRepository<OpinionSign, String>, JpaSpecificationExecutor<OpinionSign> {

    List<OpinionSign> findBySignDeptDetailIdAndOpinionFrameMarkOrderByCreateDateAsc(String signDeptDetailId,
        String opinionFrameMark);
}
