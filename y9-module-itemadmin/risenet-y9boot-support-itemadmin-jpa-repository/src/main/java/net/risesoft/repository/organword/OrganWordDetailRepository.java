package net.risesoft.repository.organword;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.organword.OrganWordDetail;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface OrganWordDetailRepository
    extends JpaRepository<OrganWordDetail, String>, JpaSpecificationExecutor<OrganWordDetail> {

    OrganWordDetail findByCustomAndCharacterValueAndYearAndItemId(String custom, String characterValue, Integer year,
        String itemId);

    OrganWordDetail findByCustomAndCharacterValueAndItemId(String custom, String characterValue, String itemId);

    @Query("select max(o.number) from OrganWordDetail o where o.custom=?1 and o.itemId=?2")
    Integer getMaxNumber(String custom, String itemId);

    OrganWordDetail findByCustomAndItemId(String custom, String itemId);
}
