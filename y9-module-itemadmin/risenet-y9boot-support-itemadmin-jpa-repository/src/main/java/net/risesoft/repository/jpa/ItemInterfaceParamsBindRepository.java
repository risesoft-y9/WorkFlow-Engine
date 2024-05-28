package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.ItemInterfaceParamsBind;

/**
 *
 * @author zhangchongjie
 * @date 2024/05/24
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface ItemInterfaceParamsBindRepository extends JpaRepository<ItemInterfaceParamsBind, String>, JpaSpecificationExecutor<ItemInterfaceParamsBind> {

    @Modifying
    @Transactional(readOnly = false)
    void deleteByItemIdAndInterfaceId(String itemId, String interfaceId);

    List<ItemInterfaceParamsBind> findByItemIdAndInterfaceIdAndBindTypeOrderByCreateTimeDesc(String itemId, String interfaceId, String type);

    List<ItemInterfaceParamsBind> findByItemIdAndInterfaceIdOrderByCreateTimeDesc(String itemId, String interfaceId);

}
