package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.ItemOpinionFrameRole;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface ItemOpinionFrameRoleRepository
    extends JpaRepository<ItemOpinionFrameRole, String>, JpaSpecificationExecutor<ItemOpinionFrameRole> {

    List<ItemOpinionFrameRole> findByItemOpinionFrameId(String itemOpinionFrameId);

    ItemOpinionFrameRole findByItemOpinionFrameIdAndRoleId(String itemOpinionFrameId, String roleId);
}
