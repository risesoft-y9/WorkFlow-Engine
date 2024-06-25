package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.ItemInterfaceBind;

/**
 *
 * @author zhangchongjie
 * @date 2024/05/24
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface ItemInterfaceBindRepository
    extends JpaRepository<ItemInterfaceBind, String>, JpaSpecificationExecutor<ItemInterfaceBind> {

    ItemInterfaceBind findByInterfaceIdAndItemId(String interfaceId, String itemId);

    List<ItemInterfaceBind> findByInterfaceIdOrderByCreateTimeDesc(String interfaceId);

    List<ItemInterfaceBind> findByItemIdOrderByCreateTimeDesc(String itemId);

}
