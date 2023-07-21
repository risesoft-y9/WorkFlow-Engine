package net.risesoft.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.OrganWordDetail;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface OrganWordDetailRepository extends JpaRepository<OrganWordDetail, String>, JpaSpecificationExecutor<OrganWordDetail> {

    OrganWordDetail findByCustomAndCharacterValueAndYearAndItemId(String custom, String characterValue, Integer year, String itemId);
}
