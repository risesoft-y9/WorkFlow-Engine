package net.risesoft.repository.jpa;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.Item;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface ItemRepository
    extends PagingAndSortingRepository<Item, String>, JpaRepository<Item, String>, JpaSpecificationExecutor<Item> {

    @Override
    @Query("from Item s order by s.tabIndex asc")
    List<Item> findAll();

    @Query("from Item s where s.systemName=?1 order by s.createDate desc")
    List<Item> findAll(String systemName);

    List<Item> findByIdNotAndNameLike(String id, String name);

    @Query("from Item s where s.workflowGuid=?1")
    Item findItemByKey(String processDefinitionKey);

    @Query(" from Item s where s.departmentId=?1 and s.accountability=?2 order by s.createDate ")
    List<Item> getItemListByDeptAndAccountability(String departmentId, String accountability);

    @Query("select distinct t.systemName as systemName, t.sysLevel as sysLevel from Item t ")
    List<Map<String, Object>> getItemSystem();

    @Query("select max(s.tabIndex) from Item s")
    Integer getMaxTabIndex();

    /**
     * 根据id修改tabIndex
     *
     * @param tabIndex
     * @param id
     */
    @Modifying
    @Transactional(readOnly = false)
    @Query("update Item t set t.tabIndex=?1 where t.id=?2")
    void updateOrder(Integer tabIndex, String id);
}