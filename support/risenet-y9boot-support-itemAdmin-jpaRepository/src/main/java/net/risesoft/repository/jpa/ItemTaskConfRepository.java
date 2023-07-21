package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.ItemTaskConf;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface ItemTaskConfRepository extends JpaRepository<ItemTaskConf, String>, JpaSpecificationExecutor<ItemTaskConf> {

    public List<ItemTaskConf> findByItemIdAndProcessDefinitionId(String itemId, String processDefinitionId);

    public ItemTaskConf findByItemIdAndProcessDefinitionIdAndTaskDefKey(String itemId, String processDefinitionId, String taskDefKey);

    public ItemTaskConf findByItemIdAndProcessDefinitionIdAndTaskDefKeyIsNull(String itemId, String processDefinitionId);
}
