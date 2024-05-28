package net.risesoft.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.ItemInterfaceRequestParamsBind;

/**
 *
 * @author zhangchongjie
 * @date 2024/05/24
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface ItemInterfaceRequestParamsBindRepository extends JpaRepository<ItemInterfaceRequestParamsBind, String>, JpaSpecificationExecutor<ItemInterfaceRequestParamsBind> {

}
