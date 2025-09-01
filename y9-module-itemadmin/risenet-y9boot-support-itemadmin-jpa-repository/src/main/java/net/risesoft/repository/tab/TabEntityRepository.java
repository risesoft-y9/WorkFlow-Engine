package net.risesoft.repository.tab;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import net.risesoft.entity.tab.TabEntity;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface TabEntityRepository extends JpaRepository<TabEntity, String>, JpaSpecificationExecutor<TabEntity> {}
