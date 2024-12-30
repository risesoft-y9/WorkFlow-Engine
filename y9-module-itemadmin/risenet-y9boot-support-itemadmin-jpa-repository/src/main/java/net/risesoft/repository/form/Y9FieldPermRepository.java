package net.risesoft.repository.form;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.form.Y9FieldPerm;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface Y9FieldPermRepository
    extends JpaRepository<Y9FieldPerm, String>, JpaSpecificationExecutor<Y9FieldPerm> {

    int countByFormIdAndFieldName(String formId, String fieldName);

    @Query("SELECT t.fieldName FROM Y9FieldPerm t where t.formId = ?1 GROUP BY t.fieldName")
    List<String> findByFormId(String formId);

    Y9FieldPerm findByFormIdAndFieldNameAndTaskDefKey(String formId, String fieldName, String taskDefKey);

    List<Y9FieldPerm> findByFormIdAndFieldName(String formId, String fieldName);

}
