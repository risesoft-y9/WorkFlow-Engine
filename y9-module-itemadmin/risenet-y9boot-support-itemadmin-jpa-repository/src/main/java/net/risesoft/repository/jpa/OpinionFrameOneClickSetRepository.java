package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.OpinionFrameOneClickSet;

@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface OpinionFrameOneClickSetRepository
    extends JpaRepository<OpinionFrameOneClickSet, String>, JpaSpecificationExecutor<OpinionFrameOneClickSet> {

    OpinionFrameOneClickSet findByBindIdAndOneSetTypeAndExecuteAction(String bindId, String oneSetType,
        String executeAction);

    List<OpinionFrameOneClickSet> findByBindId(String bindId);

}
