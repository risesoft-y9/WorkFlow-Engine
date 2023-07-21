package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.ItemViewConf;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface ItemViewConfRepository extends JpaRepository<ItemViewConf, String>, JpaSpecificationExecutor<ItemViewConf> {

    List<ItemViewConf> findByItemIdAndViewTypeOrderByTabIndexAsc(String itemId, String viewType);

    List<ItemViewConf> findByItemIdOrderByTabIndexAsc(String itemId);

    List<ItemViewConf> findByViewTypeOrderByTabIndexAsc(String viewType);

    @Query("select max(tabIndex) from ItemViewConf t where t.itemId=?1 and t.viewType=?2")
    Integer getMaxTabIndex(String itemId, String viewType);

    /**
     * 根据id修改tabIndex
     * 
     * @param tabIndex
     * @param id
     */
    @Modifying
    @Transactional(readOnly = false)
    @Query("update ItemViewConf t set t.tabIndex=?1 where t.id=?2")
    void update4Order(Integer tabIndex, String id);
}