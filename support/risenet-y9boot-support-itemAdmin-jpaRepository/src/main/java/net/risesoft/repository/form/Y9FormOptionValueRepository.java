package net.risesoft.repository.form;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.form.Y9FormOptionValue;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface Y9FormOptionValueRepository
    extends JpaRepository<Y9FormOptionValue, String>, JpaSpecificationExecutor<Y9FormOptionValue> {

    @Modifying
    @Transactional(readOnly = false)
    void deleteByType(String type);

    List<Y9FormOptionValue> findByTypeOrderByTabIndexAsc(String type);

    @Query("select max(tabIndex) from Y9FormOptionValue where type = ?1")
    Integer getMaxTabIndex(String type);

}
