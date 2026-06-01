package net.risesoft.repository.jpa;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.BackTaskConf;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface BackTaskConfRepository
    extends JpaRepository<BackTaskConf, String>, JpaSpecificationExecutor<BackTaskConf> {

    @Transactional
    void deleteByItemId(String itemId);

    List<BackTaskConf> findByItemIdAndProcessDefinitionId(String itemId, String processDefinitionId);

    BackTaskConf findByItemIdAndProcessDefinitionIdAndTaskDefKey(String itemId, String processDefinitionId,
        String taskDefKey);
}
