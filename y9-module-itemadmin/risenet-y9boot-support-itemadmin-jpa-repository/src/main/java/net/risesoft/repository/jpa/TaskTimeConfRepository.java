package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.TaskTimeConf;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface TaskTimeConfRepository
    extends JpaRepository<TaskTimeConf, String>, JpaSpecificationExecutor<TaskTimeConf> {

    @Transactional
    void deleteByItemId(String itemId);

    List<TaskTimeConf> findByItemIdAndProcessDefinitionId(String itemId, String processDefinitionId);

    TaskTimeConf findByItemIdAndProcessDefinitionIdAndTaskDefKey(String itemId, String processDefinitionId,
        String taskDefKey);
}
