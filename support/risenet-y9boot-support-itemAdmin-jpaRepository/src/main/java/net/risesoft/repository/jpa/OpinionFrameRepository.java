package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.OpinionFrame;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface OpinionFrameRepository extends JpaRepository<OpinionFrame, String>, JpaSpecificationExecutor<OpinionFrame> {

    OpinionFrame findByMark(String mark);

    @Query("from OpinionFrame t where t.mark not in ?1")
    Page<OpinionFrame> findByMarkNotIn(List<String> markList, Pageable pageable);

    @Query("from OpinionFrame t where t.mark not in ?1 and t.name like ?2")
    Page<OpinionFrame> findByMarkNotInAndNameLike(List<String> markList, String name, Pageable pageable);

    Page<OpinionFrame> findByNameContaining(String name, Pageable pageable);
}
