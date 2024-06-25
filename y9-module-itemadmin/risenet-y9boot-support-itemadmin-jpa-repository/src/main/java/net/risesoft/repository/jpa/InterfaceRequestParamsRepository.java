package net.risesoft.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.InterfaceRequestParams;

/**
 *
 * @author zhangchongjie
 * @date 2024/05/23
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface InterfaceRequestParamsRepository
    extends JpaRepository<InterfaceRequestParams, String>, JpaSpecificationExecutor<InterfaceRequestParams> {

    @Modifying
    @Transactional(readOnly = false)
    void deleteByInterfaceId(String id);

}
