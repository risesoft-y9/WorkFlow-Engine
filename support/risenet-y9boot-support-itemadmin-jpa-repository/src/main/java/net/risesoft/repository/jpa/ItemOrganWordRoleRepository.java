package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.ItemOrganWordRole;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface ItemOrganWordRoleRepository
    extends JpaRepository<ItemOrganWordRole, String>, JpaSpecificationExecutor<ItemOrganWordRole> {

    List<ItemOrganWordRole> findByItemOrganWordBindId(String itemOrganWordBindId);

    ItemOrganWordRole findByItemOrganWordBindIdAndRoleId(String itemOrganWordBindId, String roleId);
}
