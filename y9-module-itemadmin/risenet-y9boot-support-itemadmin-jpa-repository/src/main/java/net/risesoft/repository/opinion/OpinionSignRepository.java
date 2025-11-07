package net.risesoft.repository.opinion;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.opinion.OpinionSign;

/**
 * @author qinman
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface OpinionSignRepository
    extends JpaRepository<OpinionSign, String>, JpaSpecificationExecutor<OpinionSign> {

    List<OpinionSign> findBySignDeptDetailIdAndOpinionFrameMarkOrderByCreateTimeAsc(String signDeptDetailId,
        String opinionFrameMark);

    List<OpinionSign> findBySignDeptDetailIdOrderByCreateTimeAsc(String signDeptDetailId);
}
