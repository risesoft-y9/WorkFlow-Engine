package net.risesoft.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.ItemInterfaceResponseParamsBind;

/**
 *
 * @author zhangchongjie
 * @date 2024/05/24
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface ItemInterfaceResponseParamsBindRepository extends JpaRepository<ItemInterfaceResponseParamsBind, String>, JpaSpecificationExecutor<ItemInterfaceResponseParamsBind> {

}
