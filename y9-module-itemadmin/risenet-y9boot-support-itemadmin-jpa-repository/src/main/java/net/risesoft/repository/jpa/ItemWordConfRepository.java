package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.ItemWordConf;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface ItemWordConfRepository
    extends JpaRepository<ItemWordConf, String>, JpaSpecificationExecutor<ItemWordConf> {

    List<ItemWordConf> findByItemIdAndProcessDefinitionIdAndTaskDefKey(String itemId, String processDefinitionId,
        String taskDefKey);

    ItemWordConf findByItemIdAndProcessDefinitionIdAndTaskDefKeyAndWordType(String itemId, String processDefinitionId,
        String taskDefKey, String wordType);
}
