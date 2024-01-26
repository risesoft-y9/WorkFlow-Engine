package net.risesoft.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.QuickSend;

/**
 *
 * @author zhangchongjie
 * @date 2023/09/07
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface QuickSendRepository extends JpaRepository<QuickSend, String>, JpaSpecificationExecutor<QuickSend> {

    QuickSend findByItemIdAndPositionIdAndTaskKey(String itemId, String positionId, String taskKey);

}
