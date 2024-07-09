package net.risesoft.repository.jpa;

import net.risesoft.entity.OpinionFrameOneClickSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface OpinionFrameOneClickSetRepository extends JpaRepository<OpinionFrameOneClickSet, String>, JpaSpecificationExecutor<OpinionFrameOneClickSet> {

    OpinionFrameOneClickSet findByBindIdAndOneSetTypeAndExecuteAction(String bindId, String oneSetType, String executeAction);

    List<OpinionFrameOneClickSet> findByBindId(String bindId);

}
