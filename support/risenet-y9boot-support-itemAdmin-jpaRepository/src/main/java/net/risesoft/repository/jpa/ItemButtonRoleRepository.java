package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.ItemButtonRole;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface ItemButtonRoleRepository extends JpaRepository<ItemButtonRole, String>, JpaSpecificationExecutor<ItemButtonRole> {

    List<ItemButtonRole> findByItemButtonId(String itemButtonId);

    ItemButtonRole findByItemButtonIdAndRoleId(String itemButtonId, String roleId);
}
