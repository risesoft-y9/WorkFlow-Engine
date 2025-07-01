package net.risesoft.repository.form;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.form.Y9Form;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface Y9FormRepository extends JpaRepository<Y9Form, String>, JpaSpecificationExecutor<Y9Form> {

    @Query("SELECT e.tableName FROM Y9Form f,Y9FormField e WHERE f.id = ?1 and f.id = e.formId and e.tableName is not null GROUP BY e.tableName")
    List<String> findBindTableName(String formId);

    Page<Y9Form> findBySystemName(String systemName, Pageable pageable);

    List<Y9Form> findBySystemNameAndFormNameLike(String systemName, String formName);

    boolean existsBySystemNameAndFormName(String systemName, String formName);

}
