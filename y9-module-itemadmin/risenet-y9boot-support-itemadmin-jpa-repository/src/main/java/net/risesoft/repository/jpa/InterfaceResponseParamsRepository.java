package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.InterfaceResponseParams;

/**
 *
 * @author zhangchongjie
 * @date 2024/05/23
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface InterfaceResponseParamsRepository extends JpaRepository<InterfaceResponseParams, String>, JpaSpecificationExecutor<InterfaceResponseParams> {

    @Modifying
    @Transactional(readOnly = false)
    void deleteByInterfaceId(String id);

    List<InterfaceResponseParams> findByParameterName(String name);

}
