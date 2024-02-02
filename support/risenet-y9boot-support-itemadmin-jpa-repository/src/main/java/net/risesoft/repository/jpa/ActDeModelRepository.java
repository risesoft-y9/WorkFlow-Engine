package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.ActDeModel;

/**
 *
 * @author zhangchongjie
 * @date 2023/09/21
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface ActDeModelRepository extends JpaRepository<ActDeModel, String>, JpaSpecificationExecutor<ActDeModel> {

    /**
     * @return
     */
    List<ActDeModel> findAllByOrderByLastUpdatedDesc();

    /**
     * @param modelKey
     * @return
     */
    ActDeModel findByModelKey(String modelKey);

}
