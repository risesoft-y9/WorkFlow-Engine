package net.risesoft.repository.jpa;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import net.risesoft.entity.SpmApproveItem;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface SpmApproveItemRepository extends PagingAndSortingRepository<SpmApproveItem, String>, JpaRepository<SpmApproveItem, String>, JpaSpecificationExecutor<SpmApproveItem> {

    @Override
    @Query("from SpmApproveItem s order by s.createDate desc")
    List<SpmApproveItem> findAll();

    @Query("from SpmApproveItem s where s.systemName=?1 order by s.createDate desc")
    List<SpmApproveItem> findAll(String systemName);

    @Query("from SpmApproveItem s where s.workflowGuid=?1")
    public SpmApproveItem findItemByKey(String processDefinitionKey);

    @Query(" from SpmApproveItem s where s.departmentId=?1 and s.accountability=?2 order by s.createDate ")
    public List<SpmApproveItem> getItemListByDeptAndAccountability(String departmentId, String accountability);

    @Query("select distinct t.systemName as systemName, t.sysLevel as sysLevel from SpmApproveItem t ")
    public List<Map<String, Object>> getItemSystem();
}