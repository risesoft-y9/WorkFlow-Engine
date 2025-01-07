package net.risesoft.repository.form;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.form.Y9Table;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface Y9TableRepository extends JpaRepository<Y9Table, String>, JpaSpecificationExecutor<Y9Table> {

    List<Y9Table> findBySystemName(String systemName);

    Page<Y9Table> findBySystemName(String systemName, Pageable pageable);

    Y9Table findByTableName(String tableName);

    Y9Table findByTableAlias(String tableAlias);

    List<Y9Table> findByTableType(Integer tableType);
}
