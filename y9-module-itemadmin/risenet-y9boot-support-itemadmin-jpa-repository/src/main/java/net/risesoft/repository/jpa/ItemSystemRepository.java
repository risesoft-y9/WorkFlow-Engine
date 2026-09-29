package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.ItemSystem;

/**
 * @author qinman
 * @date 2026/07/09
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface ItemSystemRepository extends JpaRepository<ItemSystem, String>, JpaSpecificationExecutor<ItemSystem> {

    /**
     * 按排序号升序获取所有系统列表
     *
     * @return List<ItemSystem>
     */
    @Query("from ItemSystem t order by t.tabIndex asc")
    List<ItemSystem> findAllOrderByTabIndex();

    /**
     * 获取最大排序号
     *
     * @return Integer
     */
    @Query("select max(t.tabIndex) from ItemSystem t")
    Integer getMaxTabIndex();

    /**
     * 根据系统名称查找
     *
     * @param name 系统名称
     * @return ItemSystem
     */
    ItemSystem findByName(String name);

    /**
     * 根据中文名称查找
     *
     * @param cnName 中文名称
     * @return ItemSystem
     */
    ItemSystem findByCnName(String cnName);
}
