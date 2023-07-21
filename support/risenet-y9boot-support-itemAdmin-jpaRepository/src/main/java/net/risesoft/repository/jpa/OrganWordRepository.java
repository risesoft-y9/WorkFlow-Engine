package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.OrganWord;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface OrganWordRepository extends JpaRepository<OrganWord, String>, JpaSpecificationExecutor<OrganWord> {

    List<OrganWord> findAllByOrderByCreateTimeAsc();

    OrganWord findByCustom(String custom);
}
