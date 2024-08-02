package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.ItemNodeLinkBind;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface ItemNodeLinkBindRepository
    extends JpaRepository<ItemNodeLinkBind, String>, JpaSpecificationExecutor<ItemNodeLinkBind> {

    List<ItemNodeLinkBind> findByItemId(String itemId);

    List<ItemNodeLinkBind> findByItemIdAndProcessDefinitionId(String itemId, String latestpdId);

    ItemNodeLinkBind findByItemIdAndProcessDefinitionIdAndAndLinkIdAndTaskDefKey(String itemId, String latestpdId,
        String linkId, String taskDefKey);

    ItemNodeLinkBind findByItemIdAndProcessDefinitionIdAndTaskDefKey(String itemId, String processDefinitionId,
        String taskDefKey);

    List<ItemNodeLinkBind> findByLinkId(String id);
}
