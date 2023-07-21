package net.risesoft.repository.form;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.form.Y9FormField;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface Y9FormFieldRepository extends JpaRepository<Y9FormField, String>, JpaSpecificationExecutor<Y9FormField> {

    @Modifying
    @Transactional(readOnly = false)
    void deleteByFormId(String formId);

    List<Y9FormField> findByFormId(String formId);

    Y9FormField findByFormIdAndFieldName(String formId, String fieldName);

    List<Y9FormField> findByFormIdAndTableName(String formId, String tableName);

    List<Y9FormField> findByTableName(String tableName);

}
