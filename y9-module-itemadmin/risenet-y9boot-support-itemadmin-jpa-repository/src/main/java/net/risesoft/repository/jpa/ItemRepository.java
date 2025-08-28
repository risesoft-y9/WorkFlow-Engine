package net.risesoft.repository.jpa;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import net.risesoft.entity.Item;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface ItemRepository
    extends PagingAndSortingRepository<Item, String>, JpaRepository<Item, String>, JpaSpecificationExecutor<Item> {

    List<Item> findBySystemName(String systemName, Sort sort);

    @Query("from Item s where s.workflowGuid=?1")
    Item findItemByKey(String processDefinitionKey);

    @Query("select distinct t.systemName as systemName, t.sysLevel as sysLevel from Item t ")
    List<Map<String, Object>> getItemSystem();

    @Query("select max(s.tabIndex) from Item s")
    Integer getMaxTabIndex();
}