package net.risesoft.repository.form;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.form.Y9TableField;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface Y9TableFieldRepository
    extends JpaRepository<Y9TableField, String>, JpaSpecificationExecutor<Y9TableField> {

    @Modifying
    @Transactional(readOnly = false)
    @Query("delete from Y9TableField t where t.tableId =?1 ")
    void deleteByTableId(String id);

    List<Y9TableField> findByTableIdAndIsSystemFieldOrderByDisplayOrderAsc(String tableId, Integer isSystemField);

    List<Y9TableField> findByTableIdAndStateOrderByDisplayOrderAsc(String tableId, Integer state);

    List<Y9TableField> findByTableIdOrderByDisplayOrderAsc(String tableId);

    List<Y9TableField> findByTableName(String tableName);

    Y9TableField findByTableNameAndFieldNameIgnoreCase(String tableName, String fieldName);

    @Query("select max(t.displayOrder) from Y9TableField t where t.tableId = ?1")
    Integer getMaxDisplayOrder(String tableId);

    @Modifying
    @Transactional(readOnly = false)
    @Query("update Y9TableField t set t.oldFieldName=?2 where t.tableName =?1 and t.fieldName =?2")
    void updateOldFieldName(String tableName, String columnName);

    @Modifying
    @Transactional(readOnly = false)
    @Query("update Y9TableField t set t.state=1 where t.tableId =?1")
    void updateState(String tableId);

}
