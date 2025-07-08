package net.risesoft.repository.organword;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.organword.OrganWordProperty;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface OrganWordPropertyRepository
    extends JpaRepository<OrganWordProperty, String>, JpaSpecificationExecutor<OrganWordProperty> {

    OrganWordProperty findByOrganWordIdAndName(String organWordId, String name);

    List<OrganWordProperty> findByOrganWordIdOrderByTabIndexAsc(String organWordId);

    /**
     * 获取最大的tabIndex
     * 
     * @return
     */
    @Query("select max(t.tabIndex) from OrganWordProperty t where t.organWordId=?1")
    Integer getMaxTabIndex(String organWordId);

    /**
     * 根据id修改tabIndex
     * 
     * @param tabIndex
     * @param id
     */
    @Modifying
    @Transactional(readOnly = false)
    @Query("update OrganWordProperty t set t.tabIndex=?1 where t.id=?2")
    void update4Order(Integer tabIndex, String id);
}
