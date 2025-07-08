package net.risesoft.repository.view;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.view.ViewType;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface ViewTypeRepository extends JpaRepository<ViewType, String>, JpaSpecificationExecutor<ViewType> {

    ViewType findByMark(String mark);

    @Query("from ViewType t where t.mark not in ?1")
    Page<ViewType> findByMarkNotIn(List<String> markList, Pageable pageable);

    @Query("from ViewType t where t.mark not in ?1 and t.name like ?2")
    Page<ViewType> findByMarkNotInAndNameContaining(List<String> markList, String name, Pageable pageable);

    Page<ViewType> findByNameContaining(String name, Pageable pageable);
}
