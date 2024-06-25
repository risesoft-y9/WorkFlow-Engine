package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.ItemLinkBind;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface ItemLinkBindRepository
    extends JpaRepository<ItemLinkBind, String>, JpaSpecificationExecutor<ItemLinkBind> {

    List<ItemLinkBind> findByItemIdOrderByCreateTimeDesc(String itemId);

    ItemLinkBind findByLinkIdAndItemId(String linkId, String itemId);

    List<ItemLinkBind> findByLinkIdOrderByCreateTimeDesc(String linkId);

}
