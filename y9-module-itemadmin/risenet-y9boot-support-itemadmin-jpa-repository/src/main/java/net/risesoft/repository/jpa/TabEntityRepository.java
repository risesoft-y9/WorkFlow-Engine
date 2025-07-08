package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import net.risesoft.entity.tab.TabEntity;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface TabEntityRepository extends JpaRepository<TabEntity, String>, JpaSpecificationExecutor<TabEntity> {

    @Override
    @Query("from TabEntity t order by t.createTime asc")
    List<TabEntity> findAll();
}
