package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.DynamicRole;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface DynamicRoleRepository
    extends JpaRepository<DynamicRole, String>, JpaSpecificationExecutor<DynamicRole> {

    /**
     * 查找当前租户的所有动态角色
     * 
     * @return
     */
    List<DynamicRole> findAllByOrderByTabIndexAsc();

    /**
     * 获取最大的tabIndex
     * 
     * @return
     */
    @Query("select max(tabIndex) from DynamicRole")
    Integer getMaxTabIndex();

    /**
     * 根据id修改tabIndex
     * 
     * @param tabIndex
     * @param id
     */
    @Modifying
    @Transactional(readOnly = false)
    @Query("update DynamicRole t set t.tabIndex=?1 where t.id=?2")
    void update4Order(Integer tabIndex, String id);

    DynamicRole findByNameAndClassPath(String name, String classPath);
}
