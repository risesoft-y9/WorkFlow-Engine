package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.ItemOrganWordBind;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Repository
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface ItemOrganWordBindRepository extends JpaRepository<ItemOrganWordBind, String>, JpaSpecificationExecutor<ItemOrganWordBind> {

    List<ItemOrganWordBind> findByItemId(String itemId);

    List<ItemOrganWordBind> findByItemIdAndProcessDefinitionId(String itemId, String processDefinitionId);

    List<ItemOrganWordBind> findByItemIdAndProcessDefinitionIdAndTaskDefKey(String itemId, String processDefinitionId, String taskDefKey);

    ItemOrganWordBind findByItemIdAndProcessDefinitionIdAndTaskDefKeyAndOrganWordCustom(String itemId, String processDefinitionId, String taskDefKey, String custom);
}
