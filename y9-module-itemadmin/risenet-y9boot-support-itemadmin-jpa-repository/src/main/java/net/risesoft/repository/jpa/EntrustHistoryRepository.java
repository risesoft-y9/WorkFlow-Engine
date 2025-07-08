package net.risesoft.repository.jpa;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.entity.entrust.Entrust;
import net.risesoft.entity.entrust.EntrustHistory;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public interface EntrustHistoryRepository
    extends JpaRepository<EntrustHistory, String>, JpaSpecificationExecutor<Entrust> {

    @Override
    @Query("from EntrustHistory t order by t.startTime desc")
    Page<EntrustHistory> findAll(Pageable pageable);

    Page<EntrustHistory> findByAssigneeIdOrderByStartTimeDesc(String assigneeId, Pageable pageable);

    /**
     * 查找某个用户的委托对象集合
     * 
     * @param ownerId
     * @return
     */
    @Query("from EntrustHistory t where t.ownerId=?1 order by t.startTime desc")
    List<EntrustHistory> findByOwnerId(String ownerId);

    /**
     * 查找某个用户的委托对象集合-分页
     * 
     * @param ownerId
     * @param pageable
     * @return
     */
    @Query("from EntrustHistory t where t.ownerId=?1 order by t.startTime desc")
    Page<EntrustHistory> findByOwnerId(String ownerId, Pageable pageable);

    /**
     * 查找某个人某个事项的委托对象的集合
     * 
     * @param ownerId
     * @param itemId
     * @return
     */
    @Query("from EntrustHistory t where t.ownerId=?1 and t.itemId=?2  order by t.startTime desc")
    List<EntrustHistory> findByOwnerIdAndItemId(String ownerId, String itemId);

    Page<EntrustHistory> findByOwnerIdOrderByCreatTimeDesc(String ownerId, Pageable pageable);
}
